package com.mintyfinance.domain.user;

import com.mintyfinance.domain.blocked.BlockedService;
import com.mintyfinance.domain.category.CategoryRepository;
import com.mintyfinance.domain.category.dto.CategoryDto;
import com.mintyfinance.domain.error.ErrorReportRepository;
import com.mintyfinance.domain.exception.BalanceLimitExceededException;
import com.mintyfinance.domain.exception.ForbiddenAccessException;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.exception.UserRoleNotFoundException;
import com.mintyfinance.domain.goal.GoalRepository;
import com.mintyfinance.domain.position.PositionRepository;
import com.mintyfinance.domain.position.PositionService;
import com.mintyfinance.domain.token.PasswordResetTokenRepository;
import com.mintyfinance.domain.transactionhistory.TransactionHistoryRepository;
import com.mintyfinance.domain.transactionhistory.TransactionHistoryService;
import com.mintyfinance.domain.user.dto.UserCredentialsDto;
import com.mintyfinance.domain.user.dto.UserDto;
import com.mintyfinance.domain.user.dto.UserRegistrationDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ErrorReportRepository errorReportRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final GoalRepository goalRepository;
    private final BlockedService blockedService;
    private final PositionService positionService;

    private static final String DEFAULT_USER_ROLE = "USER";
    private static final long ADMIN_ID = 1L;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository,
                       PasswordEncoder passwordEncoder, CategoryRepository categoryRepository,
                       ErrorReportRepository errorReportRepository, @Lazy PositionService positionService,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       GoalRepository goalRepository, BlockedService blockedService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
        this.errorReportRepository = errorReportRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.goalRepository = goalRepository;
        this.blockedService = blockedService;
        this.positionService = positionService;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public List<UserCredentialsDto> getUsersByFilter(String filter) {
        List<Long> bannedUsersIds = getBannedUsersIds();

        return switch (filter) {
            case "blocked" -> getBlockedUsers(bannedUsersIds);
            case "unblocked" -> findAllUsersExcludingIds(bannedUsersIds);
            default -> findAllUsersWithoutAdmin();
        };
    }

    private List<UserCredentialsDto> getBlockedUsers(List<Long> bannedUsersIds) {
        if (!bannedUsersIds.isEmpty()) {
            return findAllUsersByIds(bannedUsersIds);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Long> getBannedUsersIds() {
        List<UserCredentialsDto> users = findAllUsersWithoutAdmin();
        return users.stream()
                .map(UserCredentialsDto::getUserId)
                .filter(blockedService::isUserBlocked)
                .collect(Collectors.toList());
    }

    public Map<String, Object> searchUsers(String email) {
        Map<String, Object> result = new HashMap<>();
        UserCredentialsDto user = findCredentialsByEmail(email).orElse(null);

        if (user != null && user.getUserId() != ADMIN_ID) {
            result.put("users", Collections.singletonList(user));
            result.put("bannedUsersIds", blockedService.isUserBlocked(user.getUserId())
                    ? Collections.singletonList(user.getUserId())
                    : Collections.emptyList());
        } else {
            result.put("users", Collections.emptyList());
            result.put("bannedUsersIds", Collections.emptyList());
        }

        return result;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }

    public boolean isBalanceNegative() {
        return getCurrentUserBalance().compareTo(BigDecimal.ZERO) < 0;
    }

    public List<UserCredentialsDto> findAllUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllByUserIdIn(ids);
        return users.stream().map(UserCredentialsDtoMapper::map).collect(Collectors.toList());
    }

    public List<UserCredentialsDto> findAllUsersExcludingIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return findAllUsersWithoutAdmin(); // zwróć wszystkich użytkowników, jeśli lista ids jest pusta
        }
        List<User> users = userRepository.findAllByUserIdNotIn(ids);
        users.remove(0); // usuwam admina z listy
        return users.stream().map(UserCredentialsDtoMapper::map).collect(Collectors.toList());
    }

    public List<UserCredentialsDto> findAllUsersWithoutAdmin() {
        List<UserCredentialsDto> users = userRepository.findAll().stream()
                .map(UserCredentialsDtoMapper::map)
                .collect(Collectors.toList());
        users.remove(0);
        return users;
    }

    public boolean isAdmin(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<UserRole> adminRoleOptional = userRoleRepository.findByName("ADMIN");
            return adminRoleOptional.isPresent() && user.getRoles().contains(adminRoleOptional.get());
        }
        return false;
    }

    @Transactional
    public void updateBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o id " + userId));
        BigDecimal newBalance = user.getBalance().add(amount);

        if (newBalance.precision() - newBalance.scale() > 9 || newBalance.scale() > 2) {
            throw new BalanceLimitExceededException("Przekroczenie dopuszczalnej wartości salda użytkownika");
        }

        user.setBalance(newBalance);
        userRepository.save(user);
    }

    @Transactional
    public void registerUserWithDefaultRole(UserRegistrationDto userRegistration) {
        UserRole defaultRole = userRoleRepository.findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> new UserRoleNotFoundException(
                        "Nie znaleziono roli użytkownika o nazwie " + DEFAULT_USER_ROLE
                ));
        User user = new User();
        user.setFirstName(userRegistration.getFirstName());
        user.setLastName(userRegistration.getLastName());
        user.setDateOfBirth(userRegistration.getDateOfBirth());
        user.setGender(userRegistration.getGender());
        user.setBalance(userRegistration.getBalance());
        user.setEmail(userRegistration.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistration.getPassword()));

        user.getRoles().add(defaultRole);
        userRepository.save(user);
    }

    public Optional<UserDto> findUserById(long id) {
        return userRepository.findById(id).map(UserDtoMapper::map);
    }

    public Optional<User> findClassicUserById(long id) {
        return userRepository.findById(id);
    }

    public void updateUser(UserDto user) {
        UserRole defaultRole = userRoleRepository.findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> new UserRoleNotFoundException(
                        "Nie znaleziono roli użytkownika o nazwie " + DEFAULT_USER_ROLE
                ));
        User userToSave = findClassicUserById(getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o id " + getCurrentUserId()));
        userToSave.setFirstName(user.getFirstName());
        userToSave.setLastName(user.getLastName());
        userToSave.setDateOfBirth(user.getDateOfBirth());
        userToSave.setGender(user.getGender());
        userToSave.setBalance(user.getBalance());
        userToSave.setEmail(user.getEmail());
        userToSave.setPassword(user.getPassword());
        userToSave.getRoles().add(defaultRole);
        userRepository.save(userToSave);
    }

    public UserDto validateUserOwnershipOfProfileAndReturnUser(Long userId) {
        UserDto user = findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o id " + userId));

        validateUserOwnershipOfProfile(userId);
        return user;
    }

    public boolean checkIfEmailAlreadyExists(String email) {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public void validateUserOwnershipOfProfile(Long userId) {
        if (!userId.equals(getCurrentUserId())) {
            throw new ForbiddenAccessException("Brak dostępu");
        }
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Long getCurrentUserId() {
        Long currentID = -1L;
        if (isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();
            UserCredentialsDto currentUser = findCredentialsByEmail(currentEmail)
                    .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o email " + currentEmail));
            currentID = currentUser.getUserId();
        }
        return currentID;
    }

    public BigDecimal getCurrentUserBalance() {
        Long currentID = getCurrentUserId();
        if (currentID != -1L) {
            return findUserById(currentID)
                    .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o id " + currentID))
                    .getBalance();
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Transactional
    public void decreaseBalance(BigDecimal amount) {
        User user = findClassicUserById(getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika o id " + getCurrentUserId()));
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }

    @Transactional
    public void closeAccount(User user) {
        errorReportRepository.deleteAllByUser(user);
        goalRepository.deleteAllByUser(user);
        positionService.deleteAllPositionsForCurrentUser();
        categoryRepository.deleteAllByUser(user);
        passwordResetTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }
}

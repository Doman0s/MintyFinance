package com.mintyfinance.domain.position;

import com.mintyfinance.domain.category.Category;
import com.mintyfinance.domain.category.CategoryRepository;
import com.mintyfinance.domain.category.dto.CategoryDto;
import com.mintyfinance.domain.exception.CategoryNotFoundException;
import com.mintyfinance.domain.exception.ForbiddenAccessException;
import com.mintyfinance.domain.exception.PositionNotFoundException;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.position.dto.PositionDto;
import com.mintyfinance.domain.position.dto.PositionSaveDto;
import com.mintyfinance.domain.transactionhistory.TransactionHistoryService;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PositionService {
    private final PositionRepository positionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TransactionHistoryService transactionHistoryService;

    public PositionService(PositionRepository positionRepository, CategoryRepository categoryRepository,
                           UserRepository userRepository, UserService userService,
                           TransactionHistoryService transactionHistoryService) {
        this.positionRepository = positionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.transactionHistoryService = transactionHistoryService;
    }

    public List<PositionDto> findFilteredAndSortedPositionsByUserId(long id, String sortBy, String direction,
                                                                    Boolean isIncome, RecurrenceType recurrenceType) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && direction != null) {
            if ("asc".equalsIgnoreCase(direction)) {
                sort = Sort.by(Sort.Order.asc(sortBy));
            } else if ("desc".equalsIgnoreCase(direction)) {
                sort = Sort.by(Sort.Order.desc(sortBy));
            }
        }

        // 1. Filtrowanie
        List<Position> positions;
        if (isIncome == null && recurrenceType == null) {
            positions = positionRepository.findAllByUser_UserId(id);
        } else if (isIncome != null && recurrenceType == null) {
            positions = positionRepository.findAllByUser_UserIdAndIsIncome(id, isIncome);
        } else if (isIncome == null) {
            positions = positionRepository.findAllByUser_UserIdAndRecurrenceType(id, recurrenceType);
        } else {
            positions = positionRepository.findAllByUser_UserIdAndIsIncomeAndRecurrenceType(id, isIncome, recurrenceType);
        }

        // 2. Przekształcenie Kwot
        positions.forEach(position -> {
            if (!position.isIncome()) {
                BigDecimal amount = position.getAmount();
                position.setAmount(amount.negate());
            }
        });

        if (sort.isSorted()) {
            positions = positions.stream()
                    .sorted((p1, p2) -> {
                        if ("amount".equalsIgnoreCase(sortBy)) {
                            // dla BigDecimal
                            return p1.getAmount().compareTo(p2.getAmount());
                        } else if ("priority".equalsIgnoreCase(sortBy)) {
                            // dla byte
                            return Byte.compare(p1.getPriority(), p2.getPriority());
                        }
                        // dla String, domyślna kolumna sortowania
                        return p1.getName().compareTo(p2.getName());
                    })
                    .collect(Collectors.toList());
            if ("desc".equalsIgnoreCase(direction)) {
                Collections.reverse(positions);
            }
        }

        return positions.stream().map(PositionDtoMapper::map).toList();
    }

    public PositionDto validateUserOwnershipOfPositionAndReturnPosition(Long positionId) {
        PositionDto position = findPositionById(positionId)
                .orElseThrow(() -> new PositionNotFoundException("Nie znaleziono pozycji o id " + positionId));

        Long userId = userService.getCurrentUserId();
        if (!position.getUserId().equals(userId)) {
            throw new ForbiddenAccessException("Brak dostępu");
        }

        return position;
    }

    public List<PositionDto> searchPositionsByName(long userId, String name) {
        List<Position> positions = positionRepository.findAllByUser_UserIdAndNameContainingIgnoreCase(userId, name);
        return positions.stream().map(PositionDtoMapper::map).toList();
    }

    public List<PositionDto> findPositionsByUserIdAndCategory_Id(long userId, long categoryId) {
        List<Position> positions = positionRepository.findAllByUser_UserIdAndCategory_CategoryId(userId, categoryId);
        return positions.stream().map(PositionDtoMapper::map).toList();
    }

    public Optional<PositionDto> findPositionById(long id) {
        return positionRepository.findById(id).map(PositionDtoMapper::map);
    }

    public List<PositionDto> findAllPositionsForCurrentUser() {
        return positionRepository.findAllByUser_UserId(userService.getCurrentUserId())
                .stream().map(PositionDtoMapper::map)
                .toList();
    }

    public Optional<Position> findClassicPositionById(long id) {
        return positionRepository.findById(id);
    }

    public List<PositionDto> findPositionsByCategoryName(String category) {
        return positionRepository.findAllByCategory_NameIgnoreCase(category).stream()
                .map(PositionDtoMapper::map)
                .toList();
    }

    public List<Position> findPositionsToProcess(LocalDateTime targetDate, LocalDateTime exclusionDate) {
        return positionRepository.findByRebillDateLessThanEqualAndRebillDateNot(targetDate, exclusionDate);
    }

    @Transactional
    public void addPosition(PositionSaveDto positionToSave) {
        Position position = new Position();
        Category category = categoryRepository.findByNameIgnoreCase(positionToSave.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Nie znaleziono kategorii o nazwie " + positionToSave.getCategory())
                );
        position.setCategory(category);
        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId())
                );
        position.setUser(user);
        position.setName(positionToSave.getName());
        position.setDescription(positionToSave.getDescription());
        position.setIsIncome(positionToSave.getIsIncome());
        position.setPriority(positionToSave.getPriority());
        position.setAmount(positionToSave.getAmount());

        LocalTime rebillTime = positionToSave.getRebillTime() != null ? positionToSave.getRebillTime() : LocalTime.MIDNIGHT;
        LocalDateTime rebillDateTime = LocalDateTime.of(positionToSave.getRebillDate(), rebillTime);
        position.setRebillDate(rebillDateTime);

        position.setRecurrenceType(positionToSave.getRecurrenceType());

        positionRepository.save(position);
    }

    @Transactional
    public void addPosition(Position position) {
        positionRepository.save(position);
    }

    @Transactional
    public void updatePosition(PositionSaveDto updatePosition) {
        Position position = positionRepository.findById(updatePosition.getPositionId())
                .orElseThrow(() -> new PositionNotFoundException(
                        "Nie znaleziono pozycji o id " + updatePosition.getPositionId()
                ));
        position.setCategory(categoryRepository.findByNameIgnoreCase(updatePosition.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Nie znaleziono kategorii o nazwie " + updatePosition.getCategory())
                ));
        position.setUser(userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Nie znaleziono użytkownika o id " + userService.getCurrentUserId())
                ));
        position.setName(updatePosition.getName());
        position.setDescription(updatePosition.getDescription());
        position.setIsIncome(updatePosition.getIsIncome());
        position.setPriority(updatePosition.getPriority());
        position.setAmount(updatePosition.getAmount());

        LocalTime rebillTime = updatePosition.getRebillTime() != null ? updatePosition.getRebillTime() : LocalTime.MIDNIGHT;
        LocalDateTime rebillDateTime = LocalDateTime.of(updatePosition.getRebillDate(), rebillTime);
        position.setRebillDate(rebillDateTime);

        position.setRecurrenceType(updatePosition.getRecurrenceType());
        positionRepository.save(position);
    }

    @Transactional
    public void deletePosition(Long id) {
        transactionHistoryService.deleteAllByPositionId(id);
        positionRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllPositionsForCurrentUser() {
        Optional<User> userOptional = userService.findClassicUserById(userService.getCurrentUserId());
        User user = userOptional.orElseThrow(
                () -> new UserNotFoundException("Nie znaleziono użytkownika o id : " + userService.getCurrentUserId()));
        transactionHistoryService.deleteAllByUser(user);
        positionRepository.deleteAllByUser(user);
    }

    public List<PositionDto> findPositionsByCategory_Id(Long categoryId) {
        return positionRepository.findAllByCategory_CategoryId(categoryId)
                .stream()
                .map(PositionDtoMapper::map)
                .toList();
    }
}

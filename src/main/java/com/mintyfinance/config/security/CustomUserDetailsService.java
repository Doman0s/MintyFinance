package com.mintyfinance.config.security;

import com.mintyfinance.domain.blocked.BlockedRepository;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.UserCredentialsDto;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BlockedRepository blockedRepository;

    public CustomUserDetailsService(UserService userService, UserRepository userRepository,
                                    BlockedRepository blockedRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.blockedRepository = blockedRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredentialsDto credentials = userService.findCredentialsByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Niepoprawne dane uwierzytelniające."));
        com.mintyfinance.domain.user.User foundUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Niepoprawne dane uwierzytelniające."));

        if (blockedRepository.existsByUser_UserId(foundUser.getUserId())) {
            throw new LockedException("Twoje konto zostało zablokowane.");
        }
        return createUserDetails(credentials);
    }

    private UserDetails createUserDetails(UserCredentialsDto credentials) {
        return User.builder()
                .username(credentials.getEmail())
                .password(credentials.getPassword())
                .roles(credentials.getRoles().toArray(String[]::new))
                .build();
    }
}

package com.mintyfinance.domain.user;

import com.mintyfinance.domain.exception.BalanceLimitExceededException;
import com.mintyfinance.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.Optional;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testUpdateBalance() {
        // Przygotowanie danych
        Long userId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        User user = createSampleUser(userId, initialBalance);

        BigDecimal amountToUpdate = BigDecimal.valueOf(-200);
        BigDecimal expectedBalance = initialBalance.add(amountToUpdate);

        // Zakładamy, że metoda userRepository.findById zwraca użytkownika z początkowym saldem
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Akcja
        userService.updateBalance(userId, amountToUpdate);

        // Weryfikacja
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals(expectedBalance, updatedUser.getBalance());
    }

    @Test
    public void testUpdateBalance_WithZeroBalance() {
        Long userId = 1L;
        User user = createSampleUser(userId, BigDecimal.ZERO);

        BigDecimal amountToUpdate = BigDecimal.valueOf(-50);
        BigDecimal expectedBalance = BigDecimal.valueOf(-50);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateBalance(userId, amountToUpdate);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals(expectedBalance, updatedUser.getBalance());
    }

    @Test
    public void testUpdateBalance_WithNegativeBalance() {
        Long userId = 1L;
        User user = createSampleUser(userId, BigDecimal.valueOf(-100));

        BigDecimal amountToUpdate = BigDecimal.valueOf(-50);
        BigDecimal expectedBalance = BigDecimal.valueOf(-150);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateBalance(userId, amountToUpdate);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertEquals(expectedBalance, updatedUser.getBalance());
    }

    @Test
    public void testUpdateBalance_WithNonExistentUserId() {
        Long nonExistentUserId = 2L;

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateBalance(nonExistentUserId, BigDecimal.valueOf(100)));

        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateBalance_ExceedsIntegerDigits() {
        Long userId = 1L;
        User user = createSampleUser(userId, BigDecimal.valueOf(999_999_999));
        BigDecimal amountToUpdate = BigDecimal.ONE;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BalanceLimitExceededException.class, () -> userService.updateBalance(userId, amountToUpdate));
    }

    private User createSampleUser(Long userId, BigDecimal balance) {
        User user = new User();
        user.setUserId(userId);
        user.setBalance(balance);
        return user;
    }
}

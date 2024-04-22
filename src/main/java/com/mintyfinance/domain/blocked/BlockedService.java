package com.mintyfinance.domain.blocked;

import com.mintyfinance.domain.blocked.dto.BlockedDto;
import com.mintyfinance.domain.exception.UserNotFoundException;
import com.mintyfinance.domain.user.User;
import com.mintyfinance.domain.user.UserRepository;
import com.mintyfinance.domain.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlockedService {
    private final BlockedRepository blockedRepository;
    private final UserRepository userRepository;

    public BlockedService(BlockedRepository blockedRepository, UserRepository userRepository) {
        this.blockedRepository = blockedRepository;
        this.userRepository = userRepository;
    }

    public boolean isUserBlocked(Long userId) {
        return blockedRepository.existsByUser_UserId(userId);
    }

    @Transactional
    public void saveBlocked(BlockedDto blockedDto) {
        Blocked blocked = new Blocked();
        User user = userRepository.findById(blockedDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Użytkownik o id %d nie został znaleziony", blockedDto.getUserId())
                ));
        blocked.setBlockDate(blockedDto.getBlockDate());
        blocked.setUser(user);
        blockedRepository.save(blocked);
    }

    @Transactional
    public void deleteBlocked(Long id) {
        blockedRepository.deleteByUser_UserId(id);
    }
}

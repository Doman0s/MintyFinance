package com.mintyfinance.config.interceptor;

import com.mintyfinance.domain.user.UserService;
import com.mintyfinance.domain.user.dto.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class MyModelInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public MyModelInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        if (modelAndView != null) {
            Optional<UserDto> userById = userService.findUserById(userService.getCurrentUserId());
            userById.ifPresent(userDto -> modelAndView.addObject("firstName", userDto.getFirstName()));
            modelAndView.addObject("userID", userService.getCurrentUserId());
            modelAndView.addObject("balance", userService.getCurrentUserBalance());
            modelAndView.addObject("isBalanceNegative", userService.isBalanceNegative());
        }
    }
}

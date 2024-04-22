package com.mintyfinance.config.notification;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class NotificationService {
    public static final String NOTIFICATION_ATTRIBUTE = "notification";

    public void add(RedirectAttributes redirectAttributes, String message, Object... args) {
        redirectAttributes.addFlashAttribute(NOTIFICATION_ATTRIBUTE, String.format(message, args));
    }

    public void add(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(NOTIFICATION_ATTRIBUTE, message);
    }
}

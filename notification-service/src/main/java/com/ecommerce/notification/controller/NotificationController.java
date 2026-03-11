package com.ecommerce.notification.controller;

import com.ecommerce.notification.domain.NotificationLog;
import com.ecommerce.notification.repository.NotificationLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Let admins inspect the notification log for a given order
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationLogRepository log_repository;

    public NotificationController(NotificationLogRepository log_repository) {
        this.log_repository = log_repository;
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<List<NotificationLog>> getLogsForOrder(
            @PathVariable Long order_id,
            @RequestHeader("X-User-Role") String role) {

        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(log_repository.findAllByOrderId(order_id));
    }
}

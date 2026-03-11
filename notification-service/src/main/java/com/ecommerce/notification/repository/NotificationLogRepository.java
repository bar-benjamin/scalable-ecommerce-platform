package com.ecommerce.notification.repository;

import com.ecommerce.notification.domain.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findAllByOrderId(Long order_id);

    boolean existsByOrderIdAndType(Long order_id, NotificationLog.NotificationType type);
}
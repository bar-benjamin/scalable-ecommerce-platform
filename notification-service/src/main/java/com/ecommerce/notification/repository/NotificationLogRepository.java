package com.ecommerce.notification.repository;

import com.ecommerce.notification.domain.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    @Query("SELECT n FROM NotificationLog n WHERE n.order_id = :order_id")
    List<NotificationLog> findAllByOrderId(@Param("order_id") Long order_id);

    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM NotificationLog n " +
            "WHERE n.order_id = :order_id AND n.type = :type")
    boolean existsByOrderIdAndType(@Param("order_id") Long order_id,
                                   @Param("type") NotificationLog.NotificationType type);
}
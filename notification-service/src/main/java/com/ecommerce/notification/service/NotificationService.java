package com.ecommerce.notification.service;

import com.ecommerce.notification.domain.NotificationLog;
import com.ecommerce.notification.messaging.event.OrderPlacedEvent;
import com.ecommerce.notification.messaging.event.PaymentConfirmedEvent;
import com.ecommerce.notification.repository.NotificationLogRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository log_repository;
    private final EmailBuilder email_builder;
    private final SendGrid send_grid;

    public NotificationService(
            NotificationLogRepository log_repository,
            EmailBuilder email_builder,
            @Value("${sendgrid.api-key}") String api_key) {
        this.log_repository = log_repository;
        this.email_builder  = email_builder;
        this.send_grid      = new SendGrid(api_key);
    }

    @Transactional
    public void sendOrderConfirmation(OrderPlacedEvent event) {
        // prevent duplicate confirmation email
        if (log_repository.existsByOrderIdAndType(event.getOrderId(),
                NotificationLog.NotificationType.ORDER_CONFIRMATION)) {
            log.warn("Order confirmation already sent for order_id={}, skipping", event.getOrderId());
            return;
        }

        Mail mail = email_builder.buildOrderConfirmationEmail(event);
        sendEmail(mail, event.getOrderId(), event.getUserEmail(),
                NotificationLog.NotificationType.ORDER_CONFIRMATION);
    }

    @Transactional
    public void sendPaymentConfirmation(PaymentConfirmedEvent event) {
        if (log_repository.existsByOrderIdAndType(event.getOrderId(),
                NotificationLog.NotificationType.PAYMENT_CONFIRMATION)) {
            log.warn("Payment confirmation already sent for order_id={}, skipping",
                    event.getOrderId());
            return;
        }

        String recipient_email = log_repository
                .findAllByOrderId(event.getOrderId())
                .stream()
                .findFirst()
                .map(NotificationLog::getRecipientEmail)
                .orElse(null);

        if (recipient_email == null) {
            log.error("Cannot send payment confirmation for order_id={}: " +
                            "no prior notification log found with recipient email",
                    event.getOrderId());
            return;
        }

        Mail mail = email_builder.buildPaymentConfirmationEmail(event, recipient_email);
        sendEmail(mail, event.getOrderId(), recipient_email,
                NotificationLog.NotificationType.PAYMENT_CONFIRMATION);
    }

    private void sendEmail(Mail mail, Long order_id, String recipient_email,
                           NotificationLog.NotificationType type) {
        NotificationLog.NotificationStatus status;
        String failure_reason = null;

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = send_grid.api(request);

            // SendGrid returns 202 Accepted on success.
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                status = NotificationLog.NotificationStatus.SENT;
                log.info("Email sent successfully for order_id={} type={} recipient={}",
                        order_id, type, recipient_email);
            } else {
                status        = NotificationLog.NotificationStatus.FAILED;
                failure_reason = "SendGrid returned status: " + response.getStatusCode()
                        + " body: " + response.getBody();
                log.error("SendGrid non-success response for order_id={}: {}",
                        order_id, failure_reason);
            }

        } catch (IOException ex) {
            status         = NotificationLog.NotificationStatus.FAILED;
            failure_reason = ex.getMessage();
            log.error("IOException sending email for order_id={}: {}",
                    order_id, ex.getMessage(), ex);
        }

        NotificationLog notification_log = NotificationLog.builder()
                .orderId(order_id)
                .recipientEmail(recipient_email)
                .type(type)
                .status(status)
                .failureReason(failure_reason)
                .build();

        log_repository.save(notification_log);
    }
}

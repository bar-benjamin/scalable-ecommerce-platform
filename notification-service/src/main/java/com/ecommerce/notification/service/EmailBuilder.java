package com.ecommerce.notification.service;

import com.ecommerce.notification.messaging.event.OrderItemEvent;
import com.ecommerce.notification.messaging.event.OrderPlacedEvent;
import com.ecommerce.notification.messaging.event.PaymentConfirmedEvent;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class EmailBuilder {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm 'UTC'")
                    .withZone(ZoneId.of("UTC"));

    private final String from_email;
    private final String from_name;

    public EmailBuilder(
            @Value("${sendgrid.from-email}") String from_email,
            @Value("${sendgrid.from-name}") String from_name) {
        this.from_email = from_email;
        this.from_name  = from_name;
    }

    public Mail buildOrderConfirmationEmail(OrderPlacedEvent event) {
        Email from    = new Email(from_email, from_name);
        Email to      = new Email(event.getUserEmail());
        String subject = "Order Confirmed #" + event.getOrderId();
        Content content = new Content("text/html",
                buildOrderConfirmationHtml(event));

        return new Mail(from, subject, to, content);
    }

    public Mail buildPaymentConfirmationEmail(PaymentConfirmedEvent event,
                                              String recipient_email) {
        Email from    = new Email(from_email, from_name);
        Email to      = new Email(recipient_email);
        String subject = "Payment Received for Order #" + event.getOrderId();
        Content content = new Content("text/html",
                buildPaymentConfirmationHtml(event));

        return new Mail(from, subject, to, content);
    }

    private String buildOrderConfirmationHtml(OrderPlacedEvent event) {
        StringBuilder items_html = new StringBuilder();
        for (OrderItemEvent item : event.getItems()) {
            BigDecimal line_total = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            items_html.append(String.format("""
                    <tr>
                      <td style="padding:8px;border-bottom:1px solid #eee;">%s</td>
                      <td style="padding:8px;border-bottom:1px solid #eee;text-align:center;">%d</td>
                      <td style="padding:8px;border-bottom:1px solid #eee;text-align:right;">$%.2f</td>
                      <td style="padding:8px;border-bottom:1px solid #eee;text-align:right;">$%.2f</td>
                    </tr>
                    """,
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    line_total));
        }

        return String.format("""
                <!DOCTYPE html>
                <html>
                <body style="font-family:Arial,sans-serif;color:#333;max-width:600px;margin:auto;">
                  <h2 style="color:#2d6a4f;">Order Confirmed!</h2>
                  <p>Thank you for your order. Here is your summary:</p>
                  <table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;">
                    <thead>
                      <tr style="background:#f4f4f4;">
                        <th style="padding:8px;text-align:left;">Product</th>
                        <th style="padding:8px;text-align:center;">Qty</th>
                        <th style="padding:8px;text-align:right;">Unit Price</th>
                        <th style="padding:8px;text-align:right;">Total</th>
                      </tr>
                    </thead>
                    <tbody>%s</tbody>
                  </table>
                  <p style="text-align:right;font-size:1.1em;">
                    <strong>Order Total: $%.2f</strong>
                  </p>
                  <p><strong>Shipping to:</strong> %s</p>
                  <p style="color:#888;font-size:0.85em;">
                    Order #%d placed on %s
                  </p>
                </body>
                </html>
                """,
                items_html.toString(),
                event.getTotalAmount(),
                event.getShippingAddress(),
                event.getOrderId(),
                DATE_FMT.format(event.getPlacedAt()));
    }

    private String buildPaymentConfirmationHtml(PaymentConfirmedEvent event) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <body style="font-family:Arial,sans-serif;color:#333;max-width:600px;margin:auto;">
                  <h2 style="color:#2d6a4f;">Payment Received</h2>
                  <p>We have successfully received your payment.</p>
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="border-collapse:collapse;background:#f9f9f9;padding:16px;">
                    <tr>
                      <td style="padding:8px;"><strong>Order ID</strong></td>
                      <td style="padding:8px;">#%d</td>
                    </tr>
                    <tr>
                      <td style="padding:8px;"><strong>Amount Paid</strong></td>
                      <td style="padding:8px;">$%.2f</td>
                    </tr>
                    <tr>
                      <td style="padding:8px;"><strong>Payment Reference</strong></td>
                      <td style="padding:8px;font-family:monospace;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:8px;"><strong>Confirmed At</strong></td>
                      <td style="padding:8px;">%s</td>
                    </tr>
                  </table>
                  <p>Your order is now being processed and you will receive
                     a shipping update soon.</p>
                </body>
                </html>
                """,
                event.getOrderId(),
                event.getAmountPaid(),
                event.getPaymentIntentId(),
                DATE_FMT.format(event.getConfirmedAt()));
    }
}
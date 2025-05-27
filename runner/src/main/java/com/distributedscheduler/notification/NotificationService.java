package com.distributedscheduler.notification;

import com.distributedscheduler.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Sends a webhook notification to the specified notificationUrl in the Task.
     * Does not throw or fail the task if the webhook fails.
     *
     * @param task The task that has reached a terminal state.
     */
    public void sendWebhook(Task task) {
        logger.info("📡 Preparing to send webhook for task: {}", task.getId());

        logger.info("🔍 Notification URL: '{}'", task.getNotificationUrl());
        if (task.getNotificationUrl() == null || task.getNotificationUrl().isBlank()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("taskId", task.getId());
        payload.put("status", task.getStatus().name());
        payload.put("tenantId", task.getTenantId());
        payload.put("retryCount", task.getRetryCount());
        payload.put("timestamp", System.currentTimeMillis());

        try {
            restTemplate.postForEntity(task.getNotificationUrl(), payload, Void.class);
            logger.info("✅ Webhook notification sent for task {}", task.getId());
        } catch (Exception e) {
            logger.warn("⚠️ Failed to send webhook for task {}: {}", task.getId(), e.toString(), e);
        }
    }

    /**
     * Placeholder for future email support.
     *
     * @param task The task that may require an email notification.
     */
    public void sendEmail(Task task) {
        if (task.getNotificationEmail() == null || task.getNotificationEmail().isBlank()) {
            return;
        }

        // 🔜 Future: Implement using JavaMailSender, SMTP or SendGrid API
        logger.info("📧 Email notification to {} not yet implemented.", task.getNotificationEmail());
    }

    /**
     * Main entrypoint to send all configured notifications.
     */
    public void notify(Task task) {
        sendWebhook(task);
        sendEmail(task); // safe even if not implemented
    }
}

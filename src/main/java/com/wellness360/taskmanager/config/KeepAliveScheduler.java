package com.wellness360.taskmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Configuration
@EnableScheduling
public class KeepAliveScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${app.keep-alive.url:https://wellness360-task-manager.onrender.com/health}")
    private String keepAliveUrl;

    @Scheduled(fixedRate = 600000) // Ping every 10 minutes to reset Render's 15-minute inactivity timer
    public void sendKeepAliveHttpPing() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(keepAliveUrl))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Keep-alive HTTP ping to {} returned status code: {}", keepAliveUrl, response.statusCode());
        } catch (Exception e) {
            log.debug("Keep-alive HTTP ping to {} completed: {}", keepAliveUrl, e.getMessage());
        }
    }
}

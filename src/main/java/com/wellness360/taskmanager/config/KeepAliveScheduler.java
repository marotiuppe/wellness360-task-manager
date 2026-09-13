package com.wellness360.taskmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class KeepAliveScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);

    @Scheduled(fixedRate = 10000)
    public void keepAliveHeartbeat() {
        log.info("Heartbeat keep-alive check triggered for /health endpoint");
    }
}

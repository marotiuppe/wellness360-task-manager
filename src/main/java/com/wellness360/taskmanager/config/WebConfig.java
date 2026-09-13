package com.wellness360.taskmanager.config;

import com.wellness360.taskmanager.model.TaskStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToTaskStatusConverter());
    }

    private static class StringToTaskStatusConverter implements Converter<String, TaskStatus> {
        @Override
        public TaskStatus convert(@NonNull String source) {
            return TaskStatus.fromValue(source);
        }
    }
}

package com.wellness360.taskmanager.config;

import com.wellness360.taskmanager.model.Task;
import com.wellness360.taskmanager.model.TaskStatus;
import com.wellness360.taskmanager.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("!test")
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(TaskRepository taskRepository) {
        return args -> {
            taskRepository.deleteAll();

            Task task1 = Task.builder()
                    .title("Review Case Study Requirements")
                    .description("Analyze Wellness360 full-stack Java developer assessment requirements")
                    .dueDate(LocalDate.now().plusDays(1))
                    .status(TaskStatus.COMPLETED)
                    .owner("Maroti Uppe")
                    .build();

            Task task2 = Task.builder()
                    .title("Setup Spring Boot Project")
                    .description("Initialize Spring Boot 3.5 project with Java 25, H2 database, and Security")
                    .dueDate(LocalDate.now().plusDays(2))
                    .status(TaskStatus.COMPLETED)
                    .owner("Maroti Uppe")
                    .build();

            Task task3 = Task.builder()
                    .title("Implement Task Management API")
                    .description("Develop REST endpoints for CRUD operations and status updates")
                    .dueDate(LocalDate.now().plusDays(3))
                    .status(TaskStatus.IN_PROGRESS)
                    .owner("user")
                    .build();

            Task task4 = Task.builder()
                    .title("Configure JWT Authentication")
                    .description("Add JWT token generation and Bearer authentication filter")
                    .dueDate(LocalDate.now().plusDays(4))
                    .status(TaskStatus.IN_PROGRESS)
                    .owner("user")
                    .build();

            Task task5 = Task.builder()
                    .title("Develop Interactive Web UI Dashboard")
                    .description("Build single-page web dashboard with 3D glassmorphism UI, stats counters, login guard, and embedded modals")
                    .dueDate(LocalDate.now().plusDays(4))
                    .status(TaskStatus.COMPLETED)
                    .owner("Maroti Uppe")
                    .build();

            Task task6 = Task.builder()
                    .title("Submit Assignment to Wellness360")
                    .description("Submit GitHub project repository link to eng-jobs@wellness360.co")
                    .dueDate(LocalDate.now().plusDays(5))
                    .status(TaskStatus.PENDING)
                    .owner("Maroti Uppe")
                    .build();

            taskRepository.saveAll(List.of(task1, task2, task3, task4, task5, task6));
        };
    }
}

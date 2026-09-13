package com.wellness360.taskmanager.repository;

import com.wellness360.taskmanager.model.Task;
import com.wellness360.taskmanager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByOwner(String owner);

    List<Task> findByOwnerAndStatus(String owner, TaskStatus status);
}

package com.dashboard.projects.service;

import com.dashboard.projects.dto.TaskRequest;
import com.dashboard.projects.dto.TaskResponse;
import com.dashboard.projects.entity.Project;
import com.dashboard.projects.entity.Task;
import com.dashboard.projects.repository.ProjectRepository;
import com.dashboard.projects.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    
    @Transactional
    public TaskResponse createTask(TaskRequest request, String userEmail) {
        // Verify user has access to the project
        Project project = projectRepository.findByIdAndOwnerOrCollaborator(request.getProjectId(), userEmail)
            .orElseThrow(() -> new RuntimeException("Project not found or access denied"));
        
        Task task = new Task();
        task.setProject(project);
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDeliveryDate(request.getDeliveryDate());
        task.setOwner(userEmail);
        task.setInfo(request.getInfo());
        
        task = taskRepository.save(task);
        return mapToResponse(task);
    }
    
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, String userEmail) {
        // Verify user has access to the project
        Project project = projectRepository.findByIdAndOwnerOrCollaborator(request.getProjectId(), userEmail)
            .orElseThrow(() -> new RuntimeException("Project not found or access denied"));
        
        Task task = taskRepository.findByIdAndProjectId(id, request.getProjectId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Only owner can update the task
        if (!task.getOwner().equals(userEmail)) {
            throw new RuntimeException("Only the owner can update the task");
        }
        
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDeliveryDate(request.getDeliveryDate());
        task.setInfo(request.getInfo());
        
        task = taskRepository.save(task);
        return mapToResponse(task);
    }
    
    public List<TaskResponse> getAllTasksByProject(Long projectId, String userEmail) {
        // Verify user has access to the project
        projectRepository.findByIdAndOwnerOrCollaborator(projectId, userEmail)
            .orElseThrow(() -> new RuntimeException("Project not found or access denied"));
        
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getProject().getId(),
            task.getName(),
            task.getDescription(),
            task.getCreatedAt(),
            task.getDeliveryDate(),
            task.getOwner(),
            task.getInfo()
        );
    }
}

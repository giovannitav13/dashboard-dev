package com.dashboard.projects.controller;

import com.dashboard.projects.dto.TaskRequest;
import com.dashboard.projects.dto.TaskResponse;
import com.dashboard.projects.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping("/project/{projectId}/search")
    public ResponseEntity<?> searchTasksByName(
            @PathVariable Long projectId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("X-User-Email") String userEmail) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            log.info("Search tasks: projectId={}, name={}, archived={}, page={}, size={}, user={}", projectId, name, archived, page, size, userEmail);
            Page<TaskResponse> tasks = taskService.searchTasksByName(projectId, name, archived, userEmail, pageable);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createTask(
            @Valid @RequestBody TaskRequest request,
            @RequestHeader("X-User-Email") String userEmail) {
        try {
            log.info("Create task: projectId={}, name={}, user={}", request.getProjectId(), request.getName(), userEmail);
            TaskResponse response = taskService.createTask(request, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @RequestHeader("X-User-Email") String userEmail) {
        try {
            log.info("Update task: id={}, projectId={}, user={}", id, request.getProjectId(), userEmail);
            TaskResponse response = taskService.updateTask(id, request, userEmail);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    private static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}

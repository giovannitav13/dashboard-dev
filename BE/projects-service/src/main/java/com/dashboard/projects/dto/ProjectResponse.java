package com.dashboard.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String sector;
    private String client;
    private String clientContact;
    private String projectManager;
    private String owner;
    private List<String> collaborators;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

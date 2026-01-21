package com.dashboard.projects.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Sector is required")
    private String sector;
    
    @NotBlank(message = "Client is required")
    private String client;
    
    @NotBlank(message = "Client contact is required")
    private String clientContact;
    
    @NotBlank(message = "Project manager is required")
    private String projectManager;
    
    private List<String> collaborators = new ArrayList<>();
}

package com.dashboard.projects.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    private LocalDate deliveryDate;
    
    private Boolean archived;

    private String info;
}

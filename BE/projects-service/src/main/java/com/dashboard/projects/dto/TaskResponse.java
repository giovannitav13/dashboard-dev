package com.dashboard.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate deliveryDate;
    private String owner;
    private Boolean archived;
    private String info;
}

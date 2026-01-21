package com.dashboard.projects.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String sector;
    
    @Column(nullable = false)
    private String client;
    
    @Column(name = "client_contact", nullable = false)
    private String clientContact;
    
    @Column(name = "project_manager", nullable = false)
    private String projectManager;
    
    @Column(nullable = false)
    private String owner;
    
    @ElementCollection
    @CollectionTable(name = "project_collaborators", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "collaborator_email")
    private List<String> collaborators = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

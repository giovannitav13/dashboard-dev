package com.dashboard.projects.service;

import com.dashboard.projects.dto.ProjectRequest;
import com.dashboard.projects.dto.ProjectResponse;
import com.dashboard.projects.entity.Project;
import com.dashboard.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    
    @Transactional
    public ProjectResponse createProject(ProjectRequest request, String ownerEmail) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setSector(request.getSector());
        project.setClient(request.getClient());
        project.setClientContact(request.getClientContact());
        project.setProjectManager(request.getProjectManager());
        project.setOwner(ownerEmail);
        project.setCollaborators(request.getCollaborators() != null ? request.getCollaborators() : new ArrayList<>());
        
        project = projectRepository.save(project);
        return mapToResponse(project);
    }
    
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request, String userEmail) {
        Project project = projectRepository.findByIdAndOwnerOrCollaborator(id, userEmail)
            .orElseThrow(() -> new RuntimeException("Project not found or access denied"));
        
        // Only owner can update
        if (!project.getOwner().equals(userEmail)) {
            throw new RuntimeException("Only the owner can update the project");
        }
        
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setSector(request.getSector());
        project.setClient(request.getClient());
        project.setClientContact(request.getClientContact());
        project.setProjectManager(request.getProjectManager());
        if (request.getCollaborators() != null) {
            project.setCollaborators(request.getCollaborators());
        }
        
        project = projectRepository.save(project);
        return mapToResponse(project);
    }
    
    public List<ProjectResponse> getAllProjects(String userEmail) {
        List<Project> projects = projectRepository.findByOwnerOrCollaborator(userEmail);
        return projects.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public ProjectResponse getProjectById(Long id, String userEmail) {
        Project project = projectRepository.findByIdAndOwnerOrCollaborator(id, userEmail)
            .orElseThrow(() -> new RuntimeException("Project not found or access denied"));
        return mapToResponse(project);
    }
    
    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getSector(),
            project.getClient(),
            project.getClientContact(),
            project.getProjectManager(),
            project.getOwner(),
            project.getCollaborators(),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }
}

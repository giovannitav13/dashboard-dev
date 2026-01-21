package com.dashboard.projects.repository;

import com.dashboard.projects.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    @Query("SELECT p FROM Project p WHERE p.owner = :email OR :email MEMBER OF p.collaborators")
    List<Project> findByOwnerOrCollaborator(@Param("email") String email);
    
    @Query("SELECT p FROM Project p WHERE p.id = :id AND (p.owner = :email OR :email MEMBER OF p.collaborators)")
    Optional<Project> findByIdAndOwnerOrCollaborator(@Param("id") Long id, @Param("email") String email);
}

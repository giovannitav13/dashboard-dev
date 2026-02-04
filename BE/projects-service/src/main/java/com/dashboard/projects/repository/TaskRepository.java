package com.dashboard.projects.repository;

import com.dashboard.projects.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (:archived IS NULL OR t.archived = :archived)")
    Page<Task> searchByProjectIdAndNameLikeIgnoreCase(@Param("projectId") Long projectId, @Param("name") String name, @Param("archived") Boolean archived, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND (:archived IS NULL OR t.archived = :archived)")
    Page<Task> findByProjectIdAndArchived(@Param("projectId") Long projectId, @Param("archived") Boolean archived, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.project.id = :projectId")
    Optional<Task> findByIdAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId);
}

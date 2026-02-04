import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { ProjectService } from '../services/project.service';
import { TaskService } from '../services/task.service';
import { TaskFormComponent } from '../task/task-form.component';
import { ProjectRequest, ProjectResponse } from '../models/project.models';
import { TaskResponse, TaskPageResponse } from '../models/task.models';

type ViewMode = 'list' | 'create' | 'edit' | 'view';
type TaskViewMode = 'list' | 'create' | 'edit';

@Component({
  selector: 'app-progetti',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarComponent, TaskFormComponent],
  templateUrl: './progetti.component.html',
  styleUrl: './progetti.component.scss'
})
export class ProgettiComponent implements OnInit, OnDestroy {
  viewMode: ViewMode = 'list';
  projects: ProjectResponse[] = [];
  selectedProject: ProjectResponse | null = null;
  
  projectForm: ProjectRequest = {
    name: '',
    description: '',
    sector: '',
    client: '',
    clientContact: '',
    projectManager: '',
    collaborators: []
  };
  
  newCollaborator: string = '';
  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Task management
  tasks: TaskResponse[] = [];
  selectedTask: TaskResponse | null = null;
  taskViewMode: TaskViewMode = 'list';
  loadingTasks: boolean = false;
  projectDetailsExpanded: boolean = false;
  taskSearchQuery = '';
  taskSearchArchived: 'all' | 'active' | 'archived' = 'all';
  taskPage: TaskPageResponse | null = null;
  currentTaskPage = 0;
  taskPageSize = 20;
  private searchSubject = new Subject<void>();
  private destroy$ = new Subject<void>();

  constructor(
    private projectService: ProjectService,
    private taskService: TaskService,
    public router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadProjects();
    this.route.params.subscribe(params => {
      const projectId = params['projectId'];
      if (projectId) {
        const id = +projectId;
        this.projectService.getProjectById(id).subscribe({
          next: (project) => {
            this.setViewMode('view', project);
          },
          error: () => {
            this.setViewMode('list');
            this.router.navigate(['/progetti']);
          }
        });
      } else {
        this.setViewMode('list');
      }
    });
    this.searchSubject.pipe(
      debounceTime(400),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      if (!this.selectedProject) return;
      this.loadTaskPage(0);
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onTaskSearchInput() {
    this.searchSubject.next();
  }

  onTaskSearchArchivedChange() {
    this.searchSubject.next();
  }

  /** Loads a page of tasks via search API. First load: no name, archived=true, page 0. */
  loadTaskPage(page: number) {
    if (!this.selectedProject) return;
    this.loadingTasks = true;
    const name = this.taskSearchQuery.trim() || undefined;
    const archived = this.taskSearchArchived === 'all' ? undefined : this.taskSearchArchived === 'archived';
    this.taskService.searchTasks(
      this.selectedProject.id,
      name,
      archived,
      page,
      this.taskPageSize
    ).subscribe({
      next: (res) => {
        this.taskPage = res;
        this.tasks = res.content;
        this.currentTaskPage = res.number;
        this.loadingTasks = false;
      },
      error: () => {
        this.taskPage = null;
        this.tasks = [];
        this.currentTaskPage = 0;
        this.loadingTasks = false;
      }
    });
  }

  get totalTaskPages(): number {
    return this.taskPage?.totalPages ?? 0;
  }

  get totalTaskElements(): number {
    return this.taskPage?.totalElements ?? 0;
  }

  get hasPrevTaskPage(): boolean {
    return this.taskPage ? !this.taskPage.first : false;
  }

  get hasNextTaskPage(): boolean {
    return this.taskPage ? !this.taskPage.last : false;
  }

  goToPrevTaskPage() {
    if (this.hasPrevTaskPage) this.loadTaskPage(this.currentTaskPage - 1);
  }

  goToNextTaskPage() {
    if (this.hasNextTaskPage) this.loadTaskPage(this.currentTaskPage + 1);
  }

  clearTaskSearch() {
    this.taskSearchQuery = '';
    this.taskSearchArchived = 'all';
    if (this.selectedProject) {
      this.loadTaskPage(0);
    }
  }

  hasActiveTaskFilters(): boolean {
    return !!this.taskSearchQuery.trim() || this.taskSearchArchived !== 'all';
  }

  loadProjects() {
    this.loading = true;
    this.errorMessage = '';
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Error loading projects';
        this.loading = false;
      }
    });
  }

  setViewMode(mode: ViewMode, project?: ProjectResponse) {
    this.viewMode = mode;
    this.errorMessage = '';
    this.successMessage = '';
    
    if (mode === 'create') {
      this.resetForm();
    } else if (mode === 'edit' && project) {
      this.selectedProject = project;
      this.projectForm = {
        name: project.name,
        description: project.description || '',
        sector: project.sector,
        client: project.client,
        clientContact: project.clientContact,
        projectManager: project.projectManager,
        collaborators: [...(project.collaborators || [])]
      };
    } else if (mode === 'view' && project) {
      this.selectedProject = project;
      this.taskSearchQuery = '';
      this.taskSearchArchived = 'active';
      this.loadTaskPage(0);
    } else if (mode === 'list') {
      this.selectedProject = null;
      this.tasks = [];
      this.taskViewMode = 'list';
      this.loadProjects();
    }
  }

  setTaskViewMode(mode: TaskViewMode, task?: TaskResponse) {
    if (mode === 'edit' && task && this.selectedProject) {
      // Navigate to task detail page instead of showing inline form
      this.router.navigate(['/progetti', this.selectedProject.id, 'tasks', task.id]);
      return;
    }
    this.taskViewMode = mode;
    this.selectedTask = task || null;
  }

  onTaskSaved() {
    if (this.selectedProject) {
      this.loadTaskPage(this.currentTaskPage);
      this.setTaskViewMode('list');
    }
  }

  onTaskCancelled() {
    this.setTaskViewMode('list');
  }

  resetForm() {
    this.projectForm = {
      name: '',
      description: '',
      sector: '',
      client: '',
      clientContact: '',
      projectManager: '',
      collaborators: []
    };
    this.newCollaborator = '';
    this.selectedProject = null;
  }

  addCollaborator() {
    if (this.newCollaborator.trim()) {
      if (!this.projectForm.collaborators) {
        this.projectForm.collaborators = [];
      }
      this.projectForm.collaborators.push(this.newCollaborator.trim());
      this.newCollaborator = '';
    }
  }

  removeCollaborator(index: number) {
    if (this.projectForm.collaborators) {
      this.projectForm.collaborators.splice(index, 1);
    }
  }

  onCreate() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.projectService.createProject(this.projectForm).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Project created successfully!';
        setTimeout(() => {
          this.setViewMode('list');
        }, 1500);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error creating project';
      }
    });
  }

  onUpdate() {
    if (!this.selectedProject) return;
    
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.projectService.updateProject(this.selectedProject.id, this.projectForm).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Project updated successfully!';
        setTimeout(() => {
          this.setViewMode('list');
        }, 1500);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error updating project';
      }
    });
  }
}

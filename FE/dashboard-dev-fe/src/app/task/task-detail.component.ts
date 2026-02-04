import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { TaskService } from '../services/task.service';
import { ProjectService } from '../services/project.service';
import { TaskResponse } from '../models/task.models';
import { ProjectResponse } from '../models/project.models';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarComponent],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.scss'
})
export class TaskDetailComponent implements OnInit {
  task: TaskResponse | null = null;
  project: ProjectResponse | null = null;
  taskId!: number;
  projectId!: number;
  loading: boolean = false;
  errorMessage: string = '';
  editMode: boolean = false;
  
  taskForm = {
    name: '',
    description: '',
    deliveryDate: '',
    info: '',
    archived: false
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService,
    private projectService: ProjectService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.projectId = +params['projectId'];
      this.taskId = +params['taskId'];
      this.loadTask();
      this.loadProject();
    });
  }

  loadTask() {
    this.loading = true;
    this.taskService.searchTasks(this.projectId, undefined, undefined, 0, 500).subscribe({
      next: (page) => {
        this.task = page.content.find(t => t.id === this.taskId) || null;
        if (this.task) {
          this.taskForm = {
            name: this.task.name,
            description: this.task.description || '',
            deliveryDate: this.task.deliveryDate || '',
            info: this.task.info || '',
            archived: this.task.archived ?? false
          };
        }
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Error loading task';
        this.loading = false;
      }
    });
  }

  loadProject() {
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
      },
      error: (error) => {
        console.error('Error loading project:', error);
      }
    });
  }

  toggleEdit() {
    this.editMode = !this.editMode;
    if (!this.editMode && this.task) {
      // Reset form if canceling
      this.taskForm = {
        name: this.task.name,
        description: this.task.description || '',
        deliveryDate: this.task.deliveryDate || '',
        info: this.task.info || '',
        archived: this.task.archived ?? false
      };
    }
  }

  saveTask() {
    if (!this.task) return;
    
    this.loading = true;
    this.taskService.updateTask(this.task.id, {
      projectId: this.projectId,
      name: this.taskForm.name,
      description: this.taskForm.description,
      deliveryDate: this.taskForm.deliveryDate,
      info: this.taskForm.info,
      archived: this.taskForm.archived
    }).subscribe({
      next: () => {
        this.loading = false;
        this.editMode = false;
        this.loadTask();
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Error updating task';
        this.loading = false;
      }
    });
  }
}

import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../services/task.service';
import { TaskRequest, TaskResponse } from '../models/task.models';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.scss'
})
export class TaskFormComponent implements OnInit {
  @Input() projectId!: number;
  @Input() task: TaskResponse | null = null;
  @Input() mode: 'create' | 'edit' = 'create';
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  taskForm: TaskRequest = {
    projectId: 0,
    name: '',
    description: '',
    deliveryDate: '',
    info: '',
    archived: false
  };

  loading: boolean = false;
  errorMessage: string = '';

  constructor(private taskService: TaskService) {}

  ngOnInit() {
    this.taskForm.projectId = this.projectId;
    
    if (this.mode === 'edit' && this.task) {
      this.taskForm = {
        projectId: this.task.projectId,
        name: this.task.name,
        description: this.task.description || '',
        deliveryDate: this.task.deliveryDate || '',
        info: this.task.info || '',
        archived: this.task.archived ?? false
      };
    }
  }

  onSubmit() {
    this.loading = true;
    this.errorMessage = '';

    if (this.mode === 'create') {
      this.taskService.createTask(this.taskForm).subscribe({
        next: () => {
          this.loading = false;
          this.saved.emit();
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Error creating task';
        }
      });
    } else if (this.mode === 'edit' && this.task) {
      this.taskService.updateTask(this.task.id, this.taskForm).subscribe({
        next: () => {
          this.loading = false;
          this.saved.emit();
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'Error updating task';
        }
      });
    }
  }

  onCancel() {
    this.cancelled.emit();
  }
}

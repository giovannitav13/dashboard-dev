import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskRequest, TaskResponse, TaskPageResponse } from '../models/task.models';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8082/api/tasks';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const userEmail = localStorage.getItem('userEmail') || '';
    return new HttpHeaders({
      'X-User-Email': userEmail,
      'Content-Type': 'application/json'
    });
  }

  /**
   * Search tasks (paginated). Always use this API for listing tasks.
   * @param projectId project id
   * @param name optional name filter
   * @param archived optional true = only archived, false = only active, undefined = all
   * @param page 0-based page index
   * @param size page size (default 20)
   */
  searchTasks(
    projectId: number,
    name?: string | null,
    archived?: boolean | null,
    page: number = 0,
    size: number = 20
  ): Observable<TaskPageResponse> {
    const params: { page: string; size: string; name?: string; archived?: string } = {
      page: String(page),
      size: String(size)
    };
    if (name !== undefined && name !== null && name !== '') {
      params.name = name;
    }
    if (archived !== undefined && archived !== null) {
      params.archived = String(archived);
    }
    return this.http.get<TaskPageResponse>(`${this.apiUrl}/project/${projectId}/search`, {
      headers: this.getHeaders(),
      params
    });
  }

  createTask(task: TaskRequest): Observable<TaskResponse> {
    return this.http.post<TaskResponse>(this.apiUrl, task, { headers: this.getHeaders() });
  }

  updateTask(id: number, task: TaskRequest): Observable<TaskResponse> {
    return this.http.put<TaskResponse>(`${this.apiUrl}/${id}`, task, { headers: this.getHeaders() });
  }
}

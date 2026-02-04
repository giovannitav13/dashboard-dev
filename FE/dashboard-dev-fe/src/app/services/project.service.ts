import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectRequest, ProjectResponse } from '../models/project.models';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8082/api/projects';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const userEmail = localStorage.getItem('userEmail') || '';
    return new HttpHeaders({
      'X-User-Email': userEmail,
      'Content-Type': 'application/json'
    });
  }

  getAllProjects(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getProjectById(id: number): Observable<ProjectResponse> {
    return this.http.get<ProjectResponse>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  createProject(project: ProjectRequest): Observable<ProjectResponse> {
    return this.http.post<ProjectResponse>(this.apiUrl, project, { headers: this.getHeaders() });
  }

  updateProject(id: number, project: ProjectRequest): Observable<ProjectResponse> {
    return this.http.put<ProjectResponse>(`${this.apiUrl}/${id}`, project, { headers: this.getHeaders() });
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  RegisterRequest, 
  LoginRequest, 
  PasswordRecoveryRequest, 
  ResetPasswordRequest,
  AuthResponse,
  MessageResponse 
} from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }

  requestPasswordRecovery(request: PasswordRecoveryRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/password-recovery`, request);
  }

  resetPassword(request: ResetPasswordRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/reset-password`, request);
  }

  logout(): Observable<MessageResponse> {
    // Quando sarà disponibile l'endpoint di logout nel backend, decommentare:
    // return this.http.post<MessageResponse>(`${this.apiUrl}/logout`, {});
    // Per ora rimuoviamo solo il token dal localStorage
    return new Observable(observer => {
      observer.next({ message: 'Logout successful' });
      observer.complete();
    });
  }
}

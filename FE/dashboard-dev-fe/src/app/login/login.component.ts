import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { LoginRequest, RegisterRequest, PasswordRecoveryRequest, ResetPasswordRequest } from '../models/auth.models';

type ViewMode = 'login' | 'register' | 'recovery' | 'reset';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  viewMode: ViewMode = 'login';
  
  // Login
  loginData: LoginRequest = { email: '', password: '' };
  
  // Register
  registerData: RegisterRequest = { 
    email: '', 
    password: '', 
    firstName: '', 
    lastName: '' 
  };
  
  // Recovery
  recoveryData: PasswordRecoveryRequest = { email: '' };
  
  // Reset
  resetData: ResetPasswordRequest = { token: '', newPassword: '' };
  
  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = `Welcome ${response.firstName}!`;
        localStorage.setItem('token', response.token);
        localStorage.setItem('userEmail', response.email);
        this.router.navigate(['/progetti']);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error during login';
      }
    });
  }

  onRegister() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Registration completed!';
        localStorage.setItem('token', response.token);
        localStorage.setItem('userEmail', response.email);
        this.router.navigate(['/progetti']);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error during registration';
      }
    });
  }

  onRequestRecovery() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.authService.requestPasswordRecovery(this.recoveryData).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Recovery email sent!';
        this.viewMode = 'reset';
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error during password recovery request';
      }
    });
  }

  onResetPassword() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.authService.resetPassword(this.resetData).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Password reset successfully!';
        this.viewMode = 'login';
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error during password reset';
      }
    });
  }

  setViewMode(mode: ViewMode) {
    this.viewMode = mode;
    this.errorMessage = '';
    this.successMessage = '';
  }
}

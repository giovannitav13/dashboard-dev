import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ProgettiComponent } from './progetti/progetti.component';
import { TaskDetailComponent } from './task/task-detail.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/progetti', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'progetti', component: ProgettiComponent, canActivate: [authGuard] },
  { path: 'progetti/:projectId/tasks/:taskId', component: TaskDetailComponent, canActivate: [authGuard] },
  { path: 'progetti/:projectId', component: ProgettiComponent, canActivate: [authGuard] }
];

import { Routes } from '@angular/router';
import { RegisterComponent } from './register/register.component';
import { HomeComponent} from "./home/home.component";

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./login/login.component').then(m => m.LoginComponent) },// or your home component if any
  { path: 'predict', loadComponent: () => import('./prediction/prediction.component').then(m => m.PredictionComponent) },
  { path: 'advanced-predict', loadComponent: () => import('./advanced-prediction/advanced-prediction.component').then(m => m.AdvancedPredictionComponent) },
  { path: 'predictions', loadComponent: () => import('./predictions/predictions.component').then(m => m.PredictionsComponent) },
  { path: 'feedback', loadComponent: () => import('./feedback/feedback.component').then(m => m.FeedbackComponent)},
  { path: 'support', loadComponent: () => import('./support/support.component').then(m => m.SupportComponent)},
  {
    path: 'support/admin-support',
    loadComponent: () => import('./support/admin-support/admin-support.component').then(m => m.AdminSupportComponent)
  }

];

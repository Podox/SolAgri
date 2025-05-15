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

];

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ReactiveFormsModule, FormBuilder, Validators, FormGroup, FormsModule} from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component'; // adjust path as needed

@Component({
  selector: 'app-login',
  standalone: true, // ✅ Required
  imports: [CommonModule, ReactiveFormsModule, FormsModule,NavbarComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.loading = true;
    this.error = null;

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: (res) => {
        console.log('Login success:', res);

        // ✅ Save user data in localStorage
        localStorage.setItem('user', JSON.stringify(res.user));

        this.router.navigate(['/']); // or '/predict', based on your routing
      },
      error: (err) => {
        this.error = err;
        this.loading = false;
      },
    });
  }
}

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { SupportService } from '../services/support.service';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule,NavbarComponent],
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.css']
})
export class SupportComponent implements OnInit {
  form: FormGroup;
  tickets: any[] = [];
  error: string | null = null;
  loggedIn = false;

  constructor(
    private fb: FormBuilder,
    private supportService: SupportService,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      subject: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loggedIn = this.authService.isLoggedIn();
    this.loadTickets();
  }

  loadTickets(): void {
    this.supportService.getMyTickets().subscribe({
      next: (res) => this.tickets = res,
      error: (err) => this.error = err.error || 'Failed to load tickets.'
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.supportService.createTicket(this.form.value).subscribe({
      next: () => {
        this.form.reset();
        this.loadTickets();
      },
      error: (err) => this.error = err.error || 'Failed to create ticket.'
    });
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        localStorage.removeItem('user');
        this.loggedIn = false;
        this.router.navigate(['/login']);
      },
      error: () => {
        this.loggedIn = false;
        this.router.navigate(['/login']);
      }
    });
  }
}

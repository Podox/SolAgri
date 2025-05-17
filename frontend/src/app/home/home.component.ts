import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  loggedIn = false;
  username: string | null = null;
  isAdmin = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.loggedIn = this.authService.isLoggedIn();

    if (this.loggedIn) {
      const user = this.authService.getUser();
      this.username = user?.username ?? null;
      this.isAdmin = this.username === 'admin';
    }
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        localStorage.removeItem('user');
        this.loggedIn = false;
        this.username = null;
        this.isAdmin = false;
        this.router.navigate(['/']);
      },
      error: () => {
        this.loggedIn = false;
        this.username = null;
        this.isAdmin = false;
        this.router.navigate(['/login']);
      }
    });
  }

  handleGetStarted(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/predictions']);
    } else {
      this.router.navigate(['/login']);
    }
  }
  handleCalculate(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/predict']);
    } else {
      this.router.navigate(['/login']);
    }
  }
}

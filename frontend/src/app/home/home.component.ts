import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';  // <-- import this
import {Router, RouterLink} from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],  // <-- add here
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  loggedIn = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.loggedIn = this.authService.isLoggedIn();
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        localStorage.removeItem('user');  // Optional if AuthService doesn’t do it
        this.loggedIn = false;
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Logout error:', err);
        // Still redirect or show error depending on your needs
        this.loggedIn = false;
        this.router.navigate(['/login']);
      }
    });
  }

}

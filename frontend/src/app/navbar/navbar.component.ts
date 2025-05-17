import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  loggedIn = false;
  isAdmin = false;
  currentPath = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.loggedIn = this.authService.isLoggedIn();
    const user = this.authService.getUser();
    this.isAdmin = user?.username === 'admin';

    // Get current path initially
    this.currentPath = this.router.url;

    // Listen to route changes
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentPath = event.urlAfterRedirects;
      });
  }

  isNotCurrentRoute(route: string): boolean {
    return this.currentPath !== route;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        localStorage.removeItem('user');
        this.loggedIn = false;
        this.isAdmin = false;
        this.router.navigate(['/login']);
      }
    });
  }
}

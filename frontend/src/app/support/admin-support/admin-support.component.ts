// admin-support.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../navbar/navbar.component';
import { SupportService } from '../../services/support.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-admin-support',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, NavbarComponent],
  templateUrl: './admin-support.component.html',
  styleUrls: ['./admin-support.component.css'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideIn', [
      transition(':enter', [
        style({ transform: 'translateX(-10px)', opacity: 0 }),
        animate('200ms ease-out', style({ transform: 'translateX(0)', opacity: 1 }))
      ])
    ])
  ]
})
export class AdminSupportComponent implements OnInit {
  tickets: any[] = [];
  error: string | null = null;
  isLoading = false;
  lastRefresh: Date | null = null;

  constructor(private supportService: SupportService) {}

  ngOnInit(): void {
    this.loadAllTickets();
  }

  loadAllTickets(): void {
    if (this.isLoading) return;
    
    this.isLoading = true;
    this.error = null;

    this.supportService.getAllTickets().subscribe({
      next: (res) => {
        this.tickets = res;
        this.lastRefresh = new Date();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Access denied or failed to load tickets.';
        this.isLoading = false;
        console.error('Error loading tickets:', err);
      }
    });
  }

  updateStatus(ticketId: number, status: string): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.error = null;

    this.supportService.updateStatus(ticketId, status).subscribe({
      next: () => {
        this.loadAllTickets();
        // Update the specific ticket in the UI immediately for better UX
        const ticket = this.tickets.find(t => t.id === ticketId);
        if (ticket) {
          ticket.status = status;
          ticket.updatedAt = new Date();
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to update ticket status.';
        this.isLoading = false;
        console.error('Error updating ticket status:', err);
      }
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'OPEN':
        return 'yellow';
      case 'IN_PROGRESS':
        return 'blue';
      case 'RESOLVED':
        return 'green';
      default:
        return 'gray';
    }
  }
}

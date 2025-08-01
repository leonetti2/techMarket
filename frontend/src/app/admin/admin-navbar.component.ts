import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-navbar',
  standalone: true,
  imports: [MatToolbarModule, MatIconModule, MatButtonModule],
  template: `
    <mat-toolbar color="primary" class="navbar">
      <span class="navbar-logo" (click)="goToDashboard($event)" style="cursor:pointer; font-weight:600; font-size:1.2em;">Admin</span>
      <span class="navbar-spacer"></span>
      <button mat-icon-button (click)="goToDashboard($event)" aria-label="Dashboard">
        <mat-icon>dashboard</mat-icon>
      </button>
      <button mat-icon-button (click)="goToProducts($event)" aria-label="Prodotti">
        <mat-icon>inventory_2</mat-icon>
      </button>
      <button mat-icon-button (click)="goToOrders($event)" aria-label="Ordini">
        <mat-icon>assignment</mat-icon>
      </button>
      <button mat-icon-button (click)="logout()" aria-label="Logout">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>
  `,
  styles: [`
    .navbar {
      background: linear-gradient(90deg, #1976d2 60%, #42a5f5 100%) !important;
      color: #fff !important;
      border-radius: 0 0 18px 18px !important;
      box-shadow: 0 2px 8px #1976d220 !important;
      font-family: 'Inter', Roboto, Arial, sans-serif !important;
      position: sticky;
      top: 0;
      z-index: 100;
      display: flex;
      flex-direction: row;
      align-items: center;
    }
    .navbar-logo {
      user-select: none;
    }
    .navbar-spacer {
      flex: 1 1 auto;
    }
    .mat-icon-button {
      color: #fff !important;
      font-weight: 600 !important;
      border-radius: 8px !important;
      transition: background 0.2s !important;
    }
    .mat-icon-button:hover {
      background: #1565c0 !important;
    }
  `]
})
export class AdminNavbarComponent {
  constructor(private router: Router) {}

  goToDashboard(event: Event) {
    event.preventDefault();
    this.router.navigate(['/admin-dashboard']);
  }
  goToProducts(event: Event) {
    event.preventDefault();
    this.router.navigate(['/admin-products']);
  }
  goToOrders(event: Event) {
    event.preventDefault();
    this.router.navigate(['/admin-orders']);
  }
  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/']);
  }
} 
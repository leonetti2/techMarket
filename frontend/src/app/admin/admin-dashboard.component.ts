import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DecimalPipe, CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { AdminNavbarComponent } from './admin-navbar.component';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, DecimalPipe, MatToolbarModule, MatIconModule, AdminNavbarComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  stats: any = null;
  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get('http://localhost:8081/admin/dashboard-stats').subscribe({
      next: data => this.stats = data,
      error: () => this.stats = null
    });
  }
  goToProducts(event: Event) {
    event.preventDefault();
    window.location.assign('/admin-products');
  }
  goToOrders(event: Event) {
    event.preventDefault();
    window.location.assign('/admin-orders');
  }
  goToDashboard(event: Event) {
    event.preventDefault();
    window.location.assign('/admin-dashboard');
  }
  isActive(path: string): boolean {
    return window.location.pathname === path;
  }
  logout() {
    localStorage.removeItem('token');
    window.location.href = '/';
  }
} 
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatBadgeModule } from '@angular/material/badge';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, MatToolbarModule, MatIconModule, MatButtonModule, MatBadgeModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  @Input() cartCount: number | null = 0;

  get displayCartCount() {
    return this.cartCount ?? 0;
  }

  goToHome() { window.location.href = '/home'; }
  goToCart() { window.location.href = '/cart'; }
  goToUserArea() { window.location.href = '/user'; }
  goToAdminProducts() { window.location.href = '/admin-products'; }
  logout() { localStorage.removeItem('token'); window.location.href = '/'; }

  isAdmin(): boolean {
    const token = localStorage.getItem('token');
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Il ruolo può essere in 'role' o 'roles' (stringa o array)
      if (payload.role) return payload.role === 'ADMIN';
      if (payload.roles) return Array.isArray(payload.roles) ? payload.roles.includes('ADMIN') : payload.roles === 'ADMIN';
      return false;
    } catch {
      return false;
    }
  }
} 
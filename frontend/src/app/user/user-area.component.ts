import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { OrderDetailsDialogComponent } from './order-details-dialog.component';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-user-area',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, MatFormFieldModule, MatInputModule, MatIconModule, MatDividerModule, MatDialogModule, MatCardModule],
  templateUrl: './user-area.component.html',
  styleUrls: ['./user-area.component.css']
})
export class UserAreaComponent implements OnInit {
  user: any = null;
  orders: any[] = [];
  selectedOrder: any = null;
  selectedOrderDetails: any = null;
  editMode = false;
  editUser: any = {};
  saving = false;
  showPasswordForm = false;
  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  savingPassword = false;
  passwordError = '';
  passwordSuccess = '';
  isEditMode: boolean = false;
  isPasswordFormVisible: boolean = false;

  constructor(private http: HttpClient, private router: Router, private dialog: MatDialog) {}

  ngOnInit() {
    this.loadUserAndOrders();
  }

  loadUserAndOrders() {
    const email = this.getEmailFromToken();
    if (!email) {
      // Se non autenticato, torna al login
      this.router.navigate(['/']);
      return;
    }
    this.http.get<any>(`http://localhost:8081/users/by-email/${encodeURIComponent(email)}`)
      .subscribe(user => {
        if (user && user.id) {
          this.user = user;
          this.editUser = { ...user };
          this.loadOrders(this.user.id);
        } else {
          this.user = null;
          this.orders = [];
        }
      });
  }

  loadOrders(userId: number) {
    this.http.get<any[]>(`http://localhost:8081/orders/user/${userId}`)
      .subscribe(orders => this.orders = orders);
  }

  showOrderDetails(order: any) {
    this.router.navigate(['/order-details', order.orderId]);
  }

  closeOrderDetails() {
    this.selectedOrder = null;
    this.selectedOrderDetails = null;
    // Il dialog si chiude da solo
  }

  saveUser() {
    if (!this.user) return;
    this.saving = true;
    // Costruisco un oggetto solo con i campi modificati
    const update: any = {};
    for (const key of Object.keys(this.editUser)) {
      if (this.editUser[key] !== this.user[key]) {
        update[key] = this.editUser[key];
      }
    }
    // Se non è stato modificato nulla, esco
    if (Object.keys(update).length === 0) {
      this.editMode = false;
      this.saving = false;
      return;
    }
    this.http.put<any>(`http://localhost:8081/users/${this.user.id}`, { ...this.user, ...update })
      .subscribe({
        next: updated => {
          this.user = updated;
          this.editMode = false;
          this.saving = false;
        },
        error: () => {
          this.passwordError = 'Errore durante il salvataggio dei dati';
          this.saving = false;
        }
      });
  }

  cancelEdit() {
    this.editUser = { ...this.user };
    this.editMode = false;
  }

  changePassword() {
    this.passwordError = '';
    this.passwordSuccess = '';
    if (!this.oldPassword || !this.newPassword || !this.confirmPassword) {
      this.passwordError = 'Compila tutti i campi.';
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.passwordError = 'Le nuove password non coincidono.';
      return;
    }
    this.savingPassword = true;
    this.http.put<any>(`http://localhost:8081/users/${this.user.id}/password`, {
      oldPassword: this.oldPassword,
      newPassword: this.newPassword
    }).subscribe({
      next: () => {
        this.passwordSuccess = 'Password aggiornata con successo!';
        this.savingPassword = false;
        this.oldPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        setTimeout(() => {
          this.showPasswordForm = false;
          this.passwordSuccess = '';
        }, 2000);
      },
      error: err => {
        this.passwordError = err.error || 'Errore durante il cambio password';
        this.savingPassword = false;
      }
    });
  }

  cancelPasswordChange() {
    this.oldPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.passwordError = '';
    this.passwordSuccess = '';
    this.showPasswordForm = false;
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
    this.editMode = this.isEditMode;
    if (this.isEditMode) {
      this.isPasswordFormVisible = false;
      this.showPasswordForm = false;
    }
  }

  togglePasswordForm() {
    this.isPasswordFormVisible = !this.isPasswordFormVisible;
    this.showPasswordForm = this.isPasswordFormVisible;
    if (this.isPasswordFormVisible) {
      this.isEditMode = false;
      this.editMode = false;
    }
  }

  private getEmailFromToken(): string | null {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || payload.email || null;
    } catch {
      return null;
    }
  }
} 
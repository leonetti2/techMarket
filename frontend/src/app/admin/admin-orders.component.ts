import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AdminNavbarComponent } from './admin-navbar.component';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [
    CommonModule, 
    MatTableModule, 
    MatButtonModule, 
    MatIconModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatSelectModule, 
    MatSnackBarModule, 
    FormsModule,
    DatePipe,
    AdminNavbarComponent
  ],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit {
  orders: any[] = [];
  filteredOrders: any[] = [];
  displayedColumns = ['id', 'date', 'userEmail', 'total', 'status', 'actions'];
  totalOrders = 0;
  pageSize = 10;
  pageNumber = 0;
  statusFilter: string[] = [];
  emailFilter = '';
  dateFrom: string = '';
  dateTo: string = '';
  minTotal: number|null = null;
  maxTotal: number|null = null;
  selectedOrder: any = null;

  constructor(private http: HttpClient, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    let params = new HttpParams()
      .set('pageNumber', this.pageNumber)
      .set('pageSize', this.pageSize);
    if (this.emailFilter.trim()) params = params.set('email', this.emailFilter.trim());
    if (this.dateFrom) params = params.set('from', this.dateFrom);
    if (this.dateTo) params = params.set('to', this.dateTo);
    if (this.minTotal !== null && this.minTotal !== undefined) params = params.set('minTotal', this.minTotal.toString());
    if (this.maxTotal !== null && this.maxTotal !== undefined) params = params.set('maxTotal', this.maxTotal.toString());
    const endpoint = (this.emailFilter.trim() || this.dateFrom || this.dateTo || this.minTotal !== null && this.minTotal !== undefined || this.maxTotal !== null && this.maxTotal !== undefined) ? 'filtered' : 'paged';
    this.http.get<any>(`http://localhost:8081/admin/orders/${endpoint}`, { params }).subscribe({
      next: response => {
        if (response && Array.isArray(response.content)) {
          this.orders = response.content;
          this.totalOrders = response.totalElements || 0;
          this.applyFilters();
        } else {
          this.orders = [];
          this.totalOrders = 0;
          this.applyFilters();
          this.snackBar.open('Formato risposta non valido', '', { 
            duration: 2500, 
            panelClass: 'snackbar-error' 
          });
        }
      },
      error: () => {
        this.orders = [];
        this.totalOrders = 0;
        this.applyFilters();
        this.snackBar.open('Errore nel caricamento ordini', '', { 
          duration: 2500, 
          panelClass: 'snackbar-error' 
        });
      }
    });
  }

  onFilterChange() {
    this.pageNumber = 0;
    this.loadOrders();
  }

  nextPage() {
    if ((this.pageNumber + 1) * this.pageSize < this.totalOrders) {
      this.pageNumber++;
      this.loadOrders();
    }
  }
  prevPage() {
    if (this.pageNumber > 0) {
      this.pageNumber--;
      this.loadOrders();
    }
  }

  applyFilters() {
    const emailFilter = this.emailFilter.trim().toLowerCase();
    this.filteredOrders = this.orders.filter(order => {
      const matchesEmail = !emailFilter ||
        (order.user && order.user.email && order.user.email.toLowerCase().includes(emailFilter));
      return matchesEmail;
    });
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'PENDING': 'In attesa',
      'CONFIRMED': 'Confermato',
      'SHIPPED': 'Spedito',
      'CANCELLED': 'Annullato'
    };
    return labels[status] || status;
  }

  viewOrderDetails(order: any) {
    this.selectedOrder = order;
  }

  closeOrderDetails() {
    this.selectedOrder = null;
  }

  logout() {
    localStorage.removeItem('token');
    window.location.href = '/';
  }

  getTotalPages(): number {
    return Math.ceil(this.totalOrders / this.pageSize) || 1;
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
} 
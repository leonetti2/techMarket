import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { PaymentMethodComponent } from '../payment/payment-method.component';
import { PaymentData } from '../payment/payment.model';
import { MatIconModule } from '@angular/material/icon';
import { NavbarComponent } from '../navbar/navbar.component';
import { CartService } from './cart.service';
import { Observable } from 'rxjs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, FormsModule, PaymentMethodComponent, MatIconModule, NavbarComponent, MatSnackBarModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cartItems: any[] = [];
  displayedColumns = ['name', 'quantity', 'price', 'subtotal', 'actions'];

  paymentData: PaymentData = { method: 'CONSEGNA', valid: true };
  showPaymentForm = false;
  orderSuccess = false;
  cartCount$: Observable<number>;

  constructor(private http: HttpClient, private cartService: CartService, private snackBar: MatSnackBar) {
    this.cartCount$ = this.cartService.getCartCount();
  }

  ngOnInit() {
    this.loadCart();
    this.updateCartCount();
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

  private getUserId(): Promise<number> {
    const email = this.getEmailFromToken();
    if (!email) return Promise.reject('Token non valido');
    return this.http.get<any>(`http://localhost:8081/users/by-email/${encodeURIComponent(email)}`).toPromise()
      .then(user => {
        if (user && user.id) return user.id;
        throw new Error('Utente non trovato');
      });
  }

  loadCart() {
    this.getUserId().then(userId => {
      this.http.get<any>(`http://localhost:8081/cart/${userId}`).subscribe({
        next: cart => {
          this.cartItems = cart.items || [];
          if (this.cartItems.length === 0) this.showPaymentForm = false;
          this.updateCartCount();
        },
        error: err => {
          this.cartItems = [];
          this.showPaymentForm = false;
          this.updateCartCount();
        }
      });
    });
  }

  updateCartCount() {
    this.getUserId().then(userId => {
      this.http.get<any>(`http://localhost:8081/cart/${userId}`).subscribe({
        next: cart => {
          const count = (cart.items || []).reduce((sum: number, item: any) => sum + item.quantity, 0);
          this.cartService.setCartCount(count);
        },
        error: () => this.cartService.setCartCount(0)
      });
    }).catch(() => this.cartService.setCartCount(0));
  }

  getTotal(): number {
    return this.cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  }

  removeItem(item: any) {
    this.getUserId().then(userId => {
      if (item.quantity > 1) {
        this.http.post(`http://localhost:8081/cart/${userId}/add/${item.productId}?quantity=-1`, {})
          .subscribe({
            next: () => this.loadCart(),
            error: () => this.loadCart()
          });
      } else {
        this.http.delete(`http://localhost:8081/cart/${userId}/remove/${item.productId}`).subscribe({
          next: () => this.loadCart(),
          error: () => this.loadCart()
        });
      }
    });
  }

  clearCart() {
    this.getUserId().then(userId => {
      this.http.post(`http://localhost:8081/cart/${userId}/clear`, {}).subscribe({
        next: () => this.loadCart(),
        error: () => this.loadCart()
      });
    });
  }

  onPaymentChange(data: PaymentData) {
    this.paymentData = data;
  }

  showPayment() {
    this.showPaymentForm = true;
  }

  confirmOrder() {
    this.getUserId().then(userId => {
      const paymentMethodMap: any = {
        'CONSEGNA': 'CONSEGNA',
        'CARTA': 'CREDIT_CARD',
        'PAYPAL': 'PAYPAL'
      };
      const paymentMethod = paymentMethodMap[this.paymentData.method];
      const extraData: any = { ...this.paymentData };
      delete extraData.method;
      delete extraData.valid;
      this.http.post('http://localhost:8081/orders/checkout', {
        userId,
        paymentMethod,
        ...extraData
      }).subscribe({
        next: (res) => {
          console.log('Ordine confermato, risposta:', res);
          this.orderSuccess = true;
          this.cartItems = [];
          this.paymentData = { method: 'CONSEGNA', valid: true };
          this.showPaymentForm = false;
          this.snackBar.open('Ordine confermato con successo!', 'Chiudi', {
            duration: 3500,
            panelClass: 'snackbar-success',
            verticalPosition: 'top'
          });
          setTimeout(() => {
            this.orderSuccess = false;
            window.location.reload();
          }, 1200);
        },
        error: (err) => {
          console.log('Errore nella conferma ordine:', err);
          this.snackBar.open('Errore nella conferma ordine', 'Chiudi', {
            duration: 3500,
            panelClass: 'snackbar-error',
            verticalPosition: 'top'
          });
        }
      });
    });
  }

  goToCatalog() {
    window.location.href = '/home';
  }
} 
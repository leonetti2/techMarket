import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-order-details-page',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatSnackBarModule],
  templateUrl: './order-details-page.component.html',
  styleUrls: ['./order-details-page.component.css']
})
export class OrderDetailsPageComponent implements OnInit {
  order: any = null;
  loading = false;
  error = false;
  returnMessage = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    public router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const orderId = params.get('id');
      if (orderId) {
        this.loading = true;
        this.http.get<any>(`http://localhost:8081/orders/${orderId}`)
          .subscribe({
            next: data => {
              this.order = data;
              this.loading = false;
              this.error = false;
            },
            error: () => {
              this.loading = false;
              this.error = true;
            }
          });
      }
    });
  }

  canRequestReturn(): boolean {
    return this.order && (this.order.status === 'COMPLETED' || this.order.status === 'CONFIRMED' || this.order.status === 'SHIPPED');
  }

  requestReturn() {
    if (!this.order) return;
    this.http.post(
      `http://localhost:8081/orders/${this.order.orderId}/return`,
      {},
      { responseType: 'text' }
    ).subscribe({
      next: () => {
        this.snackBar.open('Richiesta di reso inviata con successo!', 'Chiudi', {
          duration: 3500,
          panelClass: 'snackbar-success',
          verticalPosition: 'top'
        });
        if (this.order) this.order.status = 'RETURN_REQUESTED';
      },
      error: () => {
        this.snackBar.open('Errore durante la richiesta di reso.', 'Chiudi', {
          duration: 3500,
          panelClass: 'snackbar-error',
          verticalPosition: 'top'
        });
      }
    });
  }

  getStatusLabel(status: string): string {
    if (status === 'CONFIRMED' || status === 'COMPLETED') return 'Completato';
    if (status === 'SHIPPED') return 'Spedito';
    if (status === 'PENDING') return 'In attesa';
    if (status === 'CANCELLED') return 'Annullato';
    return status;
  }
} 
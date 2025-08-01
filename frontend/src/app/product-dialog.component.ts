import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-product-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatInputModule, MatIconModule, MatDividerModule],
  template: `
    <div class="dialog-container">
      <img class="dialog-image" [src]="getImageUrl(data.imageUrl)" alt="Immagine prodotto">
      <h2 class="dialog-title">{{ data.name }}</h2>
      <div class="dialog-description" *ngIf="data.description">
        {{ data.description }}
      </div>
      <div class="dialog-info-row">
        <span class="dialog-info-item"><mat-icon class="dialog-info-icon">sell</mat-icon> <b>Marca:</b> {{ data.brand }}</span>
        <span class="dialog-info-item"><mat-icon class="dialog-info-icon">category</mat-icon> <b>Categoria:</b> {{ data.category }}</span>
      </div>
      <div class="dialog-badges-row">
        <span class="dialog-badge dialog-price-badge"><mat-icon class="dialog-badge-icon">euro</mat-icon> {{ data.price | currency:'EUR' }}</span>
        <span class="dialog-badge" [ngClass]="{'dialog-stock-badge': true, 'dialog-stock-low': data.quantity === 0, 'dialog-stock-ok': data.quantity > 0}">
          <mat-icon class="dialog-badge-icon">inventory_2</mat-icon>
          {{ data.quantity > 0 ? 'Disponibili: ' + data.quantity : 'Non disponibile' }}
        </span>
      </div>
      <mat-divider class="dialog-divider"></mat-divider>
      <form (ngSubmit)="addToCart()" class="dialog-form">
        <mat-form-field appearance="outline" class="qty-field">
          <mat-label>Quantità</mat-label>
          <input matInput type="number" min="1" [max]="data.quantity || 99" [(ngModel)]="quantity" name="quantity">
        </mat-form-field>
        <button mat-raised-button color="primary" type="submit" [disabled]="!canAddToCart()" class="dialog-add-btn">Aggiungi al carrello</button>
      </form>
    </div>
  `,
  styles: [
    `.dialog-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px 0 0 0;
      min-width: 260px;
      max-width: 420px;
      background: #f7fafd;
      border-radius: 18px;
      box-shadow: 0 2px 16px #1976d210;
    }
    .dialog-image {
      width: 100%;
      max-width: 270px;
      height: 190px;
      object-fit: cover;
      border-radius: 14px;
      background: #eee;
      margin-bottom: 14px;
      box-shadow: 0 2px 8px #1976d220;
    }
    .dialog-title {
      font-size: 1.35rem;
      font-weight: 700;
      margin: 0 0 8px 0;
      text-align: center;
      color: #1976d2;
      letter-spacing: 0.5px;
    }
    .dialog-description {
      font-size: 1.05rem;
      color: #333;
      margin-bottom: 12px;
      text-align: center;
      background: #e3f2fd;
      border-radius: 8px;
      padding: 10px 14px;
      box-shadow: 0 1px 4px #1976d210;
      white-space: pre-line;
    }
    .dialog-info-row {
      display: flex;
      gap: 18px;
      font-size: 1rem;
      color: #555;
      margin-bottom: 10px;
      justify-content: center;
      flex-wrap: wrap;
    }
    .dialog-info-item {
      display: flex;
      align-items: center;
      gap: 4px;
    }
    .dialog-info-icon {
      font-size: 1.1em;
      color: #1976d2;
      vertical-align: middle;
    }
    .dialog-badges-row {
      display: flex;
      gap: 16px;
      margin-bottom: 10px;
      justify-content: center;
      flex-wrap: wrap;
    }
    .dialog-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      font-size: 1.08em;
      font-weight: 600;
      border-radius: 999px;
      padding: 7px 18px;
      background: #fff;
      box-shadow: 0 1px 4px #1976d220;
      border: 2px solid #e3eaf3;
      color: #1976d2;
    }
    .dialog-price-badge {
      background: #e3f2fd;
      color: #1976d2;
      border-color: #1976d2;
    }
    .dialog-stock-badge.dialog-stock-ok {
      background: #e3fcec;
      color: #1b5e20;
      border-color: #43a047;
    }
    .dialog-stock-badge.dialog-stock-low {
      background: #ffebee;
      color: #b71c1c;
      border-color: #b71c1c;
    }
    .dialog-badge-icon {
      font-size: 1.15em;
      vertical-align: middle;
    }
    .dialog-divider {
      width: 90%;
      margin: 12px 0 10px 0;
      border-top: 1.5px solid #e3eaf3;
      align-self: center;
    }
    .dialog-form {
      display: flex;
      gap: 16px;
      align-items: center;
      width: 100%;
      justify-content: center;
      margin-bottom: 8px;
      margin-top: 4px;
    }
    .qty-field {
      width: 90px;
    }
    .dialog-add-btn {
      font-size: 1.08em;
      font-weight: 700;
      padding: 10px 28px;
      border-radius: 999px;
      letter-spacing: 0.5px;
      box-shadow: 0 1px 4px #1976d220;
    }
  `]
})
export class ProductDialogComponent {
  quantity = 1;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<ProductDialogComponent>
  ) {}

  getImageUrl(url: string): string {
    if (!url) return 'https://placehold.co/250x180?text=No+Image';
    if (url.startsWith('http')) return url;
    return 'http://localhost:8081' + url;
  }

  canAddToCart(): boolean {
    return this.quantity > 0 && (!this.data.quantity || this.quantity <= this.data.quantity);
  }

  addToCart() {
    // Qui puoi emettere un evento, chiamare un servizio, o chiudere il dialog con il valore
    this.dialogRef.close({ product: this.data, quantity: this.quantity });
  }
} 
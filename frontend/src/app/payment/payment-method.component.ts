import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatRadioModule } from '@angular/material/radio';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export type PaymentMethod = 'CONSEGNA' | 'CARTA' | 'PAYPAL';

@Component({
  selector: 'app-payment-method',
  standalone: true,
  imports: [CommonModule, FormsModule, MatRadioModule, MatFormFieldModule, MatInputModule],
  templateUrl: './payment-method.component.html',
  styleUrls: ['./payment-method.component.css']
})
export class PaymentMethodComponent {
  @Input() disabled = false;
  @Output() paymentChange = new EventEmitter<any>();

  methods: { value: PaymentMethod, label: string }[] = [
    { value: 'CONSEGNA', label: 'Alla consegna' },
    { value: 'CARTA', label: 'Carta di credito' },
    { value: 'PAYPAL', label: 'PayPal' }
  ];

  paymentMethod: PaymentMethod | null = 'CONSEGNA';
  cardNumber = '';
  cardHolder = '';
  cardExpiry = '';
  cardCvv = '';
  paypalEmail = '';

  ngOnInit() {
    this.emitPayment();
  }

  selectMethod(method: PaymentMethod) {
    if (this.paymentMethod === method) {
      this.paymentMethod = null;
    } else {
      this.paymentMethod = method;
    }
    this.emitPayment();
  }

  emitPayment() {
    let valid = this.canConfirm();
    let data: any = { method: this.paymentMethod, valid };
    if (this.paymentMethod === 'CARTA') {
      data = {
        ...data,
        cardNumber: this.cardNumber,
        cardHolder: this.cardHolder,
        cardExpiry: this.cardExpiry,
        cardCvv: this.cardCvv
      };
    } else if (this.paymentMethod === 'PAYPAL') {
      data = {
        ...data,
        paypalEmail: this.paypalEmail
      };
    }
    this.paymentChange.emit(data);
  }

  canConfirm(): boolean {
    if (!this.paymentMethod) return false;
    if (this.paymentMethod === 'CONSEGNA') return true;
    if (this.paymentMethod === 'CARTA') {
      return this.cardNumber.length >= 13 && this.cardHolder.trim().length > 0 &&
        /^\d{2}\/\d{2}$/.test(this.cardExpiry) && this.cardCvv.length >= 3;
    }
    if (this.paymentMethod === 'PAYPAL') {
      return /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(this.paypalEmail);
    }
    return false;
  }
} 
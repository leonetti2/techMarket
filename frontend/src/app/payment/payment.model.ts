export type PaymentMethod = 'CONSEGNA' | 'CARTA' | 'PAYPAL';

export interface PaymentData {
  method: PaymentMethod;
  cardNumber?: string;
  cardHolder?: string;
  cardExpiry?: string;
  cardCvv?: string;
  paypalEmail?: string;
  valid: boolean;
} 
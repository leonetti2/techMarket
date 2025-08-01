import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  email = '';
  password = '';
  firstName = '';
  lastName = '';
  telephoneNumber = '';
  address = '';
  error = '';
  @Output() registered = new EventEmitter<string>();
  @Output() switchToLogin = new EventEmitter<void>();

  // Validazione
  emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;

  constructor(private http: HttpClient) {}

  isEmailValid(): boolean {
    return this.emailPattern.test(this.email);
  }
  isPasswordValid(): boolean {
    return this.passwordPattern.test(this.password);
  }
  isFirstNameValid(): boolean {
    return !!this.firstName && this.firstName.length <= 30;
  }
  isLastNameValid(): boolean {
    return !!this.lastName && this.lastName.length <= 30;
  }
  isTelephoneValid(): boolean {
    return /^[0-9]{10}$/.test(this.telephoneNumber);
  }
  isAddressValid(): boolean {
    return !this.address || this.address.length <= 60;
  }
  isFormValid(): boolean {
    return this.isEmailValid() && this.isPasswordValid() && this.isFirstNameValid() && this.isLastNameValid() && this.isTelephoneValid() && this.isAddressValid();
  }

  register() {
    if (!this.isFormValid()) {
      this.error = 'Compila correttamente tutti i campi.';
      return;
    }
    const payload = {
      email: this.email,
      password: this.password,
      firstName: this.firstName,
      lastName: this.lastName,
      telephoneNumber: this.telephoneNumber,
      address: this.address
    };
    this.http.post('http://localhost:8081/auth/register', payload, { responseType: 'text' })
      .subscribe({
        next: () => this.registered.emit(this.email),
        error: err => this.error = err.error || 'Registrazione fallita'
      });
  }
}

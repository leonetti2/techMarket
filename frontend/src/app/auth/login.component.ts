import { Component, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  @Output() loggedIn = new EventEmitter<string>();
  @Output() switchToRegister = new EventEmitter<void>();
  emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  constructor(private http: HttpClient, private router: Router) {}

  isEmailValid(): boolean {
    return this.emailPattern.test(this.email);
  }
  isPasswordValid(): boolean {
    return this.password.length > 0;
  }
  isFormValid(): boolean {
    return this.isEmailValid() && this.isPasswordValid();
  }

  login() {
    if (!this.isFormValid()) {
      this.error = 'Inserisci email e password validi.';
      return;
    }
    const payload = {
      email: this.email,
      password: this.password
    };
    this.http.post<{ token: string }>('http://localhost:8081/auth/login', payload)
      .subscribe({
        next: res => {
          localStorage.setItem('token', res.token);
          
          // Decodifico il token per ottenere il ruolo
          try {
            const payload = JSON.parse(atob(res.token.split('.')[1]));
            const role = payload.role;
            console.log('Ruolo:', role);
            
            // Navigo alla rotta corretta in base al ruolo
            if (role === 'ADMIN') {
              this.router.navigate(['/admin-products']);
            } else {
              this.router.navigate(['/home']);
            }
          } catch (error) {
            console.error('Errore decodifica token:', error);
            this.error = 'Errore durante il login';
          }
        },
        error: err => {
          console.error('Errore login:', err);
          this.error = 'Credenziali non valide';
        }
      });
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}

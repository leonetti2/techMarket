import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './auth/login.component';
import { RegisterComponent } from './auth/register.component';
import { RouterOutlet, Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, LoginComponent, RegisterComponent, RouterOutlet],
  template: `
    <ng-container *ngIf="!isAuthenticated(); else home">
        <app-login
          *ngIf="showLogin"
        (loggedIn)="onLoggedIn($event)"
        (switchToRegister)="showLogin = false; showRegister = true">
        </app-login>
        <app-register
          *ngIf="showRegister"
        (registered)="onRegistered($event)"
        (switchToLogin)="showLogin = true; showRegister = false">
        </app-register>
      <div *ngIf="welcomeMessage" class="welcome">
        {{ welcomeMessage }}
      </div>
    </ng-container>
    <ng-template #home>
      <router-outlet></router-outlet>
    </ng-template>
  `,
  styles: [`
    .welcome {
      margin-top: 16px;
      color: #388e3c;
      font-weight: bold;
      text-align: center;
    }
  `]
})
export class AppComponent {
  showLogin = true;
  showRegister = false;
  welcomeMessage = '';

  constructor(private router: Router) {}

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  onLoggedIn(event: any) {
    const email = typeof event === 'string' ? event : event?.email ?? '';
    this.welcomeMessage = `Benvenuto, ${email}!`;
    this.showLogin = false;
    this.showRegister = false;
    
    const token = localStorage.getItem('token');
    let role = '';
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        role = payload.role || (Array.isArray(payload.roles) ? payload.roles[0] : payload.roles);
        console.log('Ruolo decodificato:', role);
      } catch (error) {
        console.error('Errore decodifica token:', error);
      }
    }
    
    if (role === 'ADMIN') {
      this.router.navigate(['/admin-products']);
    } else {
      this.router.navigate(['/home']);
    }
  }

  onRegistered(event: any) {
    const email = typeof event === 'string' ? event : event?.email ?? '';
    this.welcomeMessage = `Registrazione completata per ${email}! Ora puoi effettuare il login.`;
    this.showLogin = true;
    this.showRegister = false;
  }
}

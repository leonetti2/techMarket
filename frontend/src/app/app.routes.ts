import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'auth/login',
    loadComponent: () => import('./auth/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'home',
    loadComponent: () => {
      const token = localStorage.getItem('token');
      let role = '';
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          role = payload.role || (Array.isArray(payload.roles) ? payload.roles[0] : payload.roles);
        } catch {}
      }
      if (!token) {
        window.location.href = '/auth/login';
        return Promise.resolve(null as any);
      }
      if (role === 'ADMIN') {
        window.location.href = '/admin-products';
        return Promise.resolve(null as any);
      }
      if (role !== 'USER') {
        window.location.href = '/auth/login';
        return Promise.resolve(null as any);
      }
      return import('./home/home.component').then((m) => m.HomeComponent);
    },
  },
  {
    path: 'cart',
    loadComponent: () =>
      import('./cart/cart.component').then((m) => m.CartComponent),
  },
  {
    path: 'user',
    loadComponent: () =>
      import('./user/user-area.component').then((m) => m.UserAreaComponent),
  },
  {
    path: 'user/order/:id',
    loadComponent: () => import('./user/order-details-page.component').then(m => m.OrderDetailsPageComponent),
  },
  {
    path: 'order-details/:id',
    loadComponent: () => import('./user/order-details-page.component').then(m => m.OrderDetailsPageComponent),
  },
  {
    path: 'admin-products',
    loadComponent: () => import('./admin/admin-products.component').then(m => m.AdminProductsComponent),
  },
  {
    path: 'admin-orders',
    loadComponent: () => import('./admin/admin-orders.component').then(m => m.AdminOrdersComponent),
  },
  {
    path: 'admin-dashboard',
    loadComponent: () => import('./admin/admin-dashboard.component').then(m => m.AdminDashboardComponent),
  },
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
];

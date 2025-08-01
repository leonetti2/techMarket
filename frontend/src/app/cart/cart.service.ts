import { Injectable, signal } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CartService {
  private cartCount$ = new BehaviorSubject<number>(0);

  getCartCount() {
    return this.cartCount$.asObservable();
  }

  setCartCount(count: number) {
    console.log('Set cart count:', count);
    this.cartCount$.next(count);
  }

  increment() {
    this.cartCount$.next(this.cartCount$.value + 1);
  }

  decrement() {
    this.cartCount$.next(Math.max(0, this.cartCount$.value - 1));
  }
} 
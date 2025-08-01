import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CartService } from '../cart/cart.service';
import { Observable } from 'rxjs';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProductDialogComponent } from '../product-dialog.component';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, MatToolbarModule, MatIconModule, MatButtonModule, MatInputModule, MatCardModule, NavbarComponent, MatSnackBarModule, MatDialogModule, MatSelectModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  products: any[] = [];
  search = '';
  selectedCategory = '';
  selectedBrand = '';
  sortPrice = '';
  categories: string[] = [];
  brands: string[] = [];
  cartCount$: Observable<number>;

  // Stato paginazione
  pageNumber = 0;
  pageSize = 9;
  totalPages = 1;

  constructor(private http: HttpClient, private snackBar: MatSnackBar, private cartService: CartService, private dialog: MatDialog) {
    this.cartCount$ = this.cartService.getCartCount();
  }

  ngOnInit() {
    this.loadProducts();
    this.updateCartCount();
  }

  loadProducts() {
    this.http.get<any>(`http://localhost:8081/products/paged?pageNumber=${this.pageNumber}&pageSize=${this.pageSize}`)
      .subscribe(
        data => {
          this.products = data.content;
          this.totalPages = data.totalPages;
          // Estrai categorie e marche uniche
          this.categories = Array.from(new Set(data.content.map((p: any) => p.cateogory || p.category))).filter(Boolean) as string[];
          this.brands = Array.from(new Set(data.content.map((p: any) => p.brand))).filter(Boolean) as string[];
        },
        error => {
          console.error('Errore caricamento prodotti:', error);
        }
      );
  }

  onFilterChange() {
    this.pageNumber = 0; // resetta sempre quando cambi filtro
    this.loadFilteredProducts();
  }

  loadFilteredProducts() {
    let params: any = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize
    };
    if (this.search && typeof this.search === 'string' && this.search.trim() !== '') {
      params.name = this.search.trim();
    }
    if (this.selectedCategory && typeof this.selectedCategory === 'string' && this.selectedCategory.trim() !== '') {
      params.category = this.selectedCategory.trim();
    }
    if (this.selectedBrand && typeof this.selectedBrand === 'string' && this.selectedBrand.trim() !== '') {
      params.brand = this.selectedBrand.trim();
    }
    if (this.sortPrice) {
      params.sortBy = 'price';
      params.sortDir = this.sortPrice;
    }
    this.http.get<any>(`http://localhost:8081/products/filtered`, { params }).subscribe(data => {
      this.products = data.content;
      this.totalPages = data.totalPages;
    });
  }

  goToCart() {
    window.location.href = '/cart';
  }

  goToUserArea() {
    window.location.href = '/user';
  }

  logout() {
    localStorage.removeItem('token');
    window.location.href = '/';
  }

  // Funzione per decodificare il JWT e ottenere l'email
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

  // Recupera l'id utente dal backend usando l'email estratta dal token
  private getUserId(): Promise<number> {
    const email = this.getEmailFromToken();
    if (!email) return Promise.reject('Token non valido');
    return this.http.get<any>(`http://localhost:8081/users/by-email/${encodeURIComponent(email)}`).toPromise()
      .then(user => {
        if (user && user.id) return user.id;
        throw new Error('Utente non trovato');
      });
  }

  addToCart(product: any, quantity: number = 1) {
    this.getUserId().then(userId => {
      this.http.post(`http://localhost:8081/cart/${userId}/add/${product.id}?quantity=${quantity}`, {}, { responseType: 'text' })
        .subscribe({
          next: () => {
            this.snackBar.open('Prodotto aggiunto al carrello!', '', {
              duration: 2000,
              verticalPosition: 'top',
              panelClass: 'snackbar-success'
            });
            this.updateCartCount();
          },
          error: err => {}
        });
    }).catch(err => {
      // errore silenzioso
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

  // Aggiungo metodi per la paginazione
  nextPage() {
    if (this.pageNumber < this.totalPages - 1) {
      this.pageNumber++;
      this.loadFilteredProducts();
    }
  }

  prevPage() {
    if (this.pageNumber > 0) {
      this.pageNumber--;
      this.loadFilteredProducts();
    }
  }

  getImageUrl(url: string): string {
    if (!url) return 'https://placehold.co/250x180?text=No+Image';
    if (url.startsWith('http')) return url;
    return 'http://localhost:8081' + url;
  }

  openProductDialog(product: any) {
    const dialogRef = this.dialog.open(ProductDialogComponent, {
      data: product,
      width: '400px',
      maxWidth: '95vw',
      autoFocus: false
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result && result.product && result.quantity) {
        this.addToCart(result.product, result.quantity);
      }
    });
  }
} 
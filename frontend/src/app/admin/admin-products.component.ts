import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSpinner, MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AdminNavbarComponent } from './admin-navbar.component';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule, FormsModule, MatSnackBarModule, MatProgressSpinnerModule, MatToolbarModule, AdminNavbarComponent],
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.css']
})
export class AdminProductsComponent implements OnInit {
  products: any[] = [];
  loading = false;
  showForm = false;
  showFormSpinner = false;
  editingProduct: any = null;
  formProduct: any = {};
  imageFile: File | null = null;
  imagePreview: string | null = null;
  displayedColumns = ['image', 'name', 'brand', 'category', 'price', 'quantity', 'actions'];
  searchName: string = '';
  searchCategory: string = '';
  pageNumber: number = 0;
  pageSize: number = 10;
  totalProducts: number = 0;

  constructor(private http: HttpClient, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    const params: any = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize
    };
    if (this.searchName && this.searchName.trim() !== '' && this.searchName !== 'undefined' && this.searchName !== 'null') {
      params.name = this.searchName;
    }
    if (this.searchCategory && this.searchCategory.trim() !== '' && this.searchCategory !== 'undefined' && this.searchCategory !== 'null') {
      params.category = this.searchCategory;
    }
    this.http.get<any>('http://localhost:8081/admin/products/search', { params }).subscribe({
      next: res => {
        this.products = res.content;
        this.totalProducts = res.totalElements;
      },
      error: () => {
        this.products = [];
        this.totalProducts = 0;
        this.snackBar.open('Errore nel caricamento prodotti', '', { duration: 2000, panelClass: 'snackbar-error' });
      }
    });
  }

  onSearch() {
    this.pageNumber = 0;
    this.loadProducts();
  }

  nextPage() {
    if ((this.pageNumber + 1) * this.pageSize < this.totalProducts) {
      this.pageNumber++;
      this.loadProducts();
    }
  }

  prevPage() {
    if (this.pageNumber > 0) {
      this.pageNumber--;
      this.loadProducts();
    }
  }

  openAddProduct() {
    this.editingProduct = null;
    this.formProduct = { name: '', brand: '', category: '', price: '', quantity: '', description: '' };
    this.showForm = true;
  }

  openEditProduct(product: any) {
    this.editingProduct = product;
    this.formProduct = { ...product };
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.formProduct = {};
    this.editingProduct = null;
  }

  saveProduct() {
    this.loading = true;
    if (this.editingProduct) {
      // Modifica prodotto
      this.http.put(`http://localhost:8081/admin/products/${this.editingProduct.id}`, this.formProduct)
        .subscribe({
          next: () => {
            this.snackBar.open('Prodotto aggiornato con successo!', '', { duration: 2000, panelClass: 'snackbar-success' });
            this.loadProducts();
            this.closeForm();
            this.loading = false;
          },
          error: () => {
            this.snackBar.open('Errore durante la modifica del prodotto', '', { duration: 2500, panelClass: 'snackbar-error' });
            this.loading = false;
          }
        });
    } else {
      // Aggiungi prodotto
      this.http.post('http://localhost:8081/admin/products', this.formProduct)
        .subscribe({
          next: () => {
            this.snackBar.open('Prodotto aggiunto con successo!', '', { duration: 2000, panelClass: 'snackbar-success' });
            this.loadProducts();
            this.closeForm();
            this.loading = false;
          },
          error: () => {
            this.snackBar.open('Errore durante l\'aggiunta del prodotto', '', { duration: 2500, panelClass: 'snackbar-error' });
            this.loading = false;
          }
        });
    }
  }

  deleteProduct(product: any) {
    if (confirm('Sei sicuro di voler eliminare questo prodotto?')) {
      this.loading = true;
      this.http.delete(`http://localhost:8081/admin/products/${product.id}`)
        .subscribe({
          next: () => {
            this.snackBar.open('Prodotto eliminato!', '', { duration: 2000, panelClass: 'snackbar-success' });
            this.loadProducts();
            this.loading = false;
          },
          error: () => {
            this.snackBar.open('Errore durante l\'eliminazione', '', { duration: 2500, panelClass: 'snackbar-error' });
            this.loading = false;
          }
        });
    }
  }

  logout() {
    localStorage.removeItem('token');
    window.location.href = '/';
  }

  goToOrders(event: Event) {
    event.preventDefault();
    window.location.assign('/admin-orders');
  }

  goToProducts(event: Event) {
    event.preventDefault();
    window.location.assign('/admin-products');
  }
  goToDashboard(event: Event) {
    event.preventDefault();
    window.location.assign('/admin-dashboard');
  }
  isActive(path: string): boolean {
    return window.location.pathname === path;
  }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.imageFile = file;
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = reader.result as string;
      reader.readAsDataURL(file);
    } else {
      this.imageFile = null;
      this.imagePreview = null;
    }
  }

  getTotalPages() {
    return Math.ceil(this.totalProducts / this.pageSize);
  }
} 
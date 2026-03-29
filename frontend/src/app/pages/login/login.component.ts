import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="auth-container">
      <mat-card class="auth-card">
        <mat-card-header>
          <mat-card-title>
            <mat-icon class="title-icon">task_alt</mat-icon>
            Task Manager
          </mat-card-title>
          <mat-card-subtitle>Faça login para continuar</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="onSubmit()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Login</mat-label>
              <input matInput formControlName="login" placeholder="Seu login" />
              <mat-icon matPrefix>person</mat-icon>
              <mat-error *ngIf="form.get('login')?.hasError('required')">Login obrigatório</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Senha</mat-label>
              <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" />
              <mat-icon matPrefix>lock</mat-icon>
              <button mat-icon-button matSuffix type="button" (click)="hidePassword = !hidePassword">
                <mat-icon>{{ hidePassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              <mat-error *ngIf="form.get('password')?.hasError('required')">Senha obrigatória</mat-error>
            </mat-form-field>

            <button
              mat-raised-button
              color="primary"
              class="full-width submit-btn"
              type="submit"
              [disabled]="loading || form.invalid"
            >
              <mat-spinner *ngIf="loading" diameter="20" class="inline-spinner"></mat-spinner>
              <span *ngIf="!loading">Entrar</span>
            </button>
          </form>
        </mat-card-content>

        <mat-card-actions>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .auth-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #121212;
    }
    .auth-card {
      width: 100%;
      max-width: 400px;
      padding: 16px;
      background: #1e1e1e;
    }
    mat-card-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 1.5rem;
    }
    .title-icon { color: #bb86fc; font-size: 2rem; }
    .full-width { width: 100%; margin-bottom: 12px; }
    .submit-btn { margin-top: 8px; height: 48px; font-size: 1rem; }
    .inline-spinner { display: inline-block; }
    mat-card-header { margin-bottom: 24px; }
  `]
})
export class LoginComponent {
  form = this.fb.group({
    login: ['', Validators.required],
    password: ['', Validators.required]
  });
  loading = false;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;

    this.authService.login(this.form.value as any).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/tasks']);
      },
      error: () => {
        this.loading = false;
        this.snackBar.open('Login ou senha inválidos', 'Fechar', { duration: 3000 });
      }
    });
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="auth-container">
      <mat-card class="auth-card">
        <mat-card-header>
          <mat-card-title>
            <mat-icon class="title-icon">person_add</mat-icon>
            Registrar
          </mat-card-title>
          <mat-card-subtitle>Crie sua conta</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="onSubmit()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Login</mat-label>
              <input matInput formControlName="login" />
              <mat-icon matPrefix>person</mat-icon>
              <mat-error *ngIf="form.get('login')?.hasError('required')">Login obrigatório</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>E-mail</mat-label>
              <input matInput formControlName="email" type="email" />
              <mat-icon matPrefix>email</mat-icon>
              <mat-error *ngIf="form.get('email')?.hasError('required')">E-mail obrigatório</mat-error>
              <mat-error *ngIf="form.get('email')?.hasError('email')">E-mail inválido</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Senha</mat-label>
              <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" />
              <mat-icon matPrefix>lock</mat-icon>
              <button mat-icon-button matSuffix type="button" (click)="hidePassword = !hidePassword">
                <mat-icon>{{ hidePassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              <mat-error *ngIf="form.get('password')?.hasError('required')">Senha obrigatória</mat-error>
              <mat-error *ngIf="form.get('password')?.hasError('minlength')">Mínimo 6 caracteres</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Perfil</mat-label>
              <mat-select formControlName="role">
                <mat-option value="COLLABORATOR">Colaborador</mat-option>
                <mat-option value="MANAGER">Gerente</mat-option>
                <mat-option value="ADMIN">Administrador</mat-option>
              </mat-select>
              <mat-icon matPrefix>badge</mat-icon>
              <mat-error *ngIf="form.get('role')?.hasError('required')">Perfil obrigatório</mat-error>
            </mat-form-field>

            <button
              mat-raised-button
              color="primary"
              class="full-width submit-btn"
              type="submit"
              [disabled]="loading || form.invalid"
            >
              <mat-spinner *ngIf="loading" diameter="20" class="inline-spinner"></mat-spinner>
              <span *ngIf="!loading">Registrar</span>
            </button>
          </form>
        </mat-card-content>

        <mat-card-actions>
          <p class="login-link">
            Já tem conta? <a routerLink="/login">Fazer login</a>
          </p>
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
      max-width: 420px;
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
    .login-link { text-align: center; margin: 0; }
    .login-link a { color: #bb86fc; text-decoration: none; }
    .login-link a:hover { text-decoration: underline; }
    mat-card-header { margin-bottom: 24px; }
  `]
})
export class RegisterComponent {
  form = this.fb.group({
    login: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['COLLABORATOR', Validators.required]
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

    this.authService.register(this.form.value as any).subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open('Conta criada com sucesso!', 'OK', { duration: 3000 });
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        const msg = err.status === 400 ? 'Login já cadastrado' : 'Erro ao registrar';
        this.snackBar.open(msg, 'Fechar', { duration: 3000 });
      }
    });
  }
}

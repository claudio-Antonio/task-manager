import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialogModule, MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TaskService } from '../../core/services/task.service';
import { AuthService } from '../../core/services/auth.service';
import { TaskResponseDTO, TaskRequestDTO, TaskStatus } from '../../models';

export interface TaskDialogData {
  task: TaskResponseDTO | null;
}

@Component({
  selector: 'app-task-form-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatProgressSpinnerModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data.task ? 'Editar Tarefa' : 'Nova Tarefa' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="task-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Título</mat-label>
          <input matInput formControlName="title" />
          <mat-error *ngIf="form.get('title')?.hasError('required')">Campo obrigatório</mat-error>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrição</mat-label>
          <textarea matInput formControlName="description" rows="3"></textarea>
          <mat-error *ngIf="form.get('description')?.hasError('required')">Campo obrigatório</mat-error>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Status</mat-label>
          <mat-select formControlName="status">
            <mat-option value="NOT_COMPLETE">Não Concluída</mat-option>
            <mat-option value="COMPLETE">Concluída</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>ID do Usuário</mat-label>
          <input matInput type="number" formControlName="userID" min="1" />
          <mat-error *ngIf="form.get('userID')?.hasError('required')">Campo obrigatório</mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="null">Cancelar</button>
      <button mat-raised-button color="primary" [disabled]="form.invalid" (click)="confirm()">
        {{ data.task ? 'Atualizar' : 'Criar' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`.task-form { display:flex; flex-direction:column; gap:4px; min-width:360px; padding-top:8px; } .full-width { width:100%; }`]
})
export class TaskFormDialogComponent {
  form = this.fb.group({
    title: [this.data.task?.title ?? '', Validators.required],
    description: [this.data.task?.description ?? '', Validators.required],
    status: [this.data.task?.status ?? 'NOT_COMPLETE' as TaskStatus, Validators.required],
    userID: [this.data.task?.userId ?? null, [Validators.required, Validators.min(1)]]
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<TaskFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TaskDialogData
  ) {}

  confirm(): void {
    if (this.form.valid) this.dialogRef.close(this.form.value as TaskRequestDTO);
  }
}

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [
    CommonModule, MatToolbarModule,
    RouterLink, MatButtonModule, MatIconModule,
    MatTableModule, MatDialogModule, MatSnackBarModule,
    MatProgressSpinnerModule, MatCardModule, MatTooltipModule
  ],
  template: `
    <mat-toolbar color="primary" class="toolbar">
      <mat-icon>task_alt</mat-icon>
      <span class="toolbar-title">Task Manager</span>
      <span class="spacer"></span>
      <span class="user-info">
        <mat-icon>account_circle</mat-icon>
        {{ username }}
        <span class="role-badge">{{ role }}</span>
      </span>
      <a *ngIf="isAdmin" mat-icon-button routerLink="/register" matTooltip="Cadastrar usuário">
        <mat-icon>person_add</mat-icon>
      </a>
      <button mat-icon-button (click)="logout()" matTooltip="Sair">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>

    <div class="page-container">
      <div class="page-header">
        <div>
          <h1 class="page-title">Tarefas</h1>
          <span class="page-subtitle">{{ tasks.length }} tarefa(s) encontrada(s)</span>
        </div>
        <button *ngIf="isManager" mat-raised-button color="accent" (click)="openDialog()">
          <mat-icon>add</mat-icon> Nova Tarefa
        </button>
      </div>

      <div *ngIf="loading" class="center-content">
        <mat-spinner diameter="48"></mat-spinner>
      </div>

      <div *ngIf="!loading && tasks.length === 0" class="empty-state">
        <mat-icon class="empty-icon">inbox</mat-icon>
        <p>Nenhuma tarefa encontrada</p>
      </div>

      <mat-card *ngIf="!loading && tasks.length > 0" class="table-card">
        <table mat-table [dataSource]="tasks" class="task-table">
          <ng-container matColumnDef="id">
            <th mat-header-cell *matHeaderCellDef>ID</th>
            <td mat-cell *matCellDef="let t">{{ t.id }}</td>
          </ng-container>
          <ng-container matColumnDef="title">
            <th mat-header-cell *matHeaderCellDef>Título</th>
            <td mat-cell *matCellDef="let t"><strong>{{ t.title }}</strong></td>
          </ng-container>
          <ng-container matColumnDef="description">
            <th mat-header-cell *matHeaderCellDef>Descrição</th>
            <td mat-cell *matCellDef="let t" class="desc-cell">{{ t.description }}</td>
          </ng-container>
          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef>Status</th>
            <td mat-cell *matCellDef="let t">
              <span class="status-badge" [ngClass]="statusClass(t.status)">
                {{ statusLabel(t.status) }}
              </span>
            </td>
          </ng-container>
          <ng-container matColumnDef="userId">
            <th mat-header-cell *matHeaderCellDef>Usuário ID</th>
            <td mat-cell *matCellDef="let t">{{ t.userId }}</td>
          </ng-container>
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef>Ações</th>
            <td mat-cell *matCellDef="let t">
              <ng-container *ngIf="isManager; else noAction">
                <button mat-icon-button color="accent" (click)="openDialog(t)" matTooltip="Editar">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button color="warn" (click)="deleteTask(t.id)" matTooltip="Excluir">
                  <mat-icon>delete</mat-icon>
                </button>
              </ng-container>
              <ng-template #noAction><span class="muted">—</span></ng-template>
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="columns"></tr>
          <tr mat-row *matRowDef="let row; columns: columns;"></tr>
        </table>
      </mat-card>
    </div>
  `,
  styles: [`
    .toolbar { gap: 8px; }
    .toolbar-title { font-size: 1.1rem; font-weight: 600; margin-left: 4px; }
    .spacer { flex: 1 1 auto; }
    .user-info { display: flex; align-items: center; gap: 6px; font-size: 0.875rem; margin-right: 8px; }
    .role-badge { background: rgba(255,255,255,0.15); padding: 2px 8px; border-radius: 12px; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; }
    .page-container { padding: 32px 24px; max-width: 1200px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 28px; }
    .page-title { margin: 0 0 4px; font-size: 1.75rem; font-weight: 700; }
    .page-subtitle { color: rgba(255,255,255,0.5); font-size: 0.875rem; }
    .center-content { display: flex; justify-content: center; padding: 80px; }
    .empty-state { text-align: center; padding: 80px 24px; color: rgba(255,255,255,0.4); }
    .empty-icon { font-size: 72px; width: 72px; height: 72px; margin-bottom: 16px; }
    .empty-state p { font-size: 1rem; margin-bottom: 20px; }
    .table-card { overflow: auto; border-radius: 8px; }
    .task-table { width: 100%; }
    .desc-cell { max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
    .muted { color: rgba(255,255,255,0.3); }
    .status-badge { padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.04em; }
    .not-complete { background: rgba(255,193,7,0.15); color: #ffc107; border: 1px solid rgba(255,193,7,0.3); }
    
    .complete { background: rgba(76,175,80,0.15); color: #66bb6a; border: 1px solid rgba(76,175,80,0.3); }
    :host ::ng-deep .mat-mdc-row:hover { background: rgba(255,255,255,0.04); }
  `]
})
export class TasksComponent implements OnInit {
  tasks: TaskResponseDTO[] = [];
  columns = ['id', 'title', 'description', 'status', 'userId', 'actions'];
  loading = false;
  isManager = false;
  isAdmin = false;
  username = '';
  role = '';

  constructor(
    private taskService: TaskService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const decoded = this.authService.getDecodedToken();
    this.username = decoded?.sub ?? 'Usuário';
    this.role = decoded?.role ?? '';
    this.isManager = this.authService.isManager();
    this.isAdmin = this.authService.getRole() === 'ADMIN';
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    this.taskService.findAll().subscribe({
      next: (data) => { this.tasks = data; this.loading = false; },
      error: () => { this.loading = false; this.snackBar.open('Erro ao carregar tarefas', 'Fechar', { duration: 3000 }); }
    });
  }

  openDialog(task?: TaskResponseDTO): void {
    const ref = this.dialog.open(TaskFormDialogComponent, {
      width: '480px',
      data: { task: task ?? null } as TaskDialogData
    });

    ref.afterClosed().subscribe((result: TaskRequestDTO | null) => {
      if (!result) return;
      const req$ = task
        ? this.taskService.update(task.id, result)
        : this.taskService.create(result);

      req$.subscribe({
        next: () => {
          this.snackBar.open(task ? 'Tarefa atualizada!' : 'Tarefa criada!', 'OK', { duration: 3000 });
          this.loadTasks();
        },
        error: () => this.snackBar.open('Erro ao salvar tarefa', 'Fechar', { duration: 3000 })
      });
    });
  }

  deleteTask(id: number): void {
    if (!confirm('Deseja excluir esta tarefa?')) return;
    this.taskService.delete(id).subscribe({
      next: () => { this.snackBar.open('Tarefa excluída', 'OK', { duration: 3000 }); this.loadTasks(); },
      error: () => this.snackBar.open('Erro ao excluir', 'Fechar', { duration: 3000 })
    });
  }

  logout(): void { this.authService.logout(); }

  statusLabel(status: TaskStatus): string {
    return ({ COMPLETE: 'Concluída', NOT_COMPLETE: 'Não Concluída' } as Record<string,string>)[status] ?? status;
  }

  statusClass(status: TaskStatus): string {
    return status.toLowerCase().replace('_', '-');
  }
}

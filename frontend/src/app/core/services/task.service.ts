import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskRequestDTO, TaskResponseDTO } from '../../models';
import { environment } from '../../../environments/environment';

// Re-export models for convenience
export type { TaskRequestDTO, TaskResponseDTO } from '../../models';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<TaskResponseDTO[]> {
    return this.http.get<TaskResponseDTO[]>(this.apiUrl);
  }

  findById(id: number): Observable<TaskResponseDTO> {
    return this.http.get<TaskResponseDTO>(`${this.apiUrl}/${id}`);
  }

  create(task: TaskRequestDTO): Observable<TaskResponseDTO> {
    return this.http.post<TaskResponseDTO>(`${this.apiUrl}/new-task`, task);
  }

  update(id: number, task: TaskRequestDTO): Observable<TaskResponseDTO> {
    return this.http.put<TaskResponseDTO>(`${this.apiUrl}/update-task/${id}`, task);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

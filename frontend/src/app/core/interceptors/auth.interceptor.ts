import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

const NO_TOKEN_ROUTES = ['/auth/login'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const isPublic = NO_TOKEN_ROUTES.some(route => req.url.includes(route));

  if (!isPublic) {
    const token = authService.getToken();
    if (token) {
      const cloned = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
      return next(cloned);
    }
  }

  return next(req);
};

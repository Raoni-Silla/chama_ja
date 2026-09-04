import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { LoginService } from '../../service/login-service';

interface TokenPayload {
  ROLE?: string;
  exp?: number;
}

export const guestGuardGuard: CanActivateFn = () => {
  const router = inject(Router);
  const loginService = inject(LoginService);

  const token = loginService.obterToken();

  // Sem token pode acessar login, cadastro e landing page
  if (!token) {
    return true;
  }

  try {
    const payload = jwtDecode<TokenPayload>(token);

    // Verifica se o token expirou
    if (payload.exp && payload.exp * 1000 < Date.now()) {
      sessionStorage.removeItem('token_cadastro_chamaja');
      return true;
    }

    if (payload.ROLE === 'USUARIO') {
      return router.createUrlTree(['/homepageuser']);
    }

    if (payload.ROLE === 'PRESTADOR') {
      return router.createUrlTree(['/homepageprestador']);
    }

    // Token sem uma role reconhecida
    sessionStorage.removeItem('token_cadastro_chamaja');
    return true;
  } catch {
    // Token inválido
    sessionStorage.removeItem('token_cadastro_chamaja');
    return true;
  }
};
import { CanActivateFn, Router } from '@angular/router';
import { LoginService } from '../../service/login-service';
import { inject } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const loginService = inject(LoginService);

  //primero verifica se o usuário está logado, ou seja, se existe um token
  const token = loginService.obterToken();

  //se não existir o token, redireciona para a página de login
  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  try {
    //decodifica o token para obter as roles do usuário
    const payloadAberto: any = jwtDecode(token);
    //verifica se a role do usuário é igual a role exigida pela rota
    const roles = payloadAberto.ROLE;
    //pega a role exigida pela rota
    const rolesPermitidas = route.data['roles'];

    if (rolesPermitidas.includes(roles)) {
      return true;
    } else {
      router.navigate(['/login']);
      return false;
    }
  } catch (error) {
    router.navigate(['/login']);
    return false;
  }
};

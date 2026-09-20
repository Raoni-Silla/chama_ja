import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  //pega o token na memoria do navegador, caso exista
  const token = sessionStorage.getItem('token_cadastro_chamaja');
  const router = inject(Router);

  if (req.url.includes('geoapify.com')) {
    return next(req);
  }

  //se existir o token, adiciona ele no header da requisição
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    //passa a requisição com o token adicionado para o próximo interceptor ou para o backend
    return next(authReq).pipe(
      catchError((erro : HttpErrorResponse) =>{
        if(erro.status === 401 ){
          console.warn('Sessão expirada')
          sessionStorage.removeItem('token_cadastro_chamaja');
          router.navigate(['/login']);
        }
        return throwError(() => erro);
      })
    );
  }

  //se não existir o token, passa a requisição original para o próximo interceptor ou para o backend
  return next(req);
};

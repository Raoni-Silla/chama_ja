import {
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';

import {
  NavigationEnd,
  Router,
  RouterOutlet,
} from '@angular/router';

import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import * as AOS from 'aos';

import { Navbarlogged } from './components/navbarlogged/navbarlogged';
import { Navbarloggedprestador } from './components/navbarloggedprestador/navbarloggedprestador';
import { LoginService } from './service/login-service';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    Navbarlogged,
    Navbarloggedprestador,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  protected readonly title = signal('chamajafront');

  public loginService = inject(LoginService);

  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  role: string = '';

  ngOnInit(): void {
    AOS.init({
      duration: 800,
      easing: 'ease-out',
      once: true,
      offset: 100,
    });

    this.atualizarRole();

    this.router.events
      .pipe(
        filter(
          (evento): evento is NavigationEnd =>
            evento instanceof NavigationEnd,
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => {
        this.atualizarRole();
      });
  }

  private atualizarRole(): void {
    this.role = this.loginService.obterRoleUsuario() ?? '';
  }
}
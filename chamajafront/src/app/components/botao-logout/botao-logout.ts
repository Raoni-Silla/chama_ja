import { Component, inject } from '@angular/core';
import { LoginService } from '../../service/login-service';

@Component({
  selector: 'app-botao-logout',
  imports: [],
  templateUrl: './botao-logout.html',
  styleUrl: './botao-logout.css',
})
export class BotaoLogout {
  private loginService = inject(LoginService);

  logout() {
    this.loginService.limparToken();
    window.location.reload();
  }
}

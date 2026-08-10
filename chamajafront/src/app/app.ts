import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import * as AOS from 'aos';
import { Navbarlogged } from './components/navbarlogged/navbarlogged';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbarlogged],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('chamajafront');
  ngOnInit() {
    AOS.init({
      duration: 800,     // Duração da animação (em milissegundos)
      easing: 'ease-out', // Suavidade da animação
      once: true,         // Se 'true', a animação ocorre apenas uma vez ao rolar para baixo
      offset: 100         // Distância (em px) do elemento para o fim da tela para acionar a animação
    });
  }
}

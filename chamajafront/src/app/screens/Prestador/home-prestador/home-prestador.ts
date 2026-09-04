import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePickerModule } from 'primeng/datepicker';

@Component({
  selector: 'app-home-prestador',
  imports: [FormsModule, DatePickerModule],
  templateUrl: './home-prestador.html',
  styleUrl: './home-prestador.css',
})
export class HomePrestador {
  dataSelecionada: Date = new Date(2026, 1, 23);
}

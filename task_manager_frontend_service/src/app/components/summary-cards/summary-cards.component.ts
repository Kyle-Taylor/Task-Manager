import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

import { SummaryCard } from '../../shared/dashboard.types';

@Component({
  selector: 'app-summary-cards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './summary-cards.component.html',
  styleUrl: './summary-cards.component.scss'
})
export class SummaryCardsComponent {
  @Input({ required: true }) cards: SummaryCard[] = [];
}

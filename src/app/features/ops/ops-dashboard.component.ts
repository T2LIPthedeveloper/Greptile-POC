import { Component, inject, OnInit, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-ops-dashboard',
  standalone: true,
  imports: [MatCardModule],
  template: `
    <h1>Ops Dashboard</h1>
    <div class="grid">
      <mat-card><h3>Usage events</h3><p class="stat">{{ usageTotal() }}</p></mat-card>
      <mat-card>
        <h3>By type</h3>
        @for (entry of aggregateEntries(); track entry.key) {
          <p>{{ entry.key }}: {{ entry.value }}</p>
        }
      </mat-card>
      <mat-card>
        <h3>Proxy metrics</h3>
        <p class="hint">Prometheus: /actuator/prometheus on proxy-service</p>
        <p class="hint">Rate limit: 120 req/min per org:provider</p>
      </mat-card>
    </div>
  `,
  styles: [
    `
      .grid {
        display: grid;
        gap: 1rem;
        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      }
      .stat {
        font-size: 2rem;
        font-weight: 600;
      }
      .hint {
        color: #666;
        font-size: 0.9rem;
      }
    `,
  ],
})
export class OpsDashboardComponent implements OnInit {
  private api = inject(ApiService);
  usageTotal = signal(0);
  aggregateEntries = signal<{ key: string; value: number }[]>([]);

  ngOnInit(): void {
    this.api.usageSummary().subscribe((s) => this.usageTotal.set(s.totalEvents));
    this.api.usageAggregate().subscribe((agg) => {
      this.aggregateEntries.set(Object.entries(agg).map(([key, value]) => ({ key, value })));
    });
  }
}

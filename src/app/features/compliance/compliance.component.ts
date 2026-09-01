import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-compliance',
  standalone: true,
  imports: [MatCardModule, MatTableModule, MatButtonModule],
  template: `
    <h1>Compliance Export</h1>
    <mat-card>
      <p>PII-redacted audit export for SOC2-style review.</p>
      <button mat-flat-button color="primary" (click)="load()">Load redacted export</button>
      <table mat-table [dataSource]="events()" class="full-width mt">
        <ng-container matColumnDef="action"><th mat-header-cell *matHeaderCellDef>Action</th><td mat-cell *matCellDef="let e">{{ e['action'] }}</td></ng-container>
        <ng-container matColumnDef="actor"><th mat-header-cell *matHeaderCellDef>Actor</th><td mat-cell *matCellDef="let e">{{ e['actorId'] }}</td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; } .mt { margin-top: 1rem; }'],
})
export class ComplianceComponent implements OnInit {
  private api = inject(ApiService);
  events = signal<Record<string, unknown>[]>([]);
  cols = ['action', 'actor'];

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.exportComplianceAudit().subscribe((e) => this.events.set(e));
  }
}

import { Component, inject, OnInit, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-audit-log',
  standalone: true,
  imports: [MatCardModule, MatTableModule],
  template: `
    <h1>Audit Log</h1>
    <mat-card>
      <table mat-table [dataSource]="events()" class="full-width">
        <ng-container matColumnDef="action"><th mat-header-cell *matHeaderCellDef>Action</th><td mat-cell *matCellDef="let e">{{ e['action'] }}</td></ng-container>
        <ng-container matColumnDef="actor"><th mat-header-cell *matHeaderCellDef>Actor</th><td mat-cell *matCellDef="let e">{{ e['actorType'] }} {{ e['actorId'] }}</td></ng-container>
        <ng-container matColumnDef="resource"><th mat-header-cell *matHeaderCellDef>Resource</th><td mat-cell *matCellDef="let e">{{ e['resourceType'] }}</td></ng-container>
        <ng-container matColumnDef="time"><th mat-header-cell *matHeaderCellDef>Time</th><td mat-cell *matCellDef="let e">{{ e['createdAt'] }}</td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; }'],
})
export class AuditLogComponent implements OnInit {
  private api = inject(ApiService);
  events = signal<Record<string, unknown>[]>([]);
  cols = ['action', 'actor', 'resource', 'time'];

  ngOnInit(): void {
    this.api.listAuditEvents().subscribe((e) => this.events.set(e));
  }
}

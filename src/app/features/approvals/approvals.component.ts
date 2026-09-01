import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { ApiService, Approval } from '../../core/services/api.service';

@Component({
  selector: 'app-approvals',
  standalone: true,
  imports: [MatCardModule, MatTableModule, MatButtonModule],
  template: `
    <h1>Approval Inbox</h1>
    <mat-card>
      <table mat-table [dataSource]="approvals()" class="full-width">
        <ng-container matColumnDef="versionId"><th mat-header-cell *matHeaderCellDef>Version</th><td mat-cell *matCellDef="let a">{{ a.versionId }}</td></ng-container>
        <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Status</th><td mat-cell *matCellDef="let a">{{ a.status }}</td></ng-container>
        <ng-container matColumnDef="actions"><th mat-header-cell *matHeaderCellDef></th><td mat-cell *matCellDef="let a"><button mat-button color="primary" (click)="approve(a)">Approve</button></td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; }'],
})
export class ApprovalsComponent implements OnInit {
  private api = inject(ApiService);
  private snack = inject(MatSnackBar);
  approvals = signal<Approval[]>([]);
  cols = ['versionId', 'status', 'actions'];

  ngOnInit(): void {
    this.api.listPendingApprovals().subscribe((a) => this.approvals.set(a));
  }

  approve(a: Approval): void {
    this.api.approveVersion(a.id, 'Approved via UI').subscribe(() => {
      this.approvals.update((list) => list.filter((x) => x.id !== a.id));
      this.snack.open('Version approved', 'OK', { duration: 3000 });
    });
  }
}

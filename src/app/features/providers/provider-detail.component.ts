import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { ApiService, Provider, Version } from '../../core/services/api.service';

@Component({
  selector: 'app-provider-detail',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatButtonModule, MatTableModule, MatChipsModule],
  template: `
    <a mat-button routerLink="/providers">← Back</a>
    <h1>{{ provider()?.displayName }}</h1>
    <p class="muted">{{ provider()?.slug }} · Usage events: {{ usageEvents() }}</p>
    <mat-card>
      <h3>Versions</h3>
      <table mat-table [dataSource]="versions()" class="full-width">
        <ng-container matColumnDef="label">
          <th mat-header-cell *matHeaderCellDef>Label</th>
          <td mat-cell *matCellDef="let v">{{ v.versionLabel }}</td>
        </ng-container>
        <ng-container matColumnDef="status">
          <th mat-header-cell *matHeaderCellDef>Status</th>
          <td mat-cell *matCellDef="let v"><mat-chip>{{ v.status }}</mat-chip></td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef>Actions</th>
          <td mat-cell *matCellDef="let v">
            <button mat-button (click)="validate(v)">Validate</button>
            @if (v.status === 'DRAFT') {
              <button mat-button color="primary" (click)="publish(v)">Publish</button>
            }
            @if (v.status === 'PUBLISHED') {
              <button mat-button (click)="deprecate(v)">Deprecate</button>
            }
          </td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; } .muted { color: #666; }'],
})
export class ProviderDetailComponent implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private snack = inject(MatSnackBar);

  provider = signal<Provider | null>(null);
  versions = signal<Version[]>([]);
  usageEvents = signal(0);
  cols = ['label', 'status', 'actions'];
  providerId = '';

  ngOnInit(): void {
    this.providerId = this.route.snapshot.paramMap.get('id') ?? '';
    this.api.getProvider(this.providerId).subscribe((p) => this.provider.set(p));
    this.api.listVersions(this.providerId).subscribe((v) => this.versions.set(v));
    this.api.usageSummary().subscribe((s) => this.usageEvents.set(s.totalEvents));
  }

  validate(v: Version): void {
    this.api.validateVersion(this.providerId, v.id).subscribe((r) => {
      this.snack.open(r.valid ? 'Validation passed' : r.errors.join('; '), 'OK', { duration: 4000 });
    });
  }

  publish(v: Version): void {
    this.api.publishVersion(this.providerId, v.id).subscribe((updated) => {
      this.versions.update((list) => list.map((x) => (x.id === updated.id ? updated : x)));
      this.snack.open(`Status: ${updated.status}`, 'OK', { duration: 3000 });
    });
  }

  deprecate(v: Version): void {
    this.api.deprecateVersion(this.providerId, v.id).subscribe((updated) => {
      this.versions.update((list) => list.map((x) => (x.id === updated.id ? updated : x)));
    });
  }
}

import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { ApiService, Provider } from '../../core/services/api.service';

@Component({
  selector: 'app-provider-list',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatButtonModule, MatTableModule, MatChipsModule],
  template: `
    <div class="header">
      <h1>MCP Providers</h1>
      <a mat-flat-button color="primary" routerLink="/providers/new">Onboard Provider</a>
    </div>
    <mat-card>
      <table mat-table [dataSource]="providers()" class="full-width">
        <ng-container matColumnDef="slug">
          <th mat-header-cell *matHeaderCellDef>Slug</th>
          <td mat-cell *matCellDef="let p">{{ p.slug }}</td>
        </ng-container>
        <ng-container matColumnDef="displayName">
          <th mat-header-cell *matHeaderCellDef>Name</th>
          <td mat-cell *matCellDef="let p">{{ p.displayName }}</td>
        </ng-container>
        <ng-container matColumnDef="status">
          <th mat-header-cell *matHeaderCellDef>Status</th>
          <td mat-cell *matCellDef="let p">
            <mat-chip>{{ p.status }}</mat-chip>
          </td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let p">
            <a mat-button routerLink="/providers/{{ p.id }}">Versions</a>
          </td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="columns"></tr>
        <tr mat-row *matRowDef="let row; columns: columns"></tr>
      </table>
      @if (providers().length === 0) {
        <p class="empty">No providers yet. Start onboarding your first MCP.</p>
      }
    </mat-card>
  `,
  styles: [
    `
      .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
      }
      .full-width {
        width: 100%;
      }
      .empty {
        padding: 1rem;
        color: #666;
      }
    `,
  ],
})
export class ProviderListComponent implements OnInit {
  private api = inject(ApiService);
  providers = signal<Provider[]>([]);
  columns = ['slug', 'displayName', 'status', 'actions'];

  ngOnInit(): void {
    this.api.listProviders().subscribe((list) => this.providers.set(list));
  }
}

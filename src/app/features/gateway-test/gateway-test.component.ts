import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-gateway-test',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  template: `
    <h1>Gateway Test Console</h1>
    <mat-card>
      <mat-card-content>
        <form [formGroup]="form" class="form-grid">
          <mat-form-field appearance="outline">
            <mat-label>Org slug</mat-label>
            <input matInput formControlName="orgSlug" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Provider slug</mat-label>
            <input matInput formControlName="providerSlug" />
          </mat-form-field>
        </form>
        <p class="url">Gateway URL: <code>{{ gatewayUrl() }}</code></p>
        <button mat-flat-button color="primary" (click)="checkHealth()" [disabled]="loading()">
          Test proxy health
        </button>
        <button mat-stroked-button (click)="sendInitialize()" [disabled]="loading()">
          Send Initialize
        </button>
        @if (lastResponse()) {
          <pre class="response">{{ lastResponse() }}</pre>
        }
      </mat-card-content>
    </mat-card>
  `,
  styles: [
    `
      .form-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
      }
      .url {
        margin: 1rem 0;
      }
      .response {
        margin-top: 1rem;
        padding: 1rem;
        background: #f5f5f5;
        border-radius: 4px;
        overflow: auto;
        max-height: 320px;
      }
    `,
  ],
})
export class GatewayTestComponent {
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private snackBar = inject(MatSnackBar);

  loading = signal(false);
  lastResponse = signal('');

  form = this.fb.nonNullable.group({
    orgSlug: ['demo', Validators.required],
    providerSlug: ['weather-api', Validators.required],
  });

  gatewayUrl(): string {
    const { orgSlug, providerSlug } = this.form.getRawValue();
    return `${environment.proxyBaseUrl}/mcp/${orgSlug}/${providerSlug}`;
  }

  checkHealth(): void {
    this.loading.set(true);
    this.http.get<{ status: string }>(`${environment.proxyBaseUrl}/mcp/health`).subscribe({
      next: (res) => {
        this.lastResponse.set(JSON.stringify(res, null, 2));
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.snackBar.open('Health check failed', 'Close', { duration: 3000 });
        this.lastResponse.set(JSON.stringify(err.error ?? err.message, null, 2));
      },
    });
  }

  sendInitialize(): void {
    this.loading.set(true);
    const body = JSON.stringify({
      jsonrpc: '2.0',
      id: 1,
      method: 'initialize',
      params: {
        protocolVersion: '2025-11-25',
        capabilities: {},
        clientInfo: { name: 'gateway-test-console', version: '1.0.0' },
      },
    });
    this.http.post(this.gatewayUrl(), body, { responseType: 'text' }).subscribe({
      next: (res) => {
        this.lastResponse.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.lastResponse.set(JSON.stringify(err.error ?? err.message, null, 2));
      },
    });
  }
}

import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-provider-wizard',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
  ],
  template: `
    <h1>Onboard MCP Provider</h1>
    <mat-card>
      <mat-stepper linear #stepper>
        <mat-step [stepControl]="metadataForm" label="Metadata">
          <form [formGroup]="metadataForm">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Slug</mat-label>
              <input matInput formControlName="slug" placeholder="weather-api" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Display name</mat-label>
              <input matInput formControlName="displayName" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Description</mat-label>
              <textarea matInput formControlName="description" rows="3"></textarea>
            </mat-form-field>
            <button mat-button matStepperNext>Next</button>
          </form>
        </mat-step>

        <mat-step [stepControl]="endpointForm" label="Endpoint">
          <form [formGroup]="endpointForm">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Transport</mat-label>
              <mat-select formControlName="transport">
                <mat-option value="STREAMABLE_HTTP">Streamable HTTP</mat-option>
                <mat-option value="LEGACY_SSE">Legacy SSE</mat-option>
              </mat-select>
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Upstream base URL</mat-label>
              <input matInput formControlName="baseUrl" placeholder="https://mcp.example.com/mcp" />
            </mat-form-field>
            <button mat-button matStepperPrevious>Back</button>
            <button mat-button matStepperNext>Next</button>
          </form>
        </mat-step>

        <mat-step [stepControl]="credentialForm" label="Credentials">
          <form [formGroup]="credentialForm">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Credential name</mat-label>
              <input matInput formControlName="name" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>API key / secret</mat-label>
              <input matInput type="password" formControlName="secretValue" />
            </mat-form-field>
            <p class="hint">Secrets are encrypted and never shown again.</p>
            <button mat-button matStepperPrevious>Back</button>
            <button mat-button matStepperNext>Next</button>
          </form>
        </mat-step>

        <mat-step label="Contracts">
          <form [formGroup]="contractForm">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Version label</mat-label>
              <input matInput formControlName="versionLabel" placeholder="1.0.0" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Sample tool name</mat-label>
              <input matInput formControlName="toolName" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>Input schema (JSON)</mat-label>
              <textarea matInput formControlName="inputSchema" rows="4"></textarea>
            </mat-form-field>
            <button mat-button matStepperPrevious>Back</button>
            <button mat-button matStepperNext>Next</button>
          </form>
        </mat-step>

        <mat-step label="Review">
          <p>Ready to register <strong>{{ metadataForm.value.displayName }}</strong> ({{ metadataForm.value.slug }}).</p>
          <button mat-button matStepperPrevious>Back</button>
          <button mat-flat-button color="primary" (click)="submit()" [disabled]="loading()">
            {{ loading() ? 'Saving…' : 'Complete onboarding' }}
          </button>
        </mat-step>
      </mat-stepper>
    </mat-card>
  `,
  styles: [
    `
      .full-width {
        width: 100%;
        margin-bottom: 0.5rem;
      }
      .hint {
        font-size: 0.85rem;
        color: #666;
        margin-bottom: 1rem;
      }
    `,
  ],
})
export class ProviderWizardComponent {
  private fb = inject(FormBuilder);
  private api = inject(ApiService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  loading = signal(false);

  metadataForm = this.fb.nonNullable.group({
    slug: ['', [Validators.required, Validators.pattern(/^[a-z0-9-]+$/)]],
    displayName: ['', Validators.required],
    description: [''],
  });

  endpointForm = this.fb.nonNullable.group({
    transport: ['STREAMABLE_HTTP', Validators.required],
    baseUrl: ['', Validators.required],
  });

  credentialForm = this.fb.nonNullable.group({
    name: ['upstream-key', Validators.required],
    secretValue: ['', Validators.required],
  });

  contractForm = this.fb.nonNullable.group({
    versionLabel: ['1.0.0', Validators.required],
    toolName: ['sample_tool', Validators.required],
    inputSchema: ['{"type":"object","properties":{}}', Validators.required],
  });

  submit(): void {
    this.loading.set(true);
    const meta = this.metadataForm.getRawValue();
    this.api.createProvider(meta).subscribe({
      next: (provider) => {
        this.api.createVersion(provider.id, { versionLabel: this.contractForm.value.versionLabel! }).subscribe({
          next: (version) => {
            const ep = this.endpointForm.getRawValue();
            this.api.addEndpoint(provider.id, version.id, ep).subscribe({
              next: () => {
                const cred = this.credentialForm.getRawValue();
                this.api
                  .createCredential({
                    name: cred.name,
                    credentialType: 'API_KEY',
                    secretValue: cred.secretValue,
                  })
                  .subscribe({
                    next: (credential) => {
                      const credId = (credential as { id: string }).id;
                      this.api.linkCredential(provider.id, version.id, credId).subscribe({
                        next: () => {
                          const tool = this.contractForm.getRawValue();
                          this.api
                            .addTool(provider.id, version.id, {
                              toolName: tool.toolName!,
                              inputSchema: tool.inputSchema!,
                            })
                            .subscribe({
                              next: () => {
                                this.snackBar.open('Provider onboarded', 'Close', { duration: 3000 });
                                this.router.navigate(['/providers']);
                              },
                              error: (e) => this.handleError(e),
                              complete: () => this.loading.set(false),
                            });
                        },
                        error: (e) => this.handleError(e),
                      });
                    },
                    error: (e) => this.handleError(e),
                  });
              },
              error: (e) => this.handleError(e),
            });
          },
          error: (e) => this.handleError(e),
        });
      },
      error: (e) => this.handleError(e),
    });
  }

  private handleError(err: { error?: { message?: string } }): void {
    this.loading.set(false);
    this.snackBar.open(err.error?.message ?? 'Request failed', 'Close', { duration: 4000 });
  }
}

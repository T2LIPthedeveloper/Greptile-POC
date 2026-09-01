import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService, Provider } from '../../core/services/api.service';

@Component({
  selector: 'app-subscriptions',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  template: `
    <h1>Subscription Wizard</h1>
    <mat-card>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Consumer</mat-label>
          <mat-select formControlName="consumerId">
            @for (c of consumers(); track c.id) {
              <mat-option [value]="c.id">{{ c.displayName }} ({{ c.slug }})</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Provider</mat-label>
          <mat-select formControlName="providerId">
            @for (p of providers(); track p.id) {
              <mat-option [value]="p.id">{{ p.displayName }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Version (optional pin)</mat-label>
          <mat-select formControlName="versionId">
            <mat-option value="">Latest published</mat-option>
            @for (v of versions(); track v.id) {
              <mat-option [value]="v.id">{{ v.versionLabel }} ({{ v.status }})</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <button mat-flat-button color="primary" type="submit">Create subscription</button>
      </form>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; }'],
})
export class SubscriptionsComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  private snack = inject(MatSnackBar);

  consumers = signal<{ id: string; slug: string; displayName: string }[]>([]);
  providers = signal<Provider[]>([]);
  versions = signal<{ id: string; versionLabel: string; status: string }[]>([]);

  form = this.fb.nonNullable.group({
    consumerId: ['', Validators.required],
    providerId: ['', Validators.required],
    versionId: [''],
  });

  ngOnInit(): void {
    this.api.listConsumers().subscribe((c) => this.consumers.set(c));
    this.api.listProviders().subscribe((p) => this.providers.set(p));
    this.form.controls.providerId.valueChanges.subscribe((id) => {
      if (id) this.api.listVersions(id).subscribe((v) => this.versions.set(v));
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    const { consumerId, providerId, versionId } = this.form.getRawValue();
    const body = versionId ? { providerId, versionId } : { providerId };
    this.api.createSubscription(consumerId, body).subscribe(() => {
      this.snack.open('Subscription created', 'OK', { duration: 3000 });
    });
  }
}

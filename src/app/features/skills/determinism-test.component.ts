import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ApiService, Skill } from '../../core/services/api.service';

@Component({
  selector: 'app-determinism-test',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  template: `
    <h1>Skill Determinism Test</h1>
    <mat-card>
      <form [formGroup]="form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Skill</mat-label>
          <mat-select formControlName="skillId">
            @for (s of skills(); track s.id) {
              <mat-option [value]="s.id">{{ s.slug }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Idempotency key</mat-label>
          <input matInput formControlName="idempotencyKey" />
        </mat-form-field>
      </form>
      <button mat-flat-button color="primary" (click)="invokeTwice()">Invoke twice (expect replay)</button>
      <pre class="result">{{ result() }}</pre>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; } .result { margin-top: 1rem; background: #f5f5f5; padding: 1rem; }'],
})
export class DeterminismTestComponent {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  skills = signal<Skill[]>([]);
  result = signal('');
  form = this.fb.nonNullable.group({
    skillId: '',
    idempotencyKey: 'test-key-1',
  });

  constructor() {
    this.api.listSkills().subscribe((s) => {
      this.skills.set(s);
      if (s.length) this.form.patchValue({ skillId: s[0].id });
    });
  }

  invokeTwice(): void {
    const { skillId, idempotencyKey } = this.form.getRawValue();
    const body = {
      idempotencyKey,
      deterministicSeed: 'seed-42',
      input: { city: 'NYC' },
    };
    this.api.invokeSkill(skillId, body).subscribe((r1) => {
      this.api.invokeSkill(skillId, body).subscribe((r2) => {
        this.result.set(JSON.stringify({ first: r1, second: r2 }, null, 2));
      });
    });
  }
}

import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-consumers',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  template: `
    <h1>Consumers</h1>
    <mat-card>
      <table mat-table [dataSource]="consumers()" class="full-width">
        <ng-container matColumnDef="slug"><th mat-header-cell *matHeaderCellDef>Slug</th><td mat-cell *matCellDef="let c">{{ c.slug }}</td></ng-container>
        <ng-container matColumnDef="displayName"><th mat-header-cell *matHeaderCellDef>Name</th><td mat-cell *matCellDef="let c">{{ c.displayName }}</td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
    <mat-card class="mt">
      <h3>Create consumer</h3>
      <form [formGroup]="form" (ngSubmit)="create()">
        <mat-form-field appearance="outline" class="full-width"><mat-label>Slug</mat-label><input matInput formControlName="slug" /></mat-form-field>
        <mat-form-field appearance="outline" class="full-width"><mat-label>Name</mat-label><input matInput formControlName="displayName" /></mat-form-field>
        <button mat-flat-button color="primary" type="submit">Create</button>
      </form>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; } .mt { margin-top: 1rem; }'],
})
export class ConsumersComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  consumers = signal<{ id: string; slug: string; displayName: string; status: string }[]>([]);
  cols = ['slug', 'displayName'];
  form = this.fb.nonNullable.group({ slug: ['', Validators.required], displayName: ['', Validators.required] });

  ngOnInit(): void {
    this.api.listConsumers().subscribe((c) => this.consumers.set(c));
  }

  create(): void {
    if (this.form.invalid) return;
    this.api.createConsumer(this.form.getRawValue()).subscribe(() => {
      this.api.listConsumers().subscribe((c) => this.consumers.set(c));
      this.form.reset();
    });
  }
}

import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { ApiService, Skill } from '../../core/services/api.service';

@Component({
  selector: 'app-skills',
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
    <h1>Skills</h1>
    <mat-card>
      <table mat-table [dataSource]="skills()" class="full-width">
        <ng-container matColumnDef="slug"><th mat-header-cell *matHeaderCellDef>Slug</th><td mat-cell *matCellDef="let s">{{ s.slug }}</td></ng-container>
        <ng-container matColumnDef="name"><th mat-header-cell *matHeaderCellDef>Name</th><td mat-cell *matCellDef="let s">{{ s.displayName }}</td></ng-container>
        <ng-container matColumnDef="actions"><th mat-header-cell *matHeaderCellDef></th><td mat-cell *matCellDef="let s"><button mat-button (click)="testInvoke(s)">Test</button></td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
    <mat-card class="mt">
      <h3>Create skill</h3>
      <form [formGroup]="form" (ngSubmit)="create()">
        <mat-form-field appearance="outline" class="full-width"><mat-label>Slug</mat-label><input matInput formControlName="slug" /></mat-form-field>
        <mat-form-field appearance="outline" class="full-width"><mat-label>Name</mat-label><input matInput formControlName="displayName" /></mat-form-field>
        <mat-form-field appearance="outline" class="full-width"><mat-label>Definition (JSON)</mat-label><textarea matInput formControlName="definition" rows="4"></textarea></mat-form-field>
        <button mat-flat-button color="primary" type="submit">Create</button>
      </form>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; } .mt { margin-top: 1rem; }'],
})
export class SkillsComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  skills = signal<Skill[]>([]);
  cols = ['slug', 'name', 'actions'];
  form = this.fb.nonNullable.group({
    slug: ['', Validators.required],
    displayName: ['', Validators.required],
    definition: ['{"steps":[]}', Validators.required],
  });

  ngOnInit(): void {
    this.api.listSkills().subscribe((s) => this.skills.set(s));
  }

  create(): void {
    if (this.form.invalid) return;
    this.api.createSkill(this.form.getRawValue()).subscribe((s) => {
      this.skills.update((list) => [...list, s]);
      this.form.reset({ definition: '{"steps":[]}' });
    });
  }

  testInvoke(skill: Skill): void {
    this.api.invokeSkill(skill.id, { input: { demo: true } }).subscribe();
  }
}

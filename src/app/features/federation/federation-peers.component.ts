import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { ApiService, FederationPeer } from '../../core/services/api.service';

@Component({
  selector: 'app-federation-peers',
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
    <h1>Federation Peers</h1>
    <mat-card>
      <table mat-table [dataSource]="peers()" class="full-width">
        <ng-container matColumnDef="slug"><th mat-header-cell *matHeaderCellDef>Slug</th><td mat-cell *matCellDef="let p">{{ p.slug }}</td></ng-container>
        <ng-container matColumnDef="peerUrl"><th mat-header-cell *matHeaderCellDef>URL</th><td mat-cell *matCellDef="let p">{{ p.peerUrl }}</td></ng-container>
        <ng-container matColumnDef="health"><th mat-header-cell *matHeaderCellDef>Health</th><td mat-cell *matCellDef="let p">{{ p.lastHealthStatus ?? '—' }}</td></ng-container>
        <ng-container matColumnDef="actions"><th mat-header-cell *matHeaderCellDef></th><td mat-cell *matCellDef="let p"><button mat-button (click)="probe(p)">Probe</button></td></ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
    <mat-card class="mt">
      <h3>Register peer (read-only routing in Phase C)</h3>
      <form [formGroup]="form" (ngSubmit)="create()">
        <mat-form-field appearance="outline" class="full-width"><mat-label>Slug</mat-label><input matInput formControlName="slug" /></mat-form-field>
        <mat-form-field appearance="outline" class="full-width"><mat-label>Name</mat-label><input matInput formControlName="displayName" /></mat-form-field>
        <mat-form-field appearance="outline" class="full-width"><mat-label>Peer URL</mat-label><input matInput formControlName="peerUrl" /></mat-form-field>
        <button mat-flat-button color="primary" type="submit">Register</button>
      </form>
    </mat-card>
  `,
  styles: ['.full-width { width: 100%; } .mt { margin-top: 1rem; }'],
})
export class FederationPeersComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  peers = signal<FederationPeer[]>([]);
  cols = ['slug', 'peerUrl', 'health', 'actions'];
  form = this.fb.nonNullable.group({
    slug: ['', Validators.required],
    displayName: ['', Validators.required],
    peerUrl: ['', Validators.required],
  });

  ngOnInit(): void {
    this.api.listFederationPeers().subscribe((p) => this.peers.set(p));
  }

  create(): void {
    if (this.form.invalid) return;
    this.api.createFederationPeer(this.form.getRawValue()).subscribe((p) => {
      this.peers.update((list) => [...list, p]);
      this.form.reset();
    });
  }

  probe(peer: FederationPeer): void {
    this.api.probeFederationPeer(peer.id).subscribe((updated) => {
      this.peers.update((list) => list.map((p) => (p.id === updated.id ? updated : p)));
    });
  }
}

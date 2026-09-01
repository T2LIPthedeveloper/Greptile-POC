import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-admin-shell',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
  ],
  template: `
    <mat-sidenav-container class="shell">
      <mat-sidenav mode="side" opened class="sidenav">
        <div class="brand">MCP Gateway</div>
        <mat-nav-list>
          <a mat-list-item routerLink="/providers" routerLinkActive="active">
            <mat-icon matListItemIcon>hub</mat-icon>
            <span matListItemTitle>Providers</span>
          </a>
        </mat-nav-list>
      </mat-sidenav>
      <mat-sidenav-content>
        <mat-toolbar color="primary">
          <span>Admin Portal</span>
          <span class="spacer"></span>
          <button mat-button (click)="auth.logout()">Logout</button>
        </mat-toolbar>
        <main class="content">
          <router-outlet />
        </main>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [
    `
      .shell {
        height: 100vh;
      }
      .sidenav {
        width: 240px;
      }
      .brand {
        padding: 1rem;
        font-weight: 600;
        font-size: 1.1rem;
      }
      .spacer {
        flex: 1;
      }
      .content {
        padding: 1.5rem;
      }
      a.active {
        background: rgba(0, 0, 0, 0.04);
      }
    `,
  ],
})
export class AdminShellComponent {
  constructor(public auth: AuthService) {}
}

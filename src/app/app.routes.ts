import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { AdminShellComponent } from './layouts/admin-shell.component';
import { LoginComponent } from './features/auth/login.component';
import { ProviderListComponent } from './features/providers/provider-list.component';
import { ProviderWizardComponent } from './features/providers/provider-wizard.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: AdminShellComponent,
    canActivate: [authGuard],
    children: [
      { path: 'providers', component: ProviderListComponent },
      { path: 'providers/new', component: ProviderWizardComponent },
      { path: '', redirectTo: 'providers', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: 'providers' },
];

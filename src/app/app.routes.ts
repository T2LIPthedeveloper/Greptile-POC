import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { AdminShellComponent } from './layouts/admin-shell.component';
import { LoginComponent } from './features/auth/login.component';
import { ProviderListComponent } from './features/providers/provider-list.component';
import { ProviderWizardComponent } from './features/providers/provider-wizard.component';
import { ProviderDetailComponent } from './features/providers/provider-detail.component';
import { GatewayTestComponent } from './features/gateway-test/gateway-test.component';
import { ConsumersComponent } from './features/consumers/consumers.component';
import { SubscriptionsComponent } from './features/subscriptions/subscriptions.component';
import { FederationPeersComponent } from './features/federation/federation-peers.component';
import { AuditLogComponent } from './features/audit/audit-log.component';
import { ApprovalsComponent } from './features/approvals/approvals.component';
import { SkillsComponent } from './features/skills/skills.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: AdminShellComponent,
    canActivate: [authGuard],
    children: [
      { path: 'providers', component: ProviderListComponent },
      { path: 'providers/new', component: ProviderWizardComponent },
      { path: 'providers/:id', component: ProviderDetailComponent },
      { path: 'gateway-test', component: GatewayTestComponent },
      { path: 'consumers', component: ConsumersComponent },
      { path: 'subscriptions', component: SubscriptionsComponent },
      { path: 'federation-peers', component: FederationPeersComponent },
      { path: 'audit', component: AuditLogComponent },
      { path: 'approvals', component: ApprovalsComponent },
      { path: 'skills', component: SkillsComponent },
      { path: '', redirectTo: 'providers', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: 'providers' },
];

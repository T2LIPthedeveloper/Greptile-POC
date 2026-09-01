import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface Provider {
  id: string;
  slug: string;
  displayName: string;
  description?: string;
  status: string;
  providerType?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProviderRequest {
  slug: string;
  displayName: string;
  description?: string;
  providerType?: string;
}

export interface CreateVersionRequest {
  versionLabel: string;
  protocolVersion?: string;
  changelog?: string;
}

export interface Version {
  id: string;
  providerId: string;
  versionLabel: string;
  protocolVersion: string;
  status: string;
  changelog?: string;
  publishedAt?: string;
  createdAt: string;
}

export interface CreateEndpointRequest {
  transport: string;
  baseUrl: string;
  healthCheckPath?: string;
  timeoutMs?: number;
  primary?: boolean;
}

export interface CreateCredentialRequest {
  name: string;
  credentialType: string;
  secretValue: string;
}

export interface FederationPeer {
  id: string;
  slug: string;
  displayName: string;
  peerUrl: string;
  trustLevel: string;
  status: string;
  lastHealthStatus?: string;
}

export interface Approval {
  id: string;
  versionId: string;
  status: string;
  createdAt: string;
}

export interface Skill {
  id: string;
  slug: string;
  displayName: string;
  description?: string;
  definition: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  listProviders() {
    return this.http.get<Provider[]>(`${this.base}/providers`);
  }

  getProvider(id: string) {
    return this.http.get<Provider>(`${this.base}/providers/${id}`);
  }

  createProvider(body: CreateProviderRequest) {
    return this.http.post<Provider>(`${this.base}/providers`, body);
  }

  createVersion(providerId: string, body: CreateVersionRequest) {
    return this.http.post<Version>(`${this.base}/providers/${providerId}/versions`, body);
  }

  listVersions(providerId: string) {
    return this.http.get<Version[]>(`${this.base}/providers/${providerId}/versions`);
  }

  addEndpoint(providerId: string, versionId: string, body: CreateEndpointRequest) {
    return this.http.post(`${this.base}/providers/${providerId}/versions/${versionId}/endpoints`, body);
  }

  addTool(providerId: string, versionId: string, body: { toolName: string; description?: string; inputSchema: string }) {
    return this.http.post(`${this.base}/providers/${providerId}/versions/${versionId}/tools`, body);
  }

  createCredential(body: CreateCredentialRequest) {
    return this.http.post<{ id: string }>(`${this.base}/credentials`, body);
  }

  linkCredential(providerId: string, versionId: string, credentialId: string) {
    return this.http.post(`${this.base}/providers/${providerId}/versions/${versionId}/credentials`, {
      credentialId,
      usage: 'UPSTREAM_AUTH',
    });
  }

  validateVersion(providerId: string, versionId: string) {
    return this.http.get<{ valid: boolean; errors: string[] }>(
      `${this.base}/providers/${providerId}/versions/${versionId}/validate`
    );
  }

  publishVersion(providerId: string, versionId: string) {
    return this.http.post<Version>(`${this.base}/providers/${providerId}/versions/${versionId}/publish`, {});
  }

  deprecateVersion(providerId: string, versionId: string) {
    return this.http.post<Version>(`${this.base}/providers/${providerId}/versions/${versionId}/deprecate`, {});
  }

  createSubscription(consumerId: string, body: { providerId: string; versionId?: string }) {
    return this.http.post(`${this.base}/consumers/${consumerId}/subscriptions`, body);
  }

  listConsumers() {
    return this.http.get<{ id: string; slug: string; displayName: string; status: string }[]>(
      `${this.base}/consumers`
    );
  }

  createConsumer(body: { slug: string; displayName: string }) {
    return this.http.post(`${this.base}/consumers`, body);
  }

  usageSummary() {
    return this.http.get<{ totalEvents: number }>(`${this.base}/usage/summary`);
  }

  usageAggregate() {
    return this.http.get<Record<string, number>>(`${this.base}/usage/aggregate`);
  }

  listFederationPeers() {
    return this.http.get<FederationPeer[]>(`${this.base}/federation-peers`);
  }

  createFederationPeer(body: { slug: string; displayName: string; peerUrl: string }) {
    return this.http.post<FederationPeer>(`${this.base}/federation-peers`, body);
  }

  probeFederationPeer(id: string) {
    return this.http.post<FederationPeer>(`${this.base}/federation-peers/${id}/probe`, {});
  }

  listAuditEvents() {
    return this.http.get<Record<string, unknown>[]>(`${this.base}/audit-events`);
  }

  listPendingApprovals() {
    return this.http.get<Approval[]>(`${this.base}/approvals/pending`);
  }

  approveVersion(approvalId: string, notes?: string) {
    return this.http.post(`${this.base}/approvals/${approvalId}/approve`, { notes });
  }

  listSkills() {
    return this.http.get<Skill[]>(`${this.base}/skills`);
  }

  createSkill(body: { slug: string; displayName: string; description?: string; definition: string }) {
    return this.http.post<Skill>(`${this.base}/skills`, body);
  }

  invokeSkill(id: string, body: Record<string, unknown>) {
    return this.http.post<Record<string, unknown>>(`${this.base}/skills/${id}/invoke`, body);
  }

  importOpenApi(providerId: string, versionId: string, openApiJson: string) {
    return this.http.post(`${this.base}/providers/${providerId}/versions/${versionId}/import-openapi`, {
      openApiJson,
    });
  }

  aiSuggestions(providerId: string, versionId: string, context: string) {
    return this.http.post<{ type?: string; suggestion?: string; field?: string }[]>(
      `${this.base}/providers/${providerId}/versions/${versionId}/ai-suggestions`,
      { context }
    );
  }

  exportComplianceAudit() {
    return this.http.get<Record<string, unknown>[]>(`${this.base}/compliance/audit-export`);
  }
}

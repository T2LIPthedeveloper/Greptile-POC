import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface Provider {
  id: string;
  slug: string;
  displayName: string;
  description?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProviderRequest {
  slug: string;
  displayName: string;
  description?: string;
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

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  listProviders() {
    return this.http.get<Provider[]>(`${this.base}/providers`);
  }

  createProvider(body: CreateProviderRequest) {
    return this.http.post<Provider>(`${this.base}/providers`, body);
  }

  createVersion(providerId: string, body: CreateVersionRequest) {
    return this.http.post<Version>(`${this.base}/providers/${providerId}/versions`, body);
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

  createSubscription(consumerId: string, body: { providerId: string; versionId?: string }) {
    return this.http.post(`${this.base}/consumers/${consumerId}/subscriptions`, body);
  }

  publishVersion(providerId: string, versionId: string) {
    return this.http.post<Version>(`${this.base}/providers/${providerId}/versions/${versionId}/publish`, {});
  }

  listVersions(providerId: string) {
    return this.http.get<Version[]>(`${this.base}/providers/${providerId}/versions`);
  }

  listConsumers() {
    return this.http.get<{ id: string; slug: string; displayName: string; status: string }[]>(
      `${this.base}/consumers`
    );
  }

  createConsumer(body: { slug: string; displayName: string }) {
    return this.http.post(`${this.base}/consumers`, body);
  }
}

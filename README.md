# MCP Gateway — Angular Admin Portal (Phase A baseline)

Admin UI for onboarding MCP providers. Lives on branch `angular/main`.

## Branching strategy

| Branch | Purpose |
|--------|---------|
| `angular/main` | Phase A baseline (this branch) |
| `angular/phase-b` | Gateway test console |
| `angular/phase-c` | Versioning & consumer UI |

Repo `main` stays empty.

## Development

```bash
npm install
npm start
```

App: http://localhost:4200  
Backend API (spring/main): http://localhost:8080

### First-time setup

1. Start Spring admin-service on `spring/main`
2. Bootstrap org via API or use existing credentials
3. Log in at `/login`

## Docker

```bash
docker build -t mcp-gateway-ui .
docker run -p 4200:80 mcp-gateway-ui
```

## Environment

Set `apiBaseUrl` in `src/environments/environment.ts` for local dev.

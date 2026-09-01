# MCP Gateway — Spring Backend (Phase A baseline)

Admin control plane for the internal MCP Gateway. Lives on branch `spring/main`.

## Branching strategy

| Branch | Purpose |
|--------|---------|
| `spring/main` | Phase A baseline (this branch) |
| `spring/phase-b` | Live MCP proxy (branched from `spring/main`) |
| `spring/phase-c` | Versioning & consumers |
| `spring/phase-d` | Full auth suite |
| `spring/phase-e` | Enterprise ops |

Repo `main` stays empty. Merge phase branches into `spring/main` after E2E verification.

## Quick start

```bash
./gradlew :admin-service:bootRun
```

API: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html

### Bootstrap first org

```bash
curl -X POST http://localhost:8080/api/v1/organizations/bootstrap \
  -H 'Content-Type: application/json' \
  -d '{"orgSlug":"demo","orgName":"Demo","email":"admin@demo.com","password":"password123"}'
```

## Environment variables

| Variable | Description |
|----------|-------------|
| `ENCRYPTION_MASTER_KEY` | Base64-encoded 32-byte AES key for credential vault |
| `MCP_SECURITY_JWT_SECRET` | JWT signing secret (min 32 chars) |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL (docker profile) |

## Docker

```bash
docker compose -f docker/docker-compose.yml up --build
```

## Modules

- `gateway-common` — shared types, crypto
- `gateway-domain` — JPA entities
- `gateway-security` — JWT, Spring Security
- `gateway-admin` — REST controllers & services
- `gateway-migrations` — Flyway SQL
- `admin-service` — Boot application

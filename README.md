# Access Control Manager

A Spring Boot service for managing Role-Based Access Control (RBAC). It supports role hierarchies, privilege inheritance, and user-role assignment with enforced assigner permissions.

## Prerequisites

- Java 17
- Maven 3.8+
- PostgreSQL (runtime) — H2 is used for testing

## Getting Started

### Configuration

The service is configured via `application.yml` and supports the following environment variables:

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8130` | HTTP port |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_SCHEMA` | `access_control` | Database schema |
| `SYSTEM_ADMIN_SYSTEMUSER-ID` | `9a908a6d-...` | UUID of the bootstrap admin user |
| `system.user-id` | `00000000-...` | Fallback UUID used for JPA audit fields |
| `ENVIRONMENT` | `local` | Active environment label |

### Running

```bash
mvn spring-boot:run
```

The API is available at `http://localhost:8130` and Swagger UI at `http://localhost:8130/swagger-ui.html`.

### Running Tests

```bash
mvn test
```

## API Overview

All endpoints are versioned under `/v1`.

### Users — `/v1/user`

| Method | Path | Description |
|---|---|---|
| `POST` | `/v1/user` | Create a new user |
| `GET` | `/v1/user/{systemUserId}` | Get user by system ID |
| `GET` | `/v1/user/current` | Get the authenticated user (via session token) |

### User Roles — `/v1/userRoles/{systemUserId}/roles`

| Method | Path | Description |
|---|---|---|
| `POST` | `/v1/userRoles/{systemUserId}/roles` | Assign or replace roles for a user |
| `GET` | `/v1/userRoles/{systemUserId}/roles` | List all roles for a user |
| `GET` | `/v1/userRoles/{systemUserId}/roles/{id}` | Get a specific user-role assignment |

Role assignment is permission-checked: the assigner must hold a role with an `ASSIGNMENT` inheritance relationship to the role they are trying to grant or revoke.

### Roles — `/v1/roles`

| Method | Path | Description |
|---|---|---|
| `GET` | `/v1/roles` | List all roles (filter with `?name=` or `?typeCode=`) |
| `GET` | `/v1/roles/{id}` | Get a role by ID |
| `GET` | `/v1/roles/{id}/capabilities` | Get capability roles inherited by a system role |
| `GET` | `/v1/roles/user/{systemUserId}` | Get the direct roles assigned to a user |
| `GET` | `/v1/roles/user/{systemUserId}/all` | Get all roles including inherited roles for a user |

## Domain Model

```
Role
├── roleTypeCode: PERMISSION | CAPABILITY
├── rolePrivileges: RolePrivilege[]
└── roleInheritances: RoleInheritance[]
       ├── type=INHERIT  → the parent role inherits all privileges of the child role
       └── type=ASSIGNMENT → the parent role's holders may assign the child role to users

User
├── systemUserId: UUID (external identity provider ID)
├── locked: YES | NO | NA
└── userRoles: UserRole[]
```

Role inheritance is recursive. `getAllRoles` traverses the full hierarchy to return every role a user holds, directly or transitively.

## Database Migrations

Flyway manages schema migrations under `src/main/resources/db/migration`. Migration files follow the pattern `V{major}.{minor}__{description}.sql`. Repeatable migrations (`R__*.sql`) re-run when their checksum changes and are used for seed data.

## Error Responses

All error responses use a consistent `ApiError` envelope:

```json
{
  "message": "Entity not found",
  "errors": {
    "message": ["Role with ID 42 does not exist"]
  }
}
```

Validation errors include per-field detail:

```json
{
  "message": "Validation failed",
  "errors": {
    "roleId": ["The field roleId is required"]
  }
}
```

## Architecture Notes

- **Controller layer** — interfaces carry Swagger annotations and validation; `*Impl` classes hold the `@RestController` and delegate to services.
- **Service layer** — `@Transactional` boundaries live here; repositories are not called directly from controllers.
- **Mapper layer** — MapStruct mappers convert between JPA entities and DTOs/responses.
- **JPA Auditing** — All entities extend `JpaAuditEntity`, which captures `createdBy`, `createdDate`, `modifiedBy`, and `modifiedDate` automatically via Spring Data's `AuditingEntityListener`.

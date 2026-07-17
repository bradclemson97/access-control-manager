# Access Control Manager

A Spring Boot service for managing Role-Based Access Control (RBAC). It supports role hierarchies, privilege inheritance, and user-role assignment with enforced assigner permissions.

This repository also contains [`security-library`](#security-library), a Spring Boot auto-configuration starter that other services can depend on to enforce RBAC using capabilities fetched from this service.

## Repository Structure

```
access-control-manager/
├── src/                   # The ACM Spring Boot application
├── security-library/      # Spring Boot auto-configuration starter for RBAC enforcement
└── pom.xml
```

---

## Access Control Manager

### Prerequisites

- Java 17
- Maven 3.8+
- PostgreSQL (runtime) — H2 is used for testing

### Configuration

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8130` | HTTP port |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_SCHEMA` | `access_control` | Database schema |
| `SYSTEM_ADMIN_SYSTEMUSER-ID` | `9a908a6d-...` | UUID of the bootstrap admin user |
| `system.user-id` | `00000000-...` | Fallback UUID for JPA audit fields |
| `ENVIRONMENT` | `local` | Active environment label |

### Running

```bash
mvn spring-boot:run
```

API: `http://localhost:8130` — Swagger UI: `http://localhost:8130/swagger-ui.html`

### Running Tests

```bash
mvn test
```

### API Overview

All endpoints are versioned under `/v1`.

#### Users — `/v1/user`

| Method | Path | Description |
|---|---|---|
| `POST` | `/v1/user` | Create a new user |
| `GET` | `/v1/user/{systemUserId}` | Get user by system ID |
| `GET` | `/v1/user/current` | Get the authenticated user (via session token) |
| `GET` | `/v1/user/{systemUserId}/capabilities` | Get the flat set of capability codes for a user |

The capabilities endpoint traverses the full role inheritance hierarchy and returns only `CAPABILITY`-type role names. It is called by the `security-library` on every authenticated request to populate the Spring `SecurityContext`.

#### User Roles — `/v1/userRoles/{systemUserId}/roles`

| Method | Path | Description |
|---|---|---|
| `POST` | `/v1/userRoles/{systemUserId}/roles` | Assign or replace roles for a user |
| `GET` | `/v1/userRoles/{systemUserId}/roles` | List all roles for a user |
| `GET` | `/v1/userRoles/{systemUserId}/roles/{id}` | Get a specific user-role assignment |

Role assignment is permission-checked: the assigner must hold a role with an `ASSIGNMENT` inheritance relationship to the role they are trying to grant or revoke.

#### Roles — `/v1/roles`

| Method | Path | Description |
|---|---|---|
| `GET` | `/v1/roles` | List all roles (filter with `?name=` or `?typeCode=`) |
| `GET` | `/v1/roles/{id}` | Get a role by ID |
| `GET` | `/v1/roles/{id}/capabilities` | Get capability roles inherited by a system role |
| `GET` | `/v1/roles/user/{systemUserId}` | Get the direct roles assigned to a user |
| `GET` | `/v1/roles/user/{systemUserId}/all` | Get all roles including inherited roles for a user |

### Domain Model

```
Role
├── roleTypeCode: PERMISSION | CAPABILITY
├── rolePrivileges: RolePrivilege[]
└── roleInheritances: RoleInheritance[]
       ├── type=INHERIT    → the parent role inherits all privileges of the child role
       └── type=ASSIGNMENT → the parent role's holders may assign the child role to users

User
├── systemUserId: UUID (external identity provider ID)
├── locked: YES | NO | NA
└── userRoles: UserRole[]
```

Role inheritance is recursive. `getAllRoles` and `getCapabilities` traverse the full hierarchy to return every role or capability a user holds, directly or transitively.

### Database Migrations

Flyway manages schema migrations under `src/main/resources/db/migration`. Versioned migrations follow the pattern `V{major}.{minor}__{description}.sql`. Repeatable migrations (`R__*.sql`) re-run when their checksum changes and are used for seed data.

### Error Responses

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

### Architecture Notes

- **Controller layer** — interfaces carry Swagger annotations and validation; `*Impl` classes hold the `@RestController` and delegate to services.
- **Service layer** — `@Transactional` boundaries live here; repositories are not called directly from controllers.
- **Mapper layer** — MapStruct mappers convert between JPA entities and DTOs/responses.
- **JPA Auditing** — All entities extend `JpaAuditEntity`, which captures `createdBy`, `createdDate`, `modifiedBy`, and `modifiedDate` automatically via Spring Data's `AuditingEntityListener`.

---

## Security Library

A zero-boilerplate Spring Boot auto-configuration starter that enforces RBAC in any service using capabilities fetched from ACM.

### How It Works

```
Incoming request with JWT
  → Spring Security validates the JWT (signature, expiry)
  → AccessControlAuthenticationConverter
      - Extracts systemUserId from the configured JWT claim
      - Calls GET /v1/user/{systemUserId}/capabilities on ACM
      - Populates the SecurityContext with one GrantedAuthority per capability code
  → @RequiresCapability("CREATE_USERS") on a method or class
      → CapabilityAuthorizationManager checks the SecurityContext authorities
      → Throws AccessDeniedException (HTTP 403) if the capability is absent
```

### Usage

#### 1. Add the dependency

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>security-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 2. Configure the consuming application

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-auth-server/   # or jwk-set-uri

access-control:
  security:
    manager-url: http://access-control-manager:8130  # required
    system-user-id-claim: sub                        # JWT claim holding the systemUserId UUID
    fail-on-error: false                             # set true to reject auth if ACM is unreachable
```

The auto-configuration activates only when `access-control.security.manager-url` is set, so the library is safe to include in projects that don't yet need RBAC.

#### 3. Annotate methods or classes

Use `@RequiresCapability` directly:

```java
@RequiresCapability("CREATE_USERS")
public CreateUserResponse createUser(CreateUserRequest request) { ... }
```

Or define shorthand annotations composed from it — this is the recommended pattern for readability and discoverability:

```java
// Define once in your application
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequiresCapability("CREATE_USERS")
public @interface CanCreateUsers {}

// Use everywhere
@CanCreateUsers
public CreateUserResponse createUser(CreateUserRequest request) { ... }
```

Both styles support method-level and class-level placement. A class-level annotation applies to all methods on that class.

#### 4. Custom SecurityFilterChain (optional)

The library provides a default `SecurityFilterChain` (stateless JWT, all requests authenticated). To customise security rules, define your own `SecurityFilterChain` bean and wire the converter:

```java
@Bean
public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        AccessControlAuthenticationConverter converter) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .csrf(csrf -> csrf.disable())
        .build();
}
```

### Building the Library

```bash
mvn -f security-library/pom.xml install
```

This installs the artifact to your local Maven repository, making it available as a dependency for other local projects.

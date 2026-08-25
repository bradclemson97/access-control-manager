package com.example.security.converter;

import com.example.security.client.AccessControlManagerClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessControlAuthenticationConverter Unit Tests")
class AccessControlAuthenticationConverterTest {

    @Mock
    private AccessControlManagerClient client;

    private static final String CLAIM = "system_user_id";
    private UUID systemUserId;

    @BeforeEach
    void setUp() {
        systemUserId = UUID.randomUUID();
    }

    private Jwt buildJwt(String systemUserIdValue, List<String> capabilities) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("sub", "test-subject");
        if (systemUserIdValue != null) {
            builder.claim(CLAIM, systemUserIdValue);
        }
        if (capabilities != null) {
            builder.claim("capabilities", capabilities);
        }
        return builder.build();
    }

    @Test
    @DisplayName("convert - loads capabilities from JWT claim when present and non-empty")
    void convert_capabilitiesFromJwtClaim() {
        Jwt jwt = buildJwt(systemUserId.toString(), List.of("CREATE_USERS", "READ_USERS"));
        AccessControlAuthenticationConverter converter =
                new AccessControlAuthenticationConverter(client, CLAIM, true);

        JwtAuthenticationToken token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("CREATE_USERS", "READ_USERS");
        then(client).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("convert - fetches capabilities from ACM client when JWT claim is absent")
    void convert_capabilitiesFromClient() {
        Jwt jwt = buildJwt(systemUserId.toString(), null);
        given(client.getCapabilities(systemUserId)).willReturn(Set.of("CREATE_USERS"));
        AccessControlAuthenticationConverter converter =
                new AccessControlAuthenticationConverter(client, CLAIM, true);

        JwtAuthenticationToken token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .extracting("authority")
                .containsExactly("CREATE_USERS");
    }

    @Test
    @DisplayName("convert - sets principal name to the systemUserId string")
    void convert_principalNameIsSystemUserId() {
        Jwt jwt = buildJwt(systemUserId.toString(), List.of("READ_USERS"));
        AccessControlAuthenticationConverter converter =
                new AccessControlAuthenticationConverter(client, CLAIM, true);

        JwtAuthenticationToken token = converter.convert(jwt);

        assertThat(token.getName()).isEqualTo(systemUserId.toString());
    }

    @Test
    @DisplayName("convert - throws IllegalArgumentException when systemUserIdClaim is missing from JWT")
    void convert_missingSystemUserIdClaim() {
        Jwt jwt = buildJwt(null, List.of("CREATE_USERS"));
        AccessControlAuthenticationConverter converter =
                new AccessControlAuthenticationConverter(client, CLAIM, true);

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(CLAIM);
    }

    @Test
    @DisplayName("convert - throws IllegalArgumentException when systemUserIdClaim is not a valid UUID")
    void convert_invalidUuidClaim() {
        Jwt jwt = buildJwt("not-a-valid-uuid", List.of("CREATE_USERS"));
        AccessControlAuthenticationConverter converter =
                new AccessControlAuthenticationConverter(client, CLAIM, true);

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("convert - throws AuthenticationServiceException when client fails and failOnError is true")
    void convert_clientFailsWithFailOnErrorTrue() {
        Jwt jwt = buildJwt(systemUserId.toString(), null);
        given(client.getCapabilities(systemUserId)).willThrow(new RuntimeException("ACM down"));
        AccessControlAuthenticationConverter converter =
                new AccessControlAuthenticationConverter(client, CLAIM, true);

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(AuthenticationServiceException.class);
    }

    @Test
    @DisplayName("convert - returns empty authority set when client fails and failOnError is false")
    void convert_clientFailsWithFailOnErrorFalse() {
        Jwt jwt = buildJwt(systemUserId.toString(), null);
        given(client.getCapabilities(systemUserId)).willThrow(new RuntimeException("ACM down"));
        AccessControlAuthenticationConverter converter =
                new AccessControlAuthenticationConverter(client, CLAIM, false);

        JwtAuthenticationToken token = converter.convert(jwt);

        assertThat(token.getAuthorities()).isEmpty();
    }
}

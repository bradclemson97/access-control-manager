package com.example.security.authorization;

import com.example.security.annotation.RequiresCapability;
import com.example.security.authority.CapabilityGrantedAuthority;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CapabilityAuthorizationManager Unit Tests")
class CapabilityAuthorizationManagerTest {

    @Mock
    private MethodInvocation invocation;

    private CapabilityAuthorizationManager manager;

    interface TestController {
        @RequiresCapability("CREATE_USERS")
        void createUser();

        void noAnnotationMethod();
    }

    @BeforeEach
    void setUp() {
        manager = new CapabilityAuthorizationManager();
    }

    @Test
    @DisplayName("check - grants access when invoked method has no @RequiresCapability annotation")
    void check_noAnnotation() throws Exception {
        Method method = TestController.class.getMethod("noAnnotationMethod");
        given(invocation.getMethod()).willReturn(method);

        AuthorizationDecision decision = manager.check(this::authenticatedPrincipal, invocation);

        assertThat(decision.isGranted()).isTrue();
    }

    @Test
    @DisplayName("check - denies access when authentication is null")
    void check_authenticationIsNull() throws Exception {
        Method method = TestController.class.getMethod("createUser");
        given(invocation.getMethod()).willReturn(method);

        AuthorizationDecision decision = manager.check(() -> null, invocation);

        assertThat(decision.isGranted()).isFalse();
    }

    @Test
    @DisplayName("check - denies access when authentication is not authenticated")
    void check_notAuthenticated() throws Exception {
        Method method = TestController.class.getMethod("createUser");
        given(invocation.getMethod()).willReturn(method);
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user", null);
        auth.setAuthenticated(false);

        AuthorizationDecision decision = manager.check(() -> auth, invocation);

        assertThat(decision.isGranted()).isFalse();
    }

    @Test
    @DisplayName("check - grants access when user holds the required capability")
    void check_grantedWithMatchingCapability() throws Exception {
        Method method = TestController.class.getMethod("createUser");
        given(invocation.getMethod()).willReturn(method);

        List<GrantedAuthority> authorities = List.of(new CapabilityGrantedAuthority("CREATE_USERS"));
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user", null, authorities);

        AuthorizationDecision decision = manager.check(() -> auth, invocation);

        assertThat(decision.isGranted()).isTrue();
    }

    @Test
    @DisplayName("check - denies access when user holds a different capability")
    void check_deniedWithNonMatchingCapability() throws Exception {
        Method method = TestController.class.getMethod("createUser");
        given(invocation.getMethod()).willReturn(method);

        List<GrantedAuthority> authorities = List.of(new CapabilityGrantedAuthority("READ_USERS"));
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user", null, authorities);

        AuthorizationDecision decision = manager.check(() -> auth, invocation);

        assertThat(decision.isGranted()).isFalse();
    }

    @Test
    @DisplayName("check - denies access when user has no authorities")
    void check_deniedWithNoAuthorities() throws Exception {
        Method method = TestController.class.getMethod("createUser");
        given(invocation.getMethod()).willReturn(method);

        TestingAuthenticationToken auth = new TestingAuthenticationToken("user", null, List.of());

        AuthorizationDecision decision = manager.check(() -> auth, invocation);

        assertThat(decision.isGranted()).isFalse();
    }

    private Authentication authenticatedPrincipal() {
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user", null);
        auth.setAuthenticated(true);
        return auth;
    }
}

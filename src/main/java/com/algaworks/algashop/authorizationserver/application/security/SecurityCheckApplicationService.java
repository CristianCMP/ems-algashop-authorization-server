package com.algaworks.algashop.authorizationserver.application.security;

import java.util.UUID;

public interface SecurityCheckApplicationService {
    UUID getAuthenticationUserId();

    boolean isAuthenticated();

    boolean isMachineAuthorized();
}

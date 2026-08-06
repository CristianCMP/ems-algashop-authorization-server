package com.algaworks.algashop.authorizationserver.application.user.query;

import java.util.UUID;

public class AuthUserNotFoundException extends RuntimeException {

    public AuthUserNotFoundException(UUID userId) {
        super("User nor found with ID: " + userId);
    }
}

package com.algaworks.algashop.authorizationserver.infrastructure.security.token;

import com.algaworks.algashop.authorizationserver.infrastructure.security.oidc.OidcUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
@RequiredArgsConstructor
public class OAuth2TokenCustomizarConfig {

    private final OidcUserInfoService oidcUserInfoService;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            String tokenType = context.getTokenType().getValue();
            AuthorizationGrantType authorizationGrant = context.getAuthorizationGrantType();
            if (isIdToken(tokenType)) {
                OidcUserInfo oidcUserInfo = loadUser(context);
                context.getClaims().claims(claims -> claims.putAll(oidcUserInfo.getClaims()));
            } else {
                if (isAccessToken(tokenType) &&
                        (isAuthCodeFlow(authorizationGrant))
                        || isRefreshTokenFlow(authorizationGrant)
                ) {
                    OidcUserInfo oidcUserInfo = loadUser(context);
                    context.getClaims().subject(oidcUserInfo.getSubject());
                }
            }
        };
    }

    private boolean isRefreshTokenFlow(AuthorizationGrantType authorizationGrant) {
       return AuthorizationGrantType.REFRESH_TOKEN.equals(authorizationGrant);
    }

    private boolean isAuthCodeFlow(AuthorizationGrantType authorizationGrant) {
       return AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationGrant);
    }

    private OidcUserInfo loadUser(JwtEncodingContext context) {
        String email = context.getPrincipal().getName();
        return oidcUserInfoService.loadUser(email);
    }

    private boolean isAccessToken(String tokenType) {
        return OAuth2TokenType.ACCESS_TOKEN.getValue().equals(tokenType);
    }

    private boolean isIdToken(String tokenType) {
        return OidcParameterNames.ID_TOKEN.equals(tokenType);
    }
}

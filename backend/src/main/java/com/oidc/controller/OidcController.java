package com.oidc.controller;

import com.oidc.config.OidcProperties;
import com.oidc.dto.LoginRequest;
import com.oidc.model.ClientEntity;
import com.oidc.model.RealmEntity;
import com.oidc.model.UserEntity;
import com.oidc.service.OidcService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
public class OidcController {

    private final OidcService oidcService;
    private final OidcProperties oidcProperties;

    public OidcController(OidcService oidcService, OidcProperties oidcProperties) {
        this.oidcService = oidcService;
        this.oidcProperties = oidcProperties;
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String realmName = request.getRealm() != null ? request.getRealm() : "outline";
        RealmEntity realm = oidcService.getRealm(realmName);
        if (realm == null || !Boolean.TRUE.equals(realm.getEnabled())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Realm not found"));
        }

        ClientEntity client = oidcService.getClient(realm.getId(), request.getClientId());
        if (client == null || !Boolean.TRUE.equals(client.getEnabled())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid client"));
        }

        if (request.getRedirectUri() != null && !request.getRedirectUri().isEmpty()) {
            if (!oidcService.isValidRedirectUri(client.getId(), request.getRedirectUri())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Invalid redirect_uri"));
            }
        }

        UserEntity user = oidcService.authenticate(realm.getId(), request.getUsername(), request.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid username or password"));
        }

        String code = oidcService.generateAuthorizationCode(
                user, realmName, request.getClientId(),
                request.getRedirectUri(), request.getScope(), request.getNonce());

        StringBuilder redirectUrl = new StringBuilder(request.getRedirectUri());
        redirectUrl.append("?code=").append(code);
        if (request.getState() != null && !request.getState().isEmpty()) {
            redirectUrl.append("&state=").append(request.getState());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("redirectUrl", redirectUrl.toString());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/theme-config")
    public ResponseEntity<?> themeConfig() {
        Map<String, Object> theme = new HashMap<>();
        OidcProperties.LoginTheme t = oidcProperties.getLoginTheme();
        theme.put("backgroundImage", t.getBackgroundImage());
        theme.put("backgroundColor", t.getBackgroundColor());
        theme.put("logoUrl", t.getLogoUrl());
        theme.put("title", t.getTitle());
        theme.put("primaryColor", t.getPrimaryColor());
        return ResponseEntity.ok(theme);
    }

    @GetMapping("/realms/{realm}/protocol/openid-connect/auth")
    public void authorize(@PathVariable String realm,
                          @RequestParam("response_type") String responseType,
                          @RequestParam("client_id") String clientId,
                          @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                          @RequestParam(value = "scope", defaultValue = "openid") String scope,
                          @RequestParam(value = "state", required = false) String state,
                          @RequestParam(value = "nonce", required = false) String nonce,
                          HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("/index.html").forward(request, response);
    }

    @PostMapping("/realms/{realm}/protocol/openid-connect/token")
    public ResponseEntity<?> token(@PathVariable String realm,
                                   @RequestParam("grant_type") String grantType,
                                   @RequestParam(value = "code", required = false) String code,
                                   @RequestParam(value = "redirect_uri", required = false) String redirectUri,
                                   @RequestParam(value = "client_id") String clientId,
                                   @RequestParam(value = "client_secret") String clientSecret,
                                   @RequestParam(value = "refresh_token", required = false) String refreshToken) {

        RealmEntity realmEntity = oidcService.getRealm(realm);
        if (realmEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "realm not found"));
        }

        ClientEntity client = oidcService.getClient(realmEntity.getId(), clientId);
        if (client == null || !Boolean.TRUE.equals(client.getEnabled())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "invalid client"));
        }

        // Verify client secret
        if (client.getSecret() == null || !client.getSecret().equals(clientSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "invalid client credentials"));
        }

        if ("authorization_code".equals(grantType)) {
            return handleAuthorizationCodeGrant(realm, clientId, code, redirectUri);
        } else if ("refresh_token".equals(grantType)) {
            return handleRefreshTokenGrant(realm, clientId, refreshToken);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "unsupported grant_type"));
        }
    }

    private ResponseEntity<?> handleAuthorizationCodeGrant(String realm, String clientId,
                                                            String code, String redirectUri) {
        OidcService.AuthCodeData authData = oidcService.consumeAuthCode(code);
        if (authData == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "invalid or expired code"));
        }

        List<String> scopes = parseScopes(authData.getScope());
        UserEntity user = new UserEntity();
        user.setId(authData.getUserId());
        user.setUsername(authData.getUsername());
        user.setEmail(authData.getEmail());
        user.setFirstName(authData.getFirstName());
        user.setLastName(authData.getLastName());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", oidcService.getTokenService().generateAccessToken(user, realm, clientId, scopes));
        response.put("token_type", "Bearer");
        response.put("expires_in", oidcService.getTokenService().getAccessTokenTtl());
        response.put("refresh_token", oidcService.getTokenService().generateRefreshToken(user, realm, clientId));
        response.put("scope", authData.getScope() != null ? authData.getScope() : "openid");
        response.put("id_token", oidcService.getTokenService().generateIdToken(
                user, realm, clientId, authData.getNonce(), scopes));

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> handleRefreshTokenGrant(String realm, String clientId, String refreshToken) {
        com.nimbusds.jwt.JWTClaimsSet claims = oidcService.getTokenService().verifyToken(refreshToken);
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "invalid refresh token"));
        }

        try {
            UserEntity user = new UserEntity();
            user.setId(claims.getSubject());
            user.setUsername(claims.getStringClaim("preferred_username"));

            List<String> scopes = new ArrayList<>();
            scopes.add("openid");

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("access_token", oidcService.getTokenService().generateAccessToken(user, realm, clientId, scopes));
            response.put("token_type", "Bearer");
            response.put("expires_in", oidcService.getTokenService().getAccessTokenTtl());
            response.put("refresh_token", oidcService.getTokenService().generateRefreshToken(user, realm, clientId));
            response.put("scope", "openid");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "invalid token"));
        }
    }

    @GetMapping("/realms/{realm}/protocol/openid-connect/userinfo")
    public ResponseEntity<?> userinfo(@PathVariable String realm,
                                       @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);

        com.nimbusds.jwt.JWTClaimsSet claims = oidcService.getTokenService().verifyToken(token);
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "invalid token"));
        }

        try {
            Map<String, Object> userInfo = new LinkedHashMap<>();
            userInfo.put("sub", claims.getSubject());
            userInfo.put("preferred_username", claims.getStringClaim("preferred_username"));

            if (claims.getClaim("email") != null) {
                userInfo.put("email", claims.getStringClaim("email"));
                userInfo.put("email_verified", claims.getBooleanClaim("email_verified"));
            }
            if (claims.getClaim("given_name") != null) {
                userInfo.put("given_name", claims.getStringClaim("given_name"));
            }
            if (claims.getClaim("family_name") != null) {
                userInfo.put("family_name", claims.getStringClaim("family_name"));
            }
            if (claims.getClaim("name") != null) {
                userInfo.put("name", claims.getStringClaim("name"));
            }

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "invalid token"));
        }
    }

    @GetMapping("/realms/{realm}/protocol/openid-connect/logout")
    public ResponseEntity<?> logout(@PathVariable String realm,
                                     @RequestParam(value = "redirect_uri", required = false) String redirectUri) {
        // For simplicity, just redirect
        if (redirectUri != null && !redirectUri.isEmpty()) {
            return ResponseEntity.status(302)
                    .header("Location", redirectUri)
                    .build();
        }
        return ResponseEntity.ok(Collections.singletonMap("message", "logged out"));
    }

    @GetMapping("/realms/{realm}/.well-known/openid-configuration")
    public ResponseEntity<?> openidConfiguration(@PathVariable String realm) {
        String baseUrl = oidcService.getTokenService().getIssuerForRealm(realm);

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("issuer", baseUrl);
        config.put("authorization_endpoint", baseUrl + "/protocol/openid-connect/auth");
        config.put("token_endpoint", baseUrl + "/protocol/openid-connect/token");
        config.put("userinfo_endpoint", baseUrl + "/protocol/openid-connect/userinfo");
        config.put("end_session_endpoint", baseUrl + "/protocol/openid-connect/logout");
        config.put("jwks_uri", baseUrl + "/protocol/openid-connect/certs");
        config.put("scopes_supported", Arrays.asList("openid", "profile", "email"));
        config.put("response_types_supported", Arrays.asList("code"));
        config.put("grant_types_supported", Arrays.asList("authorization_code", "refresh_token"));
        config.put("subject_types_supported", Arrays.asList("public"));
        config.put("id_token_signing_alg_values_supported", Arrays.asList("HS256"));
        config.put("token_endpoint_auth_methods_supported",
                Arrays.asList("client_secret_post", "client_secret_basic"));

        return ResponseEntity.ok(config);
    }

    private List<String> parseScopes(String scope) {
        if (scope == null || scope.isEmpty()) {
            return Arrays.asList("openid");
        }
        return Arrays.asList(scope.split("\\s+"));
    }
}

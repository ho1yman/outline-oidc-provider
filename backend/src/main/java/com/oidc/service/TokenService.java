package com.oidc.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.oidc.config.OidcProperties;
import com.oidc.model.UserEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

@Service
public class TokenService {

    private final OidcProperties oidcProperties;
    private byte[] jwtSecret;

    public TokenService(OidcProperties oidcProperties) {
        this.oidcProperties = oidcProperties;
    }

    @PostConstruct
    public void init() {
        this.jwtSecret = oidcProperties.getJwtSecret().getBytes();
    }

    public String generateAccessToken(UserEntity user, String realmName, String clientId, List<String> scopes) {
        try {
            long now = System.currentTimeMillis() / 1000;
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(user.getId())
                    .issuer(oidcProperties.getIssuer() + "/realms/" + realmName)
                    .issueTime(new Date(now * 1000))
                    .expirationTime(new Date((now + oidcProperties.getAccessTokenTtl()) * 1000))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("typ", "Bearer")
                    .claim("azp", clientId)
                    .claim("scope", String.join(" ", scopes))
                    .claim("preferred_username", user.getUsername());

            if (user.getEmail() != null) {
                builder.claim("email", user.getEmail());
                builder.claim("email_verified", user.getEmailVerified() != null && user.getEmailVerified());
            }
            if (user.getFirstName() != null) {
                builder.claim("given_name", user.getFirstName());
            }
            if (user.getLastName() != null) {
                builder.claim("family_name", user.getLastName());
            }

            JWSSigner signer = new MACSigner(jwtSecret);
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    builder.build());
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    public String generateIdToken(UserEntity user, String realmName, String clientId,
                                   String nonce, List<String> scopes) {
        try {
            long now = System.currentTimeMillis() / 1000;
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(user.getId())
                    .issuer(oidcProperties.getIssuer() + "/realms/" + realmName)
                    .audience(clientId)
                    .issueTime(new Date(now * 1000))
                    .expirationTime(new Date((now + oidcProperties.getAccessTokenTtl()) * 1000))
                    .jwtID(UUID.randomUUID().toString());

            if (nonce != null && !nonce.isEmpty()) {
                builder.claim("nonce", nonce);
            }
            if (user.getEmail() != null) {
                builder.claim("email", user.getEmail());
                builder.claim("email_verified", user.getEmailVerified() != null && user.getEmailVerified());
            }
            if (user.getFirstName() != null) {
                builder.claim("given_name", user.getFirstName());
            }
            if (user.getLastName() != null) {
                builder.claim("family_name", user.getLastName());
            }
            builder.claim("preferred_username", user.getUsername());
            builder.claim("name", buildFullName(user));

            JWSSigner signer = new MACSigner(jwtSecret);
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    builder.build());
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate id token", e);
        }
    }

    public String generateRefreshToken(UserEntity user, String realmName, String clientId) {
        try {
            long now = System.currentTimeMillis() / 1000;
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getId())
                    .issuer(oidcProperties.getIssuer() + "/realms/" + realmName)
                    .issueTime(new Date(now * 1000))
                    .expirationTime(new Date((now + oidcProperties.getRefreshTokenTtl()) * 1000))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("typ", "Refresh")
                    .claim("azp", clientId)
                    .build();

            JWSSigner signer = new MACSigner(jwtSecret);
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claims);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }

    public JWTClaimsSet verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(jwtSecret);

            if (!signedJWT.verify(verifier)) {
                return null;
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expiration = claims.getExpirationTime();
            if (expiration != null && expiration.before(new Date())) {
                return null;
            }

            return claims;
        } catch (ParseException | JOSEException e) {
            return null;
        }
    }

    public String getIssuerForRealm(String realmName) {
        return oidcProperties.getIssuer() + "/realms/" + realmName;
    }

    public int getAccessTokenTtl() {
        return oidcProperties.getAccessTokenTtl();
    }

    public int getRefreshTokenTtl() {
        return oidcProperties.getRefreshTokenTtl();
    }

    private String buildFullName(UserEntity user) {
        if (user.getFirstName() != null && user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null) {
            return user.getFirstName();
        } else if (user.getLastName() != null) {
            return user.getLastName();
        }
        return user.getUsername();
    }
}

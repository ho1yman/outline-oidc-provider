package com.oidc.service;

import com.oidc.config.OidcProperties;
import com.oidc.model.ClientEntity;
import com.oidc.model.RealmEntity;
import com.oidc.model.RedirectUriEntity;
import com.oidc.model.UserEntity;
import com.oidc.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OidcService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final ClientRepository clientRepository;
    private final RealmRepository realmRepository;
    private final RedirectUriRepository redirectUriRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final OidcProperties oidcProperties;

    // In-memory storage for authorization codes
    private final ConcurrentHashMap<String, AuthCodeData> authCodes = new ConcurrentHashMap<>();

    public OidcService(UserRepository userRepository,
                       CredentialRepository credentialRepository,
                       ClientRepository clientRepository,
                       RealmRepository realmRepository,
                       RedirectUriRepository redirectUriRepository,
                       PasswordService passwordService,
                       TokenService tokenService,
                       OidcProperties oidcProperties) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.clientRepository = clientRepository;
        this.realmRepository = realmRepository;
        this.redirectUriRepository = redirectUriRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.oidcProperties = oidcProperties;
    }

    public RealmEntity getRealm(String realmName) {
        return realmRepository.findByName(realmName).orElse(null);
    }

    public ClientEntity getClient(String realmId, String clientId) {
        return clientRepository.findByRealmIdAndClientId(realmId, clientId).orElse(null);
    }

    public boolean isValidRedirectUri(String clientDbId, String redirectUri) {
        List<RedirectUriEntity> uris = redirectUriRepository.findByClientId(clientDbId);
        for (RedirectUriEntity uri : uris) {
            if (redirectUri.startsWith(uri.getValue())) {
                return true;
            }
        }
        return false;
    }

    public UserEntity authenticate(String realmId, String username, String password) {
        // Try finding by username or email
        Optional<UserEntity> userOpt = userRepository.findByRealmIdAndUsername(realmId, username);
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByRealmIdAndEmail(realmId, username);
        }

        if (!userOpt.isPresent()) {
            return null;
        }

        UserEntity user = userOpt.get();
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            return null;
        }

        // Verify password
        List<com.oidc.model.CredentialEntity> credentials =
                credentialRepository.findByUserIdAndType(user.getId(), "password");

        for (com.oidc.model.CredentialEntity cred : credentials) {
            if (passwordService.verify(password, cred.getSecretData(), cred.getCredentialData())) {
                return user;
            }
        }

        return null;
    }

    public String generateAuthorizationCode(UserEntity user, String realmName, String clientId,
                                             String redirectUri, String scope, String nonce) {
        String code = UUID.randomUUID().toString();
        AuthCodeData data = new AuthCodeData();
        data.setUserId(user.getId());
        data.setUsername(user.getUsername());
        data.setEmail(user.getEmail());
        data.setFirstName(user.getFirstName());
        data.setLastName(user.getLastName());
        data.setRealmName(realmName);
        data.setClientId(clientId);
        data.setRedirectUri(redirectUri);
        data.setScope(scope);
        data.setNonce(nonce);
        data.setCreatedAt(System.currentTimeMillis());

        authCodes.put(code, data);

        // Cleanup old codes
        long cutoff = System.currentTimeMillis() - (oidcProperties.getCodeTtl() * 1000L);
        authCodes.entrySet().removeIf(e -> e.getValue().getCreatedAt() < cutoff);

        return code;
    }

    public AuthCodeData consumeAuthCode(String code) {
        AuthCodeData data = authCodes.remove(code);
        if (data == null) {
            return null;
        }
        long ttl = oidcProperties.getCodeTtl() * 1000L;
        if (System.currentTimeMillis() - data.getCreatedAt() > ttl) {
            return null;
        }
        return data;
    }

    public static class AuthCodeData {
        private String userId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String realmName;
        private String clientId;
        private String redirectUri;
        private String scope;
        private String nonce;
        private long createdAt;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getRealmName() { return realmName; }
        public void setRealmName(String realmName) { this.realmName = realmName; }

        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }

        public String getRedirectUri() { return redirectUri; }
        public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

        public String getScope() { return scope; }
        public void setScope(String scope) { this.scope = scope; }

        public String getNonce() { return nonce; }
        public void setNonce(String nonce) { this.nonce = nonce; }

        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    }

    public TokenService getTokenService() {
        return tokenService;
    }
}

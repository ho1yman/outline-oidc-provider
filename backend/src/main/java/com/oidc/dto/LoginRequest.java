package com.oidc.dto;

public class LoginRequest {

    private String username;
    private String password;
    private String realm;
    private String clientId;
    private String redirectUri;
    private String state;
    private String scope;
    private String responseType;
    private String nonce;
    private Boolean rememberMe;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealm() { return realm; }
    public void setRealm(String realm) { this.realm = realm; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getResponseType() { return responseType; }
    public void setResponseType(String responseType) { this.responseType = responseType; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public Boolean getRememberMe() { return rememberMe; }
    public void setRememberMe(Boolean rememberMe) { this.rememberMe = rememberMe; }
}

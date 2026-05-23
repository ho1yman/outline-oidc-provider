package com.oidc.dto;

public class TokenRequest {

    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String refreshToken;

    public String getGrantType() { return grantType; }
    public void setGrantType(String grantType) { this.grantType = grantType; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}

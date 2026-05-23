package com.oidc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oidc")
public class OidcProperties {

    private String issuer;
    private String jwtSecret;
    private int codeTtl = 300;
    private int accessTokenTtl = 300;
    private int refreshTokenTtl = 86400;
    private LoginTheme loginTheme = new LoginTheme();

    public static class LoginTheme {
        private String backgroundImage;
        private String backgroundColor = "#f0f2f5";
        private String logoUrl;
        private String title = "SSO Login";
        private String primaryColor = "#1890ff";

        public String getBackgroundImage() { return backgroundImage; }
        public void setBackgroundImage(String backgroundImage) { this.backgroundImage = backgroundImage; }

        public String getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

        public String getLogoUrl() { return logoUrl; }
        public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getPrimaryColor() { return primaryColor; }
        public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
    }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }

    public int getCodeTtl() { return codeTtl; }
    public void setCodeTtl(int codeTtl) { this.codeTtl = codeTtl; }

    public int getAccessTokenTtl() { return accessTokenTtl; }
    public void setAccessTokenTtl(int accessTokenTtl) { this.accessTokenTtl = accessTokenTtl; }

    public int getRefreshTokenTtl() { return refreshTokenTtl; }
    public void setRefreshTokenTtl(int refreshTokenTtl) { this.refreshTokenTtl = refreshTokenTtl; }

    public LoginTheme getLoginTheme() { return loginTheme; }
    public void setLoginTheme(LoginTheme loginTheme) { this.loginTheme = loginTheme; }
}

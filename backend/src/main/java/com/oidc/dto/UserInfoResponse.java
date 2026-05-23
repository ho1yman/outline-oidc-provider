package com.oidc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("name")
    private String name;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    public String getSub() { return sub; }
    public void setSub(String sub) { this.sub = sub; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getPreferredUsername() { return preferredUsername; }
    public void setPreferredUsername(String preferredUsername) { this.preferredUsername = preferredUsername; }
}

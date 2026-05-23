package com.oidc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OidcConfigResponse {

    private String issuer;
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;
    @JsonProperty("end_session_endpoint")
    private String endSessionEndpoint;
    @JsonProperty("jwks_uri")
    private String jwksUri;
    @JsonProperty("scopes_supported")
    private List<String> scopesSupported;
    @JsonProperty("response_types_supported")
    private List<String> responseTypesSupported;
    @JsonProperty("grant_types_supported")
    private List<String> grantTypesSupported;
    @JsonProperty("subject_types_supported")
    private List<String> subjectTypesSupported;
    @JsonProperty("id_token_signing_alg_values_supported")
    private List<String> idTokenSigningAlgValuesSupported;
    @JsonProperty("token_endpoint_auth_methods_supported")
    private List<String> tokenEndpointAuthMethodsSupported;

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getAuthorizationEndpoint() { return authorizationEndpoint; }
    public void setAuthorizationEndpoint(String authorizationEndpoint) { this.authorizationEndpoint = authorizationEndpoint; }

    public String getTokenEndpoint() { return tokenEndpoint; }
    public void setTokenEndpoint(String tokenEndpoint) { this.tokenEndpoint = tokenEndpoint; }

    public String getUserinfoEndpoint() { return userinfoEndpoint; }
    public void setUserinfoEndpoint(String userinfoEndpoint) { this.userinfoEndpoint = userinfoEndpoint; }

    public String getEndSessionEndpoint() { return endSessionEndpoint; }
    public void setEndSessionEndpoint(String endSessionEndpoint) { this.endSessionEndpoint = endSessionEndpoint; }

    public String getJwksUri() { return jwksUri; }
    public void setJwksUri(String jwksUri) { this.jwksUri = jwksUri; }

    public List<String> getScopesSupported() { return scopesSupported; }
    public void setScopesSupported(List<String> scopesSupported) { this.scopesSupported = scopesSupported; }

    public List<String> getResponseTypesSupported() { return responseTypesSupported; }
    public void setResponseTypesSupported(List<String> responseTypesSupported) { this.responseTypesSupported = responseTypesSupported; }

    public List<String> getGrantTypesSupported() { return grantTypesSupported; }
    public void setGrantTypesSupported(List<String> grantTypesSupported) { this.grantTypesSupported = grantTypesSupported; }

    public List<String> getSubjectTypesSupported() { return subjectTypesSupported; }
    public void setSubjectTypesSupported(List<String> subjectTypesSupported) { this.subjectTypesSupported = subjectTypesSupported; }

    public List<String> getIdTokenSigningAlgValuesSupported() { return idTokenSigningAlgValuesSupported; }
    public void setIdTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) { this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported; }

    public List<String> getTokenEndpointAuthMethodsSupported() { return tokenEndpointAuthMethodsSupported; }
    public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) { this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported; }
}

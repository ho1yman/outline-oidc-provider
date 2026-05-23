package com.oidc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "client")
public class ClientEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "client_id", length = 255)
    private String clientId;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "secret", length = 255)
    private String secret;

    @Column(name = "realm_id", length = 36)
    private String realmId;

    @Column(name = "protocol", length = 255)
    private String protocol;

    @Column(name = "public_client")
    private Boolean publicClient;

    @Column(name = "standard_flow_enabled")
    private Boolean standardFlowEnabled;

    @Column(name = "direct_access_grants_enabled")
    private Boolean directAccessGrantsEnabled;

    @Column(name = "base_url", length = 255)
    private String baseUrl;

    @Column(name = "root_url", length = 255)
    private String rootUrl;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getRealmId() { return realmId; }
    public void setRealmId(String realmId) { this.realmId = realmId; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public Boolean getPublicClient() { return publicClient; }
    public void setPublicClient(Boolean publicClient) { this.publicClient = publicClient; }

    public Boolean getStandardFlowEnabled() { return standardFlowEnabled; }
    public void setStandardFlowEnabled(Boolean standardFlowEnabled) { this.standardFlowEnabled = standardFlowEnabled; }

    public Boolean getDirectAccessGrantsEnabled() { return directAccessGrantsEnabled; }
    public void setDirectAccessGrantsEnabled(Boolean directAccessGrantsEnabled) { this.directAccessGrantsEnabled = directAccessGrantsEnabled; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getRootUrl() { return rootUrl; }
    public void setRootUrl(String rootUrl) { this.rootUrl = rootUrl; }
}

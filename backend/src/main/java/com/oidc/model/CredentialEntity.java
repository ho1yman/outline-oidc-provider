package com.oidc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "credential")
public class CredentialEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "type", length = 255)
    private String type;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "secret_data", columnDefinition = "text")
    private String secretData;

    @Column(name = "credential_data", columnDefinition = "text")
    private String credentialData;

    @Column(name = "priority")
    private Integer priority;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSecretData() { return secretData; }
    public void setSecretData(String secretData) { this.secretData = secretData; }

    public String getCredentialData() { return credentialData; }
    public void setCredentialData(String credentialData) { this.credentialData = credentialData; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}

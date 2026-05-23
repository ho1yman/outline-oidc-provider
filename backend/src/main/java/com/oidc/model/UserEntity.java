package com.oidc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_entity")
public class UserEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(name = "realm_id", length = 255)
    private String realmId;

    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "created_timestamp")
    private Long createdTimestamp;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRealmId() { return realmId; }
    public void setRealmId(String realmId) { this.realmId = realmId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(Long createdTimestamp) { this.createdTimestamp = createdTimestamp; }
}

package com.oidc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "realm")
public class RealmEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "login_with_email_allowed")
    private Boolean loginWithEmailAllowed;

    @Column(name = "remember_me")
    private Boolean rememberMe;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Boolean getLoginWithEmailAllowed() { return loginWithEmailAllowed; }
    public void setLoginWithEmailAllowed(Boolean loginWithEmailAllowed) { this.loginWithEmailAllowed = loginWithEmailAllowed; }

    public Boolean getRememberMe() { return rememberMe; }
    public void setRememberMe(Boolean rememberMe) { this.rememberMe = rememberMe; }
}

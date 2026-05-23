package com.oidc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "redirect_uris")
public class RedirectUriEntity {

    @Id
    @Column(name = "client_id", length = 36)
    private String clientId;

    @Column(name = "value", length = 255)
    private String value;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}

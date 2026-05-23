package com.oidc.repository;

import com.oidc.model.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, String> {

    Optional<ClientEntity> findByRealmIdAndClientId(String realmId, String clientId);
}

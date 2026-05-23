package com.oidc.repository;

import com.oidc.model.RedirectUriEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedirectUriRepository extends JpaRepository<RedirectUriEntity, String> {

    List<RedirectUriEntity> findByClientId(String clientId);
}

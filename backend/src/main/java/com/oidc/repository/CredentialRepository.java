package com.oidc.repository;

import com.oidc.model.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, String> {

    List<CredentialEntity> findByUserIdAndType(String userId, String type);
}

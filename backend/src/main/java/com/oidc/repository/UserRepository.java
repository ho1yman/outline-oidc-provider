package com.oidc.repository;

import com.oidc.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByRealmIdAndUsername(String realmId, String username);

    Optional<UserEntity> findByRealmIdAndEmail(String realmId, String email);
}

package com.oidc.repository;

import com.oidc.model.RealmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RealmRepository extends JpaRepository<RealmEntity, String> {

    Optional<RealmEntity> findByName(String name);
}

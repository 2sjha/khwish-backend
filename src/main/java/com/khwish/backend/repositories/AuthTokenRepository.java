package com.khwish.backend.repositories;

import com.khwish.backend.models.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {

    AuthToken findByUserId(UUID userId);

    @Transactional
    void deleteByUserId(UUID userId);
}
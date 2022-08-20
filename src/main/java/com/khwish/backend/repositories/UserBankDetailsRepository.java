package com.khwish.backend.repositories;

import com.khwish.backend.models.UserBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserBankDetailsRepository extends JpaRepository<UserBankDetails, UUID> {

    UserBankDetails findByUserId(UUID userId);
}
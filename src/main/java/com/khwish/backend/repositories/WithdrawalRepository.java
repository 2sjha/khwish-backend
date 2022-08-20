package com.khwish.backend.repositories;

import com.khwish.backend.models.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, UUID> {

    List<Withdrawal> findAllByUserId(UUID userId);
}

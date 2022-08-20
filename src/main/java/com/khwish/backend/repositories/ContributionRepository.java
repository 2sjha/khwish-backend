package com.khwish.backend.repositories;

import com.khwish.backend.models.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContributionRepository extends JpaRepository<Contribution, UUID> {

    List<Contribution> findAllByGoalIdIn(List<UUID> goalIds);
}
package com.khwish.backend.repositories;

import com.khwish.backend.models.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {

    List<Goal> findAllByEventId(UUID eventId);

    List<Goal> findAllByUserId(UUID userId);

    List<Goal> findAllByIdIn(List<UUID> ids);
}
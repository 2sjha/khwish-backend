package com.khwish.backend.repositories;

import com.khwish.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByPhoneNumber(String phoneNumber);

    User findByEmail(String email);
}
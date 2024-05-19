package com.optculture.app.repositories;

import com.optculture.shared.entities.communication.email.UserFromEmailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFromEmailIdRepository extends JpaRepository<UserFromEmailId,Long> {
    @Query("SELECT e.emailId FROM UserFromEmailId e WHERE e.users.userId = :userId AND e.status = :status")
    List<String> findByUsersUserIdAndStatus(Long userId, int status);
}

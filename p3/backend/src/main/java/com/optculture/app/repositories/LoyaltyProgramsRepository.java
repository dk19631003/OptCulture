package com.optculture.app.repositories;

import com.optculture.shared.entities.loyalty.LoyaltyProgram;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyProgramsRepository extends JpaRepository<LoyaltyProgram,Long> {

    LoyaltyProgram findByUserIdAndProgramId(Long userId,Long programId);
    
    List<LoyaltyProgram> findByUserIdAndStatusIn(Long userId, List<String> status);
}

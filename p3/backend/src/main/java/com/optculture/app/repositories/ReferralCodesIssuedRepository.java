package com.optculture.app.repositories;

import com.optculture.shared.entities.promotion.ReferralcodesIssued;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralCodesIssuedRepository extends JpaRepository<ReferralcodesIssued,Long> {

    Optional<ReferralcodesIssued> findFirstByReferredCidAndUserId(Long contactId, Long userId);
}

package com.optculture.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.communication.ereceipt.DRSmsChannelSent;

@Repository
public interface DRSmsChannelSentRepository extends JpaRepository<DRSmsChannelSent, Long> {

	DRSmsChannelSent findFirstByOriginalShortCodeOrderByIdDesc(String originalShortCode);

}

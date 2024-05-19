package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.optculture.shared.entities.communication.ereceipt.DRSent;

public interface DRSentRepository extends JpaRepository<DRSent, Long>{

	@Modifying(flushAutomatically=true,clearAutomatically= true)
	@Query("update DRSent set status=:status where Id=:Id")
	int updateStatusById(Long Id,String status);
}

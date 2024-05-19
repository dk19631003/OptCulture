package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.communication.Communication;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CommunicationRepository extends JpaRepository<Communication, Long> {

	Communication findByCommunicationId(Long id);
	@Modifying(flushAutomatically = true,clearAutomatically = true)
	@Query("update Communication set status = 'Sent' where communicationId = :commId")
	int updateCommunicationStatus(Long commId);

	List<Communication> findByScheduleTypeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndScheduleTimeBetween(String recurring, LocalDateTime startDate, LocalDateTime endDate, LocalTime localTime, LocalTime localTime1);

	}

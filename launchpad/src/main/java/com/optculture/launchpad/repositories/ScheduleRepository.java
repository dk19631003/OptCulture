package com.optculture.launchpad.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.optculture.shared.entities.communication.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	@Query("SELECT DISTINCT s FROM Schedule s JOIN User u ON s.userId = u.userId WHERE  s.scheduledDate <= :date AND s.status = 0 "
			+ " AND DATE(s.scheduledDate) <= DATE(u.packageExpiryDate) AND u.enabled = true")
	List<Schedule> getActiveList(@Param("date") LocalDateTime date);


	@Query("SELECT DISTINCT s FROM Schedule s JOIN User u ON s.userId = u.userId WHERE s.userId IS NOT NULL AND s.scheduledDate <= :date "
			+ "  AND s.status = 0 AND ( DATE(s.scheduledDate) > DATE(u.packageExpiryDate) OR u.enabled = false )")
	List<Schedule> getInActiveList(@Param("date") LocalDateTime date);


	
	@Query("SELECT DISTINCT s FROM Schedule s JOIN User u ON s.userId = u.userId WHERE s.status = 0 "
				//+ " AND DATE_ADD(CONVERT(s.scheduledDate, DATETIME),INTERVAL 9 HOUR) < :date " +
				+ " AND s.scheduledDate < :date " +
				"	AND DATE(s.scheduledDate) <= DATE(u.packageExpiryDate) AND u.enabled = true") 
	List<Schedule> getActiveForLongList(@Param("date") LocalDateTime currentDateMinus9Hrs);

//	Optional<Schedule> findFirstByCommunicationCommunicationIdAndStatus(Long communicationId, byte status);

	List<Schedule> findByCommunicationCommunicationIdAndScheduledDateGreaterThanEqualAndScheduledDateLessThan(Long communicationId, LocalDateTime startTime, LocalDateTime endTime);
}


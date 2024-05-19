package com.optculture.app.repositories;

import com.optculture.shared.entities.communication.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findFirstByCommunicationCommunicationIdAndUserIdOrderByScheduledDateDesc(Long communicationId, Long userId);
    
    Schedule findOneByUserIdAndCrId(Long userId, Long crId);
}

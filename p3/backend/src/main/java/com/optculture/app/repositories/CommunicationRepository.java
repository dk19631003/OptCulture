package com.optculture.app.repositories;

import com.optculture.app.dto.campaign.CommunicationDTO;
import com.optculture.shared.entities.communication.Communication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CommunicationRepository extends JpaRepository<Communication,Long> {
//    @Query("SELECT new com.optculture.app.dto.campaign.CommunicationDTO(co.communicationId as commId, co.name as campaignName,co.channelType as channelType,'Marketing' as campaignType,'Transactional' as campaignCategory, co.segmentId as segmentLists,co.status as status,s.scheduledDate as scheduleDate) FROM Schedule s JOIN s.communication co  WHERE co.userId = :userId AND co.channelType = :channelType ORDER BY s.scheduledDate DESC")
    @Query("SELECT new com.optculture.app.dto.campaign.CommunicationDTO(co.communicationId as commId, co.name as campaignName,co.channelType as channelType,'Marketing' as campaignType,co.frequencyType as frequencyType,co.scheduleType as scheduleType,co.startDate as startDate,co.endDate as endDate, co.segmentId as segmentLists,co.status as commStatus,s.status as status,s.scheduledDate as scheduleDate,s.crId as crId) FROM Communication co LEFT JOIN Schedule s ON co.communicationId = s.communication.communicationId AND co.userId = :userId  WHERE  co.channelType IN :channelTypes AND co.userId = :userId AND (:#{#comm.name} IS NULL OR co.name LIKE %:#{#comm.name}% ) ORDER BY CASE WHEN s.scheduledDate IS NOT NULL THEN s.scheduledDate ELSE co.startDate END DESC")
    Page<CommunicationDTO> getCampaignListByUserId(Communication comm, Long userId, List<String> channelTypes, PageRequest of);

    @Query("SELECT new com.optculture.app.dto.campaign.CommunicationDTO(co.communicationId as commId, co.name as campaignName,co.channelType as channelType,  co.segmentId as segmentLists,CAST(co.templateId AS java.lang.String) as templateId,co.messageContent as msgContent,s.scheduledDate as scheduleDate,s.status as status ,co.scheduleType,co.frequencyType as frequencyType,co.startDate as startDate,co.endDate as endDate, co.mediaUrl,co.placeholderMappings, co.senderId, co.jsonContent as jsonContent,co.attributes as attribute ) FROM Communication co LEFT JOIN Schedule s ON co.communicationId = s.communication.communicationId WHERE co.communicationId = :commId AND co.userId = :userId ORDER BY  CASE WHEN s.scheduledDate IS NOT NULL THEN s.scheduledDate ELSE co.startDate END  DESC,co.modifiedDate DESC limit 1")
    Optional<CommunicationDTO> findByCommunicationId(Long commId, Long userId);

    Communication findByCommunicationIdAndUserId(Long commId, Long userId);
    
    List<Communication> findByUserIdOrderByNameAsc(Long userId);
}

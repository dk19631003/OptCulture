package com.optculture.app.repositories;

import com.optculture.app.dto.campaign.TemplateDto;
import com.optculture.shared.entities.communication.CommunicationTemplate;
import com.optculture.shared.entities.communication.sms.TransactionalTemplates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommunicationTemplateRepository extends JpaRepository<CommunicationTemplate,Long> {

    @Query("SELECT new com.optculture.app.dto.campaign.TemplateDto(t.id as templateId,t.templateName as templateName,t.msgContent as templateContent,t.templateRegisteredId as templateRegId,t.headerText as headerText,t.senderId as senderId,t.msgType as msgType, t.footer as footer) FROM CommunicationTemplate t WHERE  t.orgId = :orgId AND ( :templateName IS NULL OR t.templateName LIKE %:templateName% ) AND t.status = :status AND t.channelType = :channelType GROUP BY t.templateRegisteredId")
    Page<TemplateDto> findByOrgIdAndStatusOrderByModifiedDateDesc(Long orgId, String status, String templateName, String channelType, PageRequest pg);

    @Query("SELECT new com.optculture.app.dto.campaign.TemplateDto(t.id as templateId,t.templateName as templateName,t.msgContent as templateContent,t.templateRegisteredId as templateRegId,t.headerText as headerText,t.senderId as senderId,t.msgType as msgType, t.footer as footer) FROM CommunicationTemplate t WHERE  t.orgId = :orgId AND ( :templateName IS NULL OR t.templateName LIKE %:templateName% ) AND t.status = :status AND t.channelType = :channelType ORDER BY t.id DESC")
    Page<TemplateDto> findByOrgIdAndStatus(Long orgId, String status, String templateName, String channelType, PageRequest pg);

    Optional<CommunicationTemplate> findFirstByTemplateRegisteredIdAndOrgIdOrderByModifiedDateDesc(String templateId, Long userOrgId);

    @Query("SELECT new com.optculture.app.dto.campaign.TemplateDto(t.id as templateId,t.templateName as templateName, t.msgContent as templateContent,t.jsonContent as jsonContent,t.templateType as templateType,t.createdDate as createdDate, t.modifiedDate as modifiedDate) FROM CommunicationTemplate t WHERE  t.orgId = :orgId AND t.channelType = :channelType AND t.templateType = :templateType AND ( :templateName IS NULL OR t.templateName LIKE %:templateName% ) ORDER BY t.id DESC")
    Page<TemplateDto> findByOrgIdAndTemplateNameOrderByModifiedDateDesc(Long orgId,String templateName,String channelType,String templateType,PageRequest pg);
    
	CommunicationTemplate findByUserIdAndTemplateNameAndChannelType(Long userId,String templateName,String channelType);

}

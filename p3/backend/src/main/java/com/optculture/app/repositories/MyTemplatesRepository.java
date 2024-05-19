package com.optculture.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.communication.email.MyTemplates;

@Repository
public interface MyTemplatesRepository extends JpaRepository<MyTemplates, Long> {
	
//	List<MyTemplates> findByUserIdAndEditorType(Long userId, String editorType);
//	
//	MyTemplates findByUserIdAndName(Long userId, String tempName);
//	
    //@Query("SELECT new com.optculture.app.dto.campaign.TemplateDto(t.id as templateId,t.templateName as templateName,t.msgContent as templateContent,t.templateRegisteredId as templateRegId,t.headerText as headerText,t.senderId as senderId,t.msgType as msgType, t.footer as footer) FROM CommunicationTemplate t WHERE  t.orgId = :orgId AND ( :templateName IS NULL OR t.templateName LIKE %:templateName% ) AND t.status = :status AND t.channelType = :channelType GROUP BY t.templateRegisteredId")

	/*
	 * @Query("Select new com.optculture.app.dto.campaign.TemplateDto(temp.id as myTemplateId,t.name as templateName,t.content as templateContent,t.jsoncontent as jsonContent) FROM MyTemplates t where t.userId = :userId and editorType = :editorType ORDER BY t.myTemplateId DESC"
	 * ) Page<TemplateDto> findByUserIdAndEditorType(Long userId, String
	 * editorType,PageRequest pg);
	 * 
	 */



}

package com.optculture.app.repositories;

import com.optculture.app.dto.campaign.TemplateDto;
import com.optculture.shared.entities.communication.sms.TransactionalTemplates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionalTemplatesRepository extends JpaRepository<TransactionalTemplates,Long> {
    @Query("SELECT new com.optculture.app.dto.campaign.TemplateDto(t.templateName as templateName,t.templateContent as templateContent,t.templateRegisteredId as templateRegId) FROM TransactionalTemplates t WHERE  t.orgId = :orgId AND ( :templateName IS NULL OR t.templateName LIKE %:templateName% ) AND t.status = :status GROUP BY t.templateRegisteredId")
    Page<TemplateDto> findByOrgIdAndStatusOrderByModifiedDateDesc(Long orgId, int status, String templateName, PageRequest pg);

    Optional<TransactionalTemplates> findFirstByTemplateRegisteredIdAndOrgIdOrderByModifiedDateDesc(String string, Long userOrgId);
}

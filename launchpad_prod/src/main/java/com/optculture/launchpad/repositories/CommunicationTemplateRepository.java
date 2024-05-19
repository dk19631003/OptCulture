package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.communication.CommunicationTemplate;

@Repository
public interface CommunicationTemplateRepository extends JpaRepository<CommunicationTemplate, Long> {

	@Query("SELECT c FROM CommunicationTemplate c WHERE c.id = :id")
    CommunicationTemplate findByTemplateId(@Param("id") Long id);

}

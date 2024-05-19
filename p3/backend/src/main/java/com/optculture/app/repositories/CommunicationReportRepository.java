package com.optculture.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.communication.CommunicationReport;

@Repository
public interface CommunicationReportRepository extends JpaRepository<CommunicationReport, Long> {

	CommunicationReport findOneByCrId(Long crId);

}

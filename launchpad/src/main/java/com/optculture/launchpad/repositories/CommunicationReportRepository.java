package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.optculture.shared.entities.communication.CommunicationReport;

@Repository
public interface CommunicationReportRepository  extends JpaRepository<CommunicationReport, Long> {
	
	// if the object is sent in rabbit queue, which was created in the submitter . we can find by Id and access and then update
	// the sent count and undelivered count(can be idwritten this in the Recover method.
	@Transactional
	@Query(value="select * from communication_report where cr_id =:id",nativeQuery=true)
	CommunicationReport findByCrId(Long id);
	
	//@Query(value="select * from communication_report where cr_id =:id",nativeQuery=true)
	//CommunicationReport findByCrId(Long id);
	
	@Transactional
	@Query("select cr from CommunicationReport cr where cr.crId=:id")
	CommunicationReport findByCommReportId(Long id);

	
	@Modifying(clearAutomatically = true,flushAutomatically = true)
	@Query("update CommunicationReport set bounces= :bounceCount where crId=:crId ")
	int setBouncesByCrId(int bounceCount,Long crId);

	@Modifying(clearAutomatically = true,flushAutomatically = true)
	@Query("update CommunicationReport set spams= :spamCount where crId=:crId")
	int setSpamsByCrId(int spamCount, Long crId);
	

}

package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.optculture.shared.entities.communication.CommunicationSent;

@Repository
@Deprecated
public interface CommunicationSentRepository extends JpaRepository<CommunicationSent, Long> {

	// if the object is sent in rabbit queue, which was created in the submitter . we can find by Id and access and then update
	// the sent count and undelivered count(can be written this in the Recover method).

	@Transactional
	@Query(value="select * from communication_sent where sent_id =:id",nativeQuery=true)
	CommunicationSent findBySentId(Long id);
	
	//CommunicationSent findBySentId(Long id);

	@Transactional
	@Query("select cs from CommunicationSent cs where cs.sentId =:id")
	CommunicationSent findByCommSentId(Long id);
	
	
		
	/*
	 * 	else if(type.equals(Constants.CR_TYPE_BOUNCES)) 
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_BOUNCES + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
		    		"') WHERE crId =" + crId;
	 */
	@Query("select count(*) from CommunicationSent where communicationReportId = :crId and status = :sentStatus")
	long findTotalCountByStatus(Long crId,String sentStatus);
		
	
	@Query("select count(*) from CommunicationSent where communicationReportId= :crId and status=:status")
	long findCountByStatus(Long crId,String status);

	@Query(value="select sent_id from communication_sent order by 1 desc limit 1",nativeQuery=true)
	long getLastSentId();
	
	CommunicationSent findByRecipientAndCommunicationReportId(String receipient,Long crId);
	
	@Modifying(flushAutomatically=true,clearAutomatically= true)
	@Query("update CommunicationSent set status=:status where sentId=:sentId")
	int updateStatusBySentId(String status,Long sentId);
	
	long countByCommunicationReportId(Long crId);
	
}

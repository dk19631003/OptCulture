package com.optculture.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.communication.ereceipt.DRSmsSent;

@Repository
public interface DRSmsSentRepository extends JpaRepository<DRSmsSent, Long> {

	DRSmsSent findFirstByOriginalShortCodeOrderByIdDesc(String originalShortCode);

	@Query(value = "SELECT user_id, doc_sid, original_url, sent_date FROM dr_sms_sent WHERE original_short_code = ?1 "
			+ "UNION ALL "
			+ "SELECT user_id, doc_sid, original_url, sent_date FROM dr_sms_channel_sent WHERE original_short_code = ?1 "
			+ "ORDER BY sent_date DESC LIMIT 1", nativeQuery = true)
	List<Object[]> findLatestDRByOriginalShortCode(String originalShortCode);

    DRSmsSent findByUserIdAndDocSid(Long userId, String docSid);
}

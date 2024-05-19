package com.optculture.app.repositories;

import com.optculture.shared.entities.contact.CustomerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.optculture.shared.entities.contact.CustomerFeedback;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {

    Optional<CustomerFeedback> findFirstByContactIdAndUserIdOrderByCreatedDateDesc(Long contactId, Long userId);

    List<CustomerFeedback> findByContactIdAndUserIdAndDocSid(Long contactId, Long userId, String docSid);
}

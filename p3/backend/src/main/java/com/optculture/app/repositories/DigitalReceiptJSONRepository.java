package com.optculture.app.repositories;

import com.optculture.shared.entities.communication.ereceipt.DigitalReceiptsJSON;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DigitalReceiptJSONRepository extends JpaRepository<DigitalReceiptsJSON,Long> {
    DigitalReceiptsJSON findFirstByUserIdAndDocSid(Long userId,String docsId);
}

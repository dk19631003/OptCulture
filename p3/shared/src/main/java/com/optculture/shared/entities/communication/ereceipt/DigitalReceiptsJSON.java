package com.optculture.shared.entities.communication.ereceipt;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "digital_receipts_json")
public class DigitalReceiptsJSON {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dr_json_id")
    private Long drjsonId;

    @Column(name = "json_str")
    private String jsonStr;

    @Column(name = "status")
    private String status;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "mode")
    private String mode;

    @Column(name = "doc_sid")
    private String docSid;

    @Column(name = "source")
    private String source;

    @Column(name = "retry")
    private Integer retryForLtyExtraction;

}

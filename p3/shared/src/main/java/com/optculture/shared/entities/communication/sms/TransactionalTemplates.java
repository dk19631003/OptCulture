package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "transactional_template")
public class TransactionalTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private java.lang.Long transactionId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "template_content")
    private String templateContent;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "type")
    private String type;

    @Column(name = "template_registered_id")
    private String templateRegisteredId;

}

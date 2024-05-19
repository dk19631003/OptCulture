package com.optculture.shared.entities.contact;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customer_feedback")
public class CustomerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "udf1", length = 200)
    private String udf1;

    @Column(name = "udf2", length = 200)
    private String udf2;

    @Column(name = "udf3", length = 200)
    private String udf3;

    @Column(name = "udf4", length = 200)
    private String udf4;

    @Column(name = "udf5", length = 200)
    private String udf5;

    @Column(name = "udf6", length = 200)
    private String udf6;

    @Column(name = "udf7", length = 200)
    private String udf7;

    @Column(name = "udf8", length = 200)
    private String udf8;

    @Column(name = "udf9", length = 200)
    private String udf9;

    @Column(name = "udf10", length = 200)
    private String udf10;

    @Column(name = "udf11", length = 200)
    private String udf11;

    @Column(name = "udf12", length = 200)
    private String udf12;

    @Column(name = "udf13", length = 200)
    private String udf13;

    @Column(name = "udf14", length = 200)
    private String udf14;

    @Column(name = "udf15", length = 200)
    private String udf15;

    @Column(name = "udf16", length = 200)
    private String udf16;

    @Column(name = "udf17", length = 200)
    private String udf17;

    @Column(name = "udf18", length = 200)
    private String udf18;

    @Column(name = "udf19", length = 200)
    private String udf19;

    @Column(name = "udf20", length = 200)
    private String udf20;

    @Column(name = "customer_no", length = 200)
    private String customerNo;

    @Column(name = "doc_sid", length = 200)
    private String docSid;

    @Column(name = "feedback_message", length = 200)
    private String feedbackMessage;

    @Column(name = "source", length = 200)
    private String source;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "store", length = 200)
    private String store;

}

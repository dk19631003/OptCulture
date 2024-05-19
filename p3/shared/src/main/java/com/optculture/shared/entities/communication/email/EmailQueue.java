package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;

@Entity
@Table(name = "email_queue")
public class EmailQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

    @Column(name = "cust_temp_id")
    private java.lang.Long customTemplateId;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "to_email_id", length = 60)
    private String toEmailId;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "child_email", length = 200)
    private String childEmail;

    @Column(name = "child_first_name", length = 200)
    private String childFirstName;

    @Column(name = "date_of_birth", length = 200)
    private String dateOfBirth;

    @Column(name = "message")
    private String message;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "cc_email_id", length = 100)
    private String ccEmailId;

    @Column(name = "bcc_email_id", length = 100)
    private String bccEmailId;

    @Column(name = "loyalty_id")
    private java.lang.Long loyaltyId;

    @Column(name = "delivery_status")
    private java.lang.String deliveryStatus;

    @Column(name = "opens")
    private int opens;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "cust_temp_name")
    private String custTempName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}

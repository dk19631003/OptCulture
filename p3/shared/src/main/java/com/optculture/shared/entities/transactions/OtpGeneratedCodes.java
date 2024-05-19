package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "otp_generated_codes")
public class OtpGeneratedCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_code_id")
    private java.lang.Long otpCodeId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "otpcode", length = 20)
    private String otpCode;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "sent_count")
    private java.lang.Long sentCount;

    @Column(name = "docsid")
    private String docsid;

    @Column(name = "receipt_amount")
    private double receiptAmount;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "email")
    private String email;

}

package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "support_ticket")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private java.lang.Long ticketId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "client_name")
    private java.lang.String clientName;

    @Column(name = "contact_name")
    private java.lang.String contactName;

    @Column(name = "contact_email", length = 255)
    private java.lang.String contactEmail;

    @Column(name = "contact_phone", length = 255)
    private java.lang.String contactPhone;

    @Column(name = "product_area", length = 255)
    private java.lang.String productArea;

    @Column(name = "description", length = 1000)
    private java.lang.String description;

    @Column(name = "captcha", length = 255)
    private java.lang.String captcha;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "type")
    private java.lang.Byte type;

    @Column(name = "userOrg_name")
    private java.lang.String userOrgName;

    @Column(name = "user_name")
    private java.lang.String userName;

    @Column(name = "product_area_type")
    private java.lang.Byte productAreaType;

    @Column(name = "file_name")
    private java.lang.String fileName;

    @Column(name = "file_path")
    private java.lang.String filePath;

    @Column(name = "status")
    private java.lang.String status;

}

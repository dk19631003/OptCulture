package com.optculture.shared.entities.unused;

import jakarta.persistence.*;

@Entity
@Table(name = "contact_score")
public class ContactScores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "email_Id")
    private String emailId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "page_visited_count")
    private java.lang.Integer pageVisitedCount;

    @Column(name = "down_loaded_count")
    private java.lang.Integer downLoadedCount;

    @Column(name = "source_of_visited_count")
    private java.lang.Integer sourceOfVisitCount;

    @Column(name = "email_opened_count")
    private java.lang.Integer emailOpenedCount;

    @Column(name = "email_clicked_count")
    private java.lang.Integer emailClickedCount;

    @Column(name = "email_not_opened_count")
    private java.lang.Integer emailNotOpenedCount;

    @Column(name = "email_unsubscribed_count")
    private java.lang.Integer emailUnsubscribedCount;

    @Column(name = "form_submitted_count")
    private java.lang.Integer formSubmittedCount;

    @Column(name = "form_abondoned_count")
    private java.lang.Integer formAbondonedCount;

    @Column(name = "form_fill_ratio_count")
    private java.lang.Integer formFillRatioCount;

    @Column(name = "last_modified_date")
    private java.util.Calendar lastModifiedDate;

    @Column(name = "total", length = 20)
    private java.lang.Long total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}

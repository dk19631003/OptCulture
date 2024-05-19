package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "UserDesignedCustomRows")
public class UserDesignedCustomRows {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "templaterow_id")
    private java.lang.Long templateRowId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "rowcategory", length = 50)
    private String rowCategory;

    @Column(name = "templatename", length = 50)
    private String templateName;

    @Column(name = "rowjsondata")
    private String rowJsonData;

    @Column(name = "rowhtmldata")
    private String rowHtmlData;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

}

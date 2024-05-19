package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "value_codes")
public class ValueCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "associated_with_FBP")
    private boolean associatedWithFBP;

    @Column(name = "description")
    private String description;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

}

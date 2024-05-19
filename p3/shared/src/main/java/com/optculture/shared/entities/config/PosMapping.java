package com.optculture.shared.entities.config;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pos_mapping")
public class PosMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_id")
    private long posId;

    @Column(name = "cust_field_name")
    private String customFieldName;

    @Column(name = "display_label")
    private String displayLabel;

    @Column(name = "default_Ph_Value")
    private String defaultPhValue;

    @Column(name = "default_Ph_Value_set")
    private String defaultPhValueSet;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "dr_data_type")
    private String drDataType;

    @Column(name = "mapping_type")
    private String mappingType;

    @Column(name = "pos_attribute")
    private String posAttribute;

    @Column(name = "digital_receipt_attribute")
    private String digitalReceiptAttribute;

    @Column(name = "optional_values")
    private String optionalValues;

    @Column(name = "unique_priority")
    private java.lang.Integer uniquePriority;

    @Column(name = "unique_in_across_files")
    private java.lang.Integer uniqueInAcrossFiles;

    @Column(name = "user_id")
    private long userId;

}

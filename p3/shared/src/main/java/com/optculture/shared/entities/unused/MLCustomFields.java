package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "ml_customFileds")
public class MLCustomFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private java.lang.Long cId;

    @Column(name = "selected_field", length = 50)
    private String selectedField;

    @Column(name = "field_index")
    private int fieldIndex;

    @Column(name = "custfield_name", length = 100)
    private String custFieldName;

    @Column(name = "data_type", length = 30)
    private String dataType;

    @Column(name = "default_value", length = 256)
    private String defaultValue;

    @Column(name = "format", length = 256)
    private String format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private com.optculture.shared.entities.contact.MailingList mailingList;

}

package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "sales_literal_hashCode")
public class SalesLiteralHashCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_literal_id")
    private java.lang.Long salesLiteralId;

    @Column(name = "sales_literal_hashCode", length = 100)
    private String hashCode;

    @Column(name = "list_id")
    private long listId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "current_file")
    private boolean currentFile;

}

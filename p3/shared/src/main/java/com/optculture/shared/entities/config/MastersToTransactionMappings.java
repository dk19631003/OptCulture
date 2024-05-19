package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "masters_to_transaction_mappings")
public class MastersToTransactionMappings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "list_id")
    private java.lang.Long listId;

    @Column(name = "type")
    private String type;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "last_modifiedd_date")
    private java.util.Calendar lastModifieddDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PosMapping parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private PosMapping childId;

}

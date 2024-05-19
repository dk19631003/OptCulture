package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "my_folders")
public class MyFolders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private java.lang.Long folderId;

    @Column(name = "folder_name", length = 100)
    private String folderName;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "type")
    private String type;

}

package com.optculture.shared.entities.communication.ereceipt;

import jakarta.persistence.*;
@Entity
@Table(name = "digital_receipt_my_templates")
public class DigitalReceiptMyTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_template_id")
    private java.lang.Long myTemplateId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "json_content", length = 16777215)
    private String jsonContent;

    @Column(name = "autoSave_HtmlContent")
    private String autoSaveHtmlContent;

    @Column(name = "autoSave_JsonContentcontent", length = 16777215)
    private String autoSaveJsonContent;

    @Column(name = "editor_type", length = 50)
    private String editorType;

    @Column(name = "folder_name", length = 90)
    private String folderName;

    @Column(name = "modifiedby")
    private java.lang.Long modifiedby;

    @Column(name = "createdby")
    private java.lang.Long createdBy;

    @Column(name = "auto_modified_Date")
    private java.util.Calendar onAutoModifiedDate;

    @Column(name = "org_id")
    private java.lang.Long orgId;

}

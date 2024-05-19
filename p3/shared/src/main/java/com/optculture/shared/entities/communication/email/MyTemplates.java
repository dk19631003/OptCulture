package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "my_templates")
public class MyTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_template_id")
    private java.lang.Long myTemplateId;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "content")
    private String content;

    @Column(name = "json_content", length = 16777215)
    private String jsoncontent;

    @Column(name = "date")
    private java.util.Calendar createdDate;

    @Column(name = "editor_type", length = 30)
    private String editorType;

    @Column(name = "folder_name", length = 30)
    private String folderName;

    @Column(name = "parent_dir", length = 30)
    private String parentDir;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;

}

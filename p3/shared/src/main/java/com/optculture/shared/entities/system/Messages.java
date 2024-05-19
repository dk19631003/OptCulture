package com.optculture.shared.entities.system;

import java.time.LocalDateTime;

import com.optculture.shared.entities.org.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "message")
    private String message;
//changed datatype
    @Column(name = "Created_date")
    private LocalDateTime createdDate;

    @Column(name = "folder", length = 50)
    private String folder;

    @Column(name = "read_flag")
    private boolean read;

    @Column(name = "type", length = 30)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User users;
    //to log all  activities did
    public Messages(String module, String subject, String message, LocalDateTime createdtime,
    		String folder,boolean read,String info, User user) {
    	this.module = module;
    	this.subject =subject;
    	this.message = message;
    	this.createdDate = createdtime;
    	this.folder = folder;
    	this.read = read;
    	this.type = info;
    	this.users = user;
	}

}

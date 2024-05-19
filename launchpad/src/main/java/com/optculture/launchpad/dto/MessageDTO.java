package com.optculture.launchpad.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
	 private Long id;
     private String module;
     private String subject;
     private String message;
     private LocalDateTime createdDate;
     private String folder;
     private boolean read;
     private String type;
     private Long userId;
     
     
    
     

}

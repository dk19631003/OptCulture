package com.optculture.app.dto.campaign.template;

import lombok.Data;

@Data
public class TemplateMapper {
   private String templateName;
   private String msgContent;
   private String senderId;
   private String templateRegisteredId;
   private String status;
   private String  createdDate;
}

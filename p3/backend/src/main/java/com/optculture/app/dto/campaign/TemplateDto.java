package com.optculture.app.dto.campaign;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TemplateDto {
    Long templateId;
    String templateName;
    String templateContent;
    String templateRegId;
    String headerText;
    String senderId;
    String msgType;
    String footer;
    
    String jsonContent;
    String templateType;
    LocalDateTime createdDate;
    LocalDateTime modifiedDate;
}


package com.optculture.app.dto.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignSchRequest {
    Long commId;
    String campaignName;
    String channelType;
    String segmentIds;
    String senderId;
    Long templateId;
    String scheduleDate;
    String createdDate;
    String messageContent;
    String scheduleType;
    String frequencyType;
    String startDate;
    String endDate;
    String mediaUrl;
    String placeholderMappings;
   
    String fromEmail;
    String replyEmail;
    String subject;
    String fromName;
    String jsonContent;
    String status ; //draft or active
}

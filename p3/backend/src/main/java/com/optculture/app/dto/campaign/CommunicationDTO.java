package com.optculture.app.dto.campaign;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommunicationDTO {
	
   
	Long commId;
    String campaignName;
    String channelType;
    String campaignType;
    String campaignCategory;
    String segmentLists;
    Long configured;
    Long sent;
    Integer clicks;
    Long delivered;
    Byte status;
    LocalDateTime scheduleDate;
    String templateId;
    String templateName;
    String headerText;
    String msgContent;
    String scheduleType;
    String frequencyType;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String msgType;
    String placeholderMap;
    String footer;
    String senderId;
    String periodType;
    Long crId;
    String fromEmail;
    String replyEmail;
    String subject;
    String fromName;
    String jsonContent;
    String attribute;
    
    public CommunicationDTO(final Long commId, final String campaignName,final String channelType,final String campaignType,final String frequencyType ,final String scheduleType,final LocalDateTime startDate,final LocalDateTime endDate,final String segmentLists,final String commStatus,final Byte status, final LocalDateTime scheduleDate,final Long crId) {
        this.commId = commId;
        this.campaignName = campaignName;
        this.segmentLists = segmentLists;
        this.status = status;
        if(this.status==null){
            if(commStatus!=null && commStatus.equals("Draft")) this.status=-1;
        }
        this.channelType=channelType;
        this.campaignType=campaignType;
        this.scheduleDate = getServerLocalDateToUTC(scheduleDate);
        this.frequencyType=frequencyType;
        this.scheduleType=scheduleType;
        this.startDate=getServerLocalDateToUTC(startDate);
        this.endDate=getServerLocalDateToUTC(endDate);
        this.crId=crId;
    }
    //for edit mode
    public CommunicationDTO(final Long commId, final String campaignName,final String channelType,final String segmentLists,final String templateId,final String msgContent,
                            final LocalDateTime scheduleDate,final Byte status, String scheduleType, String frequencyType, LocalDateTime startDate, LocalDateTime endDate,String headerText,
                            final String placeholderMap,final String senderId,
                            final String jsonContent,final String attribute) {
        this.commId = commId;
        this.campaignName = campaignName;
        this.segmentLists = segmentLists;
        this.templateId=templateId;
        this.msgContent=msgContent;
        this.scheduleDate = getServerLocalDateToUTC(scheduleDate);
        this.status = status;
        this.scheduleType=scheduleType;
        this.frequencyType= frequencyType;
        this.startDate= getServerLocalDateToUTC(startDate);
        this.endDate= getServerLocalDateToUTC(endDate);
        this.headerText=headerText;
        this.channelType=channelType;
        this.placeholderMap=placeholderMap;
        this.senderId=senderId;
       this.jsonContent = jsonContent;
       this.attribute = attribute;
    }
    public LocalDateTime getServerLocalDateToUTC(LocalDateTime scheduleDate){
        Instant currentUtcTime = Instant.now();
        if(scheduleDate==null) return  null;
        // Format UTC time to a custom string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);
        String formattedUtcTime = formatter.format(currentUtcTime);

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parse the string into a LocalDateTime object
        LocalDateTime utcCurrent = LocalDateTime.parse(formattedUtcTime,formatter2);
//        System.out.println("Current UTC time : "+utcCurrent);

        LocalDateTime localCurrentTime=LocalDateTime.now();
//        System.out.println("server local time : "+localCurrentTime);
        long minutesDiff = ChronoUnit.MINUTES.between(utcCurrent, localCurrentTime);

        return scheduleDate.minusMinutes(minutesDiff); //subtracting diff to make time in utc.
    }

    public void setAttributeData(String attribute) {
    	try {
    	JSONObject attributes  = new JSONObject(attribute);
    	this.fromEmail = attributes.has("from") ? (String) attributes.get("from") : "";
    	this.replyEmail = attributes.has("replyEmail") ? (String)attributes.get("replyEmail") : "";
    	this.subject = attributes.has("subject") ? (String) attributes.get("subject") : "";
    	this.fromName = attributes.has("fromName") ?(String) attributes.get("fromName") : "";
    	}catch(Exception e) {
    		return;
    	}
    }
}


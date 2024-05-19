package com.optculture.app.services;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.campaign.CampaignFilterReqDto;
import com.optculture.app.dto.campaign.CampaignSchRequest;
import com.optculture.app.dto.campaign.CommunicationDTO;
import com.optculture.app.repositories.CommunicationRepository;
import com.optculture.app.repositories.CommunicationTemplateRepository;
import com.optculture.app.repositories.ScheduleRepository;
import com.optculture.app.repositories.TransactionalTemplatesRepository;
import com.optculture.shared.entities.communication.Communication;
import com.optculture.shared.entities.communication.CommunicationTemplate;
import com.optculture.shared.entities.communication.Schedule;
import com.optculture.shared.entities.communication.sms.TransactionalTemplates;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.util.Constants;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommunicationService {
    @Autowired
    GetLoggedInUser getLoggedInUser;
    @Autowired
    CommunicationRepository communicationRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    CommunicationTemplateRepository templatesRepository;
    Logger logger = LoggerFactory.getLogger(CommunicationService.class);
    public ResponseEntity scheduleCampaign(CampaignSchRequest campaignSchReq) {
        User user= getLoggedInUser.getLoggedInUser();
        Communication communObj;
        if(campaignSchReq.getCommId()!=null){
            Optional<CommunicationDTO> optComm=communicationRepository.findByCommunicationId(campaignSchReq.getCommId(),user.getUserId());
                CommunicationDTO commDto=optComm.get();
                if(commDto.getStatus()!=null && commDto.getStatus()==(byte)1){ //if sent out need to create new communication.
                    communObj= new Communication();
                    communObj.setChannelType(campaignSchReq.getChannelType());
                    communObj.setCreatedDate(getUTCToServerLocal(campaignSchReq.getCreatedDate()));
                }
                else{
                    communObj=communicationRepository.findByCommunicationIdAndUserId(campaignSchReq.getCommId(),user.getUserId());
                    communObj.setModifiedDate(LocalDateTime.now());
                }
        }
       else{
            communObj= new Communication();
            communObj.setChannelType(campaignSchReq.getChannelType());
            communObj.setCreatedDate(getUTCToServerLocal(campaignSchReq.getCreatedDate()));
        }
        communObj.setName(campaignSchReq.getCampaignName());
        communObj.setTemplateId(campaignSchReq.getTemplateId());
        communObj.setUserId(user.getUserId());
        communObj.setStatus(campaignSchReq.getStatus()); //draft or active
        communObj.setStartDate(getUTCToServerLocal(campaignSchReq.getCreatedDate())); //for draft ones ordering by date in list view
        communObj.setSenderId(campaignSchReq.getSenderId());
        communObj.setPlaceholderMappings(campaignSchReq.getPlaceholderMappings());
        communObj.setMediaUrl(campaignSchReq.getMediaUrl());
        communObj.setSegmentId(campaignSchReq.getSegmentIds());
        communObj.setMessageContent(campaignSchReq.getMessageContent());
        communObj.setJsonContent(campaignSchReq.getJsonContent());

        try {
        	JSONObject attributes =  new JSONObject();

        if(campaignSchReq.getFromEmail() != null && !campaignSchReq.getFromEmail().isEmpty()) {
        	
        	attributes.put("from",campaignSchReq.getFromEmail());
        	
		}
        if(campaignSchReq.getReplyEmail() != null  &&  !campaignSchReq.getReplyEmail().isEmpty()) {
        	attributes.put("replyEmail", campaignSchReq.getReplyEmail());
        }
        if(campaignSchReq.getSubject() != null  && !campaignSchReq.getSubject().isEmpty()) {
        	attributes.put("subject", campaignSchReq.getSubject());

        }
        if(campaignSchReq.getFromName() != null && !campaignSchReq.getFromName().isEmpty()) {
        	attributes.put("fromName", campaignSchReq.getFromName());

        }
         communObj.setAttributes(attributes.toString());
        }catch(Exception e){
        	logger.info("Error while creating the json Object : "+e);
        }
        if(campaignSchReq.getScheduleType().equals("ONE TIME")){
            communObj.setScheduleType("ONE TIME");
            Communication savedComm= communicationRepository.save(communObj);
            Schedule scheduleObj=null;
            if(campaignSchReq.getStatus().equals("Active")) {
            Optional<Schedule> scheduleOpt=scheduleRepository.findFirstByCommunicationCommunicationIdAndUserIdOrderByScheduledDateDesc(communObj.getCommunicationId(),user.getUserId());
            if(!scheduleOpt.isPresent() || (scheduleOpt.get().getStatus() == (byte)1)){
                scheduleObj = new Schedule();
                scheduleObj.setChannelType(campaignSchReq.getChannelType());
            }
            else { //if already schedule exist in draft/active/failed mode we can edit same object.
                scheduleObj = scheduleOpt.get();
            }

                scheduleObj.setCommunication(savedComm);
                scheduleObj.setStatus((byte) 0);
                scheduleObj.setUserId(user.getUserId());
                LocalDateTime schDate = getUTCToServerLocal(campaignSchReq.getScheduleDate());
                logger.info("client ime in UTC :" + campaignSchReq.getScheduleDate());
                logger.info("client time after converted UTC to Server Local time :" + schDate);
                scheduleObj.setScheduledDate(schDate);
                communObj.setStartDate(schDate);
                scheduleRepository.save(scheduleObj);
            }
        }
        else{
            LocalDateTime firstSceduleDate=getUTCToServerLocal(campaignSchReq.getStartDate());
            LocalDateTime endDate=getUTCToServerLocal(campaignSchReq.getEndDate());
            //ToDo change time
            communObj.setScheduleTime(firstSceduleDate.toLocalTime());
            communObj.setStartDate(firstSceduleDate);
            communObj.setEndDate(endDate);
            communObj.setScheduleType("RECURRING");
            communObj.setFrequencyType(campaignSchReq.getFrequencyType());
            Communication savedComm= communicationRepository.save(communObj);
        }
        return new ResponseEntity<>("Schedule created !",HttpStatus.OK);
    }
    public LocalDateTime getUTCToServerLocal(String utcTimeString){

        Instant instant = Instant.parse(utcTimeString);
        // Convert Instant to LocalDateTime in the desired time zone
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        // Print the local date and time
        return localDateTime;
    }
    public Page<CommunicationDTO> getCampaignListByChannelTypeAndUserId(Long userId, CampaignFilterReqDto campaignFilterReq) {
        int pageNumber=campaignFilterReq.getPageNumber();
        int pageSize= campaignFilterReq.getPageSize();
        List<String> channelTypes=campaignFilterReq.getChannelTypes();
        String criteria=campaignFilterReq.getCriteria();
        String value=campaignFilterReq.getSearchValue();
        Communication comm = new Communication();
        if(criteria!=null && !criteria.isEmpty()){
            switch (criteria){
                case "NAME":comm.setName(value);break;
            }
        }
        return communicationRepository.getCampaignListByUserId(comm,userId,channelTypes, PageRequest.of(pageNumber,pageSize));
    }

    public CommunicationDTO getCampaignByCampaignId(Long commId, User user) {
        Optional<CommunicationDTO> commDto=communicationRepository.findByCommunicationId(commId,user.getUserId());
        if(commDto.isPresent()){
        	CommunicationDTO comm=commDto.get();
        	if(comm.getChannelType().equalsIgnoreCase(Constants.TYPE_EMAIL_CAMPAIGN)) {
            	try {
            	comm.setAttributeData(comm.getAttribute());
            	}catch(Exception e) {
            		logger.error("Error while getting attributes fields in communication object :"+e);
            	}
            	
                return comm;
            }
            
            Optional<CommunicationTemplate> templateOpt ;
            if(comm.getChannelType().equalsIgnoreCase("SMS")) {
                templateOpt  =templatesRepository.findFirstByTemplateRegisteredIdAndOrgIdOrderByModifiedDateDesc(comm.getTemplateId(), user.getUserOrganization().getUserOrgId());
            }
            else{ //whatsapp using primary key as id
            	
                templateOpt=templatesRepository.findById(Long.parseLong(comm.getTemplateId()));
            }
            if (templateOpt.isPresent()){
                CommunicationTemplate template=templateOpt.get();
               comm.setTemplateName(template.getTemplateName());
               comm.setMsgType(template.getMsgType());
               comm.setFooter(template.getFooter());
                String msgType=template.getMsgType();
               if(msgType!=null && msgType.equalsIgnoreCase("TEXT"))
                   comm.setHeaderText(template.getHeaderText());
            }
            
        return  comm;
        }
        return null;
    }

	public Map<String, String> getAllCampaignNames() {
		Map<String, String> campNamesMap = new LinkedHashMap<>();

		communicationRepository.findByUserIdOrderByNameAsc(getLoggedInUser.getLoggedInUser().getUserId())
				.forEach(campaign -> campNamesMap.put(campaign.getCommunicationId().toString(), campaign.getName()));

		return campNamesMap;
	}
	public String updateBeeInCommunication(Long communicationId, User user,String json, String html) {
		// TODO Auto-generated method stub
		String response = "Success";
		try {
			Communication commObj = communicationRepository.findByCommunicationIdAndUserId(communicationId, user.getUserId());
			
			if(commObj != null ) {
				commObj.setJsonContent(json);
				commObj.setMessageContent(html);
				commObj.setModifiedDate(LocalDateTime.now());
				
			communicationRepository.save(commObj);
			logger.info("Successfully saved json and html of the object"+commObj);
			return html+","+response;
			
			}else {
				logger.info("No communication is found in this user for this Id");
				return "No Communication Found";
			}
			
		}catch(Exception e) {
			logger.error("Exception while updating json,html in communication Object  "+e);
			return null;
		}
	}
}

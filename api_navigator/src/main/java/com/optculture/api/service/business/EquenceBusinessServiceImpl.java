package com.optculture.api.service.business;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.optculture.api.configs.MessagingConfigs;
import com.optculture.api.exception.BaseServiceException;
import com.optculture.api.general.StatusCodes;
import com.optculture.api.model.BaseRequestObject;
import com.optculture.api.model.BaseResponseObject;
import com.optculture.api.model.EquenceDLRRequestObject;
import com.optculture.api.repositories.CommunicationReportRepository;
import com.optculture.api.repositories.ContactRepository;
import com.optculture.api.service.EquenceBusinessService;
import com.optculture.shared.entities.communication.CommunicationEvent;
import com.optculture.shared.entities.communication.CommunicationReport;
import com.optculture.shared.util.Constants;


@Service
@Qualifier("equenceBusinessServiceImpl")
public class EquenceBusinessServiceImpl implements EquenceBusinessService {

	Logger logger = LoggerFactory.getLogger(EquenceBusinessServiceImpl.class);

	//@Autowired
	//CommunicationEventHandler communicationEventHandler;


	CommunicationEvent communicationevent;

	CommunicationReport comreport;

	@Autowired
	CommunicationReportRepository communicationReportRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;


	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {

		String action = baseRequestObject.getAction();

		if(action == null){
			throw new BaseServiceException("No action/type found");
		}

		BaseResponseObject baseResponseObject = null;//response to return 

		if(action.equals("DLR")) {

			EquenceDLRRequestObject EquenceDLRRequestObj = (EquenceDLRRequestObject)baseRequestObject;

			baseResponseObject = processDLRRequest(EquenceDLRRequestObj);
		}

		return baseResponseObject;
	}//processRequest

	@Override
	public BaseResponseObject processDLRRequest(EquenceDLRRequestObject EquenceDLRRequestObj)
			throws BaseServiceException {

		logger.debug("----------< processing the DLR request >----------");

		//String msgID = EquenceDLRRequestObj.getMSGID();
		String mobileNumber = EquenceDLRRequestObj.getMobile_no();
		String status = EquenceDLRRequestObj.getSms_delv_status();

		BaseResponseObject baseResponseObject = new BaseResponseObject();

		if(status == null || mobileNumber == null) {

			throw new BaseServiceException("one of the required parameters is not received");
		}
		else if(mobileNumber!=null) { //msgID != null && sentId!=null

			processCampaignDLR(EquenceDLRRequestObj);
		}

		return baseResponseObject;
	}//processDLRRequest

	public void processCampaignDLR(EquenceDLRRequestObject EquenceDLRRequestObj) throws  BaseServiceException {

		logger.debug("----------< processing processCampaignDLR >----------");


		//Long msgID = Long.parseLong(EquenceDLRRequestObj.getMSGID());

		String msgID = EquenceDLRRequestObj.getMSGID();
		String[] strArr = msgID.split(Constants.DELIMETER_DOUBLECOLON);
		Long crId=Long.parseLong(strArr[0]);
		Long campId =Long.parseLong(strArr[1]);

		String mobileNumber = EquenceDLRRequestObj.getMobile_no();
		String status = EquenceDLRRequestObj.getSms_delv_status();
		String mrID = EquenceDLRRequestObj.getMrId();
		String reason = EquenceDLRRequestObj.getReason();
		String type = StatusCodes.equenceStatusCodesMap.get(status);
		String Status = "";
		logger.info("msgID--- {} ",msgID);
		if(mobileNumber.length()> 10) {

			mobileNumber= mobileNumber.substring(mobileNumber.length() - 10);
		}

		comreport = communicationReportRepository.findByCrId(crId);
		logger.info("comreport value is {}",comreport);


		if(StatusCodes.DeliveredSet.contains(type)){

			logger.debug("entering delivered condition");
			Status=StatusCodes.STATUS_DELIVERED;

		}else if(StatusCodes.BouncedSet.contains(type) ) {

			logger.debug("entering bounced condition");
			Status=StatusCodes.STATUS_BOUNCED;

			if(StatusCodes.SuppressedSet.contains(type)){

				logger.debug("entering suppressed condition");
				Status=StatusCodes.STATUS_SUPPRESSED;
				/*if (comreport!=null) {
					addToSuppressedContacts(mobileNumber,comreport.getUserId(),type,reason);
				}*/
			}
		}
		else {

			Status=StatusCodes.STATUS_UNDELIVERED;
		}

		//Publish to the event
		CommunicationEvent communicationEvent = new CommunicationEvent(comreport.getCrId(),campId, mobileNumber, Status, LocalDateTime.now(), mrID!=null ? mrID : Constants.STRING_NILL, "SMS", comreport.getUserId(), 0L);
		rabbitTemplate.convertAndSend(MessagingConfigs.COMMUNICATION_EVENT_EXCHANGE, MessagingConfigs.COMMUNICATION_EVENT_ROUTING_KEY , communicationEvent);


	}//processCampaignDLR

	public void addToSuppressedContacts( String mobileNumber,Long userId, String type, String reason) {

		logger.debug("----------< adding contact into suppressed >----------");
		//updating status in contacts table
		contactRepository.updateContactBymobilePhone(type, userId, mobileNumber);

	}

}

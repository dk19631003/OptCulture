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
import com.optculture.api.model.WAHTTPDLRRequestObject;
import com.optculture.api.service.WABusinessService;
import com.optculture.shared.entities.communication.CommunicationEvent;
import com.optculture.shared.util.Constants;

@Service
@Qualifier("waBusinessServiceImpl")
public class WABusinessServiceImpl implements WABusinessService {

	Logger logger = LoggerFactory.getLogger(WABusinessServiceImpl.class);

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

			WAHTTPDLRRequestObject WAHTTPDLRRequestObj = (WAHTTPDLRRequestObject)baseRequestObject;

			baseResponseObject = processDLRRequest(WAHTTPDLRRequestObj);
		}

		return baseResponseObject;
	}//processRequest

	@Override
	public BaseResponseObject processDLRRequest(WAHTTPDLRRequestObject WAHTTPDLRRequestObj)
			throws BaseServiceException {

		//logger.debug("----------< processing the DLR request >----------");//TODO keep only for testing

		String mobileNumber = WAHTTPDLRRequestObj.getMobileNumber();
		String status = WAHTTPDLRRequestObj.getStatus();
		Long crId = WAHTTPDLRRequestObj.getCrId();
		Long campId = WAHTTPDLRRequestObj.getCampId();
		Long contactId = WAHTTPDLRRequestObj.getContactId();
		Long userId = WAHTTPDLRRequestObj.getUserId();

		BaseResponseObject baseResponseObject = new BaseResponseObject();

		if(status == null || crId==null || campId==null || mobileNumber==null || contactId==null || userId==null)
			throw new BaseServiceException("One of the required parameters is not received");

		else{

			if(mobileNumber.length()> 10)
				mobileNumber = mobileNumber.substring(mobileNumber.length()-10);

			String oc_status = Constants.STRING_NILL;

			if(StatusCodes.DeliveredSet.contains(status))
				oc_status = StatusCodes.STATUS_DELIVERED;

			else if(StatusCodes.BouncedSet.contains(status))
				oc_status = StatusCodes.STATUS_BOUNCED;

			else if(StatusCodes.ReadSet.contains(status))
				oc_status = StatusCodes.STATUS_READ;

			//Publish the event
			CommunicationEvent ce = new CommunicationEvent(crId, campId, mobileNumber, oc_status, LocalDateTime.now(), Constants.STRING_NILL, "WhatsApp", userId, contactId);
			rabbitTemplate.convertAndSend(MessagingConfigs.COMMUNICATION_EVENT_EXCHANGE, MessagingConfigs.COMMUNICATION_EVENT_ROUTING_KEY , ce);
		}

		return baseResponseObject;
	}//processDLRRequest

}

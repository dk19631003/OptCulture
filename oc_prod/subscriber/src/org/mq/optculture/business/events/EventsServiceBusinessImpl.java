package org.mq.optculture.business.events;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.contacts.CustomFieldValidator;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.EventsDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.events.Event;
import org.mq.optculture.model.events.EventInfo;
import org.mq.optculture.model.events.EventRequest;
import org.mq.optculture.model.events.EventResponse;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.model.events.Header;
import org.mq.optculture.model.events.SortBy;
import org.mq.optculture.model.events.Status;
import org.mq.optculture.model.events.User;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class EventsServiceBusinessImpl implements EventsBusinesService {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject = new BaseResponseObject();
		EventResponse eventResponse = null;

		try {
			logger.debug("-------entered processRequest---------");
			// json to object
			Gson gson = new Gson();
			EventRequest eventRequest = null;
			try {
				eventRequest = gson.fromJson(baseRequestObject.getJsonValue(), EventRequest.class);
			} catch (JsonSyntaxException e) {
				logger.error("Exception ::", e);
				Status status = new Status("900000",
						PropertyUtil.getErrorMessage(900000, OCConstants.ERROR_EVENTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				eventResponse = prepareFinalResponse(new Header(), status, eventRequest,null);
				String json = gson.toJson(eventRequest);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.EVENTS_SERVICE_REQUEST);
				return baseResponseObject;
			}
			EventsBusinesService eventBusinessService = (EventsBusinesService) ServiceLocator
					.getInstance().getServiceByName(OCConstants.EVENTS_BUSINESS_SERVICE);
			eventResponse = (EventResponse) eventBusinessService.processEventsRequest(eventRequest);

			// object to json
			String json = gson.toJson(eventResponse);
			baseResponseObject.setJsonValue(json);
			baseResponseObject.setAction(OCConstants.EVENTS_SERVICE_REQUEST);
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		logger.debug("-------exit  processRequest---------");
		return baseResponseObject;
}

	@Override
	public BaseResponseObject processEventsRequest(EventRequest eventRequest) throws BaseServiceException {
		// TODO Auto-generated method stub

		User user = eventRequest.getUser();
		EventResponse eventResponse = new EventResponse(); 
		Header header = eventRequest.getHeader();
		EventInfo eventInfo = eventRequest.getEventInfo();
		SortBy sortBy = eventRequest.getSortBy();
		Status status = new Status();
		try {
		UsersDao usersDao = null;
		try {
			usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("e==>"+e);
		}
			 
		String userName = user.getUserName();
		String orgId = user.getOrganizationId();
		String token = user.getToken();
		
		Users userObj = null;
		if(token != null && !token.isEmpty()) userObj = usersDao.findByToken(userName + Constants.USER_AND_ORG_SEPARATOR + orgId, token);
		else userObj = usersDao.findByUsername(userName + Constants.USER_AND_ORG_SEPARATOR + orgId);
		
		status = validateRootObjects(eventRequest);
		if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equalsIgnoreCase(status.getStatus())) {
			logger.info("Entered If root objects");
			eventResponse = prepareFinalResponse(header, status, eventRequest,null);
			return eventResponse;
		}
		
		status = validateInnerObjects(header, user);
		if (status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
			logger.info("Entered If inner objects"+status.getErrorCode());
			eventResponse = prepareFinalResponse(header, status, eventRequest,null);
			return eventResponse;
		}
		
		status = validateEventInfo(eventInfo,sortBy);
		if(status != null && !OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE.equals(status.getStatus())) {
			logger.info("Entered If validateEventInfo objects"+status.getErrorCode());
			eventResponse = prepareFinalResponse(header, status, eventRequest,null);
			return eventResponse;
		}
		
		List<Events> matchedEvents = findByEventInfo(eventInfo,userObj,sortBy);
		List<Event> matchedEvent = setEventResponse(matchedEvents,userObj);
		
		//TODO no events found		
		
		status=new Status("0","Number of events ::"+matchedEvents.size(),OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		eventResponse = prepareFinalResponse(header, status, eventRequest,matchedEvent);
		return eventResponse;
		
		}
		catch(Exception e) {
			logger.info("e==>"+e);
		}
		return eventResponse;
	}
	
	private List<Event> setEventResponse(List<Events> matchedEvents,Users userObj) {
		// TODO Auto-generated method stub
		List<Event> matchedEvent= new ArrayList<Event>();
		for(Events events : matchedEvents) {
		Event event = new Event();
		List<String> imageUrls = new ArrayList<String>();
		event.setEventId(events.getEventId());
		event.setEventTitle(events.getEventTitle());
		event.setEventStatus(events.getEventStatus());
		event.setEventCreateDate(MyCalendar.calendarToString(events.getEventCreateDate(), MyCalendar.FORMAT_DATETIME_STYEAR));
		event.setEventStartDate(events.getEventStartDateAPI()==null?"":events.getEventStartDateAPI());
		event.setEventEndDate(events.getEventEndDateAPI()==null?"":events.getEventEndDateAPI());
		event.setSubtitle(events.getSubtitle());
		event.setDescription(events.getDescription());
		event.setAddressLine1(events.getAddressLine1());
		event.setAddressLine2(events.getAddressLine2());
		event.setState(events.getState());
		event.setZipCode(events.getZipCode());
		event.setStore(events.getStore());
		event.setCity(events.getCity());
		event.setIsOneDay(events.getIsOneDay());
		event.setRightLable(events.getRightLable() == null ? Constants.STRING_NILL : events.getRightLable());
		event.setLeftLable(events.getLeftLable()  == null ? Constants.STRING_NILL : events.getLeftLable());
		event.setRightLableURL(events.getRightLableURL() == null ? Constants.STRING_NILL : events.getRightLableURL());
		event.setLeftLableURL(events.getLeftLableURL() == null ? Constants.STRING_NILL : events.getLeftLableURL());
		
		imageUrls = getImages(imageUrls,userObj.getUserName(),events.getDirId());
		event.setImageUrl(imageUrls);
		matchedEvent.add(event);
		
		}
		return matchedEvent;
	}

	private EventResponse prepareFinalResponse(Header header,Status status,EventRequest eventRequest,List<Event> matchedEvents) throws BaseServiceException {
		
		EventResponse eventResponse=new EventResponse();
		Event event = new Event();
		
		if (eventRequest != null && eventRequest.getHeader() != null) {
			header.setRequestId(eventRequest.getHeader().getRequestId());
			header.setRequestDate(eventRequest.getHeader().getRequestDate());
		}
		if(matchedEvents == null) {
			matchedEvents= new ArrayList<Event>(); 
			matchedEvents.add(event);
		}
		eventResponse.setHeader(header);
		eventResponse.setStatus(status);
		eventResponse.setMatchedEvents(matchedEvents);
		return eventResponse;
	
	}
	
	private Status validateRootObjects(EventRequest eventRequest) throws BaseServiceException {
		Status status= null;
		try {
			logger.debug("-------entered validateRootObject---------");

			if (eventRequest == null) {
				status = new Status("900001", PropertyUtil.getErrorMessage(900001, OCConstants.ERROR_EVENTS_FLAG),
						OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			throw new BaseServiceException("Exception occured while processing validateRootObject::::: ", e);
		}
		logger.info("STATUS==="+status);
		logger.debug("-------exit  validateRootObject---------");
		return status;
	}
	
	private Status validateInnerObjects(Header header, User user)  {
		Status status= null;
		if (header == null) {
			status = new Status("900002", PropertyUtil.getErrorMessage(900002, OCConstants.ERROR_EVENTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if (header.getRequestId() == null || header.getRequestId().isEmpty()) {
			status = new Status("900003", PropertyUtil.getErrorMessage(900003, OCConstants.ERROR_EVENTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		if (header.getRequestDate() == null || header.getRequestDate().isEmpty()) {
			status = new Status("900004", PropertyUtil.getErrorMessage(900004, OCConstants.ERROR_EVENTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if (user == null) {
			status = new Status("900005", PropertyUtil.getErrorMessage(900005, OCConstants.ERROR_EVENTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}

		String userNameStr = user.getUserName();
		if (userNameStr == null || userNameStr.trim().length() == 0) {
			status = new Status("900006", PropertyUtil.getErrorMessage(900006, OCConstants.ERROR_EVENTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		String orgId = user.getOrganizationId();
		if (orgId == null || orgId.trim().length() == 0) {
			status = new Status("900007", PropertyUtil.getErrorMessage(900007, OCConstants.ERROR_EVENTS_FLAG),
					OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		UsersDao usersDao = null;
		try {
			usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}	
		Users users = usersDao.findByToken(user.getUserName()+ Constants.USER_AND_ORG_SEPARATOR + user.getOrganizationId(), user.getToken());
		if (users == null) {
			status = new Status("900008", PropertyUtil.getErrorMessage(900008, OCConstants.ERROR_EVENTS_FLAG)
					,OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		return status;
	}
	
	private List<Events> findByEventInfo(EventInfo eventInfo,Users user,SortBy sortBy) {
		try {
			EventsDao eventDao = (EventsDao) ServiceLocator.getInstance()
					.getDAOByName(OCConstants.EVENTS_DAO);
			return eventDao.getByEventInfo(eventInfo,user.getUserId(),sortBy);
		} catch (Exception e) {
			logger.error("Exception in getting Events ..", e);
		}
		return null;	
	}
	private Status validateEventInfo(EventInfo eventInfo,SortBy sortBy) {		
		Status status = null;
		
		if(eventInfo != null) {
			if(eventInfo.getDate()!=null && !eventInfo.getDate().isEmpty() && !CustomFieldValidator.validateDate(eventInfo.getDate(), "Date", MyCalendar.FORMAT_DATETIME_STYEAR)) {
				logger.info("Error : unable to fetch the requested data,got wrong end date in JSON ****");
				status = new Status("900009",PropertyUtil.getErrorMessage(900009,OCConstants.ERROR_EVENTS_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			if(eventInfo.getEventStatus()!=null && !eventInfo.getEventStatus().isEmpty() &&
					!eventInfo.getEventStatus().equalsIgnoreCase("All") && !eventInfo.getEventStatus().equalsIgnoreCase("Active") 
					&&!eventInfo.getEventStatus().equalsIgnoreCase("Suspended") && !eventInfo.getEventStatus().equalsIgnoreCase("Expired")) {
				status = new Status("900010",PropertyUtil.getErrorMessage(900010,OCConstants.ERROR_EVENTS_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		}
		if(sortBy != null) {
			if(sortBy.getType() != null && !sortBy.getType().isEmpty() &&
					!sortBy.getType().equalsIgnoreCase("Event") && !sortBy.getType().equalsIgnoreCase("Date") 
					&&!sortBy.getType().equalsIgnoreCase("City")&& !sortBy.getType().equalsIgnoreCase("Store")) {
				status = new Status("900011",PropertyUtil.getErrorMessage(900011,OCConstants.ERROR_EVENTS_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			if(sortBy.getOrder() != null && !sortBy.getOrder().isEmpty() &&
					!sortBy.getOrder().equalsIgnoreCase("asc") && !sortBy.getOrder().equalsIgnoreCase("desc")) {
				status = new Status("900012",PropertyUtil.getErrorMessage(900012,OCConstants.ERROR_EVENTS_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
		}
		return status;

		
	}
	private List<String> getImages(List<String> imageUrl,String currentUserName,String dirId ){
		
		String appUrl = PropertyUtil.getPropertyValue("ApplicationUrl");
		String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		String eventsImageDirectory = PropertyUtil.getPropertyValue("eventsImageDirectory");
		List<String> imageUrls = new ArrayList<String>();

		if(dirId != null) {
		String imgPathFiles = usersParentDirectory + File.separator +  currentUserName + eventsImageDirectory+dirId+File.separator;
		String imgPath = appUrl+"UserData/"+ currentUserName + eventsImageDirectory +dirId+File.separator;
		File pathFiles = new File(imgPathFiles);
		File[] imgArray = pathFiles.listFiles();
		if(imgArray!=null) {
		for(int i=0;i<imgArray.length;i++){
			if(imgArray[i].isFile()) {
				imageUrls.add(imgPath+imgArray[i].getName());
					}
				}
			}
		}
		return imageUrls;
	}
	
}

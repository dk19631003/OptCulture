package org.mq.optculture.controller.ocevent;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.data.dao.EventsDao;
import org.mq.optculture.data.dao.EventsDaoForDML;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;

public class ManageOCEventController extends GenericForwardComposer{
	
	private EventsDao eventsDao;	
	private EventsDaoForDML eventsDaoForDML;
	private Users user;
	private Rows eventsGridRowsId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Listbox pageSizeLbId;
	private Paging eventListBottomPagingId;
	private Session session ;
	private final String SEARCH_BY_STATUS = "Status";
	private final String SEARCH_BY_DATE = "Date";
	private final String SEARCH_BY_City = "City";
	private final String SEARCH_BY_EVENT_NANME = "EventName";
	private Div searchByCityDivId,searchByStatusDivId,searchByCreatedDateDivId,searchByEventNameDivId;
	private Textbox searchByCityTbId,searchByEventNameTbId;
	private MyDatebox fromDateboxId,toDateboxId;
	private Listbox statusLbId,srchLbId;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		user = GetUser.getUserObj();
		eventsDao = (EventsDao) SpringUtil.getBean(OCConstants.EVENTS_DAO);
		eventsDaoForDML = (EventsDaoForDML) SpringUtil.getBean(OCConstants.EVENTS_DAO_ForDML);
		session =Sessions.getCurrent();
		onSelect$pageSizeLbId();
		}
	
	private void displayCouponReportGridByPage(int offset,int limit) {
		
		String value = srchLbId.getSelectedItem().getValue();
		List<Events> eventsList = eventsDao.getEventInfoByPageSize(user.getUserId(), offset, limit);
		int totalSize=0;
		if(value.equals(SEARCH_BY_DATE)) {
			eventsList = eventsDao.getEventInfoByCreatedDate(user.getUserId(), offset, limit,MyCalendar.calendarToString(getStartDate(fromDateboxId),MyCalendar.FORMAT_DATETIME_STYEAR),
					MyCalendar.calendarToString(getEndDate(toDateboxId),MyCalendar.FORMAT_DATETIME_STYEAR));		
			totalSize =eventsDao.getTotalEventSizeCreatedDate(user.getUserId(),MyCalendar.calendarToString(getStartDate(fromDateboxId),MyCalendar.FORMAT_DATETIME_STYEAR),
					MyCalendar.calendarToString(getEndDate(toDateboxId),MyCalendar.FORMAT_DATETIME_STYEAR));
		}
		else if(value.equals(SEARCH_BY_STATUS)) {
			eventsList = eventsDao.getEventInfoByTextFields(user.getUserId(), offset, limit,statusLbId.getSelectedItem().getLabel(),SEARCH_BY_STATUS);
			totalSize = eventsDao.getTotalEventSizeByTextFields(user.getUserId(),statusLbId.getSelectedItem().getLabel(),SEARCH_BY_STATUS);
		}
		else if(value.equals(SEARCH_BY_City)) {
			eventsList = eventsDao.getEventInfoByTextFields(user.getUserId(), offset, limit,searchByCityTbId.getValue().trim(),SEARCH_BY_City);
			totalSize = eventsDao.getTotalEventSizeByTextFields(user.getUserId(),searchByCityTbId.getValue().trim(),SEARCH_BY_City);
		}
		else if(value.equals(SEARCH_BY_EVENT_NANME)) {
			eventsList = eventsDao.getEventInfoByTextFields(user.getUserId(), offset, limit,searchByEventNameTbId.getValue().trim(),SEARCH_BY_EVENT_NANME);
			totalSize = eventsDao.getTotalEventSizeByTextFields(user.getUserId(),searchByEventNameTbId.getValue().trim(),SEARCH_BY_EVENT_NANME);
		}
			
		eventListBottomPagingId.setTotalSize(totalSize);

		renderingEventList(eventsList);	
		
	}
	private void renderingEventList(List<Events> eventsList) {
		// TODO Auto-generated method stub
		Components.removeAllChildren(eventsGridRowsId);
		if(eventsGridRowsId == null) {
			//TODO
			logger.debug(" No Promo-codes exists ");
			return;
		}
		for (Events event : eventsList) {
			Row row = new Row();
			
			//Event Title
			row.appendChild(new Label(event.getEventTitle()));
			//Start Date time
			row.appendChild(new Label(MyCalendar.calendarToString(event.getEventStartDate(),MyCalendar.FORMAT_DATETIME_STYEAR,(TimeZone) sessionScope.get("clientTimeZone"))));
			//End Date time
			row.appendChild(new Label(MyCalendar.calendarToString(event.getEventEndDate(),MyCalendar.FORMAT_DATETIME_STYEAR,(TimeZone) sessionScope.get("clientTimeZone"))));
			//City
			row.appendChild(new Label(event.getCity()));
			//Action
			Hbox hbox = new Hbox();

			//Edit
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit Program");
			editImg.setStyle("cursor:pointer;margin-right:5px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("type", "Edit");
			editImg.setParent(hbox);

			//Delete
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setTooltiptext("Delete Program");
			delImg.setStyle("cursor:pointer;margin-right:5px;");
			delImg.addEventListener("onClick", this);
			delImg.setAttribute("type", "delete");
			delImg.setParent(hbox);

			
			String src = "";
			String toolTipTxtStr = "";
			String type = "";
			boolean isEventStatusActive = event.getEventStatus().equalsIgnoreCase(OCConstants.EVENT_STATUS_ACTIVE)||event.getEventStatus().equalsIgnoreCase(OCConstants.EVENT_STATUS_RUNNING);
			if(isEventStatusActive) {
				src = "/images/loyalty/suspend.png";
				toolTipTxtStr  = "Suspend";
				type = "Suspend";
			}
			else {
				src = "/img/play_icn.png";
				toolTipTxtStr = "Activate";
				type = "Activate";
			}

			Image statusImg = new Image(src);
			statusImg.setTooltiptext(toolTipTxtStr);
			statusImg.setStyle("cursor:pointer;margin-right:5px;");
			statusImg.addEventListener("onClick", this);
			statusImg.setAttribute("type", type);
			statusImg.setParent(hbox);
			
			row.appendChild(hbox);

			//Setting Row
			row.setAttribute("event", event);
			row.setParent(eventsGridRowsId);

		}

			

		
	}

	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if(event.getTarget() instanceof Image) {
			Image currentImage = (Image)event.getTarget();
			String programImageType = (String)currentImage.getAttribute("type");
			if(programImageType.equalsIgnoreCase("Suspend")) {
				suspend(currentImage);
			}
			else if(programImageType.equalsIgnoreCase("Activate")) {
				activate(currentImage);
			}
			else if(programImageType.equalsIgnoreCase("Edit")) {
				Events eventObj = (Events) currentImage.getParent().getParent().getAttribute("event");
				session.setAttribute(OCConstants.SESSION_EDIT_EVENT, eventObj);
				Redirect.goTo(PageListEnum.CREATE_EVENT);
			}
			else if(programImageType.equalsIgnoreCase("delete")) {
				org.zkoss.zul.Messagebox.Button confirm= Messagebox.show(" Do you want to delete the event?", "Confirm",
						new Messagebox.Button[] {Messagebox.Button.NO, Messagebox.Button.YES },
						Messagebox.INFORMATION, null, null); 
				if(confirm==null || !confirm.equals(Messagebox.Button.YES)) return;
				
				delete(currentImage);
			}
		}
		else if(event.getTarget() instanceof Paging) {
			Paging paging = (Paging) event.getTarget();
			int desiredPage = paging.getActivePage();
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			
			if(paging.getAttribute("onPaging").equals("event")) {
				this.eventListBottomPagingId.setActivePage(desiredPage);
				displayCouponReportGridByPage(ofs, (byte) pagingEvent.getPageable().getPageSize());
				
			}
		}

	}
	private void delete(Image currentImage) {
		// TODO Auto-generated method stub
		Events event = (Events) currentImage.getParent().getParent().getAttribute("event");
		deleteProgram(event);
		displayCouponReportGridByPage(0,5);
	}
	private void activate(Image currentImage) {
		// TODO Auto-generated method stub
		Events event = (Events) currentImage.getParent().getParent().getAttribute("event");
		event.setEventStatus(OCConstants.EVENT_STATUS_ACTIVE);
		savePrgmObj(event);

		currentImage.setSrc("/images/loyalty/suspend.png");;
		currentImage.setTooltiptext("Suspend");
		currentImage.setStyle("cursor:pointer;margin-right:5px;");
		currentImage.setAttribute("type", "Suspend");
		
	}
	private void suspend(Image currentImage) {
		// TODO Auto-generated method stub
		Events event = (Events) currentImage.getParent().getParent().getAttribute("event");
		event.setEventStatus(OCConstants.EVENT_STATUS_SUSPENDED);
		savePrgmObj(event);
		
		currentImage.setSrc("/img/play_icn.png");;
		currentImage.setTooltiptext("Activate");
		currentImage.setStyle("cursor:pointer;margin-right:5px;");
		currentImage.setAttribute("type", "Activate");
	}
	private void savePrgmObj(Events event) {
		try {
			eventsDaoForDML.saveOrUpdate(event);
		} catch (Exception e) {
			logger.error("logger===>"+e);
		}

	}
	private void deleteProgram(Events event) {
		try {
			eventsDaoForDML.delete(event);
		} catch (Exception e) {
			logger.error("logger===>"+e);
		}

	}
	public void onSelect$pageSizeLbId() {
		try {
			int tempCount = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			eventListBottomPagingId.setPageSize(tempCount);
			eventListBottomPagingId.setActivePage(0);
			eventListBottomPagingId.addEventListener("onPaging", this);
			eventListBottomPagingId.setAttribute("onPaging", "event");

			displayCouponReportGridByPage(eventListBottomPagingId.getActivePage()*eventListBottomPagingId.getPageSize(),tempCount);
		
		}
		catch(Exception e) {
			logger.info("e===>"+e);
		}
	}
	public void onSelect$srchLbId() {
		String value = srchLbId.getSelectedItem().getValue();
		
		if(value.equals(SEARCH_BY_DATE)) {
			searchByCityTbId.setValue(null); 
			searchByCityDivId.setVisible(false);
			searchByEventNameTbId.setValue(null); 
			searchByEventNameDivId.setVisible(false);
			searchByCreatedDateDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
			
			searchByStatusDivId.setVisible(false);
			statusLbId.setSelectedIndex(0);
			return;
		}
		else if(value.equals(SEARCH_BY_STATUS)) {
			 
			searchByCityTbId.setValue(null);
			searchByCityDivId.setVisible(false);
			searchByEventNameTbId.setValue(null); 
			searchByEventNameDivId.setVisible(false);
			searchByCreatedDateDivId.setVisible(false);
			searchByStatusDivId.setVisible(true);
			statusLbId.setSelectedIndex(0);
			return;
		}
		else if(value.equals(SEARCH_BY_City)) {
			 
			searchByCityDivId.setVisible(true);
			searchByEventNameTbId.setValue(null); 
			searchByEventNameDivId.setVisible(false);
			searchByCreatedDateDivId.setVisible(false);
			searchByStatusDivId.setVisible(false);
			return;
		}
		else if(value.equals(SEARCH_BY_EVENT_NANME)) {
			 
			searchByEventNameDivId.setVisible(true);
			searchByCityTbId.setValue(null);
			searchByCityDivId.setVisible(false);
			searchByCreatedDateDivId.setVisible(false);
			searchByStatusDivId.setVisible(false);
			return;
		}
		
		
	}//onSelect$srchLbId()

	public void onClick$resetAnchId() {
		srchLbId.setSelectedIndex(0);
		onSelect$srchLbId();
		onSelect$pageSizeLbId();
	}
	public Calendar getEndDate(MyDatebox toDateboxId) {
		Calendar serverToDateCal = toDateboxId.getServerValue();
		Calendar tempClientToCal = toDateboxId.getClientValue();
		//change the time for startDate and endDate in order to consider right from the 
		// starting time of startDate to ending time of endDate
		
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 
				23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 
				59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);		
		return serverToDateCal;
	}
	public Calendar getStartDate(MyDatebox fromDateboxId){
		try {
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(Calendar.MINUTE, 
					serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);
			return serverFromDateCal;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::::",e);
			return null;
		}
		
	}
	
	public void	onClick$getReportBtnId(){
		validateSearchFields();
		onSelect$pageSizeLbId();
	}
	
	public void onChange$fromDateboxId (){
		if(fromDateboxId.getValue() ==null || toDateboxId.getValue()== null) return;
		Calendar  start = fromDateboxId.getClientValue();
		Calendar end = toDateboxId.getClientValue();
		
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			fromDateboxId.setText(Constants.STRING_NILL);
			return;
		}
	}
	
	public void onChange$toDateboxId(){
		Calendar  start = null,end = null;
		try {
		  start = fromDateboxId.getClientValue();
		  end = toDateboxId.getClientValue();
		}catch (Exception e) {
		  end = toDateboxId.getClientValue();
		}
		if(end.before(start)) {
			MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
			toDateboxId.setText(Constants.STRING_NILL);
			return;
		}
	}
	
	public void validateSearchFields() {
		 if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_DATE)){
			logger.info("OLA Entered else");
			if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null) {
				MessageUtil.setMessage("Please specify the dates", "red");
				return;
			}
	
		}
		else if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_EVENT_NANME)){
		
			String eventName = searchByEventNameTbId.getValue().trim();
			
			if(eventName==null || eventName.isEmpty()){
				MessageUtil.setMessage("Please enter Event Name.","color:red", "TOP");
				return;
			}
		}
		else if(srchLbId.getSelectedItem().getValue().equals(SEARCH_BY_City)){
			String City = searchByCityTbId.getValue().trim();
			
			if(City==null || City.isEmpty()){
				MessageUtil.setMessage("Please enter Event Name.","color:red", "TOP");
				return;
			}	
		}

	}
}

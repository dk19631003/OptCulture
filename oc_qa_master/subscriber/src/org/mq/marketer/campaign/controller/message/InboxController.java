package org.mq.marketer.campaign.controller.message;



import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.controller.MessageHandler;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;

public class InboxController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Label msgsLblId,totalMsgsLblId;
	
	private Listbox messageslbId;
	List<Component> childCells = null;
	Listcell cell = null;
	MessageHandler	msgHandler = new MessageHandler();
	Label unreadLblId =null;
//	List result = msgHandler.getMessages("");
	private MessagesDao messagesDao;
	private Paging messagesPagingId;
	
	private TimeZone clientTimeZone; 
	
	public InboxController() {
		messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
		clientTimeZone = (TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	}
	
	private ListitemRenderer renderer =  new MyRenderer();
	private int unRead;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		msgsLblId = (Label)Executions.getCurrent().getAttribute("viewType");
		unreadLblId = (Label)Utility.getComponentById("unreadLblId");
		
		//Logger logger = Logger.getRootLogger();
//		MessageHandler	msgHandler = new MessageHandler();
//		List result = msgHandler.getMessages("");
//		List trashList = msgHandler.getMessages("trash");
//		Label unreadLblId = (Label)Utility.getComponentById("unreadLblId");
		
		/*
		 * int totalCount = mailingListDao.getCountByUser(userId);
 		logger.debug(">>>>>>>>>>>>>>>>>>"+totalCount);
 		if(totalCount ==0 ) {
 			mlistVboxId.setVisible(false);
 			warnDivId.setVisible(true);
 		} else {
 			mlistVboxId.setVisible(true);
 			warnDivId.setVisible(false);
 		}
 			
 		mlistPaging.setDetailed(true);
		mlistPaging.setTotalSize(totalCount);
		mlistPaging.addEventListener("onPaging", this);
		
		redraw(0,mlistPaging.getPageSize());
		 */
		
		int totalCount = messagesDao.findCount(GetUser.getUserId());
		
		messagesPagingId.setDetailed(true);
		messagesPagingId.setTotalSize(totalCount);
		messagesPagingId.addEventListener("onPaging", new MyRenderer());
		
		
		List<Messages> msgList = msgHandler.getMessages("",0,messagesPagingId.getPageSize());
		
		messageslbId.setItemRenderer(renderer);
		
		ListModelList tempListModel = new ListModelList(msgList);
		tempListModel.setMultiple(true);
		//smsCampListlbId.setModel(new ListModelList(getSmsCampaigns()));
		
		
		
		messageslbId.setModel(tempListModel);
		
		//List list = messageslbId.getListModel()
		//int size = list.size();
		/*int unread = msgHandler.getNewMsgsCount(org.mq.marketer.campaign.controller.GetUser.getUserObj().getUserId());
		for(int i=0;i<messageslbId.getModel().getSize();i++){
			messages =(Messages) messageslbId.getModel().getElementAt(i);
			//messages = (Messages)li.getValue();
			read = messages.isRead();
			if(!read){
				logger.info("yes it is an unread message");
				li = messageslbId.getItemAtIndex(i);
				//unread = unread +1;
				childCells = li.getChildren();
				logger.info("size of list cells"+childCells.size());
				for(Object obj:childCells){
					cell = (Listcell)obj;
					cell.setStyle("font-weight:bold;");
				}
			}
		}*/
		int unread = msgHandler.getNewMsgsCount(GetUser.getUserId());
		unreadLblId.setValue((unread) + "");
		//totalMsgsLblId.setValue("Total: " +  messageslbId.getItemCount());
		
		
	} // doAfterCompose
	
	public List getresult() {
		logger.debug("getResult method called");
		return msgHandler.getMessages("");
	}
	
	
	public void onSelect$messageslbId() {
		try {
			if(messageslbId.getSelectedCount()==1) {
				
				getMessage(messageslbId);
			}
			else {
				
				msgsLblId.setValue("");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	} // onSelect$messageslbId
	
	
	private void  getMessage(Listbox lbox) throws Exception{
		int index = lbox.getSelectedIndex();
		if(index < 0){
			return ;
		}
		Listitem li = lbox.getSelectedItem();
		Messages messages =(Messages)li.getValue();
//		msgsLblId.value = messages.getMessage();
		msgsLblId.setValue(messages.getMessage());
		if(!messages.isRead()){
			childCells = li.getChildren();
			for(Object obj:childCells){
				cell = (Listcell)obj;
				cell.setStyle("font-weight:normal;");
			}
			msgHandler.markAsRead(messages);
			int count = Integer.parseInt(unreadLblId.getValue());
			if(count > 0){
				unreadLblId.setValue( ( count - 1) + "");
			}
		}
	} // getMessage
	
	
	public void onClick$moveToTrashAnchrId() {
		try {
			moveToTrash();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}
	
	private void moveToTrash() throws Exception{
		
		Set<Listitem> selMsgs = messageslbId.getSelectedItems();
		if(selMsgs != null && selMsgs.size() ==0){
			MessageUtil.setMessage("Please select at least one message.",  "color:red;");
			return;
		}
		/*if(selMsgs.size() ==0){
			return;
		}*/
		
		for (Listitem li : selMsgs) {
			Messages messages = (Messages)li.getValue();
			msgHandler.setFolder(messages,"Trash");
			li.setVisible(false);
			
		}
		messageslbId.clearSelection();
		
		
		
		/*for(Object obj:selMsgs){
			Listitem li = (Listitem)obj;
			Messages messages = (Messages)li.getValue();
			msgHandler.setFolder(messages,"Trash");
			li.setVisible(false);
			messageslbId.clearSelection();
			//messageslbId.removeItemAt(li.getIndex());
		}*/
		//Utility.getComponentById("msgsIncId").invalidate();
		((Label)Utility.getComponentById("unreadLblId")).setValue("" + msgHandler.getNewMsgsCount(GetUser.getUserObj().getUserId()));
		msgsLblId.setValue("");
	}
	
	private  class  MyRenderer implements ListitemRenderer,EventListener{
		
		public MyRenderer() {
			
			super();
		}
		
		
		@Override
		public void render(Listitem item, Object obj, int arg2) throws Exception {
			// TODO Auto-generated method stub
			Messages message = null;
			Listcell lc = null;
			if(obj instanceof Messages) {
				message = (Messages)obj;
				
				if(!message.isRead()){
					unRead += 1;
				}
				
				
				item.setValue(message);
				
				lc = new Listcell();
				lc.setParent(item);
				
				lc = new Listcell(message.getModule());
				if(!message.isRead()) lc.setStyle("font-weight:bold;");
				lc.setParent(item);
				
				lc = new Listcell(message.getSubject());
				if(!message.isRead()) lc.setStyle("font-weight:bold;");
				lc.setParent(item);
				
				//lc = new Listcell(""+message.getCreatedDate());
				lc = new Listcell(MyCalendar.calendarToString(message.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone));
				if(!message.isRead()) lc.setStyle("font-weight:bold;");
				lc.setParent(item);
				
				
			}//if
			
		}//render
		
		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			if(event.getTarget() instanceof Paging) {
				
				Paging mlistPaging = (Paging) event.getTarget();
				int desiredPage = mlistPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				//contactsPagingId.getActivePage()*pageSize,contactsPagingId.getPageSize());
				List<Messages> msgList = msgHandler.getMessages("", desiredPage*pSize, pSize);
				
				
				ListModelList tempListModel = new ListModelList(msgList);
				tempListModel.setMultiple(true);
				
				messageslbId.setModel(tempListModel);
				messageslbId.setItemRenderer(renderer);
				
			}
			
		} // onEvent
		
	} // MyRenderer
	
} // class

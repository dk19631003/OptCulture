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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;

public class TrashController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	MessageHandler	msgHandler = new MessageHandler();
	Label msgsLblId = null;
	List childCells = null;
	Listcell cell = null;
	Listitem li = null;
	Messages messages = null;
	
	private Listbox trashmsgslbId;
	private Label totalMsgsLblId;
	boolean read = false;
	int unRead ;
	
	private ListitemRenderer renderer =  new MyRenderer();
	private Paging messagesPagingId;
	
	private TimeZone clientTimeZone;
	private MessagesDao messagesDao ;
	
	public TrashController() {
		
		messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
		clientTimeZone = (TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		
	}
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		msgsLblId = (Label)Utility.getComponentById("msgsLblId");
		
		
		/*List list = trashmsgslbId.getChildren();
		int size = list.size();
		int unread = 0;
		for(int i=1;i<size-2;i++){
			li = (Listitem)list.get(i);
			messages = (Messages)li.getValue();
			read = messages.isRead();
			if(!read){
				unread = unread +1;
				childCells = li.getChildren();
				for(Object obj:childCells){
					cell = (Listcell)obj;
					cell.setStyle("font-weight:bold;");
				}
			}
		}*/
		int totalCount = messagesDao.findNewTrashCount(GetUser.getUserId());
		
		messagesPagingId.setDetailed(true);
		messagesPagingId.setTotalSize(totalCount);
		messagesPagingId.addEventListener("onPaging", new MyRenderer());
		
		
		
		
		List<Messages> msgList = msgHandler.getMessages("trash", 0, messagesPagingId.getPageSize());
		
		trashmsgslbId.setItemRenderer(renderer);
		ListModelList tempModelList = new ListModelList(msgList);
		tempModelList.setMultiple(true);
		trashmsgslbId.setModel(tempModelList);
		//trashmsgslbId.setModel(new ListModelList(msgList));
		//totalMsgsLblId.setValue("Total: " + trashmsgslbId.getItemCount());
		
	}
	
	public List gettrashList() {
		return msgHandler.getMessages("trash");
	}
	
	
	public void onSelect$trashmsgslbId() {
		if(trashmsgslbId.getSelectedCount() > 1) {
			msgsLblId.setValue("");
		}
		else {
		getMessage(trashmsgslbId);
		}
		
	}
	
	private void getMessage(Listbox lbox){
		int index = lbox.getSelectedIndex();
		if(index < 0){
			return ;
		}
		Listitem li = lbox.getSelectedItem();
		Messages messages = (Messages)li.getValue();
		msgsLblId.setValue(messages.getMessage());
		if(!messages.isRead()){
			childCells = li.getChildren();
			for(Object obj:childCells){
				cell = (Listcell)obj;
				cell.setStyle("font-weight:normal;");
			}
			msgHandler.markAsRead(messages);
		}		
	}
	
	
	public void onClick$moveToInboxTbarBtnId() {
		try {
			moveToInbox();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}
	
	private void moveToInbox() throws Exception{
		Set <Listitem> selMsgs = trashmsgslbId.getSelectedItems();
		if(selMsgs != null && selMsgs.size() ==0){
			MessageUtil.setMessage("Please select at least one message.",  "color:red;");
			return;
		}
		for(Object obj:selMsgs){
			Listitem li = (Listitem)obj;
			Messages messages = (Messages)li.getValue();
			msgHandler.setFolder(messages,"Inbox");
			li.setVisible(false);
		}
		trashmsgslbId.clearSelection();
		
		((Label)Utility.getComponentById("unreadLblId")).setValue("" + msgHandler.getNewMsgsCount(GetUser.getUserObj().getUserId()));
		//Utility.getComponentById("msgsIncId").invalidate();
		msgsLblId.setValue("");
	}
	
	public void onClick$deleteBtnId() {
		try {
			delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);;
		}
	}
	
	private void delete() throws Exception{
		try{
			Set selMsgs = trashmsgslbId.getSelectedItems();
			if(selMsgs.size()==0){
				int count = trashmsgslbId.getItemCount();
				if(count == 0)
					Messagebox.show("There are no messages to be deleted.","Prompt",Messagebox.OK,Messagebox.INFORMATION);
				else
					Messagebox.show("Select the messages to delete.","Prompt",Messagebox.OK,Messagebox.INFORMATION);
				
				return;
			}
			try {
				int confirm = Messagebox.show("Are you sure you want to delete the selected messages?", "Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
				if(confirm == 1){
					for(Object obj:selMsgs){
						Listitem li = (Listitem)obj;
						Messages messages = (Messages)li.getValue();
						msgHandler.deleteMessage(messages);
					}
					msgsLblId.setValue("");
				Utility.getComponentById("msgsIncId").invalidate();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);;
			}
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}
	}
	
	
	public class MyRenderer implements ListitemRenderer,EventListener{
		
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
				
				lc = new Listcell("");
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
				List<Messages> msgList = msgHandler.getMessages("trash", desiredPage*pSize, pSize);
				
				ListModelList tempListModel = new ListModelList(msgList);
				tempListModel.setMultiple(true);
				
				trashmsgslbId.setModel(tempListModel);
				trashmsgslbId.setItemRenderer(renderer);
				
			}
			
		}
		
		
		
	}
	
	
	
}

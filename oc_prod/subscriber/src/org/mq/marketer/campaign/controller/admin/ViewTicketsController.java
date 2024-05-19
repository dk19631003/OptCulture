package org.mq.marketer.campaign.controller.admin;

import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SupportTicket;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.SupportTicketDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.event.PagingEvent;

 public class ViewTicketsController extends GenericForwardComposer implements EventListener {
	 
	 

	 
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER); 
	 private static String allStr = "--All--";
	 
	 private UsersDao usersDao;
	 SupportTicketDao supportTicketDao;
	 
	 private Listbox ticketsPerPageLBId,userOrgLbId ,userNameLbId;
	 private Grid ticketsGId;
	 private Paging ticketsPagingId;
	 private Rows ticketRowsId;

	 
	 
	 public ViewTicketsController(){
		 usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		 supportTicketDao =(SupportTicketDao)SpringUtil.getBean("supportTicketDao");
		 String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
	     PageUtil.setHeader("View Tickets","",style,true);
	 }
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		defualtSettings();
	}
	
	
	
	public void defualtSettings(){
		
		
		
		try {
			setUserOrg();
			
			int totalSize = supportTicketDao.getTotalCountOfAllTickets(); 
			
			ticketsPagingId.setTotalSize(totalSize);
			ticketsPagingId.setActivePage(0);
			ticketsPagingId.addEventListener("onPaging", this);
			
			redraw(0, ticketsPagingId.getPageSize());
			
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception" , e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception" , e1);
		}
		
	}
	
	
	private void setUserOrg() {

		
		try {
			List<UserOrganization> orgList	= usersDao.findAllOrganizations();
			
			if(orgList == null) {
				logger.debug("no organization list exist from the DB...");
				return ;
			}
			Components.removeAllChildren(userOrgLbId);
			
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userOrgLbId);
			
			Listitem tempItem = null;
			
			for (UserOrganization userOrganization : orgList) {
				
				//set Organization Name
				if(userOrganization.getOrganizationName() == null || userOrganization.getOrganizationName().trim().equals("")) continue;
				
				tempItem = new Listitem(userOrganization.getOrgExternalId().trim(),userOrganization.getUserOrgId());
				tempItem.setParent(userOrgLbId);
			} // for
			userOrgLbId.setSelectedIndex(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception" , e);
		}
		
		
	} 
	
	public void onSelect$userOrgLbId() {
		try {
			Components.removeAllChildren(userNameLbId);

			logger.info("---------- entered onSelect$userOrgLbId");
			
			if(userOrgLbId.getSelectedItem().getLabel().equals(allStr)) {
				Listitem tempList = new Listitem(allStr);
				tempList.setParent(userNameLbId);
				userNameLbId.setSelectedIndex(0);
				int totalSize = supportTicketDao.getTotalCountOfAllTickets();
				
				ticketsPagingId.setTotalSize(totalSize);
				ticketsPagingId.setActivePage(0);
				redraw(0, ticketsPagingId.getPageSize());
				return;
			}
			List<Users> usersList = usersDao.getUsersByOrgIdAndUserId((Long)userOrgLbId.getSelectedItem().getValue(), null); //getPrimaryUsersByOrg((Long)userOrgLbId.getSelectedItem().getValue());
			
			if(usersList == null || usersList.size() == 0) {
				logger.debug("No users exists for the Selected Organization..");
				Listitem tempList = new Listitem(allStr);
				tempList.setParent(userNameLbId);
				int totalSize = 0; 
				
				ticketsPagingId.setTotalSize(totalSize);
				ticketsPagingId.setActivePage(0);
				
				redraw(0, totalSize);
				return;
			}
			
			Listitem tempList = new Listitem(allStr);
			tempList.setParent(userNameLbId);
			
			
			
			Listitem tempItem = null;
			for (Users users : usersList) {
				String userNameStr = Utility.getOnlyUserName(users.getUserName());
//			logger.debug("UserName is ::"+userNameStr);
				
				tempItem = new Listitem(userNameStr,users);
				tempItem.setParent(userNameLbId);
				
			} // for
			
			if(userNameLbId.getItemCount() > 0) {
				logger.debug("usersListBxId count is .."+userNameLbId.getItemCount());
				userNameLbId.setSelectedIndex(0);
			}
			
			logger.info("org label is"+userOrgLbId.getSelectedItem().getLabel());
			
			int totalSize = supportTicketDao.getTotalCountOfTicketsByOrgIdAndUserId(userOrgLbId.getSelectedItem().getLabel(), null); 
			logger.debug("total count is"+totalSize);
			
			ticketsPagingId.setTotalSize(totalSize);
			ticketsPagingId.setActivePage(0);
			redraw(0, ticketsPagingId.getPageSize());
			//ticketsGId.setModel(getTicketsModel(0, ticketsPagingId.getPageSize()));
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception" , e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("Exception" , e1);
		}

	}
	
	 public void redraw(int start , int end){
		 
		 TimeZone tz =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		 List<SupportTicket> supportList = getAllTickets(start, end);
		 
		 Components.removeAllChildren(ticketRowsId);
			
			if(supportList == null || supportList.size() == 0) return;
			
			for (SupportTicket supportTicket : supportList) {
				
				Row row = new Row();
				
				row.setParent(ticketRowsId);
				
				A emailAnch = new A(""+supportTicket.getTicketId());
				
				emailAnch.setAttribute("TICKET_VIEW", supportTicket);
				emailAnch.setStyle("cursor:pointer;color:blue;");
				emailAnch.addEventListener("onClick", this);
				emailAnch.setParent(row);
				
				
				//row.setStyle("height:5px; line-height: 7px; padding: 0 0 0 12px;");
				
				
				String typeStr=Constants.STRING_NILL;
				
				if(supportTicket.getType() == 1){
					typeStr = Constants.SUPPORT_TYPE_BUG;
				}else if(supportTicket.getType() == 2){
					typeStr = Constants.SUPPORT_TYPE_FEATURE;
				}else if(supportTicket.getType() == 3){
					typeStr = Constants.SUPPORT_TYPE_SERVICE;
				}else if(supportTicket.getType() == 4){
					typeStr = Constants.SUPPORT_TYPE_TECH;
				}
				
				
				
				row.appendChild(new Label(typeStr));
				row.appendChild(new Label(supportTicket.getUserOrgName()));
				row.appendChild(new Label(supportTicket.getUserName()));
				row.appendChild(new Label(supportTicket.getClientName()));
				row.appendChild(new Label(MyCalendar.calendarToString(supportTicket.getCreatedDate(),
						MyCalendar.FORMAT_DATETIME_STDATE, tz)));
				
				
				
				row.setValue(supportTicket);
				
				
				
				
				
				
				
			}
		 
		 
		 
	 }
	
	
	/**
	 * this method fetches the 'SupportTicket' objects from DB
	 */
	
	public List<SupportTicket> getAllTickets(int start, int end) {
		
		
		
		try {
			logger.debug("in getAllTcikets()");
			

			
			
			List<SupportTicket> supportTicketList=null;
			if(supportTicketDao == null) {
				supportTicketDao = (SupportTicketDao)SpringUtil.getBean("supportTicketDao");
			}// if
			try {
				
				
				String selUserOrg = userOrgLbId.getSelectedItem().getLabel();
				Long selUserOrgId = null;
				if(! selUserOrg.equals(allStr) ) {
					selUserOrgId = userOrgLbId.getSelectedItem().getValue();
					
				}
				
				
				String selUserName = userNameLbId.getSelectedItem().getLabel();
			
				Long selUserId = null;
				if( !selUserName.equals(allStr) ) {
					selUserId = userNameLbId.getSelectedItem().getValue() == null ? null : ((Users) userNameLbId.getSelectedItem().getValue()).getUserId();
				}
				if(selUserOrgId == null) {

					supportTicketList=supportTicketDao.findBynumberOfTickets( start, end);
					
				} // if
				else {
					
			//	Users user=(Users)userListLbId.getSelectedItem().getValue();
					//supportTicketList=supportTicketDao.findByUserAndOrg(selUserOrgId, selUserId, 0, numberOfTickets);
					supportTicketList=supportTicketDao.findByUserAndOrg(selUserOrg, start, end,selUserName);
					
				}//else
			
			} catch (Exception e) {
				logger.error("** Exception occured while fetching the campaign reports :", e);
			}//catch
			
			return supportTicketList;
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception" , e);
			return null;
		}
	}//getCampReports()
	
	public void onClick$resetSearchCriteriaAnchId(){
		userNameLbId.setSelectedIndex(0);
		defualtSettings();
		
		
	}

		
		
		public void onSelect$userNameLbId() {
			try {
				int totalSize = 0;
				if((userNameLbId.getSelectedItem().getLabel()).equals(allStr)) {
					totalSize = supportTicketDao.getTotalCountOfTicketsByOrgIdAndUserId(userOrgLbId.getSelectedItem().getLabel(), null);
				}
				else {
					totalSize = supportTicketDao.getTotalCountOfTicketsByOrgIdAndUserId(userOrgLbId.getSelectedItem().getLabel(),  userNameLbId.getSelectedItem().getLabel()); 
				}
				
				ticketsPagingId.setTotalSize(totalSize);
				ticketsPagingId.setActivePage(0);
				redraw(0, ticketsPagingId.getPageSize());
				
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				logger.error("Exception" , e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception" , e);
			}
		}
		
		
		public void onSelect$ticketsPerPageLBId() {

		try {
			int count = Integer.parseInt(ticketsPerPageLBId.getSelectedItem().getLabel());
			
		
			ticketsPagingId.setPageSize(count);
			redraw(0, count);
			
			//System.gc();
			
		} catch (Exception e) {
			logger.error("Exception" , e);
		}
	}
		
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			super.onEvent(event);
			if(event.getTarget() instanceof Paging) {
				
				
				Paging paging = (Paging)event.getTarget();
				
				int desiredPage = paging.getActivePage();
				
				
				
				
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				
				redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
				
				
			
			} else if(event.getTarget() instanceof A){
				
				A tb = (A)event.getTarget();
				logger.info(" >>> tb.getAttribute of TICKET_VIEW is  ::"+tb.getAttribute("TICKET_VIEW"));
				if(tb.getAttribute("TICKET_VIEW") != null) {
					
					SupportTicket supportTicket = (SupportTicket)tb.getAttribute("TICKET_VIEW");
					if(supportTicket != null) {
						logger.info(">>> Ticket Id is"+supportTicket.getTicketId());
						Sessions.getCurrent().setAttribute("TICKET_VIEW", supportTicket);
						Redirect.goTo(PageListEnum.SUPPORT_VIEW);
					}
					
				}
				
				
				
				
			}
		}
		

		/*@Override
		public void onEvent(Event evt) throws Exception {
//			logger.info("dhsghg");
//			super.onEvent(evt);
			Object obj = evt.getTarget();
			if(obj instanceof A){
				A tb = (A)obj;
				logger.info(" >>> tb.getAttribute of TICKET_VIEW is  ::"+tb.getAttribute("TICKET_VIEW"));
				if(tb.getAttribute("TICKET_VIEW") != null) {
					
					SupportTicket supportTicket = (SupportTicket)tb.getAttribute("TICKET_VIEW");
					if(supportTicket != null) {
						logger.info(">>> contact Id is"+supportTicket.getTicketId());
						desktopScope.setAttribute("TICKET_VIEW", supportTicket);
						Redirect.goTo(PageListEnum.SUPPORT);
					}
					
				}
			}
			
		}//onEvent
*/		
	
		
		
 }

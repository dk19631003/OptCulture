package org.mq.marketer.campaign.controller.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;

public class LatestSalesDetailsController extends GenericForwardComposer{

	private static final long serialVersionUID = 1L;
	Textbox searchBox;
	Grid customersGridId;
	private List<Users> usersList;
	
	private List<Map<String, Object>> latestSalesDataList=null;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug("-- Just Entered -- ");
		customersGridId.setRowRenderer(rowRender);
		customersGridId.setModel(getUsersModel());
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
						"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Latest Sales data details", "", style, true);
	}

	public void onChanging$searchBox(InputEvent event) {
		String key = event.getValue();
		LinkedList<Users> item = new LinkedList<Users>();
		
		if(usersList == null && usersList.size() < 1) return;
		
		logger.debug("Total Users Count :" + usersList.size());
		Users user;
		if (key.trim().length() != 0) {
			
			for (int i = 0; i < usersList.size(); i++) {
				
				user = (Users)usersList.get(i);
				if(user.getUserOrganization()==null) continue;
				
				if (user.getUserOrganization().getOrgExternalId().toLowerCase().indexOf(key.toLowerCase()) != -1 && 
						user.getUserOrganization().getOrgExternalId().toLowerCase().indexOf(key.toLowerCase()) == 0) {
					item.add(user);
				}
			} // for
			
			customersGridId.setModel(new ListModelList(item));
		} 
		else customersGridId.setModel(new ListModelList(usersList));
	}
	
	public ListModel getUsersModel() {
		return new ListModelList(getCustomers());
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> getCustomers() {
		UsersDao usersDao = (UsersDao)SpringUtil.getBean("usersDao");

		usersList = usersDao.getPOSListUsers();
		latestSalesDataList = usersDao.getSalesLatestDetails();
		
		ListIterator<Users> lit = usersList.listIterator();
		Users tempUser;
		while(lit.hasNext()) {
			tempUser = lit.next();
			if(getMapByUserId(tempUser.getUserId())==null) {
				lit.remove();
			}
		} // while
		
		logger.info("usersList size is :"+usersList.size());
		return usersList;
	}
	
	public RowRenderer getRowRenderer() {
		return rowRender;
	}
	
	private Map<String, Object> getMapByUserId(Long userId) {
		Map<String, Object> temp=null;
		
//		logger.info("UserId="+userId);
		Iterator<Map<String, Object>> it = latestSalesDataList.iterator();
		
		while(it.hasNext()) {
			temp = it.next();
			
			if(temp.containsKey("user_id") && ((Long)temp.get("user_id")).longValue()==userId.longValue()) {
				return temp;
			}
		} // while
		return null;
	}
	
	private final RowRenderer rowRender  = new MyRenderer();
	
	private class MyRenderer implements RowRenderer {
		
		MyRenderer() {
			super();
			logger.debug("new MyRenderer object is created");
		}
		
		
		
		
		
		public void render(Row row, java.lang.Object data,int arg2) {
			try {
				
				if(data instanceof Users) {
					Users user = (Users) data;
					
					
					if(user.getUserOrganization()!=null) {
						row.appendChild(new Label(user.getUserOrganization().getOrganizationName()));
						row.appendChild(new Label(user.getUserOrganization().getOrgExternalId()));
					}
					else {
						row.appendChild(new Label("No Org"));
						row.appendChild(new Label("No Org"));
					}
					row.appendChild(new Label( Utility.getOnlyUserName(user.getUserName()) ));

					
					Map<String, Object> temp = getMapByUserId(user.getUserId());
					logger.info("TEMP=="+temp);
					
					if(temp!=null) {
						
						Calendar lastFetchCal = Calendar.getInstance();
						lastFetchCal.setTime((Date)temp.get("last_fetched_time"));
						new Label(MyCalendar.calendarToString(lastFetchCal, MyCalendar.FORMAT_DATETIME_STDATE)).setParent(row);

						Calendar earliestCal = Calendar.getInstance();
						earliestCal.setTime((Date)temp.get("earliest"));
						new Label(MyCalendar.calendarToString(earliestCal, MyCalendar.FORMAT_DATETIME_STDATE)).setParent(row);

						Calendar latestCal = Calendar.getInstance();
						latestCal.setTime((Date)temp.get("latest"));
						new Label(MyCalendar.calendarToString(latestCal, MyCalendar.FORMAT_DATETIME_STDATE)).setParent(row);
						
						new Label(""+temp.get("rcptcount").toString()).setParent(row);

					}
					
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception while rendering the records ",e);
				
			}
		}
		
	}
	

}

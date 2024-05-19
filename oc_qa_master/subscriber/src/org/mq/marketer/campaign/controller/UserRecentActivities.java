package org.mq.marketer.campaign.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UserActivitiesDao;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleListModel;

@SuppressWarnings({"serial","unchecked"})
public class UserRecentActivities extends GenericForwardComposer {
	
	Checkbox chkboxId;
	Button delButtonId;
	Grid recentActGrdId;
	A moreActivityLink;

	
	private Rows userActGrdRowsId;
	
	Users user;
	UserActivitiesDao userActivitiesDao;
	UserActivitiesDaoForDML userActivitiesDaoForDML;
	UsersDao usersDao;
	UsersDaoForDML usersDaoForDML;
	List<UserActivities> activityList;
	int gridSize;
	int totalActivityCount;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public UserRecentActivities() {
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);
		
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
						"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Recent Activities", "", style, true);
		userActivitiesDao = (UserActivitiesDao)SpringUtil.getBean("userActivitiesDao");
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		user = GetUser.getUserObj();
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		usersDaoForDML = (UsersDaoForDML)SpringUtil.getBean("usersDaoForDML");
		totalActivityCount = userActivitiesDao.getActivitiesCount(user.getUserId());
		
		if(totalActivityCount > 0) {
			activityList = userActivitiesDao.findAllByUserId(user.getUserId(),0,10);
			ListModel model = new SimpleListModel(activityList);
			recentActGrdId.setRowRenderer(new GridRowRenderer());
			recentActGrdId.setModel(model);
			gridSize = activityList.size();
			
			if(totalActivityCount > gridSize) {
				moreActivityLink.getParent().setVisible(true);
			}
		} 
		else {
			
		}
		
		onLoad();
	}
	
	class GridRowRenderer implements RowRenderer {
		
		public void render(Row row, Object data,int arg2) {
			
			UserActivities userActivities = (UserActivities) data;
			row.setAttribute("activityId", userActivities.getId());
			row.setValue(userActivities);
			
			(new Checkbox()).setParent(row);
			(new Label(userActivities.getModule())).setParent(row);
			(new Label(userActivities.getActivity())).setParent(row);
			(new Label(MyCalendar.calendarToString(userActivities.getDate(),
					null))).setParent(row);
		}
	}
	
	
	public void onClick$delButtonId() {
		
		 try {
			 
			MessageUtil.clearMessage();
			
			Rows rows = recentActGrdId.getRows();
			List<Component> rowList = rows.getChildren(); 
			StringBuffer delIdsSB=new StringBuffer();
			
			for (Component eachComp : rowList) {
				Row row = (Row)eachComp;
				
				List rowChildList = row.getChildren();
				try {
					
					if(((Checkbox)rowChildList.get(0)).isChecked() == true) {
						
						delIdsSB.append(delIdsSB.length() == 0 ?
								row.getAttribute("activityId") : ","+
								row.getAttribute("activityId"));   
						
						row.setVisible(false);
						activityList.remove(row.getValue());
						gridSize--;
						totalActivityCount--;
						
					} 
				} 
				catch (Exception e) {
					logger.debug("** Exception : Error occured while deleting : "+e);
				}
			} // foreach
			
			if(delIdsSB.length() == 0) {
				MessageUtil.setMessage("No activity entry selected to delete.", "red", "top");
				logger.debug("No entry selected, Sb size is : "+delIdsSB.length());
				return;
			}
			
			//if click on yes button.
			if (Messagebox.show("Are you sure you want to delete " +
					"the selected activities?", "Delete?", Messagebox.YES | 
					Messagebox.NO, Messagebox.QUESTION)  ==  Messagebox.YES) {
				
				
				//userActivitiesDao.deleteByIds(delIdsSB.toString());
				userActivitiesDaoForDML.deleteByIds(delIdsSB.toString());
				MessageUtil.setMessage("Selected entries removed successfully.", "green", "top");
				logger.info("Entries removed successfully.");
				
			}		
		} catch (Exception e) {
			logger.debug("** Exception : Error occured while deleting : "+e);
		}
	}
	
	public void onClick$moreActivityLink() {
		List<UserActivities> list = userActivitiesDao.findAllByUserId(user.getUserId(),gridSize,10);
		activityList.addAll(list);
		ListModel model = new SimpleListModel(activityList);
		recentActGrdId.setModel(model);
		recentActGrdId.setRowRenderer(new GridRowRenderer());
		gridSize = activityList.size();
		if(totalActivityCount <= gridSize) {
			moreActivityLink.getParent().setVisible(false);
		}
	}
	
	private BigInteger userSettings; 
	private Map<String, Rows> rowsMap = new HashMap<String, Rows>();
	private List<Checkbox> cbList;
	
	private void onLoad() {
		Map<String,Checkbox> parentChkboxMap = new HashMap<String, Checkbox>();
		cbList = new ArrayList<Checkbox>();
		
		if (logger.isDebugEnabled()) {
			logger.debug("------------- just entered----------");
		}
		
		userSettings = user.getActivityAsBigInteger();
		
		logger.debug("User Settings :"+userSettings);
		
		for (ActivityEnum  activity : ActivityEnum.values()) {

			Row row = new Row();
			row.setValue(activity);
			
			if(activity.getCode() < 0) {
				
				//logger.info("desc: "+activity.getOperation()+" Code :" +activity.getCode());
				Detail detail = new Detail();
				detail.setParent(row);
				
				
				Grid grid = new Grid();
				grid.setParent(detail);
				
				Columns cols = new Columns();
				cols.setParent(grid);
				
				Column column = new Column((activity.getCode() == -1) ? "Activity" : "");
				column.setParent(cols);
				column.setWidth((activity.getCode() == -1) ? "30%" : "5%");
				
				column = new Column((activity.getCode() == -1) ? "Logging Message" : "Module");
				column.setParent(cols);
				
				Rows tempRows = new Rows();
				tempRows.setParent(grid);
				
				rowsMap.put(activity.name(), tempRows);
				
			}// if code is 0 = root row
			
			Checkbox chkbox = null;
			if(activity.getIsVisible()==true) {
				chkbox = new Checkbox();
				chkbox.setLabel(" " + activity.getOperation());
			}
			
			if(activity.getCode() < 0 ) {
				//chkbox.setChecked( (userSettings==null || userSettings.testBit(activity.getCode())));
				if(chkbox!=null) {
					chkbox.setWidgetListener("onCheck", "selAllSettingsGrid(this)");
				}
				//parentChkboxMap.put(activity.name(), chkbox);
				
			}
			
			if(activity.getCode() >= 0 ) {
				//logger.info("for code greater than 00000000000 desc: "+activity.getOperation()+" Code :" +activity.getCode()+" is going to be checked or not???????"+(userSettings==null || userSettings.testBit(activity.getCode())));
				if(chkbox!=null) {
					chkbox.setChecked( (userSettings==null || userSettings.testBit(activity.getCode())));
				}
				/*if(chkbox.isChecked()) {
					
					setParentChecked(activity, parentChkboxMap);
					if(activity.getParent() != null && parentChkboxMap.containsKey(activity.getParent().name())) {
						
						((Checkbox)parentChkboxMap.get(activity.getParent().name())).setChecked(true);
					}
					
				}//if
*/			}//if
			
			if(chkbox!=null) {
				 chkbox.setAttribute("code", (Integer)activity.getCode());
				 cbList.add(chkbox);
				 chkbox.setParent(row);
			}
			
			if(activity.getCode() >= 0 && activity.getIsVisible()==true) {
				Label column = new Label(activity.getDesc());
				column.setParent(row);
			}
			
			if(activity.getParent() == null && activity.getIsVisible()==true) {
				row.setParent(userActGrdRowsId);
			}
			else {
				if(activity.getIsVisible()==true) {
					Rows rows = rowsMap.get(activity.getParent().name());
					if(rows != null) {
						row.setParent(rows);
						
						/*if( ((Checkbox)row.getChildren().get(0) ).isChecked() ) {
							
							((Checkbox)(row.getParent().getParent().getParent().getParent().getParent()).getChildren().get(0)).setChecked(true);
							
						}//if
	*/				}//if
					else {
						logger.warn(">>>>>>>>>> Parent not found for :"+activity.name());
					}
				}
			}// else
			
		} // for activityEnum
				
	}
	
	/*public void setParentChecked(ActivityEnum activity, Map<String, Checkbox> parentChkboxMap ) {
		ActivityEnum parent = activity.getParent();
		
		while(parent != null) {
			
			if(parentChkboxMap.containsKey(parent.name()) ){
				
				((Checkbox)parentChkboxMap.get(parent.name())).setChecked(true);
				
			}
			parent = parent.getParent();
			
			
			
		}//while
		
		
		
	}*/
	
	/**
	 *  Invoked for on click functionality on save button .
	 *  It will save the settings as per user preference.
	 */
	public void onClick$saveBtnId() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("------------- just entered----------");
			}
			
			BigInteger userCode = new BigInteger("0");
			int tempCode=0;

			for (Checkbox chkbox : cbList) {
				
				if(!chkbox.isChecked()) continue;
				
				try {
					tempCode = (Integer)chkbox.getAttribute("code");
					if(tempCode < 0) continue;
					
					userCode = userCode.setBit(tempCode);
				} catch (Exception e) {
					logger.warn("Error while parsing activity code", e);
				}
			} // for chkbox
			
			logger.debug("User Code Sum Value is : "+userCode);

			user.setUserActivitySettings(userCode.toString(16));
			//usersDao.saveOrUpdate(user);
			usersDaoForDML.saveOrUpdate(user);
			MessageUtil.setMessage("Your user activity settings have been saved successfully.", "green", "top");
			
		} catch (Exception e) {
			logger.debug("** Exception while saving user activity settings : "+e);
		}
	}	
	
}

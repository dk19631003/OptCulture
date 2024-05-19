package org.mq.marketer.campaign.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UserActivities;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.general.Constants;

@SuppressWarnings("unchecked")
public class UserActivitiesDaoForDML extends AbstractSpringDaoForDML {

	private List<UserActivities> userActivitiesList;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public UserActivitiesDaoForDML() {
		userActivitiesList = new ArrayList<UserActivities>();
	}
    	
    /*public UserActivities find(Long id) {
        return (UserActivities) super.find(UserActivities.class, id);
    }*/

    public void saveOrUpdate(UserActivities userActivities) {
        super.saveOrUpdate(userActivities);
    }

    public void delete(UserActivities userActivities) {
        super.delete(userActivities);
    }

/*	public List findAll() {
        return super.findAll(UserActivities.class);
    }*/
    
    /*public synchronized  void addToActivityList(String message, String module, long userId ) {
    	
		try {
			userActivitiesList.add(new UserActivities(message, module, Calendar.getInstance(), userId));
			
			if(userActivitiesList.size() >= 10) {
				saveByCollection(userActivitiesList);
				userActivitiesList.clear();
				logger.info("Saved User Activities Queue");
			}
		} catch (Exception e) {
		}
	}*/
    
	public void addToActivityList(ActivityEnum uai,Users user, String ... params) {
		try {
//			logger.debug(">>>>>..........." + user.getUserActivitySettings());
			BigInteger userSettings = user.getActivityAsBigInteger();
			
//			logger.debug("  uiCode="+uai.getCode()+" "+ Long.toBinaryString(uai.getCode()) +" : "+uai.getDesc());
			
			if(userSettings != null && !userSettings.testBit(uai.getCode())) {
				logger.debug("Module-operation for event Log is not selected. Returning ...");
				return;
			}
			
			String tempMsg = uai.getDesc();
			if(params != null) {
//				logger.debug("desc length :"+params.length);
				
				for (int i = 0; i < params.length; i++) {
					tempMsg = tempMsg.replace("["+(i+1)+"]", params[i]);
				}
			}
			
			ActivityEnum parent = uai.getParent();
			int i = 0;
			//This loop checks for the root parent, exits if it gets parent or it loops for 10times 
			for ( ; i < 10 && parent.getParent() != null; i++) {
				parent = parent.getParent();
			}
			
			if(i == 10) { 
				logger.debug("Root Parent value is 10");
			}	
			
			addToActivityList(new UserActivities(tempMsg, parent.getOperation(), Calendar.getInstance(), user.getUserId()));
			
		} catch (Exception e) {
			logger.error("** Exception : Error occured while adding user activity object. ** ",e);
		}
	}

	/*
    public void addToActivityList(String message, String module, long userId ) {
    	
		try {
			addToActivityList(new UserActivities(message, module, Calendar.getInstance(), userId));
		} catch (Exception e) {
		}
	}*/
    
    public synchronized  void addToActivityList(UserActivities userActivities) {
    	
		try {
			userActivitiesList.add(userActivities);
			/*logger.debug("User Activity object is added for [" + userActivities.getActivity() +"]");
			logger.debug("Queue Size :" + userActivitiesList.size());*/
			
			if(userActivitiesList.size() >= 5) {
				saveByCollection(userActivitiesList);
				userActivitiesList.clear();
//				logger.debug("Saved User Activities Queue");
			}
		} catch (Exception e) {
			logger.error("** Exception : Error occured while adding the evert log.**"+e);
		}
	}
    
 public synchronized  void addToActivityList(ActivityEnum uai,Users user,boolean isOCAdmin,String ... params) {
	 try {
			/*BigInteger userSettings = user.getActivityAsBigInteger();
			if(userSettings != null && !userSettings.testBit(uai.getCode())) {
				logger.debug("Module-operation for event Log is not selected. Returning ...");
				return;
			}*/
			
			String tempMsg = uai.getDesc();
			if(params != null) {
				for (int i = 0; i < params.length; i++) {
					tempMsg = tempMsg.replace("["+(i+1)+"]", params[i]);
				}
			}
			ActivityEnum parent = uai.getParent();
			int i = 0;
			for ( ; i < 10 && parent.getParent() != null; i++) {
				parent = parent.getParent();
			}
			if(i == 10) { 
				logger.debug("Root Parent value is 10");
			}
			UserActivities userActivity = new UserActivities(tempMsg, parent.getOperation(), Calendar.getInstance(), user.getUserId());
			saveOrUpdate(userActivity);
			
		} catch (Exception e) {
			logger.error("** Exception : Error occured while adding user activity object. ** ",e);
		}
	}
    
  /*  public List<UserActivities> findAllByUserId(Long userId, int startFrom, int count) {
    		String query = "FROM UserActivities WHERE userId="+ userId +" ORDER BY DATE DESC "; 
    		return executeQuery(query, startFrom, count);
    }
    
    public int getActivitiesCount(Long userId) {
    	String queryStr = "select count(id) from UserActivities where userId="+ userId;
    	return ((Long)(getHibernateTemplate().find(queryStr)).get(0)).intValue();
    }*/
    
    public void deleteByIds(String delIds) {
    	getHibernateTemplate().bulkUpdate("delete from UserActivities where id in ("+ delIds +")");
    }
 /*   public Calendar findLastUserActivityDate(Long userId) {
	   	
    	try {
    		
			String qry = " FROM UserActivities WHERE userId ="+userId.longValue()+"  ORDER BY date DESC";
			//logger.debug("======================="+qry+"========================");
			List<UserActivities> userActivityList = executeQuery(qry, 0, 1);
			if(userActivityList.size() == 1) {
				
				return userActivityList.get(0).getDate();
				
			}
			
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
    }*/
    
    
    
  }


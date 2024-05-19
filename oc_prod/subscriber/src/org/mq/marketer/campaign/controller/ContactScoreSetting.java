package org.mq.marketer.campaign.controller;



import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ContactScores;
import org.mq.marketer.campaign.beans.UserScoreSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactScoresDao;
import org.mq.marketer.campaign.dao.ContactScoresDaoForDML;
import org.mq.marketer.campaign.dao.UserScoreSettingsDao;
import org.mq.marketer.campaign.dao.UserScoreSettingsDaoForDML;
import org.mq.marketer.campaign.general.Constants;


public class ContactScoreSetting extends Thread {

	private Queue<Object[]> scoresQueue =  new LinkedList<Object[]>();
	private Users users;
	private static volatile boolean isRunning = false;
	//private static HashMap<String, Object> scoreMap=new HashMap<String, Object>();
	private UserScoreSettingsDao userScoreSettingsDao;
	private UserScoreSettingsDaoForDML userScoreSettingsDaoForDML;
	public UserScoreSettingsDaoForDML getUserScoreSettingsDaoForDML() {
		return userScoreSettingsDaoForDML;
	}


	public void setUserScoreSettingsDaoForDML(
			UserScoreSettingsDaoForDML userScoreSettingsDaoForDML) {
		this.userScoreSettingsDaoForDML = userScoreSettingsDaoForDML;
	}

	private ContactScoresDao contactScoresDao;
	private ContactScoresDaoForDML contactScoresDaoForDML;
	public ContactScoresDaoForDML getContactScoresDaoForDML() {
		return contactScoresDaoForDML;
	}


	public void setContactScoresDaoForDML(
			ContactScoresDaoForDML contactScoresDaoForDML) {
		this.contactScoresDaoForDML = contactScoresDaoForDML;
	}

	private ContactScores  contactScores;
	Integer maxScore=null;
	Integer score=0;
	int index=0;
//	private static final Logger logger = LogManager.getLogger(ContactScoreSetting.class);
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private static HashMap<String, Object[]> scoreCndMap=new HashMap<String, Object[]>();
	//*****  source of Visit Conditions ****
	private static String SRCVISISTCNT="contains_";
	private static String SRCVISISTDOESNTCNT="doesntContains_";
	private static String SRCVISISTALLTHESEWRDS="allTeseWords_";
	private static String SRCVISISTANYOFTHESEWORDS="anyOfTheseWords_";
	
	//** Page Visit Conditions *****
	private static String PVISISTCNT="contains_";
	private static String PVISISREGEX="regEx_";
	private static String PVISISTALLVISIT="allVisit_";
	
	public  ContactScoreSetting(){
	}
	
	
	public ContactScores getContactScores() {
		return contactScores;
	}
	public void setContactScores(ContactScores contactScores) {
		this.contactScores = contactScores;
	}
	
    public void setUserScoreSettingsDao(UserScoreSettingsDao userScoreSettingsDao) {
        this.userScoreSettingsDao = userScoreSettingsDao;
    } 
    public UserScoreSettingsDao getUserScoreSettingsDao() {
        return this.userScoreSettingsDao;
    }
	
    public void setContactScoresDao(ContactScoresDao contactScoresDao) {
        this.contactScoresDao = contactScoresDao;
    } 
    public ContactScoresDao getContactScoresDao() {
        return this.contactScoresDao;
    }
	
    
   
	/**
	 * Create an Object by using these param, and added in to the queue and if Thread is not runnning,start the Thread
	 * @param user
	 * @param emailId
	 * @param scoreGroupName
	 * @param dataOne
	 * @param dataArr
	 */
   public synchronized void updateScore(Users user, String emailId, 
										String scoreGroupName, String dataOne, String ... dataArr) {
		
		logger.debug("-----just Enter in updateScore- ------");
			
		scoresQueue.add(new Object[]{ user,emailId, scoreGroupName, dataOne});
		logger.debug("Leaving the method ..");
		
		if(!this.isRunning()) {
			logger.debug("starting the thread ...");
			(new Thread(this)).start();
		}		
	} // updateScore
	
   
   /*public synchronized void googleAnalyticsUpdateScore(Users user,String emailId,String scoreGroupName){
	   logger.debug("-----just Enter in googleAnalyticsUpdateScore- ------");
	   scoresQueue.add(new Object[]{)
   }*/
	
   
   
   
   
   
	/**
	 * 
	 * @return
	 */
	private boolean isRunning() {
		return isRunning;
	}

	
	/**
	 * 
	 */
	public void run() {

		try{
			logger.debug("-- just entered ---");
			isRunning = true;
			
			Users user = null;
			String emailId=null;
			String scoreGroupName=null;
			String dataOne=null;
			Object[] tempScoreObject=null;
			ContactScores contactScore = null;
			UserScoreSettings userScoreSettings = null;
			
			while((tempScoreObject = scoresQueue.peek()) != null) {
				
				try {
					 user=(Users)tempScoreObject[0];
					 emailId=(String)tempScoreObject[1];
					 scoreGroupName=(String)tempScoreObject[2];
					 dataOne=(String)tempScoreObject[3];
					 logger.debug("OBJECT[] VAL : Data one : "+ dataOne + " ** score group name : "+ scoreGroupName + " ** user Id : "+ user.getUserId() );
					/*userScoreSettings = userScoreSettingsDao.findByUserGropNameCount(user.getUserId(), scoreGroupName, dataOne);
					maxScore=userScoreSettings.getMaxScore();
					logger.debug("userScoreSetting scoreGroupName  :"+scoreGroupName + "maxScore is"+maxScore);
					
					if(userScoreSettings == null) {
						logger.error("*** Exception: User Setting entry for "+scoreGroupName + " does not exist, User : "+ 
								user + " and DataOne is :"+ dataOne);
						return;
					}
					contactScore = contactScoresDao.getContactScoreByEmailId(user.getUserId(), emailId);
					if(contactScore == null) {
						contactScore = new ContactScores();
						contactScore.setEmailId(emailId);
						contactScore.setUser(user);
					}*/
					 
					//***********************  EmailOpen ********************
					 
					 //dataOne = dataOne.replace("'", "\\'");
					 
					if(scoreGroupName.equalsIgnoreCase(Constants.SCORE_EMAIL_OPEN)) {
						userScoreSettings = userScoreSettingsDao.findByUserGropNameCount(user.getUserId(), scoreGroupName, dataOne);
						if(userScoreSettings == null) {
							/*logger.error("*** Exception: User Setting entry for "+scoreGroupName + " does not exist, User : "+ 
									user.getUserId() + " and DataOne is :"+ dataOne);*/
							return ;
						}
						maxScore=userScoreSettings.getMaxScore();
						//logger.debug("userScoreSetting scoreGroupName  :"+scoreGroupName + "maxScore is"+maxScore);
						
						
						
						contactScore=(ContactScores)findByContactScoresObj(user, emailId);
						int openCount = contactScore.getEmailOpenedCount() != null ?  contactScore.getEmailOpenedCount() :  0;	 
						
						if(maxScore!=null  && openCount + userScoreSettings.getScore()>maxScore ){
							contactScore.setEmailOpenedCount(maxScore);
						} else{
							contactScore.setEmailOpenedCount(openCount + userScoreSettings.getScore());	 
						}
						logger.debug("open count :" + openCount + " ** userScoreSettings.getScore() =" + userScoreSettings.getScore());
					} //emailOpen
					
					//***********************  EmailClick ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_EMAIL_CLICK)) {
						
						userScoreSettings = userScoreSettingsDao.findByUserGropNameCount(user.getUserId(), scoreGroupName, dataOne);
						if(userScoreSettings == null) {
							/*logger.error("*** Exception: User Setting entry for "+scoreGroupName + " does not exist, User : "+ 
									user.getUserId() + " and DataOne is :"+ dataOne);*/
							return ;
						}
						maxScore=userScoreSettings.getMaxScore();
						logger.debug("userScoreSetting scoreGroupName  :"+scoreGroupName + "maxScore is"+maxScore);
						
						contactScore=(ContactScores)findByContactScoresObj(user, emailId);
						int clickCount = contactScore.getEmailClickedCount() != null ?  contactScore.getEmailClickedCount() :  0;	 
							
						if(maxScore!=null  && clickCount + userScoreSettings.getScore()>maxScore ){
							contactScore.setEmailClickedCount(maxScore);
						} else{
							contactScore.setEmailClickedCount(clickCount + userScoreSettings.getScore());	 
						}
						logger.debug("click  count :" + clickCount + " ** userScoreSettings.getScore() =" + userScoreSettings.getScore());
					} //  emailClick
					
					//***********************  EmailUnSubscribed ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_EMAIL_UNSUBSCRIBED)) {
						
						userScoreSettings = userScoreSettingsDao.findByUserGropNameCount(user.getUserId(), scoreGroupName, dataOne);
						if(userScoreSettings == null) {
							/*logger.error("*** Exception: User Setting entry for "+scoreGroupName + " does not exist, User : "+ 
									user.getUserId() + " and DataOne is :"+ dataOne);*/
							return ;
						}
						maxScore=userScoreSettings.getMaxScore();
						logger.debug("userScoreSetting scoreGroupName  :"+scoreGroupName + "maxScore is"+maxScore);
						
						contactScore=(ContactScores)findByContactScoresObj(user, emailId);
						
						int unSubScribeCount = contactScore.getEmailUnsubscribedCount() != null ?  contactScore.getEmailUnsubscribedCount() :  0;
						if(maxScore!=null && unSubScribeCount + userScoreSettings.getScore()>maxScore){
							contactScore.setEmailUnsubscribedCount(maxScore);
						}
						else{
							contactScore.setEmailUnsubscribedCount(unSubScribeCount + userScoreSettings.getScore());	 
						}
						logger.debug("unsubsribe count :" + unSubScribeCount + " ** userScoreSettings.getScore() =" + userScoreSettings.getScore());
					} // emailUnsubscribe
					
					//***********************  EmailNotOpen ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_EMAIL_NOTOPEN)) {
						// TODO Need to develop this code
						
					} // email NotOpen
						
					//***********************  PageVisit ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_PAGE_VISIT)) {
						
						//logger.debug("Just entered here in :"+scoreGroupName);
						List<UserScoreSettings> list=userScoreSettingsDao.findByUserGroupNameRecrd(user.getUserId(), scoreGroupName);
						//logger.debug("pageVisited record Size is========>"+list.size());
						if(list.size()>0){
							 userScoreSettings=(UserScoreSettings)list.get(0);
							 maxScore=userScoreSettings.getMaxScore(); 
							 //logger.debug("src vists maxScore is"+userScoreSettings.getMaxScore());
							for (Object object : list) {
								userScoreSettings=(UserScoreSettings)object;
								
								Object[] obj= new Object[]{userScoreSettings.getCondition(),userScoreSettings.getDataOne(),
															userScoreSettings.getScore()};
								if(userScoreSettings.getCondition().equalsIgnoreCase(Constants.PVISITCONTAINS)){
									 
									 scoreCndMap.put(PVISISTCNT+index,obj);
								 }
								else if(userScoreSettings.getCondition().equalsIgnoreCase(Constants.PVISITREGEX)){
									 
									 scoreCndMap.put(PVISISREGEX+index,obj);
								 }
								else if(userScoreSettings.getCondition().equalsIgnoreCase(Constants.PVISITALLVISIT)){
									 score =score+userScoreSettings.getScore();
//									 scoreCndMap.put(PVISISTALLVISIT+index,obj);
								 }
								index++;
							}
							
						}else {
							return;
						}
						Set set =scoreCndMap.keySet();
						 for(int j=0;j<set.size();j++){
							 if(scoreCndMap.containsKey(PVISISTCNT+j)) {
								 Object[] obj = scoreCndMap.get(PVISISTCNT+j);
								 String str = (String)obj[1];
								 if(str.contains(dataOne)) {
									 score= score+(Integer)obj[2];
								 }
							 }
							 else if(scoreCndMap.containsKey(PVISISREGEX+j)) {
								 Object[] obj = scoreCndMap.get(PVISISREGEX+j);
								 String str = (String)obj[1];
								 //logger.debug("dateOne is: ****** :"+str  +"score i val is :"+score);
								 if(str.contains(dataOne)) {
									 score= score+(Integer)obj[2];
								 }
							 }
						 }
						contactScore=(ContactScores)findByContactScoresObj(user, emailId);
						int pageVistCount = contactScore.getPageVisitedCount() != null ?  contactScore.getPageVisitedCount() :  0;	 
						
						if(maxScore!=null  && pageVistCount + userScoreSettings.getScore()>maxScore ){
							contactScore.setPageVisitedCount(maxScore);
						} else{
							contactScore.setPageVisitedCount(pageVistCount + score);	 
						}
						logger.debug("PageVisited Count :" + pageVistCount + " ** userScoreSettings.getScore() =" + score);
						
					} // pageVisited
					
					
					
					//***********************  Downloaded ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_DOWNLOAD)) {
						  List<UserScoreSettings> list=userScoreSettingsDao.findByUserGroupNameRecrd(user.getUserId(), scoreGroupName);
						  if(list.size()>0) {
							 // logger.debug("Downloaded records listSize  :"+list.size());
							  for (Object obj : list) {
								  userScoreSettings=(UserScoreSettings)obj;
								  String str=userScoreSettings.getDataOne();
								  
								  if(str.contains(dataOne)) {
									  contactScore=(ContactScores)findByContactScoresObj(user, emailId);
									  /*contactScore = contactScoresDao.getContactScoreByEmailId(user.getUserId(), emailId);
									  if(contactScore == null) {
										contactScore = new ContactScores();
										contactScore.setEmailId(emailId);
										contactScore.setUser(user);
									  }
									  contactScore.setLastModifiedDate(Calendar.getInstance());*/
									  int downloadCount = contactScore.getDownLoadedCount() != null ?  contactScore.getDownLoadedCount() :  0;	 
										 
									  if(maxScore!=null  && downloadCount + userScoreSettings.getScore()>maxScore ) {
										 contactScore.setDownLoadedCount(maxScore);
									  } else {
										 contactScore.setDownLoadedCount(downloadCount + userScoreSettings.getScore());	 
									  }
									logger.debug("download  count :" + downloadCount + " ** userScoreSettings.getScore() =" + userScoreSettings.getScore());  
								  }
							}
						  }else return;
					} //downloaded
						
					//***********************  SourceOfVisit********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_SOURCE_OF_VISIT)) {
						
						
						 List<UserScoreSettings> list=userScoreSettingsDao.findByUserGroupNameRecrd(user.getUserId(), scoreGroupName);
						
						 if(list.size()>0){
							 logger.debug("SorurceOfRecords list size is ====>"+list.size());
							 userScoreSettings=(UserScoreSettings)list.get(0);
							 maxScore=userScoreSettings.getMaxScore(); 
							 logger.debug("src vists maxScore is"+userScoreSettings.getMaxScore());
							 
							 for (Object object : list) {
								 userScoreSettings=(UserScoreSettings)object;
								 Object[] obj=new Object[]{ userScoreSettings.getDataOne(),  userScoreSettings.getScore()};
								 logger.debug("-- Anonymous object created---");
								 
								 if(userScoreSettings.getCondition().equalsIgnoreCase(Constants.SRCVISITCONTAINS)){
									 
									 scoreCndMap.put(SRCVISISTCNT+index,obj);
								 }
								 else if(userScoreSettings.getCondition().equalsIgnoreCase(Constants.SRCVISITDNTCONTAINS)){
									 scoreCndMap.put(SRCVISISTDOESNTCNT+index,obj);
								 }
								 else if(userScoreSettings.getCondition().equalsIgnoreCase(Constants.SRCVISITALLTHESEWRDS)){
									 scoreCndMap.put(SRCVISISTALLTHESEWRDS+index,obj);
								 }
								 else if(userScoreSettings.getCondition().equalsIgnoreCase(Constants.SRCVISITANYOFTHESEWRDS)){
									 scoreCndMap.put(SRCVISISTANYOFTHESEWORDS+index,obj);
								 }
								 index++;
								
								
							}
						 }else return;
						 
						
						 Set set =scoreCndMap.keySet();
						 for(int j=0;j<set.size();j++){
							 if(scoreCndMap.containsKey(SRCVISISTCNT+j)) {
								 Object[] obj = scoreCndMap.get(SRCVISISTCNT+j);
								 String str = (String)obj[0];
								 logger.debug("dateOne is  :"+str  +"score i val is :"+score);
								 if(str.contains( dataOne)){
									 score= score+(Integer)obj[1];
								 }
							 }
							 else if(scoreCndMap.containsKey(SRCVISISTDOESNTCNT+j)) {
								 Object[] obj = scoreCndMap.get(SRCVISISTDOESNTCNT+j);
								 String str = (String)obj[0];
								 logger.debug("dateOne is  :"+str  +"score i val is :"+score);
								 if(str.contains( dataOne)){
									 score= score+(Integer)obj[1];
								 }
							 }
							 else if(scoreCndMap.containsKey(SRCVISISTALLTHESEWRDS+j)) {
								 Object[] obj = scoreCndMap.get(SRCVISISTALLTHESEWRDS+j);
								 String str = (String)obj[0];
								 logger.debug("dateOne is  :"+str  +"score i val is :"+score);
								 if(str.contains( dataOne)){
									 score= score+(Integer)obj[1];
								 }
							 }
							 else if(scoreCndMap.containsKey(SRCVISISTANYOFTHESEWORDS+j)) {
								 Object[] obj = scoreCndMap.get(SRCVISISTANYOFTHESEWORDS+j);
								 String str = (String)obj[0];
								 logger.debug("dateOne is  :"+str  +"score i val is :"+score);
								 if(str.contains( dataOne)){
									 score= score+(Integer)obj[1];
								 }
							 }
							 
						 }
						 
						// logger.debug("minScore is====>"+score);
						 contactScore=(ContactScores)findByContactScoresObj(user, emailId);
						 int sourceOfVisitCount = contactScore.getSourceOfVisitCount() != null ?  contactScore.getSourceOfVisitCount() :  0;	 
							if(maxScore!=null  && sourceOfVisitCount + userScoreSettings.getScore()>maxScore ){
								contactScore.setSourceOfVisitCount(maxScore);
							} else{
								contactScore.setSourceOfVisitCount(sourceOfVisitCount + score);	 
							}
							logger.debug("SourceOfVisitCount :" + sourceOfVisitCount + " ** userScoreSettings.getScore() =" + userScoreSettings.getScore());
						  
						
						
					} //sourceOfVisit
						
					//***********************  FormAboundoned ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_FORM_ABND)) {
						// TODO Need to develop this code
					} // FormAboundoned
					//***********************  FormFillRatio ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_FORM_F_RATIO)) {
						// TODO Need to develop this code
					} // FormFillRatio
					//***********************  FormSubmit ********************
					else if (scoreGroupName.equalsIgnoreCase(Constants.SCORE_FORM_SUBMIT)) {
						// TODO Need to develop this code
					} // Form Submit
				
				//contactScoresDao.saveOrUpdate(contactScore);
				contactScoresDaoForDML.saveOrUpdate(contactScore);

				} catch (Exception ex) {
					logger.error("** Exception : Root", ex);
				}
				finally {
					scoresQueue.remove();
				}

			} //while
			
		}catch (Exception ex) {
			logger.error("** Exception : Root", (Throwable)ex);
		}
		finally {
			isRunning = false;
			//System.gc();
		}
		
	} // run
	
	public Object  findByContactScoresObj(Users  user,String emailId){
		
		try {
			/*UserScoreSettings userScoreSettings = userScoreSettingsDao.findByUserGropNameCount(user.getUserId(), scoreGroupName, dataOne);
			maxScore=userScoreSettings.getMaxScore();
			logger.debug("userScoreSetting scoreGroupName  :"+scoreGroupName + "maxScore is"+maxScore);
			
			if(userScoreSettings == null) {
				logger.error("*** Exception: User Setting entry for "+scoreGroupName + " does not exist, User : "+ 
						user.getUserId() + " and DataOne is :"+ dataOne);
				return null;
			}*/
			ContactScores contactScore = contactScoresDao.getContactScoreByEmailId(user.getUserId(), emailId);
			if(contactScore == null) {
				contactScore = new ContactScores();
				contactScore.setEmailId(emailId);
				contactScore.setUser(user);
			}
			logger.debug("ContactScore Object is ===>"+contactScore.getEmailId());
			contactScore.setLastModifiedDate(Calendar.getInstance());
			return contactScore;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
	
}

package org.mq.captiway.scheduler;



	import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;

import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.OrgSMSkeywords;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.OrgSMSkeywordsDao;
import org.mq.captiway.scheduler.dao.OrgSMSkeywordsDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zkoss.zk.ui.Sessions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


	public class KeywordExpAlert extends TimerTask implements ApplicationContextAware {
		
		private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

		public KeywordExpAlert() {
			// TODO Auto-generated constructor stub
		}
		
		private ApplicationContext applicationContext;
		
		public void setApplicationContext(ApplicationContext applicationContext) {
			this.applicationContext = applicationContext;
		}
		
		OrgSMSkeywordsDao orgSMSkeywordsDao = null;
		OrgSMSkeywordsDaoForDML orgSMSkeywordsDaoForDML = null;
		EmailQueueDao emailQueueDao = null;
		EmailQueueDaoForDML emailQueueDaoForDML = null;
		@Override
		public void run() {
			try {
				
				
				try {
					ServiceLocator locator = ServiceLocator.getInstance();
					orgSMSkeywordsDao = (OrgSMSkeywordsDao)locator.getDAOByName(OCConstants.ORGSMSKEYWORDS_DAO);
					orgSMSkeywordsDaoForDML = (OrgSMSkeywordsDaoForDML)locator.getDAOForDMLByName(OCConstants.ORGSMSKEYWORDS_DAO_FOR_DML);
					emailQueueDao = (EmailQueueDao)locator.getDAOByName("emailQueueDao");
					emailQueueDaoForDML = (EmailQueueDaoForDML)locator.getDAOForDMLByName("emailQueueDaoForDML");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					try {
						throw new BaseServiceException("No dao(s) found in the context");
					} catch (BaseServiceException e1) {
						// TODO Auto-generated catch block
						logger.error(e1);
					}
				}
				//logger.debug("************* here in run method it is printing **************");
				
				int totalCount = orgSMSkeywordsDao.getExpKeywordCount();
				int size = 100;
				int start = 0;
				
				List<EmailQueue> emailQueueList = new ArrayList<EmailQueue>();
			//	logger.debug("total count is " + totalCount);
				if(totalCount > 0) {
					
					String subjectTemplate = "";
					String originalMessageTemplate = "";
					Long userId = null;
					String toMailId = "";
					String ccEmailID = "";
					Users user = null;
					String keywords = "";
					String shortCodes = "";
					String fname = "";
					String lname = "";
					String keywordAndDateForTemplate = "";
					
					for(int i= 0;i<=totalCount;i+=size) {
						
						List<OrgSMSkeywords> expKeywordList = orgSMSkeywordsDao.getExpKeyword(i,size);
						
						//String toMailId = "";
						//logger.debug("list of size " + expKeywordList.size());
						
						
						if(expKeywordList != null) {
							
							
							int count =0;
							
							String serverTimeZoneVal = PropertyUtil.getPropertyValueFromDB(Constants.SERVER_TIMEZONE_VALUE);
							int serverTimeZoneValInt = Integer.parseInt(serverTimeZoneVal);
							subjectTemplate = PropertyUtil.getPropertyValueFromDB(Constants.KEYWORD_EXP_ALERT_SUB);
							originalMessageTemplate = PropertyUtil.getPropertyValueFromDB(Constants.KEYWORD_EXP_ALERT_MAIL_CONTENT) ;
							for(OrgSMSkeywords expKeyword : expKeywordList) {
								
								if(userId != null && userId.longValue() != expKeyword.getUser().getUserId().longValue() && 
										!toMailId.isEmpty() && !toMailId.equalsIgnoreCase(expKeyword.getUser().getEmailId())) {
									
									//peform send logic if its not already sent
									List<EmailQueue> retList = emailQueueDao.findByTypeAndStatus(Constants.EQ_TYPE_KEYWORD_ALERT,
											userId);
									
									
									String replacedContent= originalMessageTemplate;
									
									//subjectTemplate = subjectTemplate.replaceAll(Constants.KEYWORD_EXP_ALERT_SUB_PH, keywords);
									
									
									
									logger.debug("message content before replace " + originalMessageTemplate);
									
									logger.debug("fname " + fname + " lname " + lname);
									
									replacedContent = replacedContent.replace(Constants.KEYWORD_EXP_ALERT_MAIL_CONTENT_PH, keywordAndDateForTemplate);
									
									
									if(fname != null && fname.length() > 0 ) replacedContent = replacedContent.replace(Constants.USER_FIRST_NAME_PH, fname);
									
									if(lname != null && lname.length() > 0) replacedContent = replacedContent.replace(Constants.USER_LAST_NAME_PH, lname);
									
									boolean isSent = ( (retList == null) || (retList != null && retList.size() > 0 ? 
											retList.get(0).getMessage().equalsIgnoreCase(replacedContent) : false));
									
									
									//check whether we sent or not
									if(!isSent)  {
											
										logger.debug("message content after replace " + replacedContent);
										
										
										EmailQueue emailQueue = new EmailQueue(subjectTemplate, 
												replacedContent, Constants.KEYWORD_EXP_MAIL_TYPE, 
												Constants.MAIL_SENT_STATUS_ACTIVE, toMailId, new Date());
										emailQueue.setUser(user);
										emailQueue.setCcEmailId(ccEmailID);
										emailQueueList.add(emailQueue);
																				
									}
									
									
								}//if
								
								toMailId = expKeyword.getUser().getEmailId();
								ccEmailID = expKeyword.getToEmailId();
								userId = expKeyword.getUser().getUserId();
								user = expKeyword.getUser();
								
								if(!keywords.isEmpty()) keywords += ",'" + expKeyword.getKeyword() +"'";
								else keywords += "'" + expKeyword.getKeyword() +"'";
								
								if(!shortCodes.isEmpty()) shortCodes += ",'" + expKeyword.getShortCode() +"'";
								else shortCodes += "'" + expKeyword.getShortCode() +"'";
								
								//to convert into client time zone
								Calendar expDate = expKeyword.getValidUpto();
								
								String timezoneDiffrenceMinutes = expKeyword.getClientTimeZone();
								int timezoneDiffrenceMinutesInt = 0;
								
								if(timezoneDiffrenceMinutes != null)  timezoneDiffrenceMinutesInt = Integer.parseInt(timezoneDiffrenceMinutes);
								
								timezoneDiffrenceMinutesInt = timezoneDiffrenceMinutesInt-serverTimeZoneValInt;
								
								expDate.add(Calendar.MINUTE, timezoneDiffrenceMinutesInt);
								
								String date =	MyCalendar.calendarToString(expKeyword.getValidUpto(), MyCalendar.FORMAT_DATEONLY);
								
								keywordAndDateForTemplate += "Keyword_Name: "+ expKeyword.getKeyword() + "<br/> Keyword_Expire_date: " + date +"<br/>";
								
								if(fname == null || fname.isEmpty()) fname = expKeyword.getUser().getFirstName();
								if(lname == null || lname.isEmpty()) lname = expKeyword.getUser().getLastName();
								
								/*logger.debug("got keyword "+expKeyword.getKeyword() + " will expire on "+ date);
								logger.debug("mail Id is "+ expKeyword.getToEmailId());
								logger.debug("count *****************" + count);*/
								logger.debug("expKeyword ::"+expKeyword.getKeywordId()+" User Id is  ::"+expKeyword.getUser().getUserId());
								
								/*fname = "Amit";
								lname = "Mishra";*/
								
							}
							
							
							
							/*try {
								
								
								List<String> emailIds = orgSMSkeywordsDao.getToMailIds();
								boolean isToEmailIdExists = false;
								
								for(String emailId : emailIds) {
									
									if(emailId.equalsIgnoreCase(toMailId)) isToEmailIdExists = true;
								}
								
								if(!isToEmailIdExists) {
									
									if(!toMailId.isEmpty()) {
									 	EmailQueue emailQueue = new EmailQueue(subjectTemplate, messageTemplate, Constants.KEYWORD_EXP_MAIL_TYPE, Constants.MAIL_SENT_STATUS_ACTIVE, toMailId, new Date());
									 	
									 	logger.debug("subject   "+subjectTemplate);
										logger.debug("content   "+ messageTemplate);
									 	
									 	emailQueueDao.saveOrUpdate(emailQueue);
									 	
									}
								}
								else 
									logger.debug("mail for these keywords is already sent today");
						 	} catch(Exception e) {
						 		//logger.error("exception while saving email queue object");
						 		logger.error("** Exception : ", e);
						 	}*/
							
							if(emailQueueList.size() == size) {
								
								//emailQueueDao.saveByCollection(emailQueueList);
								emailQueueDaoForDML.saveByCollection(emailQueueList);
								emailQueueList.clear();
							}
							
						}
						/*else {
							logger.debug("************* list null exit **********");
						}*/
					}
					
					if(userId != null && user != null && !toMailId.isEmpty() && !ccEmailID.isEmpty() && emailQueueList.size() == 0) {
						List<EmailQueue> retList = emailQueueDao.findByTypeAndStatus(Constants.EQ_TYPE_KEYWORD_ALERT, userId);
						
						String replacedContent= originalMessageTemplate;
						
						//subjectTemplate = subjectTemplate.replaceAll(Constants.KEYWORD_EXP_ALERT_SUB_PH, keywords);
						
						
						
						logger.debug("message content before replace " + originalMessageTemplate);
						
						logger.debug("fname " + fname + " lname " + lname);
						
						replacedContent = replacedContent.replace(Constants.KEYWORD_EXP_ALERT_MAIL_CONTENT_PH, keywordAndDateForTemplate);
						
						if(fname != null && fname.length() > 0 ) replacedContent = replacedContent.replace(Constants.USER_FIRST_NAME_PH, fname);
						
						if(lname != null && lname.length() > 0) replacedContent = replacedContent.replace(Constants.USER_LAST_NAME_PH, lname);
						
						logger.debug("message content after replace " + replacedContent);
						
						boolean isSent = ( (retList == null) || (retList != null && retList.size() > 0 ? 
								retList.get(0).getMessage().equalsIgnoreCase(replacedContent) : false));
						
						//check whether we sent or not
						if(!isSent)  {
							
							EmailQueue emailQueue = new EmailQueue(subjectTemplate, 
									replacedContent, Constants.KEYWORD_EXP_MAIL_TYPE, 
									Constants.MAIL_SENT_STATUS_ACTIVE, toMailId, new Date());
							emailQueue.setUser(user);
							emailQueue.setCcEmailId(ccEmailID);
							emailQueueList.add(emailQueue);
						
						
					}
					
					if(emailQueueList.size() > 0) {
						
						//emailQueueDao.saveByCollection(emailQueueList);
						emailQueueDaoForDML.saveByCollection(emailQueueList);
						emailQueueList.clear();
						
					}
				}
				/*else {
					logger.debug("no expired keywords");
				}*/
					updateKeywordStatus(Constants.KEYWORD_STATUS_EXPIRED);
				}
				
				updateKeywordStatus(Constants.KEYWORD_STATUS_ACTIVE);
			} 
			catch (RuntimeException e) {
				logger.error("** Exception : ", e);
			}
		}
		
		public void updateKeywordStatus(String status) {
			
			//int affectedKeywords =  orgSMSkeywordsDao.keywordStatusUpdate(status);
			int affectedKeywords =  orgSMSkeywordsDaoForDML.keywordStatusUpdate(status);
			logger.debug("number of rows updated "+ affectedKeywords);
			
			
		}

	}


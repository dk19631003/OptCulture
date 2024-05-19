/**
 * 
 */
package org.mq.captiway.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;
import org.mq.captiway.scheduler.beans.ExternalSMTPEvents;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.MyCalendar;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 
 */
public class MailerCloudDLRReportTimer implements ApplicationContextAware {

	private static final  Logger logger = LogManager.getLogger(Constants.FILE_PROCESS_LOGGER);
	
	private ApplicationContext context;
	private ExternalSMTPEventsDaoForDML externalSMTPEventsDaoForDML = null;
	private UsersDao usersDao = null;


	public ExternalSMTPEventsDaoForDML getExternalSMTPEventsDaoForDML() {
		return externalSMTPEventsDaoForDML;
	}


	public void setExternalSMTPEventsDaoForDML(ExternalSMTPEventsDaoForDML externalSMTPEventsDaoForDML) {
		this.externalSMTPEventsDaoForDML = externalSMTPEventsDaoForDML;
	}


	public UsersDao getUsersDao() {
		return usersDao;
	}


	public void setUsersDao(UsersDao usersDao) {
		this.usersDao = usersDao;
	}


	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;

	}
	

	// default constructor
	MailerCloudDLRReportTimer(){
		
	}
	@Scheduled(cron="0 */15 * * * *") // runs every 15mins
	public void runTask() {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		logger.debug("....=======> Started Mailer thread report run :: " + startTime);
		
		try {
			externalSMTPEventsDaoForDML = (ExternalSMTPEventsDaoForDML)context.getBean("externalSMTPEventsDaoForDML");
			usersDao = (UsersDao) context.getBean("usersDao");
	
		// run worker thread from here : 
			

			logger.info("=============Entered Mailer thread report processing=========================");
			List<ExternalSMTPEvents> eventsToBeProcessed = new ArrayList<ExternalSMTPEvents>();

			try {
				String source = "/home/mailercloud";
				String userDataPath = PropertyUtil.getPropertyValue("usersParentDirectory");
				String sourceFileStr = userDataPath +"/processedmailercloudreport/";


				logger.info("source processing : " + source);
				File sorceFiles = new File(source);
				
				logger.info("external stmp Dao for DML object : " + externalSMTPEventsDaoForDML);

				if (!sorceFiles.exists()) {
					logger.info("Entered in the source file is not exists");
					logger.debug("*** Exception : mailercloud Source file " + sorceFiles.getName() + " does not exist ***");
					sorceFiles.mkdir();
				}

				File[] listFiles = sorceFiles.listFiles();
				logger.info("Has files in the mailercloud ? : " + listFiles.length);

				if (listFiles == null || listFiles.length <= 0) {
					logger.info("Entered in the source file list count : " + listFiles);
					return;
				}
				for (int i = 0; i < listFiles.length; ++i) {
					
					if (listFiles[i].getName().contains("sent") || listFiles[i].getName().contains("bounce")) {
						try {
							logger.info("File name started : " + listFiles[i].getName());
							if (!listFiles[i].canRead()) {
								if (logger.isDebugEnabled()) {
									logger.debug("Unable to read the file : " + listFiles[i].getName());
									continue;
								}
							}
							logger.info("File name started in else block : " + listFiles[i].getName());
							File sourceFile = listFiles[i];
							String sourceFileName = sourceFile.getName();
							logger.debug(String.valueOf(sourceFileName) + ":: file processing is started..");
							
							FileReader fileReader = new FileReader(sourceFile);
							BufferedReader br = new BufferedReader(fileReader);
						
							String lineStr = null;
										
							logger.info(
									">>>>>>>>>>>>>>>>>>>> taking data from the csv file from the mailercloud report>>>>>>>>>>>>>>>>>>>.");
							String emailStr = "";
							String bounceReasonStr = null;
							String bounceCategoryStr = null;
							Long userId = null;
			//ExternalSMTPEvents newEvent = new ExternalSMTPEvents(serverName, eventType,  userId, crId, Calendar.getInstance(), email);
							String serverName = "";
							String eventType = null;
							String statusCode = null;
							String sentId = null;
							Long crId = null;
							Users user = null;


							while ((lineStr = br.readLine()) != null) {
								if (lineStr.trim().length() == 0) {
									logger.info("Entered where lines are zero..." + listFiles[i]);
									continue;
								}
								
								logger.info("Checking the linestr in file :: "+lineStr.trim()+"File name ::"+listFiles[i].getName());
								int posRemove = lineStr.indexOf("{"); // for custom header as unable to get the- should be get in both bounce and sent
								if(posRemove == -1) {
									continue;
								}
								//to remove all unnecessary empty elements when parsed.
								 	lineStr = lineStr.replace(",,",",").replace(",,",",").replace("\"", "");//parse(lineStr); 
								 	//in sent file:
								 	//2023-10-06 10:29:28+0530,[0]
								 	//test@edm.optculture.com,[1]
								 	//anshulgelani@gmail.com,[2]
								 	//[3]"{""metadata"":{""sentId"":""1876704"",""crId"":""99422"",""serverName"":""http:\/\/qcapp.optculture.com"",""source"":""CampaignSchedule"",""userId"":""1076"",""CampaignSource"":""MailerCloud""}}"^M,,

									String customData = lineStr.toString().substring(0,posRemove);
									final String[] lineStrTokens = customData.split(",");
									//2023-10-08 19:16:09+0530, [0]
									//test@edm.optculture.com, [1]
									//sameerasultana7888@gmail.com, [2]
									//[3]smtp;550 5.1.1 The email account that you tried to reach does not exist. Please try double-checking the recipient's email address for typos or unnecessary spaces. Learn more at https://support.google.com/mail/?p=NoSuchUser f23-20020a170906085700b0099cc030dc66si3487536ejd.19 - gsmtp,
									 //[4]bad-mailbox_Hardbounce, - category.
									//[5]vnm-vmta25,,gmail.com/vnm-vmta25,
									//[7]
								if (lineStrTokens.length == 0) {
									logger.info("Entered where linetoken are zero..." + listFiles[i] + ": "
											+ lineStrTokens.length);
									continue;
								}else if(lineStrTokens.length > 0) { // basic fields 
										
									try {
										if(sourceFileName.contains("sent")) {
											eventType = "delivered";
										}
										if(sourceFileName.contains("bounce")) {
											eventType = "bounce";
										}
									logger.info("Entered where linetoken size..." + listFiles[i] + ": " + lineStrTokens.length);
									int lengthVal = 0;
									while (lengthVal < lineStrTokens.length) {
										logger.info("Data lineStr :" + lineStrTokens[lengthVal]);
										if(lengthVal == 2 && !lineStrTokens[lengthVal].isEmpty()) {
											emailStr = lineStrTokens[lengthVal]; //[2]	
											logger.info("Email Str : "+emailStr);
										}
										if(sourceFileName.contains("bounce")) {
										if(lengthVal== 3 && !lineStrTokens[lengthVal].isEmpty()) {
											 bounceReasonStr = lineStrTokens[lengthVal];//[3]
											 logger.info("bounce reason : "+bounceReasonStr);
											 
										}
										if(lengthVal== 4 && !lineStrTokens[lengthVal].isEmpty()) {
											bounceCategoryStr = lineStrTokens[lengthVal]; //[4]
											logger.info("Bounce category : "+bounceCategoryStr);
												// smtp;550 5.1.1 
											
												if(bounceReasonStr.contains("smtp;")) {
													int code = bounceReasonStr.indexOf(";");

												 statusCode = bounceReasonStr.substring(code+1, code+4);
												logger.info("status code : "+statusCode); // to store 550 code. 
												}
										}
									}
										lengthVal++;
									} // while 
									if (posRemove != -1) {
										
										customData = lineStr.toString().substring(posRemove);
										
										 logger.info("Printing data in the line str for custom header for {symbol} : "+ customData);
										
										customData = customData.replace("{", "").replace("}", "").replace("\"\"", "")
												.replace("\\/\\/", "//").replace("metadata:", "").replace("data:","").trim();
										//metadata:{sentId is coming in the sent
										//data:{sentId is  coming in the bounce 
										String[] requestedData = customData.split(",");

										for (String data : requestedData) {
											
											if (data != null && !data.isEmpty()) {
												
												String[] value = data.split(":");
												
												if (value != null && value.length > 0) {

													if (value[0].equalsIgnoreCase("crId")) {
														 crId = Long.parseLong(value[1]);
														logger.info("Saving crId : " + crId + " " + listFiles[i]);
													}
													
													if (value[0].equalsIgnoreCase("serverName")) {
														if(value.length > 2)
														 serverName = value[1]+":"+value[2];
														
														logger.info("Saving source : " + serverName + " " + listFiles[i]);

													}
													if (value[0].equalsIgnoreCase("sentId")) {
														
														 sentId = value[1];

														logger.info("Saving sentId : " + sentId + " " + listFiles[i]);

													}

													if (value[0].equalsIgnoreCase("userId")) {
														userId = Long.parseLong(value[1]);
														logger.info("Saving userId : " + listFiles[i] + " " + userId);
													}

												} else {
													logger.info("No custom header, so returning back" + listFiles[i]);
													break;
												}

											} // if

											else {
												break;
											}
										
										} // for
									} // if pos has {
				
									ExternalSMTPEvents newEvent = new ExternalSMTPEvents(serverName, eventType,  userId, crId, Calendar.getInstance(), emailStr);
									newEvent.setStatusCode(statusCode);
									newEvent.setType(bounceCategoryStr);
									newEvent.setReason(bounceReasonStr);


									if (sourceFileName.contains("sent")) {
										newEvent.setEventType("delivered");
									} else if (sourceFileName.contains("bounce")) {
										
										newEvent.setEventType("bounce");
										
									}
									
									eventsToBeProcessed.add(newEvent);
									
								} // try
								catch (Exception e) {
									logger.info("Exception while adding in ExternalSMTPevents table ..", e);
									e.printStackTrace();
								}
							
							
							if (logger.isDebugEnabled()) {
								logger.debug(String.valueOf(sourceFileName) + ":: file processing is completed..");
							}
							
						/*		if (userId != null) {
									  user = usersDao.find(userId);
								} */
								// /var/www/html/subscriber/UserData

								/*
								 * File newFile = new File(sourceFileStr); if (!newFile.exists()) {
								 * newFile.mkdir(); } else { File[] processFile = newFile.listFiles(); for (File
								 * check : processFile) { if
								 * (check.getName().equalsIgnoreCase(listFiles[i].getName())) { check.delete();
								 * // deleting already existing file to move new file. } } }
								 */
								Calendar cCal = Calendar.getInstance();
								
								String currTimeStr = MyCalendar.calendarToString(cCal, MyCalendar.FORMAT_YEARTOSEC);

								String extensionStr = FilenameUtils.getExtension(sourceFile.getName());
								try {
									String moveCmd = "mv " + sourceFile.getPath()+" "+sourceFileStr+""+sourceFile.getName()+"_OC_"+currTimeStr + cCal.get(Calendar.MILLISECOND)+ "."+extensionStr;
										//	+"processed"+ (sourceFileStr);
									logger.info("moveCmd the file after processing ::" + moveCmd);
									Runtime.getRuntime().exec(moveCmd);
									
								//	sourceFile.renameTo(newFile);

									logger.info("File moved successfully... " + listFiles[i].getPath());
									
								} catch (Exception e) {
									logger.error("Exception while moving/renaming the file " + e.getMessage());
									
								}
								try {
									String removeCmd ="rm "+sourceFile;
									logger.info("removeCmd the file as it i ::" + removeCmd);
									Runtime.getRuntime().exec(removeCmd);

								}catch(Exception e) {
									logger.error("Exception wile removing file : "+e);	
									
									}
								
							} // external size
								} //  while
							br.close();
							fileReader.close(); // closing after opening them upper while loop
					}
				catch (Exception e) {
							logger.error("Exception ::::", (Throwable) e);
						}
					} // if
				} // for
				} // try file name started.
			 catch (Exception e) {
				logger.error("Exception ::::", (Throwable) e);
				e.printStackTrace();

			}
				if(eventsToBeProcessed.size() > 0 ) {
					
					logger.debug("saving events to DB......");
					//externalSMTPEventsDao.saveByCollection(eventsToBeProcessed);
					externalSMTPEventsDaoForDML.saveByCollection(eventsToBeProcessed);

			}
			logger.info("Completed Contacts file processing............");
			long endTime = System.currentTimeMillis();
			logger.info("=============Finished Mailer thread report processing========================="+ (endTime-startTime));
		}
		catch(Exception e) {
			logger.info("Exception e.."+e);
		}
	}

	
}

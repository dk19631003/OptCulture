package com.optculture.launchpad.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.clickhouse.client.internal.apache.commons.compress.utils.FileNameUtils;
import com.optculture.launchpad.repositories.UserRepository;
import com.optculture.shared.entities.communication.email.ExternalSMTPEvents;
import com.optculture.shared.entities.org.User;

@Service
public class MailerlCloudDLRProcessor extends Thread {
	

	Logger logger = LoggerFactory.getLogger(MailerlCloudDLRProcessor.class);

	@Autowired
	private UserRepository userRepo;
	
	@Value(value="${mailercloudPath}")
	private String mailercloudPath;
	
	@Value(value="${mailercloudProcessed}")
	private String mailercloudProcessedPath;

	// default constructor
	MailerlCloudDLRProcessor() {

	}

//	@Scheduled(cron = "0 */30 * * * *") //runs every specified minute

		
		@Override
		public void run() {
			try {
		
			// run worker thread from here : 
				long startTime = System.currentTimeMillis();
				 final String mailerCloudSentFile = "sent";
				 final String mailerCloudBouncedFile = "bounced";
				logger.debug("....=======> Started Mailer thread report run :: " + startTime);

				logger.info("=============Entered Mailer thread report processing=========================");
				List<ExternalSMTPEvents> eventsToBeProcessed = new ArrayList<>();

				try {
					File source = new File(mailercloudPath);//"/home/sameersultanashaik/mailercloud";
				//	String userDataPath = "/home/sameersultanashaik/mailercloud/processed";//"usersParentDirectory"; //get from app.prop
					String sourceFileStr = mailercloudProcessedPath;

					logger.info("source processing : " + source);
					File[] files = source.listFiles();
				
					if(files != null  && files.length > 0) {
						logger.info("Has files in the mailercloud ? : " + files.length);
						try {
							
							for(int i=0; i< files.length;i++) { 
								
						if(files[i].getName().contains("sent") ||files[i].getName().contains("bounced")) {
							
							if(!files[i].canRead()) {
								logger.info("This is file is not readable.. so returning back.");
							}else {
							//file is readable, so getting data from the file. 
							File myfile = files[i];
							Path pathToFile = Paths.get(myfile.getAbsolutePath());
							logger.info("checking the path of the source file in the running program = : "+pathToFile);
							String nameOfFile = myfile.getName();

								FileReader fileReader = new FileReader(myfile);
								BufferedReader br = new BufferedReader(fileReader);
							
								String lineStr = null;
											
								logger.info(
								">>>>>>>>>>>>>>>>>>>> taking data from the csv file from the mailercloud report>>>>>>>>>>>>>>>>>>>.");
								String emailStr = "";
								String bounceReasonStr = null;
								String bounceCategoryStr = null;
								Long userId = null;
								String serverName = null;
								String eventType = null;
								String statusCode = null;
								String sentId = null;
								Long crId = null;
								int count =0;
								User user = null;
								


								while ((lineStr = br.readLine()) != null) {
									count++;
									if (lineStr.trim().length() == 0) {
										logger.info("Entered where lines are zero..." + myfile);
										continue;
									}
									
									logger.info("Checking the linestr in file :: "+lineStr.trim()+"File name ::"+myfile.getName());
	
									//to remove all unnecessary empty elements when parsed.
									 	lineStr = lineStr.replace(",,",",").replace(",,",",").replace("\"", "");//parse(lineStr); 
									 	int posRemove = lineStr.toString().indexOf("{"); // for custom header as unable to get the- should be get in both bounce and sent
										if(posRemove == -1) {
											logger.info("logger has metadata/custom sent data in the report record.");
											continue;
										}
									 	//in sent file:
									 	//2023-10-06 10:29:28+0530,[0]
									 	//test@edm.optculture.com,[1]
									 	//anshulgelani@gmail.com,[2]
									 	//[3]"{""metadata"":{""sentId"":""1876704"",""crId"":""99422"",""serverName"":""http:\/\/qcapp.optculture.com"",""source"":""CampaignSchedule"",""userId"":""1076"",""CampaignSource"":""MailerCloud""}}"^M,,

										String customData = lineStr.substring(0,posRemove);
										final String[] lineStrTokens = customData.split(",");
										//2023-10-08 19:16:09+0530, [0]
										//test@edm.optculture.com, [1]
										//sameerasultana7888@gmail.com, [2]
										//[3]smtp;550 5.1.1 The email account that you tried to reach does not exist. Please try double-checking the recipient's email address for typos or unnecessary spaces. Learn more at https://support.google.com/mail/?p=NoSuchUser f23-20020a170906085700b0099cc030dc66si3487536ejd.19 - gsmtp,
										 //[4]bad-mailbox_Hardbounce, - category.
										//[5]vnm-vmta25,,gmail.com/vnm-vmta25,
										//[7]
									if (lineStrTokens.length == 0) {
										logger.info("Entered where linetoken are zero..." + myfile + ": "
												+ lineStrTokens.length);
										continue;
									}else if(lineStrTokens.length > 0) { // basic fields 
											
										try {
											if(nameOfFile.contains(mailerCloudSentFile)) {
												eventType = "delivered";
											}
											if(nameOfFile.contains(mailerCloudBouncedFile)) {
												eventType = "bounce";
											}
										logger.info("Entered where linetoken size..." + myfile + ": " + lineStrTokens.length);
										int lengthVal = 0;
										while (lengthVal < lineStrTokens.length) {
											logger.info("Data lineStr :" + lineStrTokens[lengthVal]);
											if(lengthVal == 2 && !lineStrTokens[lengthVal].isEmpty()) {
												emailStr = lineStrTokens[lengthVal]; //[2]	
												logger.info("Email Str : "+emailStr);
											}
											if(nameOfFile.contains(mailerCloudBouncedFile)) {
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
											
											customData = lineStr.substring(posRemove);
											
											 logger.info("Printing data in the line str for custom header for {symbol} : "+ customData);
											
											customData = customData.replace("{", "").replace("}", "").replace("\"\"", "")
													.replace("\\/\\/", "//").replace("metadata:", "").replace("data:","").trim();
											//metadata:{sentId is coming in the sent
											//data:{sentId is  coming in the bounce 
											String[] requestedData = customData.split(",");

											for (String data : requestedData) {
												
												if (data != null && !data.isEmpty()) {
													
												logger.info("Parasing the string customheader:"+data);

													String[] value = data.split(":");
													
													if (value != null && value.length > 0) {
														logger.info("Value 0: "+value[0]+" : Value 1 :"+value[1] );
														
														if(value.length > 2) {
															logger.info("value 2 :"+value[2]);
														}

														if (value[0].equalsIgnoreCase("crId")) {
															 crId = Long.parseLong(value[1]);
															logger.info("Saving crId : " + crId + " " + myfile);
														}
														
														if (value[0].equalsIgnoreCase("serverName")) {
															 serverName = value[1]+":"+value[2];
															logger.info("Saving source : " + serverName + " " + myfile);

														}
														if (value[0].equalsIgnoreCase("sentId")) {
															
															 sentId = value[1];

															logger.info("Saving sentId : " + sentId + " " + myfile);

														}

														if (value[0].equalsIgnoreCase("userId")) {
															userId = Long.parseLong(value[1]);
															logger.info("Saving userId : " + myfile + " " + userId);
														}

													} else {
														logger.info("No custom header, so returning back" + myfile);
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
										newEvent.setSentId(Long.parseLong(sentId));


										if (nameOfFile.contains(mailerCloudSentFile)) {
											newEvent.setEventType("delivered");
										} else if (nameOfFile.contains(mailerCloudBouncedFile)) {
											
											newEvent.setEventType("bounce");
											
										}
										
										eventsToBeProcessed.add(newEvent);
										
									} // try
									catch (Exception e) {
										logger.info("Exception while adding in ExternalSMTPevents table ..", e);
										e.printStackTrace();
									}
										
								
								if (logger.isDebugEnabled()) {
									logger.debug(String.valueOf(nameOfFile) + ":: file processing is completed..");
								}
								
									if (userId != null) {
										  user = userRepo.findByuserId(userId);
									}
		
									
								} // external size
									} //  while
								if(count <= 1) {
									try {
										String removeCmd ="rm "+nameOfFile;
										boolean isDelete = Files.deleteIfExists(myfile.toPath());
			
										logger.info("removing the file of empty records ::" + myfile+ "is deleted : "+isDelete);
										Runtime.getRuntime().exec(removeCmd);
										break;// after deleting should break not continue or return 
									}catch(Exception e) {
										logger.error("Exception wile removing file when no counts are there in the file  : "+e);	

									}
								}
										Calendar cCal = Calendar.getInstance();
								
							//	String currTimeStr = MyCalendar.calendarToString(cCal, MyCalendar.FORMAT_YEARTOSEC);
								
						//		String currTimeStr ="";

								String extensionStr = FileNameUtils.getExtension(nameOfFile);
								
								String baseName = FileNameUtils.getBaseName(nameOfFile);
																
															
								String desFolder = mailercloudProcessedPath+File.separator+baseName+"_OC_"+Calendar.getInstance().getTimeInMillis()+"."+extensionStr;
								
								File newDestination = new File(desFolder);
								try {
							//		String moveCmd = "mv " + myfile+" "+sourceFileStr+File.separator+nameOfFile+"_OC_"+currTimeStr + cCal.get(Calendar.MILLISECOND)+ "."+extensionStr;
							//		logger.info("moveCmd the file after processing ::" + moveCmd);
									br.close();
									fileReader.close();
									File sourceFile = myfile;
									  logger.info("path of diesti "+newDestination.getAbsolutePath());
											
									  if(pathToFile.toAbsolutePath().toString().equalsIgnoreCase("/home/sameerasultanashaik/Documents/BitBucket/launchpad_oct26/launchpad/saveFile.txt")){
										logger.info("Equal paths");  
									  }
									    System.out.println("Source :"+pathToFile.toAbsolutePath());
									  
									  if (!sourceFile.exists()) {
									    System.out.println("Source file does not exist");
									    return;
									  }

									 
									 Boolean isMoved =   sourceFile.renameTo(newDestination);
									 //	Process p = Runtime.getRuntime().exec(moveCmd);
									logger.info("File moved successfully... " + myfile+" :: "+isMoved );
								//	+" Porcess P "+ (p.waitFor()==0?"moved": "not moved"));	
									
								} catch (Exception e) {
									logger.error("Exception while moving/renaming the file now " + e.getMessage());
									
								}
								/*try {
								 * 
								 * 	FileUtils.forceDelete(myfile);
									String removeCmd ="rm "+myfile;
									logger.info("removeCmd the file as it i ::" + removeCmd);
									Runtime.getRuntime().exec(removeCmd);

								}catch(Exception e) {
									logger.error("Exception wile removing file : "+e);	
									
									} */
} // else 
						
						}else {
							logger.info("no mailercloud related files are in this files[i] so returning without processing.");
						}
							  
						
					} // for
						}catch(Exception e) {
							logger.info("Exception when using mailercloud.."+e);
						}
					}else {
						logger.debug("No files to process to existing process");
						return;
					}
					} // try file name started.
	catch(Exception e)
	{
					logger.error("Exception ::::", (Throwable) e);
					e.printStackTrace();

				}if(eventsToBeProcessed.size()>0)
	{

		logger.debug("saving events to DB......");
		// externalSMTPEventsDao.saveByCollection(eventsToBeProcessed);

	}logger.info("Completed Contacts file processing............");logger.info("=============Finished Mailer thread report processing=========================");
	}catch(Exception e)
	{
		logger.info("Exception e.." + e);
	}
}

}

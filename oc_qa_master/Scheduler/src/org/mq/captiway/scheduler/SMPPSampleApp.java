package org.mq.captiway.scheduler;
import org.bulatnig.smpp.net.impl.TcpConnection;
import org.bulatnig.smpp.pdu.CommandStatus;
import org.bulatnig.smpp.pdu.Npi;
import org.bulatnig.smpp.pdu.Pdu;
import org.bulatnig.smpp.pdu.Ton;
import org.bulatnig.smpp.pdu.impl.*;
import org.bulatnig.smpp.session.MessageListener;
import org.bulatnig.smpp.session.Session;
import org.bulatnig.smpp.session.State;
import org.bulatnig.smpp.session.impl.BasicSession;
import org.bulatnig.smpp.net.impl.TcpConnection;
import org.bulatnig.smpp.pdu.CommandStatus;
import org.bulatnig.smpp.pdu.Npi;
import org.bulatnig.smpp.pdu.Pdu;
import org.bulatnig.smpp.pdu.Ton;
import org.bulatnig.smpp.pdu.impl.BindTransceiver;
import org.bulatnig.smpp.pdu.impl.SubmitSm;
import org.bulatnig.smpp.session.impl.BasicSession;
// import org.jruby.ast.IScopingNode;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDao;
import org.mq.captiway.scheduler.dao.SMSCampaignSentDaoForDML;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.smpp.TCPIPConnection;
import org.springframework.context.ApplicationContext;

import com.jcraft.jsch.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.mq.captiway.scheduler.utility.Constants;
public class SMPPSampleApp {

	
	public SMPPSampleApp() {}
	
	
	private ApplicationContext context;
	private String systemId;
	

	private String systemType;
	private String password;
	private boolean isTransactional;
	private boolean isEnded;
	private String userId;
	private String accountPwd;
	
	
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	public boolean isEnded() {
		return isEnded;
	}

	public void setEnded(boolean isEnded) {
		this.isEnded = isEnded;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isTransactional() {
		return isTransactional;
	}

	public void setTransactional(boolean isTransactional) {
		this.isTransactional = isTransactional;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccountPwd() {
		return accountPwd;
	}

	public void setAccountPwd(String accountPwd) {
		this.accountPwd = accountPwd;
	}

	 private SMSCampaignSentDao smsCampaignSentDao;
	 private SMSCampaignSentDaoForDML smsCampaignSentDaoForDML;
	 private int smsCount;
	public SMPPSampleApp(ApplicationContext context, boolean isTransactional, boolean isEnded) {
		
		this.context = context;
		this.isTransactional = isTransactional;
		this.isEnded = isEnded;
		smsCount = 0;
		if(isTransactional) {
			
			systemId = "amith";
			systemType = "amith";
			password = "amith123";
			userId = "amith.lulla@optculture.com";
			accountPwd = "amithl";
		}else {
			
			systemId = "culture";
			systemType = "culture";
			password = "opt123";
			userId = "mallika.naidu@optculture.com";
			accountPwd = "mallika";
			
		}
		
		smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
		smsCampaignSentDaoForDML = (SMSCampaignSentDaoForDML)context.getBean("smsCampaignSentDaoForDML");

	}
	
	
	public static Map<String, String> StatusMap ;
	static{
		
		if(StatusMap == null) StatusMap =  new HashMap<String, String>();
		StatusMap.put("D", "Delivered");
		StatusMap.put("E", "Expired");
		StatusMap.put("C", "Cancelled");
		StatusMap.put("S", "Pending");
		StatusMap.put("N", "Cancelled");
		StatusMap.put("X", "Cancelled");
		StatusMap.put("U", "Failed");
		
		 /**
		  * D: Delivered
			E: Expired (Out of coverage area, Switched off, Insufficient Memory, Tower station Failure, No routing info available from HLR)
			C: Cancelled (Absent Subscriber, Memory Capacity Exceeded, Mobile Equipment Error, Network Error, Barring, NDNC Failed)
			S: Pending at operator end (out of coverage area, inbox is full, handset is switched off,..)
			N: Cancelled due to DND
			X: Cancelled due to DND at operator end.
			U: Failed due to Number Portability issue.
		  */
	}
	
	private  Map<Long, String> updateStatusMap = new LinkedHashMap<Long, String>();
	
	 private class MessageListenerImpl implements MessageListener {

	        private AtomicLong sent = new AtomicLong(0);
	        private AtomicLong successfullySent = new AtomicLong(0);
	        private AtomicLong delivered = new AtomicLong(0);
	        private Session session = null;
	       
	        
	        
	        public MessageListenerImpl() {
				// TODO Auto-generated constructor stub
	        	
	        	
			}
	        public MessageListenerImpl(Session session) {
				// TODO Auto-generated constructor stub
	        	this.session = session;
	        	logger.debug("===constructor is called===");
			}
	        
	        @Override
	        public void received(Pdu pdu) {
	        	
	        	logger.debug("===received is called===");
	            if (pdu instanceof DeliverSm) {
	                delivered.incrementAndGet();
	                DeliverSm deliverSm = (DeliverSm) pdu;
	                logger.debug("DeliverSm test: {}."+ new String(deliverSm.getShortMessage()));
	                DeliverSmResp deliverSmResp = new DeliverSmResp();
	                deliverSmResp.setSequenceNumber(deliverSm.getSequenceNumber());
	                try {
	                    session.send(deliverSmResp);
	                } catch (Exception e) {
	                	logger.error("Exception ::::" , e);
	                }
	                logger.debug("Delivered total: {}."+ delivered.get());
	            } else if (pdu instanceof SubmitSmResp) {
	            	Map<Long, String> sentUpdateMap = new HashMap<Long, String>();
	                sent.incrementAndGet();
	                logger.debug("pdu.getCommandStatus() = " + pdu.getCommandStatus());
	                
	                if (0 == pdu.getCommandStatus())
	                    successfullySent.incrementAndGet();
	                
	                SubmitSmResp submitSmResp = (SubmitSmResp) pdu;
	                String messageID = submitSmResp.getMessageId();
	                Long seqNumber = submitSmResp.getSequenceNumber();
	                logger.debug(" message ID " + messageID+" seq number "+seqNumber);
	                
	                //this is require as should not lost the message ids
	                synchronized (updateStatusMap) {
	                	
	                	updateStatusMap.put(seqNumber, messageID);
	                	 logger.debug(" entered into sync block "+smsCount + "  "+isEnded +"  "+updateStatusMap.size() );
	                	if( (smsCount >= 100 || isEnded) && smsCount == updateStatusMap.size() ) {
	                		
	                		Set<Long> sentIdKeySet = updateStatusMap.keySet();
	                		String sentIdsStr = Constants.STRING_NILL;
	                		for (Long sentId : sentIdKeySet) {
	                			
	                			sentIdsStr += sentIdsStr.isEmpty() ? sentId.longValue()+sentIdsStr 
	                					: Constants.DELIMETER_COMMA+sentId.longValue();
	                			
	                		}
	                		
	                		List<SMSCampaignSent> retList = smsCampaignSentDao.findByIds(sentIdsStr);
	                		
	                		if(retList != null && retList.size() > 0 && retList.size() == sentIdKeySet.size()) {
	                			
	                			 logger.debug(" update msgids "+retList.size()  );
	                			
	                			List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
	                			
	                			for (SMSCampaignSent smsCampaignSent : retList) {
	                				
	                				smsCampaignSent.setApiMsgId(updateStatusMap.get(smsCampaignSent.getSentId()));
	                				listToBeUpdated.add(smsCampaignSent);
	                				
	                			}
	                			
	                			if(listToBeUpdated.size() > 0) {
	                				
	                				//smsCampaignSentDao.saveByCollection(listToBeUpdated);
	                				smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
	                				listToBeUpdated.clear();
	                				
	                				String msgIdStr = Constants.STRING_NILL;
	                				for (String msgId : updateStatusMap.values()) {
	                					
	                					msgIdStr += msgIdStr.isEmpty() ? msgId : Constants.DELIMETER_COMMA+msgId;
	                					
	                					
	                				}
	                				Map<String, String> returnStatusMap = getReports(msgIdStr);
	                				for (String msgId : returnStatusMap.keySet()) {
	                					
	                					for (SMSCampaignSent smsCampaignSent : retList) {
	                						
	                						if(smsCampaignSent.getApiMsgId()  == null ) continue;
	                						
	                						if(smsCampaignSent.getApiMsgId().equals(msgId)) {
	                							
	                							smsCampaignSent.setStatus(StatusMap.get(returnStatusMap.get(msgId)));
	                							listToBeUpdated.add(smsCampaignSent);
	                						}
	                						
	                					}//for
	                					
	                				}//for
	                				
	                				if(listToBeUpdated.size() > 0) {
	                					
	                					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
	                					smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);
	                					listToBeUpdated.clear();
	                					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
	                					updateStatusMap.clear();
	                					
	                				}//if
	                				
	                			}
	                			
	                		}//if
	                		
	                		
	                	}
						
					}
	                //int count = smsCampaignSentDao.updateApiMsgId(seqNumber.toString(), null, messageID);
	                
	                //logger.debug(" count " + count);
	                logger.debug("Sent to total = " + sent.get()+" and  successful sents are = "+"  "+ successfullySent.get());
	            }else if(pdu instanceof UnbindResp) {
	            	
	            	
	            	
	            }
	            else {
	            	logger.debug("No handler for {} found."+ pdu.getClass().getName());
	            }
	        }
	        
	        
	        
	        public void test() {
	    		
	   		 Session session = null;
	        try {
	        	
	   			/**
	   			* User Id: mallika.naidu@optculture.com      
	   			
	   			USERNAME/SYSTEM_TYPE: culture
	   			
	   			PASSWORD: opt12
	   			
	   			SERVER_ID: 59.162.167.36
	   			
	   			SYSTEM_ID: culture
	   			
	   			PORT: 29000
	   			 59093138665653270063
					59093138665774170169
	   			
	   			*/
	        	
	        	
	            // Prepare bind request
	            BindTransceiver bindRequest = new BindTransceiver();
	            
	            
	            
	            bindRequest.setSystemId("culture");
	            bindRequest.setPassword("opt123");
	            bindRequest.setSystemType("culture");
	            bindRequest.setAddrTon(Ton.INTERNATIONAL);
	            /*bindRequest.setSequenceNumber(sequenceNumber);
	            bindRequest.setInterfaceVersion(interfaceVersion);*/
	            bindRequest.setAddrNpi(Npi.ISDN);

	            logger.debug("PDU :: "+bindRequest.getCommandId());
	            // Create session and bind
	             session = new BasicSession(new TcpConnection(new InetSocketAddress("59.162.167.36", 29000)));
	            session.setMessageListener(new SMPPSampleApp().new MessageListenerImpl(session));
	            Pdu bindResponse = session.open(bindRequest);
	            long commandStatus = bindResponse.getCommandStatus();
	            if (CommandStatus.ESME_ROK != commandStatus)
	                throw new IOException("Bind failed with error: " + commandStatus + ".");

	            logger.debug("commans status :: CommandStatus.ESME_ROK is ::"+CommandStatus.ESME_ROK);

	            
	            // Prepare SubmitSm request
	   			SubmitSm submitSm;
	   			try {
	   				submitSm = new SubmitSm();
	   				submitSm.setSourceAddrNpi(Npi.ISDN);
	   				submitSm.setSourceAddr("TEST SMS");
	   				submitSm.setDestAddrTon(Ton.ALPHANUMERIC);
	   				submitSm.setDestAddrNpi(Npi.UNKNOWN);
	   				submitSm.setDestinationAddr("9490927928");
	   				submitSm.setRegisteredDelivery(1);
	   				submitSm.setShortMessage("Hello, how are you?".getBytes("US-ASCII"));
	   				
	   				// Set PDU sequenceNumber, which define corresponding SubmitSmResp
	   				submitSm.setSequenceNumber(session.nextSequenceNumber());
	   				session.send(submitSm);
	   				
	   				SubmitSmResp submitSmResp = new SubmitSmResp();
	   				String messageID = submitSmResp.getMessageId();
	   			} catch (Exception e) {
	   				// TODO Auto-generated catch block
	   				logger.error("Exception ::::" , e);
	   			}

	            // Send SubmitSm

	            // Wait for SMSC Delivery Receipt some time.
	            Thread.sleep(6000);
	        } catch (Exception e) {
	            logger.error("Exception ::::" , e);
	        } finally {
	            if (session != null)
	                session.close();
	        }
	    
	   	}

	        
	        
	        
	    }
	 
	 
	 
	 
	 private static Session session;
	 public void test(String content, String mobile, Long sentId) {
 		
   		 //Session session = null;
        try {
        	
   			/**
   			* User Id: mallika.naidu@optculture.com      
   			
   			USERNAME/SYSTEM_TYPE: culture
   			
   			PASSWORD: opt12
   			
   			SERVER_ID: 59.162.167.36
   			
   			SYSTEM_ID: culture
   			
   			PORT: 29000
   			
   			
   			*/
        	
        	
            // Prepare bind request
        	//bind tranceiver, receiver, transmitter everythng wrks similar
        	//this is the authentication PDU which contains the linking details with ESME.
           /* bindRequest.setSystemId("culture");
            bindRequest.setPassword("opt123");
            bindRequest.setSystemType("culture");
            bindRequest.setAddrTon(Ton.INTERNATIONAL);
            bindRequest.setAddrNpi(Npi.ISDN);*/

           /* logger.debug("PDU :: "+bindRequest.getCommandId()+" "+getSystemId()+" "+getPassword()+
            		" "+getSystemType()+" "+mobile+" "+sentId.longValue());*/
            // Create session and bind
            if(session == null) {
            	logger.debug("session is null so creating a new session"+Thread.currentThread().getName());
            	BindTransceiver bindRequest = new BindTransceiver();
            	
            	bindRequest.setSystemId(getSystemId());
            	bindRequest.setPassword(getPassword());
            	bindRequest.setSystemType(getSystemType());
            	bindRequest.setAddrTon(Ton.ALPHANUMERIC);
            	bindRequest.setAddrNpi(Npi.UNKNOWN);
            	
            	 session = new BasicSession(new TcpConnection(new InetSocketAddress("59.162.167.36", 29000)));
            	 session.setMessageListener(new SMPPSampleApp().new MessageListenerImpl(session));
	            Pdu bindResponse = session.open(bindRequest);
	            
	            long commandStatus = bindResponse.getCommandStatus();
	            
	            if (CommandStatus.ESME_ROK != commandStatus)
	                throw new IOException("Bind failed with error: " + commandStatus + ".");
	
	            logger.debug("commans status :: CommandStatus.ESME_ROK is ::"+CommandStatus.ESME_ROK);
             }

            
            // Prepare SubmitSm request
   			SubmitSm submitSm;
   			
   			try {
   				submitSm = new SubmitSm();
   				submitSm.setSourceAddrNpi(Npi.UNKNOWN);
				submitSm.setSourceAddrTon(Ton.ALPHANUMERIC);
				
				if(this.isTransactional())submitSm.setSourceAddr("OPTCLT");
			
				submitSm.setDestAddrTon(Ton.ALPHANUMERIC);
   				submitSm.setDestAddrNpi(Npi.UNKNOWN);
   				submitSm.setDestinationAddr(mobile);
   				submitSm.setRegisteredDelivery(1);
   				submitSm.setShortMessage(content.getBytes("US-ASCII"));
   				
   				// Set PDU sequenceNumber, which define corresponding SubmitSmResp
   				submitSm.setSequenceNumber(sentId);
   				session.send(submitSm);
   				smsCount ++;
   				
   				
   				
   			} catch (Exception e) {
   				// TODO Auto-generated catch block
   				logger.error("Exception ::::" , e);
   			}

            // Send SubmitSm

            // Wait for SMSC Delivery Receipt some time.
            Thread.sleep(6000);
        } catch (Exception e) {
            logger.error("Exception ::::" , e);
        } finally {
        	session.close();
        	session =null;
            /*if ((smsCount >= 100 || isEnded()) && updateStatusMap.size() == smsCount && session != null){
            	
            	//session = null;
            	smsCount = 0;
            }*/
        }
    
   	}

	 public void unbindSession() {
		 logger.debug("=====in unbindSession====="+isEnded()+"  "+updateStatusMap.size());
		 if(isEnded()) {
			  SMSCampaignSentDao smsCampaignSentDao = (SMSCampaignSentDao)context.getBean("smsCampaignSentDao");
			// Unbind ubind = new Unbind();
			 synchronized (updateStatusMap) {
             	
				 if(updateStatusMap.size() == 0) return;
             	//updateStatusMap.put(seqNumber, messageID);
             	 logger.debug(" entered into sync block "+smsCount + "  "+isEnded +"  "+updateStatusMap.size() );
             	// if( (smsCount >= 100 || isEnded) && smsCount == updateStatusMap.size() ) {
             		
             		Set<Long> sentIdKeySet = updateStatusMap.keySet();
             		String sentIdsStr = Constants.STRING_NILL;
             		for (Long sentId : sentIdKeySet) {
             			
             			sentIdsStr += sentIdsStr.isEmpty() ? sentId.longValue()+sentIdsStr 
             					: Constants.DELIMETER_COMMA+sentId.longValue();
             			
             		}
             		
             		List<SMSCampaignSent> retList = smsCampaignSentDao.findByIds(sentIdsStr);
             		
             		if(retList != null && retList.size() > 0 && retList.size() == sentIdKeySet.size()) {
             			
             			 logger.debug(" update msgids "+retList.size()  );
             			
             			List<SMSCampaignSent> listToBeUpdated = new ArrayList<SMSCampaignSent>();
             			
             			for (SMSCampaignSent smsCampaignSent : retList) {
             				
             				smsCampaignSent.setApiMsgId(updateStatusMap.get(smsCampaignSent.getSentId()));
             				listToBeUpdated.add(smsCampaignSent);
             				
             			}
             			
             			if(listToBeUpdated.size() > 0) {
             				
             				//smsCampaignSentDao.saveByCollection(listToBeUpdated);
             				smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);

             				listToBeUpdated.clear();
             				
             				String msgIdStr = Constants.STRING_NILL;
             				for (String msgId : updateStatusMap.values()) {
             					
             					msgIdStr += msgIdStr.isEmpty() ? msgId : Constants.DELIMETER_COMMA+msgId;
             					
             					
             				}
             				Map<String, String> returnStatusMap = getReports(msgIdStr);
             				for (String msgId : returnStatusMap.keySet()) {
             					
             					for (SMSCampaignSent smsCampaignSent : retList) {
             						
             						if(smsCampaignSent.getApiMsgId()  == null ) continue;
             						
             						if(smsCampaignSent.getApiMsgId().equals(msgId)) {
             							
             							smsCampaignSent.setStatus(StatusMap.get(returnStatusMap.get(msgId)));
             							listToBeUpdated.add(smsCampaignSent);
             						}
             						
             					}//for
             					
             				}//for
             				
             				if(listToBeUpdated.size() > 0) {
             					
             					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
             					smsCampaignSentDaoForDML.saveByCollection(listToBeUpdated);

             					listToBeUpdated.clear();
             					//smsCampaignSentDao.saveByCollection(listToBeUpdated);
             					updateStatusMap.clear();
             					
             				}//if
             				
             			}
             			
             		}//if
             		
             		
             	//}
					
				}
			 
			/* session.close();
			 session = null;*/
			 smsCount = 0;
			 isEnded = false;
		 }
		 
		 
		 
	 }
	 
	 
	 
	 public boolean getBalance(int totalCount) {
		 
		 
		 //http://api.mvaayoo.com/mvaayooapi/APIUtil?user=uid:pw&type=0

		 
		 try {
				/*
				 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4 
				 */
			 //Status=0,Credit balance is99989
				String postData = "";
				
				postData += "user=amith.lulla@optculture.com:amithl&type=0";
				//postData += "user="+getUserId()+":"+getAccountPwd()+"&type=0";
				
				URL url = new URL("http://api.mvaayoo.com/mvaayooapi/APIUtil");
				
				HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
				
				urlconnection.setRequestMethod("POST");
				urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlconnection.setDoOutput(true);

				OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
				out.write(postData);
				out.flush();
				out.close();
				
				BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
				
				String decodedString;
				String response = "";
				while ((decodedString = in.readLine()) != null) {
					response += decodedString;
				}
				in.close();
				logger.debug("balence response is======>"+response);
				
				if(response == null) {
					
					//logger.error("exception while fetching balence");
					return false;
					
				}//if
				
				String creditsStr = response.replace("Status=0,Credit balance is", "");
				try {
					
					int credits = Integer.parseInt(creditsStr);
					if(credits <= totalCount) {
						
						EmailQueueDao emailQueueDao = (EmailQueueDao)context.getBean("emailQueueDao");
						EmailQueueDaoForDML emailQueueDaoForDML = (EmailQueueDaoForDML)context.getBean("emailQueueDaoForDML");
							
						try {
							
							String message = PropertyUtil.getPropertyValueFromDB(Constants.SMS_LOW_CREDITS_WARN_TEXT);
							String emailId = PropertyUtil.getPropertyValueFromDB("SupportEmailId");
							
						 	EmailQueue emailQueue = new EmailQueue("Ran out of SMS Credits", message, 
						 			Constants.EQ_TYPE_LOW_SMS_CREDITS, "Active", emailId, new Date());
						 	
						 	//emailQueueDao.saveOrUpdate(emailQueue);
						 	emailQueueDaoForDML.saveOrUpdate(emailQueue);
					 	} catch(Exception e) {
					 		//logger.error("exception while saving email queue object");
					 		return false;
					 	}
						
							
						
						return false;
						
					}
					
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					
					
					logger.error("Exception ::::" , e);
					return false;
				}
				
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				return false;
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
				return false;
			}catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::::" , e);
				return false;
			}
			
		 
		 
		 return true;
		 
		 
		 
		 
	 
		 
		 
	 }
	 
	 
	 
	 public Map<String, String> getReports(String msgIdStr) {
		 
			Map<String, String> responseStatusCodeMap = new LinkedHashMap<String, String>();
		 try {
				/*
				 * http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=amith.lulla@optculture.com:amithl&senderID=OPTCLT&receipientno=9052346000&dcs=0&msgtxt=This is Test message&state=4
				 * report response is======>Status=0,oa=56263,da=919490927928,report=S,sdate=2013-12-06 12:03:24,rdate=2013-12-06 12:03:26,tid=59071138631160420375,msg=Thanks%2Bfor%2Bshopping%2Bwith%2BTresmode-promotion 
				 */
			 
			 //59071138659470666581
			 //59071138659474066595
			 
			 /**
			  * D: Delivered
				E: Expired (Out of coverage area, Switched off, Insufficient Memory, Tower station Failure, No routing info available from HLR)
				C: Cancelled (Absent Subscriber, Memory Capacity Exceeded, Mobile Equipment Error, Network Error, Barring, NDNC Failed)
				S: Pending at operator end (out of coverage area, inbox is full, handset is switched off,..)
				N: Cancelled due to DND
				X: Cancelled due to DND at operator end.
				U: Failed due to Number Portability issue.
			  */
				
			 	
			 
				String postData = "";
				
				
				String[] msgIdsArr = msgIdStr.split(Constants.DELIMETER_COMMA);
				
				boolean isAddComma = false;
				boolean isConnected = false;
				
			
				
				
				for (int i = 0; i < msgIdsArr.length;i++) {
					isConnected = false;
					if(i == 0) {
						
						//postData = "user="+userId+":"+accountPwd+"&tid=";
						
						postData = "user=amith.lulla@optculture.com:amithl&tid=";
						//postData = "user=mallika.naidu@optculture.com:mallika&tid=";
						isAddComma = false;
					}
					if(isAddComma) postData += Constants.DELIMETER_COMMA;
					
					
					if(postData.length() + msgIdsArr[i].length() <= 150) {
						responseStatusCodeMap.put(msgIdsArr[i], null);
						postData += msgIdsArr[i];
						isAddComma = true;
					}else{
						
						try{
							isConnected = true;
							logger.debug("before sending post data is===="+postData);
							
							URL url = new URL("http://api.mvaayoo.com/apidlvr/APIDlvReport");
							
							HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
							
							urlconnection.setRequestMethod("POST");
							urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
							urlconnection.setDoOutput(true);

							OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
							out.write(postData);
							out.flush();
							out.close();
							
							BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
							
							String decodedString;
							String response = "";
							while ((decodedString = in.readLine()) != null) {
								response += decodedString;
							}
							
							in.close();
							logger.debug("report response is======>"+response);
							
							if(response != null) {
								
								
								String status = Constants.STRING_NILL;;
								String tid = Constants.STRING_NILL;
								
								String[] OATokenArr = response.split("oa=");
								for (String oaToken : OATokenArr) {
									
									
									String[] responseTokenArr = oaToken.split(Constants.DELIMETER_COMMA);
									for (String token : responseTokenArr) {
										
										if(token.startsWith("report")){
											
											status = token.split("=")[1];
											
										}else if(token.startsWith("tid") ){
											
											tid = token.split("=")[1];
										}
										
										if(!status.isEmpty() && tid.isEmpty() && responseStatusCodeMap.containsKey(tid)) {
											
											responseStatusCodeMap.put(tid, status);
											
										}
										
									}//for
									
									
								}
								
								
							}//if
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						} catch (ProtocolException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::::" , e);
						}
						
						//postData = "user="+getUserId()+":"+getAccountPwd()+"&tid="+msgIdsArr[i];
						postData = "user=amith.lulla@optculture.com:amithl&tid="+msgIdsArr[i];
						isAddComma = true;
						
					}//else
					
				}//for
				if(!isConnected) {
					try{
						isConnected = true;
						logger.debug("before sending post data is===="+postData);
						
						URL url = new URL("http://api.mvaayoo.com/apidlvr/APIDlvReport");
						
						HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
						
						urlconnection.setRequestMethod("POST");
						urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
						urlconnection.setDoOutput(true);

						OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
						out.write(postData);
						out.flush();
						out.close();
						
						BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
						
						String decodedString;
						String response = "";
						while ((decodedString = in.readLine()) != null) {
							response += decodedString;
						}
						
						in.close();
						logger.debug("report response is======>"+response);
						
						if(response != null) {
							
							
							
							String status = Constants.STRING_NILL;;
							String tid = Constants.STRING_NILL;
							
							String[] OATokenArr = response.split("oa=");
							for (String oaToken : OATokenArr) {
								
								
								String[] responseTokenArr = oaToken.split(Constants.DELIMETER_COMMA);
								for (String token : responseTokenArr) {
									
									if(token.startsWith("report")){
										
										status = token.split("=")[1];
										
									}else if(token.startsWith("tid") ){
										
										tid = token.split("=")[1];
									}
									
									if(!status.isEmpty() && tid.isEmpty() && responseStatusCodeMap.containsKey(tid)) {
										
										responseStatusCodeMap.put(tid, status);
										
									}
									
								}//for
								
								
							}
							
							
						}//if
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					} catch (ProtocolException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ::::" , e);
					}
					
					//postData = "user="+getUserId()+":"+getAccountPwd()+"&tid=";
					postData = "user=amith.lulla@optculture.com:amithl&tid=";
					isAddComma = false;
					
					
					
					
				}
				
		 }catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception ::::" , e);
			}
				
				
				//59071138631160420375
				//postData += "user=amith.lulla@optculture.com:amithl&tid=59093138622453710607 ";
				//postData += "user=mallika.naidu@optculture.com:mallika&tid=59071138631160420375 ";
				
				
			return responseStatusCodeMap;
		 
		 
		 
		 
		 
		 
		 
	 }
	 
	
	public static void main(String[] args) {
		
		 Session session = null;
     try {
    	// 59093138717156102605, 59093138717156702606
			/**
			* User Id: mallika.naidu@optculture.com      
			
			USERNAME/SYSTEM_TYPE: culture
			
			PASSWORD: opt12
			
			SERVER_ID: 59.162.167.36
			
			SYSTEM_ID: culture
			
			PORT: 29000
			
			
			*/
     	
    	 SMPPSampleApp sampleApp = new SMPPSampleApp();
    	// 59093138683960335233, 59093138683961035235, 59093138673680125092
    	// sampleApp.getReports("59093138622453710607"+","+"59071138631160420375");//Status=0,oa=OPTCLT,da=919052221449,report=D,sdate=2013-12-05 11:52:17,rdate=2013-12-05 11:52:26,tid=59093138622453710607,msg=Thanks%2Bfor%2Bshopping%2Bwith%2BTresmode
    	 sampleApp.getReports("59093138717156102605"+","+"59093138673680125092");
    	 //sampleApp.getBalance(0);
    	 if(true) return;
     	String[] arr = {"9490927928","9492343434"};
    	 
    	for (int i = 0; i < arr.length; i++) {
			
    		
    		BindTransmitter bindRequest = new BindTransmitter();
    		
    		
    		/*bindRequest.setSystemId("culture");
         bindRequest.setPassword("opt123");
         bindRequest.setSystemType("culture");*/
    		bindRequest.setSystemId("amith");
    		bindRequest.setPassword("amith123");
    		bindRequest.setSystemType("amith");
    		bindRequest.setAddrTon(Ton.ALPHANUMERIC);
    		bindRequest.setAddrNpi(Npi.UNKNOWN);
    		
    		
    		
    		logger.debug("PDU :: "+bindRequest.getCommandId());
    		// Create session and bind
    		session = new BasicSession(new TcpConnection(new InetSocketAddress("59.162.167.36", 29000)));
    		session.setMessageListener(new SMPPSampleApp().new MessageListenerImpl(session));
    		Pdu bindResponse = session.open(bindRequest);
    		long commandStatus = bindResponse.getCommandStatus();
    		if (CommandStatus.ESME_ROK != commandStatus)
    			throw new IOException("Bind failed with error: " + commandStatus + ".");
    		
    		if(commandStatus == CommandStatus.ESME_RALYBND) {
    			
    			Unbind unBind = new Unbind();
    			session.open(unBind);
    			
    			//State.CONNECTED.equals(session.
    		}
    		
    		logger.debug("commans status :: CommandStatus.ESME_ROK is ::"+CommandStatus.ESME_ROK);
    		// Prepare SubmitSm request
			SubmitSm submitSm;
			try {
				submitSm = new SubmitSm();
				
				submitSm.setSourceAddrNpi(Npi.UNKNOWN);
				submitSm.setSourceAddrTon(Ton.ALPHANUMERIC);
				submitSm.setSourceAddr("OPTCLT");
				submitSm.setDestAddrTon(Ton.ALPHANUMERIC);
   				submitSm.setDestAddrNpi(Npi.UNKNOWN);
				submitSm.setDestinationAddr("9490927928");
				submitSm.setRegisteredDelivery(1);
				submitSm.setShortMessage("Thanks for shopping with Tresmode".getBytes("US-ASCII"));
				
				// Set PDU sequenceNumber, which define corresponding SubmitSmResp
				submitSm.setSequenceNumber(session.nextSequenceNumber());
				session.send(submitSm);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::" , e);
			}
			// Wait for SMSC Delivery Receipt some time.
			Thread.sleep(6000);
			
			session.close();
			
		}
         // Prepare bind request
/**
 * DeliverSm test: {}.id:59071138615259880630 sub:001 dlvrd:000 submit date:1312041553 done date:1312041553 stat:UNDELIV err:005 text:
Delivered total: {}.1

 */
         
         

         // Send SubmitSm

     } catch (Exception e) {
         logger.error("Exception ::::" , e);
     } finally {
         if (session != null)
             session.close();
     }
 
	}

	
	
}

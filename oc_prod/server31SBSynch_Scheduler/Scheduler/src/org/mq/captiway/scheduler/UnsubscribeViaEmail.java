package org.mq.captiway.scheduler;

import org.hibernate.*;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags;

@SuppressWarnings({"unused"})
public class UnsubscribeViaEmail extends TimerTask {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

    private CampaignsDao campaignsDao;
    public void setCampaignsDao(CampaignsDao campaignsDao) {
        this.campaignsDao = campaignsDao;
    } 

    public CampaignsDao getCampaignsDao() {
        return this.campaignsDao;
    }
    private SessionFactory sessionFactory;
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    } 

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    private CampaignSentDao campaignSentDao;
    public void setCampaignSentDao(CampaignSentDao campaignSentDao) {
        this.campaignSentDao = campaignSentDao;
    } 

    public CampaignSentDao getCampaignSentDao() {
        return this.campaignSentDao;
    }


	private ContactsDao contactsDao;
    public void setContactsDao(ContactsDao contactsDao) {
        this.contactsDao = contactsDao;
    } 

    public ContactsDao getContactsDao() {
        return this.contactsDao;
    }

    public void run() {
        //if(logger.isDebugEnabled()) logger.debug("UnsubscribeViaEmail Just Entered");  
        if(logger.isDebugEnabled()) logger.debug("--Entered and Executing the run method--");  
        try {
            Properties props = new Properties();

            String host = PropertyUtil.getPropertyValue("SMTPHost").trim();
            // String username = "bounce2@retailmarketer.mqtransact.com";
            // String password = "b0unce2";
            String username = PropertyUtil.getPropertyValue("UnsubMailBoxUsername").trim();
            String password = PropertyUtil.getPropertyValue("UnsubMailBoxPassword").trim();
            String provider = "pop3";
			int userId;
			Query query;
			org.hibernate.classic.Session hibSession = sessionFactory.openSession();
			Transaction tx= hibSession.beginTransaction();
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore(provider);
			if(logger.isDebugEnabled()) logger.debug("-before connecting to the inbox");
            store.connect(host, username, password);
			if(logger.isDebugEnabled()) logger.debug("UnsubscribeViaEmail--connected to the inbox");

            Folder inbox = store.getFolder("INBOX");

            if (inbox == null) {
                if(logger.isDebugEnabled()) logger.debug("No INBOX");
                return;
            }
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();
			if(logger.isInfoEnabled()) logger.info("---No of messages in the Unsubscribe mail box --"+messages.length);
            for (int i = 0; i < messages.length; i++) {
				if(logger.isInfoEnabled()) logger.info("----message number ---"+i);
                String[] headerArray = messages[i].getHeader("To"); 
                String[] addrArray = messages[i].getHeader("From"); 

                if(logger.isDebugEnabled()) logger.debug("Message Number. : " + i + " To: " + headerArray[0]);  
                if(logger.isDebugEnabled()) logger.debug("Message Number. : " + i + " From: " + addrArray[0]);  
				String to = headerArray[0];   // "mailto:unsub-"+sendId+"-"+user.getUserId()+"@retailmarketer.mqtransact.com" 
				if(logger.isInfoEnabled()) logger.info("--headerArray[0]--"+headerArray[0]);
                /*String header = to.substring(0,to.indexOf("@")); 
				logger.debug("Header --"+header);
				if(header.equals("unsub")){
					//String sendId = to.substring(to.indexOf("-")+1,to.lastIndexOf("-"));
					//userId = Integer.parseInt(headerArray[0].substring(to.lastIndexOf("-"), to.indexOf("@"))); 
					//logger.debug("User Id : "+userId);
				    try {
						/*campaignSentDao.setCampaignSentStatus(Long.parseLong(sendId),"unsubscribe");
						CampaignSent campaignSent = campaignSentDao.find(Long.parseLong(sendId));
						query = hibSession.createQuery("from MailingList where users.userId='" + userId + "'");
						List mList	= query.list();
						logger.info("--Lists--" + mList);
						for(Object obj:mList){
							MailingList mlObj = (MailingList)obj;
							query = hibSession.createQuery("update EmailId set emailStatus = 'unsubscribed', lastStatusChange = CURRENT_TIMESTAMP() where mailingList.listId = " + mlObj.getListId() + " and email='"+ addrArray[i]+ "'");
							int res = query.executeUpdate();
						}
						//query = session.createQuery("update CampaignSent set status = 'unsubscribed' where sentId = '" + sentId + "'");
						//int res = query.executeUpdate();
						//result = "success";
						tx.commit();
						/*EmailId emailId = campaignSent.getEmailId();
						emailId.setEmailStatus("unsubscribe");
						contactsDao.saveOrUpdate(emailId);
					} catch (Exception e) {
						tx.rollback();
						//logger.error("Unable to update the status for sendID: " + sendId+ " in DB");
					}
					messages[i].setFlag(Flags.Flag.DELETED, true);
					hibSession.close();
					// messages[i].writeTo(System.out);
				}*/
					messages[i].setFlag(Flags.Flag.DELETED, true);
					//hibSession.close();
					messages[i].writeTo(System.out);		
            }
            // inbox. expunge();
			if(logger.isDebugEnabled()) logger.debug("--before closing inbox---");
            inbox.close(true);
			if(logger.isDebugEnabled()) logger.debug("--after closing inbox---");
            store.close();
			if(logger.isDebugEnabled()) logger.debug("--after closing store---");
			if(logger.isDebugEnabled()) logger.debug("exiting run if there is no exception");
			
          
        } catch (Exception e) {
            if(logger.isErrorEnabled()) logger.error("** Exception is "+e.getMessage()+" **");  
        }

    }
}



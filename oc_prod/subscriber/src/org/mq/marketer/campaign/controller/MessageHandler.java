package org.mq.marketer.campaign.controller;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Messages;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zkplus.spring.SpringUtil;

@SuppressWarnings({ "unchecked"})
public class MessageHandler{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public Messages messages = null;
	public MessagesDao messagesDao = null;
	public MessagesDaoForDML messagesDaoForDML = null;
	String userName = null;
	public MessageHandler(){
	  this.messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
	  this.messagesDaoForDML  = (MessagesDaoForDML )SpringUtil.getBean("messagesDaoForDML");
	  this.userName = GetUser.getUserObj().getUserName();
	}
	public MessageHandler(MessagesDao messagesDao,String userName){
		this.messagesDao = messagesDao;
		this.userName = userName;
	}
	public List getMessages(String type){
		TimeZone tz = (TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		List<Messages> msgList = null;
		try{
		if(userName!=null && messagesDao!=null){
			if(type.equalsIgnoreCase("trash")){
				msgList = messagesDao.findAllByFolder(userName,type);
			}else{
				msgList = messagesDao.findAllByUser(userName);
			}
			Calendar cal;
			for (Messages msg : msgList) {
				cal = new MyCalendar(msg.getCreatedDate(),tz);
				msg.setCreatedDate(cal);
			}
		}
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}
		return msgList;
	}
	
	public List getMessages(String type,int startIndex, int endIndex){
		TimeZone tz = (TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		List<Messages> msgList = null;
		try{
		if(userName!=null && messagesDao!=null){
			if(type.equalsIgnoreCase("trash")){
				msgList = messagesDao.findAllByFolder(userName,type, startIndex, endIndex);
			}else{
				msgList = messagesDao.findAllByUser(userName, startIndex, endIndex);
			}
			Calendar cal;
			for (Messages msg : msgList) {
				cal = new MyCalendar(msg.getCreatedDate(),tz);
				msg.setCreatedDate(cal);
			}
		}
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}
		return msgList;
	}
	
	
	
	
	
	public void setFolder(Messages msgs,String folder){
		try{
			msgs.setFolder(folder);
			//messagesDao.saveOrUpdate(msgs);
			messagesDaoForDML.saveOrUpdate(msgs);
			logger.debug("Message folder is modifed to: " + folder);
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}
	}
	public void markAsRead(Messages msgs){
		try{
			msgs.setRead(true);
			//messagesDao.saveOrUpdate(msgs);
			messagesDaoForDML.saveOrUpdate(msgs);
			logger.debug("Message folder is modifed to: Read");
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}	
	}
	public void deleteMessage(Messages msgs){
		try{
			//messagesDao.delete(msgs);
			messagesDaoForDML.delete(msgs);
			logger.debug("Selected msg is deleted ");
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}
	}
	
	public int getNewMsgsCount(Long userId){
		try{
			return messagesDao.findNewCount(userId);
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
			return 0;
		}
	}
	
	public void sendMessage(String module,String subject,String message, String folder,boolean read, String type, Users users){
		try{
		 
			Messages msgs = new Messages(module, subject, message, Calendar.getInstance(), folder, read, type, users);
			//messagesDao.saveOrUpdate(msgs);
			messagesDaoForDML.saveOrUpdate(msgs);
		}catch(Exception e){
			logger.error("** Exception : " + e + " **");
		}		
	}
	

}

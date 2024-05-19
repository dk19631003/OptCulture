package org.mq.captiway.scheduler.services;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.EmailQueueScheduler;
import org.mq.captiway.scheduler.SimpleMailSender;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
public class SimpleMailController extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private EmailQueueDao emailQueueDao;
//	private SimpleMailSender mailSender;
	
	private  Queue<EmailQueue> queue = new LinkedList<EmailQueue>();
	 
	public EmailQueueDao getEmailQueueDao() {
		return emailQueueDao;
	}
	public void setEmailQueueDao(EmailQueueDao emailQueueDao) {
		this.emailQueueDao = emailQueueDao;
	}
	EmailQueueScheduler emailQueueScheduler;
	
	public EmailQueueScheduler getEmailQueueScheduler() {
		return emailQueueScheduler;
	}
	/*public void setMailSender(SimpleMailSender mailSender) {
		this.mailSender = mailSender;
	}
	public void setQueue(Queue<EmailQueue> queue) {
		this.queue = queue;
	}*/
	public void setEmailQueueScheduler(EmailQueueScheduler emailQueueScheduler) {
		this.emailQueueScheduler = emailQueueScheduler;
	}
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
		
		PrintWriter pr = response.getWriter();
		try{
			if(logger.isDebugEnabled()) logger.debug("----just entered---SimpleMailController");
			
			/*Thread t1 = new Thread(emailQueueScheduler);
			t1.start();*/
			if(!emailQueueScheduler.isRunning()) {
				emailQueueScheduler.run();
			}
			
		
			/*List<EmailQueue> emailQueueList = emailQueueDao.findByTypeAndStatus(Constants.EQ_STATUS_ALL,
					Constants.EQ_STATUS_ACTIVE);
			
			if(emailQueueList == null || emailQueueList.size()  <= 0){
				logger.debug("no data exists..");
				pr.println("No data exists dd ...");
				return null;
			}
			
			logger.debug(" Active Count in EmailQueue : "+emailQueueList.size());
			
			
	//	ExternalSMTPSender externalSMTPSender =  new ExternalSMTPSender(context);
			
		
			for(EmailQueue emailQueue : emailQueueList ) {
				
				emailQueue.setStatus(Constants.EQ_STATUS_SENT);
				
			}
			
			try {
				
				emailQueueDao.saveByCollection(emailQueueList);
				
				}
			
			catch (Exception e) {
				logger.error(" ** Exception : while updating the status of EmailQueue" +
						" after scheduling", e);
			}
			
			queue.addAll(emailQueueList);
			
			if((mailSender == null || mailSender.isRunning == false) && emailQueueList.size() > 0) {
				mailSender = new SimpleMailSender(queue);
				mailSender.start();
			}
			pr.println("done...");
		} catch(Exception e) {
			logger.error(" ** Exception  : Root ", e );
		} finally {
			pr.flush();
			pr.close();
		}
		System.gc();
		*/
		
		}
		catch(Exception e) {
			logger.error(" ** Exception  : Root ", e );
		}
		return null;
	}
	
	
	
	
}

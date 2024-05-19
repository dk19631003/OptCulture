package org.mq.captiway.scheduler.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.SalesUpdateErrorLogMonitor;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
/**
 * 
 * @author proumyaa
 *Temp sol to update relational mappings when the deadlock occured
 */
public class PingSalesUpdateErrorLogMoniotor extends AbstractController {
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest arg0,
			HttpServletResponse arg1) throws Exception {
		
		try {
			SalesUpdateErrorLogMonitor salesUpdateErrorLogMonitor = (SalesUpdateErrorLogMonitor)ServiceLocator.getInstance().
					getBeanByName("salesUpdateErrorMonitor");
			if(!salesUpdateErrorLogMonitor.isRunning()){
				
				salesUpdateErrorLogMonitor.run();
			}
		} catch (Exception e) {
			
			logger.error("Exception ", e);
		}
		return null;
	}
}

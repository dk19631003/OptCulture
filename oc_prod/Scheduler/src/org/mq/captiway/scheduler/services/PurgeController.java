package org.mq.captiway.scheduler.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class PurgeController extends AbstractController {
	private PurgeScheduler purgeScheduler;


	public PurgeScheduler getPurgeScheduler() {
		return purgeScheduler;
	}


	public void setPurgeScheduler(PurgeScheduler purgeScheduler) {
		this.purgeScheduler = purgeScheduler;
	}


	@Override
	protected ModelAndView handleRequestInternal(
			HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse) throws Exception {
		try{
		if(!purgeScheduler.isRunning()){
			purgeScheduler.run();
		}
	}catch (Exception e) {
		logger.info("Exception :: ",e);
	}
		return null;
		}

}

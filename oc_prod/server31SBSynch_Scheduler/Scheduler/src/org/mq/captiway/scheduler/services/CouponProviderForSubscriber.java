package org.mq.captiway.scheduler.services;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.CouponProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CouponProviderForSubscriber extends AbstractController {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private CouponProvider couponProvider;
	/*private ApplicationContext context;
	
	public ApplicationContext getContext() {
		return context;
	}
	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	*/
	
	private CouponCodesDao couponCodesDao;
	
	
	public CouponCodesDao getCouponCodesDao() {
		return couponCodesDao;
	}


	public void setCouponCodesDao(CouponCodesDao couponCodesDao) {
		this.couponCodesDao = couponCodesDao;
	}


	protected ModelAndView handleRequestInternal(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		String cfStr = request.getParameter("cfStr");
		String issuedTo = request.getParameter("issuedTo");
		String type =request.getParameter("type");
		
		if(cfStr == null || cfStr.trim().isEmpty()) {
			
			if(logger.isInfoEnabled()) logger.info("required param is null...");
			return null;
			
		}//if
		
		if(couponProvider == null) { //get coupon provider
			
			
			couponProvider = CouponProvider.getCouponProviderInstance(null, couponCodesDao);
			if(couponProvider == null) {
				if(logger.isInfoEnabled()) logger.info("No Coup provider found....");
				return null;
			}
			
		}//if
		
		if(couponProvider.couponSet != null ) {
			
			if(!couponProvider.couponSet.contains(cfStr)) {
				
				couponProvider.couponSet.add(cfStr);
			}
			
		}
		String campaignType = Constants.COUP_GENT_CAMPAIGN_TYPE_SMS;
		if(type != null ){
			campaignType = type;
		}
		
		logger.info("issued to  from Scheduler is::::"+issuedTo);
		String value = couponProvider.getNextCouponCode(cfStr, null, campaignType, issuedTo, null, null,null);
		
		if(value == null) value = Constants.STRING_NILL;
		
		PrintWriter pw = response.getWriter();
		pw.println(value);
		pw.flush();
		pw.close();
		
		return null;
		
		
		
		
		
		
		
		
		
	};
}

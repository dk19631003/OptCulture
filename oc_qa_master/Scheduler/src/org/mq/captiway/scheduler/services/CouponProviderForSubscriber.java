package org.mq.captiway.scheduler.services;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.ReferralcodesIssued;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.ReferralcodesIssuedDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.CouponProvider;
import org.mq.captiway.scheduler.utility.ReferralcodeProvider;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CouponProviderForSubscriber extends AbstractController {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private CouponProvider couponProvider;
	private ReferralcodeProvider referralcodeProvider;

	private ApplicationContext context;
	
	public ApplicationContext getContext() {
		return context;
	}
	public void setContext(ApplicationContext context) {
		this.context = context;
	}
	
	
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
		String cid = request.getParameter("cid");
		Long CID = null;
		if(cid != null && !cid.isEmpty()) {
			try {
				CID = Long.parseLong(cid);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
		}
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
		String value = Constants.STRING_NILL;
		
		if(cfStr.startsWith("CC_")){
		
			if(CID != null) {
				value = couponProvider.getAlreadyIssuedCoupon(cfStr, CID);
			}
			if(value == null || value.isEmpty())value = couponProvider.getNextCouponCode(cfStr, null, campaignType, issuedTo, null, null,CID,true);
			logger.info("issued to  from Scheduler is::::"+issuedTo);
		
			if(value == null) value = Constants.STRING_NILL;
		
		} else if (cfStr.startsWith("REF_CC_")) {
			
			logger.info("entering into referral flow of couponprovider");
			
			ReferralcodesIssuedDao refcodesissued=null;
			List<ReferralcodesIssued> refissuedobj=null;
			try {
				refcodesissued = (ReferralcodesIssuedDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(CID!=null) {
			 refissuedobj=refcodesissued.getRefcodebycontactid(CID);
			}
			
			if(refissuedobj!=null ) {
				
				logger.info("refissuedobj not equal to null "+refissuedobj);

				value = refissuedobj.get(0).getRefcode();
				
			}else {
			
			if(referralcodeProvider == null) { //get coupon provider
	                                                                                                                                        		
				referralcodeProvider = referralcodeProvider.getReferralcodeProviderInstance(context, null);
				if(referralcodeProvider == null) {
					if(logger.isInfoEnabled()) logger.info("No Referral provider found....");{
						return null;

					}
				}//if

			if(referralcodeProvider.couponSet != null ) {	
				if(!referralcodeProvider.couponSet.contains(cfStr)) {

					referralcodeProvider.couponSet.add(cfStr);
				}}
				
				value = referralcodeProvider.getNextCouponCode(cfStr,"",type, issuedTo,null, null,CID,true);
			
			}
			logger.info("Returned Referral value"+value);

			if(logger.isDebugEnabled()) logger.debug(">>>>>>>>> Referral-code "+ value);

			if(value == null) value = Constants.STRING_NILL;
			
			}
		
		}
			
		PrintWriter pw = response.getWriter();
		pw.println(value);
		pw.flush();
		pw.close();
		
		return null;
		
		
		
		
		
		
		
		
		
	};
	}

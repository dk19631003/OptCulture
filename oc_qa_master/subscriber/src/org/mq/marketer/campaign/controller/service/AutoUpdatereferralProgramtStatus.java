package org.mq.marketer.campaign.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.dao.ReferralProgramDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.EventsDao;
import org.mq.optculture.data.dao.EventsDaoForDML;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.utils.OCConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AutoUpdatereferralProgramtStatus extends TimerTask implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	ReferralProgramDao referralprogramDao;
	ReferralProgramDaoForDML referralprogramDaoForDML;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			referralprogramDao = (	ReferralProgramDao) context.getBean(OCConstants.REFERRAL_PROGRAM_DAO);
			referralprogramDaoForDML = (ReferralProgramDaoForDML) context.getBean(OCConstants.REFERRAL_PROGRAM_DAO_FOR_DML);
			int totalrefprgmCount = referralprogramDao.getAllRefprgrmCount();
		
			logger.info("totalEventsCount is :: "+totalrefprgmCount);
			if( totalrefprgmCount == 0){
				logger.info(">>> No  Events exists for Upadting the Status");
				return;
			}
			int i = 0;
			while(i <= totalrefprgmCount) {
				int k =100;
				autoUpdateReferralProgramStatus(i, k);
				
				if(i == 0) i = k;	else  i= i+k;
				
				
			}
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
		}
	}

	private void autoUpdateReferralProgramStatus(int i, int k) {
		// TODO Auto-generated method stub
		List<ReferralProgram> list =  referralprogramDao.getAllRefPrgms(i,k);
		if(list == null || list.size() == 0 ) {
			logger.info(">>> No  Events exists for Upadting the Status");
			return;
		}
		List<ReferralProgram> modifyRefPrgmStausLst = new ArrayList<ReferralProgram>();
		Calendar currntDateCal = Calendar.getInstance();
		
		for(ReferralProgram refprgmObj : list) {
			if(refprgmObj.getStatus().equals(Constants.EVENTS_STATUS_ACTIVE)) {			// CheckIf Event Status Active
				
				if(currntDateCal.after(refprgmObj.getStartDate()) && currntDateCal.before(refprgmObj.getEndDate())) {
					
					refprgmObj.setStatus(Constants.EVENTS_STATUS_RUNNING);
					modifyRefPrgmStausLst.add(refprgmObj);
				}
				else if(currntDateCal.after(refprgmObj.getEndDate())) {
					
					refprgmObj.setStatus(Constants.EVENTS_STATUS_EXPIRED);
					modifyRefPrgmStausLst.add(refprgmObj);
				}
				
				
			}
			else  if(refprgmObj.getStatus().equals(Constants.EVENTS_STATUS_RUNNING)) {	// CheckIf Event Status Running
				
				if(currntDateCal.before(refprgmObj.getStartDate())) {
					
					refprgmObj.setStatus(Constants.EVENTS_STATUS_ACTIVE);
					modifyRefPrgmStausLst.add(refprgmObj);
				}
				else if(currntDateCal.after(refprgmObj.getEndDate())) {
					
					refprgmObj.setStatus(Constants.EVENTS_STATUS_EXPIRED);
					modifyRefPrgmStausLst.add(refprgmObj);
				}
				
			} else  if(refprgmObj.getStatus().equals(Constants.EVENTS_STATUS_SUSPENDED)) {	// CheckIf Event Status Suspende
				
				if(currntDateCal.after(refprgmObj.getEndDate())) {

					refprgmObj.setStatus(Constants.EVENTS_STATUS_EXPIRED);
					modifyRefPrgmStausLst.add(refprgmObj);
				}
				
				
			} else if(refprgmObj.getStatus().equals(Constants.EVENTS_STATUS_EXPIRED)) {	// CheckIf Event Status Expired
				
				if(currntDateCal.before(refprgmObj.getStartDate()) ) {
					
					refprgmObj.setStatus(Constants.EVENTS_STATUS_ACTIVE);
					modifyRefPrgmStausLst.add(refprgmObj);
				}
				else if(currntDateCal.before(refprgmObj.getEndDate())) {
					
					refprgmObj.setStatus(Constants.EVENTS_STATUS_RUNNING);
					modifyRefPrgmStausLst.add(refprgmObj);
				}
				
			}
			
			
			if(modifyRefPrgmStausLst.size() == 100) {
				logger.info("for Every 100 coup Obj status updated");
				//couponsDao.saveByCollection(modifyCoupStausLst);
				((AbstractSpringDaoForDML) modifyRefPrgmStausLst).saveByCollection(modifyRefPrgmStausLst);

				modifyRefPrgmStausLst.clear();
			}
			
			
		} // for
		
		
		if(modifyRefPrgmStausLst.size() > 0) {
			logger.info(" finally :: "+modifyRefPrgmStausLst.size()+" ::   Promo-code Obj status updated");
			//couponsDao.saveByCollection(modifyCoupStausLst);
			referralprogramDaoForDML.saveByCollection(modifyRefPrgmStausLst);
			modifyRefPrgmStausLst.clear();
		}

			
		}

	}
	
	
	



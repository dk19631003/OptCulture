package org.mq.marketer.campaign.controller.report;




import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaignSent;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SMSUndeliveredReportController extends GenericForwardComposer  {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Listbox undeliveredCategoryLbId,undeliveredRepLbId;
	private SMSCampaignSentDao smsCampaignSentDao;
	private SMSCampaignReport smsCampaignReport;
	private Session sessionScope;
	private MyRendererListener rendererListener = new MyRendererListener();
	
	public SMSUndeliveredReportController(){
		smsCampaignSentDao = (SMSCampaignSentDao)SpringUtil.getBean("smsCampaignSentDao");
		sessionScope = Sessions.getCurrent();
	}
	
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			
			smsCampaignReport = (SMSCampaignReport)sessionScope.getAttribute("smsCampaignReport");
			undeliveredCategoryLbId.setModel(new ListModelList(getUndeliveredReason()));
			
			
			undeliveredCategoryLbId.setSelectedIndex(0);
			undeliveredRepLbId.setModel(new ListModelList(getUndeliveredReports()));
			undeliveredRepLbId.setItemRenderer(rendererListener);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public List<String> getUndeliveredReason() {
		
		List<String> reasons = null;
		try {
			
			reasons = smsCampaignSentDao.getUndeliveredResonsByCampReportId(smsCampaignReport.getSmsCrId());
			
				return reasons;
			
			
			
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	public List<SMSCampaignSent> getUndeliveredReports() {
		List<SMSCampaignSent> smsCampSentList = null;
		String status="";
		try {
			logger.info("status is===>"+ undeliveredCategoryLbId.getSelectedItem().getLabel());
			status = (String)((ListModelList)undeliveredCategoryLbId.getModel()).getElementAt(0);
			smsCampSentList = smsCampaignSentDao.getCampSentByUndeliveredCategory(smsCampaignReport.getSmsCrId(),status);
			
			return smsCampSentList;
			
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
		
		
	}
	
	
	public void onClick$undeliveredReasonBtnId() {
		List<SMSCampaignSent> smsCampSentList = null;
		
		String status = (String)undeliveredCategoryLbId.getSelectedItem().getValue();
		
		smsCampSentList = smsCampaignSentDao.getCampSentByUndeliveredCategory(smsCampaignReport.getSmsCrId(),status);
		
		undeliveredRepLbId.setModel(new ListModelList(smsCampSentList));
		undeliveredRepLbId.setItemRenderer(rendererListener);
		
		
	}
	
	
	public class MyRendererListener implements ListitemRenderer,EventListener {
		
		public MyRendererListener() {
			
			super();
		}
		
		
		@Override
		public void onEvent(Event event) throws Exception {
		
		}
		
		@Override
		public void render(Listitem item, Object obj, int arg2) throws Exception {
			
			try {
				Listcell lc = null; 
				if(obj instanceof SMSCampaignSent) {
					SMSCampaignSent smsCampaignSent = (SMSCampaignSent)obj;
					item.setValue(smsCampaignSent);
					lc = new Listcell(""+smsCampaignSent.getMobileNumber());
					lc.setParent(item);
					
					lc = new Listcell(smsCampaignSent.getStatus());
					lc.setParent(item);
					
				}else if(obj instanceof String) {
					String reason = (String)obj;
					item.setValue(reason);
					lc = new Listcell(reason);
					lc.setParent(item);
				}
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
			
			
			
		}
	}

}


package org.mq.optculture.controller.loyalty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ReferralProgram;
import org.mq.marketer.campaign.beans.RewardReferraltype;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ReferralProgramDaoForDML;
import org.mq.marketer.campaign.dao.RewardReferraltypeDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
import org.mq.optculture.model.events.Events;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.event.PagingEvent;

public class myreferralprogramcontroller extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SpecialRewardsDao specialRewardsDao;
	private ReferralProgramDao referralprogramDao;
	private LoyaltyTransactionChildDao loyaltytransactionChildDao;
	private Users user;
	private Listbox spRewardLbId,referralPrgmsLbId,pageSizeLbId,progStatusLb;
	private Session session ;
	private Paging rewardPagingId;
	private SpecialRewardsDaoForDML specialRewardsDaoForDML;
	private ReferralProgramDaoForDML referralprogramDaoForDML;
	
	private RewardReferraltypeDaoForDML rewardreferraltypeDaoForDML;
	TimeZone clientTimeZone ;
	
	public myreferralprogramcontroller() {
		
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
	//	Div refNavigationDivId =(Div)Utility.getComponentById("refNavigationDivId");
	//	refNavigationDivId.setVisible(false);
	//	PageUtil.setHeader("Referral Programs ", Constants.STRING_NILL, style, true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	specialRewardsDao = (SpecialRewardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
	specialRewardsDaoForDML = (SpecialRewardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);

	referralprogramDao=(ReferralProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRAL_PROGRAM_DAO);
	
	
	
	referralprogramDaoForDML = (ReferralProgramDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.REFERRAL_PROGRAM_DAO_FOR_DML);

	
	rewardreferraltypeDaoForDML=(RewardReferraltypeDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.REWARD_REFERRAL_TYPE_DAO_FOR_DML);
	
	loyaltytransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	user = GetUser.getUserObj();
	session =Sessions.getCurrent();
    clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	//int totalSize=specialRewardsDao.findCountByStatusInManageRewards(user.getUserId(),null);


	int totalSize=referralprogramDao.findCountByStatusInMyReferralPrgms(user.getUserId(),null);
	
	int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
	rewardPagingId.setTotalSize(totalSize);
	rewardPagingId.setPageSize(pageSize);
	rewardPagingId.addEventListener("onPaging", this); 
	rewardPagingId.setActivePage(0);
	redraw(0, rewardPagingId.getPageSize());
	}
	public void onSelect$pageSizeLbId(){
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());	
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.addEventListener("onPaging", this); 
		redraw(0, rewardPagingId.getPageSize());
	}
	public String getSelectedStatus(){
		int index = progStatusLb.getSelectedIndex();
		String status = "";
		if (index != -1)
			status = progStatusLb.getSelectedItem().getValue();
		return status;
	}//getSelectedStatus()
	
	
	
	public void onClick$filterBtnId(){
		//int total1Size=specialRewardsDao.findCountByStatusInManageRewards(user.getUserId(), getSelectedStatus());
		
		int totalSize=referralprogramDao.findCountByStatusInMyReferralPrgms(user.getUserId(), getSelectedStatus());
		
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this); 
	
		
		
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$filterBtnId()
	
	
	public void onClick$resetAnchId(){
		progStatusLb.setSelectedIndex(0);
		int totalSize=referralprogramDao.findCountByStatusInMyReferralPrgms(user.getUserId(), getSelectedStatus());
	
		
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this);
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$resetAnchId()

	public void onClick$createReferalBtnId(){
		
		Redirect.goTo(PageListEnum.LOYALTY_REFERAL_SETTINGS);
	}
	
	public void redraw(int startIdx,int size) {
		
		List<ReferralProgram> RefprgmList =null;
		
		//RefprgmList=specialRewardsDao.findSpecialRewardsByUserIdInManageRewards(user.getUserId(),getSelectedStatus(),startIdx,size);
	             
		RefprgmList = referralprogramDao.findReferalprogramsByUserId(user.getUserId(),getSelectedStatus(),startIdx,size);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			int count=referralPrgmsLbId.getItemCount();
			while(count>0){
				referralPrgmsLbId.removeItemAt(--count);	
			}
	
		try {
			for (ReferralProgram refprgrm : RefprgmList) {
			//Only when special rewards with associatedWithFBP!= true
		//	if(!specialReward.isAssociatedWithFBP()) {
			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(refprgrm.getProgramName());
			lc.setParent(li);

		
			lc = new Listcell(refprgrm.getStatus());
			lc.setParent(li);


			lc = new Listcell(MyCalendar.calendarToString(refprgrm.getStartDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
			lc.setParent(li);

			lc = new Listcell(MyCalendar.calendarToString(refprgrm.getEndDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
			lc.setParent(li);


			/*lc = new Listcell("--");
			lc.setParent(li);

			lc = new Listcell("--");
			lc.setParent(li);*/

			
			/*	lc = new Listcell(specialReward.getDescription()==null?"":specialReward.getDescription());
			lc.setParent(li);
			
			lc = new Listcell(MyCalendar.calendarToString(specialReward.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone));
			lc.setParent(li);
			String rewardMsg="";
			if(specialReward.getRewardType()!=null){
			if(specialReward.getRewardType().equals("M"))
				rewardMsg=specialReward.getRewardValue()+" X Multiplier Reward";
			else
				rewardMsg=	specialReward.getRewardValue()+" of Value-code "+specialReward.getRewardValueCode().replace(OCConstants.LOYALTY_TYPE_AMOUNT,OCConstants.LOYALTY_TYPE_CURRENCY);
			
			}
		
			lc = new Listcell(rewardMsg);
			lc.setParent(li);
			
			lc = new Listcell(specialReward.getStatusSpecialReward());
			lc.setParent(li);   */
			
		
			
			
			

			Hbox hbox = new Hbox();
			
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit Reward");
			editImg.addEventListener("onClick", this);
		
			editImg.setAttribute("Type", "Edit");
			
			editImg.setStyle(" cursor:pointer; margin-top: 10px;margin-right:10px;margin-left: 20px;");
			editImg.setParent(hbox);
			
			lc = new Listcell();
			hbox.setParent(lc);


			String src = "";
			String toolTipTxtStr = "";
			if(refprgrm.getStatus()!=null && (refprgrm.getStatus().equalsIgnoreCase("Active") || refprgrm.getStatus().equalsIgnoreCase("Running"))) {
				src = "/images/loyalty/suspend.png";
				toolTipTxtStr  = "Suspend";
			}
			else {
				src = "/img/play_icn.png";
				toolTipTxtStr = "Activate";
			}

			Image statusImg = new Image(src);
			statusImg.setTooltiptext(toolTipTxtStr);
			statusImg.addEventListener("onClick", this);
			statusImg.setAttribute("Type", "Status");
			statusImg.setStyle("margin-top: 10px;margin-right:10px ;");
			statusImg.setParent(hbox);

			
			
			Image delImg = new Image("/images/Delete_icn.png");
			delImg.setTooltiptext("Delete Reward");
			delImg.addEventListener("onClick", this);
			delImg.setAttribute("Type", "delete");
			delImg.setStyle("margin-top: 10px;margin-right:10px ;");
			delImg.setParent(hbox);
			
			lc = new Listcell();
			hbox.setParent(lc);
			lc.setParent(li);
			li.setAttribute("event", refprgrm);
			li.setParent(referralPrgmsLbId);
			li.setValue(refprgrm);

	//	}
		}
		}catch(Exception e) {}
	}
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if(event.getTarget() instanceof Image) {
			Image img =(Image)event.getTarget();
		
			String action = (String)img.getAttribute("Type");
			Listitem lcImg=(Listitem) img.getParent().getParent().getParent();
			ReferralProgram referralprogram=lcImg.getValue();
		
			logger.info("entering referral "+referralprogram);

			if(action.equals("delete")){
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the Program?", "Referral Program",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return ;
					}
					
				} catch (Exception e) {
					logger.error("Exception ::", e);
					return;
				}	
				
				rewardreferraltypeDaoForDML.deleteProgramIdsByreferralId(referralprogram.getReferralId());
				
				referralprogramDaoForDML.deleteProgramIdsByreferralId(referralprogram.getReferralId());
				//referralprogramDaoForDML.delete(referralprogram);
				//referralPrgmsLbId.removeChild(lcImg);
				int totalSize=referralprogramDao.findCountByStatusInMyReferralPrgms(user.getUserId(), getSelectedStatus());
				
				rewardPagingId.setTotalSize(totalSize);
				rewardPagingId.addEventListener("onPaging", this); 
				redraw(0, rewardPagingId.getPageSize());
				MessageUtil.setMessage("Program deleted successfully.", "color:green;");
			}
			
			 else if(action.equals("Edit"))
			 
			 { 
	
		
			
			// RewardReferraltype 	RowChildList1 =	rewardreferraltypeDao.getMilestonesListByRefId(refprgrm.getReferralId());


			session.setAttribute(OCConstants.SESSION_EDIT_REFERRAL, referralprogram);
		
			logger.info("entering referral session edit "+referralprogram);

			
			 Redirect.goTo(PageListEnum.LOYALTY_REFERAL_SETTINGS);
			 
			 }
			 
			else if(action.equals("Status")) {
				//if(specialReward.getRewardValueCode()!=null){
				if(referralprogram.getStatus()!= null && (referralprogram.getStatus().equalsIgnoreCase("Active"))) {
					int confirm = Messagebox.show("Are you sure you want to  suspend the Program?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm !=1){
						return ;
					}
					 img.setSrc("/img/play_icn.png");
					 img.setTooltiptext("Activate");
					 referralprogram.setStatus("Suspended");
					 
				}
				else {
					int confirm = Messagebox.show("Are you sure you want to activate the Program?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm !=1){
						return ;
					}
					 img.setSrc("/images/loyalty/suspend.png");
					 img.setTooltiptext("Suspend");
					referralprogram.setStatus("Active");
				}
				specialRewardsDaoForDML.saveOrUpdate(referralprogram);
				redraw(0, rewardPagingId.getPageSize());
			//}
			/*
			 * else{ MessageUtil.
			 * setMessage("No Reward Type found, Please first provide Reward Type.",
			 * "color:red", "TOP"); return; }
			 */
		  }
		}
		else if(event.getTarget() instanceof Paging) {

			Paging paging = (Paging)event.getTarget();
			int desiredPage = paging.getActivePage();
			this.rewardPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
		}
	  
	}
}



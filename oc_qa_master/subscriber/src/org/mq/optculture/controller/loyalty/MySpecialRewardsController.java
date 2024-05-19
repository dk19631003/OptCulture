
package org.mq.optculture.controller.loyalty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
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

public class MySpecialRewardsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SpecialRewardsDao specialRewardsDao;
	private LoyaltyTransactionChildDao loyaltytransactionChildDao;
	private Users user;
	private Listbox spRewardLbId,pageSizeLbId,progStatusLb;
	private Session session ;
	private Paging rewardPagingId;
	private SpecialRewardsDaoForDML specialRewardsDaoForDML;
	TimeZone clientTimeZone ;
	
	public MySpecialRewardsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Manage Rewards", Constants.STRING_NILL, style, true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	specialRewardsDao = (SpecialRewardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
	specialRewardsDaoForDML = (SpecialRewardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
	loyaltytransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	user = GetUser.getUserObj();
	session =Sessions.getCurrent();
    clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	int totalSize=specialRewardsDao.findCountByStatusInManageRewards(user.getUserId(),null);
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
		int totalSize=specialRewardsDao.findCountByStatusInManageRewards(user.getUserId(), getSelectedStatus());
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this); 
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$filterBtnId()
	public void onClick$resetAnchId(){
		progStatusLb.setSelectedIndex(0);
		int totalSize=specialRewardsDao.findCountByStatusInManageRewards(user.getUserId(), getSelectedStatus());
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this);
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$resetAnchId()

	public void onClick$createSegmentTBarBtnId(){
		Redirect.goTo(PageListEnum.LOYALTY_SPECIAL_REWARDS);
	}
	
	public void redraw(int startIdx,int size) {
		
		List<SpecialReward>  specialRewardList=specialRewardsDao.findSpecialRewardsByUserIdInManageRewards(user.getUserId(),getSelectedStatus(),startIdx,size);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			int count=spRewardLbId.getItemCount();
			while(count>0){
			spRewardLbId.removeItemAt(--count);	
			}
	
		for (SpecialReward specialReward : specialRewardList) {
			//Only when special rewards with associatedWithFBP!= true
		//	if(!specialReward.isAssociatedWithFBP()) {
			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(specialReward.getRewardName());
			lc.setParent(li);

			lc = new Listcell(specialReward.getDescription()==null?"":specialReward.getDescription());
			lc.setParent(li);
			
			lc = new Listcell(MyCalendar.calendarToString(specialReward.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone));
			lc.setParent(li);
			String rewardMsg="";
			if(specialReward.getRewardType()!=null){
			if(specialReward.getRewardType().equals("M"))
				rewardMsg=specialReward.getRewardValue()+" X Multiplier Reward";
			else if(specialReward.getRewardType().equalsIgnoreCase("P"))
				rewardMsg=	specialReward.getRewardValue()+"% of Value-code "+specialReward.getRewardValueCode().replace(OCConstants.LOYALTY_TYPE_AMOUNT,OCConstants.LOYALTY_TYPE_CURRENCY);
			else
				rewardMsg=	specialReward.getRewardValue()+" of Value-code "+specialReward.getRewardValueCode().replace(OCConstants.LOYALTY_TYPE_AMOUNT,OCConstants.LOYALTY_TYPE_CURRENCY);
			
			}
		
			lc = new Listcell(rewardMsg);
			lc.setParent(li);
			
			lc = new Listcell(specialReward.getStatusSpecialReward());
			lc.setParent(li);
			
			Object obj[]= new Object[]{0,0,0};
			List<Object[]> list=loyaltytransactionChildDao.getltyTransactionBysprewarid(Long.parseLong(specialReward.getCreatedBy()),specialReward.getRewardId());
			if(list.size()>0)
			obj=list.get(0);
			
			lc = new Listcell(obj[0].toString());
			lc.setParent(li);
			
			lc = new Listcell(obj[1].toString());
			lc.setParent(li);
			
			lc = new Listcell(obj[2].toString());
			lc.setParent(li);
			
			
			

			Hbox hbox = new Hbox();
			
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit Reward");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("Type", "edit");
			editImg.setStyle(" cursor:pointer; margin-top: 10px;margin-right:10px;margin-left: 20px;");
			editImg.setParent(hbox);
			
			lc = new Listcell();
			hbox.setParent(lc);


			String src = "";
			String toolTipTxtStr = "";
			if(specialReward.getStatusSpecialReward()!=null && (specialReward.getStatusSpecialReward().equalsIgnoreCase("Active"))) {
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
			li.setParent(spRewardLbId);
			li.setValue(specialReward);

	//	}
		}

	}
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if(event.getTarget() instanceof Image) {
			Image img =(Image)event.getTarget();
			String action = (String)img.getAttribute("Type");
			Listitem lcImg=(Listitem) img.getParent().getParent().getParent();
			SpecialReward specialReward=lcImg.getValue();
			if(action.equals("delete")){
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the Reward?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return ;
					}
					
				} catch (Exception e) {
					logger.error("Exception ::", e);
					return;
				}	
				specialRewardsDaoForDML.deleteProgramIdsByrewardId(specialReward.getRewardId());
				specialRewardsDaoForDML.delete(specialReward);
				//spRewardLbId.removeChild(lcImg);
				int totalSize=specialRewardsDao.findCountByStatus(user.getUserId(),getSelectedStatus());
				rewardPagingId.setTotalSize(totalSize);
				rewardPagingId.addEventListener("onPaging", this); 
				redraw(0, rewardPagingId.getPageSize());
				MessageUtil.setMessage("Reward deleted successfully.", "color:green;");
			}
			else if(action.equals("edit")){
				session.setAttribute("editRewardRule", specialReward);
				Redirect.goTo(PageListEnum.LOYALTY_SPECIAL_REWARDS);
			}
			else if(action.equals("Status")) {
				if(specialReward.getRewardValueCode()!=null){
				if(specialReward.getStatusSpecialReward()!=null && (specialReward.getStatusSpecialReward().equalsIgnoreCase("Active"))) {
					int confirm = Messagebox.show("Are you sure you want to  suspend the Reward?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm !=1){
						return ;
					}
					 img.setSrc("/img/play_icn.png");
					 img.setTooltiptext("Activate");
					 specialReward.setStatusSpecialReward("Suspended");
					 
				}
				else {
					int confirm = Messagebox.show("Are you sure you want to activate the Reward?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm !=1){
						return ;
					}
					 img.setSrc("/images/loyalty/suspend.png");
					 img.setTooltiptext("Suspend");
					 specialReward.setStatusSpecialReward("Active");
				}
				specialRewardsDaoForDML.saveOrUpdate(specialReward);
				redraw(0, rewardPagingId.getPageSize());
			}
			else{
				MessageUtil.setMessage("No Reward Type found, Please first provide Reward Type.", "color:red", "TOP");
				return;
			}
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

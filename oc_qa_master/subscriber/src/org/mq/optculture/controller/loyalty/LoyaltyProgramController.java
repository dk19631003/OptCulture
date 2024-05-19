
package org.mq.optculture.controller.loyalty;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.ActivityEnum;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.UserActivitiesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.helper.LoyaltyProgramHelper;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class LoyaltyProgramController  extends GenericForwardComposer{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Long userId;
	private Boolean isPasswordRequired;
	private Long currentOCAdminUserId;
	TimeZone clientTimeZone ;
	private Listbox progListlbId,progStatusLb,pageSizeLbId,addWinId$genTypeLbId,addWinId$statusLbId, 
	exportWinId$cardSetLbId,exportWinId$statusLbId,addWinId$selTierLbId;
	private Paging programListPagingId;
	private Window addWinId,exportWinId;
	private Label exportWinId$selctedCardSetId;
	private Textbox addWinId$cardSetNameTbId,addWinId$quantityNameTbId;
	private Radiogroup addWinId$ltyCardTypeRadioGrId, addWinId$tierAssignmentRadioGrId;
	private Radio addWinId$autoAssignRadioId, addWinId$linkTierRadioId;
	private Popup ltyPwdPopupId;
	private Textbox ltyPwdTbId;
	private UserActivitiesDaoForDML userActivitiesDaoForDML;
	private UsersDao usersDao;
	Long prgmId=null;
	
	public LoyaltyProgramController() {
		userId = GetUser.getUserObj().getUserId();
		session = Sessions.getCurrent();
		userActivitiesDaoForDML = (UserActivitiesDaoForDML)SpringUtil.getBean("userActivitiesDaoForDML");
		usersDao = (UsersDao) SpringUtil.getBean("usersDao");
	}//LoyaltyProgramController()

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Loyalty Programs","",style,true);
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		int totalSize = ltyPrgmSevice.getCount(userId, getSelectedStatus());
		isPasswordRequired = (Boolean)session.getAttribute("isPasswordRequired");
		currentOCAdminUserId = (Long)session.getAttribute("currentOCAdmin");
		programListPagingId.setTotalSize(totalSize);
		programListPagingId.setAttribute("onPaging", "topPaging");
		programListPagingId.addEventListener("onPaging", this); 

		programListPagingId.setActivePage(0);
		redraw(0, programListPagingId.getPageSize());
	}//doAfterCompose()

	public String getSelectedStatus(){
		int index = progStatusLb.getSelectedIndex();
		String status = "";
		if (index != -1)
			status = progStatusLb.getSelectedItem().getValue();
		return status;
	}//getSelectedStatus()

	private void redraw(int startIndex, int size) {

		MessageUtil.clearMessage();
		int count =  progListlbId.getItemCount();

		for(; count>0; count--) {
			progListlbId.removeItemAt(count-1);
		}

		//System.gc();

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();

		List<LoyaltyProgram> progList = getPrograms(startIndex, size,orderby_colName,desc_Asc);
		logger.info("progList"+progList);
		if(progList == null) return;
		if(progList != null && progList.size() > 0) {
			logger.info("setting listitems");
			Listitem li;
			Listcell lc;
			for (LoyaltyProgram loyaltyProgram : progList) {
				long totCards = ltyPrgmSevice.getInventoryCardsCount(loyaltyProgram.getProgramId(),null);

				li = new Listitem();

				lc = new Listcell();
				if(loyaltyProgram.getDefaultFlag()==OCConstants.FLAG_YES) {
					lc.setLabel(loyaltyProgram.getProgramName()+" (Default Program)");
				}
				else {
					lc.setLabel(loyaltyProgram.getProgramName());
				}
				lc.setStyle("padding-left:10px;");
				lc.setTooltiptext(loyaltyProgram.getProgramName());
				lc.setParent(li);

				lc = new Listcell();
				lc.setLabel(loyaltyProgram.getDescription());
				lc.setTooltiptext(loyaltyProgram.getDescription());
				lc.setParent(li);


				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(MyCalendar.calendarToString(loyaltyProgram.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone));
				lc.setParent(li);

				lc = new Listcell();
				String memType = (loyaltyProgram.getProgramType()!=null && loyaltyProgram.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) ? "Custom-based" : (loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ? "Mobile-based" : "Card-based");
				lc.setLabel(memType);
				lc.setTooltiptext(memType);
				lc.setParent(li);

				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				String status = loyaltyProgram.getStatus();

				if(progStatusLb.getSelectedItem() != null && 
						!progStatusLb.getSelectedItem().getValue().equals("All") && !progStatusLb.getSelectedItem().getValue().equals(status)) continue;

				lc.setLabel(status);
				if(status.equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
					String draftStatus = loyaltyProgram.getDraftStatus();
					String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
					if(draftList[1].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)){
						lc.setLabel(status+" (Incomplete)");
						lc.setStyle("color:red;");
					}
				}
				lc.setParent(li);

				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				lc.setLabel(totCards+"");
				lc.setTooltiptext(totCards+"");
				lc.setParent(li);


				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_NO) {
					lc.setLabel("NA");
					lc.setTooltip("NA");
				}
				else {
					lc.setLabel(loyaltyProgram.getNoOfTiers()+"");
					lc.setTooltiptext(loyaltyProgram.getNoOfTiers()+"");
				}
				lc.setParent(li);


				lc = new Listcell();
				lc.setStyle("padding-left:10px;");
				Hbox hbox = new Hbox();


				Image previewImg = new Image("/images/Preview_icn.png");
				previewImg.setTooltiptext("View Program");
				previewImg.setStyle("cursor:pointer;margin-right:5px;");
				previewImg.addEventListener("onClick", this);
				previewImg.setAttribute("type", "view");
				previewImg.setParent(hbox);

				Image editImg = new Image("/img/email_edit.gif");
				editImg.setTooltiptext("Edit Program");
				editImg.setStyle("cursor:pointer;margin-right:5px;");
				editImg.addEventListener("onClick", this);
				editImg.setAttribute("type", "Edit");
				editImg.setParent(hbox);

				Image addImg = new Image("/img/icons/add_icon.png");
				addImg.setTooltiptext("Add Cards");
				addImg.setStyle("cursor:pointer;margin-right:5px;");
				addImg.addEventListener("onClick", this);
				addImg.setAttribute("type", "add");
				addImg.setParent(hbox);

				Image exportImg = new Image("/images/loyalty/export.png");
				exportImg.setTooltiptext("Export");
				exportImg.setStyle("cursor:pointer;margin-right:5px;");
				exportImg.addEventListener("onClick", this);
				exportImg.setAttribute("type", "export");
				exportImg.setParent(hbox);

				String src = "";
				String toolTipTxtStr = "";
				String type = "";
				if(loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
					src = "/images/loyalty/suspend.png";
					toolTipTxtStr  = "Suspend";
					type = "Suspend";
				}
				else if(!loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
					src = "/img/play_icn.png";
					toolTipTxtStr = "Activate";
					type = "Activate";
				}

				Image statusImg = new Image(src);
				statusImg.setTooltiptext(toolTipTxtStr);
				statusImg.setStyle("cursor:pointer;margin-right:5px;");
				statusImg.addEventListener("onClick", this);
				statusImg.setAttribute("type", type);
				statusImg.setParent(hbox);

				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete Program");
				delImg.setStyle("cursor:pointer;margin-right:5px;");
				delImg.addEventListener("onClick", this);
				delImg.setAttribute("type", "delete");
				delImg.setParent(hbox);

				Image reportImg = new Image("/img/theme/home/reports_icon.png");
				reportImg.setTooltiptext("Report");
				reportImg.setStyle("cursor:pointer;margin-right:5px;");
				reportImg.addEventListener("onClick", this);
				reportImg.setAttribute("type", "report");
				reportImg.setParent(hbox);


				hbox.setParent(lc);	
				lc.setParent(li);

				li.setHeight("30px");
				li.setParent(progListlbId);
				li.setValue(loyaltyProgram);
			}
		}
	}//redraw()


	private List<LoyaltyProgram> getPrograms(int startIndex, int size,String orderby_colName,String desc_Asc) {

		try {
			int index = progStatusLb.getSelectedIndex();

			String status = "All";
			if (index != -1)
				status = progStatusLb.getSelectedItem().getValue();
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			List<LoyaltyProgram> progList =  ltyPrgmSevice.getprogramList(userId, status, startIndex, size,orderby_colName,desc_Asc);
			return progList;
		} catch (Exception e) {
			return null;
		}
	}//getPrograms()
	
	public void onCheck$ltyCardTypeRadioGrId$addWinId() {
		if(addWinId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Physical")) {
			addWinId$autoAssignRadioId.setSelected(true);
			addWinId$linkTierRadioId.setSelected(false);
			addWinId$autoAssignRadioId.setDisabled(false);
			addWinId$linkTierRadioId.setDisabled(false);
			addWinId$selTierLbId.setDisabled(true);
			addWinId$selTierLbId.setSelectedIndex(0);
			onCheck$tierAssignmentRadioGrId$addWinId();
		}
		else if(addWinId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("Virtual")) {
			addWinId$autoAssignRadioId.setSelected(true);
			addWinId$linkTierRadioId.setSelected(false);
			addWinId$autoAssignRadioId.setDisabled(true);
			addWinId$linkTierRadioId.setDisabled(true);
			addWinId$selTierLbId.setDisabled(true);
			addWinId$selTierLbId.setSelectedIndex(0);
		}
	}//onCheck$ltyCardTypeRadioGrId$addWinId()
	
	public void onCheck$tierAssignmentRadioGrId$addWinId() {
		if(addWinId$tierAssignmentRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("auto")) {
			addWinId$selTierLbId.setDisabled(true);
			addWinId$selTierLbId.setSelectedIndex(0);
		}
		else if(addWinId$tierAssignmentRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase("link")) {
			prgmId = (Long) addWinId.getAttribute("programId");
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
			if(prgmObj.getTierEnableFlag() == OCConstants.FLAG_NO) {
				MessageUtil.setMessage("Card-set cannot be linked to a particular tier level as the program is a non-tiered program.", "color:red", "TOP");
				addWinId$autoAssignRadioId.setSelected(true);
				addWinId$linkTierRadioId.setSelected(false);
				return;
			}
			addWinId$selTierLbId.setDisabled(false);
			addWinId$selTierLbId.setSelectedIndex(0);
		}
	}//onCheck$tierAssignmentRadioGrId$addWinId()
	

	public void onClick$addBtnId$addWinId(){

		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		Long prgmId = (Long) addWinId.getAttribute("programId");
		String cardSetName = addWinId$cardSetNameTbId.getValue();
		if(cardSetName == null || cardSetName.length() == 0) {
			MessageUtil.setMessage("Card-set name cannot be empty.", "color:red", "TOP");
			return;
		}else if(!Utility.validateBy(Constants.LTY_NAME_PATTERN,cardSetName)) {
			MessageUtil.setMessage("Enter valid card-set name.","color:red");
			return;
		}
		if(addWinId$cardSetNameTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Card-set name exceeds the maximum characters limit.", "color:red", "TOP");
			return;
		}

		List<LoyaltyCardSet> cardList=ltyPrgmSevice.getCardsetList(prgmId);
		if (cardList != null) {
			for (LoyaltyCardSet loyaltyCardSet : cardList) {
				if (loyaltyCardSet.getCardSetName().equalsIgnoreCase(
						addWinId$cardSetNameTbId.getValue())) {
					MessageUtil.setMessage("Card-set name already exists.",
							"color:red", "TOP");
					return;
				}
			}
		}

		if(!checkIfNumber(addWinId$quantityNameTbId.getValue().trim())) {
			MessageUtil.setMessage("Please provide number value for quantity.", "red");
			return;
		}

		if(addWinId$quantityNameTbId.getValue() == null || addWinId$quantityNameTbId.getValue().length() == 0 || Long.parseLong(addWinId$quantityNameTbId.getValue()) <= 0){
			MessageUtil.setMessage("Quantity cannot be empty or zero.", "color:red", "TOP");
			return;
		}
		if(addWinId$quantityNameTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Quantity value exceeds the maximum characters limit.", "color:red", "TOP");
			return;
		}

		if(LoyaltyProgramHelper.anotherCardGeneration(prgmId)){
			MessageUtil.setMessage("Card generation is already underway by organisation user. Please try again after some time.", "color:blue", "TOP");
			return;
		}

		String quantity = addWinId$quantityNameTbId.getValue();
		String cardsetType = "";
		int linkedTierLevel = 0;
		if(addWinId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_TYPE_PHYSICAL)) {
			cardsetType = OCConstants.LOYALTY_CARDSET_TYPE_PHYSICAL;
			linkedTierLevel = Integer.parseInt(addWinId$selTierLbId.getSelectedItem().getValue().toString());
		}
		else if(addWinId$ltyCardTypeRadioGrId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL)) {
			cardsetType = OCConstants.LOYALTY_CARDSET_TYPE_VIRTUAL;
		}
		String genTyp = addWinId$genTypeLbId.getSelectedItem().getValue();
		String status = addWinId$statusLbId.getSelectedItem().getValue();
		char migrationFlag = OCConstants.FLAG_NO;
		LoyaltyProgram prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
		//		String prgmStatus= "";
		String draftStatus = "";
		if (prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {
			draftStatus = prgmObj.getDraftStatus();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
			draftList[2] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
			draftStatus = "";
			for (String eachStr : draftList) {

				if (draftStatus.isEmpty()) {
					draftStatus = eachStr;
				} else {
					draftStatus += Constants.ADDR_COL_DELIMETER + eachStr;
				}
			}

			prgmObj.setDraftStatus(draftStatus);
			ltyPrgmSevice.savePrgmObj(prgmObj);

		}
		ltyPrgmSevice.onAddCardSet(cardSetName,quantity, cardsetType,genTyp,status,migrationFlag,prgmId,userId,linkedTierLevel,OCConstants.LOYALTY_CARD_GENERATION_TYPE_SYSTEM);
		MessageUtil.setMessage("Card-set added successfully.", "color:blue", "TOP");
		addWinId$cardSetNameTbId.setValue("");
		addWinId$quantityNameTbId.setValue("");
		addWinId$genTypeLbId.setSelectedIndex(0);
		addWinId$ltyCardTypeRadioGrId.setSelectedIndex(0);
		addWinId$statusLbId.setSelectedIndex(0);
		addWinId.setVisible(false);

		int totalSize = ltyPrgmSevice.getCount(userId, getSelectedStatus());
		programListPagingId.setTotalSize(totalSize);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		programListPagingId.setPageSize(pageSize);
		programListPagingId.setAttribute("onPaging", "topPaging");
		programListPagingId.addEventListener("onPaging", this); 
		programListPagingId.setActivePage(0);
		redraw(0, pageSize);
	}//onClick$addBtnId$addWinId()

	public boolean checkIfNumber(String in) {
		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}// checkIfNumber()


	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);

		if(event.getTarget() instanceof Image) {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			Image currentImage = (Image)event.getTarget();
			Listitem li = (Listitem)currentImage.getParent().getParent().getParent();
			LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)(li).getValue();
			List<LoyaltyCardSet> list = ltyPrgmSevice.getCardsetList(loyaltyProgram.getProgramId());
			String programImageType = (String)currentImage.getAttribute("type");
			if(programImageType.equalsIgnoreCase("Suspend")) {
				ltyPwdPopupId.setAttribute("listitem", li);
				suspend(currentImage);
				
				
				/*if(loyaltyProgram.getDefaultFlag() == OCConstants.FLAG_YES) {
					MessageUtil.setMessage("You are suspending a default program, enrollments without card number cannot be done in future.", "color:blue", "TOP");
				}
				loyaltyProgram.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED);
				ltyPrgmSevice.savePrgmObj(loyaltyProgram);
				Listcell lc = (Listcell) li.getChildren().get(4);
				lc.setStyle("padding-left:10px;");
//				lc.setStyle("color:#414042;");
				lc.setLabel(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED);
				lc.setParent(li);

				lc = (Listcell) li.getLastChild();
				Hbox hbox = (Hbox) lc.getFirstChild();
				lc.removeChild(hbox);

				hbox = new Hbox();

				Image previewImg = new Image("/images/Preview_icn.png");
				previewImg.setTooltiptext("View Program");
				previewImg.setStyle("cursor:pointer;margin-right:5px;");
				previewImg.addEventListener("onClick", this);
				previewImg.setAttribute("type", "view");
				previewImg.setParent(hbox);

				Image editImg = new Image("/img/email_edit.gif");
				editImg.setTooltiptext("Edit Program");
				editImg.setStyle("cursor:pointer;margin-right:5px;");
				editImg.addEventListener("onClick", this);
				editImg.setAttribute("type", "Edit");
				editImg.setParent(hbox);

				Image addImg = new Image("/img/icons/add_icon.png");
				addImg.setTooltiptext("Add Cards");
				addImg.setStyle("cursor:pointer;margin-right:5px;");
				addImg.addEventListener("onClick", this);
				addImg.setAttribute("type", "add");
				addImg.setParent(hbox);

				Image exportImg = new Image("/images/loyalty/export.png");
				exportImg.setTooltiptext("Export");
				exportImg.setStyle("cursor:pointer;margin-right:5px;");
				exportImg.addEventListener("onClick", this);
				exportImg.setAttribute("type", "export");
				exportImg.setParent(hbox);

				String src = "";
				String toolTipTxtStr = "";
				String type = "";
				if(loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
					src = "/images/loyalty/suspend.png";
					toolTipTxtStr  = "Suspend";
					type = "Suspend";
				}
				else if(!loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
					src = "/img/play_icn.png";
					toolTipTxtStr = "Activate";
					type = "Activate";
				}

				Image statusImg = new Image(src);
				statusImg.setTooltiptext(toolTipTxtStr);
				statusImg.setStyle("cursor:pointer;margin-right:5px;");
				statusImg.addEventListener("onClick", this);
				statusImg.setAttribute("type", type);
				statusImg.setParent(hbox);

				Image delImg = new Image("/img/action_delete.gif");
				delImg.setTooltiptext("Delete Program");
				delImg.setStyle("cursor:pointer;margin-right:5px;");
				delImg.addEventListener("onClick", this);
				delImg.setAttribute("type", "delete");
				delImg.setParent(hbox);

				Image reportImg = new Image("/img/theme/home/reports_icon.png");
				reportImg.setTooltiptext("Report");
				reportImg.setStyle("cursor:pointer;margin-right:5px;");
				reportImg.addEventListener("onClick", this);
				reportImg.setAttribute("type", "report");
				reportImg.setParent(hbox);

				hbox.setParent(lc);	*/
			}
			else if(programImageType.equalsIgnoreCase("Activate")) {
				
				ltyPwdPopupId.setAttribute("listitem", li);
				activate(currentImage);
				
				/*String draftStatus = loyaltyProgram.getDraftStatus();
				String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
				if(draftList[0].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && draftList[1].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && draftList[2].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
					loyaltyProgram.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE);
					ltyPrgmSevice.savePrgmObj(loyaltyProgram);

					Listcell lc = (Listcell) li.getChildren().get(4);
					lc.setLabel(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE);
					lc.setStyle("padding-left:10px;");
					lc.setParent(li);

					lc = (Listcell) li.getLastChild();
					Hbox hbox = (Hbox) lc.getFirstChild();
					lc.removeChild(hbox);

					hbox = new Hbox();

					Image previewImg = new Image("/images/Preview_icn.png");
					previewImg.setTooltiptext("View Program");
					previewImg.setStyle("cursor:pointer;margin-right:5px;");
					previewImg.addEventListener("onClick", this);
					previewImg.setAttribute("type", "view");
					previewImg.setParent(hbox);

					Image editImg = new Image("/img/email_edit.gif");
					editImg.setTooltiptext("Edit Program");
					editImg.setStyle("cursor:pointer;margin-right:5px;");
					editImg.addEventListener("onClick", this);
					editImg.setAttribute("type", "Edit");
					editImg.setParent(hbox);

					Image addImg = new Image("/img/icons/add_icon.png");
					addImg.setTooltiptext("Add Cards");
					addImg.setStyle("cursor:pointer;margin-right:5px;");
					addImg.addEventListener("onClick", this);
					addImg.setAttribute("type", "add");
					addImg.setParent(hbox);

					Image exportImg = new Image("/images/loyalty/export.png");
					exportImg.setTooltiptext("Export");
					exportImg.setStyle("cursor:pointer;margin-right:5px;");
					exportImg.addEventListener("onClick", this);
					exportImg.setAttribute("type", "export");
					exportImg.setParent(hbox);

					String src = "";
					String toolTipTxtStr = "";
					String type = "";
					if(loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
						src = "/images/loyalty/suspend.png";
						toolTipTxtStr  = "Suspend";
						type = "Suspend";
					}
					else if(!loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
						src = "/img/play_icn.png";
						toolTipTxtStr = "Activate";
						type = "Activate";
					}

					Image statusImg = new Image(src);
					statusImg.setTooltiptext(toolTipTxtStr);
					statusImg.setStyle("cursor:pointer;margin-right:5px;");
					statusImg.addEventListener("onClick", this);
					statusImg.setAttribute("type", type);
					statusImg.setParent(hbox);

					Image delImg = new Image("/img/action_delete.gif");
					delImg.setTooltiptext("Delete Program");
					delImg.setStyle("cursor:pointer;margin-right:5px;");
					delImg.addEventListener("onClick", this);
					delImg.setAttribute("type", "delete");
					delImg.setParent(hbox);

					Image reportImg = new Image("/img/theme/home/reports_icon.png");
					reportImg.setTooltiptext("Report");
					reportImg.setStyle("cursor:pointer;margin-right:5px;");
					reportImg.addEventListener("onClick", this);
					reportImg.setAttribute("type", "report");
					reportImg.setParent(hbox);

					hbox.setParent(lc);	
				}
				else {
					String step2 = draftList[1];
					String step3 = draftList[2];
					String errMsg = "The program cannot be activated, ";
					if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
							step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)) {
						errMsg += "as no card-sets were found and not all tiers have been configured for this program.";
					}
					else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
							step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
						errMsg += "as no card-sets were found for this program.";
					}
					else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && 
							step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)){
						errMsg += "as all tiers have not been configured for this program";
					}
					MessageUtil.setMessage(errMsg, "color:red", "TOP");
				}*/

			}
			else if(programImageType.equalsIgnoreCase("Edit")) {
				edit(currentImage);
			} 
			else if(programImageType.equalsIgnoreCase("view")) {
				view(currentImage);
			} 
			else if (programImageType.equalsIgnoreCase("add")) {

				if( loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
					MessageUtil.setMessage("No cards can be added for a mobile based program.", "color:red", "TOP");
					return;
				}
				if( loyaltyProgram.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) {
					MessageUtil.setMessage("No cards can be added for a custom number based program.", "color:red", "TOP");
					return;
				}
				add(currentImage);

			} else if (programImageType.equalsIgnoreCase("export")) {

				if( loyaltyProgram.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) || 
						loyaltyProgram.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) {
					MessageUtil.setMessage("No cards found to export.", "color:red", "TOP");
					return;
				}

				if(list==null) {
					MessageUtil.setMessage("No card-set found for this program.", "color:red", "TOP");
					return;
				}
				exportWinId.setAttribute("programId", loyaltyProgram.getProgramId());
				exportWinId.setVisible(true);
				exportWinId.setPosition("center");
				setCards(list);
				exportWinId.doHighlighted();

			} else if (programImageType.equalsIgnoreCase("delete")) {

				if(!loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {

					MessageUtil.setMessage("Active / Suspended programs cannot be deleted.", "color:red", "TOP");
					return;
				}

				delete(currentImage);


			} else if (programImageType.equalsIgnoreCase("report")) {

				report(currentImage);
			}
		} else if(event.getTarget() instanceof Paging) {

			Paging paging = (Paging)event.getTarget();

			int desiredPage = paging.getActivePage();
			this.programListPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());

		}//else if

	}//onEvent()
	
	public void onClick$submitPwdBtnId() {
		try {
			String pwdStr = ltyPwdTbId.getValue().trim();

			if(pwdStr == null || pwdStr.trim().length() ==0) {
				MessageUtil.setMessage( "Please provide password.", "color:red;", "TOP");
				ltyPwdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}
			/*else if(pwdStr ==null || pwdStr.trim().length() <= 3) {
				MessageUtil.setMessage( "Password must be greater than 3 characters.", "color:red;", "TOP");
				ltyPwdTbId.setStyle("border:1px solid #DD7870;");
				return;
			}
*/
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
			if(loyaltyThresholdAlerts != null && EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd()).equals(pwdStr)) {
				Long prgId = (Long) ltyPwdPopupId.getAttribute("programId");
				LoyaltyProgramDao loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
				LoyaltyProgram loyaltyProgram =  loyaltyProgramDao.findById(prgId);
				if(ltyPwdPopupId.getAttribute("type").toString().equalsIgnoreCase("edit")) {
					String draftStatus = loyaltyProgram.getDraftStatus();
					String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);

					if(draftList[0].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) ) {
						session.setAttribute("programId", prgId);
						session.removeAttribute("loyaltyProgramTier");
						MessageUtil.clearMessage();
						Redirect.goTo(PageListEnum.LOYALTY_CREATE_PROGRAM);
					}else if(draftList[1].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) ) {
						
						session.setAttribute("programId", prgId);
						session.removeAttribute("loyaltyProgramTier");
						MessageUtil.clearMessage();
						if(loyaltyProgram.getRewardType()!=null && loyaltyProgram.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK))
							Redirect.goTo(PageListEnum.LOYALTY_PERK_RULES);
						else
						   Redirect.goTo(PageListEnum.LOYALTY_RULES);
					}else if(draftList[2].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) ) {
						session.setAttribute("programId", prgId);
						session.removeAttribute("loyaltyProgramTier");
						MessageUtil.clearMessage();
						Redirect.goTo(PageListEnum.LOYALTY_ADD_CARDS);
					}else if(draftList[3].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) ) {
						session.setAttribute("programId", prgId);
						session.removeAttribute("loyaltyProgramTier");
						MessageUtil.clearMessage();
						Redirect.goTo(PageListEnum.LOYALTY_ADDITIONAL_SETTINGS);
					}else if(draftList[4].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) ) {
						session.setAttribute("programId", prgId);
						session.removeAttribute("loyaltyProgramTier");
						MessageUtil.clearMessage();
						Redirect.goTo(PageListEnum.LOYALTY_AUTO_COMMUNICATION);
					}else if(draftList[5].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) ) {
						session.setAttribute("programId", prgId);
						session.removeAttribute("loyaltyProgramTier");
						MessageUtil.clearMessage();
						Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_OVERVIEW);
					}
				}
				else if(ltyPwdPopupId.getAttribute("type").toString().equalsIgnoreCase("view")) {
					session.setAttribute("programId", prgId);
					session.removeAttribute("loyaltyProgramTier");
					MessageUtil.clearMessage();
					Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_OVERVIEW);
				}
				else if(ltyPwdPopupId.getAttribute("type").toString().equalsIgnoreCase("suspend")){
					/*session.setAttribute("programId", prgId);
					session.removeAttribute("loyaltyProgramTier");
					MessageUtil.clearMessage();
					Redirect.goTo(PageListEnum.LOYALTY_PROGRAMS_LIST);*/
					
					Listitem li = (Listitem)ltyPwdPopupId.getAttribute("listitem");
					
					if(loyaltyProgram.getDefaultFlag() == OCConstants.FLAG_YES) {
						MessageUtil.setMessage("You are suspending a default program, enrollments without card number cannot be done in future.", "color:blue", "TOP");
					}
					loyaltyProgram.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED);
					ltyPrgmSevice.savePrgmObj(loyaltyProgram);
					Listcell lc = (Listcell) li.getChildren().get(4);
					lc.setStyle("padding-left:10px;");
//					lc.setStyle("color:#414042;");
					lc.setLabel(OCConstants.LOYALTY_PROGRAM_STATUS_SUSPENDED);
					lc.setParent(li);

					lc = (Listcell) li.getLastChild();
					Hbox hbox = (Hbox) lc.getFirstChild();
					lc.removeChild(hbox);

					hbox = new Hbox();

					Image previewImg = new Image("/images/Preview_icn.png");
					previewImg.setTooltiptext("View Program");
					previewImg.setStyle("cursor:pointer;margin-right:5px;");
					previewImg.addEventListener("onClick", this);
					previewImg.setAttribute("type", "view");
					previewImg.setParent(hbox);

					Image editImg = new Image("/img/email_edit.gif");
					editImg.setTooltiptext("Edit Program");
					editImg.setStyle("cursor:pointer;margin-right:5px;");
					editImg.addEventListener("onClick", this);
					editImg.setAttribute("type", "Edit");
					editImg.setParent(hbox);

					Image addImg = new Image("/img/icons/add_icon.png");
					addImg.setTooltiptext("Add Cards");
					addImg.setStyle("cursor:pointer;margin-right:5px;");
					addImg.addEventListener("onClick", this);
					addImg.setAttribute("type", "add");
					addImg.setParent(hbox);

					Image exportImg = new Image("/images/loyalty/export.png");
					exportImg.setTooltiptext("Export");
					exportImg.setStyle("cursor:pointer;margin-right:5px;");
					exportImg.addEventListener("onClick", this);
					exportImg.setAttribute("type", "export");
					exportImg.setParent(hbox);

					String src = "";
					String toolTipTxtStr = "";
					String type = "";
					if(loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
						src = "/images/loyalty/suspend.png";
						toolTipTxtStr  = "Suspend";
						type = "Suspend";
					}
					else if(!loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
						src = "/img/play_icn.png";
						toolTipTxtStr = "Activate";
						type = "Activate";
					}

					Image statusImg = new Image(src);
					statusImg.setTooltiptext(toolTipTxtStr);
					statusImg.setStyle("cursor:pointer;margin-right:5px;");
					statusImg.addEventListener("onClick", this);
					statusImg.setAttribute("type", type);
					statusImg.setParent(hbox);

					Image delImg = new Image("/img/action_delete.gif");
					delImg.setTooltiptext("Delete Program");
					delImg.setStyle("cursor:pointer;margin-right:5px;");
					delImg.addEventListener("onClick", this);
					delImg.setAttribute("type", "delete");
					delImg.setParent(hbox);

					Image reportImg = new Image("/img/theme/home/reports_icon.png");
					reportImg.setTooltiptext("Report");
					reportImg.setStyle("cursor:pointer;margin-right:5px;");
					reportImg.addEventListener("onClick", this);
					reportImg.setAttribute("type", "report");
					reportImg.setParent(hbox);

					hbox.setParent(lc);
					
					session.setAttribute("programId", prgId);
					session.removeAttribute("loyaltyProgramTier");
					MessageUtil.clearMessage();
					
					ltyPwdPopupId.close();
				}
				else if(ltyPwdPopupId.getAttribute("type").toString().equalsIgnoreCase("activate")){
					
					Listitem li = (Listitem)ltyPwdPopupId.getAttribute("listitem");
					
					String draftStatus = loyaltyProgram.getDraftStatus();
					String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);
					if(draftList[0].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && draftList[1].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && draftList[2].equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
						loyaltyProgram.setStatus(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE);
						ltyPrgmSevice.savePrgmObj(loyaltyProgram);

						Listcell lc = (Listcell) li.getChildren().get(4);
						lc.setLabel(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE);
						lc.setStyle("padding-left:10px;");
						lc.setParent(li);

						lc = (Listcell) li.getLastChild();
						Hbox hbox = (Hbox) lc.getFirstChild();
						lc.removeChild(hbox);

						hbox = new Hbox();

						Image previewImg = new Image("/images/Preview_icn.png");
						previewImg.setTooltiptext("View Program");
						previewImg.setStyle("cursor:pointer;margin-right:5px;");
						previewImg.addEventListener("onClick", this);
						previewImg.setAttribute("type", "view");
						previewImg.setParent(hbox);

						Image editImg = new Image("/img/email_edit.gif");
						editImg.setTooltiptext("Edit Program");
						editImg.setStyle("cursor:pointer;margin-right:5px;");
						editImg.addEventListener("onClick", this);
						editImg.setAttribute("type", "Edit");
						editImg.setParent(hbox);

						Image addImg = new Image("/img/icons/add_icon.png");
						addImg.setTooltiptext("Add Cards");
						addImg.setStyle("cursor:pointer;margin-right:5px;");
						addImg.addEventListener("onClick", this);
						addImg.setAttribute("type", "add");
						addImg.setParent(hbox);

						Image exportImg = new Image("/images/loyalty/export.png");
						exportImg.setTooltiptext("Export");
						exportImg.setStyle("cursor:pointer;margin-right:5px;");
						exportImg.addEventListener("onClick", this);
						exportImg.setAttribute("type", "export");
						exportImg.setParent(hbox);

						String src = "";
						String toolTipTxtStr = "";
						String type = "";
						if(loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
							src = "/images/loyalty/suspend.png";
							toolTipTxtStr  = "Suspend";
							type = "Suspend";
						}
						else if(!loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE)) {
							src = "/img/play_icn.png";
							toolTipTxtStr = "Activate";
							type = "Activate";
						}

						Image statusImg = new Image(src);
						statusImg.setTooltiptext(toolTipTxtStr);
						statusImg.setStyle("cursor:pointer;margin-right:5px;");
						statusImg.addEventListener("onClick", this);
						statusImg.setAttribute("type", type);
						statusImg.setParent(hbox);

						Image delImg = new Image("/img/action_delete.gif");
						delImg.setTooltiptext("Delete Program");
						delImg.setStyle("cursor:pointer;margin-right:5px;");
						delImg.addEventListener("onClick", this);
						delImg.setAttribute("type", "delete");
						delImg.setParent(hbox);

						Image reportImg = new Image("/img/theme/home/reports_icon.png");
						reportImg.setTooltiptext("Report");
						reportImg.setStyle("cursor:pointer;margin-right:5px;");
						reportImg.addEventListener("onClick", this);
						reportImg.setAttribute("type", "report");
						reportImg.setParent(hbox);

						hbox.setParent(lc);	
					}
					else {
						String step2 = draftList[1];
						String step3 = draftList[2];
						String errMsg = "The program cannot be activated, ";
						if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
								step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)) {
							errMsg += "as no card-sets were found and not all tiers have been configured for this program.";
						}
						else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE) && 
								step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE)) {
							errMsg += "as all tiers have not been configured for this program";
						}
						else if(step2.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE) && 
								step3.equalsIgnoreCase(OCConstants.LOYALTY_DRAFT_STATUS_INCOMPLETE)){
							errMsg += "as no card-sets were found for this program.";
						}
						MessageUtil.setMessage(errMsg, "color:red", "TOP");
					}
					ltyPwdPopupId.close();
				}
			}
			else {
				MessageUtil.setMessage("Please enter the correct password", "color:red", "TOP");
				return;
			}

		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}
	}

	private void edit(Image img) {
		if(session.getAttribute("isPasswordRequired")!=null) {
			Boolean isPasswordRequired = (boolean)session.getAttribute("isPasswordRequired");
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbId.setValue("");
					ltyPwdTbId.setValue(encryptedPwd.trim());
					LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
					ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
					ltyPwdPopupId.setAttribute("type","edit");
					onClick$submitPwdBtnId();
					userActivitylogAdminUser("Edited", loyaltyProgram.getProgramName());
				} catch (Exception e) {
					logger.error("Loyalty edit ::"+e);
				}
				
			}
		}else {
			ltyPwdTbId.setValue("");
			LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
			ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
			ltyPwdPopupId.setAttribute("type","edit");
			ltyPwdPopupId.open(img, "middle_right");
			ltyPwdTbId.setStyle("border:1px solid #7F9DB9;");
		}
	}//edit()
	
	
	private void suspend(Image img){
		if(session.getAttribute("isPasswordRequired")!=null) {
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbId.setValue(encryptedPwd.trim());
					LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
					ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
					ltyPwdPopupId.setAttribute("type","suspend");
					onClick$submitPwdBtnId();
					userActivitylogAdminUser("Suspended", loyaltyProgram.getProgramName());
				} catch (Exception e) {
					logger.error("Loyalty suspend ::"+e);
				}
				
			}
		}else {
			ltyPwdTbId.setValue("");
			LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
			ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
			ltyPwdPopupId.setAttribute("type","suspend");
			ltyPwdPopupId.open(img, "middle_right");
			ltyPwdTbId.setStyle("border:1px solid #7F9DB9;");
		}
	}
	
	private void activate(Image img){
		if(session.getAttribute("isPasswordRequired")!=null) {
			Boolean isPasswordRequired = (boolean)session.getAttribute("isPasswordRequired");
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbId.setValue("");
					ltyPwdTbId.setValue(encryptedPwd.trim());
					LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
					ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
					ltyPwdPopupId.setAttribute("type","activate");
					onClick$submitPwdBtnId();
					userActivitylogAdminUser("Activated", loyaltyProgram.getProgramName());
				} catch (Exception e) {
					logger.error("Loyalty edit ::"+e);
				}
				
			}
		}else {
			ltyPwdTbId.setValue("");
			LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
			ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
			ltyPwdPopupId.setAttribute("type","activate");
			ltyPwdPopupId.open(img, "middle_right");
			ltyPwdTbId.setStyle("border:1px solid #7F9DB9;");
		}
	}

	private void report(Image img) {
		try {
			LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
			Long prgId=loyaltyProgram.getProgramId();
			MessageUtil.clearMessage();
			if(!loyaltyProgram.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_DRAFT)) {
				session.setAttribute("PROGRAM_REPORT_DETAILS",prgId);
				Redirect.goTo(PageListEnum.LOYALTY_PROGRAM_DETAILED_REPORT);
			}
			else {
				MessageUtil.setMessage("No reports found for this program.", "color:red","TOP");
			}
		} catch (Exception e) {
			logger.error("** Exception **" + e);
		}
	}//report()

	private void delete(Image img) {
		LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
		Long prgId=loyaltyProgram.getProgramId();

		int confirm = Messagebox.show("Are you sure you want to delete the program?", 
				"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
		if(confirm == Messagebox.OK) {
			LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
			ltyPrgmSevice.deleteProgram(prgId);
			MessageUtil.setMessage("Program deleted successfully.", "color:blue", "TOP");

			int totalSize = ltyPrgmSevice.getCount(userId, getSelectedStatus());
			programListPagingId.setTotalSize(totalSize);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			programListPagingId.setPageSize(pageSize);
			programListPagingId.setAttribute("onPaging", "topPaging");
			programListPagingId.addEventListener("onPaging", this); 
			programListPagingId.setActivePage(0);
			redraw(0, pageSize);
		}
	}//delete()

	public void onClick$exportBtnId$exportWinId() {
		Set<Listitem> list=exportWinId$cardSetLbId.getSelectedItems();
		logger.info("list"+list);
		String cardIdStr="";
		if(list != null) {
		for(Listitem listItem :list) {
			if(cardIdStr==null || cardIdStr.isEmpty()) {
				cardIdStr=listItem.getValue().toString();
			}else {
				cardIdStr+=","+listItem.getValue().toString();
			}
		}
		}
		if(cardIdStr.isEmpty()){
			MessageUtil.setMessage("Please select a card-set.","color:red","TOP");
			return;
		}
		Long prgmid=(Long) exportWinId.getAttribute("programId");
		
		Listitem li=exportWinId$statusLbId.getSelectedItem();
		String status=li.getValue().toString();
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		long size = ltyPrgmSevice.getCardsCount(cardIdStr,status,prgmid); 
		logger.info("prgmId in LoyaltyProgramController is  ::"+prgmid);

		logger.info("total size is  ::"+size);
		if(size == 0l){
			MessageUtil.setMessage("No Cards exists.","color:red","TOP");
			return;
		}

		JdbcResultsetHandler jdbcResultSetHandler = null;
		try {

			String userName = GetUser.getUserName();
			String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
			String exportDir = usersParentDirectory + "/" + userName + "/Export/Cards" ;
			File downloadDir = new File(exportDir);

			StringBuffer sb = null;
			if(downloadDir.exists()){
				try {
					FileUtils.deleteDirectory(downloadDir);
					logger.info(downloadDir.getName() + " is deleted");
				} catch (Exception e) {
					logger.error("Exception ::" , e);
					logger.info(downloadDir.getName() + " is not deleted");
				}
			}
			if(!downloadDir.exists()){
				downloadDir.mkdirs();
			}

			String filePath = exportDir +File.separator+  "LoyaltyCards_" +	MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);

			filePath = filePath + ".csv";
			logger.info("Download File path : " + filePath);
			File file = new File(filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
			bw.write("\"Card Number\",\"Card Pin\",\"Status\",\r\n");

			jdbcResultSetHandler = new JdbcResultsetHandler();
			String sqlqry = "";
			if(status==null || status.trim().equalsIgnoreCase("All")) {
				sqlqry = "select card_number,card_pin,status from loyalty_cards where card_set_id in ("+cardIdStr+") AND program_id="+prgmid+" ";
			}else if(status != null && status.trim().equalsIgnoreCase("Registered")) {
				sqlqry = "select card_number,card_pin,status from loyalty_cards where card_set_id in ("+cardIdStr+") and registered_flag = '"+OCConstants.FLAG_YES+"' AND program_id="+prgmid+" ";
			}
			else{
				sqlqry = "select card_number,card_pin,status from loyalty_cards where card_set_id in ("+cardIdStr+") and status ='"+status.trim()+"' AND program_id="+prgmid+" ";
			}
			logger.info("sqlqry ==== "+sqlqry);
			jdbcResultSetHandler.executeStmt(sqlqry);

			List<String> recordList = jdbcResultSetHandler.getRecords();
			if(recordList == null) {
				logger.info("Error getting from jdbcResulSetHandler  ...");
			}
			logger.info("record list is "+recordList.size());
			int countingCount = recordList.size();

			while(countingCount <= size ){
				sb = new StringBuffer();
				String statusStr = "";

				for (String eachRecord : recordList) {

					logger.info("eachRecord"+eachRecord);
					String[] strArr = eachRecord.split(";");
					sb.append("\"");sb.append(strArr[0].replace("card_number=", ""));sb.append("\",");
					sb.append("\"");sb.append(strArr[1].replace("card_pin=", ""));sb.append("\",");
					sb.append("\"");sb.append(strArr[2].replace("status=", ""));sb.append("\"");
					sb.append("\r\n");

				}

				bw.write(sb.toString());
				sb = null;
				//System.gc();
				recordList = jdbcResultSetHandler.getRecords();
				if(recordList == null) break;
				countingCount += recordList != null ?recordList.size() :0;
				logger.info("countingCount"+countingCount);
			}

			bw.flush();
			bw.close();
			Filedownload.save(file, "text/plain");

		} catch (Exception e) {
			logger.error("Error  :: ",e);
		}finally{
			if(jdbcResultSetHandler != null)jdbcResultSetHandler.destroy();
		}

		exportWinId$cardSetLbId.setSelectedIndex(-1);
		exportWinId$statusLbId.setSelectedIndex(0);
		exportWinId$selctedCardSetId.setValue("");
		exportWinId.setVisible(false);
	}//onClick$exportBtnId$exportWinId()

	private void setCards(List<LoyaltyCardSet> list) {
		
		List<Listitem> listitems = exportWinId$cardSetLbId.getItems();
		for (int i=exportWinId$cardSetLbId.getItemCount()-1; i>=0 ;i--) {
			exportWinId$cardSetLbId.removeItemAt(i);
		}
		
		Listitem li=null;
		Listcell cell = null;
		for(LoyaltyCardSet loyaltyCardSet:list) {
			li= new Listitem();
			li.setValue(loyaltyCardSet.getCardSetId());
			/*cell = new Listcell();
			cell.setParent(li);
			*/
			cell = new Listcell(loyaltyCardSet.getCardSetName());
			cell.setParent(li);
			li.setParent(exportWinId$cardSetLbId);
		}
	}//setCards()

	public void onSelect$cardSetLbId$exportWinId() {
		String selectedCardstr = "";
		Set<Listitem> set=exportWinId$cardSetLbId.getSelectedItems();
		for(Listitem li:set) {
			if(selectedCardstr.trim().length() == 0) {
				selectedCardstr = li.getLabel();
			}else{
				selectedCardstr = selectedCardstr + ", " + li.getLabel(); 
			}
		}
		exportWinId$selctedCardSetId.setValue(selectedCardstr);
	}//onSelect$cardSetLbId$exportWinId()

	private void add(Image img) {
		LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
		if(loyaltyProgram.getTierEnableFlag() == OCConstants.FLAG_YES) {
			setTiersList(loyaltyProgram.getNoOfTiers());
		}
		addWinId$autoAssignRadioId.setDisabled(false);
		addWinId$linkTierRadioId.setDisabled(false);
		/*else{
			addWinId$autoAssignRadioId.setDisabled(true);
			addWinId$linkTierRadioId.setDisabled(true);
		}*/
		addWinId.setAttribute("programId", loyaltyProgram.getProgramId());
		addWinId.setVisible(true);
		addWinId.setPosition("center");
		addWinId.doHighlighted();
	}//add()
	
	private void setTiersList(int noOfTiers) {
		Components.removeAllChildren(addWinId$selTierLbId);
		Listitem li = new Listitem("Select Tier" , 0);
		li.setParent(addWinId$selTierLbId);
		li.setSelected(true);
		for(int i=1 ; i <= noOfTiers ; i++){
			li = new Listitem("Tier "+i , i);
			li.setParent(addWinId$selTierLbId);
		}
	}

	private void view(Image img) {
		if(session.getAttribute("isPasswordRequired")!=null) {
			Boolean isPasswordRequired = (boolean)session.getAttribute("isPasswordRequired");
			if(isPasswordRequired!=null && isPasswordRequired) {
				LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
				LoyaltyThresholdAlerts loyaltyThresholdAlerts = ltyPrgmSevice.findPwdByUserID(userId);
				try {
					String encryptedPwd = EncryptDecryptLtyMembshpPwd.decryptProgramPwd(loyaltyThresholdAlerts.getLtySecurityPwd());
					ltyPwdTbId.setValue("");
					ltyPwdTbId.setValue(encryptedPwd.trim());
					LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
					ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
					ltyPwdPopupId.setAttribute("type","view");
					onClick$submitPwdBtnId();
					userActivitylogAdminUser("viewed", loyaltyProgram.getProgramName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else {
			ltyPwdTbId.setValue("");
			LoyaltyProgram loyaltyProgram =  (LoyaltyProgram)((Listitem)img.getParent().getParent().getParent()).getValue();
			ltyPwdPopupId.setAttribute("programId", loyaltyProgram.getProgramId());
			ltyPwdPopupId.setAttribute("type","view");
			ltyPwdPopupId.open(img, "middle_right");
			ltyPwdTbId.setStyle("border:1px solid #7F9DB9;");
		}
		
	}//view()


	public void onClick$filterBtnId(){
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		int totalSize = ltyPrgmSevice.getCount(userId, getSelectedStatus());
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		programListPagingId.setTotalSize(totalSize);
		programListPagingId.setPageSize(pageSize);
		programListPagingId.setActivePage(0);
		programListPagingId.setAttribute("onPaging", "topPaging");
		programListPagingId.addEventListener("onPaging", this); 
		redraw(programListPagingId.getActivePage()*programListPagingId.getPageSize(), programListPagingId.getPageSize());
	}//onClick$filterBtnId()
    public String orderby_colName="modifiedDate",desc_Asc="desc"; 
    
    public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }	
	public void onClick$sortbyProgramName(){
		orderby_colName = "programName";
		desc2ascasc2desc();	
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
	public void onClick$sortbyDescription(){
		orderby_colName = "description";
		desc2ascasc2desc();	
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
	public void onClick$sortbyCreatedOn(){
		orderby_colName = "createdDate";
		desc2ascasc2desc();	
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
	public void onClick$sortbyMembershipType(){
		orderby_colName = "membershipType";
		desc2ascasc2desc();	
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
/*	public void onclick$sortbyCardsAvailable(){
		orderby_colName = "campaignName";
		desc2ascasc2desc();	
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
	*/	public void onClick$sortbyPrivilegeTiers(){
		orderby_colName = "noOfTiers";
		desc2ascasc2desc();	
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}


	public void onClick$resetAnchId(){
		progStatusLb.setSelectedIndex(0);
		LoyaltyProgramService ltyPrgmSevice = new LoyaltyProgramService();
		int totalSize = ltyPrgmSevice.getCount(userId, getSelectedStatus());
		pageSizeLbId.setSelectedIndex(1);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		programListPagingId.setTotalSize(totalSize);
		programListPagingId.setPageSize(pageSize);
		programListPagingId.setActivePage(0);
		programListPagingId.setAttribute("onPaging", "topPaging");
		programListPagingId.addEventListener("onPaging", this); 
		orderby_colName="modifiedDate";
		desc_Asc="desc";
		redraw(programListPagingId.getActivePage()*programListPagingId.getPageSize(), programListPagingId.getPageSize());
	}//onClick$resetAnchId()

	public void onSelect$pageSizeLbId(){

		try {
			programListPagingId.setActivePage(0);
			if(progListlbId.getItemCount() == 0 ) {
				logger.info("No Loyalty programs found for this user...");
				return;
			}
			changeRows(pageSizeLbId.getSelectedItem().getLabel(),programListPagingId);
		} catch (Exception e) {
			logger.error("Exception :: errorr while getting from the changeRows method",e);
		}
	}//onSelect$pageSizeLbId()

	private void changeRows(String selStr, Paging programListPagingId) {
		try {
			if(programListPagingId!=null){
				int pNo = Integer.parseInt(selStr);
				programListPagingId.setPageSize(pNo);
				programListPagingId.setPageSize(pNo);
				redraw(0,pNo);
			}
		} catch (Exception e) {
			logger.error("Exception while getting the programs...",e);

		}
	}//changeRows()

	public void onClick$cancelBtnId$exportWinId() {
		exportWinId$cardSetLbId.setSelectedIndex(-1);
		exportWinId$statusLbId.setSelectedIndex(0);
		exportWinId$selctedCardSetId.setValue("");
		exportWinId.setVisible(false);
	}//onClick$cancelBtnId$exportWinId()

	public void onClick$cancelBtnId$addWinId() {
		addWinId$cardSetNameTbId.setValue("");
		addWinId$quantityNameTbId.setValue("");
		addWinId$genTypeLbId.setSelectedIndex(0);
		addWinId$ltyCardTypeRadioGrId.setSelectedIndex(0);
		addWinId$statusLbId.setSelectedIndex(0);
		addWinId.setVisible(false);
	}//onClick$cancelBtnId$addWinId()
	
	private void userActivitylogAdminUser(String functionalityType, String lolyaltyProgramName) {
		try {
			if (userActivitiesDaoForDML != null) {
				Users currentUser = GetUser.getUserObj();
				String name = null;
				if (currentUser.getLastName() != null && !currentUser.getLastName().isEmpty()) {
					name = Utility.getOnlyUserName(currentUser.getFirstName() + " " + currentUser.getLastName());
				} else {
					name = Utility.getOnlyUserName(currentUser.getFirstName());
				}
				Users adminUser = usersDao.find(currentOCAdminUserId);
				String adminUsername = Utility.getOnlyUserName(adminUser.getUserName());
				userActivitiesDaoForDML.addToActivityList(ActivityEnum.LOYALTY_ADMIN_PASSWORD_BYPASS, currentUser, true,
						adminUsername, functionalityType, name, lolyaltyProgramName);
			}
		} catch (Exception e) {
			logger.error("LoyaltyProgramController :: userActivitylogAdminUser() ::" + e);
		}
	}
}

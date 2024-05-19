package org.mq.optculture.controller.loyalty;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.UserEmailAlert;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltySettingsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDaoForDML;
import org.mq.optculture.data.dao.UserEmailAlertDao;
import org.mq.optculture.data.dao.UserEmailAlertDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.data.dao.ValueCodesDaoForDML;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;


public class ValueCodesController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Listbox valueCodePerPageLBId;
	private Textbox valueTbId,valueCodeNameTbId,descriptionTbId;
	private Button updateValueCodesBtnId,saveValueCodesBtnId,cancelBtnId;
	private long ValueCodeId;
	private Listbox valueCodeLbId;
	private Window ValueCodeEditWinId;
	private Textbox ValueCodeEditWinId$valueCodeNameTbId,ValueCodeEditWinId$descriptionTbId;
	private Paging valueCodePagingId;
	private String key;
	private String menuSrc;
	
	public ValueCodesController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Value Codes","",style,true);
		
		menuSrc = (String) Sessions.getCurrent().getAttribute("menuSrc");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception  {
		super.doAfterCompose(comp);
		try {
			logger.debug("------------------menuSrc:::::::"+menuSrc);
			
			//Changes Loyalty Spec rules
			valueCodeNameTbId.setValue("");
			valueCodeNameTbId.setDisabled(false);
			descriptionTbId.setValue("");
			saveValueCodesBtnId.setLabel("Save");
			saveValueCodesBtnId.removeAttribute("ValueCodeObj");
			int tempCount = Integer.parseInt(valueCodePerPageLBId
					.getSelectedItem().getLabel());
			valueCodePagingId.addEventListener("onPaging", this);
			ValueCodesGrid(null,0,tempCount,0);
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		} 
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		
		// TODO Auto-generated method stub
			super.onEvent(event);
			
			
			
			if (event.getTarget() instanceof Paging) {
				Paging paging = (Paging) event.getTarget();
				int desiredPage = paging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				if (paging.getId().equals("valueCodePagingId")) {
				ValueCodesGrid(key,ofs,pSize,desiredPage);
				}
			}
			
			
			if(event.getTarget() instanceof Image) {
				Image img =(Image)event.getTarget();
				String imgEdit =(String)img.getAttribute("Edit");
				if("edit".equals(imgEdit)){
					Listitem lcImg=(Listitem) img.getParent().getParent().getParent();
					ValueCodes valcode=lcImg.getValue();
					valueCodeNameTbId.setValue(valcode.getValuCode());
					valueCodeNameTbId.setDisabled(true);
					descriptionTbId.setValue(valcode.getDescription());
					saveValueCodesBtnId.setLabel("Update");
					saveValueCodesBtnId.setAttribute("ValueCodeObj", valcode);
					cancelBtnId.setVisible(true);
					//ImageEdit(lcImg.getValue());
				}
				
			}
					
	}
	
	
	private void ValueCodesGrid(String SerachKey,int startIndex, int endCount,int pageSize) throws Exception {
        for(int cnt=valueCodeLbId.getItemCount(); cnt>0;cnt--){//For Removing children component UI.
        	valueCodeLbId.removeItemAt(cnt-1);
        }
		UserOrganization OrgID=GetUser.getUserObj().getUserOrganization();
		ValueCodesDao valueCodesDao = (ValueCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
		List<ValueCodes> ValueCodeList =null;
		
		
		
		SerachKey =SerachKey == null ? Constants.STRING_NILL : SerachKey ;
		Long totRecords=valueCodesDao.findNonFbpCountByValueCodeByOrgIdAndKey(OrgID.getUserOrgId(),SerachKey);
	try {	
		//Pagination related code for listbox
		valueCodePagingId.setPageSize(endCount);
		valueCodePagingId.setTotalSize(totRecords.intValue());		
		valueCodePagingId.setActivePage(pageSize);
	}
	catch(Exception e) {
		logger.info("e===>"+e);
	}
		
		ValueCodeList = valueCodesDao.findByNonFbpValueCodeByOrgIdandSearchKey(OrgID.getUserOrgId(),SerachKey,startIndex,endCount);


		for (ValueCodes valCodes : ValueCodeList) {

			Listcell lc;
			Listitem li;
			li = new Listitem();

			lc = new Listcell(valCodes.getValuCode());
			lc.setParent(li);

			lc = new Listcell(valCodes.getDescription());
			lc.setParent(li);

			Hbox hbox = new Hbox();
			
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit Value-Code");
			editImg.setStyle("cursor:pointer;margin-right:5px;margin-left: 50px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("Edit", "edit");
			editImg.setParent(hbox);
			
			lc = new Listcell();
			hbox.setParent(lc);

			lc.setParent(li);
			li.setParent(valueCodeLbId);
			li.setValue(valCodes);

		}

	
	}
	
	public void onClick$saveValueCodesBtnId(){
	try {
		addValueCodeData();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.info("onClick$saveValueCodesBtnId error===>"+e);
	}	
	}
	
	public void onClick$cancelBtnId(){
		try {
			int tempCount = Integer.parseInt(valueCodePerPageLBId
					.getSelectedItem().getLabel());
			ValueCodesGrid(null,0,tempCount,0);
			valueCodeNameTbId.setValue("");
			valueCodeNameTbId.setDisabled(false);
			descriptionTbId.setValue("");
			saveValueCodesBtnId.setLabel("Save");
			saveValueCodesBtnId.removeAttribute("ValueCodeObj");
			cancelBtnId.setVisible(false);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("onClick$cancelBtnId error===>"+e);
		}	
		}
	
	private void addValueCodeData() throws Exception {
		String valueCode = valueCodeNameTbId.getValue().trim();
		if (valueCode == null || valueCode.trim().length() == 0) {
			MessageUtil.setMessage("Please provide Value-Code Name. Name cannot be left empty.", "color:red", "TOP");
			valueCodeNameTbId.setFocus(true);
			return;
		}
		if (valueCode.length() > 0 && !Utility.validateName(valueCode)) {
			MessageUtil.setMessage("Special characters not allowed in Value-Code Name.", "color:red", "TOP");
			return;
		}
		if(valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)|| valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_CURRENCY) 
				||valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
				MessageUtil.setMessage("Please provide a value code other than default value codes.", "color:red", "TOP");
				return;
		}
		UserOrganization OrgID=GetUser.getUserObj().getUserOrganization();
		ValueCodesDao valueCodesDao = (ValueCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
		ValueCodesDaoForDML valueCodesDaoForDML = (ValueCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.VALUE_CODES_DAO_FOR_DML);
		ValueCodes vcode=null;
		vcode=(ValueCodes) saveValueCodesBtnId.getAttribute("ValueCodeObj");
		if(vcode==null){
			List<ValueCodes> ValueCodeList =null;
			ValueCodeList = valueCodesDao.findValueCode(OrgID.getUserOrgId(),valueCode);
			if(ValueCodeList!=null && ValueCodeList.size()>0){
				MessageUtil.setMessage("Value-Code already exist. Please provide another Value-Code.", "color:red", "TOP");
				return;
			}
			vcode=new ValueCodes();
			vcode.setCreatedBy(GetUser.getUserObj().getUserId().toString());
			vcode.setCreatedDate(MyCalendar.getInstance());
			vcode.setOrgId(GetUser.getUserObj().getUserOrganization().getUserOrgId());
			vcode.setValuCode(valueCodeNameTbId.getValue());
		}
		vcode.setDescription(descriptionTbId.getValue());
		vcode.setModifiedBy(GetUser.getUserObj().getUserId().toString());
		vcode.setModifiedDate(MyCalendar.getInstance());
		valueCodesDaoForDML.saveOrUpdate(vcode);
		int tempCount = Integer.parseInt(valueCodePerPageLBId
				.getSelectedItem().getLabel());
		ValueCodesGrid(null,0,tempCount,0);
		valueCodeNameTbId.setValue("");
		valueCodeNameTbId.setDisabled(false);
		descriptionTbId.setValue("");
		saveValueCodesBtnId.setLabel("Save");
		saveValueCodesBtnId.removeAttribute("ValueCodeObj");
	}
	
	private void ImageEdit(ValueCodes valcode) {
		
		
		ValueCodeEditWinId.setVisible(true);
		ValueCodeEditWinId.setPosition("center");
		ValueCodeEditWinId.doHighlighted();
		ValueCodeId=valcode.getId();
		ValueCodeEditWinId$valueCodeNameTbId.setValue(valcode.getValuCode());
		ValueCodeEditWinId$descriptionTbId.setValue(valcode.getDescription());
		ValueCodeEditWinId$valueCodeNameTbId.setDisabled(true);
		
		
	}
	
	public void onClick$updateValueCodesBtnId$ValueCodeEditWinId() throws Exception{
		ValueCodesDaoForDML valueCodesDaoForDML = (ValueCodesDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.VALUE_CODES_DAO_FOR_DML);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ModifiedDate = format.format(cal.getTime());
		valueCodesDaoForDML.updateValueCodeById(ValueCodeId,ValueCodeEditWinId$descriptionTbId.getValue(),ModifiedDate,GetUser.getUserId());
		MessageUtil.setMessage("Description saved successfully.", "color:blue", "TOP");
		ValueCodeEditWinId.setVisible(false);
		int tempCount = Integer.parseInt(valueCodePerPageLBId
				.getSelectedItem().getLabel());
		ValueCodesGrid(null,0,tempCount,0);
	}
	
	public void onChanging$searchBoxId(InputEvent event) {
		key = event.getValue();
		try {
			int tempCount = Integer.parseInt(valueCodePerPageLBId
					.getSelectedItem().getLabel());
			ValueCodesGrid(key,0,tempCount,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("e===>"+e);
		}
	}
	
	public void onSelect$valueCodePerPageLBId() {

		try {

			int tempCount = Integer.parseInt(valueCodePerPageLBId
					.getSelectedItem().getLabel());
			ValueCodesGrid(key,0,tempCount,0);

		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}

	}// onSelect$pageSizeLbId()

	
}

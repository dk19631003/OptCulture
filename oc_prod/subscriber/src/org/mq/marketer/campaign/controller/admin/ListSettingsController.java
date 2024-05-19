package org.mq.marketer.campaign.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MastersToTransactionMappings;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.MastersToTransactionMappingsDao;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.POSMappingDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

public class ListSettingsController extends GenericForwardComposer implements EventListener {

	UsersDao usersDao=  null;
	POSMappingDao posMappingDao = null;
	POSMappingDaoForDML posMappingDaoForDML = null;
	Rows contactRowsId,loyaltyRowsId;
	Groupbox listMapLoyaltyGroupbox;
	private boolean isAdmin;
	Session sessionScope = null;
	private static String contctGenFieldStr = PropertyUtil.getPropertyValue("defaultContactMapFieldList");
	private static String[] defaultFieldArray = StringUtils.split(contctGenFieldStr, ','); 
	private static String[] udfSetStr ={"UDF1" ,"UDF2" ,"UDF3","UDF4", "UDF5","UDF6","UDF7","UDF8",
		"UDF9","UDF10",	"UDF11","UDF12","UDF13","UDF14","UDF15"};
	private static String contctGenFieldStrLoyalty = PropertyUtil.getPropertyValue("defaultLoyaltyMapFieldList");
	private static String[] defaultFieldArrayLoyalty = StringUtils.split(contctGenFieldStrLoyalty, ',');
	private static String ANNIVERSARY = "Anniversary";
	private static String BIRTHDAY = "BirthDay";
	private static String CREATEDDATE = "Created Date";
	private static String ZIP = "ZIP";
	private static String MOBILE = "Mobile";
	private Users users;
	private boolean loyaltyFlag = false;

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public ListSettingsController() {}
	
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("List Settings","",style,true);
		
		usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		posMappingDaoForDML= (POSMappingDaoForDML)SpringUtil.getBean("posMappingDaoForDML");
		users = GetUser.getUserObj();
		sessionScope = Sessions.getCurrent();
		isAdmin = (Boolean)sessionScope.getAttribute("isAdmin");
		List<POSMapping> recordList = null;
		recordList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", users.getUserId());
		
		//Changes 2.5.4.0
		recordList.addAll(posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_LOYALTY+"'", users.getUserId()));
		
		if(recordList == null || recordList.size() == 0 ) {
			
			Utility.setUserDefaultMapping(users.getUserId());
			/*Set<String> set = genFieldContMap.keySet();
			for (String string : set) {
				defaultGenFieldMapp(string ,Constants.POS_MAPPING_TYPE_CONTACTS);
			}*/
			recordList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", users.getUserId());
		}
		
		//Changes 2.5.4.0 start
		
		String loyaltyServicetype = GetUser.getUserObj().getloyaltyServicetype();
		loyaltyFlag = loyaltyServicetype != null && !loyaltyServicetype.isEmpty() && !loyaltyServicetype.equals("SB");
		logger.info("loyaltyFlag==>"+loyaltyFlag);
		logger.info(loyaltyFlag);
		
		if(loyaltyFlag) {
			listMapLoyaltyGroupbox.setVisible(true);
		}
		
		
		//Changes 2.5.4.0 end
		
		deafultPOSMappingSettings(recordList);
		
		
	} // doAfterCompose
	
	
	
	
	private void deafultPOSMappingSettings(List<POSMapping> recordList) {
try {
	//		//logger.debug("USerId is::"+user.getUserId());
			
			/*List<POSMapping> recordList = null;
			recordList = posMappingDao.findAllByUserId(users.getUserId());*/
			MastersToTransactionMappingsDao masterToTranscDao=(MastersToTransactionMappingsDao)SpringUtil.getBean("mastersToTransactionMappingsDao");
			List<MastersToTransactionMappings> tempList = masterToTranscDao.findByUserId(users.getUserId());
			
			if(recordList == null || recordList.size() == 0) return;
			
			for (POSMapping posMapping : recordList) {
				
				Row row = new Row();
				
	//			POS Attribute
				Textbox posAttrTextBx = new Textbox(posMapping.getPosAttribute());
				posAttrTextBx.setWidth("100px");
				posAttrTextBx.setParent(row);
				
	
				
	//			Custom Field
				Listbox optMappingListBx = null;
	//			logger.debug("posMapping.getMappingType() is :: "+posMapping.getMappingType());
				
				if(posMapping.getMappingType().trim().equals("BCRM")) continue;
				
				optMappingListBx =  createContactPosMappingListbox();
				optMappingListBx.setParent(row);
				/*if(posMapping.getMappingType().trim().equalsIgnoreCase(Constants.POS_MAPPING_TYPE_CONTACTS)) {
					
				}else if(posMapping.getMappingType().trim().equalsIgnoreCase(Constants.POS_MAPPING_TYPE_SALES)) {
					posMappingListBx =  createSalePosMappingFieldListBox();
					posMappingListBx.setParent(row);
				}else if(posMapping.getMappingType().trim().equals(Constants.POS_MAPPING_TYPE_SKU)) {
					posMappingListBx =  createSkuPosMappingFieldListBox();
					posMappingListBx.setParent(row);
				}*/
			logger.info("optMappingListBx===>"+optMappingListBx);
				if(optMappingListBx == null) continue;
				
				optMappingListBx.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
				optMappingListBx.addEventListener("onSelect", this);
				List posMappingChilItemList = optMappingListBx.getChildren();
				Listitem tempItem= null;
				for (Object object : posMappingChilItemList) {
					tempItem = (Listitem)object;
					if(tempItem.getLabel().equals(posMapping.getCustomFieldName())){
						optMappingListBx.setSelectedItem(tempItem);
					}
				}
				
				
				
	//			Data Type
				String dataTypeStr = posMapping.getDataType();
				
				
				Div dataTypeDiv = new Div();
	//			dataTypeDiv.setWidth("350px");
				Listbox dataTypelb = createDataTypeListBox();
				dataTypelb.setParent(dataTypeDiv);
				
				Listbox dateFormatListBx = createDateFormatListbox();
				dateFormatListBx.setParent(dataTypeDiv);
				dateFormatListBx.setSelectedIndex(0);
				dateFormatListBx.setVisible(false);
				List ChildItemList =  dataTypelb.getChildren();
				
				for (Object object : ChildItemList) {
					
					Listitem listItem = (Listitem)object;
					
					if(dataTypeStr.contains("Date") &&  listItem.getLabel().equals("Date")) {
						dataTypelb.setSelectedItem(listItem);
						dateFormatListBx.setVisible(true);
						
						List dateFormatList = dateFormatListBx.getChildren();
						dataTypeStr =  dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));
						
						for (Object obj : dateFormatList) {
							
							Listitem tempListItem = (Listitem)obj;
							if(tempListItem.getLabel().equals(dataTypeStr)) {
								dateFormatListBx.setSelectedItem(tempListItem);
								break;
							}
						}
					
						
					}else if(listItem.getLabel().equals(dataTypeStr)) {
						dataTypelb.setSelectedItem(listItem);
						break;
					}
					
				} // for
				
				
				
				dataTypelb.addEventListener("onSelect", this);
				dataTypeDiv.setParent(row);
				
				// Optional Value
				
				Div optDiv=new Div();
				optDiv.setParent(row);
				
				Combobox cb = new Combobox();
				cb.setSclass("cb_100w");
				cb.setParent(optDiv);
				
				if(posMapping.getOptionalValues()!=null) {
					String optValues[] = posMapping.getOptionalValues().split(Constants.ADDR_COL_DELIMETER);
					for (String optVal : optValues) {
						cb.appendItem(optVal);
					}
				}
				if(cb.getItemCount()>0) cb.setSelectedIndex(0);
				
				//Optional Value Add Action
				Image optAddValImg = new Image();
				optAddValImg.setSrc("/img/action_add.jpg");
				optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
				optAddValImg.addEventListener("onClick", this);
				optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
				optAddValImg.setParent(optDiv);			
				
				//Optional Value Add Action
				Image optDelValImg = new Image();
				optDelValImg.setSrc("/img/action_delete.gif");
				optDelValImg.setStyle("cursor:pointer;");
				optDelValImg.addEventListener("onClick", this);
				optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
				optDelValImg.setParent(optDiv);			
				
	//			Display Label
				Textbox dispLabelTextBx = new Textbox(posMapping.getDisplayLabel());
				dispLabelTextBx.setWidth("100px");
				dispLabelTextBx.setParent(row);
				
				//Default Merge-Tags
				
				Div optdefValDiv=new Div();
				optdefValDiv.setParent(row);
				
				Combobox defValCb = new Combobox();
				Comboitem emptyValItem = new Comboitem("");
				emptyValItem.setParent(defValCb);
				defValCb.setSclass("cb_100w");
				defValCb.setParent(optdefValDiv);
				
				if(posMapping.getDefaultPhValueSet()!=null) {
					String defValues[] = posMapping.getDefaultPhValueSet().split(Constants.ADDR_COL_DELIMETER);
					for (String defVal : defValues) {
						defValCb.appendItem(defVal);
					}
				}
				defValCb.setValue(posMapping.getDefaultPhValue() != null ? posMapping.getDefaultPhValue() : "");
				
				//Optional Value Add Action
				Image defValAddValImg = new Image();
				defValAddValImg.setSrc("/img/action_add.jpg");
				defValAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
				defValAddValImg.addEventListener("onClick", this);
				defValAddValImg.setAttribute("TYPE", "POS_ADD_DEFAULT_VALUE");
				defValAddValImg.setParent(optdefValDiv);			
				
				//Optional Value Add Action
				Image defValDelValImg = new Image();
				defValDelValImg.setSrc("/img/action_delete.gif");
				defValDelValImg.setStyle("cursor:pointer;");
				defValDelValImg.addEventListener("onClick", this);
				defValDelValImg.setAttribute("TYPE", "POS_DEL_DEFAULT_VALUE");
				defValDelValImg.setParent(optdefValDiv);			
				
				
				/*//TODO need to delete
				Textbox defPhValTextBx = new Textbox(posMapping.getDefaultPhValue());
				defPhValTextBx.setWidth("100px");
				defPhValTextBx.setParent(row);*/
				
				
	//			Unique priority
				Intbox priIntBx = new Intbox();
				priIntBx.setMaxlength(1);
				priIntBx.setWidth("30px");
				if(posMapping.getUniquePriority()!=null) priIntBx.setValue(posMapping.getUniquePriority());
				
				priIntBx.setDisabled(true);
				if(isAdmin) {
					priIntBx.setDisabled(false);
				}
				priIntBx.setParent(row);
				
				Div div=new Div();
				div.setParent(row);
				
				boolean canproceed = false;
				
				if(tempList != null && tempList.size() >0) {
					
					for(MastersToTransactionMappings eachmapping : tempList) {
						
						if( ( eachmapping.getParentId().getPosId().longValue() == posMapping.getPosId().longValue() ) || 
								( eachmapping.getChildId().getPosId().longValue() == posMapping.getPosId().longValue() )) canproceed = true;
						
					}
				}
				//Delete Action
				if(!canproceed) {
					
					Image delImg = new Image();
					delImg.setSrc("/images/action_delete.gif");
					delImg.setStyle("cursor:pointer;");
					delImg.setTooltiptext("Delete");
					delImg.addEventListener("onClick", this);
					delImg.setParent(div);
					delImg.setAttribute("TYPE", "POS_CONTACT_MAPPING");
					
					if(!optMappingListBx.getSelectedItem().getLabel().startsWith("UDF")) delImg.setVisible(false);
				}
				
				if(!optMappingListBx.getSelectedItem().getLabel().startsWith("UDF")) {
					
					dataTypelb.setDisabled(true);
					
					
				}
				
				logger.info("MappingType===>"+posMapping.getMappingType());
				
				if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_CONTACTS))
				row.setParent(contactRowsId);
				
				else if(posMapping.getMappingType().equals(Constants.POS_MAPPING_TYPE_LOYALTY))	
				row.setParent(loyaltyRowsId);

				row.setAttribute("referenceId", posMapping);
				
			} //for
			
			
			
} catch (WrongValueException e) {
	// TODO Auto-generated catch block
	logger.error("Exception  ::", e);
}
	} //deafultSettingPOSMappingSettings
	
	
	
	
	//create ContactGenField ListBox
	private Listbox createContactPosMappingListbox() {
		
		Listbox dateFormatListBx = new Listbox();
		Listitem tempItem = null;
		
		//genralFieldList
		
		if(loyaltyFlag) {
			for (int i=0; i< defaultFieldArrayLoyalty.length; i++) {
//				tempStr = (String)contactGenFieldList.get(i);
				tempItem = new Listitem(defaultFieldArrayLoyalty[i]);
				tempItem.setParent(dateFormatListBx);
			}
			
			}
		else {
		for (int i=0; i< defaultFieldArray.length; i++) {
//						tempStr = (String)contactGenFieldList.get(i);
			tempItem = new Listitem(defaultFieldArray[i]);
			tempItem.setParent(dateFormatListBx);
		}
		//loyaltyFieldList
		}
		
		
		//UDF FieldList
		for(int i=0; i<udfSetStr.length; i++) { 
			tempItem = new Listitem(udfSetStr[i]);
			tempItem.setParent(dateFormatListBx);
		}
		
		dateFormatListBx.setMold("select");
		return dateFormatListBx;
		
	}// createContactPosMappingListbox()
	
	//DataType
	private Listbox createDataTypeListBox() {
		
		Listbox dataTypelb = new Listbox();
		
		Listitem tempItem = new Listitem("String");
		tempItem.setParent(dataTypelb);
		
		//String","Date","Number","Double","Boolean"
		tempItem = new Listitem("Date");
		tempItem.setParent(dataTypelb);
		
		tempItem = new Listitem("Number");
		tempItem.setParent(dataTypelb);
		
		tempItem = new Listitem("Double");
		tempItem.setParent(dataTypelb);
		
		tempItem = new Listitem("Boolean");
		tempItem.setParent(dataTypelb);
		dataTypelb.setMold("select");
		return dataTypelb;
		
	} //createDataTypeListBox()
	
	
	//DateFormat
	private Listbox createDateFormatListbox() {

		Listbox dateFormatListBx = new Listbox();
		
		Listitem tempItem = new Listitem("dd/MM/yyyy");
		tempItem.setParent(dateFormatListBx);
		
		//String","Date","Number","Double","Boolean"
		tempItem = new Listitem("dd-MM-yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM-dd-yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM-dd-yy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd/MM/yyyy HH:mm");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yyyy HH:mm");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("MM/dd/yyyy HH:mm:ss");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd-MMMMM-yyyy");
		tempItem.setParent(dateFormatListBx);
		
		tempItem = new Listitem("dd-MMMMM-yy");
		tempItem.setParent(dateFormatListBx);
		
		dateFormatListBx.setMold("select");
		return dateFormatListBx;
	} //createDateFormatListbox()
			
	
public void onClick$addLoyaltyMapBtnId() {
		
		Row tempRow = new Row();
		
		Textbox tempTextBox;
		
		//POS Attr
		tempTextBox = new Textbox();
		tempTextBox.setParent(tempRow);
		
		
		Listbox tempListbox = null;
		
		//Custom Field
		tempListbox = createContactPosMappingListbox();
		tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setSelectedIndex(0);
		tempListbox.setDisabled(true);
		tempListbox.setParent(tempRow);
		
		//DataType
		Div tempDiv = new Div();
		
		//Data Type
		tempListbox = createDataTypeListBox();
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setSelectedIndex(0);
		tempListbox.setDisabled(true);
		tempListbox.setParent(tempDiv);
		
		//Date Format
		tempListbox = createDateFormatListbox();
		tempListbox.setSelectedIndex(0);
		tempListbox.setVisible(false);
		tempListbox.setParent(tempDiv);
		
		tempDiv.setParent(tempRow);
		
		// Optional Value
		Div optDiv=new Div();
		optDiv.setParent(tempRow);
		
		Combobox cb = new Combobox();
		cb.setSclass("cb_100w");
		cb.setParent(optDiv);
		
		
		//Optional Value Add Action
		Image optAddValImg = new Image();
		optAddValImg.setSrc("/img/action_add.jpg");
		optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
		optAddValImg.addEventListener("onClick", this);
		optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
		optAddValImg.setParent(optDiv);			
		
		//Optional Value Add Action
		Image optDelValImg = new Image();
		optDelValImg.setSrc("/img/action_delete.gif");
		optDelValImg.setStyle("cursor:pointer;");
		optDelValImg.addEventListener("onClick", this);
		optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
		optDelValImg.setParent(optDiv);			
		
		//Display Label
		tempTextBox = new Textbox();
		tempTextBox.setParent(tempRow);
		
		//Default Merge-Tags
		
		Div optdefValDiv=new Div();
		optdefValDiv.setParent(tempRow);
		
		Combobox defValCb = new Combobox();
		Comboitem emptyValItem = new Comboitem("");
		emptyValItem.setParent(defValCb);
		defValCb.setSclass("cb_100w");
		defValCb.setParent(optdefValDiv);
		
		//Optional Value Add Action
		Image defValAddValImg = new Image();
		defValAddValImg.setSrc("/img/action_add.jpg");
		defValAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
		defValAddValImg.addEventListener("onClick", this);
		defValAddValImg.setAttribute("TYPE", "POS_ADD_DEFAULT_VALUE");
		defValAddValImg.setParent(optdefValDiv);			
		
		//Optional Value Add Action
		Image defValDelValImg = new Image();
		defValDelValImg.setSrc("/img/action_delete.gif");
		defValDelValImg.setStyle("cursor:pointer;");
		defValDelValImg.addEventListener("onClick", this);
		defValDelValImg.setAttribute("TYPE", "POS_DEL_DEFAULT_VALUE");
		defValDelValImg.setParent(optdefValDiv);			
		
		/*//Default PhValues
		tempTextBox = new Textbox();
		tempTextBox.setParent(tempRow);*/
		
//			Unique priority
		Intbox priIntBx = new Intbox();
		priIntBx.setMaxlength(1);
		priIntBx.setWidth("30px");
		
		priIntBx.setDisabled(true);
		/*if(isAdmin) {
			priIntBx.setDisabled(false);
		}*/
		
		priIntBx.setParent(tempRow);
		
		Div div=new Div();
		div.setParent(tempRow);

		//Delete Action
		Image delImg = new Image();
		delImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE_LOYALTY");
		delImg.setSrc("/images/action_delete.gif");
		delImg.setStyle("cursor:pointer;");
		delImg.addEventListener("onClick", this);
		delImg.setParent(div);
		
		
		
		
		tempRow.setParent(loyaltyRowsId);
		
		
	} // onClick$addLoyaltyPosMapBtnId
	
	public void onClick$addContactMapBtnId() {
		
		Row tempRow = new Row();
		
		Textbox tempTextBox;
		
		//POS Attr
		tempTextBox = new Textbox();
		tempTextBox.setParent(tempRow);
		
		
		Listbox tempListbox = null;
		
		//Custom Field
		tempListbox = createContactPosMappingListbox();
		tempListbox.setAttribute("CUST_FIELD_LISTBOX", "CUST_FIELD_LISTBOX");
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setSelectedIndex(17);
		tempListbox.setParent(tempRow);
		
		//DataType
		Div tempDiv = new Div();
		
		//Data Type
		tempListbox = createDataTypeListBox();
		tempListbox.addEventListener("onSelect", this);
		tempListbox.setSelectedIndex(0);
		tempListbox.setParent(tempDiv);
		
		//Date Format
		tempListbox = createDateFormatListbox();
		tempListbox.setSelectedIndex(0);
		tempListbox.setVisible(false);
		tempListbox.setParent(tempDiv);
		
		tempDiv.setParent(tempRow);
		
		// Optional Value
		Div optDiv=new Div();
		optDiv.setParent(tempRow);
		
		Combobox cb = new Combobox();
		cb.setSclass("cb_100w");
		cb.setParent(optDiv);
		
		
		//Optional Value Add Action
		Image optAddValImg = new Image();
		optAddValImg.setSrc("/img/action_add.jpg");
		optAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
		optAddValImg.addEventListener("onClick", this);
		optAddValImg.setAttribute("TYPE", "POS_ADD_OPTIONAL_VALUE");
		optAddValImg.setParent(optDiv);			
		
		//Optional Value Add Action
		Image optDelValImg = new Image();
		optDelValImg.setSrc("/img/action_delete.gif");
		optDelValImg.setStyle("cursor:pointer;");
		optDelValImg.addEventListener("onClick", this);
		optDelValImg.setAttribute("TYPE", "POS_DEL_OPTIONAL_VALUE");
		optDelValImg.setParent(optDiv);			
		
		//Display Label
		tempTextBox = new Textbox();
		tempTextBox.setParent(tempRow);
		
		//Default Merge-Tags
		
		Div optdefValDiv=new Div();
		optdefValDiv.setParent(tempRow);
		
		Combobox defValCb = new Combobox();
		Comboitem emptyValItem = new Comboitem("");
		emptyValItem.setParent(defValCb);
		defValCb.setSclass("cb_100w");
		defValCb.setParent(optdefValDiv);
		
		//Optional Value Add Action
		Image defValAddValImg = new Image();
		defValAddValImg.setSrc("/img/action_add.jpg");
		defValAddValImg.setStyle("cursor:pointer;margin:0 5px 0 15px;");
		defValAddValImg.addEventListener("onClick", this);
		defValAddValImg.setAttribute("TYPE", "POS_ADD_DEFAULT_VALUE");
		defValAddValImg.setParent(optdefValDiv);			
		
		//Optional Value Add Action
		Image defValDelValImg = new Image();
		defValDelValImg.setSrc("/img/action_delete.gif");
		defValDelValImg.setStyle("cursor:pointer;");
		defValDelValImg.addEventListener("onClick", this);
		defValDelValImg.setAttribute("TYPE", "POS_DEL_DEFAULT_VALUE");
		defValDelValImg.setParent(optdefValDiv);			
		
		/*//Default PhValues
		tempTextBox = new Textbox();
		tempTextBox.setParent(tempRow);*/
		
//			Unique priority
		Intbox priIntBx = new Intbox();
		priIntBx.setMaxlength(1);
		priIntBx.setWidth("30px");
		
		priIntBx.setDisabled(true);
		/*if(isAdmin) {
			priIntBx.setDisabled(false);
		}*/
		
		priIntBx.setParent(tempRow);
		
		Div div=new Div();
		div.setParent(tempRow);

		//Delete Action
		Image delImg = new Image();
		delImg.setAttribute("TYPE", "CONTACT_MAPPING_DELETE");
		delImg.setSrc("/images/action_delete.gif");
		delImg.setStyle("cursor:pointer;");
		delImg.addEventListener("onClick", this);
		delImg.setParent(div);
		
		tempRow.setParent(contactRowsId);
		
		
	} // onClick$addContactPosMapBtnId
	
	
	List<String> scopeCustfieldList = null;
			List<String> scopePosAttrList = null;
	/*public void onClick$saveContinueBtnId() {
		
		try {
			
			int confirm = Messagebox.show("Are you sure you want to save the POS mapping?", 
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
			if(confirm == Messagebox.OK) {
				
				try {
					
					// Validate Contact POS Mapping
					List contactRowChildList =  contactRowsId.getChildren();
					
					if(contactRowChildList != null && contactRowChildList.size() >0) {
						
						if(validatePOSMappingColl(contactRowChildList, Constants.POS_MAPPING_TYPE_CONTACTS) == false) {
							return;
						}
					}
					
					
					
					
						savePOSMappingByType(contactRowChildList ,  Constants.POS_MAPPING_TYPE_CONTACTS);
					
					MessageUtil.setMessage("POS mapping saved successfully.", "green", "TOP");
					
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception  ::", e);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", e);
		}
		
	} //onClick$saveContinueBtnId()
	*/
	
	private boolean validatePOSMappingColl(List tempList,String mappingType) {
		
		
		List<String> scopeCustfieldList = new ArrayList<String>();
		List<String> scopePosAttrList = new ArrayList<String>();
		List<String> scopeDispLblList = new ArrayList<String>();
		
		for (Object object : tempList) {
			
			Row temRow = (Row)object;
			
//			POSMapping posMapping = null;
			List chaildLis = temRow.getChildren();
			
			//optculture attribute
			Listbox custFieldListbx = (Listbox)chaildLis.get(1);
			Listitem optvalue=custFieldListbx.getSelectedItem();
			//POS Attribute
			Textbox posAttrbuteTextbox = (Textbox) chaildLis.get(0);
			posAttrbuteTextbox.setStyle("border:1px solid #7F9DB9;");
			
			//Display label
			Textbox displayTextbox = (Textbox) chaildLis.get(4);
			displayTextbox.setStyle("border:1px solid #7F9DB9;");
			
			//Default PhValues Label
			Combobox  defPhValCB= (Combobox) ((Div)chaildLis.get(5)).getFirstChild();
			String defVal = defPhValCB.getValue();
			
			if(defVal != null && !defVal.isEmpty())   {
				if(optvalue.getLabel().equals("Email")) {
					if(!Utility.validateEmail(defVal.trim())) {
						
						Messagebox.show("Please enter a valid email address.", "Error", Messagebox.OK, Messagebox.ERROR);
						return false;
				}
				}
				  
					else if(!Utility.validateDefaultValue(defVal)) {
							MessageUtil.setMessage("Please provide valid default value : "+defVal+" , no special characters are allowed.", "color:red;");
						return false;
						}
				
			}
			
			//Uniq Prio label
			Intbox uniqPriIntbox = (Intbox) chaildLis.get(6);
			uniqPriIntbox.setStyle("border:1px solid #7F9DB9;");
			
			if(posAttrbuteTextbox.getValue().trim().equals("")) {
				//logger.debug("Custom fieldData is eampty");
				posAttrbuteTextbox.setStyle("border:1px solid #DD7870;");
				
				MessageUtil.setMessage("Provide "+mappingType+" POS attribute. ", "color:red", "TOP");
				return false;
			}
			else if(displayTextbox.getValue().trim().equals("")) {
				displayTextbox.setStyle("border:1px solid #DD7870;");
				//logger.debug("Custom fieldData is eampty");
				MessageUtil.setMessage("Provide "+ mappingType +" Display label. ", "color:red", "TOP");
				return false;
			}
			
			
			else {
				try {
					if(uniqPriIntbox.getValue()!=null && uniqPriIntbox.getValue()<=0) {
						uniqPriIntbox.setStyle("border:1px solid #DD7870;");
						//logger.debug("Invalid unique priority eampty");
						MessageUtil.setMessage("Provide "+ mappingType +" valid Unique Priority value. ", "color:red", "TOP");
						return false;
					}
				} catch (Exception e) {
					uniqPriIntbox.setStyle("border:1px solid #DD7870;");
					//logger.debug("Invalid unique priority eampty");
					MessageUtil.setMessage("Provide "+ mappingType +" valid Unique Priority value. ", "color:red", "TOP");
					return false;
				}
			}
			
			
			
			//optculture field
			String scopeCustFieldStr = mappingType+"_"+ custFieldListbx.getSelectedItem().getLabel();
			String scopePosStr = mappingType+"_"+posAttrbuteTextbox.getValue();
			String scopeDispStr = mappingType+"_"+displayTextbox.getValue();
			if(scopeCustfieldList.contains(scopeCustFieldStr) ) {
				MessageUtil.setMessage(mappingType +"  Optculture Field  "+ custFieldListbx.getSelectedItem().getLabel() +" already mapped.","color:red","TOP");
				return false;
			}else if(scopePosAttrList.contains(scopePosStr)) {
				MessageUtil.setMessage(mappingType +"  POS Attribute "+ posAttrbuteTextbox.getValue() +" already mapped.","color:red","TOP");
				return false;
			}else if(scopeDispLblList.contains(scopeDispStr)){
				MessageUtil.setMessage(mappingType +"  POS DispLay Label "+ displayTextbox.getValue() +" already mapped.","color:red","TOP");
				return false;
			}
			scopeCustfieldList.add(scopeCustFieldStr);
			scopePosAttrList.add(scopePosStr);
			scopeDispLblList.add(scopeDispStr);
		} //for
		return true;
		
	} //validatePOSMappingColl
	
	
	
	private void savePOSMappingByType(List rowList , String mappingType) {
		
		boolean isObjModified = false;

		if(rowList.size() > 0) {
			
			for (Object object : rowList) {
				
				isObjModified = false;
				
				Row temRow = (Row)object;
				
				POSMapping posMapping = null;
				List chaildLis = temRow.getChildren();
				
				
				//POS Attribute
				Textbox posAttrbuteTextbox = (Textbox) chaildLis.get(0);
				
				//Data Type
				Div dateFormatDiv = (Div)chaildLis.get(2); 
				
				Listbox dataTypeListbox  = (Listbox)dateFormatDiv.getChildren().get(0);
				String dataTypeStr = dataTypeListbox.getSelectedItem().getLabel();
				if(dataTypeStr.equals("Date")) {
					dataTypeStr = dataTypeStr+"("+((Listbox)dateFormatDiv.getChildren().get(1)).getSelectedItem().getLabel()+")";
				}
				
				//custom field
				Listbox custFielListBx = (Listbox) chaildLis.get(1);
				
				//Display Label
				Textbox  dispLblTextBx = (Textbox) chaildLis.get(4);
				
				//Default PhValues Label
				//Textbox  defPhValTextBx = (Textbox) chaildLis.get(5);
				Combobox defValueCombBox = (Combobox) ((Div) chaildLis.get(5)).getFirstChild();
				String defValCombStr="";
				
				List<Comboitem> cbDefValItemsList = defValueCombBox.getItems();
				for (Comboitem cbItem : cbDefValItemsList) {
					if(defValCombStr.length()>0) defValCombStr += Constants.ADDR_COL_DELIMETER ;
					defValCombStr += cbItem.getLabel().trim();
				}
				
				String selectedDefVal = defValueCombBox.getValue().trim();
				
				
				//Unique Priority 
				Intbox  uniqPriIntBx = (Intbox) chaildLis.get(6);

				//Optional Values
				Combobox optCombBox = (Combobox) ((Div) chaildLis.get(3)).getFirstChild();
				String optCombStr="";
				
				List<Comboitem> cbItemsList = optCombBox.getItems();
				for (Comboitem cbItem : cbItemsList) {
					if(optCombStr.length()>0) optCombStr += Constants.ADDR_COL_DELIMETER ;
					optCombStr += cbItem.getLabel();
				}
			//	temRow.setParent(contactRowsId);
				
				
				//String scopeStr,String posAttrStr,String custFieldStr, long userId
				posMapping = (POSMapping)temRow.getAttribute("referenceId");
				//logger.info("5"+posMapping);
				//posMapping = posMappingDao.findRecord(scopeLbl.getValue(), posAttrLbl.getValue(), custFieldLbl.getValue(), users.getUserId());
				
				
				//Check if the Fields are modified...
				if(posMapping != null) {
					
					if(!(posMapping.getPosAttribute().trim().equals(posAttrbuteTextbox.getValue().trim()))) {
//						logger.info("Pos Attri old::"+posMapping.getPosAttribute().trim());
						isObjModified = true;
					}
					else if(!(posMapping.getCustomFieldName().equals(custFielListBx.getSelectedItem().getLabel().trim()))){
//						logger.info("Pos custField old::"+posMapping.getCustomFieldName().trim());
						isObjModified = true;
					}
					else if(!(posMapping.getDataType().trim().equals(dataTypeStr.trim()))) {
//						logger.info("Pos getDataType old::"+posMapping.getDataType().trim() );
						isObjModified = true;
					}
					else if(!(posMapping.getDisplayLabel().trim().equals(dispLblTextBx.getValue().trim()))) {
						isObjModified = true;
					}
					
					else if(posMapping.getOptionalValues()==null && optCombStr.length()>0) {
						isObjModified = true;
					}
					else if(posMapping.getOptionalValues()!=null && !posMapping.equals(optCombStr)) {
						isObjModified = true;
					}
					
					else if(posMapping.getUniquePriority()!=null && uniqPriIntBx.getValue()!=null && 
							posMapping.getUniquePriority().intValue()!=uniqPriIntBx.intValue()) {
						isObjModified = true;
					}
					else if(posMapping.getUniquePriority()==null && uniqPriIntBx.getValue()!=null) {
						isObjModified = true;
					}
					else if(posMapping.getUniquePriority()!=null && uniqPriIntBx.getValue()==null) {
						isObjModified = true;
					}
					else if((posMapping.getDefaultPhValue() == null || posMapping.getDefaultPhValue().isEmpty() )){
						
						/*posMapping.setDefaultPhValue(defPhValTextBx.getValue());
						posMappingDao.saveOrUpdate(posMapping);*/
						isObjModified = true;
						
					}
					else if(posMapping.getDefaultPhValue()!=null && selectedDefVal.isEmpty()) {
						isObjModified = true;
					}
					else if(posMapping.getDefaultPhValue()==null && !selectedDefVal.isEmpty()) {
						isObjModified = true;
					}
					else{
						if(!(posMapping.getDefaultPhValue().trim().equals(selectedDefVal))) {
							isObjModified = true;
							
						}
					}
					
				}
				
				else if(posMapping == null) {
					posMapping = new POSMapping();
					isObjModified = true;
					temRow.setAttribute("referenceId", posMapping);
				}
				
				if(!isObjModified) {
					continue;
				}
				
				
				//scope
				posMapping.setMappingType(mappingType.trim());
				//CustomField Name
				posMapping.setCustomFieldName(custFielListBx.getSelectedItem().getLabel());
				//POSAttribute
				posMapping.setPosAttribute(posAttrbuteTextbox.getValue());
				//Display Label
				posMapping.setDisplayLabel(dispLblTextBx.getValue());
				//Default PhValues
				posMapping.setDefaultPhValue(selectedDefVal.isEmpty() ? null : selectedDefVal);
				posMapping.setDefaultPhValueSet(defValCombStr.isEmpty() ? null : defValCombStr );
				// Optional Values 
				if(optCombStr.length()>0) posMapping.setOptionalValues(optCombStr);
				else posMapping.setOptionalValues(null);
				
				//Unique priority Label
				posMapping.setUniquePriority(uniqPriIntBx.getValue());
				
				//Data type
				posMapping.setDataType(dataTypeStr);
				//UserId
				posMapping.setUserId(users.getUserId());
				
				
				//posMappingDao.saveOrUpdate(posMapping);
				posMappingDaoForDML.saveOrUpdate(posMapping);
				
			} //for
			//logger.debug("POS mapping saved successfully..");
		}
		
	}
	
	
	
	
	public void onClick$saveBtnId() {
		
		try {
			
			int confirm = Messagebox.show("Are you sure you want to save the List settings?", 
					"Confirm", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
			if(confirm == Messagebox.OK) {
				
				try {
					
					// Validate Contact POS Mapping
					List contactRowChildList =  contactRowsId.getChildren();
					
					//Changes 2.5.4.0
					List loyaltyRowChildList =  loyaltyRowsId.getChildren();
					if(contactRowChildList != null && contactRowChildList.size() >0) {
						
						if(validatePOSMappingColl(contactRowChildList, Constants.POS_MAPPING_TYPE_CONTACTS) == false) {
							return;
						}
					}
					
					//Changes 2.5.4.0
					if(loyaltyRowChildList != null && loyaltyRowChildList.size() >0) {
						
						if(validatePOSMappingColl(loyaltyRowChildList, Constants.POS_MAPPING_TYPE_LOYALTY) == false) {
							return;
						}
					}
					
					savePOSMappingByType(contactRowChildList , Constants.POS_MAPPING_TYPE_CONTACTS);
					//Changes 2.5.4.0
					savePOSMappingByType(loyaltyRowChildList , Constants.POS_MAPPING_TYPE_LOYALTY);
					
					MessageUtil.setMessage("List mappings saved successfully.", "green", "TOP");
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception  ::", e);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", e);
		}
		
	}
	
	
	private Comboitem findOptionalComboitem(Combobox cb, String label) {
		try {
			List<Comboitem> items = cb.getItems();
			for (Comboitem cbitem : items) {
				if(cbitem.getLabel().equals(label)) return cbitem;
			} // for
			
			return null;
		} catch (Exception e) {
			logger.error("Exception  ::", e);
			return null;
		}
	}
	
	

	@Override
	public void onEvent(Event event) throws Exception {

		// TODO Auto-generated method stub
		super.onEvent(event);
		
		
		if(event.getTarget() instanceof Image) {
			
			Image img =(Image)event.getTarget();
			Row temRow = null;
			String imgAction = (String)img.getAttribute("TYPE");
			
			if(!imgAction.equals("FTP_SETTINGS")) {
				
				temRow = (Row)img.getParent().getParent();
			}				
			
			try {
				
				if(imgAction.equals("POS_ADD_OPTIONAL_VALUE") || imgAction.equals("POS_DEL_OPTIONAL_VALUE")) { // POS CONTACT
					
					Div div = (Div)img.getParent();
					Combobox cb =  (Combobox)div.getFirstChild();
					
					if(cb.getValue()!=null && cb.getValue().trim().length() > 0) {
						String label = cb.getValue().trim();
						
						if(imgAction.equals("POS_ADD_OPTIONAL_VALUE") && findOptionalComboitem(cb, label)==null) {
							cb.appendChild(new Comboitem(label));
							cb.setValue("");
						}
						else if(imgAction.equals("POS_DEL_OPTIONAL_VALUE")) {
							Comboitem delItem = findOptionalComboitem(cb, label);
							if(delItem!=null) {
								cb.removeChild(delItem);
								cb.setValue("");
							}
						}
					}
					
					return;
				} // if

				
				if(imgAction.equals("POS_ADD_DEFAULT_VALUE") || imgAction.equals("POS_DEL_DEFAULT_VALUE")) { // POS CONTACT
					
					Div div = (Div)img.getParent();
					Combobox cb =  (Combobox)div.getFirstChild();
					
					Row row=(Row)div.getParent();
					Listbox lb = (Listbox)row.getChildren().get(1);
					Listitem li = lb.getSelectedItem();
					if(cb.getValue()!=null && cb.getValue().trim().length() > 0) {
						
						String label = cb.getValue().trim();
						Comboitem cbitem = findOptionalComboitem(cb, label);
						
						if(imgAction.equals("POS_ADD_DEFAULT_VALUE") && cbitem==null) {
							
							if(label != null && !label.isEmpty())  {
								if(li.getLabel().equals("Email")) {
									
									if(!Utility.validateEmail(label.trim())) {

										Messagebox.show("Please enter a valid email address.", "Error", Messagebox.OK, Messagebox.ERROR);
										return ;
									}
									
								}else if(!Utility.validateDefaultValue(label)) {
									
									MessageUtil.setMessage("Please provide valid default value : "+label+" , no special characters are allowed.", "color:red;");
									return ;
								}
								
								
							}
							Comboitem ci =  new Comboitem(label);
							cb.appendChild(ci);
							cb.setValue(label);
							
							
						}
						else if(imgAction.equals("POS_DEL_DEFAULT_VALUE")) {
							Comboitem delItem = findOptionalComboitem(cb, label);
							if(delItem!=null) {
								cb.removeChild(delItem);
								cb.setValue(cb.getItemAtIndex(0).getLabel());
							}
						}
					}
					
					return;
				} // if
				try {
						
					if(img.getAttribute("TYPE").equals("POS_CONTACT_MAPPING")	||
							img.getAttribute("TYPE").equals("CONTACT_MAPPING_DELETE")) { // POS CONTACT
						
						if(temRow.getAttribute("referenceId") == null) { //contactRowsId,,
							contactRowsId.removeChild(temRow);
						}
						else {
							int confirm = Messagebox.show("Are you sure you want to delete the record?",
									"Delete POS Mapping", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
							if(confirm != Messagebox.OK) return;
							
								POSMapping posCustFieldMapping = (POSMapping)temRow.getAttribute("referenceId");
								
								//posMappingDao.delete(posCustFieldMapping); //delete posMapping entry
								posMappingDaoForDML.delete(posCustFieldMapping); //delete posMapping entry
								contactRowsId.removeChild(temRow);
								MessageUtil.setMessage("Deleted successfully.", "green", "TOP");
						}
						
					}
					
					if(img.getAttribute("TYPE").equals("POS_CONTACT_MAPPING")	||
							img.getAttribute("TYPE").equals("CONTACT_MAPPING_DELETE_LOYALTY")) { // POS CONTACT
						
						if(temRow.getAttribute("referenceId") == null) { //loyaltyRowsId,,
							loyaltyRowsId.removeChild(temRow);
						}
					
					}
					
					
				} catch (Exception e) {
					logger.error("Exception  ::", e);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception  ::", e);
			}
		}
		else if(event.getTarget() instanceof Listbox) {
			
			
			Listbox tempListBx = (Listbox) event.getTarget();
			
//			CUST_FIELD_LISTBOX
			if(tempListBx.getAttribute("CUST_FIELD_LISTBOX") != null && 
					tempListBx.getAttribute("CUST_FIELD_LISTBOX").equals("CUST_FIELD_LISTBOX")) {
				
				String selectDataType = tempListBx.getSelectedItem().getLabel();
				
//				logger.info("########## selectDateType #######"+selectDateType);
				Div dataTypeDiv	 = (Div)((Row)tempListBx.getParent()).getChildren().get(2);
				Listbox dataTypeListbox   = (Listbox)dataTypeDiv.getChildren().get(0);
				
				if(selectDataType.startsWith(BIRTHDAY) || selectDataType.startsWith(ANNIVERSARY) || selectDataType.startsWith(CREATEDDATE)) {
					
					dataTypeListbox.setSelectedIndex(1);
					dataTypeListbox.setDisabled(true);
					((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(true);
					
				}
				/*else if(selectDataType.startsWith(ZIP) || selectDataType.startsWith(MOBILE)) {
					dataTypeListbox.setSelectedIndex(2);
//					dataTypeListbox.setDisabled(false);
					dataTypeListbox.setDisabled(true);
					((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
				} */
				else if(selectDataType.startsWith("UDF")) {
					dataTypeListbox.setSelectedIndex(0);
					dataTypeListbox.setDisabled(false);
//					dataTypeListbox.setDisabled(true);
					((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
				}else {
					dataTypeListbox.setSelectedIndex(0);
//					dataTypeListbox.setDisabled(false);
					dataTypeListbox.setDisabled(true);
					((Listbox)dataTypeDiv.getChildren().get(1)).setVisible(false);
				}
				
				Div actionDiv = (Div)((Row)tempListBx.getParent()).getChildren().get(7);
				logger.info("Selected ItemCount>>>>>>>>>>>>>"+tempListBx.getSelectedCount());
				if(tempListBx.getSelectedIndex() < 17) {
					
					((Image)actionDiv.getChildren().get(0)).setVisible(false);
				}else  {
					((Image)actionDiv.getChildren().get(0)).setVisible(true);
				}
				
				
				
			}else {
				
				Div tempDiv = (Div)tempListBx.getParent();
				Listbox dateFormatLb= (Listbox)tempDiv.getChildren().get(1);
				
				if(tempListBx.getSelectedItem().getLabel().equals("Date")) {
					dateFormatLb.setSelectedIndex(8);
					dateFormatLb.setVisible(true);
				}else dateFormatLb.setVisible(false);
			}
			
			
			
		}
	} //onEvent
	
	
} // class

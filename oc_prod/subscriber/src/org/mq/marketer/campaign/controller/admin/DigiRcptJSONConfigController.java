package org.mq.marketer.campaign.controller.admin;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.ApplicationProperties;
import org.mq.marketer.campaign.dao.ApplicationPropertiesDao;
import org.mq.marketer.campaign.dao.ApplicationPropertiesDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

public class DigiRcptJSONConfigController extends GenericForwardComposer implements EventListener  {

	private Listbox digiRcptSettingLstId1;
	private String[] allPlaceHoldersArr;
	private Grid templateAndJSONMappingGridId;
	private StringBuffer templateSettingSB;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
	
	public DigiRcptJSONConfigController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;" +
				"font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Digital Receipt Settings","",style,true);
		
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		setJSONFields();
	}
	
	private void setJSONFields() {
		try {
			String generalPlaceHolders = PropertyUtil.getPropertyValue("DRPlaceHolders");
			String paymentSetPlaceHolders = PropertyUtil.getPropertyValue("DRItemLoopPlaceHolders");
			String ItemsSetPlaceHolders = PropertyUtil.getPropertyValue("DRPaymentLoopPlaceHolders");
			
			if(generalPlaceHolders == null || paymentSetPlaceHolders == null || ItemsSetPlaceHolders == null) {
				//return new RuntimeException("");
				logger.info("Required place holders are missing in the APPLICATION PROPERTIES file.");
				return;
			}
			
			String[] generalPlaceHoldersArr = generalPlaceHolders.split(",");
			String[] paymentSetPlaceHoldersArr = paymentSetPlaceHolders.split(",");
			String[] ItemsSetPlaceHoldersArr = ItemsSetPlaceHolders.split(",");
			
			allPlaceHoldersArr = new String[generalPlaceHoldersArr.length + paymentSetPlaceHoldersArr.length 
			                                         + ItemsSetPlaceHoldersArr.length];
			
			System.arraycopy(generalPlaceHoldersArr, 0, allPlaceHoldersArr, 0, generalPlaceHoldersArr.length);
			System.arraycopy(paymentSetPlaceHoldersArr, 0, allPlaceHoldersArr, generalPlaceHoldersArr.length, paymentSetPlaceHoldersArr.length);
			System.arraycopy(ItemsSetPlaceHoldersArr, 0, allPlaceHoldersArr, generalPlaceHoldersArr.length + paymentSetPlaceHoldersArr.length ,ItemsSetPlaceHoldersArr.length);
			
			
			Listitem tempLi;
			for(int i=0;i<allPlaceHoldersArr.length;i++) {
				tempLi = new Listitem(allPlaceHoldersArr[i]);
				tempLi.setValue(allPlaceHoldersArr[i]);
				tempLi.setParent(digiRcptSettingLstId1);
			}
			
			ListModelArray listModelArr = new ListModelArray(allPlaceHoldersArr);

			digiRcptSettingLstId1.setModel(listModelArr);
			digiRcptSettingLstId1.setSelectedIndex(0);
		} catch(Exception e) {
			
		}
	}
	
	private Textbox jsonKeyFieldTbxId;
	
	public void onClick$addFieldBtnId() {
		
		if(jsonKeyFieldTbxId.getValue().trim().equals("")) {
			MessageUtil.setMessage("JSON key field cannot be left empty.","red","top");
			return;
		}
		
		Rows rows = templateAndJSONMappingGridId.getRows();
		
		Row row = new Row();
		Cell cell = new Cell();
		
		//Template Field
		cell.appendChild(new Label(digiRcptSettingLstId1.getSelectedItem().getLabel()));
		row.appendChild(cell);
		
		
		//Json Field Key
		cell = new Cell();
		cell.appendChild(new Label(jsonKeyFieldTbxId.getValue()));
		row.appendChild(cell);
		
		//Options
		cell = new Cell();
		Image img2 = new Image("/img/action_delete.gif");
		img2.addEventListener("onClick", this);
		img2.setAttribute("templateSettgRowId", row);
		img2.setParent(cell);
		row.appendChild(cell);
		
		row.setParent(rows);
		
	}
	
	private void validateJSONFieldMapping() {
		List<Component> list = templateAndJSONMappingGridId.getRows().getChildren();
		boolean found = false;
		
		// Create setting string parallel to the verification process.
		templateSettingSB = new StringBuffer();
		for(int i=0; i<allPlaceHoldersArr.length; i++) {
				found = false;
				
				for (Component component : list) {
					Row row = (Row)component;
					String templateFieldStr = ((Label)((Cell)row.getChildren().get(0)).getChildren().get(0)).getValue().trim();
					logger.info("  templateFieldStr : "+ templateFieldStr);
				
					if(templateFieldStr.equals(allPlaceHoldersArr[i])) {
						if(templateSettingSB.length() == 0) {
							templateSettingSB.append(templateFieldStr + ":" + 
									((Label)((Cell)row.getChildren().get(1)).getChildren().get(0)).getValue().trim());
						} else {
							templateSettingSB.append( "^|^"  + templateFieldStr + ":" + 
									((Label)((Cell)row.getChildren().get(1)).getChildren().get(0)).getValue().trim());
						}
						found = true;
						break;
					}
				
				 }
			
			if(!found) {
				logger.info("--3---");
				MessageUtil.setMessage("Please configure the "+ allPlaceHoldersArr[i], "red", "top");
				return;
			}
			
			
		}
		
		logger.info("**** ALL FIELDS CONFIGURED PROPERLY ...**** settings : "+ templateSettingSB.toString());
	}
	
	public void onClick$submitBtnid() {
		
		validateJSONFieldMapping();
		ApplicationPropertiesDao appPropDao = (ApplicationPropertiesDao)SpringUtil.getBean("applicationPropertiesDao");
		ApplicationPropertiesDaoForDML appPropDaoForDML = (ApplicationPropertiesDaoForDML)SpringUtil.getBean("applicationPropertiesDaoForDML");
		ApplicationProperties appProp = appPropDao.findByPropertyKey("DigitalReceiptSetting");
		if(appProp == null) {
			appProp = new ApplicationProperties("DigitalReceiptSetting", templateSettingSB.toString());
		} else {
			appProp.setValue(templateSettingSB.toString());
		}
		appPropDaoForDML.saveOrUpdate(appProp);
	}
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget() instanceof Image) {
			
			Image img = (Image)event.getTarget();
			if(img.getAttribute("templateSettgRowId")!= null) {
				
				Row row = (Row)img.getAttribute("templateSettgRowId");
				(row.getParent()).removeChild(row);
			}
		}
	}
	
}

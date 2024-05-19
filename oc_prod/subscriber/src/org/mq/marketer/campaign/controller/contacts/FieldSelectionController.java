package org.mq.marketer.campaign.controller.contacts;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

@SuppressWarnings({"serial","unchecked"})
public class FieldSelectionController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private String[] cfDataTypes = {"--Type--","String","Date","Number","Double","Boolean"};
	private ListModel dataTypeSet = null;
	private Session session = null;
	private Row currentRow;
	private Rows fieldRowsId;
	//private Vbox currentField = null;
	private Toolbarbutton addTbId,remTbId;
	private ListModel listModel = null;
	
	private String[] defaultFieldArray = null;
	private MailingList mailingList = null;
	private String isNew = null;
	private Set mailingLists = null;
	private int colsPerRow = 1;
	private int i = 0 ;
	private String dateFormatStr;
	private static final String SINGLE_SPACE = "";
	private List tempList = new ArrayList();
	
	
	public FieldSelectionController() {
		
		if(logger.isDebugEnabled())logger.debug("-- Just entered -- ");
		
		session = Sessions.getCurrent();
		mailingLists = (Set)session.getAttribute("uploadFile_Ml");
		dateFormatStr = PropertyUtil.getPropertyValue("customFiledDateFormat").toLowerCase();

		MLCustomFieldsDao mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		String defaultFieldList = (String)PropertyUtil.getPropertyValue("defaultFieldList");
		
		//String defaultFieldArrayList = (String)PropertyUtil.getPropertyValue("defaultFieldArrayList");

		String cfList = (String)PropertyUtil.getPropertyValue("CustomFiledList");

		isNew = (String)session.getAttribute("isNewML");
		if(logger.isDebugEnabled())logger.debug(" isNewML :"+isNew);
		
		defaultFieldArray =StringUtils.split(defaultFieldList, ','); 
		
		String[] fieldArray = defaultFieldArray;
		
		if(isNew != null) {
			
			if(isNew.equals("true")) {
				fieldArray = StringUtils.split(defaultFieldList+","+cfList, ',');
				for(int i=0; i <fieldArray.length ; i++) {
					tempList.add(fieldArray[i]);
				}
//				listModel = new SimpleListModel(fieldArray);
			} // if(isNew.equals("true"))
			else{
				if(mailingLists.size() > 1) {
					for(int i=0; i <fieldArray.length ; i++) {
						tempList.add(fieldArray[i]);
					}
					
//					listModel = new SimpleListModel(fieldArray);
				} //if(mailingLists.size() > 1)
				else if(mailingLists.size() == 1) {
					mailingList = (MailingList)mailingLists.iterator().next(); 
					List list = mlCFDao.findAllByList(mailingList);
					if(logger.isDebugEnabled())logger.debug(" Total Custom Fileds for the already existing list :"+list.size());
					
					for(int i=0;i<fieldArray.length;i++)
						tempList.add(fieldArray[i]);
					
					for(Object o:list)
						tempList.add("CF :"+((MLCustomFields)o).getCustFieldName());
//					listModel = new SimpleListModel(tempList);
				} //else if(mailingLists.size() == 1) 
			} //else if(isNew.equals("true"))
		} //if(isNew != null)
		
		if(logger.isDebugEnabled())logger.debug("-- exit --");
	}
	
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		while(i < 4)
			addColumn();
		
	}
	
	public ListModel getDataTypeSet(){
		return dataTypeSet;
	}
	
	public ListModel getFieldData(){
		return listModel;
	}
	
	/**
	 *  Adds a column to the field Selection grid, this will add up to 30 fields 
	 */
	public void addColumn() throws Exception{
		if(logger.isDebugEnabled())logger.debug("-- just entered --");
		MessageUtil.clearMessage();
		
		//boolean flag =checkAvailRowIndex();
		
		if(i>=30) {
			return;
		}
		Row row = new Row();
		
		//row.setId("rowId" + ( (i/colsPerRow) + 1) );
		//currentRow = row;
		
		/*if(i%colsPerRow == 0) {
		}*/

		i++;
		
		
		/**** parent Vbox *****/
		Vbox vb = new Vbox();
		vb.setAlign("center");
		vb.setStyle("padding:10px");
		//vb.setId("vbId"+i); 
		
		//set Label for Column index
		Label label = new Label("Column "+""+i+" : ");
		label.setSclass("theme-lbHeading");
		label.setStyle("display: inline-block;text-align: left;width: 80px;");
		
		//set field Type listbox
		Listbox lb = new Listbox();
		//lb.setName("FieldTypelb");
		lb.setMold("select");
		listModel = new SimpleListModel(tempList);
		// lb.setModel(listModel);
		
		updateLabels(lb);
		
		//lb.setId("colLbId"+i);
//		lb.setParent(vb);
		
		//delete Column
		Toolbarbutton tbn = new Toolbarbutton("Remove Column", "/images/action_delete.gif");
		tbn.addEventListener("onClick", this);
		tbn.setStyle("margin-left:10px;text-decoration: underline; color:#2787BA; font-weight:bold;font-size:12px;");
		
		Div tempDiv = new Div();
		label.setParent(tempDiv);
		lb.setParent(tempDiv);
		tbn.setParent(tempDiv);
		tempDiv.setParent(vb);
		
		lb.setWidth("250px");
//		lb.setStyle("font-size:14px");
		lb.setSelectedIndex(0);
		
		
//		if(isNew !=null && isNew.equalsIgnoreCase("true")) {
		if(isNew !=null) {
			
			
			Div div = new Div();
			div.setStyle("text-align:left;padding-left:80px;");
			div.setVisible(false);
			//div.setId("cfDivId"+i);
			div.setParent(vb);
			
			//lb.setAttribute("divId","cfDivId"+i);
			Hbox hb = new Hbox();
			hb.setAlign("center");
			
			Textbox tb = new Textbox();
			tb.setMaxlength(20);
			tb.setWidth("65px");
			tb.setStyle("font-size:11px;");
			
			Div labelDiv = new Div();
			labelDiv.setStyle("text-align:center;");
			Label nameLbl = new Label("Name");
			nameLbl.setStyle("font-size:10px;display:block;");
			nameLbl.setParent(labelDiv);
			tb.setParent(labelDiv);
			labelDiv.setParent(hb);
			
			Listbox dataTypelb =  new Listbox();
			dataTypelb.setMold("select");
			dataTypelb.setName("dataTypelb");
			dataTypeSet = new SimpleListModel(cfDataTypes);
			dataTypelb.setModel(dataTypeSet);
			dataTypelb.setStyle("font-size:11px;margin:0 5px;");
			dataTypelb.addEventListener("onSelect", this);
//			dataTypelb.setParent(hb);

			labelDiv = new Div();
			labelDiv.setStyle("text-align:center;");
			nameLbl = new Label("Type");
			nameLbl.setStyle("font-size:10px;display:block;");
			nameLbl.setParent(labelDiv);
			dataTypelb.setParent(labelDiv);
			labelDiv.setParent(hb);
			
			
			tb = new Textbox();
			tb.setMaxlength(20);
			tb.setWidth("65px");
			
			tb.setStyle("font-size:11px;color:#6F6F6F");
//			tb.setParent(hb);
			
			labelDiv = new Div();
			labelDiv.setStyle("text-align:center;");
			nameLbl = new Label("Default Value");
			nameLbl.setStyle("font-size:10px;display:block;");
			nameLbl.setParent(labelDiv);
			tb.setParent(labelDiv);
			labelDiv.setVisible(false);
			labelDiv.setParent(hb);
			
			
			/*****dateFormat*****/
			labelDiv = new Div();
			
			labelDiv.setStyle("text-align:center;");
			nameLbl = new Label("Format");
			nameLbl.setStyle("font-size:10px;display:block;");
			nameLbl.setParent(labelDiv);
			labelDiv = setdateFormatCombox(labelDiv);
			labelDiv.setVisible(false);
			labelDiv.setParent(hb);
			
			
			
			
			hb.setParent(div);
			lb.addEventListener("onSelect",new EventListener() {
				
				@Override
				public void onEvent(Event event) throws Exception {
					Listbox tempListBox = (Listbox)event.getTarget();
//					Listitem li = lb.getSelectedItem();
					//String field = tempListBox.getSelectedItem().getLabel();
					String field = tempListBox.getSelectedItem().getValue();
					
					Vbox tempVbox = (Vbox)tempListBox.getParent().getParent();
					if(field.contains("Custom Field")) {
						tempVbox.getChildren().get(1).setVisible(true);
						//Date Format
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(3).setVisible(false);
						
						//Name
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(0).setVisible(true);
						//Type
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(1).setVisible(true);
						//Default value
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(2).setVisible(true);
						
						
					}else if(field.contains("BirthDay")||field.contains("Anniversary")) {
						tempVbox.getChildren().get(1).setVisible(true);
						//Date Format
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(3).setVisible(true);
						
						//Name
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(0).setVisible(false);
						//Type
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(1).setVisible(false);
						//Default value
						((Hbox)((Div)tempVbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(2).setVisible(false);
					}
						else {
							logger.info("");
						tempVbox.getChildren().get(1).setVisible(false);
					}
					
				}
			});

			
			
		} // if(isNew !=null && isNew.equalsIgnoreCase("true"))
//		currentField = vb;//row
//		vb.setParent(currentRow);
		vb.setParent(row);
		row.setParent(fieldRowsId);
//		remTbId.setVisible(true);
		if(i == 30){
			addTbId.setVisible(false);
		}
	} //addColumn()

	
	
	private Div setdateFormatCombox(Div tempDiv) {
		try {
			Combobox formtComboBx= new Combobox();
			formtComboBx.setWidth("160px");
			Comboitem tempComboitem = new Comboitem("dd/MM/yyyy");
			tempComboitem.setParent(formtComboBx);
			
			tempComboitem = new Comboitem("dd-MM-yyyy");
			tempComboitem.setParent(formtComboBx);
			
			
			tempComboitem = new Comboitem("MM/dd/yyyy");
			tempComboitem.setParent(formtComboBx);
			
			tempComboitem = new Comboitem("MM-dd-yyyy");
			tempComboitem.setParent(formtComboBx);
			
			tempComboitem = new Comboitem("dd/MM/yyyy HH:mm");
			tempComboitem.setParent(formtComboBx);
			
			tempComboitem = new Comboitem("MM/dd/yyyy HH:mm");
			tempComboitem.setParent(formtComboBx);
			formtComboBx.setSelectedIndex(0);
			formtComboBx.setParent(tempDiv);
			return tempDiv;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return tempDiv;
		}
	
	}
	
	
	private void updateLabels(Listbox lb) {
		
		logger.info("Update Label");
		String tempLbl, tempVal;
		
		for (int i = 0; i < tempList.size(); i++) {
			
			tempLbl = tempVal = (String)tempList.get(i);
			
			if(tempLbl.equals("Pin")) {
				tempLbl="Zip / Pin";
			}
			else if(tempLbl.equals("Phone")) {
				tempLbl ="Mobile";
			}
			
			lb.appendChild(new Listitem(tempLbl, tempVal));
			
		} // for
	} //
	
	
	/*private boolean checkAvailRowIndex() {
		
	}*/
	/**
	 * removes last field column from the grid, it removes up to one column
	 */
	/*public void removeColumn() throws Exception{
		try{
			if(i <= 1) return;
			
			String rowId="";
			currentRow.removeChild(currentField);
			i--;
			if(i%colsPerRow  == 0){
				fieldRowsId.removeChild(currentRow);
				
				rowId = "rowId" + ( ((i+1)/colsPerRow) );
				currentRow = (Row)Utility.getComponentById(rowId);
			}
			currentField = (Vbox)Utility.getComponentById("vbId"+i);
			addTbId.setVisible(true);
			
			if(i == 1){
				remTbId.setVisible(false);
			}
		}catch(Exception e){
			logger.error(" ** Exception :"+ e +" **");
		}
	} //removeColumn()
*/	
	/**
	 * Validates the field selection - <BR/> 
	 * 	<UL>
	 * 		<LI>Email field must be one of the selection </LI>
	 *  	<LI>One field should not select more than once </LI>
	 *  	<LI>If Custom Field is selected - Name, Data type are must</LI>
	 *  	<LI>If Default value is provided for the Custom Field, it must be of selected data type</LI>
	 *  </UL>
	 * Puts the selection info in the session <BR/>
	 * and moves to file upload page
	 */
	
	public void onClick$nextBtnId() {
		next();
	}
	
	public void next(){
		try{
			MessageUtil.clearMessage();
			
			MLCustomFieldsDao mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
			Set<String> cfNameSet = new HashSet<String>();
			String cfName,defValue= "";
			List rowList = fieldRowsId.getChildren();
			List rowChildren ,vbChildren;
			List fieldList = new ArrayList();
			List fieldIndexList = new ArrayList();
			String field = "";
			Map cfMap = new HashMap();
			Map<String, String>genFieldMap = new HashMap<String, String>();
			boolean emailExists = false;
			Vbox vb;
			Listbox fieldLb;
			int index,colIndex=0;
			int cfIndex = 1;
//			colIndex = 0;
			for(Object obj:rowList) {
				
				if(!(obj instanceof Row)) return;
				
				Row row = (Row)obj;
				rowChildren = row.getChildren();
				
				for(Object obj1:rowChildren){
					
					colIndex++;
					vb = (Vbox)obj1;
					
					Div temDiv = (Div) vb.getChildren().get(0);
					
					//vbChildren = vb.getChildren();
					vbChildren = temDiv.getChildren();
					
					fieldLb = (Listbox)vbChildren.get(1);
					
					index = fieldLb.getSelectedIndex();
					if(logger.isDebugEnabled())logger.debug("index is"+index);
					logger.info("index is"+index);
					
					
					if(index>0) {
						
						if(index == 1 || index == 10 ) emailExists = true;
						
						if(index <= 13) {
							//field = fieldLb.getSelectedItem().getLabel();
							field = fieldLb.getSelectedItem().getValue();//changed when (pin---pin/zip;phone-----mobile)
							
							//logger.info("Field::"+field+", "+fieldLb.getSelectedItem().getValue());
							
							if(isFieldExists(fieldList,field)) { 
								MessageUtil.setMessage(field + " field is repeated","color:red","TOP");
								return ;
							}
							//BirthDay,Anniversary
							
//							logger.info(">>>>>>>>Field name is::" +field);
							if(field.trim().equals("BirthDay") || field.trim().equals("Anniversary")) {
								//dateFormat 
								logger.info("------"+vb.getChildren().get(1));
								Div tempDiv = (Div)vb.getChildren().get(1);
								Hbox  tempHbox = (Hbox)tempDiv.getChildren().get(0);
								Div div = (Div)tempHbox.getChildren().get(3);
								
								String dateFormatStr = ((Combobox)div.getChildren().get(1)).getSelectedItem().getLabel();
								logger.debug(">>>>>>>>Field name ::" +field);
								logger.debug("dateFormatStr ::" +dateFormatStr);
								
								genFieldMap.put(field, dateFormatStr);
								
								logger.debug(">>Field "+genFieldMap.get("BirthDay"));
								logger.debug(">>Field "+genFieldMap.get("Anniversary"));
//								cfMap.put(field,dateFormatStr);;
								
								//cfMap.put(field,colIndex+ ":" + cfName +":"+typeLb.getSelectedItem().getLabel() + ":" + defValue);
							} 
						}
						else {
							if(isNew != null) {
								fieldIndexList.add(-1);  // TODO : testing code need to be modified.
								
								if(isNew.equalsIgnoreCase("true")) {

									
									field = "Custom Field"+cfIndex;
									//fieldIndexList.add(-1 * cfIndex);  // TODO : testing code need to be modified.

									cfIndex++;
									
									Hbox tempHbox = (Hbox)vb.getChildren().get(1).getChildren().get(0);
									/***** Custom Field Name*******/
									Div tempDiv =(Div)tempHbox.getChildren().get(0);
									
									//Textbox tb = ((Textbox)((Hbox)((Div)vbChildren.get(2)).getChildren().get(1)).getChildren().get(0));
									Textbox tb =(Textbox)tempDiv.getChildren().get(1);
									cfName = tb.getValue().trim();
									
									
									if(logger.isDebugEnabled())logger.debug("name"+tb.getValue());
									/*********custom field Data Type***********/
									tempDiv =(Div)tempHbox.getChildren().get(1);
									Listbox typeLb = (Listbox)tempDiv.getChildren().get(1);
									//Listbox typeLb = ((Listbox)((Hbox)((Div)vbChildren.get(2)).getChildren().get(1)).getChildren().get(1));
									
									if(tb.getValue().length()==0 && (typeLb.getSelectedIndex() <= 0 )){
										MessageUtil.setMessage("Provide name and type for the custom field.", "color:red", "TOP");
										if(logger.isDebugEnabled())logger.debug("name is"+tb.getValue());
										return;
									}
									
									if(!Utility.validateVariableName(cfName)) {
										MessageUtil.setMessage("No special characters allowed for custom field name except '_'.","color:red","TOP");
										return;
									}
									
									logger.debug(" Checking " + cfName + " in the set for exist :" + cfNameSet.contains(cfName));
									if( cfNameSet.contains(cfName) ) {
										MessageUtil.setMessage("Custom field name :" + cfName + " is repeated.","color:red","TOP");
										return;
									}
									cfNameSet.add(cfName);
									if(typeLb.getSelectedIndex() <= 0){
										MessageUtil.setMessage("Select type for custom field.","color:red","TOP");
										return;
									}
									
									/************Default Value**********/
									
									if(!typeLb.getSelectedItem().getLabel().equals("Date")) { //except Date Type
										
										tempDiv =(Div)tempHbox.getChildren().get(2);
										//tb = ((Textbox)((Hbox)((Div)vbChildren.get(2)).getChildren().get(1)).getChildren().get(2));
										tb= (Textbox)tempDiv.getChildren().get(1);
										defValue = tb.getValue();
										if(defValue.length() ==0){
											defValue = "";
										}
										else if(!CustomFieldValidator.validate(defValue,typeLb.getSelectedItem().getLabel())) {
											MessageUtil.setMessage("Invalid default data for the custom field :" + cfName,"color:red","TOP");
											return;
										}
										
									} else {
										//DateFormat  
										//NOTE: here defValue = dateFormat
										tempDiv =(Div)tempHbox.getChildren().get(3);
										Combobox temCombobox =(Combobox) tempDiv.getChildren().get(1);
										defValue = temCombobox.getSelectedItem().getLabel().trim();
										
									}
									
									
									////====>Custom Field1:5:Number:age:100 //Custom Field2=5:dob:Date:12/12/1234
									logger.info("====>"+field +"::colIndex ::"+colIndex +":::cfName ::"+cfName +
											"::::typeLb.getSelectedItem().getLabel()"+typeLb.getSelectedItem().getLabel() +":::::defValue :"+defValue);
									
									cfMap.put(field,colIndex+ ":" + cfName +":"+typeLb.getSelectedItem().getLabel() + ":" + defValue);
								}
								else {  
									
									 
									//field = fieldLb.getSelectedItem().getLabel();
									field = fieldLb.getSelectedItem().getValue();
									
									String temp = field.substring(field.indexOf(':')+1);
									
									MLCustomFields mlfield = mlCFDao.findCFObjByName(mailingList,temp);

//									fieldIndexList.add(-1 * mlfield.getFieldIndex());  // Testing code need to be modified.
									
									//Custom Field2=5:dob:Date:12/12/1234
									logger.info(":(mlfield.getSelectedField() is :"+(mlfield.getSelectedField() +"::colIndex :"+colIndex+ " ::temp is ::"+temp +
											
											":mlfield.getDataType() is::"+mlfield.getDataType()+"::+ mlfield.getDefaultValue() is ::"+ mlfield.getDefaultValue()));
								
									cfMap.put(mlfield.getSelectedField(),colIndex+ ":" + temp+":" + mlfield.getDataType() + ":" + mlfield.getDefaultValue());
								}
								
							}
						}
						
						fieldList.add(field);
						
						int fieldIndex = getFieldIndex(defaultFieldArray,field);
						
						if(fieldIndex!=-1)
							fieldIndexList.add(fieldIndex);
						
						if(logger.isDebugEnabled())logger.debug("field is"+field);
						if(logger.isDebugEnabled())logger.debug("size of field"+fieldList.size());
					}
				} // inner for 
			} // outer for
			
			if(logger.isDebugEnabled())logger.debug("size of field"+fieldList.size());
			if(!emailExists){
				if (fieldList.size()==1 && !(field.contains("Email")) ){MessageUtil.setMessage("At least email must be selected.","color:red","TOP");
				return;}else
			
				MessageUtil.setMessage("Email or mobile must be selected.","color:red","TOP");
				return;
			}
			if(cfIndex > 21){
				MessageUtil.setMessage("You can select maximum of 20 custom fields only.","color:red","TOP");
				return;
			}
			MessageUtil.clearMessage();
			session.setAttribute("fieldList",fieldList);
			session.setAttribute("fieldIndexList",fieldIndexList);
			
			//this map contains only Birthday and Anniversary fields and their Format
			
			logger.debug("genFieldMap.size() &&&&&&&&"+genFieldMap.size());
			if(genFieldMap.size() >0) {
				session.setAttribute("genFieldMap", genFieldMap);
			}
			
			if(cfMap.size() > 0){
				Set upFile = (Set)session.getAttribute("uploadFile_Ml");
				for(Object mlObj:upFile){
					MailingList mailingList = (MailingList)mlObj;
					mailingList.setCustField(true);
				}
				session.setAttribute("cfMap", cfMap);
			}
			PageUtil.setFromPage("/contact/FieldSelection.zul");
			Redirect.goTo(PageListEnum.CONTACT_UPLOAD_FILE);
		}catch(Exception e){
			logger.error(" ** Exception :"+ e +" **");
		}
	}

	
	public void onClick$backBtnId() {
   		session.removeAttribute("newListName");
		session.removeAttribute("isNewML");
		session.removeAttribute("uploadFile_Ml");
		Redirect.goTo(PageListEnum.CONTACT_UPLOAD);
	}
	
	public boolean isFieldExists(List fieldList,String field){
		for(int j=0;j<fieldList.size();j++){
			if(field.equals(fieldList.get(j))){
				return true;
			}
		}
		return false;
	}
	
	public int getFieldIndex(String[] fieldArray, String field) {
		for(int i=0;i<fieldArray.length;i++) {
			if(field.equals(fieldArray[i])) {
				return i;
			}
		}
		return -1;
	}

	
	/**
	 *   If Custom Field is selected, this method enables the custom field components
	 */
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		try{
			if(event.getTarget() instanceof Listbox) {
			
			
			Listbox lb = (Listbox)event.getTarget();
			if(lb.getName().equals("dataTypelb")) {
				
				/*********defaultValue Div******/
				Div defaultValueDiv  = (Div)lb.getParent().getParent().getChildren().get(2);
				
				/*********dateFormat Div******/
				Div dateFormatDiv  = (Div)lb.getParent().getParent().getChildren().get(3);
				
				//Textbox tb = (Textbox)lb.getParent().getChildren().get(2);
				Textbox tb = (Textbox)lb.getParent().getParent().getChildren().get(2).getChildren().get(1);
				
				logger.info(" lb.getSelectedItem().getValue() :"+lb.getSelectedItem().getValue());
				if(!lb.getSelectedItem().getValue().equals("Date")) {
					
					tb.removeEventListener(Events.ON_CLICK, this);
					tb.removeEventListener(Events.ON_BLUR, this);
					tb.setValue(SINGLE_SPACE);
					defaultValueDiv.setVisible(true);
					dateFormatDiv.setVisible(false);
					
					return;
				}else  {
					defaultValueDiv.setVisible(false);
					dateFormatDiv.setVisible(true);
					return;
				}
				/*tb.setValue(dateFormatStr);
				tb.addEventListener(Events.ON_CLICK, new EventListener() { 
					public void onEvent(Event event) throws Exception {
						Textbox tb = (Textbox)event.getTarget();
						String tbValStr = tb.getValue(); 
						if(tbValStr.trim().equals(dateFormatStr)) {
							tb.setValue(SINGLE_SPACE);
						}	
					}
				});
				tb.addEventListener(Events.ON_BLUR, new EventListener() { 
					public void onEvent(Event event) throws Exception {
						Textbox tb = (Textbox)event.getTarget();
						if((tb.getValue().trim().equals(SINGLE_SPACE))) {
							tb.setValue(dateFormatStr);
						}	
					}
				});*/
			}	
			/*else {
			Listitem li = lb.getSelectedItem();
			String field = (String)li.getValue();
			String cfDivId = (String)lb.getAttribute("divId");
			logger.info("cfDivId is >>>>"+Utility.getComponentById(cfDivId));
			Div div = (Div)Utility.getComponentById(cfDivId);
				
				Listitem li = lb.getSelectedItem();
				String field = lb.getSelectedItem().getLabel();
				
				Vbox tempVbox = (Vbox)lb.getParent().getParent();
				if(field.contains("Custom Field")) {
					tempVbox.getChildren().get(1).setVisible(true);
				}else {
					tempVbox.getChildren().get(1).setVisible(false);
				}
				String cfDivId = (String)lb.getAttribute("divId");
				field
				logger.info("cfDivId is >>>>"+Utility.getComponentById(cfDivId));
				Div div = (Div)Utility.getComponentById(cfDivId);
				
			
			if(field != null) {
				if(field.contains("Custom Field"))
					div.setVisible(true);
				else
					div.setVisible(false);
			}
			}*/
			
			}
			
			
			 if(event.getTarget() instanceof Toolbarbutton) {
				
				if(i <= 1) return;
				
				i--;
				
				Row parentRow =(Row) event.getTarget().getParent().getParent().getParent();
				
				fieldRowsId.removeChild(parentRow);
				resetColomnIndex();
			}
			
			
		}catch (Exception e) {
			logger.error("Exception in the Event :", e);
		}
	}

	public void onClick$addTbId() {
		try {
			addColumn();
		} catch (Exception e) {
			logger.error("Exception in the addColumn method :", e);
		}
	}
	
	/*public void onClick$remTbId() {
		try {
			removeColumn();
		} catch (Exception e) {
			logger.error("Exception in the removeColumn method :", e);
		}
	}*/
	
	
	private void resetColomnIndex() {
		try {
			List  temRowList = fieldRowsId.getChildren();
			int index = 1;
			for (Object object : temRowList) {
				
				
				Row tempRow = (Row) object;
//				logger.info("tempRow Children list size ::" +tempRow.getChildren());
				Vbox tempVbox = (Vbox)tempRow.getChildren().get(0);
//				logger.info("tempVbox Children list size ::" +tempVbox.getChildren().size());
				Div tempDiv = (Div)tempVbox.getChildren().get(0);
				
				Label resetColomnLbl  = (Label)tempDiv.getFirstChild();
				
				resetColomnLbl.setValue("Column "+""+index+" : ");
				resetColomnLbl.setSclass("theme-lbHeading");
				resetColomnLbl.setStyle("display: inline-block;text-align: left;width: 80px;");
				index++;
				
				
			}
		} catch (Exception e) {
			logger.error("Exception while reset the Column index ",e);
		}
	}
	
}

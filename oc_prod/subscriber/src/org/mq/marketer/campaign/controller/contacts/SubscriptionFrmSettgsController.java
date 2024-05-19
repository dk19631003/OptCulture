package org.mq.marketer.campaign.controller.contacts;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.components.NumberField;
import org.mq.marketer.campaign.components.TextBoxField;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class SubscriptionFrmSettgsController extends GenericForwardComposer  {
	
	private Div selectedFormDivId;
	private Listbox mailingListLbId;
	private Div finalLinkDivId;
	private Grid formOneFieldsGridId,formTwoFieldsGridId;
	private Rows formOneFieldsRowsId,formTwoFieldsRowsId;
	private Button saveBtnId;
	private Button saveFldBtnId;
	//private Button txtBoxFldBtnId;
	private Textbox finalLinkTbId;
	private Textbox formNameTbId;
	private MailingListDao mailingListDao;
	private Textbox finalHtmlHiddenId;
	private CustomTemplatesDao customTemplatesDao;
	private CustomTemplatesDaoForDML customTemplatesDaoForDML;
	private Checkbox doubleOptInCbId,rqrFldCbId;
	private Users currentUser;
	private Desktop desktop;
	
	Html previewContentHTMLId1;
	private Radiogroup fldVisibilityRgId;
	private Tab fldSettingTabId;
	private  Listbox fieldsLbId;
	private  Textbox fldLblTbId,fldDefValTbId,fldIdTbId;
	private Label fldTypeLblId,errLabelId;
	private Listbox inputFieldsLbId;
	private Long editFormId = null;
	private String formHTML = null;
	private String token[];
	private String formIdStr;
	private List tbList ;
	private CustomTemplates customTemplates;
	
	private Button saveBtnSettingsBtnId;
  	private Textbox submitBtnLblTbId,resetBtnLblTbId;
	private Intbox  submitBtnWidthIbId,resetBtnWidthIbId;
	private Checkbox resetBtnRqrCbId;
	private Radiogroup  buttonsAlignmntRgId;
	private Radio lftAlignRadioId,centerAlignRadioId,rghtAlignRadioId;
	private Button subsFrmResetButtonBtnId,subsFrmSubmitButtonBtnId;
	private Hbox subsFrmBtnsHbId;
	private Label frmBtnsErrLabelId;
	
	private Listbox editFormViewLbId;
	private Textbox frmHeadngTbId;
	private Div formBtnPropsDivId,formFldPropsDivId,formTitleDivId;
	 
	private Listbox mlFieldsLbId;
	private Map<String,String> custFldsMap;
	   
	
	private static Map<String,String> addField =null;
	private Map<String,TextBoxField> formCompMap;// = new HashMap<String, TextBoxField>();
	
	private Window inputFieldsWinId;
	private Div fieldsDivId;
	private Map<String,String> strContentStr;
	
	private Session session;
	
	//Set<Long> userIdsSet = GetUser.getUsersSet();//added for multy user acc
	
	
	static {
		
		addField = new HashMap<String, String>();
		addField.put("text", "<tr><td <VAL7>><span id='"+"<VAL6>"+"'>"+"<VAL1>"+" </span></td><td><input type='"+"<VAL4>"+"' name='"+"<VAL5>"+"' id='"+"<VAL2>"+"' value='"+"<VAL3>"+"'  MAXLENGTH='100' onclick='jsFunction:' /></td></tr>");
		addField.put("text_form", "<tr><td <VAL7>><span id='"+"<VAL6>"+"'>"+"<VAL1>"+ "</span></td></tr><tr><td><input type='"+"<VAL4>"+"' name='"+"<VAL5>"+"' id='"+"<VAL2>"+"' value='"+"<VAL3>"+"'  MAXLENGTH='100' onclick='jsFunction:' /></td></tr>");
	}														
															
	private String resetBtnStr = "<td align='<VAL>'> <input type='reset' value='<VAL3>' width='<VAL4>' /></td>";
															
	private String formTopStr = "<form name='webForm' action='|^actionAttribute^|' method='get'>"+        
								"<table id='formId' cellpadding='2' cellspacing='2' style='background-color: transparent;' width='300px'>"+
							 	"<tr><td colspan='2' align='center'> <span><FORMHEADING></span> </td></tr>"+
								"<tr><td colspan='2' align='center'><div id='errorDivId'></div></td></tr>";
			 			
	private String formFieldsStr = "<tr> <RESET> <td align='"+"<VAL>"+"'>"+
									"<input type='HIDDEN' name='process' value='true' />"+
									"<input type='HIDDEN' name='uId' value='|^uId^|' />"+
									"<input type='HIDDEN' name='mId' value='|^mId^|' />"+
									"<input type='HIDDEN' name='dId' value='|^dId^|' />"+
									"<input type='submit' value='"+"<VAL1>"+ "' width='"+"<VAL2>"+"' onClick='javascript:' /></td></tr></table></form>";	 	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public SubscriptionFrmSettgsController() {

		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		customTemplatesDao = (CustomTemplatesDao)SpringUtil.getBean("customTemplatesDao");
		customTemplatesDaoForDML = (CustomTemplatesDaoForDML)SpringUtil.getBean("customTemplatesDaoForDML");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
     	PageUtil.setHeader("Edit Subscription Form","",style,true);
     	
     	session = Sessions.getCurrent();
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		try {
			super.doAfterCompose(comp);
			
			String mlId="";
			tbList = new ArrayList();
			strContentStr = new HashMap<String, String>();//used to store the temporary modified string values
			desktop = Executions.getCurrent().getDesktop();
			Html html = new Html();
			formCompMap = (Map)desktop.getAttribute("formComponentsMap");
			formHTML = (String)desktop.getAttribute("formHTML");
			formIdStr=(String)desktop.getAttribute("selectedForm");//in promotecentercontroller desktop.setAttribute("selectedForm", formIdStr);
			
			if(formIdStr!=null){//it is a new form
				token= formIdStr.split(":");
				logger.debug("the selected form is...."+token[0]);
				tbList = (List)formCompMap.get(token[0]);
			} // if
			
			
			editFormId = (Long)desktop.getAttribute("editSubscriptionFormId");
			logger.debug("the existing form id is...."+editFormId);
			
			
			if(formHTML == null && editFormId == null) {
				this.onClick$back();
			}
			
			if(editFormId != null && formHTML == null) {
				List<CustomTemplates> list = customTemplatesDao.getSubscriptionFormById(editFormId);
				customTemplates = (CustomTemplates)list.get(0);
				token = customTemplates.getSelectedForm().split(":");
				formHTML = customTemplates.getHtmlText();
				mlId = ""+customTemplates.getIframeLink().substring(customTemplates.getIframeLink().indexOf("&mId="), customTemplates.getIframeLink().indexOf("&dId="));
				mlId = mlId.replace("&mId=", "");
				
				//***********prepare complete view to edit the existing form********
				prepareView(formHTML);
			
				
				
				formNameTbId.setValue(customTemplates.getTemplateName());
				formNameTbId.setReadonly(true);
				finalLinkDivId.setVisible(true);
				finalLinkTbId.setValue(customTemplates.getIframeLink());
				//saveBtnId.setLabel("Update");
				
			}
			//html.setContent("<div id='FormDivId'> " + formHTML  + "</div>");
			
			//html.setParent(selectedFormDivId);
			
			
			
			
			//set mailing Lists for List box
			currentUser = GetUser.getUserObj();
			Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
			//List<MailingList> list = mailingListDao.findAllByUser(userIdsSet);
			List<MailingList> list = mailingListDao.findByIds(listIdsSet);
			
			if(list.size() > 0) {
				ListModel strset = new SimpleListModel(list);
				mailingListLbId.setModel(strset);
				mailingListLbId.setItemRenderer(new ListRenderer());
			} else {
				Listitem li; 
				li = new Listitem("No mailingList");
				li.setParent(mailingListLbId);
			}
			
			mailingListLbId.setSelectedIndex(0);
			
			//********sets the selected mailing list fields to map the form fields******
			if(editFormId != null){
				for(int i=0;i<mailingListLbId.getItemCount(); i++){
					if(mlId.equals(""+((MailingList)mailingListLbId.getListModel().getElementAt(i)).getListId())){
						logger.info("id is=====>"+""+((MailingList)mailingListLbId.getListModel().getElementAt(i)).getListId());
						mailingListLbId.setSelectedIndex(i);
						break;
					}
				}
			}else if(editFormId == null){
				mailingListLbId.setSelectedIndex(0);
			}
			setMlFieldsToList();
			
			editFormViewLbId.setModel(new ListModelList(tbList));
			editFormViewLbId.setItemRenderer(new ListRenderer());
			
			
			editFormViewLbId.setSelectedIndex(1);
			Object obj = editFormViewLbId.getListModel().getElementAt(1);
			setFldValues(obj);
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
	}
	/**
	 * this method prepares the form view to allow the user to edit the existing form 
	 *
	 * @param formHTML is the form html content
	 */
	
	
	public void prepareView(String formHTML){
		String elementAttrVal="";
		String[] nameArr = null;
		String[] elementAttrValArr = null;
		TextBoxField textbox = null;
		
		NumberField number = null;
		//*************parse the existing html form to prepare the view*************
		HashMap<String, String> retMap = parseForm(formHTML,"input");
		HashMap<String, String> returnedMap = parseForm(formHTML,"span");
		String title = returnedMap.get("formTitle");
		logger.info("title is....>"+title);
		Hbox hBox = new Hbox();
		hBox.setWidth("300px");
		hBox.setPack("center");
		
		Label formTitleLabel = new Label();
		formTitleLabel.setMultiline(true);
		formTitleLabel.setValue(title);
		formTitleLabel.setParent(hBox);
		
		tbList.add(hBox);
		//**********form elements settings*******
		if(formHTML.contains("reset")){
			//*****if the form contains reset button*********
			for(int i=0;i<retMap.size()-2;i++){
				logger.info(retMap.size());
				elementAttrVal = retMap.get(""+i);
				
				logger.info("eleemtAttVal  : "+elementAttrVal);
				
				 elementAttrValArr = elementAttrVal.split("\\|");
				//logger.info(("State_false_Text_string|textBoxId4|enter text...").split("|"));
				 nameArr  = elementAttrValArr[0].split("_");
				//logger.info(elementAttrValArr[0]+"*******"+elementAttrValArr[1]+"*********"+elementAttrValArr[2]);
				if(nameArr[2].equalsIgnoreCase("text")) {
					textbox = new TextBoxField();
					textbox.setMapField(elementAttrValArr[0]);
					textbox.setId(elementAttrValArr[1]);
					textbox.setLabel(returnedMap.get(elementAttrValArr[0]));
					//logger.info(returnedMap.get(elementAttrValArr[0]));
					textbox.setDefaultValue(elementAttrValArr[2]);
					if(elementAttrValArr[3].equalsIgnoreCase("text")) {
						textbox.setVisible("visible");
					}else {
						textbox.setVisible("hidden");
					}
					if(nameArr[1].equalsIgnoreCase("true")){
						textbox.setRequired(1==1);
					}else{
						textbox.setRequired(1!=1);
					}
					tbList.add(textbox);
					//logger.info("tbList size is....2....."+tbList.size());
				}else if(nameArr[2].equalsIgnoreCase("number")){
					number = new NumberField();
					number.setMapField(elementAttrValArr[0]);
					number.setId(elementAttrValArr[1]);
					number.setDefaultValue(elementAttrValArr[2]);
					if(elementAttrValArr[3].equalsIgnoreCase("text")){
						number.setVisible("visible");
					}else {
						number.setVisible("hidden");
					}
					//logger.info(elementAttrValArr[1]);
					number.setLabel(returnedMap.get(elementAttrValArr[0]));
					if(nameArr[1].equalsIgnoreCase("true")){
						number.setRequired(1==1);
					}else{
						number.setRequired(1!=1);
					}
					tbList.add(number);
					//logger.info("tbList size is....3....."+tbList.size());
				}//else if
			}//for
		}//if
		else{
			//*****if the form doesnot contain reset button********* 
			for(int i=0;i<retMap.size()-1;i++){
				logger.info(retMap.size());
				elementAttrVal = retMap.get(""+i);
				
				logger.info("eleemtAttVal  : "+elementAttrVal);
				
				 elementAttrValArr = elementAttrVal.split("\\|");
				//logger.info(("State_false_Text_string|textBoxId4|enter text...").split("|"));
				 nameArr  = elementAttrValArr[0].split("_");
				logger.info(elementAttrValArr[0]+"*******"+elementAttrValArr[1]+"*********"+elementAttrValArr[2]);
				if(nameArr[2].equalsIgnoreCase("text")){
					textbox = new TextBoxField();
					textbox.setMapField(elementAttrValArr[0]);
					textbox.setId(elementAttrValArr[1]);
					textbox.setLabel(returnedMap.get(elementAttrValArr[0]));
					logger.info(returnedMap.get(elementAttrValArr[0]));
					textbox.setDefaultValue(elementAttrValArr[2]);
					if(elementAttrValArr[3].equalsIgnoreCase("text")){
						textbox.setVisible("visible");
					}else {
						textbox.setVisible("hidden");
					}
					if(nameArr[1].equalsIgnoreCase("true")){
						textbox.setRequired(1==1);
					}else{
						textbox.setRequired(1!=1);
					}
					tbList.add(textbox);
				}else if(nameArr[2].equalsIgnoreCase("number")){
					number = new NumberField();
					number.setMapField(elementAttrValArr[0]);
					number.setId(elementAttrValArr[1]);
					number.setDefaultValue(elementAttrValArr[2]);
					number.setLabel(returnedMap.get(elementAttrValArr[0])); 
					if(elementAttrValArr[3].equalsIgnoreCase("text")){
						number.setVisible("visible");
					}else {
						number.setVisible("hidden");
					}
					if(nameArr[1].equalsIgnoreCase("true")){
						number.setRequired(1==1);
					}else{
						number.setRequired(1!=1);
					}
					tbList.add(number);
				}//else if
			}//for
		}//else
		//*******button settings**********
		String[] btnprops = null;
		hBox = new Hbox();
		if(formHTML.contains("reset")){
		btnprops = retMap.get("reset").split("\\|");
		//logger.info("align is--->"+btnprops[0]);
		if(btnprops[0].equalsIgnoreCase("left")){
			hBox.setPack("start");
		}else if(btnprops[0].equalsIgnoreCase("right")){
			hBox.setPack("end");
		}else{
			hBox.setPack("center");
		}
		
		
		Button resetBtn = new Button();
		resetBtn.setLabel(btnprops[1]);
		resetBtn.setWidth(btnprops[2]);
		resetBtn.setParent(hBox);
		}
		
		btnprops = retMap.get("submit").split("\\|");
		Button submitBtn = new Button();
		submitBtn.setLabel(btnprops[1]);
		submitBtn.setWidth(btnprops[2]);
		submitBtn.setParent(hBox);
		
		tbList.add(hBox);
		//logger.info("tbList size is....4....."+tbList.size());
		
		
	}
	/**
	 * this method parses the existing html form to get the element's attribute
	 * values to prepare the view to edit the existing form 
	 * @param htmlStr is the form's(to be parsed) html content
	 * @param tag is the html element 
	 * @return a map containing all the info
	 */
	
	public HashMap<String, String> parseForm(String htmlStr,String tag){
		HashMap<String, String> fldsInfoHashMap = new HashMap<String, String>();
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(htmlStr.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName(tag);
			logger.info("NodeList : "+nodeLst +" and Len="+nodeLst.getLength());
			if(tag.equalsIgnoreCase("input")){
				Node node = null;
				Element element = null;
				String name = "";
				String id = "";
				String defVal = "";
				String type = "";
				if(htmlStr.contains("reset")){
					//logger.info("in if....");
					for(int i=0; i<(nodeLst.getLength())-6; i++){
						logger.info(nodeLst.getLength());
						node = nodeLst.item(i);
						element = (Element)node;
						name = element.getAttribute("name");
						id = element.getAttribute("id");
						defVal = element.getAttribute("value");
						type = element.getAttribute("type");
						fldsInfoHashMap.put(""+i, name+"|"+id+"|"+defVal+"|"+type);
						
						//logger.info(hashmapMessages);
					}//for	
				}else{
					logger.info("in else...");
					for(int i=0; i<(nodeLst.getLength())-5; i++){
						logger.info(nodeLst.getLength());
						node = nodeLst.item(i);
						element = (Element)node;
						name = element.getAttribute("name");
						id = element.getAttribute("id");
						defVal = element.getAttribute("value");
						type = element.getAttribute("type");
						fldsInfoHashMap.put(""+i, name+"|"+id+"|"+defVal+"|"+type);
					}//for
				}//else
				Node btnNode = nodeLst.item(nodeLst.getLength()-1);
				Element btnElement = (Element)btnNode;
				String width = btnElement.getAttribute("width");
				String value = btnElement.getAttribute("value");
				Node tdNode = btnElement.getParentNode();
				Element tdElement = (Element)tdNode;
				String align = tdElement.getAttribute("align");
				fldsInfoHashMap.put("submit", align+"|"+value+"|"+width);
				
				
				if(htmlStr.contains("reset")){
					logger.info("just entered for reset settings");
					btnNode = nodeLst.item(nodeLst.getLength()-6);
					btnElement = (Element)btnNode;
					width = btnElement.getAttribute("width");
					value = btnElement.getAttribute("value");
					tdNode = btnElement.getParentNode();
					tdElement = (Element)tdNode;
					align = tdElement.getAttribute("align");
					fldsInfoHashMap.put("reset", align+"|"+value+"|"+width);
				}//if
				
				logger.debug("the size of fldsInfoHashMap is..."+fldsInfoHashMap.size());
			}else if(tag.equalsIgnoreCase("span")){
				String id = "";
				Node node = nodeLst.item(0);
				Element element = (Element)node;
				String attrVal =element.getTextContent();
				fldsInfoHashMap.put("formTitle", attrVal);
				for(int i=1;i<nodeLst.getLength(); i++){
					node = nodeLst.item(i);
					element = (Element)node;
					id = element.getAttribute("id");
					attrVal =element.getTextContent();
					
					//Element element = (Element)node;
					//String attrVal = element.getNodeValue();
					fldsInfoHashMap.put(id, attrVal);
					logger.debug("the size of fldsInfoHashMap is..."+fldsInfoHashMap.size());
				}//for
				
			}//else if
			
			
		}catch (Exception e) {
			logger.error("Exception ::", e);
		}
		return fldsInfoHashMap;
	}
	
	class ListRenderer implements ListitemRenderer {
		public void render(Listitem li, Object data, int arg2) {
			 if (data instanceof MailingList) {
				
				
				MailingList ml = (MailingList) data;
				li.setValue(ml);
				li.setLabel(ml.getListName());
				li.setAttribute("isDoubleOptin", ml.getCheckDoubleOptin());
			}else if(data instanceof TextBoxField) {
				TextBoxField tb = (TextBoxField)data;
				li.setValue(tb);
				li.setHeight("30px");
				Listcell lc = new Listcell();
				
				Box box = new Box();
				box.setWidth("300px");
				if(token[0].equalsIgnoreCase("select1")){
					box.setOrient("horizontal");
					box.setPack("end");
				}else{
					box.setOrient("vertical");
					box.setPack("center");
				}
				Label label= new Label(tb.getLabel());
				label.setWidth("150px");
				label.setParent(box);
				label.setStyle("padding:15px 5px 0px 0px; ");
				Textbox textBox = new Textbox();
				textBox.setWidth("150px");
				textBox.setValue(tb.getDefaultValue());
				
				textBox.setParent(box);
				box.setParent(lc);
				lc.setParent(li);
				//editFormViewLbId.setSelectedIndex(editFormViewLbId.getItemCount()-1);
				
			}else if(data instanceof NumberField) {
				NumberField number = (NumberField)data;
				li.setValue(number);
				li.setHeight("30px");
				Listcell lc = new Listcell();
				
				Box box = new Box();
				box.setWidth("300px");
				if(token[0].equalsIgnoreCase("select1")){
				box.setOrient("horizontal");
				box.setPack("end");
				}else{
					box.setOrient("vertical");
					box.setPack("center");
				}
				Label label= new Label(number.getLabel());
				label.setWidth("150px");
				label.setParent(box);
				Textbox textBox = new Textbox();
				textBox.setWidth("150px");
				textBox.setValue(number.getDefaultValue());
				
				textBox.setParent(box);
				box.setParent(lc);
				lc.setParent(li);
				//editFormViewLbId.setSelectedIndex(editFormViewLbId.getItemCount()-1);
			}else if(data instanceof Hbox){
				Hbox hbox = (Hbox)data;
				li.setHeight("50px");
				li.setValue(hbox);
				Listcell lc = new Listcell();
				hbox.setWidth("300px");
				if(formIdStr!=null){
					if(((String)hbox.getAttribute("name")).equalsIgnoreCase("FormTitle")){
						hbox.setPack("center");
						
//						Label frmTitle = new Label("Form Title");
//						frmTitle.setParent(hbox);
						
						Label titleLabel = new Label("Subscribe to our Mailing List");
						titleLabel.setMultiline(true);
						titleLabel.setParent(hbox);
						
						hbox.setParent(lc);
						lc.setParent(li);
						
					}else{
						hbox.setPack("center");
						Button button = new Button();
						button.setLabel("Reset");
						button.setWidth("60px");
						button.setParent(hbox);
						
						button = new Button();
						button.setLabel("Submit");
						button.setWidth("60px");
						button.setParent(hbox);
						
						hbox.setParent(lc);
						lc.setParent(li);
						
					}//else
				}else{
					hbox.setParent(lc);
					logger.info("*************>"+hbox.getPack());
					lc.setParent(li);
				}//else
			}//else if
		}//render
	}//ListitemRenderer	
	
	public void onClick$back() {
		//Redirect.goTo(PageListEnum.CONTACT_PROMOTE_CENTER);
	}
	
	public void onSelect$mailingListLbId() {
		MessageUtil.clearMessage();
		int count = mlFieldsLbId.getItemCount();

		
		for(; count>0; count--) {
			mlFieldsLbId.removeItemAt(count-1);
		}
		
		setMlFieldsToList();
		
		
	}
	
	public void onClick$closeBtId() {
		try {
			MessageUtil.clearMessage();
			finalLinkTbId.setValue("");
			finalLinkDivId.setVisible(false);
			//saveFldBtnId.setDisabled(false);
			finalHtmlHiddenId.setValue("");
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}
	
	/*public void onChange$finalHtmlHiddenId() {
		try {
			
			MessageUtil.clearMessage();
			logger.debug("Selected Value is : "+mailingListLbId.getSelectedIndex());
			
			String formNameStr = Utility.condense(formNameTbId.getValue());
			logger.info("Name of the form is------------>"+formNameStr);
			
			if(!Utility.validateName(formNameStr)) {
				MessageUtil.setMessage("Please enter valid form name.", "red", "top");
				finalHtmlHiddenId.setValue("");
				return;
			}
			
			if(editFormId == null) {
				if(customTemplatesDao.checkTemplateStatusByName(users.getUserId(), formNameStr)) {
					MessageUtil.setMessage("Form with the given name already exists.", "red", "top");
					finalHtmlHiddenId.setValue("");
					return;
				}
			}
			
			if(mailingListLbId.getSelectedItem().getValue() == null) {
				MessageUtil.setMessage("No mailing list is selected.", "red", "top");
				finalHtmlHiddenId.setValue("");
				return;
			}
			
			String ifrmSrcStr = PropertyUtil.getPropertyValue("subscriptionSrc");
			
			// Check if update or new Form.
			if(editFormId != null) {
				customTemplatesDao.saveOrUpdate(customTemplates); 
				if(logger.isDebugEnabled())
					logger.debug("Custome Templates updated succesfully . Form Id : "+editFormId);
			}
			else {
				String finalHtmlStr = new String(finalHtmlHiddenId.getValue());
				
				String subscrptnFrmActnAtt = PropertyUtil.getPropertyValue("subscrptnFrmActnAtt");
				finalHtmlStr.replace(subscrptnFrmActnAtt, ifrmSrcStr);
				
				customTemplates = new CustomTemplates(users, formNameStr, finalHtmlStr, "subscriptionFormHTML",token[0]+":"+token[1]);
				
				customTemplates.setSelectedForm(token[0]+":"+token[1]);
				customTemplatesDao.saveOrUpdate(customTemplates);
				logger.debug("Last inserted id is :" + customTemplates.getTemplateId());
			}
			
			String ifrmLinkStr = "<iframe height='200px' width='350px' src='" + ifrmSrcStr 
			+ "?uId=" + users.getUserId() + "&mId=" + mailingListLbId.getSelectedItem().getValue()
			+ "&dId=" + customTemplates.getTemplateId() + "'></iframe>";
			
			finalLinkTbId.setValue(ifrmLinkStr);
			
			if(editFormId == null) {
				customTemplates.setIframeLink(ifrmLinkStr);
				customTemplatesDao.saveOrUpdate(customTemplates);
			}
						
			saveBtnId.setDisabled(true);
			finalLinkDivId.setVisible(true);
			finalHtmlHiddenId.setValue("");
			
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
	}*/
	
	@SuppressWarnings("unchecked")
	public void onCheck$doubleOptInCbId() {
		try {
			MessageUtil.clearMessage();
			List<Component> itemList = mailingListLbId.getChildren();
			boolean ischeck;
			for (Component eachComp : itemList) {
				Listitem li = (Listitem)eachComp;
				ischeck = doubleOptInCbId.isChecked()? (Boolean)li.getAttribute("isDoubleOptin") : true;
				li.setVisible(ischeck);
			}
				
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
	}
	
	

	
	
	public String resetBtnSettings(Button resetBtn, Hbox hbox, String tempFormFieldsStr){
		
		try {
			
			MessageUtil.clearMessage();
			
			String tempResetBtnStr = resetBtnStr;
			//String tempFormFieldsStr = "";
			//************* sets the reset button label *************
			if(resetBtnLblTbId.getValue().trim().length() > 0 ) {
				
				resetBtn.setLabel(resetBtnLblTbId.getValue());
				//resetBtnStr = resetBtnStr.replace("<VAL3>",resetBtnLblTbId.getValue() );
				tempResetBtnStr = tempResetBtnStr.replace("<VAL3>",resetBtnLblTbId.getValue() );
				//logger.info("after modifying the label===>"+tempResetBtnStr);
				//logger.info("in if formFieldsStr is>>>>>>>>>>"+formFieldsStr);
			}
			else {
				//resetBtn.setLabel("Reset");
				//resetBtnStr = resetBtnStr.replace("<VAL3>","Reset");
				tempResetBtnStr = tempResetBtnStr.replace("<VAL3>",resetBtn.getLabel());
				//logger.info("in else formFieldsStr is>>>>>>>>>>"+formFieldsStr);
			}//else
			
			//****** sets reset button width ************
			if( resetBtnWidthIbId.getValue() != null) {
				
				resetBtn.setWidth(resetBtnWidthIbId.getValue().intValue()+"px");
				//resetBtnStr = resetBtnStr.replace("<VAL4>", ""+resetBtnWidthIbId.getValue().intValue());
				tempResetBtnStr = tempResetBtnStr.replace("<VAL4>", ""+resetBtnWidthIbId.getValue().intValue());
				//logger.info("formTopStr after changing the width is"+formFieldsStr);
			}
			else{
				//resetBtnStr = resetBtnStr.replace("<VAL4>", "30");
				tempResetBtnStr = tempResetBtnStr.replace("<VAL4>", resetBtn.getWidth());
				//logger.info("formTopStr after changing the width is (in else)"+formFieldsStr);
			}
			
			
			//************ checks whether reset is required or not ***************
			if(!(resetBtnRqrCbId.isChecked())) {
				resetBtn.setVisible(false);
				
				/*if(hbox.removeChild(resetBtn)){
					//logger.info("reset is removed"+hbox.getChildren().size());
				}else{
					logger.info("reset is not removed"+hbox.getChildren().size());
				}*/
				//logger.info("resetBtnStr is...>"+resetBtnStr);
				//logger.info("formFieldsStr is.....>"+formFieldsStr);
				
				//formFieldsStr = formFieldsStr.replace("<RESET>", "");
				tempFormFieldsStr = tempFormFieldsStr.replace("<RESET>", "");
				
				//logger.info("formFieldsStr is------------------->"+formFieldsStr);
			}
			else {
				//logger.info("tempResetBtnStr is===>"+tempResetBtnStr);
				//formFieldsStr = formFieldsStr.replace("<RESET>", resetBtnStr);
				tempFormFieldsStr = tempFormFieldsStr.replace("<RESET>", tempResetBtnStr);
				//logger.info("tempFormFieldsStr is====>"+tempFormFieldsStr);
			}//else
			//************ sets the alignment for the buttons ***************
			
			if(!(buttonsAlignmntRgId.getSelectedItem().getValue().toString().equalsIgnoreCase("center"))){
				
				//resetBtnStr = resetBtnStr.replace("<VAL>", buttonsAlignmntRgId.getSelectedItem().getValue());
				tempResetBtnStr = tempResetBtnStr.replace("<VAL>", buttonsAlignmntRgId.getSelectedItem().getValue().toString());
				//formFieldsStr = formFieldsStr.replace("<VAL>", buttonsAlignmntRgId.getSelectedItem().getValue());
				tempFormFieldsStr = tempFormFieldsStr.replace("<VAL>", buttonsAlignmntRgId.getSelectedItem().getValue().toString());
			}else{
				//resetBtnStr = resetBtnStr.replace("<VAL>", "center");
				tempResetBtnStr = tempResetBtnStr.replace("<VAL>", "center");
				//formFieldsStr = formFieldsStr.replace("<VAL>", "center");
				tempFormFieldsStr = tempFormFieldsStr.replace("<VAL>", "center");
			}//else
			
		
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		return tempFormFieldsStr;
	}
	
	
	public void onClick$saveBtnSettingsBtnId() {
		try {
			MessageUtil.clearMessage();
			String align="";
			frmBtnsErrLabelId.setVisible(true);
			
			String tempFormFldStr = formFieldsStr;
			
			Hbox hbox = (Hbox)((Listcell)editFormViewLbId.getItemAtIndex(editFormViewLbId.getItemCount()-1).getChildren().get(0)).
						getChildren().get(0);
				if(hbox.getChildren().size() == 2){
					Button resetBtn = (Button)hbox.getChildren().get(0);
					Button submitBtn = (Button)hbox.getChildren().get(1);
					
					//*********** sets the label for submit button ******
					if(submitBtnLblTbId.getValue().trim().length() > 0 ){
						submitBtn.setLabel(submitBtnLblTbId.getValue());
						//logger.info("******************"+submitBtnLblTbId.getValue());
						//formFieldsStr = formFieldsStr.replace("<VAL1>", submitBtnLblTbId.getValue());
						tempFormFldStr = tempFormFldStr.replace("<VAL1>", submitBtnLblTbId.getValue());
					}else{
						//submitBtn.setLabel("Submit");
						//formFieldsStr = formFieldsStr.replace("<VAL1>", "Submit");
						tempFormFldStr = tempFormFldStr.replace("<VAL1>", submitBtn.getLabel());
					}//else
					
					
					
					//***********sets the width for submit button **********
					
					if(submitBtnWidthIbId.getValue() != null) {
						submitBtn.setWidth(submitBtnWidthIbId.getValue().intValue()+"px");
						//formFieldsStr = formFieldsStr.replace("<VAL2>",""+submitBtnWidthIbId.getValue().intValue());
						tempFormFldStr = tempFormFldStr.replace("<VAL2>",""+submitBtnWidthIbId.getValue().intValue());
					}
					else {
						
						//formFieldsStr = formFieldsStr.replace("<VAL2>","30");
						tempFormFldStr = tempFormFldStr.replace("<VAL2>",submitBtn.getWidth());
					}//else
					
					
					tempFormFldStr = resetBtnSettings(resetBtn,hbox,tempFormFldStr);
					align = buttonsAlignmntRgId.getSelectedItem().getValue();
					if(align.equalsIgnoreCase("left")){
						hbox.setPack("start");
					}else if(align.equalsIgnoreCase("right")){
						hbox.setPack("end");
					}else{
						hbox.setPack("center");
					}
					
			}else{
				Button submitBtn = (Button)hbox.getChildren().get(0);
				
				//*********** sets the label for submit button ******
				if(submitBtnLblTbId.getValue().trim().length() > 0 ){
					submitBtn.setLabel(submitBtnLblTbId.getValue());
					logger.info("******************"+submitBtnLblTbId.getValue());
					//formFieldsStr = formFieldsStr.replace("<VAL1>", submitBtnLblTbId.getValue());
					tempFormFldStr = tempFormFldStr.replace("<VAL1>", submitBtnLblTbId.getValue());
				}else{
					//submitBtn.setLabel("Submit");
					//formFieldsStr = formFieldsStr.replace("<VAL1>", "Submit");
					tempFormFldStr = tempFormFldStr.replace("<VAL1>", submitBtn.getLabel());
				}//else
				
				
				
				//***********sets the width for submit button **********
				
				if(submitBtnWidthIbId.getValue() != null) {
					//logger.info("submit button width is"+submitBtnWidthIbId.getValue().intValue());
					submitBtn.setWidth(submitBtnWidthIbId.getValue().intValue()+"px");
					//formFieldsStr = formFieldsStr.replace("<VAL2>",""+submitBtnWidthIbId.getValue().intValue());
					tempFormFldStr = tempFormFldStr.replace("<VAL2>",""+submitBtnWidthIbId.getValue().intValue());
				}
				else {
					
					//formFieldsStr = formFieldsStr.replace("<VAL2>","30");
					tempFormFldStr = tempFormFldStr.replace("<VAL2>",submitBtn.getWidth());
				}//else
				
				//************ sets the alignment for the buttons ***************
				
				if(!(buttonsAlignmntRgId.getSelectedItem().getValue().toString().equalsIgnoreCase("center"))){
					//logger.info("align is....>"+buttonsAlignmntRgId.getSelectedItem().getValue());
					//hbox.setPack(buttonsAlignmntRgId.getSelectedItem().getValue());
					//formFieldsStr = formFieldsStr.replace("<VAL>", buttonsAlignmntRgId.getSelectedItem().getValue());
					tempFormFldStr = tempFormFldStr.replace("<VAL>", buttonsAlignmntRgId.getSelectedItem().getValue().toString());
					align = buttonsAlignmntRgId.getSelectedItem().getValue();
					if(align.equalsIgnoreCase("left")){
						hbox.setPack("start");
					}else if(align.equalsIgnoreCase("right")){
						hbox.setPack("end");
					}else{
						hbox.setPack("center");
					}
				}else{
					
					//formFieldsStr = formFieldsStr.replace("<VAL>", "center");
					tempFormFldStr = tempFormFldStr.replace("<VAL>", "center");
				}//else
				if(resetBtnRqrCbId.isChecked()) {
					Button resetButton = new Button();
					tempFormFldStr = resetBtnSettings(resetButton,hbox,tempFormFldStr);
					resetButton.setParent(hbox);
					//hbox.beforeChildAdded(resetButton, submitBtn);
					
				}//if
				
				
				//formFieldsStr = formFieldsStr.replace("<RESET>", "");
				tempFormFldStr = tempFormFldStr.replace("<RESET>", "");
				
			}//else
			logger.debug("formFieldsStr is------------------->"+formFieldsStr);
			//logger.info("formFieldsStr is====>"+tempFormFldStr);
			strContentStr.put("btnSettings", tempFormFldStr);
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}
	
	
	 public void onClick$saveFldBtnId(){
		 try {
			 MessageUtil.clearMessage();
			
		 
			 Object obj = editFormViewLbId.getSelectedItem().getValue();
			 if(obj instanceof TextBoxField){
				 
				 TextBoxField tb = (TextBoxField)obj;
				 if(fldIdTbId.getValue().trim().length() > 0){
					 if(Utility.validateName(fldIdTbId.getValue())){
						 tb.setId(fldIdTbId.getValue());
					 }
					 else{
						 errLabelId.setValue("");
						 errLabelId.setStyle("color:red;font-size:14px;");
						 errLabelId.setValue("No special characters allowed for id !");
						 return;
					 }
					  
				 }else{
					 errLabelId.setValue("");
					 errLabelId.setStyle("color:red;font-size:14px;");
					 errLabelId.setValue("please provide id for the form field !");
					 return;
					 
				 }
				 if(fldLblTbId.getValue().trim().length() > 0){
					 if(Utility.validateName(fldLblTbId.getValue())){
						 tb.setLabel(fldLblTbId.getValue());
					 }
					 else{
						 errLabelId.setValue("");
						 errLabelId.setStyle("color:red;font-size:14px;");
						 errLabelId.setValue("No special characters allowed for field label!");
						 return;
					 }
					  
				 }else{
					 errLabelId.setValue("");
					 errLabelId.setStyle("color:red;font-size:14px;");
					 errLabelId.setValue("please provide Label for the form field !");
					 return;
					 
				 }
				 
				 tb.setRequired(rqrFldCbId.isChecked());
				 
				 tb.setVisible(fldVisibilityRgId.getSelectedItem().getValue().toString());
				 logger.info("visibility is=====>"+tb.getVisible());
				 //logger.debug("**********************"+mlFieldsLbId.getSelectedIndex());
				 if(!(mlFieldsLbId.getSelectedIndex()>0)) {
					 errLabelId.setValue("");
					 errLabelId.setStyle("color:red;font-size:14px;");
					 errLabelId.setValue("fields should be mapped with one of the fields of selected mailing list !");
					 return;
					 
				 }else {
					 if(!(mlFieldsLbId.getSelectedItem().getLabel().startsWith("CF:"))){
						 if(mlFieldsLbId.getSelectedItem().getLabel().equalsIgnoreCase("phone")||mlFieldsLbId.getSelectedItem().getLabel().equalsIgnoreCase("pin")){
							 tb.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+tb.isRequired()+"_Text_"+"number");
						 }else if(mlFieldsLbId.getSelectedItem().getLabel().equalsIgnoreCase("email")){
							 tb.setRequired(true);
							 rqrFldCbId.setChecked(true);
							 if(fldVisibilityRgId.getSelectedIndex()==1){
								 errLabelId.setValue("");
								 errLabelId.setStyle("color:red;font-size:14px;");
								 errLabelId.setValue("Email Field should not be a hidden field!");
								 return; 
							 }
							 tb.setVisible("visible");
							 fldVisibilityRgId.setSelectedIndex(0);
							 tb.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+tb.isRequired()+"_Text_"+"eml");
						 }
						 else{
							 tb.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+tb.isRequired()+"_Text_"+"string");
						 }
					 }//if
					 else {
						 String[] custFlds = mlFieldsLbId.getSelectedItem().getLabel().split(":");
						 String type = custFldsMap.get(custFlds[1]);
						 if(type.equalsIgnoreCase("string")){
							 tb.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+tb.isRequired()+"_Text_"+"string");
						 }else if(type.equalsIgnoreCase("date")){
							 tb.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+tb.isRequired()+"_Text_"+"date");
						 }else if(type.equalsIgnoreCase("number")){
							 tb.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+tb.isRequired()+"_Text_"+"number");
						 }else if(type.equalsIgnoreCase("boolean")){
							 tb.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+tb.isRequired()+"_Text_"+"boolean");
						 }
					 }//else
				 }//else
				 if(fldVisibilityRgId.getSelectedItem().getValue().toString().equalsIgnoreCase("hidden")){
					 if(fldDefValTbId.getValue().trim().length() > 0){
						 tb.setDefaultValue(fldDefValTbId.getValue());
					 } else {
						 errLabelId.setValue("");
						 errLabelId.setStyle("color:red;font-size:14px;");
						 errLabelId.setValue("please provide default value for the field!");
						 return;
					 } 
				 }else{
					 tb.setDefaultValue(fldDefValTbId.getValue());
				 }
				 
				 List lst=((Box)((Listcell)editFormViewLbId.getSelectedItem().getChildren().get(0)).getChildren().get(0)).getChildren();
				 ((Label)lst.get(0)).setValue(tb.getLabel());
				 ((Textbox)lst.get(1)).setValue(tb.getDefaultValue());
				 //mlFieldsLbId.setSelectedIndex(0);
			
			 }else if(obj instanceof NumberField){
				 NumberField number = (NumberField)obj;
				 if(fldIdTbId.getValue().trim().length() > 0){
					 if(Utility.validateName(fldIdTbId.getValue())){
						 number.setId(fldIdTbId.getValue());
					 }
					 else{
						 errLabelId.setValue("");
						 errLabelId.setStyle("color:red;font-size:14px;");
						 errLabelId.setValue("No special characters allowed for id !");
						 return;
					 }
					  
				 }else{
					 errLabelId.setValue("");
					 errLabelId.setStyle("color:red;font-size:14px;");
					 errLabelId.setValue("please provide id for the form field !");
					 return;
					 
				 }
				 if(fldLblTbId.getValue().trim().length() > 0){
					 if(Utility.validateName(fldLblTbId.getValue())){
						 number.setLabel(fldLblTbId.getValue());
					 }
					 else{
						 errLabelId.setValue("");
						 errLabelId.setStyle("color:red;font-size:14px;");
						 errLabelId.setValue("No special characters allowed for field label!");
						 return;
					 }
					  
				 }else{
					 errLabelId.setValue("");
					 errLabelId.setStyle("color:red;font-size:14px;");
					 errLabelId.setValue("please provide Label for the form field !");
					 return;
					 
				 }
				
				 number.setRequired(rqrFldCbId.isChecked());
				 
				 number.setVisible(fldVisibilityRgId.getSelectedItem().getValue().toString());
				 //logger.debug("**********************"+mlFieldsLbId.getSelectedIndex());
				 if(!(mlFieldsLbId.getSelectedIndex()>0)) {
					 errLabelId.setValue("");
					 errLabelId.setStyle("color:red;font-size:14px;");
					 errLabelId.setValue("Fields should be mapped with one of the fields of selected mailing list ! ");
					 return;
					 
				 } else {
					 if(!(mlFieldsLbId.getSelectedItem().getLabel().startsWith("CF:"))){
						 if(mlFieldsLbId.getSelectedItem().getLabel().equalsIgnoreCase("phone")||mlFieldsLbId.getSelectedItem().getLabel().equalsIgnoreCase("pin")){
							 number.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+number.isRequired()+"_number_"+"number");
						 }else if(mlFieldsLbId.getSelectedItem().getLabel().equalsIgnoreCase("email")){
							 
							 number.setRequired(true);
							 rqrFldCbId.setChecked(true);
							 if(fldVisibilityRgId.getSelectedIndex()==1){
								 errLabelId.setValue("");
								 errLabelId.setStyle("color:red;font-size:14px;");
								 errLabelId.setValue("Email Field should not be a hidden field!");
								 return; 
							 }

							 number.setVisible("visible");
							 fldVisibilityRgId.setSelectedIndex(0);
							 number.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+number.isRequired()+"_Text_"+"eml");
						 }
						 else{
							 number.setMapField(mlFieldsLbId.getSelectedItem().getLabel()+"_"+number.isRequired()+"_number_"+"string");
						 }
					 }//if
					 else {
						 String[] custFlds = mlFieldsLbId.getSelectedItem().getLabel().split("_");
						 String type = custFldsMap.get(custFlds[1]);
						 if(type.equalsIgnoreCase("string")){
							 number.setMapField(custFlds[1]+"_"+number.isRequired()+"_number_"+"string");
						 }else if(type.equalsIgnoreCase("date")){
							 number.setMapField(custFlds[1]+"_"+number.isRequired()+"_number_"+"date");
						 }else if(type.equalsIgnoreCase("number")){
							 number.setMapField(custFlds[1]+"_"+number.isRequired()+"_number_"+"number");
						 }else if(type.equalsIgnoreCase("boolean")){
							 number.setMapField(custFlds[1]+"_"+number.isRequired()+"_number_"+"boolean");
						 }
					 }//else
				 }//else
				 if(fldVisibilityRgId.getSelectedItem().getValue().toString().equalsIgnoreCase("hidden")){
					 if(fldDefValTbId.getValue().trim().length() > 0){
						 number.setDefaultValue(fldDefValTbId.getValue());
					 } else {
						 errLabelId.setValue("");
						 errLabelId.setStyle("color:red;font-size:14px;");
						 errLabelId.setValue("please provide default value for the field!");
						 return;
					 } 
				 }else{
					 number.setDefaultValue(fldDefValTbId.getValue());
				 }
				 
				 List lst=((Box)((Listcell)editFormViewLbId.getSelectedItem().getChildren().get(0)).getChildren().get(0)).getChildren();
				 ((Label)lst.get(0)).setValue(number.getLabel());
				 ((Textbox)lst.get(1)).setValue(number.getDefaultValue());
				 
			 }//else if
			 errLabelId.setValue("");
			 errLabelId.setStyle("color:green;font-size:14px;");
			 errLabelId.setValue("New Form field saved successfully ! ");
		 }catch (Exception e) {
			logger.error("Exception ::", e);
		}
	 
	 }
	 
	
	 public void onSelect$editFormViewLbId() {
		  try {	
			  	MessageUtil.clearMessage();
			  	errLabelId.setValue("");
			  	frmBtnsErrLabelId.setVisible(false);
		  		if(editFormViewLbId.getSelectedIndex()!=0 && editFormViewLbId.getSelectedIndex() != editFormViewLbId.getItemCount()-1) {
		 		
		  			formTitleDivId.setVisible(false);
		  			 formBtnPropsDivId.setVisible(false);
		  			 formFldPropsDivId.setVisible(true);
		  			 Object obj = editFormViewLbId.getSelectedItem().getValue();
		  			 setFldValues(obj);
					 /*if(obj instanceof TextBoxField){
						 TextBoxField tb = (TextBoxField)obj;
						 fldTypeLblId.setValue("Element Type : "+tb.getType());
						 fldLblTbId.setValue(tb.getLabel());
						 fldIdTbId.setValue(tb.getId());
						 rqrFldCbId.setChecked(tb.isRequired());
						 fldDefValTbId.setValue(tb.getDefaultValue());
						 mlFieldsLbId.setSelectedIndex(setSelectoinInMlFldsLb(tb.getMapField().split("_")[0]));
					 }else if(obj instanceof NumberField) {
						 NumberField number = (NumberField)obj;
						 
						 fldTypeLblId.setValue("Element Type : "+number.getType());
						 fldLblTbId.setValue(number.getLabel());
						 fldIdTbId.setValue(number.getId());
						 rqrFldCbId.setChecked(number.isRequired());
						 fldDefValTbId.setValue(number.getDefaultValue());
						 mlFieldsLbId.setSelectedIndex(setSelectoinInMlFldsLb(number.getMapField().split("_")[0]));
						 
					 }//else if
*/	  		  }//if
		 
			 if(editFormViewLbId.getSelectedIndex() == editFormViewLbId.getItemCount()-1) {
				 //logger.info("hai");
		 			formTitleDivId.setVisible(false);
		 			formFldPropsDivId.setVisible(false);
					 formBtnPropsDivId.setVisible(true);
					 Hbox hbox = (Hbox)((Listcell)editFormViewLbId.getSelectedItem().getChildren().get(0)).getChildren().get(0);
					 if(hbox.getChildren().size() == 2){
						 Button btn = (Button)hbox.getChildren().get(0);
						 //logger.info("button is----->"+btn+"     "+btn.getWidth());
						 if(btn.getWidth()==null){
							 btn.setWidth("60px");
						 }
						 resetBtnWidthIbId.setValue(Integer.parseInt(btn.getWidth().replace("px", "")));
						 resetBtnLblTbId.setValue(btn.getLabel());
						 //logger.info("pack of hbox is...>"+hbox.getPack());
						 if(hbox.getPack().equalsIgnoreCase("start")){
						 buttonsAlignmntRgId.setSelectedItem(lftAlignRadioId);
						 } else if(hbox.getPack().equalsIgnoreCase("end")) {
							 buttonsAlignmntRgId.setSelectedItem(rghtAlignRadioId);
						 }else{
							 buttonsAlignmntRgId.setSelectedItem(centerAlignRadioId);
						 }
						 resetBtnRqrCbId.setChecked(resetBtnRqrCbId.isChecked());
						 
						 btn = (Button)hbox.getChildren().get(1);
						 
						 submitBtnLblTbId.setValue(btn.getLabel());
						 if(btn.getWidth()==null){
							 btn.setWidth("60px");
						 }
						 
						 submitBtnWidthIbId.setValue(Integer.parseInt(btn.getWidth().replace("px", "")));
					 } else{
						 Button btn = (Button)hbox.getChildren().get(0);
						 submitBtnLblTbId.setValue(btn.getLabel());
						 if(btn.getWidth()==null){
							 btn.setWidth("60px");
						 }
						 if(hbox.getPack().equalsIgnoreCase("start")){
							 buttonsAlignmntRgId.setSelectedItem(lftAlignRadioId);
							 } else if(hbox.getPack().equalsIgnoreCase("end")) {
								 buttonsAlignmntRgId.setSelectedItem(rghtAlignRadioId);
							 }else{
								 buttonsAlignmntRgId.setSelectedItem(centerAlignRadioId);
							 }
						 submitBtnWidthIbId.setValue(Integer.parseInt(btn.getWidth().replace("px", "")));
						 resetBtnRqrCbId.setChecked(false);
					 }
			 }//if
			 if(editFormViewLbId.getSelectedIndex() == 0){
				 //logger.info("------just entered----");
				 formFldPropsDivId.setVisible(false);
				 formBtnPropsDivId.setVisible(false);
				 formTitleDivId.setVisible(true);
				 Label frmHdngLabel = (Label) ((Hbox)((Listcell)editFormViewLbId.getSelectedItem().getChildren().get(0)).getChildren().
						 					get(0)).getChildren().get(0);
				 frmHeadngTbId.setValue(frmHdngLabel.getValue());
				 
				 
			 }
		  }catch(Exception e){
			  logger.error("Exception ::", e);
		  }//catch
		 
	 }//onSelect$editFormViewLbId()
	 
	 private Button saveFrmHdngBtnId;
	 public void onClick$saveFrmHdngBtnId(){
		 String tempformTopStr = "";
		 MessageUtil.clearMessage();
		 if(frmHeadngTbId.getValue().trim().length() > 0 ){
			((Label) ((Hbox)((Listcell)editFormViewLbId.getItemAtIndex(0).getChildren().get(0)).getChildren().
						get(0)).getChildren().get(0)).setValue( frmHeadngTbId.getValue());
			tempformTopStr = formTopStr.replace("<FORMHEADING>", frmHeadngTbId.getValue());
			//logger.debug(""+formTopStr);
		 }else{
			String frmHedding =  ((Label) ((Hbox)((Listcell)editFormViewLbId.getItemAtIndex(0).getChildren().get(0)).getChildren().
						get(0)).getChildren().get(0)).getValue();
			 tempformTopStr = formTopStr.replace("<FORMHEADING>", frmHedding);
		 }
		 strContentStr.put("headdingSettings", tempformTopStr);
	 }
	 
	 public void onClick$textMenuItemId() {
		 try {
			logger.debug("-----just entered-----");
			MessageUtil.clearMessage();
			 TextBoxField tb = new TextBoxField();
			 
			 ((ListModelList) editFormViewLbId.getListModel()).add(editFormViewLbId.getItemCount()-1,tb);
			 editFormViewLbId.setSelectedIndex(editFormViewLbId.getItemCount()-2);
			 editFormViewLbId.invalidate();
			 
			 fldTypeLblId.setValue("Element Type : "+tb.getType());
			 fldLblTbId.setValue(tb.getLabel());
			 fldIdTbId.setValue(tb.getId());
			 rqrFldCbId.setChecked(tb.isRequired());
			 fldDefValTbId.setValue(tb.getDefaultValue());
			 mlFieldsLbId.setSelectedIndex(0);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}//catch
			
	 }// onClick$textMenuItemId()
	 
	 
	 public void onClick$numberMenuItemId() {
		 logger.debug("-----just entered-----");
		 MessageUtil.clearMessage();
		 NumberField number = new NumberField();
		 ((ListModelList) editFormViewLbId.getListModel()).add(editFormViewLbId.getItemCount()-1,number);
		 editFormViewLbId.setSelectedIndex(editFormViewLbId.getItemCount()-2);
		 editFormViewLbId.invalidate();
		 
		 fldTypeLblId.setValue("Element Type : "+number.getType());
		 fldLblTbId.setValue(number.getLabel());
		 fldIdTbId.setValue(number.getId());
		 rqrFldCbId.setChecked(number.isRequired());
		 fldDefValTbId.setValue(number.getDefaultValue());
		 mlFieldsLbId.setSelectedIndex(0);
	 }//onClick$numberMenuItemId()
	
	 public int setSelectoinInMlFldsLb(String mapFieldName) {
		 int index=0;
		 Vector<String> mapFldsVector = new Vector<String>();
		 int count = mlFieldsLbId.getItemCount();
		 for(int i=0; i<count; i++){
			 mapFldsVector.add(mlFieldsLbId.getItemAtIndex(i).getLabel());
			 if(mapFldsVector.contains(mapFieldName)){
				 mlFieldsLbId.setSelectedIndex(i);
				index=i;
				break;
			 }//if
			 
		 }//for
		 return index;
	 }//setSelectoinInMlFldsLb
	 
	 public void onClick$deleteFldBtnId() {
		 try {
			 	MessageUtil.clearMessage();
			 	if(editFormViewLbId.getSelectedIndex() == -1){
			 		MessageUtil.setMessage("Please select the field to be deleted.", "color:red;", "top");
			 		return;
			 	}
			 	
			 	try {
					int confirm = Messagebox.show("Are you sure you want to delete the field?",
								"Delete Field", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION); 
					if(confirm != Messagebox.OK) {
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
			 	if(!(editFormViewLbId.getSelectedIndex() == 0 || editFormViewLbId.getSelectedIndex() == editFormViewLbId.getItemCount()-1)){
			 	 Object obj = editFormViewLbId.getSelectedItem().getValue();
			 	 if(obj instanceof TextBoxField){
			 		 TextBoxField tb = (TextBoxField)obj;
			 		
			 		 if(tb.isRequired()){
			 			 MessageUtil.setMessage("Mandatory field cannot be deleted.", "color:red;", "top");
			 			 return;
			 		 }
			 		editFormViewLbId.removeItemAt(editFormViewLbId.getSelectedIndex());
			 		
					 fldTypeLblId.setValue("");
					 fldLblTbId.setValue("");
					 fldIdTbId.setValue("");
					 rqrFldCbId.setChecked(false);
					 fldDefValTbId.setValue("");
					 ((ListModelList) editFormViewLbId.getListModel()).remove(tb);
					 editFormViewLbId.invalidate();
					 tbList.remove(tb);
			 	 }else if(obj instanceof NumberField){
			 		 NumberField number = (NumberField)obj;
			 		if(number.isRequired()){
			 			 MessageUtil.setMessage("Mandatory field cannot be deleted.", "color:red;", "top");
			 			 return;
			 		 }
			 		 
			 		 editFormViewLbId.removeItemAt(editFormViewLbId.getSelectedIndex());
					 fldTypeLblId.setValue("");
					 fldLblTbId.setValue("");
					 fldIdTbId.setValue("");
					 rqrFldCbId.setChecked(false);
					 fldDefValTbId.setValue("");
					 ((ListModelList) editFormViewLbId.getListModel()).remove(number);
					 editFormViewLbId.invalidate();
					 tbList.remove(number);
			 	 }//else if
			 	}else{
			 		MessageUtil.setMessage("Only input fields of the form are allowed to be deleted.", "red", "top");
			 		return;
			 	}//else
			 }catch (Exception e) {
				logger.debug("Exception : Error occured while deleting the field **",e);
			}//catch
	 }
	
	 private Button saveFormBtnId;
	 public void onClick$saveFormBtnId() {
		 try {	
				//logger.info("*******************FomrHTml before"+ formHTML);
				MessageUtil.clearMessage();
			 	this.onClick$saveBtnSettingsBtnId();
			 	this.onClick$saveFrmHdngBtnId();
			 	
			 	Vector<String> mappedFldsVector = new Vector<String>();
			 	Vector<String> fldIdsVector = new Vector<String>();
			 	
			 	MessageUtil.clearMessage();
			 	
			 	logger.debug("---just entered---");
			 	TextBoxField tb = null;
			 	NumberField number = null;
			 	String[] name = null;
			 	String finaltext="";
			 	String textHtml="";
			 	Html html = null; //html.setContent("<div id='FormDivId'> " + formHTML  + "</div>");
			 	List<Component> fieldsList = editFormViewLbId.getChildren();
			 	
			 	for(Component itemComp : fieldsList) {
			 		Listitem item = (Listitem)itemComp;
			 		Object obj = item.getValue();
			 		if(obj instanceof TextBoxField){
			 			tb = (TextBoxField)obj;
			 			name = tb.getMapField().split("_") ;
			 			if(!fldIdsVector.contains(tb.getId())){
			 				fldIdsVector.add(tb.getId());
			 			}else {
			 				MessageUtil.setMessage("Some of the IDs are duplicated.", "color:red", "top");
			 				item.setSelected(true);
			 				return;
			 			}
			 			if(!mappedFldsVector.contains(name[0])) {
			 				mappedFldsVector.add(name[0]);
			 			}else{
			 				MessageUtil.setMessage(name[0]+" field is mapped to more than one element.", "color:red", "top");
			 				item.setSelected(true);
			 				setFldValues(tb);
			 				return;
			 			}
			 			if(tb.getMapField()==null||tb.getMapField().equals("")){
			 				item.setSelected(true);
			 				MessageUtil.setMessage(tb.getLabel()+" field is not mapped with any field of selected mailing list.", "color:red", "top");
			 				item.setSelected(true);
			 				setFldValues(tb);
			 				return;
			 			}
			 		
			 			if(tb.getVisible().equalsIgnoreCase("hidden")){
			 				 if(token[0].equalsIgnoreCase("select1")){
			 					 	textHtml = (addField.get("text")).replace("<VAL1>",tb.getLabel()).replace("<VAL2>", tb.getId()).
			 				 					replace("<VAL3>", tb.getDefaultValue()).replace("<VAL4>", tb.getVisible()).
			 				 					replace("<VAL5>", tb.getMapField()).replace("<VAL6>", tb.getMapField()).
			 				 					replace("<VAL7>", "style='visibility:hidden;'");
			 				 	}else {
			 				 		 textHtml = (addField.get("text_form")).replace("<VAL1>",tb.getLabel()).replace("<VAL2>", tb.getId()).
		 				 						replace("<VAL3>", tb.getDefaultValue()).replace("<VAL4>", tb.getVisible()).
		 				 						replace("<VAL5>", tb.getMapField()).replace("<VAL6>", tb.getMapField()).
		 				 						replace("<VAL7>", "style='visibility:hidden;'");
			 				 	}
			 				 }else {
			 					if(token[0].equalsIgnoreCase("select1")){
			 						textHtml = (addField.get("text")).replace("<VAL1>", tb.getLabel()).replace("<VAL2>", tb.getId()).
			 									replace("<VAL3>", tb.getDefaultValue()).replace("<VAL4>", tb.getType()).
			 									replace("<VAL5>", tb.getMapField()).replace("<VAL6>", tb.getMapField()).replace("<VAL7>", "");
			 					}else {
			 						textHtml = (addField.get("text_form")).replace("<VAL1>", tb.getLabel()).replace("<VAL2>", tb.getId()).
 												replace("<VAL3>", tb.getDefaultValue()).replace("<VAL4>", tb.getType()).
 												replace("<VAL5>", tb.getMapField()).replace("<VAL6>", tb.getMapField()).replace("<VAL7>", "");
			 					}
			 				 }//else if
			 			finaltext = finaltext+textHtml;
			 		}else if(obj instanceof NumberField) {
			 			number = (NumberField)obj;
			 			name = number.getMapField().split("_") ;
			 			if(!fldIdsVector.contains(number.getId())){
			 				fldIdsVector.add(number.getId());
			 			}else {
			 				MessageUtil.setMessage("Some of the IDs are duplicated.", "color:red", "top");
			 				item.setSelected(true);
			 				setFldValues(item.getValue());
			 				return;
			 			}
			 			if(!mappedFldsVector.contains(name[0])) {
			 				mappedFldsVector.add(name[0]);
			 			}else{
			 				MessageUtil.setMessage(name[0]+" field is mapped to more than one element.", "color:red", "top");
			 				item.setSelected(true);
			 				setFldValues(tb);
			 				return;
			 			}
			 			
			 			
			 			if(number.getMapField()==null||number.getMapField().trim().equals("")){
			 				item.setSelected(true);
			 				MessageUtil.setMessage(number.getLabel()+"field is not mapped with any field of selected mailig list.", "color:red", "top");
			 				item.setSelected(true);
			 				setFldValues(tb);
			 				return;
			 			}
			 			/*try{
			 				long val = Long.parseLong(number.getDefaultValue());
			 			}catch(NumberFormatException e){
			 				item.setSelected(true);
			 				MessageUtil.setMessage("Provide number type value only.", "color:red", "top");
			 				return;
			 				
			 			}*/
			 			if(number.getVisible().equalsIgnoreCase("hidden")){
			 				 if(token[0].equalsIgnoreCase("select1")){
			 					 	textHtml = (addField.get("text")).replace("<VAL1>",number.getLabel()).replace("<VAL2>", number.getId()).
			 				 					replace("<VAL3>", number.getDefaultValue()).replace("<VAL4>", number.getVisible()).
			 				 					replace("<VAL5>", number.getMapField()).replace("<VAL6>", number.getMapField()).
			 				 					replace("<VAL7>", "style='visibility:hidden;'");
			 				 	}else {
			 				 		 textHtml = (addField.get("text_form")).replace("<VAL1>",number.getLabel()).replace("<VAL2>", number.getId()).
		 				 						replace("<VAL3>", number.getDefaultValue()).replace("<VAL4>", number.getVisible()).
		 				 						replace("<VAL5>", number.getMapField()).replace("<VAL6>", number.getMapField()).
		 				 						replace("<VAL7>", "style='visibility:hidden;'");
			 				 	}
			 				 }else {
			 					if(token[0].equalsIgnoreCase("select1")){
			 						textHtml = (addField.get("text")).replace("<VAL1>", number.getLabel()).replace("<VAL2>", number.getId()).
			 									replace("<VAL3>", number.getDefaultValue()).replace("<VAL4>", number.getType()).
			 									replace("<VAL5>", number.getMapField()).replace("<VAL6>", number.getMapField()).
			 									replace("<VAL7>", "");
			 					}else {
			 						textHtml = (addField.get("text_form")).replace("<VAL1>", number.getLabel()).replace("<VAL2>", number.getId()).
												replace("<VAL3>", number.getDefaultValue()).replace("<VAL4>", number.getType()).
												replace("<VAL5>", number.getMapField()).replace("<VAL6>", number.getMapField()).
												replace("<VAL7>", "");
			 					}
			 				 }//else if
			 			finaltext = finaltext+textHtml;
			 		}//else if
			 	}//for
			 	if(!(mappedFldsVector.contains("Email"))){
			 		MessageUtil.setMessage("One of the form elements should be mapped with 'Email' field of selected mailing list.", "color:red;", "top");
			 		return;
			 	}
			 	//logger.info("strContentStr is======>"+strContentStr.get("headdingSettings"));
			 	formHTML = formHTML.replace(formHTML, strContentStr.get("headdingSettings")+finaltext+strContentStr.get("btnSettings"));
			 	logger.debug("html content is.......>   "+formHTML);
			 	html = (Html)inputFieldsWinId.getFellowIfAny("previewContentHTMLId1");//setVisible(true);
			 	html.setContent("<div id='FormDivId'>"+formHTML +" </div>");
			 	
			 	//logger.debug("Selected Value is : "+mailingListLbId.getSelectedIndex());
			 	String formNameStr = Utility.condense(formNameTbId.getValue());
			 	
			 	if(!Utility.validateName(formNameStr)) {
					MessageUtil.setMessage("Please enter valid form name.", "red", "top");
					finalHtmlHiddenId.setValue("");
					return;
				}//if
			 	
			 	if(editFormId == null) {
			 		Set<Long> listIdsSet = (Set<Long>)session.getAttribute(Constants.LISTIDS_SET);
					if(customTemplatesDao.checkTemplateStatusByName(listIdsSet, formNameStr)) {
						MessageUtil.setMessage("Form with the given name already exists.", "red", "top");
						finalHtmlHiddenId.setValue("");
						return;
					}//if
				}//if
			 	
			 	if(mailingListLbId.getSelectedItem().getValue() == null) {
					MessageUtil.setMessage("No mailing list is selected.", "red", "top");
					finalHtmlHiddenId.setValue("");
					return;
				}//if
			 	
			 	String ifrmSrcStr = PropertyUtil.getPropertyValue("subscriptionSrc");
			 	if(editFormId != null) {
			 		logger.debug("the existing form id is ...>"+editFormId);
			 		customTemplates.setHtmlText(formHTML);
					//customTemplatesDao.saveOrUpdate(customTemplates); 
					customTemplatesDaoForDML.saveOrUpdate(customTemplates); 
					if(logger.isDebugEnabled())
						logger.debug("Custom Templates updated succesfully . Form Id : "+editFormId);
				}//if
				else {
					String finalHtmlStr = new String(formHTML);
					
					String subscrptnFrmActnAtt = PropertyUtil.getPropertyValue("subscrptnFrmActnAtt");
					finalHtmlStr = finalHtmlStr.replace(subscrptnFrmActnAtt, ifrmSrcStr);
					logger.info("final html content is"+finalHtmlStr);
					customTemplates = new CustomTemplates(currentUser, formNameStr, finalHtmlStr, "subscriptionFormHTML",token[0]+":"+token[1]);
					//customTemplatesDao.saveOrUpdate(customTemplates);
					customTemplatesDaoForDML.saveOrUpdate(customTemplates);
					logger.debug("Last inserted id is :" + customTemplates.getTemplateId());
				}//else
			 	
			 	String ifrmLinkStr = "<iframe height='200px' width='350px' src='" + ifrmSrcStr 
				+ "?uId=" + currentUser.getUserId() + "&mId=" + ((MailingList)mailingListLbId.getSelectedItem().getValue()).getListId()
				+ "&dId=" + customTemplates.getTemplateId() + "'></iframe>";
				
				finalLinkTbId.setValue(ifrmLinkStr);
				finalLinkTbId.setValue(ifrmLinkStr);
				
				
				
				customTemplates.setIframeLink(ifrmLinkStr);
				//customTemplatesDao.saveOrUpdate(customTemplates);
				customTemplatesDaoForDML.saveOrUpdate(customTemplates);
			
							
				//saveFldBtnId.setDisabled(true);
				finalLinkDivId.setVisible(true);
				finalHtmlHiddenId.setValue("");
				
				MessageUtil.setMessage("Form saved successfully.", "color:green", "top");
			 	formNameTbId.setReadonly(true);
			 	//saveFormBtnId.setDisabled(true);
				 
		 }catch (Exception e) {
			logger.error("Exception ::", e);
		}
	 }
	 /*public void setFieldValues(TextBoxField tb,Listitem item) {
		 try{
			 
			 fldTypeLblId.setValue("Field Type : "+tb.getType());
			 fldLblTbId.setValue(tb.getLabel());
			 fldIdTbId.setValue(tb.getId());
			 rqrFldCbId.setChecked(tb.isRequired());
			 fldDefValTbId.setValue(tb.getDefaultValue());
			//mlFieldsLbId.setSelectedItem(tb.)
			 
			 
		 }catch(Exception e){
			 logger.debug("Exception **",e);
		 }
	 }//setFieldValues
	 
	 */
	 public void onClick$previewFormTBtnId() {
		try{ 
		 MessageUtil.clearMessage();
		 
		 Html html = (Html)inputFieldsWinId.getFellowIfAny("previewContentHTMLId1");
		 if(html.getContent()==null||html.getContent().equals("")){
			 MessageUtil.setMessage("Please save the form first.", "color:red;", "top");
			 return;
		 }else{
			 inputFieldsWinId.setVisible(true);
			 logger.debug("html contnt"+formHTML);
			 html.setContent("<div id='FormDivId'>"+formHTML +" </div>");
		 }//else
		}catch(Exception e) {
			logger.error("Error occured ",e);
		}
	 }//onClick$previewFormTBtnId
	 
	 
	 public void setMlFieldsToList(){
		 try{
			 logger.debug("---just entered----");
			MLCustomFieldsDao mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		 	String defaultFieldStr = PropertyUtil.getPropertyValue("defaultFieldList");
			String[] defaultFieldArray = defaultFieldStr.split(",");
			Listitem li = null;
			for(int j=0;j<defaultFieldArray.length;j++) {	// Add Default Fields .
				
				 li = new Listitem();
				 li.setLabel(defaultFieldArray[j]);
				 li.setValue(defaultFieldArray[j]);
			
				 li.setParent(mlFieldsLbId);
			}//for
			
			
			
			MailingList mailingList = (MailingList)mailingListLbId.getListModel().getElementAt(mailingListLbId.getSelectedIndex());
			logger.debug("ML id value is : " +( mailingList)); 
			if(mailingList == null){
				return;
			}
			
			if(mailingList.isCheckParentalConsent()) {
				
				li = new Listitem("Parent Email", "Parent Email");
				li.setParent(mlFieldsLbId);
				
			}
			if(mailingList.getListType()!= null && mailingList.getListType().equals(Constants.MAILINGLIST_TYPE_POS)) {
				
				li = new Listitem("Loyalty Enroll", "Loyalty Enroll");
				li.setParent(mlFieldsLbId);
				
				li = new Listitem("Store Location","Store Location");
				li.setParent(mlFieldsLbId);
				
				
			}
			
			if(mailingList.isCustField()) { 				// Add Custom Fields. 
				Listitem cfListItem = null;
				String custName = "";
				String custType = "";
				List<MLCustomFields> mlFieldsList = mlCFDao.findAllByList(mailingList);
				for (MLCustomFields customFields : mlFieldsList) {
					custName = customFields.getCustFieldName();
					custType = customFields.getDataType();
					custFldsMap=new HashMap<String, String>();
					custFldsMap.put(custName, custType);
				 	cfListItem = new Listitem();  
				 	cfListItem.setLabel("CF:"+customFields.getCustFieldName());
				 	cfListItem.setValue("CF:"+ 
				 			customFields.getCustFieldName() + "_" + customFields.getDataType() + "_" +customFields.getFieldIndex());
				 	cfListItem.setParent(mlFieldsLbId);
				 }//for
			 }//if
			mlFieldsLbId.setSelectedIndex(0);
		
		 }catch (Exception e) {
			logger.error("Exception**",e);
		}
		
		
	 }//setMlFieldsToList
	 
	 public void setFldValues(Object obj) {
		 logger.debug("----just entered---");
		formFldPropsDivId.setVisible(true);
		formBtnPropsDivId.setVisible(false);
		formTitleDivId.setVisible(false);
		 if(obj instanceof TextBoxField) {
			 TextBoxField tb = (TextBoxField)obj;
			 fldTypeLblId.setValue("Element Type : "+tb.getType());
			 fldLblTbId.setValue(tb.getLabel());
			 fldIdTbId.setValue(tb.getId());
			 rqrFldCbId.setChecked(tb.isRequired());
			 fldDefValTbId.setValue(tb.getDefaultValue());
			 mlFieldsLbId.setSelectedIndex(setSelectoinInMlFldsLb(tb.getMapField().split("_")[0]));
			 //logger.info("visibl?????????????????/"+tb.getVisible());
			 if(tb.getVisible().equalsIgnoreCase("visible")) {
				 fldVisibilityRgId.setSelectedIndex(0);
			 }else if(tb.getVisible().equalsIgnoreCase("hidden")){
				 fldVisibilityRgId.setSelectedIndex(1); 
			 }else{
				 fldVisibilityRgId.setSelectedIndex(0);
			 }
			 
		 }else if(obj instanceof NumberField){
			 NumberField number = (NumberField)obj;
			 fldTypeLblId.setValue("Element Type : "+number.getType());
			 fldLblTbId.setValue(number.getLabel());
			 fldIdTbId.setValue(number.getId());
			 rqrFldCbId.setChecked(number.isRequired());
			 fldDefValTbId.setValue(number.getDefaultValue());
			 mlFieldsLbId.setSelectedIndex(setSelectoinInMlFldsLb(number.getMapField().split("_")[0]));
			 if(number.getVisible().equalsIgnoreCase("visible")) {
				 fldVisibilityRgId.setSelectedIndex(0);
			 }else if(number.getVisible().equalsIgnoreCase("hidden")){
				 fldVisibilityRgId.setSelectedIndex(1); 
			 }else{
				 fldVisibilityRgId.setSelectedIndex(0);
			 }
			 
			 
		 }
		 
		 
	 }
}

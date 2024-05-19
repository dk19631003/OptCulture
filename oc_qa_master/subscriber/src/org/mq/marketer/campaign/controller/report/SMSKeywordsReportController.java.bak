package org.mq.marketer.campaign.controller.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.dao.SMSCampaignSentDao;
import org.mq.marketer.campaign.dao.SMSSettingsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;
import org.mq.marketer.campaign.general.Utility;


public class SMSKeywordsReportController extends GenericForwardComposer{

	private Listhead genericLbHeaderId;
	private Paging keywordListPaging, responseListPaging;
	private SMSCampaignReport smsCampaignReport;
	
	
	private SMSCampaignSentDao smsCampaignSentDao;
	private OrgSMSkeywordsDao orgSMSkeywordsDao;
	
	private Listbox keywordsLbId, responsesLbId, keywordPageSizeLbId, responsePageSizeLbId, complaincyKeywordsLbId;
	private int currentPage = 1;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Tabbox editorTabboxId;
	private Tab keywordtabId, responseTabId;
	private Long orgId;
	
	Combobox exportKeywordCbId, exportResponseCbId, exportComplaincyKeywordCbId;
	private Session session;
	private TimeZone clientTimeZone;
	
	private SMSSettingsDao smsSettingsDao ;
	private Users currentUser;
	private Textbox mobileFilterTbId;
	
	public SMSKeywordsReportController() {
		
		//smsCampaignReport = (SMSCampaignReport)Sessions.getCurrent().getAttribute("smsCampaignReport");
		smsCampaignSentDao = (SMSCampaignSentDao)SpringUtil.getBean("smsCampaignSentDao");
		orgSMSkeywordsDao = (OrgSMSkeywordsDao)SpringUtil.getBean("orgSMSkeywordsDao");
		orgId = GetUser.getUserObj().getUserOrganization().getUserOrgId();
		session = Sessions.getCurrent();
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		
		
		smsSettingsDao = (SMSSettingsDao)SpringUtil.getBean("smsSettingsDao");

		currentUser = GetUser.getUserObj();
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("SMS Keyword Reports","",style,true);
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		keywordListPaging.setDetailed(true);
		responseListPaging.setDetailed(true);
		exportKeywordCbId.setSelectedIndex(0);
		exportResponseCbId.setSelectedIndex(0);
		exportComplaincyKeywordCbId.setSelectedIndex(0);
		
		editorTabboxId.setAttribute("type", keywordtabId);

		responseListPaging.setAttribute("type", "response");
		
		keywordListPaging.setAttribute("type","keywords");	
		
		keywordListPaging.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(type,ofs, (byte) pagingEvent.getPageable().getPageSize());
			}
		});
		
		
		responseListPaging.addEventListener("onPaging", new EventListener() {
			public void onEvent(Event e) {
				Paging openPaging = (Paging) e.getTarget();
				int desiredPage = openPaging.getActivePage();
				PagingEvent pagingEvent = (PagingEvent) e;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				String type = (String)openPaging.getAttribute("type");
				redraw(type,ofs, (byte) pagingEvent.getPageable().getPageSize());
			}
		});
		//Components.removeAllChildren(genericLbHeaderId);
		
		int totalCount = orgSMSkeywordsDao.findAllCountByOrg(orgId);
		
		keywordListPaging.setTotalSize(totalCount);
		
		redraw("keywords",0, (byte)10);
		
		totalCount = orgSMSkeywordsDao.getKeywordResponseReportCount(orgId, mobileFilterTbId.getText().trim());
		responseListPaging.setTotalSize(totalCount);
		redraw("response", 0, (byte)10);
		
		populateComplaincyKeyWordResponses();
		
	}
	
	
	private void populateComplaincyKeyWordResponses() {
		
		List<SMSSettings> smsSettings = smsSettingsDao.findByUser(currentUser.getUserId().longValue());
		if(smsSettings == null) return;
		
		SMSSettings optinSettings = null;
		SMSSettings optOutSettings = null;
		SMSSettings helpSettings = null;
		
		for (SMSSettings eachSMSSetting : smsSettings) {
			
			if(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN.equalsIgnoreCase(eachSMSSetting.getType())) optinSettings = eachSMSSetting;
			else if(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT.equalsIgnoreCase(eachSMSSetting.getType())) optOutSettings = eachSMSSetting;
			else if(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP.equalsIgnoreCase(eachSMSSetting.getType())) helpSettings = eachSMSSetting;
			
		}
		
		
		List<Object[]> list = null;
		
		int count = complaincyKeywordsLbId.getItemCount();
		
		for(;count>0;count--){
			complaincyKeywordsLbId.removeItemAt(count-1);
		}
		Listitem li = null;
		if(optinSettings != null) {
			
			list = smsSettingsDao.findAllkeyWordResponses(currentUser.getUserId(), 
					currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
			if(list != null) {
				for (Object[] obj : list) {
					li = new Listitem();
					li.appendChild(new Listcell((String)obj[0] == null ? optinSettings.getKeyword() : (String)obj[0] ));
					li.appendChild(new Listcell("Opt-in"));
					li.appendChild(new Listcell("" + obj[1]));
					//logger.info("values are---->"+obj[1]+"    "+obj[2]+"   "+obj[3]+"   "+obj[4]);
					complaincyKeywordsLbId.appendChild(li);
					//logger.info("the children of list item are=====>"+li.getChildren().size());
				}
			}
		}
		if(optOutSettings != null) {
			
			
			list = smsSettingsDao.findAllkeyWordResponses(currentUser.getUserId(), 
					currentUser.getUserOrganization().getUserOrgId(), OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
			
			if(list != null) {
			
				for (Object[] obj : list) {
					li = new Listitem();
					li.appendChild(new Listcell((String)obj[0] == null ? optOutSettings.getKeyword() : (String)obj[0] ));
					li.appendChild(new Listcell("Opt-out"));
					li.appendChild(new Listcell("" + obj[1]));
					//logger.info("values are---->"+obj[1]+"    "+obj[2]+"   "+obj[3]+"   "+obj[4]);
					complaincyKeywordsLbId.appendChild(li);
					//logger.info("the children of list item are=====>"+li.getChildren().size());
				}
				
			}
		}
		
		if(helpSettings != null) {
		
			list = smsSettingsDao.findAllkeyWordResponses(currentUser.getUserId(), 
					currentUser.getUserOrganization().getUserOrgId(),  OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP);
			if(list != null) {
			
			
				for (Object[] obj : list) {
					li = new Listitem();
					li.appendChild(new Listcell((String)obj[0] == null ? helpSettings.getKeyword() : (String)obj[0] ));
					li.appendChild(new Listcell("Help"));
					li.appendChild(new Listcell("" + obj[1]));
					//logger.info("values are---->"+obj[1]+"    "+obj[2]+"   "+obj[3]+"   "+obj[4]);
					complaincyKeywordsLbId.appendChild(li);
					//logger.info("the children of list item are=====>"+li.getChildren().size());
				}
			}
		}
	}//populateComplaincyKeyWordResponses
	
	private void redraw(String type,int start_index, int _size) {
		try {
			logger.debug("type is--------->"+type);
			
			
			
			Listitem li = null;
			if(type.equalsIgnoreCase("keywords")) {
				
				
				int count = keywordsLbId.getItemCount();
				
				for(;count>0;count--){
					keywordsLbId.removeItemAt(count-1);
				}
				
				List<Object[]> keyWrdsUsageList = orgSMSkeywordsDao.getUsedKeywordReport(orgId, start_index, _size,orderby_colName_keywords,desc_Asc);
				
			//	keywordListPaging.setTotalSize(keyWrdsUsageList.size());
				
				for (Object[] obj : keyWrdsUsageList) {
					li = new Listitem();
					
					//li.appendChild(new Listcell((String)obj[0]));
					
					OrgSMSkeywords keywordObj = (OrgSMSkeywords) obj[0];
					li.setValue(keywordObj);
					
					Label label = new Label(keywordObj.getKeyword());
					label.setMaxlength(25);
					label.setStyle("cursor:pointer;color:blue;");
					label.addEventListener("onClick", this);
					Listcell lc = new Listcell();
					label.setParent(lc);
					li.appendChild(lc);
					String recvNUm = keywordObj.getShortCode();
					
					if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
						if(!(recvNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && 
								(recvNUm.length() >= currentUser.getUserOrganization().getMinNumberOfDigits()
								&& recvNUm.length() <= currentUser.getUserOrganization().getMaxNumberOfDigits())) recvNUm = "+" + GetUser.getUserObj().getCountryCarrier() + recvNUm;
						else if(recvNUm.length() < 10) recvNUm = recvNUm;
						else recvNUm = "+"  + recvNUm;
					}					
					li.appendChild(new Listcell(recvNUm));
					li.appendChild(new Listcell("" + obj[1]));
					//logger.info("values are---->"+obj[1]+"    "+obj[2]+"   "+obj[3]+"   "+obj[4]);
					keywordsLbId.appendChild(li);
					//logger.info("the children of list item are=====>"+li.getChildren().size());
				}
				//logger.debug("list box is set"+keywordsLbId);
				//List<Object[]> list = getRecipientsBySize(start_index, _size);
				//logger.debug("campReport id is--->"+smsCampaignReport.getSmsCrId());
				//logger.debug(" dao : "+ smsCampaignSentDao + " ---" + smsCampaignReport);
				//List<Object[]> list = smsCampaignSentDao.getAllMobileNumsByCrId(smsCampaignReport.getSmsCrId(),start_index,_size); //(start_index, _size);
				
				/*//List<Object[]> list = org
				
				logger.debug("list is---->"+list+" "+list.size());
				Listitem li;
				for (Object[] obj : list) {
					li = new Listitem();
					li.setValue(obj[0]);
					li.appendChild(new Listcell((String)obj[1]));
					li.appendChild(new Listcell("" + obj[2]));
					li.appendChild(new Listcell("" + obj[3]));
					li.appendChild(new Listcell((String)obj[4]));
					//logger.info("values are---->"+obj[1]+"    "+obj[2]+"   "+obj[3]+"   "+obj[4]);
					keywordsLbId.appendChild(li);
					//logger.info("the children of list item are=====>"+li.getChildren().size());
				}*/
			}
			else if(type.equalsIgnoreCase("response")) {
				
				setUpDataFiletredByMobile(mobileFilterTbId.getText().trim(), start_index, _size);
				
				/*
				
				int count = responsesLbId.getItemCount();
				
				for(;count>0;count--){
					responsesLbId.removeItemAt(count-1);
				}
				
				
				List<Object[]> keyWrdsUsageList = orgSMSkeywordsDao.getKeywordResponseReport(orgId, start_index, _size);
				
				
				
				int totalCount = orgSMSkeywordsDao.findAllResponseCountByOrg(orgId);
				responseListPaging.setTotalSize(totalCount);
			//	responseListPaging.setTotalSize(keyWrdsUsageList.size());
				
				logger.debug("number of records  "+keyWrdsUsageList.size());
				
				for (Object[] obj : keyWrdsUsageList) {
					
					String mobNUm = (String)obj[0];
					
					if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
						if(!(mobNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && mobNUm.length() == 10) mobNUm = "+" + GetUser.getUserObj().getCountryCarrier() + mobNUm;
						else if(mobNUm.length() < 10) mobNUm = mobNUm;
						else mobNUm = "+"  + mobNUm;
					}
					
					li = new Listitem();
					li.appendChild(new Listcell(mobNUm));
					li.appendChild(new Listcell("" + obj[1]));
					
					String recvNUm = "" + obj[2];
					
					if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
						if(!(recvNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && recvNUm.length() == 10) recvNUm = "+" + GetUser.getUserObj().getCountryCarrier() + recvNUm;
						else if(recvNUm.length() < 10) recvNUm = recvNUm;
						else recvNUm = "+"  + recvNUm;
					}
					
					li.appendChild(new Listcell(recvNUm));
					li.appendChild(new Listcell("" + obj[3]));
					
					//if(obj.length > 2) {
						
						
						if(obj[4] != null && obj[4] instanceof Calendar ) {
							
							Calendar responseTime = (Calendar)obj[4];
							li.appendChild(new Listcell(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
							
						}//if
						else{
							
							li.appendChild(new Listcell("--"));
							
						}//else
						
						
					//}//if
					Listcell lc = new Listcell( );
					String autoResponse = obj[5] != null ? obj[5].toString() : Constants.STRING_NILL;
					Label lbl = new Label(autoResponse);
					lbl.setMaxlength(20);
					lbl.setTooltiptext(autoResponse);
					lbl.setParent(lc);	
					li.appendChild(lc);
					
					
					li.appendChild(new Listcell(obj[6] != null ? obj[6].toString() : "--"));
					if(obj[7] != null && obj[7] instanceof Calendar ) {
						
						Calendar responseTime = (Calendar)obj[7];
						li.appendChild(new Listcell(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
						
					}//if
					else{
						
						li.appendChild(new Listcell("--"));
						
					}//else
					responsesLbId.appendChild(li);
					
				}
				
				
				
			*/}
		}catch (Exception e) {
			logger.error("Exception :: errorr while getting from the changeRows method",e);
		}
	}
	
	
	
 public void onSelect$responsePageSizeLbId() {
		 
	 try {
			logger.debug("Just enter here...");
			
			if(responsesLbId.getItemCount() == 0 ) {
				
				logger.debug("No reports found for this user...");
				return;
			}
			changeRows(responsePageSizeLbId.getSelectedItem().getLabel(),responseListPaging,"response" );
		} catch (Exception e) {
			logger.error("Exception :: errorr while getting from the changeRows method",e);
		}
	 
 }
	
 
 public void onSelect$keywordPageSizeLbId() {
	 
	 try {
			logger.debug("Just enter here...");
			
			if(keywordsLbId.getItemCount() == 0 ) {
				
				logger.debug("No reports found for this user...");
				return;
			}
			changeRows(responsePageSizeLbId.getSelectedItem().getLabel(),keywordListPaging,"keywords" );
		} catch (Exception e) {
			logger.error("Exception :: errorr while getting from the changeRows method",e);
		}
	 
 }
 
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
	 if(event.getTarget() instanceof Label) {
			
			Label lb = (Label)event.getTarget();
			OrgSMSkeywords reportObj = (OrgSMSkeywords)((Listitem)lb.getParent().getParent()).getValue();
			
			PageUtil.clearHeader();
			MessageUtil.clearMessage();
			
			session.setAttribute("keywordReport",reportObj);
			session.setAttribute("fromPage","report/smsKeywordReport");
			Redirect.goTo(PageListEnum.REPORT_SMS_KEYWORD_RESPONSE_REPORTS);
		
		}
	}
	
 public void changeRows(String selStr, Paging campListPaging, String type) throws Exception {
	try {
		
		if(campListPaging!=null){
			int pNo = Integer.parseInt(selStr);
			campListPaging.setPageSize(pNo);
			//campListPaging1.setPageSize(pNo);
			redraw(type, 0, (byte)pNo);
		}
		
	} catch (WrongValueException e) {
		logger.error("Exception while getting the contacts...",e);
		
	} catch (NumberFormatException e) {
		logger.error("Exception while gettinf the contacts...",e);
	}
}
	
 
 
 
 //Sorting hard code
 
 public String orderby_colName_keywords="createdDate",desc_Asc="desc"; 
 
 public void desc2ascasc2desc()
 {
 	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
 }
 public void desecnding_Order_DateTpe(){
	 firstclickonDeliveredTime=true;
 }
	 
	//Promotional Keywords
	public void onClick$sortbyKeyword() {
		orderby_colName_keywords = "keyword";
		desc2ascasc2desc();	
		redraw("keywords", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );		
		desecnding_Order_DateTpe();
	 }
	 
	 public void onClick$sortbyReceivingNumber() {
		 orderby_colName_keywords = "shortCode";
			desc2ascasc2desc();	
			redraw("keywords", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );	
			desecnding_Order_DateTpe();
		 }
	 
	 
	 
	 
	 public String orderby_colName_response="i.timeStamp";
	 //In-bounce Message
	 public void onClick$sortbyMobileNumber() {
			orderby_colName_response = "i.moFrom";
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );		
			desecnding_Order_DateTpe();
		 }
	 
	 public void onClick$sortbyReponseKeyword() {
			orderby_colName_response = "k.keyword";
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );		
			desecnding_Order_DateTpe();
		 }
	 public void onClick$sortbyReponseReceivingNumber() {
			orderby_colName_response = "i.moTo";
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );	
			desecnding_Order_DateTpe();
		 }
	 public void onClick$sortbyReceivedMessage() {
			orderby_colName_response = "i.text";
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );	
			desecnding_Order_DateTpe();
		 }
	 public void onClick$sortbyReceivedTime() {
			orderby_colName_response = "i.timeStamp";
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );	
			desecnding_Order_DateTpe();
		 }
	 public void onClick$sortbyAutoResponse() {
			orderby_colName_response = "i.autoResponse";
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );	
			desecnding_Order_DateTpe();
		 }
	 public void onClick$sortbyDeliveryStatus() {
			orderby_colName_response = "i.deliveryStatus";
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );	
			desecnding_Order_DateTpe();
		 }
	 public boolean firstclickonDeliveredTime=true;
	 public void onClick$sortbyDeliveredTime() {
			orderby_colName_response = "i.deliveredTime";
			if(firstclickonDeliveredTime)
			{
				desc_Asc="asc";
				firstclickonDeliveredTime=false;
			}
			desc2ascasc2desc();	
			redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );		
		 }
	
	 
	 
	 
	 
	
	
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
	
	
	
public void onClick$rcpntsTbBtnId() {
		
	setKeywordUsageRepSubContent("keywords");
	
}
	

public void setKeywordUsageRepSubContent(String subContentName){
	
	logger.debug("in setReptActRepSubContent" );	
	if(subContentName.equals("keywords")) {
		//repPagingId.setAttribute("type","All");
		Components.removeAllChildren(genericLbHeaderId);
		//repPagingId.setTotalSize((int)smsCampaignReport.getSent());
		//getUsedkeywords();
		currentPage = 1;
		
	} 
}
	

public void onClick$exportKeywordBtnId() {
	
	try {
		export(exportKeywordCbId, "keywords");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}	
	
	
}


public void onClick$exportResponseBtnId() {
	try {
		export(exportResponseCbId,"response");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}
} // onClick$exportBtnId


public void onClick$exportComplaincyKeywordBtnId() {
	try {
		exportComplaincyKeywordResponse(exportComplaincyKeywordCbId);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception ::" , e);
	}
} // onClick$exportBtnId

public void exportComplaincyKeywordResponse(Combobox fileType) throws Exception {
	
	int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
	String type = (String)fileType.getItemAtIndex(index).getValue();
	
	List<SMSSettings> smsSettings = smsSettingsDao.findByUser(currentUser.getUserId().longValue());
	
	if(smsSettings == null || complaincyKeywordsLbId.getItemCount() <= 0) {
		MessageUtil.setMessage("No records found to export.", "color:red;");
		
		return;
	}
	
	
	if(type.equalsIgnoreCase("csv")){
	  	String s = ",";
        StringBuffer sb = new StringBuffer();
        for (Object head : complaincyKeywordsLbId.getHeads()) {
	        String h = "";
	        for (Object header : ((Listhead) head).getChildren()) {
	       	 
	       	 if(h.trim().length() > 0 ) h += s; 
	       	 h += "\""+((Listheader) header).getLabel()+"\"" ;
	        }
	        sb.append(h + "\r\n");
          }
          if(complaincyKeywordsLbId.getItemCount()==0){
          	try {
  				MessageUtil.setMessage("No records found to export.","color:blue");
  			} catch (Exception e) {
  			}
          	return;
          }
         
      	SMSSettings optinSettings = null;
  		SMSSettings optOutSettings = null;
  		SMSSettings helpSettings = null;
  		
  		for (SMSSettings eachSMSSetting : smsSettings) {
  			
  			if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN)) optinSettings = eachSMSSetting;
  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT)) optOutSettings = eachSMSSetting;
  			else if(eachSMSSetting.getType().equalsIgnoreCase(OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP)) helpSettings = eachSMSSetting;
  			
  		}
  		List<Object[]> keyWrdsList = null;
  		if(optinSettings != null) {
  			keyWrdsList = smsSettingsDao.findAllkeyWordResponses(currentUser.getUserId(), orgId,  OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTIN);
 			
 			
 			if(keyWrdsList != null && keyWrdsList.size()>0) {
 				
 				for (Object[] objects : keyWrdsList) {
 					 String j = "";

 					 j +=  "\""+( objects[0] == null ? optinSettings.getKeyword() : (String)objects[0])+"\""+s+ "\"Opt-in \""+ s+ "\""+objects[1]+"\"";
 					 sb.append(j + "\r\n");
 				}
 				
 				
 			}//if
  		}
  		if(optOutSettings != null) {
			 keyWrdsList = smsSettingsDao.findAllkeyWordResponses(currentUser.getUserId(), orgId,  OCConstants.SMS_PROGRAM_KEYWORD_TYPE_OPTOUT);
			
			
			if(keyWrdsList != null && keyWrdsList.size()>0) {
				
				for (Object[] objects : keyWrdsList) {
					 String j = "";
	
					 j +=  "\""+( objects[0] == null ? optOutSettings.getKeyword() : (String)objects[0])+"\""+s+ "\"Opt-out \""+ s+ "\""+objects[1]+"\"";
					 sb.append(j + "\r\n");
				}
				
				
			}//if
  		}
  		if(helpSettings != null) {
 			keyWrdsList = smsSettingsDao.findAllkeyWordResponses(currentUser.getUserId(), orgId,  OCConstants.SMS_PROGRAM_KEYWORD_TYPE_HELP);
 			
 			
 			if(keyWrdsList != null && keyWrdsList.size()>0) {
 				
 				for (Object[] objects : keyWrdsList) {
 					 String j = "";

 					 j +=  "\""+( objects[0] == null ? helpSettings.getKeyword() : (String)objects[0])+"\""+s+ "\"Help \""+ s+ "\""+objects[1]+"\"";
 					 sb.append(j + "\r\n");
 				}
 				
 				
 			}//if
  		}	
 			Filedownload.save(sb.toString().getBytes(), "text/plain", "SMSKeywordUsageReports.csv");
	}
	
	
	
}


public void export(Combobox fileType, String exportType) throws Exception{
	//logger.debug("-- just entered --");
	int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
	String type = (String)fileType.getItemAtIndex(index).getValue();
	if(type.equalsIgnoreCase("csv")){
	  	String s = ",";
        StringBuffer sb = new StringBuffer();
        
        if(exportType.equalsIgnoreCase("keywords")) {
        	 for (Object head : keywordsLbId.getHeads()) {
                 String h = "";
                 for (Object header : ((Listhead) head).getChildren()) {
                	 
                	 if(h.trim().length() > 0 ) h += s; 
                	 h += "\""+((Listheader) header).getLabel()+"\"" ;
                 }
                 sb.append(h + "\r\n");
               }
               if(keywordsLbId.getItemCount()==0){
               	try {
       				MessageUtil.setMessage("No records found to export.","color:blue");
       			} catch (Exception e) {
       			}
               	return;
               }
               
           int size = 1000;
       		
       		long total = orgSMSkeywordsDao.findAllCountByOrg(orgId);
       		
       		for(int i=0;i < total; i+=size) {
       			
       			
       			
       			List<Object[]> keyWrdsList = orgSMSkeywordsDao.getUsedKeywordReport(orgId, i,size,orderby_colName_keywords,desc_Asc);
       			
       			
       			if(keyWrdsList.size()>0) {
       				
       				for (Object[] objects : keyWrdsList) {
       					 String j = "";
       					 OrgSMSkeywords keyword = (OrgSMSkeywords) objects[0];
       					 j +=  "\""+keyword.getKeyword()+"\""+ s+"\""+keyword.getShortCode()+"\""+ s+"\""+objects[1]+"\"";
       					 sb.append(j + "\r\n");
       				}
       				
       				
       			}//if
       			
       					
       					
       		}
       		 Filedownload.save(sb.toString().getBytes(), "text/plain", "SMSKeywordUsageReports.csv");
               
        	
        	
        }//if
        else if(exportType.equalsIgnoreCase("response")) {
        	
        	
        	 for (Object head : responsesLbId.getHeads()) {
                 String h = "";
                 for (Object header : ((Listhead) head).getChildren()) {
                	 
                	 if(h.trim().length() > 0 ) h += s; 
                   h += "\""+((Listheader) header).getLabel() +"\"";
                 }
                 sb.append(h + "\r\n");
               }
               if(responsesLbId.getItemCount()==0){
               	try {
       				MessageUtil.setMessage("No records found to export.","color:blue");
       			} catch (Exception e) {
       			}
               	return;
               }
               
               int size = 1000;
       		
       		long total = keywordListPaging.getTotalSize();
       		List<Object[]> keyWrdsList = null;
       		
       		for(int i=0;i < total; i+=size) {
       		if(mobileFilterTbId.getText().isEmpty() || mobileFilterTbId.getText().equalsIgnoreCase("Mobile Number...")) {
       			keyWrdsList = orgSMSkeywordsDao.getKeywordResponseReport(orgId,i, size);
       		}
       		else {
       			
       			keyWrdsList = orgSMSkeywordsDao.getKeywordResponseReport(orgId,mobileFilterTbId.getText(),  i, size,orderby_colName_response,desc_Asc);
       		}
       			
       			if(keyWrdsList.size()>0){
       				
       				for (Object[] objects : keyWrdsList) {
       					 String j = "";
       					
       					String mobNUm = (String)objects[0];
       					if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
       						
	    					if(!(mobNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && 
	    							(mobNUm.length() >= currentUser.getUserOrganization().getMinNumberOfDigits()
	    							&& mobNUm.length() <= currentUser.getUserOrganization().getMaxNumberOfDigits())) mobNUm = "+" + GetUser.getUserObj().getCountryCarrier() + mobNUm;
	    					else if(mobNUm.length() < 10) mobNUm = mobNUm;
	    					else mobNUm = "+" + mobNUm;
       					}
       					 j +=  "\""+mobNUm+"\""+ s+"\""+objects[1]+"\""+s+"\""+objects[2]+"\""+s+"\""+objects[3]+"\""+s;
       					// if(objects.length > 2) {
       						 
       						 if(objects[4] != null && objects[4] instanceof Calendar) {
       							 
       							Calendar responseTime = (Calendar)objects[4];
       							j +=  "\""+(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone))+"\"";
       							 
       							 
       						 }//if
       						 else {
       							 
       							j += "\"\"";
       							 
       						 }
       						 
       						 
       					// }//if
       					 
       					 j += s+"\""+(objects[5] != null ? objects[5].toString() : Constants.STRING_NILL)+"\""+ s+"\""+(objects[6]!= null ? objects[6].toString() : Constants.STRING_NILL)+"\""+s;
       					 if(objects[7] != null && objects[7] instanceof Calendar) {
   							 
    							Calendar responseTime = (Calendar)objects[7];
    							j +=  "\""+(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone))+"\"";
    							 
    							 
    						 }//if
    						 else {
    							 
    							j += "\"\"";
    							 
    						 }
    						 
       					 
       					 sb.append(j + "\r\n");
       				}
       				
       				
       			}
       			
       					
       					
       		}
       		 Filedownload.save(sb.toString().getBytes(), "text/plain", "SMSKeywordResponseReports.csv");
               
        	
        }//else

       
        
       /* for (Object item : suppContRepLbId.getItems()) {
          String i = "";
          for (Object cell : ((Listitem) item).getChildren()) {
            i += ((Listcell) cell).getLabel() + s;
          }
          sb.append(i + "\r\n");
        }
        Filedownload.save(sb.toString().getBytes(), "text/plain", "SuppressedContactsReports.csv");*/
	}
} // fileDownload

	

	public void onChanging$mobileFilterTbId(InputEvent event) {
	 
		 String mobile  = event.getValue();
		 int pNo = Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel());
		 responseListPaging.setPageSize(pNo);
		 int totalCount = orgSMSkeywordsDao.getKeywordResponseReportCount(orgId, mobile);
		responseListPaging.setTotalSize(totalCount);
		responseListPaging.setActivePage(0);
			
		 setUpDataFiletredByMobile(mobile, 0, (byte)pNo);
	}
	
	public void onClick$resetAnchId() {
		orderby_colName_response="i.timeStamp";
		desc_Asc="desc";
		mobileFilterTbId.setValue("");
		 int totalCount = orgSMSkeywordsDao.getKeywordResponseReportCount(orgId, mobileFilterTbId.getText().trim());
		responseListPaging.setTotalSize(totalCount);
		responseListPaging.setActivePage(0);
		responsePageSizeLbId.setSelectedIndex(0);
		redraw("response", 0, Integer.parseInt(responsePageSizeLbId.getSelectedItem().getLabel()) );
	}
		 
	 public void setUpDataFiletredByMobile(String mobNo, int start_index,int  _size) {
		 
		 if(mobNo.length() > 0 || !mobNo.isEmpty()) {
			 
			 if(mobNo.startsWith(""+GetUser.getUserObj().getCountryCarrier())) {
					
				 mobNo = mobNo.substring((""+GetUser.getUserObj().getCountryCarrier()).length());
			}
			
			 logger.debug("sending mob no "+ mobNo); 
		 
			 
			 if(!Utility.validateBy(Constants.MOBNUM_VALID_FOR_SEARCH,mobNo)) {
				 MessageUtil.setMessage("Enter valid mobile number.","color:red");
				 return;
			 }
		 }
		  
		 	
		 	Listitem li = null;
			
			int count = responsesLbId.getItemCount();
			
			for(;count>0;count--){
				responsesLbId.removeItemAt(count-1);
			}
			
			
			List<Object[]> keyWrdsUsageList = orgSMSkeywordsDao.getKeywordResponseReport(orgId, mobNo, start_index, _size,orderby_colName_response,desc_Asc);
			logger.debug("number of records  "+keyWrdsUsageList.size());
			
			for (Object[] obj : keyWrdsUsageList) {
				
				String mobNUm = (String)obj[0];
				
				if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
					if(!(mobNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && 
							(mobNUm.length() >= currentUser.getUserOrganization().getMinNumberOfDigits()
							&& mobNUm.length() <= currentUser.getUserOrganization().getMaxNumberOfDigits())) mobNUm = "+" + GetUser.getUserObj().getCountryCarrier() + mobNUm;
					else if(mobNUm.length() < 10) mobNUm = mobNUm;
					else mobNUm = "+"  + mobNUm;
				}
				
				li = new Listitem();
				li.appendChild(new Listcell(mobNUm));
				li.appendChild(new Listcell("" + obj[1]));
				li.appendChild(new Listcell("" + obj[2]));
				li.appendChild(new Listcell("" + obj[3]));
				
				//if(obj.length > 2) {
					
					
					if(obj[4] != null && obj[4] instanceof Calendar ) {
						
						Calendar responseTime = (Calendar)obj[4];
						li.appendChild(new Listcell(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
						
					}//if
					else{
						
						li.appendChild(new Listcell("--"));
						
					}//else
					
					
				//}//if
				Listcell lc = new Listcell( );
				String autoResponse = obj[5] != null ? obj[5].toString() : Constants.STRING_NILL;
				Label lbl = new Label(autoResponse);
				lbl.setMaxlength(20);
				lbl.setTooltiptext(autoResponse);
				lbl.setParent(lc);	
				li.appendChild(lc);
				
				
				li.appendChild(new Listcell(obj[6] != null ? obj[6].toString() : "--"));
				if(obj[7] != null && obj[7] instanceof Calendar ) {
					
					Calendar responseTime = (Calendar)obj[7];
					li.appendChild(new Listcell(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
					
				}//if
				else{
					
					li.appendChild(new Listcell("--"));
					
				}//else
				responsesLbId.appendChild(li);
				
			}
			
			
			
		
	 }
	 
		 
}

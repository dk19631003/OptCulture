package org.mq.marketer.campaign.controller.report;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.OrgSMSkeywordsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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
import org.zkoss.zul.event.PagingEvent;

public class SMSKeywordsResponseReportController extends GenericForwardComposer{

	private Paging responseListPaging;
	private OrgSMSkeywordsDao orgSMSkeywordsDao;
	
	private Listbox responsesLbId, responsePageSizeLbId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private Long orgId;
	
	Label noOfResLblId, keywordLblId, recvNumLblId;
	Combobox exportKeywordCbId, exportResponseCbId, exportComplaincyKeywordCbId;
	private Session session;
	private TimeZone clientTimeZone;
		
	public SMSKeywordsResponseReportController() {
		
		orgSMSkeywordsDao = (OrgSMSkeywordsDao)SpringUtil.getBean("orgSMSkeywordsDao");
		session = Sessions.getCurrent();
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		orgId = GetUser.getUserObj().getUserOrganization().getUserOrgId();
		
		
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("SMS Keyword Reports","",style,true);
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		
		
		responseListPaging.setDetailed(true);
		
		exportResponseCbId.setSelectedIndex(0);
		
		responseListPaging.setAttribute("type", "response");
				
		/*String keyword = (String) session.getAttribute("keywordReport");
		
		String receivingNum = (String) session.getAttribute("keywordReportReceiveingNumber");*/
		OrgSMSkeywords keyword = (OrgSMSkeywords)session.getAttribute("keywordReport");
		
		if(keyword == null) return;
		
		logger.debug("keyword is  "+keyword.getKeyword()+ "  receiving number " +keyword.getShortCode() );
		
		int totalCount = orgSMSkeywordsDao.findAllResponseCountByOrg(keyword.getKeyword(), keyword.getShortCode());
		
		responseListPaging.setTotalSize(totalCount);
		
		keywordLblId.setValue(keyword.getKeyword());
		
		String recvNUm = keyword.getShortCode();
		Users currUser = keyword.getUser();
		if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
			if(!(recvNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && 
					(recvNUm.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
					&& recvNUm.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())) recvNUm = "+" + GetUser.getUserObj().getCountryCarrier() + recvNUm;
			else if(recvNUm.length() < 10) recvNUm = recvNUm;
			else recvNUm = "+"  + recvNUm;
		}
		
		
		recvNumLblId.setValue(recvNUm);
		
		Long keyWrdsNumRes = orgSMSkeywordsDao.getUsedKeywordReport(orgId, keyword.getKeyword(),keyword.getShortCode());
		
		noOfResLblId.setValue(keyWrdsNumRes+"");
		
		
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
		redraw("response", 0, (byte)10);		
	}
	
	

	private void redraw(String type,int start_index, byte _size) {
		try {
			logger.debug("type is--------->"+type);
			
			
			
			Listitem li = null;
			
				// this is our block
			OrgSMSkeywords keyword = (OrgSMSkeywords)session.getAttribute("keywordReport");
				
				//String receivingNum = (String) session.getAttribute("keywordReportReceiveingNumber");
				int count = responsesLbId.getItemCount();
				
				for(;count>0;count--){
					responsesLbId.removeItemAt(count-1);
				}
				
				String keywordStr = keyword.getKeyword();	
				String receivingNum = keyword.getShortCode();
				
				logger.debug("keyword is  "+keyword.getKeyword()+ "  receiving number " +keyword.getShortCode() );
				
				List<Object[]> keyWrdsResUsageList = orgSMSkeywordsDao.getKeywordResponseReport(keywordStr,receivingNum, start_index, _size);
				
				logger.debug("number of records  "+keyWrdsResUsageList.size());
				Users currUser = keyword.getUser();
				for (Object[] obj : keyWrdsResUsageList) {
					
					String mobNUm = (String)obj[0];
					
					if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
						if(!(mobNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && 
								(mobNUm.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
								&& mobNUm.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())) mobNUm = "+" + GetUser.getUserObj().getCountryCarrier() + mobNUm;
						else if(mobNUm.length() < 10) mobNUm = mobNUm;
						else mobNUm = "+"  + mobNUm;
					}
					
					li = new Listitem();
					li.appendChild(new Listcell(mobNUm));
					
					String recvNUm = "" + obj[1];
					
					if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
						if(!(recvNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && 
								(recvNUm.length() >= currUser.getUserOrganization().getMinNumberOfDigits()
								&& recvNUm.length() <= currUser.getUserOrganization().getMaxNumberOfDigits())) recvNUm = "+" + GetUser.getUserObj().getCountryCarrier() + recvNUm;
						else if(recvNUm.length() < 10) recvNUm = recvNUm;
						else recvNUm = "+"  + recvNUm;
					}
					
					li.appendChild(new Listcell(recvNUm));
					li.appendChild(new Listcell("" + obj[2]));
					
					//if(obj.length > 2) {
						
						
						if(obj[3] != null && obj[3] instanceof Calendar ) {
							
							Calendar responseTime = (Calendar)obj[3];
							li.appendChild(new Listcell(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
							
						}//if
						else{
							
							li.appendChild(new Listcell("--"));
							
						}//else
						
						
					//}//if
					Listcell lc = new Listcell( );
					String autoResponse = obj[4] != null ? obj[4].toString() : Constants.STRING_NILL;
					Label lbl = new Label(autoResponse);
					lbl.setMaxlength(20);
					lbl.setTooltiptext(autoResponse);
					lbl.setParent(lc);	
					li.appendChild(lc);
					
					
					li.appendChild(new Listcell(obj[5] != null ? obj[5].toString() : "--"));
					if(obj[6] != null && obj[6] instanceof Calendar ) {
						
						Calendar responseTime = (Calendar)obj[6];
						li.appendChild(new Listcell(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)));
						
					}//if
					else{
						
						li.appendChild(new Listcell("--"));
						
					}//else
					responsesLbId.appendChild(li);
					
				}
				
				
				
				// our block ends
		}catch (Exception e) {
			// TODO: handle exception
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
	


public void onClick$exportResponseBtnId() {
	try {
		export(exportResponseCbId,"response");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception :::",e);
	}
} // onClick$exportBtnId




public void export(Combobox fileType, String exportType) throws Exception{
	
	int index = fileType.getSelectedIndex()==-1?0:fileType.getSelectedIndex();
	String type = (String)fileType.getItemAtIndex(index).getValue();
	if(type.equalsIgnoreCase("csv")){
	  	String s = ",";
        StringBuffer sb = new StringBuffer();
        
        if(exportType.equalsIgnoreCase("response")) {
        	
        	
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
       		
               OrgSMSkeywords keyword = (OrgSMSkeywords)session.getAttribute("keywordReport");
               
              // String receivingNum = (String) session.getAttribute("keywordReportReceiveingNumber");
               
               
       		String keywordStr = keyword.getKeyword();	
			String receivingNum = keyword.getShortCode();
			
			logger.debug("keyword is  "+keyword.getKeyword()+ "  receiving number " +keyword.getShortCode() );
			
			long total = orgSMSkeywordsDao.findAllResponseCountByOrg(keywordStr, receivingNum);
			logger.debug("total size "+total);
			
       		for(int i=0;i < total; i+=size) {
       			
       			
       			
       			List<Object[]> keyWrdsList = orgSMSkeywordsDao.getKeywordResponseReport(keywordStr,receivingNum, i, size);
       			logger.debug("export size "+keyWrdsList);
       			
       			if(keyWrdsList.size()>0){
       				
       				for (Object[] objects : keyWrdsList) {
       					 String j = "";
       					
       					String mobNUm = (String)objects[0];
       					if(SMSStatusCodes.setCountryCode.get(GetUser.getUserObj().getCountryType())) {
       						
	    					if(!(mobNUm.startsWith(GetUser.getUserObj().getCountryCarrier().toString())) && 
	    							(mobNUm.length() >= GetUser.getUserObj().getUserOrganization().getMinNumberOfDigits()
	    							&& mobNUm.length() <= GetUser.getUserObj().getUserOrganization().getMaxNumberOfDigits())) mobNUm = "+" + GetUser.getUserObj().getCountryCarrier() + mobNUm;
	    					else if(mobNUm.length() < 10) mobNUm = mobNUm;
	    					else mobNUm = "+" + mobNUm;
       					}
       					 j +=  "\""+mobNUm+"\""+ s+"\""+objects[1]+"\""+s+"\""+objects[2]+"\""+s;
       					// if(objects.length > 2) {
       						 
       						 if(objects[3] != null && objects[3] instanceof Calendar) {
       							 
       							Calendar responseTime = (Calendar)objects[3];
       							j +=  "\""+(MyCalendar.calendarToString(responseTime, MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone))+"\"";
       							 
       							 
       						 }//if
       						 else {
       							 
       							j += "\"\"";
       							 
       						 }
       						 
       						 
       					// }//if
       					 
       					 j += s+"\""+(objects[4] != null ? objects[4].toString() : Constants.STRING_NILL)+"\""+ s+"\""+(objects[5]!= null ? objects[5].toString() : Constants.STRING_NILL)+"\""+s;
       					 if(objects[6] != null && objects[6] instanceof Calendar) {
   							 
    							Calendar responseTime = (Calendar)objects[6];
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

	}
} // fileDownload

	public void onClick$backBtnId() throws Exception {
		Redirect.goToPreviousPage();
	}
	

}

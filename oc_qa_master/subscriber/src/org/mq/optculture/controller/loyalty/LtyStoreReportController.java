package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

public class LtyStoreReportController  extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private LoyaltyProgramService ltyPrgmSevice;
	private Long userId;
	private Long prgmId;
	private LoyaltyProgram prgmObj;
	private TimeZone clientTimeZone;
	private Label listLblId, enrollFooterId, issFooterId;
	private Label enrollmentFooterId,ltyIssFooterId,gftIssFooterId,redemFooterId,inquiryFooterId,returnFooterId,storeCreditFooterId,transferFooterId,totalFooterId;
	private Rows storeLiabilityRowsId,enrollRowsId,transRowsId,storeKpisRowsId;
	private Foot storeLiabilityFooterId,transFooterId,storeKpiFooterId;
	private Listbox enrollDurLbId,transDurLbId,timeDurLbId, storeListLBId, sbsListLBId,dateLBId;
	private Combobox exportCbId;
	private MyDatebox fromDateboxId,toDateboxId,transFromDateboxId,transToDateboxId,storeKPIfromDateboxId,storeKPItoDateboxId,storeFromDateId,storeToDateboxId;
	private Div datesDivId,transDatesDivId,storeKPIdatesDivId,custExport$chkDivId,dateDivId;
	private Columns enrollColsId,transColsId;
	//private Column giftCardIssId,transferId,giftIssueId;
	private Listheader giftCardIssId,transferId,giftIssueId;
	private Window custExport;
	private Grid enrollLbId,storeLiabilityLbId,storeKpiLbId,transLbId;
	private List<OrganizationStores> storeList;
	private Listfoot storeFooterDivID, sbsFooterDivID;
	private Listfooter gftFooter, transferFooter;
	private final String FOOTER="footer";
	
	
	public LtyStoreReportController() {
		ltyPrgmSevice = new LoyaltyProgramService();
		userId = GetUser.getUserObj().getUserId();
		session = Sessions.getCurrent();
		prgmId = (Long) session.getAttribute("PROGRAM_REPORT_DETAILS");
		prgmObj = ltyPrgmSevice.getProgmObj(prgmId);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);
		clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
		
		listLblId.setValue(MyCalendar.calendarToString(new MyCalendar(clientTimeZone), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));
		//redrawKpiStore();
		storeList = ltyPrgmSevice.getAllStores(GetUser.getUserObj().getUserOrganization().getUserOrgId());
		exportCbId.setSelectedIndex(0);
		//redrawStoreLiability();
		redrawTransStore();
	    redrawRegStore();
	    redrawStoreKPIs();
	    
	    Map<Integer, Field> objMap = new HashMap<Integer, Field>();

	    objMap.put(0, OrganizationStores.class.getDeclaredField("subsidiaryName"));
	    objMap.put(1, OrganizationStores.class.getDeclaredField("storeName"));

	    storeListLBId.setAttribute(FOOTER, storeFooterDivID);
	    LBFilterEventListener.lbFilterSetup(storeListLBId, null, null, null, null,  objMap);
	    
	    sbsListLBId.setAttribute(FOOTER, sbsFooterDivID);
	    LBFilterEventListener.lbFilterSetup(sbsListLBId, null, null, null, null,  objMap);
		
	}
	
	public void onSelect$enrollDurLbId() {

	   
		
		if(enrollDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			 fromDateboxId.setText("");
			 toDateboxId.setText("");
			 datesDivId.setVisible(true);
			 }
		 else {
			 datesDivId.setVisible(false);
			 redrawRegStore();
		 }
		/*if(enrollRowsId.getChildren().size() > 5){
				enrollLbId.setHeight("240px");
							}else{
							enrollLbId.setHeight("");	
							}*/
		
		}
	public void onSelect$dateLBId() {
		
		 if(dateLBId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			 storeFromDateId.setText("");
			 storeToDateboxId.setText("");
			 dateDivId.setVisible(true);
		 }
		 else {
			 dateDivId.setVisible(false);
			 redrawStoreKPIs();
			 redrawRegStore();
			 redrawTransStore();
			 
		 }
		 if(storeKpisRowsId.getChildren().size() > 5){
			 storeKpiLbId.setHeight("240px");
							}else{
								storeKpiLbId.setHeight("");	
							}
	}
	public void onClick$storeDateFilterBtnId(){
		 if(dateLBId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")){ 
				if(!isValidate(storeFromDateId,storeToDateboxId)){
					return;
				 }
			 }
		 redrawStoreKPIs();
		 redrawRegStore();
		 redrawTransStore();
	}
	public void onClick$storeDateResetBtnId(){
		dateLBId.setSelectedIndex(0);
		storeFromDateId.setText("");
		storeToDateboxId.setText("");
		dateDivId.setVisible(false);
		redrawStoreKPIs();
		redrawRegStore();
	    redrawTransStore();
	}
	public void onSelect$timeDurLbId() {
		
		 if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			 storeKPIfromDateboxId.setText("");
			 storeKPItoDateboxId.setText("");
			 storeKPIdatesDivId.setVisible(true);
		 }
		 else {
			 storeKPIdatesDivId.setVisible(false);
			 redrawStoreKPIs();
			 
		 }
		 if(storeKpisRowsId.getChildren().size() > 5){
			 storeKpiLbId.setHeight("240px");
							}else{
								storeKpiLbId.setHeight("");	
							}
	}
	
	private void redrawStoreKPIs() {
		
		Components.removeAllChildren(storeKpisRowsId);
		Components.removeAllChildren(storeKpiFooterId);
		DecimalFormat f = new DecimalFormat("#0.00");
		long totVisits = 0 ;
		double totRevenue = 0.00;
		double totalIssued=0.00;
		double totalRedeemed=0.00;

	//	Object[] datearry = getDateValues(timeDurLbId,storeKPIfromDateboxId,storeKPItoDateboxId);
		Object[] datearry = getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
		
		
			Row row = null;

			List<Object[]> kpiobjArr = null;
			kpiobjArr =ltyPrgmSevice.getStorelevelKPI(userId, prgmId,MyCalendar.calendarToString((Calendar) datearry[0], null),
					MyCalendar.calendarToString((Calendar) datearry[1], null));
            if(kpiobjArr != null && kpiobjArr.size() > 5){
            	storeKpiLbId.setHeight("240px");
            }
			if(kpiobjArr != null) {
				for(Object[] obj : kpiobjArr) {
					row = new Row();
					if(obj[0] != null && obj[3]!=null){
					String sName = "Store ID "+obj[0].toString() ;
				//	String sbsName = "Subsidiary ID "+obj[3].toString();
					String sbsName="";
					for (OrganizationStores org : storeList){
					if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) 
						&& obj[3].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()){
				    	sName = org.getStoreName().toString();
				    	sbsName = org.getSubsidiaryName().toString();
				    
				    	break;
					}}
				
					row.appendChild(new Label(sbsName));
					row.appendChild(new Label(sName));
			
					}
						else{
						row.appendChild(new Label("--"));
						row.appendChild(new Label("--"));
						}
                    row.appendChild(new Label(obj[1]==null? 0+"" :obj[1]+""));
					row.appendChild(new Label(obj[2]==null? f.format(0.00):f.format(obj[2])));
					totVisits = totVisits + (obj[1] == null ? 0 : Long.parseLong(obj[1].toString()));
					totRevenue = totRevenue + (obj[2] == null ? 0.00 : Double.parseDouble(obj[2].toString()));
					
/*					row.appendChild(new Label(obj[3]==null? f.format(0.00):f.format(obj[3])));
					row.appendChild(new Label(obj[4]==null? f.format(0.00):f.format(obj[4])));
					
					totalIssued=totalIssued+(obj[3] == null ? 0.00 : Double.parseDouble(obj[3].toString()));
					totalRedeemed=totalRedeemed+(obj[4] == null ? 0.00 : Double.parseDouble(obj[4].toString()));
*/
					row.setParent(storeKpisRowsId);
				}

				Footer footer = new Footer();
				footer.appendChild(new Label("TOTAL"));
				footer.setParent(storeKpiFooterId);
				
				footer = new Footer();
				footer.appendChild(new Label(""));
				footer.setParent(storeKpiFooterId);

				footer = new Footer();
				footer.appendChild(new Label(totVisits+""));
				footer.setParent(storeKpiFooterId);
				
				footer = new Footer();
				footer.appendChild(new Label(f.format(totRevenue)));
				footer.setParent(storeKpiFooterId);
				
				/*footer = new Footer();
				footer.appendChild(new Label(f.format(totalIssued)));
				footer.setParent(storeKpiFooterId);
				
				footer = new Footer();
				footer.appendChild(new Label(f.format(totalRedeemed)));
				footer.setParent(storeKpiFooterId);*/
			}
	}
			
			
		private void redrawRegStore() {
			resetGridCols();
			
			/*Components.removeAllChildren(enrollRowsId);
			Components.removeAllChildren(enrollFooterId);*/
			int totReg = 0;
			int totGift = 0;
			
			MessageUtil.clearMessage();
			logger.debug("-- just entered --");
			int count =  storeListLBId.getItemCount();
			
			for(; count>0; count--) {
				 storeListLBId.removeItemAt(count-1);
			}

			//Object[] arry = getDateValues(enrollDurLbId,fromDateboxId,toDateboxId);
			Object[] arry =  getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
			
			
			//Mobile Based Program
			
			if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
				//Row row = null;
				Listitem li = null;
				Listcell lc = null;
				
				List<Object[]> objArr = null;
				objArr =ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
						MyCalendar.calendarToString((Calendar) arry[1], null),true,null);
				/*if(objArr != null && objArr.size() > 5 ) {
					enrollLbId.setHeight("240px");
				}*/
				if(objArr != null) {
					for(Object[] obj : objArr) {
						//row = new Row();
						lc = new Listcell();
						li = new Listitem();
	                    if(obj[0] != null){
						String sName = "Store ID "+obj[0].toString() ;
						String sbsName = "Subsidiary ID "+obj[2].toString() ;
						for (OrganizationStores org : storeList){
						/*if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
					    	sName = org.getStoreName().toString();
					    	break;*/
							if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) &&
									obj[2].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()){
						    	sName = org.getStoreName().toString();
						    	sbsName = org.getSubsidiaryName().toString();
						    	break;						
						}}
						/*row.appendChild(new Label(sbsName));
						row.appendChild(new Label(sName));*/
						lc.setLabel(sbsName);
						lc.setParent(li);
						
						lc = new Listcell();
						lc.setLabel(sName);
						lc.setParent(li);
						}else{
							//row.appendChild(new Label(""));
							lc = new Listcell();
							lc.setLabel("");
							lc.setParent(li);
							
							lc = new Listcell();
							lc.setLabel("");
							lc.setParent(li);
							}
						//row.appendChild(new Label(obj[1]==null? 0+"" :obj[1]+""));
	                    
	                    lc = new Listcell();
						lc.setLabel(obj[1]==null? 0+"" :obj[1]+"");
						totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));

						//row.setParent(enrollRowsId);
						lc.setParent(li);
						li.setParent( storeListLBId);
					}

					/*Footer footer = new Footer();
					footer.appendChild(new Label("TOTAL"));
					footer.setParent(enrollFooterId);
					
					footer = new Footer();
					footer.appendChild(new Label(""));
					footer.setParent(enrollFooterId);

					footer = new Footer();
					footer.appendChild(new Label(totReg+""));
					footer.setParent(enrollFooterId);*/
					
					enrollFooterId.setValue(""+totReg);
				}else{
					enrollFooterId.setValue("");
				}
			}else {
				//Row row = null;
				Listitem li = null;
				Listcell lc = null;
				String storeNo;
				String sbsNo;
				List<Object[]> loyaltyObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
						MyCalendar.calendarToString((Calendar) arry[1], null),false,"loyalty");
				List<Object[]> giftObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
						MyCalendar.calendarToString((Calendar) arry[1], null),false,"gift");

				List <Object[]> objArr = null;
				if(loyaltyObjArr != null && loyaltyObjArr.size() > 0) {
					objArr = new ArrayList<Object[]>();
					for(Object[] obj : loyaltyObjArr) {
						
						//Object[] storeObj = new Object[3];
						Object[] storeObj = new Object[4];
						storeObj[0] = obj[0];
						storeObj[1] = obj[1] == null ? 0+"" :obj[1]+"";
						storeObj[3] = obj[2];
						objArr.add(storeObj);
					}

				}
				if(giftObjArr != null && giftObjArr.size() > 0) {

					if(objArr == null) {
						objArr = new ArrayList<Object[]>();
						for(Object[] gftObj : giftObjArr) {
							//Object[] storeObj = new Object[3];
							Object[] storeObj = new Object[4];
							storeObj[0] = gftObj[0];
							storeObj[3] = gftObj[2];
							storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
							objArr.add(storeObj);
						}
					}
					else {
						for(Object[] gftObj : giftObjArr) {
							boolean isExists = false;
							for (Object[] existObj : objArr) {
								logger.info("existObj[0]"+existObj[0] +"existObj[1]-------------"+existObj[1]+"existObj[2]"+existObj[2]);
								//if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString())) {
								if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString()) && existObj[3].toString().equalsIgnoreCase(gftObj[2].toString())) {
									existObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
									isExists = true;
									break;
								}
							}
							if(!isExists) {
								//Object[] storeObj = new Object[3];
								Object[] storeObj = new Object[4];
								storeObj[0] = gftObj[0];
								storeObj[3] = gftObj[2];
								storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
								objArr.add(storeObj);
							}
						}
					}
				}
				/*if(objArr != null && objArr.size() > 5 ) {
					enrollLbId.setHeight("240px");
				}*/
				if(objArr != null && objArr.size() > 0) {
					for(Object[] obj : objArr) {
						//row = new Row();
						lc = new Listcell();
						li = new Listitem();
						//if(obj[0] != null){
						if(obj[0] != null && obj[3] != null){
							storeNo = "Store ID "+obj[0].toString() ;
							sbsNo = "Subsidiary ID"+obj[3].toString();
							for (OrganizationStores org : storeList){
							//if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
							if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) &&
								    obj[3].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
								storeNo = org.getStoreName().toString();
								sbsNo = org.getSubsidiaryName().toString();
						    	break;
							}}
							}else{
								storeNo = "";
								sbsNo ="";
								}

						/*row.appendChild(new Label(sbsNo));
						row.appendChild(new Label(""+storeNo));
						row.appendChild(new Label(obj[1]==null? 0+"" :obj[1]+""));
						row.appendChild(new Label(obj[2]==null? 0+"" :obj[2]+""));*/
						lc.setLabel(sbsNo);
						lc.setParent(li);
						
						lc = new Listcell();
						lc.setLabel(""+storeNo);
						lc.setParent(li);
						
						lc = new Listcell();
						lc.setLabel(obj[1]==null? 0+"" :obj[1]+"");
						lc.setParent(li);
						
						lc = new Listcell();
						lc.setLabel(obj[2]==null? 0+"" :obj[2]+"");
						
						totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));
						totGift = totGift + (obj[2] == null ? 0 : Integer.parseInt(obj[2].toString()));

						//row.setParent(enrollRowsId);
						
						lc.setParent(li);
						li.setParent(storeListLBId);
					}

					/*Footer footer = new Footer();
					footer.appendChild(new Label("TOTAL"));
					footer.setParent(enrollFooterId);

					footer = new Footer();
					footer.appendChild(new Label(""));
					footer.setParent(enrollFooterId);
					
					footer = new Footer();
					footer.appendChild(new Label(totReg+""));
					footer.setParent(enrollFooterId);
					
					footer = new Footer();
					footer.appendChild(new Label(totGift+""));
					footer.setParent(enrollFooterId);*/
					
					enrollFooterId.setValue(""+totReg);
					issFooterId.setValue(""+totGift);
				}else{
					enrollFooterId.setValue("");
					issFooterId.setValue("");
				}
			}
		}

	
	private void resetGridCols() {
		
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			giftCardIssId.setVisible(false);
		}


	}
	
	
	private void resetTransGridCols() {
		
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			/*giftIssueId.setVisible(false);
			transferId.setVisible(false);*/
			giftIssueId.setVisible(false);
			transferId.setVisible(false);
			sbsFooterDivID.removeChild(gftFooter);
			sbsFooterDivID.removeChild(transferFooter);
		}
	
	}

	public void onClick$regDateFilterBtnId(){
		 if(enrollDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")){ 
			 if(!isValidate(fromDateboxId,toDateboxId)){
				 return;
			 }
		 }
		 redrawRegStore();
	}
	
	public void onClick$regDateResetBtnId(){
		enrollDurLbId.setSelectedIndex(0);
		fromDateboxId.setText("");
		toDateboxId.setText("");
		datesDivId.setVisible(false);
		redrawRegStore();
	}
	public void onClick$storeKPIregDateFilterBtnId(){
		 if(timeDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")){ 
			if(!isValidate(storeKPIfromDateboxId,storeKPItoDateboxId)){
				return;
			 }
		 }
		 redrawStoreKPIs();
	}
	
	public void onClick$storeKPIregDateResetBtnId(){
		timeDurLbId.setSelectedIndex(0);
		storeKPIfromDateboxId.setText("");
		storeKPItoDateboxId.setText("");
		storeKPIdatesDivId.setVisible(false);
		redrawStoreKPIs();
	}
	
	private Object[] getDateValues(Listbox type, MyDatebox fromDateId, MyDatebox toDateId) {
		
		Calendar startDate,endDate;
		endDate = new MyCalendar(clientTimeZone);
		endDate.set(MyCalendar.HOUR_OF_DAY, 23);
		endDate.set(MyCalendar.MINUTE, 59);
		endDate.set(MyCalendar.SECOND, 59);
		
		startDate = new MyCalendar(clientTimeZone);
		startDate.set(MyCalendar.HOUR_OF_DAY, 00);
		startDate.set(MyCalendar.MINUTE, 00);
		startDate.set(MyCalendar.SECOND, 00);
		
		if(type.getSelectedItem().getLabel().equalsIgnoreCase("Last 30 Days")) {
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 30);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(type.getSelectedItem().getLabel().equalsIgnoreCase("Last 3 Months")) {
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - 3)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(type.getSelectedItem().getLabel().equalsIgnoreCase("Last 6 Months")) {
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - 6)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(type.getSelectedItem().getLabel().equalsIgnoreCase("Last 1 Year")) {
			startDate.set(MyCalendar.MONTH, (endDate.get(MyCalendar.MONTH) - 12)+1);
			startDate.set(MyCalendar.DATE, 1);
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
		}else if(type.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			
				startDate = getStartDate(fromDateId);
				endDate = getEndDate(toDateId);

				/*if(startDate.get(Calendar.DATE) != endDate.get(Calendar.DATE) || startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)
						|| startDate.get(Calendar.YEAR) != endDate.get(Calendar.YEAR)) {
					endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE) - 1);
				}*/
		
		}else{
			startDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
			endDate.set(MyCalendar.DATE, endDate.get(MyCalendar.DATE));
		}
		
		Object[] arry = new Object[2];
		arry[0] = startDate;
		arry[1] = endDate;
		
		logger.info("str endDate 2"+ startDate + " "+endDate);
		return arry;
		
	}

	
	public void onSelect$transDurLbId() {
		
		 if(transDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")) {
			 transToDateboxId.setText("");
			 transFromDateboxId.setText("");
			 transDatesDivId.setVisible(true);
		 }
		 else {
			 transDatesDivId.setVisible(false);
			 redrawTransStore();
		 }
		 /*if(transRowsId.getChildren().size() > 6){
			 transLbId.setHeight("240px");
							}else{
								transLbId.setHeight("");	
							}*/
	}
	
	private void redrawTransStore() {
		resetTransGridCols();
		/*Components.removeAllChildren(transRowsId);
		Components.removeAllChildren(transFooterId);*/
		MessageUtil.clearMessage();
		logger.debug("-- just entered --");
		int count =  sbsListLBId.getItemCount();
		
		for(; count>0; count--) {
			 sbsListLBId.removeItemAt(count-1);
		}
		
		
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
			redrawMobileBasedTransStore();
		}
		
		else {
			redrawCardBasedTransStore();
		}
	}
	
	private void redrawCardBasedTransStore() {


		logger.info("-----------------------------in transactions-------------");
		//Row row = null;
		Listitem li = null;
		Listcell lc = null;

		//Object[] arry = getDateValues(transDurLbId,transFromDateboxId,transToDateboxId);
		Object[] arry = getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
		
		List<Object[]> objArr = ltyPrgmSevice.getStoresTransData(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
				MyCalendar.calendarToString((Calendar) arry[1], null));

		List <Object[]> storeObjArr = null;
		String storeNo;
		String sbsNo;
		if(objArr != null && objArr.size() > 0) {
			for(Object[] obj : objArr) {
				//if(obj[0]!=null){
				if(obj[0]!=null && obj[4] != null){
				if(storeObjArr == null) {
					storeObjArr = new ArrayList<Object[]>();
					//Object[] storeObj = new Object[10];
					Object[] storeObj = new Object[11];
					storeObj[0] = obj[0];
					storeObj[10] = obj[4];
					if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
						storeObj[1] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
						storeObj[2] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)&& 
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE.equalsIgnoreCase(obj[3].toString())){
						storeObj[3] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)&& 
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT.equalsIgnoreCase(obj[3].toString())){
						storeObj[4] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
						storeObj[5] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
							 OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
						storeObj[6] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
						storeObj[7] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
						storeObj[8] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER)){
						storeObj[9] = obj[2];
					}
					storeObjArr.add(storeObj);
				}
				else {
					boolean isExists = false;
					for (Object[] existObj : storeObjArr) {
						//if(existObj[0].toString().equalsIgnoreCase(obj[0].toString())) {
						if(existObj[0].toString().equalsIgnoreCase(obj[0].toString()) &&
								existObj[10].toString().equalsIgnoreCase(obj[4].toString())) {
							if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
								existObj[1] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
								existObj[2] = obj[2];
							}

							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE.equalsIgnoreCase(obj[3] == null ?"":obj[3].toString())){
								existObj[3] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT.equalsIgnoreCase(obj[3] == null ?"":obj[3].toString())){
								existObj[4] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
								existObj[5] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
								existObj[6] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
								existObj[7] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
								existObj[8] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER)){
								existObj[9] = obj[2];
							}
							isExists = true;
							break;
						}
					}
					if(!isExists) {
						//Object[] storeObj = new Object[10];
						Object[] storeObj = new Object[11];
						storeObj[0] = obj[0];
						storeObj[10] = obj[4];
						if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
							storeObj[1] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
							storeObj[2] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE.equalsIgnoreCase(obj[3].toString())){
							storeObj[3] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT.equalsIgnoreCase(obj[3].toString())){
							storeObj[4] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
							storeObj[5] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
							storeObj[6] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
							storeObj[7] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
							storeObj[8] = obj[2];
						}
						if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER)){
							storeObj[9] = obj[2];
						}
						storeObjArr.add(storeObj);
					}
				}
				}

			}
		}
		/*if(storeObjArr != null && storeObjArr.size() > 6) {
			transLbId.setHeight("240px");
		}*/
		if(storeObjArr != null && storeObjArr.size() > 0) {

			int totEnrollment = 0;
			int totInquiry = 0;
			int totIssuance = 0;
			int totGftIssuance = 0;
			int totRedemption = 0;
			int totReversal = 0;
			int totStoreCredit = 0;
			int tottransfer = 0;
			int total = 0;
			for (Object[] storeObj : storeObjArr) {
				//row = new Row();				
				lc = new Listcell();
				li = new Listitem();

				int rowTot = 0;
				int enrCount = storeObj[1] == null ? 0 : Integer.parseInt(storeObj[1].toString());
				int inqCount = storeObj[2] == null ? 0 : Integer.parseInt(storeObj[2].toString());
				int issCount = storeObj[3] == null ? 0 : Integer.parseInt(storeObj[3].toString());
				int gftIssCount = storeObj[4] == null ? 0 : Integer.parseInt(storeObj[4].toString());
				int redCount = storeObj[5] == null ? 0 : Integer.parseInt(storeObj[5].toString());
				int revCount = (storeObj[6] == null ? 0 : Integer.parseInt(storeObj[6].toString())) +
							   (storeObj[8] == null ? 0 : Integer.parseInt(storeObj[8].toString()));
				int strCrdtCount = storeObj[7] == null ? 0 : Integer.parseInt(storeObj[7].toString());
				int traCount = storeObj[9] == null ? 0 : Integer.parseInt(storeObj[9].toString());
				rowTot = enrCount + inqCount + issCount + gftIssCount + redCount + revCount + strCrdtCount + traCount;

				totEnrollment += enrCount;
				totInquiry += inqCount;
				totIssuance += issCount;
				totGftIssuance +=gftIssCount;
				totRedemption += redCount;
				totReversal += revCount;
				totStoreCredit += strCrdtCount;
				tottransfer += traCount;
				total += rowTot;
				
				//if(storeObj[0] != null){
				if(storeObj[0] != null && storeObj[10] != null){
					sbsNo = "Subsidiary ID "+storeObj[10].toString();
					storeNo = "Store ID "+storeObj[0].toString() ;
					for (OrganizationStores org : storeList){
					//if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
					if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty() &&
							storeObj[10].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
						sbsNo = org.getSubsidiaryName().toString();	
						storeNo = org.getStoreName().toString();
				    	break;
					}}
					}else{
						sbsNo = "";
						storeNo = "";
						}
				
                /*row.appendChild(new Label(""+sbsNo)); // subsidiary number
				row.appendChild(new Label(""+storeNo)); //store number
				row.appendChild(new Label(""+enrCount)); //enrollment
				row.appendChild(new Label(""+issCount)); //issuance
				row.appendChild(new Label(""+gftIssCount)); //giftIssuance
				row.appendChild(new Label(""+redCount)); //redemption
				row.appendChild(new Label(""+inqCount)); //inquiry
				row.appendChild(new Label(""+revCount)); //reversal
				row.appendChild(new Label(""+strCrdtCount)); //storeCredit
				row.appendChild(new Label(""+traCount)); //Transfer
				row.appendChild(new Label(""+rowTot)); //total  */	
				
				lc.setLabel(""+sbsNo);
				lc.setParent(li); // subsidiary number
				
				lc = new Listcell();
				lc.setLabel(""+storeNo);
				lc.setParent(li); //store number
				
				lc = new Listcell();
				lc.setLabel(""+enrCount);
				lc.setParent(li); //enrollment
				
				lc = new Listcell();
				lc.setLabel(""+issCount);
				lc.setParent(li); //issuance
				
				lc = new Listcell();
				lc.setLabel(""+gftIssCount);
				lc.setParent(li); //giftIssuance
				
				lc = new Listcell();
				lc.setLabel(""+redCount);
				lc.setParent(li); //redemption
				
				lc = new Listcell();
				lc.setLabel(""+inqCount);
				lc.setParent(li); //inquiry
				
				lc = new Listcell();
				lc.setLabel(""+revCount);
				lc.setParent(li); //reversal
				
				lc = new Listcell();
				lc.setLabel(""+strCrdtCount);
				lc.setParent(li); //storeCredit
				
				lc = new Listcell();
				lc.setLabel(""+traCount);
				lc.setParent(li); //Transfer
				
				lc = new Listcell();
				lc.setLabel(""+rowTot);
				lc.setParent(li); //total
				
				//row.setParent(transRowsId);
				lc.setParent(li);
				li.setParent(sbsListLBId);

			}
			

			/*Footer footer = new Footer();
			footer.appendChild(new Label("TOTAL"));
			footer.setParent(transFooterId);
			
			footer = new Footer();
			footer.appendChild(new Label(""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totEnrollment+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totIssuance+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totGftIssuance+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totRedemption+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totInquiry+""));
			footer.setParent(transFooterId);
			
			footer = new Footer();
			footer.appendChild(new Label(totReversal+""));
			footer.setParent(transFooterId);
			
			footer = new Footer();
			footer.appendChild(new Label(totStoreCredit+""));
			footer.setParent(transFooterId);
			
			footer = new Footer();
			footer.appendChild(new Label(tottransfer+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(total+""));
			footer.setParent(transFooterId);*/
			
			enrollmentFooterId.setValue(totEnrollment+"");
			ltyIssFooterId.setValue(totIssuance+"");
			gftIssFooterId.setValue(totGftIssuance+"");
			redemFooterId.setValue(totRedemption+"");
			inquiryFooterId.setValue(totInquiry+"");			
			returnFooterId.setValue(totReversal+"");			
			storeCreditFooterId.setValue(totStoreCredit+"");			
			transferFooterId.setValue(tottransfer+"");
			totalFooterId.setValue(total+"");
		}else{
			enrollmentFooterId.setValue("");
			ltyIssFooterId.setValue("");
			gftIssFooterId.setValue("");
			redemFooterId.setValue("");
			inquiryFooterId.setValue("");			
			returnFooterId.setValue("");			
			storeCreditFooterId.setValue("");			
			transferFooterId.setValue("");
			totalFooterId.setValue("");
		}

	}

	private void redrawMobileBasedTransStore() {

		//Row row = null;
		Listitem li = null;
		Listcell lc = null;
		String storeNo;
		String sbsNo;

		//Object[] arry = getDateValues(transDurLbId,transFromDateboxId,transToDateboxId);
		Object[] arry = getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
		List<Object[]> objArr = ltyPrgmSevice.getStoresTransData(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
				MyCalendar.calendarToString((Calendar) arry[1], null));
		List <Object[]> storeObjArr = null;

		if(objArr != null && objArr.size() > 0) {
			for(Object[] obj : objArr) {
                   if(storeObjArr == null) {
					storeObjArr = new ArrayList<Object[]>();
					//Object[] storeObj = new Object[8];
					Object[] storeObj = new Object[9];
					storeObj[0] = obj[0];
					storeObj[8] = obj[4];
					if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
						storeObj[1] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
						storeObj[2] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) ){
						storeObj[3] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
						storeObj[4] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
						storeObj[5] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
						storeObj[6] = obj[2];
					}
					else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
							OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
						storeObj[7] = obj[2];
					}
					storeObjArr.add(storeObj);
				}
				else {
					boolean isExists = false;
					for (Object[] existObj : storeObjArr) {
						//if(existObj[0].toString().equalsIgnoreCase(obj[0].toString())) {
						if(existObj[0].toString().equalsIgnoreCase(obj[0].toString()) 
								&& existObj[8].toString().equalsIgnoreCase(obj[4].toString())) {
							if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
								existObj[1] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
								existObj[2] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)){
								existObj[3] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
								existObj[4] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
								existObj[5] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
								existObj[6] = obj[2];
							}
							else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
									OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
								existObj[7] = obj[2];
							}
							isExists = true;
							break;
						}
					}
					if(!isExists) {
						//Object[] storeObj = new Object[8];
						Object[] storeObj = new Object[9];
						storeObj[0] = obj[0];
						storeObj[8] = obj[4];
						if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
							storeObj[1] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
							storeObj[2] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)){
							storeObj[3] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
							storeObj[4] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
							storeObj[5] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
							storeObj[6] = obj[2];
						}
						else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
								OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
							storeObj[7] = obj[2];
						}
					   storeObjArr.add(storeObj);
					}
				}


			}
			
			/*if(storeObjArr != null && storeObjArr.size() > 6) {
				transLbId.setHeight("240px");
			}*/
			if(storeObjArr != null && storeObjArr.size() > 0) {
				int totEnrollment = 0;
				int totInquiry = 0;
				int totIssuance = 0;
				int totRedemption = 0;
				int totReversal = 0;
				int totStoreCredit = 0;
				int total = 0;
				for (Object[] storeObj : storeObjArr) {
					//row = new Row();
					lc = new Listcell();
					li = new Listitem();

					int rowTot = 0;
					int enrCount = storeObj[1] == null ? 0 : Integer.parseInt(storeObj[1].toString());
					int inqCount = storeObj[2] == null ? 0 : Integer.parseInt(storeObj[2].toString());
					int issCount = storeObj[3] == null ? 0 : Integer.parseInt(storeObj[3].toString());
					int redCount = storeObj[4] == null ? 0 : Integer.parseInt(storeObj[4].toString());
					int revCount = (storeObj[5] == null ? 0 : Integer.parseInt(storeObj[5].toString()))+ 
							(storeObj[7] == null ? 0 :Integer.parseInt(storeObj[7].toString()));
					int strCrdtCount = storeObj[6] == null ? 0 : Integer.parseInt(storeObj[6].toString());
					rowTot = enrCount + inqCount + issCount + redCount + revCount + strCrdtCount;

					totEnrollment += enrCount;
					totInquiry += inqCount;
					totIssuance += issCount;
					totRedemption += redCount;
					totReversal += revCount;
					totStoreCredit += strCrdtCount;
					total += rowTot;
					//if(storeObj[0] != null){
					if(storeObj[0] != null && storeObj[8] != null){
						sbsNo ="Subsidiary ID "+storeObj[8].toString();
						storeNo = "Store ID "+storeObj[0].toString() ;
						for (OrganizationStores org : storeList){
							//if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
							if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty() &&
									storeObj[8].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
								sbsNo = org.getSubsidiaryName().toString();
								storeNo = org.getStoreName().toString();
								break;
							}}
					}else{
						sbsNo = "";
						storeNo = "";
					}
					/*row.appendChild(new Label(""+sbsNo)); //subsidiary number
				row.appendChild(new Label(""+storeNo)); //store number
				row.appendChild(new Label(""+enrCount)); //enrollment
				row.appendChild(new Label(""+issCount)); //issuance
				row.appendChild(new Label(""+redCount)); //redemption
				row.appendChild(new Label(""+inqCount)); //inquiry
				row.appendChild(new Label(""+revCount)); //reversal
				row.appendChild(new Label(""+strCrdtCount)); //storeCredit
				row.appendChild(new Label(""+rowTot)); //total */

					lc.setLabel(""+sbsNo);
					lc.setParent(li); //subsidiary number

					lc = new Listcell();
					lc.setLabel(""+storeNo);
					lc.setParent(li); //store number

					lc = new Listcell();
					lc.setLabel(""+enrCount);
					lc.setParent(li); //enrollment

					lc = new Listcell();
					lc.setLabel(""+issCount);
					lc.setParent(li); //issuance
					
					lc = new Listcell();
					lc.setLabel("");
					lc.setParent(li); //gift issuance

					lc = new Listcell();
					lc.setLabel(""+redCount);
					lc.setParent(li); //redemption

					lc = new Listcell();
					lc.setLabel(""+inqCount);
					lc.setParent(li); //inquiry

					lc = new Listcell();
					lc.setLabel(""+revCount);
					lc.setParent(li); //reversal

					lc = new Listcell();
					lc.setLabel(""+strCrdtCount);
					lc.setParent(li); //storeCredit
					
					lc = new Listcell();
					lc.setLabel("");
					lc.setParent(li); //transfer

					lc = new Listcell();
					lc.setLabel(""+rowTot);
					lc.setParent(li); //total

					//row.setParent(transRowsId);
					lc.setParent(li);
					li.setParent(sbsListLBId);

				}

				/*Footer footer = new Footer();
			footer.appendChild(new Label("TOTAL"));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(" "));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totEnrollment+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totIssuance+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totRedemption+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totInquiry+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totReversal+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(totStoreCredit+""));
			footer.setParent(transFooterId);

			footer = new Footer();
			footer.appendChild(new Label(total+""));
			footer.setParent(transFooterId);*/

				enrollmentFooterId.setValue(""+totEnrollment);
				ltyIssFooterId.setValue(""+totIssuance);
				//gftIssFooterId.setValue("");
				redemFooterId.setValue(""+totRedemption);
				inquiryFooterId.setValue(""+totInquiry);
				returnFooterId.setValue(""+totReversal);
				storeCreditFooterId.setValue(""+totStoreCredit);
				//transferFooterId.setValue("");
				totalFooterId.setValue(""+total);

			}
		}else{
			enrollmentFooterId.setValue("");
			ltyIssFooterId.setValue("");
			//gftIssFooterId.setValue("");
			redemFooterId.setValue("");
			inquiryFooterId.setValue("");
			returnFooterId.setValue("");
			storeCreditFooterId.setValue("");
			//transferFooterId.setValue("");
			totalFooterId.setValue("");
		}
	}

	public void onClick$transDateFilterBtnId(){
		 if(transDurLbId.getSelectedItem().getLabel().equalsIgnoreCase("Custom Dates")){ 
			 if(!isValidate(transFromDateboxId,transToDateboxId)){
				 return;
			 }
		 }
		 redrawTransStore();
	}
	
	public void onClick$transDateResetBtnId(){
		transDurLbId.setSelectedIndex(0);
		transFromDateboxId.setText("");
		transToDateboxId.setText("");
		transDatesDivId.setVisible(false);
		redrawTransStore();
	}

	private void redrawStoreLiability() {

		Components.removeAllChildren(storeLiabilityRowsId);
		Components.removeAllChildren(storeLiabilityFooterId);
		DecimalFormat f = new DecimalFormat("#0.00");
		Row row = null;
		String storeNo;

		List<Object[]> issObjArr = ltyPrgmSevice.getStoresIssLiabilityData(prgmId,userId);
		List<Object[]> redeemObjArr = ltyPrgmSevice.getStoresRedeemLiabilityData(prgmId,userId);

		List <Object[]> objArr = null;
		if(issObjArr != null && issObjArr.size() > 0) {
			objArr = new ArrayList<Object[]>();
			for(Object[] obj : issObjArr) {
				
				double earnedAmount = obj[1] == null ? 0.0 : Double.parseDouble(obj[1].toString().trim());
				double convrsnAmount = obj[2] == null ? 0.0 : Double.parseDouble(obj[2].toString().trim());
				double issuedAmount = earnedAmount + convrsnAmount;

				Object[] storeObj = new Object[3];
				storeObj[0] = obj[0];
				storeObj[1] = issuedAmount;
				objArr.add(storeObj);
			}

		}
		if(redeemObjArr != null && redeemObjArr.size() > 0) {

			if(objArr == null) {
				objArr = new ArrayList<Object[]>();
				for(Object[] rdmObj : redeemObjArr) {
					double redeemAmount = rdmObj[1] == null ? 0.0 : Double.parseDouble(rdmObj[1].toString().trim());
					Object[] storeObj = new Object[3];
					storeObj[0] = rdmObj[0];
					storeObj[2] = redeemAmount;
					objArr.add(storeObj);
				}
			}
			else {
				for(Object[] rdmObj : redeemObjArr) {
					double redeemAmount = rdmObj[1] == null ? 0.0 : Double.parseDouble(rdmObj[1].toString().trim());
					boolean isExists = false;
					for (Object[] existObj : objArr) {
						if((existObj[0] != null && rdmObj[0] != null) && existObj[0].toString().equalsIgnoreCase(rdmObj[0].toString())) {
							existObj[2] = redeemAmount;
							isExists = true;
							break;
						}
					}
					if(!isExists) {
						Object[] storeObj = new Object[3];
						storeObj[0] = rdmObj[0];
						storeObj[2] = redeemAmount;
						objArr.add(storeObj);
					}
				}
			}
		}

		double totIssuance = 0.00; 
		double totRedeem = 0.00; 
		double totDiff = 0.00; 
		
		if(objArr != null && objArr.size() > 5){
			storeLiabilityLbId.setHeight("240px");
		}
		if(objArr != null) {
			for(Object[] object : objArr) {
				row = new Row();
				if(object[0] != null){
					storeNo = "Store ID "+object[0].toString() ;
					for (OrganizationStores org : storeList){
					if (object[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
						storeNo = org.getStoreName().toString();
				    	break;
					}}
					}else{
						storeNo = "";
						}
				row.appendChild(new Label(""+storeNo));
				row.appendChild(new Label(object[1] == null ? f.format(0.00): f.format(Double.parseDouble(object[1]+"")))); 
				totIssuance += (object[1] == null ? 0.0 : Double.parseDouble(object[1]+""));
				row.appendChild(new Label(object[2] == null ? f.format(0.00) : f.format(Double.parseDouble(object[2]+"")))); 
				totRedeem += (object[2] == null ? 0.0 : Double.parseDouble(object[2]+""));
				double difference = (object[1] == null ? 0.0 : Double.parseDouble(object[1]+"")) - (object[2] == null ? 0.0 : Double.parseDouble(object[2]+"")); 
				totDiff += difference;
				row.appendChild(new Label(f.format(difference))); 
				row.setParent(storeLiabilityRowsId);
			}
		}
		
		

		Footer footer = new Footer();
		footer.appendChild(new Label("TOTAL"));
		footer.setParent(storeLiabilityFooterId);

		footer = new Footer();
		footer.appendChild(new Label(f.format(totIssuance)));
		footer.setParent(storeLiabilityFooterId);

		footer = new Footer();
		footer.appendChild(new Label(f.format(totRedeem)));
		footer.setParent(storeLiabilityFooterId);
		
		footer = new Footer();
		footer.appendChild(new Label(f.format(totDiff)));
		footer.setParent(storeLiabilityFooterId);

	}

	public Calendar getStartDate(MyDatebox fromDateId){
		
		if(fromDateId.getValue() != null && !fromDateId.getValue().toString().trim().isEmpty()) {
		Calendar serverFromDateCal = fromDateId.getServerValue();
		Calendar tempClientFromCal = fromDateId.getClientValue();
		serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE, 
				serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);
		return serverFromDateCal;
		}
		else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red", "TOP");
			return null;
		}
	}
	
	public Calendar getEndDate(MyDatebox toDateId) {
		
		if(toDateId.getValue() != null && !toDateId.getValue().toString().trim().isEmpty()) {
		Calendar serverToDateCal = toDateId.getServerValue();
		Calendar tempClientToCal = toDateId.getClientValue();
		
		
		//change the time for startDate and endDate in order to consider right from the 
		// starting time of startDate to ending time of endDate
		
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 
				23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 
				59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);
		return serverToDateCal;
		}
		else {
			MessageUtil.setMessage("To date cannot be empty.", "color:red", "TOP");
			return null;
		}
		
	}
	
	private boolean isValidate(MyDatebox fromDateId, MyDatebox toDateId) {
		Calendar startDate,endDate;
		if(fromDateId.getValue() != null && !fromDateId.getValue().toString().isEmpty()) {
			startDate = MyCalendar.getNewCalendar();
			startDate.setTime(fromDateId.getValue());
		}
		else {
			MessageUtil.setMessage("From date cannot be empty.", "color:red", "TOP");
			return false;
		}

		if(startDate == null) {
			return false;
		}

		if(toDateId.getValue() != null && !toDateId.getValue().toString().isEmpty()) {
			endDate = MyCalendar.getNewCalendar();
			endDate.setTime(toDateId.getValue());
		}
		else{
			MessageUtil.setMessage("To date cannot be empty.", "color:red", "TOP");
			return false;
		}

		if(endDate == null) {
			return false;
		}
		if(endDate.before(startDate)) {
			MessageUtil.setMessage("To date must be later than From date", "color:red", "TOP");
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(!sdf.format(startDate.getTime()).equals(sdf.format(prgmObj.getCreatedDate().getTime()))){
			if(startDate.before(prgmObj.getCreatedDate())) {
				MessageUtil.setMessage("From date should be after the program creation date.", "color:red", "TOP");
				return false;
			}
		}
		return true;
	}
	
	public void onClick$exportBtnId(){
		try{
			//createWindow();
			
			anchorEvent(true);
			
			custExport.setVisible(true);
			custExport.doHighlighted();
			
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
	}
	
	public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;
			if(i==1) continue;
			tempChk = (Checkbox)chkList.get(i);
			tempChk.setChecked(flag);
			
		} // for

	}
	
	public void onClick$selectAllAnchr$custExport() {
		
		anchorEvent(true);

	}

	public void onClick$clearAllAnchr$custExport() {
		
		anchorEvent(false);
	}
	
	public void onClick$selectFieldBtnId$custExport() {

		custExport.setVisible(false);
		List<Component> chkList = custExport$chkDivId.getChildren();
		boolean checked=false;
		int indexes[]=new int[chkList.size()];
		for(int i=0; i<indexes.length ; i++){
			indexes[i] = -1;
		}
		int i = 0;
		for(Component checkbox:custExport$chkDivId.getChildren()){
			if(((Checkbox)checkbox).isChecked()){
				checked = true;
				indexes[i] = 0;
			}
			i +=1;
		}
		
		if(checked) {

			int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			if(confirm==1){
				try{

					exportCSV((String)exportCbId.getSelectedItem().getValue(), indexes);

				}catch(Exception e){
					logger.error("Exception caught :: ",e);
				}
			}
			else{
				custExport.setVisible(true);
			}

		}
		else {

			MessageUtil.setMessage("Please select atleast one field", "red");
			custExport.setVisible(true);
		}
		
	}
	
private void exportCSV(String type, int indexes[]){
	
	logger.debug("-- just entered --");
	StringBuffer sb = null;
	String userName = GetUser.getUserName();
	String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
	String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
	File downloadDir = new File(exportDir);
	JdbcResultsetHandler jdbcResultsetHandler =  null;
	BufferedWriter bw = null;
	ResultSet rs =null;
	/*if((indexes[0]==0?storeKpisRowsId.getChildren().size() == 0:true) && (indexes[1]==0?storeLiabilityRowsId.getChildren().size() == 0:true)
			&& (indexes[2]==0?enrollRowsId.getChildren().size()==0:true )&&( indexes[3]==0?transRowsId.getChildren().size() == 0:true)){*/
	if((indexes[0]==0?storeKpisRowsId.getChildren().size() == 0:true) && (indexes[1]==0?storeLiabilityRowsId.getChildren().size() == 0:true)
			&& (indexes[2]==0?storeListLBId.getItemCount()==0:true )&&( indexes[3]==0?sbsListLBId.getItemCount() == 0:true)){
		MessageUtil.setMessage("No records found to export", "color:red", "TOP");
		return;
	}
	if(downloadDir.exists()){
		try {
			FileUtils.deleteDirectory(downloadDir);
			logger.debug(downloadDir.getName() + " is deleted");
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			
			logger.debug(downloadDir.getName() + " is not deleted");
		}
	}
	if(!downloadDir.exists()){
		downloadDir.mkdirs();
	}
	
	if(type.contains("csv")){
		
		DecimalFormat f = new DecimalFormat("#0.00");
		String prgmName = prgmObj.getProgramName();
		if(prgmName.contains("/")) {
			
			prgmName = prgmName.replace("/", "_") ;
			
		}
		String filePath = exportDir +  "Loyalty_Store_Report_" + prgmName + "_" +
				MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
				try {
						filePath = filePath + "Store_Report.csv";
						logger.debug("Download File path : " + filePath);
						File file = new File(filePath);
						bw = new BufferedWriter(new FileWriter(filePath));
						int count =0;
						sb =  new StringBuffer();
						
						if(indexes[0] ==0){
							sb.append("Store-level KPIs For Loyalty Customers\n");
							sb.append("\""+"Store"+"\","+"\""+"Visits"+"\","+"\""+"Revenue"+"\"\n");
							if(storeKpisRowsId.getChildren().size() != 0){
							try{
							//	Object[] datearry = getDateValues(timeDurLbId,storeKPIfromDateboxId,storeKPItoDateboxId);
								Object[] datearry = getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
								String query = "SELECT  sales.store_number,COUNT(DISTINCT sales.doc_sid),ROUND(SUM((sales.quantity*sales.sales_price)+ sales.tax -(IF(sales.discount is null,0,sales.discount))),2) " +
										   " FROM  retail_pro_sales sales, (SELECT DISTINCT contact_id FROM contacts_loyalty " +
										   " WHERE user_id = " + userId + " AND program_id = " + prgmId + ") cl  " +
										   " WHERE sales.user_id = " + userId + " AND sales.cid IS NOT NULL AND sales.cid = cl.contact_id" + 
										   " AND sales.sales_date BETWEEN '" + MyCalendar.calendarToString((Calendar) datearry[0], null) + "' AND '"+ MyCalendar.calendarToString((Calendar) datearry[1], null) + "' AND sales.store_number IS NOT NULL  " +
										   " GROUP BY sales.store_number " ;
								
								logger.debug("QRy :: "+query);
								jdbcResultsetHandler = new JdbcResultsetHandler();
								jdbcResultsetHandler.executeStmt(query);
								rs = jdbcResultsetHandler.getResultSet(); 
								long totalVisits =0 ;
								double totalRevenue = 0;
								while(rs.next()){
								if(rs.getString(1) != null){
								String sName = "Store ID "+rs.getString(1);
								 for (OrganizationStores org : storeList){
								if (rs.getString(1).equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
								sName = org.getStoreName().toString();
							     break;
								}}
								 sb.append("\""+sName+"\",");
								}else{
									sb.append("\""+ "" +"\",");
									}
									sb.append("\""+rs.getLong(2)+"\",");
									totalVisits += rs.getLong(2);
									sb.append("\""+f.format(rs.getDouble(3))+"\"\n");
									totalRevenue += rs.getDouble(3);
								}
								sb.append("\""+"Total"+"\","+"\""+totalVisits+"\","+"\""+f.format(totalRevenue)+"\"\n\n");
								bw.write(sb.toString());
								sb = new StringBuffer();
							}catch(Exception e){
								logger.error("Exception :: ",e);
							}finally{
								if(rs !=null)rs.close();rs=null;
								if(jdbcResultsetHandler != null)jdbcResultsetHandler.destroy();
								jdbcResultsetHandler = null;
							}
										
						}
						
						}
						
						if(indexes[1] ==0){
							sb.append("\""+"Store Liability"+"\"\n");
							sb.append("\""+"Store"+"\","+"\""+"Issued"+"\","+"\""+"Redeemed"+"\","+"\""+"Difference"+"\"\n");
							if(storeLiabilityRowsId.getChildren().size() != 0){
								
								List<Object[]> issObjArr = ltyPrgmSevice.getStoresIssLiabilityData(prgmId,userId);
								List<Object[]> redeemObjArr = ltyPrgmSevice.getStoresRedeemLiabilityData(prgmId,userId);
								
								List <Object[]> objArr = null;
								if(issObjArr != null && issObjArr.size() > 0) {
									objArr = new ArrayList<Object[]>();
									for(Object[] obj : issObjArr) {
										String storeNo;
										if(obj[0] != null){
											storeNo = "Store ID "+obj[0].toString() ;
											for (OrganizationStores org : storeList){
											if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
												storeNo = org.getStoreName().toString();
										    	break;
											}}
											}else{
												storeNo = "";
												}
										double earnedAmount = obj[1] == null ? 0.0 : Double.parseDouble(obj[1].toString().trim());
										double convrsnAmount = obj[2] == null ? 0.0 : Double.parseDouble(obj[2].toString().trim());
										double issuedAmount = earnedAmount + convrsnAmount;
										
										Object[] storeObj = new Object[3];
										storeObj[0] = storeNo;
										storeObj[1] = issuedAmount;
										objArr.add(storeObj);
									}
									
								}
								if(redeemObjArr != null && redeemObjArr.size() > 0) {
									
									if(objArr == null) {
										objArr = new ArrayList<Object[]>();
										for(Object[] rdmObj : redeemObjArr) {
											String storeNo;
											if(rdmObj[0] != null){
												storeNo = "Store ID "+rdmObj[0].toString() ;
												for (OrganizationStores org : storeList){
												if (rdmObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
													storeNo = org.getStoreName().toString();
											    	break;
												}}
												}else{
													storeNo = "";
													}
											double redeemAmount = rdmObj[1] == null ? 0.0 : Double.parseDouble(rdmObj[1].toString().trim());
											Object[] storeObj = new Object[3];
											storeObj[0] = storeNo;
											storeObj[2] = redeemAmount;
											objArr.add(storeObj);
										}
									}
									else {
										for(Object[] rdmObj : redeemObjArr) {
											String storeNo;
											if(rdmObj[0] != null){
												storeNo = "Store ID "+rdmObj[0].toString() ;
												for (OrganizationStores org : storeList){
												if (rdmObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
													storeNo = org.getStoreName().toString();
											    	break;
												}}
												}else{
													storeNo = "";
													}
											double redeemAmount = rdmObj[1] == null ? 0.0 : Double.parseDouble(rdmObj[1].toString().trim());
											boolean isExists = false;
											for (Object[] existObj : objArr) {
												if(existObj[0].toString().equalsIgnoreCase(rdmObj[0].toString())) {
													existObj[2] = redeemAmount;
													isExists = true;
													break;
												}
											}
											if(!isExists) {
												Object[] storeObj = new Object[3];
												storeObj[0] = storeNo;
												storeObj[2] = redeemAmount;
												objArr.add(storeObj);
											}
										}
									}
								}
								
								double totIssuance = 0.00; 
								double totRedeem = 0.00; 
								double totDiff = 0.00; 
								
								
								if(objArr != null) {
									for(Object[] object : objArr) {
										sb.append("\"");sb.append(object[0] == null ? "" : object[0]+"");sb.append("\",");
										sb.append("\"");sb.append(object[1] == null ? f.format(0.00): f.format(Double.parseDouble(object[1]+"")));sb.append("\","); 
										totIssuance += (object[1] == null ? 0.0 : Double.parseDouble(object[1]+""));
										sb.append("\"");sb.append(object[2] == null ? f.format(0.00) : f.format(Double.parseDouble(object[2]+"")));sb.append("\","); 
										totRedeem += (object[2] == null ? 0.0 : Double.parseDouble(object[2]+""));
										double difference = (object[1] == null ? 0.0 : Double.parseDouble(object[1]+"")) - (object[2] == null ? 0.0 : Double.parseDouble(object[2]+"")); 
										totDiff += difference;
										sb.append("\"");sb.append(f.format(difference));sb.append("\"\n"); 
										
									}
									
									sb.append("\""+"Total"+"\","+"\""+f.format(totIssuance)+"\","+"\""+f.format(totRedeem)+"\","+"\""+f.format(totDiff)+"\"\n\n");
								}
								bw.write(sb.toString());
								sb =  new StringBuffer();
							}
							
						}
						
						if(indexes[2]==0){
							sb.append("\""+"Store Enrollments"+"\"\n");
							//if(enrollRowsId.getChildren().size() != 0){
							if(storeListLBId.getItemCount() != 0){
								//Object[] arry = getDateValues(enrollDurLbId,fromDateboxId,toDateboxId);
								Object[] arry = getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
								if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
									Row row = null;
									//sb.append("\""+"Store"+"\","+"\""+"No. of Enrollments"+"\"\n");
									sb.append("\""+"Subsidiary"+"\","+"\""+"Store"+"\","+"\""+"No. of Enrollments"+"\"\n");
									List<Object[]> objArr = null;
									objArr =ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
											MyCalendar.calendarToString((Calendar) arry[1], null),true,null);
									long totReg =0;
									if(objArr != null) {
										for(Object[] obj : objArr) {
											if(obj[0] != null){
												String sName = "Store ID "+obj[0].toString() ;
												String sbsName = "Subsidiary ID "+obj[2].toString() ;
												for (OrganizationStores org : storeList){
												/*if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
											    	sName = org.getStoreName().toString();
											    	break;
												}*/
													if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty() &&
															obj[2].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
												    	sName = org.getStoreName().toString();
												    	sbsName = org.getSubsidiaryName().toString();
												    	break;
													}}
												sb.append("\"");sb.append(sbsName);sb.append("\",");
												sb.append("\"");sb.append(sName);sb.append("\",");
												}else{
													sb.append("\"");sb.append("");sb.append("\",");
													sb.append("\"");sb.append("");sb.append("\",");
													}

											sb.append("\"");sb.append(obj[1]==null? 0+"" :obj[1]+"");sb.append("\"\n");
											totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));

										}
										sb.append("\""+"Total"+"\","+"\""+" "+"\","+"\""+totReg+"\"\n\n");
										
									}
								}else {
									String storeNo;
									String sbsNo;
									//sb.append("\""+"Store"+"\","+"\""+"No. of Enrollments"+"\","+"\""+"No. of Gift-Card Issuances"+"\"\n");
									sb.append("\""+"Subsidiary"+"\","+"\""+"Store"+"\","+"\""+"No. of Enrollments"+"\","+"\""+"No. of Gift-Card Issuances"+"\"\n");
									List<Object[]> loyaltyObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
											MyCalendar.calendarToString((Calendar) arry[1], null),false,"loyalty");
									List<Object[]> giftObjArr = ltyPrgmSevice.getStoreContactLtyList(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
											MyCalendar.calendarToString((Calendar) arry[1], null),false,"gift");

									List <Object[]> objArr = null;
									if(loyaltyObjArr != null && loyaltyObjArr.size() > 0) {
										objArr = new ArrayList<Object[]>();
										for(Object[] obj : loyaltyObjArr) {
											/*String storeNo;
											if(obj[0] != null){
												storeNo = "Store ID "+obj[0].toString() ;
												sbsNo = "Subsidiary ID "+obj[2].toString();
												for (OrganizationStores org : storeList){
												if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
													storeNo = org.getStoreName().toString();
											    	break;
												}}
												}else{
													storeNo = "";
													}*/
											
											//Object[] storeObj = new Object[3];
											Object[] storeObj = new Object[4];
											//storeObj[0] = storeNo;
											storeObj[0] = obj[0];
											storeObj[1] = obj[1] == null ? 0+"" :obj[1]+"";
											storeObj[3] = obj[2];
											objArr.add(storeObj);
										}

									}
									if(giftObjArr != null && giftObjArr.size() > 0) {

										if(objArr == null) {
											objArr = new ArrayList<Object[]>();
											for(Object[] gftObj : giftObjArr) {
												/*String storeNo;
												if(gftObj[0] != null){
													sbsNo = "Subsidiary ID "+gftObj[2].toString();
													storeNo = "Store ID "+gftObj[0].toString() ;
													for (OrganizationStores org : storeList){
														if (gftObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()){
														storeNo = org.getStoreName().toString();
												    	break;
													}}
													}else{
														sbsNo = "";
														storeNo = "";
														}*/
												//Object[] storeObj = new Object[3];
												Object[] storeObj = new Object[4];
												//storeObj[0] = storeNo;
												storeObj[0] = gftObj[0];
												storeObj[3] = gftObj[2];
												storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
												objArr.add(storeObj);
											}
										}
										else {
											for(Object[] gftObj : giftObjArr) {
												/*String storeNo;
												if(gftObj[0] != null){
													sbsNo = "Subsidiary ID "+gftObj[2].toString() ;
													storeNo = "Store ID "+gftObj[0].toString() ;
													for (OrganizationStores org : storeList){
													if (gftObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
														storeNo = org.getStoreName().toString();
												    	break;
													}}
													}else{
														sbsNo = "";
														storeNo = "";
														}*/
												boolean isExists = false;
												for (Object[] existObj : objArr) {
//													logger.info("existObj[0]"+existObj[0] +"existObj[1]-------------"+existObj[1]+"existObj[2]"+existObj[2]);
													//if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString())) {
													if(existObj[0].toString().equalsIgnoreCase(gftObj[0].toString()) && existObj[3].toString().equalsIgnoreCase(gftObj[2].toString())) {
														existObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
														isExists = true;
														break;
													}
												}
												if(!isExists) {
													//Object[] storeObj = new Object[3];
													Object[] storeObj = new Object[4];
													//storeObj[0] = storeNo;
													storeObj[0] = gftObj[0];
													storeObj[3] = gftObj[2];
													storeObj[2] = gftObj[1] == null ? 0+"" :gftObj[1]+"";
													objArr.add(storeObj);
												}
											}
										}
									}
									long totReg=0, totGift=0;
									if(objArr != null && objArr.size() > 0) {
										for(Object[] obj : objArr) {
											//if(obj[0] != null){
											if(obj[0] != null && obj[3] != null){
												storeNo = "Store ID "+obj[0].toString() ;
												sbsNo = "Subsidiary ID"+obj[3].toString();
												for (OrganizationStores org : storeList){
												//if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
												if ((obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) &&
													    obj[3].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
													storeNo = org.getStoreName().toString();
													sbsNo = org.getSubsidiaryName().toString();
											    	break;
												}}
												}else{
													storeNo = "";
													sbsNo ="";
													}
											/*sb.append("\"");sb.append(obj[3]==null? "":obj[3]+"");sb.append("\",");
											sb.append("\"");sb.append(obj[0]==null? "":obj[0]+"");sb.append("\",");*/
											sb.append("\"");sb.append(sbsNo);sb.append("\",");
											sb.append("\"");sb.append(storeNo);sb.append("\",");
											sb.append("\"");sb.append(obj[1]==null? 0+"" :obj[1]+"");sb.append("\",");
											sb.append("\"");sb.append(obj[2]==null? 0+"" :obj[2]+"");sb.append("\"\n");
											totReg = totReg + (obj[1] == null ? 0 : Integer.parseInt(obj[1].toString()));
											totGift = totGift + (obj[2] == null ? 0 : Integer.parseInt(obj[2].toString()));

										}

										
									}
									//sb.append("\""+"Total"+"\","+"\""+totReg+"\","+"\""+totGift+"\"\n\n");
									sb.append("\""+"Total"+"\","+"\""+" "+"\","+"\""+totReg+"\","+"\""+totGift+"\"\n\n");
								}
							}
							bw.write(sb.toString());
							sb =  new StringBuffer();
							
						}
						
						if(indexes[3]==0){
							sb.append("\""+"Store Transactions"+"\"\n");
							
							//if(transRowsId.getChildren().size() != 0){
							if(sbsListLBId.getItemCount() != 0){
								if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE)) {
									sb.append("\""+"Subsidiary"+"\","+"\""+"Store"+"\","+"\""+"Enrollment"+"\","+"\""+"Loyalty Issuance"+"\","+"\""+"Redemption"+"\","+"\""+"Inquiry"+"\","+"\""+"Returns"+"\","+"\""+"Store Credit"+"\","+"\""+"Total"+"\"\n");
									//Object[] arry = getDateValues(transDurLbId,transFromDateboxId,transToDateboxId);
									Object[] arry = getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
									
									List<Object[]> objArr = ltyPrgmSevice.getStoresTransData(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
											MyCalendar.calendarToString((Calendar) arry[1], null));
									List <Object[]> storeObjArr = null;
									String sbsNo;
									String storeNo;
									if(objArr != null && objArr.size() > 0) {
										//List <Object[]> storeObjArr = null;
										for(Object[] obj : objArr) {
											//String storeNo;
											/*if(obj[0] != null){
												storeNo = "Store ID "+obj[0].toString() ;
												for (OrganizationStores org : storeList){
												if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
													storeNo = org.getStoreName().toString();
											    	break;
												}}
												}else{
													storeNo = "";
													}*/

											if(storeObjArr == null) {
												storeObjArr = new ArrayList<Object[]>();
												//Object[] storeObj = new Object[8];
												Object[] storeObj = new Object[9];
												storeObj[8] = obj[4];
												storeObj[0] = obj[0];
												if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
													storeObj[1] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
													storeObj[2] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) ){
													storeObj[3] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
													storeObj[4] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
													storeObj[5] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
													storeObj[6] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
													storeObj[7] = obj[2];
												}
												storeObjArr.add(storeObj);
											}
											else {
								
											//}
												boolean isExists = false;
												for (Object[] existObj : storeObjArr) {
													//if(existObj[0].toString().equalsIgnoreCase(obj[0].toString())) {
													if(existObj[0].toString().equalsIgnoreCase(obj[0].toString()) &&
															existObj[8].toString().equalsIgnoreCase(obj[4].toString())) {
														if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
															existObj[1] = obj[2];
														}
														else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
															existObj[2] = obj[2];
														}
														else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)){
															existObj[3] = obj[2];
														}
														else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
															existObj[4] = obj[2];
														}
														else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
																OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
															existObj[5] = obj[2];
														}
														else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
																OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
															existObj[6] = obj[2];
														}
														else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
																OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
															existObj[7] = obj[2];
														}
														isExists = true;
														break;
													}
												}
												if(!isExists) {
													//Object[] storeObj = new Object[8];
													Object[] storeObj = new Object[9];
													storeObj[8] = obj[4];
													storeObj[0] = obj[0];
													if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
														storeObj[1] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
														storeObj[2] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)){
														storeObj[3] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
														storeObj[4] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
														storeObj[5] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
														storeObj[6] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
														storeObj[7] = obj[2];
													}
												   storeObjArr.add(storeObj);
												}
											}

										}
									}
										
									if(storeObjArr != null && storeObjArr.size() > 0) {

										int totEnrollment = 0;
										int totInquiry = 0;
										int totIssuance = 0;
										int totRedemption = 0;
										int totReversal = 0;
										int totStoreCredit = 0;
										int total = 0;
										for (Object[] storeObj : storeObjArr) {

											int rowTot = 0;
											int enrCount = storeObj[1] == null ? 0 : Integer.parseInt(storeObj[1].toString());
											int inqCount = storeObj[2] == null ? 0 : Integer.parseInt(storeObj[2].toString());
											int issCount = storeObj[3] == null ? 0 : Integer.parseInt(storeObj[3].toString());
											int redCount = storeObj[4] == null ? 0 : Integer.parseInt(storeObj[4].toString());
											int revCount = (storeObj[5] == null ? 0 : Integer.parseInt(storeObj[5].toString()))+ 
														   (storeObj[7] == null ? 0 :Integer.parseInt(storeObj[7].toString()));
											int strCrdtCount = storeObj[6] == null ? 0 : Integer.parseInt(storeObj[6].toString());
											rowTot = enrCount + inqCount + issCount + redCount + revCount + strCrdtCount;

											totEnrollment += enrCount;
											totInquiry += inqCount;
											totIssuance += issCount;
											totRedemption += redCount;
											totReversal += revCount;
											totStoreCredit += strCrdtCount;
											total += rowTot;
											//if(storeObj[0] != null){
											if(storeObj[0] != null && storeObj[8] != null){
												sbsNo ="Subsidiary ID "+storeObj[8].toString();
												storeNo = "Store ID "+storeObj[0].toString() ;
												for (OrganizationStores org : storeList){
												//if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
												if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty() &&
														storeObj[8].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
													sbsNo = org.getSubsidiaryName().toString();
													storeNo = org.getStoreName().toString();
											    	break;
												}}
												}else{
													sbsNo = "";
													storeNo = "";
													}

											//sb.append("\"");sb.append(storeObj[0] == null ? "" : storeObj[0]+"");sb.append("\","); //store number
											sb.append("\"");sb.append(storeObj[8] == null ? "" : sbsNo+"");sb.append("\","); //store number
											sb.append("\"");sb.append(storeObj[0] == null ? "" : storeNo+"");sb.append("\","); //store number
											sb.append("\"");sb.append(""+enrCount);sb.append("\","); //enrollment
											sb.append("\"");sb.append(""+issCount);sb.append("\","); //issuance
											sb.append("\"");sb.append(""+redCount);sb.append("\","); //redemption
											sb.append("\"");sb.append(""+inqCount);sb.append("\","); //inquiry
											sb.append("\"");sb.append(""+revCount);sb.append("\","); //reversal
											sb.append("\"");sb.append(""+strCrdtCount);sb.append("\","); //storeCredit
											sb.append("\"");sb.append(""+rowTot);sb.append("\"\n"); //total

										}
										sb.append("\""+"Total"+"\","+"\""+" "+"\","+"\""+totEnrollment+"\","+"\""+totIssuance+"\","+"\""+totRedemption+"\","+"\""+totInquiry+"\","+"\""+totReversal+"\","+"\""+totStoreCredit+"\","+"\""+total+"\"\n\n");
									}
								//}
									bw.write(sb.toString());
								}
								
								else{
									sb.append("\""+"Subsidiary"+"\","+"\""+"Store"+"\","+"\""+"Enrollment"+"\","+"\""+"Loyalty Issuance"+"\","+"\""+"Gift Issuance"+"\","+"\""+"Redemption"+"\","+"\""+"Inquiry"+"\","+"\""+"Returns"+"\","+"\""+"Store Credit"+"\","+"\""+"Transfers"+"\","+"\""+"Total"+"\"\n");
								
								//Object[] arry = getDateValues(transDurLbId,transFromDateboxId,transToDateboxId);
								Object[] arry = getDateValues(dateLBId,storeFromDateId,storeToDateboxId);
								List<Object[]> objArr = ltyPrgmSevice.getStoresTransData(userId, prgmId,MyCalendar.calendarToString((Calendar) arry[0], null),
										MyCalendar.calendarToString((Calendar) arry[1], null));

								List <Object[]> storeObjArr = null;
								String sbsNo;
								String storeNo;
								if(objArr != null && objArr.size() > 0) {
									for(Object[] obj : objArr) {
									//	String storeNo;
										/*if(obj[0] != null){
											storeNo = "Store ID "+obj[0].toString() ;
											for (OrganizationStores org : storeList){
											if (obj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
												storeNo = org.getStoreName().toString();
										    	break;
											}}
											}else{
												storeNo = "";
												}*/

										if(storeObjArr == null) {
											storeObjArr = new ArrayList<Object[]>();
											//Object[] storeObj = new Object[10];
											Object[] storeObj = new Object[11];
											//storeObj[0] = storeNo;
											storeObj[0] = obj[0];
											storeObj[10] = obj[4];
											if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
												storeObj[1] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
												storeObj[2] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)&& 
													OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE.equalsIgnoreCase(obj[3].toString())){
												storeObj[3] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE)&& 
													OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT.equalsIgnoreCase(obj[3].toString())){
												storeObj[4] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
												storeObj[5] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
													 OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
												storeObj[6] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
													OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
												storeObj[7] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
													OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
												storeObj[8] = obj[2];
											}
											else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER)){
												storeObj[9] = obj[2];
											}
											storeObjArr.add(storeObj);
										}
										else {
											boolean isExists = false;
											for (Object[] existObj : storeObjArr) {
												//if(existObj[0].toString().equalsIgnoreCase(obj[0].toString())) {
												if(existObj[0].toString().equalsIgnoreCase(obj[0].toString()) &&
														existObj[10].toString().equalsIgnoreCase(obj[4].toString())) {
													if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
														existObj[1] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
														existObj[2] = obj[2];
													}

													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE.equalsIgnoreCase(obj[3] == null ?"":obj[3].toString())){
														existObj[3] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT.equalsIgnoreCase(obj[3] == null ?"":obj[3].toString())){
														existObj[4] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
														existObj[5] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
														existObj[6] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
														existObj[7] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
															OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
														existObj[8] = obj[2];
													}
													else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER)){
														existObj[9] = obj[2];
													}
													isExists = true;
													break;
												}
											}
											if(!isExists) {
												//Object[] storeObj = new Object[10];
												Object[] storeObj = new Object[11];
												storeObj[0] = obj[0];
												storeObj[10] = obj[4];
												if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT)){
													storeObj[1] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY)){
													storeObj[2] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_PURCHASE.equalsIgnoreCase(obj[3].toString())){
													storeObj[3] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE) &&
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_GIFT.equalsIgnoreCase(obj[3].toString())){
													storeObj[4] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION)){
													storeObj[5] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_ISSUANCE_REVERSAL.equalsIgnoreCase(obj[3].toString())){
													storeObj[6] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_STORE_CREDIT.equalsIgnoreCase(obj[3].toString())){
													storeObj[7] = obj[2];
												}
												else if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_RETURN)&& 
														OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_REDEMPTION_REVERSAL.equalsIgnoreCase(obj[3].toString())){
													storeObj[8] = obj[2];
												}
												if(obj[1].toString().equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_TRANSFER)){
													storeObj[9] = obj[2];
												}
												storeObjArr.add(storeObj);
											}
										}


									}
								}

								if(storeObjArr != null && storeObjArr.size() > 0) {

									int totEnrollment = 0;
									int totInquiry = 0;
									int totIssuance = 0;
									int totGftIssuance = 0;
									int totRedemption = 0;
									int totReversal = 0;
									int totStoreCredit = 0;
									int tottransfer = 0;
									int total = 0;
									for (Object[] storeObj : storeObjArr) {

										int rowTot = 0;
										int enrCount = storeObj[1] == null ? 0 : Integer.parseInt(storeObj[1].toString());
										int inqCount = storeObj[2] == null ? 0 : Integer.parseInt(storeObj[2].toString());
										int issCount = storeObj[3] == null ? 0 : Integer.parseInt(storeObj[3].toString());
										int gftIssCount = storeObj[4] == null ? 0 : Integer.parseInt(storeObj[4].toString());
										int redCount = storeObj[5] == null ? 0 : Integer.parseInt(storeObj[5].toString());
										int revCount = (storeObj[6] == null ? 0 : Integer.parseInt(storeObj[6].toString())) +
													   (storeObj[8] == null ? 0 : Integer.parseInt(storeObj[8].toString()));
										int strCrdtCount = storeObj[7] == null ? 0 : Integer.parseInt(storeObj[7].toString());
										int traCount = storeObj[9] == null ? 0 : Integer.parseInt(storeObj[9].toString());
										rowTot = enrCount + inqCount + issCount + gftIssCount + redCount + revCount + strCrdtCount + traCount;

										totEnrollment += enrCount;
										totInquiry += inqCount;
										totIssuance += issCount;
										totGftIssuance +=gftIssCount;
										totRedemption += redCount;
										totReversal += revCount;
										totStoreCredit += strCrdtCount;
										tottransfer += traCount;
										total += rowTot;
										//if(storeObj[0] != null){
										if(storeObj[0] != null && storeObj[10] != null){
											sbsNo = "Subsidiary ID "+storeObj[10].toString();
											storeNo = "Store ID "+storeObj[0].toString() ;
											for (OrganizationStores org : storeList){
											//if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty()) {
											if (storeObj[0].toString().equalsIgnoreCase(org.getHomeStoreId()) && org.getStoreName() != null && !org.getStoreName().isEmpty() &&
													storeObj[10].toString().equalsIgnoreCase(org.getSubsidiaryId()) && org.getSubsidiaryName() != null && !org.getSubsidiaryName().isEmpty()) {
												sbsNo = org.getSubsidiaryName().toString();
												storeNo = org.getStoreName().toString();
										    	break;
											}}
											}else{
												sbsNo = "";
												storeNo = "";
												}

										//sb.append("\"");sb.append(storeObj[0] == null ? "" : storeObj[0]+"");sb.append("\","); //store number
										sb.append("\"");sb.append(storeObj[10] == null ? "" : sbsNo+"");sb.append("\","); //subsidiary number
										sb.append("\"");sb.append(storeObj[0] == null ? "" : storeNo+"");sb.append("\","); //store number
										sb.append("\"");sb.append(""+enrCount);sb.append("\","); //enrollment
										sb.append("\"");sb.append(""+issCount);sb.append("\","); //issuance
										sb.append("\"");sb.append(""+gftIssCount);sb.append("\","); //giftIssuance
										sb.append("\"");sb.append(""+redCount);sb.append("\","); //redemption
										sb.append("\"");sb.append(""+inqCount); sb.append("\",");//inquiry
										sb.append("\"");sb.append(""+revCount); sb.append("\",");//reversal
										sb.append("\"");sb.append(""+strCrdtCount);sb.append("\","); //storeCredit
										sb.append("\"");sb.append(""+traCount);sb.append("\","); //Transfer
										sb.append("\"");sb.append(""+rowTot);sb.append("\"\n"); //total

									}
									sb.append("\""+"Total"+"\","+"\""+" "+"\","+"\""+totEnrollment+"\","+"\""+totIssuance+"\","+"\""+totGftIssuance+"\","+"\""+totRedemption+"\","+"\""+totInquiry+"\","+"\""+totReversal+"\","+"\""+totStoreCredit+"\","+"\""+tottransfer+"\","+"\""+total+"\"\n\n");
								}
						
							bw.write(sb.toString());
							
						}
							}
						}
						bw.flush();
						bw.close();
						Filedownload.save(file, "text/csv");
						
				}catch(Exception e){
					logger.error("Exception :: ",e);
				}finally{
					sb = null;
					bw = null;
					userName = null;
					usersParentDirectory = null;
					exportDir = null ;
					downloadDir = null;
				}
	}
}

}

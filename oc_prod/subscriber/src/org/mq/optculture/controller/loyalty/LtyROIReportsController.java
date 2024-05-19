
package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.data.dao.JdbcResultsetHandler;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.SpecialRewardsDao;
import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
import org.mq.optculture.data.dao.ValueCodesDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.PagingEvent;

public class LtyROIReportsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Users user;
	private Listbox rewardROILbId,pageSizeLbId,searchbyLbId;
	private Combobox exportCbId;
	private Textbox searchByValueCodeTbId;
	private Session session ;
	private Paging rewardROIPagingId;
	private LoyaltyTransactionChildDao loyaltytransactionChildDao;
	private LoyaltyBalanceDao loyaltyBalanceDao;
	TimeZone clientTimeZone ;
	
	public LtyROIReportsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Revenue on Issuance Report", Constants.STRING_NILL, style, true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	loyaltytransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
	user = GetUser.getUserObj();
	session =Sessions.getCurrent();
    clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	int totalSize=loyaltytransactionChildDao.getTotalCountByValueCode(user.getUserId(),null);
	int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
	rewardROIPagingId.setTotalSize(totalSize);
	rewardROIPagingId.setPageSize(pageSize);
	rewardROIPagingId.addEventListener("onPaging", this); 
	rewardROIPagingId.setActivePage(0);
	fetchValueCodes(0, rewardROIPagingId.getPageSize(),null);
	redraw(0, rewardROIPagingId.getPageSize(),null);
	}
	public void onSelect$pageSizeLbId(){
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());	
		rewardROIPagingId.setPageSize(pageSize);
		rewardROIPagingId.addEventListener("onPaging", this); 
		redraw(0, rewardROIPagingId.getPageSize(),null);
	}
	/*public void onSelect$searchbyLbId() {
		if(!searchbyLbId.getSelectedItem().getValue().equals("value_code")) {
			searchByValueCodeTbId.setVisible(false);
		}
	}*/
	public void onClick$filterBtnId(){
		int totalSize=loyaltytransactionChildDao.getTotalCountByValueCode(user.getUserId(),searchbyLbId.getSelectedItem().getValue());
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardROIPagingId.setTotalSize(totalSize);
		rewardROIPagingId.setPageSize(pageSize);
		rewardROIPagingId.setActivePage(0);
		rewardROIPagingId.addEventListener("onPaging", this); 
		redraw(rewardROIPagingId.getActivePage()*rewardROIPagingId.getPageSize(), rewardROIPagingId.getPageSize(),
				searchbyLbId.getSelectedItem().getValue());
	}//onClick$filterBtnId()
	public void onClick$resetAnchId(){
		/*Listitem li=new Listitem();
		li.setValue("All");
		li.setParent(searchbyLbId);
		searchbyLbId.setSelectedItem(li);*/
		int totalSize=loyaltytransactionChildDao.getTotalCountByValueCode(user.getUserId(),null);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardROIPagingId.setTotalSize(totalSize);
		rewardROIPagingId.setPageSize(pageSize);
		rewardROIPagingId.setActivePage(0);
		rewardROIPagingId.addEventListener("onPaging", this);
		fetchValueCodes(rewardROIPagingId.getActivePage()*rewardROIPagingId.getPageSize(), rewardROIPagingId.getPageSize(),null);
		redraw(rewardROIPagingId.getActivePage()*rewardROIPagingId.getPageSize(), rewardROIPagingId.getPageSize(),null);
	}//onClick$resetAnchId()

	
	public void redraw(int startIdx,int size,String valueCode) {
		
		List<LoyaltyTransactionChild> valueCodeList=loyaltytransactionChildDao.findTransactionsByValueCode(user.getUserId(),
				startIdx,size,orderby_colName,desc_Asc,valueCode);
			int count=rewardROILbId.getItemCount();
			while(count>0){
				rewardROILbId.removeItemAt(--count);	
			}
	
		for (LoyaltyTransactionChild trxChild : valueCodeList) {

			Listcell lc;
			Label tempLabel;
			Listitem li;
			li = new Listitem();

			lc = new Listcell();
			tempLabel = new Label(trxChild.getEarnType());
			tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
			tempLabel.addEventListener("onClick", this);
			tempLabel.setParent(lc);
			lc.setParent(li);
			
			//if(trxChild.getEarnType().equalsIgnoreCase("Points") || trxChild.getEarnType().equalsIgnoreCase("Amount")) {
				
				Object issuedobj[]= new Object[]{0,0};
				List<Object[]> list=loyaltytransactionChildDao.getltyTransactionByValueCode(trxChild.getEarnType(),trxChild.getUserId(),"Issued");
				if(list.size()>0)
					issuedobj=list.get(0);
				Object liabilityobj[]= new Object[]{0,0};
				List<Object[]> list1=loyaltytransactionChildDao.getltyTransactionByValueCode(trxChild.getEarnType(),trxChild.getUserId(),"Liability");
				if(list1.size()>0)
					liabilityobj=list1.get(0);
				
				lc = new Listcell(issuedobj[0].toString());
				lc.setParent(li);
				
				lc = new Listcell(liabilityobj[1].toString());
				lc.setParent(li);
				
			/*}else {
				Object balobj[]= new Object[]{0,0};
				List<Object[]> ltyBal=loyaltyBalanceDao.getLiabilityByValueCode(trxChild.getEarnType(),trxChild.getUserId());
				if(ltyBal.size()>0)
				balobj=ltyBal.get(0);
				
				lc = new Listcell(balobj[0].toString());
				lc.setParent(li);
				
				lc = new Listcell(balobj[1].toString());
				lc.setParent(li);
			}*/
			Object revenueobj[]= new Object[]{0};
			List<Object[]> revenuelist=loyaltytransactionChildDao.getRevenueByValueCode(trxChild.getEarnType(),trxChild.getUserId());
			if(revenuelist.size()>0)
			revenueobj=revenuelist.get(0);
			
			lc = new Listcell(revenueobj[0].toString());
			lc.setParent(li);
			
			li.setParent(rewardROILbId);
			li.setValue(trxChild);

		}

	}
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if(event.getTarget() instanceof Paging) {

			Paging paging = (Paging)event.getTarget();
			int desiredPage = paging.getActivePage();
			this.rewardROIPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize(),null);
		}else if(event.getTarget() instanceof Label) {
			
			Label tempLabel = (Label)event.getTarget();
			Listcell listcell = (Listcell)tempLabel.getParent();
			Listitem tempRow = (Listitem)listcell.getParent();
			
			if(tempRow.getValue()!=null){
			
			LoyaltyTransactionChild trxObj = (LoyaltyTransactionChild)tempRow.getValue();
			
			if(trxObj != null){
				
				session.setAttribute("SPECIAL_REWARD_ROI_DETAILS", trxObj);
			}
			}else{
				//String store = ""+tempLable.getAttribute("original value");
				session.removeAttribute("SPECIAL_REWARD_ROI_DETAILS");
				//session.setAttribute("STORE_REDEEMED_DETAILS", store);
			}
			Redirect.goTo(PageListEnum.LOYALTY_DETAILED_ROI_REPORTS);
			
			}
	  
	}
	
	public void onClick$exportBtnId() {
		if(exportCbId.getSelectedItem().getValue().equals(".csv")) {
			exportCsv();
		}else if(exportCbId.getSelectedItem().getValue().equals(".xls")) {
			exportExcel();
		}
	}
	public void exportCsv() {
		JdbcResultsetHandler jdbcResultsetHandler = null;
		StringBuffer sb = null;
		BufferedWriter bw = null;
		ResultSet resultSet = null;
		try {
			if (rewardROILbId.getChildren().size() == 0) {
				MessageUtil.setMessage("No records exist in the selected search.", "color:red", "TOP");
				return;
			}

			String ext = "csv";
			String userName = user.getUserName();

			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Loyalty_ROI_Report_"
					+ System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append(
					"\"Value Code\",\"Issued Value\",\"Liability\",\"Revenue\"\n");
			bw.write(sb.toString());
			
			int totalSize=loyaltytransactionChildDao.getTotalCountByValueCode(user.getUserId(),null);
			List<LoyaltyTransactionChild> valueCodeList=loyaltytransactionChildDao.findTransactionsByValueCode(user.getUserId(),
					0, totalSize,orderby_colName,desc_Asc,searchbyLbId.getSelectedItem().getValue());
			for(LoyaltyTransactionChild vc : valueCodeList) {
				sb.setLength(0);
				
				sb.append("\"" + vc.getEarnType() + "\",");
				/*if(vc.getEarnType().equalsIgnoreCase("Points") || vc.getEarnType().equalsIgnoreCase("Amount")) {
					
				Object obj[]= new Object[]{0,0};
				List<Object[]> list=loyaltytransactionChildDao.getltyTransactionByValueCode(vc.getEarnType(),user.getUserId());
				if(list.size()>0) obj=list.get(0);
				sb.append("\"" + obj[0].toString() + "\",");
				sb.append("\"" + obj[1].toString() + "\",");
				}else {
					Object balobj[]= new Object[]{0,0};
					List<Object[]> ltyBal=loyaltyBalanceDao.getLiabilityByValueCode(vc.getEarnType(),user.getUserId());
					if(ltyBal.size()>0)
					balobj=ltyBal.get(0);
					sb.append("\"" + balobj[0].toString() + "\",");
					sb.append("\"" + balobj[1].toString() + "\",");
				}*/
					Object issuedobj[]= new Object[]{0,0};
					List<Object[]> list=loyaltytransactionChildDao.getltyTransactionByValueCode(vc.getEarnType(),vc.getUserId(),"Issued");
					if(list.size()>0)
						issuedobj=list.get(0);
					Object liabilityobj[]= new Object[]{0,0};
					List<Object[]> list1=loyaltytransactionChildDao.getltyTransactionByValueCode(vc.getEarnType(),vc.getUserId(),"Liability");
					if(list1.size()>0)
						liabilityobj=list1.get(0);
					sb.append("\"" + issuedobj[0].toString() + "\",");
					sb.append("\"" + liabilityobj[1].toString() + "\",");
				Object revenueobj[]= new Object[]{0};
				List<Object[]> revenuelist=loyaltytransactionChildDao.getRevenueByValueCode(vc.getEarnType(),vc.getUserId());
				if(revenuelist.size()>0)
				revenueobj=revenuelist.get(0);
				sb.append("\"" + revenueobj[0].toString() + "\",");
				sb.append("\n");

				bw.write(sb.toString());
			}
			bw.flush();
			bw.close();

			Filedownload.save(file, "text/csv");

		} catch (Exception e) {
			logger.error("Exception :: ", e);
		} finally {
			if (jdbcResultsetHandler != null)
				jdbcResultsetHandler.destroy();
			jdbcResultsetHandler = null;
			sb = null;
			bw = null;
		}
		}
	public void exportExcel() {
		try {
			String userName = GetUser.getUserName();
			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Loyalty_ROI_Report_"
					+ System.currentTimeMillis() + "." + exportCbId.getSelectedItem().getValue();
			File file = new File(filePath);
			logger.debug("Writing to the file : " + filePath);
			FileOutputStream fileOut = new FileOutputStream(filePath);
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Loyalty ROI Report");
			HSSFRow row = sheet.createRow((short) 0);
			HSSFCell cell = null;
			int count=loyaltytransactionChildDao.getTotalCountByValueCode(user.getUserId(),searchbyLbId.getSelectedItem().getValue());
		
			if (count == 0) {
				Messagebox.show("No report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				return;
		      }
			
			row = sheet.createRow(0);
			int cellNo = 0;
				cell = row.createCell((cellNo++));
				cell.setCellValue("Value Code");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Issued Value");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Liability");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Revenue");
				
				List<LoyaltyTransactionChild> valueCodeList=null;
				int size = count;
				for (int i = 0; i < count; i += size) {
				valueCodeList=loyaltytransactionChildDao.findTransactionsByValueCode(user.getUserId(),
						0, count,orderby_colName,desc_Asc,searchbyLbId.getSelectedItem().getValue());
				int rowId = 1;
				for(LoyaltyTransactionChild vc : valueCodeList) {
					row = sheet.createRow(rowId++);
					int columnId = 0;
					cell = null;
					
					cell = row.createCell(columnId++);
					cell.setCellValue(vc.getEarnType());
					
					//if(vc.getEarnType().equalsIgnoreCase("Points") || vc.getEarnType().equalsIgnoreCase("Amount")) {
						
					Object issuedobj[]= new Object[]{0,0};
					List<Object[]> list=loyaltytransactionChildDao.getltyTransactionByValueCode(vc.getEarnType(),vc.getUserId(),"Issued");
					if(list.size()>0)
						issuedobj=list.get(0);
					Object liabilityobj[]= new Object[]{0,0};
					List<Object[]> list1=loyaltytransactionChildDao.getltyTransactionByValueCode(vc.getEarnType(),vc.getUserId(),"Liability");
					if(list1.size()>0)
						liabilityobj=list1.get(0);
					cell = row.createCell(columnId++);
					cell.setCellValue(issuedobj[0].toString());
					
					cell = row.createCell(columnId++);
					cell.setCellValue(liabilityobj[1].toString());
					
						
					/*}else {
						Object balobj[]= new Object[]{0,0};
						List<Object[]> ltyBal=loyaltyBalanceDao.getLiabilityByValueCode(vc.getEarnType(),user.getUserId());
						if(ltyBal.size()>0)
						balobj=ltyBal.get(0);
						
						cell = row.createCell(columnId++);
						cell.setCellValue(balobj[0].toString());
						
						cell = row.createCell(columnId++);
						cell.setCellValue(balobj[1].toString());
					}*/
					Object revenueobj[]= new Object[]{0};
					List<Object[]> revenuelist=loyaltytransactionChildDao.getRevenueByValueCode(vc.getEarnType(),vc.getUserId());
					if(revenuelist.size()>0)
					revenueobj=revenuelist.get(0);
					
					cell = row.createCell(columnId++);
					cell.setCellValue(revenueobj[0].toString());
				}
			}
				
				hwb.write(fileOut);
				fileOut.flush();
				fileOut.close();
			Filedownload.save(file, "application/vnd.ms-excel");
			logger.debug("exited");

		} catch (Exception e) {
			logger.error("** Exception : ", e);
		}

	}
	public void fetchValueCodes(int startIdx,int size,String valueCode) {
		int count=searchbyLbId.getItemCount();
		while(count>0){
			searchbyLbId.removeItemAt(--count);	
		}
		Listitem li = null;
		li = new Listitem("All");
		li.setParent(searchbyLbId);
		li.setValue(null);
		li.setSelected(true);
		List<String> valueCodeList=loyaltytransactionChildDao.getTotalValueCodes(user.getUserId());
		for (String trx : valueCodeList) {
			li = new Listitem(trx);
			li.setParent(searchbyLbId);
			li.setValue(trx);
		}
	}
	public String orderby_colName="createdDate",desc_Asc="desc";
	public void desc2ascasc2desc()
    {
    	if(desc_Asc=="desc")
			desc_Asc="asc";
		else
			desc_Asc="desc";
	
    }
}

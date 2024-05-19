
package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
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
import org.zkoss.zul.event.PagingEvent;

public class RewardsReportsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SpecialRewardsDao specialRewardsDao;
	private LoyaltyTransactionChildDao loyaltytransactionChildDao;
	private Users user;
	private Listbox spRewardLbId,pageSizeLbId,progStatusLb;
	private Combobox exportCbId;
	private Session session ;
	private Paging rewardPagingId;
	private SpecialRewardsDaoForDML specialRewardsDaoForDML;
	TimeZone clientTimeZone ;
	
	public RewardsReportsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Rewards Reports", Constants.STRING_NILL, style, true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	specialRewardsDao = (SpecialRewardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
	specialRewardsDaoForDML = (SpecialRewardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
	loyaltytransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	user = GetUser.getUserObj();
	session =Sessions.getCurrent();
    clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	int totalSize=specialRewardsDao.findCountByStatus(user.getUserId(),null);
	int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
	rewardPagingId.setTotalSize(totalSize);
	rewardPagingId.setPageSize(pageSize);
	rewardPagingId.addEventListener("onPaging", this); 
	rewardPagingId.setActivePage(0);
	redraw(0, rewardPagingId.getPageSize());
	}
	public void onSelect$pageSizeLbId(){
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());	
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.addEventListener("onPaging", this); 
		redraw(0, rewardPagingId.getPageSize());
	}
	public String getSelectedStatus(){
		int index = progStatusLb.getSelectedIndex();
		String status = "";
		if (index != -1)
			status = progStatusLb.getSelectedItem().getValue();
		return status;
	}//getSelectedStatus()
	public void onClick$filterBtnId(){
		int totalSize=specialRewardsDao.findCountByStatus(user.getUserId(), getSelectedStatus());
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this); 
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$filterBtnId()
	public void onClick$resetAnchId(){
		progStatusLb.setSelectedIndex(0);
		int totalSize=specialRewardsDao.findCountByStatus(user.getUserId(), getSelectedStatus());
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this);
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$resetAnchId()

	public void onClick$createSegmentTBarBtnId(){
		Redirect.goTo(PageListEnum.LOYALTY_SPECIAL_REWARDS);
	}
	
	public void redraw(int startIdx,int size) {
		
		List<SpecialReward>  specialRewardList=specialRewardsDao.findSpecialRewardsByUserId(user.getUserId(),getSelectedStatus(),startIdx,size);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			int count=spRewardLbId.getItemCount();
			while(count>0){
			spRewardLbId.removeItemAt(--count);	
			}
	
		for (SpecialReward specialReward : specialRewardList) {

			Listcell lc;
			Label tempLabel;
			Listitem li;
			li = new Listitem();

			lc = new Listcell();
			tempLabel = new Label(specialReward.getRewardName());
			tempLabel.setStyle("cursor:pointer;color:blue;text-decoration: underline;");
			tempLabel.addEventListener("onClick", this);
			tempLabel.setParent(lc);
			lc.setParent(li);

			lc = new Listcell(MyCalendar.calendarToString(specialReward.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone));
			lc.setParent(li);
			String rewardMsg="";
			if(specialReward.getRewardType()!=null){
			if(specialReward.getRewardType().equals("M"))
				rewardMsg=specialReward.getRewardValue()+" X Multiplier Reward";
			else
				rewardMsg=	specialReward.getRewardValue()+" of Value-code "+specialReward.getRewardValueCode();
			}
		
			lc = new Listcell(rewardMsg);
			lc.setParent(li);
			
			lc = new Listcell(specialReward.getStatusSpecialReward());
			lc.setParent(li);
			
			Object obj[]= new Object[]{0,0,0};
			List<Object[]> list=loyaltytransactionChildDao.getltyTransactionBysprewarid(Long.parseLong(specialReward.getCreatedBy()),specialReward.getRewardId());
			if(list.size()>0)
			obj=list.get(0);
			
			lc = new Listcell(obj[0].toString());
			lc.setParent(li);
			
			lc = new Listcell(obj[1].toString());
			lc.setParent(li);
			
			lc = new Listcell(obj[2].toString());
			lc.setParent(li);
			
			li.setParent(spRewardLbId);
			li.setValue(specialReward);

		}

	}
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		if(event.getTarget() instanceof Image) {
			Image img =(Image)event.getTarget();
			String action = (String)img.getAttribute("Type");
			Listitem lcImg=(Listitem) img.getParent().getParent().getParent();
			SpecialReward specialReward=lcImg.getValue();
			if(action.equals("delete")){
				try {
					int confirm = Messagebox.show("Are you sure you want to delete the Reward?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm != 1){
						return ;
					}
					
				} catch (Exception e) {
					logger.error("Exception ::", e);
					return;
				}	
				specialRewardsDaoForDML.deleteProgramIdsByrewardId(specialReward.getRewardId());
				specialRewardsDaoForDML.delete(specialReward);
				//spRewardLbId.removeChild(lcImg);
				int totalSize=specialRewardsDao.findCountByStatus(user.getUserId(),getSelectedStatus());
				rewardPagingId.setTotalSize(totalSize);
				rewardPagingId.addEventListener("onPaging", this); 
				redraw(0, rewardPagingId.getPageSize());
				MessageUtil.setMessage("Reward deleted successfully.", "color:green;");
			}
			else if(action.equals("edit")){
				session.setAttribute("editRewardRule", specialReward);
				Redirect.goTo(PageListEnum.LOYALTY_SPECIAL_REWARDS);
			}
			else if(action.equals("Status")) {
				if(specialReward.getRewardValueCode()!=null){
				if(specialReward.getStatusSpecialReward()!=null && (specialReward.getStatusSpecialReward().equalsIgnoreCase("Active"))) {
					int confirm = Messagebox.show("Are you sure you want to  suspend the Reward?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm !=1){
						return ;
					}
					 img.setSrc("/img/play_icn.png");
					 img.setTooltiptext("Activate");
					 specialReward.setStatusSpecialReward("Suspended");
					 
				}
				else {
					int confirm = Messagebox.show("Are you sure you want to activate the Reward?", " Special Reward",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm !=1){
						return ;
					}
					 img.setSrc("/images/loyalty/suspend.png");
					 img.setTooltiptext("Suspend");
					 specialReward.setStatusSpecialReward("Active");
				}
				specialRewardsDaoForDML.saveOrUpdate(specialReward);
				redraw(0, rewardPagingId.getPageSize());
			}
			else{
				MessageUtil.setMessage("No Reward Type found, Please first provide Reward Type.", "color:red", "TOP");
				return;
			}
		  }
		}
		else if(event.getTarget() instanceof Paging) {

			Paging paging = (Paging)event.getTarget();
			int desiredPage = paging.getActivePage();
			this.rewardPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
		}else if(event.getTarget() instanceof Label) {
			
			Label tempLabel = (Label)event.getTarget();
			Listcell listcell = (Listcell)tempLabel.getParent();
			Listitem tempRow = (Listitem)listcell.getParent();
			
			if(tempRow.getValue()!=null){
			
			SpecialReward specialRewardObj = (SpecialReward)tempRow.getValue();
			
			if(specialRewardObj != null){
				
				session.setAttribute("SPECIAL_REWARD_DETAILS", specialRewardObj);
			}
			}else{
				//String store = ""+tempLable.getAttribute("original value");
				session.removeAttribute("SPECIAL_REWARD_DETAILS");
				//session.setAttribute("STORE_REDEEMED_DETAILS", store);
			}
			Redirect.goTo(PageListEnum.LOYALTY_DETAILED_REWARDS_REPORTS);
			
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
		if (spRewardLbId.getChildren().size() == 0) {
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

		String filePath = usersParentDirectory + "/" + userName + "/List/download/Special_Reward_Report_"
				+ System.currentTimeMillis() + "." + ext;

		sb = new StringBuffer();
		File file = new File(filePath);
		bw = new BufferedWriter(new FileWriter(file));
		sb.append(
				"\"Special Reward Name\",\"Created On\",\"Reward\",\"Status\",\"No.of times Issued\",\"Total Reward Issued\",\"Total Revenue\"\n");
		bw.write(sb.toString());

		int totalSize=specialRewardsDao.findCountByStatus(user.getUserId(), getSelectedStatus());
		
		String sbquery="";
		/*if(getSelectedStatus()!=null && getSelectedStatus().length()>0)
			sbquery=" AND statusSpecialReward like'"+getSelectedStatus()+"'";
		String query="FROM SpecialReward WHERE createdBy ="+user.getUserId()+sbquery+" order by createdDate desc";*/
		List<SpecialReward>  specialExportRewardList=specialRewardsDao.findSpecialRewardsByUserId(user.getUserId(),getSelectedStatus(),
				0,totalSize);
		
		/*jdbcResultsetHandler = new JdbcResultsetHandler();
		jdbcResultsetHandler.executeStmt(query);
		resultSet = jdbcResultsetHandler.getResultSet();*/
		//while (resultSet.next()) {
		for(SpecialReward sp : specialExportRewardList) {
			sb.setLength(0);
			
			sb.append("\"" + sp.getRewardName() + "\",");
			sb.append("\"" + MyCalendar.calendarToString(sp.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone)
					+ "\",");
			String rewardMsg="";
			if(sp.getRewardType()!=null){
				if(sp.getRewardType().equals("M"))
					rewardMsg=sp.getRewardValue()+" X Multiplier Reward";
				else
					rewardMsg=sp.getRewardValue()+" of Value-code "+sp.getRewardValueCode();
				}
			sb.append("\"" + rewardMsg + "\",");
			sb.append("\"" + sp.getStatusSpecialReward() + "\",");
			Object obj[]= new Object[]{0,0,0};
			List<Object[]> list=loyaltytransactionChildDao.getltyTransactionBysprewarid(Long.parseLong(sp.getCreatedBy()),sp.getRewardId());
			if(list.size()>0)
			obj=list.get(0);
			sb.append("\"" + obj[0].toString() + "\",");
			sb.append("\"" + obj[1].toString() + "\",");
			sb.append("\"" + obj[2].toString() + "\",");
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

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Special_Reward_Report_"
					+ System.currentTimeMillis() + "." + exportCbId.getSelectedItem().getValue();
			File file = new File(filePath);
			logger.debug("Writing to the file : " + filePath);
			FileOutputStream fileOut = new FileOutputStream(filePath);
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Special Reward Report");
			HSSFRow row = sheet.createRow((short) 0);
			HSSFCell cell = null;
			List<LoyaltyProgramTier> tiersList =null;
			String subQuery = Constants.STRING_NILL;
			
			int count=specialRewardsDao.findCountByStatus(user.getUserId(), getSelectedStatus());
			
			String sbquery="";
			List<SpecialReward>  specialExportRewardList=specialRewardsDao.findSpecialRewardsByUserId(user.getUserId(),getSelectedStatus(),
					0,count);
		
			if (count == 0) {
				Messagebox.show("No report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				return;
		      }
			row = sheet.createRow(0);
			int cellNo = 0;
				cell = row.createCell((cellNo++));
				cell.setCellValue("Special Reward Name");
			
				cell = row.createCell((cellNo++));
				cell.setCellValue("Created on");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Reward");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Status");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("No. of times Issued");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Total Reward Issued");
				
				cell = row.createCell((cellNo++));
				cell.setCellValue("Total Revenue");
				
				List<Object[]> list=null;
				
				int size = count;
				for (int i = 0; i < count; i += size) {
					
					
				int rowId = 1;
				for (SpecialReward sp : specialExportRewardList) {
					row = sheet.createRow(rowId++);
					int columnId = 0;
					cell = null;
					
					cell = row.createCell(columnId++);
					cell.setCellValue(sp.getRewardName());
					
					cell = row.createCell(columnId++);
					cell.setCellValue(MyCalendar.calendarToString(sp.getCreatedDate(), MyCalendar.FORMAT_DATEONLY_GENERAL,clientTimeZone));
					
					cell = row.createCell(columnId++);
					String rewardMsg="";
					if(sp.getRewardType()!=null){
						if(sp.getRewardType().equals("M"))
							rewardMsg=sp.getRewardValue()+" X Multiplier Reward";
						else
							rewardMsg=sp.getRewardValue()+" of Value-code "+sp.getRewardValueCode();
						}
					cell.setCellValue(rewardMsg);
					
					cell = row.createCell(columnId++);
					cell.setCellValue(sp.getStatusSpecialReward());
					
					Object obj[]= new Object[]{0,0,0};
					list=loyaltytransactionChildDao.getltyTransactionBysprewarid(Long.parseLong(sp.getCreatedBy()),sp.getRewardId());
					if(list.size()>0)
					obj=list.get(0);
					cell = row.createCell(columnId++);
					cell.setCellValue(obj[0].toString());
					
					cell = row.createCell(columnId++);
					cell.setCellValue(obj[1].toString());
					
					cell = row.createCell(columnId++);
					cell.setCellValue(obj[2].toString());
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


}

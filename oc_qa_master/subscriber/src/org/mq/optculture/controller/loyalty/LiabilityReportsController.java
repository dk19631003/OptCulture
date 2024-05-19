package org.mq.optculture.controller.loyalty;

	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileOutputStream;
	import java.io.FileWriter;
	import java.sql.ResultSet;
	import java.text.DateFormat;
	import java.text.SimpleDateFormat;
	import java.util.Calendar;
	import java.util.HashMap;
	import java.util.LinkedHashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.TimeZone;

	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;
	import org.apache.poi.hssf.usermodel.HSSFCell;
	import org.apache.poi.hssf.usermodel.HSSFRow;
	import org.apache.poi.hssf.usermodel.HSSFSheet;
	import org.apache.poi.hssf.usermodel.HSSFWorkbook;
	import org.mq.marketer.campaign.beans.ContactsLoyalty;
	import org.mq.marketer.campaign.beans.Coupons;
	import org.mq.marketer.campaign.beans.LoyaltyProgram;
	import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
	import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
	import org.mq.marketer.campaign.beans.OrganizationStores;
	import org.mq.marketer.campaign.beans.SpecialReward;
	import org.mq.marketer.campaign.beans.Users;
	import org.mq.marketer.campaign.beans.ValueCodes;
	import org.mq.marketer.campaign.controller.GetUser;
	import org.mq.marketer.campaign.custom.MyCalendar;
	import org.mq.marketer.campaign.custom.MyDatebox;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.LoyaltyBalanceDao;
	import org.mq.marketer.campaign.dao.OrganizationStoresDao;
	import org.mq.marketer.campaign.general.Constants;
	import org.mq.marketer.campaign.general.MessageUtil;
	import org.mq.marketer.campaign.general.PageListEnum;
	import org.mq.marketer.campaign.general.PageUtil;
	import org.mq.marketer.campaign.general.PropertyUtil;
	import org.mq.marketer.campaign.general.Redirect;
	import org.mq.optculture.business.loyalty.LoyaltyProgramService;
	import org.mq.optculture.data.dao.JdbcResultsetHandler;
	import org.mq.optculture.data.dao.LoyaltyProgramDao;
	import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
	import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
	import org.mq.optculture.data.dao.SpecialRewardsDao;
	import org.mq.optculture.data.dao.SpecialRewardsDaoForDML;
	import org.mq.optculture.data.dao.ValueCodesDao;
	import org.mq.optculture.exception.LoyaltyProgramException;
	import org.mq.optculture.utils.OCConstants;
	import org.mq.optculture.utils.ServiceLocator;
	import org.zkoss.zk.ui.Component;
	import org.zkoss.zk.ui.Components;
	import org.zkoss.zk.ui.Session;
	import org.zkoss.zk.ui.Sessions;
	import org.zkoss.zk.ui.event.Event;
	import org.zkoss.zk.ui.util.GenericForwardComposer;
	import org.zkoss.zkplus.spring.SpringUtil;
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

	public class LiabilityReportsController extends GenericForwardComposer {
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
		private LoyaltyTransactionChildDao loyaltytransactionChildDao;
		private LoyaltyProgramDao loyaltyProgramDao;
		private LoyaltyProgramTierDao loyaltyProgramTierDao;
		private ContactsLoyaltyDao contactsLoyaltyDao;
		private Users user;
		private Listbox lbReportsLbId,pageSizeLbId,searchbyLbId,programLb;
		private Div searchByProgramDivId;
		private Session session ;
		private Combobox exportCbId;
		private Paging lbReportPagingId;
		private MyDatebox fromDateboxId,toDateboxId;
		private String fromDateStr,toDateStr;
		TimeZone clientTimeZone ;
		private  static String ITALIC_GREY_STYLE = "color:grey;font-style: italic;font-size:12px;";
		private final String SEARCH_BY_PROGRAM = "program_id";
		private final String SEARCH_BY_DATE = "created_date";

		
		public LiabilityReportsController() {
			String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
			PageUtil.setHeader("Liability Reports", Constants.STRING_NILL, style, true);
		}
		@Override
		public void doAfterCompose(Component comp) throws Exception {
			super.doAfterCompose(comp);
		loyaltytransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
		loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
		loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
		contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		user = GetUser.getUserObj();
		session =Sessions.getCurrent();
	    clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
	    //searchByDateDivId.setVisible(true);
	    onSelect$searchbyLbId();
		int totalSize=loyaltyProgramTierDao.findLiabilityCountByProgram(user.getUserId(),Constants.STRING_NILL);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		lbReportPagingId.setTotalSize(totalSize);
		lbReportPagingId.setPageSize(pageSize);
		lbReportPagingId.addEventListener("onPaging", this); 
		lbReportPagingId.setActivePage(0);
		redraw(0, lbReportPagingId.getPageSize());
		}
		public void onSelect$pageSizeLbId(){
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());	
			lbReportPagingId.setPageSize(pageSize);
			lbReportPagingId.addEventListener("onPaging", this); 
			redraw(0, lbReportPagingId.getPageSize());
		}
		public void onClick$filterBtnId(){
			String subQuery = Constants.STRING_NILL;
			
			if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
				if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					subQuery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
				}
			}
			/*if(searchByDateDivId.isVisible()){  
				boolean status = validateSetCreationDate();
				if(status == false) return;
				subQuery = "AND createdDate between '"+fromDateStr+"' AND '"+toDateStr+"' ";
			}*/
			int totalSize=loyaltyProgramTierDao.findLiabilityCountByProgram(user.getUserId(),subQuery );
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			lbReportPagingId.setTotalSize(totalSize);
			lbReportPagingId.setPageSize(pageSize);
			lbReportPagingId.setActivePage(0);
			lbReportPagingId.addEventListener("onPaging", this); 
			redraw(lbReportPagingId.getActivePage()*lbReportPagingId.getPageSize(), lbReportPagingId.getPageSize());
		}//onClick$filterBtnId()
		public void onClick$resetAnchId(){
			//searchByDateDivId.setVisible(true);
			//searchByProgramDivId.setVisible(false);
			onSelect$searchbyLbId();
			//programLb.setVisible(false);
			/*searchbyLbId.setSelectedIndex(0);
			Listitem li=new Listitem();
			li.setValue("All");
			programLb.setSelectedItem(li);*/
			fromDateStr=null;
			toDateStr=null;
			
			int totalSize=loyaltyProgramTierDao.findLiabilityCountByProgram(user.getUserId(),Constants.STRING_NILL);
			int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
			lbReportPagingId.setTotalSize(totalSize);
			lbReportPagingId.setPageSize(pageSize);
			lbReportPagingId.setActivePage(0);
			lbReportPagingId.addEventListener("onPaging", this);
			redraw(lbReportPagingId.getActivePage()*lbReportPagingId.getPageSize(), lbReportPagingId.getPageSize());
		}//onClick$resetAnchId()

		public void onClick$backBtnId() {
			Redirect.goTo(PageListEnum.LOYALTY_ROI_REPORTS);
		}
		public void redraw(int startIdx,int size) {
			
			List<LoyaltyProgramTier> tiersList =null;
			String subQuery = Constants.STRING_NILL;
			
			if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
				if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					subQuery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
				}
			}
			/*if(searchByDateDivId.isVisible() && fromDateStr!=null && toDateStr!=null) {
				subQuery = "AND createdDate between '"+fromDateStr+"' AND '"+toDateStr+"' ";
			}*/
			//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				int count=lbReportsLbId.getItemCount();
				while(count>0){
					lbReportsLbId.removeItemAt(--count);	
				}
				
				//Object obj[]= new Object[]{0,0,0,0};
				//List<Object[]> list=loyaltytransactionChildDao.getltyTransactionByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),ltyTransactionChildObj.getUserId());
				List<LoyaltyProgram> programsList = loyaltyProgramDao.getAllProgramsListByUserId(user.getUserId());
				String prgmIds =Constants.STRING_NILL;
				for(LoyaltyProgram prgm: programsList) {
					if(prgmIds.length() > 0) prgmIds += Constants.DELIMETER_COMMA;
					prgmIds += prgm.getProgramId(); 
				}
				tiersList = loyaltyProgramTierDao.findLiabilityByProgram(user.getUserId(),prgmIds,subQuery,startIdx,size);
				HashMap<Long, String> prgmNameMap= new HashMap<Long, String>();
				for(LoyaltyProgramTier tier : tiersList) {
					prgmNameMap.put(tier.getProgramId(),(loyaltyProgramDao.findById(tier.getProgramId())).getProgramName());
				}
			for (LoyaltyProgramTier tier : tiersList) {
				

				Listcell lc;
				Listitem li;
				li = new Listitem();

				//Program
				lc = new Listcell();
				lc.setLabel(prgmNameMap.get(tier.getProgramId()));
				lc.setParent(li);
				//Tier
				lc = new Listcell();
				lc.setLabel(tier.getTierName());
				lc.setParent(li);
				//Value code
				lc = new Listcell(tier.getEarnType().equalsIgnoreCase("Amount") ? OCConstants.LOYALTY_TYPE_CURRENCY : tier.getEarnType());
				lc.setParent(li);
				
				Object[] obj = contactsLoyaltyDao.getLiabilityAndRedeemableValue(user.getUserId(),tier.getProgramId(),tier.getTierId());
				//Value
				lc = new Listcell(tier.getEarnType().equalsIgnoreCase("Amount") ? obj[0].toString() : obj[1].toString());
				lc.setParent(li);
				//Redeemable value
				double redeemableValue=0.0;
				if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0) { 
				
					double factor = Long.valueOf(obj[1].toString())/tier.getConvertFromPoints();
					int intFactor = (int)factor;
					redeemableValue = tier.getConvertToAmount() * intFactor;
					
				}
				redeemableValue += Long.valueOf(obj[0].toString());
				lc = new Listcell(tier.getEarnType().equalsIgnoreCase("Amount") ? obj[0].toString() : String.valueOf(redeemableValue));
				lc.setParent(li);
				
				li.setParent(lbReportsLbId);
				li.setValue(tier);

			}

		}
		@Override
		public void onEvent(Event event) throws Exception {
			super.onEvent(event);
			
			if(event.getTarget() instanceof Paging) {

				Paging paging = (Paging)event.getTarget();
				int desiredPage = paging.getActivePage();
				this.lbReportPagingId.setActivePage(desiredPage);
				PagingEvent pagingEvent = (PagingEvent) event;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
			}
		  
		}
		public void onSelect$searchbyLbId() {
			String value = searchbyLbId.getSelectedItem().getValue();
			if(value.equals(SEARCH_BY_PROGRAM)) {
				searchByProgramDivId.setVisible(true);
				//searchByDateDivId.setVisible(false);
				programLb.setVisible(true);
				
				int count=programLb.getItemCount();
				while(count>0){
				programLb.removeItemAt(--count);	
				}
				Listitem li = null;
				li = new Listitem("All");
				li.setParent(programLb);
				li.setValue("All");
				li.setSelected(true);
				List<LoyaltyProgram> programsList = loyaltyProgramDao.getAllProgramsListByUserId(user.getUserId());
				for (LoyaltyProgram loyaltyProgram : programsList) {
					li = new Listitem(loyaltyProgram.getProgramName());
					li.setParent(programLb);
					li.setValue(loyaltyProgram.getProgramId());
				}
				
				/*tierLb.setVisible(true);
				int tierCount=tierLb.getItemCount();
				while(tierCount>0){
				tierLb.removeItemAt(--tierCount);	
				}
				Listitem li1 = null;
				li1 = new Listitem("All");
				li1.setParent(tierLb);
				li1.setValue("All");
				li1.setSelected(true);
				List<LoyaltyProgramTier> tiersList = loyaltyProgramTierDao.getTierListByPrgmId(programLb.getSelectedItem().getValue());
				for (LoyaltyProgramTier loyaltyProgramTier : tiersList) {
					li1 = new Listitem(loyaltyProgramTier.getTierName());
					li1.setParent(tierLb);
					li1.setValue(loyaltyProgramTier.getTierId());
				}*/
				return;
			}/*else if(value.equals(SEARCH_BY_DATE)) {
				searchByDateDivId.setVisible(true);
				searchByProgramDivId.setVisible(false);
				
				searchByDateDivId.setVisible(true);
				fromDateboxId.setText(Constants.STRING_NILL);
				toDateboxId.setText(Constants.STRING_NILL);

				return;
			}*/
		}
		/*public void onSelect$valueCodeLbId() {
				
				int count=valueCodeLb.getItemCount();
				while(count>0){
					valueCodeLb.removeItemAt(--count);	
				}
				Listitem li = null;
				li = new Listitem("All");
				li.setParent(valueCodeLb);
				li.setValue("All");
				li.setSelected(true);
				List<ValueCodes> valueCodeList = valueCodesDao.findValueCode(user.getUserOrganization().getUserOrgId(),ltyTransactionChildObj.getValueCode());
				for (ValueCodes vc : valueCodeList) {
					li = new Listitem(vc.getValuCode());
					li.setParent(valueCodeLb);
					li.setValue(vc.getId());
				}
				return;
			
		}*/
		private boolean validateSetCreationDate() {
			
			if(fromDateboxId.getValue() == null || toDateboxId.getValue() == null ){
				MessageUtil.setMessage("Please specify the dates.",
						"color:red", "TOP");
				
				
				return false;
			}
			
			Calendar serverFromDateCal = fromDateboxId.getServerValue();
			Calendar serverToDateCal = toDateboxId.getServerValue();

			Calendar tempClientFromCal = fromDateboxId.getClientValue();
			Calendar tempClientToCal = toDateboxId.getClientValue();
			
			logger.debug("client From :" + tempClientFromCal + ", client To :"
					+ tempClientToCal);

			// change the time for startDate and endDate in order to consider right
			// from the
			// starting time of startDate to ending time of endDate
			serverFromDateCal.set(
					Calendar.HOUR_OF_DAY,
					serverFromDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientFromCal.get(Calendar.HOUR_OF_DAY));
			serverFromDateCal.set(
					Calendar.MINUTE,
					serverFromDateCal.get(Calendar.MINUTE)
							- tempClientFromCal.get(Calendar.MINUTE));
			serverFromDateCal.set(Calendar.SECOND, 0);

			serverToDateCal.set(Calendar.HOUR_OF_DAY,
					23 + serverToDateCal.get(Calendar.HOUR_OF_DAY)
							- tempClientToCal.get(Calendar.HOUR_OF_DAY));
			serverToDateCal.set(
					Calendar.MINUTE,
					59 + serverToDateCal.get(Calendar.MINUTE)
							- tempClientToCal.get(Calendar.MINUTE));
			serverToDateCal.set(Calendar.SECOND, 59);

			if (serverToDateCal.compareTo(serverFromDateCal) < 0) {
				MessageUtil.setMessage("'To' date must be later than 'From' date.",
						"color:red", "TOP");
				return false;
			}
			
			fromDateStr = serverFromDateCal.toString();
			toDateStr = serverToDateCal.toString();
			
			
			return true;
			
		}
		public String orderby_colName="createdDate",desc_Asc="desc";
		public void desc2ascasc2desc()
	    {
	    	if(desc_Asc=="desc")
				desc_Asc="asc";
			else
				desc_Asc="desc";
		
	    }
		public void onClick$sortByProgram() {
		orderby_colName = "programId";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		}
		public void onClick$sortbyTier() {
			orderby_colName = "tierId";
			desc2ascasc2desc();
			redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		}
		public void onClick$sortbyReward() {
			orderby_colName = "earnedReward";
			desc2ascasc2desc();
			redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
			}
		public void onClick$sortbyIssuedValue() {
			orderby_colName = "enteredAmount";
			desc2ascasc2desc();
			redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
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
				if (lbReportsLbId.getChildren().size() == 0) {
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

				String filePath = usersParentDirectory + "/" + userName + "/List/download/Liability_Report_"
						+ System.currentTimeMillis() + "." + ext;

				sb = new StringBuffer();
				File file = new File(filePath);
				bw = new BufferedWriter(new FileWriter(file));
				sb.append(
						"\"Loyalty Program\",\"Tier\",\"Value\",\"Liability\",\"Revenue\"\n");
				bw.write(sb.toString());

				List<LoyaltyProgramTier> tiersList =null;
				String subQuery = Constants.STRING_NILL;
				
				if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
					if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
						subQuery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
					}
				}
				List<LoyaltyProgram> programsList = loyaltyProgramDao.getAllProgramsListByUserId(user.getUserId());
				String prgmIds =Constants.STRING_NILL;
				for(LoyaltyProgram prgm: programsList) {
					if(prgmIds.length() > 0) prgmIds += Constants.DELIMETER_COMMA;
					prgmIds += prgm.getProgramId(); 
				}
				int totalSize=loyaltyProgramTierDao.findLiabilityCountByProgram(user.getUserId(),Constants.STRING_NILL);
				tiersList = loyaltyProgramTierDao.findLiabilityByProgram(user.getUserId(),prgmIds,subQuery,0, totalSize);
				HashMap<Long, String> prgmNameMap= new HashMap<Long, String>();
				for(LoyaltyProgramTier tier : tiersList) {
					prgmNameMap.put(tier.getProgramId(),(loyaltyProgramDao.findById(tier.getProgramId())).getProgramName());
				}
				for (LoyaltyProgramTier tier : tiersList) {
					sb.setLength(0);
					
					sb.append("\"" + prgmNameMap.get(tier.getProgramId()) + "\",");
					sb.append("\"" + tier.getTierName() + "\",");
					sb.append("\"" + (tier.getEarnType().equalsIgnoreCase("Amount") ? OCConstants.LOYALTY_TYPE_CURRENCY : tier.getEarnType()) + "\",");
					Object[] obj = contactsLoyaltyDao.getLiabilityAndRedeemableValue(user.getUserId(),tier.getProgramId(),tier.getTierId());
					sb.append("\"" + (tier.getEarnType().equalsIgnoreCase("Amount") ? obj[0].toString() : obj[1].toString()) + "\",");
					double redeemableValue=0.0;
					if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0) { 
					
						double factor = Long.valueOf(obj[1].toString())/tier.getConvertFromPoints();
						int intFactor = (int)factor;
						redeemableValue = tier.getConvertToAmount() * intFactor;
						
					}
					redeemableValue += Long.valueOf(obj[0].toString());
					sb.append("\"" + (tier.getEarnType().equalsIgnoreCase("Amount") ? obj[0].toString() : String.valueOf(redeemableValue)) + "\",");
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

				String filePath = usersParentDirectory + "/" + userName + "/List/download/Liability_Report_"
						+ System.currentTimeMillis() + "." + exportCbId.getSelectedItem().getValue();
				File file = new File(filePath);
				logger.debug("Writing to the file : " + filePath);
				FileOutputStream fileOut = new FileOutputStream(filePath);
				HSSFWorkbook hwb = new HSSFWorkbook();
				HSSFSheet sheet = hwb.createSheet("Liability Report");
				HSSFRow row = sheet.createRow((short) 0);
				HSSFCell cell = null;
				List<LoyaltyProgramTier> tiersList =null;
				String subQuery = Constants.STRING_NILL;
				
				if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
					if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
						subQuery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
					}
				}
				int count=loyaltyProgramTierDao.findLiabilityCountByProgram(user.getUserId(),subQuery );
			
				if (count == 0) {
					Messagebox.show("No report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
					return;
			      }
				List<LoyaltyProgram> programsList = loyaltyProgramDao.getAllProgramsListByUserId(user.getUserId());
				String prgmIds =Constants.STRING_NILL;
				for(LoyaltyProgram prgm: programsList) {
					if(prgmIds.length() > 0) prgmIds += Constants.DELIMETER_COMMA;
					prgmIds += prgm.getProgramId(); 
				}
				int totalSize=loyaltyProgramTierDao.findLiabilityCountByProgram(user.getUserId(),Constants.STRING_NILL);
				tiersList = loyaltyProgramTierDao.findLiabilityByProgram(user.getUserId(),prgmIds,subQuery,0, totalSize);
				HashMap<Long, String> prgmNameMap= new HashMap<Long, String>();
				for(LoyaltyProgramTier tier : tiersList) {
					prgmNameMap.put(tier.getProgramId(),(loyaltyProgramDao.findById(tier.getProgramId())).getProgramName());
				}
				row = sheet.createRow(0);
				int cellNo = 0;
					cell = row.createCell((cellNo++));
					cell.setCellValue("Loyalty Program");
				
					cell = row.createCell((cellNo++));
					cell.setCellValue("Tier");
					
					cell = row.createCell(cellNo++);
					cell.setCellValue("Value");
					
					cell = row.createCell(cellNo++);
					cell.setCellValue("Liability");
					
					cell = row.createCell(cellNo++);
					cell.setCellValue("Revenue");
					
					List<Object[]> list=null;
					int size = count;
					for (int i = 0; i < count; i += size) {
						
						
					int rowId = 1;
					for (LoyaltyProgramTier tier : tiersList) {
						row = sheet.createRow(rowId++);
						int columnId = 0;
						cell = null;
						
						cell = row.createCell(columnId++);
						cell.setCellValue(prgmNameMap.get(prgmNameMap.get(tier.getProgramId())));
						
						cell = row.createCell(columnId++);
						cell.setCellValue(tier.getTierName());
						
						cell = row.createCell(columnId++);
						cell.setCellValue((tier.getEarnType().equalsIgnoreCase("Amount") ? OCConstants.LOYALTY_TYPE_CURRENCY : tier.getEarnType()));
						
						Object[] obj = contactsLoyaltyDao.getLiabilityAndRedeemableValue(user.getUserId(),tier.getProgramId(),tier.getTierId());
						cell = row.createCell(columnId++);
						cell.setCellValue((tier.getEarnType().equalsIgnoreCase("Amount") ? obj[0].toString() : obj[1].toString()));
						double redeemableValue=0.0;
						if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0) { 
						
							double factor = Long.valueOf(obj[1].toString())/tier.getConvertFromPoints();
							int intFactor = (int)factor;
							redeemableValue = tier.getConvertToAmount() * intFactor;
							
						}
						redeemableValue += Long.valueOf(obj[0].toString());
						cell = row.createCell(columnId++);
						cell.setCellValue((tier.getEarnType().equalsIgnoreCase("Amount") ? obj[0].toString() : String.valueOf(redeemableValue)));
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


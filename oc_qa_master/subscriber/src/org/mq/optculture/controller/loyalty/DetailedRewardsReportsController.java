
package org.mq.optculture.controller.loyalty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.SpecialReward;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.ValueCodes;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.custom.MyDatebox;
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

public class DetailedRewardsReportsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SpecialRewardsDao specialRewardsDao;
	private LoyaltyTransactionChildDao loyaltytransactionChildDao;
	private LoyaltyProgramDao loyaltyProgramDao;
	private OrganizationStoresDao organizationStoresDao;
	private Users user;
	private Listbox spRewardLbId,pageSizeLbId,searchbyLbId,programLb,storeLb;
	private Div searchByDateDivId,searchByStoreDivId,searchByCardDivId,searchByProgramDivId;
	private Textbox searchByCardTbId,searchByStoreTbId;
	private Combobox exportCbId;
	private Session session ;
	private Paging rewardPagingId;
	private MyDatebox fromDateboxId,toDateboxId;
	private String fromDateStr,toDateStr;
	private SpecialRewardsDaoForDML specialRewardsDaoForDML;
	TimeZone clientTimeZone ;
	SpecialReward specialRewardObj;
	private  static String ITALIC_GREY_STYLE = "color:grey;font-style: italic;font-size:12px;";
	private final String SEARCH_BY_CARD = "card_number";
	private final String SEARCH_BY_STORE = "store";
	private final String SEARCH_BY_DATE = "created_date";
	private final String SEARCH_BY_PROGRAM = "program_id";

	
	public DetailedRewardsReportsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Detailed Reward Reports", Constants.STRING_NILL, style, true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	specialRewardsDao = (SpecialRewardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
	specialRewardsDaoForDML = (SpecialRewardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
	loyaltytransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
	organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean(OCConstants.ORGANIZATION_STORES_DAO);
	user = GetUser.getUserObj();
	session =Sessions.getCurrent();
    clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
    specialRewardObj = (SpecialReward) session.getAttribute("SPECIAL_REWARD_DETAILS");
    searchByCardDivId.setVisible(true);
	int totalSize=loyaltytransactionChildDao.findCountBySpecialReward(specialRewardObj.getRewardId(), user.getUserId());
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
	public void onClick$filterBtnId(){
		String subQuery = Constants.STRING_NILL;
		
		if(searchByCardDivId.isVisible() && searchByCardTbId.getValue()!=null && !searchByCardTbId.getValue().isEmpty()) {
			subQuery = "AND membershipNumber='"+searchByCardTbId.getValue().trim()+"' ";
		}
		if(searchByStoreDivId.isVisible() && storeLb.isVisible()) {
			if(!storeLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND storeNumber='"+storeLb.getSelectedItem().getValue()+"' ";
			}
		}
		if(searchByDateDivId.isVisible()){  
			boolean status = validateSetCreationDate();
			if(status == false) return;
			subQuery = "AND createdDate between '"+fromDateStr+"' AND '"+toDateStr+"' ";
		}
		if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
			if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
			}
		}
		int totalSize=loyaltytransactionChildDao.findCountByMemebershipNumber(specialRewardObj.getRewardId(),user.getUserId(),subQuery );
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this); 
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$filterBtnId()
	public void onClick$resetAnchId(){
		searchByCardTbId.setText(Constants.STRING_NILL);
		searchByCardDivId.setVisible(true);
		searchByDateDivId.setVisible(false);
		searchByStoreDivId.setVisible(false);
		searchByProgramDivId.setVisible(false);
		programLb.setVisible(false);
		storeLb.setVisible(false);
		searchbyLbId.setSelectedIndex(0);
		
		int totalSize=loyaltytransactionChildDao.findCountBySpecialReward(specialRewardObj.getRewardId(), user.getUserId());
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		rewardPagingId.setTotalSize(totalSize);
		rewardPagingId.setPageSize(pageSize);
		rewardPagingId.setActivePage(0);
		rewardPagingId.addEventListener("onPaging", this);
		redraw(rewardPagingId.getActivePage()*rewardPagingId.getPageSize(), rewardPagingId.getPageSize());
	}//onClick$resetAnchId()

	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.LOYALTY_REWARDS_REPORTS);
	}
	public void redraw(int startIdx,int size) {
		
		List<LoyaltyTransactionChild> specialRewardTrxList =null;
		String subQuery = Constants.STRING_NILL;
		
		if(searchByCardDivId.isVisible() && searchByCardTbId.getValue()!=null && !searchByCardTbId.getValue().isEmpty()) {
			subQuery = "AND membershipNumber='"+searchByCardTbId.getValue().trim()+"' ";
		}
		if(searchByStoreDivId.isVisible()) {
			if(!storeLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND storeNumber='"+storeLb.getSelectedItem().getValue()+"' ";
			}
		}
		if(searchByDateDivId.isVisible() && fromDateStr!=null && toDateStr!=null) {
			subQuery = "AND createdDate between '"+fromDateStr+"' AND '"+toDateStr+"' ";
		}
		if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
			if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
			}
		}
		
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			int count=spRewardLbId.getItemCount();
			while(count>0){
			spRewardLbId.removeItemAt(--count);	
			}
			
			specialRewardTrxList=loyaltytransactionChildDao.findTransactionsByMemebership(specialRewardObj.getRewardId(), 
					user.getUserId(),startIdx,size,subQuery,orderby_colName,desc_Asc);
		for (LoyaltyTransactionChild specialRewardTrx : specialRewardTrxList) {

			Listcell lc;
			Listitem li;
			li = new Listitem();

			//Date
			lc = new Listcell();
			lc.setLabel(MyCalendar.calendarToString(specialRewardTrx.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
			lc.setParent(li);
			//Program
			LoyaltyProgram ltyPrgm = loyaltyProgramDao.findByIdAndUserId(specialRewardTrx.getProgramId(),specialRewardTrx.getUserId());
			lc = new Listcell();
			lc.setLabel(ltyPrgm.getProgramName());
			lc.setParent(li);
			//Membership
			lc = new Listcell();
			lc.setLabel(specialRewardTrx.getMembershipNumber());
			lc.setParent(li);
			//Subsidiary
			lc = new Listcell();
			lc.setLabel(specialRewardTrx.getSubsidiaryNumber()!=null ? specialRewardTrx.getSubsidiaryNumber().toString() : "--");
			lc.setParent(li);
			//Store
			lc = new Listcell();
			OrganizationStores organizationStores = organizationStoresDao.findByStoresId(specialRewardTrx.getStoreNumber(),user.getUserOrganization().getUserOrgId());
			lc.setLabel(organizationStores!=null && organizationStores.getStoreName()!=null ? organizationStores.getStoreName() : "--");
			lc.setParent(li);
			//ReceiptNum
			lc = new Listcell();
			lc.setLabel(specialRewardTrx.getReceiptNumber()!=null ? specialRewardTrx.getReceiptNumber().toString() : "--");
			lc.setParent(li);
			/*Object obj[]= new Object[]{0,0,0};
			List<Object[]> list=specialRewardsDao.getTransactionBySprewarId(specialRewardObj.getRewardId());
			if(list.size()>0)
			obj=list.get(0);*/
			
			//Issued Reward
			//lc = new Listcell(obj[0].toString());
			lc = new Listcell();
			Double issuedreward=0.0;
			try {
			if(specialRewardTrx.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) {
				issuedreward = (specialRewardTrx.getEarnedPoints()!=null ? specialRewardTrx.getEarnedPoints() : 0.0);
			}else if(specialRewardTrx.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
				issuedreward = (specialRewardTrx.getEarnedAmount()!=null ? specialRewardTrx.getEarnedAmount() : 0.0);
			}else {
				issuedreward = (specialRewardTrx.getEarnedReward()!=null ? specialRewardTrx.getEarnedReward() : 0.0);
			}
			}catch(Exception e)	{
		
			}
			issuedreward = new BigDecimal(issuedreward).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			lc.setLabel(issuedreward!=null ? issuedreward.toString() + " " + specialRewardTrx.getEarnType(): "0.0");
			lc.setParent(li);
			//Revenue
			//lc = new Listcell(obj[1].toString());
			lc = new Listcell();
			Double issuanceAmt=(specialRewardTrx.getIssuanceAmount()!=null?specialRewardTrx.getIssuanceAmount():0.0);
			Double returnAmt = (specialRewardTrx.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN) && 
					specialRewardTrx.getDescription()!=null && !specialRewardTrx.getDescription().isEmpty()?Double.valueOf(specialRewardTrx.getDescription()):0.0);
			Double revenue=(specialRewardTrx.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)?
							issuanceAmt-returnAmt:issuanceAmt);
			revenue = new BigDecimal(revenue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			lc.setLabel(revenue.toString());
			lc.setParent(li);
			
			li.setParent(spRewardLbId);
			li.setValue(specialRewardTrx);

		}

	}
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		
		if(event.getTarget() instanceof Paging) {

			Paging paging = (Paging)event.getTarget();
			int desiredPage = paging.getActivePage();
			this.rewardPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
		}
	  
	}
	public void onSelect$searchbyLbId() {
		String value = searchbyLbId.getSelectedItem().getValue();
		if(value.equals(SEARCH_BY_CARD)) {
			searchByDateDivId.setVisible(false);
			searchByStoreDivId.setVisible(false);
			searchByProgramDivId.setVisible(false);
			programLb.setVisible(false);
			storeLb.setVisible(false);
			
			searchByCardDivId.setVisible(true);
			searchByCardTbId.setText(Constants.STRING_NILL);
			searchByCardTbId.setFocus(true);
			
			return;
		}
		else
		if(value.equals(SEARCH_BY_DATE)) {
			searchByCardDivId.setVisible(false);
			searchByStoreDivId.setVisible(false);
			searchByProgramDivId.setVisible(false);
			programLb.setVisible(false);
			storeLb.setVisible(false);
			
			searchByDateDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);

			return;
		}
		else
		if(value.equals(SEARCH_BY_STORE)) {
			searchByCardDivId.setVisible(false);
			searchByDateDivId.setVisible(false);
			searchByProgramDivId.setVisible(false);
			programLb.setVisible(false);
			
			searchByStoreDivId.setVisible(true);
			storeLb.setVisible(true);
			
			int count=storeLb.getItemCount();
			while(count>0){
			storeLb.removeItemAt(--count);	
			}
			Listitem li = null;
			li = new Listitem("All");
			li.setParent(storeLb);
			li.setValue("All");
			li.setSelected(true);
			List<OrganizationStores> storeList = organizationStoresDao.findByOrganization(user.getUserOrganization().getUserOrgId());
			for (OrganizationStores store : storeList) {
				li = new Listitem(store.getStoreName());
				li.setParent(storeLb);
				li.setValue(store.getHomeStoreId());
			}
			return;
		
		}
		else
		if(value.equals(SEARCH_BY_PROGRAM)) {
			searchByCardDivId.setVisible(false);
			searchByDateDivId.setVisible(false);
			searchByStoreDivId.setVisible(false);
			storeLb.setVisible(false);
			
			searchByProgramDivId.setVisible(true);
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
			return;
		}
		
	}
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
	public void onClick$sortbyCard() {
		orderby_colName = "membershipNumber";
		desc2ascasc2desc();	
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
	public void onClick$sortByProgram() {
	orderby_colName = "programId";
	desc2ascasc2desc();
	redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
	public void onClick$sortbyStore() {
		orderby_colName = "storeNumber";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
	}
	public void onClick$sortbyReward() {
		orderby_colName = "earnedReward";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		}
	public void onClick$sortbyRevenue() {
		orderby_colName = "issuanceAmount";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		}
	public void onClick$sortbySubsidiary() {
		orderby_colName = "subsidiaryNumber";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		}
	public void onClick$sortbyReceiptNo() {
		orderby_colName = "receiptNumber";
		desc2ascasc2desc();
		redraw(0,Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
		}
	public void onClick$sortbyDate() {
		orderby_colName = "createdDate";
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

		private void  exportCsv(){
		// TODO Auto-generated method stub
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

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Detailed_Reward_Report_"
					+ System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append(
					"\"Date\",\"Program\",\"Membership No\",\"Subsidary\",\"Store\",\"Receipt Number\",\"Issued Reward\",\"Revenue\"\n");
			//sb.append(
			//		"\"Date\",\"Program\",\"Membership No\",\"Subsidary\",\"Store\",\"Receipt Number\",\"Issued Reward\" , \"Revenue\"\n");
			bw.write(sb.toString());
			
				int startIdx = 0;
				int size = 0;
			
		
			List<LoyaltyTransactionChild> specialRewardTrxList =null;
			String subquery = Constants.STRING_NILL;
			
			if(searchByCardDivId.isVisible() && searchByCardTbId.getValue()!=null && !searchByCardTbId.getValue().isEmpty()) {
				subquery = "AND membershipNumber='"+searchByCardTbId.getValue().trim()+"' ";
			}
			if(searchByStoreDivId.isVisible()) {
				if(!storeLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					subquery = "AND storeNumber='"+storeLb.getSelectedItem().getValue()+"' ";
				}
			}
			if(searchByDateDivId.isVisible() && fromDateStr!=null && toDateStr!=null) {
				subquery = "AND createdDate between '"+fromDateStr+"' AND '"+toDateStr+"' ";
			}
			if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
				if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					subquery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
				}
			}
			
			//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//		int counts=spRewardLbId.getItemCount();
		int counts=loyaltytransactionChildDao.findCountBySpReward(specialRewardObj.getRewardId(), user.getUserId());
			//		while(counts>0){
		//		spRewardLbId.removeItemAt(--count);	
		//		}
		if(counts==0) {
			MessageUtil.setMessage("There are no records to export", "red");
			return;
		}
		logger.info("total rows"+counts)	;	
				specialRewardTrxList=loyaltytransactionChildDao.findTransactionsByMemebershipSp(specialRewardObj.getRewardId(), 
						user.getUserId(),0,counts,subquery,orderby_colName,desc_Asc);
		logger.info("sp value is"+specialRewardTrxList);
		logger.info("sp value2 is"+specialRewardObj.getRewardId());

				for (LoyaltyTransactionChild specialRewardTrx : specialRewardTrxList) {

				sb.setLength(0);
			
				//Date
		
				sb.append("\"" +MyCalendar.calendarToString(specialRewardTrx.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone)+ "\",");
			
			
				//Program
				LoyaltyProgram ltyPrgm = loyaltyProgramDao.findByIdAndUserId(specialRewardTrx.getProgramId(),specialRewardTrx.getUserId());
			
				sb.append("\"" + ltyPrgm.getProgramName() + "\",");
		
				//Membership
				sb.append("\"" +specialRewardTrx.getMembershipNumber()+ "\",");

				//Subsidiary
				
				sb.append("\"");
				sb.append(specialRewardTrx.getSubsidiaryNumber()!=null ? specialRewardTrx.getSubsidiaryNumber().toString() : "--" );
				sb.append("\",");
				logger.info("entering when it is null");

				//Store number	
				OrganizationStores organizationStores = organizationStoresDao.findByStoresId(specialRewardTrx.getStoreNumber(),user.getUserOrganization().getUserOrgId());
					if(organizationStores!=null) {
				sb.append("\"" + organizationStores.getStoreName()+ "\",");
					}else {
						sb.append("\"" +"--"+"\",");
					}
					
				//Receipt number	
					sb.append("\"");
					sb.append(specialRewardTrx.getReceiptNumber()!=null ? specialRewardTrx.getReceiptNumber().toString() : "--" );
					sb.append("\",");
		
				//Issued Reward
				
				Double issuedreward=0.0;
				try {
				if(specialRewardTrx.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) {
					issuedreward = (specialRewardTrx.getEarnedPoints()!=null ? specialRewardTrx.getEarnedPoints() : 0.0);
				//	sb.append("\"" +	issuedreward!=null ? issuedreward.toString() + " " + specialRewardTrx.getEarnType(): 0.0+ "\",");

				}else if(specialRewardTrx.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
					issuedreward = (specialRewardTrx.getEarnedAmount()!=null ? specialRewardTrx.getEarnedAmount() : 0.0);
				//	sb.append("\"" +	issuedreward!=null ? issuedreward.toString() + " " + specialRewardTrx.getEarnType(): 0.0+ "\",");


				}else {
					issuedreward = (specialRewardTrx.getEarnedReward()!=null ? specialRewardTrx.getEarnedReward() : 0.0);
				//	sb.append("\"" +	issuedreward!=null ? issuedreward.toString() + " " + specialRewardTrx.getEarnType(): 0.0+ "\",");


				}
				}catch(Exception e) {
					
				}
				issuedreward = new BigDecimal(issuedreward).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				String ireward=null;
				if(issuedreward!=null) {
					ireward= issuedreward.toString() +" "+specialRewardTrx.getEarnType();
				}
				
			
				sb.append("\"" +ireward+ "\",");
				 
				

			Double revenue=0.0;//
				Double issuanceAmt=(specialRewardTrx.getIssuanceAmount()!=null?specialRewardTrx.getIssuanceAmount():0.0);
				Double returnAmt = (specialRewardTrx.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN) && 
						specialRewardTrx.getDescription()!=null && !specialRewardTrx.getDescription().isEmpty()?Double.valueOf(specialRewardTrx.getDescription()):0.0);
				revenue=(specialRewardTrx.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)?
								issuanceAmt-returnAmt:issuanceAmt);
				revenue = new BigDecimal(revenue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				String Revnue=null;
				if(revenue!=null) {
				
					Revnue=revenue.toString();
			}
				logger.info("revenue is"+Revnue);
				sb.append("\"" +Revnue+ "\",");
				
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
		
	

	private void exportExcel() {
		// TODO Auto-generated method stub
	
		try {
			String userName = GetUser.getUserName();
			String usersParentDirectory = (String) PropertyUtil.getPropertyValue("usersParentDirectory");

			File downloadDir = new File(usersParentDirectory + "/" + userName + "/List/download/");

			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Details_Reward_Report_"
					+ System.currentTimeMillis() + "." + exportCbId.getSelectedItem().getValue();
			File file = new File(filePath);
			logger.debug("Writing to the file : " + filePath);
			FileOutputStream fileOut = new FileOutputStream(filePath);
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Detail Reward Report");
			HSSFRow row = sheet.createRow((short) 0);
			HSSFCell cell = null;
		//	List<LoyaltyProgramTier> tiersList =null;
			String subQuery = Constants.STRING_NILL;
			
			int count=loyaltytransactionChildDao.findCountBySpReward(specialRewardObj.getRewardId(), user.getUserId());

			List<LoyaltyTransactionChild> specialRewardTrxList =null;

			String sbquery="";
			specialRewardTrxList=loyaltytransactionChildDao.findTransactionsByMemebershipSp(specialRewardObj.getRewardId(), 
					user.getUserId(),0,count,sbquery,orderby_colName,desc_Asc);
		
			if (count == 0) {
				MessageUtil.setMessage("There are no records to export", "red");

				//Messagebox.show("No reports found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				return;
		      }
			row = sheet.createRow(0);
			int cellNo = 0;
				cell = row.createCell((cellNo++));
				cell.setCellValue("Date");
			
				cell = row.createCell((cellNo++));
				cell.setCellValue("Program");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Membership No");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Subsidiary");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Store");

				cell = row.createCell((cellNo++));
				cell.setCellValue("Receipt Number");
				
				cell = row.createCell((cellNo++));
				cell.setCellValue("Issued Reward");
				
				cell = row.createCell((cellNo++));
				cell.setCellValue("Revenue");
				
			//	List<Object[]> list=null;
				int rowId = 1;

				int size = count;
				for (int i = 0; i < count; i += size) {
					
					for (LoyaltyTransactionChild specialRewardTrx : specialRewardTrxList) {

						row = sheet.createRow(rowId++);
						int columnId = 0;
						cell = null;
						

						//Date
						cell = row.createCell(columnId++);
						cell.setCellValue(MyCalendar.calendarToString(specialRewardTrx.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR,clientTimeZone));
					
		
						//Program
						LoyaltyProgram ltyPrgm = loyaltyProgramDao.findByIdAndUserId(specialRewardTrx.getProgramId(),specialRewardTrx.getUserId());
						cell = row.createCell(columnId++);
						cell.setCellValue(ltyPrgm.getProgramName());
						
						//Membership
						cell = row.createCell(columnId++);
						cell.setCellValue(specialRewardTrx.getMembershipNumber());

						//Subsidiary
						cell = row.createCell(columnId++);
					//	cell.setCellValue(specialRewardTrx.getSubsidiaryNumber());
						cell.setCellValue(specialRewardTrx.getSubsidiaryNumber()!=null ? specialRewardTrx.getSubsidiaryNumber().toString() : "--");
						
						//Store
						cell = row.createCell(columnId++);

						OrganizationStores organizationStores = organizationStoresDao.findByStoresId(specialRewardTrx.getStoreNumber(),user.getUserOrganization().getUserOrgId());
							if(organizationStores!=null) {
								cell.setCellValue(	organizationStores.getStoreName());
							}else {
								cell.setCellValue("--");
							}
	
						//ReceiptNum
							cell = row.createCell(columnId++);
							cell.setCellValue(specialRewardTrx.getReceiptNumber()!=null ? specialRewardTrx.getReceiptNumber().toString() : "--" );
						
	
						//Issued Reward
						
						Double issuedreward=0.0;
						try {
						if(specialRewardTrx.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_POINTS)) {
							issuedreward = (specialRewardTrx.getEarnedPoints()!=null ? specialRewardTrx.getEarnedPoints() : 0.0);
						//	sb.append("\"" +	issuedreward!=null ? issuedreward.toString() + " " + specialRewardTrx.getEarnType(): 0.0+ "\",");

						}else if(specialRewardTrx.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
							issuedreward = (specialRewardTrx.getEarnedAmount()!=null ? specialRewardTrx.getEarnedAmount() : 0.0);
						//	sb.append("\"" +	issuedreward!=null ? issuedreward.toString() + " " + specialRewardTrx.getEarnType(): 0.0+ "\",");


						}else {
							issuedreward = (specialRewardTrx.getEarnedReward()!=null ? specialRewardTrx.getEarnedReward() : 0.0);
						//	sb.append("\"" +	issuedreward!=null ? issuedreward.toString() + " " + specialRewardTrx.getEarnType(): 0.0+ "\",");


						}
						}catch(Exception e) {
							
						}
						issuedreward = new BigDecimal(issuedreward).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						String ireward=null;
						if(issuedreward!=null) {
							ireward= issuedreward.toString() +" "+specialRewardTrx.getEarnType();
						}
						
						cell = row.createCell(columnId++);
						cell.setCellValue(ireward);
	
						Double revenue=0.0;
						Double issuanceAmt=(specialRewardTrx.getIssuanceAmount()!=null?specialRewardTrx.getIssuanceAmount():0.0);
						Double returnAmt = (specialRewardTrx.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN) && 
								specialRewardTrx.getDescription()!=null && !specialRewardTrx.getDescription().isEmpty()?Double.valueOf(specialRewardTrx.getDescription()):0.0);
						revenue=(specialRewardTrx.getTransactionType().equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_RETURN)?
										issuanceAmt-returnAmt:issuanceAmt);
						revenue = new BigDecimal(revenue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						String Revnue=null;
						if(revenue!=null) {
						
							Revnue=revenue.toString();
					}
						logger.info("revenue is"+Revnue);
						cell = row.createCell(columnId++);
						cell.setCellValue(Revnue);
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

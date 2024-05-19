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

public class DetailedROIReportsController extends GenericForwardComposer {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SpecialRewardsDao specialRewardsDao;
	private LoyaltyTransactionChildDao loyaltytransactionChildDao;
	private LoyaltyBalanceDao loyaltyBalanceDao;
	private LoyaltyProgramDao loyaltyProgramDao;
	private OrganizationStoresDao organizationStoresDao;
	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	private ValueCodesDao valueCodesDao;
	private Users user;
	private Listbox detailedROILbId,pageSizeLbId,searchbyLbId,programLb,tierLb,valueCodeLb;
	private Label valuCodeId;
	private Div searchByTierDivId,valueCodeDivId,searchByProgramDivId,searchByDateDivId;
	private Session session ;
	private Combobox exportCbId;
	private Paging detailedROIPagingId;
	private MyDatebox fromDateboxId,toDateboxId;
	private String fromDateStr,toDateStr;
	private SpecialRewardsDaoForDML specialRewardsDaoForDML;
	TimeZone clientTimeZone ;
	LoyaltyTransactionChild ltyTransactionChildObj;
	private  static String ITALIC_GREY_STYLE = "color:grey;font-style: italic;font-size:12px;";
	private final String SEARCH_BY_TIER = "tier_id";
	private final String SEARCH_BY_VALUE_CODE = "value_code";
	private final String SEARCH_BY_PROGRAM = "program_id";
	private final String SEARCH_BY_DATE = "created_date";

	
	public DetailedROIReportsController() {
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Detailed Revenue On Issuance Reports", Constants.STRING_NILL, style, true);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	specialRewardsDao = (SpecialRewardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SPECIAL_REWARDS_DAO);
	specialRewardsDaoForDML = (SpecialRewardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPECIAL_REWARDS_DAO_FOR_DML);
	loyaltytransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
	loyaltyProgramDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
	organizationStoresDao = (OrganizationStoresDao)SpringUtil.getBean(OCConstants.ORGANIZATION_STORES_DAO);
	loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
	loyaltyBalanceDao = (LoyaltyBalanceDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_BALANCE_DAO);
	valueCodesDao = (ValueCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.VALUE_CODES_DAO);
	user = GetUser.getUserObj();
	session =Sessions.getCurrent();
    clientTimeZone =(TimeZone)Sessions.getCurrent().getAttribute("clientTimeZone");
    ltyTransactionChildObj = (LoyaltyTransactionChild) session.getAttribute("SPECIAL_REWARD_ROI_DETAILS");
    searchByDateDivId.setVisible(true);
    valuCodeId.setValue(ltyTransactionChildObj.getEarnType());
	int totalSize=loyaltytransactionChildDao.findCountByValueCode(ltyTransactionChildObj.getEarnType(), user.getUserId(),Constants.STRING_NILL);
	int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
	detailedROIPagingId.setTotalSize(totalSize);
	detailedROIPagingId.setPageSize(pageSize);
	detailedROIPagingId.addEventListener("onPaging", this); 
	detailedROIPagingId.setActivePage(0);
	//onSelect$valueCodeLbId();
	redraw(0, detailedROIPagingId.getPageSize());
	}
	public void onSelect$pageSizeLbId(){
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());	
		detailedROIPagingId.setPageSize(pageSize);
		detailedROIPagingId.addEventListener("onPaging", this); 
		redraw(0, detailedROIPagingId.getPageSize());
	}
	public void onClick$filterBtnId(){
		String subQuery = Constants.STRING_NILL;
		
		/*if(searchByTierDivId.isVisible() && tierLb.isVisible()) {
			if(!tierLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND tierId='"+tierLb.getSelectedItem().getValue()+"' ";
			}
		}
		*/if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
			if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND programId='"+programLb.getSelectedItem().getValue()+"' ";
			}
		}
		if(searchByDateDivId.isVisible()){  
			boolean status = validateSetCreationDate();
			if(status == false) return;
			subQuery = "AND createdDate between '"+fromDateStr+"' AND '"+toDateStr+"' ";
		}
		int totalSize=loyaltytransactionChildDao.findCountByValueCode(ltyTransactionChildObj.getEarnType(),user.getUserId(),subQuery );
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		detailedROIPagingId.setTotalSize(totalSize);
		detailedROIPagingId.setPageSize(pageSize);
		detailedROIPagingId.setActivePage(0);
		detailedROIPagingId.addEventListener("onPaging", this); 
		redraw(detailedROIPagingId.getActivePage()*detailedROIPagingId.getPageSize(), detailedROIPagingId.getPageSize());
	}//onClick$filterBtnId()
	public void onClick$resetAnchId(){
		searchByDateDivId.setVisible(true);
		searchByProgramDivId.setVisible(false);
		//searchByTierDivId.setVisible(false);
		programLb.setVisible(false);
		//tierLb.setVisible(false);
		searchbyLbId.setSelectedIndex(0);
		Listitem li=new Listitem();
		li.setValue("All");
		//programLb.setSelectedItem(li);
		fromDateStr=null;
		toDateStr=null;
		
		int totalSize=loyaltytransactionChildDao.findCountByValueCode(ltyTransactionChildObj.getEarnType(), user.getUserId(),Constants.STRING_NILL);
		int pageSize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		detailedROIPagingId.setTotalSize(totalSize);
		detailedROIPagingId.setPageSize(pageSize);
		detailedROIPagingId.setActivePage(0);
		detailedROIPagingId.addEventListener("onPaging", this);
		redraw(detailedROIPagingId.getActivePage()*detailedROIPagingId.getPageSize(), detailedROIPagingId.getPageSize());
	}//onClick$resetAnchId()

	public void onClick$backBtnId() {
		Redirect.goTo(PageListEnum.LOYALTY_ROI_REPORTS);
	}
	public void redraw(int startIdx,int size) {
		
		List<LoyaltyTransactionChild> valueCodeTrxList =null;
		String subQuery = Constants.STRING_NILL;
		
		/*if(searchByTierDivId.isVisible()) {
			if(!tierLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND tierId='"+tierLb.getSelectedItem().getValue()+"' ";
			}
		}*/
		if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
			if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
				subQuery = "AND program_id='"+programLb.getSelectedItem().getValue()+"' ";
			}
		}
		if(searchByDateDivId.isVisible() && fromDateStr!=null && toDateStr!=null) {
			subQuery = "AND created_date between '"+fromDateStr+"' AND '"+toDateStr+"' ";
		}
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			int count=detailedROILbId.getItemCount();
			while(count>0){
				detailedROILbId.removeItemAt(--count);	
			}
			
			//Object obj[]= new Object[]{0,0,0,0};
			List<Object[]> list=loyaltytransactionChildDao.getROIByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),ltyTransactionChildObj.getUserId()
					,startIdx,size,subQuery);
			HashMap<Long, String> prgmNameMap= new HashMap<Long, String>();
			for(Object[] object:list) {
				prgmNameMap.put(((Long)object[0]).longValue(),(loyaltyProgramDao.findById(((Long)object[0]).longValue())).getProgramName());
			}
			HashMap<Long, String> tierMap= new HashMap<Long, String>();
			List<LoyaltyProgramTier> listOfTiers = null;
			try {
				for(Object[] object:list) {
					listOfTiers = loyaltyProgramTierDao.fetchByTier(((Long)object[0]).longValue(),((Long)object[1]).longValue());
					String tierName = ((listOfTiers!=null && !listOfTiers.isEmpty())? listOfTiers.get(0).getTierName() : "--");
					tierMap.put(((Long)object[1]).longValue(),tierName);
				}
			} catch (LoyaltyProgramException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e);
			}
			/*valueCodeTrxList=loyaltytransactionChildDao.findTransactionsByValueCode(user.getUserId(),startIdx,size,subQuery,
					orderby_colName,desc_Asc,ltyTransactionChildObj.getEarnType());*/
		for (Object[] valueCodeTrx : list) {
			

			Listcell lc;
			Listitem li;
			li = new Listitem();

			//Program
			lc = new Listcell();
			lc.setLabel(prgmNameMap.get(valueCodeTrx[0]));
			lc.setParent(li);
			//Tier
			lc = new Listcell();
			lc.setLabel(tierMap.get(valueCodeTrx[1]));
			lc.setParent(li);
			
			Object[] issued=loyaltytransactionChildDao.getIssuedProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
					ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString(),valueCodeTrx[1].toString());
			Object[] liability=loyaltytransactionChildDao.getLiabilityByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
					ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString(),valueCodeTrx[1].toString());
			Object[] revenue=loyaltytransactionChildDao.getRevenueByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
					ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString(),valueCodeTrx[1].toString());
			List<Object[]> valueCodeLiability=loyaltyBalanceDao.getLiabilityListByValueCode(ltyTransactionChildObj.getEarnType(),
					ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString());
			Object[] liabilityObj = new Object[]{0,0,0};
			if(valueCodeLiability.size()>0) liabilityObj=valueCodeLiability.get(0);
			//if(ltyTransactionChildObj.getEarnType().equalsIgnoreCase("Points") || ltyTransactionChildObj.getEarnType().equalsIgnoreCase("Amount")) {
			//Issued Reward
			lc = new Listcell(issued!=null ? issued[0].toString() : "0");
			lc.setParent(li);
			//Liability
			lc = new Listcell(liability!=null ? liability[0].toString() : "0");
			lc.setParent(li);
			/*}else {
				lc = new Listcell(liabilityObj[0].toString());
				lc.setParent(li);
				lc = new Listcell(liabilityObj[1].toString());
				lc.setParent(li);
			}*/
			//revenue
			lc = new Listcell(revenue!=null ? revenue[0].toString() : "0");
			lc.setParent(li);
			
			li.setParent(detailedROILbId);
			li.setValue(valueCodeTrx);

		}

	}
	@Override
	public void onEvent(Event event) throws Exception {
		super.onEvent(event);
		
		if(event.getTarget() instanceof Paging) {

			Paging paging = (Paging)event.getTarget();
			int desiredPage = paging.getActivePage();
			this.detailedROIPagingId.setActivePage(desiredPage);
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
		}
	  
	}
	public void onSelect$searchbyLbId() {
		String value = searchbyLbId.getSelectedItem().getValue();
		if(value.equals(SEARCH_BY_PROGRAM)) {
			//searchByTierDivId.setVisible(false);
			searchByProgramDivId.setVisible(true);
			searchByDateDivId.setVisible(false);
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
		}else if(value.equals(SEARCH_BY_DATE)) {
			searchByDateDivId.setVisible(true);
			searchByProgramDivId.setVisible(false);
			
			searchByDateDivId.setVisible(true);
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);

			return;
		}
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
			if (detailedROILbId.getChildren().size() == 0) {
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

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Detailed_Loyalty_ROI_Report_"
					+ System.currentTimeMillis() + "." + ext;

			sb = new StringBuffer();
			File file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			sb.append(
					"\"Loyalty Program\",\"Tier\",\"Issued Value\",\"Liability\",\"Revenue\"\n");
			bw.write(sb.toString());

			int totalSize=loyaltytransactionChildDao.findCountByValueCode(ltyTransactionChildObj.getEarnType(), user.getUserId(),Constants.STRING_NILL);
			String subQuery = Constants.STRING_NILL;
			
			if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
				if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					subQuery = "AND program_id='"+programLb.getSelectedItem().getValue()+"' ";
				}
			}
			if(searchByDateDivId.isVisible() && fromDateStr!=null && toDateStr!=null) {
				subQuery = "AND created_date between '"+fromDateStr+"' AND '"+toDateStr+"' ";
			}
			
			List<Object[]> list=loyaltytransactionChildDao.getROIByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),ltyTransactionChildObj.getUserId()
					,0,totalSize,subQuery);
			HashMap<Long, String> prgmNameMap= new HashMap<Long, String>();
			for(Object[] object:list) {
				prgmNameMap.put(((Long)object[0]).longValue(),(loyaltyProgramDao.findById(((Long)object[0]).longValue())).getProgramName());
			}
			HashMap<Long, String> tierMap= new HashMap<Long, String>();
			List<LoyaltyProgramTier> listOfTiers = null;
			try {
				for(Object[] object:list) {
					listOfTiers = loyaltyProgramTierDao.fetchByTier(((Long)object[0]).longValue(),((Long)object[1]).longValue());
					String tierName = (listOfTiers!=null ? listOfTiers.get(0).getTierName() : "");
					tierMap.put(((Long)object[1]).longValue(),tierName);
				}
			} catch (LoyaltyProgramException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e);
			}
			
		for (Object[] valueCodeTrx : list) {
				sb.setLength(0);
				
				sb.append("\"" + prgmNameMap.get(valueCodeTrx[0]) + "\",");
				sb.append("\"" + prgmNameMap.get(valueCodeTrx[1]) + "\",");
				Object[] issued=loyaltytransactionChildDao.getIssuedProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
						ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString(),valueCodeTrx[1].toString());
				Object[] liability=loyaltytransactionChildDao.getLiabilityByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
						ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString(),valueCodeTrx[1].toString());
				Object[] revenue=loyaltytransactionChildDao.getRevenueByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
						ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString(),valueCodeTrx[1].toString());
				List<Object[]> valueCodeLiability=loyaltyBalanceDao.getLiabilityListByValueCode(ltyTransactionChildObj.getEarnType(),
						ltyTransactionChildObj.getUserId(),valueCodeTrx[0].toString());
				Object[] liabilityObj = new Object[]{0,0,0};
				if(valueCodeLiability.size()>0) liabilityObj=valueCodeLiability.get(0);
				//if(ltyTransactionChildObj.getEarnType().equalsIgnoreCase("Points") || ltyTransactionChildObj.getEarnType().equalsIgnoreCase("Amount")) {
				sb.append("\"" + issued[0].toString() + "\",");
				sb.append("\"" + liability[0].toString() + "\",");
				/*}else {
					sb.append("\"" + liabilityObj[0].toString() + "\",");
					sb.append("\"" + liabilityObj[1].toString() + "\",");
				}*/
				sb.append("\"" + revenue[0].toString() + "\",");
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

			String filePath = usersParentDirectory + "/" + userName + "/List/download/Detailed_Loyalty_ROI_Report_"
					+ System.currentTimeMillis() + "." + exportCbId.getSelectedItem().getValue();
			File file = new File(filePath);
			logger.debug("Writing to the file : " + filePath);
			FileOutputStream fileOut = new FileOutputStream(filePath);
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Detailed Loyalty ROI Report");
			HSSFRow row = sheet.createRow((short) 0);
			HSSFCell cell = null;
			String subQuery = Constants.STRING_NILL;
			
			if(searchByProgramDivId.isVisible() && programLb.isVisible()) {
				if(!programLb.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
					subQuery = "AND program_id='"+programLb.getSelectedItem().getValue()+"' ";
				}
			}
			if(searchByDateDivId.isVisible() && fromDateStr!=null && toDateStr!=null) {
				subQuery = "AND created_date between '"+fromDateStr+"' AND '"+toDateStr+"' ";
			}
			int count=loyaltytransactionChildDao.findCountByValueCode(ltyTransactionChildObj.getEarnType(), user.getUserId(),Constants.STRING_NILL);
		
			if (count == 0) {
				Messagebox.show("No report found.", "Info", Messagebox.OK, Messagebox.INFORMATION);
				return;
		      }
			
			row = sheet.createRow(0);
			int cellNo = 0;
				cell = row.createCell((cellNo++));
				cell.setCellValue("Loyalty Program");
				
				cell = row.createCell((cellNo++));
				cell.setCellValue("Tier");
			
				cell = row.createCell(cellNo++);
				cell.setCellValue("Issued Value");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Liability");
				
				cell = row.createCell(cellNo++);
				cell.setCellValue("Revenue");
				
				List<Object[]> list=null;
				int size = count;
				for (int i = 0; i < count; i += size) {
					list=loyaltytransactionChildDao.getROIByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),ltyTransactionChildObj.getUserId(),
							0,count,subQuery);
					HashMap<Long, String> prgmNameMap= new HashMap<Long, String>();
					for(Object[] object:list) {
						prgmNameMap.put(((Long)object[0]).longValue(),(loyaltyProgramDao.findById(((Long)object[0]).longValue())).getProgramName());
					}
					HashMap<Long, String> tierMap= new HashMap<Long, String>();
					List<LoyaltyProgramTier> listOfTiers = null;
					try {
						for(Object[] object:list) {
							listOfTiers = loyaltyProgramTierDao.fetchByTier(((Long)object[0]).longValue(),((Long)object[1]).longValue());
							String tierName = (listOfTiers!=null ? listOfTiers.get(0).getTierName() : "");
							tierMap.put(((Long)object[1]).longValue(),tierName);
						}
					} catch (LoyaltyProgramException e) {
						// TODO Auto-generated catch block
						logger.error("Exception ",e);
					}
				int rowId = 1;
				for(Object[] vc : list) {
					row = sheet.createRow(rowId++);
					int columnId = 0;
					cell = null;
					
					cell = row.createCell(columnId++);
					cell.setCellValue(prgmNameMap.get(vc[0]));
					
					cell = row.createCell(columnId++);
					cell.setCellValue(prgmNameMap.get(vc[1]));
					
					Object[] issued=loyaltytransactionChildDao.getIssuedProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
							ltyTransactionChildObj.getUserId(),vc[0].toString(),vc[1].toString());
					Object[] liability=loyaltytransactionChildDao.getLiabilityByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
							ltyTransactionChildObj.getUserId(),vc[0].toString(),vc[1].toString());
					Object[] revenue=loyaltytransactionChildDao.getRevenueByProgramAndValueCode(ltyTransactionChildObj.getEarnType(),
							ltyTransactionChildObj.getUserId(),vc[0].toString(),vc[1].toString());
					List<Object[]> valueCodeLiability=loyaltyBalanceDao.getLiabilityListByValueCode(ltyTransactionChildObj.getEarnType(),
							ltyTransactionChildObj.getUserId(),vc[0].toString());
					Object[] liabilityObj = new Object[]{0,0,0};
					if(valueCodeLiability.size()>0) liabilityObj=valueCodeLiability.get(0);
					//if(ltyTransactionChildObj.getEarnType().equalsIgnoreCase("Points") || ltyTransactionChildObj.getEarnType().equalsIgnoreCase("Amount")) {
					cell = row.createCell(columnId++);
					cell.setCellValue(issued[0].toString());
					
					cell = row.createCell(columnId++);
					cell.setCellValue(liability[0].toString());
					/*}else {
						cell = row.createCell(columnId++);
						cell.setCellValue(liabilityObj[0].toString());
						
						cell = row.createCell(columnId++);
						cell.setCellValue(liabilityObj[1].toString());
					}*/
					
					cell = row.createCell(columnId++);
					cell.setCellValue(revenue[0].toString());
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

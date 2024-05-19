package org.mq.marketer.campaign.controller.contacts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Campaigns;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SMSCampaigns;
import org.mq.marketer.campaign.beans.SegmentRules;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.CampaignsDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.MailingListDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SMSCampaignsDao;
import org.mq.marketer.campaign.dao.SegmentRulesDao;
import org.mq.marketer.campaign.dao.SegmentRulesDaoForDML;
import org.mq.marketer.campaign.dao.UsersDomainsDaoForDML;
import org.mq.marketer.campaign.general.ClickHouseSalesQueryGenerator;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.LBFilterEventListener;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.SalesQueryGenerator;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

public class ViewSegmentsController extends GenericForwardComposer {

	private Grid segmentRulesGridId;
	private Session session;
	private SegmentRules segmentRules;
	private SegmentRulesDao segmentRulesDao;
	
	private ContactsDao contactsDao;
	private MLCustomFieldsDao mlCFDao;
	private MailingListDao mailingListDao;
	private SegmentRulesDaoForDML segmentRulesDaoForDML;
	private MailingListDaoForDML mailingListDaoForDML;
	
	private Users currentUser;
	//private Set<Long> userIdsSet = GetUser.getUsersSet();
	
	private final RowRenderer rowRender  = new MyRenderer();
	
	private Paging segmentsPagingId;
	private Listbox pageSizeLbId, searchByLbId;
	private Window viewSegRuleWinId;
	private Div viewSegRuleWinId$previewSegDivId,viewSegRuleWinId$custFieldExprtDivId,viewSegRuleWinId$viewDownloadSegDivId,viewSegRuleWinId$chkDivId;
	private Div viewSegmentWinId$searchBySegmentDateDivId,viewSegmentWinId$searchBySegmentNameDivId,viewSegmentWinId$filterBtnDivId,viewSegmentWinId$resetAnchDivId;
	private Label viewSegRuleWinId$segRuleLblId,viewSegRuleWinId$fileNameLblId;
	private CampaignsDao campaignsDao;
	private Textbox searchByTbId;
	private MyDatebox fromDateboxId, toDateboxId;
	
	
	boolean isAdmin;
	Button updateSegmentBtnId, updateqrySegmentBtnId;
	Intbox start,end;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private TimeZone clientTimeZone;
	
	private Set<Long> segmentIdsSet; 
	private POSMappingDao posMappingDao; 
	
	private final String ALL = "All";
	private final String SEARCH_BY_NAME="Name";
	private final String SEARCH_BY_DATE ="Date";
	
	public ViewSegmentsController() {
		
		logger.debug("---just---");
		
		mlCFDao = (MLCustomFieldsDao)SpringUtil.getBean("mlCustomFieldsDao");
		contactsDao = (ContactsDao)SpringUtil.getBean("contactsDao");
		mailingListDao = (MailingListDao)SpringUtil.getBean("mailingListDao");
		mailingListDaoForDML = (MailingListDaoForDML)SpringUtil.getBean("mailingListDaoForDML");
		currentUser = GetUser.getUserObj();
		session = Sessions.getCurrent();
		segmentRulesDao = (SegmentRulesDao)SpringUtil.getBean("segmentRulesDao");
		campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
		posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("Segments","",style,true);
		segmentRulesDaoForDML = (SegmentRulesDaoForDML)SpringUtil.getBean("segmentRulesDaoForDML");
		segmentIdsSet = (Set<Long>)session.getAttribute(Constants.SEGMENTIDS_SET);
		
	}
	
	
	Paging myPagingId;
	String filterQuery=null;
	String filterCountQuery=null;
	String qryPrefix=null;
	LBFilterEventListener contactLbELObj =null;	
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		//int totalSize = segmentRulesDao.findCountByUser(userIdsSet);
		/*int totalSize = segmentRulesDao.findCountByIds(segmentIdsSet, null, null, null);
		segmentsPagingId.setTotalSize(totalSize);
		segmentsPagingId.addEventListener("onPaging", new MyRenderer());
		
		
		
		List retList = getSegRuleList(0, segmentsPagingId.getPageSize(), null, null, null);
		if(retList != null) {
		
			segmentRulesGridId.setModel(new ListModelList(retList));
			segmentRulesGridId.setRowRenderer(rowRender);
		
		}*/
		clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
		isAdmin = (Boolean)session.getAttribute("isAdmin");
		segmentRulesGridId.setAttribute("defaultOrderBy", "modifiedDate");
		registerEventListner(0, 5, false);
		Utility.refreshGridModel(contactLbELObj, 0, true);
		//Utility.refreshGridModel(contactLbELObj, 0, true);
		
		
		/*if(!isAdmin) {
			updateqrySegmentBtnId.setVisible(false);
			updateSegmentBtnId.setVisible(false);
			start.setVisible(false);
			end.setVisible(false);
		}*/
		
		
	} //doAfterCompose
	
	private void registerEventListner(int strtIdx, int endIdx, boolean isFilter){

		String value = searchByLbId.getSelectedItem().getValue().toString();
		String segmentIds = Utility.getIdsAsString(segmentIdsSet);

		//String segmentName = null;
		String startDate = null;
		String endDate = null;
		String subQry = "";
		
		if(value.equals(SEARCH_BY_NAME) && isFilter){
			
			if(searchByTbId.getValue().isEmpty() || searchByTbId.getValue().trim().length()==0){
				
				MessageUtil.setMessage("Please enter text in the Textbox.", "red");
				return;
			}
			subQry = " AND segRuleName LIKE '%"+ searchByTbId.getValue().trim() +"%' ";
			//segmentName = searchByTbId.getValue().trim();
			}
		else if(value.equals(SEARCH_BY_DATE)){
			
			if(fromDateboxId.getServerValue() == null || toDateboxId.getServerValue() == null) {
				MessageUtil.setMessage("Please specify the dates", "red");
				return;
			}
						
			if(toDateboxId.getClientValue().before(fromDateboxId.getClientValue())) {
				MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
				return;
			}
			startDate = getStartDate();
			endDate = getEndDate();
			subQry = " AND createdDate BETWEEN '"+ startDate +"' AND '"+ endDate +"' ";
		}
		
		
		
		 filterQuery = "FROM SegmentRules WHERE segRuleId IN ("+segmentIds+") AND segmentType='"+Constants.SEGMENT_TYPE_CONTACT+"' " + subQry ;
		
		          //  filterQuery =    " FROM SegmentRules WHERE userId ="+users.getUserId().longValue(); 				           
					filterCountQuery = "SELECT COUNT(segRuleId) "+filterQuery;
		                
					qryPrefix="";   
							
					try{
				Map<Integer, Field> objMap = new HashMap<Integer, Field>();
				objMap.put(1, SegmentRules.class.getDeclaredField("segRuleName"));
				objMap.put(2, SegmentRules.class.getDeclaredField("description"));
				//objMap.put(3, SegmentRules.class.getDeclaredField("segRuleName"));
				objMap.put(4, SegmentRules.class.getDeclaredField("createdDate"));
				objMap.put(5, SegmentRules.class.getDeclaredField("lastRefreshedOn"));
				objMap.put(6, SegmentRules.class.getDeclaredField("totSize"));
				objMap.put(7, SegmentRules.class.getDeclaredField("size"));
				objMap.put(8, SegmentRules.class.getDeclaredField("totMobileSize"));
				
				segmentsPagingId.setPageSize(Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel()));
				
				contactLbELObj= LBFilterEventListener.grFilterSetup(segmentRulesGridId, segmentsPagingId, filterQuery, filterCountQuery, qryPrefix, objMap);
				segmentRulesGridId.setRowRenderer(rowRender);
					}catch(Exception e){
						logger.error("Exception :: ",e);
					}


		}
	
	
	public void onClick$updateSegmentBtnId() {
		
		int start = this.start.getValue();
		int range = this.end.getValue();
		
		List<SegmentRules> retList = segmentRulesDao.findAll(start, range);
		List<SegmentRules> updateSegments = new ArrayList<SegmentRules>();
		for (SegmentRules segmentRules : retList) {
			
			String rule = segmentRules.getSegRule();
			
			if(rule == null ) continue;
			String[] tokensArr = rule.split("\\|\\|");
			String userIdStr=null;
			String finalStr="";

			String token=null;
			String tokenFilterStr="";
			String valStr = "";
			boolean foundFlag = false;
			
			String updateStr = rule;
			for (int i = 0; i < tokensArr.length; i++) {
				token=tokensArr[i].trim();
				
				 //logger.info("TOKEN="+token+" ----1-----------"); -- APP-3914
				 String keyStr=null;
				 
				 valStr = "";
				 
				//****** Get the first token *********
				if(i==0) {
					if(token.indexOf(':')==-1) break ;
					
					userIdStr = token.substring(token.indexOf(':')+1).trim();
					token = token.substring(0, token.indexOf(':')).trim();
					tokenFilterStr =(token.equalsIgnoreCase("all") || token.equalsIgnoreCase("AND")) ? "AND" : "OR";
					continue;
				}
				String[] tempTokenArr = token.split("<OR>");
				for (int tokenIndex=0; tokenIndex<tempTokenArr.length;tokenIndex++) {
					String tempToken = tempTokenArr[tokenIndex];
					String[] fieldsArr = tempToken.split("\\|");
					String columnName = fieldsArr[0];
						
					SegmentEnum segemntEnum = SegmentEnum.getEnumByColumn(columnName);
					String item = null;
					if(segemntEnum == null) {
						logger.info(" ----2-----------");
						if(columnName.toLowerCase().contains("udf")) {
							logger.info(" ----3-----------");
							item = getPOSDisplayLabel(columnName, segmentRules.getUserId());
							logger.info(" ----9-----------"+item);
							if(item == null) continue;
						}else {
							
							logger.info("no segment enum");
							continue;
						}
					}
					if(segemntEnum != null && segemntEnum.getParentEnum() != null) {
						logger.info(" ----11-----------");
						segemntEnum = getMostParentEnum(segemntEnum);
						if(segemntEnum == null) {
							logger.info(" ----12-----------");
							continue;
						}
					}
					
					if(item == null ) item	= segemntEnum.getItem();
					
					logger.info("item::"+item);
					
					if(item != null && !item.isEmpty() && !tempToken.startsWith(item)) {
						updateStr = updateStr.replace(tempToken, item+"|"+tempToken);
					}
				}
				
				
			}
			
			if(!rule.equals(updateStr)) {
				
				segmentRules.setSegRule(updateStr);
				updateSegments.add(segmentRules);
			}
			
		}//for
		if(updateSegments.size() > 0) {
			
			//segmentRulesDao.saveByCollection(updateSegments);
			segmentRulesDaoForDML.saveByCollection(updateSegments);

		}
		
		
	}
	
	public void onClick$updateqrySegmentBtnId() {
		
		try {
			int start = this.start.getValue();
			int range = this.end.getValue();
			
			List<SegmentRules> updateSegments = new ArrayList<SegmentRules>();
			List<SegmentRules> retList = segmentRulesDao.findAll(start, range);

			long size = 0;
			long totSize = 0;
			long totMobilesize = 0;
			//showSegmentRulePanel(false, segmentRules);
			//TODO need to get the count of contacts matching to this segment rule
			SegmentRules updateSegRule = null;
			for (SegmentRules segmentRules : retList) {
				
				
				
				String generatedquery = segmentRules.getEmailSegQuery();
				
				
				List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
				Set<MailingList> mlSet = new HashSet<MailingList>();
				long mlBit = 0;
				
				if(mlList != null)  {
					mlSet.addAll(mlList);
					mlBit = Utility.getMlsBit(mlSet);
					
				}
				//ClickHouse changes
				if(!currentUser.isEnableClickHouseDBFlag())
					generatedquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);
				else
					generatedquery = ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);

				logger.debug("Email :: "+ generatedquery);
				if(generatedquery != null) {
					
					try {
						//ClickHouse changes
						String generatedCountQuery = "";
						if(!currentUser.isEnableClickHouseDBFlag()) {
							generatedCountQuery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);
							size = contactsDao.getSegmentedContactsCount(generatedCountQuery);
						}
						else {
							generatedCountQuery = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);
							size = contactsDao.getSegmentedContactsCountFromCH(generatedCountQuery);
						}

						
						segmentRules.setEmailSegQuery(generatedquery);
						
						segmentRules.setSize(size);
					} catch (Exception e) {
						logger.error("Exception while updating the segments ", e);
						continue;
					}
				}
				
				String generatedTotquery = segmentRules.getTotSegQuery();
				
				
				//ClickHouse changes
				if(!currentUser.isEnableClickHouseDBFlag())
					generatedTotquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
						true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
				else
					generatedTotquery = ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_EXTERNALID, mlBit);

				logger.debug("total :: "+generatedTotquery);
				
				if(generatedTotquery != null) {
					
					try {
						//ClickHouse changes
						String generatedTotCountquery = "";
						if(!currentUser.isEnableClickHouseDBFlag()) {
							generatedTotCountquery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
									true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
							totSize = contactsDao.getSegmentedContactsCount(generatedTotCountquery );
							
						}
						else {							
							generatedTotCountquery = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
									true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
							totSize = contactsDao.getSegmentedContactsCountFromCH(generatedTotCountquery );
						}
						
						segmentRules.setTotSize(totSize);
						segmentRules.setTotSegQuery(generatedTotquery);
					} catch (Exception e) {
						logger.error("Exception while updating the segments ", e);
						continue;
					}
					
				}
				
				/*if(generatedTotquery == null){
				
				
				generatedTotquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
						true, Constants.SEGMENT_ON_EXTERNALID);
				
				if(generatedTotquery ==null) {
					logger.debug("got segment query as null from generator");
					
				}else{
					totSize = contactsDao.getSegmentedContactsCount(generatedTotquery);
					segmentRules.setTotSegQuery(generatedTotquery);
				}
				
				
				
				
				
				
				
				
				
			}
			else if(generatedTotquery != null) {
				
				totSize = contactsDao.getSegmentedContactsCount(generatedTotquery);
				
			}*/
				
				String generatedMobilequery = segmentRules.getMobileSegQuery();
				//ClickHouse changes
				if(!currentUser.isEnableClickHouseDBFlag())
					generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_MOBILE, mlBit);
				else
					generatedMobilequery = ClickHouseSalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_MOBILE, mlBit);

				logger.debug("Mobile :: "+generatedMobilequery);
				
				if(generatedMobilequery != null) {
					
					try {
						//ClickHouse changes
						String generatedMobileCountquery = "";
						if(!currentUser.isEnableClickHouseDBFlag()) {
							generatedMobileCountquery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
									true, Constants.SEGMENT_ON_MOBILE, mlBit);
							totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobileCountquery);
							
						}
						else {							
							generatedMobileCountquery = ClickHouseSalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
									true, Constants.SEGMENT_ON_MOBILE, mlBit);
							totMobilesize = contactsDao.getSegmentedContactsCountFromCH(generatedMobileCountquery);
						}
						segmentRules.setMobileSegQuery(generatedMobilequery);
						segmentRules.setTotMobileSize(totMobilesize);
					} catch (Exception e) {
						logger.error("Exception while updating the segments ", e);
						continue;
					}
					
				}
				
				/*if(generatedMobilequery == null){
				
				
				
				
				generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
						true, Constants.SEGMENT_ON_MOBILE);
				
				if(generatedMobilequery ==null) {
					logger.debug("got segment mobile query as null from generator");
					
				}else{
					totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobilequery);
					segmentRules.setMobileSegQuery(generatedMobilequery);
				}
				
				
				
				
				
				
				
				
				
			}
			else if(generatedMobilequery != null) {
				
				totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobilequery);
				
			}*/
				
				/*segmentRules.setSize(size);
			segmentRules.setTotSize(totSize);
			segmentRules.setTotMobileSize(totMobilesize);*/
				
				updateSegments.add(segmentRules);
				if(updateSegments.size() >= 100) {
					
					
					//segmentRulesDao.saveByCollection(updateSegments);
					segmentRulesDaoForDML.saveByCollection(updateSegments);

					updateSegments.clear();
					
				}//if
				
				//segmentRules.setLastRefreshedOn(Calendar.getInstance());
				
				
			}//for
			if(updateSegments.size() > 0) {
				
				
				//segmentRulesDao.saveByCollection(updateSegments);
				segmentRulesDaoForDML.saveByCollection(updateSegments);

				updateSegments.clear();
				
			}//if
		} catch (WrongValueException e) {
			logger.error("Exception while updating the segments ", e);
		}
	
		
		
	}
	
	
	
	
	public String getPOSDisplayLabel(String columnName,Long userId ) {
		logger.info(" ----4-----------");
		if(columnName.toLowerCase().startsWith("loyalty.") 
				||columnName.toLowerCase().startsWith("date(loyalty.") 
				|| columnName.toLowerCase().startsWith("new_") 
			|| columnName.toLowerCase().trim().startsWith("aggr.") 
			|| columnName.toLowerCase().trim().startsWith("date(aggr.") 
			||columnName.toLowerCase().trim().startsWith("cs.") 
			|| columnName.toLowerCase().trim().startsWith("date(cs.") ){
			logger.info(" ----5-----------");
			return null;
		}
		
		String mappingType = null;
		if(columnName.toLowerCase().startsWith("c.") 
				||columnName.toLowerCase().startsWith("date(c.") ) {
			mappingType = Constants.POS_MAPPING_TYPE_CONTACTS;
		}else if(columnName.toLowerCase().startsWith("sal.") 
				||columnName.toLowerCase().startsWith("date(sal.") ) {
			mappingType = Constants.POS_MAPPING_TYPE_SALES;
		}else if(columnName.toLowerCase().startsWith("sku.") 
				||columnName.toLowerCase().startsWith("date(sku.") ) {
			mappingType = Constants.POS_MAPPING_TYPE_SKU;
		}
		String customfieldName = (columnName.endsWith(")") ? columnName.substring(columnName.indexOf(".")+1,columnName.indexOf(")")) :columnName.substring(columnName.indexOf(".")+1)); 	
		logger.info(" ----6-----------"+customfieldName);
	
		logger.debug("customfieldName ::"+customfieldName);
		List<POSMapping> retList = posMappingDao.findOnlyByType(mappingType, userId);
		for (POSMapping posMapping : retList) {
			logger.info(" ----7-----------"+posMapping.getCustomFieldName());
			if(posMapping.getCustomFieldName().equalsIgnoreCase(customfieldName)) {
				logger.info(" ----7-----------"+customfieldName);
				return posMapping.getDisplayLabel() != null ? posMapping.getDisplayLabel() : null; 
			}
			
		}
		logger.info(" ----8-----------"+customfieldName);
		return null;
	}
	
	
public SegmentEnum getMostParentEnum(SegmentEnum childEnum) {
		
		SegmentEnum parentEnum = childEnum.getParentEnum();
		
		if(parentEnum == null) return childEnum;
		
		return getMostParentEnum(parentEnum);
		
		
		
	}
	
	public List getSegRuleList(int firstResult, int maxResult, String segmentName, String startDate, String endDate) {
		logger.debug("---just entered---6");
		//List retList = segmentRulesDao.findByUser(userIdsSet, Constants.SEGMENT_TYPE_CONTACT, firstResult, maxResult);
		List retList = segmentRulesDao.findByIds(segmentIdsSet, Constants.SEGMENT_TYPE_CONTACT, firstResult, maxResult, segmentName, startDate, endDate);
		
		//logger.debug("got segmentRules::"+retList.size());
		return retList;
	}
	
	
	public void onSelect$pageSizeLbId() {
		int psize = Integer.parseInt(pageSizeLbId.getSelectedItem().getLabel());
		segmentsPagingId.setPageSize(psize);
		
		/*String segmentName = null;
		String startDate = null;
		String endDate = null;
		
		if("segmentName".equalsIgnoreCase(searchByLbId.getSelectedItem().getValue().toString())){
			
			segmentName = searchByTbId.getValue();
			}
		else if("creationDate".equalsIgnoreCase(searchByLbId.getSelectedItem().getValue().toString())){
			
			startDate = getStartDate();
			endDate = getEndDate();
		}*/
		
		Utility.refreshGridModel(contactLbELObj, 0, false);
		
		/*List retList = getSegRuleList(0, segmentsPagingId.getPageSize(), segmentName, startDate, endDate);
		if(retList != null) {
		
			segmentRulesGridId.setModel(new ListModelList(retList));
			segmentRulesGridId.setRowRenderer(rowRender);
		
		}*/
		/*segmentRulesGridId.setModel(new ListModelList(getSegRuleList(0, segmentsPagingId.getPageSize())));
		segmentRulesGridId.setRowRenderer(rowRender);
		*/
		
	}
	private Toolbarbutton createSegmentTBarBtnId;
	public void onClick$createSegmentTBarBtnId(){
		Redirect.goTo(PageListEnum.CONTACT_CREATE_SEGMENTS);
	}
	
	private Textbox viewSegRuleWinId$sendEmailTxtBxId,viewSegRuleWinId$sendMobileTxtBxId;
	
	public void onClick$downloadPageLinkId1$viewSegRuleWinId(){
		fileExportThread("", "");
		Redirect.goTo(PageListEnum.CONTACT_FILE_DOWNLOAD);
	}
	
	public void onClick$downloadPageLinkId2$viewSegRuleWinId(){
		fileExportThread("", "");
		Redirect.goTo(PageListEnum.CONTACT_FILE_DOWNLOAD);
	}
	public void onClick$downloadPageLinkId3$viewSegRuleWinId(){
		fileExportThread("", "");
		Redirect.goTo(PageListEnum.CONTACT_FILE_DOWNLOAD);
	}
	public void onClick$okBtnId$viewSegRuleWinId(){
		fileExportThread("", "");
	}
	
	public void onClick$notifyMeBtnId$viewSegRuleWinId(){
		
		if(viewSegRuleWinId$sendEmailTxtBxId.getValue().isEmpty() && 
				viewSegRuleWinId$sendMobileTxtBxId.getValue().trim().isEmpty()) {
//			MessageUtil.setMessage("Please provide email or moblile number.", "color:red", "TOP");
			MessageUtil.setMessage("Please provide email ", "color:red", "TOP");
			return;
		}
		
		viewSegRuleWinId$sendEmailTxtBxId.setStyle("border:1px solid #7F9DB9;");
		viewSegRuleWinId$sendMobileTxtBxId.setStyle("border:1px solid #7F9DB9;");
		
		String phoneNumber = viewSegRuleWinId$sendMobileTxtBxId.getValue().trim();
		
		if(viewSegRuleWinId$sendEmailTxtBxId.getValue().trim().length() > 0) {
			if(!Utility.validateEmail(viewSegRuleWinId$sendEmailTxtBxId.getValue().trim())){
				viewSegRuleWinId$sendEmailTxtBxId.setStyle("border:1px solid #DD7870;");
				MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
				return;
			}
		}
		
		if(viewSegRuleWinId$sendMobileTxtBxId.getValue().trim().length() > 0) {
			UserOrganization organization = currentUser != null ? currentUser.getUserOrganization() : null ;
			phoneNumber = Utility.phoneParse(phoneNumber,organization);
			if(phoneNumber == null || phoneNumber.trim().isEmpty()){
				viewSegRuleWinId$sendMobileTxtBxId.setStyle("border:1px solid #DD7870;");
				MessageUtil.setMessage("Please provide valid data for the fields in red.", "color:red", "TOP");
				return;
			}
		}
		
		fileExportThread(viewSegRuleWinId$sendEmailTxtBxId.getValue().trim(), phoneNumber);
		
	} // onClick$startExprtBtnId$viewSegRuleWinId
	
	
	private void fileExportThread(String emailStr, String mobileStr){

		
		
		if(exportSegmentRuleObj == null ) {
			logger.error("export segement rule is null ..and returning :: "+exportSegmentRuleObj);
			return;
		}
		String fileNameStr = viewSegRuleWinId$fileNameLblId.getValue();
		
		String userName = GetUser.getLoginUserObj().getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String filePath = usersParentDirectory + "/" + userName + "/List/Export_Files/"+fileNameStr;
		logger.info("query is :::: "+query);
		Object[] obj = {"segment",exportSegmentRuleObj,headerStr,
					viewSegRuleWinId$segDownloadTypeLbId.getSelectedItem().getValue(),
					filePath,displayPosMapObj,
					emailStr,mobileStr,
					GetUser.getLoginUserObj(),query};
		ExportThread exportThread = new ExportThread(); 
		exportThread.uploadQueue.add(obj);
		Thread thread =new Thread(exportThread);
		thread.start();
		
		
		//MessageUtil.setMessage("File exporting in moment and its available in Downloads with the file name is :"+fileNameStr, "color:green");
		
		
		viewSegRuleWinId.setVisible(false);
	
	}
	
	
	
	
	
	SegmentRules exportSegmentRuleObj = null;
	
	
	Listbox viewSegRuleWinId$segDownloadTypeLbId;
	String query = Constants.STRING_NILL;
	String headerStr = Constants.STRING_NILL;
	public void onClick$selectFieldBtnId$viewSegRuleWinId(){
		
		String segDownloadType = (String)viewSegRuleWinId$segDownloadTypeLbId.getSelectedItem().getValue();
		if(segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_EXTERNALID) && exportSegmentRuleObj.getTotSize() <= 0 || 
		   segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_EMAIL) && exportSegmentRuleObj.getSize() <= 0 || 
		   segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_MOBILE) && exportSegmentRuleObj.getTotMobileSize() <= 0)
		{
			MessageUtil.setMessage("No contacts found.", "color:red;");
			return;
		}
		
		query = "select ";
		viewSegRuleWinId.setVisible(false);	
		boolean isChecked = false;
		List<Component> chekBoxList = viewSegRuleWinId$chkDivId.getChildren();
		for (Object eachObj : chekBoxList) {
			Checkbox tempChk = (Checkbox)eachObj;
			if(!tempChk.isChecked()){
				continue;
			}
			isChecked = true;
		
			if(headerStr.trim().length()  > 0) {
				headerStr +=",";
			}
			headerStr += "\""+tempChk.getLabel()+"\"";
			if(query.indexOf((String)tempChk.getValue()) == -1 ) {
			query += tempChk.getValue();
			}
			
		}
		query = query.substring(0, query.length()-2);
		String segmentQuery = Constants.STRING_NILL;
		if (currentUser.isEnableClickHouseDBFlag()) {
			long mlsbit = 0;
			String mListIdsStr = exportSegmentRuleObj.getSegmentMlistIdsStr(); //11,22,33
			List<MailingList> mlList = mailingListDao.findByIds(mListIdsStr);
			for (MailingList mailingList : mlList) {
				mlsbit += mailingList.getMlBit();
			}
			logger.info("BITS :: "+ mlsbit);
			if (segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_EXTERNALID)) {
				segmentQuery = SalesQueryGenerator.generateListSegmentQuery(exportSegmentRuleObj.getSegRule(), true, Constants.SEGMENT_ON_EXTERNALID, mlsbit);
			} else if (segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_EMAIL)) {
				segmentQuery = SalesQueryGenerator.generateListSegmentQuery(exportSegmentRuleObj.getSegRule(), false, Constants.SEGMENT_ON_EMAIL, mlsbit);
			} else if (segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_MOBILE)) {
				segmentQuery = SalesQueryGenerator.generateListSegmentQuery(exportSegmentRuleObj.getSegRule(), false, Constants.SEGMENT_ON_MOBILE, mlsbit);
			}
		} else {
			if (segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_EXTERNALID)) {
				segmentQuery = exportSegmentRuleObj.getTotSegQuery();
			} else if (segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_EMAIL)) {
				segmentQuery = exportSegmentRuleObj.getEmailSegQuery();
			} else if (segDownloadType.equalsIgnoreCase(Constants.SEGMENT_ON_MOBILE)) {
				segmentQuery = exportSegmentRuleObj.getMobileSegQuery();
			}
		}
		
		
		logger.info("segmentQuery "+segmentQuery);
		//queryquery += " from ("+segmentQuery+") as c left join contacts_loyalty cl on c.cid = cl.contact_id WHERE cl.user_id="+exportSegmentRuleObj.getUserId().longValue();
		if(segmentQuery.startsWith(OCConstants.replacedSegmentQryPrefiX)){
            //query =segmentQuery;
			query += " from ("+segmentQuery+") as c";
		    query = query.replace("cl.","c.");
		    query = query.replace("contacts_loyalty cl","contacts_loyalty c");
        }
        else{
           // query += " from ("+segmentQuery+") as c left join contacts_loyalty cl on c.cid = cl.contact_id ";
            
            query += " FROM ("+segmentQuery+") AS c LEFT JOIN (SELECT * FROM contacts_loyalty WHERE user_id="+
    			    exportSegmentRuleObj.getUserId().longValue()+") AS cl ON c.cid = cl.contact_id";
        }
		/*query += " FROM ("+segmentQuery+") AS c LEFT JOIN (SELECT * FROM contacts_loyalty WHERE user_id="+
			    exportSegmentRuleObj.getUserId().longValue()+") AS cl ON c.cid = cl.contact_id";*/
		
		if(!isChecked) {
			 MessageUtil.setMessage("Please select any one to export.", "color:red;");
			 return;
		}
		
		Calendar cal = Calendar.getInstance();
		logger.info("===2====");
		logger.info("segmentRules is  ::"+exportSegmentRuleObj);
		String fileNameStr = exportSegmentRuleObj.getSegRuleName()+"_"+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_MONTH_TIME)+".csv";
		viewSegRuleWinId$fileNameLblId.setValue(fileNameStr);
		viewSegRuleWinId$sendEmailTxtBxId.setValue("");
		viewSegRuleWinId$sendMobileTxtBxId.setValue("");
		viewSegRuleWinId.setWidth("600px");
		openSubWindow(viewSegRuleWinId$viewDownloadSegDivId);
		
		
		/*
		
		
		
		
		chckedChkBoxList = new ArrayList<Checkbox>();
		
		logger.info("onClick$selectFieldBtnId$viewSegRuleWinId clicked");
		
		List chekBoxList = viewSegRuleWinId$chkDivId.getChildren();
		for (Object eachObj : chekBoxList) {
			Checkbox tempChk = (Checkbox)eachObj;
			if(tempChk.isChecked()){
				chckedChkBoxList.add(tempChk);
			}
		}
		
		logger.info("chked chk box list size is  :"+chckedChkBoxList.size());
		
		if(chckedChkBoxList == null || chckedChkBoxList.size() == 0) {
			 MessageUtil.setMessage("Please select any one to export.", "color:red;");
			 return;
		}
		
		Calendar cal = Calendar.getInstance();
		logger.info("===2====");
		logger.info("segmentRules is  ::"+exportSegmentRuleObj);
		String fileNameStr = exportSegmentRuleObj.getSegRuleName()+"_"+MyCalendar.calendarToString(cal, MyCalendar.FORMAT_DATE_MONTH_TIME)+".csv";
		viewSegRuleWinId$fileNameLblId.setValue(fileNameStr);
		viewSegRuleWinId$sendEmailTxtBxId.setValue("");
		viewSegRuleWinId$sendMobileTxtBxId.setValue("");
		viewSegRuleWinId.setWidth("600px");
		openSubWindow(viewSegRuleWinId$viewDownloadSegDivId);
		
		*/
		
		
	} // onClick$selectFieldBtnId$viewSegRuleWinId
	
	private void openSubWindow(Div openDivId){
		Div[] tempDiv = {viewSegRuleWinId$previewSegDivId,viewSegRuleWinId$custFieldExprtDivId,viewSegRuleWinId$viewDownloadSegDivId};
		for (Div eachDiv : tempDiv) {
			if(openDivId.getId().equals(eachDiv.getId())){
				logger.info(">. is Visisble true");
				eachDiv.setVisible(true);
			}else {
//				logger.info("is visible false ::");
				eachDiv.setVisible(false);
			}
		}
//		viewSegRuleWinId.doModal();
		viewSegRuleWinId.doHighlighted();
	}
	
	
	
	
//	private Div custExport$chkDivId;
	/*public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;
			
			tempChk = (Checkbox)chkList.get(i);
			tempChk.setChecked(flag);
			
		} // for

	}*/

	
	//Labels
	private String[] contactLoyaltyLabelsTwo= new String[]{"Points","Reward Balance","Gift Balance","Hold Points","Hold Currency"};
	public void createWindow()	{

		try {
			if(viewSegRuleWinId$chkDivId.getChildren() != null && viewSegRuleWinId$chkDivId.getChildren().size() > 0) {
				Components.removeAllChildren(viewSegRuleWinId$chkDivId);
			}
			
			if(currentUser == null) {

				logger.debug("do not Export as user is null....");
				return ;
			}
			Map<String, POSMapping> orderedMappingMap = new HashMap<String, POSMapping>();
			
			List<POSMapping> posMappingsList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_CONTACTS+"'", currentUser.getUserId());
			
			if(posMappingsList == null  || posMappingsList.size() == 0) {
				return ;
			}

			
			
			List<String[]> headerFields = getHeaders(posMappingsList);
			
			//Created Date
			Checkbox tempChk1 = new Checkbox("Created Date");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.created_date, ");
			tempChk1.setParent(viewSegRuleWinId$chkDivId);

			//Email
			tempChk1 = new Checkbox("Email");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.email_id, ");
			tempChk1.setParent(viewSegRuleWinId$chkDivId);

			//Card Number
			tempChk1 = new Checkbox("Card Number");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("cl.card_number, ");
			tempChk1.setParent(viewSegRuleWinId$chkDivId);

			//Card Pin
			tempChk1 = new Checkbox("Card Pin");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("cl.card_pin, ");
			tempChk1.setParent(viewSegRuleWinId$chkDivId);

			tempChk1 = new Checkbox("Mobile Number");
			tempChk1.setSclass("custCheck");
			tempChk1.setValue("c.mobile_phone, ");
			tempChk1.setParent(viewSegRuleWinId$chkDivId);
			logger.info(">>>size is  ::"+viewSegRuleWinId$chkDivId.getChildren().size()+" is visible ::"+viewSegRuleWinId$chkDivId.isVisible());


			Checkbox tempChk=null;
			if(headerFields!=null) {

				////Point Balance & Reward Balance
				for (int i = 0; i < contactLoyaltyLabelsTwo.length; i++) {

					tempChk=new Checkbox(contactLoyaltyLabelsTwo[i]);
					tempChk.setSclass("custCheck");
					tempChk.setParent(viewSegRuleWinId$chkDivId);
					if(i==0) {
						tempChk.setValue("cl.loyalty_balance, ");
					}
					else if(i == 1) {
						tempChk.setValue("cl.giftcard_balance, ");
					}
					else if(i == 2) {
						tempChk.setValue("cl.gift_balance, ");
					}
					else if(i == 3) {
						tempChk.setValue("cl.holdpoints_balance, ");
					}
					else {
						tempChk.setValue("cl.holdAmount_balance, ");
					}
				}

				int size = headerFields.size();
				for (int i = 0; i < size; i++) {
					
					tempChk = new Checkbox(headerFields.get(i)[0]);
					tempChk.setSclass("custCheck");
					tempChk.setValue(headerFields.get(i)[1]);
					tempChk.setParent(viewSegRuleWinId$chkDivId);
				} // for

			}

			else {

				MessageUtil.setMessage("There is no field to export ", "info");
				return;
			}


		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//create window
	
	
	public void onClick$selectAllAnchr$viewSegRuleWinId() {
		logger.info("selectAllAnchr clicked");
		anchorEvent(true);
	}

	public void onClick$clearAllAnchr$viewSegRuleWinId() {
		logger.info("clearAllAnchr clicked");
		anchorEvent(false);
	}
	
//	Div viewSegRuleWinId$chkDivId;
	public void anchorEvent(boolean flag) {
		List<Component> chkList = viewSegRuleWinId$chkDivId.getChildren();
		Checkbox tempChk = null;
		for (int i = 0; i < chkList.size(); i++) {
			if(!(chkList.get(i) instanceof Checkbox)) continue;
			
			tempChk = (Checkbox)chkList.get(i);
			tempChk.setChecked(flag);
			
		} // for

	}
	
	
	public void onClick$viewDownloadsTBarBtnId(){
		Redirect.goTo(PageListEnum.CONTACT_FILE_DOWNLOAD);
	}//onClick$viewDownloadsTBarBtnId
	
//	UsersDao usersDao;
	Map<String,POSMapping> displayPosMapObj = new HashMap<String, POSMapping>();
	public List<String[]> getHeaders(List<POSMapping> posMappingsList) {

		/*int headdersCount = 0;
		for (POSMapping eachPos : posMappingsList) {
			displayPosMapObj.put(eachPos.getDisplayLabel(), eachPos);
			if(eachPos.getCustomFieldName().equals(POSFieldsEnum.emailId.getOcAttr())
					|| eachPos.getCustomFieldName().equals(POSFieldsEnum.mobilePhone.getOcAttr()) 
					|| eachPos.getCustomFieldName().equals(POSFieldsEnum.createdDate.getOcAttr())){
				continue;
			}
			headdersCount++;
		}*/
		
		List<String[]> headers = new ArrayList<String[]>();
		String[] headerWithDbValue = null;
		for (POSMapping eachPos : posMappingsList) {
			
			if(eachPos.getCustomFieldName().equals(POSFieldsEnum.emailId.getOcAttr())
					|| eachPos.getCustomFieldName().equals(POSFieldsEnum.mobilePhone.getOcAttr()) 
					|| eachPos.getCustomFieldName().equals(POSFieldsEnum.createdDate.getOcAttr())){
				continue;
			}
			headerWithDbValue = new String[2];
			headerWithDbValue[0] = eachPos.getDisplayLabel().trim();
			headerWithDbValue[1] = "c."+POSFieldsEnum.findByOCAttribute(eachPos.getCustomFieldName()).getDbColumn()+", ";
			

			if(headerWithDbValue[0] !=null) {
				headers.add(headerWithDbValue);

			}
//			orderedMappingMap.put(eachPos.getDisplayLabel().trim(), eachPos);
			
		} // 
		
	
		return headers;


	}
	
	public void onSelect$searchByLbId() {
		String value = searchByLbId.getSelectedItem().getValue().toString();
		
		if(value.equals(SEARCH_BY_NAME)){
			
			viewSegmentWinId$searchBySegmentNameDivId.setVisible(true);
			searchByTbId.setVisible(true);
			searchByTbId.setText(Constants.STRING_NILL);
			/*fromDateboxId.setVisible(false);
    		toDateboxId.setVisible(false);*/
			viewSegmentWinId$searchBySegmentDateDivId.setVisible(false);
			
			
		}
		
		else if(value.equals(SEARCH_BY_DATE)){
			
			
			searchByTbId.setVisible(false);
			viewSegmentWinId$searchBySegmentDateDivId.setVisible(true);
			/*fromDateboxId.setVisible(true);
    		toDateboxId.setVisible(true);*/
			fromDateboxId.setText(Constants.STRING_NILL);
			toDateboxId.setText(Constants.STRING_NILL);
			
		}
		
		else if(value.equals(ALL)){
			
			searchByTbId.setVisible(false);
			viewSegmentWinId$searchBySegmentDateDivId.setVisible(false);
		}
    	
	}
	
	
	public String getStartDate(){
		
		if(fromDateboxId.getValue() != null)
		{
		Calendar serverFromDateCal = fromDateboxId.getServerValue();
		Calendar tempClientFromCal = fromDateboxId.getClientValue();
		serverFromDateCal.set(Calendar.HOUR_OF_DAY, 
				serverFromDateCal.get(Calendar.HOUR_OF_DAY)-tempClientFromCal.get(Calendar.HOUR_OF_DAY));
		serverFromDateCal.set(Calendar.MINUTE, 
				serverFromDateCal.get(Calendar.MINUTE)-tempClientFromCal.get(Calendar.MINUTE));
		serverFromDateCal.set(Calendar.SECOND, 0);
		String fromDate = MyCalendar.calendarToString(serverFromDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		return fromDate;
		}
		else 
		{
			return null;
		}
		
	}

	public String getEndDate() {
		
		if(toDateboxId.getValue() != null)
		{
		Calendar serverToDateCal = toDateboxId.getServerValue();
		
		Calendar tempClientToCal = toDateboxId.getClientValue();
		
		
		//change the time for startDate and endDate in order to consider right from the 
		// starting time of startDate to ending time of endDate
		
		serverToDateCal.set(Calendar.HOUR_OF_DAY, 
				23+serverToDateCal.get(Calendar.HOUR_OF_DAY)-tempClientToCal.get(Calendar.HOUR_OF_DAY));
		serverToDateCal.set(Calendar.MINUTE, 
				59+serverToDateCal.get(Calendar.MINUTE)-tempClientToCal.get(Calendar.MINUTE));
		serverToDateCal.set(Calendar.SECOND, 59);
		
		String endDate = MyCalendar.calendarToString(serverToDateCal, MyCalendar.FORMAT_DATETIME_STYEAR);
		
		
		return endDate;
		}
		else 
		{
			return null;
		}
		
		}
	

	public void onClick$filterBtnId(){
		
		try{
			
			/*if(searchByLbId.getSelectedIndex() == 0){
				
				MessageUtil.setMessage("Please select at least one item in listbox.", "red");
				return;
			}
*/
		/*String segmentName = null;
		String startDate = null;
		String endDate = null;
		String value = searchByLbId.getSelectedItem().getValue().toString();
		
		if(value.equals(SEARCH_BY_NAME)){
			
			if(searchByTbId.getValue().isEmpty()){
				
				MessageUtil.setMessage("Please enter Segment Name.", "red");
				return;
			}
			segmentName = searchByTbId.getValue().trim();
			}
		
		else if(value.equals(SEARCH_BY_DATE)){
			
			if(fromDateboxId.getServerValue() == null || toDateboxId.getServerValue() == null) {
				MessageUtil.setMessage("Please specify the dates", "red");
				return;
			}
						
			if(toDateboxId.getClientValue().before(fromDateboxId.getClientValue())) {
				MessageUtil.setMessage("'From' date cannot be later than 'To' date.", "red");
				return;
			}
			startDate = getStartDate();
			endDate = getEndDate();
		}
		
		int totalSize = segmentRulesDao.findCountByIds(segmentIdsSet, segmentName, startDate, endDate);
		segmentsPagingId.setTotalSize(totalSize);
		List retList = getSegRuleList(0, segmentsPagingId.getPageSize(), segmentName, startDate, endDate);
		if(retList != null) {
			segmentRulesGridId.setModel(new ListModelList(retList));
		}
		else {
			segmentRulesGridId.setModel(new ListModelList());
		}
		segmentRulesGridId.setRowRenderer(rowRender);*/
		registerEventListner(0, segmentsPagingId.getPageSize(), true);
		Utility.refreshGridModel(contactLbELObj, 0, true);
			
		
	} catch (Exception e) {
		logger.error("Exception ::" , e);
	}
		//searchByTbId.setValue("");
	}	
	
	public void onClick$resetAnchId() {
		
		searchByLbId.setSelectedIndex(0);
		searchByTbId.setValue(Constants.STRING_NILL);
		searchByTbId.setVisible(true);
		
		viewSegmentWinId$searchBySegmentDateDivId.setVisible(false);
		Date frmAndTodateDef = null;
		fromDateboxId.setValue(frmAndTodateDef);
		toDateboxId.setValue(frmAndTodateDef);
		
		/*String segmentName = null;
		String startDate = null;
		String endDate = null;
		int totalSize = segmentRulesDao.findCountByIds(segmentIdsSet, segmentName, startDate, endDate);
		segmentsPagingId.setTotalSize(totalSize);
		List retList = getSegRuleList(0, segmentsPagingId.getPageSize(), segmentName, startDate, endDate);
		if(retList != null) {
			
			segmentRulesGridId.setModel(new ListModelList(retList));
			segmentRulesGridId.setRowRenderer(rowRender);
			
			
		}*/
		registerEventListner(0, segmentsPagingId.getPageSize(), false);
		Utility.refreshGridModel(contactLbELObj, 0, true);
		
	}
	

	private class MyRenderer implements RowRenderer,EventListener {
		MyRenderer() {
			super();
			logger.debug("new MyRenderer object is created");
		}
		
		public void render(Row row, java.lang.Object data,int arg2) {
			
			try {
				int size = 0;
				String generatedquery=null;
			if(data instanceof SegmentRules) {
				SegmentRules segmentRules = (SegmentRules) data;
			
				row.setValue(segmentRules);
				
				
				Label label = new Label(segmentRules.getSegRuleName());
				label.setMaxlength(25);
				label.setTooltiptext(segmentRules.getSegRuleName());
				
				label.setParent(row);
				
				
				
				//new Label(segmentRules.getSegRuleName()).setParent(row);
				
				new Label(segmentRules.getDescription()).setParent(row);
				
				/*generatedquery = segmentRules.getEmailSegQuery();
				if(generatedquery != null) {
					
					size = contactsDao.getSegmentedContactsCount(generatedquery);
					
				}*/
				
				String listnamesStr = "";
				String listIdsStr = segmentRules.getSegmentMlistIdsStr();
				if(listIdsStr == null || listIdsStr.length() == 0) {
					
					listIdsStr = segmentRules.getListId().longValue()+"";
				}
				
				List<MailingList> mlList = mailingListDao.findByIds(listIdsStr);
				
				if(mlList != null) {
					
					for (MailingList mailingList : mlList) {
						
						
						
						if(listnamesStr != null && listnamesStr.length() > 0) listnamesStr += ", ";
						listnamesStr += mailingList.getListName();
						
					}//for
					
				}
				
				
				new Label(listnamesStr).setParent(row);
				
				new Label(MyCalendar.calendarToString(segmentRules.getCreatedDate(),
						MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)).setParent(row);
				
				new Label(MyCalendar.calendarToString(segmentRules.getLastRefreshedOn(),
						MyCalendar.FORMAT_DATETIME_STDATE, clientTimeZone)).setParent(row);
				
			
				
				label = new Label(segmentRules.getTotSize()+"");
				label.setStyle("cursor:pointer;color:blue;");
				label.setAttribute("type", "Total");
				label.addEventListener("onClick", this);
				label.setParent(row);
				
				
				
				label = new Label(segmentRules.getSize()+"");
				label.setStyle("cursor:pointer;color:blue;");
				label.setAttribute("type", "Email");
				label.addEventListener("onClick", this);
				label.setParent(row);
				
				label = new Label(segmentRules.getTotMobileSize()+"");
				label.setStyle("cursor:pointer;color:blue;");
				label.setAttribute("type", "Mobile");
				label.addEventListener("onClick", this);
				label.setParent(row);
				
				
				Hbox hbox = new Hbox();
				hbox.setPack("center");
				
				
				Image img = new Image("/images/Refresh-list-size_icn.png");
				img.setTooltiptext("Refresh");
				img.setAttribute("segmentRuleObj", segmentRules);
				img.setAttribute("imageEventName", "refresh");
				img.setStyle("cursor:pointer;padding-right:8px;");
				img.addEventListener("onClick", this);
				//img.setStyle("padding-left:10px");
				img.setParent(hbox);
				
				
				
				img = new Image("/images/Preview_icn.png");
				img.setTooltiptext("Preview");
				img.setAttribute("segmentRuleObj", segmentRules);
				img.setAttribute("imageEventName", "PreviewSegRule");
				img.setStyle("cursor:pointer;padding-right:8px;");
				img.addEventListener("onClick", this);
				//img.setStyle("padding-right:8px");
				img.setParent(hbox);
				
				//TODO need to enable after completing edit code
				
				img = new Image("/images/Edit_icn.png");
				img.setTooltiptext("Edit");
				img.setAttribute("segmentRuleObj", segmentRules);
				img.setAttribute("imageEventName", "editSegRule");
				img.setStyle("cursor:pointer;padding-right:8px;");
				img.addEventListener("onClick", this);
				//img.setStyle("padding-right:8px");
				img.setParent(hbox);
				
				
				//Export
				img = new Image("/img/icons/Export-of-Promo-codes-icon.png");
				img.setTooltiptext("Export");
				img.setAttribute("segmentRuleObj", segmentRules);
				img.setAttribute("imageEventName", "export");
				img.setStyle("cursor:pointer;padding-right:8px;");
				img.addEventListener("onClick", this);
				//img.setStyle("padding-right:8px");
				img.setParent(hbox);
				
				
				img = new Image("/images/Delete_icn.png");
				img.setTooltiptext("Delete");
				img.setAttribute("segmentRuleObj", segmentRules);
				img.setAttribute("imageEventName", "deletesegRule");
				img.setStyle("cursor:pointer;");
				img.addEventListener("onClick", this);
				//img.setStyle("padding-right:8px");
				img.setParent(hbox);
				
				hbox.setParent(row);
				
			} // if
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}

		} // render
		
		public String dispRule(String rule) {
			String dispRule = "";
			String option=null;
			String campaignId = null;
			String campName = "";
			if(rule != null) {
				
				
				String[] rowsArr = rule.split("\\|\\|");
				String[] columnsArr; 
				
				
				columnsArr = rowsArr[0].split(":");
				if(columnsArr.length > 0) {
					
					if(columnsArr[0].trim().equalsIgnoreCase("Any") ) { 
						option = "OR";
					} 
					else {
						option = "AND";
					}
					
					if(columnsArr.length > 2) {
						
						campaignId = columnsArr[2];
						if(campaignId != null && !campaignId.isEmpty()) {
							
							List<Campaigns> campLst = campaignsDao.getCampaignById((campaignId));
							if(campLst != null) { 
							for (Campaigns campaigns : campLst) {
								
								if(!campName.isEmpty()) campName += ", ";
								campName +=  (campaigns != null ? campaigns.getCampaignName() : "");
							}
							}
						}
						
						//StringTokenizer tokenizerr = new StringTokenizer(columnsArr, Constants.DELIMETER_COMMA);
						//numOfCampaigns = tokenizerr.countTokens();
					}//if
					
				}//if
				
				
				String[] tempStrArr = null;
				String fieldNameStr = null;
				String itemStr = null;
				String dataTypeStr = null;
				String constraintStr = null;
				String data1 = null;
				String data2 = null;
				String data = "";
				String[] tokenArr = null;
				
				for(int i=1;i<rowsArr.length;i++) {
					
					tokenArr = rowsArr[i].split("<OR>");
					String innerRule = "";
					for (String token : tokenArr) {
						
						columnsArr = token.split("\\|");
						if(innerRule.length()>0) innerRule += " "+"OR"+" ";
						
						itemStr = columnsArr[0].trim();
						fieldNameStr = columnsArr[1].trim();
						tempStrArr = columnsArr[2].trim().split(":");
						dataTypeStr = tempStrArr[0].toUpperCase().trim();
						constraintStr = tempStrArr[1];
						
						data = data1 = (columnsArr.length>3)?columnsArr[3]:"";
						
						
						logger.debug("fieldNameStr :"+fieldNameStr);
						logger.debug("dataTypeStr :"+dataTypeStr);
						logger.debug("constraintStr :"+constraintStr);
						logger.debug("data1 :"+data1);
						logger.debug("data2 :"+data2);
						
						/*if(fieldNameStr.trim().startsWith("cs.")) {
							
							if((columnsArr.length>5)) {
								campaignId = columnsArr[5].trim().length()>0 ? columnsArr[5] : null;
								data2 = columnsArr[4].trim().length()>0 ? columnsArr[4] : "";;
							}
							
							else if(columnsArr.length==5 ){
								
								campaignId = columnsArr[4].trim().length()>0 ? columnsArr[4] : null;
								
							}
							
							
							if(campaignId != null) {
								
								List<Campaigns> campLst = campaignsDao.getCampaignById((campaignId));
								if(campLst == null) continue;
								
								for (Campaigns campaigns : campLst) {
									
									if(!campName.isEmpty()) campName += ", ";
									campName +=  (campaigns != null ? campaigns.getCampaignName() : "");
								}
								
							}
							
							
							if(data2 != null && campName.trim().length()>0){
								data = data1+" , "+data2+ " IN Campaign: "+campName;
							}else if(data2 == null && campName.trim().length()>0) {
								data = data1+ " IN Campaign: "+campName;
							}
							
						}*/
						if(itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_CLICKS.getItem()) 
								|| itemStr.equalsIgnoreCase(SegmentEnum.INTERACTION_OPENS.getItem()) ) {
							
							/*if((columnsArr.length>5)) {
								campaignId = columnsArr[5].trim().length()>0 ? columnsArr[5] : null;
								data2 = columnsArr[4].trim().length()>0 ? columnsArr[4] : "";;
							}
							
							else if(columnsArr.length==5 ){
								
								campaignId = columnsArr[4].trim().length()>0 ? columnsArr[4] : null;
								
							}*/
							
							SegmentEnum retEnum = SegmentEnum.getEnumByColumn(fieldNameStr);
							
							if(retEnum != null) {
								
								fieldNameStr = retEnum.getParentEnum().getDispLabel();
								
								constraintStr = retEnum.getDispLabel() +  " IN Campaign(s): "+campName;
								data = "";
							}//if
							
							
							
							/*if(data2 != null && campName.trim().length()>0){
								data = data1+" , "+data2+ " IN Campaign: "+campName;
							}else if(data2 == null && campName.trim().length()>0) {
								data = data1+ " IN Campaign: "+campName;
							}*/
							
						}
						else{
							
							data2 = (columnsArr.length>4)?columnsArr[4]:"";
							if(data2 != null ){
								data = data1+" , "+data2;
							}
						}
						
						
						innerRule += "("+fieldNameStr+" "+constraintStr+" "+data+")";
						
						
					}//for 
					
					
					if(dispRule.length()>0) dispRule += " "+option+" ";
					dispRule += "("+innerRule+")";
					
					
					
				} // outer for
				
				
			
			}
			
			
			
			return dispRule;
			
		}
		
		
		
		@Override
		public void onEvent(Event evt) throws Exception {
			
			Object obj = evt.getTarget();
			if(obj instanceof Image) {
				
				Image img = (Image)obj;
				Row row = (Row)img.getParent().getParent();
				SegmentRules segmentRules = (SegmentRules)img.getAttribute("segmentRuleObj");
				
				if(img.getAttribute("imageEventName").equals("editSegRule")) {
					
					//showSegmentRulePanel(false, segmentRules);
					//TODO need to go createSegment.zul with edit mode
					segmentRules.setModifiedDate(Calendar.getInstance());
					segmentRulesDaoForDML.saveOrUpdate(segmentRules);
					
					session.setAttribute("editSegment", segmentRules);
					
					Redirect.goTo(PageListEnum.CONTACT_MANAGE_SEGMENTS);
					
					
				}
				
				else if(img.getAttribute("imageEventName").equals("refresh")) {
					
					if(segmentRules == null){
						logger.info("Segment rule not found ... returning");
						return;
					} 
				 
					MessageUtil.setMessage("Your segment is being refreshed to reflect the most recent count.\n Please check back momentarily to see the updated numbers.",  "color:green;");
					
					RefreshSegmentThread refreshSegThread = new RefreshSegmentThread();
					Object[] threadObj = {segmentRules};
					logger.info("segmentRuleID::::"+segmentRules.getSegRuleId());
					refreshSegThread.uploadQueue.add(threadObj);
					Thread segmentThread =new Thread(refreshSegThread);
					segmentThread.start();
					segmentThread.join();
					Utility.refreshGridModel(contactLbELObj, 0, true);
					
					
					/*
					
					
					
					
					long size = 0;
					long totSize = 0;
					long totMobilesize = 0;
					//showSegmentRulePanel(false, segmentRules);
					//TODO need to get the count of contacts matching to this segment rule
					List<MailingList> mlList = mailingListDao.findByIds(segmentRules.getSegmentMlistIdsStr());
					Set<MailingList> mlSet = new HashSet<MailingList>();
					long mlBit = 0;
					
					if(mlList != null)  {
					mlSet.addAll(mlList);
					 mlBit = Utility.getMlsBit(mlSet);
					
					}
					
					//String generatedquery = segmentRules.getEmailSegQuery();
					
					
					
					
					String generatedquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);
					String generatedTotquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
					String generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_MOBILE, mlBit);
					
					
					
					
					String generatedCountQuery =  null;
					String generatedTotCountquery = null;
					String generatedMobileCountquery = null;
					
					if(generatedquery != null) {
						
						 generatedCountQuery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(), true, Constants.SEGMENT_ON_EMAIL,mlBit);
						
						size = contactsDao.getSegmentedContactsCount(generatedCountQuery);
						
						segmentRules.setEmailSegQuery(generatedquery);
						
						segmentRules.setSize(size);
						
					}
					
					//String generatedTotquery = segmentRules.getTotSegQuery();
					
					
					
					String generatedTotquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
					logger.debug("total :: "+generatedTotquery);
					
					if(generatedTotquery != null) {
						
						 generatedTotCountquery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
								true, Constants.SEGMENT_ON_EXTERNALID, mlBit);
						totSize = contactsDao.getSegmentedContactsCount(generatedTotCountquery );
						
						segmentRules.setTotSize(totSize);
						segmentRules.setTotSegQuery(generatedTotquery);
						
					}
					
					if(generatedTotquery == null){
						
						
						generatedTotquery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
								true, Constants.SEGMENT_ON_EXTERNALID);
						
						if(generatedTotquery ==null) {
							logger.debug("got segment query as null from generator");
							
						}else{
							totSize = contactsDao.getSegmentedContactsCount(generatedTotquery);
							segmentRules.setTotSegQuery(generatedTotquery);
						}
						
						
						
						
						
						
						
						
						
					}
					else if(generatedTotquery != null) {
						
						totSize = contactsDao.getSegmentedContactsCount(generatedTotquery);
						
					}
					
					//String generatedMobilequery = segmentRules.getMobileSegQuery();
					generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
							true, Constants.SEGMENT_ON_MOBILE, mlBit);
					
					

					if(generatedMobilequery != null) {
						
						 generatedMobileCountquery = SalesQueryGenerator.generateListSegmentCountQuery(segmentRules.getSegRule(),
								true, Constants.SEGMENT_ON_MOBILE, mlBit);
						totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobileCountquery);
						segmentRules.setMobileSegQuery(generatedMobilequery);
						segmentRules.setTotMobileSize(totMobilesize);
						
					}
					
					if(generatedMobilequery == null){
						
						
						
						
						generatedMobilequery = SalesQueryGenerator.generateListSegmentQuery(segmentRules.getSegRule(),
								true, Constants.SEGMENT_ON_MOBILE);
						
						if(generatedMobilequery ==null) {
							logger.debug("got segment mobile query as null from generator");
							
						}else{
							totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobilequery);
							segmentRules.setMobileSegQuery(generatedMobilequery);
						}
						
						
						
						
						
						
						
						
						
					}
					else if(generatedMobilequery != null) {
						
						totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobilequery);
						
					}
					if(SalesQueryGenerator.CheckForIsLatestCamapignIdsFlag(segmentRules.getSegRule())) {
						String csCampIds = SalesQueryGenerator.getCamapignIdsFroFirstToken(segmentRules.getSegRule());
						
						if(csCampIds != null ) {
							String crIDs = Constants.STRING_NILL;
							CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
							List<Object[]> campList = campaignsDao.findAllLatestSentCampaignsBySql(segmentRules.getUserId(), csCampIds);
							if(campList != null) {
								for (Object[] crArr : campList) {
									
									if(!crIDs.isEmpty()) crIDs += Constants.DELIMETER_COMMA;
									crIDs += ((Long)crArr[0]).longValue();
									
								}
							}
							
							if(generatedquery != null) generatedquery = generatedquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
							if(generatedMobilequery != null)  generatedMobilequery = generatedMobilequery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
							if(generatedTotquery != null)  generatedTotquery = generatedTotquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
							
							if(generatedCountQuery != null) generatedCountQuery = generatedCountQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
							if(generatedMobileCountquery != null) generatedMobileCountquery = generatedMobileCountquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
							if(generatedTotCountquery != null) generatedTotCountquery = generatedTotCountquery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, ("AND cr_id in("+crIDs+")"));
							
							//segmentQuery = segmentQuery.replace(Constants.INTERACTION_CAMPAIGN_CRID_PH, crIDs);
						}
					}
					
					if(generatedCountQuery != null ) {
						
						size = contactsDao.getSegmentedContactsCount(generatedCountQuery);
						
						segmentRules.setSize(size);
						
						if(generatedquery != null) segmentRules.setEmailSegQuery(generatedquery);
						
						logger.debug("Email :: "+ generatedquery);
						
					}
					if(generatedTotCountquery != null) {
						 
						totSize = contactsDao.getSegmentedContactsCount(generatedTotCountquery );
						
						segmentRules.setTotSize(totSize);
						
						if(generatedTotquery != null)  segmentRules.setTotSegQuery(generatedTotquery);
						
						logger.debug("total :: "+generatedTotquery);
						
					}
					
					if(generatedMobileCountquery != null) {
						
						totMobilesize = contactsDao.getSegmentedContactsCount(generatedMobileCountquery);
						
						segmentRules.setTotMobileSize(totMobilesize);
						
						if(generatedMobilequery != null)segmentRules.setMobileSegQuery(generatedMobilequery);
						
						logger.debug("Mobile :: "+generatedMobilequery);
						
					}
					
					List list = row.getChildren();
					((Label)list.get(3)).setValue(MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_DATETIME_STDATE,clientTimeZone));
					((Label)list.get(4)).setValue(""+totSize);
					((Label)list.get(5)).setValue(""+size);
					((Label)list.get(6)).setValue(""+totMobilesize);
					
					segmentRules.setSize(size);
					segmentRules.setTotSize(totSize);
					segmentRules.setTotMobileSize(totMobilesize);
					
					
					
					segmentRules.setLastRefreshedOn(Calendar.getInstance());
					segmentRulesDao.saveOrUpdate(segmentRules);
				*/}
				else if(img.getAttribute("imageEventName").equals("deletesegRule")) {
					
					try {
						/*String searchStr = "Segment:"+segmentRules.getSegRuleId();
						CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						Campaigns campaign = campaignsDao.findIfSegmentAssociates(searchStr);*/
						boolean deleteFlagC=false;
						String campIdToDeleteAssociation="";
						String campaignIds="";
						String segRuleIds ="";
						String segMlIds="";
						String searchStr = "Segment:"+segmentRules.getSegRuleId();
						String currSegRuleId=""+segmentRules.getSegRuleId().toString();
						String mlIdStr = segmentRules.getSegmentMlistIdsStr();
						logger.info("Associated mailing lists = "+mlIdStr);
						String comparedList = "";
						String smsComparedList="";
						CampaignsDao campaignsDao = (CampaignsDao)SpringUtil.getBean("campaignsDao");
						
						//List<String> activeConfiguredList = segmentRulesDao.findStatusBySegment(mlIdStr,segmentRules.getUserId(),Constants.MAILINGLIST_CONFIGURED_TYPE_CAMPAIGN);
						
						List<Campaigns> campaignList = campaignsDao.findSegmentAssociates(searchStr,segmentRules.getSegRuleId());
						
						if(campaignList!=null){
						
							/*if(activeConfiguredList.size()!=0) {
							
							
							MessageUtil.setMessage("Selected segment is associated with the campaign "+campaignList+"" +
									"\n and cannot be deleted.", "color:red;");
							return;
							
							}*/
						
							Campaigns camp = null;
							SegmentRules seg=null;
							String segRuleIdsFromListType ="";
							Iterator<Campaigns> camItr = campaignList.iterator();
							while(camItr.hasNext()){
								camp = camItr.next();
								campaignIds +=  campaignIds.length()== 0? camp.getCampaignId().toString():","+camp.getCampaignId().toString();
								segRuleIdsFromListType += segRuleIdsFromListType.length()==0 ? camp.getListsType().split(""+Constants.DELIMETER_COLON)[1] :","+camp.getListsType().split(""+Constants.DELIMETER_COLON)[1];
								campIdToDeleteAssociation=campaignIds;
							}
							List<String> activeConfiguredList = segmentRulesDao.findStatusBySegment(campIdToDeleteAssociation,mlIdStr,segmentRules.getUserId(),Constants.MAILINGLIST_CONFIGURED_TYPE_CAMPAIGN);
							if(activeConfiguredList.size()!=0) {
								
								
								MessageUtil.setMessage("Selected segment is associated with the campaign "+campaignList+"" +
										"\n and cannot be deleted.", "color:red;");
								return;
								
								}
							String[] segRuleIdArray=segRuleIdsFromListType.split(",");
							int segRuleIdArraySize=segRuleIdArray.length;
							int campLoopVar1=0;
							while(campLoopVar1<segRuleIdArraySize){
								if(!segRuleIdArray[campLoopVar1].equals(currSegRuleId)){
								segRuleIds += segRuleIds.length()==0 ? segRuleIdArray[campLoopVar1] :","+segRuleIdArray[campLoopVar1];
								}
								campLoopVar1++;
							}
							if(segRuleIds.length()==0) {
								segRuleIds=currSegRuleId;
								comparedList=mlIdStr;
								deleteFlagC=true;
								}
							else{
									List<SegmentRules> segRules = segmentRulesDao.findById(segRuleIds);
									if(segRules==null){
										comparedList=mlIdStr;
										deleteFlagC=true;
									}else{
									Iterator<SegmentRules> segItr = segRules.iterator();
									while(segItr.hasNext()){
										seg=segItr.next();
										segMlIds += segMlIds.length()==0 ? seg.getSegmentMlistIdsStr():","+seg.getSegmentMlistIdsStr();
									}
							
								String[] mlIdStrarray=mlIdStr.split(",");
								String[] segRuleMlIdsarray=segMlIds.split(",");
	
								for(int campLoopVar2 = 0; campLoopVar2 < mlIdStrarray.length; campLoopVar2++)
								   {
								      if(!Arrays.asList(segRuleMlIdsarray).contains(mlIdStrarray[campLoopVar2])){
								    	  comparedList += comparedList.length()==0 ? mlIdStrarray[campLoopVar2] :","+mlIdStrarray[campLoopVar2];
								      }
								   }
							
								if(comparedList.length()!=0){
									deleteFlagC=true;
								}
							}
						}
						}
							
						//SMS
						boolean deleteFlagSms=false;
						String SMScampIdToDeleteAssociation="";
						String SMScampaignIds="";
						String segmentRuleIds ="";
						String segmentMlIds="";
						
						SMSCampaignsDao smsCampaignsDao = (SMSCampaignsDao)SpringUtil.getBean("smsCampaignsDao");
						//SMSCampaigns smsCampaign = smsCampaignsDao.findIfSegmentAssociates(searchStr);
						//List<String> activeSmsConfiguredList = segmentRulesDao.findStatusBySegment(mlIdStr,segmentRules.getUserId(),Constants.MAILINGLIST_CONFIGURED_TYPE_SMS);
						List<SMSCampaigns> smsCampaignList = smsCampaignsDao.findIfSegmentAssociates(searchStr,segmentRules.getSegRuleId());
						if(smsCampaignList!=null){
						/*if(activeSmsConfiguredList.size()!=0) {
							
							
							MessageUtil.setMessage("Selected segment is associated with the SMS campaign "+smsCampaignList+"" +
									"\n and cannot be deleted.", "color:red;");
							return;
							
							}
						*/
						SMSCampaigns SMScamp = null;
						SegmentRules segment=null;
						String segRuleIdsFromSMSListType ="";	
						Iterator<SMSCampaigns> SMScamItr = smsCampaignList.iterator();
						while(SMScamItr.hasNext()){
							SMScamp = SMScamItr.next();
							SMScampaignIds +=  SMScampaignIds.length()== 0? SMScamp.getSmsCampaignId().toString():","+SMScamp.getSmsCampaignId().toString();
							segRuleIdsFromSMSListType += segRuleIdsFromSMSListType.length()==0 ? SMScamp.getListType().split(""+Constants.DELIMETER_COLON)[1] :","+SMScamp.getListType().split(""+Constants.DELIMETER_COLON)[1];
							SMScampIdToDeleteAssociation=SMScampaignIds;
						}
						List<String> activeSmsConfiguredList = segmentRulesDao.findStatusBySegment(SMScampIdToDeleteAssociation,mlIdStr,segmentRules.getUserId(),Constants.MAILINGLIST_CONFIGURED_TYPE_SMS);
						if(activeSmsConfiguredList.size()!=0) {
							
							
							MessageUtil.setMessage("Selected segment is associated with the SMS campaign "+smsCampaignList+"" +
									"\n and cannot be deleted.", "color:red;");
							return;
							
							}
						
						
								String[] segmentRuleIdArray=segRuleIdsFromSMSListType.split(",");
								int segmentRuleIdArraySize=segmentRuleIdArray.length;
								int SMScampLoopVar1=0;
								while(SMScampLoopVar1<segmentRuleIdArraySize){
									if(!segmentRuleIdArray[SMScampLoopVar1].equals(currSegRuleId)){
										segmentRuleIds += segmentRuleIds.length()==0 ? segmentRuleIdArray[SMScampLoopVar1] :","+segmentRuleIdArray[SMScampLoopVar1];
									}
									SMScampLoopVar1++;
								}
								if(segmentRuleIds.length()==0) {
									segmentRuleIds=currSegRuleId;
									smsComparedList=mlIdStr;
									deleteFlagSms=true;
									}
								else{
								
								List<SegmentRules> segRule = segmentRulesDao.findById(segmentRuleIds);
								if(segRule==null){
									smsComparedList=mlIdStr;
									deleteFlagSms=true;
								}else{
								Iterator<SegmentRules> segmentItr = segRule.iterator();
								while(segmentItr.hasNext()){
									segment=segmentItr.next();
									segmentMlIds += segMlIds.length()==0 ? segment.getSegmentMlistIdsStr().toString():","+segment.getSegmentMlistIdsStr();
									}
								
							String[] mlIdArray=mlIdStr.split(",");
							String[] segmentRuleMlIdsarray=segmentMlIds.split(",");


							for(int SMScampLoopVar2 = 0; SMScampLoopVar2 < mlIdArray.length; SMScampLoopVar2++)
							   {
							      if(!Arrays.asList(segmentRuleMlIdsarray).contains(mlIdArray[SMScampLoopVar2])){
							    	  smsComparedList += smsComparedList.length()==0 ? mlIdArray[SMScampLoopVar2] :","+mlIdArray[SMScampLoopVar2]; 
							      }
							   }
							logger.info("SMScomparedArrayList---"+smsComparedList);
							if(smsComparedList.length()!=0){ 
								deleteFlagSms=true;
							}
								}
						}
						}	
						
						if(segmentRules.getUserId().longValue() != currentUser.getUserId().longValue()) {
							MessageUtil.setMessage("You cannot delete other's shared segment. \n "+segmentRules.getSegRuleName()+
									" cannot be deleted.", "color:red;");
							return;
							
							
						}
						
							//List<SegmentRules> segmenRules = segmentRulesDao.findById(segRuleIds);
							//SegmentRules seg = null;
							//List<String> verifyAssociationList = mailingListDao.verifyAssociation(campaignIds,mlistIds);
							try {
								
							int confirm =  Messagebox.show("Are you sure you want to delete the segment?", "Prompt", 
									Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) ;
							 if (confirm == 1) {
								 
								 if(deleteFlagC==true){
								segmentRulesDaoForDML.deleteSharedEmailCampaign(comparedList,campIdToDeleteAssociation);
								 }
								 if(deleteFlagSms==true){
									segmentRulesDaoForDML.deleteSharedSMSCampaign(smsComparedList,SMScampIdToDeleteAssociation);
								 }
								segmentRulesDaoForDML.deleteSharedAssociation(segmentRules.getSegRuleId());
								segmentRulesDaoForDML.delete(segmentRules);
								 //img.getParent().getParent().setVisible(false);
								 
								 MessageUtil.setMessage("Segment deleted successfully.", 
								 "color:green;");
								 
								 logger.debug("--After Delete--");
								 Redirect.goTo(PageListEnum.EMPTY);
								 Redirect.goTo( PageListEnum.CONTACT_VIEW_SEGMENTS);
							}
						} catch (Exception e) {
							logger.error("Exception ::", e);
							return;
						}
						
						 
							/*Include xcontents = (Include)Sessions.getCurrent().getAttribute("xcontents");
							xcontents.setSrc("/zul/" + PageListEnum.EMPTY + ".zul");
							xcontents.setSrc("/zul/" + PageListEnum.CONTACT_VIEW_SEGMENTS + ".zul");*/
					} catch (Exception e) {
						logger.error("Exception ::", e);
					}
						
						
					
				}//delete
				else if(img.getAttribute("imageEventName").equals("PreviewSegRule")) {
					viewSegRuleWinId.setWidth("500px");
					viewSegRuleWinId.setTitle("Segment Rule");
					/*
					viewSegRuleWinId$viewDownloadSegDivId.setVisible(false);
					viewSegRuleWinId$previewSegDivId.setVisible(true);*/
					String segRuleToView = segmentRules.getSegRuleToView();
					if(segRuleToView == null || segRuleToView.trim().length() == 0) {
						
						viewSegRuleWinId$segRuleLblId.setValue(dispRule(segmentRules.getSegRule()));
					}
					else if(segRuleToView != null &&  segRuleToView.trim().length() > 0){
						viewSegRuleWinId$segRuleLblId.setValue(segRuleToView);
						
					}
					openSubWindow(viewSegRuleWinId$previewSegDivId);
				}else if(img.getAttribute("imageEventName").equals("export")){
					viewSegRuleWinId.setWidth("670px");
					logger.info("==1===");
					exportSegmentRuleObj = segmentRules;
					viewSegRuleWinId.setTitle("Export Segment");
					createWindow();
					openSubWindow(viewSegRuleWinId$custFieldExprtDivId);
					/*viewSegRuleWinId$previewSegDivId.setVisible(false);
					viewSegRuleWinId$viewDownloadSegDivId.setVisible(true);
					viewSegRuleWinId.doModal();
					*/
				}
				
				
				
			}//image
			else if(obj instanceof Paging) {
				Paging segPaging = (Paging)obj;
				int desiredPage = segPaging.getActivePage();
				segPaging.setActivePage(desiredPage);
				
				PagingEvent pagingEvent = (PagingEvent)evt;
				int pSize = pagingEvent.getPageable().getPageSize();
				int ofs = desiredPage * pSize;
				
				String segmentName = null;
				String startDate = null;
				String endDate = null;
				
				if("segmentName".equalsIgnoreCase(searchByLbId.getSelectedItem().getValue().toString())){
					
					segmentName = searchByTbId.getValue();
					}
				else if("creationDate".equalsIgnoreCase(searchByLbId.getSelectedItem().getValue().toString())){
					
					startDate = getStartDate();
					endDate = getEndDate();
				}
				
				
				List retList = getSegRuleList(ofs, pagingEvent.getPageable().getPageSize(), segmentName, startDate, endDate);
				if(retList != null) {
				
					segmentRulesGridId.setModel(new ListModelList(retList));
					segmentRulesGridId.setRowRenderer(rowRender);
				
				}
				
				
				
				/*segmentRulesGridId.setModel(new ListModelList(getSegRuleList(ofs, pagingEvent.getPageable().getPageSize())));
				segmentRulesGridId.setRowRenderer(rowRender);*/
				//redraw(ofs, (byte) pagingEvent.getPageable().getPageSize());
			}
			else if(obj instanceof Label) {
				
				try {
					Label lbl = (Label)obj;
					
					if(lbl.getValue().trim().equals("0")) {
						
						MessageUtil.setMessage("No contacts found.", "color:red;");
						return;
						
					}
					
					Row row = (Row)lbl.getParent();
					String query = "";
					SegmentRules segmentRule = (SegmentRules)row.getValue();
					String type = (String)lbl.getAttribute("type");
					
					//logger.debug("type===============>   "+type);
					String labelType="";
					
					if(type.equals("Total")) {
						
						query = segmentRule.getTotSegQuery();
						labelType=Constants.SEGMENT_ON_EXTERNALID;
						
					}else if(type.equals("Email")) {
						
						query = segmentRule.getEmailSegQuery();
						labelType=Constants.SEGMENT_ON_EMAIL;
						
					}else if(type.equals("Mobile")) {
						
						query = segmentRule.getMobileSegQuery();
						labelType=Constants.SEGMENT_ON_MOBILE;
					}
					
					session.removeAttribute("SegmentRule");
					//session.removeAttribute("SegmentQuery");
					session.removeAttribute("labelType");
					//session.setAttribute("SegmentQuery", query);
					session.setAttribute("SegmentRule", segmentRule);
					
					//logger.debug("type   "+labelType);
					session.setAttribute("labelType",labelType);
					Redirect.goTo(PageListEnum.CONTACT_VIEW_SEGMENTED_CONTACTS);
				} catch (Exception e) {
					logger.error("Exception ::", e);
				}
				
				
				
				
			}
		}//onEvent()
		
	}
	
	
}//class

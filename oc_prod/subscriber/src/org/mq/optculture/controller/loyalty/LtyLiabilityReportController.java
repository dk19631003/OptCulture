package org.mq.optculture.controller.loyalty;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.loyalty.LoyaltyProgramService;
import org.mq.optculture.utils.OCConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.SimpleCategoryModel;

public class LtyLiabilityReportController extends GenericForwardComposer  {
	
	private LoyaltyProgramService ltyPrgmSevice;
	private Long userId;
	private Long prgmId;
	private LoyaltyProgram prgmObj;
	private Rows liabilityRowsId;
	private TimeZone clientTimeZone;
	private Foot footerId;
	private Listbox cardsetLbId,tierLbId;
	private Label listLblId;
	private Columns liabilityColsId;
	private Div cardSetDivId;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public LtyLiabilityReportController() {
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
//		setDefaultStores();
		if(prgmObj.getTierEnableFlag() == 'Y'){
			tierLbId.setDisabled(false);
			setTiers();
		}else{
			tierLbId.setDisabled(true);	
		}
		if(prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			cardsetLbId.setDisabled(false);
			cardSetDivId.setVisible(true);
			setCardSets();
		}else {
			cardsetLbId.setDisabled(true);
			cardSetDivId.setVisible(false);
		}
		
		redrawLiabilityCount(prgmId,null,null);
		
	}
	
	/*private void setDefaultStores() {
		Long orgId=ltyPrgmSevice.getOrgId(userId) ;
		logger.info("orgId"+orgId);
		List<OrganizationStores> storeIdList = ltyPrgmSevice.getAllStores(orgId);
		logger.info("storeIdList"+storeIdList);
		if(storeIdList == null || storeIdList.size() == 0) return;
		for (OrganizationStores eachStore : storeIdList){
			Listitem li = new Listitem(eachStore.getHomeStoreId(),eachStore.getStoreId());
			li.setParent(storeLbId);
		}
	}*/
	private void setTiers() {
		List<LoyaltyProgramTier> tierList = ltyPrgmSevice.getTierList(prgmId);
		logger.info("tierList" + tierList);
		if (tierList == null || tierList.size() == 0)
			return;
		for (LoyaltyProgramTier eachTier : tierList) {
			Listitem li = new Listitem(eachTier.getTierType(),
					eachTier.getTierId());
			li.setParent(tierLbId);
		}
	}
	private void setCardSets() {
		List<LoyaltyCardSet> cardsetList = ltyPrgmSevice.getCardsetList(prgmId);
		logger.info("cardsetList"+cardsetList);
		if(cardsetList == null || cardsetList.size() == 0) return;
		for (LoyaltyCardSet eachCardset : cardsetList){
			Listitem li = new Listitem(eachCardset.getCardSetName(),eachCardset.getCardSetId());
			li.setParent(cardsetLbId);
		}
	}

	
	
	private void redrawLiabilityCount(Long prgmId, Long cardsetId,Long tierId) {
		resetGridCols();
		Components.removeAllChildren(liabilityRowsId);
		Components.removeAllChildren(footerId);
		long totCount = 0;
		double totAmount = 0;
		long totPoints = 0;
		DecimalFormat f = new DecimalFormat("#0.00");
		
		Object[] obj = ltyPrgmSevice.getAggregatedLiabilityData(userId, prgmId,null,cardsetId,tierId);
		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD)) {
			Row row = null;

			row = new Row();

			row.appendChild(new Label("Active"));
			row.appendChild(new Label(obj[0]==null?0+"":obj[0]+""));
			totCount = totCount + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));

			row.appendChild(new Label(obj[1]==null?f.format(0.00):obj[1]+""));
			totAmount = totAmount + (obj[1] == null ? 0.00 : Double.parseDouble(obj[1]+""));

			row.appendChild(new Label(obj[2]==null?0+"":obj[2]+""));
			totPoints = totPoints + (obj[2] == null ? 0 : Long.parseLong(obj[2]+""));

			row.setParent(liabilityRowsId);

			row = new Row();
			long inventoryCount = ltyPrgmSevice.getInventoryCardsCount(prgmId,cardsetId);
			row.appendChild(new Label("Inventory"));
			row.appendChild(new Label(inventoryCount+""));
			totCount = totCount + inventoryCount;
			//Liability Amount
			row.appendChild(new Label("--"));
			//Liability Points
			row.appendChild(new Label("--"));

			row.setParent(liabilityRowsId);
			
			row = new Row();
			//Object[] objsuspend = ltyPrgmSevice.getLiabilityDataforSuspended(userId, prgmId,cardsetId,tierId);
			row.appendChild(new Label("Suspended"));
			row.appendChild(new Label(obj[6]==null?0+"":obj[6]+""));
			totCount = totCount + (obj[6] == null ? 0 : Long.parseLong(obj[6]+""));
			
			row.appendChild(new Label(obj[7]==null?f.format(0.00):obj[7]+""));
			totAmount = totAmount + (obj[7] == null ? 0.00 : Double.parseDouble(obj[7]+""));

			row.appendChild(new Label(obj[8]==null?0+"":obj[8]+""));
			totPoints = totPoints + (obj[8] == null ? 0 : Long.parseLong(obj[8]+""));

			row.setParent(liabilityRowsId);
			
			row = new Row();
			//Object[] objexpire = ltyPrgmSevice.getLiabilityDataforExpired(userId,prgmId,cardsetId,tierId);

			row.appendChild(new Label("Expired"));
			row.appendChild(new Label(obj[3]==null?0+"":obj[3]+""));
			totCount = totCount + (obj[3] == null ? 0 : Long.parseLong(obj[3]+""));

			row.appendChild(new Label(obj[4]==null?f.format(0.00):obj[4]+""));
			totAmount = totAmount + (obj[4] == null ? 0.00 : Double.parseDouble(obj[4]+""));

			row.appendChild(new Label(obj[5]==null?0+"":obj[5]+""));
			totPoints = totPoints + (obj[5] == null ? 0 : Long.parseLong(obj[5]+""));

			row.setParent(liabilityRowsId);
			
			
		}
		else {
			Row row = null;

			row = new Row();
			//Object[] obj = ltyPrgmSevice.getLiabilityData(userId, prgmId,null,null,tierId);

			row.appendChild(new Label("Mobile"));
			row.appendChild(new Label(obj[0]==null?0+"":obj[0]+""));
			totCount = totCount + (obj[0] == null ? 0 : Long.parseLong(obj[0]+""));

			row.appendChild(new Label(obj[1]==null?f.format(0.00):obj[1]+""));
			totAmount = totAmount + (obj[1] == null ? 0.00 : Double.parseDouble(obj[1]+""));

			row.appendChild(new Label(obj[2]==null?0+"":obj[2]+""));
			totPoints = totPoints + (obj[2] == null ? 0 : Long.parseLong(obj[2]+""));

			row.setParent(liabilityRowsId);
			
			row = new Row();
			//Object[] objsuspend = ltyPrgmSevice.getLiabilityDataforSuspended(userId, prgmId,cardsetId,tierId);
			row.appendChild(new Label("Suspended"));
			row.appendChild(new Label(obj[6]==null?0+"":obj[6]+""));
			totCount = totCount + (obj[6] == null ? 0 : Long.parseLong(obj[6]+""));
			
			row.appendChild(new Label(obj[7]==null?f.format(0.00):obj[7]+""));
			totAmount = totAmount + (obj[7] == null ? 0.00 : Double.parseDouble(obj[7]+""));

			row.appendChild(new Label(obj[8]==null?0+"":obj[8]+""));
			totPoints = totPoints + (obj[8] == null ? 0 : Long.parseLong(obj[8]+""));

			row.setParent(liabilityRowsId);
			
			row = new Row();
			//Object[] objexpire = ltyPrgmSevice.getLiabilityDataforExpired(userId, prgmId,cardsetId,tierId);

			row.appendChild(new Label("Expired"));
			row.appendChild(new Label(obj[3]==null?0+"":obj[3]+""));
			totCount = totCount + (obj[3] == null ? 0 : Long.parseLong(obj[3]+""));

			row.appendChild(new Label(obj[4]==null?f.format(0.00):obj[4]+""));
			totAmount = totAmount + (obj[4] == null ? 0.00 : Double.parseDouble(obj[4]+""));

			row.appendChild(new Label(obj[5]==null?0+"":obj[5]+""));
			totPoints = totPoints + (obj[5] == null ? 0 : Long.parseLong(obj[5]+""));

			row.setParent(liabilityRowsId);
			
			
			
		}
		
		Footer footer = new Footer();
		footer.appendChild(new Label("TOTAL"));
		footer.setParent(footerId);
		
		footer = new Footer();
		footer.appendChild(new Label(totCount+""));
		footer.setParent(footerId);
		
		footer = new Footer();
		footer.appendChild(new Label(f.format(totAmount)));
		footer.setParent(footerId);
		
		footer = new Footer();
		footer.appendChild(new Label(totPoints+""));
		footer.setParent(footerId);
		
	}
	
	private void resetGridCols() {
		Components.removeAllChildren(liabilityColsId);

		if(prgmObj.getMembershipType().equalsIgnoreCase(OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE) ||
				prgmObj.getProgramType().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TYPE_DYNAMIC)) {
			Column memType = new Column("Membership Type");
			memType.setWidth("35%");
			memType.setParent(liabilityColsId);
			Column enrollCount = new Column("No. of Enrollments");
			enrollCount.setWidth("45%");
			enrollCount.setParent(liabilityColsId);
			Column amount = new Column("Currency");
			amount.setWidth("30%");
			amount.setParent(liabilityColsId);
			Column points = new Column("Points");
			points.setWidth("30%");
			points.setParent(liabilityColsId);

		}
		else {
			Column memType = new Column("Card Status");
			memType.setWidth("35%");
			memType.setParent(liabilityColsId);
			Column enrollCount = new Column("Number of Cards");
			enrollCount.setWidth("45%");
			enrollCount.setParent(liabilityColsId);
			Column amount = new Column("Currency");
			amount.setWidth("30%");
			amount.setParent(liabilityColsId);
			Column points = new Column("Points");
			points.setWidth("30%");
			points.setParent(liabilityColsId);

		}


	}

	public void onClick$filterBtnId() {


		listLblId.setValue(MyCalendar.calendarToString(new MyCalendar(clientTimeZone), MyCalendar.FORMAT_MONTHDATE_ONLY, clientTimeZone));

		/*String storeNo = null;
		if(!storeLbId.getSelectedItem().getLabel().equalsIgnoreCase("All")) {
			storeNo = storeLbId.getSelectedItem().getLabel();
		}*/
		Long cardsetId = null;
		if(!cardsetLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			cardsetId = Long.parseLong(cardsetLbId.getSelectedItem().getValue().toString());
		}
		Long tierId = null;
		if (!tierLbId.getSelectedItem().getValue().toString().equalsIgnoreCase("All")) {
			tierId = Long.parseLong(tierLbId.getSelectedItem().getValue().toString());
		}
		redrawLiabilityCount(prgmId, cardsetId,tierId);

	}

}

package org.mq.marketer.campaign.controller.admin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDaoForDML;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.ArrayOfLocation;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.Location;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.LocationsView;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.LocationsViewResponse;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.RequestHeader;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.SparkbaseAdminV45WsdlImplService;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.SparkbaseAdminV45WsdlPort;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class SparkBaseSettingsController extends GenericForwardComposer {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private SparkBaseLocationDetailsDao sparkBaseLocationDetailsDao;
	private SparkBaseLocationDetailsDaoForDML sparkBaseLocationDetailsDaoForDML;
	private SparkBaseCardDao sparkBaseCardDao;
	private SparkBaseCardDaoForDML sparkBaseCardDaoForDML;
	private OrganizationStoresDao organizationStoresDao;
	private UsersDao usersDao;
	private Grid spGridId;
	private Textbox coversionfromTbId, conversionToTbId, valueTbId, amountSpentTbId, systemIdTBId, clientIdTBId,
			integrationUserNameTBId, integrationPasswordTBId, LocationIdTBId, TransactionLocationIdTBId, terminalIdTBId,
			initiatorIdTBId, initiatorPasswordTBId, externalIdTBId, percentTxtbId, numTxtbId, userNameTBId;
//	private Listbox ptsActiveValueLbId;
	private Listbox  initiatorTypeLBId, earnTypeLbId, earnValueTypeLbId;
	private Combobox orgIdsComboboxId, usersComboboxId;

	private Checkbox enabledChkId, enabledChId, emailChkId, smsChkId, phoneChId;
	private Button addBtnId, updateBtnId;
	private Groupbox newLocaitonGbId;
	private Button addLocationBtnId;
	private Radio percentRadioBtId, numRadioBtId;
	private SparkBaseLocationDetails editSBLocationDetails;

	private Window uploadCardsWindId;
	//private Radio autoConvertRadioId;
	//private Radio  onDemandRadioId;
	private Radiogroup uploadCardsWindId$formatRdBtnId;
	private Radiogroup uploadCardsWindId$typeRdBtnId;
	private Radiogroup alertRdgrpId;
	private Div  alertDivId, amountDivId, percentDivId, pointsConversionDivId;
	//private Checkbox enableDateChkId;
	//private Div activateAfterDivId
	private SparkBaseLocationDetails sparkbaseLocation;

	// private List<UserOrganization> existingLocOrgList;
	// private Window viewCardsWindID;

	// private Div viewCardsWindID$innerWindDivId;

	public SparkBaseSettingsController() {
		// TODO Auto-generated constructor stub
		String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		PageUtil.setHeader("SparkBase Settings", "", style, true);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		try {
			usersDao = (UsersDao) SpringUtil.getBean("usersDao");
			sparkBaseLocationDetailsDao = (SparkBaseLocationDetailsDao) SpringUtil
					.getBean("sparkBaseLocationDetailsDao");
			sparkBaseLocationDetailsDaoForDML = (SparkBaseLocationDetailsDaoForDML) SpringUtil
					.getBean("sparkBaseLocationDetailsDaoForDML");
			sparkBaseCardDao = (SparkBaseCardDao) SpringUtil.getBean("sparkBaseCardDao");
			sparkBaseCardDaoForDML = (SparkBaseCardDaoForDML) SpringUtil.getBean("sparkBaseCardDaoForDML");
			organizationStoresDao = (OrganizationStoresDao) SpringUtil.getBean("organizationStoresDao");

			List<SparkBaseLocationDetails> sparkBaseLocList = sparkBaseLocationDetailsDao.findAll();

			if (sparkBaseLocList != null && sparkBaseLocList.size() > 0) {
				for (SparkBaseLocationDetails sparkBaseLocObj : sparkBaseLocList) {
					fillGridRow(sparkBaseLocObj);
				}
			}

			setOrganisations(null);
			sparkbaseLocation = null;

		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}

	}

	private void setOrganisations(UserOrganization editMode) {

		Comboitem item = null;

		Components.removeAllChildren(orgIdsComboboxId);

		List<UserOrganization> existList = sparkBaseLocationDetailsDao.findAllLocOrg();
		logger.info("Exist list : " + existList.size());
		List<UserOrganization> alllist = usersDao.findAllOrganizations();
		logger.info("All list : " + alllist.size());

		if (editMode == null) {

			boolean flag = false;
			for (UserOrganization userOrg : alllist) {
				flag = false;

				for (UserOrganization existOrg : existList) {

					// logger.info("Comparing : " + userOrg.getOrgExternalId() +
					// " with "+ existOrg.getOrgExternalId());
					if (userOrg.getOrgExternalId().equals(existOrg.getOrgExternalId())) {
						// logger.info("found ...");
						flag = true;
						break;
					}
				}

				if (!flag) {

					item = new Comboitem(userOrg.getOrganizationName());
					item.setDescription(userOrg.getOrgExternalId());
					item.setValue(userOrg);
					item.setParent(orgIdsComboboxId);

				}
			}

			if (orgIdsComboboxId.getItemCount() > 0)
				orgIdsComboboxId.setSelectedIndex(0);
		} else {
			for (UserOrganization userOrg : alllist) {
				/*
				 * Modified logger.info("EDIT MODE >>>>"); item = new
				 * Comboitem(userOrg.getOrganizationName());
				 * item.setDescription(userOrg.getOrgExternalId());
				 * item.setValue(userOrg); item.setParent(orgIdsComboboxId);
				 * logger.info("Comparing : " +editMode.getOrgExternalId() +
				 * " with "+ userOrg.getOrgExternalId());
				 */
				if (editMode.getOrgExternalId().equals(userOrg.getOrgExternalId())) {
					// logger.info("Match found .::::::::::;..");
					item = new Comboitem(userOrg.getOrganizationName());
					item.setDescription(userOrg.getOrgExternalId());
					item.setValue(userOrg);
					item.setParent(orgIdsComboboxId);
					orgIdsComboboxId.setSelectedItem(item);// deselects
															// currently
															// selected item &
															// select the given
															// items
					break;
				}
			}
		}

	} //

	private void fillGridRow(SparkBaseLocationDetails sparkBaseLocObj) {
		try {
			Rows rows = spGridId.getRows();
			if (rows == null) {
				rows = new Rows();
				rows.setParent(spGridId);
			}

			Row row = new Row();
			row.setParent(rows);

			row.appendChild(new Label(sparkBaseLocObj.getUserOrganization().getOrganizationName()));
			row.appendChild(new Label(sparkBaseLocObj.getOrgUserName()));
			row.appendChild(new Label(sparkBaseLocObj.getSystemId()));
			row.appendChild(new Label(sparkBaseLocObj.getClientId()));
			row.appendChild(new Label(sparkBaseLocObj.getIntegrationUserName()));
			row.appendChild(new Label(sparkBaseLocObj.getIntegrationPassword()));
			row.appendChild(new Label(sparkBaseLocObj.getLocationId()));
			row.appendChild(new Label(sparkBaseLocObj.getTransactionLocationId()));
			row.appendChild(new Label(sparkBaseLocObj.getTerminalId()));

			row.appendChild(new Label(sparkBaseLocObj.getInitiatorType()));
			row.appendChild(new Label(sparkBaseLocObj.getInitiatorId()));
			row.appendChild(new Label(sparkBaseLocObj.getInitiatorPassword()));

			row.appendChild(new Label(sparkBaseLocObj.getExternalId()));
			row.appendChild(new Label(sparkBaseLocObj.isEnabled() ? "enabled" : "disabled"));
			row.appendChild(new Label(
					MyCalendar.calendarToString(sparkBaseLocObj.getCreatedDate(), MyCalendar.FORMAT_SCHEDULE)));
			row.appendChild(new Label(sparkBaseLocObj.getUserOrganization().getOrganizationName()));

			row.setValue(sparkBaseLocObj);

			Div div = new Div();
			Image img = new Image("/img/icons/edit_lists_icon.png");
			img.setTooltiptext("Edit");
			img.setStyle("margin:0px 10px;cursor:pointer;");
			img.setAttribute("imageEventName", "editObj");
			img.addEventListener("onClick", this);
			img.setParent(div);

			img = new Image("/img/icons/delete_icon.png");
			img.setTooltiptext("Delete");
			img.setStyle("margin:0px 10px;cursor:pointer;");
			img.setAttribute("imageEventName", "deleteObj");
			img.addEventListener("onClick", this);
			img.setParent(div);

			img = new Image("/img/icons/csv_upload.jpeg");
			img.setTooltiptext("Upload Cards");
			img.setHeight("18px");
			img.setStyle("margin:0px 10px;cursor:pointer;");
			img.setAttribute("imageEventName", "addCards");
			img.addEventListener("onClick", this);
			img.setParent(div);

			img = new Image("/img/preview.gif");
			img.setTooltiptext("View Cards");
			img.setStyle("cursor:pointer;");
			img.setAttribute("imageEventName", "viewCards");
			img.addEventListener("onClick", this);
			img.setParent(div);

			div.setParent(row);

		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}
	}

	public void onClick$addBtnId() {
		addBtnId.setFocus(true);
		doAddOrUpdate(false);
	}

	private void updateSBSettings(boolean isEdit) {

		Long earnValue = null;
		Long earnOnSpentAmount = null, convertFromPoints = null, convertToAmount = null;
		//Long ptsActiveDateValue = null;
		String conversionType = "";
		String earnType = earnTypeLbId.getSelectedItem().getValue().toString();
		String earnValueType = earnValueTypeLbId.getSelectedItem().getValue().toString();
		//boolean dateEnable = enableDateChkId.isChecked();

		if (Messagebox.show("Do you want to save the location details?", "Confirm", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION) == Messagebox.OK) {

			SparkBaseLocationDetails sparkBaseLocObj = null;
			sparkBaseLocObj = editSBLocationDetails;

			String systemId = systemIdTBId.getValue().trim();

			// Changes

			//if (coversionfromTbId.getValue() != null && !coversionfromTbId.getValue().trim().isEmpty()) {
				
					conversionType = OCConstants.LOYALTY_CONVERSION_TYPE_AUTO;
				
		//	}
			
			
			/*if (enableDateChkId.isChecked()) {
				if (ptsActiveValueLbId.getSelectedItem().getValue().toString() != null
						&& !ptsActiveValueLbId.getSelectedItem().getValue().toString().isEmpty()) {
					ptsActiveDateValue = Long.parseLong(ptsActiveValueLbId.getSelectedItem().getValue().toString());
				}
			}*/

			if (earnTypeLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
				if (coversionfromTbId.getValue() != null && !coversionfromTbId.getValue().trim().isEmpty()) {
					convertFromPoints = Long.parseLong(coversionfromTbId.getValue().trim());
				}
				if (conversionToTbId.getValue() != null && !conversionToTbId.getValue().trim().isEmpty()) {
					convertToAmount = Long.parseLong(conversionToTbId.getValue().trim());
				}

			}

			if (earnValueTypeLbId.getSelectedItem().getValue().toString()
					.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
				if (amountSpentTbId.getValue() != null && !amountSpentTbId.getValue().trim().isEmpty()) {
					earnOnSpentAmount = Long.parseLong(amountSpentTbId.getValue().trim());
				}
			}

			if (valueTbId.getValue() != null && !valueTbId.getValue().trim().isEmpty()) {
				earnValue = Long.parseLong(valueTbId.getValue().trim());
			}

			// Changes


			// Changes

			sparkBaseLocObj.setEarnValue(earnValue);
			sparkBaseLocObj.setEarnValueType(earnValueType);
			sparkBaseLocObj.setEarnType(earnType);
			sparkBaseLocObj.setEarnOnSpentAmount(earnOnSpentAmount);
			sparkBaseLocObj.setConvertFromPoints(convertFromPoints);
			sparkBaseLocObj.setConvertToAmount(convertToAmount);
			/*sparkBaseLocObj.setActivationFlag(dateEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			sparkBaseLocObj.setPtsActiveDateValue(ptsActiveDateValue);
			*/logger.info("==>" + conversionType);
			sparkBaseLocObj.setConversionType(conversionType);
			
			//Changes
			
			sparkBaseLocObj.setSystemId(systemId);
			sparkBaseLocObj.setClientId(clientIdTBId.getValue().trim());
			sparkBaseLocObj.setIntegrationUserName(integrationUserNameTBId.getValue().trim());
			sparkBaseLocObj.setIntegrationPassword(integrationPasswordTBId.getValue().trim());
			sparkBaseLocObj.setLocationId(LocationIdTBId.getValue().trim());
			sparkBaseLocObj.setTransactionLocationId(TransactionLocationIdTBId.getValue().trim());
			sparkBaseLocObj.setTerminalId(terminalIdTBId.getValue().trim());
			sparkBaseLocObj.setInitiatorId(initiatorIdTBId.getValue().trim());
			sparkBaseLocObj.setInitiatorPassword(initiatorPasswordTBId.getValue().trim());
			sparkBaseLocObj.setExternalId(externalIdTBId.getValue().trim());
			sparkBaseLocObj.setInitiatorType(initiatorTypeLBId.getSelectedItem().getValue().toString().trim());
			sparkBaseLocObj.setEnabled(enabledChkId.isChecked());

			sparkBaseLocObj.setUserOrganization((UserOrganization) orgIdsComboboxId.getSelectedItem().getValue());

			Users user = (Users) usersComboboxId.getSelectedItem().getValue();
			sparkBaseLocObj.setOrgUserName(user.getUserName());
			sparkBaseLocObj.setOrgUserId(user.getUserId());

			if (phoneChId.isChecked()) {
				sparkBaseLocObj.setMobileUnique(true);
			} else {
				sparkBaseLocObj.setMobileUnique(false);
			}

			if (enabledChId.isChecked()) {
				sparkBaseLocObj.setEnableAlerts(enabledChId.isChecked());
				sparkBaseLocObj.setEmailAlerts(emailChkId.isChecked());
				// sparkBaseLocObj.setSmsAlerts(smsChkId.isChecked());
				if (percentRadioBtId.isChecked()) {
					sparkBaseLocObj.setCountType(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE);
					sparkBaseLocObj.setCountValue(percentTxtbId.getValue());
				} else if (numRadioBtId.isChecked()) {
					sparkBaseLocObj.setCountType(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_COUNT);
					sparkBaseLocObj.setCountValue(numTxtbId.getValue());
				}
			}

			else {

				emailChkId.setChecked(false);
				percentRadioBtId.setChecked(false);
				numRadioBtId.setChecked(false);
				percentTxtbId.setDisabled(true);
				numTxtbId.setDisabled(true);
				percentTxtbId.setValue("");
				numTxtbId.setValue("");

				sparkBaseLocObj.setEnableAlerts(enabledChId.isChecked());
				sparkBaseLocObj.setEmailAlerts(emailChkId.isChecked());
				// sparkBaseLocObj.setSmsAlerts(smsChkId.isChecked());
				sparkBaseLocObj.setCountType("");
				sparkBaseLocObj.setCountValue("");
			}
			// sparkBaseLocObj.setStoreLocation((OrganizationStores)posLocationComboBoxId.getSelectedItem().getValue());
			sparkBaseLocationDetailsDaoForDML.saveOrUpdate(sparkBaseLocObj);
			session.setAttribute("SBOBJ", sparkBaseLocObj);
			logger.info("-- Spark base object saved successfully. ---");
			MessageUtil.setMessage("SparkBase location details saved successfully.", "green");
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.ADMIN_SPARKBASE_SETTINGS);

		}

	}

	private void doAddOrUpdate(boolean isEdit) {

		Long earnValue = null;
		Long earnOnSpentAmount = null, convertFromPoints = null, convertToAmount = null;
		//Long ptsActiveDateValue = null;
		String conversionType = "";

		if (!validateFields()) {
			return;
		}
		// Changes

	//	if (coversionfromTbId.getValue() != null && !coversionfromTbId.getValue().trim().isEmpty()) {
			
				conversionType = OCConstants.LOYALTY_CONVERSION_TYPE_AUTO;
			
		//}

		/*if (enableDateChkId.isChecked()) {
			if (ptsActiveValueLbId.getSelectedItem().getValue().toString() != null
					&& !ptsActiveValueLbId.getSelectedItem().getValue().toString().isEmpty()) {
				ptsActiveDateValue = Long.parseLong(ptsActiveValueLbId.getSelectedItem().getValue().toString());
			}
		}

		boolean dateEnable = enableDateChkId.isChecked();
*/
		if (earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
			if (coversionfromTbId.getValue() != null && !coversionfromTbId.getValue().trim().isEmpty()) {
				convertFromPoints = Long.parseLong(coversionfromTbId.getValue().trim());
			}
			if (conversionToTbId.getValue() != null && !conversionToTbId.getValue().trim().isEmpty()) {
				convertToAmount = Long.parseLong(conversionToTbId.getValue().trim());
			}

		}

		String earnType = earnTypeLbId.getSelectedItem().getValue().toString();
		String earnValueType = earnValueTypeLbId.getSelectedItem().getValue().toString();

		if (earnValueTypeLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
			if (amountSpentTbId.getValue() != null && !amountSpentTbId.getValue().trim().isEmpty()) {
				earnOnSpentAmount = Long.parseLong(amountSpentTbId.getValue().trim());
			}
		}

		if (valueTbId.getValue() != null && !valueTbId.getValue().trim().isEmpty()) {
			earnValue = Long.parseLong(valueTbId.getValue().trim());
		}

		// Changes

		if (Messagebox.show("Do you want to save the location details?", "Confirm", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION) == Messagebox.OK) {

			// fetch transaction location id using location id from sparkbase
			String transLocId = getTransactionLocationId(LocationIdTBId.getValue().trim());
			if (transLocId == null) {
				MessageUtil.setMessage("Locaton details are not created. Invalid LocationId.", "red");
				return;
			}

			SparkBaseLocationDetails sparkBaseLocObj = null;
			if (isEdit) {
				sparkBaseLocObj = editSBLocationDetails;
			} else {
				sparkBaseLocObj = new SparkBaseLocationDetails();
			}

			// Check Mandatory Fields ..

			String systemId = systemIdTBId.getValue().trim();
			// Changes

			sparkBaseLocObj.setEarnValue(earnValue);
			sparkBaseLocObj.setEarnValueType(earnValueType);
			sparkBaseLocObj.setEarnType(earnType);
			sparkBaseLocObj.setEarnOnSpentAmount(earnOnSpentAmount);
			sparkBaseLocObj.setConvertFromPoints(convertFromPoints);
			sparkBaseLocObj.setConvertToAmount(convertToAmount);
			/*sparkBaseLocObj.setActivationFlag(dateEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			sparkBaseLocObj.setPtsActiveDateValue(ptsActiveDateValue);
			*/logger.info("==>" + conversionType);
			sparkBaseLocObj.setConversionType(conversionType);

			// Changes

			sparkBaseLocObj.setSystemId(systemId);
			sparkBaseLocObj.setClientId(clientIdTBId.getValue().trim());
			sparkBaseLocObj.setIntegrationUserName(integrationUserNameTBId.getValue().trim());
			sparkBaseLocObj.setIntegrationPassword(integrationPasswordTBId.getValue().trim());
			sparkBaseLocObj.setLocationId(LocationIdTBId.getValue().trim());
			sparkBaseLocObj.setTransactionLocationId(transLocId);
			sparkBaseLocObj.setTerminalId(terminalIdTBId.getValue().trim());
			sparkBaseLocObj.setInitiatorId(initiatorIdTBId.getValue().trim());
			sparkBaseLocObj.setInitiatorPassword(initiatorPasswordTBId.getValue().trim());
			sparkBaseLocObj.setExternalId(externalIdTBId.getValue().trim());
			sparkBaseLocObj.setInitiatorType(initiatorTypeLBId.getSelectedItem().getValue().toString().trim());
			sparkBaseLocObj.setEnabled(enabledChkId.isChecked());

			if (!isEdit) {
				sparkBaseLocObj.setUserId(GetUser.getUserId());
				sparkBaseLocObj.setCreatedDate(Calendar.getInstance());
				sparkBaseLocObj.setLastFetchedTime(Calendar.getInstance());
				sparkBaseLocObj.setFetchFreqInMin(10);
			}

			// sparkBaseLocObj.setUserOrganization(GetUser.getUserObj().getUserOrganization());

			sparkBaseLocObj.setUserOrganization((UserOrganization) orgIdsComboboxId.getSelectedItem().getValue());

			Users user = (Users) usersComboboxId.getSelectedItem().getValue();
			sparkBaseLocObj.setOrgUserName(user.getUserName());
			sparkBaseLocObj.setOrgUserId(user.getUserId());

			if (phoneChId.isChecked()) {
				sparkBaseLocObj.setMobileUnique(true);
			} else {
				sparkBaseLocObj.setMobileUnique(false);
			}

			if (enabledChId.isChecked()) {
				sparkBaseLocObj.setEnableAlerts(enabledChId.isChecked());
				sparkBaseLocObj.setEmailAlerts(emailChkId.isChecked());
				// sparkBaseLocObj.setSmsAlerts(smsChkId.isChecked());
				if (percentRadioBtId.isChecked()) {
					sparkBaseLocObj.setCountType(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE);
					sparkBaseLocObj.setCountValue(percentTxtbId.getValue());
				} else if (numRadioBtId.isChecked()) {
					sparkBaseLocObj.setCountType(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_COUNT);
					sparkBaseLocObj.setCountValue(numTxtbId.getValue());
				}
			}

			else {

				emailChkId.setChecked(false);
				percentRadioBtId.setChecked(false);
				numRadioBtId.setChecked(false);
				percentTxtbId.setDisabled(true);
				numTxtbId.setDisabled(true);
				percentTxtbId.setValue("");
				numTxtbId.setValue("");

				sparkBaseLocObj.setEnableAlerts(enabledChId.isChecked());
				sparkBaseLocObj.setEmailAlerts(emailChkId.isChecked());
				// sparkBaseLocObj.setSmsAlerts(smsChkId.isChecked());
				sparkBaseLocObj.setCountType("");
				sparkBaseLocObj.setCountValue("");
			}
			// sparkBaseLocObj.setStoreLocation((OrganizationStores)posLocationComboBoxId.getSelectedItem().getValue());
			sparkBaseLocationDetailsDaoForDML.saveOrUpdate(sparkBaseLocObj);
			session.setAttribute("SBOBJ", sparkBaseLocObj);
			logger.info("-- Spark base object saved successfully. ---");
			MessageUtil.setMessage("SparkBase location details saved successfully.", "green");
			Redirect.goTo(PageListEnum.EMPTY);
			Redirect.goTo(PageListEnum.ADMIN_SPARKBASE_SETTINGS);
		}
	}

	// Fetch Transaction LocationId from Sparkbase by locationId
	private String getTransactionLocationId(String locationId) {

		try {
			String transLocId = null;

			SparkbaseAdminV45WsdlImplService service = new SparkbaseAdminV45WsdlImplService();
			SparkbaseAdminV45WsdlPort client = service.getSparkbaseAdminV45WsdlPortPort();

			Location location = new Location();
			location.setIdPos(locationId);

			LocationsView locationsView = new LocationsView();
			locationsView.setHeader(promptForHeader());
			locationsView.setLocation(location);
			LocationsViewResponse locationResponse = client.locationsView(locationsView);
			if (locationResponse == null) {
				return null;
			}
			if (locationResponse.getTotal().intValue() == 0) {
				return null;
			}

			ArrayOfLocation alocation = locationResponse.getLocations();
			Location loc = alocation.getLocations().get(0);
			transLocId = loc.getLocationId();
			return transLocId;

		} catch (Exception e) {
			logger.info("Exception while retrieving SB Transaction LocationId for the given LocationId::" + locationId);
			logger.info(e);
		}

		return null;
	}// getTransactionLocationId

	// Sparkbase connection API Header preparation
	private static RequestHeader promptForHeader() {
		RequestHeader header = new RequestHeader();
		logger.info("Admin Api RequestHeader Prepration...");
		header.setClient("7708");
		header.setIntegrationAuth("clnt77082");
		header.setIntegrationPass("clnt77082");
		header.setInitiatorType("user");
		header.setInitiatorId("adminapi@optculture.com");
		header.setInitiatorPass("OptCulture7708");
		header.setInitiatorIP("64.22.65.111");

		return header;
	}// promptForHeader

	private boolean validateFields() {

		if (systemIdTBId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please provide mandatory field System ID.", "red");
			return false;
		}

		if (clientIdTBId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please provide mandatory field Client ID.", "red");
			return false;
		}

		if (integrationUserNameTBId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please provide mandatory field Integration Username.", "red");
			return false;
		}

		if (integrationPasswordTBId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please provide mandatory field Integration Password.", "red");
			return false;
		}

		if (LocationIdTBId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please provide mandatory field Location.", "red");
			return false;
		}

		/*
		 * if(TransactionLocationIdTBId.getValue().trim().length() == 0) {
		 * MessageUtil.
		 * setMessage("Please provide mandatory field TransactionLocation.",
		 * "red"); return false; }
		 */

		if (terminalIdTBId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please provide mandatory field Terminal.", "red");
			return false;
		}
		/*
		 * if(posLocationComboBoxId.getItemCount() == 0){
		 * MessageUtil.setMessage("Please create store.", "red"); return false;
		 * }
		 */

		if (enabledChId.isChecked()) {

			if (!emailChkId.isChecked()) {
				MessageUtil.setMessage("Please check the alert by checkbox.", "red");
				return false;
			}

			if (!percentRadioBtId.isChecked() && !numRadioBtId.isChecked()) {
				MessageUtil.setMessage("Please check the alert on radio button.", "red");
				return false;
			}

			if (percentRadioBtId.isChecked()) {
				if (percentTxtbId.getValue().trim().length() != 0) {
					if (!checkIfNumber(percentTxtbId.getValue().trim())) {
						MessageUtil.setMessage("Please provide number value for % of total cards.", "red");
						return false;
					}

				}
				if (percentTxtbId.getValue().trim().length() == 0) {
					MessageUtil.setMessage("Please provide the value for % of total cards.", "red");
					return false;
				}
			}

			else if (numRadioBtId.isChecked()) {
				if (numTxtbId.getValue().trim().length() != 0) {
					if (!checkIfNumber(numTxtbId.getValue().trim())) {
						MessageUtil.setMessage("Please provide number value for no of cards.", "red");
						return false;
					}
				}

				if (numTxtbId.getValue().trim().length() == 0) {
					MessageUtil.setMessage("Please provide the  value for no of cards.", "red");
					return false;
				}
			}

		}

		if (usersComboboxId.getValue() == null || usersComboboxId.getText().trim().isEmpty()
				|| usersComboboxId.getValue().trim().length() == 0) {
			MessageUtil.setMessage("Please select POS List User ", "red");
			return false;
		}
		
		//Changes


		if(valueTbId.getValue().trim().isEmpty() || valueTbId.getValue()==null ) {
			MessageUtil.setMessage("Earn rule cannot be empty.", "red");
			valueTbId.setFocus(true);
			return false;
		}

		if(valueTbId.getValue().trim().length() > 60) {
			MessageUtil.setMessage("Earn rule exceeds the maximum characters limit.", "red");
			valueTbId.setFocus(true);
			return false;
		}

		if(valueTbId.getValue().trim().contains("-")) {
			MessageUtil.setMessage("Please enter valid value for earn rule.", "red");
			valueTbId.setFocus(true);
			return false;
		}
		if(valueTbId.getValue().trim().contains(".")) {
			MessageUtil.setMessage("Please provide number value for earn rule.", "red");
			valueTbId.setFocus(true);
			return false;
		}
		
		if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
			if(!checkIfLong(valueTbId.getValue().trim())) {
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				valueTbId.setFocus(true);
				return false;
			}
		}else if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {

			if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
				if(!checkIfDouble(valueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for earn rule.", "red");
					valueTbId.setFocus(true);
					return false;
				}
			}else if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERCENTAGE)){
				if(!checkIfLong(valueTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for earn rule.", "red");
					valueTbId.setFocus(true);
					return false;
				}
			}

		}
		
		logger.info("===>enter Validation");
		if(earnValueTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_VALUE)) {
			logger.info("===>enter If - Clause");
			if(amountSpentTbId.getValue().trim().isEmpty() || amountSpentTbId.getValue()==null) {
				MessageUtil.setMessage("Earn rule cannot be empty.", "red");
				amountSpentTbId.setFocus(true);
				return false;
			}
			if(amountSpentTbId.getValue().trim().length() > 60) {
				MessageUtil.setMessage("Earn rule exceeds the maximum characters limit.", "red");
				amountSpentTbId.setFocus(true);
				return false;
			}

			if(amountSpentTbId.getValue().trim().contains("-")) {
				MessageUtil.setMessage("Please enter valid value for earn rule.", "red");
				return false;
			}

			if(amountSpentTbId.getValue().trim().contains(".")) {
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				valueTbId.setFocus(true);
				return false;
			}
			if(!checkIfDouble(amountSpentTbId.getValue().trim())) {
				logger.info("===>enter Double clause");
				MessageUtil.setMessage("Please provide number value for earn rule.", "red");
				amountSpentTbId.setFocus(true);
				return false;
			}
		}

		
		if(earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {

			if((conversionToTbId.getValue().trim().isEmpty() && coversionfromTbId.getValue().trim().length() > 0) 
					||(conversionToTbId.getValue().trim().length() > 0 && coversionfromTbId.getValue().trim().isEmpty()) ){
				MessageUtil.setMessage("Conversion rule cannot be empty.", "red");
				coversionfromTbId.setFocus(true);
				return false;
			}

			if(coversionfromTbId.getValue().trim().contains("-")) {
				MessageUtil.setMessage("Please enter valid value for conversion rule.", "red");
				coversionfromTbId.setFocus(true);
				return false;
			}

			if(conversionToTbId.getValue().trim().contains("-")) {
				MessageUtil.setMessage("Please enter valid value for conversion rule.", "red");
				conversionToTbId.setFocus(true);
				return false;
			}
			if(conversionToTbId.getValue().trim().contains(".")) {
				MessageUtil.setMessage("Please provide number value for conversion rule.", "red");
				conversionToTbId.setFocus(true);
				return false;
			}
			if (conversionToTbId.getValue().trim().length() > 0) {
				if (!checkIfDouble(conversionToTbId.getValue().trim())) {
					MessageUtil.setMessage(
							"Please provide number value for conversion rule.",	"red");
					conversionToTbId.setFocus(true);
					return false;
				}
			}
			if (conversionToTbId.getValue().trim().length() > 60) {
				MessageUtil.setMessage("Conversion rule exceeds the maximum characters limit.","red");
				conversionToTbId.setFocus(true);
				return false;
			}

			if(coversionfromTbId.getValue().trim().length() > 0) {
				if (!checkIfLong(coversionfromTbId.getValue().trim())) {
					MessageUtil.setMessage("Please provide number value for conversion rule.","red");
					coversionfromTbId.setFocus(true);
					return false;
				}
			}

			if (coversionfromTbId.getValue().trim().length() > 60) {
				MessageUtil	.setMessage("Conversion rule exceeds the maximum characters limit.","red");
				coversionfromTbId.setFocus(true);
				return false;
			}


		}
		
		//Changes
		
		
		
		
		
		
		return true;

	}

	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);

		if (event.getTarget() instanceof Image) {

			Image img = (Image) event.getTarget();
			String imageEventName = img.getAttribute("imageEventName").toString();
			logger.debug("Event Type is ::" + imageEventName);
			SparkBaseLocationDetails sparkBaseLocationDetails = ((Row) img.getParent().getParent()).getValue();

			if (imageEventName.equals("editObj")) {
				if (sparkBaseLocationDetails != null) {
					editSparkbaseDetails(sparkBaseLocationDetails);
					sparkbaseLocation = sparkBaseLocationDetails;
				}
			} else if (imageEventName.equals("deleteObj")) {
				if (Messagebox.show("Do you want to delete the sparkbase details?", "Confirm",
						Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK) {
					// sparkBaseLocationDetailsDao.delete(sparkBaseLocationDetails);
					deleteSparkbaseDetails(sparkBaseLocationDetails);
					Row row = (Row) img.getParent().getParent();
					row.detach();
					MessageUtil.setMessage("SparkBase properties deleted successfully.", "green");
				}
			}

			else if (imageEventName.equals("addCards")) {
				// deleteSelected(contactsListLBId,img);

				// uploadCards();
				uploadSBLocationDetails = sparkBaseLocationDetails;
				onClick$uploadImgBtnId();
			} else if (imageEventName.equals("viewCards")) {
				logger.info("==1==");
				viewCards(sparkBaseLocationDetails.getSparkBaseLocationDetails_id());
			}
		}
	}

	private void viewCards(Long sparkBaseLocId) {
		try {
			logger.info("==2==");
			List<Object[]> objectArrList = sparkBaseCardDao.findCardCountByStatus(sparkBaseLocId);

			if (objectArrList != null && objectArrList.size() > 0) {

				String tempStr = "Loyalty card details : ";
				for (Object[] objArr : objectArrList) {

					tempStr += "\n\t" + objArr[0].toString() + " : " + objArr[1].toString();

					// lbl.setParent(viewCardsWindID$innerWindDivId);

				}

				MessageUtil.setMessage(tempStr, "color:green;");
			} else {
				MessageUtil.setMessage("Loyalty card details not found.", "color:green;");
			}
			logger.info("==3==");
			logger.info("==4==");
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}
	}

	SparkBaseLocationDetails uploadSBLocationDetails;

	public void onClick$uploadImgBtnId() {
		try {

			uploadCardsWindId.setVisible(true);
			uploadCardsWindId.doPopup();
			uploadCardsWindId.doHighlighted();

			/*
			 * logger.debug("just click upload Image button"); Media media =
			 * (Media)Fileupload.get();
			 * 
			 * logger.debug("========>"+media); if(media == null){ return; }
			 * upload(media);
			 */

		} catch (Exception e) {
			logger.error("unable to upload the images" + e.getMessage());
		}
	}

	/*
	 * private Radiogroup uploadCardsWindId$formatRdBtnId; private Radiogroup
	 * uploadCardsWindId$typeRdBtnId;
	 */

	public void onClick$uploadBtnId$uploadCardsWindId() {
		// Executions.sendRedirect("/zul/admin/test.zul");
		// }
		try {

			Media media = (Media) Fileupload.get();

			logger.debug(" Media is ==>" + media);
			// logger.info("1--> "+
			// uploadCardsWindId$formatRdBtnId.getSelectedItem().getValue() + "
			// 2-->" +
			// uploadCardsWindId$typeRdBtnId.getSelectedItem().getValue());
			if (media == null) {
				uploadCardsWindId.setVisible(false);
				return;
			}

			upload(media);
			uploadCardsWindId.setVisible(false);
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}
	}

	// public void onUpload$uploadBtnId$uploadCardsWindID(UploadEvent event) {
	public void upload(Media media) {

		try {
			String contentType = media.getContentType();
			int colCount = Integer.parseInt(uploadCardsWindId$formatRdBtnId.getSelectedItem().getValue().toString());
			String cardTYpe = uploadCardsWindId$typeRdBtnId.getSelectedItem().getValue();
			// String format = media.getFormat();
			String appUserParentFolder = PropertyUtil.getPropertyValue("usersParentDirectory");
			logger.info("format : " + contentType);

			// String ext = Files.getFileExtension(file media);
			String ext = media.getName();
			logger.info("extension : " + ext);

			if (media.getName().endsWith(".csv")) {
				// if(ext.endsWith(".csv") && contentType.startsWith("text/")){

				// BufferedReader in = new
				// BufferedReader((InputStreamReader)media.getReaderData());
				// BufferedReader in = new
				// BufferedReader((InputStreamReader)media.getReaderData());
				// InputStream in = new
				// ByteArrayInputStream(media.getByteData());
				File file = new File(appUserParentFolder + File.separator + GetUser.getUserName() + File.separator
						+ "sparkBaseCardCSV/");
				logger.info("file path : " + file);
				if (!file.exists()) {
					file.mkdirs();
				}

				File cardsFile = new File(file, media.getName());

				// BufferedOutputStream bos=new BufferedOutputStream(new
				// FileOutputStream(cardsFile));
				BufferedWriter bos = new BufferedWriter(new FileWriter(cardsFile));
				logger.info("cardsfile : " + cardsFile);
				// logger.debug("Copying the contents of the file to the output
				// stream");
				// Copy the contents of the file to the output stream
				char[] buf = new char[1024];
				int count = 0;
				if (contentType.startsWith("text/")) {

					BufferedReader in = new BufferedReader(media.getReaderData());
					while ((count = in.read(buf)) >= 0) {
						bos.write(buf, 0, count);
					}
					in.close();
				} else {
					/*
					 * InputStream in = new
					 * ByteArrayInputStream(media.getByteData()); while ((count
					 * = in.read()) >= 0) { bos.write(buf, 0, count); }
					 * in.close();
					 */
					byte[] data = media.getByteData();
					FileOutputStream fos = new FileOutputStream(cardsFile);
					fos.write(data);
					fos.flush();
					fos.close();
				}

				bos.close();

				ParseCSVFileThread parseTh = new ParseCSVFileThread(cardsFile, uploadSBLocationDetails,
						sparkBaseCardDaoForDML, colCount, cardTYpe);

				parseTh.start();

				MessageUtil.setMessage("SparkBase cards will be uploaded in a moment.", "green");
			}

		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", ioe);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", e);
		}
	}

	// new code two methods resetToDefaultValues()
	// ,deleteSparkbaseDetails(sparkBaseLocationDetails)

	public void resetToDefaultValues() {

		systemIdTBId.setValue("");
		clientIdTBId.setValue("");
		integrationUserNameTBId.setValue("");
		integrationPasswordTBId.setValue("");
		LocationIdTBId.setValue("");
		TransactionLocationIdTBId.setValue("");
		terminalIdTBId.setValue("");
		initiatorPasswordTBId.setValue("");
		externalIdTBId.setValue("");
		addBtnId.setVisible(true);
		updateBtnId.setVisible(false);
		orgIdsComboboxId.setSelectedIndex(0);
		// usersComboboxId.setSelectedIndex(0);
		// in sparkBaseSettings.zul we are setting value for Enabled default as
		// "true"
		// so here we are not setting it's value using enabledChkId option
		enabledChId.setChecked(true);
		emailChkId.setChecked(true);
		// smsChkId.setChecked(false);
		percentRadioBtId.setChecked(false);
		numRadioBtId.setChecked(true);
		percentTxtbId.setValue("");
		numTxtbId.setValue("1000");
		alertDivId.setVisible(true);
		percentTxtbId.setDisabled(true);
		numTxtbId.setDisabled(false);
		addBtnId.setFocus(true);
		updateBtnId.setFocus(true);
		phoneChId.setChecked(false);
		valueTbId.setValue("");
		
		
		
		//Changes
		//List<Listitem> listItemsPtsActiveDateValue = ptsActiveValueLbId.getItems();
		List<Listitem> listItemsEarnValue = earnValueTypeLbId.getItems();
		List<Listitem> listItemsEarnType = earnTypeLbId.getItems();
		earnValueTypeLbId.selectItem(listItemsEarnValue.get(0));//this.onSelect$earnValueTypeLbId();
		earnTypeLbId.setSelectedItem(listItemsEarnType.get(0));//this.onSelect$earnTypeLbId();
		//enableDateChkId.setChecked(false);//this.onCheck$enableDateChkId();
	//	autoConvertRadioId.setChecked(true);
		valueTbId.setValue("");
		conversionToTbId.setValue("");
		coversionfromTbId.setValue("");
		amountSpentTbId.setValue("");
		//ptsActiveValueLbId.setSelectedItem(listItemsPtsActiveDateValue.get(0));
		this.onSelect$earnTypeLbId();
		this.onSelect$earnValueTypeLbId();
		//this.onCheck$enableDateChkId();
		
		//Changes
		
		
		
	}

	private void deleteSparkbaseDetails(SparkBaseLocationDetails sparkBaseLocationDetails) {
		newLocaitonGbId.setVisible(false);
		resetToDefaultValues();
		sparkBaseLocationDetailsDaoForDML.delete(sparkBaseLocationDetails);
		addLocationBtnId.setVisible(true);

	}

	private void editSparkbaseDetails(SparkBaseLocationDetails sparkBaseLocationDetails) {

		logger.info("<<<----Just Entered ---->>>");

		addBtnId.setVisible(false);
		updateBtnId.setVisible(true);
		newLocaitonGbId.setVisible(true);
		addLocationBtnId.setVisible(false);

		systemIdTBId.setValue(sparkBaseLocationDetails.getSystemId());
		clientIdTBId.setValue(sparkBaseLocationDetails.getClientId());
		integrationUserNameTBId.setValue(sparkBaseLocationDetails.getIntegrationUserName());
		integrationPasswordTBId.setValue(sparkBaseLocationDetails.getIntegrationPassword());
		LocationIdTBId.setValue(sparkBaseLocationDetails.getLocationId());
		TransactionLocationIdTBId.setValue(sparkBaseLocationDetails.getTransactionLocationId());
		terminalIdTBId.setValue(sparkBaseLocationDetails.getTerminalId());
		initiatorPasswordTBId.setValue(sparkBaseLocationDetails.getInitiatorPassword());
		externalIdTBId.setValue(sparkBaseLocationDetails.getExternalId());

		// new

		enabledChkId.setChecked(sparkBaseLocationDetails.isEnabled());

		phoneChId.setChecked(sparkBaseLocationDetails.isMobileUnique());

		// on click of edit the screen is scrolled down.
		addBtnId.setFocus(true);
		updateBtnId.setFocus(true);
		// Changes

		try {
			String EarnValueType = sparkBaseLocationDetails.getEarnValueType();
			String EarnType = sparkBaseLocationDetails.getEarnType();

			List<Listitem> listItemsEarnValue = earnValueTypeLbId.getItems();
			logger.info("======1" + EarnValueType);
			if (EarnValueType != null) {
				for (Listitem listitem : listItemsEarnValue) {

					if (listitem.getValue().toString().equals(EarnValueType)) {
						earnValueTypeLbId.setSelectedItem(listitem);
						logger.info("=====Check2");
						this.onSelect$earnValueTypeLbId();
						break;
					}
				}
			} else {
				earnValueTypeLbId.selectItem(listItemsEarnValue.get(0));
				this.onSelect$earnValueTypeLbId();
			}

			List<Listitem> listItemsEarnType = earnTypeLbId.getItems();
			if (EarnType != null) {
				logger.info("======" + EarnType);
				for (Listitem listitem : listItemsEarnType) {

					if (listitem.getValue().toString().equals(EarnType)) {

						earnTypeLbId.setSelectedItem(listitem);
						logger.info("=====Check");
						this.onSelect$earnTypeLbId();
						break;
					}
				}
			} else {
				earnTypeLbId.setSelectedItem(listItemsEarnType.get(0));
				this.onSelect$earnTypeLbId();
			}
/*			List<Listitem> listItemsPtsActiveDateValue = ptsActiveValueLbId.getItems();
			if (sparkBaseLocationDetails.getActivationFlag() != '\0'
					&& sparkBaseLocationDetails.getActivationFlag() == 'Y') {
				enableDateChkId.setChecked(true);
				this.onCheck$enableDateChkId();
				if (sparkBaseLocationDetails.getPtsActiveDateValue() != null) {


					for (Listitem listitem : listItemsPtsActiveDateValue) {

						if (listitem.getValue().toString()
								.equals(sparkBaseLocationDetails.getPtsActiveDateValue().toString())) {

							ptsActiveValueLbId.setSelectedItem(listitem);
							logger.info("=====Check3");
							break;
						}
					}

				}
			}
			
			else {
				enableDateChkId.setChecked(false);
				ptsActiveValueLbId.setSelectedItem(listItemsPtsActiveDateValue.get(0));
				this.onCheck$enableDateChkId();
				// Change the value of ptsActiveDateValue at update
			}
*/
		/*	if (sparkBaseLocationDetails.getConversionType() != null) {
				logger.info("inside ConversionType");
				if (sparkBaseLocationDetails.getConversionType().toString()
						.equals(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)) {
					autoConvertRadioId.setChecked(true);
					logger.info("inside ConversionTypeIF");
				} else {
					logger.info("inside ConversionTypeELSE");
					onDemandRadioId.setChecked(true);
				}
			} else {
				onDemandRadioId.setChecked(true);
			}
			*/
			valueTbId.setValue(sparkBaseLocationDetails.getEarnValue() != null
					? sparkBaseLocationDetails.getEarnValue().toString() : "");
			conversionToTbId.setValue(sparkBaseLocationDetails.getConvertToAmount() != null
					? sparkBaseLocationDetails.getConvertToAmount().toString() : "");
			coversionfromTbId.setValue(sparkBaseLocationDetails.getConvertFromPoints() != null
					? sparkBaseLocationDetails.getConvertFromPoints().toString() : "");
			amountSpentTbId.setValue(sparkBaseLocationDetails.getEarnOnSpentAmount() != null
					? sparkBaseLocationDetails.getEarnOnSpentAmount().toString() : "");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Changes

		if (sparkBaseLocationDetails.isEnableAlerts()) {
			enabledChId.setChecked(sparkBaseLocationDetails.isEnableAlerts());
			alertDivId.setVisible(enabledChId.isChecked());
			emailChkId.setChecked(sparkBaseLocationDetails.isEmailAlerts());
			// smsChkId.setChecked(sparkBaseLocationDetails.isSmsAlerts());

			if (sparkBaseLocationDetails.getCountType() != null && sparkBaseLocationDetails.getCountType()
					.equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE)) {
				percentRadioBtId.setChecked(true);
				percentTxtbId.setValue(sparkBaseLocationDetails.getCountValue());
				percentTxtbId.setDisabled(false);
				numTxtbId.setDisabled(true);
				numTxtbId.setValue("");
			} else if (sparkBaseLocationDetails.getCountType() != null && sparkBaseLocationDetails.getCountType()
					.equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_COUNT)) {
				numRadioBtId.setChecked(true);
				numTxtbId.setValue(sparkBaseLocationDetails.getCountValue());
				numTxtbId.setDisabled(false);
				percentTxtbId.setDisabled(true);
				percentTxtbId.setValue("");
			}

		} else {
			enabledChId.setChecked(sparkBaseLocationDetails.isEnableAlerts());
			alertDivId.setVisible(enabledChId.isChecked());
			emailChkId.setChecked(sparkBaseLocationDetails.isEmailAlerts());
			percentRadioBtId.setChecked(false);
			numRadioBtId.setChecked(false);
			percentTxtbId.setDisabled(true);
			numTxtbId.setDisabled(true);
			percentTxtbId.setValue("");
			numTxtbId.setValue("");
		}

		String initiatrType = sparkBaseLocationDetails.getInitiatorType();
		logger.info("IntiatrType===>" + initiatrType);
		List<Listitem> listItems = initiatorTypeLBId.getItems();

		for (Listitem listitem : listItems) {

			if (listitem.getValue().toString().equals(initiatrType)) {
				initiatorTypeLBId.setSelectedItem(listitem);
				break;
			}
		}

		if (sparkBaseLocationDetails.isEnabled()) {
			enabledChkId.setChecked(true);
		}

		if (sparkBaseLocationDetails.isMobileUnique()) {
			phoneChId.setChecked(true);
		}

		setOrganisations(sparkBaseLocationDetails.getUserOrganization());

		setOrganizationUsers(sparkBaseLocationDetails);
		/*
		 * Components.removeAllChildren(usersComboboxId);
		 * usersComboboxId.setText(""); Comboitem comboItem = null; Users user =
		 * usersDao.findByUserId(sparkBaseLocationDetails.getOrgUserId());
		 * if(user != null){ String userNameStr =
		 * Utility.getOnlyUserName(user.getUserName()); comboItem = new
		 * Comboitem(userNameStr); comboItem.setValue(user);
		 * comboItem.setParent(usersComboboxId);
		 * usersComboboxId.setSelectedItem(comboItem); }
		 */

		editSBLocationDetails = sparkBaseLocationDetails;

	}

	public void onClick$percentRadioBtId() {
		percentTxtbId.setDisabled(false);
		numTxtbId.setDisabled(true);
		if (sparkbaseLocation != null && sparkbaseLocation.getCountType() != null && sparkbaseLocation.getCountType()
				.equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE)) {
			percentTxtbId.setValue(sparkbaseLocation.getCountValue());
			numTxtbId.setValue("");
		} else {
			percentTxtbId.setValue("");
			numTxtbId.setValue("");
		}
	}

	public void onClick$numRadioBtId() {
		numTxtbId.setDisabled(false);
		percentTxtbId.setDisabled(true);
		if (sparkbaseLocation != null && sparkbaseLocation.getCountType() != null
				&& sparkbaseLocation.getCountType().equalsIgnoreCase(Constants.SB_CARDS_AVAILABLE_COUNT_TYPE_COUNT)) {
			numTxtbId.setValue(sparkbaseLocation.getCountValue());
			percentTxtbId.setValue("");
		} else {
			numTxtbId.setValue("");
			percentTxtbId.setValue("");
		}
	}

	public boolean checkIfNumber(String in) {
		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}// checkIfNumber

	public void onCheck$enabledChId() {
		alertDivId.setVisible(enabledChId.isChecked());
	}

	public void onClick$updateBtnId() {
		updateBtnId.setFocus(true);
		if (!validateFields()) {
			return;
		}
		updateSBSettings(true);
		logger.info("Success : updated location details successfully.");

		addBtnId.setVisible(true);
		updateBtnId.setVisible(false);
		newLocaitonGbId.setVisible(false);
		addLocationBtnId.setVisible(true);
		editSBLocationDetails = null;
	}

	public void onClick$addLocationBtnId() {

		newLocaitonGbId.setVisible(true);
		addLocationBtnId.setVisible(false);

		// method to reset the values

		resetToDefaultValues();

		/*
		 * systemIdTBId.setValue(""); clientIdTBId.setValue("");
		 * integrationUserNameTBId.setValue("");
		 * integrationPasswordTBId.setValue(""); LocationIdTBId.setValue("");
		 * TransactionLocationIdTBId.setValue(""); terminalIdTBId.setValue("");
		 * initiatorPasswordTBId.setValue(""); externalIdTBId.setValue("");
		 * addBtnId.setVisible(true); updateBtnId.setVisible(false);
		 * orgIdsComboboxId.setSelectedIndex(0);
		 * //usersComboboxId.setSelectedIndex(0); //in sparkBaseSettings.zul we
		 * are setting value for Enabled default as "true" //so here we are not
		 * setting it's value using enabledChkId option
		 * enabledChId.setChecked(false); emailChkId.setChecked(false);
		 * //smsChkId.setChecked(false); percentRadioBtId.setChecked(false);
		 * numRadioBtId.setChecked(false); percentTxtbId.setValue("");
		 * numTxtbId.setValue(""); alertDivId.setVisible(false);
		 * percentTxtbId.setDisabled(true); numTxtbId.setDisabled(true);
		 */
		sparkbaseLocation = null;

		// SparkBaseLocationDetails sbloc =
		// (SparkBaseLocationDetails)orgIdsComboboxId.getSelectedItem().getValue();
		// logger.debug("sbloc = "+sbloc.getClientId());

		// this is new line of code
		setOrganisations(null);

		setOrganizationUsers(null);

	}

	public void onSelect$orgIdsComboboxId() {
		setOrganizationUsers(null);
	}

	public void setOrganizationUsers(SparkBaseLocationDetails sparkbaseLocation) {

		Components.removeAllChildren(usersComboboxId);
		usersComboboxId.setText("");
		UserOrganization userOrg = null;
		Comboitem comboItem = orgIdsComboboxId.getSelectedItem();
		if (comboItem != null) {
			userOrg = (UserOrganization) comboItem.getValue();
			List<Users> usersOfOrg = usersDao.getPOSListUsersByOrgId(userOrg.getUserOrgId());
			Comboitem userCmbItem = null;
			for (Users user : usersOfOrg) {
				String userNameStr = Utility.getOnlyUserName(user.getUserName());
				userCmbItem = new Comboitem();
				userCmbItem.setLabel(userNameStr);
				userCmbItem.setValue(user);
				userCmbItem.setParent(usersComboboxId);

				if (sparkbaseLocation != null && sparkbaseLocation.getOrgUserId() != null
						&& sparkbaseLocation.getOrgUserId().longValue() == user.getUserId().longValue()) {
					usersComboboxId.setSelectedItem(userCmbItem);
				} else if (sparkbaseLocation == null)
					usersComboboxId.setSelectedIndex(0);
			}
		}

	}
	
	
	// Changes

	private boolean checkIfLong(String in) {
		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
	//checkIfNum()

		public boolean checkIfDouble(String in) {
			try {
				logger.info("===>enter try");
				Double.parseDouble(in);
			} catch (NumberFormatException ex) {
				return false;
			}
			return true;
		}// checkIfNumber()
	

	// onCheck$enableDateChkId()
	/*public void onCheck$enableDateChkId() {
		if (enableDateChkId.isChecked()) {
			activateAfterDivId.setVisible(true);
		} else {
			activateAfterDivId.setVisible(false);
		}
	}*/// onCheck$enableDateChkId()

	// onSelect$earnValueTypeLbId()

	public void onSelect$earnTypeLbId() {

		if (earnTypeLbId.getSelectedItem().getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
			coversionfromTbId.setDisabled(true);
			conversionToTbId.setDisabled(true);
			//autoConvertRadioId.setDisabled(true);
			//onDemandRadioId.setDisabled(true);
		} else {
			coversionfromTbId.setDisabled(false);
			conversionToTbId.setDisabled(false);
//			autoConvertRadioId.setDisabled(false);
			//onDemandRadioId.setDisabled(false);
			if (OCConstants.LOYALTY_SERVICE_TYPE_SBTOOC
					.equalsIgnoreCase(GetUser.getUserObj().getloyaltyServicetype())) {
				/*autoConvertRadioId.setSelected(true);
				autoConvertRadioId.setDisabled(true);*/
				//onDemandRadioId.setDisabled(true);
				pointsConversionDivId.setVisible(false);
			}
		}
	}// onSelect$earnTypeLbId()

	// onSelect$ruleTypeLbId()

	public void onSelect$earnValueTypeLbId() {
		if (earnValueTypeLbId.getSelectedItem().getValue().toString()
				.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_PERCENTAGE)) {
			amountDivId.setVisible(false);
			percentDivId.setVisible(true);
		} else {
			amountDivId.setVisible(true);
			percentDivId.setVisible(false);
		}
	}// onSelect$earnValueTypeLbId()

	// Changes

}

class ParseCSVFileThread extends Thread {

	private File fileName;
	private SparkBaseLocationDetails sparkBaseLocationDetails;
	private SparkBaseCardDao sparkBaseCardDao;
	private SparkBaseCardDaoForDML sparkBaseCardDaoForDML;
	private int colsCount;
	private String cardType;

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public ParseCSVFileThread(File fileName, SparkBaseLocationDetails sparkBaseLocationDetails,
			SparkBaseCardDaoForDML sparkBaseCardDaoForDML, int colsCount, String cardType) {
		// TODO Auto-generated constructor stub
		this.fileName = fileName;
		this.sparkBaseLocationDetails = sparkBaseLocationDetails;
		this.sparkBaseCardDaoForDML = sparkBaseCardDaoForDML;
		this.colsCount = colsCount;
		this.cardType = cardType;
	}

	@Override
	public void run() {

		try {
			if (!fileName.exists()) {
				logger.info("Error : Source File does not exist .");
				return;
			}

			BufferedReader br = new BufferedReader(new FileReader(fileName));

			String lineStr = "";
			StringTokenizer stringTokenizer = null;
			SparkBaseCard sparkBaseCard = null;

			while ((lineStr = br.readLine()) != null) {
				try {
					lineStr = lineStr.replace("\t", ",");
					stringTokenizer = new StringTokenizer(lineStr, ",");
					sparkBaseCard = new SparkBaseCard();

					if (colsCount == 3) {
						stringTokenizer.nextToken(); // to ignore the sequence
														// number
					}

					logger.info("-->" + sparkBaseLocationDetails.getSparkBaseLocationDetails_id());
					
					Long cardLong = OptCultureUtils.validateCardNumber(stringTokenizer.nextToken().trim());
					sparkBaseCard.setCardId(""+cardLong);
					// String cardId = stringTokenizer.nextToken();
					String cardPin = stringTokenizer.nextToken();
					// Add 0(zero) when the card pin length is 5

					if (cardPin.trim().length() == 5)
						cardPin = "0" + cardPin.trim();
					logger.info("cardpin=== " + cardPin);

					String pattern = "[0-9]+";

					// Create a Pattern object
					Pattern r = Pattern.compile(pattern);

					// Now create matcher object.
					Matcher m = r.matcher(cardPin);
					if (m.matches()) {

						sparkBaseCard.setCardPin(cardPin);
						sparkBaseCard.setSparkBaseLocationId(sparkBaseLocationDetails);
						sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_INVENTORY);
						sparkBaseCard.setCardType(cardType);
						sparkBaseCardDaoForDML.saveOrUpdate(sparkBaseCard);
					}

				} catch (DataIntegrityViolationException dive) {

					logger.info("Duplication Entry : spark base card already exists " + lineStr);
					logger.info("DataIntegrityViolationException div ", dive);
				} catch (Exception e) {
					logger.info("Exception at line==>" + lineStr);
					logger.info("Exception at lineStr:", e);
				}
			}

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception  ::", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception while reading csv file:  ::", e);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception while reading csv file:  ::", e);

		}

	}

}

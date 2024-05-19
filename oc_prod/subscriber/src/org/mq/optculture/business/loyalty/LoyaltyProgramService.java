package org.mq.optculture.business.loyalty;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.AutoSMS;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.CouponCodes;
import org.mq.marketer.campaign.beans.Coupons;
import org.mq.marketer.campaign.beans.CustomTemplates;
import org.mq.marketer.campaign.beans.EmailQueue;
import org.mq.marketer.campaign.beans.LoyaltyAutoComm;
import org.mq.marketer.campaign.beans.LoyaltyCardSet;
import org.mq.marketer.campaign.beans.LoyaltyProgram;
import org.mq.marketer.campaign.beans.LoyaltyProgramExclusion;
import org.mq.marketer.campaign.beans.LoyaltyProgramTier;
import org.mq.marketer.campaign.beans.LoyaltySettings;
import org.mq.marketer.campaign.beans.LoyaltyThresholdAlerts;
import org.mq.marketer.campaign.beans.LoyaltyThresholdBonus;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.beans.LtySettingsActivityLogs;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.RetailProSalesCSV;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.AutoSMSDao;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.CouponCodesDao;
import org.mq.marketer.campaign.dao.CouponsDao;
import org.mq.marketer.campaign.dao.CustomTemplatesDao;
import org.mq.marketer.campaign.dao.CustomerSalesUpdateDataDao;
import org.mq.marketer.campaign.dao.EmailQueueDao;
import org.mq.marketer.campaign.dao.EmailQueueDaoForDML;
import org.mq.marketer.campaign.dao.MailingListDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.RetailProSalesDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.optculture.business.helper.LoyaltyCardGenerator;
import org.mq.optculture.data.dao.LoyaltyAutoCommDao;
import org.mq.optculture.data.dao.LoyaltyAutoCommDaoForDML;
import org.mq.optculture.data.dao.LoyaltyCardSetDao;
import org.mq.optculture.data.dao.LoyaltyCardSetDaoForDML;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyCardsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramDao;
import org.mq.optculture.data.dao.LoyaltyProgramDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDao;
import org.mq.optculture.data.dao.LoyaltyProgramExclusionDaoForDML;
import org.mq.optculture.data.dao.LoyaltyProgramTierDao;
import org.mq.optculture.data.dao.LoyaltyProgramTierDaoForDML;
import org.mq.optculture.data.dao.LoyaltySettingsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDao;
import org.mq.optculture.data.dao.LoyaltyThresholdAlertsDaoForDML;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDao;
import org.mq.optculture.data.dao.LoyaltyThresholdBonusDaoForDML;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LtySettingsActivityLogsDao;
import org.mq.optculture.data.dao.LtySettingsActivityLogsDaoForDML;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class LoyaltyProgramService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private LoyaltyProgramDao loyaltyProgramDao;
	private LoyaltyProgramDaoForDML loyaltyProgramDaoForDML;
	private LoyaltyCardSetDao loyaltyCardSetDao;
	private LoyaltyCardSetDaoForDML loyaltyCardSetDaoForDML;
	private LoyaltyProgramTierDao loyaltyProgramTierDao;
	private LoyaltyProgramTierDaoForDML loyaltyProgramTierDaoForDML;
	private LoyaltyThresholdBonusDao loyaltyThresholdBonusDao;
	private LoyaltyThresholdBonusDaoForDML loyaltyThresholdBonusDaoForDML;
	private LoyaltyProgramExclusionDao loyaltyProgramExclusionDao;
	private LoyaltyProgramExclusionDaoForDML loyaltyProgramExclusionDaoForDML;
	private LoyaltyCardsDao loyaltyCardsDao;
	private LoyaltyCardsDaoForDML loyaltyCardsDaoForDML;
	private CouponsDao couponsDao; 
	private CouponCodesDao couponCodesDao;
	private OrganizationStoresDao organizationStoresDao;
	private CustomTemplatesDao customTemplatesDao;
	private LoyaltyAutoCommDao  loyaltyAutoCommDao;
	private LoyaltyAutoCommDaoForDML  loyaltyAutoCommDaoForDML;
	private SkuFileDao  skuFileDao;
	private LoyaltyTransactionChildDao loyaltyTransactionChildDao;
	private ContactsLoyaltyDao contactsLoyaltyDao;
	private ContactsLoyaltyDaoForDML contactsLoyaltyDaoForDML;

	private RetailProSalesDao retailProSalesDao;
	private ContactsDao contactsDao;
	private AutoSMSDao autoSMSDao;
	private MailingListDao mailingListDao;
	private EmailQueueDao emailQueueDao;
	private EmailQueueDaoForDML emailQueueDaoForDML;
	private CustomerSalesUpdateDataDao customerSalesUpdatedDao ;

	public List<LoyaltyProgram> getProgList(Long userId) {
		List<LoyaltyProgram> progList = null;
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			progList = loyaltyProgramDao.getProgListByUserId(userId);


		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return progList;
	}
	public Long getCustomizedOTPEnabledProgList(Long userId, boolean checkforRedemption) {
		List<LoyaltyProgram> progList = null;
		Long loyaltyProgramID = null;
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			progList = loyaltyProgramDao.getCustomizedOTPEnabledProgListBy(userId, checkforRedemption);
			if(progList != null && !progList.isEmpty()) {
				
				for (LoyaltyProgram loyaltyProgram : progList) {
					loyaltyProgramID = loyaltyProgram.getProgramId();
					if(loyaltyProgram.getDefaultFlag()==OCConstants.FLAG_YES) return loyaltyProgramID;
					
				}
			}

		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return loyaltyProgramID;
	}

	public Long onSaveProgram(String prgmName, String desc, boolean mobileNoBasedFlag, boolean tierEnable,
			String noOfTiers, String status, boolean defaultEnable, boolean uniqMblEnable,boolean uniqEmailEnable,
			Set<Listitem> regReqSet, boolean otpEnable, boolean partialReversalEnable, Double otpLimit, Long userId, Long prgmId, String draftStatus, String vallidationString, String loyaltyType, String programType, boolean includeRedeemedAmount) {
		//, Double minReceiptAmtRedemValue, Double minBalanceToRedemValue, String minBalanceType
		try {
			boolean isChanged = false;
			//boolean sendEmailFlag = false;
			StringBuffer logDetails = null;
			LoyaltyProgram ltyPrgmObj = null;
			if(prgmId != null) {
				ltyPrgmObj = getProgmObj(prgmId);
				if(!ltyPrgmObj.getProgramName().equalsIgnoreCase(prgmName) || !ltyPrgmObj.getDescription().equalsIgnoreCase(desc) || 
					!ltyPrgmObj.getMembershipType().equalsIgnoreCase(mobileNoBasedFlag == true ? OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE : OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD) ||
					ltyPrgmObj.getTierEnableFlag() != (tierEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					ltyPrgmObj.getNoOfTiers() != Integer.parseInt(noOfTiers) || !ltyPrgmObj.getStatus().equalsIgnoreCase(status) ||
					//ltyPrgmObj.getRedemptionPercentageLimit() != redemPercentageLimit || ltyPrgmObj.getRedemptionValueLimit() != redemValueLimit ||
					ltyPrgmObj.getDefaultFlag() != (defaultEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					ltyPrgmObj.getUniqueMobileFlag() != (uniqMblEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					ltyPrgmObj.getUniqueEmailFlag() != (uniqEmailEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO)){
					//ltyPrgmObj.getRedemptionOTPFlag() != (otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					//ltyPrgmObj.getPartialReversalFlag() != (partialReversalEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO)
					//ltyPrgmObj.getConsiderRedeemedAmountFlag() !=(includeRedeemedAmount == true ? OCConstants.FLAG_YES:OCConstants.FLAG_NO) ||
					//ltyPrgmObj.getMinReceiptAmtValue()!= minReceiptAmtRedemValue || ltyPrgmObj.getMinBalanceRedeemValue()!= minBalanceToRedemValue ||
					//ltyPrgmObj.isIssuanceDisable() != (IssuanceDisabled) ||//?
					//(ltyPrgmObj.getOtpLimitAmt() != null && otpLimit == null) ||
					//(ltyPrgmObj.getOtpLimitAmt() == null && otpLimit != null) ||
					//(ltyPrgmObj.getOtpLimitAmt() != null && otpLimit != null && ltyPrgmObj.getOtpLimitAmt().doubleValue() != otpLimit.doubleValue())
					isChanged = true;
					
					/*if(ltyPrgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) && 
					//(ltyPrgmObj.getRedemptionOTPFlag() != (otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					(ltyPrgmObj.getOtpLimitAmt() != null && otpLimit == null) ||
					(ltyPrgmObj.getOtpLimitAmt() == null && otpLimit != null) ||
					(ltyPrgmObj.getOtpLimitAmt() != null && otpLimit != null && ltyPrgmObj.getOtpLimitAmt().doubleValue() != otpLimit.doubleValue())){
						sendEmailFlag = true;
					}*/
					
					logDetails = logProgramChanges(ltyPrgmObj.getProgramId(), userId, null, false);
				}
			}
			else {
				isChanged = true;
				ltyPrgmObj = new LoyaltyProgram();
				logDetails = logProgramChanges(null, userId, null, false);
			}
			ltyPrgmObj.setProgramName(prgmName);
			ltyPrgmObj.setDescription(desc);
			ltyPrgmObj.setMembershipType(mobileNoBasedFlag == true ? OCConstants.LOYALTY_MEMBERSHIP_TYPE_MOBILE : OCConstants.LOYALTY_MEMBERSHIP_TYPE_CARD);
			ltyPrgmObj.setTierEnableFlag(tierEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			ltyPrgmObj.setNoOfTiers(Integer.parseInt(noOfTiers));
			ltyPrgmObj.setStatus(status);
			ltyPrgmObj.setDefaultFlag(defaultEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			ltyPrgmObj.setUniqueMobileFlag(uniqMblEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			ltyPrgmObj.setUniqueEmailFlag(uniqEmailEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			//ltyPrgmObj.setRedemptionOTPFlag(otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			//ltyPrgmObj.setPartialReversalFlag(partialReversalEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			//ltyPrgmObj.setConsiderRedeemedAmountFlag(includeRedeemedAmount == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
			//ltyPrgmObj.setIssuanceDisable(IssuanceDisabled);
			//ltyPrgmObj.setOtpLimitAmt(otpLimit);
			ltyPrgmObj.setProgramType(programType);
			ltyPrgmObj.setRewardType(loyaltyType);
			ltyPrgmObj.setValidationRule(vallidationString);
			//ltyPrgmObj.setRedemptionPercentageLimit(redemPercentageLimit);
			//ltyPrgmObj.setRedemptionValueLimit(redemValueLimit);
			//ltyPrgmObj.setMinReceiptAmtValue(minReceiptAmtRedemValue);
			//ltyPrgmObj.setMinBalanceRedeemValue(minBalanceToRedemValue);
			//ltyPrgmObj.setMinBalanceType(minBalanceType);
			String regReqStr = "";
			if(regReqSet != null) {
				for (Listitem listitem : regReqSet) {

					if(regReqStr.isEmpty()) {
						regReqStr = listitem.getValue().toString();
					}else {

						regReqStr = regReqStr + Constants.ADDR_COL_DELIMETER + listitem.getValue().toString()  ;
					}
				}
			}
			ltyPrgmObj.setRegRequisites(regReqStr);
			ltyPrgmObj.setUserId(userId);
			Long orgId = getOrgId(userId);
			ltyPrgmObj.setOrgId(orgId);
			if(prgmId != null) {
				ltyPrgmObj.setModifiedDate(Calendar.getInstance());
				ltyPrgmObj.setModifiedBy(userId.toString());
			}
			else {
				ltyPrgmObj.setCreatedDate(Calendar.getInstance());
				ltyPrgmObj.setCreatedBy(userId.toString());
			}
			ltyPrgmObj.setDraftStatus(draftStatus);
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			loyaltyProgramDaoForDML = (LoyaltyProgramDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_DAO_FOR_DML);
			//loyaltyProgramDao.saveOrUpdate(ltyPrgmObj);
			loyaltyProgramDaoForDML.saveOrUpdate(ltyPrgmObj);
			loyaltyProgramDaoForDML.saveOrUpdate(ltyPrgmObj);
			if(isChanged){
				logProgramChanges(ltyPrgmObj.getProgramId(), userId, logDetails, true);
			}

			return ltyPrgmObj.getProgramId();
		} catch (Exception e) {
			logger.error("Exception::",e);
			return null;
		}

	}

	public Long getOrgId(Long userId) { 
		Long orgId=null;
		try {
			UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			orgId = usersDao.findByUserId(userId).getUserOrganization().getUserOrgId();
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return orgId;
	}

	public LoyaltyProgram getProgmObj(Long prgmId) {
		LoyaltyProgram progObj = null;
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			progObj = loyaltyProgramDao.findById(prgmId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return progObj;

	}

	public List<LoyaltyCardSet> getCardsetList(Long prgmId) {
		List<LoyaltyCardSet> list = null;
		try {
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			list = loyaltyCardSetDao.findByProgramId(prgmId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return list;
	}

	public void onAddCardSet(String cardSetName, String quantity,
			String cardsetType,String genTyp, String status, char migrationFlag, Long prgmId, Long userId, int linkedTierLevel, String cardGenerationType) {
		try {

			setCardGenFlag(prgmId);

			LoyaltyCardSet ltyCrdSetObj = new LoyaltyCardSet();
			ltyCrdSetObj.setCardSetName(cardSetName);
			if(quantity==null || quantity.isEmpty()) {
				ltyCrdSetObj.setQuantity(0l);
				ltyCrdSetObj.setStatus(status);
			}else {
				ltyCrdSetObj.setQuantity(Long.parseLong(quantity));
				ltyCrdSetObj.setStatus("Processing");
			}
			ltyCrdSetObj.setGenerationType(genTyp);
			ltyCrdSetObj.setCardSetType(cardsetType);
			ltyCrdSetObj.setMigratedFlag(migrationFlag);
			ltyCrdSetObj.setProgramId(prgmId);
			ltyCrdSetObj.setCreatedDate(Calendar.getInstance());
			ltyCrdSetObj.setCreatedBy(userId.toString());
			ltyCrdSetObj.setLinkedTierLevel(linkedTierLevel);
			ltyCrdSetObj.setCardGenerationType(cardGenerationType);
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardSetDaoForDML = (LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
			//loyaltyCardSetDao.saveOrUpdate(ltyCrdSetObj);
			loyaltyCardSetDaoForDML.saveOrUpdate(ltyCrdSetObj);
			long orgId = getOrgId(userId);
			if(quantity != null && !quantity.isEmpty() && Long.parseLong(quantity) > 0) {

				//long orgId = getOrgId(userId);
				/*LoyaltyCardsGeneration cardsGen = new LoyaltyCardsGeneration(Long.parseLong(quantity), genTyp, ltyCrdSetObj.getCardSetId(),
						status, prgmId, orgId, userId);*/
				// generate cards
				LoyaltyCardGenerator cardsGen = new LoyaltyCardGenerator(Long.parseLong(quantity), genTyp, ltyCrdSetObj.getCardSetId(),
						status, prgmId, orgId, userId);

				Thread thrd = new Thread(cardsGen);
				thrd.start();

			}else{
				resetCardGenFlag(prgmId);
			}

			enableThresholdAlertSettings(userId);
			//reset cardgenflag for organisation
			//resetCardGenFlag(prgmId);

		} catch (Exception e) {
			logger.error("Exception::",e);
			return;
		}
	}

	private void enableThresholdAlertSettings(Long userId) {
		try {
			LoyaltyThresholdAlerts alertObj = null;
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			List<LoyaltyCardSet> cardSetList = loyaltyCardSetDao.findByUserId(userId);
			if(cardSetList != null && cardSetList.size() == 1) {
				UsersDao usersDao = (UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				UsersDaoForDML usersDaoForDML = (UsersDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
				Users userObj = usersDao.findByUserId(userId);
				alertObj = findPwdByUserID(userId);
				if(alertObj == null || (alertObj != null && alertObj.getEnableAlerts() != OCConstants.FLAG_YES)){
					if(alertObj == null){
						alertObj = new LoyaltyThresholdAlerts();
						alertObj.setUserId(userId);
						alertObj.setOrgId(userObj.getUserOrganization().getUserOrgId());
					}
					alertObj.setEnableAlerts(OCConstants.FLAG_YES);
					alertObj.setAlertEmailId(userObj.getEmailId() != null ? userObj.getEmailId() : "");
					alertObj.setAlertMobilePhn(userObj.getPhone() != null ? userObj.getPhone() : "");
					alertObj.setCountType(OCConstants.LOYALTY_CARDS_AVAILABLE_COUNT_TYPE_PERCENTAGE);
					alertObj.setCountValue("10");

					saveOrUpdateThresholdAlerts(alertObj);
				}
			}
		}
		catch(Exception e) {
			logger.error("Exception ::",e);
		}

	}

	public LoyaltyProgramTier saveTier(Long programId, String tierType, String tierName,
			String earnType, String earnValueType, Double maxcap,boolean issuanceEnable, Double earnValue,
			Double earnOnSpentAmount, String ptsActiveDateType,
			Long ptsActiveDateValue, Double convertFromPoints,
			Double convertToAmount, String conversionType,
			String tierUpgdConstraint, Double tierUpgdConstraintValue, Long tierId, Long userId, boolean dateEnable,
			Long tierUpgradeCumulativeValue , String roundingType,boolean activateAfterDisableAllStore,String disallowActivateAfterStores,Double minReceiptValue,Double minBalanceValue,Double redemptionPercentageLimit,Double redemptionValueLimit,String expType,Long expAftrMonths,Double crossOverBonus,boolean includeRedeemedAmount,boolean otpEnable,Double otpLimit,boolean partialReversalEnable,String mulTierRules) {

		try {
			boolean isChanged = false;
			boolean sendEmailFlag = false;
			StringBuffer logDetails = null;
			LoyaltyProgramTier tierObj=null;
			LoyaltyProgram prgmObj = getProgmObj(programId);
			loyaltyProgramTierDao=(LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			loyaltyProgramTierDaoForDML=(LoyaltyProgramTierDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO_FOR_DML);
			logger.info(""+programId+tierType+tierName+earnType+earnValueType+earnValue+earnOnSpentAmount+ptsActiveDateType+ptsActiveDateValue+convertFromPoints+convertToAmount+conversionType+tierUpgdConstraint+tierUpgdConstraintValue);
			if(tierId != null) {
				tierObj = loyaltyProgramTierDao.getTierById(tierId);
				if(!tierObj.getTierType().equalsIgnoreCase(tierType) || !tierObj.getTierName().equalsIgnoreCase(tierName) || 
					!tierObj.getEarnType().equalsIgnoreCase(earnType) || !tierObj.getEarnValueType().equalsIgnoreCase(earnValueType) ||
					tierObj.getEarnValue() != earnValue || tierObj.getEarnOnSpentAmount() != earnOnSpentAmount ||
					tierObj.getMaxcap() != maxcap || tierObj.getMinBalanceValue()!= minBalanceValue || tierObj.getMinReceiptValue()!=minReceiptValue ||
					tierObj.getRedemptionPercentageLimit()!= redemptionPercentageLimit || tierObj.getRedemptionValueLimit()!= redemptionValueLimit ||
					tierObj.getCrossOverBonus()!=crossOverBonus || tierObj.getOtpLimitAmt()!= otpLimit ||
					tierObj.getRedemptionOTPFlag() != (otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					tierObj.getConsiderRedeemedAmountFlag() !=(includeRedeemedAmount == true ? OCConstants.FLAG_YES:OCConstants.FLAG_NO) ||
					tierObj.getPartialReversalFlag() != (partialReversalEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					!tierObj.getRewardExpiryDateType().equalsIgnoreCase(expType) || tierObj.getRewardExpiryDateValue()!= expAftrMonths ||
					(dateEnable == true && tierObj.getActivationFlag() == OCConstants.FLAG_NO) || 
					(dateEnable == false && tierObj.getActivationFlag() == OCConstants.FLAG_YES) ||
					(activateAfterDisableAllStore == true && !tierObj.getActivateAfterDisableAllStore()) ||
					(activateAfterDisableAllStore == false && tierObj.getActivateAfterDisableAllStore()) ||
					(tierObj.getPtsActiveDateType() == null && ptsActiveDateType != null) ||
					(tierObj.getPtsActiveDateType() != null && ptsActiveDateType == null) ||
					(tierObj.getPtsActiveDateType() != null && ptsActiveDateType != null && !tierObj.getPtsActiveDateType().equalsIgnoreCase(ptsActiveDateType)) ||
					(tierObj.getPtsActiveDateValue() == null && ptsActiveDateValue != null) ||
					(tierObj.getPtsActiveDateValue() != null && ptsActiveDateValue == null) ||
					(tierObj.getPtsActiveDateValue() != null && ptsActiveDateValue != null && tierObj.getPtsActiveDateValue().longValue() != ptsActiveDateValue.longValue()) ||
					(tierObj.getConvertFromPoints() == null && convertFromPoints != null) ||
					(tierObj.getConvertFromPoints() != null && convertFromPoints == null) ||
					(tierObj.getConvertFromPoints() != null && convertFromPoints != null && tierObj.getConvertFromPoints().longValue() != convertFromPoints.longValue()) ||
					(tierObj.getConvertToAmount() == null && convertToAmount != null) ||
					(tierObj.getConvertToAmount() != null && convertToAmount == null) ||
					(tierObj.getConvertToAmount() != null && convertToAmount != null && tierObj.getConvertToAmount().longValue() != convertToAmount.longValue()) ||
					(tierObj.getTierUpgdConstraint().equalsIgnoreCase(tierUpgdConstraint)) ||
					(tierObj.getTierUpgdConstraintValue() == null && tierUpgdConstraintValue != null) ||
					(tierObj.getTierUpgdConstraintValue() != null && tierUpgdConstraintValue == null) ||
					(tierObj.getTierUpgdConstraintValue() != null && tierUpgdConstraintValue != null && tierObj.getTierUpgdConstraintValue().doubleValue() != tierUpgdConstraintValue.doubleValue()) ||
					(tierObj.getTierUpgradeCumulativeValue() == null && tierUpgradeCumulativeValue != null) ||
					(tierObj.getTierUpgradeCumulativeValue() != null && tierUpgradeCumulativeValue == null) ||
					(tierObj.getTierUpgradeCumulativeValue() != null && tierUpgradeCumulativeValue != null && tierObj.getTierUpgradeCumulativeValue().longValue() != tierUpgradeCumulativeValue.longValue()) ||
					(tierObj.getOtpLimitAmt() != null && otpLimit == null) ||
					(tierObj.getOtpLimitAmt() == null && otpLimit != null) ||
					(tierObj.getOtpLimitAmt() != null && otpLimit != null && tierObj.getOtpLimitAmt().doubleValue() != otpLimit.doubleValue()) ||
					(tierObj.getRoundingType()!=null && roundingType!=null && !tierObj.getRoundingType().equalsIgnoreCase(roundingType)) ||
					(!tierObj.getMultipleTierUpgrdRules().equalsIgnoreCase(mulTierRules))){
					
					isChanged = true;
					
					if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) && 
							(tierObj.getRedemptionOTPFlag() != (otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO)) ||
							(tierObj.getOtpLimitAmt() != null && otpLimit == null) ||
							(tierObj.getOtpLimitAmt() == null && otpLimit != null) ||
							(tierObj.getOtpLimitAmt() != null && otpLimit != null && tierObj.getOtpLimitAmt().doubleValue() != otpLimit.doubleValue())){
								sendEmailFlag = true;
							}
					
					logDetails = logTierChanges(programId, userId, null, false,sendEmailFlag);
					
				}
			}
			else {
				isChanged = true;
				logDetails = logTierChanges(programId, userId, null, false,sendEmailFlag);
				tierObj = new LoyaltyProgramTier();
			}

			if(isChanged){
				tierObj.setProgramId(programId);
				tierObj.setTierType(tierType);
				tierObj.setTierName(tierName);
				tierObj.setEarnType(earnType);
				tierObj.setEarnValueType(earnValueType);
				tierObj.setEarnValue(earnValue);
				if(issuanceEnable) {
					tierObj.setMaxcap(maxcap);
				}
				tierObj.setIssuanceChkEnable(issuanceEnable);
				tierObj.setEarnOnSpentAmount(earnOnSpentAmount);
				tierObj.setActivationFlag(dateEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
				tierObj.setPtsActiveDateType(ptsActiveDateType);
				tierObj.setPtsActiveDateValue(ptsActiveDateValue);
				tierObj.setConvertFromPoints(convertFromPoints);
				tierObj.setConvertToAmount(convertToAmount);
				tierObj.setConversionType(conversionType);
				tierObj.setTierUpgdConstraint(tierUpgdConstraint);
				tierObj.setTierUpgdConstraintValue(tierUpgdConstraintValue);
				tierObj.setTierUpgradeCumulativeValue(tierUpgradeCumulativeValue);
				tierObj.setMinBalanceValue(minBalanceValue);
				tierObj.setMinReceiptValue(minReceiptValue);
				tierObj.setRedemptionPercentageLimit(redemptionPercentageLimit);
				tierObj.setRedemptionValueLimit(redemptionValueLimit);
				tierObj.setRewardExpiryDateType(expType);
				tierObj.setRewardExpiryDateValue(expAftrMonths);
				tierObj.setCrossOverBonus(crossOverBonus);
				tierObj.setConsiderRedeemedAmountFlag(includeRedeemedAmount == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
				tierObj.setRedemptionOTPFlag(otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
				tierObj.setOtpLimitAmt(otpLimit);
				tierObj.setPartialReversalFlag(partialReversalEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
				tierObj.setMultipleTierUpgrdRules(mulTierRules);
				//tierObj.setTierUpgradeCumulativeType(tierUpgradeCumulativeType);
				tierObj.setCreatedBy(userId.toString());
				tierObj.setCreatedDate(Calendar.getInstance());
				//tierObj.setRewardExpiryDateType(rewardExpDateType);
				//tierObj.setRewardExpiryDateValue(rewardExpDateValue);
				//tierObj.setMembershipExpiryDateType(memExpDateType);
				//tierObj.setMembershipExpiryDateValue(memExpDateValue);
				tierObj.setRoundingType(roundingType);
				tierObj.setActivateAfterDisableAllStore(activateAfterDisableAllStore);
				tierObj.setDisallowActivateAfterStores(disallowActivateAfterStores);
				logger.info(""+tierObj);
				//loyaltyProgramTierDao.saveOrUpdate(tierObj);
				loyaltyProgramTierDaoForDML.saveOrUpdate(tierObj);
				logTierChanges(programId, userId, logDetails, true,sendEmailFlag);
			}
			/*if(ruleType.equalsIgnoreCase("Split")){
				
				saveEarnRules(null, tierObj.getTierId(), programId, null, null, null, null, userId, "D");
				saveEarnRules(null, tierObj.getTierId(), programId, null, null, null, null, userId, "ND");

				
			}*/
			
			return tierObj;
		} catch (Exception e) {
			logger.error("Exception::",e);
			return null;
		}

	}
	
	public LoyaltyProgramTier savePerkTier(Long programId,String tierType,String tierName,String perkIssuanceType,Double perkIssuanceValue,String earnValueType,Double earnOnSpentAmount,
			String perkExpType,Long perkExpDateValue,Long perkUsageLimit,String perkUsageLimitType,Double convertFromPoints,Double convertToAmount,
			   Double redemptionPercentageLimit,Double redemptionValueLimit,Double minReceiptValue,Double minBalanceValue,Long tierId,Long userId,Double otpLimit, boolean otpEnable) {

		try {
			boolean isChanged = false;
			boolean sendEmailFlag = false;
			StringBuffer logDetails = null;
			LoyaltyProgramTier tierObj=null;
			LoyaltyProgram prgmObj = getProgmObj(programId);
			loyaltyProgramTierDao=(LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			loyaltyProgramTierDaoForDML=(LoyaltyProgramTierDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO_FOR_DML);
			
			if(tierId != null) {
				tierObj = loyaltyProgramTierDao.getTierById(tierId);
				if(!tierObj.getTierType().equalsIgnoreCase(tierType) || !tierObj.getTierName().equalsIgnoreCase(tierName) || 
					!tierObj.getEarnType().equalsIgnoreCase(perkIssuanceType) || tierObj.getEarnValue() != perkIssuanceValue || 
					tierObj.getPerkLimitValue() != perkUsageLimit || !tierObj.getEarnValueType().equalsIgnoreCase(earnValueType) ||
					tierObj.getEarnOnSpentAmount() != earnOnSpentAmount ||
					!tierObj.getPerkLimitExpType().equalsIgnoreCase(perkUsageLimitType) || tierObj.getOtpLimitAmt()!= otpLimit ||
					tierObj.getRedemptionOTPFlag() != (otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO) ||
					tierObj.getRedemptionPercentageLimit() != redemptionPercentageLimit || tierObj.getRedemptionValueLimit() != redemptionValueLimit ||
					tierObj.getMinBalanceValue()!= minBalanceValue || tierObj.getMinReceiptValue()!= minReceiptValue ||
					(tierObj.getConvertFromPoints() == null && convertFromPoints != null) ||
					(tierObj.getConvertFromPoints() != null && convertFromPoints == null) ||
					(tierObj.getConvertFromPoints() != null && convertFromPoints != null && tierObj.getConvertFromPoints().longValue() != convertFromPoints.longValue()) ||
					(tierObj.getConvertToAmount() == null && convertToAmount != null) ||
					(tierObj.getConvertToAmount() != null && convertToAmount == null) ||
					(tierObj.getConvertToAmount() != null && convertToAmount != null && tierObj.getConvertToAmount().longValue() != convertToAmount.longValue()) ||
					(tierObj.getOtpLimitAmt() != null && otpLimit == null) ||
					(tierObj.getOtpLimitAmt() == null && otpLimit != null) ||
					(tierObj.getOtpLimitAmt() != null && otpLimit != null && tierObj.getOtpLimitAmt().doubleValue() != otpLimit.doubleValue())){
					
					isChanged = true;
					
					if(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) && 
							(tierObj.getRedemptionOTPFlag() != (otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO)) ||
							(tierObj.getOtpLimitAmt() != null && otpLimit == null) ||
							(tierObj.getOtpLimitAmt() == null && otpLimit != null) ||
							(tierObj.getOtpLimitAmt() != null && otpLimit != null && tierObj.getOtpLimitAmt().doubleValue() != otpLimit.doubleValue())){
								sendEmailFlag = true;
							}
					
					logDetails = logTierChanges(programId, userId, null, false,sendEmailFlag);
					//logDetails = logTierChanges(programId, userId, null, false);
					
				}
			}
			else {
				isChanged = true;
				logDetails = logTierChanges(programId, userId, null, false,sendEmailFlag);
				tierObj = new LoyaltyProgramTier();
			}

			if(isChanged){
				tierObj.setProgramId(programId);
				tierObj.setTierType(tierType);
				tierObj.setTierName(tierName);
				tierObj.setEarnType(perkIssuanceType);
				tierObj.setEarnValue(perkIssuanceValue);
				tierObj.setEarnValueType(earnValueType);
				tierObj.setEarnOnSpentAmount(earnOnSpentAmount);
				//tierObj.setValueCode(valueCode);
				//tierObj.setActivationFlag(OCConstants.FLAG_NO);
				tierObj.setRewardExpiryDateType(perkExpType);
				tierObj.setRewardExpiryDateValue(perkExpDateValue);
				//tierObj.setPerkIssuanceExpiryCheck(expiryCheck);
				tierObj.setPerkLimitValue(perkUsageLimit);
				tierObj.setPerkLimitExpType(perkUsageLimitType);
				tierObj.setConvertFromPoints(convertFromPoints);
				tierObj.setConvertToAmount(convertToAmount);
				tierObj.setRedemptionPercentageLimit(redemptionPercentageLimit);
				tierObj.setRedemptionValueLimit(redemptionValueLimit);
				tierObj.setMinBalanceValue(minBalanceValue);
				tierObj.setMinReceiptValue(minReceiptValue);
				tierObj.setRedemptionOTPFlag(otpEnable == true ? OCConstants.FLAG_YES : OCConstants.FLAG_NO);
				tierObj.setOtpLimitAmt(otpLimit);
				tierObj.setCreatedBy(userId.toString());
				tierObj.setCreatedDate(Calendar.getInstance());
				logger.info(""+tierObj);
				loyaltyProgramTierDaoForDML.saveOrUpdate(tierObj);
				logTierChanges(programId, userId, logDetails, true,sendEmailFlag);
			}
			
			return tierObj;
		} catch (Exception e) {
			logger.error("Exception::",e);
			return null;
		}

	}

	public List<LoyaltyProgramTier> getTierList(Long prgmId) {
		List<LoyaltyProgramTier> tierList = null;
		try {
			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			tierList = loyaltyProgramTierDao.getTierListByPrgmId(prgmId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return tierList;
	}

	public String getEarn(LoyaltyProgramTier loyaltyProgramTier) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String earn="";
		String earnValue=loyaltyProgramTier.getEarnValue()==null?"":loyaltyProgramTier.getEarnValue().toString();
		String amountSpent=loyaltyProgramTier.getEarnOnSpentAmount()==null?"":loyaltyProgramTier.getEarnOnSpentAmount().toString();
		if(earnValue!=null && !earnValue.isEmpty()) {
			if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Points")) {

				if(loyaltyProgramTier.getEarnValueType()!=null && loyaltyProgramTier.getEarnValueType().equalsIgnoreCase("Value")) {
					if(amountSpent!=null && !amountSpent.isEmpty()) {
						earn= "Earn "+loyaltyProgramTier.getEarnValue().intValue()+" Points for every $ "+f.format(loyaltyProgramTier.getEarnOnSpentAmount())+" spent.";
					}
				}else {
					earn= "Earn "+loyaltyProgramTier.getEarnValue().intValue()+" % Points of purchase value."; 
				}

			}else {
				if(loyaltyProgramTier.getEarnValueType()!=null && loyaltyProgramTier.getEarnValueType().equalsIgnoreCase("Value")) {
					if(amountSpent!=null && !amountSpent.isEmpty()) {
						earn= "Get "+"$ "+f.format(loyaltyProgramTier.getEarnValue())+" cashback for every $ "+f.format(loyaltyProgramTier.getEarnOnSpentAmount())+" spent.";
					}
				}else {
					//earn= loyaltyProgramTier.getEarnValue().intValue()+" "+"% amount of purchase value"; 
					earn= "Get "+loyaltyProgramTier.getEarnValue().doubleValue()+" "+"% amount of purchase value."; 
				}  
			}
		}
		if(loyaltyProgramTier.getIssuanceChkEnable()!=null && loyaltyProgramTier.getIssuanceChkEnable()) {
			logger.info("inside maxcap enable check");
			if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Points")) {
				earn+="\n"+"Issuance Limit maximum "+loyaltyProgramTier.getMaxcap().intValue()+" points";
			} else {
				earn+="\n"+"Issuance Limit maximum $"+loyaltyProgramTier.getMaxcap().intValue();
			}
		}
		return earn;
	}
	
	/*public String getQuota(LoyaltyProgramTier loyaltyProgramTier) {
		
		String quota = "";
	    if(loyaltyProgramTier.getEarnType()!=null && loyaltyProgramTier.getEarnType()!=null) {
	    	
	    	/*if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Perks")) {
	    		quota = "Earn "+loyaltyProgramTier.getEarnValue().intValue()+" perks on purchase.";
	    	} else if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Amount")) {
	    		quota = "Earn $"+loyaltyProgramTier.getEarnValue()+" on purchase.";
	    	} else {
	    		quota = "Earn "+loyaltyProgramTier.getEarnValue()+" value codes on purchase.";
	    	}
	    	quota = "Earn "+loyaltyProgramTier.getEarnValue()+" "+loyaltyProgramTier.getEarnType()+" on purchase.";
	    }
	    return quota;
		
	}*/
	
	/*public String getPerkLimit(LoyaltyProgramTier loyaltyProgramTier) {
		
		String perkUsageLimit = "";
		if(loyaltyProgramTier.getPerkLimitExpType()!=null && loyaltyProgramTier.getPerkLimitValue()!=null && loyaltyProgramTier.getEarnType()!=null) {
			
			/*if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Perks")) {
				perkUsageLimit = "Max "+loyaltyProgramTier.getPerkLimitValue().intValue()+" perks per "+loyaltyProgramTier.getPerkLimitExpType();
	    	} else if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Amount")) {
	    		perkUsageLimit = "Max $"+loyaltyProgramTier.getPerkLimitValue()+" per "+loyaltyProgramTier.getPerkLimitExpType();
	    	} else {
	    		perkUsageLimit = "Max "+loyaltyProgramTier.getPerkLimitValue()+" value codes per "+loyaltyProgramTier.getPerkLimitExpType();
	    	}
			perkUsageLimit = "Max "+loyaltyProgramTier.getPerkLimitValue()+" "+loyaltyProgramTier.getEarnType() +" per "+loyaltyProgramTier.getPerkLimitExpType();
		}
		return perkUsageLimit;
	}*/
	
	/*public String quotaAndExpiry(LoyaltyProgramTier loyaltyProgramTier) {
		
		String quotaAndExp = "";
		if(loyaltyProgramTier.getEarnType()!=null && loyaltyProgramTier.getEarnValue()!=null && 
				loyaltyProgramTier.getRewardExpiryDateType()!=null && loyaltyProgramTier.getRewardExpiryDateValue()!=null) {
			
			/*if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Perks")) {
				
				quotaAndExp = loyaltyProgramTier.getEarnValue().intValue()+" perks Valid till "+
	                    +loyaltyProgramTier.getRewardExpiryDateValue()+" "+loyaltyProgramTier.getRewardExpiryDateType()+"(s).";
				
	    	} else if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Amount")) {
	    		
	    		quotaAndExp = "$"+loyaltyProgramTier.getEarnValue()+" Valid till "+
	                    +loyaltyProgramTier.getRewardExpiryDateValue()+" "+loyaltyProgramTier.getRewardExpiryDateType()+"(s).";
	    	} else {
	    		quotaAndExp = loyaltyProgramTier.getEarnValue()+" value codes Valid till "+
	                    +loyaltyProgramTier.getRewardExpiryDateValue()+" "+loyaltyProgramTier.getRewardExpiryDateType()+"(s).";
	    	}
			quotaAndExp = loyaltyProgramTier.getEarnValue().intValue()+" "+loyaltyProgramTier.getEarnType() +" Valid till "+
                    +loyaltyProgramTier.getRewardExpiryDateValue()+" "+loyaltyProgramTier.getRewardExpiryDateType()+"(s).";
			
		}
		return quotaAndExp;
	}*/
	
	/*public String getEarnInSplitCase(LoyaltyProgramTier loyaltyProgramTier) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String result="";
		LoyaltyProgramEarnRuleDao loyaltyProgramEarnRuleDao;
		loyaltyProgramEarnRuleDao = (LoyaltyProgramEarnRuleDao) ServiceLocator.getInstance().getBeanByName(OCConstants.LOYALTY_PROGRAM_EARN_RULE_DAO);
		List<LoyaltyProgramEarnRule> rulesList;
		try {
			rulesList = loyaltyProgramEarnRuleDao.fetchByTierId(loyaltyProgramTier.getProgramId(), loyaltyProgramTier.getTierId());
		
		//this is for the first rule of the split case
		
	//	StringBuffer earn=new StringBuffer();
		String earn="";
		String earnValue=rulesList.get(0).getEarnValue()==null?"":rulesList.get(0).getEarnValue().toString();
		String amountSpent=rulesList.get(0).getEarnOnSpentAmount()==null?"":rulesList.get(0).getEarnOnSpentAmount().toString();
		String additionalStringForRuleOn = "";
		if(rulesList.get(0).getRuleOn().toString().equalsIgnoreCase("D")){
			additionalStringForRuleOn=" for discounted items";
		}else if(rulesList.get(0).getRuleOn().toString().equalsIgnoreCase("ND")){
			additionalStringForRuleOn=" for non-discounted items";

		}
		if(earnValue!=null && !earnValue.isEmpty()) {
			if(rulesList.get(0).getEarnType().equalsIgnoreCase("Points")) {

				if(rulesList.get(0).getEarnValueType().equalsIgnoreCase("Value")) {
					if(amountSpent!=null && !amountSpent.isEmpty()) {
						earn= rulesList.get(0).getEarnValue().intValue()+" points for $"+f.format(rulesList.get(0).getEarnOnSpentAmount());
					}
				}else {
					earn= rulesList.get(0).getEarnValue().intValue()+" % points of purchase value"; 
				}

			}else {
				if(rulesList.get(0).getEarnValueType().equalsIgnoreCase("Value")) {
					if(amountSpent!=null && !amountSpent.isEmpty()) {
						earn= "$"+f.format(rulesList.get(0).getEarnValue())+" for $"+f.format(rulesList.get(0).getEarnOnSpentAmount());
					}
				}else {
					//earn= loyaltyProgramTier.getEarnValue().intValue()+" "+"% amount of purchase value"; 
					earn= rulesList.get(0).getEarnValue().doubleValue()+" "+"% amount of purchase value"; 
				}  
			}
			earn = earn + additionalStringForRuleOn;
		}
		//this is for the second rule of the split case
		
				String earnSecond="";
				String earnValueSecond=rulesList.get(1).getEarnValue()==null?"":rulesList.get(1).getEarnValue().toString();
				String amountSpentSecond=rulesList.get(1).getEarnOnSpentAmount()==null?"":rulesList.get(1).getEarnOnSpentAmount().toString();
				String additionalStringForRuleOnSecond = "";
				if(rulesList.get(1).getRuleOn().toString().equalsIgnoreCase("D")){
					additionalStringForRuleOnSecond=" for discounted items";
				}else if(rulesList.get(1).getRuleOn().toString().equalsIgnoreCase("ND")){
					additionalStringForRuleOnSecond=" for non-discounted items";

				}
				if(earnValueSecond!=null && !earnValueSecond.isEmpty()) {
					if(rulesList.get(1).getEarnType().equalsIgnoreCase("Points")) {

						if(rulesList.get(1).getEarnValueType().equalsIgnoreCase("Value")) {
							if(amountSpentSecond!=null && !amountSpentSecond.isEmpty()) {
								earnSecond= rulesList.get(1).getEarnValue().intValue()+" points for $"+f.format(rulesList.get(1).getEarnOnSpentAmount());
							}
						}else {
							earnSecond= rulesList.get(1).getEarnValue().intValue()+" % points of purchase value"; 
						}

					}else {
						if(rulesList.get(1).getEarnValueType().equalsIgnoreCase("Value")) {
							if(amountSpentSecond!=null && !amountSpentSecond.isEmpty()) {
								earnSecond= "$"+f.format(rulesList.get(1).getEarnValue())+" for $"+f.format(rulesList.get(1).getEarnOnSpentAmount());
							}
						}else {
							//earn= loyaltyProgramTier.getEarnValue().intValue()+" "+"% amount of purchase value"; 
							earnSecond= rulesList.get(1).getEarnValue().doubleValue()+" "+"% amount of purchase value"; 
						}  
					}
					earnSecond=earnSecond + additionalStringForRuleOnSecond;
				}
			result=earn +"\n"+earnSecond;	
			logger.info("result is ===="+result);
		return result;
		} catch (LoyaltyProgramException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("result is ===="+result);
			return result;
		}
	}*/

	public String getActivationTime(LoyaltyProgramTier loyaltyProgramTier) {
		String activationTime="";
		//if(loyaltyProgramTier.getPtsActiveDateType().equalsIgnoreCase("Day")) {
		if(loyaltyProgramTier.getPtsActiveDateValue()!=null && !loyaltyProgramTier.getPtsActiveDateValue().toString().isEmpty()) {
			activationTime="After"+" "+loyaltyProgramTier.getPtsActiveDateValue()+" "+"day(s)";
		}
		/*}/*else {
			activationTime="Immediate activation";
		}*/
		/*else if(loyaltyProgramTier.getPtsActiveDateType().equalsIgnoreCase("Hour")){
			if(loyaltyProgramTier.getPtsActiveDateValue()!=null && !loyaltyProgramTier.getPtsActiveDateValue().toString().isEmpty()) {
			activationTime="After"+" "+loyaltyProgramTier.getPtsActiveDateValue()+" "+"hour(s)";
			}
		}*/
		return activationTime;
	}

	public String getRule(LoyaltyProgramTier loyaltyProgramTier) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String rule="";
		String amount=loyaltyProgramTier.getConvertToAmount()==null?"":f.format(loyaltyProgramTier.getConvertToAmount());
		String points=loyaltyProgramTier.getConvertFromPoints()==null?"":loyaltyProgramTier.getConvertFromPoints().intValue()+"";
		if(amount!=null && !amount.isEmpty() && points!=null && !points.isEmpty() ) {
			rule= points+" Points gets you "+"$ "+amount;
		}
		if(loyaltyProgramTier.getMinReceiptValue()!=null) {
			rule+= " Minimum receipt amount required to redeem: $"+loyaltyProgramTier.getMinReceiptValue();
		}
		if(loyaltyProgramTier.getMinBalanceValue()!=null && loyaltyProgramTier.getEarnType()!=null) {
			if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Points")) {
				rule+= " Minimum balance required to redeem: "+loyaltyProgramTier.getMinBalanceValue().intValue()+" Points";
			} else if(loyaltyProgramTier.getEarnType().equalsIgnoreCase("Currency")) {
				rule+= " Minimum balance required to redeem: $"+loyaltyProgramTier.getMinBalanceValue();
			}
		}
		return rule;
	}
	
	public String getConversionRule(LoyaltyProgramTier loyaltyProgramTier) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String conversionRule="";
		String amount=loyaltyProgramTier.getConvertToAmount()==null?"":f.format(loyaltyProgramTier.getConvertToAmount());
		String points=loyaltyProgramTier.getConvertFromPoints()==null?"":loyaltyProgramTier.getConvertFromPoints().intValue()+"";
		if(amount!=null && !amount.isEmpty() && points!=null && !points.isEmpty() ) {
			conversionRule= points+" "+loyaltyProgramTier.getEarnType()+" = $"+amount;
		}
		if(loyaltyProgramTier.getMinReceiptValue()!=null && loyaltyProgramTier.getEarnType()!=null) {
			conversionRule+= " Minimum receipt amount required to redeem: $"+f.format(loyaltyProgramTier.getMinReceiptValue());
		}
		
		return conversionRule;
	}
	
	public String getRedeemRule(LoyaltyProgramTier loyaltyProgramTier) {
		
		String redeemRule = "";
		Double reedemPercnt = loyaltyProgramTier.getRedemptionPercentageLimit()!=0.0 && loyaltyProgramTier.getRedemptionPercentageLimit()!=null?loyaltyProgramTier.getRedemptionPercentageLimit():0.0;
		Double reedemValue = loyaltyProgramTier.getRedemptionValueLimit()!=0.0 && loyaltyProgramTier.getRedemptionValueLimit()!=null?loyaltyProgramTier.getRedemptionValueLimit():0.0;
		if(reedemPercnt!=null && reedemPercnt!=0.0 && reedemValue!=null && reedemValue!=0.0) {
			
			redeemRule = loyaltyProgramTier.getRedemptionPercentageLimit()+"% of the receipt value or $"+loyaltyProgramTier.getRedemptionValueLimit()+" whichever is lower.";
			
		} else if(reedemPercnt!=0.0 && reedemPercnt!=null && (reedemValue==0.0 || reedemValue==null)) {
			
			redeemRule = loyaltyProgramTier.getRedemptionPercentageLimit()+"% of the receipt value.";
			
		} else if((reedemPercnt==0.0 || reedemPercnt==null) && reedemValue!=null && reedemValue!=0.0) {
			
			redeemRule = "$"+loyaltyProgramTier.getRedemptionValueLimit()+" of the receipt value.";
			
		}
		
		return redeemRule;
	}

	public void saveBonus(Long programId, String extraBonusType,
			Double extraBonusValue, String earnedLevelType, Double earnedLevelValue, Long thresholdId, char registrationFlag, Long userId) {
		try {
			boolean isChanged = false;
			StringBuffer logDetails = null;
			LoyaltyThresholdBonus loyaltyThresholdBonus = null;
			loyaltyThresholdBonusDao=(LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			loyaltyThresholdBonusDaoForDML=(LoyaltyThresholdBonusDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO_FOR_DML);
			if(thresholdId != null) {
				loyaltyThresholdBonus = loyaltyThresholdBonusDao.getThresholdById(thresholdId);
				if(registrationFlag == OCConstants.FLAG_YES){
					if(!loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(extraBonusType) ||
						loyaltyThresholdBonus.getExtraBonusValue().doubleValue() != extraBonusValue.doubleValue()){
						isChanged = true;
//						logDetails = logRegBonusChanges(programId, userId, null, false);
					}
				}
				else if(registrationFlag == OCConstants.FLAG_NO){
					if(!loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(extraBonusType) ||
						loyaltyThresholdBonus.getExtraBonusValue().doubleValue() != extraBonusValue.doubleValue() ||
						loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase(earnedLevelType) ||
						loyaltyThresholdBonus.getEarnedLevelValue().doubleValue() != earnedLevelValue.doubleValue()){
						isChanged = true;
//						logDetails = logThresholdBonusChanges(programId, userId, null, false);
					}
				}
				if(isChanged) logDetails = logBonusChanges(programId, userId, null, false);
			}
			else {
				isChanged = true;
				loyaltyThresholdBonus = new LoyaltyThresholdBonus();
				logDetails = logBonusChanges(programId, userId, null, false);
				/*if(registrationFlag == OCConstants.FLAG_YES){
					logDetails = logRegBonusChanges(programId, userId, null, false);
				}
				else if(registrationFlag == OCConstants.FLAG_NO){
					logDetails = logThresholdBonusChanges(programId, userId, null, false);
				}*/
			}
			loyaltyThresholdBonus.setProgramId(programId);
			loyaltyThresholdBonus.setExtraBonusType(extraBonusType);
			loyaltyThresholdBonus.setExtraBonusValue(extraBonusValue);
			loyaltyThresholdBonus.setEarnedLevelType(earnedLevelType);
			loyaltyThresholdBonus.setEarnedLevelValue(earnedLevelValue);
			loyaltyThresholdBonus.setRegistrationFlag(registrationFlag);
			loyaltyThresholdBonus.setCreatedBy(userId.toString());
			loyaltyThresholdBonus.setCreatedDate(Calendar.getInstance());
			//loyaltyThresholdBonusDao.saveOrUpdate(loyaltyThresholdBonus);
			loyaltyThresholdBonusDaoForDML.saveOrUpdate(loyaltyThresholdBonus);
			/*if(registrationFlag == OCConstants.FLAG_YES && isChanged){
				logRegBonusChanges(programId, userId, logDetails, true);
			}
			else if(registrationFlag == OCConstants.FLAG_NO){
				logThresholdBonusChanges(programId, userId, logDetails, true);
			}*/
			
			if(isChanged) logBonusChanges(programId, userId, logDetails, true);
			
		}catch (Exception e) {
			logger.error("Exception",e);
		}
	}
	public void saveBonus(Long programId, String extraBonusType,
			Double extraBonusValue, String earnedLevelType, Double earnedLevelValue, Long thresholdId,
			char registrationFlag, Long userId , boolean recurring,Double thresholdLimitID,Long emailTempId,Long smsTempId,
			String expiryDateType, Long expiryDateValue,Long emailExpiryTempId,Long smsExpiryTempId) {
		try {
			boolean isChanged = false;
			StringBuffer logDetails = null;
			LoyaltyThresholdBonus loyaltyThresholdBonus = null;
			loyaltyThresholdBonusDao=(LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			loyaltyThresholdBonusDaoForDML=(LoyaltyThresholdBonusDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO_FOR_DML);
			if(thresholdId != null) {
				loyaltyThresholdBonus = loyaltyThresholdBonusDao.getThresholdById(thresholdId);
				if(registrationFlag == OCConstants.FLAG_YES){
					if(!loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(extraBonusType) ||
						loyaltyThresholdBonus.getExtraBonusValue().doubleValue() != extraBonusValue.doubleValue()){
						isChanged = true;
//						logDetails = logRegBonusChanges(programId, userId, null, false);
					}
				}
				else if(registrationFlag == OCConstants.FLAG_NO){
					if(!loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(extraBonusType) ||
						loyaltyThresholdBonus.getExtraBonusValue().doubleValue() != extraBonusValue.doubleValue() ||
						loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase(earnedLevelType) ||
						loyaltyThresholdBonus.getEarnedLevelValue().doubleValue() != earnedLevelValue.doubleValue()){
						isChanged = true;
//						logDetails = logThresholdBonusChanges(programId, userId, null, false);
					}
				}
				if(isChanged) logDetails = logBonusChanges(programId, userId, null, false);
			}
			else {
				isChanged = true;
				loyaltyThresholdBonus = new LoyaltyThresholdBonus();
				logDetails = logBonusChanges(programId, userId, null, false);
				/*if(registrationFlag == OCConstants.FLAG_YES){
					logDetails = logRegBonusChanges(programId, userId, null, false);
				}
				else if(registrationFlag == OCConstants.FLAG_NO){
					logDetails = logThresholdBonusChanges(programId, userId, null, false);
				}*/
			}
			loyaltyThresholdBonus.setProgramId(programId);
			loyaltyThresholdBonus.setExtraBonusType(extraBonusType);
			loyaltyThresholdBonus.setExtraBonusValue(extraBonusValue);
			loyaltyThresholdBonus.setEarnedLevelType(earnedLevelType);
			loyaltyThresholdBonus.setEarnedLevelValue(earnedLevelValue);
			loyaltyThresholdBonus.setRegistrationFlag(registrationFlag);
			loyaltyThresholdBonus.setCreatedBy(userId.toString());
			loyaltyThresholdBonus.setCreatedDate(Calendar.getInstance());
			loyaltyThresholdBonus.setRecurring(recurring);
			loyaltyThresholdBonus.setThresholdLimit(thresholdLimitID);
			loyaltyThresholdBonus.setEmailTempId(emailTempId);
			loyaltyThresholdBonus.setSmsTempId(smsTempId);
			loyaltyThresholdBonus.setBonusExpiryDateType(expiryDateType);
			loyaltyThresholdBonus.setBonusExpiryDateValue(expiryDateValue);
			loyaltyThresholdBonus.setEmailExpiryTempId(emailExpiryTempId);
			loyaltyThresholdBonus.setSmsExpiryTempId(smsExpiryTempId);
			//loyaltyThresholdBonusDao.saveOrUpdate(loyaltyThresholdBonus);
			loyaltyThresholdBonusDaoForDML.saveOrUpdate(loyaltyThresholdBonus);
			/*if(registrationFlag == OCConstants.FLAG_YES && isChanged){
				logRegBonusChanges(programId, userId, logDetails, true);
			}
			else if(registrationFlag == OCConstants.FLAG_NO){
				logThresholdBonusChanges(programId, userId, logDetails, true);
			}*/
			
			if(isChanged) logBonusChanges(programId, userId, logDetails, true);
			
		}catch (Exception e) {
			logger.error("Exception",e);
		}
	}
	/*public void saveBonusLimit(Long programId, String extraBonusType,
			Double extraBonusValue, String earnedLevelType, Double earnedLevelValue, Long thresholdId,
			char registrationFlag, Long userId , boolean recurring,Double thresholdLimitID) {

		try {
			boolean isChanged = false;
			StringBuffer logDetails = null;
			LoyaltyThresholdBonus loyaltyThresholdBonus = null;
			loyaltyThresholdBonusDao=(LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			loyaltyThresholdBonusDaoForDML=(LoyaltyThresholdBonusDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO_FOR_DML);
			if(thresholdId != null) {
				loyaltyThresholdBonus = loyaltyThresholdBonusDao.getThresholdById(thresholdId);
				if(registrationFlag == OCConstants.FLAG_YES){
					if(!loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(extraBonusType) ||
						loyaltyThresholdBonus.getExtraBonusValue().doubleValue() != extraBonusValue.doubleValue()){
						isChanged = true;
//						logDetails = logRegBonusChanges(programId, userId, null, false);
					}
				}
				else if(registrationFlag == OCConstants.FLAG_NO){
					if(!loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase(extraBonusType) ||
						loyaltyThresholdBonus.getExtraBonusValue().doubleValue() != extraBonusValue.doubleValue() ||
						loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase(earnedLevelType) ||
						loyaltyThresholdBonus.getEarnedLevelValue().doubleValue() != earnedLevelValue.doubleValue()){
						isChanged = true;
//						logDetails = logThresholdBonusChanges(programId, userId, null, false);
					}
				}
				if(isChanged) logDetails = logBonusChanges(programId, userId, null, false);
			}
			else {
				isChanged = true;
				loyaltyThresholdBonus = new LoyaltyThresholdBonus();
				logDetails = logBonusChanges(programId, userId, null, false);
				if(registrationFlag == OCConstants.FLAG_YES){
					logDetails = logRegBonusChanges(programId, userId, null, false);
				}
				else if(registrationFlag == OCConstants.FLAG_NO){
					logDetails = logThresholdBonusChanges(programId, userId, null, false);
				}
			}
			loyaltyThresholdBonus.setProgramId(programId);
			loyaltyThresholdBonus.setExtraBonusType(extraBonusType);
			loyaltyThresholdBonus.setExtraBonusValue(extraBonusValue);
			loyaltyThresholdBonus.setEarnedLevelType(earnedLevelType);
			loyaltyThresholdBonus.setEarnedLevelValue(earnedLevelValue);
			loyaltyThresholdBonus.setRegistrationFlag(registrationFlag);
			loyaltyThresholdBonus.setCreatedBy(userId.toString());
			loyaltyThresholdBonus.setCreatedDate(Calendar.getInstance());
			loyaltyThresholdBonus.setRecurring(recurring);
			loyaltyThresholdBonus.setThresholdLimit(thresholdLimitID);
			//loyaltyThresholdBonusDao.saveOrUpdate(loyaltyThresholdBonus);
			loyaltyThresholdBonusDaoForDML.saveOrUpdate(loyaltyThresholdBonus);
			if(registrationFlag == OCConstants.FLAG_YES && isChanged){
				logRegBonusChanges(programId, userId, logDetails, true);
			}
			else if(registrationFlag == OCConstants.FLAG_NO){
				logThresholdBonusChanges(programId, userId, logDetails, true);
			}
			
			if(isChanged) logBonusChanges(programId, userId, logDetails, true);
			
		}catch (Exception e) {
			logger.error("Exception",e);
		}
	
	}*/

	public List<LoyaltyThresholdBonus> getBonusList(Long programId) {
		List<LoyaltyThresholdBonus> bonusList = null;
		try {
			loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			bonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(programId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return bonusList;
	}
	public List<LoyaltyThresholdBonus> getBonusListByOrderAndEarnType(Long programId,String orderType,String bonusEarnType) {
		List<LoyaltyThresholdBonus> bonusList = null;
		try {
			loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			bonusList = loyaltyThresholdBonusDao.getBonusListByPrgmIdAndOrder(programId, orderType,bonusEarnType);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return bonusList;
	}
	public List<LoyaltyThresholdBonus> getBonusListByEarnType(Long programId,String bonusEarnType) {
		List<LoyaltyThresholdBonus> bonusList = null;
		try {
			loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			bonusList = loyaltyThresholdBonusDao.getBonusListByBonusEarnType(programId,bonusEarnType);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return bonusList;
	}
	
	public List<LoyaltyThresholdBonus> getThresholdBonusList(Long programId) {
		List<LoyaltyThresholdBonus> bonusList = null;
		try {
			loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			bonusList = loyaltyThresholdBonusDao.getBonusListByPrgmId(programId, OCConstants.FLAG_NO);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return bonusList;
	}

	/*public String getEarnedPoints(LoyaltyThresholdBonus loyaltyThresholdBonus) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String earnedPoints="";
		if(loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase("Points")) {
			if(loyaltyThresholdBonus.getEarnedLevelValue()!=null && !loyaltyThresholdBonus.getEarnedLevelValue().toString().equalsIgnoreCase("")){
				earnedPoints=loyaltyThresholdBonus.getEarnedLevelValue().intValue()+" "+"Total earned points";
			}
		}else {
			if(loyaltyThresholdBonus.getEarnedLevelValue()!=null && !loyaltyThresholdBonus.getEarnedLevelValue().toString().isEmpty()) {
				earnedPoints="$"+f.format(loyaltyThresholdBonus.getEarnedLevelValue())+" Total earned amount";
			}
		}
		return earnedPoints;
	}*/
	public String getEarnedPoints(LoyaltyThresholdBonus loyaltyThresholdBonus) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String earnedPoints="";
		if(loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase("Points")) {
			if(loyaltyThresholdBonus.getEarnedLevelValue()!=null && !loyaltyThresholdBonus.getEarnedLevelValue().toString().equalsIgnoreCase("")){
				//earnedPoints=loyaltyThresholdBonus.getEarnedLevelValue().intValue()+" "+"Total earned points";
				if(loyaltyThresholdBonus.isRecurring()){
					earnedPoints="For every increment of"+" "+loyaltyThresholdBonus.getEarnedLevelValue().intValue()+" "+"of Total earned points";
				}else earnedPoints= "On reaching level of"+" "+loyaltyThresholdBonus.getEarnedLevelValue().intValue()+" "+"of Total earned points";
				
			}
		}else if(loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase("Tier")) {
			if(loyaltyThresholdBonus.getEarnedLevelValue()!=null && !loyaltyThresholdBonus.getEarnedLevelValue().toString().equalsIgnoreCase("")){
				earnedPoints= "On reaching level of"+" "+loyaltyThresholdBonus.getEarnedLevelValue().intValue()+" Tier";
			}
		}else if(loyaltyThresholdBonus.getEarnedLevelType().equalsIgnoreCase("Amount")){
			if(loyaltyThresholdBonus.getEarnedLevelValue()!=null && !loyaltyThresholdBonus.getEarnedLevelValue().toString().isEmpty()) {
				if(loyaltyThresholdBonus.isRecurring()){
				earnedPoints="For every increment of"+" "+"$"+f.format(loyaltyThresholdBonus.getEarnedLevelValue())+" of Total earned amount";
				}else earnedPoints="On reaching level of"+" "+"$"+f.format(loyaltyThresholdBonus.getEarnedLevelValue())+" of Total earned amount";
			}
		}else {
			if(loyaltyThresholdBonus.getEarnedLevelValue()!=null && !loyaltyThresholdBonus.getEarnedLevelValue().toString().isEmpty()) {
				if(loyaltyThresholdBonus.isRecurring()){
				earnedPoints="For every increment of"+" "+"$"+f.format(loyaltyThresholdBonus.getEarnedLevelValue())+" of Lifetime purchase value"+
						(loyaltyThresholdBonus.getThresholdLimit()==null?"":" "+"upto"+" "+loyaltyThresholdBonus.getThresholdLimit());
				}else earnedPoints="On reaching level of"+" "+"$"+f.format(loyaltyThresholdBonus.getEarnedLevelValue())+" of Lifetime purchase value";
			}
		}
		return earnedPoints;
	}

	public String getBonus(LoyaltyThresholdBonus loyaltyThresholdBonus) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String bonus="";
		if(loyaltyThresholdBonus.getExtraBonusType().equalsIgnoreCase("Points")) {
			if(loyaltyThresholdBonus.getExtraBonusValue()!=null && !loyaltyThresholdBonus.getExtraBonusValue().toString().isEmpty()) {
				bonus=loyaltyThresholdBonus.getExtraBonusValue().intValue()+" "+"Points";
			}
		}else {
			if(loyaltyThresholdBonus.getExtraBonusValue()!=null && !loyaltyThresholdBonus.getExtraBonusValue().toString().isEmpty()) {
				//bonus="$"+f.format(loyaltyThresholdBonus.getExtraBonusValue());
				String res = Utility.truncateUptoTwoDecimal(loyaltyThresholdBonus.getExtraBonusValue());
				if(res != null)
					bonus="$"+ Double.parseDouble(res);
			}
		}
		return bonus;
	}

	public void savePrgmObj(LoyaltyProgram ltyPrgmObj) {
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			loyaltyProgramDaoForDML = (LoyaltyProgramDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_DAO_FOR_DML);
			//loyaltyProgramDao.saveOrUpdate(ltyPrgmObj);
			loyaltyProgramDaoForDML.saveOrUpdate(ltyPrgmObj);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

	}

	public void saveTierObj(LoyaltyProgramTier tierObj) {
		try {
			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			loyaltyProgramTierDaoForDML = (LoyaltyProgramTierDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO_FOR_DML);
			//loyaltyProgramTierDao.saveOrUpdate(tierObj);
			loyaltyProgramTierDaoForDML.saveOrUpdate(tierObj);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

	}

	public List<Coupons> getCouponList(Long orgId) {
		List<Coupons> couponList = null;
		try {
			couponsDao = (CouponsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			couponList = couponsDao.findCouponsByOrgId(orgId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return couponList;
	}

	public List<OrganizationStores> getAllStores(Long orgId) {
		List<OrganizationStores> storeList=null;
		try {
			organizationStoresDao=(OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			storeList=organizationStoresDao.findAllStores(orgId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return storeList;
	}
	
	public List<String> findAllLtySubsidiaries(Long userId,String serviceType ){
		List<String> ltySbsList=null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			ltySbsList = loyaltyTransactionChildDao.findAllSubsidiaries(userId, serviceType);
			
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return ltySbsList;
	}
	
	public List<Object[]> findAllStoresForSubsdiaries(Long userId, String subsidiaryNo ){
		List<Object[]> ltyStoreList=null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			ltyStoreList = loyaltyTransactionChildDao.findAllStoresForSubsdiaries(userId, subsidiaryNo);
			
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return ltyStoreList;
	}
	
	public List<String> findAllLtyStores(Long userId,String serviceType ){
		List<String> ltyStoreList=null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			ltyStoreList = loyaltyTransactionChildDao.findAllStores(userId, serviceType);
			
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return ltyStoreList;
	}
	public List<Object[]> findAllLtyStoresAndSbs(Long userId,String serviceType ){
		List<Object[]> ltyStoreList=null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			ltyStoreList = loyaltyTransactionChildDao.findAllStoresAndSbs(userId, serviceType);
			
		}catch(Exception e){
			logger.info("Exception while getting stores ",e);
		}
		return ltyStoreList;
	}

	public void saveExclusions(Long prgmId,
			char memExpiryFlag, char mbrshipExpiryOnLevelUpgd,
			char issuanceWithPromoFlag, char redemptionWithPromoFlag,
			Set<Listitem> issueCouponSet, Set<Listitem> redeemCouponSet,
			List<Listitem> storeItemLst, List<Listitem> productItemLst,List<Listitem> dateItemLst,List<Listitem> exclRedemdateItemLst,LoyaltyProgramExclusion loyaltyProgramExclusion,
			Long userId, char giftAmountExpiryFlag, String giftAmountExpiryDateType, Long giftAmountExpiryDateValue,
			char giftMembrshpExpiryFlag, String giftMembrshpExpiryDateType, Long giftMembrshpExpiryDateValue, boolean isStrRedmChk,boolean isAllStrChk,List<Listitem> selectedStoreLst) {
		try  {
			loyaltyProgramExclusionDao=(LoyaltyProgramExclusionDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
			loyaltyProgramExclusionDaoForDML=(LoyaltyProgramExclusionDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO_FOR_DML);
			if(loyaltyProgramExclusion==null) {
				loyaltyProgramExclusion=new LoyaltyProgramExclusion();
			}
			loyaltyProgramExclusion.setProgramId(prgmId);
			loyaltyProgramExclusion.setRedemptionWithPromoFlag(redemptionWithPromoFlag);
			loyaltyProgramExclusion.setIssuanceWithPromoFlag(issuanceWithPromoFlag);
			String issuancePromoIdStr = "";
			if(issueCouponSet != null) {
				for (Listitem listitem : issueCouponSet) {
					if(listitem.getValue().toString().equalsIgnoreCase("All Promos")) { 
						issuancePromoIdStr =OCConstants.LOYALTY_PROMO_EXCLUSION_ALL;
						break;
					}
					else {
						if(issuancePromoIdStr.length() == 0) {
							issuancePromoIdStr = issuancePromoIdStr + listitem.getValue();
						}
						else {
							issuancePromoIdStr = issuancePromoIdStr + Constants.ADDR_COL_DELIMETER + listitem.getValue() ;
						}
					}
				}
			}
			loyaltyProgramExclusion.setIssuancePromoIdStr(issuancePromoIdStr);
			String redemptionPromoIdStr="";
			if(redeemCouponSet != null) {
				for (Listitem listitem : redeemCouponSet) {
					if(listitem.getValue().toString().equalsIgnoreCase("All Promos")) {
						redemptionPromoIdStr = OCConstants.LOYALTY_PROMO_EXCLUSION_ALL;
						break;
					}
					else {
						if(redemptionPromoIdStr.length() == 0) {
							redemptionPromoIdStr = redemptionPromoIdStr + listitem.getValue();
						}
						else {
							redemptionPromoIdStr = redemptionPromoIdStr + Constants.ADDR_COL_DELIMETER + listitem.getValue() ;
						}
					}
				}
			}
			loyaltyProgramExclusion.setRedemptionPromoIdStr(redemptionPromoIdStr);
			String seledStoreStr = null;
			for (Listitem eachItem : storeItemLst) {
				String storeNumber = ((Listcell)eachItem.getFirstChild()).getLabel();
				if((seledStoreStr == null || seledStoreStr.trim().length() == 0) && storeNumber.trim().length() >0) {
					seledStoreStr = storeNumber;
				}else if(storeNumber.trim().length() >0){
					seledStoreStr +=Constants.ADDR_COL_DELIMETER+storeNumber.trim();
				}
			}

			loyaltyProgramExclusion.setStoreNumberStr(seledStoreStr);
			loyaltyProgramExclusion.setStrRedempChk(isStrRedmChk);
			loyaltyProgramExclusion.setAllStrChk(isAllStrChk);
			
			String seledRedemStoreStr = null;
			if(selectedStoreLst!=null) {
				for (Listitem eachItem : selectedStoreLst) {
					String storeNumber = ((Listcell)eachItem.getFirstChild()).getLabel();
					if((seledRedemStoreStr == null || seledRedemStoreStr.trim().length() == 0) && storeNumber.trim().length() >0) {
						seledRedemStoreStr = storeNumber;
					}else if(storeNumber.trim().length() >0){
						seledRedemStoreStr +=Constants.ADDR_COL_DELIMETER+storeNumber.trim();
					}
				}
			}
			
			loyaltyProgramExclusion.setSelectedStoreStr(seledRedemStoreStr);

			/*String productIdStr = null;
		for (Listitem eachItem : productItemLst) {
			String productCode = ((Listcell)eachItem.getFirstChild()).getLabel();
			if((productIdStr == null || productIdStr.trim().length() == 0) && productCode.trim().length() >0) {
				productIdStr = productCode;
			}else if(productCode.trim().length() >0){
				productIdStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
			}
		}
		loyaltyProgramExclusion.setProductIdStr(productIdStr);*/

			String itemCatStr="",vendorStr="",subClassStr= "",dcsStr="",skuNumStr="",deptCodeStr="",classStr="";
			for (Listitem eachItem : productItemLst) {
				logger.info("eachItem------------"+eachItem);
				String productCode= ((Listcell)eachItem.getChildren().get(1)).getLabel();
				logger.info("productCode---------"+productCode);
				logger.info("eachItem.getValue().toString()"+eachItem.getValue().toString());
				if(eachItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_PRODUCT_ITEMCATEGORY)) {
					if((itemCatStr == null || itemCatStr.trim().length() == 0) && productCode.trim().length() >0) {
						itemCatStr = productCode;
					}else if(productCode.trim().length() >0){
						itemCatStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
					}
					logger.info("itemCatStr---------"+itemCatStr);
				}

				else if(eachItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_PRODUCT_DEPARTMENTCODE)) {
					if((deptCodeStr == null || deptCodeStr.trim().length() == 0) && productCode.trim().length() >0) {
						deptCodeStr = productCode;
					}else if(productCode.trim().length() >0){
						deptCodeStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
					}
					logger.info("deptCodeStr---------"+deptCodeStr);
				}

				else if(eachItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_PRODUCT_CLASS)) {
					if((classStr == null || classStr.trim().length() == 0) && productCode.trim().length() >0) {
						classStr = productCode;
					}else if(productCode.trim().length() >0){
						classStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
					}
					logger.info("classStr---------"+classStr);
				}

				else if(eachItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_PRODUCT_SUBCLASS)) {
					if((subClassStr == null || subClassStr.trim().length() == 0) && productCode.trim().length() >0) {
						subClassStr = productCode;
					}else if(productCode.trim().length() >0){
						subClassStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
					}
					logger.info("subClassStr---------"+subClassStr);
				}

				else if(eachItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_PRODUCT_DCS)) {
					if((dcsStr == null || dcsStr.trim().length() == 0) && productCode.trim().length() >0) {
						dcsStr = productCode;
					}else if(productCode.trim().length() >0){
						dcsStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
					}
					logger.info("dcsStr---------"+dcsStr);
				}

				else if(eachItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_PRODUCT_VENDORCODE)) {
					if((vendorStr == null || vendorStr.trim().length() == 0) && productCode.trim().length() >0) {
						vendorStr = productCode;
					}else if(productCode.trim().length() >0){
						vendorStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
					}
					logger.info("vendorStr---------"+vendorStr);
				}

				else if(eachItem.getValue().toString().equalsIgnoreCase(OCConstants.LOYALTY_PRODUCT_SKUNUMBER)) {
					if((skuNumStr == null || skuNumStr.trim().length() == 0) && productCode.trim().length() >0) {
						skuNumStr = productCode;
					}else if(productCode.trim().length() >0){
						skuNumStr +=Constants.ADDR_COL_DELIMETER+productCode.trim();
					}
					logger.info("skuNumStr---------"+skuNumStr);
				}
			}

			loyaltyProgramExclusion.setItemCatStr(itemCatStr);
			loyaltyProgramExclusion.setDeptCodeStr(deptCodeStr);
			loyaltyProgramExclusion.setClassStr(classStr);
			loyaltyProgramExclusion.setDcsStr(dcsStr);
			loyaltyProgramExclusion.setVendorStr(vendorStr);
			loyaltyProgramExclusion.setSubClassStr(subClassStr);
			loyaltyProgramExclusion.setSkuNumStr(skuNumStr);


			String dateStr = null;
			for (Listitem eachItem : dateItemLst) {
				String date = ((Listcell)eachItem.getFirstChild()).getLabel();
				if((dateStr == null || dateStr.trim().length() == 0) && date.trim().length() >0) {
					dateStr = date;
				}else if(date.trim().length() >0){
					dateStr +=Constants.ADDR_COL_DELIMETER+date.trim();
				}
			}
			loyaltyProgramExclusion.setDateStr(dateStr);
			
			String excluRedemDate = null;
			for (Listitem eachItem : exclRedemdateItemLst) {
				String date = ((Listcell)eachItem.getFirstChild()).getLabel();
				if((excluRedemDate == null || excluRedemDate.trim().length() == 0) && date.trim().length() >0) {
					excluRedemDate = date;
				}else if(date.trim().length() >0){
					excluRedemDate +=Constants.ADDR_COL_DELIMETER+date.trim();
				}
			}
			loyaltyProgramExclusion.setExclRedemDateStr(excluRedemDate);
			loyaltyProgramExclusion.setCreatedBy(userId.toString());
			loyaltyProgramExclusion.setCreatedDate(Calendar.getInstance());
			//loyaltyProgramExclusionDao.saveOrUpdate(loyaltyProgramExclusion);
			loyaltyProgramExclusionDaoForDML.saveOrUpdate(loyaltyProgramExclusion);

			//save loyalty program object
			LoyaltyProgram loyaltyProgram=getProgmObj(prgmId);
			StringBuffer logGiftAmt = null;
			StringBuffer logGiftMbrshp = null;
			boolean isGiftAmtChanged = false;
			boolean isGiftMbrshpChanged = false;
			if(loyaltyProgram.getGiftAmountExpiryFlag() != giftAmountExpiryFlag || 
					(loyaltyProgram.getGiftAmountExpiryDateType() == null && giftAmountExpiryDateType != null) ||
					(loyaltyProgram.getGiftAmountExpiryDateType() != null && giftAmountExpiryDateType == null) || 
					(loyaltyProgram.getGiftAmountExpiryDateType() != null && giftAmountExpiryDateType != null &&
					!giftAmountExpiryDateType.equalsIgnoreCase(loyaltyProgram.getGiftAmountExpiryDateType())) ||
					(loyaltyProgram.getGiftAmountExpiryDateValue() == null && giftAmountExpiryDateValue != null) ||
					(loyaltyProgram.getGiftAmountExpiryDateValue() != null && giftAmountExpiryDateValue == null) || 
					(loyaltyProgram.getGiftAmountExpiryDateValue() != null && giftAmountExpiryDateValue != null &&
					giftAmountExpiryDateValue.longValue() != loyaltyProgram.getGiftAmountExpiryDateValue().longValue())){
				isGiftAmtChanged = true;
				logGiftAmt = logGiftAmtValidity(prgmId, userId, null, false, (loyaltyProgram.getGiftAmountExpiryFlag() == OCConstants.FLAG_YES ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
			}
			
			if(loyaltyProgram.getGiftMembrshpExpiryFlag() != giftMembrshpExpiryFlag || 
					(loyaltyProgram.getGiftMembrshpExpiryDateType() == null && giftMembrshpExpiryDateType != null) ||
					(loyaltyProgram.getGiftMembrshpExpiryDateType() != null && giftMembrshpExpiryDateType == null) || 
					(loyaltyProgram.getGiftMembrshpExpiryDateType() != null && giftMembrshpExpiryDateType != null &&
					!giftMembrshpExpiryDateType.equalsIgnoreCase(loyaltyProgram.getGiftMembrshpExpiryDateType())) ||
					(loyaltyProgram.getGiftMembrshpExpiryDateValue() == null && giftMembrshpExpiryDateValue != null) ||
					(loyaltyProgram.getGiftMembrshpExpiryDateValue() != null && giftMembrshpExpiryDateValue == null) || 
					(loyaltyProgram.getGiftMembrshpExpiryDateValue() != null && giftMembrshpExpiryDateValue != null &&
					giftMembrshpExpiryDateValue.longValue() != loyaltyProgram.getGiftMembrshpExpiryDateValue().longValue())){
				isGiftMbrshpChanged = true;
				logGiftMbrshp = logGiftCardValidity(prgmId, userId, null, false, (loyaltyProgram.getGiftMembrshpExpiryFlag() == OCConstants.FLAG_YES ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
			}
			//loyaltyProgram.setRewardExpiryFlag(rewardExpiryFlag);
			loyaltyProgram.setMembershipExpiryFlag(memExpiryFlag);
			loyaltyProgram.setMbrshipExpiryOnLevelUpgdFlag(mbrshipExpiryOnLevelUpgd);
			loyaltyProgram.setGiftAmountExpiryFlag(giftAmountExpiryFlag);
			loyaltyProgram.setGiftAmountExpiryDateType(giftAmountExpiryDateType);
			loyaltyProgram.setGiftAmountExpiryDateValue(giftAmountExpiryDateValue);
			loyaltyProgram.setGiftMembrshpExpiryFlag(giftMembrshpExpiryFlag);
			loyaltyProgram.setGiftMembrshpExpiryDateType(giftMembrshpExpiryDateType);
			loyaltyProgram.setGiftMembrshpExpiryDateValue(giftMembrshpExpiryDateValue);

			String draftStatus = loyaltyProgram.getDraftStatus();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);

			draftList[3] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
			draftStatus = "";
			for (String eachStr : draftList) {

				if (draftStatus.isEmpty()) {
					draftStatus = eachStr;
				} else {
					draftStatus += Constants.ADDR_COL_DELIMETER + eachStr;
				}
			}
			loyaltyProgram.setDraftStatus(draftStatus);
			savePrgmObj(loyaltyProgram);
			if(isGiftAmtChanged){
				logGiftAmtValidity(prgmId, userId, logGiftAmt, true, giftAmountExpiryFlag);
			}
			if(isGiftMbrshpChanged){
				logGiftCardValidity(prgmId, userId, logGiftMbrshp, true, giftMembrshpExpiryFlag);
			}
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
	}

	public int getCount(Long userId, String selectedStatus) {
		int totalSize=0;
		try {
			logger.info("selectedStatus--"+totalSize+selectedStatus);
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			totalSize = loyaltyProgramDao.getCount(userId,selectedStatus);
			logger.info("totalSize"+totalSize);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalSize;
	}

	public List<LoyaltyProgram> getprogramList(Long userId, String status,
			int startIndex, int size,String orderby_colName,String desc_Asc) {
		List<LoyaltyProgram> programList=null;
		logger.info("getprogramList"+userId+status+startIndex+size);
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			programList=loyaltyProgramDao.findProgramListByStatus(userId,status,startIndex,size,orderby_colName,desc_Asc);
			logger.info("progList4"+programList);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return programList;
	}

	public void deleteTier(LoyaltyProgramTier loyaltyProgramTier) {
		try {
/*			loyaltyProgramEarnRuleDao = (LoyaltyProgramEarnRuleDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EARN_RULE_DAO);
			loyaltyProgramEarnRuleDaoForDML = (LoyaltyProgramEarnRuleDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_EARN_RULE_DAO_FOR_DML);
			List<LoyaltyProgramEarnRule> rules=loyaltyProgramEarnRuleDao.fetchByTierId(loyaltyProgramTier.getProgramId(), loyaltyProgramTier.getTierId());
			if(rules!=null && rules.size()>0){
				
				loyaltyProgramEarnRuleDaoForDML.delete(rules.get(0));
				loyaltyProgramEarnRuleDaoForDML.delete(rules.get(1));
			}
*/			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			loyaltyProgramTierDaoForDML = (LoyaltyProgramTierDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO_FOR_DML);
			//loyaltyProgramTierDao.delete(loyaltyProgramTier);
			loyaltyProgramTierDaoForDML.delete(loyaltyProgramTier);


		}catch (Exception e) {
			logger.error("Exception ::",e);
		}

	}

	public String getUpgradeRule(LoyaltyProgramTier loyaltyProgramTier) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String upgradeRule="";
		if (loyaltyProgramTier.getMultipleTierUpgrdRules() != null
				&& !loyaltyProgramTier.getMultipleTierUpgrdRules().isEmpty()) {
			String multipleTierRules = loyaltyProgramTier.getMultipleTierUpgrdRules();
			String[] arrMulTierRules = multipleTierRules.split("\\|\\|");
			for (String tierRule : arrMulTierRules) {
				int firstColon = tierRule.indexOf(':');
				String tierName = tierRule.substring(0, firstColon);
				if (tierName.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
					int lastColon = tierRule.lastIndexOf(':');
					String value = tierRule.substring(firstColon + 1, lastColon);
					String month = tierRule.substring(lastColon + 1);
					upgradeRule += tierName + " " + value + " spent within " + month + "-month(s)" + " OR ";
				} else if (tierName.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_POINTS)) {
					if (tierRule.contains(";=;CumulativeVisits")) {
						String[] tierRulesArray = tierRule.split(";=;");
						int lastColon1 = tierRulesArray[0].lastIndexOf(':');
						String value1 = tierRulesArray[0].substring(firstColon + 1, lastColon1);
						String month1 = tierRulesArray[0].substring(lastColon1 + 1);
						
						int lastColon2 = tierRulesArray[1].lastIndexOf(':');
						String value2 = tierRulesArray[1].substring(firstColon + 1, lastColon2);
						String month2 = tierRulesArray[1].substring(lastColon2 + 1);
						
						upgradeRule += tierName + " " + value1 + " within " + month1 + "-month(s)" + " & CumulativeVisits " + value2 + " within " + month2 + "-month(s)" + " OR ";
					} else {
						int lastColon = tierRule.lastIndexOf(':');
						String value = tierRule.substring(firstColon + 1, lastColon);
						String month = tierRule.substring(lastColon + 1);
						upgradeRule += tierName + " " + value + " within " + month + "-month(s)" + " OR ";
					}
				} else if (tierName.equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_VISITS)) {
					int lastColon = tierRule.lastIndexOf(':');
					String value = tierRule.substring(firstColon + 1, lastColon);
					String month = tierRule.substring(lastColon + 1);
					upgradeRule += tierName + " " + value + " within " + month + "-month(s)" + " OR ";
				} else if (tierName.equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)) {
					String value = tierRule.substring(firstColon + 1);
					upgradeRule += tierName + " " + value + " points" + " OR ";
				} else {
					String value = tierRule.substring(firstColon + 1);
					upgradeRule += tierName + " " + value + " OR ";
				}
			}
			return upgradeRule.substring(0, upgradeRule.length() - 3);

		} else {
		
		if(loyaltyProgramTier.getTierUpgdConstraint() != null && loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase(OCConstants.LOYALTY_CUMULATIVE_PURCHASE_VALUE)) {
			if((loyaltyProgramTier.getTierUpgdConstraintValue()!=null && !loyaltyProgramTier.getTierUpgdConstraintValue().toString().isEmpty()) && 
					(loyaltyProgramTier.getTierUpgradeCumulativeValue()!=null && !loyaltyProgramTier.getTierUpgradeCumulativeValue().toString().isEmpty())) {
				upgradeRule="Cross-over at $"+f.format(loyaltyProgramTier.getTierUpgdConstraintValue())+" spent within "+loyaltyProgramTier.getTierUpgradeCumulativeValue()+" "+"month(s)";
			}	
		}
		else if(loyaltyProgramTier.getTierUpgdConstraint() != null && loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase(OCConstants.LOYALTY_LIFETIME_POINTS)){
			if(loyaltyProgramTier.getTierUpgdConstraintValue()!=null && !loyaltyProgramTier.getTierUpgdConstraintValue().toString().isEmpty()) {
				upgradeRule="Cross-over at"+" "+loyaltyProgramTier.getTierUpgdConstraintValue().intValue()+" "+"points";
			}
		}
		else {
			if(loyaltyProgramTier.getTierUpgdConstraintValue()!=null && !loyaltyProgramTier.getTierUpgdConstraintValue().toString().isEmpty()) {
				upgradeRule="Cross-over at $"+f.format(loyaltyProgramTier.getTierUpgdConstraintValue());
			}	
		}
		return upgradeRule;
		}
	}

	public void deleteThreshold(LoyaltyThresholdBonus loyaltyThresholdBonus) {
		try {
			loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			loyaltyThresholdBonusDaoForDML = (LoyaltyThresholdBonusDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO_FOR_DML);
			//loyaltyThresholdBonusDaoForDML.delete(loyaltyThresholdBonus);
			loyaltyThresholdBonusDaoForDML.delete(loyaltyThresholdBonus);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}

	}

	public void saveCardSetObj(LoyaltyCardSet loyaltyCardSet) {
		try {
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardSetDaoForDML = (LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
			//loyaltyCardSetDao.saveOrUpdate(loyaltyCardSet);
			loyaltyCardSetDaoForDML.saveOrUpdate(loyaltyCardSet);
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
	}

	public LoyaltyThresholdBonus getThresholdObj(Long programId) {
		LoyaltyThresholdBonus loyaltyThresholdBonus=null;
		try {
			loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			loyaltyThresholdBonus=loyaltyThresholdBonusDao.getRegistrationBonusByPrgmId(programId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyThresholdBonus;
	}

	public LoyaltyProgramExclusion getExclusionObj(Long prgmId) {
		LoyaltyProgramExclusion loyaltyProgramExclusion=null;
		try {
			loyaltyProgramExclusionDao=(LoyaltyProgramExclusionDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
			loyaltyProgramExclusion=loyaltyProgramExclusionDao.getExlusionByProgId(prgmId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyProgramExclusion;
	}

	public void deletePrgmSettings(Long prgmId) {
		try {
			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			loyaltyProgramTierDaoForDML = (LoyaltyProgramTierDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO_FOR_DML);
			//loyaltyProgramTierDao.deleteByPrgmId(prgmId);
			loyaltyProgramTierDaoForDML.deleteByPrgmId(prgmId);

			loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
			loyaltyThresholdBonusDaoForDML = (LoyaltyThresholdBonusDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO_FOR_DML);
			//loyaltyThresholdBonusDao.deleteByPrgmId(prgmId);
			loyaltyThresholdBonusDaoForDML.deleteByPrgmId(prgmId);

			loyaltyProgramExclusionDao=(LoyaltyProgramExclusionDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
			loyaltyProgramExclusionDaoForDML=(LoyaltyProgramExclusionDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO_FOR_DML);
			//loyaltyProgramExclusionDao.deleteByPrgmId(prgmId);
			loyaltyProgramExclusionDaoForDML.deleteByPrgmId(prgmId);
		} catch (Exception e) {
			logger.error("Exception ::",e);
		} 

	}

	public void deleteProgram(Long prgmId) {
		try {
			deletePrgmSettings(prgmId);

			loyaltyAutoCommDao = (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			loyaltyAutoCommDaoForDML = (LoyaltyAutoCommDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_AUTO_COMM_DAO_FOR_DML);
			//loyaltyAutoCommDao.deleteByPrgmId(prgmId);
			loyaltyAutoCommDaoForDML.deleteByPrgmId(prgmId);

			loyaltyCardsDao = (LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			loyaltyCardsDaoForDML = (LoyaltyCardsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARDS_DAO_FOR_DML);
			//loyaltyCardsDao.deleteByPrgmId(prgmId);
			loyaltyCardsDaoForDML.deleteByPrgmId(prgmId);

			loyaltyCardSetDao=(LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardSetDaoForDML=(LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);
			//loyaltyCardSetDao.deleteByPrgmId(prgmId);
			loyaltyCardSetDaoForDML.deleteByPrgmId(prgmId);

			LoyaltyProgram  loyaltyProgram = getProgmObj(prgmId);
			loyaltyProgramDao=(LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			loyaltyProgramDaoForDML=(LoyaltyProgramDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_DAO_FOR_DML);
			//loyaltyProgramDao.delete(loyaltyProgram);
			loyaltyProgramDaoForDML.delete(loyaltyProgram);

		} catch (Exception e) {
			logger.error("Exception ::",e);
		} 

	}

	public void copyPrgmSettings(Long prgmId, LoyaltyProgram addFromPrgmObj, Long userId, String draftStatus, String status, char uniqueMblFlag) {

		try {
			LoyaltyProgram prgmObj = getProgmObj(prgmId);

			//set Program Object
			prgmObj.setTierEnableFlag(addFromPrgmObj.getTierEnableFlag());
			prgmObj.setNoOfTiers(addFromPrgmObj.getNoOfTiers());
			prgmObj.setStatus(status);
			//			prgmObj.setDefaultFlag(addFromPrgmObj.getDefaultFlag());
			prgmObj.setUniqueMobileFlag(uniqueMblFlag);
			prgmObj.setRegRequisites(addFromPrgmObj.getRegRequisites());
			//prgmObj.setRedemptionOTPFlag(addFromPrgmObj.getRedemptionOTPFlag());
			//prgmObj.setPartialReversalFlag(addFromPrgmObj.getPartialReversalFlag());	
			//prgmObj.setOtpLimitAmt(addFromPrgmObj.getOtpLimitAmt());
			//prgmObj.setRewardExpiryFlag(addFromPrgmObj.getRewardExpiryFlag());
			prgmObj.setMbrshipExpiryOnLevelUpgdFlag(addFromPrgmObj.getMbrshipExpiryOnLevelUpgdFlag());
			prgmObj.setModifiedDate(Calendar.getInstance());
			prgmObj.setModifiedBy(userId.toString());
			prgmObj.setDraftStatus(draftStatus); 
			prgmObj.setMembershipExpiryFlag(addFromPrgmObj.getMembershipExpiryFlag());
			prgmObj.setGiftAmountExpiryFlag(addFromPrgmObj.getGiftAmountExpiryFlag());
			prgmObj.setGiftAmountExpiryDateType(addFromPrgmObj.getGiftAmountExpiryDateType());
			prgmObj.setGiftAmountExpiryDateValue(addFromPrgmObj.getGiftAmountExpiryDateValue());
			prgmObj.setGiftMembrshpExpiryFlag(addFromPrgmObj.getGiftMembrshpExpiryFlag());
			prgmObj.setGiftMembrshpExpiryDateType(addFromPrgmObj.getGiftMembrshpExpiryDateType());
			prgmObj.setGiftMembrshpExpiryDateValue(addFromPrgmObj.getGiftMembrshpExpiryDateValue());
			//prgmObj.setMinReceiptAmtValue(addFromPrgmObj.getMinReceiptAmtValue());
			//prgmObj.setMinBalanceRedeemValue(addFromPrgmObj.getMinBalanceRedeemValue());
			//prgmObj.setMinBalanceType(addFromPrgmObj.getMinBalanceType());
			savePrgmObj(prgmObj);

			//set Tier Object
			List<LoyaltyProgramTier> tierList = getTierList(addFromPrgmObj.getProgramId());
			if(tierList != null) {
				List<LoyaltyProgramTier> newTierList = new ArrayList<LoyaltyProgramTier>();
				for (LoyaltyProgramTier loyaltyProgramTier : tierList) {
					LoyaltyProgramTier newTierObj = new LoyaltyProgramTier();

					newTierObj.setProgramId(prgmId);
					newTierObj.setTierType(loyaltyProgramTier.getTierType());
					newTierObj.setTierName(loyaltyProgramTier.getTierName());
					newTierObj.setEarnType(loyaltyProgramTier.getEarnType());
					newTierObj.setEarnValueType(loyaltyProgramTier.getEarnValueType());
					newTierObj.setEarnValue(loyaltyProgramTier.getEarnValue());
					if(loyaltyProgramTier.getIssuanceChkEnable()) {
						newTierObj.setMaxcap(loyaltyProgramTier.getMaxcap());
					}
					newTierObj.setIssuanceChkEnable(loyaltyProgramTier.getIssuanceChkEnable());
					newTierObj.setEarnOnSpentAmount(loyaltyProgramTier.getEarnOnSpentAmount());
					newTierObj.setActivationFlag(loyaltyProgramTier.getActivationFlag());
					newTierObj.setPtsActiveDateType(loyaltyProgramTier.getPtsActiveDateType());
					newTierObj.setPtsActiveDateValue(loyaltyProgramTier.getPtsActiveDateValue());
					newTierObj.setConvertFromPoints(loyaltyProgramTier.getConvertFromPoints());
					newTierObj.setConvertToAmount(loyaltyProgramTier.getConvertFromPoints());
					newTierObj.setConversionType(loyaltyProgramTier.getConversionType());
					newTierObj.setTierUpgdConstraint(loyaltyProgramTier.getTierUpgdConstraint());
					newTierObj.setTierUpgdConstraintValue(loyaltyProgramTier.getTierUpgdConstraintValue());
				//	newTierObj.setTierUpgradeCumulativeType(loyaltyProgramTier.getTierUpgradeCumulativeType());
					newTierObj.setTierUpgradeCumulativeValue(loyaltyProgramTier.getTierUpgradeCumulativeValue());
					newTierObj.setMinBalanceValue(loyaltyProgramTier.getMinBalanceValue());
					newTierObj.setMinReceiptValue(loyaltyProgramTier.getMinReceiptValue());
					newTierObj.setRedemptionPercentageLimit(loyaltyProgramTier.getRedemptionPercentageLimit());
					newTierObj.setRedemptionValueLimit(loyaltyProgramTier.getRedemptionValueLimit());
					newTierObj.setCrossOverBonus(loyaltyProgramTier.getCrossOverBonus());
					newTierObj.setConsiderRedeemedAmountFlag(loyaltyProgramTier.getConsiderRedeemedAmountFlag());
					newTierObj.setRedemptionOTPFlag(loyaltyProgramTier.getRedemptionOTPFlag());
					newTierObj.setOtpLimitAmt(loyaltyProgramTier.getOtpLimitAmt());
					newTierObj.setPartialReversalFlag(loyaltyProgramTier.getPartialReversalFlag());
					newTierObj.setMultipleTierUpgrdRules(loyaltyProgramTier.getMultipleTierUpgrdRules());
					newTierObj.setRewardExpiryDateType(loyaltyProgramTier.getRewardExpiryDateType());
					newTierObj.setRewardExpiryDateValue(loyaltyProgramTier.getRewardExpiryDateValue());
					newTierObj.setCreatedDate(Calendar.getInstance());
					newTierObj.setCreatedBy(userId.toString());
					newTierObj.setPerkLimitExpType(loyaltyProgramTier.getPerkLimitExpType());
					newTierObj.setPerkLimitValue(loyaltyProgramTier.getPerkLimitValue());
					newTierObj.setMembershipExpiryDateType(loyaltyProgramTier.getMembershipExpiryDateType());
					newTierObj.setMembershipExpiryDateValue(loyaltyProgramTier.getMembershipExpiryDateValue());
					//APP-3284
					newTierObj.setActivateAfterDisableAllStore(loyaltyProgramTier.getActivateAfterDisableAllStore());
					newTierObj.setDisallowActivateAfterStores(loyaltyProgramTier.getDisallowActivateAfterStores());
					
					newTierList.add(newTierObj);
				}

				loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
				loyaltyProgramTierDaoForDML = (LoyaltyProgramTierDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO_FOR_DML);

				//loyaltyProgramTierDao.saveByCollection(newTierList);
				loyaltyProgramTierDaoForDML.saveByCollection(newTierList);

			}

			//set Threshold Object
			List<LoyaltyThresholdBonus> bonusList = getBonusList(addFromPrgmObj.getProgramId());
			if(bonusList != null) {
				List<LoyaltyThresholdBonus> newBonusList = new ArrayList<LoyaltyThresholdBonus>();
				for (LoyaltyThresholdBonus loyaltyThresholdBonus : bonusList) {
					LoyaltyThresholdBonus newBonusObj = new LoyaltyThresholdBonus();

					newBonusObj.setProgramId(prgmId);
					newBonusObj.setExtraBonusType(loyaltyThresholdBonus.getExtraBonusType());
					newBonusObj.setExtraBonusValue(loyaltyThresholdBonus.getExtraBonusValue());
					newBonusObj.setEarnedLevelType(loyaltyThresholdBonus.getEarnedLevelType());
					newBonusObj.setEarnedLevelValue(loyaltyThresholdBonus.getEarnedLevelValue());
					newBonusObj.setCreatedDate(Calendar.getInstance());
					newBonusObj.setCreatedBy(userId.toString());
					newBonusObj.setRegistrationFlag(loyaltyThresholdBonus.getRegistrationFlag());

					newBonusList.add(newBonusObj);
				}

				loyaltyThresholdBonusDao = (LoyaltyThresholdBonusDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO);
				loyaltyThresholdBonusDaoForDML = (LoyaltyThresholdBonusDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_BONUS_DAO_FOR_DML);

				//loyaltyThresholdBonusDao.saveByCollection(newBonusList);
				loyaltyThresholdBonusDaoForDML.saveByCollection(newBonusList);

			}


			//set Exclusion Object
			LoyaltyProgramExclusion exclusionObj = getExclusionObj(addFromPrgmObj.getProgramId());
			if(exclusionObj != null) {
				LoyaltyProgramExclusion newExclusnObj = new LoyaltyProgramExclusion();
				newExclusnObj.setProgramId(prgmId);
				newExclusnObj.setIssuanceWithPromoFlag(exclusionObj.getIssuanceWithPromoFlag());
				newExclusnObj.setIssuancePromoIdStr(exclusionObj.getIssuancePromoIdStr());
				newExclusnObj.setRedemptionWithPromoFlag(exclusionObj.getRedemptionWithPromoFlag());
				newExclusnObj.setRedemptionPromoIdStr(exclusionObj.getRedemptionPromoIdStr());
				newExclusnObj.setStoreNumberStr(exclusionObj.getStoreNumberStr());
				newExclusnObj.setItemCatStr(exclusionObj.getItemCatStr());
				newExclusnObj.setSubClassStr(exclusionObj.getSubClassStr());
				newExclusnObj.setClassStr(exclusionObj.getClassStr());
				newExclusnObj.setVendorStr(exclusionObj.getVendorStr());
				newExclusnObj.setDeptCodeStr(exclusionObj.getDeptCodeStr());
				newExclusnObj.setSkuNumStr(exclusionObj.getSkuNumStr());
				newExclusnObj.setDcsStr(exclusionObj.getDcsStr());
				newExclusnObj.setDateStr(exclusionObj.getDateStr());
				newExclusnObj.setExclRedemDateStr(exclusionObj.getExclRedemDateStr());
				newExclusnObj.setCreatedDate(Calendar.getInstance());
				newExclusnObj.setCreatedBy(userId.toString());
				newExclusnObj.setStrRedempChk(exclusionObj.getStrRedempChk());
				newExclusnObj.setAllStrChk(exclusionObj.getAllStrChk());
				newExclusnObj.setSelectedStoreStr(exclusionObj.getSelectedStoreStr());

				loyaltyProgramExclusionDao=(LoyaltyProgramExclusionDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO);
				loyaltyProgramExclusionDaoForDML=(LoyaltyProgramExclusionDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_EXCLUSION_DAO_FOR_DML);
				//loyaltyProgramExclusionDao.saveOrUpdate(newExclusnObj);
				loyaltyProgramExclusionDaoForDML.saveOrUpdate(newExclusnObj);

			}
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}

	}

	public long getCardsCount(String cardIdStr, String status,Long prgmId ) { 
		long count=0;
		try {
			loyaltyCardsDao=(LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			count=loyaltyCardsDao.getLoyaltyCardsCountByStatus(cardIdStr,status,prgmId);

		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return count;
	}

	public void updateTier(LoyaltyProgramTier loyaltyProgramTier) {
		try {
			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			loyaltyProgramTierDaoForDML = (LoyaltyProgramTierDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO_FOR_DML);
			//loyaltyProgramTierDao.saveOrUpdate(loyaltyProgramTier);
			loyaltyProgramTierDaoForDML.saveOrUpdate(loyaltyProgramTier);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}

	}

	public LoyaltyProgramTier getTierObj(Long tierId) {
		LoyaltyProgramTier tierObj=null;
		try {
			loyaltyProgramTierDao = (LoyaltyProgramTierDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_TIER_DAO);
			logger.info("loyaltyProgramTierDao::"+loyaltyProgramTierDao);
			tierObj = loyaltyProgramTierDao.getTierById(tierId);
			logger.info("tierObj::"+tierObj);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return tierObj;
	}

	public long getInventoryCardsCount(Long programId, Long cardsetId) {
		long count=0;
		try {
			loyaltyCardsDao=(LoyaltyCardsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			count = loyaltyCardsDao.getInventoryCardsCountByPrgmId(programId,cardsetId);

		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return count;

	}

	public List<CustomTemplates> getTemplateList(Long userId, String type) {

		List<CustomTemplates> templateList=null;
		try {
			customTemplatesDao=(CustomTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			templateList = customTemplatesDao.findAllByUser(userId, type);
		}catch (Exception e) {
			logger.error("Exception e ::",e);
		}
		return templateList;
	}

		


	public void saveAutoCommunication(Long prgmId, Long regEmailTmpltId, Long tierUpgdEmailTmpltId, Long threshBonusEmailTmpltId, Long rewardExpiryEmailTmpltId,
			Long mbrshipExpiryEmailTmpltId, Long userId, LoyaltyAutoComm loyaltyAutoComm, 
			Long giftAmtExpiryEmailTmpltId, Long giftMembrshpExpiryEmailTmpltId,
			Long giftCardIssuanceEmailTmpltId, Long regSmsTmpltId, Long tierUpgdSmsTmpltId, 
			Long threshBonusSmsTmpltId, Long rewardExpirySmsTmpltId, Long mbrshipExpirySmsTmpltId, 
			Long giftAmtExpirySmsTmpltId, Long giftMembrshpExpirySmsTmpltId, Long giftCardIssuanceSmsTmpltId,
			Long adjustmentAutoEmailTmplId,Long adjustmentAutoSmsTmplId,
			Long issuanceAutoEmailTmplId,Long issuanceAutoSmsTmplId,
			Long redemptionAutoEmailTmplId,Long redemptionAutoSmsTmplId,
			Long otpMessageAutoEmailTmplId, Long otpMessageAutoSmsTmpltId,Long redemptionOtpAutoEmailTmplId,Long redemptionOtpAutoSmsTmpltId
	
			) {
		try {
			loyaltyAutoCommDao= (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			loyaltyAutoCommDaoForDML= (LoyaltyAutoCommDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_AUTO_COMM_DAO_FOR_DML);
			if(loyaltyAutoComm == null ) {
				loyaltyAutoComm = new LoyaltyAutoComm();
			}
			loyaltyAutoComm.setProgramId(prgmId);
			loyaltyAutoComm.setRegEmailTmpltId(regEmailTmpltId);
			loyaltyAutoComm.setTierUpgdEmailTmpltId(tierUpgdEmailTmpltId);
			loyaltyAutoComm.setThreshBonusEmailTmpltId(threshBonusEmailTmpltId);
			loyaltyAutoComm.setRewardExpiryEmailTmpltId(rewardExpiryEmailTmpltId);
			loyaltyAutoComm.setMbrshipExpiryEmailTmpltId(mbrshipExpiryEmailTmpltId);
			loyaltyAutoComm.setGiftAmtExpiryEmailTmpltId(giftAmtExpiryEmailTmpltId);
			loyaltyAutoComm.setGiftMembrshpExpiryEmailTmpltId(giftMembrshpExpiryEmailTmpltId);
			loyaltyAutoComm.setGiftCardIssuanceEmailTmpltId(giftCardIssuanceEmailTmpltId);
			loyaltyAutoComm.setAdjustmentAutoEmailTmplId(adjustmentAutoEmailTmplId);
			loyaltyAutoComm.setIssuanceAutoEmailTmplId(issuanceAutoEmailTmplId);
			loyaltyAutoComm.setRedemptionAutoEmailTmplId(redemptionAutoEmailTmplId);

			loyaltyAutoComm.setOtpMessageAutoEmailTmplId(otpMessageAutoEmailTmplId);
			loyaltyAutoComm.setRedemptionOtpAutoEmailTmplId(redemptionOtpAutoEmailTmplId);
			
			loyaltyAutoComm.setRegSmsTmpltId(regSmsTmpltId);
			loyaltyAutoComm.setGiftCardIssuanceSmsTmpltId(giftCardIssuanceSmsTmpltId);
			loyaltyAutoComm.setGiftAmtExpirySmsTmpltId(giftAmtExpirySmsTmpltId);
			loyaltyAutoComm.setGiftMembrshpExpirySmsTmpltId(giftMembrshpExpirySmsTmpltId);
			loyaltyAutoComm.setGiftCardIssuanceSmsTmpltId(giftCardIssuanceSmsTmpltId);
			loyaltyAutoComm.setTierUpgdSmsTmpltId(tierUpgdSmsTmpltId);
			loyaltyAutoComm.setThreshBonusSmsTmpltId(threshBonusSmsTmpltId);
			loyaltyAutoComm.setRewardExpirySmsTmpltId(rewardExpirySmsTmpltId);
			loyaltyAutoComm.setMbrshipExpirySmsTmpltId(mbrshipExpirySmsTmpltId);
			loyaltyAutoComm.setAdjustmentAutoSmsTmplId(adjustmentAutoSmsTmplId);
			loyaltyAutoComm.setIssuanceAutoSmsTmplId(issuanceAutoSmsTmplId);
			loyaltyAutoComm.setRedemptionAutoSmsTmplId(redemptionAutoSmsTmplId);
			
			loyaltyAutoComm.setOtpMessageAutoSmsTmpltId(otpMessageAutoSmsTmpltId);

			loyaltyAutoComm.setRedemptionOtpAutoSmsTmpltId(redemptionOtpAutoSmsTmpltId);
			/*
			 * to get the auto email type of the unmatched selected domain
			 */
			/*
			 * String types = validateAutoCommunication(loyaltyAutoComm);
			 * if(!types.isEmpty()) { return types; }
			 */
			loyaltyAutoComm.setCreatedBy(userId.toString());
			loyaltyAutoComm.setCreatedDate(Calendar.getInstance());
			//loyaltyAutoCommDao.saveOrUpdate(loyaltyAutoComm);
			loyaltyAutoCommDaoForDML.saveOrUpdate(loyaltyAutoComm);

			LoyaltyProgram prgmObj = getProgmObj(prgmId);
			String draftStatus = prgmObj.getDraftStatus();
			String[] draftList = draftStatus.split(Constants.ADDR_COL_DELIMETER);

			draftList[4] = OCConstants.LOYALTY_DRAFT_STATUS_COMPLETE;
			draftStatus = "";
			for (String eachStr : draftList) {

				if (draftStatus.isEmpty()) {
					draftStatus = eachStr;
				} else {
					draftStatus += Constants.ADDR_COL_DELIMETER + eachStr;
				}
			}
			prgmObj.setDraftStatus(draftStatus);
			savePrgmObj(prgmObj);

		}catch (Exception e) {
			logger.error("Exception ::",e);
		}

	}

	public LoyaltyAutoComm getAutoCommunicationObj(Long prgmId) {
		LoyaltyAutoComm loyaltyAutoComm=null;
		try {
			loyaltyAutoCommDao= (LoyaltyAutoCommDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_AUTO_COMM_DAO);
			loyaltyAutoComm=loyaltyAutoCommDao.findById(prgmId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return loyaltyAutoComm;
	}


	public CustomTemplates getCustomTemplate(Long tempId) {
		CustomTemplates customTemplates = null ;
		try {
			customTemplatesDao = (CustomTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			customTemplates=customTemplatesDao.findCustTemplateById(tempId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return customTemplates;
	}


	public SkuFile getSkuFile(String selectdCategory, String productCode, Long userId) {
		SkuFile skuFile = null ;
		try {
			skuFileDao = (SkuFileDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);
			skuFile = skuFileDao.getSkuFileByCategory(selectdCategory,productCode,userId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return skuFile;
	}

	public OrganizationStores getStore(Long orgId, String value) {
		OrganizationStores orgStores = null;
		try {
			organizationStoresDao = (OrganizationStoresDao) ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			orgStores = organizationStoresDao.getStore(orgId,value);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}	
		return orgStores;
	}

	// Added for Reports

	/*public List<Object[]> findTotTransactionsRate(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {

		List<Object[]> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.findTotTransactionsRate(userId, prgmId, startDateStr, endDateStr, transType, storeNo, cardsetId,typeDiff,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}*/
	
	public List<Object[]> findTotTransactionsRate(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String subsidiaryNo, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {

		List<Object[]> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.findTotTransactionsRate(userId, prgmId, startDateStr, endDateStr, transType, subsidiaryNo, storeNo, cardsetId,typeDiff,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}
	
	/*public List<Object[]> findTotTransactionsRateforAll(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {

		List<Object[]> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.findTotTransactionsRateforAll(userId, prgmId, startDateStr, endDateStr, transType, storeNo, cardsetId,typeDiff,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}*/
	public List<Object[]> findTotTransactionsRateforAll(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String subsidiaryNo, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {

		List<Object[]> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.findTotTransactionsRateforAll(userId, prgmId, startDateStr, endDateStr, transType, subsidiaryNo, storeNo, cardsetId,typeDiff,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}
	
	/*public List<Object[]> findTotTransactionsRateforReturn(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {

		List<Object[]> returnList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			returnList = loyaltyTransactionChildDao.findTotTransactionsRateforReturn(userId, prgmId, startDateStr, endDateStr, transType, storeNo, cardsetId,typeDiff,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return returnList;
	}*/
	
	public List<Object[]> findTotTransactionsRateforReturn(Long userId,
			Long prgmId, String startDateStr, String endDateStr, String transType, String subsidiaryNo, String storeNo, Long cardsetId, String typeDiff,String employeeIdStr,Long tierId) {

		List<Object[]> returnList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			returnList = loyaltyTransactionChildDao.findTotTransactionsRateforReturn(userId, prgmId, startDateStr, endDateStr, transType, subsidiaryNo, storeNo, cardsetId,typeDiff,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return returnList;
	}
	public int getAllTransactionsCount(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId, String ltyIds,String employeeIdStr,Long tierId ) {
		int count = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			count = loyaltyTransactionChildDao.getAllTransactionsCount(userId, prgmId, startDateStr, endDateStr,key, transType, storeNo, subsidiaryNo, cardsetId, ltyIds,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return count;
	}
	
	public int getAllTransactionsCount(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId, String employeeIdStr,Long tierId) {
		int count = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			count = loyaltyTransactionChildDao.getAllTransactionsCount(userId, prgmId, startDateStr, endDateStr,key, transType, storeNo, subsidiaryNo, cardsetId, employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return count;
	}
	
	public List<Object[]> getAllTransactions(Long userId, Long prgmId, String startDateStr, 
			String endDateStr, int firstResult, int size, String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId, String destLtyIds,String employeeIdStr,Long tierId) {
		List<Object[]> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.getAllTransactions(userId, prgmId, startDateStr, endDateStr,firstResult,size,key, transType, storeNo, subsidiaryNo, cardsetId, destLtyIds,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}
	/*public List<Object[]> getAllTransactions(Long userId, Long prgmId, String startDateStr, 
			String endDateStr, int firstResult, int size, String key, String transType, String storeNo, Long cardsetId,String employeeIdStr,Long tierId ) {
		List<Object[]> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.getAllTransactions(userId, prgmId, startDateStr, endDateStr,firstResult,size,key, transType, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}*/
	public List<Object[]> getAllTransactions(Long userId, Long prgmId, String startDateStr, 
			String endDateStr, int firstResult, int size, String key, String transType, String storeNo, String subsidiaryNo, Long cardsetId,String employeeIdStr,Long tierId ) {
		List<Object[]> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.getAllTransactions(userId, prgmId, startDateStr, endDateStr,firstResult,size,key, transType, storeNo, subsidiaryNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}
	public Map<String , Object> getAllDestCards(Long userId, Long prgmId){
		Map<String, Object> destCardMap = null;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			destCardMap = contactsLoyaltyDao.getAllDestCards(userId, prgmId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return destCardMap;
	}
	public String getAllDestCards(Long userId, Long prgmId, String startDateStr, 
			String endDateStr, String key, String transType, String storeNo, Long cardsetId,String employeeIdStr) {
		String destCards = Constants.STRING_NILL;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			destCards = loyaltyTransactionChildDao.getAllDestCards(userId, prgmId, startDateStr, endDateStr,key, transType, storeNo, cardsetId,employeeIdStr);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return destCards;
	}
	/*public List<Object[]> findTotRegistrationsRate(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String storeNo, Long cardsetId, boolean isTransacted, String typeDiff, String type,String employeeIdStr,Long tierId) {
		List<Object[]> regList = null;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			regList = contactsLoyaltyDao.findTotRegistrationsRate(userId, prgmId, startDateStr, endDateStr,storeNo,cardsetId,isTransacted,typeDiff,type,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return regList;
	}*/
	public List<Object[]> findTotRegistrationsRate(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId, boolean isTransacted, String typeDiff, String type,String employeeIdStr,Long tierId) {
		List<Object[]> regList = null;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			regList = contactsLoyaltyDao.findTotRegistrationsRate(userId, prgmId, startDateStr, endDateStr,subsidiaryNo,storeNo,cardsetId,isTransacted,typeDiff,type,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return regList;
	}


	/*public int getRegistrationsCount(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String key, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {
		int totalSize=0;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			totalSize = contactsLoyaltyDao.getRegistrationsCount(userId,prgmId,startDateStr,endDateStr,key, storeNo, cardsetId,isTransacted,status,employeeIdStr,tierId);
			logger.info("totalSize"+totalSize);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalSize;
	}*/
	public int getRegistrationsCount(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String key, String subsidiaryNo, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {
		int totalSize=0;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			totalSize = contactsLoyaltyDao.getRegistrationsCount(userId,prgmId,startDateStr,endDateStr,key, subsidiaryNo, storeNo, cardsetId,isTransacted,status,employeeIdStr,tierId);
			logger.info("totalSize"+totalSize);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalSize;
	}
	
	
	public int getRegistrationsCountforkey(Long userId, Long prgmId,String key) {
		int totalSize=0;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			totalSize = contactsLoyaltyDao.getRegistrationsCountforkey(userId,prgmId,key);
			logger.info("totalSize"+totalSize);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalSize;
	}

	
	
	/*public List<Object[]> getContactLtyList(Long userId, Long prgmId,
			String startDateStr, String endDateStr, int firstResult, int size, String key, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {
		List<Object[]> contactLtyList = null;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactLtyList = contactsLoyaltyDao.getContactLtyList(userId, prgmId, startDateStr, endDateStr,firstResult,size,key, storeNo, cardsetId,isTransacted,status,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return contactLtyList;}*/
	public List<Object[]> getContactLtyList(Long userId, Long prgmId,
			String startDateStr, String endDateStr, int firstResult, int size, String key, String subsidiaryNo, String storeNo, Long cardsetId, boolean isTransacted,String status,String employeeIdStr,Long tierId) {
		List<Object[]> contactLtyList = null;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactLtyList = contactsLoyaltyDao.getContactLtyList(userId, prgmId, startDateStr, endDateStr,firstResult,size,key, subsidiaryNo, storeNo, cardsetId,isTransacted,status,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return contactLtyList;}

	/*public int getNoOfTransByType(String transType, Long prgmId,
			String startDateStr, String endDateStr) {
		int count = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			count = loyaltyTransactionChildDao.getNoOfTransByType(transType, prgmId, startDateStr, endDateStr);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return count;
	}*/

	
	public List<Object[]> getContactLtyListforkey(Long userId, Long prgmId,int firstResult, int size, String key) {
		List<Object[]> contactLtyList = null;
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactLtyList = contactsLoyaltyDao.getContactLtyListforkey(userId, prgmId,firstResult,size,key);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return contactLtyList;
		}
	
	public List<LoyaltyProgram> getProgList(Long userId,  int firstResult,int pageSize,String key) {
		List<LoyaltyProgram> progList = null;
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			progList = loyaltyProgramDao.getProgList(userId,firstResult,pageSize,key);

		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return progList;
	}

	public int getProgramsCount(Long userId, String key) {
		int  totalSize = 0;
		try {
			loyaltyProgramDao = (LoyaltyProgramDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			totalSize = loyaltyProgramDao.getProgramCount(userId, key);

		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalSize;
	}

	public int getIssuanceCount(Long programId,Long userId) {//APP-4728
		int issuanceCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issuanceCount = loyaltyTransactionChildDao.getIssuanceCount(programId,userId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return issuanceCount;
	}

	public int getRedemptionCount(Long programId,Long userId) {//APP-4728
		int redemptionCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redemptionCount = loyaltyTransactionChildDao.getRedemptionCount(programId,userId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return redemptionCount;
	}
	
	public int getReversalCount(Long programId,Long userId) {//APP-4728
		int redemptionCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redemptionCount = loyaltyTransactionChildDao.getReversalCount(programId, userId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return redemptionCount;
	}
	
	public int getStoreCreditCount(Long programId,Long userId) {//APP-4728
		int redemptionCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redemptionCount = loyaltyTransactionChildDao.getStoreCreditCount(programId,userId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return redemptionCount;
	}

	public Object[] getLiabilityData(Long userId, Long programId, String storeNo, Long cardsetId,Long tierId) {
		Object[] obj = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			obj = contactsLoyaltyDao.getLiabilityData(userId, programId,storeNo,cardsetId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return obj;
	}
	public Object[] getAggregatedLiabilityData(Long userId, Long programId, String storeNo, Long cardsetId,Long tierId) {
		Object[] obj = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			obj = contactsLoyaltyDao.getAggregatedLiabilityData(userId, programId,storeNo,cardsetId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return obj;
	}
	
	public Object[] getLiabilityDataforSuspended(Long userId, Long programId,Long cardsetId,Long tierId) {
		Object[] objsuspend = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			objsuspend = contactsLoyaltyDao.getLiabilityDataforSuspended(userId, programId,cardsetId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objsuspend;
	}
	
	public Object[] getLiabilityDataforExpired(Long userId, Long programId,Long cardsetId,Long tierId) {
		Object[] objexpire = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			objexpire = contactsLoyaltyDao.getLiabilityDataforExpired(userId, programId,cardsetId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objexpire;
	}

	/*	public int getLiabilityPoints(Long programId) {
		int sum = 0;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACT_LOYALITY_DAO);
			sum = contactsLoyaltyDao.getLiabilityPoints(programId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return sum;
	}*/

	/*public Object[] getIssuanceTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId, String type,String employeeIdStr,Long tierId) {
		Object[] issList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issList = loyaltyTransactionChildDao.getIssuanceTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId, type,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return issList;
	}*/
	public Object[] getIssuanceTrans(Long prgmId, String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId, String type,String employeeIdStr,Long tierId) {
		Object[] issList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issList = loyaltyTransactionChildDao.getIssuanceTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId, type,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return issList;
	}
	
	public Object[] getReversalTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] issList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issList = loyaltyTransactionChildDao.getReversalTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return issList;
	}
	
	/*public Object[] getIssRedReversalTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId, String enteredAmountType) {
		Object[] issList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issList = loyaltyTransactionChildDao.getIssRedReversalTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId, enteredAmountType);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return issList;
	}*/
	public Object[] getIssRedReversalTrans(Long prgmId, String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId, String enteredAmountType) {
		Object[] issList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issList = loyaltyTransactionChildDao.getIssRedReversalTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId, enteredAmountType);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return issList;
	}
	
	/*public Object[] getStoreCreditTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] issList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issList = loyaltyTransactionChildDao.getStoreCreditTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return issList;
	}*/
	public Object[] getStoreCreditTrans(Long prgmId, String startDateStr, String endDateStr,String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] issList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			issList = loyaltyTransactionChildDao.getStoreCreditTrans(prgmId, startDateStr, endDateStr,subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return issList;
	}

	/*public Object[] getRedemptionTransAmt(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] redList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redList = loyaltyTransactionChildDao.getRedemptionTransAmt(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return redList;
	}*/
	public Object[] getRedemptionTransAmt(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] redList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redList = loyaltyTransactionChildDao.getRedemptionTransAmt(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return redList;
	}

	/*public Object[] getRedemptionTransPts(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] redList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redList = loyaltyTransactionChildDao.getRedemptionTransPts(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return redList;
	}*/
	public Object[] getRedemptionTransPts(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] redList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redList = loyaltyTransactionChildDao.getRedemptionTransPts(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return redList;
	}
	/*public int getEnrolledCount(Long prgmId, Long cardsetId,String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId) {
		int enrCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			enrCount = loyaltyTransactionChildDao.getEnrolledCount(prgmId, startDateStr, endDateStr,null,cardsetId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return enrCount;
	}*/
	public Map<Long,Long> getTierEnrolledCount(Long userId,Long prgmId, String startDateStr,
			String endDateStr,String linkCardSetStr,String linkTierStr) {
		Map<Long,Long> enrolledCount = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			enrolledCount = contactsLoyaltyDao.getTierEnrolledCount(userId,prgmId, startDateStr, endDateStr,linkCardSetStr,linkTierStr);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return enrolledCount;
	}
	public Map<Long,Long> getTierEnrolledCount(Long userId,Long prgmId, String startDateStr,
			String endDateStr,String linkCardSetStr,String linkTierStr,boolean isTransacted,String storeNo,String employeeIdStr) {
		Map<Long,Long> enrolledCount = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			enrolledCount = contactsLoyaltyDao.getTierEnrolledCount(userId,prgmId, startDateStr, endDateStr,linkCardSetStr,linkTierStr,isTransacted,storeNo,employeeIdStr);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return enrolledCount;
	}
	public List<Object[]> getTierUpgradedCount(Long userId,Long prgmId, String startDateStr,
			String endDateStr,Long tierId) {
		List<Object[]> upgradedCount = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			upgradedCount = contactsLoyaltyDao.getTierUpgradedCount(userId,prgmId, startDateStr, endDateStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return upgradedCount;
	}
	public List<Object[]> getTierUpgradedCount(Long userId,Long prgmId, String startDateStr,
			String endDateStr,Long tierId,boolean isTransacted,String storeNo,Long cardsetId,String employeeIdStr) {
		List<Object[]> upgradedCount = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			upgradedCount = contactsLoyaltyDao.getTierUpgradedCount(userId,prgmId, startDateStr, endDateStr,tierId,isTransacted,storeNo,cardsetId,employeeIdStr);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return upgradedCount;
	}
	/*public int getEnrollementTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		int enrCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			enrCount = loyaltyTransactionChildDao.getEnrollementTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		returnvcvcv enrCount;
	}*/
	
	
	
	public Object[] getTrxSummery(Long userID, Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId){
		Object[] objArry = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			objArry = loyaltyTransactionChildDao.getTrxSummery(userID , prgmId,  startDateStr,
					 endDateStr,   subsidiaryNo,  storeNo,  cardsetId, employeeIdStr, tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArry;
		
	}
	
	public Object[] getTrxSummeryByDate(Long userID, Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId){
		Object[] objArry = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			objArry = loyaltyTransactionChildDao.getTrxSummeryByDate(userID , prgmId,  startDateStr,
					 endDateStr,   subsidiaryNo,  storeNo,  cardsetId, employeeIdStr, tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArry;
		
	}
	
	public int getEnrollementTrans(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		int enrCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			enrCount = loyaltyTransactionChildDao.getEnrollementTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return enrCount;
	}
	
	/*public int getChangeTierTrans(Long prgmId, String startDateStr,
			String endDateStr,  String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		int enrCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			enrCount = loyaltyTransactionChildDao.getChangeTierTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return enrCount;
	}*/
	public int getChangeTierTrans(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		int enrCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			enrCount = loyaltyTransactionChildDao.getChangeTierTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return enrCount;
	}
	
	/*public int getTransferTrans(Long prgmId,String startDateStr,String endDateStr,String  storeNo,Long cardsetId,String employeeIdStr,Long tierId){
		int tranferCount = 0;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			tranferCount = loyaltyTransactionChildDao.getTransferTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
			
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return tranferCount;
	}*/
	public int getTransferTrans(Long prgmId,String startDateStr,String endDateStr, String subsidiaryNo,String  storeNo,Long cardsetId,String employeeIdStr,Long tierId){
		int tranferCount = 0;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			tranferCount = loyaltyTransactionChildDao.getTransferTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
			
		}catch (Exception e) {
			logger.error("Exception :: ",e);
		}
		return tranferCount;
	}

	/*public Object[] getBonusTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] bonusList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			bonusList = loyaltyTransactionChildDao.getBonusTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e); 
		}
		return bonusList;
	}*/
	public Object[] getBonusTrans(Long prgmId, String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] bonusList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			bonusList = loyaltyTransactionChildDao.getBonusTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e); 
		}
		return bonusList;
	}
	
	/*public Object[] getAdjustmentTrans(Long prgmId, String startDateStr, String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] adjustList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			adjustList = loyaltyTransactionChildDao.getAdjustmentTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return adjustList;
	}*/
	public Object[] getAdjustmentTrans(Long prgmId, String startDateStr, String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		Object[] adjustList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			adjustList = loyaltyTransactionChildDao.getAdjustmentTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return adjustList;
	}
	
	/*public int getInquiryTrans(Long prgmId, String startDateStr,
			String endDateStr, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		int inqCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			inqCount = loyaltyTransactionChildDao.getInquiryTrans(prgmId, startDateStr, endDateStr,  storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return inqCount;
	}*/
	public int getInquiryTrans(Long prgmId, String startDateStr,
			String endDateStr, String subsidiaryNo, String storeNo, Long cardsetId,String employeeIdStr,Long tierId) {
		int inqCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			inqCount = loyaltyTransactionChildDao.getInquiryTrans(prgmId, startDateStr, endDateStr, subsidiaryNo, storeNo, cardsetId,employeeIdStr,tierId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return inqCount;
	}
	
	public List<Object[]> getEnrollmentCountByOptinMedium(Long prgmId, Long userId,
			String optInType, String startDateStr, String endDateStr) {
		List<Object[]> count = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			count = contactsLoyaltyDao.getEnrollmentCountByOptinMedium(prgmId,userId,optInType,startDateStr,endDateStr);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return count;
	}

	public List<Object[]> getLoyaltyRevenueByPrgmId(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String storeNo, Long cardsetId, String typeDiff,String subId,Long tierId) {
		List<Object[]> loyaltyRevenue = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			loyaltyRevenue = retailProSalesDao.getLoyaltyRevenueByPrgmId(userId, prgmId, startDateStr, endDateStr,storeNo,cardsetId,typeDiff,subId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return loyaltyRevenue;
	}

	public List<Object[]> getLoyaltyRevenue(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String storeNo, Long cardsetId, String typeDiff,String subId,Long tierId) {
		List<Object[]> loyaltyRevenue = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			loyaltyRevenue = retailProSalesDao.getLoyaltyRevenueAndVisits(userId, prgmId, startDateStr, endDateStr,storeNo,cardsetId,typeDiff,subId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return loyaltyRevenue;
	}
	public List<Map<String,Object>> getTotalRevenueAndVisitsByUserId(Long userId, String startDateStr,
			String endDateStr) {
		List<Map<String,Object>> totalRevenue = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			totalRevenue = retailProSalesDao.getTotalRevenueByUserId(userId, startDateStr, endDateStr);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalRevenue;
	}

	public List<Object[]> getLoyaltyVisitsByPrgmId(Long userId, Long prgmId,
			String startDateStr, String endDateStr, String storeNo, Long cardsetId, String typeDiff,String subsId,Long tierId) {
		List<Object[]> loyaltyVisits = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			loyaltyVisits = retailProSalesDao.getLoyaltyVisitsByPrgmId(userId, prgmId, startDateStr, endDateStr, storeNo,cardsetId,typeDiff,subsId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return loyaltyVisits;
	}

	public Long getTotalVisitsByUserId(Long userId, String startDateStr,
			String endDateStr) {
		Long totalVisits = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			totalVisits = retailProSalesDao.getTotalVisitsByUserId(userId, startDateStr, endDateStr);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalVisits;
	}


	public List<Object[]> getNonLoyaltyRevenue(Long userId,
			String startDateStr, String endDateStr, String storeNo,
			String typeDiff) {
		List<Object[]> nonLoyaltyRevenue = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			nonLoyaltyRevenue = retailProSalesDao.getNonLoyaltyRevenue(userId, startDateStr, endDateStr,storeNo,typeDiff);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return nonLoyaltyRevenue;
	}

	public List<Object[]> getTotalRevenue(Long userId,
			String startDateStr, String endDateStr, String storeNo,
			String typeDiff) {
		List<Object[]> totalRevenue = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			totalRevenue = retailProSalesDao.getTotalRevenue(userId, startDateStr, endDateStr,storeNo,typeDiff);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalRevenue;
	}

	public List<Object[]> getNonLoyaltyVisits(Long userId,
			String startDateStr, String endDateStr, String storeNo,
			String typeDiff) {
		List<Object[]> nonLoyaltyVisits = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			nonLoyaltyVisits = retailProSalesDao.getNonLoyaltyVisits(userId, startDateStr, endDateStr,storeNo,typeDiff);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return nonLoyaltyVisits;
	}

	public List<Object[]> getTotalVisits(Long userId,
			String startDateStr, String endDateStr, String storeNo,
			String typeDiff) {
		List<Object[]> totalVisits = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			totalVisits = retailProSalesDao.getTotalVisits(userId, startDateStr, endDateStr,storeNo,typeDiff);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return totalVisits;
	}

	public Contacts getContactObj(Long cid) {
		Contacts contacts = null;
		try {
			contactsDao = (ContactsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_DAO);
			contacts = contactsDao.findById(cid);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return contacts;
	}
	
	public List<Object[]> getStorelevelKPI(Long userId, Long prgmId, String startDatestr, String endDatestr) {
		List<Object[]> objArr = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			objArr = retailProSalesDao.getStorelevelKPI(userId, prgmId, startDatestr, endDatestr);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArr;
	}
	
	public List<Object[]> getTierVisitsAndRevenue(Long userId, Long prgmId, String startDatestr, String endDatestr,Long tierId) {
		List<Object[]> objArr = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			objArr = retailProSalesDao.getTierVisitsAndRevenue(userId, prgmId, startDatestr, endDatestr,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArr;
	}

	public List<Object[]> getTierVisitsAndRevenue(Long userId, Long prgmId, String startDatestr, String endDatestr,String storeNo,Long cardsetId,Long tierId) {
		List<Object[]> objArr = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			objArr = retailProSalesDao.getTierVisitsAndRevenue(userId, prgmId, startDatestr, endDatestr,storeNo,cardsetId,tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArr;
	}

	/*public List<Object[]> getTotalMemberships(Long userId, Long prgmId, String startDatestr, String endDatestr,Long tierId,boolean isTransacted,String storeNo,Long cardsetId,String employeeIdStr) {
		List<Object[]> tierobjArr = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			tierobjArr = contactsLoyaltyDao.getTotalMemberships(userId, prgmId, startDatestr, endDatestr,tierId,isTransacted,storeNo,cardsetId,employeeIdStr);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return tierobjArr;
	}*/
	public List<Object[]> getTotalMemberships(Long userId, Long prgmId, String startDatestr, String endDatestr,Long tierId,boolean isTransacted, String subsidiaryNo, String storeNo,Long cardsetId,String employeeIdStr) {
		List<Object[]> tierobjArr = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			tierobjArr = contactsLoyaltyDao.getTotalMemberships(userId, prgmId, startDatestr, endDatestr,tierId,isTransacted, subsidiaryNo, storeNo,cardsetId,employeeIdStr);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return tierobjArr;
	}
	public List<Object[]> getStoreContactLtyList(Long userId, Long prgmId, String startDate, String endDate, boolean isMobile, String type) {
		List<Object[]> objArr = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			objArr = contactsLoyaltyDao.getStoreContactLtyList(prgmId,userId,startDate,endDate,isMobile,type);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArr;
	}

	public List<Object[]> getStoresTransData(Long userId, Long prgmId,String startDate, String endDate) {
		List<Object[]> objArry = null ;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			objArry = loyaltyTransactionChildDao.getStoresTransData(prgmId,userId,startDate,endDate);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return objArry;
	}

	public int getCustomersCountByTierId( Long userId, Long tierId) {
		int count = 0;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			count = contactsLoyaltyDao.getCustomersCountByTierId(userId, tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return count;
	}

	public List<ContactsLoyalty> getCustomersByTierId(Long userId, Long tierId) {
		List<ContactsLoyalty> contObj = null;
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contObj = contactsLoyaltyDao.getCustomersByTierId(userId, tierId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return contObj;
	}

	public void updateContactTierIds(List<ContactsLoyalty> newList) {
		try {
			contactsLoyaltyDao=(ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactsLoyaltyDaoForDML=(ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);

			//contactsLoyaltyDao.saveByCollection(newList);
			contactsLoyaltyDaoForDML.saveByCollection(newList);

		}catch (Exception e) {
			logger.error("Exception::",e);
		}
	}
	
	public void updateCardSets(List<LoyaltyCardSet> newList) {
		try {
			loyaltyCardSetDao=(LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardSetDaoForDML=(LoyaltyCardSetDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_CARD_SET_DAO_FOR_DML);

			//loyaltyCardSetDao.saveByCollection(newList);
			loyaltyCardSetDaoForDML.saveByCollection(newList);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
	}

	public Object[] getHighLevelMetrics(Long userID, Long programId) {
		Object[] objArry = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			objArry = loyaltyTransactionChildDao.getHighLevelMetrics(userID , programId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArry;
		
	}
	public int getEnrollmentsCount(Long programId,Long userId) {//APP-4728
		int enrollCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			enrollCount = loyaltyTransactionChildDao.getEnrollmentsCount(programId,userId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return enrollCount;
	}

	public List<Object[]> getStoresIssLiabilityData(Long prgmId, Long userId) {
		List<Object[]> objArry = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			objArry = loyaltyTransactionChildDao.getStoresIssLiabilityData(prgmId,userId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArry;
	}

	public List<Object[]> getStoresRedeemLiabilityData(Long prgmId, Long userId) {
		List<Object[]> objArry = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			objArry = loyaltyTransactionChildDao.getStoresRedeemLiabilityData(prgmId,userId);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objArry;
	}

	public long getAllTransactionsCountByPrgmId(Long userId, Long prgmId,
			String startDate, String endDate) {
		long totCount = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			totCount = loyaltyTransactionChildDao.getAllTransactionsCountByPrgmId(userId,prgmId,startDate,endDate);
		}catch (Exception e) {
			logger.error("Exception--::",e);
		}
		return totCount;
	}

	public List<Object[]> getTransactionCountByStores(Long userId, Long prgmId,
			String startDate, String endDate) {
		List<Object[]> objList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			objList = loyaltyTransactionChildDao.getTransactionCountByStores(userId,prgmId,startDate,endDate);
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		return objList;
	}

	public List<AutoSMS> getSmsTemplateList(Long userId, String type) {

		List<AutoSMS> smsTemplateList=null;
		try {
			autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			smsTemplateList = autoSMSDao.getTemplatesByStatus(userId, type);
		}catch (Exception e) {
			logger.error("Exception e ::",e);
		}
		return smsTemplateList;
	}


	public AutoSMS getAutoSmsTemplateById(Long tempId) {
		AutoSMS autoSMS = null ;
		try {
			autoSMSDao = (AutoSMSDao) ServiceLocator.getInstance().getDAOByName(OCConstants.AUTO_SMS_DAO);
			autoSMS = autoSMSDao.getAutoSmsTemplateById(tempId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return autoSMS;
	}


	/**
	 * This method fetch the ContactsLoyalty based on membership Number & userid
	 * @param cardNumber
	 * @param userId
	 * @return contactsLoyalty
	 */
	public ContactsLoyalty getContactLtyByMembershipNumber(Long cardNumber, Long  userId){
		ContactsLoyalty contactsLoyalty = null;

		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactsLoyalty = contactsLoyaltyDao.findByMembershipNoAndUserId(cardNumber , userId ); 
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return contactsLoyalty;
	}//getContactLtyByMembershipNumber

	/**
	 * This method find's the contact Loyalty list based on the search criteria.
	 * @param userId
	 * @param searchKey
	 * @param searchValue
	 * @return contactsLoyalties
	 */
	public   List<Map<String, Object>>  getContactLtyBySearchCriteria(Users user,String searchKey,String searchValue){
		 List<Map<String, Object>>  contactsLoyalties = null;
		 logger.debug(">>>>>>>>>>>>> entered in getContactLtyBySearchCriteria");
		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactsLoyalties = contactsLoyaltyDao.findContactLtyBySearchCriteria(user,searchKey,searchValue);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getContactLtyBySearchCriteria ");
		return contactsLoyalties;
	}//getContactLtyByFullName

	/**
	 * This method fetch the LastPurchaseSKU Details based on Docsid
	 * @param docsid
	 * @param lastSalesDate
	 * @param userId
	 * @return skuList
	 */
	public List<Object[]> getLastPurchaseSKUDetailsByDocsid(String docsid, Long userId) {

		List<Object[]> skuList =null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			skuList = retailProSalesDao.getLastPurchaseSKUDetailsByDocsid(docsid, userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return skuList;
	}//getLastPurchaseSKUDetailsByDocsid

	public Object[] getLastPurchaseQtyAndAmtByDocSid(String docSid, Long userId) {
		Object[] obj=null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			obj = retailProSalesDao.getLastPurchaseQtyAndAmtByDocSid(docSid, userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return obj;
	}

	public RetailProSalesCSV getLastPurchaseSalesData(String contactIds, Long userId) {
		RetailProSalesCSV retailProSales = null;
		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			retailProSales = retailProSalesDao.findRecordByContactId(contactIds , "DESC" , userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return retailProSales;
	}
	
	/**
	 * This method get LoyaltyTransactionChild based on Issuance & docsid
	 * @param transactionType
	 * @param docSid
	 * @return loyaltyTransactionChild
	 */
	public LoyaltyTransactionChild getLtyTransByIssuanceAndDocSid(String docSid,Long userId) {
		LoyaltyTransactionChild child =null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			child = loyaltyTransactionChildDao.findLtyTransByIssuanceAndDocSid(docSid,userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return child;
	}//getLtyTransByIssuanceAndDocSid

	/**
	 * This method get LoyaltyTransactionChild based on Redemption & docsid
	 * @param transactionType
	 * @param docSid
	 * @return redemptionList
	 */
	public Object[] findRedemptionByDocSid(String docSid,Long userId) {

		Object[] redemptionList= null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			redemptionList = loyaltyTransactionChildDao.findRedemptionByDocSid(docSid,userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return redemptionList;
	}//getLtyTransByRedemptionAndDocSid


	/**
	 * This method get coupon code based on docsid and orgId
	 * @param docSid
	 * @param orgId
	 * @return couponCodeList
	 */
	public List<CouponCodes> getCouponCodeByDocSid(String docSid, Long orgId) {
		List<CouponCodes> couponCodeList = null;
		try {
			couponCodesDao = (CouponCodesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			couponCodeList = couponCodesDao.findCouponCodeByDocSid(docSid,orgId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return couponCodeList;
	}//getCouponCodeByDocSid

	/**
	 * This method get's count of Issuance base on userid,membershipNumber and loylatyTransaction type
	 * @param userId
	 * @param cardNumber
	 * @param loyaltyTransType
	 * @return countOfIssuance
	 */
	public long getCountOfIssuance(Long userId,String cardNumber,String loyaltyTransType) {
		long countOfIssuance =0L;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			countOfIssuance = loyaltyTransactionChildDao.getCountOfIssuance(userId,cardNumber,loyaltyTransType);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return countOfIssuance;
	}//getCountOfIssuance

	/**
	 * This method get's count of Redemption base on userid,membershipNumber and loylatyTransaction type
	 * @param userId
	 * @param cardNumber
	 * @param loyaltyTransType
	 * @return countOfRedemption
	 */
	public long getCountOfRedemption(Long userId,String cardNumber,String loyaltyTransType) {
		long countOfRedemption =0L;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			countOfRedemption = loyaltyTransactionChildDao.getCountOfRedemption(userId,cardNumber,loyaltyTransType);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return countOfRedemption;
	}//getCountOfRedemption

	/**
	 * This method get's list of loyalty transaction child based on Membership Number
	 * @param cardNumber
	 * @param userId
	 * @return loyaltyTransactionChildList
	 */
	
	/**
	 * This method get's count of Issuance base on userid,membershipNumber and loylatyTransaction type
	 * @param userId
	 * @param cardNumber
	 * @param loyaltyTransType
	 * @return countOfIssuance
	 */
	public long getAllCountOfIssuance(Long userId,Long loyaltyId,String loyaltyTransType) {
		long countOfIssuance =0L;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			countOfIssuance = loyaltyTransactionChildDao.getAllCountOfIssuance(userId,loyaltyTransType,loyaltyId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return countOfIssuance;
	}//getCountOfIssuance

	/**
	 * This method get's count of Redemption base on userid,membershipNumber and loylatyTransaction type
	 * @param userId
	 * @param cardNumber
	 * @param loyaltyTransType
	 * @return countOfRedemption
	 */
	public long getAllCountOfRedemption(Long userId,Long loyaltyId,String loyaltyTransType) {
		long countOfRedemption =0L;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			countOfRedemption = loyaltyTransactionChildDao.getAllCountOfRedemption(userId,loyaltyTransType,loyaltyId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return countOfRedemption;
	}//getCountOfRedemption

	/**
	 * This method get's list of loyalty transaction child based on Membership Number
	 * @param cardNumber
	 * @param userId
	 * @return loyaltyTransactionChildList
	 */
	public List<LoyaltyTransactionChild> getAllTransactionsByCardNumber(Long cardNumber, Long userId) {
		List<LoyaltyTransactionChild>  list= null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			list = loyaltyTransactionChildDao.findAllTransactionsByCardNumber(cardNumber,userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return list;
	}//getAllTransactionsByCardNumber
	
	public List<String> getAllempIds(Long orgId) {
		List<String>  employeeIdList= null;
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			employeeIdList = loyaltyTransactionChildDao.findAllempIds(orgId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return employeeIdList;
	}
	
	



	/**
	 * This method get the loyalty card set based on cardset id
	 * @param cardSetId
	 * @return loyaltyCardSet
	 */
	public LoyaltyCardSet getLoyaltyCardSetObj(Long cardSetId) {
		LoyaltyCardSet loyaltyCardSet = null;
		try {
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardSet = loyaltyCardSetDao.findByCardSetId(cardSetId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return loyaltyCardSet;
	}//getLoyaltyCardSetObj

	/**
	 * 
	 * @param mailinglistTypePos
	 * @param userId
	 * @return mailingList
	 */
	public MailingList findListTypeMailingList(String mailinglistTypePos,Long userId) {
		MailingList mailingList = null;
		try{
			mailingListDao = (MailingListDao) ServiceLocator.getInstance().getDAOByName(OCConstants.MAILINGLIST_DAO);
			mailingList = mailingListDao.findListTypeMailingList(mailinglistTypePos,userId);
		}catch(Exception e){
			logger.error("Exception::",e);
		}
		return mailingList;
	}//findListTypeMailingList

	/**
	 * 
	 * @param customTemplateId
	 * @return CustomTemplates
	 */
	public CustomTemplates findCustTemplateById(Long customTemplateId) {
		CustomTemplates customTemplates = null;
		try {
			customTemplatesDao=(CustomTemplatesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CUSTOMTEMPLATES_DAO);
			customTemplates = customTemplatesDao.findCustTemplateById(customTemplateId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return customTemplates;
	}//findCustTemplateById

	/**
	 * 
	 * @param emailQueue
	 */
	public void saveOrUpdateEmailQueue(EmailQueue emailQueue) {
		try{
			emailQueueDao = (EmailQueueDao) ServiceLocator.getInstance().getDAOByName(OCConstants.EMAILQUEUE_DAO);
			emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.EMAILQUEUE_DAO_ForDML);
			//emailQueueDao.saveOrUpdate(emailQueue);
			emailQueueDaoForDML.saveOrUpdate(emailQueue);


		} catch (Exception e) {
			logger.error("Exception::",e);
		}


	}//saveOrUpdateEmailQueue

	/**
	 * 
	 * @param contactsLoyalty
	 * @return flag
	 */

	public boolean saveOrUpdateContactLoyalty(ContactsLoyalty contactsLoyalty){
		boolean flag =false;
		try{
			ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			loyaltyDao.saveOrUpdate(contactsLoyalty);
			flag = true;
		} catch (Exception e) {
			flag = false;
			logger.error("Exception::",e);
		}
		return flag;
	}//saveOrUpdateContactLoyalty

	/**
	 * 
	 * @param loyaltyId
	 * @return contactsLoyalty
	 */

	public ContactsLoyalty getContactLtyById(long loyaltyId) {
		ContactsLoyalty contactsLoyalty = null;

		try {
			contactsLoyaltyDao = (ContactsLoyaltyDao) ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			contactsLoyalty = contactsLoyaltyDao.findAllByLoyaltyId(loyaltyId); 
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

		return contactsLoyalty;
	}//getContactLtyById
	/**
	 * 
	 * @param userId
	 * @param loyaltyId
	 * @param startDateStr
	 * @param endDateStr
	 * @return countOfTransactionChild
	 */
	public int getMaxTransactionsByMembershipnumberCount(Long userId,String loyaltyId,String startDateStr, String endDateStr){
		int count = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			count = loyaltyTransactionChildDao.getMaxTransactionsByMembershipnumberCount(userId, loyaltyId, startDateStr, endDateStr);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return count;
	}//getAllTransactionsByMembershipnumberCount

	/**
	 * 
	 * @param userId
	 * @param loyaltyId
	 * @param startDateStr
	 * @param endDateStr
	 * @return countOfTransactionChild
	 */
	public int getAllTransactionsByMembershipnumberCount(Long userId,Long loyaltyId,String startDateStr, String endDateStr){
		int count = 0;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			count = loyaltyTransactionChildDao.getAllTransactionsByMembershipnumberCount(userId, loyaltyId, startDateStr, endDateStr);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return count;
	}//getAllTransactionsByMembershipnumberCount

	/**
	 * 
	 * @param userId
	 * @param membershipNumber
	 * @param startDateStr
	 * @param endDateStr
	 * @param firstResult
	 * @param size
	 * @return loyaltyTransactionChildList
	 */
	public List<LoyaltyTransactionChild> getAllTransactionsByMembershipnumber(Long userId, Long loyaltyId, String startDateStr,String endDateStr, int firstResult, int size){
		List<LoyaltyTransactionChild> transList = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			transList = loyaltyTransactionChildDao.getAllTransactionsByMembershipnumber(userId, loyaltyId, startDateStr, endDateStr,firstResult,size);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return transList;
	}//getAllTransactionsByMembershipnumber

	public LoyaltyTransactionChild getTransByMembershipNoAndTransType(Long loyaltyId,String transactionType,Long userId){
		LoyaltyTransactionChild loyaltyTransactionChild = null;
		try {
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			loyaltyTransactionChild = loyaltyTransactionChildDao.getTransByMembershipNoAndTransTypeAndLtyId(loyaltyId,transactionType,userId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}

		return loyaltyTransactionChild;
	}

	/**
	 * Sets flag Y to the organisation before it generates card sets.
	 * @param programId
	 */
	private void setCardGenFlag(Long programId) {
		logger.info("Setting card gen flag Y to the organisation...");

		try{
			LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram program = programDao.findById(programId);
			Long orgId = program.getOrgId();
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO); 
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			UserOrganization userOrg = usersDao.findByOrgId(orgId);
			userOrg.setCardGenerateFlag(OCConstants.LOYALTY_CARD_GENERATION_FLAG_Y);
			//usersDao.saveOrUpdate(userOrg);
			usersDaoForDML.saveOrUpdate(userOrg);
		} catch(Exception e){
			logger.error("excetpion...", e);
			return;
		}

	}

	/**
	 * This method fetch Receipt Number from retail_pro_sales based on the doc_sid
	 * @param docSID
	 * @return receiptNumber
	 */
	public String getReceiptNumberByDocsid(String docSID, Long userId) {
		logger.debug(">>>>>>>>>>>>> entered in getReceiptNumberByDocsid");
		String receiptNumber = null;
		RetailProSalesCSV retailProSalesCSVs = null;

		try {
			retailProSalesDao=(RetailProSalesDao) ServiceLocator.getInstance().getDAOByName(OCConstants.RETAILPRO_SALES_DAO);
			retailProSalesCSVs = retailProSalesDao.findReceiptNumberByDocsid(docSID, userId);

			if(retailProSalesCSVs != null && retailProSalesCSVs.getRecieptNumber() != null){
				receiptNumber= retailProSalesCSVs.getRecieptNumber();
			}
			else{
				receiptNumber = null;
			}
		}catch (Exception e) {
			logger.error("Exception::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getReceiptNumberByDocsid ");
		return receiptNumber;
	}//getReceiptNumberByDocsid

	/**
	 * This method get's LoyaltySettings by User Id.
	 * @param userId
	 * @return loyaltySettings.
	 */
	public LoyaltySettings findLoyaltySettingsByOrgId(Long orgId) {
		logger.debug(">>>>>>>>>>>>> entered in findLoyaltySettingsByOrgId");
		LoyaltySettings loyaltySettings=null;
		try{
			LoyaltySettingsDao loyaltySettingsDao = (LoyaltySettingsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_SETTINGS_DAO);
			loyaltySettings = loyaltySettingsDao.findByOrgId(orgId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findLoyaltySettingsByOrgId ");
		return loyaltySettings;
	}//findByUserId

	public LoyaltyThresholdAlerts findPwdByUserID(Long userId) {
		logger.debug(">>>>>>>>>>>>> entered in findPwdByUserID");
		LoyaltyThresholdAlerts loyaltyThresholdAlerts = null;
		try{
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			loyaltyThresholdAlerts = loyaltyThresholdAlertsDao.findByUserId(userId);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findPwdByUserID ");
		return loyaltyThresholdAlerts;
	}

	public void saveOrUpdateThresholdAlerts(LoyaltyThresholdAlerts loyaltyThresholdAlerts) {
		try{
			LoyaltyThresholdAlertsDao loyaltyThresholdAlertsDao = (LoyaltyThresholdAlertsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO);
			LoyaltyThresholdAlertsDaoForDML loyaltyThresholdAlertsDaoForDML = (LoyaltyThresholdAlertsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_THRESHOLD_ALERTS_DAO_FOR_DML);

			//loyaltyThresholdAlertsDao.saveOrUpdate(loyaltyThresholdAlerts);
			loyaltyThresholdAlertsDaoForDML.saveOrUpdate(loyaltyThresholdAlerts);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
	}

	private StringBuffer logProgramChanges(Long programId, Long userId, StringBuffer sb, boolean saveFlag) {
		if(sb == null) sb = new StringBuffer();
		try {
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON);
			
			LoyaltyProgram prgmObj = null;
			if(programId == null){
				sb.append("null");
			}
			else{
				prgmObj = getProgmObj(programId);
				if(prgmObj != null) {
					sb.append(prgmObj.getProgramName() + Constants.DELIMETER_COMMA);
					sb.append(prgmObj.getDescription() + Constants.DELIMETER_COMMA);
					sb.append(prgmObj.getMembershipType() + Constants.DELIMETER_COMMA);
					sb.append(prgmObj.getTierEnableFlag() + Constants.DELIMETER_COMMA);
					sb.append(prgmObj.getNoOfTiers() + Constants.DELIMETER_COMMA);
					sb.append(prgmObj.getStatus() + Constants.DELIMETER_COMMA);
					sb.append(prgmObj.getDefaultFlag() + Constants.DELIMETER_COMMA);
					sb.append(prgmObj.getUniqueMobileFlag() + Constants.DELIMETER_COMMA);
					//sb.append(prgmObj.getRedemptionOTPFlag() + Constants.DELIMETER_COMMA);
					//sb.append(prgmObj.getOtpLimitAmt() == null ? "--" : prgmObj.getOtpLimitAmt());
				}
			}

			if(saveFlag){
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_PROGRAM, sb.toString(), 
						(prgmObj != null && prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				logger.debug("---------SB:::" + sb.toString());
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				LtySettingsActivityLogsDaoForDML activityLogDaoForDML = (LtySettingsActivityLogsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
				//activityLogDao.saveOrUpdate(activityLog);
				activityLogDaoForDML.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return sb;
	}
	
	public StringBuffer logTierChanges(Long programId, Long userId, StringBuffer sb, boolean saveFlag, boolean sendEmailFlag) {
		if(sb == null) sb = new StringBuffer();
		try {
			LoyaltyProgram prgmObj = getProgmObj(programId);
			List<LoyaltyProgramTier> tierList = getTierList(programId);
			int noOfTiers=prgmObj.getNoOfTiers();
			
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON);

			for(int i = 1 ; i <= noOfTiers ; i++){
				String tier="Tier "+i;
				boolean isExists=false;
				if(!tier.equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_TIER1)){
					sb.append(Constants.DELIMITER_DOUBLE_PIPE);
				}
				if (tierList != null) {
					for (LoyaltyProgramTier loyaltyProgramTier : tierList) {
						if (loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {

							sb.append(loyaltyProgramTier.getTierType() + Constants.DELIMETER_COMMA);
							sb.append(loyaltyProgramTier.getTierName() + Constants.DELIMETER_COMMA);
							sb.append(getEarn(loyaltyProgramTier) + Constants.DELIMETER_COMMA);
							String conversionRule = "";
							if(prgmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) {
								conversionRule = getConversionRule(loyaltyProgramTier);
							} else {
								conversionRule = getRule(loyaltyProgramTier);
							}
							
							sb.append((conversionRule.isEmpty() ? "--" : conversionRule) + Constants.DELIMETER_COMMA);
							sb.append((getActivationTime(loyaltyProgramTier).isEmpty() ? "--" : getActivationTime(loyaltyProgramTier)) + Constants.DELIMETER_COMMA);
							sb.append(loyaltyProgramTier.getRedemptionOTPFlag() + Constants.DELIMETER_COMMA);
							sb.append(loyaltyProgramTier.getOtpLimitAmt() == null ? "--" : loyaltyProgramTier.getOtpLimitAmt());

							if(loyaltyProgramTier.getEarnType().equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)&& !conversionRule.isEmpty() && loyaltyProgramTier.getConversionType() != null ) {
								sb.append((loyaltyProgramTier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO) ? "Auto-Conversion" : "On-Demand") + Constants.DELIMETER_COMMA);
							}
							else {
								sb.append("--" + Constants.DELIMETER_COMMA);
							}

							if(!(prgmObj.getRewardType().equalsIgnoreCase(OCConstants.REWARD_TYPE_PERK)) && prgmObj.getTierEnableFlag() == OCConstants.FLAG_YES ) {
								sb.append(getUpgradeRule(loyaltyProgramTier).isEmpty() ? "--" : getUpgradeRule(loyaltyProgramTier));
							}
							else{
								sb.append("--");
							}
							isExists = true;
						}
					}
				}
				if(!isExists) {
					sb.append(tier+",null");
				}
			}

			if(saveFlag){
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_TIER, sb.toString(),
						(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) && sendEmailFlag ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				logger.debug("---------SB:::" + sb.toString());
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				LtySettingsActivityLogsDaoForDML activityLogDaoForDML = (LtySettingsActivityLogsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
				//activityLogDao.saveOrUpdate(activityLog);
				activityLogDaoForDML.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return sb;
	}
	
	/*public StringBuffer logRegBonusChanges(Long programId, Long userId, StringBuffer sb, boolean saveFlag) {
		if(sb == null) sb = new StringBuffer();
		try {
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON);

			LoyaltyThresholdBonus bonusObj = getThresholdObj(programId);
			if(bonusObj != null) {
				sb.append("On Enrollment" + Constants.DELIMETER_COMMA);
				sb.append(getBonus(bonusObj).isEmpty() ? "--" : getBonus(bonusObj));
			}
			else{
				sb.append("null");
			}

			if(saveFlag){
				LoyaltyProgram prgmObj = getProgmObj(programId);
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_REGISTRATION_BONUS, sb.toString(),
						(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				activityLogDao.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		logger.debug("---------SB:::" + sb.toString());
		return sb;
	}*/
	
	public StringBuffer logBonusChanges(Long programId, Long userId, StringBuffer sb, boolean saveFlag) {
		if(sb == null) sb = new StringBuffer();
		try {
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON);

			LoyaltyThresholdBonus regBonusObj = getThresholdObj(programId);
			if(regBonusObj != null){
				sb.append("On Enrollment" + Constants.DELIMETER_COMMA);
				sb.append(getBonus(regBonusObj).isEmpty() ? "--" : getBonus(regBonusObj));
			}
			List<LoyaltyThresholdBonus> bonusList = getThresholdBonusList(programId);
			if(bonusList != null) {
				for (LoyaltyThresholdBonus bonusObj : bonusList) {
					if(!sb.toString().endsWith(Constants.DELIMETER_DOUBLECOLON)){
						sb.append(Constants.DELIMITER_DOUBLE_PIPE);
					}
					sb.append((getEarnedPoints(bonusObj).isEmpty() ? "--" : getEarnedPoints(bonusObj)) + Constants.DELIMETER_COMMA);
					sb.append(getBonus(bonusObj).isEmpty() ? "--" : getBonus(bonusObj));
				}
			}
			if(regBonusObj == null && bonusList == null){
				/*if(!sb.toString().endsWith(Constants.DELIMETER_DOUBLECOLON)){
					sb.append(Constants.DELIMITER_DOUBLE_PIPE);
				}*/
				sb.append("null");
			}

			if(saveFlag){
				LoyaltyProgram prgmObj = getProgmObj(programId);
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_BONUS, sb.toString(),
						(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				logger.debug("---------SB:::" + sb.toString());
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				LtySettingsActivityLogsDaoForDML activityLogDaoForDML = (LtySettingsActivityLogsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
				//activityLogDao.saveOrUpdate(activityLog);
				activityLogDaoForDML.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return sb;
	}

	public StringBuffer logLtyRewardValidityChanges(Long programId, Long userId, StringBuffer sb, boolean saveFlag, char rewardExpiryFlag) {
		if(sb == null) sb = new StringBuffer();
		try {
			LoyaltyProgram prgmObj = getProgmObj(programId);
			List<LoyaltyProgramTier> tierList = getTierList(programId);
			
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON + rewardExpiryFlag);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON + rewardExpiryFlag);

			if(rewardExpiryFlag == OCConstants.FLAG_YES) {
				int noOfTiers=prgmObj.getNoOfTiers();
				for(int i = 1 ; i <= noOfTiers ; i++){
					String tier="Tier "+i;
					boolean isExists=false;
					sb.append(Constants.DELIMITER_DOUBLE_PIPE);
					if (tierList != null) {
						for (LoyaltyProgramTier loyaltyProgramTier : tierList) {
							if (loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {
								if(loyaltyProgramTier.getRewardExpiryDateType() == null || loyaltyProgramTier.getRewardExpiryDateValue() == null){
									sb.append(tier + ",null");
								}
								else{
									sb.append(tier + Constants.DELIMETER_COMMA);
									sb.append(loyaltyProgramTier.getRewardExpiryDateType() + Constants.DELIMETER_COMMA);
									sb.append(loyaltyProgramTier.getRewardExpiryDateValue());
								}
								isExists = true;
							}
						}
					}
					if(!isExists) {
						sb.append(tier + ",null");
					}
				}
			}
			else if(rewardExpiryFlag == OCConstants.FLAG_NO){
				sb.append(Constants.DELIMITER_DOUBLE_PIPE + "null");
			}

			if(saveFlag){
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_LOYALTY_REWARD_VALIDITY, sb.toString(),
						(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				logger.debug("---------SB:::" + sb.toString());
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				LtySettingsActivityLogsDaoForDML activityLogDaoForDML = (LtySettingsActivityLogsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
				//activityLogDao.saveOrUpdate(activityLog);
				activityLogDaoForDML.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return sb;
	}

	public StringBuffer logLtyMembrshpValidityChanges(Long programId, Long userId, StringBuffer sb, boolean saveFlag, char memExpiryFlag, char mbrshipExpiryOnLevelUpgd) {
		if(sb == null) sb = new StringBuffer();
		try {
			LoyaltyProgram prgmObj = getProgmObj(programId);
			List<LoyaltyProgramTier> tierList = getTierList(programId);
			
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON + memExpiryFlag);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON + memExpiryFlag);

			if(memExpiryFlag == OCConstants.FLAG_YES) {
				sb.append(Constants.DELIMITER_DOUBLE_PIPE + mbrshipExpiryOnLevelUpgd);
				int noOfTiers=prgmObj.getNoOfTiers();
				for(int i = 1 ; i <= noOfTiers ; i++){
					String tier="Tier "+i;
					boolean isExists=false;
					sb.append(Constants.DELIMITER_DOUBLE_PIPE);
					if (tierList != null) {
						for (LoyaltyProgramTier loyaltyProgramTier : tierList) {
							if (loyaltyProgramTier.getTierType().equalsIgnoreCase(tier)) {
								if(loyaltyProgramTier.getMembershipExpiryDateType() == null || loyaltyProgramTier.getMembershipExpiryDateValue() == null){
									sb.append(tier + ",null");
								}
								else{
									sb.append(tier + Constants.DELIMETER_COMMA);
									sb.append(loyaltyProgramTier.getMembershipExpiryDateType() + Constants.DELIMETER_COMMA);
									sb.append(loyaltyProgramTier.getMembershipExpiryDateValue());
								}
								isExists = true;
							}
						}
					}
					if(!isExists) {
						sb.append(tier + ",null");
					}
				}
			}
			else if(memExpiryFlag == OCConstants.FLAG_NO){
				sb.append(Constants.DELIMITER_DOUBLE_PIPE + "null");
			}

			if(saveFlag){
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_LOYALTY_MEMBERSHIP_VALIDITY, sb.toString(),
						(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				LtySettingsActivityLogsDaoForDML activityLogDaoForDML = (LtySettingsActivityLogsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
				//activityLogDao.saveOrUpdate(activityLog);
				activityLogDaoForDML.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		logger.debug("---------SB:::" + sb.toString());
		return sb;
	}
	
	private StringBuffer logGiftAmtValidity(Long programId, Long userId, StringBuffer sb, boolean saveFlag, char giftAmountExpiryFlag) {
		if(sb == null) sb = new StringBuffer();
		try {
			LoyaltyProgram prgmObj = getProgmObj(programId);
			
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON + giftAmountExpiryFlag);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON + giftAmountExpiryFlag);

			if(giftAmountExpiryFlag == OCConstants.FLAG_YES) {
				sb.append(Constants.DELIMITER_DOUBLE_PIPE);
				sb.append(prgmObj.getGiftAmountExpiryDateType() + Constants.DELIMETER_COMMA);
				sb.append(prgmObj.getGiftAmountExpiryDateValue());
			}
			else if(giftAmountExpiryFlag == OCConstants.FLAG_NO){
				sb.append(Constants.DELIMITER_DOUBLE_PIPE + "null");
			}

			if(saveFlag){
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_GIFT_AMOUNT_VALIDITY, sb.toString(),
						(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				LtySettingsActivityLogsDaoForDML activityLogDaoForDML = (LtySettingsActivityLogsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
				//activityLogDao.saveOrUpdate(activityLog);
				activityLogDaoForDML.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		logger.debug("---------SB:::" + sb.toString());
		return sb;
	}
	

	private StringBuffer logGiftCardValidity(Long programId, Long userId, StringBuffer sb, boolean saveFlag, char giftMembrshpExpiryFlag) {
		if(sb == null) sb = new StringBuffer();
		try {
			LoyaltyProgram prgmObj = getProgmObj(programId);
			
			if (sb.length() > 0) sb.append(Constants.DELIMETER_DOUBLECOLON + giftMembrshpExpiryFlag);
			else sb.append("U"+Constants.DELIMETER_DOUBLECOLON + giftMembrshpExpiryFlag);

			if(giftMembrshpExpiryFlag == OCConstants.FLAG_YES) {
				sb.append(Constants.DELIMITER_DOUBLE_PIPE);
				sb.append(prgmObj.getGiftMembrshpExpiryDateType() + Constants.DELIMETER_COMMA);
				sb.append(prgmObj.getGiftMembrshpExpiryDateValue());
			}
			else if(giftMembrshpExpiryFlag == OCConstants.FLAG_NO){
				sb.append(Constants.DELIMITER_DOUBLE_PIPE + "null");
			}

			if(saveFlag){
				LtySettingsActivityLogs activityLog = new LtySettingsActivityLogs(userId, programId, Calendar.getInstance(),
						OCConstants.LTY_ACTIVITY_LOG_TYPE_GIFT_CARD_VALIDITY, sb.toString(),
						(prgmObj.getStatus().equalsIgnoreCase(OCConstants.LOYALTY_PROGRAM_STATUS_ACTIVE) ? OCConstants.FLAG_YES : OCConstants.FLAG_NO));
				LtySettingsActivityLogsDao activityLogDao = (LtySettingsActivityLogsDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO);
				LtySettingsActivityLogsDaoForDML activityLogDaoForDML = (LtySettingsActivityLogsDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LTY_SETTINGS_ACTIVITY_LOGS_DAO_FOR_DML);
				//activityLogDao.saveOrUpdate(activityLog);
				activityLogDaoForDML.saveOrUpdate(activityLog);
			}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		logger.debug("---------SB:::" + sb.toString());
		return sb;
	}

	public List<LoyaltyCardSet> findCardSetByTierLevel(int tierLevel, Long programId) {
		List<LoyaltyCardSet> cardSetList = null;
		try {
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			cardSetList = loyaltyCardSetDao.findCardSetByTierLevel(tierLevel, programId);
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return cardSetList;
	}

	public int findHighestTierLinkedToCardset(Long prgmId) {
		int linkedTierLevel = 0;
		try {
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			linkedTierLevel = loyaltyCardSetDao.findHighestTierLinkedToCardset(prgmId);
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return linkedTierLevel;
	}
	
	public LoyaltyCardSet getCardSetsByCardGenerationType(String cardGenType, Long prgmId){
		LoyaltyCardSet loyaltyCardSet =  null;
		try{
			loyaltyCardSetDao = (LoyaltyCardSetDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARD_SET_DAO);
			loyaltyCardSet = loyaltyCardSetDao.getCardSetsByCardGenerationType(cardGenType, prgmId);
		}catch(Exception e){
			logger.error("Exception :: ",e);
		}
		return loyaltyCardSet;
	}
	private void resetCardGenFlag(Long programId) {
		logger.info("Setting card gen flag N to the organisation...");
		try{
			LoyaltyProgramDao programDao = (LoyaltyProgramDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_DAO);
			LoyaltyProgram program = programDao.findById(programId);
			Long orgId = program.getOrgId();
			UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO); 
			UsersDaoForDML usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			UserOrganization userOrg = usersDao.findByOrgId(orgId);
			userOrg.setCardGenerateFlag(OCConstants.LOYALTY_CARD_GENERATION_FLAG_N);
			//usersDao.saveOrUpdate(userOrg);
			usersDaoForDML.saveOrUpdate(userOrg);
		} catch(Exception e) {
			logger.error("excetpion...", e);
			return;
		}
	}
	public int getTotalVisitBy(Long cardNumber, Long userId) {
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			return loyaltyTransactionChildDao.getTotalVisitBy(cardNumber,userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return 0;
	}
	public double getLifetimePurchesvalue(Long cardNumber, Long userId){
		try{
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			return loyaltyTransactionChildDao.getTotalLifeTimePurchaseValue(cardNumber,userId);
		} catch (Exception e) {
			logger.error("Exception::",e);
		}
		return 0.0;
		
	}
/*	
	// 2 records .. 2 ids
	public void saveEarnRules(Long id,Long tierId ,Long programId,
			String earnType, String earnValueType, Double earnValue,
			Double earnOnSpentAmount, Long userId,String ruleOn) {

		try {
			boolean isChanged = false;
			StringBuffer logDetails = null;
			LoyaltyProgramEarnRule earnRuleObj=null;
			loyaltyProgramEarnRuleDao=(LoyaltyProgramEarnRuleDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_PROGRAM_EARN_RULE_DAO);
			loyaltyProgramEarnRuleDaoForDML=(LoyaltyProgramEarnRuleDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_PROGRAM_EARN_RULE_DAO_FOR_DML);
			logger.info(""+programId+earnType+earnValueType+earnValue+earnOnSpentAmount);
			//List<LoyaltyProgramEarnRule> rulesObj = loyaltyProgramEarnRuleDao.fetchByTierId(programId, tierId);
			//for(LoyaltyProgramEarnRule tierObj : rulesObj){
			if(id != null) {
				earnRuleObj=loyaltyProgramEarnRuleDao.getEarnRuleById(id);
				if(!earnRuleObj.getEarnType().equalsIgnoreCase(earnType) || !earnRuleObj.getEarnValueType().equalsIgnoreCase(earnValueType) ||
						earnRuleObj.getEarnValue() != earnValue || earnRuleObj.getEarnOnSpentAmount() != earnOnSpentAmount ||
					(earnRuleObj.getRuleOn()!=null && ruleOn!=null && !earnRuleObj.getRuleOn().equalsIgnoreCase(ruleOn))){
					
					isChanged = true;
					logDetails = logTierChanges(programId, userId, null, false);
					
				}
			}
			else {
				isChanged = true;
				logDetails = logTierChanges(programId, userId, null, false);
				earnRuleObj = new LoyaltyProgramEarnRule();
			}

			if(isChanged){
				earnRuleObj.setTierId(tierId);
				earnRuleObj.setProgramId(programId);
				earnRuleObj.setEarnType(earnType);
				earnRuleObj.setEarnValueType(earnValueType);
				earnRuleObj.setEarnValue(earnValue);
				earnRuleObj.setEarnOnSpentAmount(earnOnSpentAmount);
				earnRuleObj.setRuleOn(ruleOn);
				logger.info(""+earnRuleObj);
				//loyaltyProgramTierDao.saveOrUpdate(tierObj);
				loyaltyProgramEarnRuleDaoForDML.saveOrUpdate(earnRuleObj);
				logTierChanges(programId, userId, logDetails, true);
			}
		//	}
		} catch (Exception e) {
			logger.error("Exception::",e);
		}

	}*/
	//Expiry Transaction //
	public int getExpiryTierTrans1(Long prgmId, String startDateStr, String endDateStr,Long userId, String transType) {
		// TODO Auto-generated method stub
		int ExpiryTierCount=0;
		try {
		//	loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			Object storeNo = null;
			loyaltyTransactionChildDao = (LoyaltyTransactionChildDao) ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			Object subsidiaryNo=null;
			ExpiryTierCount = (int) loyaltyTransactionChildDao.getExpiryTierTrans1(prgmId, startDateStr, endDateStr, userId, subsidiaryNo, storeNo);
		}catch (Exception e) {
			logger.error("Exception ::",e);
		
		
		
		}
		
		return ExpiryTierCount;
	}
}//EOF

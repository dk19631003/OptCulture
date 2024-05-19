package org.mq.optculture.business.helper;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.ContactsLoyalty;
import org.mq.marketer.campaign.beans.ContactsLoyaltyStage;
import org.mq.marketer.campaign.beans.SparkBaseCard;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDaoForDML;
import org.mq.marketer.campaign.dao.ContactsLoyaltyStageDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDao;
import org.mq.marketer.campaign.dao.SparkBaseCardDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.sparkbase.SparkBaseAdminService;
//import org.mq.marketer.sparkbase.SparkBaseService;
import org.mq.marketer.sparkbase.SparkBaseServiceAsync;
import org.mq.marketer.sparkbase.sparkbaseAdminWsdl.CardsViewResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ErrorMessageComponent;
import org.mq.marketer.sparkbase.transactionWsdl.InquiryResponse;
import org.mq.marketer.sparkbase.transactionWsdl.ResponseStandardHeaderComponent;
import org.mq.optculture.business.loyalty.SparkbaseCardFinder;
import org.mq.optculture.model.loyalty.EnrollResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

public class LoyaltyEnrollmentHelper {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Contacts ocContact;//OC saved contact
	private Contacts enrollContact;//Input contact data to sparkbase
	private String cardNumber;
	private String cardPin;
	private String posStoreLocationId;
	private String subsidiaryNumber;
	//private Long userId;
	private Users user;
	private SparkBaseLocationDetails sparkBaseLoc;
	private String cardType;
	private String empId;
	private String sourceMedium;
	private String mode;
	private String sourceType;
	
	 
	public LoyaltyEnrollmentHelper(Contacts ocContact, Contacts enrollContact, String cardNumber, String cardPin,
			String posStoreLocationId, String subsidiaryNumber,  Users user, SparkBaseLocationDetails sparkBaseLoc, String cardType, String empId, String mode, String sourceType){
		this.ocContact = ocContact;
		this.enrollContact = enrollContact;
		this.cardNumber = cardNumber;
		this.cardPin = cardPin;
		this.posStoreLocationId = posStoreLocationId;
		this.subsidiaryNumber = subsidiaryNumber;
		//this.userId = userId;
		this.user=user;
		this.sparkBaseLoc = sparkBaseLoc;
		this.cardType=cardType;
		this.empId=empId;
		this.mode = mode;
		this.sourceType = sourceType;
	}
	
	public EnrollResponse asynEnroll() throws Exception {
		logger.info("Entered enroll method>>>>");
		EnrollResponse enrollResponse = null;
		SparkBaseCard sparkBaseCard = null;
		//ContactsLoyalty contactLoyalty = null;
		if(sparkBaseLoc.isMobileUnique()) {
			//logger.debug(">>>>>>>>1");
			String mobile = enrollContact.getMobilePhone() ;
			if(mobile != null && !mobile.isEmpty()) {
				ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
				//List<ContactsLoyaltyStage> loyaltyStageList = contactsLoyaltyStageDao.findByPhone(mobile);
				List<ContactsLoyaltyStage> loyaltyStageList = contactsLoyaltyStageDao.findByPhoneAndUser(mobile,user.getUserName());
				//logger.debug(">>>>>>>>2");
				if(loyaltyStageList != null && loyaltyStageList.size() == 1) {
					//logger.debug(">>>>>>>>3");
					enrollResponse = findExistingLoyaltyByPhone(user.getUserId(),mobile);
					if("100105".equals(enrollResponse.getErrorCode())){
						//logger.debug(">>>>>>>>4");
						return enrollResponse;
					}/*else{
						
						//logger.debug(">>>>>>>>4-1");
						enrollResponse = new EnrollResponse();;
						enrollResponse.setErrorCode("100105");
						enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100105, OCConstants.ERROR_LOYALTY_FLAG));
						enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
						
						if("100105".equals(enrollResponse.getErrorCode())){
							//logger.debug(">>>>>>>>6");
							return enrollResponse;
						}
					}*/
					
				}else if(loyaltyStageList != null && loyaltyStageList.size() > 1){
					//logger.debug(">>>>>>>>5");
					enrollResponse = new EnrollResponse();;
					enrollResponse.setErrorCode("100105");
					enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100105, OCConstants.ERROR_LOYALTY_FLAG));
					enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
					
					if("100105".equals(enrollResponse.getErrorCode())){
						//logger.debug(">>>>>>>>6");
						return enrollResponse;
					}
					
				}
			}//if
			
		}//if
		
		//logger.debug(">>>>>>>>7");
		enrollResponse = findExistingLoyalty();
		if("100101".equals(enrollResponse.getErrorCode())){
			logger.info("Entered enroll method>>>>2");
			return enrollResponse;
		}
		
		//String[] cardStatus = null;
		if(cardNumber != null && cardNumber.trim().length() > 0){
			
			sparkBaseCard = findCardInOCRepository();
			//Not inventory in OC simply return to POS with failure
			if(sparkBaseCard != null && !sparkBaseCard.getStatus().equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY)){
				logger.info("Card status is not inventory");
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100120");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100120, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				logger.info("Exited enroll method >>>>>"+enrollResponse.getErrorMessage());
				return enrollResponse;
			}else if(sparkBaseCard != null && sparkBaseCard.getStatus().equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY)){
				 enrollResponse.setCardPin(sparkBaseCard.getCardPin());
				 sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_SELECTED);
				 SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
				 SparkBaseCardDaoForDML sparkBaseCardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASECARD_DAO_FOR_DML);
				 //sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
				 sparkBaseCardDaoForDML.saveOrUpdate(sparkBaseCard);
			}
			else if(sparkBaseCard == null){ //create new entry in sparkbasecard table
				//logger.info("Entered enroll method>>>>sparkBaseCard == null");
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("200009");
				enrollResponse.setErrorMessage("Given Card is Not Available. Please try with another card. Later, please inform Head Office about the error");
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				logger.info("Exited enroll method >>>>>"+enrollResponse.getErrorMessage());
				return enrollResponse;
			}
			//avoid this step
			//cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			//enrollResponse = preparePosCardStatus(cardStatus);
			if(enrollResponse != null){
				return enrollResponse;
			}
		}
		else{// card is not given in the request
			sparkBaseCard =  getInventoryCardAssync();
			if(sparkBaseCard == null) {
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100102");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100102, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				return enrollResponse;
			}else{
				logger.debug("card pin =====>"+sparkBaseCard.getCardPin());
				enrollResponse = new EnrollResponse();
				enrollResponse.setCardNumber(sparkBaseCard.getCardId()+"");
				enrollResponse.setCardPin(sparkBaseCard.getCardPin());
				enrollResponse.setErrorCode("0");
				enrollResponse.setErrorMessage("Successfully enrolled.");
				enrollResponse.setStoreLocationId(sparkBaseLoc.getLocationId());
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_SUCCESS);
				//enrollResponse.setCardPin(cardPin);
				return enrollResponse;
			}
			//avoid this step
			//cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			//enrollResponse = prepareOcCardStatus(cardStatus);
			
		}
		
		/*SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		
		if(cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY) && cardStatus[1].equals("114")){
			ContactsLoyalty contactLoyalty = prepareContactsLoyaltyObject(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			logger.info("enrollment call..."+contactLoyalty.getCardNumber()+" sbloc: "+sparkBaseLoc.getLocationId());
			
			Object enrollRespObj = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ENROLLMENT, sparkBaseLoc, contactLoyalty, enrollContact, null, true);
			enrollResponse = getEnrollStatus(enrollRespObj);
			if(enrollResponse != null){
				sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_INVENTORY);
				sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
				return enrollResponse;
			}
			
			if(sparkBaseCard.getCardPin() == null || sparkBaseCard.getCardPin().trim().isEmpty() ) {
				  Object cardresponseObj = SparkBaseAdminService.cardsView(String.valueOf(sparkBaseCard.getCardId()), sparkBaseCard);
				  if(cardresponseObj != null && cardresponseObj instanceof CardsViewResponse) {
					  logger.info("pin is set from SB through admin API ");
					  contactLoyalty.setCardPin(sparkBaseCard.getCardPin());
				  }
				  else{
					  logger.debug("got errors in cardView ");
				  }
			}//if 
			
			if(sparkBaseCard.getCardPin() == null || sparkBaseCard.getCardPin().trim().isEmpty() ) {
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100109");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100109, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				return enrollResponse;
			}
			
			sparkBaseCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
			sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
			if(sparkBaseCard.getActivationDate() == null ){
				sparkBaseCard.setActivationDate(Calendar.getInstance());
			}
			sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
			
			ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
			//logger.info("saving loyalty object..." + contactLoyalty.getCardNumber()+"__ "+contactLoyalty.getPosStoreLocationId()+"__"+contactLoyalty.getContact().getContactId());
			
			contactLoyalty.setMode(mode);
			contactLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
			contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			contactLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
			
			contactsLoyaltyDao.saveOrUpdate(contactLoyalty);
			
			enrollResponse = new EnrollResponse();
			enrollResponse.setCardNumber(String.valueOf(contactLoyalty.getCardNumber()));
			enrollResponse.setCardPin(contactLoyalty.getCardPin());
			enrollResponse.setErrorCode("0");
			enrollResponse.setErrorMessage("Successfully enrolled.");
			enrollResponse.setStoreLocationId(contactLoyalty.getLocationId());
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_SUCCESS);
			return enrollResponse;
		}*/
		return null;
	}
	public EnrollResponse enrollAsync() throws Exception {
		logger.info("Entered enrollAsync method>>>>");
		EnrollResponse enrollResponse = null;
		SparkBaseCard sparkBaseCard = null;
		//ContactsLoyalty contactLoyalty = null;
		
		/*if(sparkBaseLoc.isMobileUnique()) {
			
			String mobile = enrollContact.getMobilePhone() ;
			if(mobile != null && !mobile.isEmpty()) {
				ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
				ContactsLoyaltyStage loyaltyStage = contactsLoyaltyStageDao.findByPhone(mobile);
				if(loyaltyStage == null) {
					
					enrollResponse = findExistingLoyaltyByPhone(mobile);
					if("100105".equals(enrollResponse.getErrorCode())){
						return enrollResponse;
					}
					
				}else{
					
					enrollResponse = new EnrollResponse();;
					enrollResponse.setErrorCode("100105");
					enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100105, OCConstants.ERROR_LOYALTY_FLAG));
					enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
					
					if("100105".equals(enrollResponse.getErrorCode())){
						return enrollResponse;
					}
					
				}
			}//if
			
		}//if
*/		//else{
			enrollResponse = findExistingLoyalty();
			if("100101".equals(enrollResponse.getErrorCode())){
				logger.info("Entered enroll method>>>>2");
				return enrollResponse;
			}
		//}
		
		String[] cardStatus = null;
		if(cardNumber != null && cardNumber.trim().length() > 0){
			logger.info("Entered enroll method>>>>cardNumber != null");
			sparkBaseCard = findCardInOCRepository();
			//Not inventory in OC simply return to POS with failure
			if(sparkBaseCard != null && !sparkBaseCard.getStatus().equals(Constants.SPARKBASE_CARD_STATUS_SELECTED)){
				logger.info("Card status is not selected");
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100120");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100120, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				logger.info("Exited enroll method >>>>>"+enrollResponse.getErrorMessage());
				return enrollResponse;
			}
			else if(sparkBaseCard == null){ //create new entry in sparkbasecard table
				//logger.info("Entered enroll method>>>>sparkBaseCard == null");
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("200009");
				enrollResponse.setErrorMessage("Card Not Available, Upload Given Card To OptCulture.");
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				logger.info("Exited enroll method >>>>>"+enrollResponse.getErrorMessage());
				return enrollResponse;
			}
			//avoid this step
			cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			//logger.info("Entered enroll method>>>>cardStatus"+cardStatus[0] + " "+cardStatus[1]);
			enrollResponse = preparePosCardStatus(cardStatus);
			
			if(enrollResponse != null){
				logger.info("Entered enroll method>>>>enrollResponse != null");
				return enrollResponse;
			}
		}
		else{// card is not given in the request
			logger.info("Entered enroll method>>>>else");
			sparkBaseCard =  getInventoryCard();
			if(sparkBaseCard == null) {
				logger.info("Entered enroll method>>>>sparkBaseCard == null");
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100102");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100102, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				return enrollResponse;
			}
			//avoid this step sbloyalty enhancements
			/*cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			enrollResponse = prepareOcCardStatus(cardStatus);*/
			if(enrollResponse != null){
				logger.debug("card status response =====");
				return enrollResponse;
			}
		}
		
		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		SparkBaseCardDaoForDML sparkBaseCardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASECARD_DAO_FOR_DML);
		if(cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY) && cardStatus[1].equals("114")){
			ContactsLoyalty contactLoyalty = prepareContactsLoyaltyObject(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin(), sourceType);
			Object enrollRespObj = null;
			for(int i=1; i <=3; i++){
			logger.info("enrollment call..."+contactLoyalty.getCardNumber()+" sbloc: "+sparkBaseLoc.getLocationId());
			enrollRespObj = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ENROLLMENT, sparkBaseLoc, contactLoyalty, enrollContact, null, true);
			enrollResponse = getEnrollStatus(enrollRespObj);
			if (enrollResponse == null || Long.parseLong(enrollResponse.getErrorCode()) != 1) {
				break;
			}
			}
			if(enrollResponse != null){
				//sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_INVENTORY);
				//sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
				//sparkBaseCardDaoForDML.saveOrUpdate(sparkBaseCard);
				return enrollResponse;
			}
			
			if(sparkBaseCard.getCardPin() == null || sparkBaseCard.getCardPin().trim().isEmpty() ) {
				  Object cardresponseObj = SparkBaseAdminService.cardsView(String.valueOf(sparkBaseCard.getCardId()), sparkBaseCard);
				  if(cardresponseObj != null && cardresponseObj instanceof CardsViewResponse) {
					  logger.info("pin is set from SB through admin API ");
					  contactLoyalty.setCardPin(sparkBaseCard.getCardPin());
				  }
				  else{
					  logger.debug("got errors in cardView ");
				  }
			}//if 
			
			if(sparkBaseCard.getCardPin() == null || sparkBaseCard.getCardPin().trim().isEmpty() ) {
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100109");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100109, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				return enrollResponse;
			}
			
			sparkBaseCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
			sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
			if(sparkBaseCard.getActivationDate() == null ){
				sparkBaseCard.setActivationDate(Calendar.getInstance());
			}
			//sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
			sparkBaseCardDaoForDML.saveOrUpdate(sparkBaseCard);

			
			ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			//logger.info("saving loyalty object..." + contactLoyalty.getCardNumber()+"__ "+contactLoyalty.getPosStoreLocationId()+"__"+contactLoyalty.getContact().getContactId());
			
			contactLoyalty.setMode(mode);
			contactLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
			contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			contactLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
			logger.debug("saving loyalty object with phone==="+contactLoyalty.getMobilePhone());
			contactsLoyaltyDao.saveOrUpdate(contactLoyalty);
			
			enrollResponse = new EnrollResponse();
			enrollResponse.setCardNumber(String.valueOf(contactLoyalty.getCardNumber()));
			enrollResponse.setCardPin(contactLoyalty.getCardPin());
			enrollResponse.setErrorCode("0");
			enrollResponse.setErrorMessage("Successfully enrolled.");
			enrollResponse.setStoreLocationId(contactLoyalty.getLocationId());
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_SUCCESS);
			return enrollResponse;
		}
		return null;
	}
	
	public EnrollResponse enroll() throws Exception {
		logger.info("Entered enroll method>>>>");
		EnrollResponse enrollResponse = null;
		SparkBaseCard sparkBaseCard = null;
		//ContactsLoyalty contactLoyalty = null;
		
		if(sparkBaseLoc.isMobileUnique()) {
			
			String mobile = enrollContact.getMobilePhone() ;
			if(mobile != null && !mobile.isEmpty()) {
				ContactsLoyaltyStageDao contactsLoyaltyStageDao = (ContactsLoyaltyStageDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_STAGE_DAO);
				//List<ContactsLoyaltyStage> loyaltyStageList = contactsLoyaltyStageDao.findByPhone(mobile);
				List<ContactsLoyaltyStage> loyaltyStageList = contactsLoyaltyStageDao.findByPhoneAndUser(mobile,user.getUserName());
				logger.debug(">>>>>>>>2");
				if(loyaltyStageList != null && loyaltyStageList.size() == 1) {
					logger.debug(">>>>>>>>3");
					enrollResponse = findExistingLoyaltyByPhone(user.getUserId(),mobile);
					if("100105".equals(enrollResponse.getErrorCode())){
						logger.debug(">>>>>>>>4");
						return enrollResponse;
					}
					
				}else if(loyaltyStageList != null && loyaltyStageList.size() > 1){
					logger.debug(">>>>>>>>5");
					enrollResponse = new EnrollResponse();
					enrollResponse.setErrorCode("100105");
					enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100105, OCConstants.ERROR_LOYALTY_FLAG));
					enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
					
					if("100105".equals(enrollResponse.getErrorCode())){
						logger.debug(">>>>>>>>6");
						return enrollResponse;
					}
					
				}
			}//if
			
		}//if
	
		enrollResponse = findExistingLoyalty();
		if("100101".equals(enrollResponse.getErrorCode())){
			logger.info("Entered enroll method>>>>2");
			return enrollResponse;
		}
		
		
		String[] cardStatus = null;
		if(cardNumber != null && cardNumber.trim().length() > 0){
			
			sparkBaseCard = findCardInOCRepository();
			//Not inventory in OC simply return to POS with failure
			if(sparkBaseCard != null && !sparkBaseCard.getStatus().equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY)){
				logger.info("Card status is not inventory");
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100120");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100120, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				logger.info("Exited enroll method >>>>>"+enrollResponse.getErrorMessage());
				return enrollResponse;
			}
			else if(sparkBaseCard == null){ //create new entry in sparkbasecard table
			  sparkBaseCard = new SparkBaseCard(cardNumber, sparkBaseLoc);
			  sparkBaseCard.setCardType(Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
			  sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_INVENTORY);
			  sparkBaseCard.setCardPin(cardPin);
			  sparkBaseCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
			}
			//avoid this step
			cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			enrollResponse = preparePosCardStatus(cardStatus);
			if(enrollResponse != null){
				return enrollResponse;
			}
		}
		else{// card is not given in the request
			sparkBaseCard =  getInventoryCard();
			if(sparkBaseCard == null) {
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100102");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100102, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				return enrollResponse;
			}
			//avoid this step
			cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			enrollResponse = prepareOcCardStatus(cardStatus);
			if(enrollResponse != null){
				return enrollResponse;
			}
		}
		
		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		SparkBaseCardDaoForDML sparkBaseCardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASECARD_DAO_FOR_DML);
	
		if(cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY) && cardStatus[1].equals("114")){
			ContactsLoyalty contactLoyalty = prepareContactsLoyaltyObject(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin(), sourceType);
			logger.info("enrollment call..."+contactLoyalty.getCardNumber()+" sbloc: "+sparkBaseLoc.getLocationId());
			
			Object enrollRespObj = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.ENROLLMENT, sparkBaseLoc, contactLoyalty, enrollContact, null, true);
			enrollResponse = getEnrollStatus(enrollRespObj);
			if(enrollResponse != null){
				sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_INVENTORY);
				//sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
				sparkBaseCardDaoForDML.saveOrUpdate(sparkBaseCard);
				return enrollResponse;
			}
			
			if(sparkBaseCard.getCardPin() == null || sparkBaseCard.getCardPin().trim().isEmpty() ) {
				  Object cardresponseObj = SparkBaseAdminService.cardsView(String.valueOf(sparkBaseCard.getCardId()), sparkBaseCard);
				  if(cardresponseObj != null && cardresponseObj instanceof CardsViewResponse) {
					  logger.info("pin is set from SB through admin API ");
					  contactLoyalty.setCardPin(sparkBaseCard.getCardPin());
				  }
				  else{
					  logger.debug("got errors in cardView ");
				  }
			}//if 
			
			if(sparkBaseCard.getCardPin() == null || sparkBaseCard.getCardPin().trim().isEmpty() ) {
				enrollResponse = new EnrollResponse();
				enrollResponse.setErrorCode("100109");
				enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100109, OCConstants.ERROR_LOYALTY_FLAG));
				enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
				return enrollResponse;
			}
			
			sparkBaseCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
			sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
			if(sparkBaseCard.getActivationDate() == null ){
				sparkBaseCard.setActivationDate(Calendar.getInstance());
			}
			//sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
			sparkBaseCardDaoForDML.saveOrUpdate(sparkBaseCard);

			
			ContactsLoyaltyDaoForDML contactsLoyaltyDao = (ContactsLoyaltyDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			//logger.info("saving loyalty object..." + contactLoyalty.getCardNumber()+"__ "+contactLoyalty.getPosStoreLocationId()+"__"+contactLoyalty.getContact().getContactId());
			
			contactLoyalty.setMode(mode);
			contactLoyalty.setMembershipStatus(OCConstants.LOYALTY_MEMBERSHIP_STATUS_ACTIVE);
			contactLoyalty.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
			contactLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
			logger.debug("saving loyalty object with phone==="+contactLoyalty.getMobilePhone());
			contactsLoyaltyDao.saveOrUpdate(contactLoyalty);
			
			enrollResponse = new EnrollResponse();
			enrollResponse.setCardNumber(String.valueOf(contactLoyalty.getCardNumber()));
			enrollResponse.setCardPin(contactLoyalty.getCardPin());
			enrollResponse.setErrorCode("0");
			enrollResponse.setErrorMessage("Successfully enrolled.");
			enrollResponse.setStoreLocationId(contactLoyalty.getLocationId());
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_SUCCESS);
			return enrollResponse;
		}
		return null;
	}
	
	private EnrollResponse getEnrollStatus(Object object) throws Exception {
		logger.info("Entered getEnrollStatus method >>>>>");
		ErrorMessageComponent errMsg = null;
		EnrollResponse enrollResponse = null;
		if(object instanceof ErrorMessageComponent) {
			enrollResponse = new EnrollResponse();
			errMsg = (ErrorMessageComponent)object;
			logger.info("error msg is not null after enrollment "+errMsg);
			enrollResponse.setErrorCode(Long.parseLong(errMsg.getErrorCode())==1?"1":"100103");
			enrollResponse.setErrorMessage(errMsg.getBriefMessage()+" ( "+errMsg.getInDepthMessage() +" ) ");
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
			return enrollResponse;
		}
		logger.info("Exited getEnrollStatus method");
		return enrollResponse;
	}
	
	private EnrollResponse prepareOcCardStatus(String[] cardStatus) throws Exception {
		logger.info("Entered prepareOcCardStatus method >>>>>");
		EnrollResponse enrollResponse = null;
		if (cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY) && "100108".equals(cardStatus[1])){
			enrollResponse = new EnrollResponse();
			enrollResponse.setCardNumber(cardNumber);
			enrollResponse.setCardPin(cardPin);
			enrollResponse.setErrorCode("100108");
			enrollResponse.setErrorMessage(cardStatus[2]);
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
			return enrollResponse;
		}
		logger.info("Exited prepareOcCardStatus method >>>>>");
		return enrollResponse;
	}
	
	private EnrollResponse preparePosCardStatus(String[] cardStatus) throws Exception {
		logger.info("Entered preparePosCardStatus method >>>>>");
		EnrollResponse enrollResponse = null;
		if(cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_ACTIVATED)){
			enrollResponse = new EnrollResponse();
			enrollResponse.setCardNumber(cardNumber);
			enrollResponse.setCardPin(cardPin);
			enrollResponse.setErrorCode("100110");
			enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100110, OCConstants.ERROR_LOYALTY_FLAG));
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
			return enrollResponse;
		}
		else if (cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY) && cardStatus[1] != null && cardStatus[1].equals("100108")){
			enrollResponse = new EnrollResponse();
			enrollResponse.setCardNumber(cardNumber);
			enrollResponse.setCardPin(cardPin);
			enrollResponse.setErrorCode("100108");
			enrollResponse.setErrorMessage(cardStatus[2]);
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
			return enrollResponse;
		}
		logger.info("Exited preparePosCardStatus method >>>>>");
		return enrollResponse;
	}
	private SparkBaseCard getInventoryCardAssync() throws Exception {
		logger.info("Entered getInventoryCard method >>>>>");
		boolean flag = true;
		SparkBaseCard sparkBaseCard = null;
		String[] cardStatus = null;
		while(flag) {
			sparkBaseCard = findInventoryCardFromLocation(sparkBaseLoc.getSparkBaseLocationDetails_id().longValue(), Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
			
			if(sparkBaseCard == null) {
				break;
			}else {
				
				flag=false;
			}
			/*cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			if(cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY)) {
				flag = false;
			} */
			/*else {//Card status activated in sparkbase
				sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
				sparkBaseCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
				sparkBaseCard.setComments(Constants.SPARKBASE_CARD_COMMENTS_ACTIVATED_IN_SB);
				if(sparkBaseCard.getActivationDate() == null){
					sparkBaseCard.setActivationDate(Calendar.getInstance());	
				}
				SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO); 
				sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
			}*/
		}
		logger.info("Exited getInventoryCard method >>>>"+sparkBaseCard);
		return sparkBaseCard;
	}
	
	private SparkBaseCard getInventoryCard() throws Exception {
		logger.info("Entered getInventoryCard method >>>>>");
		boolean flag = true;
		SparkBaseCard sparkBaseCard = null;
		String[] cardStatus = null;
		while(flag) {
			sparkBaseCard = findInventoryCardFromLocation(sparkBaseLoc.getSparkBaseLocationDetails_id().longValue(), Constants.SPARKBASE_CARD_TYPE_PHYSICAL);
			if(sparkBaseCard == null) {
				break;
			}
			cardStatus = getCardStatusInSparkBase(sparkBaseCard.getCardId(), sparkBaseCard.getCardPin());
			if(cardStatus[0].equals(Constants.SPARKBASE_CARD_STATUS_INVENTORY)) {
				flag = false;
			} 
			else {//Card status activated in sparkbase
				sparkBaseCard.setStatus(Constants.SPARKBASE_CARD_STATUS_ACTIVATED);
				sparkBaseCard.setFromSource(Constants.SPARKBASE_CARD_FROMSOURCE_POS);
				sparkBaseCard.setComments(Constants.SPARKBASE_CARD_COMMENTS_ACTIVATED_IN_SB);
				if(sparkBaseCard.getActivationDate() == null){
					sparkBaseCard.setActivationDate(Calendar.getInstance());	
				}
				
				SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
				SparkBaseCardDaoForDML sparkBaseCardDaoForDML = (SparkBaseCardDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SPARKBASECARD_DAO_FOR_DML);
					
				//sparkBaseCardDao.saveOrUpdate(sparkBaseCard);
				sparkBaseCardDaoForDML.saveOrUpdate(sparkBaseCard);

			}
		}
		logger.info("Exited getInventoryCard method >>>>"+sparkBaseCard);
		return sparkBaseCard;
	}
	
	private String[] getCardStatusInSparkBase(String cardNumber, String cardPin) throws Exception {
		logger.info("Entered getCardStatusInSparkBase >>>>>");
		ErrorMessageComponent errorMsg = null;
		String[] status = new String[3];//0 -- card status , 1 -- errorcode , 2 -- errormessage
		
		ContactsLoyalty contactLoyalty = new ContactsLoyalty();
		contactLoyalty.setCardNumber(cardNumber+"");
		contactLoyalty.setCardPin(cardPin);
		Object object = SparkBaseServiceAsync.getInstance().fetchData(SparkBaseServiceAsync.INQUIRY, sparkBaseLoc, contactLoyalty, null,null, true);
		if(object instanceof ErrorMessageComponent) {
			errorMsg = (ErrorMessageComponent)object;
		}
		else if (object instanceof InquiryResponse){
			InquiryResponse response = (InquiryResponse)object;
			ResponseStandardHeaderComponent standardHeader = response.getStandardHeader();
			if (standardHeader.getStatus().equals("E")) {
		          errorMsg = response.getErrorMessage();
		    }
		}
		//Card status inventory
		if(errorMsg != null && Long.parseLong(errorMsg.getErrorCode()) == 114) {
			status[0] = Constants.SPARKBASE_CARD_STATUS_INVENTORY;
			status[1] = "114";
			logger.info("Exited getCardStatusInSparkBase >>>>>"+status[0]+status[1]);
			return status;
		} 
		else if (errorMsg != null && Long.parseLong(errorMsg.getErrorCode()) != 114) { // other error
			//logger.info("err msgcode is not 114 "+errorMsg);
			String msg = errorMsg.getBriefMessage();
			msg += " ( "+errorMsg.getInDepthMessage() +" ) ";
			status[0] = Constants.SPARKBASE_CARD_STATUS_INVENTORY;
			status[1] = "100108";
			status[2] = msg;
			logger.info("Exited getCardStatusInSparkBase >>>>>"+status[0]+status[1]);
			return status;
		}
		else {//Card status activated in sparkbase
			status[0] = Constants.SPARKBASE_CARD_STATUS_ACTIVATED;
			logger.info("Exited getCardStatusInSparkBase >>>>>"+status[0]);
			return status;
		}
	}
	
	
	private ContactsLoyalty prepareContactsLoyaltyObject(String cardNumber, String cardPin, String sourceType) throws Exception {
		//logger.info("Entered prepareContactsLoyaltyObject >>>>>");
		ContactsLoyalty contactLoyalty = new ContactsLoyalty();
		contactLoyalty.setCardNumber(cardNumber);
		contactLoyalty.setCreatedDate(Calendar.getInstance());
		contactLoyalty.setOptinDate(Calendar.getInstance());
		contactLoyalty.setCardPin(cardPin);
		contactLoyalty.setContact(ocContact);
		//contactLoyalty.setUserId(userId);
		contactLoyalty.setUserId(user.getUserId());
//		contactLoyalty.setOptinMedium(Constants.CONTACT_LOYALTY_TYPE_POS);
		contactLoyalty.setContactLoyaltyType(Constants.CONTACT_LOYALTY_TYPE_POS);
		contactLoyalty.setSourceType(sourceType);
		contactLoyalty.setCustomerId(ocContact.getExternalId());
		contactLoyalty.setLocationId(sparkBaseLoc.getLocationId());
		contactLoyalty.setPosStoreLocationId(posStoreLocationId);
		contactLoyalty.setSubsidiaryNumber(subsidiaryNumber);
		contactLoyalty.setCardType(cardType);
		contactLoyalty.setEmpId(empId);
		contactLoyalty.setMobilePhone(enrollContact.getMobilePhone());
		contactLoyalty.setServiceType(OCConstants.LOYALTY_SERVICE_TYPE_SB);
//		contactLoyalty.setOptinMedium(sourceMedium);
		return contactLoyalty;
	}
	
	/*private synchronized SparkBaseCard findInventoryCardFromLocation(Long sbLocId, String cardType) throws Exception{
		//logger.info("Entered findInventoryCardFromLocation >>>>>");
		SparkBaseCard sbcard = null;
		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		sbcard = sparkBaseCardDao.findAvailableCardByStore(sbLocId, cardType);
		if(sbcard != null){
			sbcard.setStatus(Constants.SPARKBASE_CARD_STATUS_SELECTED);
			sparkBaseCardDao.saveOrUpdate(sbcard);
		}
		//logger.info("Exited findInventoryCardFromLocation >>>>>"+sbcard);
		return sbcard;
	}*/
	
	private SparkBaseCard findInventoryCardFromLocation(Long sbLocId, String cardType) throws Exception{
	
		SparkbaseCardFinder cardFinder = new SparkbaseCardFinder(); 
		return cardFinder.findInventoryCardFromLocation(sbLocId, cardType);
	}
	private SparkBaseCard findCardInOCRepository() throws Exception {
		//logger.info("Entered findCardInOcRepository >>>>>>");
		SparkBaseCardDao sparkBaseCardDao = (SparkBaseCardDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASECARD_DAO);
		List<SparkBaseCard> sbCardList = sparkBaseCardDao.findByCardId(sparkBaseLoc.getSparkBaseLocationDetails_id(), cardNumber);
	    SparkBaseCard sbCard = null;
		if(sbCardList != null && sbCardList.size() > 0) {
			sbCard = sbCardList.get(0);
		}
		//logger.info("Exited findCardInOcRepository >>>>>"+sbCard);
		return sbCard;
	}
	
	private EnrollResponse findExistingLoyaltyByPhone(Long userId, String mobile) throws Exception {

		//logger.info("Entered findExistingLoyalty >>>>>");
		EnrollResponse enrollResponse = new EnrollResponse();;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListByMobile(userId,mobile);
		Iterator<ContactsLoyalty> listIter = loyaltyList.iterator();
		boolean loyaltyExist = false;
		ContactsLoyalty latestLoyalty = null;
		ContactsLoyalty currentLoyalty = null;
		
		while(listIter.hasNext()){
			currentLoyalty = listIter.next();
			if(latestLoyalty != null && latestLoyalty.getCreatedDate().after(currentLoyalty.getCreatedDate())){
				continue;
			}
			latestLoyalty = currentLoyalty;
			loyaltyExist = true;
		}
		
		if(loyaltyExist){
			enrollResponse.setErrorCode("100105");
			enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100105, OCConstants.ERROR_LOYALTY_FLAG));
			enrollResponse.setCardNumber(String.valueOf(latestLoyalty.getCardNumber()));
			enrollResponse.setCardPin(String.valueOf(latestLoyalty.getCardPin() == null ? "" :latestLoyalty.getCardPin()));
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
			
			latestLoyalty.setMode(mode);
			saveLoyaltyObject(latestLoyalty);
		}
		//logger.info("Exited findExistingLoyalty >>>>>"+enrollResponse.getCardNumber());
		return enrollResponse;
	
		
		
	}
	
	private EnrollResponse findExistingLoyalty() throws Exception {
		//logger.info("Entered findExistingLoyalty >>>>>");
		EnrollResponse enrollResponse = new EnrollResponse();;
		ContactsLoyaltyDao contactsLoyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		//List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListByContactId(ocContact.getContactId()); 
		//As a part of query tuning--based on new relic
		List<ContactsLoyalty> loyaltyList = contactsLoyaltyDao.findLoyaltyListBy(user.getUserId(),ocContact.getContactId());
		Iterator<ContactsLoyalty> listIter = loyaltyList.iterator();
		boolean loyaltyExist = false;
		ContactsLoyalty latestLoyalty = null;
		ContactsLoyalty currentLoyalty = null;
		
		while(listIter.hasNext()){
			currentLoyalty = listIter.next();
			if(latestLoyalty != null && latestLoyalty.getCreatedDate().after(currentLoyalty.getCreatedDate())){
				continue;
			}
			latestLoyalty = currentLoyalty;
			loyaltyExist = true;
		}
		
		if(loyaltyExist){
			enrollResponse.setErrorCode("100101");
			enrollResponse.setErrorMessage(PropertyUtil.getErrorMessage(100101, OCConstants.ERROR_LOYALTY_FLAG));
			enrollResponse.setCardNumber(String.valueOf(latestLoyalty.getCardNumber()));
			enrollResponse.setCardPin(String.valueOf(latestLoyalty.getCardPin() == null ? "" :latestLoyalty.getCardPin()));
			enrollResponse.setStatus(OCConstants.LOYALTY_ENROLL_STATUS_FAILURE);
			
			latestLoyalty.setMode(mode);
			saveLoyaltyObject(latestLoyalty);
		}
		logger.info("Exited findExistingLoyalty >>>>>"+enrollResponse.getCardNumber());
		return enrollResponse;
	}
	
	private void saveLoyaltyObject(ContactsLoyalty latestLoyalty) {
		try{
			ContactsLoyaltyDaoForDML loyaltyDao = (ContactsLoyaltyDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.CONTACTS_LOYALTY_DAO_FOR_DML);
			loyaltyDao.saveOrUpdate(latestLoyalty);
		}catch(Exception e){
			logger.error("Exception in saving loyalty object", e);
		}
	}
}

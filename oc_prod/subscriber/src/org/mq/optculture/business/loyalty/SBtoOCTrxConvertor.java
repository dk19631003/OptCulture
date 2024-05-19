package org.mq.optculture.business.loyalty;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.LoyaltyCards;
import org.mq.marketer.campaign.beans.LoyaltyTransaction;
import org.mq.marketer.campaign.beans.LoyaltyTransactionChild;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.LoyaltyCardsDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDao;
import org.mq.optculture.data.dao.LoyaltyTransactionChildDaoForDML;
import org.mq.optculture.model.loyalty.Balances;
import org.mq.optculture.model.loyalty.LoyaltyEnrollJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyEnrollRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyInquiryJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyInquiryRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyInquiryResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceRequestObject;
import org.mq.optculture.model.loyalty.LoyaltyIssuanceResponseObject;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonRequest;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionJsonResponse;
import org.mq.optculture.model.loyalty.LoyaltyRedemptionResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

import com.google.gson.Gson;

public class SBtoOCTrxConvertor extends Thread {
	
	public static void main(String[] args) {
		
		logger.debug("enter json");
		Gson gson = new Gson();
		Scanner sc = new Scanner(System.in);
		String json = sc.nextLine();
		LoyaltyEnrollJsonRequest request = gson.fromJson(json, LoyaltyEnrollJsonRequest.class);
			if(request.getENROLLMENTREQ()==null){
				request =  new LoyaltyEnrollJsonRequest();
				LoyaltyEnrollRequestObject l = gson.fromJson(json, LoyaltyEnrollRequestObject.class);
				request.setENROLLMENTREQ(l);
			}
//			/LoyaltyCardsDao cardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			//LoyaltyCards loyaltyCards = cardsDao.findByCardNoAnduserId(loyaltyTransaction.getCardNumber(),userId);
			
			LoyaltyTransactionChild	transaction = new LoyaltyTransactionChild();
			/*transaction.setTransactionId(loyaltyTransaction.getId());
			transaction.setMembershipNumber(""+loyaltyTransaction.getCardNumber());
			transaction.setMembershipType("Card");
			transaction.setCreatedDate(loyaltyTransaction.getRequestDate());
			transaction.setOrgId(loyaltyCards.getOrgId());
			transaction.setProgramId(loyaltyCards.getProgramId());
			//transaction.setTierId(tierId);
			transaction.setUserId(userId);*/
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
			transaction.setStoreNumber(request.getENROLLMENTREQ().getHEADERINFO().getSTORENUMBER());
			transaction.setEmployeeId(request.getENROLLMENTREQ().getENROLLMENTINFO().getEMPID()!=null && !request.getENROLLMENTREQ().getENROLLMENTINFO().getEMPID().trim().isEmpty() ? request.getENROLLMENTREQ().getENROLLMENTINFO().getEMPID().trim():null);
			//transaction.setTerminalId(loyaltyTransaction.getTerminalId()!=null && !loyaltyTransaction.getTerminalId().trim().isEmpty() ? loyaltyTransaction.getTerminalId().trim():null);
	}
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private LoyaltyTransaction loyaltyTransaction;
	//Long programId;
	//Long tierId;
	Long userId;
	
	public SBtoOCTrxConvertor(LoyaltyTransaction loyaltyTransaction, Long userId){
		this.loyaltyTransaction = loyaltyTransaction;
		//this.programId = programId;
		//this.tierId = tierId;
		this.userId = userId;
		
	}
	
	private LoyaltyTransactionChild enroll(){
			
		try{
			LoyaltyTransactionChild transaction = null;
			Gson gson = new Gson();
			
			LoyaltyEnrollJsonRequest request = gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyEnrollJsonRequest.class);
				if(request.getENROLLMENTREQ()==null){
					request =  new LoyaltyEnrollJsonRequest();
					request.setENROLLMENTREQ(gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyEnrollRequestObject.class));
				}
				LoyaltyCardsDao cardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
				//LoyaltyCards loyaltyCards = cardsDao.findByCardNoAnduserId(loyaltyTransaction.getCardNumber(),userId);
				
				transaction = new LoyaltyTransactionChild();
				transaction.setTransactionId(loyaltyTransaction.getId());
				transaction.setMembershipNumber(""+loyaltyTransaction.getCardNumber());
				transaction.setMembershipType("Card");
				transaction.setCreatedDate(loyaltyTransaction.getRequestDate());
				/*transaction.setOrgId(loyaltyCards.getOrgId());
				transaction.setProgramId(loyaltyCards.getProgramId());*/
				//transaction.setTierId(tierId);
				transaction.setUserId(userId);
				transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ENROLLMENT);
				transaction.setStoreNumber(request.getENROLLMENTREQ().getHEADERINFO().getSTORENUMBER() != null && !request.getENROLLMENTREQ().getHEADERINFO().getSTORENUMBER().trim().isEmpty() ? request.getENROLLMENTREQ().getHEADERINFO().getSTORENUMBER() : null);
				transaction.setEmployeeId(request.getENROLLMENTREQ().getENROLLMENTINFO().getEMPID()!=null && !request.getENROLLMENTREQ().getENROLLMENTINFO().getEMPID().trim().isEmpty() ? request.getENROLLMENTREQ().getENROLLMENTINFO().getEMPID().trim():null);
				transaction.setTerminalId(loyaltyTransaction.getTerminalId()!=null && !loyaltyTransaction.getTerminalId().trim().isEmpty() ? loyaltyTransaction.getTerminalId().trim():null);
				//transaction.setDocSID(docSID);
				//transaction.setCardSetId(loyaltyCards.getCardSetId());
				transaction.setSourceType(request.getENROLLMENTREQ().getHEADERINFO().getSOURCETYPE() != null &&
						!request.getENROLLMENTREQ().getHEADERINFO().getSOURCETYPE().trim().isEmpty() ? request.getENROLLMENTREQ().getHEADERINFO().getSOURCETYPE() : null);
				transaction.setDescription("loyaltyEnroll");
				//transaction.setCustomerId(request.getENROLLMENTREQ().getCUSTOMERINFO().getCUSTOMERID());
				//transaction.setLoyaltyId(loyaltyCards.getMembershipId());
				LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
				loyaltyTransChildDaoForDML.saveOrUpdate(transaction);
				return transaction;
			
			}catch(Exception e){
				logger.error("Exception while creating transaction in child table..."+loyaltyTransaction.getId(),e);
				
				
			}
			return null;
			
		
	}
	
	public LoyaltyTransactionChild issuance(){
		
		

		try{
		
		LoyaltyTransactionChild transaction = null;
		Gson gson = new Gson();
		LoyaltyIssuanceJsonRequest request = gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyIssuanceJsonRequest.class);

			if(request.getLOYALTYISSUANCEREQ()==null){
				request =  new LoyaltyIssuanceJsonRequest();
				request.setLOYALTYISSUANCEREQ(gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyIssuanceRequestObject.class));
			}
			LoyaltyCardsDao cardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			//LoyaltyCards loyaltyCards = cardsDao.findByCardNoAnduserId(loyaltyTransaction.getCardNumber(),userId);
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(loyaltyTransaction.getId());
			transaction.setMembershipNumber(""+loyaltyTransaction.getCardNumber());
			transaction.setMembershipType("Card");
			//transaction.setCardSetId(loyaltyCards.getCardSetId());
			
			transaction.setCreatedDate(loyaltyTransaction.getRequestDate());
			LoyaltyIssuanceJsonResponse response  = null;
			if(loyaltyTransaction.getMode().equalsIgnoreCase("online")){
				response  = gson.fromJson(loyaltyTransaction.getJsonResponse(), LoyaltyIssuanceJsonResponse.class);
			}else if(loyaltyTransaction.getMode().equalsIgnoreCase("offline")){
				response = new LoyaltyIssuanceJsonResponse();
				response.setLOYALTYISSUANCERESPONSE(gson.fromJson(loyaltyTransaction.getJsonResponse(), LoyaltyIssuanceResponseObject.class));
			}
			
			
			String amt = null;
			String pts = null;
			String amtDiff = "0";
			String ptsDiff = "0";
			for(Balances bal : response.getLOYALTYISSUANCERESPONSE().getBALANCES()){
				if(bal.getVALUECODE().equalsIgnoreCase("Points")){
					
					pts = bal.getAMOUNT();
					ptsDiff = bal.getDIFFERENCE();
					
				}else if(bal.getVALUECODE().equalsIgnoreCase("USD")){
					amt = bal.getAMOUNT();
					amtDiff = bal.getDIFFERENCE();
					
				}
			}
			
			transaction.setAmountDifference(amtDiff);
			transaction.setPointsDifference(ptsDiff);
			
			if(amt!=null&&pts!=null){
				transaction.setEarnType("Amount");
			}else{
				transaction.setEarnType("Points");
			}
		//	earnedValue = 
			if(transaction.getEarnType().equals(OCConstants.LOYALTY_TYPE_POINTS)){
				if(!ptsDiff.isEmpty())transaction.setEarnedPoints(Double.valueOf(ptsDiff));
				//transaction.setNetEarnedPoints((double)earnedValue);
			}
			else if(transaction.getEarnType().equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
				if(!amtDiff.isEmpty())transaction.setEarnedAmount(Double.valueOf(amtDiff));
				//transaction.setNetEarnedAmount((double)earnedValue);
			}
			
			transaction.setEarnStatus("Processed");
			transaction.setEnteredAmount(Double.valueOf(request.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getENTEREDAMOUNT()));
			transaction.setExcludedAmount(new Double(0));
			transaction.setEnteredAmountType("Purchase");
			//transaction.setOrgId(loyaltyCards.getOrgId());
			if(pts!=null && !pts.isEmpty())transaction.setPointsBalance(Double.valueOf(pts));
			if(amt!=null && !amt.isEmpty())transaction.setAmountBalance(Double.valueOf(amt));
			//transaction.setGiftBalance(loyalty.getGiftBalance());
			//transaction.setProgramId(loyaltyCards.getProgramId());
			//transaction.setTierId(loyalty.getProgramTierId());
			transaction.setUserId(userId);
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE);
			transaction.setStoreNumber(request.getLOYALTYISSUANCEREQ().getHEADERINFO().getSTORENUMBER() != null && !request.getLOYALTYISSUANCEREQ().getHEADERINFO().getSTORENUMBER().trim().isEmpty() ? request.getLOYALTYISSUANCEREQ().getHEADERINFO().getSTORENUMBER().trim() : null);
			transaction.setEmployeeId(request.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getEMPID()!=null && !request.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getEMPID().trim().isEmpty() ? request.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getEMPID().trim():null);
			transaction.setTerminalId(loyaltyTransaction.getTerminalId()!=null && !loyaltyTransaction.getTerminalId().trim().isEmpty() ? loyaltyTransaction.getTerminalId().trim():null);
			transaction.setDocSID(request.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getDOCSID());
			//transaction.setDescription(conversionRate);
			transaction.setConversionAmt(new Double(0));
			//transaction.setSource(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_STORE);
			
			transaction.setSourceType(request.getLOYALTYISSUANCEREQ().getHEADERINFO().getSOURCETYPE() != null && 
					!request.getLOYALTYISSUANCEREQ().getHEADERINFO().getSOURCETYPE().trim().isEmpty() ? 
							request.getLOYALTYISSUANCEREQ().getHEADERINFO().getSOURCETYPE() : null);
			
			
			
			
			//transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
			//transaction.setLoyaltyId(loyaltyCards.getMembershipId());
			//transaction.setCustomerId(request.getLOYALTYISSUANCEREQ().getISSUANCEINFO().getCUSTOMERID());
			LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransChildDaoForDML.saveOrUpdate(transaction);
			
			return transaction;
			
			//logger.debug("Issuance Transaction Id:::"+transaction.getTransChildId());
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction..."+loyaltyTransaction.getId(),e);
		}
	
		return null;
		
		
	}
	
	private LoyaltyTransactionChild redemption(){
		
logger.debug("===entered===redemption====");
		try{
		
		LoyaltyTransactionChild transaction = null;
		
		Gson gson = new Gson();
		//LoyaltyRedemptionRequestObject request = gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyRedemptionRequestObject.class);
			
			LoyaltyCardsDao cardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			//LoyaltyCards loyaltyCards = cardsDao.findByCardNoAnduserId(loyaltyTransaction.getCardNumber(),userId);
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(loyaltyTransaction.getId());
			
			transaction.setMembershipNumber(""+loyaltyTransaction.getCardNumber());
			transaction.setMembershipType("Card");
			
			transaction.setCreatedDate(loyaltyTransaction.getRequestDate());
			LoyaltyRedemptionJsonRequest request = null;
			if(loyaltyTransaction.getJsonRequest() != null) request = gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyRedemptionJsonRequest.class);
			LoyaltyRedemptionJsonResponse response  = null;
			if(loyaltyTransaction.getMode().equalsIgnoreCase("online")){
				response  = gson.fromJson(loyaltyTransaction.getJsonResponse(), LoyaltyRedemptionJsonResponse.class);
			}else if(loyaltyTransaction.getMode().equalsIgnoreCase("offline")){
				response = new LoyaltyRedemptionJsonResponse();
				response.setLOYALTYREDEMPTIONRESPONSE(gson.fromJson(loyaltyTransaction.getJsonResponse(), LoyaltyRedemptionResponseObject.class));
			}
			
			String amt = null;
			String pts = null;
			String amtDiff = "0";
			String ptsDiff = "0";
			for(Balances bal : response.getLOYALTYREDEMPTIONRESPONSE().getBALANCES()){
				if(bal.getVALUECODE().equalsIgnoreCase("Points")){
					
					pts = bal.getAMOUNT();
					ptsDiff = bal.getDIFFERENCE();
					
				}else if(bal.getVALUECODE().equalsIgnoreCase("USD")){
					amt = bal.getAMOUNT();
					amtDiff = bal.getDIFFERENCE();
					
				}
			}
			
			
			transaction.setPointsDifference(ptsDiff);
			transaction.setAmountDifference(amtDiff);
			//transaction.setGiftDifference(giftDiff);
			
			if(response.getLOYALTYREDEMPTIONRESPONSE().getREDEMPTIONINFO().getENTEREDAMOUNT() != null && !response.getLOYALTYREDEMPTIONRESPONSE().getREDEMPTIONINFO().getENTEREDAMOUNT().trim().isEmpty()){
				transaction.setEnteredAmount(Double.valueOf(response.getLOYALTYREDEMPTIONRESPONSE().getREDEMPTIONINFO().getENTEREDAMOUNT().trim()));
				transaction.setEnteredAmountType("AmountRedeem");
			}
			
			transaction.setUserId(userId);
			//transaction.setOrgId(loyaltyCards.getOrgId());
			
			if(pts!=null && !pts.isEmpty())transaction.setPointsBalance(Double.valueOf(pts));
			if(amt!=null && !amt.isEmpty())transaction.setAmountBalance(Double.valueOf(amt));
			//transaction.setGiftBalance(loyalty.getGiftBalance());
			
			//transaction.setProgramId(loyaltyCards.getProgramId());
			//transaction.setTierId(loyalty.getProgramTierId());
			//transaction.setCardSetId(loyaltyCards.getCardSetId());
			
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_REDEMPTION);
			if(request != null){
				transaction.setStoreNumber(request.getLOYALTYREDEMPTIONREQ().getHEADERINFO().getSTORENUMBER() != null && 
						!request.getLOYALTYREDEMPTIONREQ().getHEADERINFO().getSTORENUMBER().trim().isEmpty() ? request.getLOYALTYREDEMPTIONREQ().getHEADERINFO().getSTORENUMBER() : null);
				transaction.setSourceType(request.getLOYALTYREDEMPTIONREQ().getHEADERINFO().getSOURCETYPE() != null && 
						!request.getLOYALTYREDEMPTIONREQ().getHEADERINFO().getSOURCETYPE().trim().isEmpty() ? request.getLOYALTYREDEMPTIONREQ().getHEADERINFO().getSOURCETYPE() : null);
			}
			transaction.setEmployeeId(response.getLOYALTYREDEMPTIONRESPONSE().getREDEMPTIONINFO().getEMPID()!=null && !response.getLOYALTYREDEMPTIONRESPONSE().getREDEMPTIONINFO().getEMPID().trim().isEmpty() ? response.getLOYALTYREDEMPTIONRESPONSE().getREDEMPTIONINFO().getEMPID().trim():null);
			transaction.setTerminalId(loyaltyTransaction.getTerminalId()!=null && !loyaltyTransaction.getTerminalId().trim().isEmpty() ? loyaltyTransaction.getTerminalId().trim():null);
			//transaction.setDocSID(request.getHEADERINFO().get);
			/*try{
			transaction.setCustomerId(response.getLOYALTYREDEMPTIONRESPONSE().getCUSTOMERINFO().getCUSTOMERID());
			}catch (Exception e) {
				transaction.setCustomerId(response.getLOYALTYREDEMPTIONRESPONSE().getREDEMPTIONINFO().getCUSTOMERID());
			}*/
			//transaction.setDescription();
			transaction.setConversionAmt(new Double(0));
			//transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
//			transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
			//transaction.setLoyaltyId(loyaltyCards.getMembershipId());
			
			LoyaltyTransactionChildDaoForDML loyaltyTransactionChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransactionChildDaoForDML.saveOrUpdate(transaction);
			
			//Event Trigger sending part
			
			return transaction;
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction..."+loyaltyTransaction.getId(),e);//2746010

		}
		return null;
	}
	
	private LoyaltyTransactionChild inquiry(){
		
		try{
		LoyaltyTransactionChild transaction = null;
		
		Gson gson = new Gson();
		LoyaltyInquiryJsonRequest request = gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyInquiryJsonRequest.class);
			if(request.getLOYALTYINQUIRYREQ()==null){
				request =  new LoyaltyInquiryJsonRequest();
				request.setLOYALTYINQUIRYREQ(gson.fromJson(loyaltyTransaction.getJsonRequest(), LoyaltyInquiryRequestObject.class));
			}
			LoyaltyCardsDao cardsDao = (LoyaltyCardsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_CARDS_DAO);
			//LoyaltyCards loyaltyCards = cardsDao.findByCardNoAnduserId(loyaltyTransaction.getCardNumber(),userId);
			
			
			transaction = new LoyaltyTransactionChild();
			transaction.setTransactionId(loyaltyTransaction.getId());
			transaction.setMembershipNumber(""+loyaltyTransaction.getCardNumber());
			transaction.setMembershipType("Card");
			//transaction.setCardSetId(loyaltyCards.getCardSetId());
			//transaction.setTierId(loyalty.getProgramTierId());
			transaction.setCreatedDate(loyaltyTransaction.getRequestDate());
		transaction.setUserId(userId);
			//transaction.setOrgId(loyaltyCards.getOrgId());
			
			LoyaltyInquiryJsonResponse response  = null;
			if(loyaltyTransaction.getMode().equalsIgnoreCase("online")){
				response  = gson.fromJson(loyaltyTransaction.getJsonResponse(), LoyaltyInquiryJsonResponse.class);
			}else if(loyaltyTransaction.getMode().equalsIgnoreCase("offline")){
				response = new LoyaltyInquiryJsonResponse();
				response.setLOYALTYINQUIRYRESPONSE(gson.fromJson(loyaltyTransaction.getJsonResponse(), LoyaltyInquiryResponseObject.class));
			}
			String amt = null;
			String pts = null;
			String amtDiff = "0";
			String ptsDiff = "0";
			for(Balances bal : response.getLOYALTYINQUIRYRESPONSE().getBALANCES()){
				if(bal.getVALUECODE().equalsIgnoreCase("Points")){
					
					pts = bal.getAMOUNT();
					ptsDiff = bal.getDIFFERENCE();
					
				}else if(bal.getVALUECODE().equalsIgnoreCase("USD")){
					amt = bal.getAMOUNT();
					amtDiff = bal.getDIFFERENCE();
					
				}
			}
			
			if(pts!=null && !pts.isEmpty())transaction.setPointsBalance(Double.valueOf(pts));
			if(amt!=null && !amt.isEmpty())transaction.setAmountBalance(Double.valueOf(amt));
			//transaction.setGiftBalance(loyalty.getGiftBalance());
		//	transaction.setProgramId(loyaltyCards.getProgramId());
			transaction.setStoreNumber(request.getLOYALTYINQUIRYREQ().getHEADERINFO().getSTORENUMBER() != null && 
					!request.getLOYALTYINQUIRYREQ().getHEADERINFO().getSTORENUMBER().trim().isEmpty() ? request.getLOYALTYINQUIRYREQ().getHEADERINFO().getSTORENUMBER() : null);
			transaction.setEmployeeId(request.getLOYALTYINQUIRYREQ().getINQUIRYINFO().getEMPID()!=null && !request.getLOYALTYINQUIRYREQ().getINQUIRYINFO().getEMPID().trim().isEmpty() ? request.getLOYALTYINQUIRYREQ().getINQUIRYINFO().getEMPID().trim():null);
			transaction.setTerminalId(loyaltyTransaction.getTerminalId()!=null && !loyaltyTransaction.getTerminalId().trim().isEmpty() ? loyaltyTransaction.getTerminalId().trim():null);
			
			//transaction.setDocSID(request.getHEADERINFO().getDocSID());
			transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_INQUIRY);
			//transaction.setSourceType(inquiryRequest.getHeader().getSourceType());
			//transaction.setLoyaltyId(loyaltyCards.getMembershipId());
			//transaction.setCustomerId(response.getLOYALTYINQUIRYRESPONSE().getCUSTOMERINFO().getCUSTOMERID());

			LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
			loyaltyTransChildDaoForDML.saveOrUpdate(transaction);
			
			return transaction;
		}catch(Exception e){
			logger.error("Exception while logging enroll transaction..."+loyaltyTransaction.getId(),e);
		}
	return null;
	}
	static long  connections =0;
	static List<LoyaltyTransactionChild>  list = new ArrayList<LoyaltyTransactionChild>();
	@Override
	public void run() {
		logger.debug("entered ====trx migrator ===");
		try {
			connections +=1;
			//check if the transaction is already existing
			
			/*LoyaltyTransactionChildDao loyaltyTransactionChildDao = (LoyaltyTransactionChildDao)ServiceLocator.getInstance().getDAOByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO);
			
			List<LoyaltyTransactionChild> retList = loyaltyTransactionChildDao.executeQuery("FROM LoyaltyTransactionChild WHERE userId="+userId+" AND transactionId="+loyaltyTransaction.getId());
			if(retList != null && !retList.isEmpty()){
				
				logger.debug("===Found a record for this transaction==="+loyaltyTransaction.getId());

				return;
			}*/
					
					//synchronized(loyaltyTransaction.getCardNumber().toString()){
				if(loyaltyTransaction.getType().equalsIgnoreCase("Enrolment")){
					LoyaltyTransactionChild tr = enroll();
					if(tr!=null){
					list.add(tr);
					}else{
						logger.debug("Not have trx for "+loyaltyTransaction.getId());
					}
					
				}else if(loyaltyTransaction.getType().equalsIgnoreCase("Issuance")){
					LoyaltyTransactionChild tr =  issuance();
					if(tr!=null){
						list.add(tr);
						}else{
							logger.debug("Not have trx for "+loyaltyTransaction.getId());
						}
				}else if(loyaltyTransaction.getType().equalsIgnoreCase("Redemption")){
					LoyaltyTransactionChild tr =  redemption();
					if(tr!=null){
						list.add(tr);
						}else{
							logger.debug("Not have trx for "+loyaltyTransaction.getId());
						}
				}else{
					LoyaltyTransactionChild tr =  inquiry();
					if(tr!=null){
						list.add(tr);
						}else{
							logger.debug("Not have trx for "+loyaltyTransaction.getId());
						}
				}
				
				connections -=1;
			//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ===", e);
		}
	}
	
	public static void save() throws Exception{
		while(true){
			
			if(connections==0){
				LoyaltyTransactionChildDaoForDML loyaltyTransChildDaoForDML = (LoyaltyTransactionChildDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.LOYALTY_TRANSACTION_CHILD_DAO_FOR_DML);
				loyaltyTransChildDaoForDML.saveByCollection(list);
				list.clear();
				break;
				
			}
		}
	}

}

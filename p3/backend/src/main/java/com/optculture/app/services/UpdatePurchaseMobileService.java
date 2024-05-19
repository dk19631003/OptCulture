package com.optculture.app.services;

import com.optculture.app.dto.updatePurchaseMobile.UpdateMobileRequestDTO;
import com.optculture.app.dto.updatePurchaseMobile.UpdateMobileResponseDTO;
import com.optculture.app.repositories.*;
import com.optculture.shared.entities.communication.ereceipt.DigitalReceiptsJSON;
import com.optculture.shared.entities.contact.ContactLoyalty;
import com.optculture.shared.entities.loyalty.LoyaltyBalance;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionChild;
import com.optculture.shared.entities.org.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdatePurchaseMobileService {

    Logger logger = LoggerFactory.getLogger(UpdatePurchaseMobileService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContactLoyaltyRepository contactsLoyaltyRepository;

    @Autowired
    DigitalReceiptJSONRepository digitalReceiptsJSONRepository;

    @Autowired
    LoyaltyBalanceRepository loyaltyBalanceRepository;

    @Autowired
    LoyaltyTransactionChildRepository loyaltyTransactionChildRepository;
    public UpdateMobileResponseDTO updateMobile(UpdateMobileRequestDTO updateMobileRequestDTO,User currentUser) {

        if(updateMobileRequestDTO==null) return new UpdateMobileResponseDTO("Missing Mandate Fields");
        Long userId = currentUser.getUserId();
        logger.info("user Id "+userId);
        String membershipNumber = updateMobileRequestDTO.getWrongMembership();
        String receiptNumber = updateMobileRequestDTO.getReceiptNumber();
        String docsId = updateMobileRequestDTO.getDocsId();
        if(receiptNumber.isEmpty()) return new UpdateMobileResponseDTO("Required Receipt Number to Process");
        logger.info("update mobile "+membershipNumber+" receipt "+receiptNumber);
        List<LoyaltyTransactionChild> trxList = loyaltyTransactionChildRepository.findByUserIdAndMembershipNumberAndReceiptNumberAndDocSID(userId,membershipNumber,receiptNumber,docsId);
        logger.info("trx list size "+trxList.size());
        updateDigitalReceiptJSON(userId,updateMobileRequestDTO.getDocsId(),updateMobileRequestDTO.getCorrectMobile());// update mobile number in req json and change status to "New"
        calculateBalances(userId,trxList,updateMobileRequestDTO);//deduct or add balances and amounts for the wrong mobile number
        revertTransactions(userId,membershipNumber,receiptNumber,docsId);// delete the transaction of wrong membership number
        return new UpdateMobileResponseDTO("updated");
    }

    private void updateDigitalReceiptJSON(Long userId,String docsId,String mobilePhone) {
        ContactLoyalty contactsLoyalty = contactsLoyaltyRepository.findFirstByUserIdAndMobilePhone(userId,mobilePhone);
        DigitalReceiptsJSON reqJSON = digitalReceiptsJSONRepository.findFirstByUserIdAndDocSid(userId,docsId);
        String reqJSONStr = reqJSON!=null ? reqJSON.getJsonStr():"";
        String updatedJsonString;
        try {
            logger.info("json format "+reqJSONStr);
            if(reqJSONStr.isEmpty()) return;
            JSONObject jsonObject = new JSONObject(reqJSONStr);
            JSONObject body = jsonObject.getJSONObject("Body");
            JSONObject receipt = body.getJSONObject("Receipt");

            receipt.put("BillToPhone1", mobilePhone);
            receipt.put("DocSID","ReAssign"+reqJSON.getDocSid());
            if(contactsLoyalty==null) {
                receipt.put("BillToEMail","");
                receipt.put("BillToCustSID","ReAssign"+Math.random());
            } else if (contactsLoyalty.getEmailId()!=null) {
                receipt.put("BillToEMail",contactsLoyalty.getEmailId());
            }
            //String billToPhone1 = receipt.getString("BillToPhone1");
            logger.info("random id generated: ReAssign"+reqJSON.getDocSid());
            updatedJsonString = jsonObject.toString();// request format is changing!! will it effect?
            reqJSON.setJsonStr(updatedJsonString);
            reqJSON.setStatus("New");
            digitalReceiptsJSONRepository.save(reqJSON);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        logger.info(" req JSON "+updatedJsonString);
    }

    private void revertTransactions(Long userId,String cardNumber,String receiptNumber,String docsId) {
        loyaltyTransactionChildRepository.deleteByUserIdAndMembershipNumberAndReceiptNumberAndDocSID(userId,cardNumber,receiptNumber,docsId);
        logger.info("reverted transactions");
    }

    public void calculateBalances(Long userId,List<LoyaltyTransactionChild> trxList,UpdateMobileRequestDTO updateMobileRequestDTO) {
        if(!trxList.isEmpty()) {
            Double pointsToDeduct = 0.0;
            Double amountToDeduct = 0.0;
            Double rewardToDeduct = 0.0;
            Double pointsToAdd = 0.0;
            Double amountToAdd = 0.0;
            Double rewardToAdd = 0.0;
            Double receiptAmount= 0.0;
            for (LoyaltyTransactionChild loyaltyTransactionChild : trxList) {
                String trxType = loyaltyTransactionChild.getTransactionType();
                String enteredAmntType = loyaltyTransactionChild.getEnteredAmountType()!=null?
                        loyaltyTransactionChild.getEnteredAmountType():"";
                String earnType = loyaltyTransactionChild.getEarnType();

                if (trxType.equalsIgnoreCase("Issuance") || trxType.equalsIgnoreCase("Bonus")) {
                    if(trxType.equalsIgnoreCase("Issuance") && enteredAmntType.equalsIgnoreCase("Purchase"))
                        receiptAmount=loyaltyTransactionChild.getIssuanceAmount()!=null?
                            loyaltyTransactionChild.getIssuanceAmount():0.0;
                    if (enteredAmntType.equalsIgnoreCase("Purchase") ||
                            enteredAmntType.equalsIgnoreCase("Reward")) {
                        if (loyaltyTransactionChild.getPointsDifference()!=null &&
                                Double.parseDouble(loyaltyTransactionChild.getPointsDifference())>0)
                            pointsToDeduct += Double.parseDouble(loyaltyTransactionChild.getPointsDifference());
                        else if (loyaltyTransactionChild.getAmountDifference()!=null &&
                                Double.parseDouble(loyaltyTransactionChild.getAmountDifference())>0)
                            amountToDeduct += Double.parseDouble(loyaltyTransactionChild.getAmountDifference());
                        else if (loyaltyTransactionChild.getSpecialRewardId() !=null){
                            if(earnType!=null && earnType.equalsIgnoreCase("Points")) {
                                pointsToDeduct+=loyaltyTransactionChild.getEarnedPoints();
                            } else if (earnType!=null && earnType.equalsIgnoreCase("Amount")) {
                                amountToDeduct+=loyaltyTransactionChild.getEarnedAmount();
                            }else {
                                LoyaltyBalance loyaltyBalance = loyaltyBalanceRepository.findByUserIdAndMemberShipNumberAndValueCode(userId,updateMobileRequestDTO.getWrongMembership(),loyaltyTransactionChild.getEarnType());
                                logger.info("loyalty balances "+loyaltyBalance.getMemberShipNumber());
                                Double earnedReward = Double.parseDouble(loyaltyTransactionChild.getRewardDifference());
                                logger.info("earnedReward "+earnedReward);
                                //if(loyaltyBalance==null) return;// need to handle?
                                if(loyaltyBalance.getBalance()==null || loyaltyBalance.getBalance()<earnedReward){
                                    logger.info("No balance to deduct");
                                }else {
                                    if(earnedReward>0) {
                                        loyaltyBalance.setBalance(loyaltyBalance.getBalance()-earnedReward.longValue());
                                        loyaltyBalance.setTotalEarnedBalance(loyaltyBalance.getTotalEarnedBalance()-earnedReward);
                                        if(loyaltyBalance.getBalance() < 0) loyaltyBalance.setBalance(0L);
                                        if(loyaltyBalance.getTotalEarnedBalance() < 0) loyaltyBalance.setTotalEarnedBalance(0.0);
                                        loyaltyBalanceRepository.save(loyaltyBalance);
                                    }
                                }
                            }
                        }
                    } else if (enteredAmntType.isEmpty()) {
                        if(earnType.equalsIgnoreCase("Points"))
                            pointsToDeduct +=loyaltyTransactionChild.getEarnedPoints();
                        else
                            amountToDeduct +=loyaltyTransactionChild.getEnteredAmount();
                    }
                } /*else if (trxType.equalsIgnoreCase("Redemption")) {
                    if (enteredAmntType.equalsIgnoreCase("PointsRedeem"))
                        pointsToAdd += Double.parseDouble(loyaltyTransactionChild.getPointsDifference());
                    else if (enteredAmntType.equalsIgnoreCase("AmountRedeem"))
                        amountToAdd += Double.parseDouble(loyaltyTransactionChild.getAmountDifference());
                    else
                        rewardToAdd += Double.parseDouble(loyaltyTransactionChild.getRewardDifference());
                }*/
            }
            logger.info("balances to deduct " + pointsToDeduct);
            logger.info("balances to deduct " + amountToDeduct);
            //logger.info("balances to add " + pointsToAdd);
            //logger.info("balances to add " + amountToAdd);
            //logger.info("balances to add " + rewardToAdd);
            updateLoyaltyBalances(userId,updateMobileRequestDTO,pointsToDeduct,amountToDeduct,rewardToDeduct,pointsToAdd,amountToAdd,rewardToAdd,receiptAmount);
        }
    }

    private void updateLoyaltyBalances(Long userId,UpdateMobileRequestDTO updateMobileRequestDTO,
                                       Double pointsToDeduct,Double amountToDeduct,Double rewardToDeduct,
                                       Double pointsToAdd,Double amountToAdd,Double rewardToAdd,Double receiptAmount) {
        ContactLoyalty contactsLoyalty = contactsLoyaltyRepository.findFirstByUserIdAndCardNumber(userId,updateMobileRequestDTO.getWrongMembership());
        //issuance or bouns points/currency
        if(contactsLoyalty!=null) {
            if(contactsLoyalty.getTotalLoyaltyPointsEarned()!=null && contactsLoyalty.getTotalLoyaltyPointsEarned()>=pointsToDeduct)
                contactsLoyalty.setTotalLoyaltyPointsEarned(contactsLoyalty.getTotalLoyaltyPointsEarned()-pointsToDeduct);
            else
                contactsLoyalty.setTotalLoyaltyPointsEarned(0.0);

            if(contactsLoyalty.getLoyaltyPointBalance()!=null && contactsLoyalty.getLoyaltyPointBalance()>=pointsToDeduct)
                contactsLoyalty.setLoyaltyPointBalance(contactsLoyalty.getLoyaltyPointBalance()-pointsToDeduct);
            else
                contactsLoyalty.setLoyaltyPointBalance(0.0);

            if(contactsLoyalty.getLoyaltyCurrencyBalance()!=null && contactsLoyalty.getLoyaltyCurrencyBalance()>=amountToDeduct)
                contactsLoyalty.setLoyaltyCurrencyBalance(contactsLoyalty.getLoyaltyCurrencyBalance()-amountToDeduct);
            else
                contactsLoyalty.setLoyaltyCurrencyBalance(0.0);

            if (contactsLoyalty.getCummulativePurchaseValue()!=null && contactsLoyalty.getCummulativePurchaseValue()>=receiptAmount)
                contactsLoyalty.setCummulativePurchaseValue(contactsLoyalty.getCummulativePurchaseValue()-receiptAmount);
            else
                contactsLoyalty.setCummulativePurchaseValue(0.0);

            //redemption points/currency
            //contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption()+pointsToAdd);
            //contactsLoyalty.setLoyaltyPointBalance(contactsLoyalty.getLoyaltyPointBalance()-pointsToAdd);
            //contactsLoyalty.setLoyaltyCurrencyBalance(contactsLoyalty.getLoyaltyCurrencyBalance()-amountToAdd);

            contactsLoyaltyRepository.save(contactsLoyalty);
        }

    }
}

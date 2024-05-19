package com.optculture.app.services;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.loyalty.BalanceAdjustmentRequest;
import com.optculture.app.dto.loyalty.BalanceAdjustmentResponse;
import com.optculture.app.repositories.*;
import com.optculture.shared.entities.contact.ContactLoyalty;
import com.optculture.shared.entities.loyalty.*;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.util.Constants;
import com.optculture.shared.util.OCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BalanceAdjustmentService {

    Logger logger = LoggerFactory.getLogger(BalanceAdjustmentService.class);
    @Autowired
    ContactLoyaltyRepository contactLoyaltyRepository;

    @Autowired
    LoyaltyTransactionChildRepository loyaltyTransactionChildRepository;

    @Autowired
    LoyaltyProgramsRepository loyaltyProgramsRepository;

    @Autowired
    GetLoggedInUser getLoggedInUser;
    @Autowired
    LoyaltyBalanceService loyaltyBalanceService;
    @Autowired
    LoyaltyBalanceRepository loyaltyBalanceRepository;
    @Autowired
    LoyaltyProgramTierService loyaltyProgramTierService;
    @Autowired
    LoyaltyTransactionExpiryRepository loyaltyTransactionExpiryRepository;
    @Autowired
    LoyaltyTransactionExpiryService loyaltyTransactionExpiryService;

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public BalanceAdjustmentResponse doBalanceAdjustment(BalanceAdjustmentRequest balanceAdjustReq) {

        User user = getLoggedInUser.getLoggedInUser();
        Long userId=user.getUserId();
        Long cardNumber = balanceAdjustReq.getCardNumber();
        ContactLoyalty contactLoyalty = contactLoyaltyRepository.findFirstByUserIdAndCardNumber(userId,cardNumber.toString());
        logger.info("contactLoyalty object "+contactLoyalty.getCardNumber());
        if(contactLoyalty!=null) {
            LoyaltyProgram ltyPrgm = loyaltyProgramsRepository.findByUserIdAndProgramId(userId,contactLoyalty.getProgramId());
            if(ltyPrgm!=null && ltyPrgm.getStatus().equalsIgnoreCase("Active")
                    && contactLoyalty.getMembershipStatus().equalsIgnoreCase("Active")) {
                // CHECK IF THE CARD REWARD FLAG OF TYPE L/GL, THEN ALLOW ADJUSTMENT. FOR G TYPE DO NOT ALLOW ADJUSTMENTS.
                if(!("G".equalsIgnoreCase(contactLoyalty.getRewardFlag()))){
                    if(balanceAdjustReq.getAdjustmentType()!=null) {
                        switch (balanceAdjustReq.getAdjustmentType()){
                            case "ADD":return addBalance(contactLoyalty,ltyPrgm,balanceAdjustReq,user);
                            case "SUBTRACT":return subtractBalance(contactLoyalty,ltyPrgm,balanceAdjustReq,user);
                        }
                }
                } else return new BalanceAdjustmentResponse("Unable to perform adjustments because Reward Flag is G");

            }
            else return new BalanceAdjustmentResponse("Please select a contact with  active program & membership status.");
        }
//        if(balanceAdjustReq.getValue()!=null) {
//            if(balanceAdjustReq.getBalanceType().equals("Points")) {
//
//            } else if (balanceAdjustReq.getBalanceType().equals("Currency")) {
//
//            }
//        }
        return null;
    }

    private BalanceAdjustmentResponse addBalance(ContactLoyalty contactsLoyaltyObj, LoyaltyProgram ltyPrgm, BalanceAdjustmentRequest balanceAdjustReq, User user) {

        LoyaltyBalance lbal=null;
        String valueCode=balanceAdjustReq.getBalanceType();
        Double balanceToAdd=balanceAdjustReq.getValue();
        Double fromLtyBalance = contactsLoyaltyObj.getTotalLoyaltyPointsEarned();
        Double fromAmtBalance = contactsLoyaltyObj.getTotalGiftcardAmount();
        Double fromLPVBalance = getLPV(contactsLoyaltyObj);
        Double fromCPVBalance = contactsLoyaltyObj.getCummulativePurchaseValue();
        boolean flag=false;
        if(valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)) {
            //update totalGiftcardAmount,giftcardBalance
            Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
            //totalGiftCardAmount = totalGiftCardAmount + balanceToAdd ;
            totalGiftCardAmount = totalGiftCardAmount + balanceToAdd;
            Double giftCardBalance =  contactsLoyaltyObj.getLoyaltyCurrencyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyCurrencyBalance();
            //giftCardBalance = giftCardBalance + balanceToAdd ;
            giftCardBalance =giftCardBalance + balanceToAdd;
            contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount);
            logger.info("giftcard balance before rounding:::" + giftCardBalance);

            logger.info("Double setTotalGiftcardAmount = "+totalGiftCardAmount);
            contactsLoyaltyObj.setLoyaltyCurrencyBalance(giftCardBalance);
            contactLoyaltyRepository.save(contactsLoyaltyObj);
            logger.info("giftcard balance after rounding:::"+contactsLoyaltyObj.getLoyaltyCurrencyBalance());
            flag=true;
        }
        else if(valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)) {
            //loyaltyPointsBalance,totalLoyaltyPointsEarned
            Double loyaltyPointsBalance =  contactsLoyaltyObj.getLoyaltyPointBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyPointBalance();
            Double totalLoyaltyPointsEarned = contactsLoyaltyObj.getTotalLoyaltyPointsEarned()== null ? 0.0 : contactsLoyaltyObj.getTotalLoyaltyPointsEarned();
            logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyPointsBalance);
            loyaltyPointsBalance = loyaltyPointsBalance + balanceToAdd;
            totalLoyaltyPointsEarned = totalLoyaltyPointsEarned + balanceToAdd;
            contactsLoyaltyObj.setLoyaltyPointBalance(loyaltyPointsBalance);
            contactsLoyaltyObj.setTotalLoyaltyPointsEarned(totalLoyaltyPointsEarned);
            contactLoyaltyRepository.save(contactsLoyaltyObj);
            logger.info("New LoyaltyBalance is ::::::::::::"+loyaltyPointsBalance);
            flag=true;
        }
        else if(valueCode.equalsIgnoreCase("LPV")){
            Double LPVBal =  contactsLoyaltyObj.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyaltyObj.getCummulativePurchaseValue();
            logger.info("Previous LPVBal was ::::::::::::"+LPVBal);
            LPVBal = LPVBal + balanceToAdd;
            
            contactsLoyaltyObj.setCummulativePurchaseValue(LPVBal);
            contactLoyaltyRepository.save(contactsLoyaltyObj);
            logger.info("New LPVBal is ::::::::::::"+LPVBal);
            flag=true;
        }
        else{
            //for selection of valuecode
            List<LoyaltyBalance> valueCodesList =loyaltyBalanceService.findbyCardNo(contactsLoyaltyObj.getCardNumber(),user.getUserId());
            Set<String> vcodesSet= new HashSet<>();
            if(valueCodesList==null || valueCodesList.isEmpty()){
                lbal=new LoyaltyBalance();
                lbal.setBalance(balanceToAdd.longValue());
                lbal.setOrgId(user.getUserOrganization().getUserOrgId());
                lbal.setMemberShipNumber(contactsLoyaltyObj.getCardNumber());
                lbal.setLoyaltyId(contactsLoyaltyObj.getLoyaltyId());
                lbal.setProgramId(contactsLoyaltyObj.getProgramId());
                lbal.setValueCode(valueCode);
                lbal.setTotalEarnedBalance((lbal.getTotalEarnedBalance()!=null)?(lbal.getTotalEarnedBalance()+balanceToAdd.longValue()):(balanceToAdd.longValue()));
                lbal.setUserId(contactsLoyaltyObj.getUserId());
                lbal.setCreatedDate(Calendar.getInstance());
                loyaltyBalanceRepository.save(lbal);
                flag=true;
            }
            else{

                for(LoyaltyBalance loyaltyBalance : valueCodesList){
                    Long balance=loyaltyBalance.getBalance()==null?0:loyaltyBalance.getBalance();
                    if(loyaltyBalance.getValueCode().equalsIgnoreCase(valueCode)){
                        balance=(long) (balance+balanceToAdd);
                        loyaltyBalance.setBalance(balance);
                        loyaltyBalance.setTotalEarnedBalance((loyaltyBalance.getTotalEarnedBalance()!=null)?(loyaltyBalance.getTotalEarnedBalance()+balanceToAdd.longValue()):(balanceToAdd.longValue()));
                        valueCode=loyaltyBalance.getValueCode();
                        lbal=loyaltyBalance;
                        loyaltyBalanceRepository.save(lbal);
                        vcodesSet.add(valueCode);
//                        flag=true;
                    }
                }
                //for(ValueCodes vcodes : vCodesList){
                if(!vcodesSet.contains(valueCode)){
                    lbal=new LoyaltyBalance();
                    lbal.setBalance(balanceToAdd.longValue());
                    lbal.setOrgId(user.getUserOrganization().getUserOrgId());
                    lbal.setMemberShipNumber(contactsLoyaltyObj.getCardNumber());
                    lbal.setLoyaltyId(contactsLoyaltyObj.getLoyaltyId());
                    lbal.setProgramId(contactsLoyaltyObj.getProgramId());
                    lbal.setValueCode(valueCode);
                    lbal.setTotalEarnedBalance((lbal.getTotalEarnedBalance()!=null)?(lbal.getTotalEarnedBalance()+balanceToAdd.longValue()):(balanceToAdd.longValue()));
                    lbal.setUserId(contactsLoyaltyObj.getUserId());
                    lbal.setCreatedDate(Calendar.getInstance());
                    loyaltyBalanceRepository.save(lbal);
//                    flag=true;

                }
                for(String set:vcodesSet){

                    logger.info("value codes---"+set);
                }
        }
        }
        if(flag){
            String description2 = balanceAdjustReq.getReason();
            // CREATE TRANSACTION
            LoyaltyTransactionChild transactionChild = createPurchaseTransaction(contactsLoyaltyObj, balanceToAdd, valueCode, Constants.STRING_NILL, null, null,OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD,description2,lbal);
            // CREATE EXPIRY TRANSACTION
            Long adjustPoints = 0l;
            Double adjustAmt = 0.0;
            Double adjustLPV = 0.0;
            if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
                    adjustPoints = balanceToAdd.longValue();
                }
                else if(valueCode.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
                    adjustAmt = balanceToAdd;
                }
                else adjustLPV = balanceToAdd;


                if(lbal != null) {
                //for valuecode adjustments
    //                createRewardExpiryTransaction(contactsLoyaltyObj, adjustPoints, adjustAmt,transactionChild.getTransChildId(),OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L,balanceToAdd,
    //                        lbal.getValueCode(),null);
                }
                else {
                    createExpiryTransaction(contactsLoyaltyObj, adjustPoints, adjustAmt, contactsLoyaltyObj.getOrgId(),
                            transactionChild.getTransChildId(),null);
                }
            LoyaltyProgramTier loyaltyProgramTier = null;
            if(contactsLoyaltyObj.getProgramTierId() == null) {
                // TODO
                /* loyaltyProgramTier = findTier(contactsLoyaltyObj);
                if (loyaltyProgramTier == null) {
                    // CALL BONUS
                    updateThresholdBonus(contactsLoyaltyObj, loyaltyProgram, fromLtyBalance, fromAmtBalance,fromCPVBalance ,fromLPVBalance, null,false);
                    ltyPrgmService.saveOrUpdateContactLoyalty(contactsLoyaltyObj);
                    updateLoyaltyData(contactsLoyaltyObj);
                    addTbId.setValue(Constants.STRING_NILL);
                    ltyPwdTbSubId.setText("");
                    addLbId2.setSelectedIndex(0);
                    addLbId.setSelectedIndex(0);
                    addLbId.getItemAtIndex(1).setVisible(true);
                    MessageUtil.setMessage(StringUtils.capitalize(displayLabel)+" added successfully.", "blue");
                    subTb2Id.setText(Constants.STRING_NILL);
                    return;
                }
                else {
                    contactsLoyaltyObj.setProgramTierId(loyaltyProgramTier.getTierId());
                } */
            }
            else{
                loyaltyProgramTier = loyaltyProgramTierService.getLoyaltyTierByTierId(contactsLoyaltyObj.getProgramTierId());
            }

            //points ->amount conversion

            Long pointsDifference = 0l;
            Double amountDifference = 0.0;
            String[] diffArr = applyConversionRules(contactsLoyaltyObj, loyaltyProgramTier); //0 - amountdiff, 1 - pointsdiff
            logger.info("balances After conversion rules updatation --  points = "+contactsLoyaltyObj.getLoyaltyPointBalance()+" currency = "+contactsLoyaltyObj.getLoyaltyCurrencyBalance());
            String conversionRate = null;
            long convertPoints = 0;
            double convertAmount = 0;
            if(diffArr != null){
                convertAmount = Double.valueOf(diffArr[0].trim());
                convertPoints = Double.valueOf(diffArr[1].trim()).longValue();
                conversionRate = diffArr[2];
            }

            pointsDifference = adjustPoints - convertPoints;
            amountDifference = (double)adjustAmt + (diffArr != null ? Double.parseDouble(diffArr[0].trim()) : 0.0);

            LoyaltyProgramTier preTier = loyaltyProgramTier;
            // CALL TIER UPGD
            // TODO
           /* if(loyaltyProgramTier!=null && loyaltyProgramTier.getMultipleTierUpgrdRules()!=null && !loyaltyProgramTier.getMultipleTierUpgrdRules().isEmpty())//APP-4511
             loyaltyProgramTier = applyMultipleTierUpgradeRule(contactsLoyaltyObj, loyaltyProgramTier,loyaltyProgram,transactionChild);
            else
                         loyaltyProgramTier = applyTierUpgradeRule(contactsLoyaltyObj, loyaltyProgram, transactionChild, loyaltyProgramTier);
            */

            String description21 = null;
            updatePurchaseTransaction(transactionChild, contactsLoyaltyObj, ""+pointsDifference, ""+amountDifference, conversionRate, convertAmount,loyaltyProgramTier);
            logger.info("balances before balance object = "+contactsLoyaltyObj.getLoyaltyPointBalance()+" currency = "+contactsLoyaltyObj.getLoyaltyCurrencyBalance());

            boolean tierUpgd = false;
            if (!preTier.getTierType().equalsIgnoreCase(loyaltyProgramTier.getTierType())) {
                tierUpgd = true;
            }
            // CALL BONUS
            //TODO
//            updateThresholdBonus(contactsLoyaltyObj, ltyPrgm, fromLtyBalance, fromAmtBalance, fromCPVBalance,fromLPVBalance, loyaltyProgramTier,tierUpgd);

            contactLoyaltyRepository.save(contactsLoyaltyObj);
            return  new BalanceAdjustmentResponse("Adding "+valueCode+" Balance Successful");
        }
        else return new BalanceAdjustmentResponse("Transaction was not successful.");
    }

    private LoyaltyTransactionExpiry createExpiryTransaction(ContactLoyalty loyalty,
                                                             Long expiryPoints, Double expiryAmount, Long orgId, Long transChildId,Long bonusId){
        logger.debug(">>>>>>>>>>>>> entered in createExpiryTransaction");
        LoyaltyTransactionExpiry transaction = null;
        try{

            transaction = new LoyaltyTransactionExpiry();
            transaction.setTransChildId(transChildId);
            transaction.setMembershipNumber(""+loyalty.getCardNumber());
            transaction.setMembershipType(loyalty.getMembershipType());
            transaction.setCreatedDate(LocalDateTime.now());
            transaction.setOrgId(orgId);
            transaction.setProgramId(loyalty.getProgramId());
            transaction.setTierId(loyalty.getProgramTierId());
            transaction.setUserId(loyalty.getUserId());
            transaction.setExpiryPoints(expiryPoints);
            transaction.setExpiryAmount(expiryAmount);
            transaction.setRewardFlag(OCConstants.LOYALTY_MEMBERSHIP_REWARD_FLAG_L);
            transaction.setLoyaltyId(loyalty.getLoyaltyId());
            transaction.setBonusId(bonusId);

            loyaltyTransactionExpiryRepository.save(transaction);

        }catch(Exception e){
            logger.error("Exception while logging enroll transaction...",e);
        }
        logger.debug("<<<<<<<<<<<<< completed createExpiryTransaction");
        return transaction;
    }//createExpiryTransaction

    private BalanceAdjustmentResponse subtractBalance(ContactLoyalty contactsLoyaltyObj, LoyaltyProgram ltyPrgm, BalanceAdjustmentRequest balanceAdjustReq,User user) {

       LoyaltyProgramTier ltyPrgmTier = loyaltyProgramTierService.getLoyaltyTierByTierId(contactsLoyaltyObj.getProgramTierId());
        String valueCode=balanceAdjustReq.getBalanceType();
        Double balanceToSub=balanceAdjustReq.getValue();
        LoyaltyBalance ltyBalanceObj=null;
        String substratAmount = null;
        try {
        if(valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_AMOUNT)){
            double existingGiftCardBalance = contactsLoyaltyObj.getLoyaltyCurrencyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyCurrencyBalance();
            logger.info("Temp ::"+balanceToSub+"Existing loyalty balance"+existingGiftCardBalance);

            if(balanceToSub > existingGiftCardBalance){
                //balance to be is subtracted is greater than existing balance condition is true & display popup & return
                return new BalanceAdjustmentResponse("Rewards to be subtracted should be less or equal to existing currency balance.");
            }
            Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
            totalGiftCardAmount = new BigDecimal(totalGiftCardAmount -balanceToSub).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            Double giftCardBalance =  contactsLoyaltyObj.getLoyaltyCurrencyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyCurrencyBalance();
            giftCardBalance = new BigDecimal(giftCardBalance - balanceToSub).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("giftCardBalance "+giftCardBalance+" - "+"balanceToSub "+balanceToSub);
            contactsLoyaltyObj.setLoyaltyCurrencyBalance(giftCardBalance);
            contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount);
            contactLoyaltyRepository.save(contactsLoyaltyObj);
        }
        else if(valueCode.equalsIgnoreCase(OCConstants.LOYALTY_TYPE_POINTS)){
            Double loyaltyBalance =  contactsLoyaltyObj.getLoyaltyPointBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyPointBalance();
            Double totalLoyaltyEarned = contactsLoyaltyObj.getTotalLoyaltyPointsEarned() == null ? 0.0 : contactsLoyaltyObj.getTotalLoyaltyPointsEarned();

            logger.info("Previous LoyaltyBalance was ::::::::::::"+loyaltyBalance);
            Double totalConvertedPoints=getAutoConvertionReversalVal(contactsLoyaltyObj, ltyPrgmTier);

            logger.info("TotalConvertedPoints :"+totalConvertedPoints);

            //TODO
                // if conversion allowed
          // loyaltyBalance = loyaltyBalance + totalConvertedPoints;

            if(balanceToSub>loyaltyBalance){
                return new BalanceAdjustmentResponse("Points to be subtracted should be less or equal to existing point balance.");
            }
           /* deducting the amount , since amount converted to points.
            Double totalGiftCardAmount = contactsLoyaltyObj.getTotalGiftcardAmount()== null ? 0.0 : contactsLoyaltyObj.getTotalGiftcardAmount();
            Double giftCardBalance =  contactsLoyaltyObj.getLoyaltyCurrencyBalance() == null ? 0.0 : contactsLoyaltyObj.getLoyaltyCurrencyBalance();
            contactsLoyaltyObj.setTotalGiftcardAmount(totalGiftCardAmount-giftCardBalance);
            contactsLoyaltyObj.setLoyaltyCurrencyBalance(0.0); */

            loyaltyBalance = loyaltyBalance -balanceToSub;
            totalLoyaltyEarned=totalLoyaltyEarned-balanceToSub;

            //convert remaining points to amount
           /* double amtToAdd=loyaltyBalance/ltyPrgmTier.getConvertFromPoints();
            int convAmt = (int)amtToAdd;
            Double convertedAmount=ltyPrgmTier.getConvertToAmount()*convAmt;
            contactsLoyaltyObj.setLoyaltyCurrencyBalance(convertedAmount);
            contactsLoyaltyObj.setTotalGiftcardAmount(contactsLoyaltyObj.getTotalGiftcardAmount()+convertedAmount);
            contactsLoyaltyObj.setLoyaltyPointBalance(loyaltyBalance % ltyPrgmTier.getConvertFromPoints());
            contactsLoyaltyObj.setTotalLoyaltyPointsEarned(totalLoyaltyEarned); */
            contactsLoyaltyObj.setLoyaltyPointBalance(loyaltyBalance);
            contactsLoyaltyObj.setTotalLoyaltyPointsEarned(totalLoyaltyEarned);
            contactLoyaltyRepository.save(contactsLoyaltyObj);
           }
        else if(valueCode.equalsIgnoreCase("LPV")){
            Double LPVBal =  contactsLoyaltyObj.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyaltyObj.getCummulativePurchaseValue();
        }

        String description2=balanceAdjustReq.getReason();
        createPurchaseTransaction(contactsLoyaltyObj, balanceToSub, valueCode, Constants.STRING_NILL, null, null, OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_SUB,description2,ltyBalanceObj);
        if(valueCode.equals(OCConstants.LOYALTY_TYPE_POINTS)){
            deductPointsFromExpiryTable(contactsLoyaltyObj,  balanceToSub.longValue());
        }
        else if(valueCode.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
            deductLoyaltyAmtFromExpiryTable(contactsLoyaltyObj, balanceToSub);
        }

        } catch (Exception e) {
        logger.error("Exception ::",e);
        }
        return new BalanceAdjustmentResponse("Substracting "+valueCode+" balance was successful !");
    }


    private double getAutoConvertionReversalVal(ContactLoyalty contactsLoyalty, LoyaltyProgramTier tier) {
        double unitAmtFactor = (double)tier.getConvertFromPoints()/tier.getConvertToAmount();
        double multiple = (double)unitAmtFactor;
        double totConvertedPts = contactsLoyalty.getLoyaltyCurrencyBalance() * multiple;

        return totConvertedPts;
    }

    private LoyaltyTransactionChild createPurchaseTransaction(ContactLoyalty loyalty, Double adjustValue, String earnType,
                                                              String entAmountType, LocalDateTime activationDate,String earnStatus, String adjType,String description2,LoyaltyBalance ltyBalance){
        logger.debug(">>>>>>>>>>>>> entered in createPurchaseTransaction");
        LoyaltyTransactionChild transaction = null;
        try{

            transaction = new LoyaltyTransactionChild();
            transaction.setMembershipNumber(""+loyalty.getCardNumber());
            transaction.setMembershipType(loyalty.getMembershipType());
            transaction.setCardSetId(loyalty.getCardSetId());
            transaction.setCreatedDate(LocalDateTime.now());
            transaction.setEarnType(earnType);
            if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
                long adjustValueLong = adjustValue.longValue();//APP-823
                transaction.setEarnedPoints(adjustValue);
                transaction.setPointsDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValueLong:-adjustValueLong));
            }
            else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
                transaction.setEarnedAmount(adjustValue);
                transaction.setAmountDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));

            }else{
                transaction.setRewardBalance((ltyBalance!=null)?(ltyBalance.getBalance().doubleValue()):null);
                transaction.setRewardDifference(""+(adjType.equalsIgnoreCase(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT_ADD)?adjustValue:-adjustValue));
                transaction.setEarnedReward(adjustValue);
            }
            if(earnStatus != null) {
                transaction.setEarnStatus(earnStatus);
            }
            if(activationDate != null){
                if(earnType.equals(OCConstants.LOYALTY_TYPE_POINTS)){
                    transaction.setHoldPoints((double)adjustValue);
                }
                else if(earnType.equals(OCConstants.LOYALTY_TYPE_AMOUNT)){
                    transaction.setHoldAmount((double)adjustValue);
                }
                transaction.setValueActivationDate(activationDate);
            }
            transaction.setEnteredAmount(adjustValue);
            transaction.setEnteredAmountType(adjType);
            transaction.setOrgId(loyalty.getOrgId());
            transaction.setPointsBalance(loyalty.getLoyaltyPointBalance());
            transaction.setAmountBalance(loyalty.getLoyaltyCurrencyBalance());
            transaction.setGiftBalance(loyalty.getGiftBalance());
            transaction.setProgramId(loyalty.getProgramId());
            transaction.setTierId(loyalty.getProgramTierId());
            transaction.setUserId(loyalty.getUserId());
            transaction.setTransactionType(OCConstants.LOYALTY_TRANS_TYPE_ADJUSTMENT);
            transaction.setSourceType(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MANUAL);
            transaction.setContactId(loyalty.getContact() == null ? null : loyalty.getContact().getContactId());
//			transaction.setEventTriggStatus(OCConstants.LOYALTY_TRANSACTION_STATUS_NEW);
            transaction.setLoyaltyId(loyalty.getLoyaltyId());
            //transaction.setDescription2(subTb2Id.getValue().trim());
//            description2=subTb2Id.getValue().trim();
            transaction.setDescription2(description2);

            loyaltyTransactionChildRepository.save(transaction);

            //Event Trigger sending part
            //TODO
           /* EventTriggerEventsObservable eventTriggerEventsObservable = (EventTriggerEventsObservable) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVABLE);
            EventTriggerEventsObserver eventTriggerEventsObserver = (EventTriggerEventsObserver) ServiceLocator.getInstance().getBeanByName(OCConstants.EVENT_TRIGGER_EVENTS_OBSERVER);
            eventTriggerEventsObservable.addObserver(eventTriggerEventsObserver);
            EventTriggerDao eventTriggerDao  = (EventTriggerDao)ServiceLocator.getInstance().getDAOByName(OCConstants.EVENT_TRIGGER_DAO);
            List<EventTrigger> etList = eventTriggerDao.findAllETByUserAndType(transaction.getUserId(),Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);

            if(etList != null) {
                eventTriggerEventsObservable.notifyToObserver(etList, transaction.getTransChildId(), transaction.getTransChildId(),
                        transaction.getUserId(), OCConstants.LOYALTY_ADJUSTMENT,Constants.ET_TYPE_ON_LOYALTY_ADJUSTMENT);
            } */

        }catch(Exception e){
            logger.error("Exception while logging enroll transaction...",e);
        }

        logger.debug("<<<<<<<<<<<<< completed createPurchaseTransaction");
        return transaction;
    }
    private String[] applyConversionRules(ContactLoyalty contactsLoyalty, LoyaltyProgramTier tier){

        String[] differenceArr = null;

        try{

            if(tier.getConversionType().equalsIgnoreCase(OCConstants.LOYALTY_CONVERSION_TYPE_AUTO)){

                if(tier.getConvertFromPoints() != null && tier.getConvertFromPoints() > 0
                        && contactsLoyalty.getLoyaltyPointBalance() != null && contactsLoyalty.getLoyaltyPointBalance() > 0
                        && contactsLoyalty.getLoyaltyPointBalance() >= tier.getConvertFromPoints()){

                    differenceArr = new String[3];

                    double multipledouble = contactsLoyalty.getLoyaltyPointBalance()/tier.getConvertFromPoints();
                    int multiple = (int)multipledouble;
                    //double convertedAmount = tier.getConvertToAmount() * multiple;
                    double convertedAmount = 0.0;
                    String result = decimalFormat.format(tier.getConvertToAmount() * multiple);
                    if(result != null)
                        convertedAmount = Double.parseDouble(result);
                    //double subPoints = multiple * tier.getConvertFromPoints();
                    String res =decimalFormat.format(multiple * tier.getConvertFromPoints());
                    double subPoints = 0.0;
                    if(res != null)
                        subPoints = Double.parseDouble(res);

                    differenceArr[0] = ""+convertedAmount;
                    differenceArr[1] = ""+subPoints;
                    differenceArr[2] = tier.getConvertFromPoints().intValue()+" Points -> "+tier.getConvertToAmount().intValue();

                    logger.info("multiple factor = "+multiple);
                    logger.info("Conversion amount ="+convertedAmount);
                    logger.info("subtract points = "+subPoints);


                    //update giftcard balance
                    if(contactsLoyalty.getLoyaltyCurrencyBalance() == null ) {
                        contactsLoyalty.setLoyaltyCurrencyBalance(convertedAmount);
                    }
                    else{
                        //contactsLoyalty.setGiftcardBalance(contactsLoyalty.getGiftcardBalance() + convertedAmount);
                        contactsLoyalty.setLoyaltyCurrencyBalance(new BigDecimal(contactsLoyalty.getLoyaltyCurrencyBalance() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    }
                    if(contactsLoyalty.getTotalGiftcardAmount() == null){
                        contactsLoyalty.setTotalGiftcardAmount(convertedAmount);
                    }
                    else{
                        //contactsLoyalty.setTotalGiftcardAmount(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount);
                        contactsLoyalty.setTotalGiftcardAmount(new BigDecimal(contactsLoyalty.getTotalGiftcardAmount() + convertedAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    }

                    //deduct loyalty points
                    contactsLoyalty.setLoyaltyPointBalance(contactsLoyalty.getLoyaltyPointBalance() - subPoints);
                    contactsLoyalty.setTotalLoyaltyRedemption(contactsLoyalty.getTotalLoyaltyRedemption() == null ? subPoints :
                            contactsLoyalty.getTotalLoyaltyRedemption() + subPoints);

                    logger.info("contactsLoyalty.getGiftcardBalance() = "+contactsLoyalty.getLoyaltyCurrencyBalance());

                    deductPointsFromExpiryTable(contactsLoyalty, subPoints, convertedAmount);
                }
            }

        }catch(Exception e){
            logger.error("Exception while applying auto conversion rules...", e);
            return null;
        }
        return differenceArr;
    }
    //    This method is used to deduct auto converted Points From ExpiryTable
    private void deductPointsFromExpiryTable(ContactLoyalty contactLoyalty, double subPoints, double earnedAmt) throws Exception{
        logger.debug(">>>>>>>>>>>>> entered in deductPointsFromExpiryTable");
        List<LoyaltyTransactionExpiry> expiryList = null; //expiryDao.fetchExpPointsTrans(""+membershipNumber, 100, userId);
        Iterator<LoyaltyTransactionExpiry> iterList = null; //expiryList.iterator();
        LoyaltyTransactionExpiry expiry = null;
        long remainingPoints = (long)subPoints;

        do{
            expiryList = loyaltyTransactionExpiryService.fetchExpLoyaltyPtsTrans(contactLoyalty.getLoyaltyId(),
                    100, contactLoyalty.getUserId());
            //logger.info("expiryList size = "+expiryList.size());
            if(expiryList == null || remainingPoints <= 0) break;
            iterList = expiryList.iterator();

            while(iterList.hasNext()){

                logger.info("remainingPoints = "+remainingPoints +" earnedAmt = "+earnedAmt);
                expiry = iterList.next();

                //logger.info("expiry points= "+expiry.getExpiryPoints()+" expiry amount = "+expiry.getExpiryAmount());

                if((expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0) &&
                        (expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0)){
                    logger.info("Wrong entry condition...");
                }
                else if(expiry.getExpiryPoints() < remainingPoints){
                    logger.info("subtracted points = "+expiry.getExpiryPoints());
                    remainingPoints = remainingPoints - expiry.getExpiryPoints().longValue();
                    expiry.setExpiryPoints(0l);
                    //expiryDao.saveOrUpdate(expiry);
                    loyaltyTransactionExpiryRepository.save(expiry);
                    continue;

                }
                else if(expiry.getExpiryPoints() >= remainingPoints){
                    logger.info("subtracted points = "+expiry.getExpiryPoints());
                    expiry.setExpiryPoints(expiry.getExpiryPoints() - remainingPoints);
                    remainingPoints = 0;
                    if(expiry.getExpiryAmount() == null){
                        expiry.setExpiryAmount(earnedAmt);
                    }
                    else{
                        expiry.setExpiryAmount(expiry.getExpiryAmount() + earnedAmt);
                    }
                    //logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
                    //expiryDao.saveOrUpdate(expiry);
                    loyaltyTransactionExpiryRepository.save(expiry);
                    //logger.info("expiry.getExpiryAmount() = "+expiry.getExpiryAmount()+ " earnedAmt = "+earnedAmt);
                    break;
                }

            }

        }while(remainingPoints > 0);
        logger.debug("<<<<<<<<<<<<< completed deductPointsFromExpiryTable");
    }//deductPointsFromExpiryTable

    private void deductPointsFromExpiryTable(ContactLoyalty loyalty, long subPoints) throws Exception{


        List<LoyaltyTransactionExpiry> expiryList = null;
        Iterator<LoyaltyTransactionExpiry> iterList = null;
        LoyaltyTransactionExpiry expiry = null;
        long remPoints = subPoints;

        do{
            expiryList = loyaltyTransactionExpiryService.fetchExpLoyaltyPtsTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
            if(expiryList == null || remPoints <= 0) break;
            iterList = expiryList.iterator();

            while(iterList.hasNext()){
                expiry = iterList.next();

                if(expiry.getExpiryPoints() == null || expiry.getExpiryPoints() <= 0){
                    logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
                    continue;
                }
                else if(expiry.getExpiryPoints() < remPoints){
                    logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
                    remPoints = remPoints - expiry.getExpiryPoints().longValue();
                    expiry.setExpiryPoints(0l);
                    //expiryDao.saveOrUpdate(expiry);
                    loyaltyTransactionExpiryRepository.save(expiry);
                    continue;

                }
                else if(expiry.getExpiryPoints() >= remPoints){
                    logger.info("subtracted loyalty points = "+expiry.getExpiryPoints());
                    expiry.setExpiryPoints(expiry.getExpiryPoints() - remPoints);
                    remPoints = 0;
                    //expiryDao.saveOrUpdate(expiry);
                    loyaltyTransactionExpiryRepository.save(expiry);
                    break;
                }

            }
            expiryList = null;

        }while(remPoints > 0);

        //createTransactionForExpiry(loyalty, subPoints-remPoints, OCConstants.LOYALTY_TRANS_ENTEREDAMOUNT_TYPE_POINTS_EXP);
    }

    private void deductLoyaltyAmtFromExpiryTable(ContactLoyalty loyalty, Double balanceToSub) {
        List<LoyaltyTransactionExpiry> expiryList = null;
        Iterator<LoyaltyTransactionExpiry> iterList = null;
        LoyaltyTransactionExpiry expiry = null;
        double remAmount = balanceToSub;
        do{
            expiryList = loyaltyTransactionExpiryService.fetchExpLoyaltyAmtTrans(loyalty.getLoyaltyId(), 100, loyalty.getUserId());
            if(expiryList == null || remAmount <= 0) break;
            iterList = expiryList.iterator();

            while(iterList.hasNext()){
                expiry = iterList.next();

                if(expiry.getExpiryAmount() == null || expiry.getExpiryAmount() <= 0){
                    logger.info("WRONG EXPIRY TRANSACTION FETCHED...");
                    continue;
                }
                else if(expiry.getExpiryAmount() < remAmount){
                    logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
                    remAmount = remAmount - expiry.getExpiryAmount().doubleValue();
                    expiry.setExpiryAmount(0.0);
                    //expiryDao.saveOrUpdate(expiry);
                    loyaltyTransactionExpiryRepository.save(expiry);
                    logger.info("Expiry Amount deducted..."+expiry.getExpiryAmount().doubleValue());
                    continue;

                }
                else if(expiry.getExpiryAmount() >= remAmount){
                    logger.info("subtracted loyalty amount = "+expiry.getExpiryAmount());
                    expiry.setExpiryAmount(expiry.getExpiryAmount() - remAmount);
                    remAmount = 0;
                    //expiryDao.saveOrUpdate(expiry);
                    loyaltyTransactionExpiryRepository.save(expiry);
                    logger.info("Expiry Amount deducted..."+remAmount);
                    break;
                }

            }
            expiryList = null;

        }while(remAmount > 0);
    }

    private void updatePurchaseTransaction(LoyaltyTransactionChild transaction, ContactLoyalty loyalty,
                                           String ptsDiff, String amtDiff, String conversionRate, double convertAmt, LoyaltyProgramTier tier){

        try{

            transaction.setAmountDifference(decimalFormat.format(Double.parseDouble(amtDiff)));
            transaction.setPointsDifference(ptsDiff);
            transaction.setPointsBalance(loyalty.getLoyaltyPointBalance());
            transaction.setAmountBalance(loyalty.getLoyaltyCurrencyBalance());
            transaction.setGiftBalance(loyalty.getGiftBalance());
            transaction.setDescription(conversionRate);
            transaction.setConversionAmt(convertAmt);
            transaction.setTierId(tier.getTierId());

            loyaltyTransactionChildRepository.save(transaction);

        }catch(Exception e){
            logger.error("Exception while logging enroll transaction...",e);

        }

    }
    public  Double getLPV(ContactLoyalty contactsLoyalty) {
        Double totPurchaseValue = null;
        Double cummulativePurchaseValue = contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
        Double cummulativeReturnValue = contactsLoyalty.getCummulativeReturnValue() == null ? 0.0 : contactsLoyalty.getCummulativeReturnValue();
        totPurchaseValue = (new BigDecimal(cummulativePurchaseValue-cummulativeReturnValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        //totPurchaseValue = cummulativePurchaseValue-cummulativeReturnValue;
        logger.debug("totPurchaseValue =="+totPurchaseValue);
        return totPurchaseValue;
    }

}

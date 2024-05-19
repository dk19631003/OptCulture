package com.optculture.app.services;

import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.transaction.TransactionDto;
import com.optculture.app.dto.transaction.TransactionItem;
import com.optculture.app.repositories.LoyaltyTransactionChildRepository;
import com.optculture.shared.entities.loyalty.LoyaltyTransactionChild;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class LoyaltyTransactionChildService {

    LoyaltyTransactionChildService (LoyaltyTransactionChildRepository ltcrepo){
        this.loyaltyTransactionChildRepository = ltcrepo;
    }

    LoyaltyTransactionChildRepository loyaltyTransactionChildRepository;

    public ResponseEntity<ResponseObject<List<TransactionItem>>> getTransactionsByLoyaltyId(Long loyaltyId, int pageNumber, int pageSize, String transactionType, LocalDateTime fromDate, LocalDateTime toDate, Long userId) {

//        Sort.Order order1= Sort.Order.desc("transChildId");
//        Sort.Order order2=Sort.Order.desc("receiptNumber");
//        Sort sort= Sort.by(Arrays.asList(order1,order2));

        PageRequest pageRequest= PageRequest.of(pageNumber,pageSize);

        Page<LoyaltyTransactionChild> loyaltyTransactionChildPage= loyaltyTransactionChildRepository.findByLoyaltyIdAndTimePeriod(loyaltyId,userId,fromDate,toDate,pageRequest);

        List<TransactionItem> transactionItemList=getTransactionItemList(loyaltyTransactionChildPage.getContent()); //grouping by receiptnumber and prepare dtolist
        return new ResponseEntity<>(new ResponseObject<List<TransactionItem>>(loyaltyTransactionChildPage.getTotalElements(),(long)loyaltyTransactionChildPage.getTotalPages(),transactionItemList), HttpStatus.OK);
    }

    private List<TransactionItem> getTransactionItemList(List<LoyaltyTransactionChild> loyaltyTransactionChildList) {

        List<TransactionItem> transactionItems= new ArrayList<>();
        Map<String,List<TransactionDto>> receiptNumbersAndTransactions=new HashMap<>();

        for(LoyaltyTransactionChild ltc:loyaltyTransactionChildList){
            if(ltc.getTransactionType().equalsIgnoreCase("Enrollment")||ltc.getTransactionType().equalsIgnoreCase("Inquiry"))
                continue;
            if(ltc.getReceiptNumber()!=null){
                if(receiptNumbersAndTransactions.containsKey(ltc.getReceiptNumber())){
                    receiptNumbersAndTransactions.get(ltc.getReceiptNumber()).add(getTransactionDto(ltc));
                }
                else{
                    receiptNumbersAndTransactions.put(ltc.getReceiptNumber(),new ArrayList<>());
                    receiptNumbersAndTransactions.get(ltc.getReceiptNumber()).add(getTransactionDto(ltc));
                    TransactionItem transactionItem = new TransactionItem();
                    transactionItem = new TransactionItem();
                    transactionItem.setReceiptNumber(ltc.getReceiptNumber());
                    transactionItem.setCreatedDate(ltc.getCreatedDate());
                    transactionItem.setTransactionType(ltc.getTransactionType());
                    transactionItem.setDocSID(ltc.getDocSID());
                    transactionItems.add(transactionItem);
                }
            }
            else{
                TransactionItem transactionItem = new TransactionItem();
                transactionItem = new TransactionItem();
                transactionItem.setReceiptNumber(ltc.getReceiptNumber());
                transactionItem.setCreatedDate(ltc.getCreatedDate());
                transactionItem.setTransactionType(ltc.getTransactionType());
                transactionItem.setDocSID(ltc.getDocSID());
                TransactionDto transactionDto= getTransactionDto(ltc);
                List<TransactionDto> loyaltyTransactionDtoList=new ArrayList<>(Arrays.asList(transactionDto));
                transactionItem.setTransactions(loyaltyTransactionDtoList);
                transactionItems.add(transactionItem);
            }
        }
        for(TransactionItem transactionItem:transactionItems){ //setting  transactoins back to receiptNumberobjects
            if(transactionItem.getReceiptNumber()!=null){
                List<TransactionDto> transactionDtos=receiptNumbersAndTransactions.get(transactionItem.getReceiptNumber());
                transactionItem.setTransactions(transactionDtos);
            }
        }
        return transactionItems;
    }

    private TransactionDto getTransactionDto(LoyaltyTransactionChild ltc) {
        
        TransactionDto transactionDto= new TransactionDto();
        transactionDto.setDocSID(ltc.getDocSID());
        if(ltc.getTransactionType().equalsIgnoreCase("Issuance")){

        if(ltc.getEnteredAmountType().equalsIgnoreCase("Purchase")){
            transactionDto.setTransactionType(ltc.getTransactionType());
            if(ltc.getAmountDifference() !=null && Double.parseDouble(ltc.getAmountDifference())>0){
                transactionDto.setAmount(ltc.getAmountDifference());
            }
            else if(ltc.getPointsDifference()!=null && Double.parseDouble(ltc.getPointsDifference())>0){
                transactionDto.setPoints(ltc.getPointsDifference());
            }
        }
        else if(ltc.getEnteredAmountType().equalsIgnoreCase("Reward")){
         
            transactionDto.setTransactionType("Reward");
            if(ltc.getEarnType().equalsIgnoreCase("Points")||ltc.getEarnType().equalsIgnoreCase("Amount")||ltc.getEarnType().equalsIgnoreCase("Currency")){

                if(ltc.getEarnedAmount() !=null){
                    transactionDto.setAmount(ltc.getEarnedAmount().toString());
                }
                else if(ltc.getEarnedPoints()!=null){
                    transactionDto.setPoints(ltc.getEarnedPoints().toString());
                }
            }
            else {
                transactionDto.setPoints(ltc.getEarnedReward()+" "+ltc.getEarnType()); //count of rewards and type of valuecode
            }
        }
        }
        else if(ltc.getTransactionType().equalsIgnoreCase("Adjustment")){
            transactionDto.setTransactionType(ltc.getEnteredAmountType()); //type ->Add/sub
            if(ltc.getEnteredAmountType().equalsIgnoreCase("Add")) {
                if (ltc.getAmountDifference() != null && Double.parseDouble(ltc.getAmountDifference())>0) {
                    transactionDto.setAmount(ltc.getAmountDifference());
                } else if (ltc.getPointsDifference() != null && Double.parseDouble(ltc.getPointsDifference())>0) {
                    transactionDto.setPoints(ltc.getPointsDifference());
                } else transactionDto.setPoints(ltc.getRewardDifference() + " " + ltc.getEarnType());
            }
           else if(ltc.getEnteredAmountType().equalsIgnoreCase("Sub")) {
                if (ltc.getAmountDifference() != null && Double.parseDouble(ltc.getAmountDifference())<0) {
                    transactionDto.setAmount(ltc.getAmountDifference());
                } else if (ltc.getPointsDifference() != null && Double.parseDouble(ltc.getPointsDifference())<0) {
                    transactionDto.setPoints(ltc.getPointsDifference());
                } else transactionDto.setPoints(ltc.getRewardDifference() + " " + ltc.getEarnType());
            }
        }
        else if(ltc.getTransactionType().equalsIgnoreCase("Bonus")){

            transactionDto.setTransactionType(ltc.getTransactionType()); //type bonus

            if(ltc.getAmountDifference() !=null && Double.parseDouble(ltc.getAmountDifference())>0){
                transactionDto.setAmount(ltc.getAmountDifference());
            }
            else if(ltc.getPointsDifference()!=null && Double.parseDouble(ltc.getPointsDifference())>0){
                transactionDto.setPoints(ltc.getPointsDifference());
            }

            else if(ltc.getEarnedAmount()!=null && ltc.getEarnedAmount()>0) {
                transactionDto.setAmount(ltc.getEarnedAmount().toString());
            }
            else if(ltc.getEarnedPoints()!=null && ltc.getEarnedPoints()>0) {
                transactionDto.setAmount(ltc.getEarnedPoints().toString());
            }
            else transactionDto.setPoints("0");
        }
        else if(ltc.getTransactionType().equalsIgnoreCase("Redemption")){
           
            transactionDto.setTransactionType(ltc.getTransactionType());
            if(ltc.getAmountDifference() !=null &&ltc.getAmountDifference().length()>0 ){
                transactionDto.setAmount(ltc.getAmountDifference());
            }
            else if(ltc.getPointsDifference()!=null && ltc.getPointsDifference().length()>0){
                transactionDto.setPoints(ltc.getPointsDifference());
            }
            else transactionDto.setPoints(ltc.getRewardDifference()+" "+ltc.getEarnType()); //rewards redemption and valuecode
        }
        else if(ltc.getTransactionType().equalsIgnoreCase("Return")){

            transactionDto.setTransactionType(ltc.getEnteredAmountType()); //type can be redeem reverse/issuance reverse/reward reversal
            if(ltc.getAmountDifference() !=null &&ltc.getAmountDifference().length()>0 ){
                transactionDto.setAmount(ltc.getAmountDifference());
            }
            else if(ltc.getPointsDifference()!=null && ltc.getPointsDifference().length()>0){
                transactionDto.setPoints(ltc.getPointsDifference());
            }
            else transactionDto.setPoints(ltc.getRewardDifference()+" "+ltc.getEarnType());
        }
        else if(ltc.getTransactionType().equalsIgnoreCase("Tier adjustment")||ltc.getTransactionType().equalsIgnoreCase("Tier Upgrade")){
            transactionDto.setTransactionType("Tier Name"); // to display tier name
            transactionDto.setTierId(ltc.getTierId().toString());
        }
        return  transactionDto;
    }

    public LocalDateTime getLastInteractionOfContact(Long contactId, Long userId) {
        Optional<LoyaltyTransactionChild> optLoyaltyTransactionChild= loyaltyTransactionChildRepository.findFirstByContactIdAndUserIdOrderByCreatedDateDesc(contactId,userId);
        return optLoyaltyTransactionChild.isPresent()?optLoyaltyTransactionChild.get().getCreatedDate():null;
    }

    public Long getCumulativePoints(Long userId, Long programId, Long loyaltyId, LocalDateTime startDate, LocalDateTime endDate) {
        return loyaltyTransactionChildRepository.getCumulativePoints(userId,programId,startDate,endDate);
    }
}

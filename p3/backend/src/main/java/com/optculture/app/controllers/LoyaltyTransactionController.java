package com.optculture.app.controllers;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.transaction.TransactionItem;
import com.optculture.app.services.DRSmsSentService;
import com.optculture.app.services.LoyaltyTransactionChildService;
import com.optculture.app.services.SalesService;
import com.optculture.app.services.SqIdService;
import com.optculture.shared.entities.org.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/loyalty-transactions")
public class LoyaltyTransactionController {
    LoyaltyTransactionChildService transactionChildService;
    @Autowired
    SalesService salesService;

    public LoyaltyTransactionController(LoyaltyTransactionChildService transactionChildService) {
        this.transactionChildService = transactionChildService;
    }
    @Autowired
    private GetLoggedInUser getLoggedInUser;
    @Autowired
    SqIdService sqIdService;
    @Autowired
    private DRSmsSentService drSmsSentService;
    @GetMapping("/")
    public ResponseEntity<ResponseObject<List<TransactionItem>>> getTransactionsByLoyaltyId(@RequestParam String loyaltyId, @RequestParam(defaultValue = "ALL",required = true) String transactionType, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime fromDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime toDate, @RequestParam(defaultValue = "0",required = false) int pageNumber, @RequestParam(defaultValue = "10",required = false) int pageSize,@RequestParam String cid){
        User currentUser=getLoggedInUser.getLoggedInUser();
        List<Long> idList=sqIdService.decodeId(loyaltyId);
//        if(toDate==null){
//            toDate=LocalDateTime.now();
//        }
//        if(fromDate==null) {
//            fromDate=LocalDateTime.now().minusMonths(1);
//        }
        if(loyaltyId.equals("--")){
            Long contactId=sqIdService.decodeId(cid).get(0);
            return salesService.getSalesRecordsByContactId(contactId,pageNumber,pageSize,fromDate,toDate,currentUser.getUserId());
        }
        
        return transactionChildService.getTransactionsByLoyaltyId(idList.get(0),pageNumber,pageSize,transactionType,fromDate,toDate, currentUser.getUserId());
    }
    @GetMapping("/ereceipt")
    public  ResponseEntity getEreceiptLinkByDocSid(@RequestParam String docSid){
        User currentUser=getLoggedInUser.getLoggedInUser();
        return drSmsSentService.getEreceiptLink(currentUser.getUserId(), docSid);
    }
}

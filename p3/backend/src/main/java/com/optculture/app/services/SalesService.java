package com.optculture.app.services;

import java.time.LocalDateTime;
import java.util.*;

import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.sales.SalesDto;
import com.optculture.app.dto.transaction.TransactionDto;
import com.optculture.app.dto.transaction.TransactionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.optculture.app.repositories.SalesRepository;
import com.optculture.shared.entities.transactions.Sales;

@Service
public class SalesService {

	@Autowired
	private SalesRepository salesRepository;

	public List<Sales> getSales(Long userId, String docSid) {
		return salesRepository.findAllByUserIdAndDocSid(userId, docSid);
	}

	public Object[] getMaxAndAvgOrderValueByContactId(Long contactId, Long userId) {
				List<Object []> objects=salesRepository.findMaxAndAvgOrderValueByContactId(contactId,userId);
		return objects.get(0);

	}

    public List<Sales> getSalesByReceiptNumber(Long userId, String receiptNumber) {
		return salesRepository.findAllByUserIdAndReceiptNumber(userId, receiptNumber);
    }

	public ResponseEntity<ResponseObject<List<TransactionItem>>> getSalesRecordsByContactId(Long cid, int pageNumber, int pageSize, LocalDateTime fromDate, LocalDateTime toDate, Long userId) {
		PageRequest pr=PageRequest.of(pageNumber,pageSize, Sort.Direction.DESC,"createdDate");
		Page<SalesDto> salesDtoPage= salesRepository.findSalesByCid(cid,userId,fromDate,toDate,pr);
		List<TransactionItem> transactionItems=new ArrayList<>();
		for(SalesDto salesDto:salesDtoPage.getContent()){

			TransactionItem transacItem= new TransactionItem();
			transacItem.setCreatedDate(salesDto.getCreatedDate());
			transacItem.setTransactionType("Purchase");
			transacItem.setDocSID(salesDto.getDocSid());
			transacItem.setReceiptNumber(salesDto.getReceiptNumber());

			TransactionDto transacDto= new TransactionDto();
			transacDto.setAmount(salesDto.getAmount().toString());
			transacDto.setTransactionType("Total Amount");
			transacDto.setDocSID(salesDto.getDocSid());

			List<TransactionDto> transactionDtos=new ArrayList<>();
			transactionDtos.add(transacDto);
			transacItem.setTransactions(transactionDtos);
			transactionItems.add(transacItem);
		}
		return new ResponseEntity<>(new ResponseObject<List<TransactionItem>>(salesDtoPage.getTotalElements(),(long)salesDtoPage.getTotalPages(),transactionItems), HttpStatus.OK);
	}
}

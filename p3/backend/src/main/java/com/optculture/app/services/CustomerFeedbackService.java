package com.optculture.app.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.app.repositories.CustomerFeedbackRepository;
import com.optculture.shared.entities.contact.CustomerFeedback;

import io.jsonwebtoken.Claims;

@Service
public class CustomerFeedbackService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    CustomerFeedbackRepository customerFeedbackRepository;

    public void saveCustomerFeedBack(Map<String, Object> customerFeedBackData) {

        Claims data = jwtService.getClaims(customerFeedBackData.get("customerToken").toString());

        CustomerFeedback cfb = new CustomerFeedback();

        Map<String, Integer> udfMap = (Map<String, Integer>) customerFeedBackData.get("shoppingExp");

			cfb.setUdf1(customerFeedBackData.get("customerRating").toString());
			cfb.setUdf2(udfMap.get("udf2").toString());
			cfb.setUdf3(udfMap.get("udf3").toString());
			cfb.setUdf4(udfMap.get("udf4").toString());
			cfb.setUdf5(udfMap.get("udf5").toString());
			cfb.setUdf6(udfMap.get("udf6").toString());
			cfb.setUdf7(udfMap.get("udf7").toString());
			cfb.setFeedbackMessage(customerFeedBackData.get("optionalTextBox").toString());
			cfb.setCustomerNo(customerFeedBackData.get("mobilePhone").toString());
			cfb.setStore(customerFeedBackData.get("storeName").toString());
			cfb.setDocSid(customerFeedBackData.get("docSid").toString());
			cfb.setUserId(Long.valueOf(data.get("userId").toString()));
			cfb.setContactId(Long.valueOf(data.get("contactId").toString()));
			cfb.setSource("EReceiptNps");
			cfb.setCreatedDate(LocalDateTime.now());

        customerFeedbackRepository.save(cfb);
    }

    public CustomerFeedback findFirstByContactIdAndUserIdOrderByCreatedDateDesc(Long contactId, Long userId) {
        Optional<CustomerFeedback> optCustomerFeedback = customerFeedbackRepository.findFirstByContactIdAndUserIdOrderByCreatedDateDesc(contactId, userId);
        return optCustomerFeedback.isPresent() ? optCustomerFeedback.get() : null;
    }

    public boolean isReceiptLevelFeedbackAvailable(Long contactId, Long userId, String docSid) {
        return customerFeedbackRepository.findByContactIdAndUserIdAndDocSid(contactId, userId, docSid).size() == 0 ? false : true;
    }
}

package com.optculture.app.services;

import com.optculture.app.repositories.FaqRepository;
import com.optculture.shared.entities.config.Faq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaqService {
    @Autowired
    FaqRepository faqRepository;


    public String getTermsAndConditionsByUserId(Long userId) {
        Faq faq=faqRepository.findOneByUserId(userId);
        if(faq==null){
            return "NA";
        }
        return faq.getTermsAndCondition();
    }
}

package com.optculture.app.services;

import com.optculture.app.repositories.ValueCodesRepository;
import com.optculture.shared.entities.loyalty.ValueCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValueCodesService {
    @Autowired
    ValueCodesRepository valueCodesRepository;
    public List<String> getValueCodes(Long orgId) {
        List<ValueCodes> valueCodesList = valueCodesRepository.findByOrgId(orgId);
        List<String> listOfValueCodes = new ArrayList<>();
        for(ValueCodes valueCode : valueCodesList) {
            listOfValueCodes.add(valueCode.getValueCode());
        }
        return listOfValueCodes;
    }
}

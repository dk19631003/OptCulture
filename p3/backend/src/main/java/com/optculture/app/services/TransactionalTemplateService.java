package com.optculture.app.services;


import com.optculture.app.dto.campaign.TemplateDto;
import com.optculture.app.repositories.TransactionalTemplatesRepository;
import com.optculture.shared.entities.org.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionalTemplateService {

    @Autowired
    TransactionalTemplatesRepository trantemplateRepository;
    public ResponseEntity getPreApprovedTemplates(User user, int pageNumber,int pageSize, String templateName) {
        if(templateName.equals("--")) templateName=null;
        Page<TemplateDto> templateDtos= trantemplateRepository.findByOrgIdAndStatusOrderByModifiedDateDesc(user.getUserOrganization().getUserOrgId(),1,templateName, PageRequest.of(pageNumber,pageSize));
        return  new ResponseEntity<>(templateDtos,HttpStatus.OK);
    }
}

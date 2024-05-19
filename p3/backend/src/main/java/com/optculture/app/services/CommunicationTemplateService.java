package com.optculture.app.services;

import com.optculture.app.dto.campaign.TemplateDto;
import com.optculture.app.repositories.CommunicationTemplateRepository;
import com.optculture.shared.entities.communication.CommunicationTemplate;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.util.Constants;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommunicationTemplateService {
    @Autowired
    CommunicationTemplateRepository templateRepository;
    ModelMapper modelMapper = new ModelMapper();
    public ResponseEntity getPreApprovedTemplates(User user, int pageNumber, int pageSize, String templateName, String channelType) {
        if(templateName.equals("--")) templateName=null;
        Page<TemplateDto> templateDtos=null;
        if(channelType.equalsIgnoreCase("sms"))
            templateDtos= templateRepository.findByOrgIdAndStatusOrderByModifiedDateDesc(user.getUserOrganization().getUserOrgId(),"Approved",templateName, channelType, PageRequest.of(pageNumber,pageSize));
        else if(channelType.equalsIgnoreCase("whatsapp"))
            templateDtos=templateRepository.findByOrgIdAndStatus(user.getUserOrganization().getUserOrgId(),"Approved",templateName, channelType, PageRequest.of(pageNumber,pageSize));
        else templateDtos =  templateRepository.findByOrgIdAndTemplateNameOrderByModifiedDateDesc(user.getUserOrganization().getUserOrgId(),templateName,Constants.TYPE_EMAIL_CAMPAIGN,"beeEditor",PageRequest.of(pageNumber,pageSize));

        return  new ResponseEntity<>(templateDtos, HttpStatus.OK);
    }

    public ResponseEntity addWhatsappTemplate(TemplateDto templateDto, User user, String channelType) {
        if(templateDto !=null){
            CommunicationTemplate commTemplate= new CommunicationTemplate();
            commTemplate.setTemplateName(templateDto.getTemplateName());
            commTemplate.setTemplateRegisteredId(templateDto.getTemplateRegId());
            commTemplate.setMsgType(templateDto.getMsgType());
            commTemplate.setStatus("Approved");
            commTemplate.setMsgContent(templateDto.getTemplateContent());
            commTemplate.setFooter(templateDto.getFooter());
            commTemplate.setHeaderText(templateDto.getHeaderText());
            commTemplate.setUserId(user.getUserId());
            commTemplate.setOrgId(user.getUserOrganization().getUserOrgId());
            commTemplate.setCreatedDate(LocalDateTime.now());
            commTemplate.setChannelType(channelType);
//            System.out.println("+==-"+commTemplate.toString());
            templateRepository.save(commTemplate);
            return new ResponseEntity<>("Template added successfully !",HttpStatus.OK);
        }
        return new ResponseEntity<>("Error while adding template !",HttpStatus.OK);
    }
}

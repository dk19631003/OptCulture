package com.optculture.app.controllers;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.campaign.CampaignFilterReqDto;
import com.optculture.app.dto.campaign.CampaignSchRequest;
import com.optculture.app.dto.campaign.CommunicationDTO;
import com.optculture.app.dto.campaign.TemplateDto;
import com.optculture.app.dto.campaign.email.EmailDomainsDTO;
import com.optculture.app.dto.campaign.template.BeeEditorBody;
import com.optculture.app.repositories.EmailCommunicationSettingsRepository;
import com.optculture.app.services.CommunicationTemplateService;
import com.optculture.app.services.CommunicationService;
import com.optculture.app.services.UrlShortCodeMappingService;
import com.optculture.app.services.UserFromEmailIdService;
import com.optculture.app.util.TemplateImporter;
import com.optculture.shared.entities.communication.email.EmailCommunicationSettings;
import com.optculture.shared.entities.org.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaigns")
public class CommunicationController {
    @Autowired
    GetLoggedInUser getLoggedInUser;
    @Autowired
    CommunicationTemplateService templateService;
    @Autowired
    CommunicationService communicationService;
    @Autowired
    UrlShortCodeMappingService urlShortCodeMapService;
    @Autowired
    TemplateImporter templateImporter;

    @Autowired
    UserFromEmailIdService userFromEmailIdService;

    @Autowired
    EmailCommunicationSettingsRepository emailSettingsRepo;

    @PostMapping("/schedule")
    public ResponseEntity scheduleCampaign(@RequestBody CampaignSchRequest campaignSchReq){
        return  communicationService.scheduleCampaign(campaignSchReq);
    }
    @PostMapping("/list")
    public ResponseEntity getCampaignList(@RequestBody CampaignFilterReqDto campaignFiltReq){
        User user=getLoggedInUser.getLoggedInUser();
        Page<CommunicationDTO> communicationDTOS=communicationService.getCampaignListByChannelTypeAndUserId(user.getUserId(),campaignFiltReq);
        return new ResponseEntity(communicationDTOS,HttpStatus.OK); }
    @GetMapping("/campaign-id")
    public ResponseEntity getCampaignByCampaignId(@RequestParam Long campaignId){
        User user=getLoggedInUser.getLoggedInUser();
        CommunicationDTO commDTO= communicationService.getCampaignByCampaignId(campaignId, user);
        return  new ResponseEntity<>(commDTO, HttpStatus.OK);
    }

    @GetMapping("/approved-templates")
    public ResponseEntity getPreApprovedTemplates(@RequestParam int pageNumber, @RequestParam(defaultValue = "3" ,required = false) int pageSize, @RequestParam(defaultValue = "--",required = false) String templateName,@RequestParam String channelType){
        User user=getLoggedInUser.getLoggedInUser();
        return templateService.getPreApprovedTemplates(user,pageNumber,pageSize,templateName,channelType);
    }
    @GetMapping("/url-shortner")
    public ResponseEntity getURLShortCodes(@RequestParam List<String> urls){
        User user=getLoggedInUser.getLoggedInUser();
        return  urlShortCodeMapService.getURLShortCodes(urls,user.getUserId());
    }
    @PostMapping("/template-import")
    public  ResponseEntity importTemplates(@RequestParam("file") MultipartFile csvFile){
        try{
            File file = File.createTempFile("temp", null);

            // Copy the contents of the MultipartFile to the temporary file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(csvFile.getBytes());
            }
           return templateImporter.processFile(file);
        } catch (IOException e) {
            // handle exception
        }
        return  new ResponseEntity<>("",HttpStatus.OK);
    }

    @PostMapping("/add-template")
    public ResponseEntity addWhatsappTemplate(@RequestBody TemplateDto templateDto){
        User user=getLoggedInUser.getLoggedInUser();
        return templateService.addWhatsappTemplate(templateDto,user,"WhatsApp");
    }
    
    @PostMapping("/saveBeeCommunicationTemplate")
    public ResponseEntity saveInCommunicationTemplate(@RequestBody BeeEditorBody beeEditorBody) {
       
 	   User user= getLoggedInUser.getLoggedInUser();

        String json = beeEditorBody.getJson();
 	   String html = beeEditorBody.getHtml();
 	   Long communicationId = beeEditorBody.getCommId();
 	   
 	  return new ResponseEntity(communicationService.updateBeeInCommunication(communicationId,user,json,html),HttpStatus.OK);
 	   
    }

    @GetMapping("/approved-emails")
    public ResponseEntity getUserEmailDomains(){
        User user=getLoggedInUser.getLoggedInUser();
        EmailDomainsDTO emailDomains =userFromEmailIdService.getUserEmailDomains(user.getUserId());
        return  new ResponseEntity<>(emailDomains,HttpStatus.OK);
    }
    @PostMapping("/save-email-settings")
    public  ResponseEntity saveEmailSettings(@RequestBody EmailCommunicationSettings emailSettings){
        try {
            User user = getLoggedInUser.getLoggedInUser();
            emailSettings.setUserId(user.getUserId());
            System.out.println(emailSettings.toString());
            emailSettingsRepo.save(emailSettings);
            return new ResponseEntity<>("Emails settings saved", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Exception while saving email changes", HttpStatus.OK);
        }
    }
    @GetMapping("/email-settings")
    public ResponseEntity getEmailSettings(){
        User user = getLoggedInUser.getLoggedInUser();
        Optional<EmailCommunicationSettings> emailSettingsOpt=emailSettingsRepo.findByUserId(user.getUserId());
        if(emailSettingsOpt.isPresent()) return new ResponseEntity<>(emailSettingsOpt.get(),HttpStatus.OK);
        return new ResponseEntity("No settings found !",HttpStatus.NOT_FOUND);
    }
}

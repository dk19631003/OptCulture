package com.optculture.app.controllers;

import com.optculture.app.services.BeeEditorService;
import com.optculture.app.services.CommunicationService;
import com.optculture.app.services.EmailTemplateService;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.campaign.TemplateDto;
import com.optculture.app.dto.campaign.template.BeeEditorBody;
import com.optculture.shared.entities.communication.email.MyTemplates;
import com.optculture.shared.entities.org.User;
import com.optculture.app.repositories.UserRepository;
import com.optculture.app.repositories.ApplicationPropertiesRepository;


@RestController
@CrossOrigin
@RequestMapping("/api/bee")
public class BeeController {

	@Autowired
	private BeeEditorService beeEditorService;
	
	@Autowired
	private GetLoggedInUser getLoggedInUser;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ApplicationPropertiesRepository applicationPropertiesRepository;
	
	@Autowired
	private EmailTemplateService emailTemplateService;
	@Value("${beeClientKeyvalueDR}")
	String beeClientKeyvalueDR;
	@Value("${beeFileManagerKey}") 
	String beeFileManagerKey;
	@Autowired
	private CommunicationService communicationService;
	
    private Logger logger = LoggerFactory.getLogger(BeeController.class);
    
   @GetMapping("/externalUrl")
	public String getAllExternalUrlForUser(@RequestParam String type){
		       User user= getLoggedInUser.getLoggedInUser();
	   logger.info("Entered the external url method to get the mapping. {}",type);

			JSONArray result =  beeEditorService.dynamicUrlforCustomRowsBeeEditor(type,user);
			logger.info("result for the external url : "+result.toString());
			return result.toString();
	}
   
   @GetMapping("/beeKey")
   public String  getBeeKey(@RequestParam String type) {
	   logger.info("Getting beekey from app.props DB. for :"+type);
	   switch (type){
		   case "filemanager":return beeFileManagerKey;
		   default:return beeClientKeyvalueDR;
	   }
//		return applicationPropertiesRepository.findByKey("beeClientKeyvalueDR");
	}
   
   //to get all the template on the screen.getAllUserTemplates
   //   (, @RequestParam(defaultValue = "3" ,required = false) int pageSize, @RequestParam(defaultValue = "--",required = false) String templateName,@RequestParam String channelType){

//   @GetMapping("/getUserTemplates") 
//   public Page<TemplateDto> getAllUserTemplates(@RequestParam(defaultValue = "--",required= false)String templateName,@RequestParam int pageNumber,@RequestParam int pageSize) {
//      // User user= getLoggedInUser.getLoggedInUser();
//	   
//	   User user = userRepository.findByUserId(240L);
//	logger.info("Entered in get all user template");
//	Page<TemplateDto> result = emailTemplateService.getAllUserTemplates(user,templateName,pageNumber,pageSize);
//	//   List<MyTemplates> result = new ArrayList<>();
//	 //  System.out.println(">>>> "+result);
//	  
//	   	   
//	   return result;
//   }
//   
   @PostMapping("/saveTemplate")
   public ResponseEntity saveBeeTemplate(@RequestBody BeeEditorBody beeEditorBody) {
		logger.info("This is the call for saving Template rows"+beeEditorBody.getHtml());
		String response = "Success";
		try {
			logger.info("This is the call for saving Template rows"+beeEditorBody.getJson());

			String templateName = beeEditorBody.getTemplateName();
			logger.info("Template Name  : "+templateName);
	        User user= getLoggedInUser.getLoggedInUser();

	        String json = beeEditorBody.getJson();
	        String html = beeEditorBody.getHtml();
       // Extracting JSON and HTML from each object in the array
	        
     
 	   response = beeEditorService.saveBeeTemplateInCommunicationTemplate(json, html, user,templateName);

		}catch(Exception e) {
			logger.error("Exception while saving template :"+e);
			return new ResponseEntity(e,HttpStatus.OK);
		}
	    return new ResponseEntity(response,HttpStatus.OK);
   }
   
  
  
   @PostMapping("/saveCustomRow")
   public ResponseEntity saveCustomRow(@RequestBody BeeEditorBody jsonObj) {
		String response = "Success";
		try {
			logger.info("This is the call for saving custom rows"+jsonObj);
	        User user= getLoggedInUser.getLoggedInUser();

			   String json = jsonObj.getJson();
			   String html = jsonObj.getHtml();
		       
		
	   response = beeEditorService.saveCustomRowForUser(json,html, user);
		}catch(Exception e) {
			logger.info("Exception while saving custom rows,"+e);
			return new ResponseEntity(e,HttpStatus.OK);
		}
	    return new ResponseEntity(response,HttpStatus.OK);

   }
   
   @GetMapping("/dynamicBarCodes")
   public List<String> getDynamicBarCode() {
      User user= getLoggedInUser.getLoggedInUser();


       String response = "";
       try {
    	   return beeEditorService.getBarCodesforUser(user);
    	   
       }catch(Exception e) {
    	   logger.error("Exception while getting dynamic content",e);
    	   return new ArrayList<String>();
       }

   }
}

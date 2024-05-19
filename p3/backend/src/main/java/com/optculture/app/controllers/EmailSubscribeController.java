package com.optculture.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optculture.app.services.EmailSubscribeService;

/*
 * @written to handle unsubscribe and resubscribe for email communication. 
 */
@RestController
@RequestMapping("/api/service")
public class EmailSubscribeController {
	
	Logger logger = LoggerFactory.getLogger(EmailSubscribeController.class);
	
	@Autowired
	EmailSubscribeService emailSubscribeService;
	//emailId=sameera@optculture.com&campId=9&crId=170&userId=1&cId=19466776
	@PostMapping("/unsubscribe")
	public ResponseEntity requestToUnsubscribe(
		@RequestParam String hash) 
	/*@RequestParam String email ,
	@RequestParam String campId,@RequestParam String crId,
	@RequestParam String userId,@RequestParam String cId) { */

	{
		String response= "Success";
		try {
			response = emailSubscribeService.processUnsubscribeEvent(hash.trim());//email,campId,userId,cId,crId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity(response, HttpStatus.OK);
		
	}
	
	@GetMapping("/get/unsubscribe")
	public String getRequestToUnsubscribe(@RequestParam String hash) {
		
		String response  = "Success";
		try {
			response = emailSubscribeService.processUnsubscribeEvent(hash.trim());//email,campId,userId,cId,crId);
			System.out.println("Unsubscribing the service");
		}catch(Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		return "<h1>Unsubscribing...</h1>";
	}

}

package com.optculture.app.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.repositories.UserRepository;
import com.optculture.app.services.BEECustomRowsService;
import com.optculture.shared.entities.org.User;

@RestController
@RequestMapping("/api/custom-rows")
public class BEECustomRowsController {

	@Autowired
	private BEECustomRowsService beeCustomRowsService;
	
	@Autowired
	private GetLoggedInUser getLoggedInUser;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/savedRows")
	public String getCustomRows(@RequestParam String name) {
		
		return beeCustomRowsService.getCustomRowsByType(name);
	}
	
	@GetMapping("/userDesignedRows")
	public String getUserDesignedRows(@RequestParam String name,@RequestParam String userId) {
				
		return beeCustomRowsService.getUserDesignedCustomRowsByUser(Long.valueOf(userId),name);
	}
	

}

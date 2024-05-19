package com.optculture.app.controllers;


import com.optculture.app.dto.login.AuthRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.optculture.app.dto.login.AuthResponseDTO;
import com.optculture.app.services.AuthService;

@RestController
@RequestMapping("/api/login")
public class AuthController {
	
	Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private AuthService authService; 

	@PostMapping("/authenticate")
	public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO dto) {
		logger.info("Inside Authenticate method of Class AuthController");
		return ResponseEntity.ok(authService.authenticate(dto));
	}
	@GetMapping("/switch-user")
	public ResponseEntity<AuthResponseDTO> switchUser(@RequestParam String userName){
		return authService.switchUser(userName);
	}
}

package com.optculture.app.services;

import com.optculture.app.dto.config.PosMappingDto;
import com.optculture.app.dto.login.AuthRequestDTO;
import com.optculture.app.dto.login.AuthResponseDTO;
import com.optculture.app.dto.login.UserData;
import com.optculture.shared.entities.org.User;
import lombok.Data;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Data
@Service
public class AuthService {
	

	Logger logger = LoggerFactory.getLogger(AuthService.class);

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserService userService;
	@Autowired
	private PosMappingService posMappingService;
	@Autowired
	private ValueCodesService valueCodesService;

	@Value("${jwt.expiration}")
	private Long expiration;


	public AuthResponseDTO authenticate(AuthRequestDTO dto) {

		logger.trace("Username: {}", dto.getUserName());
		final User user = userService.findByUserName(dto.getUserName().concat("__org__").concat(dto.getOrganizationId()));
		if(user==null) return new AuthResponseDTO("",false);
		logger.info("{} {} from db", user.getUserName(), user.getPassword());
		
		boolean matched = encryptPwd(dto.getPassword(),user.getPassword());
		if(!matched) return new AuthResponseDTO("",false);
		


		return getUserData(user); //returns userdata,token
	}

	public boolean encryptPwd(String reqPassword,String DBPassword) {
		
		var result = BCrypt.checkpw(reqPassword, DBPassword);
		logger.debug("Password match {}", result);
		return result;
	
	}

	public ResponseEntity<AuthResponseDTO> switchUser(String userName) {
		User user=userService.findByUserName(userName);
		return new ResponseEntity<>(getUserData(user), HttpStatus.OK);
	}
	public AuthResponseDTO getUserData(User user){ //abilties and token ,userdata preparing
		List<String> abilities = userService.getAbilities(user.getUserId());
		User parentUser=user;
		if(!user.getAccountType().equalsIgnoreCase("primary")){
			parentUser =userService.findParentUser(user.getUserOrganization().getUserOrgId());

		}
		AuthResponseDTO authResponseDTO  = new AuthResponseDTO(jwtService.generateToken(parentUser, abilities),true);
		// List<String> authorities = userService.getAuthorities(user.getUserId());
		List<Map<String,String>> userAbilities = new ArrayList<>();
		
		// new ArrayList<>(Arrays.asList(userAbilities));
		
		Map<String,String> userAbility = new HashMap<>();
//		userAbility.put("action","manage");
//		userAbility.put("subject", "all");
		userAbilities.add(userAbility);

		for(String authority: abilities) {
			Map<String,String> ability = new HashMap<>();
			ability.put("action", authority.substring(0, 4));
			ability.put("subject", authority.substring(4));
			userAbilities.add(ability);
		}
		
		List<PosMappingDto> posMappingList= posMappingService.getPosMappingListByUserId(user.getUserId(),"Contacts");
		List<String> valueCodes=valueCodesService.getValueCodes(user.getUserOrganization().getUserOrgId());
		UserData userData = new UserData();
		userData.setUserName(user.getUserName());
		userData.setEmailId(user.getEmailId());
		userData.setCompleteAddress(userData.getAddress(user));
		userData.setCompanyName(user.getCompanyName());
		userData.setRole("admin");
		userData.setCurrencyInfo(userService.getCurrencyInfo(user.getCountryType()));
		userData.setPosMappingList(posMappingList);
		userData.setValueCodes(valueCodes);
		authResponseDTO.setUserAbilities(userAbilities);
		authResponseDTO.setExpirationTime((System.currentTimeMillis() + expiration));
		authResponseDTO.setUserData(userData);

		return authResponseDTO;
	}
}

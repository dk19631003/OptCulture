package com.optculture.app.services;

import java.util.*;

import com.optculture.app.dto.user.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.optculture.shared.entities.org.User;
import com.optculture.app.repositories.UserRepository;

@Service
public class UserService {

	Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	public UserService(UserRepository repo) {
		this.userRepository = repo;
	}

	public User getUserByUserId(Long userId) {

		return userRepository.findByUserId(userId);
	}

	public User findByUserName(String userName) {
		logger.info("Username Inside User Service {}", userName);
		return userRepository.findByUserName(userName);
	}

	List<String> getAuthorities(Long userId) {
		return userRepository.getAuthorities(userId);
	}

	List<String> getAbilities(Long userId) {
		return userRepository.getAbilities(userId);
	}
	public List<UserDto> fetchAllUsers() {
		List<UserDto> userList=userRepository.findByEnabledAndAccountTypeOrderByCreatedDateDesc(true,"primary");
		return  userList;
	}

	public User findParentUser(Long userOrgId) {
		return userRepository.findFirstByAccountTypeAndUserOrganizationUserOrgIdOrderByUserIdAsc("primary",userOrgId);
	}

	public String getCurrencyInfo(String countryName) {

			if (countryName.equalsIgnoreCase("US")) {
				return "en-US,USD";
			}
			if (countryName.equalsIgnoreCase("Myanmar")) {
				return "my-MM,MMK";
			}
			if(countryName.equalsIgnoreCase("UAE")){
				countryName="AE"; //for arab emirates
			}

			String countryCode = getCountryCode(countryName);
//			System.out.println("%s : %s".formatted(countryName, countryCode));

		return countryCode;
	}

	// Method to get the country code from the country name
	public static String getCountryCode(String countryName) {
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			if (locale.getDisplayCountry().equalsIgnoreCase(countryName) || locale.getCountry().equalsIgnoreCase(countryName) ) {
				String localString =locale.toString();
				if(localString.indexOf('#')!=-1){ //removing extra unnecessary infor.
					localString=localString.substring(0,localString.indexOf('#')-1);
				}
				localString=localString.replace('_','-'); // for js locale obj requirement
				Locale local= new Locale("",locale.getCountry());
				Currency currency= Currency.getInstance(locale);
				return localString+","+currency.getCurrencyCode();
			}
		}
		return "";
	}
}

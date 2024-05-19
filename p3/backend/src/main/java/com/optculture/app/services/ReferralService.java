package com.optculture.app.services;
import com.optculture.app.dto.referral.ReferralCodeInfoDto;
import com.optculture.app.dto.referral.ReferralEnquiryRequestDto;
import com.optculture.app.dto.referral.UserDetailsDto;
import com.optculture.app.repositories.UserRepository;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.entities.org.UserOrganization;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ReferralService {

    Logger logger = LoggerFactory.getLogger(ReferralService.class);

    @Value("${baseUrl}")
    private String baseUrl;
//    @Autowired
//    private final RestTemplate restTemplate;

//    public ReferralService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public String findReferralData(Map<String, Object> data) {
        Claims claimsData = jwtService.getClaims(data.get("customerToken").toString());
        Long userId = Long.valueOf(claimsData.get("userId").toString());
        String mobileNumber = data.get("mobileNumber").toString();
        String userName = "";
        String orgId = "";
        String token = "";
        String endPoint = "";
        endPoint = baseUrl + "ReferralCodeEnquiryRequestOPT.mqrm";
        User userObj = userRepository.findByUserId(userId);
        String[] fullName = userObj.getUserName().split("__");
        userName = fullName[0];
        token = userObj.getToken();
        logger.info("UserName :" + userName + " Token :" + token);
        UserOrganization userOrganizationObj = userObj.getUserOrganization();
        orgId = userOrganizationObj.getOrgExternalId();
        if (token == null) {
            logger.info("Token not found in Users table :: So trying with Optsync key");
            token = userOrganizationObj.getOptSyncKey();
        }
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //Create RequestBody using Dtos
//        ReferralCodeInfoDto referralCodeInfoDto = new ReferralCodeInfoDto();
//        referralCodeInfoDto.setPHONE(mobileNumber);
//        UserDetailsDto userDetailsDto = new UserDetailsDto();
//        userDetailsDto.setORGID(orgId);
//        userDetailsDto.setUSERNAME(userName);
//        userDetailsDto.setTOKEN(token);
        ReferralEnquiryRequestDto requestBody = new ReferralEnquiryRequestDto();
//        requestBody.setREFERRALCODEINFO(referralCodeInfoDto);
//        requestBody.setUSERDETAILS(userDetailsDto);
        requestBody.setREFERRALCODEINFO(new ReferralCodeInfoDto(mobileNumber));
        requestBody.setUSERDETAILS(new UserDetailsDto(userName, orgId, token));

        // Create HttpEntity with headers and body
        HttpEntity<ReferralEnquiryRequestDto> requestEntity = new HttpEntity<>(requestBody, headers);
        logger.info("Request :"+requestEntity.getBody());

        //Creating RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        //Sending the Post Request
        ResponseEntity<String> response = null;
        try {
        response = restTemplate.postForEntity(endPoint, requestEntity, String.class);
        logger.info("Response :",response);
        } catch (RestClientException e) {
            logger.error("Exception While posting Delivery event :", e);
        }
        return response.getBody();
    }
}

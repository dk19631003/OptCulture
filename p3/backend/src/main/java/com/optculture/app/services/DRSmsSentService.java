package com.optculture.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.optculture.shared.entities.communication.ereceipt.DRSmsSent;
import com.optculture.app.repositories.DRSmsSentRepository;

@Service
public class DRSmsSentService {
	@Value("${ereceiptUrl}")
	private String ereceiptUrl;
	@Autowired
	private DRSmsSentRepository drSmsSentRepository;

	public DRSmsSent getDRSmsSentByOriginalShortCode(String originalShortCode) {
		return drSmsSentRepository.findFirstByOriginalShortCodeOrderByIdDesc(originalShortCode);
	}
	public ResponseEntity getEreceiptLink(Long userId, String docSid){
		DRSmsSent drSmsSent= drSmsSentRepository.findByUserIdAndDocSid(userId,docSid);
		if(drSmsSent==null) return  new ResponseEntity<>("Ereceipt Link Not Found", HttpStatus.OK);
		String originalShortCode=drSmsSent.getOriginalShortCode();
		String ereceiptLink=ereceiptUrl+originalShortCode;
		return  new ResponseEntity<>(ereceiptLink,HttpStatus.OK);
	}
}

package com.optculture.launchpad.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optculture.launchpad.dto.CommunicationJSONDump;

@Service
public class CommunicationService {
	
	Logger logger = LoggerFactory.getLogger(CommunicationService.class);
	
	ObjectMapper objectMapper;

	    public CommunicationService( ObjectMapper objectMapper) {
	        this.objectMapper = objectMapper;
	    }

	    public CommunicationJSONDump retrieveCommunicationJSONDumpById(String commAttribute) {
	      //  Optional<Communication> CommunicationOptional = CommunicationRepository.findById(CommunicationId);

	        if (!commAttribute.isEmpty()) {
	            try {
	                CommunicationJSONDump commJSONDump = objectMapper.readValue(commAttribute, CommunicationJSONDump.class);

	                if (commJSONDump instanceof CommunicationJSONDump) {
	                    return commJSONDump;
	                }
	            } catch (Exception e) {
	                logger.error("Exception :",e);
	            }
	        }
	        return null; // Handle the case where the data is not found or not a CommunicationJSONDump
	    }

	

}

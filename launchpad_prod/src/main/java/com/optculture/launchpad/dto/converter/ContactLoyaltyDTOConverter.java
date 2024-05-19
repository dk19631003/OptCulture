package com.optculture.launchpad.dto.converter;


import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.optculture.launchpad.dto.ContactLoyaltyDTO;
import com.optculture.shared.entities.contact.ContactLoyalty;

@Service
public class ContactLoyaltyDTOConverter {
	ModelMapper modelMapper = new ModelMapper();
	Logger logger = LoggerFactory.getLogger(ContactLoyaltyDTOConverter.class);

	
	public ContactLoyaltyDTO convert(ContactLoyalty contactLoyalty) {
		ContactLoyaltyDTO contactLoyaltyDTO = null;
		try {
		TypeMap<ContactLoyalty,ContactLoyaltyDTO> result =
				modelMapper.typeMap(ContactLoyalty.class,
						ContactLoyaltyDTO.class).addMappings(mapper -> 
						{ 

							mapper.map( src -> contactLoyalty.getCardPin(),
									ContactLoyaltyDTO::setMembershipPin);

							mapper.map(src -> contactLoyalty.getCardNumber(),
									ContactLoyaltyDTO  ::setMembershipNumber);
							
							mapper.map(src -> contactLoyalty.getCreatedDate(),
									ContactLoyaltyDTO  ::setEnrollmentDate);
							
							mapper.map(src -> contactLoyalty.getTotalLoyaltyPointsEarned(),
									ContactLoyaltyDTO  ::setLifetimePoints);
							
							mapper.map(src -> contactLoyalty.getPosStoreLocationId(),
									ContactLoyaltyDTO  ::setEnrollmentStore);
							
														

						});

		contactLoyaltyDTO = result.map(contactLoyalty);
		}catch(Exception e) {
			logger.error("Exception while loadinf contact Loyalty converter for "+contactLoyalty.getContact().getContactId());
		}
		return contactLoyaltyDTO;
	}
	
	
}

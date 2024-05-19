package com.optculture.launchpad.dto.converter;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Service;

import com.optculture.launchpad.dto.ContactDTO;
import com.optculture.shared.entities.contact.Contact;

@Service
public class ContactDTOConverter {
	ModelMapper modelMapper = new ModelMapper();
	
	public ContactDTO convert(Contact contact) {
		ContactDTO contactDTO = null;
		
		
		TypeMap<Contact,ContactDTO> result =
				modelMapper.typeMap(Contact.class,
						ContactDTO.class).addMappings(mapper -> 
						{ 

							mapper.map( src -> contact.getZip(),
									ContactDTO::setPin);
						});
		
		contactDTO = result.map(contact);
		return  contactDTO;
		
	}
	
}

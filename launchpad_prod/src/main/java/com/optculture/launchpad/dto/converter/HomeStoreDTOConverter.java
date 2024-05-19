package com.optculture.launchpad.dto.converter;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Service;

import com.optculture.launchpad.dto.HomeStoreDTO;
import com.optculture.shared.entities.org.User;

@Service
public class HomeStoreDTOConverter {
ModelMapper modelMapper = new ModelMapper();


	
	public HomeStoreDTO convert(User user) {
		HomeStoreDTO  homeStoreDTO = null;
		String fullAddress = "";

		TypeMap<User,HomeStoreDTO> result =
				modelMapper.typeMap(User.class,
						HomeStoreDTO.class).addMappings(mapper -> 
						{ 

							mapper.map( src -> user.getAddressOne(),
									HomeStoreDTO::setStreet);

							mapper.map(src -> user.getPinCode(),
									HomeStoreDTO  ::setZip);						

						});

		 homeStoreDTO= result.map(user);
		 
		 if(user.getAddressOne()!=null && !user.getAddressOne().trim().equals("")){
				fullAddress =  fullAddress + user.getAddressOne(); 
			}
			if(user.getAddressTwo()!=null && !user.getAddressTwo().trim().equals("")){
				fullAddress = fullAddress + ", " + user.getAddressTwo(); 
			}
			if(user.getCity()!=null && !user.getCity().trim().equals("")){
				fullAddress = fullAddress + ", " + user.getCity(); 
			}
			if(user.getState()!=null && !user.getState().trim().equals("")){
				fullAddress = fullAddress + ", " + user.getState(); 
			}
			if(user.getCountry()!=null && !user.getCountry().trim().equals("")){
				fullAddress = fullAddress + ", " + user.getCountry(); 
			}
			if(user.getPinCode()!=null && !user.getPinCode().trim().equals("")){
				fullAddress = fullAddress + ", " + user.getPinCode(); 
			}
			if(user.getPhone()!=null && !user.getPhone().trim().equals("")){
				fullAddress = fullAddress + " | Phone: " + user.getPhone(); 
			}

			homeStoreDTO.setAddressStr(fullAddress);
			
		return homeStoreDTO;
	}

	
}

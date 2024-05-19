package com.optculture.app.services;

import com.optculture.app.dto.config.PosMappingDto;
import com.optculture.app.repositories.PosMappingRepository;
import com.optculture.shared.entities.config.PosMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PosMappingService {

    @Autowired
    PosMappingRepository posMappingRepository;
//    @Cacheable(value = "posMappingList",key = "#userId+'_'+#mappingType")
//    @Cacheable("posMappingList")
    public List<PosMappingDto> getPosMappingListByUserId(Long userId, String mappingType) {

        List<PosMapping> posMappingList=posMappingRepository.findByUserIdAndMappingTypeOrderByCustomFieldName(userId,mappingType);
        List<PosMappingDto> posMappingDtoList= new ArrayList<>();
        for(PosMapping posMapping:posMappingList){
            if(posMapping.getCustomFieldName().toLowerCase().startsWith("udf")){
                PosMappingDto posMappingDto= new PosMappingDto();
                posMappingDto.setCustFieldName(posMapping.getCustomFieldName().toLowerCase());
                posMappingDto.setDataType(posMapping.getDataType());
                posMappingDto.setDisplayLabel(posMapping.getDisplayLabel());
                posMappingDtoList.add(posMappingDto);
            }
        }
        return  posMappingDtoList;
    }
    
    public List<Map<String, String>> getPosMappedUdfs(Long userId) {

		List<Map<String, String>> listOfMaps = new ArrayList<>();
		String[] mappingTypes = { "Contacts", "Sales", "SKU" };

		for (String mappingType : mappingTypes) {
			Map<String, String> map = new LinkedHashMap<>();
			List<PosMapping> posMappingList = posMappingRepository.findByUserIdAndMappingTypeOrderByCustomFieldName(userId, mappingType);
			String alias = mappingType.equals("Contacts") ? "c." : (mappingType.equals("Sales") ? "sal." : "sku.");
			
			posMappingList.forEach(posMapping -> {
				if (posMapping.getCustomFieldName().toLowerCase().startsWith("udf"))
					map.put(posMapping.getDisplayLabel(), alias + posMapping.getCustomFieldName().toLowerCase());
			});
			listOfMaps.add(map);
		}

		return listOfMaps;
    }
}

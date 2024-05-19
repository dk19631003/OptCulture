package com.optculture.app.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.optculture.shared.entities.config.ApplicationProperties;


public interface ApplicationPropertiesRepository extends JpaRepository<ApplicationProperties,Long> {
    
	@Query("Select app.value from ApplicationProperties app where app.key = :key")
    String findByKey(String key);
    
   // @Query(value="select * application_properties where props_key in (:type) and props_value is not null", nativeQuery=true)
    List<ApplicationProperties> findByKeyIn(List<String> type);
    
}

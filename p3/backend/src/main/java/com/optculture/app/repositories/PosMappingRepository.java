package com.optculture.app.repositories;

import com.optculture.shared.entities.config.PosMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PosMappingRepository extends JpaRepository<PosMapping,Long> {

    List<PosMapping> findByUserIdAndMappingTypeOrderByCustomFieldName(Long userId, String mappingType);
}

package com.optculture.app.repositories;

import com.optculture.app.dto.campaign.email.OrgStoreDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.org.OrganizationStore;

import java.util.List;

@Repository
public interface OrganizationStoreRepository extends JpaRepository<OrganizationStore, Long>{
	
	OrganizationStore findFirstByHomeStoreIdAndUserOrganizationUserOrgId(String homeStoreId, Long userOrgId);
	
	OrganizationStore findFirstByHomeStoreIdAndUserOrganizationUserOrgIdAndSubsidiaryId(String homeStoreId, Long userOrgId, String subsidiaryId);

	@Query("SELECT new com.optculture.app.dto.campaign.email.OrgStoreDTO(s.storeId as storeId,s.storeName as storeName,s.addressStr as address) FROM OrganizationStore s WHERE s.userOrganization.userOrgId = :userOrgId")
	List<OrgStoreDTO> findStoreListByOrgId(Long userOrgId);

}

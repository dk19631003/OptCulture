package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.org.OrganizationStore;

@Repository
public interface OrganizationStoreRepository extends JpaRepository<OrganizationStore, Long> {

	@Query("select org from OrganizationStore org where org.homeStoreId= :homeId and org.userOrganization.userOrgId =:orgId")
	OrganizationStore findByhomeStoreId(String homeId,Long orgId);
	
	@Query("select org from OrganizationStore org where org.homeStoreId= :homeId and org.userOrganization.userOrgId = :orgId and org.subsidiaryId=:subsidiaryNumber")
	OrganizationStore findByhomeStoreIdAndSubsidiaryNumber(String homeId,Long orgId,String subsidiaryNumber);
}

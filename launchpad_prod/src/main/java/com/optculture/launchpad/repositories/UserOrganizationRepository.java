package com.optculture.launchpad.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.org.UserOrganization;


public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {
	

	UserOrganization findByUserOrgId(Long orgId);
}

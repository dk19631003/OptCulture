package com.optculture.app.services;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.campaign.email.OrgStoreDTO;
import com.optculture.shared.entities.org.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.app.repositories.OrganizationStoreRepository;
import com.optculture.shared.entities.org.OrganizationStore;

import java.util.List;

@Service
public class OrganizationStoreService {

	@Autowired
	OrganizationStoreRepository storesRepository;

	@Autowired
	GetLoggedInUser getLoggedInUser;


	public OrganizationStore getOrgStore(String homeStoreId, Long userOrgId, String subsidiaryNumber) {
		try {
			return storesRepository.findFirstByHomeStoreIdAndUserOrganizationUserOrgId(homeStoreId, userOrgId);
		} catch (Exception e) {
			return storesRepository.findFirstByHomeStoreIdAndUserOrganizationUserOrgIdAndSubsidiaryId(homeStoreId, userOrgId, subsidiaryNumber);
		}
	}
	public List<OrgStoreDTO> getStoreList() {
		User user=getLoggedInUser.getLoggedInUser();
		List<OrgStoreDTO> storeList=storesRepository.findStoreListByOrgId(user.getUserOrganization().getUserOrgId());
		return storeList;
	}

}

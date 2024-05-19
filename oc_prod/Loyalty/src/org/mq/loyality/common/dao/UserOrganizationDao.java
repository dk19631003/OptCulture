package org.mq.loyality.common.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrganizationDao  extends AbstractDAO<UserOrganization, Long> {

	List<UserOrganization> getOrgDetails(Long url);
}

package com.optculture.app.repositories;

import java.util.List;

import com.optculture.app.dto.user.UserDto;
import com.optculture.shared.entities.org.OrganizationStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.optculture.shared.entities.org.User;

//@RepositoryRestResource(exported = true, path = "user")
//public interface UserRepository extends PagingAndSortingRepository<Users, Long> {
//
//}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUserId(Long id);

	User findByUserName(String userName);


	@Query("select distinct name from User u join u.roles r where userId = :userId")
	List<String> getAuthorities(@Param("userId") Long userId);

	@Query("SELECT DISTINCT g.name FROM User u JOIN u.roles r JOIN r.groups g WHERE u.userId = :userId")
	List<String> getAbilities(@Param("userId") Long userId);
	
	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.enableSmartEReceipt = true WHERE u.userId = :userId")
	int enableSmartEreceipt(@Param(value = "userId") Long userId);
	@Query("select new com.optculture.app.dto.user.UserDto(u.userName as userName,u.companyName as companyName) from User u where u.enabled = :b and u.accountType= :accountType")
	List<UserDto> findByEnabledAndAccountTypeOrderByCreatedDateDesc(boolean b,String accountType);

	User findFirstByAccountTypeAndUserOrganizationUserOrgIdOrderByUserIdAsc(String AccountType,Long userOrgId);
}

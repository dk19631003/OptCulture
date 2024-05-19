package com.optculture.api.repositories;

import com.optculture.shared.entities.org.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    //@Query("select new com.optculture.app.dto.user.UserDto(u.userName as userName,u.companyName as companyName) from User u where u.enabled = :b and u.accountType= :accountType")
    //List<UserDto> findByEnabledAndAccountTypeOrderByCreatedDateDesc(boolean b, String accountType);

    User findFirstByAccountTypeAndUserOrganizationUserOrgIdOrderByUserIdAsc(String AccountType,Long userOrgId);

    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.token = :token and u.enabled = TRUE " +
            "AND u.packageExpiryDate > CURRENT_TIMESTAMP")
    User findByUserNameAndToken(@Param("userName") String userName,@Param("token") String token);

    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.userOrganization.optSyncKey = :token and u.enabled = TRUE AND u.packageExpiryDate > CURRENT_TIMESTAMP")
    User findByUserNameOptSyncKey(@Param("userName") String userName, @Param("token") String token);
}
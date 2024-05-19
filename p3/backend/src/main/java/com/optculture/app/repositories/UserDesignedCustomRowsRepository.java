package com.optculture.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.communication.email.UserDesignedCustomRows;

@Repository
public interface UserDesignedCustomRowsRepository extends JpaRepository<UserDesignedCustomRows,Long> {
    
  List<UserDesignedCustomRowsRepository> findByUserIdAndRowCategory(Long userId, String rowCategory);
  
  
  @Query(value="select templatename from UserDesignedCustomRows where user_id=:userId and rowjsondata is not null group by rowcategory",nativeQuery=true)
  List<String> findByUserId(Long userId);
  
  @Query(value="select * from UserDesignedCustomRows where user_id=:userId and templatename =:name and rowjsondata is not null",nativeQuery=true)
  UserDesignedCustomRows findByUserIdAndTemplateName(Long userId, String name);
}

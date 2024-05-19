package com.optculture.app.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.transactions.Sku;

import java.util.List;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
	
	Sku findOneBySkuId(Long skuId);
	@Query("SELECT rps.description AS title, rps.udf1 as url , rps.listPrice as itemPrice, " +
			"rps.udf10 AS imgUrl, rps.udf12 AS barCode, rps.departmentCode as departmentCode " +
			"FROM Sku rps WHERE rps.userId = :userId AND rps.departmentCode= :deptStr " +
			"AND rps.udf10 IS NOT NULL AND rps.description IS NOT NULL AND rps.udf12 IS NOT NULL")
	List<Object[]> findByUserIdAndDepartmentCode(@Param("userId") Long userId, @Param("deptStr") String deptStrJson, PageRequest request);

	@Query("SELECT DISTINCT r.departmentCode " +
			"FROM Sku r " +
			"WHERE r.userId = :userId AND r.departmentCode IS NOT NULL AND r.udf12 IN :barCodes")
	List<String> findByUserIdAndUdf12(@Param("userId") Long userId, @Param("barCodes") List<String> udf12Values);



}

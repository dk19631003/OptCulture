package com.optculture.app.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.optculture.app.dto.sales.SalesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.optculture.shared.entities.transactions.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

	List<Sales> findAllByUserIdAndDocSid(Long userId, String docSid);
	@Query("SELECT cast(COUNT(res.docsid) as int) , SUM(res.price), AVG(res.price), MAX(res.price)" +
			"FROM (SELECT SUM(inn.salesPrice * inn.quantity - inn.discount) AS price, inn.docSid AS docsid FROM Sales inn WHERE inn.userId = :userId AND inn.contactId = :contactId GROUP BY inn.docSid) res")
	List<Object[]> findMaxAndAvgOrderValueByContactId( Long userId,Long contactId);

	List<Sales> findAllByUserIdAndReceiptNumber(Long userId, String receiptNumber);
	@Query("SELECT new com.optculture.app.dto.sales.SalesDto(s2.docSid as docSid,s2.amount as amount,s2.createdDate as createdDate,s2.receiptNumber as receiptNumber) FROM " +
			"( SELECT "+
			" SUM(s1.salesPrice * s1.quantity - s1.discount) AS amount," +
			" MAX(s1.salesDate) AS createdDate, " +
			" s1.docSid AS docSid, " +
			" s1.receiptNumber AS receiptNumber " +
			" FROM Sales s1" +
			" WHERE s1.contactId = :cid AND s1.userId = :userId AND (:fromDate is null or s1.salesDate >= :fromDate) AND (:toDate is null or s1.salesDate <= :toDate )" +
			" GROUP BY s1.docSid " +
			" ) s2 ")
	Page<SalesDto> findSalesByCid(Long cid, Long userId, LocalDateTime fromDate, LocalDateTime toDate, PageRequest pr);

//	@Query("SELECT s1.recieptNumber, s1.salesDate, s1.quantity, s1.salesPrice, s1.tax, s1.storeNumber, s1.contactId, "
//			+ "s1.itemSid, s1.docSid, s1.discount, s2.description, s2.itemCategory FROM Sales s1, Sku s2 "
//			+ "WHERE s1.userId = :userId AND s2.userId = :userId "
//			+ "AND s1.inventoryId = s2.skuId AND s1.docSid = :docSid")
//	List<SalesAndSkuDTO> getRetailProSalesAndSkuForEreceipt(@Param("userId") Long userId, @Param("docSid") String docSid);

}

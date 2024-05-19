package com.optculture.launchpad.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.transactions.Sales;

public interface SaleRepository extends JpaRepository<Sales, Long>{
	
	Optional<Sales> findBycontactId(Long id);
	
	

}

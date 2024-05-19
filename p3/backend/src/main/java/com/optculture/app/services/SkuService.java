package com.optculture.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.app.repositories.SkuRepository;
import com.optculture.shared.entities.transactions.Sku;

@Service
public class SkuService {
	
	@Autowired
	SkuRepository skuRepository;
	
	public Sku getSkuBySkuId(Long skuId) {
		return skuRepository.findOneBySkuId(skuId);
	}

}

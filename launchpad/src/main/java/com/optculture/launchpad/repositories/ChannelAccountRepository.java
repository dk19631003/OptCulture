package com.optculture.launchpad.repositories;


import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.communication.ChannelAccount;


public interface ChannelAccountRepository extends JpaRepository<ChannelAccount, Long> {
	@Cacheable(cacheNames = "channelAccount")
	Optional<ChannelAccount> findById(Long id);
	
}

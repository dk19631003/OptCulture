package com.optculture.launchpad.repositories;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.communication.ChannelSetting;


public interface ChannelSettingRepository extends JpaRepository<ChannelSetting, Long>{
	@Cacheable(cacheNames = "channelSetting")
	Optional<ChannelSetting> findById(Long id);

}

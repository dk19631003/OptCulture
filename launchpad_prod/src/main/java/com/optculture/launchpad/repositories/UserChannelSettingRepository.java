package com.optculture.launchpad.repositories;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.optculture.shared.entities.communication.UserChannelSetting;

import java.util.concurrent.TimeUnit;


public interface UserChannelSettingRepository extends JpaRepository<UserChannelSetting, Long> {

	@Cacheable(cacheNames = "userChannelSetting")
	UserChannelSetting findByUserIdAndChannelType(Long userId,String channelType);

	UserChannelSetting findByUserIdAndChannelTypeAndSenderId(Long userId,String channelType,String senderId);

	UserChannelSetting findByUserId(Long userId);

}

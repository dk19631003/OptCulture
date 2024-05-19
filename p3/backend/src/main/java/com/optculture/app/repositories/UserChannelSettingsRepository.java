package com.optculture.app.repositories;

import com.optculture.app.dto.user.ChannelAccountsDto;
import com.optculture.app.dto.user.UserChannelSettingsDto;
import com.optculture.shared.entities.communication.UserChannelSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChannelSettingsRepository extends JpaRepository<UserChannelSetting,Long> {
    @Query("SELECT new com.optculture.app.dto.user.UserChannelSettingsDto(" +
            "ucs.channelType as channelType," +
            "ucs.id as userChannelSettingId," +
            "cs.gatewayName as gatewayName," +
            "senderId as senderId," +
            "accountName as channelAccountName," +
            "apiKey as apiKey," +
            "ca.accountType as accountType," +
            "ca.id as channelAccountId)" +
            "FROM UserChannelSetting ucs " +
            "JOIN ChannelAccount ca ON ucs.channelAccount.id=ca.id " +
            "JOIN ChannelSetting cs ON ca.channelSettings.id=cs.id " +
            "WHERE ucs.userId =:userId " +
            "ORDER BY ucs.channelType")
    Page<UserChannelSettingsDto> findByUserId(Long userId, PageRequest pg);

    @Query("SELECT new com.optculture.app.dto.user.ChannelAccountsDto("+
    "ca.id as id,"+
    "ca.accountName as accountName,"+
    "cs.channelType as channelType)" +
    "FROM ChannelAccount ca "+
    "JOIN ChannelSetting cs ON ca.channelSettings.id=cs.id")
    List<ChannelAccountsDto> getChannelAccounts();

    @Query("SELECT COUNT(ucs.id) FROM UserChannelSetting ucs WHERE ucs.userId = :userId AND ucs.channelType = :channelType")
    int getUserChannelSettingsCountByUserIdAndChannelType(Long userId, String channelType);

    @Query("SELECT DISTINCT(ucs.senderId) FROM UserChannelSetting ucs WHERE ucs.userId = :userId AND ucs.channelType = :channelType")
    List<String> getUserEmailDomainsByUserId(Long userId, String channelType);
}

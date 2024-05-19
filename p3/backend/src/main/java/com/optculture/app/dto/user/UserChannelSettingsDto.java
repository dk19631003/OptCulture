package com.optculture.app.dto.user;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
@Data
@RequiredArgsConstructor
public class UserChannelSettingsDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userChannelSettingId;
    private Long channelAccountId;
    private String gatewayName;  // Equence/CM
    private String channelType; // Email/Sms/Wa
    private String accountType; // Promotional or Transactional
    private String channelAccountName;  // Sub Accounts names in providers
    private String senderId;
    private String apiKey;

}

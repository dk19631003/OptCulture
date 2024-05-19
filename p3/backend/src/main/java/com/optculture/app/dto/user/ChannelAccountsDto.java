package com.optculture.app.dto.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChannelAccountsDto {
    private Long id;
    private String accountName;
    private String channelType;
}

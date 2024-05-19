package com.optculture.app.dto.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AddUserChannelSettingDto {
    private Long ucsId;
    private Long userId;
    private Long channelAccountId;
    private String selectedChannel;
    private String senderId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

}

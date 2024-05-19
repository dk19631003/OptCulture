package com.optculture.app.dto.campaign.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data

public class EmailDomainsDTO {
    Set<String> replyToEmails; //all emails which are added by user in (user_from_email_id) table
    Set<String> fromEmails; // the emails whose domains are configured in user_channel_setting

    public EmailDomainsDTO(List<String> replyToEmails, List<String> fromEmalis) {
        this.replyToEmails = new HashSet<>(replyToEmails);
        this.fromEmails = new HashSet<>(fromEmalis);
    }
}

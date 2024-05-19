package com.optculture.app.services;

import com.optculture.app.dto.campaign.email.EmailDomainsDTO;
import com.optculture.app.repositories.UserFromEmailIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserFromEmailIdService {
    @Autowired
    UserFromEmailIdRepository userFromEmailIdRepo;

    @Autowired
    UserChannelSettingService ucsService;

    public EmailDomainsDTO getUserEmailDomains(Long userId) {
        List<String> userEmails=userFromEmailIdRepo.findByUsersUserIdAndStatus(userId,1);
        List<String> configuredDomains=ucsService.getUserEmailDomains(userId);
        Set<String> approvedDomains=new HashSet<>(configuredDomains);
        List<String> approvedEmails=userEmails.stream().filter(email ->  approvedDomains.contains(email.split("@")[1])).toList();
        return new EmailDomainsDTO(userEmails,approvedEmails);
    }
}


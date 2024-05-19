package com.optculture.app.services;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.user.AddUserChannelSettingDto;
import com.optculture.app.dto.user.ChannelAccountsDto;
import com.optculture.app.dto.user.UserChannelSettingsDto;
import com.optculture.app.repositories.ChannelAccountRepository;
import com.optculture.app.repositories.UserChannelSettingsRepository;
import com.optculture.shared.entities.communication.ChannelAccount;
import com.optculture.shared.entities.communication.UserChannelSetting;
import com.optculture.shared.entities.org.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UserChannelSettingService {

    @Autowired
    private UserChannelSettingsRepository userChannelSettingsRepo;

    @Autowired
    private ChannelAccountRepository channelAccountRepository;

    @Autowired
    GetLoggedInUser getLoggedInUser;

    //Get the channel Settings of logged in user
    public ResponseEntity getALlChannelSettings(int pageSize,int pageNumber){
        User user = getLoggedInUser.getLoggedInUser();
        Page<UserChannelSettingsDto> userChannelSettingsDtos=null;
        userChannelSettingsDtos= userChannelSettingsRepo.findByUserId(user.getUserId(), PageRequest.of(pageNumber,pageSize));
        return new ResponseEntity<>(userChannelSettingsDtos, HttpStatus.OK);
    }

    // Delete the user channel setting
    public void deleteUserChannel(Long ucsId) {
        userChannelSettingsRepo.deleteById(ucsId);
    }

    // Get the Channel Accounts for dropdown selection
    public ResponseEntity getChannelAccounts() {
        List<ChannelAccountsDto> channelAccountsDto=null;
        channelAccountsDto= userChannelSettingsRepo.getChannelAccounts();
        return new ResponseEntity<>(channelAccountsDto, HttpStatus.OK);
    }

    //Add new User Channel Setting or Edit the existing User Channel setting
    public ResponseEntity saveUserChannelSettings(AddUserChannelSettingDto addUserChannelSettingDto) {
        User user = getLoggedInUser.getLoggedInUser();
        UserChannelSetting userChannelSettingObj = null;
        if(addUserChannelSettingDto.getUcsId()!=null){
            userChannelSettingObj=userChannelSettingsRepo.findById(addUserChannelSettingDto.getUcsId()).get();
            userChannelSettingObj.setModifiedDate(LocalDateTime.now());
        }
        else {
            if (addUserChannelSettingDto != null) {
                userChannelSettingObj = new UserChannelSetting();
                userChannelSettingObj.setCreatedDate(LocalDateTime.now());
            }
        }
        userChannelSettingObj.setUserId(user.getUserId());
        userChannelSettingObj.setChannelType(addUserChannelSettingDto.getSelectedChannel());
        userChannelSettingObj.setSenderId(addUserChannelSettingDto.getSenderId());
        Optional<ChannelAccount> channelAccount = channelAccountRepository.findById(addUserChannelSettingDto.getChannelAccountId());
        userChannelSettingObj.setChannelAccount(channelAccount.get());
        userChannelSettingsRepo.save(userChannelSettingObj);
        return new ResponseEntity("UserChannel added Successfully", HttpStatus.OK);
    }

    public ResponseEntity getUserChannelSettingsCount(String channelType) {
         User user=getLoggedInUser.getLoggedInUser();
        int count=userChannelSettingsRepo.getUserChannelSettingsCountByUserIdAndChannelType(user.getUserId(),channelType);
        return new ResponseEntity<>(count,HttpStatus.OK);
    }

    public List<String> getUserEmailDomains(Long userId) {
        List<String> emailDomains=userChannelSettingsRepo.getUserEmailDomainsByUserId(userId,"Email");
        return emailDomains;
    }
}

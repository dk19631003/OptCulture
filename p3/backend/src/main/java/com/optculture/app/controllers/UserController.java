package com.optculture.app.controllers;
import com.optculture.app.dto.campaign.email.OrgStoreDTO;
import com.optculture.app.dto.user.AddUserChannelSettingDto;
import com.optculture.app.dto.user.UserDto;
import com.optculture.app.services.OrganizationStoreService;
import com.optculture.app.services.UserChannelSettingService;
import com.optculture.app.services.UserService;
import com.optculture.shared.entities.org.OrganizationStore;
import com.optculture.shared.entities.org.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.optculture.app.dto.login.MyUserPrincipal;

import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserChannelSettingService ucsService;
    @Autowired
    OrganizationStoreService storeService;

    @RequestMapping( "/hello" )
    public String echo() {

       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       if (principal instanceof MyUserPrincipal) {
          var user = (MyUserPrincipal)principal;
          return user.getUser().getUserId().toString();
       } else {
          return principal.toString();
       }
    }
    @GetMapping("/fetch-all")
//    @PreAuthorize("hasAuthority('ViewOCAdmin')")
    public ResponseEntity<List<UserDto>> fetchAllUsers(){
    return  new ResponseEntity<>(userService.fetchAllUsers(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/user-channel-settings")
    public ResponseEntity fetchUserChannelSettings(@RequestParam(defaultValue = "10" ,required = false) int pageSize,@RequestParam int pageNumber){
        return new ResponseEntity<>(ucsService.getALlChannelSettings(pageSize,pageNumber),HttpStatusCode.valueOf(200));
    }
    @GetMapping("/channel-settings-count/{channelType}")
    public ResponseEntity getUserChannelSettingsCount(@PathVariable String channelType){
        return ucsService.getUserChannelSettingsCount(channelType);
    }


    @DeleteMapping("/user-channel-settings/{ucsId}")
    public ResponseEntity<String> deleteUserChannel(@PathVariable Long ucsId) {
        try {
            ucsService.deleteUserChannel(ucsId);
            return new ResponseEntity<>("User channel deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete user channel", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetch-channel-accounts")
    public ResponseEntity fetchChannelAccounts(){
        return new ResponseEntity<>(ucsService.getChannelAccounts(),HttpStatusCode.valueOf(200));
    }

    @PostMapping("/add-user-channel")
    public ResponseEntity saveUserChannelSettings(@RequestBody AddUserChannelSettingDto addUserChannelSettingDto){
        return new ResponseEntity<>(ucsService.saveUserChannelSettings(addUserChannelSettingDto),HttpStatus.OK);
    }
    @GetMapping("/store-list")
    public ResponseEntity getStoreList(){

        List<OrgStoreDTO> orgStoreList=storeService.getStoreList();
        return new ResponseEntity<>(orgStoreList,HttpStatus.OK);
    }
}

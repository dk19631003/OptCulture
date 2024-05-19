package com.optculture.app.controllers;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.contacts.ContactsDto;
import com.optculture.app.services.ContactService;
import com.optculture.app.services.SqIdService;
import com.optculture.shared.entities.org.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/contacts")
public class ContactsController {
    Logger logger = LoggerFactory.getLogger(ContactsController.class);

    public ContactsController(ContactService contactService) {
        this.contactService = contactService;
    }
    @Autowired
    private GetLoggedInUser getLoggedInUser;
    ContactService contactService;
    @Autowired
    SqIdService sqIdService;
    @GetMapping("/")
    public ResponseEntity getContactByContactId(@RequestParam String contactId){
        User currentUser=getLoggedInUser.getLoggedInUser();
        List<Long> idList=sqIdService.decodeId(contactId);
        ContactsDto contactsDto= contactService.getContactsByContactId(idList.get(0),currentUser.getUserId());
        if(contactsDto==null) return new ResponseEntity<>("No contact Found", HttpStatus.OK);
        return new ResponseEntity<>(contactsDto,HttpStatus.OK);
    }


    @GetMapping("/search")
//    @PreAuthorize("hasAuthority('ReadContacts')")
    public ResponseObject<List<ContactsDto>> getContactsFilter(@RequestParam int pageNumber , @RequestParam int  pageSize, @RequestParam(defaultValue ="--",required = false) String criteria, @RequestParam(defaultValue = "--",required = false) String searchvalue, @RequestParam(defaultValue = "--",required = false) String firstName, @RequestParam(defaultValue = "--",required = false) String lastName){
        User currentUser=getLoggedInUser.getLoggedInUser();
        logger.info("logged in user id is {}", currentUser.getUserId());
         return contactService.getContactsFilter(pageSize,pageNumber,currentUser.getUserId(),criteria,searchvalue,firstName,lastName);

    }
    @GetMapping("/contact-by-mobile")
    public  ContactsDto getContactByMobileNumber(@RequestParam String mobileNumber){
        User currentUser=getLoggedInUser.getLoggedInUser();
        return  contactService.getContactByMobileNumber(currentUser.getUserId(), mobileNumber);
    }
    @PutMapping("/update")
    public ResponseEntity updateContactDetails(@RequestBody ContactsDto contactRequestDto){
        User currentUser=getLoggedInUser.getLoggedInUser();
         return contactService.updateContactInfo(contactRequestDto,currentUser.getUserId());
    }

}

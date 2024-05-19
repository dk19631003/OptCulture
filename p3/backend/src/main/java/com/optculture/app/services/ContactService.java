package com.optculture.app.services;

import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.config.PosMappingDto;
import com.optculture.app.dto.contacts.ContactsDto;
import com.optculture.app.dto.coupons.CouponInventoryDto;
import com.optculture.app.dto.coupons.ReferralCodesDto;
import com.optculture.app.dto.ereceipt.Loyalty;
import com.optculture.app.dto.sales.OrderValueAggregation;
import com.optculture.app.repositories.*;
import com.optculture.shared.entities.contact.ContactLoyalty;

import com.optculture.shared.entities.contact.CustomerFeedback;
import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;

import com.optculture.shared.entities.promotion.ReferralProgram;
import com.optculture.shared.entities.promotion.ReferralcodesIssued;
import io.jsonwebtoken.Claims;

import com.optculture.app.repositories.ContactLoyaltyRepository;

import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.optculture.shared.entities.contact.Contact;
import com.optculture.app.repositories.ContactRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import org.slf4j.Logger;

@Service
public class ContactService {

    @Autowired
    private JwtService jwtService;

    private ContactRepository contactRepository;
    private CustomerFeedbackService customerFeedbackService;
    private ContactLoyaltyRepository contactLoyaltyRepository;

    private SalesService salesService;
    private ContactLoyaltyService contactsLoyaltyService;
    private LoyaltyProgramTierService loyaltyProgramTierService;
    @Autowired
    private LoyaltyTransactionChildService loyaltyTransactionChildService;
    private ReferralCodesIssuedService referralCodesIssuedService;
    @Autowired
    ReferralcodesRedeemedService referralcodesRedeemedService;
    @Autowired
    PosMappingService posMappingService;
    @Autowired
    SqIdService sqIdService;
    ModelMapper modelMapper = new ModelMapper();
    @Autowired
    ValueCodesRepository valueCodesRepository;

    @Autowired
    ReferralProgramRepository referralProgramRepository;

    @Autowired
    CouponsService couponsService;
    Logger logger= LoggerFactory.getLogger(ContactService.class);
    @Autowired
    LoyaltyTransactionChildRepository loyaltyTransactionChildRepository;

    public ContactService(ContactRepository contactRepository, ContactLoyaltyRepository contactLoyaltyRepository,
                          CustomerFeedbackService customerFeedbackService, SalesService salesService,
                          ContactLoyaltyService contactsLoyaltyService, LoyaltyProgramTierService loyaltyProgramTierService
            , ReferralCodesIssuedService referralCodesIssuedService) {
        this.contactRepository = contactRepository;
        this.contactLoyaltyRepository = contactLoyaltyRepository;
        this.customerFeedbackService = customerFeedbackService;
        this.salesService = salesService;
        this.contactsLoyaltyService = contactsLoyaltyService;
        this.loyaltyProgramTierService = loyaltyProgramTierService;
        this.referralCodesIssuedService = referralCodesIssuedService;
    }

    public Contact getContactByContactId(Long contactId, Long userId) {
        return contactRepository.findOneByContactIdAndUserId(contactId, userId);
    }

    public Contact saveOrUpdate(Contact contact) {
        return contactRepository.save(contact);
    }

    public void updateEreceiptContact(Map<String, Object> contactData) {

        Claims data = jwtService.getClaims(contactData.get("customerToken").toString());

        Long contactId = Long.valueOf(data.get("contactId").toString());
        Long userId = Long.valueOf(data.get("userId").toString());

        Contact contact = getContactByContactId(contactId, userId);
        contact.setFirstName(contactData.get("firstName").toString());
        contact.setLastName(contactData.get("lastName").toString());
        contact.setEmailId(contactData.get("emailId").toString());
        contact.setMobilePhone(contactData.get("mobilePhone").toString());
        String birthDay = (String) contactData.get("birthDay");
		if (Objects.isNull(birthDay))
			contact.setBirthDay(null);
		else if (!birthDay.contains("T00:00:01"))
			contact.setBirthDay(LocalDateTime.parse(birthDay + "T00:00:01")); // ISO
		else
			contact.setBirthDay(LocalDateTime.parse(birthDay));
		
		String birthMonth = Optional.ofNullable(contactData.get("birthMonth")).isPresent()
				? contactData.get("birthMonth").toString()
				: null;
		contact.setUdf11(birthMonth);

		saveOrUpdate(contact);
    }

    public ContactsDto getContactsByContactId(Long contactId, Long userId) {

        Contact contacts = contactRepository.findOneByContactIdAndUserId(contactId, userId);
        //last Nps rating of customer
        CustomerFeedback CustFeedback = customerFeedbackService.findFirstByContactIdAndUserIdOrderByCreatedDateDesc(contactId, userId);
        //fetches max,avg, total ordervalues and count of orders placed
        Object[] salesAggreObjects = salesService.getMaxAndAvgOrderValueByContactId(userId, contactId);
       //referral Information
        Long count = referralProgramRepository.countByUserId(userId);
        ReferralcodesIssued referralcodesIssued = referralCodesIssuedService.getReferralCodeByContactId(contactId, userId);
        ReferralCodesDto referralCodesDto = new ReferralCodesDto("", 0L, 0L,false);
        if(count>0)
            referralCodesDto.setRefProgram(true);
        if (referralcodesIssued != null) {

            Long redeemedCount = referralcodesRedeemedService.getRedeemedCountByReferredCid(contactId, userId);
            referralCodesDto.setReferralCode(referralcodesIssued.getRefcode());
            referralCodesDto.setRedeemedCount(redeemedCount);
        }
        if (contacts != null) {
            ContactsDto contactsDto = getLoyaltyInfo(contacts);
            if (CustFeedback != null && CustFeedback.getUdf1() != null) {
                contactsDto.setLastNpsRating(Integer.parseInt(CustFeedback.getUdf1()));
            } else {
                contactsDto.setLastNpsRating(0);
            }
            if (salesAggreObjects.length > 0) {
                OrderValueAggregation orderValueAggregation = new OrderValueAggregation();
                orderValueAggregation.setNoOfOrders(salesAggreObjects[0] != null ? (Integer) salesAggreObjects[0] : 0);
                orderValueAggregation.setTotalAmountSpent(salesAggreObjects[1] != null ? (Double) salesAggreObjects[1] : 0.00);
                orderValueAggregation.setAvgOrderValue(salesAggreObjects[2] != null ? (Double) salesAggreObjects[2] : 0.00);
                orderValueAggregation.setMaxOrderValue(salesAggreObjects[3] != null ? (Double) salesAggreObjects[3] : 0.00);
                contactsDto.setOrdersInfo(orderValueAggregation);
            }
            contactsDto.setReferralInfo(referralCodesDto);
            List<CouponInventoryDto> couponsList= couponsService.getActiveAndRunningCouponsList();
            contactsDto.setInventoryCoupons(couponsList);
            return contactsDto;
        }
        return null;
    }


    public ResponseObject<List<ContactsDto>> getContactsFilter(int pageSize, int pageNumber, long userId, String searchCriteria, String searchValue, String firstName, String lastName) {

        Contact contact = new Contact();
        contact.setUserId(userId);
        //*EXAMPLE MATCHER*//
        switch (searchCriteria) {
            case "EMAILID":
                contact.setEmailId(searchValue);
                return getContactsByFilter(contact, pageSize, pageNumber);

            case "MOBILE":
                contact.setMobilePhone(searchValue);
                return getContactsByFilter(contact, pageSize, pageNumber);

            case "NAME":
                if (!firstName.equalsIgnoreCase("--")) {
                    contact.setFirstName(firstName);
                }
                if (!lastName.equalsIgnoreCase("--")) {
                    contact.setLastName(lastName);
                }
                return getContactsByFilter(contact, pageSize, pageNumber);

            case "MEMBERSHIPNUMBER":
                return getContactsByMembershipNumber(searchValue, userId, pageSize, pageNumber);

            default:
                return getContactsByFilter(contact, pageSize, pageNumber);
        }
    }

    private ResponseObject<List<ContactsDto>> getContactsByMembershipNumber(String searchValue, long userId, int pageSize, int pageNumber) {

//        List<ContactsDto> contactsDtoList = new ArrayList<>();
//        ContactLoyalty contactLoyalty = searchCriteriaMembership(searchValue, userId);
//        if (contactLoyalty == null) {
//            return new ResponseObject<>(0L, 0L, contactsDtoList);
//        }
//        Optional<Contact> optionalContact = contactRepository.findById(contactLoyalty.getContact().getContactId());
//        ContactsDto contactsDto;
//        if (optionalContact.isPresent()) {
//            contactsDto = getLastInteractionAndTierInfo(optionalContact.get(), contactLoyalty);
//            contactsDtoList.add(contactsDto);
//        }
        Contact contact= new Contact();

//        Sort sort= Sort.by(Sort.Direction.DESC,"createdDate");
//        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize);
        String memberShipNumber=searchValue;
        List<ContactsDto> result=contactRepository.findByContactDetails(contact,memberShipNumber,userId,pageNumber*pageSize,pageSize);
        return getContactDtoList(result, contact, userId,pageSize,memberShipNumber);

    }

    public ResponseObject<List<ContactsDto>> getContactsByFilter(Contact contact, int pageSize, int pageNumber) {

        Sort sort= Sort.by(Sort.Direction.DESC,"createdDate");
        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize,sort);
        String memberShipNumber=null;
        List<ContactsDto> result=contactRepository.findByContactDetails(contact,memberShipNumber,contact.getUserId(),pageSize*pageNumber,pageSize);
        return getContactDtoList(result,contact,contact.getUserId(),pageSize, memberShipNumber);
}


    public ContactLoyalty searchCriteriaMembership(String searchValue, Long userId) {

        ContactLoyalty contactsLoyalty = new ContactLoyalty();

        contactsLoyalty.setCardNumber(searchValue);
        contactsLoyalty.setUserId(userId);
        ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.EXACT).withIgnoreNullValues();
        Example<ContactLoyalty> example = Example.of(contactsLoyalty, matcher);
        Optional<ContactLoyalty> optionalContactLoyalty = contactLoyaltyRepository.findOne(example);
        return optionalContactLoyalty.isPresent() ? optionalContactLoyalty.get() : null;
    }
    private ResponseObject getContactDtoList(List<ContactsDto> result, Contact contact, Long userId, int pageSize, String memberShipNumber) {

        for (ContactsDto contactsDto : result) {
            String encodedId=sqIdService.encodeId(Arrays.asList(Long.parseLong(contactsDto.getCid())));
            contactsDto.setCid(encodedId);
            if(contactsDto.getLastInteraction()==null){
                contactsDto.setLastInteraction(contactsDto.getCreatedDate());
            }
        }
//        if(memberShipNumber!=null){
//            long totalContacts=contactLoyaltyRepository.findCountByUserIdAndCardNumber(userId,memberShipNumber);
//            long totalPages=(long)Math.ceil(totalContacts/pageSize);
//            return  new ResponseObject<>(totalContacts,totalPages,result);
//        }else {
            long totalContacts = contactRepository.findTotalContacts(userId);
            long totalPages = (long) Math.ceil((double)totalContacts / (double)pageSize);
            return new ResponseObject<>(totalContacts, totalPages, result);
//        }
    }

    //contacts -> contactsdto converter
    private List<ContactsDto> toContactDto(List<Contact> contactslist) {
        List<ContactsDto> contactsList2 = new ArrayList<>();

        for (Contact contacts : contactslist) {
//            ContactLoyalty contactLoyalty = getContactLoyalty(contacts);
//            ContactsDto contactsDto = getLastInteractionAndTierInfo(contacts, contactLoyalty);
//            contactsList2.add(contactsDto);
        }
        return contactsList2;
    }
    //last transactiondate && loyalty information
    private  ContactsDto getLastInteractionAndTierInfo(Contact contact,ContactLoyalty contactLoyalty){
        ContactsDto contactsDto=modelMapper.map(contact,ContactsDto.class);
        String encodedId=sqIdService.encodeId(Arrays.asList(contact.getContactId()));
        contactsDto.setCid(encodedId);
        if(contactLoyalty!=null){
            contactsDto.setMembershipNumber(contactLoyalty.getCardNumber());
            List<LoyaltyProgramTier> loyaltyProgramTierList = loyaltyProgramTierService.getListOfLoyaltyProgramTiersByProgramId(contactLoyalty.getProgramId());
            Loyalty loyalty=modelMapper.map(contactLoyalty,Loyalty.class);
            for (LoyaltyProgramTier loyaltyProgramTier : loyaltyProgramTierList) {
                if (loyaltyProgramTier.getTierId().equals(contactLoyalty.getProgramTierId())) {
                    //current tier Name
                    loyalty.setCurrentTierName(loyaltyProgramTier.getTierName());
                    break;
                }
            }
            contactsDto.setLoyalty(loyalty);
        }
        LocalDateTime lastInteraction=loyaltyTransactionChildService.getLastInteractionOfContact(contact.getContactId(),contact.getUserId());
        contactsDto.setLastInteraction(lastInteraction);

        return  contactsDto;
    }
    private  ContactsDto getLoyaltyInfo(Contact contact){
        ContactLoyalty contactLoyalty = getContactLoyalty(contact);
        ContactsDto contactsDto=modelMapper.map(contact,ContactsDto.class);
        String encodedId=sqIdService.encodeId(Arrays.asList(contact.getContactId()));
        contactsDto.setCid(encodedId);
        if(contactLoyalty!=null){
            contactsDto.setMembershipNumber(contactLoyalty.getCardNumber());
            String loyaltyIdEncoded =sqIdService.encodeId(Arrays.asList(contactLoyalty.getLoyaltyId()));
            contactsDto.setLoyaltyId(loyaltyIdEncoded);
            Loyalty loyalty = getLoyalty(contactLoyalty);
            contactsDto.setLoyalty(loyalty);
        }
        return  contactsDto;
    }

    public Loyalty getLoyalty(ContactLoyalty contactLoyalty) {
        List<LoyaltyProgramTier> loyaltyProgramTierList = loyaltyProgramTierService.getListOfLoyaltyProgramTiersByProgramId(contactLoyalty.getProgramId());
        Loyalty loyalty=modelMapper.map(contactLoyalty,Loyalty.class);
        Map<String,Long> tierList = new HashMap<>();

        for (LoyaltyProgramTier loyaltyProgramTier:loyaltyProgramTierList) {
            if(!Objects.equals(loyaltyProgramTier.getTierId(), contactLoyalty.getProgramTierId()))
                tierList.put(loyaltyProgramTier.getTierName(),loyaltyProgramTier.getTierId());
        }
        loyalty.setListOfTiers(tierList);
//        loyalty.setValueCodes(getValueCodes();

        Double previousTierUpgradedValue = 0.0;
        boolean foundCurrentTier = false;
        for (LoyaltyProgramTier loyaltyProgramTier : loyaltyProgramTierList) {

            loyalty.setTierUpgradeCriteria(loyaltyProgramTier.getTierUpgdConstraint());
            if (foundCurrentTier) {
                loyalty.setNextTierName(loyaltyProgramTier.getTierName());
                break;
            }

            if (!loyaltyProgramTier.getTierId().equals(contactLoyalty.getProgramTierId())) {
                previousTierUpgradedValue = loyaltyProgramTier.getTierUpgdConstraintValue();
            }
            if (loyaltyProgramTier.getTierId().equals(contactLoyalty.getProgramTierId())) {

                //current tier Name
                loyalty.setCurrentTierName(loyaltyProgramTier.getTierName());
                loyalty.setTierUpgradeValue(loyaltyProgramTier.getTierUpgdConstraintValue());

                // upgrade mile stone,current value
                if (Optional.ofNullable(loyaltyProgramTier.getTierUpgdConstraintValue()).isPresent()) {
                    if (loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase("LifetimePoints")) {
                        loyalty.setTierUpgradeMileStone(loyaltyProgramTier.getTierUpgdConstraintValue() - (contactLoyalty.getTotalLoyaltyPointsEarned() == null ? 0 : contactLoyalty.getTotalLoyaltyPointsEarned()));
                        loyalty.setCurrentTierValue(contactLoyalty.getTotalLoyaltyPointsEarned());
                    } else if (loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase("LifetimePurchaseValue")) {
                        Double LPV = getLPV(contactLoyalty);
                        loyalty.setTierUpgradeMileStone(loyaltyProgramTier.getTierUpgdConstraintValue() - LPV);
                        loyalty.setCurrentTierValue(LPV);
                    } else if (loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase("CumulativePurchaseValue")) {
                        // Long loyaltyId =
                        // contactLoyalty.getTransferedTo()==null?contactLoyalty.getLoyaltyId():contactLoyalty.getTransferedTo();
                        Long months = loyaltyProgramTier.getTierUpgradeCumulativeValue();
                        LocalDateTime createdDate = LocalDateTime.now().minusMonths(months);

                        Double purchaseValue =0.0;
                       Double purchaValue= getCummulativeAmount(contactLoyalty, createdDate);
                       if(purchaValue!=null) purchaseValue=purchaValue;
                        loyalty.setTierUpgradeMileStone(loyaltyProgramTier.getTierUpgdConstraintValue() - purchaseValue);
                        loyalty.setCurrentTierValue(purchaseValue);
                        // Double purchaseValue =
                        // loyaltyTransactionChildRepository.findByUserIdAndloyaltyIdAndcreatedDate(contactLoyalty.getUserId(),loyaltyId,createdDate);
                    }
                    else if(loyaltyProgramTier.getTierUpgdConstraint().equalsIgnoreCase("CumulativePoints")){

                        Long cumulativePoints = 0L;
                        Long months = loyaltyProgramTier.getTierUpgradeCumulativeValue();
                        LocalDateTime startDate=contactLoyalty.getTierUpgradedDate()!=null?contactLoyalty.getTierUpgradedDate():contactLoyalty.getCreatedDate();
                        LocalDateTime endDate=contactLoyalty.getTierUpgradedDate()!=null?contactLoyalty.getTierUpgradedDate():contactLoyalty.getCreatedDate();
                        endDate=endDate.minusMonths(months);
                       Long cumulativePointsDb=getCumulativePoints(contactLoyalty,startDate,endDate);
                       if(cumulativePointsDb!=null) cumulativePoints=cumulativePointsDb;
                        loyalty.setTierUpgradeMileStone(loyaltyProgramTier.getTierUpgdConstraintValue() - cumulativePoints);
                        loyalty.setCurrentTierValue(cumulativePoints*1.0);
                    }
                }
                foundCurrentTier = true;
            }
        }
        loyalty.setPreviousTierUpgradedValue(previousTierUpgradedValue);


        return loyalty;
    }

    private Long getCumulativePoints(ContactLoyalty contactLoyalty, LocalDateTime startDate, LocalDateTime endDate) {
        Long loyaltyId = contactLoyalty.getTransferedTo()==null?contactLoyalty.getLoyaltyId():contactLoyalty.getTransferedTo();
        return  loyaltyTransactionChildService.getCumulativePoints(contactLoyalty.getUserId(),contactLoyalty.getProgramId(),loyaltyId,startDate,endDate);
    }

    public static Double getLPV(ContactLoyalty contactsLoyalty) {
        Double totPurchaseValue = null;
        Double cummulativePurchaseValue = contactsLoyalty.getCummulativePurchaseValue() == null ? 0.0 : contactsLoyalty.getCummulativePurchaseValue();
        Double cummulativeReturnValue = contactsLoyalty.getCummulativeReturnValue() == null ? 0.0 : contactsLoyalty.getCummulativeReturnValue();
        totPurchaseValue = (new BigDecimal(cummulativePurchaseValue-cummulativeReturnValue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        return totPurchaseValue;
    }

    public Double getCummulativeAmount(ContactLoyalty contactLoyalty, LocalDateTime createdDate) {
        Long loyaltyId = contactLoyalty.getTransferedTo()==null?contactLoyalty.getLoyaltyId():contactLoyalty.getTransferedTo();
        return loyaltyTransactionChildRepository.findByUserIdAndloyaltyIdAndcreatedDate(contactLoyalty.getUserId(),loyaltyId,createdDate);
    }

    private ContactLoyalty getContactLoyalty(Contact contact) { // getting contactloyalty from contacts
        if (contact == null) return null;
        return contactLoyaltyRepository.findFirstByUserIdAndContactContactIdAndMembershipStatusOrderByLoyaltyIdDesc(contact.getUserId(), contact.getContactId(), "Active");
    }

    public ContactsDto getContactByMobileNumber(Long userId, String mobileNumber) {
        logger.info("Inside contact by MobileNumber block");
        Optional<ContactsDto> contactsDto = contactRepository.findFirstByMobilePhoneAndUserId(userId, mobileNumber);
        if (contactsDto.isPresent()) {
            return contactsDto.get();
        }
       else return null;
    }


    public ResponseEntity updateContactInfo(ContactsDto contactReqDto,Long userId) {

        if(contactReqDto==null) return new ResponseEntity("Required fields not Sent", HttpStatus.BAD_REQUEST);
        Long contactId= sqIdService.decodeId(contactReqDto.getCid()).get(0); //decoding the contactId
        Contact contact=contactRepository.findOneByContactIdAndUserId(contactId,userId);

   if(contact==null) return new ResponseEntity("Contact not Found", HttpStatus.NOT_FOUND);
        List<PosMappingDto> posMappingDtoList=posMappingService.getPosMappingListByUserId(userId,"Contacts");
//         Map<String,String> udfFieldsMap=getUdfMap();

         Contact updatedContact= prepareContactFromDto(contact,contactReqDto,posMappingDtoList);
         contactRepository.save(updatedContact);
        return  new ResponseEntity<>("updated",HttpStatus.OK);
    }

    private Contact prepareContactFromDto(Contact contact, ContactsDto contactReqDto, List<PosMappingDto> posMappingDtoList) {

            contact.setEmailId(contactReqDto.getEmailId());
            contact.setMobilePhone(contactReqDto.getMobilePhone());
            contact.setFirstName(contactReqDto.getFirstName());
            contact.setLastName(contactReqDto.getLastName());
            contact.setAddressOne(contactReqDto.getAddressOne());
            contact.setAddressTwo(contactReqDto.getAddressTwo());
            contact.setCity(contactReqDto.getCity());
            contact.setCountry(contactReqDto.getCountry());
            contact.setState(contactReqDto.getState());
            contact.setZip(contactReqDto.getZip());
            contact.setGender(contactReqDto.getGender());
            contact.setBirthDay(contactReqDto.getBirthDay());
            contact.setAnniversary(contactReqDto.getAnniversary());
            //saving customfields which are configured on pos
        for (PosMappingDto posMappingDto:posMappingDtoList){
            setUdfFields(contact,contactReqDto,posMappingDto.getCustFieldName());
        }
            contact.setModifiedDate(LocalDateTime.now());
        return  contact;
    }
    private Contact setUdfFields(Contact contact,ContactsDto contactReqDto,String udfName){
        switch(udfName){
            case "udf1": contact.setUdf1(contactReqDto.getUdf1());break;
            case "udf2": contact.setUdf2(contactReqDto.getUdf2());break;
            case "udf3": contact.setUdf3(contactReqDto.getUdf3());break;
            case "udf4": contact.setUdf4(contactReqDto.getUdf4());break;
            case "udf5": contact.setUdf5(contactReqDto.getUdf5());break;
            case "udf6": contact.setUdf6(contactReqDto.getUdf6());break;
            case "udf7": contact.setUdf7(contactReqDto.getUdf7());break;
            case "udf8": contact.setUdf8(contactReqDto.getUdf8());break;
            case "udf9": contact.setUdf9(contactReqDto.getUdf9());break;
            case "udf10": contact.setUdf10(contactReqDto.getUdf10());break;
            case "udf11": contact.setUdf11(contactReqDto.getUdf11());break;
            case "udf12": contact.setUdf12(contactReqDto.getUdf12());break;
            case "udf13": contact.setUdf13(contactReqDto.getUdf13());break;
            case "udf14": contact.setUdf14(contactReqDto.getUdf14());break;
            case "udf15": contact.setUdf15(contactReqDto.getUdf15());break;
            default: return  contact;
        }
        return  contact;
    }
}

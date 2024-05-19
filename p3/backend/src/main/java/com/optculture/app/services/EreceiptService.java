package com.optculture.app.services;

import java.util.*;

import com.optculture.app.dto.ereceipt.*;
import com.optculture.shared.entities.promotion.Coupons;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optculture.app.repositories.ContactRepository;
import com.optculture.app.repositories.DRSmsSentRepository;
import com.optculture.shared.entities.communication.ereceipt.DRSmsChannelSent;
import com.optculture.shared.entities.communication.ereceipt.DRSmsSent;
import com.optculture.shared.entities.contact.Contact;
import com.optculture.shared.entities.contact.ContactLoyalty;
import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;
import com.optculture.shared.entities.loyalty.LoyaltySetting;
import com.optculture.shared.entities.org.OrganizationStore;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.entities.transactions.Sales;

@Service
public class EreceiptService {
    @Value("${usersParentDirectory}")
	private String usersParentDirectory;
	@Value("${imagesUrl}")
	private String imagesUrl;
	@Autowired
	private DRSmsSentService drSmsSentService;

	@Autowired
	private DRSmsChannelSentService drSmsChannelSentService;

	@Autowired
	private ContactService contactService;

	@Autowired
	private SalesService salesService;

	@Autowired
	private UserService userService;

	@Autowired
	private LoyaltySettingService loyaltySettingsService;

	@Autowired
	private ContactLoyaltyService contactsLoyaltyService;

	@Autowired
	private LoyaltyProgramTierService loyaltyProgramTierService;

	@Autowired
	private OrganizationStoreService organizationStoresService;

	@Autowired
	private CustomerFeedbackService customerFeedbackService;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private DRSmsSentRepository drSmsSentRepository;

	private  CouponsService couponsService;
	private  FaqService faqService;
	public  EreceiptService(CouponsService couponsService,FaqService faqService)
	{
		this.couponsService=couponsService;
		this.faqService=faqService;
	}

	@Autowired
	private JwtService jwtService;
	
	private final Logger logger = LoggerFactory.getLogger(EreceiptService.class);

	public EreceiptType getEReceiptData(String originalShortCode, String channelType) {

		Long userId = null;
		String docSid = null;
		String downloadReceiptLink = null;
		ModelMapper modelMapper = new ModelMapper();

		if (channelType.equals("SMS")) {
			DRSmsSent drSmsSent = drSmsSentService.getDRSmsSentByOriginalShortCode(originalShortCode);

			if (Optional.ofNullable(drSmsSent).isPresent()) {
				userId = drSmsSent.getUserId();
				docSid = drSmsSent.getDocSid();
				downloadReceiptLink = drSmsSent.getOriginalUrl().replace("requestedAction=drsms", "requestedAction=pdfSMS");
			}
		} else if (channelType.equals("WHATSAPP")) {
			DRSmsChannelSent drSmsChannelSent = drSmsChannelSentService.getDRSmsChannelSentByOriginalShortCode(originalShortCode);

			if (Optional.ofNullable(drSmsChannelSent).isPresent()) {
				userId = drSmsChannelSent.getUserId();
				docSid = drSmsChannelSent.getDocSid();
				downloadReceiptLink = drSmsChannelSent.getOriginalUrl().replace("requestedAction=wa", "requestedAction=pdfWA");
			}
		} else {
			List<Object[]> drData = drSmsSentRepository.findLatestDRByOriginalShortCode(originalShortCode);

			if (drData != null && !drData.isEmpty()) {
				userId = Long.valueOf(drData.get(0)[0].toString());
				docSid = drData.get(0)[1].toString();
				downloadReceiptLink = drData.get(0)[2].toString()
						.replace("requestedAction=drsms", "requestedAction=pdfSMS")
						.replace("requestedAction=wa", "requestedAction=pdfWA");
			}
		}
		
		if (userId == null || docSid == null) {
			logger.error("********* DR Not found ***********");
			return null;
		}

		// Load all the sales items for this bill
		List<Sales> salesRecords = salesService.getSales(userId, docSid);
		Receipt receipt = Receipt.of(salesRecords, downloadReceiptLink);
		LineItem[] lineItems = LineItem.of(salesRecords);


		// Load store information
		User user = userService.getUserByUserId(userId);
		Organization organization = modelMapper.map(user, Organization.class);
		if(user.getUserId() == 1207) {
			organization.setFacebook("https://www.facebook.com/Styleunion.in");
			organization.setTwitter("");
			organization.setInstagram("https://www.instagram.com/styleunion.in/");
			organization.setLinkedin("https://www.linkedin.com/company/styleunion-in/");
			organization.setYoutube("https://www.youtube.com/@styleunion_in");
		}

		OrganizationStore orgStore = organizationStoresService.getOrgStore(salesRecords.get(0).getStoreNumber(), user.getUserOrganization().getUserOrgId(), salesRecords.get(0).getSubsidiaryNumber());
		StoreDetails storeDetails = Optional.ofNullable(orgStore)
				.map(store -> modelMapper.map(store, StoreDetails.class)).orElse(null);


		// Get loyalty info if present
		ContactLoyalty contactLoyalty = contactsLoyaltyService.getContactsLoyaltyByUserIdAndContactId(userId, salesRecords.get(0).getContactId());
		Loyalty loyalty = Optional.ofNullable(contactLoyalty).map(contactLyt -> {
			List<LoyaltyProgramTier> loyaltyProgramTierList = loyaltyProgramTierService.getListOfLoyaltyProgramTiersByProgramId(contactLyt.getProgramId());
			return Loyalty.of(loyaltyProgramTierList, contactLyt);
		}).orElse(null);

		Contact contact = null;
		
		contact = contactService.getContactByContactId(salesRecords.get(0).getContactId(), userId);
		
		if(!Optional.ofNullable(contact).isPresent()) {
			Optional<Contact> contactObj = contactRepository.findFirstByUserIdAndMobilePhone(userId, salesRecords.get(0).getUdf1());
			if(contactObj.isPresent()) {
				contact = contactObj.get();
			}
		}
		Long contactId = contact.getContactId();
		Customer customer = modelMapper.map(contact, Customer.class);
		customer.setDobEnabled(user.getUserId() != 1207);

		LoyaltySetting loyaltySetting = loyaltySettingsService.getLoyaltySettingsByUserId(userId);
		
		
		// Get the branding information
		String logo= loyaltySetting.getCompanyLogo();
		if(logo!=null) {
			logo = logo.replace(usersParentDirectory, imagesUrl);
			loyaltySetting.setCompanyLogo(logo);
		}
		Branding branding = modelMapper.map(loyaltySetting, Branding.class);

		String termsAndCondition=faqService.getTermsAndConditionsByUserId(userId);

		TermsAndConditions termsAndConditions = new TermsAndConditions(termsAndCondition);
		Tax tax = new Tax();
		tax.setTotalTax(receipt.getTotalTax());
		Tender tender = new Tender();
		tender.setName(salesRecords.get(0).getTenderType());
		tender.setType(salesRecords.get(0).getTenderType());
		tender.setAmount(receipt.getTotal());

		Map<String,List<String>> bannerImageList=couponsService.getBannerImageByUserId(userId);
		List<String> imglist =  bannerImageList.get("ImageBanner");
		List<String> imgRedirectlist =  bannerImageList.get("RedirectUrl");
    	        Offers[] offersArray = new Offers[imglist.size()];
		for (int i=0;i<imglist.size();i++)
		{
			String modifiedImageUrl = imagesUrl + "subscriber/UserData/" + user.getUserName() + "/Coupon/offerBanner/" +imglist.get(i);
			offersArray[i]=new Offers(modifiedImageUrl,imgRedirectlist.get(i));
		}

		String jsonComponentsConfig = loyaltySettingsService.getEReceiptConfigured(user.getUserId());

		Map<String, String> map = new HashMap<>();
		map.put("userId", userId.toString());
		map.put("contactId", contact.getContactId().toString());
		String customerToken = jwtService.encryptMap(map);

        boolean receiptFeedbackAvailable = customerFeedbackService.isReceiptLevelFeedbackAvailable(contactId, userId, docSid);
        String[] npsOptions = {"Collection", "Pricing", "Customer service", "Billing payment", "Store ambience", "Others"};
        return new EreceiptType(loyalty, customer, branding, receipt, lineItems, tax, termsAndConditions, tender, organization, storeDetails, offersArray, jsonComponentsConfig, customerToken, npsOptions, receiptFeedbackAvailable);

    }

	public ResponseEntity getReceiptItemsList(String docSid, String receiptNumber, Long userId) {
		List<Sales> salesRecords=new ArrayList<>();
		if(docSid.equalsIgnoreCase("--")){ //docsid not present
			salesRecords = salesService.getSalesByReceiptNumber(userId, receiptNumber);
		}
		else {
			salesRecords =  salesService.getSales(userId, docSid);
		}
		if(salesRecords.isEmpty()) return new ResponseEntity(new EreceiptType(), HttpStatus.NO_CONTENT);
		Receipt receipt = Receipt.of(salesRecords, "");
		LineItem[] lineItems = LineItem.of(salesRecords);
		Tax tax = new Tax();
		tax.setTotalTax(receipt.getTotalTax());
		Tender tender = new Tender();
		tender.setName(salesRecords.get(0).getTenderType());
		tender.setType(salesRecords.get(0).getTenderType());
		tender.setAmount(receipt.getTotal());
		 return new ResponseEntity(new EreceiptType(receipt,lineItems,tax,tender),HttpStatus.OK);
	}
}

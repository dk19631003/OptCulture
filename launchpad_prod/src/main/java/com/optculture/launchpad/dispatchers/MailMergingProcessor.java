package com.optculture.launchpad.dispatchers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.optculture.launchpad.controller.CouponProviderController;
import com.optculture.launchpad.configs.OCConstants;
import com.optculture.launchpad.dto.ContactDTO;
import com.optculture.launchpad.dto.ContactLoyaltyDTO;
import com.optculture.launchpad.dto.HomeStoreDTO;
import com.optculture.launchpad.dto.LoyaltyProgramTierDTO;
import com.optculture.launchpad.dto.UserOrganizationDTO;
import com.optculture.launchpad.dto.converter.ContactDTOConverter;
import com.optculture.launchpad.dto.converter.ContactLoyaltyDTOConverter;
import com.optculture.launchpad.dto.converter.HomeStoreDTOConverter;
import com.optculture.launchpad.repositories.CommunicationReportRepository;
import com.optculture.launchpad.repositories.CommunicationSentRepository;
import com.optculture.launchpad.repositories.ContactLoyaltyRepository;
import com.optculture.launchpad.repositories.LoyaltyProgramTierRepository;
import com.optculture.launchpad.repositories.OrganizationStoreRepository;
import com.optculture.launchpad.repositories.UserOrganizationRepository;
import com.optculture.launchpad.repositories.UserRepository;
import com.optculture.launchpad.services.LoyaltyProgramService;
import com.optculture.shared.entities.communication.Communication;
import com.optculture.shared.entities.communication.CommunicationReport;
import com.optculture.shared.entities.communication.CommunicationSent;
import com.optculture.shared.entities.contact.Contact;
import com.optculture.shared.entities.contact.ContactLoyalty;
import com.optculture.shared.entities.loyalty.LoyaltyProgramTier;
import com.optculture.shared.entities.org.OrganizationStore;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.entities.org.UserOrganization;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MailMergingProcessor {

	Logger logger = LoggerFactory.getLogger(MailMergingProcessor.class);

	@Autowired
	OrganizationStoreRepository orgstoreRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	ContactLoyaltyRepository contactLoyaltyRepo;


	@Autowired
	ContactLoyaltyDTOConverter loyaltyconvertor;

	@Autowired
	HomeStoreDTOConverter homeStoreDTOconverter;

	ModelMapper modelMapper = new ModelMapper();

	
	@Autowired
	CouponProviderController couponProviderController ;
	@Autowired
	LoyaltyProgramTierRepository loyaltyProgramTierRepo;
	
	@Autowired
	LoyaltyProgramService loyaltyService;
	
	@Autowired
	UserOrganizationRepository userOrganizationRepository;
	
	@Autowired
	ContactDTOConverter contactDTOConverter; 



	/*
	 * Purpose : to prepocess any string/Html/xml related here before sending actual
	 * processing. Params : communication object, Contact Object Return : returns
	 * the final processed string.
	 */
	public String getProcessedTemplate(Communication commObj, Contact contactObj) {
		String finalHtmlContent = "";

		try {
			if (!commObj.getMessageContent().isEmpty()) {
				finalHtmlContent = processedContent(commObj.getMessageContent(), commObj, contactObj);
			}

			logger.info("finalHtmlContent str :" + finalHtmlContent);

		} catch (Exception e) {
			logger.info("Exception while preprocessing :" + e);
		}
		return finalHtmlContent;

	}
	/*
	 * purpose : collects merge tags of each type and helps in replacing. Params :
	 * MessageContent, CommunicationObject , contact Object Returns : Replace
	 * MessageContent with the proper placeholder values.
	 */

	public String processedContent(String content, Communication commObj, Contact contactobj)
			throws TemplateException {

		String templateStr = content;
		String finalOutput = "";
		String finalStrAndPlaceHolder = "";
		String replacedPlaceholders = OCConstants.STRING_NILL;
		Map data = new HashMap<>();

		try {

			Map<String,Object> totalTags = getTotalPlaceHolders(templateStr);
			List<ContactLoyalty> contactLoyaltyListObj = contactLoyaltyRepo.findByContactContactIdAndUserId(contactobj.getContactId(),contactobj.getUserId()); // contactId

			if( totalTags == null || totalTags.isEmpty()){
				//finalStrAndPlaceHolder.add("content");
				return finalStrAndPlaceHolder;
			}
			ContactDTO contactDTO = null;

			if (contactobj != null) {

				Set<String> contactSet = (Set<String>) totalTags.get("contact");

				if (!contactSet.isEmpty()) {

					contactDTO = contactDTOConverter.convert(contactobj);
					data.put("contact", contactDTO); // loadContactDTO()

//					replacedPlaceholders = getPlaceHolderStr(replacedPlaceholders, contactSet, "contact",
//							contactDTO);
				}
				
				Set<String> orgSet = (Set<String>) totalTags.get("organization");
				if(!orgSet.isEmpty()) {
					UserOrganizationDTO orgDTO = loadOrganizationDTO(contactobj,orgSet);
					data.put("organization", orgDTO); 

				}
				

				Set<String> homeStoreSet = (Set<String>) totalTags.get("homeStore");
				
				if (!homeStoreSet.isEmpty()) {
					HomeStoreDTO homeStoreDTO = loadHomeStoreDTO(contactobj);
					data.put("homeStore", homeStoreDTO);
//					replacedPlaceholders = getPlaceHolderStr(replacedPlaceholders, homeStoreSet, "homeStore",
//							homeStoreDTO);

				} // if homeStoreSet > 0
				
				Set<String> loyaltySet =  (Set<String>) totalTags.get("loyalty");
				

				if(contactLoyaltyListObj != null && !contactLoyaltyListObj.isEmpty()) {
		
					logger.info("we got the list of objects :"+contactLoyaltyListObj.get(0).getContact().getContactId());
					
					ContactLoyalty	contactLoyaltyObj = contactLoyaltyListObj.get(0);
						
					if(contactLoyaltyObj != null) {
					
						logger.info(" contact loyalty object : "+contactLoyaltyObj);

					ContactLoyaltyDTO loyaltyDTO = loyaltyconvertor.convert(contactLoyaltyObj);

				if (!loyaltySet.isEmpty()) { // start processing loyalty related tags.

					Map<String, Object> loyaltyMapDTO = loadLoyaltyDTO(contactLoyaltyObj, loyaltySet, commObj,
							replacedPlaceholders);

//					replacedPlaceholders = replacedPlaceholders + loyaltyMapDTO.get("replacePh");
					loyaltyMapDTO.remove("replacePh");
					loyaltyDTO = (ContactLoyaltyDTO) loyaltyMapDTO.get("loyalty");
					data.put("loyalty", loyaltyDTO);

				} // if loyaltyset> 0
				
				Set<String> loyaltyTierSet = (Set<String>) totalTags.get("loyalty.tier");
				
				if(!loyaltyTierSet.isEmpty()) {
					

					Map<String,Object> loyaltyTierDTO = loadLoyaltyTierDTO(contactLoyaltyObj, loyaltyTierSet,replacedPlaceholders);
					replacedPlaceholders = replacedPlaceholders+ loyaltyTierDTO.get("replacePh");
					loyaltyTierDTO.remove("replacePh");
					LoyaltyProgramTierDTO tierDTO  = (LoyaltyProgramTierDTO)loyaltyTierDTO.get("loyalty.tier");
					if(loyaltyDTO != null)
						loyaltyDTO.setTier(tierDTO);
					else {
						loyaltyDTO = new ContactLoyaltyDTO();
						loyaltyDTO.setTier(tierDTO);
					}
				//	data.put("loyalty.tier", loyaltyTierDTO);
					data.put("loyalty",loyaltyDTO);

				}
					}	
				}else {
					ContactLoyaltyDTO loyaltyDTO = new ContactLoyaltyDTO();
					LoyaltyProgramTierDTO tierDTO = new  LoyaltyProgramTierDTO();
					loyaltyDTO.setTier(tierDTO);
					data.put("loyalty", loyaltyDTO);

				}
				Set<String> dateSet = (Set<String>) totalTags.get("date");
				if (!dateSet.isEmpty()) {
					templateStr = replaceDateValues(dateSet, templateStr,replacedPlaceholders);
				}
				Set<String> couponSet = (Set<String>) totalTags.get("coupon");
				logger.info("size of coupon set :"+couponSet.size());
				if(!couponSet.isEmpty()) {
				
				 Map<String,Object> datat = replaceCouponValues(couponSet,templateStr,contactobj);
				
				
				data.put("coupon", datat);
			
				logger.info("replaced coupon : "+datat);
			}

				finalOutput = submitToFreeMarker( data, templateStr);
			}

		} catch (Exception e) {
			logger.info("Exception while mail merge : ", e);
		}
		/*// TODO
		 * if (!replacedPlaceholders.isEmpty() && communicationSent != null ) {
		 * communicationSent.setContactPhValStr(replacedPlaceholders);
		 * communicationSentRepo.save(communicationSent); }
		 */
		finalStrAndPlaceHolder = finalOutput;
		//finalStrAndPlaceHolder.add(communicationSent);
		//finalStrAndPlaceHolder.add(communicationReport);

		return finalStrAndPlaceHolder;
	}

	private String submitToFreeMarker(Map data, String templateStr)
			throws TemplateException {
		String finalOutput = "";
		try {
			Configuration cfg = new Configuration(new Version("2.3.30"));
			Template template = new Template("myTemplate", templateStr, cfg);
			StringWriter output = new StringWriter();
			template.process(data, output);
			finalOutput = output.toString();

			logger.info("Template after processng : {}" , finalOutput);
		} catch (IOException e) {
			logger.error("Exception caused : ", e);
		}
		return finalOutput;
	}

	private Map<String, Object> getTotalPlaceHolders(String messageContent
			) {
		Map<String, Object> totalTags = new HashMap<>();
		try {
			Set<String> contactSet = new HashSet<>();
			Set<String> loyaltySet = new HashSet<>();
			Set<String> loyaltyTierSet = new HashSet<>();
			Set<String> homeStoreSet = new HashSet<>();
			Set<String> orgSet = new HashSet<>();

			Set<String> dateSet = new HashSet<>();
			Set<String> couponSet = new HashSet<String>();
			String ph = "";
			StringBuilder placeholder = new StringBuilder();
			// get all the placeholders to here.
			Pattern p = Pattern.compile("\\$\\{.*?\\}", Pattern.CASE_INSENSITIVE);

		Matcher match = p.matcher(messageContent);

			while (match.find()) {

				ph = match.group(0); // .toUpperCase()
				logger.debug("Group 0 : {}" , match.group(0));
				ph = ph.replace("${", "").replace("}", "");

				if (!placeholder.isEmpty()) {
					placeholder = placeholder.append(",");
				}
				placeholder = placeholder.append(ph);

				if (ph.startsWith("contact")) {
					contactSet.add(ph);
				} else if (ph.startsWith("loyalty") && !ph.contains("loyalty.tier")) {
					loyaltySet.add(ph);
				} else if (ph.startsWith("loyalty.tier")) {
					loyaltyTierSet.add(ph);
				} else if (ph.startsWith("homeStore")) {
					homeStoreSet.add(ph);
				}else if (ph.startsWith("organization")) {
					orgSet.add(ph);
				} else if (ph.startsWith("date")) {
					dateSet.add(ph);
				}else if(ph.startsWith("coupon")) {
					//	${coupon.CC_couponID}.replace("${coupon.", "").replace("}","");
						// CC_13123
						couponSet.add(ph);
					}


			} // while
			totalTags.put("contact",contactSet );
			totalTags.put("loyalty", loyaltySet);
			totalTags.put("loyalty.tier",loyaltyTierSet );
			totalTags.put("homeStore",homeStoreSet );
			totalTags.put("organization",orgSet );
			totalTags.put("date",dateSet );
			totalTags.put("coupon", couponSet);

			/*// TODO
			 * if (!placeholder.isEmpty()) {
			 * communicationReport.setPlaceHolders(placeholder.toString());
			 * communicationReportRepo.save(communicationReport); }
			 */
			
		} catch (Exception e) {
			logger.error("Exception while getting the symbol place holders ", e);
		}
		return totalTags;
	}
	/*
	 * purpose : loads all the loyalty tags and loyalty.tier tags in the template
	 * params : contactObject, loyaltySet, Communication object(for sms) returns :
	 * map of 2 objects 1.contactLoyalty = ${loyalty.######} 2.LoyaltyTier =
	 * ${loyalty.tier.######}
	 */
	private Map<String, Object> loadLoyaltyDTO(ContactLoyalty contactLoyaltyObj, Set<String> loyaltySet,
			Communication commObj, String replacedPhValue) {

		LoyaltyProgramTier tierObj = null;
		ContactLoyaltyDTO contactLoyaltyDTO = new ContactLoyaltyDTO();
		Map<String, Object> resultSet = new HashMap<>();
		

		try {
							
			contactLoyaltyDTO = loyaltyconvertor.convert(contactLoyaltyObj);


			for (String tag : loyaltySet) {
			
					if (tag.contains("giftAmountExpirationPeriod")) {
						String giftAmountExpirationPeriod = loyaltyService
								.getGiftAmountExpirationPeriod(contactLoyaltyObj);
						contactLoyaltyDTO.setGiftAmountExpirationPeriod(giftAmountExpirationPeriod);
					}
					if(tag.contains("lifeTimePurchaseValue") && contactLoyaltyObj.getMembershipStatus().equalsIgnoreCase("Active")) {
						double lifeTimeValue = loyaltyService.getLPV(contactLoyaltyObj);
						contactLoyaltyDTO.setLifeTimePurchaseValue(lifeTimeValue);

					}
				if (tag.contains("enrollmentSource")) {
					String source = loyaltyService.getEnrollmentSource(contactLoyaltyObj.getContactLoyaltyType());
					contactLoyaltyDTO.setEnrollmentSource(source);
				}

				if (tag.contains("holdBalance")) {

					String holdBalance = loyaltyService.getHoldBalanceValue(contactLoyaltyObj);
					contactLoyaltyDTO.setHoldBalance(holdBalance);
				}
				if (tag.contains("redeemedCurrency")) {
					
					if(contactLoyaltyObj.getProgramTierId() != null && contactLoyaltyObj.getMembershipStatus().equalsIgnoreCase("Active"))
						tierObj = loyaltyProgramTierRepo.findBytierId(contactLoyaltyObj.getProgramTierId());

					double redeemedCurrency = loyaltyService.getRedeemedCurrencyValue(contactLoyaltyObj, tierObj);
					contactLoyaltyDTO.setRedeemedCurrency(redeemedCurrency);
				}

				if (contactLoyaltyObj.getCardNumber() != null) {
					
					if (tag.contains("lastEarnedValue")) {
						String lastEarnedValue = loyaltyService.getLoyaltyLastEarnedValue(contactLoyaltyObj.getUserId(),
								contactLoyaltyObj.getLoyaltyId(), "Issuance");
						contactLoyaltyDTO.setLastEarnedValue(lastEarnedValue);
					}
					if (tag.contains("lastRedeemedValue")) {
						String lastRedeemedValue = loyaltyService.getLoyaltyLastRedeemedValue(
								contactLoyaltyObj.getUserId(), contactLoyaltyObj.getLoyaltyId(), "Redemption");
						contactLoyaltyDTO.setLastRedeemedValue(lastRedeemedValue);
					}
					if (tag.contains("lastBonusValue")) {
						String lastBonusValue = loyaltyService.getLastBonusValue(contactLoyaltyObj.getUserId(),
								contactLoyaltyObj.getLoyaltyId(), "Bouns");
						contactLoyaltyDTO.setLastBonusValue(lastBonusValue);
					}
				}
				if (!contactLoyaltyObj.getRewardFlag().equalsIgnoreCase("G") && tag.contains("membershipPassword")) {
					boolean isSms = commObj.getChannelType().equalsIgnoreCase("SMS");
					String membershipPassword = loyaltyService.getMembershipPasswordValue(contactLoyaltyObj, isSms);
					contactLoyaltyDTO.setMembershipPassword(membershipPassword);
				}

				if (contactLoyaltyObj.getProgramId() != null) {

					if (tag.contains("giftCardExpriationPeriod")) {
						LocalDateTime giftCardExpriationDate = loyaltyService
								.getGiftCardExpirationDate(contactLoyaltyObj);
						String desiredDate = giftCardExpriationDate != null ? giftCardExpriationDate.toString(): null;
						
						contactLoyaltyDTO.setGiftCardExpriationDate(desiredDate);
					}

					// TODO need to verify as it involves the only few fields in resultset (
					// fetchExpiryValues method in 2.0)
					if (tag.contains("giftAmountExpiringValue")) {
						String giftAmountExpiringValue = loyaltyService.getGiftAmountExpiringValue(contactLoyaltyObj);
						contactLoyaltyDTO.setGiftAmountExpiringValue(giftAmountExpiringValue);

					}
				}
			}
				if (contactLoyaltyObj != null) {
					replacedPhValue = getPlaceHolderStr(replacedPhValue, loyaltySet, "loyalty", contactLoyaltyDTO);
				}
			
				

			//replace all the loyalty.tags as not availble.
			resultSet.put("loyalty", contactLoyaltyDTO);
			
			resultSet.put("replacedPh", replacedPhValue);
			
		} catch (Exception e) {
			logger.info("Exception while getting the loyalty DTO :", e);
		}
		return resultSet;
	}

	private Map<String,Object> loadLoyaltyTierDTO(ContactLoyalty contactLoyaltyObj,Set<String> loyaltyTierSet,String replacePlaceholder
			
			//ContactLoyalty contactLoyaltyObj, LoyaltyProgramTier tierObj,
			) {
		
		Map<String,Object> result =  new HashMap<>();
		LoyaltyProgramTier tierObj = null; //loyaltyProgramTierRepo.findBytierId(contactLoyaltyObj.getProgramTierId());
		LoyaltyProgramTierDTO tierDTO = null;
		
		if (contactLoyaltyObj.getMembershipStatus().equalsIgnoreCase("Active")
				&& contactLoyaltyObj.getProgramTierId() != null) {

			tierObj = loyaltyProgramTierRepo.findBytierId(contactLoyaltyObj.getProgramTierId());
			 tierDTO = modelMapper.map(tierObj, LoyaltyProgramTierDTO.class);
		
		for (String tag : loyaltyTierSet) {
			
			if(tag.contains("programTierName")){
			tierDTO.setProgramTierName(getProgramTierName(tierObj.getTierType(),tierObj.getTierName()));
			}				
			if (tag.contains("rewardExpirationPeriod")) {
				String rewardExpirationPeriodValue = loyaltyService.getRewardExpirationPeriodValue(contactLoyaltyObj);
				tierDTO.setRewardExpirationPeriod(rewardExpirationPeriodValue);
			}
			if (tag.contains("membershipExpirationDate")) {
				LocalDateTime membershipExpirationDate = loyaltyService
						.getLoyaltyMembershipExpirationDate(contactLoyaltyObj);
				String desiredDataType = membershipExpirationDate != null ?membershipExpirationDate.toString() : null;
				tierDTO.setMembershipExpirationDate(desiredDataType);
			} // ---done
			if (tag.contains("rewardActivationPeriod") && (tierObj.getActivationFlag() == 'Y')) {

				String rewardActivationPeriod = tierObj.getPtsActiveDateValue() + " " + tierObj.getPtsActiveDateType()
						+ "(s)";
				tierDTO.setRewardActivationPeriod(rewardActivationPeriod);
			}

			// check the query once as it has the custom fields.
			// TODO - skipping for now as query has to finalize
			if ((contactLoyaltyObj.getProgramId() != null) && tag.contains("rewardExpiringValue")) {
				String rewardExpiringValue = loyaltyService.getRewardExpiringValue(contactLoyaltyObj);
				tierDTO.setRewardExpiringValue(rewardExpiringValue);

			}
		} // for 
		} // tierObj ! =null
		try {
		if (tierObj != null) {
			replacePlaceholder = getPlaceHolderStr(replacePlaceholder, loyaltyTierSet, "loyalty.tier", tierDTO);
		}else {
			tierDTO = modelMapper.map(tierObj, LoyaltyProgramTierDTO.class);
		}
		}catch(Exception e) {
			logger.error("Exception occurred while replacing merge loyalty.tier tags : ",e);
		}
		result.put("loyalty.tier", tierDTO);
		result.put("replacePh",replacePlaceholder);

		return result ;
	}

	/*
	 * purpose : to load the home store related merge tags. params : contact Object
	 * returns : HomestoreDTO to map in Data object.
	 */
	private HomeStoreDTO loadHomeStoreDTO(Contact contactobj) {

		HomeStoreDTO homeStoreDTO;
		String homeStoreId = contactobj.getHomeStore();
		OrganizationStore orgStoreObj = null;
		User userObj = userRepo.findByuserId(contactobj.getUserId());

		if (contactobj.getSubsidiaryNumber() != null) {
			orgStoreObj = orgstoreRepo.findByhomeStoreIdAndSubsidiaryNumber(homeStoreId,
					userObj.getUserOrganization().getUserOrgId(), contactobj.getSubsidiaryNumber());
		} else {
			orgStoreObj = orgstoreRepo.findByhomeStoreId(homeStoreId, userObj.getUserOrganization().getUserOrgId());
		}
		if (orgStoreObj == null) {
			// get user's address...
			homeStoreDTO = homeStoreDTOconverter.convert(userObj);
			logger.info("unable to replace the homestore object as we got null");

			return homeStoreDTO;

		} else {
			homeStoreDTO = modelMapper.map(orgStoreObj, HomeStoreDTO.class);
			if(orgStoreObj.getAddressStr() != null && !orgStoreObj.getAddressStr().isEmpty())
				homeStoreDTO.setAddressStr(loadHomeStoreAddress(orgStoreObj));

			logger.info("Organisation to replace the homestore object as :" + homeStoreDTO);

			return homeStoreDTO;

		}

	} // end of the loadHomeStore()userObj

	private String loadHomeStoreAddress(OrganizationStore orgStoreObj) {
		// TODO Auto-generated method stub
		StringBuilder storeAddress = new StringBuilder();
		String[] strAddr = orgStoreObj.getAddressStr().split(";=;");
		int count = 0;
		for(String str : strAddr){
			count++;
			
			if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
				storeAddress.append(" | Phone: "+str); // we have 7 fields appended so  7th pos will be phone number. 
			}
			else if(storeAddress.length()==0 && str.trim().length()>0){
				storeAddress.append(str);
			}
			else if(storeAddress.length()>0 && str.trim().length()>0){
				storeAddress.append(", "+str);
			}
		}
		return storeAddress.toString();
	}
	private String replaceDateValues(Set<String> dateSet, String templateStr,String replacedPlaceholders) {

		// replace the content having those values date values.
		/*
		 * ${date.tomorrow} -- handled. ${date.next_7_days} ${date.next_30_days}
		 * ${date.next_x_days}
		 */
		
		for (String date : dateSet) {
			LocalDateTime today = LocalDateTime.now();
			// can have the format passed from the user and change.
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			if (date.contains("tomorrow")) {
				logger.info("Date we got from the string : ${date.tomorrow} ", date);

				LocalDate tomorrow = today.toLocalDate().plusDays(1);
				String formattedDateTime = tomorrow.format(dateTimeFormatter);
				templateStr = templateStr.replace("${" + date + "}", formattedDateTime);
				logger.info("Tomorrow day : " + formattedDateTime);
//				if(!replacedPlaceholders.isEmpty())
//					replacedPlaceholders += replacedPlaceholders+";";
//				replacedPlaceholders+=replacedPlaceholders+date+"::"+formattedDateTime;
//
//
			}
			if (date.contains("next")) {
				logger.info("Date we got from the string : ${date.next_7_days} ", date);
				String[] daysForward = date.split("_");
				/*
				 * 0.date.next 1.7,30,X 2.days
				 */
				LocalDate moveTo = today.toLocalDate();
				LocalDate actualDate = moveTo.plusDays(Integer.parseInt(daysForward[1].trim()));
				String formattedDateTime = actualDate.format(dateTimeFormatter);
				templateStr = templateStr.replace("${" + date + "}", formattedDateTime);
				logger.info("after  day : ", formattedDateTime);
//				if(!replacedPlaceholders.isEmpty())
//					replacedPlaceholders += replacedPlaceholders+";";
//				replacedPlaceholders+=replacedPlaceholders+date+"::"+formattedDateTime;
//

			}
			
		}
		return templateStr;
	}

private  Map<String,Object> replaceCouponValues(Set<String> couponSet,String templateStr,Contact contactobj) throws IOException {
		
			//couponSet= {${coupon.CC_13123}};
		
			Map<String, Object> couponmap = new HashMap<>();
	       
			for(String coupon : couponSet) {
				
				String prevPh = coupon;//"${coupon.CC_12626}"; 
				String couponIdStr = 	coupon.replace("${coupon.", "").replace("}","");
				logger.info("after removing value is"+couponIdStr);
				String[] parts = couponIdStr.split("\\.");
				logger.info("after splitting  part1 value is"+parts[1]); //CC_12345
				
				String value="";
				String[] strArr = parts[1].split("_"); //CC,12345
				try {
					Long couponId =Long.parseLong(strArr[1]);
					value = couponProviderController.generateCoupon(couponId, contactobj.getMobilePhone(), "SMS", contactobj.getContactId());
				}
				catch (Exception e){
					logger.info("Exception while extracting couponId",e);
				}
				if(value == null) value = "";
				
				//in.close();
				logger.info("response is======>"+value); 
				couponmap.put(parts[1],value);
				 
				
			}


			
			return couponmap;
	
		
		
	}

	public String getPlaceHolderStr(String replaceValue, Set<String> tags, String type, Object referenceClass)
			throws IntrospectionException {
		try {

			for (String phValue : tags) {

				int pos = phValue.indexOf("!");
				if (pos == -1)
					return "";

				phValue = phValue.substring(0, pos);
				if (type.contains("contact")) {
					phValue = phValue.replace("contact.", "").trim();// .replace("!","").replace("'N/A'", "");
				} else if (type.contains("loyalty.tier")) {
					phValue = phValue.replace("loyalty.tier.", "").trim();// .replace("!","").replace("'N/A'", "");
				}else if (type.contains("loyalty")) {
					phValue = phValue.replace("loyalty.", "").trim();// .replace("!","").replace("'N/A'", "");
				}  else if (type.contains("homeStore")) {
					phValue = phValue.replace("homeStore.", "").trim();// .replace("!","").replace("'N/A'", "");
				}

				PropertyDescriptor propertyDescriptor = new PropertyDescriptor(phValue.trim(),
						referenceClass.getClass());
				Object value = propertyDescriptor.getReadMethod().invoke(referenceClass);

				logger.info("Tags values : " + phValue + " :: " + value);
				if (value == null) {
					continue;
				} else {
					if (!replaceValue.isEmpty()) {
						replaceValue += ";";
					}
					if(replaceValue != null)
					replaceValue = replaceValue + type + "." + phValue + "::" + value;
				}
			}

			logger.info("values replaced are : " + replaceValue);
			// save the respective value saved for respective placeholder.
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		logger.error("Got exception while getting properties from the object ,{} ,{} ,{}"+e);
		}
		return replaceValue;
	}
	
		private String getProgramTierName(String tierType,String tierName) {
		// tier name is shown along with the tier type
		
		String level= "";
		
		level = " ( Level : "+(tierType != null ? tierType: "")+")"; 

		return  tierName +level;

		
	
	}
		private UserOrganizationDTO loadOrganizationDTO(Contact contactObj,Set<String> orgSet) {
			logger.debug(">>>>>>>>>>>>> entered in getUserOrganization");
			
			UserOrganizationDTO orgDTO = null;
			try {
		// as we only have one ph :organizationName is directly mapped while modelMapper.
			User user = userRepo.findByuserId(contactObj.getUserId());
			UserOrganization userOrg = userOrganizationRepository.findByUserOrgId(user.getUserOrganization().getUserOrgId());
			orgDTO = modelMapper.map(userOrg,UserOrganizationDTO.class);
				
			}catch (Exception e) {
				logger.error("Exception ::",e);
			}
			logger.debug("<<<<<<<<<<<<< completed getUserOrganization");
			return orgDTO;
		}
}

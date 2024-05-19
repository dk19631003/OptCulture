package org.mq.optculture.business.mobileapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.beans.UsersDomains;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.mobileapp.City;
import org.mq.optculture.model.mobileapp.Country;
import org.mq.optculture.model.mobileapp.FilterBy;
import org.mq.optculture.model.mobileapp.Locality;
import org.mq.optculture.model.mobileapp.RequestHeader;
import org.mq.optculture.model.mobileapp.ResponseHeader;
import org.mq.optculture.model.mobileapp.ResponseReport;
import org.mq.optculture.model.mobileapp.StoreInfo;
import org.mq.optculture.model.mobileapp.StoreInquiry;
import org.mq.optculture.model.mobileapp.StoreInquiryRequest;
import org.mq.optculture.model.mobileapp.StoreInquiryResponse;
import org.mq.optculture.model.mobileapp.StoreSortedBy;
import org.mq.optculture.model.mobileapp.Stores;
import org.mq.optculture.model.ocloyalty.Status;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.google.gson.Gson;

public class StoreInquiryServiceImpl implements StoreInquiryService{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	public BaseResponseObject processRequest(BaseRequestObject baseRequestObject) throws BaseServiceException {
		BaseResponseObject baseResponseObject=new BaseResponseObject();
		StoreInquiryResponse storeInquiryResponse=new StoreInquiryResponse();
		try {
			
			logger.debug("-------entered processRequest---------");
			String serviceRequest = baseRequestObject.getAction();
			String requestJson = baseRequestObject.getJsonValue();
			String transactionID = baseRequestObject.getTransactionId();
			String transactionDate = baseRequestObject.getTransactionDate();
			
			String responseJson = null;
			Gson gson = new Gson();
			StoreInquiryRequest storeInquiryRequest = null;
			try {
				storeInquiryRequest = gson.fromJson(baseRequestObject.getJsonValue(), StoreInquiryRequest.class);
			} catch (Exception e) {
				logger.error("Exception ::",e);
				Status status = new Status("800000",PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				ResponseHeader header = new ResponseHeader(Constants.STRING_NILL,
						Constants.STRING_NILL, transactionDate, transactionID);
				
				storeInquiryResponse = prepareFinalResponse(header,status);
				String json=gson.toJson(storeInquiryResponse);
				baseResponseObject.setJsonValue(json);
				baseResponseObject.setAction(OCConstants.COUPONS_HISTORY_SERVICE_REQUEST);
				return baseResponseObject;
			}
			try{
				StoreInquiryService storeInquiryService = (StoreInquiryService) ServiceLocator.getInstance().getServiceByName(OCConstants.STORE_INQUIRY_SERVICE);
				storeInquiryResponse = storeInquiryService.processStoreInquiryRequest(storeInquiryRequest, transactionID, transactionDate);
				responseJson = gson.toJson(storeInquiryResponse);
				
				baseResponseObject = new BaseResponseObject();
				baseResponseObject.setAction(serviceRequest);
				baseResponseObject.setJsonValue(responseJson);
			}catch(Exception e){
				logger.error("Exception in loyalty enroll base service.",e);
				throw new BaseServiceException("Server Error.");
			}
			logger.info("Completed processing baserequest... ");
			return baseResponseObject;
		} catch (Exception e) {
			logger.error("Exception ::",e);
		}
		return baseResponseObject;
	}

	@Override
	public StoreInquiryResponse processStoreInquiryRequest(StoreInquiryRequest storeInquiryRequest,
			String transactionId, String transactionDate) throws BaseServiceException {
		StoreInquiryResponse storeInquiryResponse = null;
		Status status = null;
		Users user = null;
		ResponseHeader responseHeader = new ResponseHeader();
		responseHeader.setRequestDate(storeInquiryRequest.getHeader().getRequestDate());
		responseHeader.setRequestId(storeInquiryRequest.getHeader().getRequestId());
		responseHeader.setTransactionDate(transactionDate);
		responseHeader.setTransactionId(transactionId);
		try{
		//	ApplicationPropertiesDao applicationPropertiesDao = (ApplicationPropertiesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.APPLICATION_PROPERTIES_DAO);
			
			status = validateJsonData(storeInquiryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				storeInquiryResponse = prepareFinalResponse(responseHeader, status);
				return storeInquiryResponse;
			}
			
			
			user = getUser(storeInquiryRequest.getUser().getUserName(), storeInquiryRequest.getUser().getOrganizationId());
			if(user == null){
				status = new Status("800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				storeInquiryResponse = prepareFinalResponse(responseHeader, status);
				return storeInquiryResponse;
			}
			
			if(!user.isEnabled()){
				status = new Status("800003", PropertyUtil.getErrorMessage(800003, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				storeInquiryResponse = prepareFinalResponse(responseHeader, status);
				return storeInquiryResponse;
			}
			if(user.getPackageExpiryDate().before(Calendar.getInstance())){
				status = new Status("800004", PropertyUtil.getErrorMessage(800004, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				storeInquiryResponse = prepareFinalResponse(responseHeader, status);
				return storeInquiryResponse;
			}
			
			
			/*status = validateInnerObjects(storeInquiryRequest);
			if(status != null && OCConstants.JSON_RESPONSE_FAILURE_MESSAGE.equals(status.getStatus())){
				storeInquiryResponse = prepareFinalResponse(responseHeader, status);
				return storeInquiryResponse;
			}*/
			UsersDao usersDao  = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			
			List<UsersDomains> domainsList = usersDao.getAllDomainsByUser(user.getUserId());//finding this power user's domain
			logger.debug("==STEP I====");
			if(domainsList == null) return null;
			
			logger.info("==domainsList.get(0).getDomainId()=="+domainsList.get(0).getDomainId());
			
			Long domainId=domainsList.get(0).getDomainId();	
			FilterBy filterBy=storeInquiryRequest.getFilterBy();
			String brand=filterBy.getBrand();
			String subId=filterBy.getSubsidiaryId();
			String subName=filterBy.getSubsidiaryName();
			StoreInquiry storeInquiry=storeInquiryRequest.getStoreInquiry();
			String country=storeInquiry.getCountry();
			String city=storeInquiry.getCity();
			String locality=storeInquiry.getLocality();
			String storeId=storeInquiry.getStoreId();
			String storeName=storeInquiry.getStoreName();
			String zipcode=storeInquiry.getZipCode();
			StoreSortedBy sortby = storeInquiryRequest.getSortBy();
			String type=sortby.getType();
			
			
			String brandStr="";
			String[] brands=null;
			if(brand!=null && !brand.isEmpty()){
			if(brand.contains(",")){
				brands=brand.split(",");
				for(String singleBrand : brands){
					if (singleBrand.contains("'")) {
						singleBrand = StringEscapeUtils.escapeSql(singleBrand);
					}
					if (!brandStr.isEmpty())
						brandStr += ", ";
					brandStr += "'" + singleBrand.trim() + "'";
					
				}
			}else{
				brandStr="'"+StringEscapeUtils.escapeSql(brand)+"'";
			}
			}
				
			
			
			
			
			
			
			String order="desc";
			if(sortby.getOrder()!=null || !sortby.getOrder().isEmpty()){
			order=((sortby.getOrder().equalsIgnoreCase("asc"))?(sortby.getOrder().toLowerCase()):order);
			}
			
			OrganizationStoresDao organizationStoresDao = (OrganizationStoresDao)ServiceLocator.getInstance().getDAOByName(OCConstants.ORGANIZATION_STORES_DAO);
			List<OrganizationStores> orgStoresList=organizationStoresDao.findStoreInquiryRequest(user.getUserOrganization().getUserOrgId(),domainId,
					country,city,locality,storeId,storeName,zipcode,brandStr,subId,subName,type,order);
			
			//StoreInfo storeinfo=prepareResponseByFilter(orgStoresList,storeInquiryRequest);
			//storeInquiryResponse=prepareFinalResponse(responseHeader, status,filterBy, storeInquiry, storeinfo);
			
			
			LinkedHashSet<String> subNameSet = new LinkedHashSet<String>();
			LinkedHashSet<String> brandSet = new LinkedHashSet<String>();
			LinkedHashSet<String> countrySet = new LinkedHashSet<String>();
			LinkedHashSet<String> citySet =null;
			LinkedHashSet<String> localitySet=null;
		//	Set<String> localitySet = new HashSet<String>();
			List<Country> countryResponseList=null;
			List<City> cityResponseList=null;
			List<Locality> localityResponseList=null;
			List<Stores> storeResponseList=null;
			
			
			countryResponseList=new ArrayList<Country>();
			if(orgStoresList==null){
				//status = new Status("800003", PropertyUtil.getErrorMessage(800003, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				logger.info("---"+PropertyUtil.getErrorMessage(800019, OCConstants.ERROR_MOBILEAPP_FLAG));
				status = new Status("800019", PropertyUtil.getErrorMessage(800019, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				storeInquiryResponse = prepareFinalResponse(responseHeader, status);
			}else{
			
		    for (OrganizationStores organizationStores : orgStoresList) {
		    	if(organizationStores.getCountry()!=null){
		    	
		    
		    			  countrySet.add(organizationStores.getCountry().toUpperCase());
		    			
		    	}
		    	if(organizationStores.getStoreBrand()!=null && !organizationStores.getStoreBrand().isEmpty()){
		    		brandSet.add(organizationStores.getStoreBrand().toUpperCase());
		    	}
		    	if(organizationStores.getSubsidiaryName()!=null && !organizationStores.getSubsidiaryName().isEmpty()){
		    		subNameSet.add(organizationStores.getSubsidiaryName().toUpperCase());
		    	}
		    	}
		    
		    
		    
			
		  //  List<OrganizationStores> countryList=null;
		    for (String countryName : countrySet) {
		    	List<OrganizationStores> countryList=new ArrayList<OrganizationStores>();
		    	for(OrganizationStores organizationStores: orgStoresList){
		    		if(countryName.equalsIgnoreCase(organizationStores.getCountry())){
		    			
		    				countryList.add(organizationStores);
		    			}
		    		}
		    	citySet = new LinkedHashSet<String>();
		    	for (OrganizationStores organizationStores : countryList) {
		    		if(organizationStores.getCity()!=null){
		    	citySet.add(organizationStores.getCity().toUpperCase());	
		    		}
		    	}
		    	//List<OrganizationStores> cityList=null;
		    	cityResponseList=new ArrayList<City>();
		    	for(String cityName : citySet){
		    		List<OrganizationStores> cityList=new ArrayList<OrganizationStores>();
		    	for(OrganizationStores organizationStores : countryList){
		    		if(cityName.equalsIgnoreCase(organizationStores.getCity())){
		    			cityList.add(organizationStores);
		    		}
		    	}
		    
		    	localitySet = new LinkedHashSet<String>();
		    	for (OrganizationStores organizationStores : cityList) {
		    		if(organizationStores.getLocality()!=null){
		    		localitySet.add(organizationStores.getLocality().toUpperCase());		
		    	}
		    	}
		    //	List<OrganizationStores> localityList=null;
		    	localityResponseList=new ArrayList<Locality>();
		    	for(String localityName : localitySet){
		    		List<OrganizationStores> localityList =new ArrayList<OrganizationStores>();
		    		
		    	for(OrganizationStores organizationStores : cityList){
		    		if(localityName.equalsIgnoreCase(organizationStores.getLocality())){
		    			localityList.add(organizationStores);
		    		}
		    	}
		    	
		    	storeResponseList=new ArrayList<Stores>();
		    	for(OrganizationStores orgStores:localityList){
		    		Stores stores=new Stores();
		    		stores.setBrand(orgStores.getStoreBrand());
		    		stores.setCity(orgStores.getCity());
		    		stores.setCountry(orgStores.getCountry());
		    		stores.setBrandImagePath(orgStores.getBrandImagePath());
		    		stores.setDescription(orgStores.getDescription());
		    		stores.setEmailId(orgStores.getEmailId());
		    		stores.setLatitude(orgStores.getLatitude());
		    		stores.setLongitude(orgStores.getLongitude());
		    		stores.setLocality(orgStores.getLocality());
		    		stores.setMobileNo(orgStores.getMobileNo());
		    		stores.setState(orgStores.getState());
		    		stores.setStoreId(orgStores.getHomeStoreId().toString());
		    		stores.setStoreImagePath(orgStores.getStoreImagePath());
		    		stores.setStoreManager(orgStores.getStoreManagerName());
		    		stores.setStoreName(orgStores.getStoreName());
		    		stores.setSubsidiaryId(orgStores.getSubsidiaryId());
		    		stores.setSubsidiaryName(orgStores.getSubsidiaryName());
		    		stores.setZipCode(orgStores.getZipCode());
		    		
		    		storeResponseList.add(stores);
		    	}
		    	Locality localityResponse=new Locality();
		    	localityResponse.setLocalityName(localityName);
		    	if(!storeInquiryRequest.getInquiryType().equalsIgnoreCase("filters")){
		    		localityResponse.setStores(storeResponseList);
		    	}
		    	localityResponseList.add(localityResponse);
		    	}
		    	City cityResponse=new City();
		    	cityResponse.setCityName(cityName);
		    	cityResponse.setLocality(localityResponseList);
		    	cityResponseList.add(cityResponse);
		    }
		    Country countryResponse=new Country();
		    countryResponse.setCountryName(countryName);
		    countryResponse.setCity(cityResponseList);
		    
		    countryResponseList.add(countryResponse);
		    }
		
		    
		    StoreInfo storeInfo=new StoreInfo();
		    storeInfo.setCountry(countryResponseList);
			
		    status = new Status("0", "Store Inquiry was successful.",OCConstants.JSON_RESPONSE_SUCCESS_MESSAGE);
		    Filter filter = new Filter();
		    filter.setBrand(brandSet);
		    filter.setSubsidiaryName(subNameSet);
		    if(storeInquiryRequest.getInquiryType().equalsIgnoreCase("filters")){
		    }	
		    
		    storeInquiryResponse=prepareFinalResponse(responseHeader, status,filterBy, storeInquiry, storeInfo,filter,storeInquiryRequest.getInquiryType(),sortby);
		
		}
		}catch (Exception e) {
			status = new Status("800001", PropertyUtil.getErrorMessage(800001, OCConstants.ERROR_MOBILEAPP_FLAG),OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			storeInquiryResponse = prepareFinalResponse(responseHeader, status);
			return storeInquiryResponse;
		}
		return storeInquiryResponse;
	}
	private Status validateJsonData(StoreInquiryRequest storeInquiryRequest) throws Exception{
		logger.info("Entered validateJsonData method >>>>");
		Status status = null;
		if(storeInquiryRequest == null ){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(storeInquiryRequest.getUser() == null){
			status = new Status(
					"800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(storeInquiryRequest.getStoreInquiry() == null || storeInquiryRequest.getFilterBy()==null){
			status = new Status(
					"800000", PropertyUtil.getErrorMessage(800000, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		if(storeInquiryRequest.getUser().getUserName() == null || 
				storeInquiryRequest.getUser().getUserName().trim().length() <=0 || 
						storeInquiryRequest.getUser().getOrganizationId() == null || 
								storeInquiryRequest.getUser().getOrganizationId().trim().length() <=0 
										) {
			status = new Status("800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		String sourceType= storeInquiryRequest.getHeader().getSourceType();
		if(storeInquiryRequest.getHeader().getSourceType() != null && 
				!storeInquiryRequest.getHeader().getSourceType().equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP) && 
				(storeInquiryRequest.getUser().getToken() == null || 
				storeInquiryRequest.getUser().getToken().trim().length() <=0)){
			status = new Status("800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
		}
		
		if(sourceType != null && sourceType.equals(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_LOYALTY_APP)){
			
			if(storeInquiryRequest.getUser().getSessionID() == null || storeInquiryRequest.getUser().getSessionID().isEmpty()){
				
				status = new Status("800002", PropertyUtil.getErrorMessage(800002, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
				return status;
			}
			
		}
		
		if(sourceType != null && sourceType.equalsIgnoreCase(OCConstants.LOYALTY_TRANSACTION_SOURCE_TYPE_MOBILE_APP)){
			status = new Status("800032", PropertyUtil.getErrorMessage(800032, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			return status;
			
		}
		
				
		return status;
	}
	private Status validateInnerObjects(StoreInquiryRequest storeInquiryRequest) throws Exception{
		
		Status status = null;
		StoreInquiry storeInquiry = storeInquiryRequest.getStoreInquiry();
		

		FilterBy filterBy = storeInquiryRequest.getFilterBy();
		if((storeInquiry.getCountry() ==null || storeInquiry.getCountry().isEmpty())) {}
				
		if((storeInquiry.getCountry() == null || storeInquiry.getCountry().isEmpty()) && 
				(storeInquiry.getCity() == null || storeInquiry.getCity().isEmpty()) && 
				(storeInquiry.getLocality() == null || storeInquiry.getLocality().isEmpty())&&
				(storeInquiry.getStoreId() == null || storeInquiry.getStoreId().isEmpty()) &&
				(storeInquiry.getZipCode() == null || storeInquiry.getZipCode().isEmpty()) &&
				(storeInquiry.getStoreName() == null || storeInquiry.getZipCode().isEmpty())&&
				(filterBy.getBrand()==null || filterBy.getSubsidiaryId().isEmpty())&&
				(filterBy.getSubsidiaryId()==null || filterBy.getSubsidiaryName().isEmpty())) {
			status = new Status(
					"800005", PropertyUtil.getErrorMessage(800005, OCConstants.ERROR_MOBILEAPP_FLAG), OCConstants.JSON_RESPONSE_FAILURE_MESSAGE);
			
		}
		
		return status;
		
		
	}
	private Users getUser(String userName, String orgId) throws Exception{
		
		String completeUserName = userName+Constants.USER_AND_ORG_SEPARATOR+orgId;
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		Users user = usersDao.findByUsername(completeUserName);
		return user;
	}
	private StoreInquiryResponse prepareFinalResponse(
			ResponseHeader header, Status status)throws BaseServiceException {
		
		StoreInquiryResponse storeInquiryResponse = new StoreInquiryResponse();
		
		storeInquiryResponse.setHeader(header);
		
		StoreInquiry storeInquiry=new StoreInquiry();
		storeInquiryResponse.setStoreInquiry(storeInquiry);
		FilterBy filterBy = new FilterBy();
		storeInquiryResponse.setFilterBy(filterBy);
		storeInquiryResponse.setStatus(status);
	
		return storeInquiryResponse;
	}//prepareFinalResponse
	
	private StoreInquiryResponse prepareFinalResponse(
			ResponseHeader header, Status status,
			FilterBy filtersBy, StoreInquiry storeInquiry,StoreInfo storeInfo,Filter filter,String inquiryType,StoreSortedBy sortBy)throws BaseServiceException {
		
		StoreInquiryResponse storeInquiryResponse = new StoreInquiryResponse();
		storeInquiryResponse.setHeader(header);
		storeInquiryResponse.setInquiryType(inquiryType);
		storeInquiryResponse.setSortBy(sortBy);
		storeInquiryResponse.setFilterBy(filtersBy);	
		storeInquiryResponse.setStatus(status);
		storeInquiryResponse.setStoreInquiry(storeInquiry);
		//if(storeInfo!=null){
			storeInquiryResponse.setStoreInfo(storeInfo);
	//	}
		if(inquiryType.equalsIgnoreCase("filters")){
			storeInquiryResponse.setFilter(filter);
		}
		
		return storeInquiryResponse;
	}
	/*public static void main(String args[]){
		
		
	//	StoreInquiry storeInquiry=storeInquiryRequest.getStoreInquiry();
		StoreInquiry storeInquiry=new StoreInquiry();
		String country="";
		String city="";
		String locality="";
		String storeId="";
		String storeName="";
		String zipcode="";
		String brand="";
		String subId="";
		String subName="";
		List<OrganizationStores> orgStoresList=new ArrayList<OrganizationStores>();
		
		Set<String> countrySet = new HashSet<String>();
		Set<String> citySet = new HashSet<String>();
		Set<String> localitySet = new HashSet<String>();
		List<Country> countryResponseList=null;
		List<City> cityResponseList=null;
		List<Locality> localityResponseList=null;
		List<Stores> storeResponseList=null;
		List<OrganizationStores> orgStoresList=new ArrayList<OrganizationStores>();
	    for (OrganizationStores organizationStores : orgStoresList) {
	    	countrySet.add(organizationStores.getCountry());
		}
	    List<OrganizationStores> countryList=null;
	    for (String countryName : countrySet) {
	    	countryList=new ArrayList<OrganizationStores>();
	    	for(OrganizationStores organizationStores: orgStoresList){
	    		countryList.add(organizationStores);
	    		if(countryName.equalsIgnoreCase(organizationStores.getCountry())){
	    		}
	    	}			
	    	for (OrganizationStores organizationStores : countryList) {
	    	citySet.add(organizationStores.getCity());		
	    	}
	    	List<OrganizationStores> cityList=null;
	    	for(String cityName : citySet){
	    		cityList=new ArrayList<>();
	    	for(OrganizationStores organizationStores : countryList){
	    		cityList.add(organizationStores);
	    		if(cityName.equalsIgnoreCase(organizationStores.getCity())){
	    		}
	    	}
	    
	    	for (OrganizationStores organizationStores : cityList) {
	    		localitySet.add(organizationStores.getCity());		
	    	}
	    	List<OrganizationStores> localityList=null;
	    	for(String localityName : localitySet){
	    		localityList=new ArrayList<>();
	    	for(OrganizationStores organizationStores : cityList){
	    		localityList.add(organizationStores);
	    		if(localityName.equalsIgnoreCase(organizationStores.getLocality())){
	    		}
	    	}
	    	//for(OrganizationStores orgStores:localityList){
	    		Stores stores=new Stores();
	    		stores.setBrand("");
	    		stores.setCity("");
	    		stores.setCountry("");
	    		stores.setBrandImagePath("");
	    		stores.setDescription("");
	    		stores.setEmailId("");
	    		stores.setLatLng("");
	    		stores.setLocality("");
	    		stores.setMobileNo("");
	    		stores.setOffers("");
	    		stores.setState("");
	    		stores.setStoreId("");
	    		stores.setStoreImagePath("");
	    		stores.setStoreManager("");
	    		stores.setStoreName("");
	    		stores.setSubsidiaryId("");
	    		stores.setSubsidiaryName("");
	    		stores.setZipCode("");
	    		storeResponseList=new ArrayList<Stores>();
	    		storeResponseList.add(stores);
	    //	}
	    	Locality localityResponse=new Locality();
	    	localityResponse.setStores(storeResponseList);
	    	localityResponseList=new ArrayList<Locality>();
	    	localityResponseList.add(localityResponse);
	    	}
	    	City cityResponse=new City();
	    	cityResponse.setLocality(localityResponseList);
	    	cityResponseList=new ArrayList<City>();
	    	cityResponseList.add(cityResponse);
	    }
	    Country countryResponse=new Country();
	    countryResponse.setCity(cityResponseList);
	    countryResponseList=new ArrayList<Country>();
	    countryResponseList.add(countryResponse);
	    }
	    

		
	    
	    StoreInfo storeInfo=new StoreInfo();
	    storeInfo.setCountry(countryResponseList);
	    
	    StoreInquiryResponse storeInquiryResponse = new StoreInquiryResponse();
	//	storeInquiryResponse.setHeader("");
	//	storeInquiryResponse.setFilterBy("");	
	//	storeInquiryResponse.setStatus(status);
	//	storeInquiryResponse.setStoreInquiry(storeInquiry);
		storeInquiryResponse.setStoreInfo(storeInfo);
		Gson gson=new Gson();
		String jsonValue=gson.toJson(storeInquiryResponse);
		System.out.println("JSONN---"+jsonValue);
	
	}*/
	
	public static void main(String[] args) throws JsonProcessingException{
			
		 StoreInquiryResponse res = new StoreInquiryResponse(); 
		  	ResponseHeader header=new ResponseHeader();
		  	header.setRequestDate("");
		  	header.setRequestId("");
		  	header.setTransactionDate("");
		  	header.setTransactionId("");
		  	res.setHeader(header);
		   List<Stores> storelist = new ArrayList<Stores>();
		   List<Locality> localityList=new ArrayList<Locality>();
		   List<City> cityList=new ArrayList<City>();
		
		   List<Country> countryList= new ArrayList<Country>();
		   
		   Stores stores=new Stores();
			stores.setCountry("");
			stores.setState("");
			stores.setCity("");
			stores.setLocality("");
			stores.setZipCode("");
			stores.setStoreId("");
			stores.setStoreName("");
			stores.setStoreManager("");
			stores.setSubsidiaryId("");
			stores.setSubsidiaryName("");
			stores.setBrand("");
			stores.setLongitude("");
    		stores.setLocality("");
			stores.setEmailId("");
			stores.setMobileNo("");
			stores.setStoreImagePath("");
			stores.setBrandImagePath("");
			stores.setDescription("");
		   storelist.add(stores);
			
			Locality localty =new Locality();
			localty.setLocalityName("");
			localty.setStores(storelist);
			localityList.add(localty);
			
			City city=new City();
			city.setCityName("");
			city.setLocality(localityList);
			cityList.add(city);
			
	
			Country country=new Country();
			country.setCity(cityList);
			country.setCountryName("");
			countryList.add(country);
			
			
			StoreInfo storeInfo = new StoreInfo();
			storeInfo.setCountry(countryList);
	    	res.setStoreInfo(storeInfo);
	    	
	    	 FilterBy fBy=new FilterBy();
		       fBy.setBrand("");
		       fBy.setSubsidiaryId("");
		       fBy.setSubsidiaryName("");
		       
		    res.setFilterBy(fBy);
		    
		   
		 		   
		 	Locality localityInfo=new Locality();
		 	localityInfo.setLocalityName("");

		    
		    
		    	
	    	
	    	Gson gson = new Gson();
	    	String retJson = gson.toJson(res);//(json, classOfT)
	    	System.out.println(retJson);
		           
		        
		    } 
		    

	
	
	
	
}

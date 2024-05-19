package org.mq.optculture.restservice.ocsurveywebhook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.FormMapping;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.FormMappingDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.business.updatecontacts.UpdateContactsBusinessService;
import org.mq.optculture.model.BaseRequestObject;
//import org.mq.optculture.model.ocBesideSurvey.BesideSurveyJsonPojo;
//import org.mq.optculture.model.ocBesideSurvey.Field;
import org.mq.optculture.model.ocloyalty.LoyaltyEnrollRequest;
import org.mq.optculture.model.updatecontacts.ContactRequest;
import org.mq.optculture.model.updatecontacts.ContactResponse;
import org.mq.optculture.model.updatecontacts.Customer;
import org.mq.optculture.model.updatecontacts.Header;
import org.mq.optculture.model.updatecontacts.User;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class OCSurveyService extends Thread {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	String jsonVal;
	FormMapping formMapping1;

	public OCSurveyService(){
		logger.info("before getting Besideeeeeeeeee TypeForm.......constructure");
	}
	public OCSurveyService(String jsonVal,	FormMapping formMapping1)
{
		this.jsonVal = jsonVal;
		this.formMapping1 = formMapping1;
	}

@Override
public void run() {
	// TODO Auto-generated method stub
	super.run();
	
	oCSurveyJSONFormMapping(jsonVal, formMapping1);
	
	
}
	public void oCSurveyJSONFormMapping(String jsonString , FormMapping formMapping){

		try{

			Header header = new Header();
			Customer customer = new Customer();

			//String besideSurveyrequestJson = OptCultureUtils.getParameterJsonValue(request);
			String ocSurveyrequestJson = jsonString;
			//FormMapping formMapping = forMapping ;
			ServiceLocator locator = ServiceLocator.getInstance();


			logger.info("before getting OCcccccccccccc TyprForm......."+ocSurveyrequestJson);

			JSONObject jsonMainObj = null;
			jsonMainObj = (JSONObject)JSONValue.parse(ocSurveyrequestJson); 

			//logger.info("Afetr getting Besideeeeeeeeee TyprForm(form_response)......."+jsonMainObj.get("form_response")+"");

			JSONObject jsonFormRespMainObj = (JSONObject)jsonMainObj.get("form_response");
			//logger.info("Afetr getting Besideeeeeeeeee TyprForm(answers)......."+jsonFormRespMainObj.get("answers")+"");

			JSONArray jsonAnswersArrayObj = (JSONArray)jsonFormRespMainObj.get("answers");

			String inputMappingStr = formMapping.getInputFieldMapping();

			HashMap<String, List> inputHM = prepareInputMap(inputMappingStr);

			Set<String> fieldKeySet = inputHM.keySet();

			//event_id : "event_idValue";
			String event_idValue = (String)jsonMainObj.get("event_id");
			header.setRequestId(event_idValue);
			//header.setContactSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
			header.setContactSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM);


			String submitted_atValue = (String)jsonFormRespMainObj.get("submitted_at");//TypeForm Date Format 2016-03-23T13:37:00Z
			submitted_atValue = submitted_atValue.replace("T"," ");
			submitted_atValue = submitted_atValue.replace("Z","");
			//ocdate format "2014-12-19 12:12:12"
			header.setRequestDate(submitted_atValue);

			customer.setCreationDate(submitted_atValue);

			String OCAttribute ="";

			for (String fieldKey : fieldKeySet) {
				//<id>+<type>_:_<OCattribute>
				List tempList = inputHM.get(fieldKey);

				OCAttribute = tempList.get(0).toString();

				for(int i=0 ; i<jsonAnswersArrayObj.size(); i++){
					//logger.info("For loop "+ i +"  " +jsonAnswersArrayObj.get(i)+"");
					String[] fieldMapArr = null;
					try{
						fieldMapArr = fieldKey.split("\\+");
					}catch (Exception e) {


						// TODO: handle exception
						//logger.info("fieldMapArr"+e);
					}
					JSONObject eachAnsObject = (JSONObject)jsonAnswersArrayObj.get(i);

					String id = (String)((JSONObject)eachAnsObject.get("field")).get("id");
					//switch is better

					//logger.info("fieldMapArr......"+fieldMapArr[0]);
					//logger.info("id_value........."+id);
					if(fieldMapArr[0].equals(id)){
						String type = (String)eachAnsObject.get("type");
						//logger.info("For  type..."+type);
						String typeVal = null;
						JSONObject typeJSONObj = null;
						Object typeobj = (Object)eachAnsObject.get(""+type+"");
						//logger.info("For  typeobj..."+typeobj+"");
						if(typeobj instanceof String || typeobj instanceof Boolean || typeobj instanceof Long) {
							typeVal = typeobj.toString();
							//logger.info("For  typeobj..."+typeobj+"");
							//logger.info("For  typeVal..."+typeVal+"");
						}

						else if(typeobj instanceof JSONObject) {
							typeJSONObj = (JSONObject)typeobj;
							//logger.info("For  typeJSONObj..."+typeJSONObj+"");
							try{
								if(typeJSONObj.containsKey("label")) {
									typeVal = (String)typeJSONObj.get("label");
									//logger.info("For lableeee typeVal..."+typeVal+""); 
								}}catch (Exception e) {

									logger.info("typeJSONObj.containsKey(labellllll)"+e);
									// TODO: handle exception
								}
							try{
								if(typeJSONObj.containsKey("labels")) {
									JSONArray jsonlabelsArrayObj = (JSONArray)typeJSONObj.get("labels");
									for(int j=0 ; j<jsonlabelsArrayObj.size(); j++){
										String lablesVal = (String)jsonlabelsArrayObj.get(j);
										//typeVal = (String)typeJSONObj.get("labels");
										if(typeVal==null) typeVal = lablesVal;
										else typeVal += lablesVal;
										if(j < jsonlabelsArrayObj.size()-1) typeVal=typeVal.concat(", ");
									}
									//logger.info("For lablesss typeVal..."+typeVal+""); 
								}}catch (Exception e) {

									logger.info("typeJSONObj.containsKey(labelsssss)",e);
									// TODO: handle exception
								}
						}
						try{
							//logger.info("TypeVal..."+typeVal+""); 

							//logger.info("OCAttribute...."+OCAttribute+"");
							if(OCAttribute.equals("Email") || OCAttribute.equals("EmailId")  ){
								customer.setEmailAddress(typeVal);
								logger.info("For  typeval for Email"+typeVal);
							}   	
							else if(OCAttribute.equals("Phone") || OCAttribute.equals("MobilePhone")){
								typeVal=typeVal.replaceAll("[- _ +]+", "");
						        boolean isnumberornot = typeVal.matches("-?\\d+(\\.\\d+)?");
								if(isnumberornot){
								 customer.setPhone(typeVal);
								 logger.info("For  typeval for Phone if number"+typeVal);
								}else{
									customer.setPhone("");
								logger.info("For  typeval for Phone if notnumber empty not null"+typeVal);
								}
							}
							else if(OCAttribute.equals("FirstName")){
								customer.setFirstName(typeVal);
								logger.info("For  typeval for FirstName"+typeVal);
							}
							else if(OCAttribute.equals("LastName")){
								customer.setLastName(typeVal);
								logger.info("For  typeval for setLastName"+typeVal);
							}
							else if(OCAttribute.equals("BirthDay")){  
								
								//"dd/mm" --> "yyyy-mm-dd 00:00:00" 
								//String typevalreg=typeVal+"/0004";
								//typevalreg.matches(^(0[1-9]|[12][0-9]|3[01])[\/](0[1-9]|1[012])[\/]\d{4}$);
								//[0-9]{1,2}(/|-)[0-9]{1,2}(/|-)[0-9]{4}
								
								String typeValafterappend = typeVal+"/1904 00:00:00";
								
								DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
								Date date = inputFormat.parse(typeValafterappend);

								// Format date into output format
								DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String finialtypeVal = outputFormat.format(date);
								customer.setBirthday(finialtypeVal);//yyyy-MM-dd HH:mm:ss
								logger.info("For  typeval for BirthDay"+finialtypeVal);

							}
							else if(OCAttribute.equals("Gender")){
								customer.setGender(typeVal);      					
								logger.info("For  typeval for Gender"+typeVal);

							} 
							//For Beside client the Country is mapped to UDF6
			/*				else if(OCAttribute.equals("Country")){
								customer.setCountry(typeVal);
								logger.info("For  typeval for Country"+typeVal);
							}																
*/						
							else if(OCAttribute.equals("UDF6")){
								customer.setUDF6(typeVal);
								logger.info("For  typeval for Country(UDF6)"+typeVal);

							}
							else if(OCAttribute.equals("UDF11")){
								customer.setUDF11(typeVal);
								logger.info("For  typeval for UDF11"+typeVal);

							}
							else if(OCAttribute.equals("UDF12")){
								customer.setUDF12(typeVal);
								logger.info("For  typeval for UDF12"+typeVal);

							}
							else if(OCAttribute.equals("UDF13")){
								customer.setUDF13(typeVal);
								logger.info("For  typeval for UDF13"+typeVal);

							}
							else if(OCAttribute.equals("UDF14")){
								customer.setUDF14(typeVal);
								logger.info("For  typeval for UDF14"+typeVal);

							}
							else if(OCAttribute.equals("UDF15")){
								customer.setUDF15(typeVal);
								logger.info("For  typeval for UDF15"+typeVal);

							}
							

						}catch (Exception e) {
							// TODO: handle exception

							logger.info("setting customer atrributes....."+e);
						}

					}
				}


			}

			ContactRequest contactRequest = prepareCreateOrUpdateContactRequest(customer,header,formMapping);
			
			Gson gson = new Gson();
			String finialjsonbeforegivingtoUpdate = gson.toJson(contactRequest);
			
			logger.info("final json to from ocsurvey to updateContactsBusinessService.processUpdateContactRequest..:"+finialjsonbeforegivingtoUpdate);
			
			UpdateContactsBusinessService updateContactsBusinessService=(UpdateContactsBusinessService) ServiceLocator.getInstance().getServiceByName(OCConstants.UPDATE_CONTACTS_BUSINESS_SERVICE);

			ContactResponse baseService = (ContactResponse)updateContactsBusinessService.processUpdateContactRequest(contactRequest,true);
  
			String finialjson = gson.toJson(baseService);
			logger.info("final json for ocsurvey from updateContactsBusinessService.processUpdateContactRequest..:"+finialjson);
			
			//System.out.println("Is contact created or not status"+baseService.getStatus()+"");

		}
		catch (Exception e) {

			logger.info("OCSurveyJSONFormMapping.....",e);
			// TODO: handle exception
		}


	}

	public ContactRequest prepareCreateOrUpdateContactRequest(Customer customerFieldValues,Header headerFieldValues,FormMapping formMapping){

		ContactRequest contactRequest = new ContactRequest();
		Header header = new Header();
		header = headerFieldValues;
		Customer customer = new Customer();
		customer = customerFieldValues;
		//TODO customer.setCreationDate()-----> clarification we should provide the time which he is given "submitted_at"?
		User user = new User();

		/*private String requestId;
		 private String requestDate;
		 private String contactSource;
		 private String contactList;*/
		//TODO Clarification can we use eventid from typeform json as requestid ?// venkat :06/04/2017 

		//header.setRequestId(System.currentTimeMillis()+"");
		//header.setContactSource(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM+Constants.DELIMETER_COLON+formMapping.getFormMappingName());
		//header.setRequestDate(MyCalendar.string2Calendar(Calendar.getInstance().toString()).toString());//Time when contact is created //Mycalender
		//TODO clarification can we provide the list name as "Survey"?
		
		
		header.setContactList(OCConstants.SURVEY_LIST_NAME);
		//header.setContactList("Mahesh");
		
		user.setUserName(Utility.getOnlyUserName(formMapping.getUsers().getUserName()));//utility
		user.setOrganizationId(formMapping.getUsers().getUserOrganization().getOrgExternalId());//VVVVVV question ?
		user.setToken(formMapping.getUsers().getToken());	 

		customer.setAddressLine1("");
		customer.setAddressLine2("");
		customer.setAnniversary("");
		//customer.setBirthday("");// VVVVV question?
		customer.setCity("");
		//customer.setCreationDate("");//VVVVV question?
		customer.setCustomerId("");//VVVVV question?
		//customer.setGender("");//VVVVV question?
		customer.setHomeStore("");
		//customer.setLastName("");
		customer.setCountry("");
		customer.setPostal("");
		customer.setState("");
		customer.setUDF2("");
		customer.setUDF3("");
		customer.setUDF4("");
		customer.setUDF5("");;
		//customer.setUDF6("");
		customer.setUDF7("");
		customer.setUDF8("");
		customer.setUDF9("");
		customer.setUDF10("");
		//customer.setUDF11("");
		//customer.setUDF12("");
		//customer.setUDF13("");
		//customer.setUDF14("");
		//customer.setUDF15("");

		contactRequest.setHeader(header);
		contactRequest.setCustomer(customer);
		contactRequest.setUser(user);

		/*private User user;
		private String userName;
		private String organizationId;
		private String token;

		private String customerId;
		private String phone;
		private String emailAddress;
		private String firstName;
		private String lastName;
		private String city;
		private String state;
		private String postal;
		private String country;
		private String homeStore;
		private String gender;
		private String creationDate;
		private String birthday;
		private String anniversary;
		private String addressLine1;
		private String addressLine2;
		private String UDF1;
		private String UDF2;
		private String UDF3;
		private String UDF4;
		private String UDF5;
		private String UDF6;
		private String UDF7;
		private String UDF8;
		private String UDF9;
		private String UDF10;
		private String UDF11;
		private String UDF12;
		private String UDF13;
		private String UDF14;
		private String UDF15;*/


		return contactRequest;

	}//prepareCreateOrUpdateContactRequest


	public HashMap<String, List> prepareInputMap(String formMapStr){ 
		logger.debug("---------Entered prepareInputMap-----------");
		HashMap<String, List> inputMapSettingHM = null;
		if (formMapStr != null) {
			String[] tagMapStrArr = formMapStr.split("\\|");
			String[] tempStr = null;
			if (tagMapStrArr.length > 0) {
				inputMapSettingHM = new HashMap<String, List>();
				List list = null;
				for (int i = 0; i < tagMapStrArr.length; i++) {
					list = new ArrayList();
					tempStr = tagMapStrArr[i].trim().split("_:_");
					if (tempStr.length == 2) {
						list.add(tempStr[1]);
					} else if (tempStr.length == 3) {
						list.add(tempStr[1]);
						list.add(tempStr[2]);
					}
					inputMapSettingHM.put(tempStr[0], list);
				}
			}
		} 
		else {
			logger.error("Exception : Error occured while getting Input Field Mapping **");
		}
		logger.debug("---------Exiting prepareInputMap-----------");
		return inputMapSettingHM;
	}//prepareInputMap



}

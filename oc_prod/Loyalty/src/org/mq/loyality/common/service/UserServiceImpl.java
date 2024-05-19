package org.mq.loyality.common.service;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.mq.loyality.common.dao.ContactsDao;
import org.mq.loyality.common.dao.IUserDao;
import org.mq.loyality.common.dao.RetailProSalesDao;
import org.mq.loyality.common.dao.UserOrganizationDao;
import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.common.hbmbean.LoginDetails;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.hbmbean.Users;
import org.mq.loyality.utils.ActivityForm;
import org.mq.loyality.utils.Constants;
import org.mq.loyality.utils.MyCalendar;
import org.mq.loyality.utils.OCConstants;
import org.mq.loyality.utils.TransDetails;
import org.mq.loyality.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private IUserDao userDAO;
	@Autowired
	private UserOrganizationDao userOrganizationDao;
	@Autowired
	private ContactsDao contactDao;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	@Override
	@Transactional
	public LoyaltySettings getSettingDetails(String url) {
		// List<LoyaltySettings> list=userDAO.getSettingDetails(url);
		logger.info("url in userservice is==asasa====>"+url);
		logger.info("url in userservice is==asasa==sasaasas==>"+userDAO.getSettingDetails(url));
		
		
		if (userDAO.getSettingDetails(url).size() != 0) {
			return userDAO.getSettingDetails(url).get(0);
		} else {
			LoyaltySettings l = new LoyaltySettings();
			/* l.setColorCode("#c90000"); */
			// l.setUserId(userId);
			return l;
		}
	}

	@Override
	@Transactional
	public List<UserOrganization> getOrgDetails(Long id) {
		return userOrganizationDao.getOrgDetails(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Contacts findById(Long cid) {
		// TODO Auto-generated method stub
		return contactDao.getContacts(cid);
	}

	@Override
	@Transactional
	public void saveProfile(Contacts data) {
		contactDao.saveOrUpdate(data);
	}
	@Override
	@Transactional
	public List<TransDetails> getPageTransDetails(StringBuffer sb, int page, int pageSize){
		ArrayList<Object[]> list = contactDao.getTotTransDetails(sb, page, pageSize);
		return getTransDetailsObjs(list);
	}
	
	
	
	@Override
	@Transactional
	public List<TransDetails> getTotTransDetails(StringBuffer sb)
			throws ParseException {
		
		ArrayList<Object[]> list = contactDao.getTotTransDetails(sb);
		return getTransDetailsObjs(list);
	}

	@Override
	public Users findByUserId(Long userId) {
		// TODO Auto-generated method stub
		return userDAO.findByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityForm> getRecieptDetails(String id,Long orgId, String storeName, String docSid, Long userId) {
		ArrayList<Object[]> list = contactDao.getRecieptDetails(id , docSid,orgId, storeName,userId);
		List<ActivityForm> beansList = new ArrayList<ActivityForm>();
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		double extAmntl;
		for (Object[] row : list) {
			ActivityForm a = new ActivityForm();
			if (row[0] != null) {
				a.setPrice(Double.parseDouble(row[0].toString()));
				a.setPriceString(decimalFormat.format(Double.parseDouble(row[0]
						.toString())));
			}
			a.setRecieptNo(id);
			if(row[2]!=null && storeName != null && !storeName.trim().equals("")){
				a.setStoreName(row[2].toString());
			}else{
				a.setStoreName("");
			}
			if (row[3] != null && storeName != null && !storeName.trim().equals("")) {
			a.setEmail(row[3].toString());
			}else{
				a.setEmail("");
			}
			if (row[4] != null) {
				a.setItemSid((row[4].toString()));
			}
			if (row[5] != null) {
				a.setTax(Double.parseDouble(row[5].toString()));
			}
			if (row[6] != null) {
				a.setDCS(row[6].toString());
			}
			if (row[7] != null) {
				a.setType(row[7].toString());
			}
			
			if (row[8] != null && storeName != null && !storeName.trim().equals("")) {
				a.setAdd1(row[8].toString());
				StringTokenizer stringTokenizer = new StringTokenizer(
						row[8].toString(), ";=;");
				while (stringTokenizer.hasMoreTokens()) {
					try{
					a.setAdd1(stringTokenizer.nextElement().toString());
					a.setAdd2(stringTokenizer.nextElement().toString());
					a.setAdd3(stringTokenizer.nextElement().toString());
					a.setZip(stringTokenizer.nextElement().toString());
					a.setPhoneNo(stringTokenizer.nextElement().toString());
					}catch(Exception e){
						logger.info("store don't have phone number");
					}
				}
			}
			if (row[9] != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				try {
					date = sdf.parse(row[9].toString());
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					a.setDate((MyCalendar.calendarToString(cal,
							MyCalendar.FORMAT_DATEONLY_GENERAL, null)));
				} catch (ParseException e) {
					logger.info("Parse Exception ::: ",e);
				}
			}
				if (row[10] != null) {
					a.setQuantity(row[10].toString());
					Double d = (((Double.parseDouble(row[10].toString()))));
					a.setQuantityLong(d.longValue());
				}
				if (row[0] != null && row[10] != null) {
					extAmntl = Double.parseDouble(row[0].toString())
							* Double.parseDouble(row[10].toString());
					a.setExtPriceString(decimalFormat.format(extAmntl));
					a.setExtPrice(extAmntl);
				}
				if (row[11] != null) {
					a.setItemSid(row[11].toString());
				}
				if (row[12] != null) {
					a.setDCS(row[12].toString());
				}
			
			beansList.add(a);
		}
		return beansList;
	}

	@Override
	@Transactional
	public void savePassword(ContactsLoyalty contactLoyality) {
		contactDao.saveOrUpdate(contactLoyality);

	}

	@Override
	@Transactional
	public List<ContactsLoyalty> getUser(String userName,LoyaltySettings settings) {
		// TODO Auto-generated method stub
		return userDAO.getContactList(userName,settings);
	}

	@Override
	@Transactional
	public void saveOrUpdate(EmailQueue queue) {
		// TODO Auto-generated method stub
		userDAO.saveOrUpdate(queue);
	}

	@Override
	@Transactional
	public List<ContactsLoyalty> findLoyaltyListByContactId(Long contactId) {
		// TODO Auto-generated method stub
		return userDAO.findLoyaltyListByContactId(contactId);
	}

	@Override
	@Transactional
	public void saveOrUpdate(ContactsLoyalty obj) {
		userDAO.saveOrUpdate(obj);

	}

	@Override
	public Users getLoyaltyByUserId(Long userId) {
		// TODO Auto-generated method stub
		return userDAO.getLoyaltyByUserId(userId);
	}

	@Override
	public String getPropertyValueFromDB(String prop_key) {
		// TODO Auto-generated method stub
		return userDAO.getPropertyValueFromDB(prop_key);
	}

	@Override
	public Character isValidEmail(String cardId) {
		// TODO Auto-generated method stub
		return userDAO.isvalidEmail(cardId);
	}

	@Override
	public List<ContactsLoyalty> getContactWithEmail(String cardId) {
		// TODO Auto-generated method stub
		 List<Object[]> list=userDAO.getContactWithEmail(cardId);
		 List<ContactsLoyalty> cList=new ArrayList<ContactsLoyalty>();
		 for (Object[] row : list) {
			 ContactsLoyalty t = new ContactsLoyalty();
			 if(row[0]!=null){
			 t.setCardNumber(row[1].toString());
			 if(row[1]!=null)
			 {
				 t.setMembershipPwd(row[1].toString());
			 }
			 }
			 cList.add(t);
		
	}
		return cList;
	}

	@Override
	public void saveOrUpdate(LoginDetails loginDet) {
		userDAO.saveOrUpdate(loginDet);
		
	}
	
	private List<TransDetails> getTransDetailsObjs(ArrayList<Object[]> list){
		String enterAmountType = "";
		String transactionType = "";
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		List<TransDetails> beansList = new ArrayList<TransDetails>();
		if(list!=null){
		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat outputFormat = new SimpleDateFormat(MyCalendar.FORMAT_DATETIME_STDATE);
		
		for (Object[] row : list) {
			TransDetails t = new TransDetails();
			
			if(row[0]!=null){
				t.setRecieptNumber(Long.parseLong(row[0].toString()));
				/*try {
					String receiptNumber = "";
					receiptNumber = retailProSalesDao.findReceiptNumberByDocsid(row[1].toString(), Long.parseLong(row[16].toString()));
					t.setRecieptNumber(receiptNumber);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
			}
			if(row[1]!=null){
				t.setRecieptAmount(decimalFormat.format(Double.parseDouble(row[1].toString())));
			}
			if(row[2]!=null){
				t.setStoreName((row[2].toString()));
				/*String store;
				String storeName = retailProSalesDao.findStoreNameByStoreNumber((row[2].toString()), Long.parseLong((row[17].toString())));
				if(storeName != null){
					store = storeName;
				}
				else{
					store = "Store Id "+row[2].toString();
				}
				t.setStoreName(store);*/
			}
			if (row[3].toString() != null) {
				try {
					t.setRecieptDate(outputFormat.format(inputFormat.parse(row[3].toString())));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.info(" Exception :: ",e);
				}
			} else {
				t.setRecieptDate("--");
			}
			if(row[5] != null){
				t.setDocSid(row[5].toString());
			}
			
			if(row[10]!=null && row[7]!=null){
				t.setTransactionType(row[10].toString());
				transactionType = row[10].toString();
			enterAmountType = row[7].toString();
			t.setTransactionType(enterAmountType);
			if ( transactionType.equals("Redemption") ) {
				Double d = null;
				if(row[8] !=null && !row[8].toString().isEmpty() && row[9] !=null && !row[9].toString().isEmpty() )
				{
					d = Double.parseDouble(row[8].toString()) + Double.parseDouble(row[9].toString());
				}else if(row[8] !=null && !row[8].toString().isEmpty()){
					d = Double.parseDouble(row[8].toString());
				}else if(row[9] !=null && !row[9].toString().isEmpty()){
					d = Double.parseDouble(row[9].toString());
				}
				
				String redeemBal = Constants.STRING_NILL;
				 if(row[13] !=null && !row[13].toString().isEmpty()){
				redeemBal = row[13].toString().split("\\.")[0]+" Points";
				 }
				 if(!redeemBal.isEmpty() && d !=null)redeemBal = " & "+redeemBal;
				 //redeemBal = (d != null ? "- "+decimalFormat.format(-d) : "")+redeemBal;
				 redeemBal = (d != null ? "- "+decimalFormat.format(-d) : "")+redeemBal + " Currency";
				t.setLoyalityBal(redeemBal);
			}
			/*if (enterAmountType.equals("AmountRedeem")
					&& transactionType.equals("Redemption") && row[8] == null) {
				
				if (row[9] != null) {
					t.setLoyalityBal(decimalFormat.format(Double.parseDouble(row[9].toString())));
				}
			}
			if (enterAmountType.equals("AmountRedeem")
					&& transactionType.equals("Redemption") && row[9] != null) {

				if (row[8]!=null && row[8].toString().isEmpty()) {
					t.setLoyalityBal(decimalFormat.format(Double.parseDouble(row[8].toString())));
				}
			}

			if (enterAmountType.equals("'AmountRedeem'")
					&& transactionType.equals("Redemption")
					&& ((row[9] == null))) {
				if (row[8] != null) {
					t.setLoyalityBal(decimalFormat.format(Double.parseDouble(row[8].toString())));
				}

			}
			if (enterAmountType.equals("'AmountRedeem'")
					&& transactionType.equals("Redemption")
					&& ((row[8] != null))) {
				if (row[8].toString().equals("") && row[9]!=null ) {
					t.setLoyalityBal(decimalFormat.format(Double.parseDouble(row[9].toString())));
				}
			}*/
			/*if (enterAmountType.equals("PointsRedeem")
					&& transactionType.equals("Redemption") && row[14] != null && !row[14].toString().isEmpty() ) {
				t.setLoyalityBal(("- "+row[14].toString().split("\\.")[0]+" Points"));
			}*/
			if (transactionType.equals("Issuance")
		
					&& enterAmountType.equals("Purchase") && row[11] != null) {
				t.setLoyalityBal("+"+row[11].toString().split("\\.")[0]+" Points");
			}
			if (transactionType.equals("Issuance")
					&& enterAmountType.equals("Purchase") && row[12] != null) {
				//t.setLoyalityBal("+ "+decimalFormat.format(Double.parseDouble(row[12].toString())));
				t.setLoyalityBal("+ "+decimalFormat.format(Double.parseDouble(row[12].toString())) +" Currency");
			}
			if (transactionType.equals("Issuance")
					&& enterAmountType.equals("Gift") && row[12] != null) {
				//t.setLoyalityBal("+ "+decimalFormat.format(Double.parseDouble(row[12].toString())));
				t.setLoyalityBal("+ "+decimalFormat.format(Double.parseDouble(row[12].toString())) +" Currency");
			}
			
			if(enterAmountType.equals("Add")){
				if(row[15].toString().equals("Points")){
					t.setLoyalityBal(row[13] != null ? "+ "+row[13].toString().split("\\.")[0]+" Points" : "");
				}
				else{
					t.setLoyalityBal("+ "+decimalFormat.format(Double.parseDouble(row[8].toString()))+" Currency");
				}
			}else if(enterAmountType.equals("Sub")){
				if(row[15].toString().equals("Points")){
					t.setLoyalityBal(row[13] != null ? row[13].toString().split("\\.")[0]+" Points" : "");
				}else{
					t.setLoyalityBal(""+decimalFormat.format(Double.parseDouble(row[8].toString()))+" Currency");
				}
			}

			beansList .add(t);
			}
		}
		}
		return beansList;
	}
	
}



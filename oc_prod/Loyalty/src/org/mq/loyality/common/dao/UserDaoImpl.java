package org.mq.loyality.common.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.mq.loyality.common.hbmbean.Contacts;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.mq.loyality.common.hbmbean.EmailQueue;
import org.mq.loyality.common.hbmbean.LoginDetails;
import org.mq.loyality.common.hbmbean.LoyaltySettings;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.hbmbean.Users;
import org.mq.loyality.common.service.UserService;
import org.mq.loyality.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class UserDaoImpl implements IUserDao {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private SessionFactory sessionFactoryForDML;
	@Autowired
	private UserService userService;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	@Override
	@Transactional
	public List<ContactsLoyalty> getUser(String username,Long orgno,String password) {
		List<ContactsLoyalty> user=null;
		try{
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ContactsLoyalty.class);
		criteria.add(Restrictions.eq("orgId",orgno));
		if(username.length()==10)
		{
		criteria.add(Restrictions.like("mobilePhone", username, MatchMode.ANYWHERE).ignoreCase());
		}
		else if(username.length()!=10 )
		{
			criteria.add(Restrictions.eq("cardNumber",username));
		}
		
		//criteria.add(Restrictions.like("membershipPwd",password));
		
		user = criteria.list();
		}
		catch(Exception e)
		{
			logger.info(" Exception :: ",e);
		}
		
		return user;
		
		}
	@Override
	 @Transactional
	public List<LoyaltySettings> getSettingDetails(String url) {
		// TODO Auto-generated method stub
		List<LoyaltySettings> list = sessionFactory.getCurrentSession().createQuery("from LoyaltySettings where urlStr =  '"+url+"'").list();
	//	query.setParameter("code", "'"+url+"'");
		//List<LoyaltySettings> list = query.list();
		
     	return list;
	}
	@Override
	 @Transactional
	public Users findByUserId(Long userId) {
		// TODO Auto-generated method stub
		//Users u = (Users) sessionFactory.getCurrentSession().get("Users.class",userId);
		List<Users> uList=sessionFactory.getCurrentSession().createQuery("from Users where userId="+userId).list();
		if(uList.size()!=0){
		return uList.get(0);
		}
		return null;
		
	}
	@Override
	@Transactional("txMngrForDML")
	public void saveOrUpdate(EmailQueue queue) {
		
		try {
			sessionFactoryForDML.getCurrentSession().saveOrUpdate(queue);
			//sessionFactoryForDML.getCurrentSession().flush();
			//sessionFactory.getCurrentSession().saveOrUpdate(queue);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in email queue:::", e);
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception in email queue:::", e);
			e.printStackTrace();
		}
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ContactsLoyalty> getContactList(String c,LoyaltySettings settings) {
		
	/*	final String EMAIL_PATTERN = 
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);*/
		Long orgId = settings.getUserOrgId();
		List<UserOrganization> userList = userService
				.getOrgDetails(orgId);
		UserOrganization user = null;
		if (userList.size() != 0) {
		user = userList.get(0);
		}
		
		String mobileRange[] = null;
		int minDigits = user.getMinNumberOfDigits();
		int maxDigits = user.getMaxNumberOfDigits();
		String countryCarrier = settings.getOrgOwner().getCountryCarrier()+"";
		if(minDigits == maxDigits){
			mobileRange = new String[] { minDigits+""};
		}else{
			mobileRange = new String[] {minDigits+"",maxDigits+""};
		}	
		
		boolean isCarrier = false;
		boolean isMobile = false;
		String mobile = Constants.STRING_NILL;
		if(mobileRange.length==1){
			isCarrier = (c.length() == Integer.valueOf(mobileRange[0].trim()) ? false : true );
			isMobile = isCarrier ? c.startsWith(countryCarrier) && c.substring((""+countryCarrier+"").length()).length() == Integer.valueOf(mobileRange[0].trim()) : true;
			mobile = isMobile && c.startsWith(String.valueOf(countryCarrier)) && isCarrier  ? c.substring((""+countryCarrier+"").length()) : c; 
		}else{
			isCarrier = ((c.length() >= Integer.valueOf(mobileRange[0].trim())) && (c.length() <= Integer.valueOf(mobileRange[1].trim()))  ? false : true );
		//	isMobile = isCarrier ? c.startsWith(String.valueOf(countryCarrier)) && c.substring((""+countryCarrier+"").length()).length() <= Integer.valueOf(mobileRange[0].trim()) && c.substring((""+countryCarrier+"").length()).length() >= Integer.valueOf(mobileRange[1].trim()):true;
			isMobile =   isCarrier ? true:true;
			mobile =  c;
		}
		
		//String mobile = isMobile && c.startsWith(String.valueOf(countryCarrier)) && isCarrier  ? c.substring((""+countryCarrier+"").length()) : c; 

		
		
		
		Pattern pattern=Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(c.trim());
		Query query = null;
		if(matcher.matches())
		{
			query=sessionFactory.getCurrentSession().createQuery("from ContactsLoyalty where serviceType ='OC' and rewardFlag in ('GL','L') and contact.emailId=:code and orgId="+orgId);
			query.setParameter("code", c);
		}
		else if(isMobile)
		{
			//query=sessionFactory.getCurrentSession().createQuery("from ContactsLoyalty where serviceType ='OC' and rewardFlag in ('GL','L') and ( mobilePhone =:code OR mobilePhone =:code2 OR cardNumber=:code1) and orgId="+orgId);
			query=sessionFactory.getCurrentSession().createQuery("from ContactsLoyalty where serviceType ='OC' and rewardFlag in ('GL','L') and ( mobilePhone like :code  OR cardNumber=:code1) and orgId="+orgId);
			query.setParameter("code", '%'+mobile);
			//query.setParameter("code2", countryCarrier+mobile);
			query.setParameter("code1", c);
		}
		else //if((!(matcher.matches())) && !(c.length() >= user.getMinNumberOfDigits() && c.length() <=user.getMaxNumberOfDigits()) )
		{
			query=sessionFactory.getCurrentSession().createQuery("from ContactsLoyalty where serviceType ='OC' and rewardFlag in ('GL','L') and cardNumber=:code and orgId="+orgId);
			query.setParameter("code", c);
		}
		
		
		
		
		List<ContactsLoyalty> cList = query.list();
		// TODO Auto-generated method stub
		return cList;
	}
	@Override
	@Transactional("txMngrForDML")
	public void saveOrUpdate(LoginDetails loginDet) {
		//sessionFactory.getCurrentSession().saveOrUpdate(loginDet);
		sessionFactoryForDML.getCurrentSession().saveOrUpdate(loginDet);
		sessionFactoryForDML.getCurrentSession().flush();
	}
	@Override
	@Transactional
	public List<LoginDetails> checkLoginDetails(String login, Long orgId) {
		logger.info("login is :::::::: "+login);
		Query query=sessionFactory.getCurrentSession().createQuery("from LoginDetails where userId=:code and orgId=:org");
		 query.setParameter("code", login);
		 query.setParameter("org", orgId);
		 List<LoginDetails> cList = query.list();
		return cList;
	}
	@Override
	@Transactional
	public List<ContactsLoyalty> findLoyaltyListByContactId(Long contactId) {
		
		Query query=sessionFactory.getCurrentSession().createQuery("from ContactsLoyalty where contact.contactId=:code");
		 query.setParameter("code", contactId);
		 List<ContactsLoyalty> cList = query.list();
		 System.out.println("cLIst is==================>"+cList);
		return cList;
	}
	@Override
	@Transactional("txMngrForDML")
	public void saveOrUpdate(ContactsLoyalty obj) {
		sessionFactoryForDML.getCurrentSession().saveOrUpdate(obj);
		sessionFactoryForDML.getCurrentSession().flush();
		
	}
	@Override
	@Transactional
	public Users getLoyaltyByUserId(Long userId) {
		Users u=(Users) sessionFactory.getCurrentSession().get(Users.class,userId);
		return u;
	}
	
	@Override
	@Transactional
	public String getPropertyValueFromDB(String prop_key) {
		List<String> values=sessionFactory.getCurrentSession().createSQLQuery(" select props_value from application_properties where props_key= '"+prop_key+"'").list();
		if( values !=null && values.size()>0){
		return values.get(0) ;
		}
		return null;
	}
	@Override
	@Transactional
	public List<ContactsLoyalty> getUserMobile(String username, Long orgno) {
		List<ContactsLoyalty> user=null;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ContactsLoyalty.class);
		criteria.add(Restrictions.eq("orgId",orgno));
	    criteria.add(Restrictions.eq("cardNumber",username));
	//	criteria.add(Restrictions.like("membershipPwd",password));
		user = criteria.list();
		
		return user;
	}
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<ContactsLoyalty> getUser(String username, String mobileRangeJava, Long userOrgId, Short countryCarrier) {
		// TODO Auto-generated method stub
		List<ContactsLoyalty> user=null;
		username = username.replaceAll("\\s","");
		String mobileRange[] = mobileRangeJava.split("to");
		boolean isCarrier = false;
		boolean isMobile = false;
		String mobile = Constants.STRING_NILL;
		if(mobileRange.length==1){
			isCarrier = (username.length() == Integer.valueOf(mobileRange[0].trim()) ? false : true );
			isMobile = isCarrier ? username.startsWith(String.valueOf(countryCarrier)) && username.substring((""+countryCarrier+"").length()).length() == Integer.valueOf(mobileRange[0].trim()) : true;
			mobile = isMobile && username.startsWith(String.valueOf(countryCarrier)) && isCarrier ? username.substring((""+countryCarrier+"").length()) : username; 
		}else{//for UAE
			isCarrier = ((username.length() >= Integer.valueOf(mobileRange[0].trim())) && (username.length() <= Integer.valueOf(mobileRange[1].trim()))  ? false : true );
			/* isMobile =   isCarrier ? username.startsWith(String.valueOf(countryCarrier)) && username.substring((""+countryCarrier+"").length()).length() >= Integer.valueOf(mobileRange[0].trim()) && username.substring((""+countryCarrier+"").length()).length() <= Integer.valueOf(mobileRange[1].trim()):true;*/
			isMobile =   isCarrier ? true:true;
			mobile =  username;
		}
		//String mobile = isMobile && username.startsWith(String.valueOf(countryCarrier)) && isCarrier ? username.substring((""+countryCarrier+"").length()) : username;
		try{
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ContactsLoyalty.class);
		criteria.add(Restrictions.eq("orgId",userOrgId));
		/*check with mallika about login */
		/*if(loyaltyType.equalsIgnoreCase("Card Number")){*/
		Criterion cExp = isMobile ? Restrictions.or(Restrictions.like("mobilePhone", mobile),Restrictions.eq("cardNumber",username.replaceAll("\\s",""))):Restrictions.eq("cardNumber",username.replaceAll("\\s",""));
		criteria.add(cExp);
		/*}else{
			
		}*/
		/*if(username.length()==10)
		{
		criteria.add(Restrictions.like("mobilePhone", username));
		criteria.add(Restrictions.like("membershipType", "Mobile"));
		}
		else if(username.length()!=10)
		{
			criteria.add(Restrictions.like("membershipType", "Card"));
			criteria.add(Restrictions.eq("cardNumber",Long.parseLong(username.replaceAll("\\s",""))));
		}*/
		String rf[] = {"GL","L"};
		criteria.add(Restrictions.eq("serviceType","OC"));
		criteria.add(Restrictions.in("rewardFlag", rf));
		user = criteria.list();
		}
		catch(Exception e)
		{
			logger.info(" Exception :: ",e);
		}
		return user;
		}
	
	@Override
	@Transactional
	public Character isvalidEmail(String cardId) {
		
		List<Contacts> conList=sessionFactory.getCurrentSession().createQuery("from Contacts where cardId="+cardId).list();
		if(conList!=null && conList.size()==0)
		{
			return 'N';
		}
		return 'Y';
	}
	@Override
	public ArrayList<Object[]> getContactWithEmail(String cardId) {
		// TODO Auto-generated method stub
		ArrayList<Object[]> conList=(ArrayList<Object[]>) sessionFactory.getCurrentSession().createSQLQuery("select card_number,membership_pwd from contacts_loyalty cl,contacts c  " +
				"where c.email_id='"+cardId+"' and cl.contact_id=c.cid").list();
		return conList;
	}
	
	
	@Override
	@Transactional
	public List<ContactsLoyalty> getUserCard(String login, Long userOrgId) {
		List<ContactsLoyalty> user=null;
		login = login.replaceAll("\\s","");
		try{
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ContactsLoyalty.class);
		criteria.add(Restrictions.eq("orgId",userOrgId));
			criteria.add(Restrictions.eq("cardNumber",login));
		user = criteria.list();
		}
		catch(Exception e)
		{
			logger.info(" Exception :: ",e);
		}
		return user;
		}
	@Override
	@Transactional
	public int updateUsedSMSCount(Long userId, int usedCount) {
		
				String query = " UPDATE users SET used_sms_count = (used_sms_count + "+usedCount+") " +
				" WHERE user_id= "+userId.longValue();
				return		sessionFactoryForDML.getCurrentSession().createSQLQuery(query).executeUpdate();
	}
	}
	 



/**/


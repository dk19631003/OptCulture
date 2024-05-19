package org.mq.captiway.scheduler.utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.CouponCodes;
import org.mq.captiway.scheduler.beans.Coupons;
import org.mq.captiway.scheduler.beans.ReferralProgram;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponCodesDaoForDML;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.CouponsDaoForDML;
import org.mq.captiway.scheduler.dao.ReferralProgramDao;
import org.mq.captiway.scheduler.dao.ReferralcodesIssuedDao;
import org.mq.captiway.scheduler.dao.ReferralcodesIssuedDaoForDML;
import org.mq.captiway.scheduler.beans.ReferralcodesIssued;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.context.ApplicationContext;

public class ReferralcodeProvider {

	
    
    private Hashtable<String, Queue<ReferralcodesIssued>> referralcodesmap = null;

    
    
    private Vector<ReferralcodesIssued> usedReferralcodeVec = new Vector<ReferralcodesIssued>();
	
    public synchronized void addCouponCodeToVec(ReferralcodesIssued cc) {
    	usedReferralcodeVec.add(cc);
    	flushCouponCodesToDB(false);
    } //
	
    private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER); 
	
	
	/*
	 * public class SingletonDemo {
        private static volatile SingletonDemo instance = null;
 
        private SingletonDemo() {       }
 
        public static SingletonDemo getInstance() {
                if (instance == null) {
                        synchronized (SingletonDemo .class){
                                if (instance == null) {
                                        instance = new SingletonDemo ();
                                }
                      }
                }
                return instance;
        }
}

	 */
	private ApplicationContext context;
	private CouponCodesDao couponCodesDao;
	
	private ReferralcodesIssuedDao referralcodesdao;
	private ReferralcodesIssuedDaoForDML referralcodeDaoForDML;
	private ReferralProgramDao refprgramdao;
	private ReferralProgram refprgmobj=null;

	
	//private CouponCodesDaoForDML couponCodesDaoForDML;

	
	
	private Coupons coupon=null;
	private CouponsDao couponsDao;
	private CouponsDaoForDML couponsDaoForDML;
	
	private ReferralcodeProvider(ApplicationContext context) {
		
		//logger.debug("context is ::"+context);
		this.context = context;
	//	couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
	//	couponCodesDaoForDML = (CouponCodesDaoForDML)context.getBean("couponCodesDaoForDML");
		
		referralcodesdao=(ReferralcodesIssuedDao)context.getBean("referralCodesIssuedDao");
		referralcodeDaoForDML = (ReferralcodesIssuedDaoForDML)context.getBean("referralCodesIssuedDaoForDML");
		
		
		
		couponsDao = (CouponsDao)context.getBean("couponsDao");
		refprgramdao=(ReferralProgramDao)context.getBean("referralProgramDao");

		
		couponsDaoForDML = (CouponsDaoForDML)context.getBean("couponsDaoForDML");
		
		if(couponSet == null) {
			
			couponSet = new HashSet<String>();
		}
		
		
	}
	
	private ReferralcodeProvider(ReferralcodesIssuedDao referralCodesIssuedDao) {
		
		
		try {
			if(couponSet == null) {
				
				couponSet = new HashSet<String>();
			}
			ServiceLocator locator = ServiceLocator.getInstance();
			referralcodesdao = (ReferralcodesIssuedDao)locator.getDAOByName("referralCodesIssuedDao");
		
			referralcodeDaoForDML = (ReferralcodesIssuedDaoForDML)locator.getDAOForDMLByName("referralCodesIssuedDaoForDML");
			
			refprgramdao=(ReferralProgramDao)locator.getDAOByName("referralProgramDao");
			
			couponsDao = (CouponsDao)locator.getDAOByName("couponsDao");
			couponsDaoForDML = (CouponsDaoForDML)locator.getDAOForDMLByName("couponsDaoForDML");
		} catch (Exception e) {
			logger.error("Exception ===", e);
		}
		
		
		
		
	}
	
	
	public  Set<String> couponSet = null;
	private static ReferralcodeProvider referralcodeProvider;
	
	 public static synchronized ReferralcodeProvider getReferralcodeProviderInstance(ApplicationContext context, ReferralcodesIssuedDao referralCodesIssuedDao) {
		 
         if (referralcodeProvider == null) {
                 
 			logger.info("entering ReferralcodeProvider instance" );

        	 if(context != null) {
        		 
      						logger.info("entering ReferralcodeProvider instance context" );
      				//	try {
      		
      						referralcodeProvider = new ReferralcodeProvider(context);
      						logger.info(" ReferralcodeProvider instance value is"+referralcodeProvider );

      					//	}catch(Exception e)	{
      						//logger.info("referralcodeProvidercontexif value" +e);
      						//}
      						logger.info("referralcodeProvidercontexif value" +referralcodeProvider);
        	 
         	} else{
         				logger.info("entering ReferralcodeProvider instance context else" );
         				referralcodeProvider = new ReferralcodeProvider(referralCodesIssuedDao);
         				logger.info("entering ReferralcodeProvider instance context else value"+referralcodeProvider );

         			}
        	
	 	}
		logger.info("referralcodeProvider value at return" +referralcodeProvider);

        return referralcodeProvider;
	 }
	 
	 public synchronized void flushCouponCodesToDB(boolean flushFlag) {
		 
		 	logger.info("entering flushCouponCodesToDB");
		 	logger.info("usedReferralcodeVec value"+usedReferralcodeVec);
		 	logger.info("usedReferralcodeVec size"+usedReferralcodeVec.size());
		 	logger.info("flushFlag value"+flushFlag);

	    	if(usedReferralcodeVec==null || usedReferralcodeVec.isEmpty()) return;
	    	

	    	
			if(flushFlag==true || usedReferralcodeVec.size()>0) {
				
			 	logger.info("entering if");

				//couponCodesDao.saveByCollection(usedCouponCodesVec);
				referralcodeDaoForDML.saveByCollection(usedReferralcodeVec);
				usedReferralcodeVec.clear();
			
			} // if
	    }

	 public synchronized String  getNextCouponCode(String tempStr, String campaignName, String campaignType, 
				String issuedTo, Long sentId, String smsContent,Long contactId) {
			
		 logger.info("entering getNextCouponCode first method");

		 return getNextCouponCode(tempStr,campaignName, campaignType, issuedTo, sentId, smsContent, contactId,false);
		 
	 }
	    
		public synchronized String  getNextCouponCode(String tempStr, String campaignName, String campaignType, 
												String issuedTo, Long sentId, String smsContent,Long contactId, boolean flushToDB) {
			try {
				
				Long userid=null;
				logger.info("entering getNextCouponCodereferral method"+referralcodesmap);

				if(referralcodesmap==null) referralcodesmap = new Hashtable<String, Queue<ReferralcodesIssued>>();
				
				
				try {
					if(referralcodesdao == null)referralcodesdao = (ReferralcodesIssuedDao)ServiceLocator.getInstance().getDAOByName(OCConstants.REFERRALCODES_DAO);
					
					
					if(couponsDao == null) couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
					if(couponsDaoForDML == null) couponsDaoForDML = (CouponsDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONS_DAOForDML);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new BaseServiceException("no dao found with id ::"+OCConstants.REFERRALCODES_DAO);
				}
				
				Queue<ReferralcodesIssued> phCouponCodeQue = referralcodesmap.get(tempStr);
				
				if(phCouponCodeQue==null || phCouponCodeQue.size() == 0) {
					//fillCouponCodes();
					logger.debug("creating the que====");
					//before filling the queue of coupcode let it be flushed for DB sake
					flushCouponCodesToDB(true);
					
					try {
						
						if(couponSet == null || couponSet.size() == 0) {
							
							if(logger.isDebugEnabled()) logger.debug("no  Promo-code set found with  Promo-code place holder:source request is wrong");
							return null;
							
						}
						
						for (String eachPh : couponSet) {
							
							logger.info("couponSet value is"+couponSet);

							
							logger.info("entering for loop getNextCouponCodereferral method");

							 if(eachPh.startsWith("REF_CC_")) {
							
									logger.info("entering if loop getNextCouponCodereferral method");

								
								if(logger.isDebugEnabled()) logger.debug("Filling  Promo-codes ::"+eachPh);
								try {
									
									Queue<ReferralcodesIssued> tempQue = null;
									tempQue = referralcodesmap.get(eachPh);
									
									if(tempQue!=null && !tempQue.isEmpty()) {
										continue; // skip if it is not Empty
									}
									
									String[] strArr = eachPh.split("_");
									
									String coupid=strArr[2];
									
									if(logger.isDebugEnabled()) logger.debug("Filling  Promo-code with Id = "+strArr[2]);
									//TODO need to handle this exception
									
									List<ReferralcodesIssued> referralCodesList = referralcodesdao.getInventoryCouponCodesByCouponId(Long.parseLong(strArr[2].trim()));
									coupon = couponsDao.findById(Long.parseLong(coupid));
									userid=coupon.getUserId();
									
									if(referralCodesList!=null && referralCodesList.size() > 0) {
										if(logger.isDebugEnabled()) logger.debug("=============== couponCodesList=="+referralCodesList.size());
									}
									else {
										if(logger.isDebugEnabled()) logger.debug("=============== No  Promo-codes found for the Coupons Id=="+strArr[1]);
										
										// TODO handle single type of Coupons
										//coupon = couponsDao.findById(Long.parseLong(coupid));
										//userid=coupon.getUserId();
										if(coupon==null || coupon.getAutoIncrCheck()==false) {
										
										logger.info("entering if");	
											
											continue;
									
										
										
										}
										else if(coupon!=null && 
												coupon.getCouponGeneratedType().equals(Constants.COUP_GENT_TYPE_MULTIPLE) &&
												coupon.getAutoIncrCheck()==true) { // CC Multi Type Auto 
										
											logger.info("entering else");	

											Long cqty = coupon.getTotalQty();
											if(cqty==null) cqty = new Long(0);
											
											coupon.setTotalQty(new Long(cqty.longValue()));
											//couponsDao.saveOrUpdate(coupon);
											couponsDaoForDML.saveOrUpdate(coupon);
											
											CouponCodesUtility ccu = new CouponCodesUtility(context, coupon);
											ccu.start();
											if(logger.isDebugEnabled()) logger.debug("Waiting to create the 1000  Promo-codes...");
											ccu.join();
											if(logger.isDebugEnabled()) logger.debug("Created 1000  Promo-codes.");
											
											referralCodesList = referralcodesdao.getInventoryCouponCodesByCouponId(coupon.getCouponId());
										
											
											logger.info("referralCodesList valueinventory is "+referralCodesList);
										}
										else if(coupon!=null && coupon.getAutoIncrCheck()==true) { // CC Single Type Auto 
											
											logger.info("entering elseif");	

											referralCodesList = new ArrayList<ReferralcodesIssued>();
											ReferralcodesIssued tempCC = null;
											
											for (int i = 0; i < 1000; i++) {
												tempCC = new ReferralcodesIssued();
												tempCC.setOrgId(coupon.getOrgId());
												tempCC.setCouponId(coupon);
												tempCC.setRefcode(coupon.getCouponCode()); // Set the Coupon code from Coupon
												referralCodesList.add(tempCC);
											
											
											
											} // for i
										} // else
										
									} // else
									
									logger.info("referralCodesList value is "+referralCodesList);
									
									if(referralCodesList!=null && !referralCodesList.isEmpty()) {
										
										tempQue = new PriorityQueue<ReferralcodesIssued>();
										tempQue.addAll(referralCodesList);
										
										referralcodesmap.put(eachPh, tempQue);
										
										logger.info("coupid value is "+coupid);
										
										logger.info("tempQue value is "+tempQue);

									} // if
							
								} catch (Exception e) {
									logger.error("Exception ::::",e);
								}
								
							} // if
							
							
						}
							
							
				
					
							
						 // for eachPh
						
						//if(logger.isDebugEnabled()) logger.debug("After filling the  Promo-code, provider.Map ="+couponCodesMap);
						
					} catch (Exception e) {
						logger.error("** Error in filling the  Promo-codes",e);
					}
					
				//REF_CC_11225_mar34 printing this
					logger.info("tempStr value is "+tempStr);

					
					phCouponCodeQue = referralcodesmap.get(tempStr);
					
					logger.info("phCouponCodeQue value is "+referralcodesmap.get(tempStr));

					logger.info("phCouponCodeQue value"+phCouponCodeQue);

					if(phCouponCodeQue==null) {
					
					logger.info("entering ");
						
						
						return null;
					} // if
				} // outer if
				/*
				 * tempCC.setCampaignName(campaignName);
					tempCC.setSentId(sentId.longValue());
					tempCC.setCampaignType(type);
					tempCC.setIssuedTo(issuedTo);
					tempCC.setIssuedOn(Calendar.getInstance());
					tempCC.setStatus(Constants.COUP_GENT_CODE_STATUS_ACTIVE);
					couponCodesDao.saveOrUpdate(tempCC);
				 */
				
			
				try {
					refprgmobj  =	refprgramdao.findReferalprogramByUserId(userid);
					logger.info("refprgmobj value "+refprgmobj);
					//logger.info("refprgmobj name is"+refprgmobj.getProgramName());
					}catch(Exception e) {
						logger.info("refprgmobjis null"+e);	
					}
				logger.debug("phCouponCodeQue size is ===="+phCouponCodeQue.size());
				ReferralcodesIssued tempCC = phCouponCodeQue.poll();
				
				tempCC.setCampaignType(campaignType);
				tempCC.setSentTo(issuedTo);
				tempCC.setStatus(Constants.COUP_GENT_CODE_STATUS_ACTIVE);
				tempCC.setIssuedDate(Calendar.getInstance());
				tempCC.setReferredCId(contactId);
				tempCC.setRefprogramName(refprgmobj.getProgramName());
				addCouponCodeToVec(tempCC);
				
				logger.debug("tempCC.getRefcode()  is ===="+tempCC.getRefcode());
				//for sms bracode
				flushCouponCodesToDB(flushToDB);
				return tempCC.getRefcode();
			
			} catch (Exception e) {
				logger.error("Exception ::::", e);
			}
			
			return null;
		}
	
	
}


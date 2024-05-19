package com.optculture.launchpad.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.optculture.launchpad.repositories.CouponCodesRepository;
import com.optculture.launchpad.repositories.CouponsRepository;
import com.optculture.launchpad.submitter.Submitter;
import com.optculture.shared.entities.promotion.CouponCodes;
import com.optculture.shared.entities.promotion.Coupons;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Service
public class CouponProvider {



	
    private Hashtable<Long, LinkedBlockingQueue<CouponCodes>> couponCodesMap = null;
    
    private Vector<CouponCodes> usedCouponCodesVec = new Vector<CouponCodes>();
	
    public synchronized void addCouponCodeToVec(CouponCodes cc) {
    	usedCouponCodesVec.add(cc);
    	flushCouponCodesToDB(false);
    } //
	
	Logger logger = LoggerFactory.getLogger(CouponProvider.class);
	
	@Autowired
	CouponsRepository couponsRepository;
	
	@Autowired
	CouponCodesRepository couponCodesRepository;
	
	public CouponProvider(CouponCodesRepository couponCodesRepository) {
        this.couponCodesRepository = couponCodesRepository;
    }
	
	public Set<Long> couponSet = new HashSet<>();
	
	private static CouponProvider couponProvider;
	
	public synchronized void flushCouponCodesToDB(boolean flushFlag) {
		 
		 
	    	try {
				if(usedCouponCodesVec==null || usedCouponCodesVec.isEmpty()) return;
				
				if(flushFlag==true || usedCouponCodesVec.size()>100) {
					//couponCodesDao.saveByCollection(usedCouponCodesVec);
					couponCodesRepository.saveAll(usedCouponCodesVec);
					usedCouponCodesVec.clear();
				} // if
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
			}
	    }

	 public synchronized String  getNextCouponCode(Long couponId, String campaignName, String campaignType,
				String issuedTo, Long sentId, String smsContent,Long contactId) {
		 
		 return getNextCouponCode(couponId,campaignName, campaignType, issuedTo, sentId, smsContent, contactId,false);
		 
	 }
	 public String getAlreadyIssuedCoupon(Long couponId,  Long contactID){
		 try {
				//if(couponCodesDao == null)couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
				//if(couponsDao == null) couponsDao = (CouponsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONS_DAO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("no dao found with id ::");
			}
			
//		 String[] strArr = CC_ID.split("_");
		 Optional<Coupons> coupon = couponsRepository.findById(couponId);
		 
		 
		 if(coupon!=null && 	
					coupon.get().getCouponGeneratedType().equalsIgnoreCase("multiple")) {
			 
			CouponCodes cc  =  couponCodesRepository.findByCouponIdCouponIdAndContactId(coupon.get().getCouponId() , contactID);
			
//			 long existCount =
//						couponCodesRepository.findCountByCouponId(coupon.get().getCouponId());
			
//				logger.info("existCount is"+existCount);

			if(cc != null ) return cc.getCouponCode();
		 }
		 return null;
	 }
		public synchronized String  getNextCouponCode(Long couponId, String campaignName, String campaignType,
												String issuedTo, Long sentId, String smsContent,Long contactId, boolean flushToDB) {
			try {
				
				if(couponCodesMap==null) couponCodesMap = new Hashtable<Long, LinkedBlockingQueue<CouponCodes>>();

				Queue<CouponCodes> phCouponCodeQue = couponCodesMap.get(couponId);
				
				if(phCouponCodeQue==null || phCouponCodeQue.size() == 0) {
					//fillCouponCodes();
					
					//before filling the queue of coupcode let it be flushed for DB sake
					flushCouponCodesToDB(true);
					
					try {


						if(couponSet == null || couponSet.size() == 0) {
							
							if(logger.isDebugEnabled()) logger.debug("no  Promo-code set found with  Promo-code place holder:source request is wrong");
							return null;
							
						}
						for (Long eachCouponId : couponSet) {
							
//							if(eachCC_ID.startsWith("CC_")) {
							
								if(logger.isDebugEnabled()) logger.debug("Filling  Promo-codes ::"+eachCouponId);
								try {
									
									LinkedBlockingQueue<CouponCodes> tempQue = null;
									tempQue = couponCodesMap.get(eachCouponId);
									
									if(tempQue!=null && !tempQue.isEmpty()) {
										continue; // skip if it is not Empty
									}
									
//									String[] strArr = eachCC_ID.split("_");
									
									if(logger.isDebugEnabled()) logger.debug("Filling  Promo-code with Id = "+eachCouponId);
									//TODO need to handle this exception
									//TODO need to optimize && add in set wihtout going to db
									List<CouponCodes> couponCodesList = couponCodesRepository.findByCouponIdCouponIdAndStatus( eachCouponId,"Inventory");
									if(couponCodesList!=null && couponCodesList.size() > 0) {
										if(logger.isDebugEnabled()) logger.debug("=============== couponCodesList=="+couponCodesList.size());
									}
									else {
										if(logger.isDebugEnabled()) logger.debug("=============== No  Promo-codes found for the Coupons Id=="+eachCouponId);
										
										// TODO handle single type of Coupons
										Optional<Coupons> coupon = couponsRepository.findById(eachCouponId);
										
										logger.info(" coupon value is"+coupon);

										logger.info(" coupon genet type is"+coupon.get().getCouponGeneratedType());

										logger.info(" coupon  autoincr check is"+coupon.get().getAutoIncrCheck());

										
										if(coupon==null || coupon.get().getAutoIncrCheck()==false) {
											continue;
										}
										else if(coupon!=null && 
												coupon.get().getCouponGeneratedType().equalsIgnoreCase("multiple") &&
												coupon.get().getAutoIncrCheck()==true) { // CC Multi Type Auto 
											
											logger.info("entering into elseif block");
											Long cqty = coupon.get().getTotalQty();
											if(cqty==null) cqty = new Long(0);
											
											coupon.get().setTotalQty(new Long(cqty.longValue()+1000));
											//couponsDao.saveOrUpdate(coupon);
											couponsRepository.save(coupon.get());
											
											CouponCodesUtility ccu = new CouponCodesUtility(coupon.get(),couponCodesRepository);
											ccu.start();
											if(logger.isDebugEnabled()) logger.debug("Waiting to create the 1000  Promo-codes...");
											ccu.join();
											if(logger.isDebugEnabled()) logger.debug("Created 1000  Promo-codes.");
											
											couponCodesList = couponCodesRepository.findByCouponIdCouponIdAndStatus(coupon.get().getCouponId(),"Inventory");
											
										}
										else if(coupon!=null && coupon.get().getAutoIncrCheck()==true) { // CC Single Type Auto 
											couponCodesList = new ArrayList<CouponCodes>();
											CouponCodes tempCC = null;
											
											for (int i = 0; i < 1000; i++) {
												tempCC = new CouponCodes();
												tempCC.setOrgId(coupon.get().getOrgId());
												tempCC.setCouponId(coupon.get());
												tempCC.setCouponCode(coupon.get().getCouponCode()); // Set the Coupon code from Coupon
												couponCodesList.add(tempCC);
											} // for i
										} // else
										
									} // else
									
									if(couponCodesList!=null && !couponCodesList.isEmpty()) {
										
										tempQue = new LinkedBlockingQueue<>();
										tempQue.addAll(couponCodesList);
										
										couponCodesMap.put(couponId, tempQue);
										
									} // if
								} catch (Exception e) {
									logger.error("Exception ::::",e);
								}
								
//							} // if
							
						} // for eachPh
						
						//if(logger.isDebugEnabled()) logger.debug("After filling the  Promo-code, provider.Map ="+couponCodesMap);
						
					} catch (Exception e) {
						logger.error("** Error in filling the  Promo-codes",e);
					}
					
					phCouponCodeQue = couponCodesMap.get(couponId);
					if(phCouponCodeQue==null) {
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
				CouponCodes tempCC = phCouponCodeQue.poll();
				tempCC.setCampaignName(campaignName);
				tempCC.setCampaignType(campaignType);
				tempCC.setIssuedTo(issuedTo);
				tempCC.setStatus("Active");
				tempCC.setSentId(sentId);
				tempCC.setIssuedOn(LocalDateTime.now());
				tempCC.setContactId(contactId);
				addCouponCodeToVec(tempCC);
				
				//for sms barcode
				logger.info("sms ph CC_ID: "+couponId);
				Coupons coupon = tempCC.getCouponId();
				
				
				// changes related to coupon status change
				if(campaignType.equalsIgnoreCase("AutoSMS")) {
					
					logger.info("entering campaign Type is AutoSMS to call flushCouponCodesToDB");
					flushCouponCodesToDB(true);
				
				}else {
				
					flushCouponCodesToDB(flushToDB);
				}
				return tempCC.getCouponCode();
				
			} catch (Exception e) {
				logger.error("Exception ::::", e);
			}
			
			return null;
		}
	
	

	
	
}

package com.optculture.launchpad.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.optculture.launchpad.configs.MessageListener;
import com.optculture.launchpad.repositories.CouponCodesRepository;
import com.optculture.launchpad.repositories.CouponsRepository;
import com.optculture.shared.entities.promotion.CouponCodes;
import com.optculture.shared.entities.promotion.Coupons;

@Service
public class CouponCodesUtility extends Thread implements ApplicationContextAware {
		
		static Logger logger = LoggerFactory.getLogger(CouponCodesUtility.class);

	
		private ApplicationContext context;
		Coupons coupon;

	
	
	    private  CouponCodesRepository couponCodesRepository;
		
		
		@Override
		public void setApplicationContext(ApplicationContext context) throws BeansException {
			this.context=context;
		}
		

	    @Autowired
	    public CouponCodesUtility(CouponCodesRepository couponCodesRepository) {
	        this.couponCodesRepository = couponCodesRepository;
	    }
		
		public CouponCodesUtility(Coupons coupon,CouponCodesRepository couponCodesRepository) {
			this.context = context;
			this.coupon = coupon;
	        this.couponCodesRepository = couponCodesRepository;

			try {
			//	couponCodesDao = (CouponCodesDao)ServiceLocator.getInstance().getDAOByName(OCConstants.COUPONCODES_DAO);
			//	couponCodesDaoForDML = (CouponCodesDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.COUPONCODES_DAOForDML);
			//	referralcodesDaoForDML = (ReferralcodesIssuedDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.REFERRALCODES_DAOFORDML);

		
			} catch (Exception e) {
				logger.error("Exception ", e);
			}
			if(logger.isDebugEnabled()) logger.debug("couponCodesDao==");
		}

		public void run() {
			
			try {
				if(logger.isDebugEnabled()) logger.debug("Thread Starting::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
				
				generateCouponCodes();
				
				
				if(logger.isDebugEnabled()) logger.debug("Thread Finished::"+Thread.currentThread().getName()+"_"+System.currentTimeMillis());
			} catch (Exception e) {
				logger.error("Exception ::::", e);
			}
		}
		
		//Generates coupon codes for random or multiple generation type for each contact
		public synchronized boolean generateCouponCodes() {
			try {
				if(logger.isDebugEnabled()) logger.debug("coupon="+coupon);
				if(logger.isDebugEnabled()) logger.debug(" Promo-code id="+coupon.getCouponId());
				
				if(coupon.getCouponGeneratedType().equals("SINGLE") &&
						coupon.getAutoIncrCheck()==true) {
					if(logger.isDebugEnabled()) logger.debug(" Promo-code type single no need to generate.");
					return true;
				}
				
				logger.info("couponCodesRepository value is"+couponCodesRepository);
				
				long existCount = 
						couponCodesRepository.findCountByCouponId(coupon.getCouponId());
				
				Long genQty = coupon.getTotalQty();
				
				if(genQty==null) {
					if(logger.isDebugEnabled()) logger.debug(" Promo-code Total qty. is null");
					return false;
				}
				
				if(genQty.longValue() <= existCount) {
					return false;
				}
				
				
				long codesToGenerate = genQty.longValue()-existCount;
				logger.info("codesToGenerate value "+codesToGenerate);
				long organizationId = coupon.getOrgId().longValue();
				
				List<CouponCodes> ccList = new ArrayList<CouponCodes>();
				CouponCodes ccObj=null;
				
				for (int i = 0; i < codesToGenerate; i++) {
					
					String couponCode = null;
					 // Assign the CouponCode from Coupon Object 
					if(coupon.getCouponCode()!=null && coupon.getAutoIncrCheck()==false) {
						couponCode = coupon.getCouponCode();
					}
					if(coupon.getCoupCodeGenType() != null && coupon.getCoupCodeGenType().equals("Numerical")) {
						
						Random rnd = new Random();
					    int number = rnd.nextInt(99999999);

					    // this will convert any number sequence into 6 character.
					    couponCode = String.format("%08d", number);
					}else {
						
						//Generate coupon codes
						List<String> md5List = null;
						while(couponCode == null) {
							
							md5List = couponGenarationCode(organizationId + System.nanoTime()+"", 8);
							
							Iterator<String> iterator = md5List.iterator();
							while (iterator.hasNext()) {
								String code = iterator.next();
								if(couponCodesRepository.findByCouponCodeAndOrgId(code, organizationId) == null) {
									couponCode = code;
									break;
								} 
							}//while 
							
						}//while 
					}
					
					ccObj = new CouponCodes();
					ccObj.setOrgId(organizationId);
					ccObj.setCouponId(coupon);
					ccObj.setStatus("INVENTORY");
					ccObj.setCouponCode(couponCode);
					
					ccList.add(ccObj);
					
					if(ccList.size()>200) {
						//couponCodesDao.saveByCollection(ccList);
						couponCodesRepository.saveAll(ccList);
						ccList.clear();
					}
				} // for i
				
				if(ccList.size() > 0) {
					//couponCodesDao.saveByCollection(ccList);
					couponCodesRepository.saveAll(ccList);
					ccList.clear();
				}

				return true;
			} catch (Exception e) {
				logger.error("Exception in generating the  Promo-codes.", e);
				return false;
			}	
			
					
		} // End generateCouponCodes()
		
		
		public static List<String> couponGenarationCode(String inputURL , int param) {
			
			try {
				List<String> couponGenList = new ArrayList<String>();
				
				char[] base32 =  {
					    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
					    'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 
					    'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 
					    'Z', '1', '2', '3', '4', '5', '6', '7'
				};
				
				String Md5result = MD5Algo(inputURL);
				if(Md5result == null) {
					//logger.info(" :: generated  MD5 algo is null");
					return null;
				}
				//logger.info("MD5 result string is ===>"+Md5result);
				 int lenthofInt = Md5result.length();
				// logger.info("lenthofInt ===>"+lenthofInt);
				 int subHexLenInt = lenthofInt / 8;
				// logger.info("subHexLenInt ===>"+subHexLenInt);
				 
				 int maxLength = 8;
				 
				 for(int i=0; i < subHexLenInt; i++) {
					 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);
				//	 logger.info("subHexStr ::"+subHexStr);
				//	 logger.info("Hex ::"+Long.parseLong(subHexStr, 16));
					 
					 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
				//	 logger.info("hexaValue is::"+hexaValue);
					 
					 StringBuffer outSb = new StringBuffer();
					 
					 for (int j = 0; j < param; j++) {
					      long val = 0x0000001F & hexaValue;
					    
					      outSb.append(base32[(int)val]);
					      hexaValue = hexaValue >>> param-1;
					  } //inner for
					 
				//	 logger.info("== "+outSb);
					 if(outSb !=  null)
						 couponGenList.add(outSb.toString());
				 } // outer for
				 return couponGenList;
				 
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
			 
		} // couponGenarationCode
		private static String MD5Algo(String inputUrl) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				 byte[] messageDigest = md.digest(inputUrl.getBytes());
				 
//				 logger.debug("md5 lengtyh ==="+messageDigest.length);
				 BigInteger number = new BigInteger(1, messageDigest);
				 String hashtext = number.toString(16);
				   
				   // Now we need to zero pad it if you actually want the full 32 chars.
				   while (hashtext.length() < 32) {
					   
				       hashtext = "0" + hashtext;
				   }
				   return hashtext;
			} catch (Exception e) {
				logger.error("Exception ::error occured while generating the MD5 algoitham",e);
				return null;
			}
		} // MD5Algo
		
		
	}//End class

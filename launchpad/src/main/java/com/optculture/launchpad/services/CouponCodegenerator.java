package com.optculture.launchpad.services;

import com.optculture.launchpad.repositories.CouponCodesRepository;
import com.optculture.launchpad.repositories.CouponsRepository;
import com.optculture.launchpad.utility.CustomCouponObj;
import com.optculture.shared.entities.promotion.CouponCodes;
import com.optculture.shared.entities.promotion.Coupons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class CouponCodegenerator {
    private ApplicationContext context;
//   private Coupons coupon;
    @Autowired
   private CouponCodesRepository couponCodesRepository;
   @Autowired
   private CouponsRepository couponsRepository;
   private static CouponsProvider couponProvider;
   @Value("${couponsMinSize}")
   private int COUPON_MIN_SIZE;
   @Value("${couponsGenCount}")
    private int COUPON_GEN_COUNT;
  public static Hashtable<Long,CustomCouponObj> couponCodesMap;
  public  static  BlockingQueue<CouponCodes> usedCouponCodesQue;
  static Logger logger = LoggerFactory.getLogger(CouponCodegenerator.class);

    public void intializeQueue(Coupons coupon, Long ccId) {
        if(couponProvider==null) {
            couponProvider = CouponsProvider.getCouponsProvider();//get singleton obj
        }
        this.couponCodesMap=couponProvider.couponCodesMap;
        if(usedCouponCodesQue==null)
        this.usedCouponCodesQue=couponProvider.usedCouponCodesQue;
        CustomCouponObj customCouponObj=couponCodesMap.get(ccId);
        if(customCouponObj== null){
            logger.info("Intializing the availble couponcodes queue for couponId "+ccId);
            BlockingQueue<CouponCodes> blockingQueue= new LinkedBlockingQueue<>();
            List<CouponCodes> couponCodesList = couponCodesRepository.findByCouponIdCouponIdAndStatus(coupon.getCouponId(),"Inventory");
            if(couponCodesList!= null || !(couponCodesList.isEmpty())){

                blockingQueue.addAll(couponCodesList);
            }
            customCouponObj= new CustomCouponObj(blockingQueue,coupon);
            couponCodesMap.put(ccId,customCouponObj);
        }
    }

//    @Override
//    public void setApplicationContext(ApplicationContext context) throws BeansException {
//        this.context=context;
//    }
//    public  void run(){
////        genrerateCouponCodes();
//    }
    public  CouponCodegenerator(){
    }
    @Scheduled(cron = "*/20 * * * * *") //for every 20 secs
    private void genrerateCouponCodes() {
        if(couponCodesMap!=null) {
            for (Long couponId : couponCodesMap.keySet()) {
                CustomCouponObj customCouponObj = couponCodesMap.get(couponId);
                Coupons coupon = customCouponObj.getCoupon();
                BlockingQueue<CouponCodes> availbleQue = customCouponObj.getCouponCodesQue();
                synchronized (CouponCodegenerator.class) {
                    if (availbleQue.size() < COUPON_MIN_SIZE) {
                        logger.info("Couponcodes size lessthan threshold size of coupon :" + couponId + " start time :" + LocalDateTime.now());
                        List<CouponCodes> couponCodes = getNewCouponCodes(coupon, couponId, availbleQue);
                        availbleQue.addAll(couponCodes);
                        logger.info("Coupon codes generated of size :" + couponCodes.size() + "  for coupon " + couponId + " generation completion time" + LocalDateTime.now());
                        Long existingQuantity = coupon.getTotalQty() == null ? 0L : coupon.getTotalQty();
                        coupon.setTotalQty(existingQuantity + couponCodes.size());
                        coupon = couponsRepository.save(coupon); //updated count
                        customCouponObj.setCoupon(coupon); //set updated coupon
                    }
                }
            }
        }
    }

    public synchronized List<CouponCodes> getNewCouponCodes(Coupons coupon, Long couponId, BlockingQueue<CouponCodes> availbleQue) {

//
            if(availbleQue.size() >COUPON_MIN_SIZE ) return new ArrayList<>();
//            if(logger.isDebugEnabled()) logger.debug("coupon="+coupon);
//            if(logger.isDebugEnabled()) logger.debug(" Promo-code id="+coupon.getCouponId());

            if(coupon.getCouponGeneratedType().equalsIgnoreCase("single") &&
                    coupon.getAutoIncrCheck()==true) {
               logger.info("Generating Promo codes for coupon "+coupon.getCouponId());
                List<CouponCodes> couponCodesList = new ArrayList<CouponCodes>();
                CouponCodes tempCC = null;

                for (int i = 0; i < COUPON_GEN_COUNT; i++) {
                    tempCC = new CouponCodes();
                    tempCC.setOrgId(coupon.getOrgId());
                    tempCC.setCouponId(coupon);
                    tempCC.setCouponCode(coupon.getCouponCode()); // Set the Coupon code from Coupon
                    couponCodesList.add(tempCC);
                }
                return couponCodesList;
                }

           else if(coupon.getCouponGeneratedType().equalsIgnoreCase("multiple") &&
                    coupon.getAutoIncrCheck()==true) {
                long organizationId = coupon.getOrgId().longValue();
                logger.info("Generating Coupon codes for coupon "+coupon.getCouponId());
                List<CouponCodes> ccList = new ArrayList<CouponCodes>();
                Set<String> generatedcodes=new HashSet<>();
                CouponCodes ccObj = null;

                for (int i = 0; i < COUPON_GEN_COUNT; i++) {

                    String couponCode = null;
                    if (coupon.getCoupCodeGenType() != null && coupon.getCoupCodeGenType().equals("Numerical")) {

                        Random rnd = new Random();
                        int number = rnd.nextInt(99999999);

                        // this will convert any number sequence into 6 character.
                        couponCode = String.format("%08d", number);
                        generatedcodes.add(couponCode);
                    } else {
                        //Generate alpha-numeric coupon codes
                        List<String> md5List = null;
                        md5List = couponGenarationCode(organizationId + System.nanoTime() + "", 8);
                        if(md5List!=null && !md5List.isEmpty()){
                            generatedcodes.addAll(md5List);
                        }
                    }
                } // for i
                //filter generated codes by removing , if already any exist in DB,available in Queue,or in used Queue.
                List<CouponCodes> codesExistInDb= couponCodesRepository.findByOrgIdAndCouponCodeIn(coupon.getOrgId(),generatedcodes.stream().toList());
                HashSet<String> codesExistSet= new HashSet<>(codesExistInDb.stream().map(couponCode -> couponCode.getCouponCode()).toList());
                HashSet<String> codesExistInAvailQue=new HashSet<>(availbleQue.stream().map(couponCode ->couponCode.getCouponCode()).toList());
                HashSet<String> usedCodesSet= new HashSet<>(usedCouponCodesQue.stream().map(couponCode -> couponCode.getCouponCode()).toList());
                for(String code :generatedcodes) {
                    if(!(codesExistSet.contains(code)) && !(codesExistInAvailQue.contains(code)) && !(usedCodesSet.contains(code))) {
                        ccObj = new CouponCodes();
                        ccObj.setOrgId(organizationId);
                        ccObj.setCouponId(coupon);
                        ccObj.setStatus("INVENTORY");
                        ccObj.setCouponCode(code);
                        ccList.add(ccObj);
                    }
                }
                return ccList;
            }
           return new ArrayList<>();
    }// End generateCouponCodes()


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

            int lenthofInt = Md5result.length(); //fixed length of 32 characters

            int subHexLenInt = lenthofInt / 8;

            int maxLength = 8;
            // TODO generating four codes at time
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

}

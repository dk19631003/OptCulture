package com.optculture.app.services;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.coupons.IssueCouponRequest;
import com.optculture.app.dto.coupons.CouponInventoryDto;
import com.optculture.app.dto.coupons.CouponIssuedResponseDto;
import com.optculture.app.repositories.CouponCodesRepository;
import com.optculture.app.repositories.CouponsRepository;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.entities.promotion.CouponCodes;
import com.optculture.shared.entities.promotion.Coupons;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;


@Service
public class CouponsService {
    @Autowired
    CouponsRepository couponsRepository;
    @Autowired
    GetLoggedInUser getLoggedInUser;
    @Autowired
    CouponCodesRepository couponCodesRepository;
    @Autowired
    SqIdService sqIdService;
    @Value("${launchpadURL}")
    String baseUrl;
    Logger logger= LoggerFactory.getLogger(CouponsService.class);
    public Map<String,List<String>> getBannerImageByUserId(Long userId)
    {
        Page<Coupons> couponsPage= couponsRepository.findByUserIdAndStatusAndHighlightedOfferAndBannerImageIsNotNullOrderByCouponCreatedDate(userId,"Running",true, PageRequest.of(0,3));
        List<Coupons> couponsList=couponsPage.getContent();
        Map<String,List<String>> result=new HashMap<String,List<String>>();
        List<String> bannerImages = new ArrayList<>();
        List<String> bannerRedirectUrls = new ArrayList<>();

        for(Coupons coupons:couponsList)
        {
          bannerImages.add(coupons.getBannerImage());
          bannerRedirectUrls.add(coupons.getBannerUrlRedirect()!=null? coupons.getBannerUrlRedirect():"--");
        }
        result.put("ImageBanner",bannerImages);
        result.put("RedirectUrl",bannerRedirectUrls);
        return result;
    }

    public ResponseObject<List<CouponIssuedResponseDto>> getCouponsListByContactId(Long contactId, Integer pageNumber, Integer pageSize) {
        User user=getLoggedInUser.getLoggedInUser();
        Long orgId=user.getUserOrganization().getUserOrgId();
        Sort sort=Sort.by(Sort.Direction.DESC,"ccId");
        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize,sort);
        Page<CouponIssuedResponseDto> couponDtoList=couponsRepository.findCouponsByContactId(contactId,orgId, pageRequest);
        long totalIssuedCoupons=couponDtoList.getTotalElements();
        long totalPages=couponDtoList.getTotalPages();
        return  new ResponseObject<>(totalIssuedCoupons,totalPages,couponDtoList.getContent());
    }
    public  Page<CouponInventoryDto> getInventoryCouponsByUserId(Long userId, int pageSize, int pageNumber, String couponName){

        String couponCodeStatus="Inventory";
        User user=getLoggedInUser.getLoggedInUser();
        Long orgId=user.getUserOrganization().getUserOrgId();
        Sort.Order order1= Sort.Order.desc("couponId");
//        Sort.Order order2 =Sort.Order.asc("ccId");
        Sort sort=Sort.by(Arrays.asList(order1));
        Coupons coupon= new Coupons();
        if(!couponName.equals("--")) coupon.setCouponName(couponName);
        List<String > status= Arrays.asList("Running","Active");
        coupon.setOrgId(orgId);
        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize,sort);
        return couponsRepository.findInventoryCoupons(coupon,status,pageRequest);
    }

    public ResponseEntity issueCoupontoContact(IssueCouponRequest issueCouponReq)  {


        String endpoint = "/coupon-provider/get-next-coupon";
        String queryParams=new String();
        queryParams=queryParams.concat("couponId=");
        if(issueCouponReq.getCouponId()!=null)
           queryParams=queryParams.concat(issueCouponReq.getCouponId().toString());
        queryParams=queryParams.concat("&contactId=");
        if(issueCouponReq.getContactId()!=null) {
            Long contactId=sqIdService.decodeId(issueCouponReq.getContactId()).get(0);
            queryParams = queryParams.concat(contactId.toString());
        }
        queryParams=queryParams.concat("&issuedTo=");
        queryParams=queryParams.concat(issueCouponReq.getMobile()!=null?issueCouponReq.getMobile():issueCouponReq.getEmailId());
        try {
            URI uri = new URI(baseUrl + endpoint + "?" + queryParams);
            logger.info("URI is :"+uri);
            RestTemplate restTemplate=new RestTemplate();
            HttpEntity<String> requestEntity = new HttpEntity<>(null);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
            System.out.println("response is "+response.getBody());
            String resp[]=response.getBody().split("::");
            if(resp[0].equalsIgnoreCase("coupon code"))
             return new ResponseEntity("issued code ::"+resp[1],HttpStatus.OK);
            else return new ResponseEntity<>("Failed !",HttpStatus.OK);
        }
        catch (Exception e){
            logger.info("Exception while issueing coupon "+e);
        }
         return new ResponseEntity<>("Failed !",HttpStatus.OK);
    }

    public Map<String, String> getActiveAndRunningCouponsMap() {
    	Map<String, String> couponsMap = new LinkedHashMap<>();

		couponsRepository.findActiveAndRunningCouponsList(getLoggedInUser.getLoggedInUser().getUserId())
				.forEach(coupon -> couponsMap.put(coupon.getCouponName(), coupon.getCouponId().toString()));

		return couponsMap;
    }
    public List<CouponInventoryDto> getActiveAndRunningCouponsList() {
        User user=getLoggedInUser.getLoggedInUser();
		return  couponsRepository.findActiveAndRunningCouponsDto(user.getUserOrganization().getUserOrgId());
    }

}

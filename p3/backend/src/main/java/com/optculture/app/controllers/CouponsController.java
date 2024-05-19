package com.optculture.app.controllers;

import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.custom.ResponseObject;
import com.optculture.app.dto.coupons.IssueCouponRequest;
import com.optculture.app.dto.coupons.CouponIssuedResponseDto;
import com.optculture.app.services.CouponsService;
import com.optculture.app.services.SqIdService;
import com.optculture.shared.entities.org.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponsController {
    @Autowired
    GetLoggedInUser getLoggedInUser;
    @Autowired
    SqIdService sqIdService;
    @Autowired
    CouponsService couponsService;
    @GetMapping("/issued")
    public ResponseObject<List<CouponIssuedResponseDto>> getCouponsList(@RequestParam  String contactId, @RequestParam(defaultValue = "0",required = false) Integer pageNumber, @RequestParam(defaultValue = "10",required = false) Integer pageSize){

        List<Long> idList=sqIdService.decodeId(contactId);
        return couponsService.getCouponsListByContactId(idList.get(0),pageNumber,pageSize);
    }
    @PutMapping("/issue-coupon")
    public ResponseEntity issueCoupon(@RequestBody IssueCouponRequest issueCouponReq){

        List<Long> idList=sqIdService.decodeId(issueCouponReq.getContactId());
        return couponsService.issueCoupontoContact(issueCouponReq);
    }
    @GetMapping("/inventory")
    public ResponseEntity getInventoryCoupons(@RequestParam(defaultValue = "0",required = false) int pageNumber,@RequestParam(defaultValue = "6" ,required = false) int pageSize, @RequestParam(defaultValue = "--",required = false) String couponName){
        User user= getLoggedInUser.getLoggedInUser();
        return  new ResponseEntity<>(couponsService.getInventoryCouponsByUserId(user.getUserId(),pageSize,pageNumber,couponName), HttpStatus.OK);
    }
}

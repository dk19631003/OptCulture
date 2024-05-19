import { Customer } from './../classes/customer';
import { HttpHandlerService } from './../services/http-handler.service';
import { UrlEnums } from 'src/app/enums/urlenums.enum';
import { Platform } from '@ionic/angular';
import { Component, ViewChild, OnInit } from '@angular/core';
import { Router, ActivationStart, RouterOutlet } from '@angular/router';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { GenericService } from '../services/generic.service';
import { MemberShip } from '../classes/member-ship';
import { Header } from '../classes/header';
import { LoyaltyInquiry } from '../classes/LoyaltyInquiry';
import { User } from '../classes/user';

@Component({
  selector: 'app-tab3',
  templateUrl: 'tab3.page.html',
  styleUrls: ['tab3.page.scss']
})
export class Tab3Page implements OnInit {
  @ViewChild(RouterOutlet) outlet: RouterOutlet;
  barCodeString: string;
  detailsObject;
  height =  window.screen.height * 0.35 + 'px';
  spacesNumber;
  monospace = 'monospace';
  profilePicImageUrl = 'assets/images/empty-profile.png';
  ismobile = this.genericService.ismobileWeb();
  headerObject;
  memberShip;
  customer;
  loyaltyInquiryObject;
  user;
  loyaltyEnquiryResponse;
  constructor(private router: Router,
              public platform: Platform,
              private sanitizer: DomSanitizer,
              public statusBar: StatusBar,
              public genericService: GenericService,
              private httpHandler: HttpHandlerService
    ) {  
      this.memberShip = new MemberShip();
      this.headerObject = new Header();
      this.loyaltyInquiryObject = new LoyaltyInquiry();
      this.user = new User();
      this.customer = new Customer();
      this.user = JSON.parse(this.genericService.getItem('user'));
    }

  getImgContent(): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(this.profilePicImageUrl);
  }

  ngOnInit() {
    this.router.events.subscribe(e => {
      if (e instanceof ActivationStart && e.snapshot.outlet === 'administration') {
        this.outlet.deactivate();
      }
    });
  }

  ionViewWillEnter() {
    this.profilePicImageUrl = window.localStorage.getItem('profileimageUrl') ?
    window.localStorage.getItem('profileimageUrl') : this.profilePicImageUrl;
    this.detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
    this.barCodeString = this.detailsObject.cardNumber;
    if(this.barCodeString === undefined || this.barCodeString === null || this.barCodeString === ''){
       this.getMemberShipNumber();
    }
    this.spacesNumber = this.numberWithSpaces(this.detailsObject.phoneNumber.toString());
  }

 async getMemberShipNumber(){
      let email, mobileNumber = '';
      if (this.genericService.getItem('mobileNumber').includes('@')) {
        email = this.genericService.getItem('mobileNumber');
      } else {
        mobileNumber = this.genericService.getItem('mobileNumber');
      }
      const loading = await this.genericService.getLoading();
      loading.present();
      this.headerObject = this.genericService.prepareHeaderObject();
      this.memberShip.issueCardFlag = 'Y';
      this.memberShip.cardNumber = this.genericService.getItem('cardNumber');
      this.memberShip.phoneNumber = mobileNumber;
      this.memberShip.cardPin = '';
      this.customer.emailAddress = email;
      this.loyaltyInquiryObject.header = this.headerObject;
      this.loyaltyInquiryObject.membership = this.memberShip;
      this.loyaltyInquiryObject.user = this.user;
      this.loyaltyInquiryObject.customer = this.customer;
      this.httpHandler.httpPostRequestWithBody(
        UrlEnums.BASE_URL + UrlEnums.LOYALTY_ENQUIRY_URL, this.loyaltyInquiryObject)
        .subscribe(loyaltyEnquiryResponse => {
          loading.dismiss();
          console.log(loyaltyEnquiryResponse);
          this.loyaltyEnquiryResponse = loyaltyEnquiryResponse;
  
          if (this.loyaltyEnquiryResponse.status.errorCode === '0') {
           this.barCodeString = this.loyaltyEnquiryResponse.membership;
          }
          
        }, err => {
          loading.dismiss();
        });
  }

  numberWithSpaces(x) {
    const original = x;
    const part1 = x.substring(0, 3);
    const part2 =  x.substring(3, 6);
    const part3 = x.substring(6, 10);
    return part1 + ' ' + part2 + ' ' + part3;
    // return x.toString().replace(/\B(?=(\d{3}\1\d{4})+(?!\d))/g, " ");
}

}

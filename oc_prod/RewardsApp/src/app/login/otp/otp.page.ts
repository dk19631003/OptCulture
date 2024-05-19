import 'rxjs/add/observable/timer';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/take';
import { MemberShip } from './../../classes/member-ship';
import { NavController, Platform } from '@ionic/angular';
import { User } from './../../classes/user';
import { Header } from './../../classes/header';
import { Customer } from './../../classes/customer';
import { HttpHandlerService } from './../../services/http-handler.service';
import { UrlEnums } from './../../enums/urlenums.enum';
import { Component, OnInit, ViewChild, NgZone, AfterViewInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { GenericService } from 'src/app/services/generic.service';
import { LoyaltyInquiry } from 'src/app/classes/LoyaltyInquiry';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { Constants } from 'src/app/enums/constants.enum';
import { LoginRequest, Membership } from 'src/app/classes/login.model';
import { UUID } from 'angular2-uuid';

@Component({
  selector: 'app-otp',
  templateUrl: './otp.page.html',
  styleUrls: ['./otp.page.scss'],
})
export class OtpPage implements OnInit , AfterViewInit {

  textDir;
  show = false;
  @ViewChild('otp1') otpInput;
  mobileNumber: string;
  user: User;
  customer: Customer;
  otpResponse: any;
  sendingOTP: boolean;
  loyaltyEnquiryResponse: any;
  headerObject: Header;
  memberShip: Membership = {} as Membership;
  loyaltyInquiryObject: LoginRequest = {} as LoginRequest;
  password: string;
  correctOTP: string;
  digit1;
  digit2;
  digit3;
  digit4;
  countDown;
  counter = 30;
  otpInterval;
  isOTPResent = false;
  spacesNumber: string;
  minvalue = 0;
  maxvalue = 9;
  isKeyboardOpen = false;
  imageurl: string;
  isMobile = false;
  portalBrandingResponse;
  loginMemberShipResponse;
  constructor(public router: Router,
              public httpHandler: HttpHandlerService,
              public navCtrl: NavController,
              public route: ActivatedRoute,
              public zone: NgZone,
              public platform: Platform,
              public statusBar: StatusBar,
              public genericService: GenericService) {
                this.isMobile = this.platform.is('mobile');
                this.portalBrandingResponse =  JSON.parse(this.genericService.getSessionItem('portalBranding'));
                if (this.portalBrandingResponse) {
                  this.imageurl = this.portalBrandingResponse.bannerImage;
                }
                const params =  this.router.getCurrentNavigation().extras.state;
                console.log(params);
                // this.memberShip = new MemberShip();
                this.headerObject = new Header();
                // this.loyaltyInquiryObject = new LoyaltyInquiry();
                this.user = new User();
                this.customer = new Customer();
                this.user = JSON.parse(this.genericService.getItem('user'));
               }

  ngOnInit() {


    this.textDir = this.genericService.getItem('textDir');
    window.addEventListener('native.keyboardshow', () => {
      this.zone.run(() => {
        this.isKeyboardOpen = true;
      });
  });

    window.addEventListener('native.keyboardhide', () => {

      this.zone.run(() => {
        this.isKeyboardOpen = false;
      });
    });
  }

  ngAfterViewInit() {
    const params =  this.router.getCurrentNavigation().extras.state;
    console.log(params);
    // if (params) {
    if (params ) {
        this.mobileNumber = params ? params.mobile : params;
        this.sendingOTP = false;
        this.sendOTP('Issue');
      } else {
        this.sendingOTP = true;
      }
    // }
  }

  ionViewWillEnter() {
    this.isOTPResent = false;
  }

  ionViewDidLoad() {
    // setTimeout(() => {

    // }, 500);

  }
  backButtonEvent() {
      this.router.navigateByUrl('/login');

  }

  loginUsingPassword() {
    this.router.navigateByUrl('/login');
  }

  next(el: any, value) {
    if (value && value >= 0 && value <= 9) {
       el.setFocus();
    }

  }

  back(el: any, value) {
    if (!value) {
      el.setFocus();
    }
  }


  async sendOTP(type) {
    if (this.mobileNumber) {
      let email, mobileNumber = '';
      if (this.mobileNumber.includes('@')) {
         email = this.mobileNumber;
      } else {
         mobileNumber = this.mobileNumber;
         this.spacesNumber = this.genericService.numberWithSpaces(mobileNumber);
      }
      const otpRequestObject: any = {};
      const customer = new Customer();
      let headerObject;
      headerObject = this.genericService.prepareHeaderObject();
      headerObject.requestType = type;
      customer.phone = '' + mobileNumber;
      customer.emailAddress =  email;
      customer.addressLine1 = '';
      customer.addressLine2 = '';
      customer.birthday = '';
      customer.customerId = '';
      customer.gender = '';
      customer.postal = '';
      customer.state = '';
      customer.anniversary = '';
      otpRequestObject.header = headerObject;
      otpRequestObject.user = this.user;
      otpRequestObject.customer = customer;
      otpRequestObject.otpCode = type === 'Issue' ? '' : this.correctOTP;
      otpRequestObject.issueOnAction = "login"
      const loading = await this.genericService.getLoading();
      loading.present();
      this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.OCOTPSERVICE_URL, otpRequestObject)
      .subscribe(otpResponse => {
        // if(type=="Issue")
        // {
          console.log(otpResponse);
          this.otpResponse = otpResponse;
          if (this.otpResponse.status.errorCode === '0') {
            this.sendingOTP = false;
            this.correctOTP = this.otpResponse.otpCode ;
            this.setCounter();
            // this.otpInput.focus();
          } else {
            alert(this.otpResponse.status.message);
          }
          loading.dismiss();
        // }
      }, err =>{
        loading.dismiss();
      });
    } else {
      alert('Please Enter MobileNumber or Email');
    }
  }

  setCounter() {
    this.otpInterval = setInterval(() => {
      this.counter--;
      if (this.counter < 0) {
         clearInterval(this.otpInterval);
      }
    }, 1000);
  }


  checkOTP() {
    this.correctOTP =  this.digit1.toString() + this.digit2.toString() + this.digit3.toString() + this.digit4.toString();
    this.login(false);
  }
  resendOTP() {
    this.sendOTP('Issue');
  }

  login(withOutPassword) {

    let email, mobileNumber = '';
    if (this.mobileNumber.includes('@')) {
       email = this.mobileNumber;
    } else {
       mobileNumber = this.mobileNumber;
    }
    const enteredOTP =  this.digit1.toString() + this.digit2.toString() + this.digit3.toString() + this.digit4.toString();
    this.headerObject = this.genericService.prepareHeaderObject();
    this.memberShip.phoneNumber = mobileNumber;
    this.memberShip.emailId = email;
    this.memberShip.OTP = enteredOTP;
    this.memberShip.deviceId = this.genericService.getUDID() ? this.genericService.getUDID() : UUID.UUID();
    this.memberShip.instanceId = this.genericService.getItem('deviceToken');
    this.loyaltyInquiryObject.header = this.headerObject;
    this.loyaltyInquiryObject.membership = this.memberShip;
    this.loyaltyInquiryObject.user = this.user;
    // this.loyaltyInquiryObject.customer = this.customer;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.LOGIN_URL, this.loyaltyInquiryObject)
      .subscribe(loyaltyEnquiryResponse => {
        console.log(loyaltyEnquiryResponse);
        this.loyaltyEnquiryResponse = loyaltyEnquiryResponse;

        if (this.loyaltyEnquiryResponse.status.errorCode === '0') {
        this.genericService.setItem('mobileNumber', this.mobileNumber);
        this.loginMemberShipResponse = this.loyaltyEnquiryResponse.membership;
        this.genericService.setItem('cardNumber', this.loginMemberShipResponse.cardNumber);

        this.genericService.setItem('password', this.password);
        let userName: string;
          // tslint:disable-next-line: prefer-const
        let phoneNumber,cardNumber: string;
        if (this.loyaltyEnquiryResponse.matchedCustomers.length > 0) {
               // tslint:disable-next-line: max-line-length
               userName = this.loyaltyEnquiryResponse.matchedCustomers[0].firstName + ' ' + this.loyaltyEnquiryResponse.matchedCustomers[0].lastName;
               mobileNumber = mobileNumber === '' ? this.loyaltyEnquiryResponse.matchedCustomers[0].phone : mobileNumber;
               email = this.loyaltyEnquiryResponse.matchedCustomers[0].emailAddress;
               cardNumber = this.loyaltyEnquiryResponse.matchedCustomers[0].membershipNumber;
            }
          this.genericService.setItem('cardNumber', cardNumber);
          const tierName = this.loyaltyEnquiryResponse.membership.tierName;
          let points = '0';
          if (this.loyaltyEnquiryResponse.balances.length > 0) {
              const balances = this.loyaltyEnquiryResponse.balances;
              // tslint:disable-next-line: prefer-for-of
              for (let i = 0; i < balances.length; i++) {
                 if (balances[i].valueCode === 'Points') {
                   if (balances[i].amount) {
                   points = balances[i].amount;
                   }
                   break;
                 }
              }
            }
          // const pushvalue = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.pushNotifications;
          let pushNotifications = false;
          // if (pushvalue && typeof pushvalue === 'string') {
          //     pushNotifications =  pushvalue.toLowerCase() === 'true';
          //   } else {
          //     pushNotifications = false;
          //   }

          // const fingerPrintvalue = this.loyaltyEnquiryResponse.membership.fingerprintValidation;
          let isFingerprintEnable = false;
          // if (fingerPrintvalue && typeof fingerPrintvalue === 'string') {
          //      isFingerprintEnable = this.loyaltyEnquiryResponse.membership.fingerprintValidation.toLowerCase() === 'true';
          //   } else {
          //     isFingerprintEnable = false;
          //   }

          // let langauage = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.language;

          // this.genericService.setItem('isFingerPrintEnable', isFingerprintEnable.toString());

          let langauage = 'en';
          this.genericService.setLanguage(langauage);
          this.genericService.setItem('points', points);
          const homePageObject = {
              userName,
              phoneNumber : mobileNumber,
              tierName,
              points,
              pushNotifications,
              langauage,
              cardNumber: cardNumber,
              isFingerprintEnable,
              email
             };

          const tierDetails = {
              tierName: this.loyaltyEnquiryResponse.membership.tierName,
              currentTierValue: this.loyaltyEnquiryResponse.membership.currentTierValue,
              nextTierName: this.loyaltyEnquiryResponse.membership.nextTierName,
              nextTierMilestone: this.loyaltyEnquiryResponse.membership.nextTierMilestone,
              tierUpgradeCriteria: this.loyaltyEnquiryResponse.membership.tierUpgradeCriteria,
             };
	        const sessionId =  this.loyaltyEnquiryResponse.membership.sessionID;
          this.genericService.setItem('sessionId', sessionId);
          if(this.genericService.getItem('user')) {
            const orgInfo  = JSON.parse(this.genericService.getItem('user'));
            orgInfo.sessionID = sessionId;
            this.genericService.setItem('user', JSON.stringify(orgInfo));
          }
          
          this.genericService.setItem('tierDetails', JSON.stringify(tierDetails));
          this.genericService.setItem('homePageObject' , JSON.stringify(homePageObject));
          this.genericService.setItem('companyLogo', this.loyaltyEnquiryResponse.additionalInfo.companyLogo);

          if (this.platform.is('ios')) {
               this.router.navigateByUrl('app/tabs/home');
             } else {
              this.navCtrl.navigateRoot('app/tabs/home');
             }
            // this.router.navigateByUrl('app/tabs/home');
       } else {
         alert(this.loyaltyEnquiryResponse.status.message);
       }
      });
  }

  navigateToSignUp() {
    this.router.navigateByUrl('/enrolment');
  }
}

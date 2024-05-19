import { Platform } from '@ionic/angular';
import { Constants, SOURCE_TYPE } from './../enums/constants.enum';
import { LoyaltyInquiry } from 'src/app/classes/LoyaltyInquiry';
import { MemberShip } from './../classes/member-ship';
import { ImportContact } from './../classes/importContact';
import { LookUp } from './../classes/lookup';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Customer } from './../classes/customer';
import { UrlEnums } from './../enums/urlenums.enum';
import { Component, OnInit, ViewChild, NgZone, AfterViewInit } from '@angular/core';
import { GenericService } from '../services/generic.service';
import { Header } from '../classes/header';
import { HttpHandlerService } from '../services/http-handler.service';
import { UpdateContacts } from '../classes/updateContact';
import { Report } from '../classes/report';
import { User } from '../classes/user';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { UUID } from 'angular2-uuid';

@Component({
  selector: 'app-forgot-password',
  templateUrl: 'forgot-password.page.html',
  styleUrls: ['forgot-password.page.scss'],
})
export class ForgotPasswordPage implements OnInit , AfterViewInit {

  otpResponse;
  tokenDetails;
  updatedCustomer;
  password;
  updatedContactsResponse;
  phone: string;
  show = true;
  passwordType = 'password';
  sentOTP;
  otp;
  customer: any = {};
  report: Report;
  user: User;
  lookup: LookUp;
  loyaltyEnquiryResponse: any;
  importContactObject: ImportContact;
  textDir = 'ltr';
  correctOTP: string;
  digit1;
  digit2;
  digit3;
  digit4;
  sendingOTP: boolean;
  enteringOTP: boolean ;
  resetingPwd: boolean;
  sucessMessage: boolean;
  newPassword;
  confirmPassword;
  resetShow =  true;
  otpInterval;
  counter = 30;
  memberShip;
  headerObject;
  loyaltyInquiryObject;
  importContactResponse;
  spacesNumber;
  isKeyboardOpen = false;
  imageurl: string;
  isMobile = false;
  portalBrandingResponse;
  resetPasswordSessionToken;
  constructor(private genericService: GenericService,
              private httpHandler: HttpHandlerService,
              private route: ActivatedRoute,
              private router: Router,
              public zone: NgZone,
              public platform: Platform,
              public statusBar: StatusBar,
              private datePipe: DatePipe) {
                this.portalBrandingResponse =  JSON.parse(this.genericService.getSessionItem('portalBranding'));
                if (this.portalBrandingResponse) {
                    this.imageurl = this.portalBrandingResponse.bannerImage  ?
                  this.portalBrandingResponse.bannerImage : this.imageurl;
                }
                this.isMobile = this.platform.is('mobile');
                this.textDir = this.genericService.getItem('textDir');
                this.memberShip = new MemberShip();
                this.headerObject = new Header();
                this.loyaltyInquiryObject = new LoyaltyInquiry();
                this.user = new User();
                this.customer = new Customer();
                this.updatedCustomer = new UpdateContacts();
                this.importContactObject =  new ImportContact();
                this.report = new Report();
                this.user = new User();
                this.lookup = new LookUp();
                this.route.params.subscribe(params => {
                  console.log(params);
                  this.phone = params.phone;
             });
                this.tokenDetails = JSON.parse(this.genericService.getItem('user'));
   }

  ngOnInit() {


    window.addEventListener('native.keyboardshow', (e: any) => {
        this.zone.run(() => {
          this.isKeyboardOpen = true;
        });
    });

    window.addEventListener('native.keyboardhide', () => {

        this.zone.run(() => {
          this.isKeyboardOpen = false;
        });
      });

    // this.sendOTP();
  }

  ngAfterViewInit() {
    const params =  this.router.getCurrentNavigation().extras.state;
    console.log(params);
    // if (params) {
    if (params ) {
        this.phone = params ? params.mobile : params;
        this.sendingOTP =  false;
        this.enteringOTP = true;
        this.sendOTP('Issue');
      } else {
        this.sendingOTP = true;
        this.enteringOTP = false;
      }
    // }

    this.resetingPwd =  false;
    this.sucessMessage = false;
  }

  resendOTP() {
    this.sendOTP('Issue');
  }

  async sendOTP(type) {
    const loading = await this.genericService.getLoading();
    loading.present();
    let email = '';
    let mobileNumber = '';
    if (this.phone.includes('@')) {
       email = this.phone;
    } else {
       mobileNumber = this.phone;
       this.spacesNumber = this.genericService.numberWithSpaces(mobileNumber);
    }
    const otpRequestObject: any = {};
    const customer = new Customer();
    let headerObject;
    headerObject = this.genericService.prepareHeaderObject();
    headerObject.requestType = type;
    customer.phone = '' + mobileNumber;
    customer.emailAddress = '' + email;
    customer.addressLine1 = '';
    customer.addressLine2 = '';
    customer.birthday = '';
    customer.customerId = '';
    customer.gender = '';
    customer.postal = '';
    customer.state = '';
    customer.anniversary = '';
    customer.deviceID =  this.genericService.getUDID() ? this.genericService.getUDID() : UUID.UUID();
    otpRequestObject.header = headerObject;
    otpRequestObject.user = this.tokenDetails;
    otpRequestObject.customer = customer;
    otpRequestObject.otpCode = type === 'Issue' ? '' : this.correctOTP;
    otpRequestObject.issueOnAction = 'resetPassword';
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.OCOTPSERVICE_URL, otpRequestObject)
      .subscribe(otpResponse => {
        loading.dismiss();
          console.log(otpResponse);
          this.otpResponse = otpResponse;
          if (this.otpResponse.status.errorCode === '0') {
            this.correctOTP =  this.otpResponse.otpCode;
            this.sendingOTP =  false;
            this.enteringOTP = true;
            this.setCounter();
            if (type === 'Issue') {
              this.otpResponse = otpResponse;
            } else {
              this.enteringOTP = false;
              this.resetingPwd = true;
              setTimeout( () => {
                this.showPassword();
                this.showResetPassword();
              }, 1000);
              this.resetPasswordSessionToken = this.otpResponse.sessionID;
            }
        } else {
          alert(this.otpResponse.status.message);
        }
        
      }, err => {
        loading.dismiss();
      });
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
    const enteredOTP =  this.digit1.toString() + this.digit2.toString() + this.digit3.toString() + this.digit4.toString();
    // if (enteredOTP === this.correctOTP) {
     this.correctOTP = enteredOTP;
      this.sendOTP('Acknowledge');
     

    // } else {
    //   alert('Please Enter Valid OTP');
    // }
  }

  submit() {
    if (this.newPassword === this.confirmPassword) {
        this.changePassword();
    } else {
      alert('Both Passwords should be same');
    }
  }

  async changePassword() {
    const loading = await this.genericService.getLoading();
    loading.present();
    let email, mobileNumber = '';
    if (this.phone.includes('@')) {
       email = this.phone;
    } else {
       mobileNumber = this.phone;
       this.spacesNumber = this.genericService.numberWithSpaces(mobileNumber);
    }
    const currentTimeStamp =  this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');
    const headerObject = new Header();
    headerObject.requestId  = currentTimeStamp;
    headerObject.requestDate = currentTimeStamp;
    headerObject.contactSource = SOURCE_TYPE;
    headerObject.sourceType = SOURCE_TYPE;
    headerObject.contactList = 'List1';
    this.updatedCustomer.header = headerObject;
    this.updatedCustomer.user = this.tokenDetails;
    this.updatedCustomer.user.sessionID = this.resetPasswordSessionToken;
    this.customer.emailAddress = email;
    this.customer.phone = mobileNumber;
    this.customer.creationDate = currentTimeStamp;
    const loyalty = {
      password : '' + this.genericService.encryptData(this.newPassword),
    };
    this.customer.loyalty = loyalty;
    this.customer.supress  = {
      phone: {
        isTrue : '',
        reason: ''
      },
      email: {
        isTrue : '',
        reason: '',
        timestamp: ''
      }
    }
    this.updatedCustomer.customer = this.customer;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.UPDATE_CONTACT_URL, this.updatedCustomer)
      .subscribe(updatedContactsResponse => {
        console.log(updatedContactsResponse);
        this.updatedContactsResponse = updatedContactsResponse;

        if (this.updatedContactsResponse.status.errorCode === '0') {
         this.resetingPwd = false;
         this.sucessMessage = true;
       } else {
         alert(this.updatedContactsResponse.status.message);
       }
        loading.dismiss();
      } , err => {
        loading.dismiss();
      });

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

  showPassword() {
  this.show = !this.show;
  const x = document.getElementById('newPassword').getElementsByTagName('input')[0]; // undefined
  if (x.type === 'password') {
      x.type = 'text';
    } else {
      x.type = 'password';
    }
  }

  async getEnrolMentData() {
    const loading = await this.genericService.getLoading();
    loading.present();
    let email, mobileNumber = '';
    if (this.phone.includes('@')) {
       email = this.phone;
    } else {
       mobileNumber = this.phone;
       this.spacesNumber = this.genericService.numberWithSpaces(mobileNumber);
    }
    this.headerObject = this.genericService.prepareHeaderObject();
    this.headerObject.pcFlag = 'false';
    this.report.contactList = '';
    this.report.contactSource = '';
    this.report.contactType = '';
    this.report.endDate = '';
    this.report.offset = 0;
    this.report.maxRecords = 0;
    this.report.store = '';
    this.report.startDate = '';
    this.lookup.emailAddress = email;
    this.lookup.membershipNumber =  '';
    this.lookup.phone = mobileNumber;
    this.importContactObject.header = this.headerObject;
    this.importContactObject.importType = 'Lookup';
    this.importContactObject.lookup = this.lookup;
    this.importContactObject.report = this.report;
    this.importContactObject.user = this.tokenDetails;
    this.importContactObject.user.sessionID = this.resetPasswordSessionToken;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.IMPORT_CONTACT_URL, this.importContactObject)
      .subscribe(importContactResponse => {
        console.log(importContactResponse);
        this.importContactResponse = importContactResponse;

        if (this.importContactResponse.status.errorCode === '0') {
        this.customer = this.importContactResponse.matchedCustomers[0];
        this.changePassword();
       } else {
         alert(this.importContactResponse.status.message);
       }
        loading.dismiss();
      } , err => {
        loading.dismiss();
      });
  }
  showResetPassword() {
    this.resetShow = !this.resetShow;
    const x = document.getElementById('confirmPassword').getElementsByTagName('input')[0]; // undefined
    if (x.type === 'password') {
      x.type = 'text';
    } else {
      x.type = 'password';
    }
  }
  getLoyaltyEnquiryData() {
    if (this.phone) {
    let email, mobileNumber = '';
    if (this.phone.includes('@')) {
       email = this.phone;
    } else {
       mobileNumber = this.phone;
    }
    this.headerObject = this.genericService.prepareHeaderObject();
    this.memberShip.issueCardFlag = 'Y';
    this.memberShip.cardNumber = '';
    this.memberShip.phoneNumber = '';
    this.memberShip.cardPin = '';
    this.customer.phone = mobileNumber;
    this.customer.emailAddress = email;
    this.loyaltyInquiryObject.header = this.headerObject;
    this.loyaltyInquiryObject.membership = this.memberShip;
    this.loyaltyInquiryObject.user = this.tokenDetails;
    this.loyaltyInquiryObject.customer = this.customer;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.LOYALTY_ENQUIRY_URL, this.loyaltyInquiryObject)
      .subscribe(loyaltyEnquiryResponse => {
        console.log(loyaltyEnquiryResponse);
        this.loyaltyEnquiryResponse = loyaltyEnquiryResponse;

        if (this.loyaltyEnquiryResponse.status.errorCode === '0') {
        if (this.loyaltyEnquiryResponse.matchedCustomers.length > 0) {
          this.customer = this.loyaltyEnquiryResponse.matchedCustomers[0];
          this.sendOTP('Issue');
        } else {
          alert('You are not registered with us.Please Register with us');
        }
      } else {
        alert(this.loyaltyEnquiryResponse.status.message);
      }
      });
    } else {
      alert('Please enter MobileNumber or Email');
    }
    }

  backButtonEvent() {
    this.router.navigateByUrl('/login');
  }
  gotTOLogin() {
    this.router.navigateByUrl('/login');
  }


}

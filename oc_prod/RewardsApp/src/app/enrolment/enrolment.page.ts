import { AlertController, Platform } from '@ionic/angular';
import { Customer } from './../classes/customer';
import { LoyaltyInquiry } from './../classes/LoyaltyInquiry';
import { Constants, SOURCE_TYPE } from './../enums/constants.enum';
import { MemberShip } from './../classes/member-ship';
import { User } from './../classes/user';
import { Header } from './../classes/header';
import { Enrollment } from './../classes/enrollment';
import { UrlEnums } from './../enums/urlenums.enum';
import { Component, OnInit, NgZone } from '@angular/core';
import { HttpHandlerService } from '../services/http-handler.service';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { GenericService } from '../services/generic.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

export interface ValidationResult {
  [key: string]: boolean;
}

export class PasswordValidator {

  public static strong(control: FormControl): ValidationResult {
      let hasNumber = /\d/.test(control.value);
      let hasUpper = /[A-Z]/.test(control.value);
      let hasLower = /[a-z]/.test(control.value);
      // console.log('Num, Upp, Low', hasNumber, hasUpper, hasLower);
      const valid = hasNumber && hasUpper && hasLower;
      if (!valid) {
          // return what´s not valid
          return { strong: true };
      }
      return null;
  }
}

@Component({
  selector: 'app-enrolment',
  templateUrl: 'enrolment.page.html',
  styleUrls: ['enrolment.page.scss'],
})
export class EnrolmentPage implements OnInit {


  show = false;
  customer: Customer;
  registerDetails: Enrollment;
  headerObject: Header;
  tokenDetails: User;
  memberShip: MemberShip;
  tokenResponse: any;
  registrationResponse: any;
  isFingerPrintEnable = false;
  showFooter = true;
  loyaltyInquiryObject;
  loyaltyEnquiryResponse;
  otpResponse;
  textDir: string;
  keyboardHeight = '0px';
  imageurl: string;
  correctOTP;
  portalBrandingResponse;
  registerForm: FormGroup;
  isSubmitted = false;
  isMobile = false;
  registerSessionId;
  constructor(private httpHandler: HttpHandlerService,
              private datePipe: DatePipe,
              private genericService: GenericService,
              private alertCtrl: AlertController,
              public zone: NgZone,
              public platform: Platform,
              private formBuilder: FormBuilder,
              private router: Router) {
    this.portalBrandingResponse =  JSON.parse(this.genericService.getSessionItem('portalBranding'));
    if (this.portalBrandingResponse) {
      this.imageurl = this.portalBrandingResponse.bannerImage;
    }
    this.textDir = this.genericService.getItem('textDir');
    this.customer = new Customer();
    this.tokenDetails = new User();
    this.registerDetails =  new Enrollment();
    this.headerObject = new Header();
    this.memberShip =  new MemberShip();
    this.loyaltyInquiryObject = new LoyaltyInquiry();
    this.tokenDetails = JSON.parse(this.genericService.getItem('user'));
    this.isMobile = this.genericService.ismobileWeb();
  }

  backButtonEvent() {
    this.router.navigateByUrl('/login');
   // window.history.back();
  }

  ionViewWillEnter() {
    this.textDir = this.genericService.getItem('textDir');
  }

  get errorControl() {
    return this.registerForm.controls;
  }

  submitFrom() {
    this.isSubmitted = true;
    if (!this.registerForm.valid) {
      console.log('Please provide all the required values!');
      return false;
    } else {
      console.log(this.registerForm.value);
      const registerData = this.registerForm.value;
      this.customer.firstName = registerData.firstName;
      this.customer.lastName = registerData.lastName;
      this.customer.phone = registerData.mobile;
      this.customer.emailAddress = registerData.email;
      this.customer.password = this.genericService.encryptData(registerData.password);
      // this.customer.country = registerData.country;
      this.isFingerPrintEnable = registerData.fingerPrint;
      this.sendOtp('Issue');
    }
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required ]],
      email: ['', [Validators.required,
        Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
      mobile: ['', [Validators.required, Validators.maxLength(10) , Validators.minLength(10) , Validators.pattern('^[0-9]+$')  ]],
      password: ['',[Validators.required ,
         Validators.minLength(8), 
         Validators.pattern('(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&].{8,}')]
    ],
      // country: ['', [Validators.required ]],
      fingerPrint: [false]
   });

    window.addEventListener('keyboardWillShow', (e: any) => {
      console.log(e);
      this.zone.run(() => {
          if (this.platform.is('android')) {
            this.keyboardHeight = (e.keyboardHeight + 50) + 'px';
          }
          if (this.platform.is('ios')) {
            this.keyboardHeight =  (e.keyboardHeight + 10) + 'px';
            console.log(this.keyboardHeight);
          }
          console.log(this.keyboardHeight);
        });
    });


    window.addEventListener('keyboardWillHide', () => {
        this.zone.run(() => {
        this.keyboardHeight = '0px';
        });
      });

  }


  async register() {
    const loading = await this.genericService.getLoading();
    loading.present();
    const currentTimeStamp =  this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');
    this.headerObject.requestId  = currentTimeStamp;
    this.headerObject.requestDate = currentTimeStamp;
    this.headerObject.docSID = '' ;
    this.headerObject.sourceType = SOURCE_TYPE;
    this.headerObject.terminalId = '';
    this.headerObject.storeNumber = '10';
    this.headerObject.employeeId = '';
    this.headerObject.pcFlag = 'false';
    this.headerObject.receiptNumber = '';
    this.headerObject.subsidiaryNumber = '';
    this.memberShip.issueCardFlag = 'Y';
    this.memberShip.cardNumber = '';
    this.memberShip.phoneNumber = '';
    this.memberShip.cardPin = '';
    this.memberShip.fingerprintValidation = this.isFingerPrintEnable ? 'true' : 'false';
    this.customer.addressLine1 = '';
    this.customer.addressLine2 = '';
    this.customer.birthday = '';
    this.customer.customerId = '';
    this.customer.gender = '';
    this.customer.postal = '';
    this.customer.state = '';
    this.customer.anniversary = '';
    this.registerDetails.header = this.headerObject;
    this.registerDetails.membership =  this.memberShip;
    this.customer.instanceId = this.genericService.getItem('deviceToken');
    this.customer.deviceType = this.genericService.getPlatformName();
    this.registerDetails.customer = this.customer;
    this.registerDetails.user =  this.tokenDetails;
    this.httpHandler.httpPostRequestWithBody(
                     UrlEnums.BASE_URL + UrlEnums.ENROLMENT_URL, this.registerDetails)
                     .subscribe(registrationResponse => {
                       console.log(registrationResponse);
                       this.registrationResponse = registrationResponse;

                       if (this.registrationResponse.status.errorCode === '0') {
                        this.genericService.setItem('cardNumber', this.registrationResponse.membership.cardNumber);
                        this.genericService.setItem('isFingerPrintEnable', this.isFingerPrintEnable.toString());
                        this.genericService.setItem('mobileNumber', this.customer.phone);
                        this.genericService.setItem('password', this.memberShip.password);
                        alert(Constants.REGISTRATATIO_MSG);
                        this.router.navigateByUrl('/login');

                      } else {
                        alert(this.registrationResponse.status.message);
                      }
                       loading.dismiss();
                     } , err => {
                       loading.dismiss();
                     });
  }


  navigateToSignIn() {
    this.router.navigateByUrl('/login');
  }

  async getEnrolementData() {
    const loading = await this.genericService.getLoading();
    loading.present();
    const customer = new Customer();
    const memberShip = new MemberShip();
    const headerObject = this.genericService.prepareHeaderObject();
    memberShip.issueCardFlag = 'Y';
    memberShip.cardNumber = '';
    memberShip.phoneNumber = '';
    memberShip.cardPin = '';
    customer.phone = this.customer.phone;
    customer.emailAddress = '';
    this.loyaltyInquiryObject.header = headerObject;
    this.loyaltyInquiryObject.membership = memberShip;
    this.loyaltyInquiryObject.user = this.tokenDetails;
    this.loyaltyInquiryObject.customer = customer;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.LOYALTY_ENQUIRY_URL, this.loyaltyInquiryObject)
      .subscribe(loyaltyEnquiryResponse => {
        this.loyaltyEnquiryResponse = loyaltyEnquiryResponse;

      //  if(this.loyaltyEnquiryResponse.status.errorCode === "0")
      //  {
        if (this.loyaltyEnquiryResponse.matchedCustomers.length <= 0) {
          this.sendOtp('Issue');
        } else {
          alert('You already registered with phone no ' + this.customer.phone);
        }
      //  }
      //  else
      //  {
      //    alert(this.loyaltyEnquiryResponse.status.message)
      //  }
        loading.dismiss();
      } , err => {
        loading.dismiss();
      });
  }

   async sendOtp(type) {
    const loading = await this.genericService.getLoading();
    loading.present();
    const otpRequestObject: any = {};
    const customer = new Customer();
    this.headerObject = this.genericService.prepareHeaderObject();
    this.headerObject.requestType = type;
    customer.phone = '' + this.customer.phone;
    customer.emailAddress = '' + this.customer.emailAddress;
    customer.addressLine1 = '';
    customer.addressLine2 = '';
    customer.birthday = '';
    customer.customerId = '';
    customer.gender = '';
    customer.postal = '';
    customer.state = '';
    customer.anniversary = '';
    otpRequestObject.header = this.headerObject;
    otpRequestObject.user = this.tokenDetails;
    otpRequestObject.customer = customer;
    otpRequestObject.otpCode = type === 'Issue' ? '' : this.correctOTP;
    otpRequestObject.issueOnAction = "";
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.OCOTPSERVICE_URL, otpRequestObject)
      .subscribe(otpResponse => {
          // console.log(otpResponse);
          this.otpResponse = otpResponse;
          if (this.otpResponse.status.errorCode === '0') {
            this.correctOTP = this.otpResponse.otpCode;
            if (type === 'Issue') {
             this.showOTPAlert(this.otpResponse.otpCode);
            } else {
              this.register();
            }
          } else {
            alert(this.otpResponse.status.message);
            if('Acknowledge'){
              this.showOTPAlert(null);
            }
          }
          loading.dismiss();
      } , err => {
        loading.dismiss();
      });
  }

  async showOTPAlert(otp) {
    const otpalert = await this.alertCtrl.create({
      header: 'Enter Verification Code',
      inputs: [
        {
          type: this.isMobile ? 'number' : 'text',
          label: 'Verification Code',
        }
      ],
      backdropDismiss: false,
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
          cssClass: 'secondary',
          handler: () => {
            console.log('Confirm Cancel');
          }
        }, {
          text: 'Ok',
          handler: data => {
           console.log( data[0]);
           this.correctOTP = data[0]
           this.sendOtp('Acknowledge');
          }
        }
      ]
    });
    await otpalert.present();
  }

  showPassword() {
    this.show = !this.show;
    const x = document.getElementById('password').getElementsByTagName('input')[0]; // undefined
    console.log(x.type);
    if (x.type === 'password') {
      x.type = 'text';
    } else {
      x.type = 'password';
    }
  }

}

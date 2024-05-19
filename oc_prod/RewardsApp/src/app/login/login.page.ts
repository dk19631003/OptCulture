import { FingerprintLoginConfirmationPopupComponent } from './../components/fingerprint-login-confirmation-popup/fingerprint-login-confirmation-popup.component';
import { Membership } from './../classes/login.model';
import { UUID } from 'angular2-uuid';
import { GenericService } from './../services/generic.service';
import { Component, OnInit, ViewChild, NgZone, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { Platform, AlertController, NavController, IonContent, PopoverController, ModalController } from '@ionic/angular';
import { Customer } from './../classes/customer';
import { User } from './../classes/user';
import { Header } from './../classes/header';
import { UrlEnums } from './../enums/urlenums.enum';
import { HttpHandlerService } from './../services/http-handler.service';
import { DatePipe } from '@angular/common';
import { ShowHideInputDirective } from '../directives/show-hide-input.directive';
import { AndroidFingerprintAuth, AFAAuthOptions } from '@ionic-native/android-fingerprint-auth/ngx';
import { Keyboard } from '@ionic-native/keyboard/ngx';
import { TranslateService } from '@ngx-translate/core';
import { FingerprintAIO } from '@ionic-native/fingerprint-aio/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { LoginRequest } from '../classes/login.model';

const LNG_KEY = 'SELECTED_LANGUAGE';
@Component({
  selector: 'app-login',
  templateUrl: 'login.page.html',
  styleUrls: ['login.page.scss'],
  viewProviders: [ShowHideInputDirective]
})
export class LoginPage implements OnInit , AfterViewInit {

  @ViewChild(ShowHideInputDirective) input: ShowHideInputDirective;
  @ViewChild(IonContent) ionContent: IonContent;
  errorMessage = 'An error occurred';
  show = false;
  loyaltyInquiryObject: LoginRequest = {} as LoginRequest;
  headerObject: Header;
  loyaltyEnquiryResponse: any;
  memberShip: Membership = {} as Membership ;
  user: User;
  customer: Customer;
  cardNumber: string;
  mobileNumber: string;
  password: string;
  showBottom = false;
  showFooter = true;
  textDir: string;
  otpResponse: any;
  encryptSecretKey = 'enfldsgbnlsngdlksdsgm';
  keySize;
  iterationCount ;
  alert: any = {};
  backbuttonSubscription;
  rememberMe = true;
  passwordType = 'text';
  keyboardHeight = '0px';
  isKeyboardOpen = false;
  imageurl: string;
  customerName;
  portalBrandingResponse ;
  isMobile = false;
  showBtn = false;
  deferredPrompt;
  loginResponsmembership;
  constructor(
              private router: Router,
              private androidFingerprintAuth: AndroidFingerprintAuth,
              private translate: TranslateService,
              private httpHandler: HttpHandlerService,
              private platform: Platform,
              private genericService: GenericService,
              private alertCtrl: AlertController,
              public navCtrl: NavController,
              public zone: NgZone,
              public statusBar: StatusBar,
              public popCtrl: PopoverController,
              public modalCtrl: ModalController,
              private faio: FingerprintAIO) {
                history.pushState(null, null, location.href);
                window.onpopstate =  () => {
                    history.go(1);
                };
                // this.memberShip = new MemberShip();
                this.headerObject = new Header();
                // this.loyaltyInquiryObject = new LoyaltyInquiry();
                this.user = new User();
                this.customer = new Customer();
                this.isMobile = this.platform.is('mobile');
                this.genericService.loadPortalBrandingResponse().subscribe( (response) => {
                  console.log(response);
                  this.portalBrandingResponse =  JSON.parse(this.genericService.getSessionItem('portalBranding'));
                  if (this.portalBrandingResponse) {
                    this.customerName =
                    // tslint:disable-next-line: max-line-length
                    this.portalBrandingResponse.bannerName;
                    this.imageurl = this.portalBrandingResponse.bannerImage;
                  }
                  this.user = JSON.parse(this.genericService.getItem('user'));
                });
                this.textDir = this.genericService.getItem('textDir');
                }
 test() {
   console.log('test for otp component');
 }
  ngOnInit() {
    this.translate.get('OTP_ALERT_TITLE').subscribe(t => {
      this.alert.title = t;
    });


    // this.scrollToBottom();
    window.addEventListener('keyboardWillShow', (e: any) => {
      console.log(e);
      this.zone.run(() => {

         // alert("opening in keyboard 2")
         this.isKeyboardOpen = true;
         setTimeout(() => {
            if (this.platform.is('android')) {
              this.keyboardHeight = (e.keyboardHeight + 50) + 'px';
            }

            if (this.platform.is('ios')) {
              this.keyboardHeight =  '80px';
              console.log(this.keyboardHeight);
            }

           // this.scrollToBottom();
          }, 200);
         console.log(this.keyboardHeight);
        });
    });


    window.addEventListener('keyboardWillHide', () => {

      //  alert("closing in keyboard 2")
        this.zone.run(() => {
          this.isKeyboardOpen = false;
          this.keyboardHeight = '0px';
        // this.scrollToTop();
        });
      });
  }

  ngAfterViewInit() {
    this.scrollToBottom();
  }
   getContent() {
    return document.querySelector('ion-content');
  }
   scrollToBottom() {
    this.ionContent.scrollToBottom(500);
  }

  scrollToTop() {
    this.getContent().scrollToTop(500);
  }

  hardWarebackButtonEvent() {
    this.backbuttonSubscription = this.platform.backButton.subscribe(async () => {
      navigator['app'].exitApp();
    });
  }


  sendOTP() {
    if (this.mobileNumber) {
      let email, mobileNumber = '';
      if (this.mobileNumber.includes('@')) {
         email = this.mobileNumber;
      } else {
         mobileNumber = this.mobileNumber;
      }
      const otpRequestObject: any = {};
      const customer = new Customer();
      let headerObject = new Header();
      headerObject = this.genericService.prepareHeaderObject();
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
      otpRequestObject.header = headerObject;
      otpRequestObject.user = this.user;
      otpRequestObject.customer = customer;
      this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.OCOTPSERVICE_URL, otpRequestObject)
      .subscribe(otpResponse => {
          console.log(otpResponse);
          this.otpResponse = otpResponse;
          if (this.otpResponse.status.errorCode === '0') {
             this.showOTPAlert(this.otpResponse.otpCode);
          } else {
            alert(this.otpResponse.status.message);
          }

      });
    } else {
      alert('Please enter mobilenumber');
    }
  }

  intiaiteFingerprintLoginConfirmPopup(){
    if(this.platform.is('cordova')){
      if (!(this.genericService.getItem('isFingerPrintEnable') === undefined ||
      this.genericService.getItem('isFingerPrintEnable') === null)
      && (this.genericService.getItem('isFingerPrintEnable') === 'true') && 
      this.genericService.getItem('fingerprintloginreq')  ) {
        this.openFingerprintLoginConfirmPopup();
      } 
    } 
  }

  async  openFingerprintLoginConfirmPopup() {
    const popover = await this.modalCtrl.create({
      component: FingerprintLoginConfirmationPopupComponent,
      // componentProps: {url : erecepiturl},
      cssClass: 'fingerprint-modal'
    });

    return await popover.present();
  }

  ionViewWillEnter() {
    this.platform.ready().then(() => {
      this.intiaiteFingerprintLoginConfirmPopup();
      this.hardWarebackButtonEvent();
      // this.fingerPrintChecking();
      // this.keyboard.onKeyboardShow().subscribe(()=>
      // {
      //   document.body.classList.add('keyboard-is-open');
      // })
      // this.keyboard.onKeyboardHide().subscribe(()=>
      // {
      //   document.body.classList.remove('keyboard-is-open');
      // })

      window.addEventListener('beforeinstallprompt', (e) => {
        // Prevent Chrome 67 and earlier from automatically showing the prompt
        e.preventDefault();
        console.log(e);
        // Stash the event so it can be triggered later on the button event.
        this.deferredPrompt = e;
      // Update UI by showing a button to notify the user they can add to home screen
        this.showBtn = true;
      });
      // button click event to show the promt
      window.addEventListener('appinstalled', (event) => {
      //  alert('installed');
      });
      if (window.matchMedia('(display-mode: standalone)').matches) {
        // alert('display-mode is standalone');
      }
      // this.hardWarebackButtonEvent();
    });
    this.user = JSON.parse(this.genericService.getItem('user'));
    this.showPassword();
  }

  add_to_home(e) {
    // hide our user interface that shows our button
    // Show the prompt
    this.deferredPrompt.prompt();
    // Wait for the user to respond to the prompt
    this.deferredPrompt.userChoice
      .then((choiceResult) => {
        if (choiceResult.outcome === 'accepted') {
          // alert('User accepted the prompt');
        } else {
          // alert('User dismissed the prompt');
        }
        this.deferredPrompt = null;
      });
  }
  ionViewWillLeave() {
    this.backbuttonSubscription.unsubscribe();
  }

  loginWithFaceBook() {

    // this.fb.login(['public_profile'])
    // .then((res: FacebookLoginResponse) =>
    // {
    //   this.genericService.setItem("mobileNumber",mobileNumber);
    //   console.log('Logged into Facebook!', res);
    // //  this.fb.logEvent(this.fb.EVENTS.EVENT_NAME_ADDED_TO_CART);
    //   this.router.navigateByUrl('/home')

    // })
    // .catch(e => console.log('Error logging into Facebook', e));
  }

  androidFingerprint() {
    this.androidFingerprintAuth.isAvailable()
    .then((result) => {
      if (result.isAvailable) {
        // it is available
        let fingerprintoptions: AFAAuthOptions = { clientId: 'LoyaltyApp',
        username: 'Anil',
        password: 'Test@123'
      };
        if (this.genericService.getItem('homePageObject')) {
          const detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
          fingerprintoptions = {
            clientId: 'LoyaltyApp',
            username: 'Anil',
            password: 'Test@123',
            dialogTitle: 'Hello' + detailsObject.userName + ',',
            disableBackup: true,
            dialogMessage: 'Use touch ID to login again'
          };
        } else {
          fingerprintoptions = {
            clientId: 'LoyaltyApp',
            username: 'Anil',
            password: 'Test@123',
            disableBackup: true,
            dialogMessage: 'Use touch ID to login again'
          };
        }

        this.androidFingerprintAuth.encrypt( fingerprintoptions)
          .then((fingerprintResult) => {
            if (fingerprintResult.withFingerprint) {
                console.log('Successfully encrypted credentials.');
                console.log('Encrypted credentials: ' + fingerprintResult.token);
                if (!(this.genericService.getItem('mobileNumber') === 'undefined' ||
                       this.genericService.getItem('mobileNumber') === null) ) {
                   this.mobileNumber =  this.genericService.getItem('mobileNumber');
                   this.password = '';
                   this.login(true);
                }
            } else if (fingerprintResult.withBackup) {
              console.log('Successfully authenticated with backup password!');
            } else { console.log('Didn\'t authenticate!'); }
          })
          .catch(error => {
            if (error === this.androidFingerprintAuth.ERRORS.FINGERPRINT_CANCELLED) {
              console.log('Fingerprint authentication cancelled');
            } else { console.error(error); }
          });

      } else {
        // fingerprint auth isn't available
      }
  })
   .catch(error => console.error(error));
  }

  iosFingerPrint() {
    this.faio.isAvailable().then((isAvailable) => {
      console.log(isAvailable);
      this.faio.show({
        title: 'Fingerprint', // (Android Only) | optional | Default: "<APP_NAME> Biometric Sign On"
        subtitle: 'Use touch ID to login again', // (Android Only) | optional | Default: null // optional | When disableBackup is false defaults to "Use Pin".
                                           // When disableBackup is true defaults to "Cancel"
        disableBackup:true,  // optional | default: false
      })
    .then((result: any) => {
      console.log(result);
      this.mobileNumber =  this.genericService.getItem('mobileNumber');
      this.password = '';
      this.login(true);
    })
    .catch((error: any) => console.log(error));
    });

  }

  checkismobileandpassword() {
    if (this.mobileNumber && this.password) {
      this.login(false);
    } else {
      alert('Please enter mobilenumber and password');
    }
  }

  fingerPrintChecking() {
    if (!(this.genericService.getItem('isFingerPrintEnable') === 'undefined' ||
    this.genericService.getItem('isFingerPrintEnable') === null)
    && (this.genericService.getItem('isFingerPrintEnable') === 'true') ) {
      //  if(this.platform.is('android'))
      //  {
      //    this.androidFingerprint();
      //  }
      //  if(this.platform.is('ios'))
      //  {
         this.iosFingerPrint();
      //  }
    }
  }


 async login(withOutPassword) {
    if (this.mobileNumber && this.password || withOutPassword ) {
    let email, mobileNumber = '';
    if (this.mobileNumber.includes('@')) {
       email = this.mobileNumber;
    } else {
       mobileNumber = this.mobileNumber;
    }
    const loading = await this.genericService.getLoading();
    loading.present();
    this.headerObject = this.genericService.prepareHeaderObject();
    this.memberShip.phoneNumber = mobileNumber;
    this.memberShip.emailId = email;
    this.memberShip.password = this.password;
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
        this.memberShip = this.loyaltyEnquiryResponse.membership;
      
        this.genericService.setItem('password', this.password);
        let userName,cardNumber: string;
        if (this.loyaltyEnquiryResponse.matchedCustomers.length > 0) {
               // tslint:disable-next-line: max-line-length
               userName = this.loyaltyEnquiryResponse.matchedCustomers[0].firstName + ' ' + this.loyaltyEnquiryResponse.matchedCustomers[0].lastName;
               mobileNumber = mobileNumber === '' ? this.loyaltyEnquiryResponse.matchedCustomers[0].phone : mobileNumber;
               email = this.loyaltyEnquiryResponse.matchedCustomers[0].emailAddress;
               cardNumber =  this.loyaltyEnquiryResponse.matchedCustomers[0].membershipNumber;
            }
          this.genericService.setItem('cardNumber', cardNumber);
        const tierName = this.loyaltyEnquiryResponse.membership.tierName;
        let points = '0';
        let currency ;
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
              // tslint:disable-next-line: prefer-for-of
              for (let i = 0; i < balances.length; i++) {
              if (balances[i].valueCode === 'Currency') {
                if (balances[i].amount) {
                currency = balances[i].amount;
                }
                break;
              }
             }
            }

        console.log(currency);
        // const pushvalue = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.pushNotifications;
        let pushNotifications;
        // if (pushvalue && typeof pushvalue === 'string') {
        //       pushNotifications =  pushvalue.toLowerCase() === 'true';
        //     } else {
        //       pushNotifications = false;
        //     }

        // const fingerPrintvalue = this.loyaltyEnquiryResponse.membership.fingerprintValidation;
        let isFingerprintEnable;
        // if (fingerPrintvalue && typeof fingerPrintvalue === 'string') {
        //        isFingerprintEnable = this.loyaltyEnquiryResponse.membership.fingerprintValidation.toLowerCase() === 'true';
        //     } else {
        //       isFingerprintEnable = false;
        //     }
        // let langauage = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.language;
        // this.genericService.setItem('isFingerPrintEnable', isFingerprintEnable.toString());

        let langauage =  'en';
        this.setLanguage(langauage);
        this.genericService.setItem('points', points);
        const homePageObject = {
              userName,
              phoneNumber : mobileNumber,
              tierName,
              points,
              currency,
              pushNotifications,
              langauage,
              cardNumber: cardNumber,
              isFingerprintEnable,
              email
             };
             const sessionId =  this.loyaltyEnquiryResponse.membership.sessionID;
             this.genericService.setItem('sessionId', sessionId);
             if(this.genericService.getItem('user')) {
               const orgInfo  = JSON.parse(this.genericService.getItem('user'));
               orgInfo.sessionID = sessionId;
               this.genericService.setItem('user', JSON.stringify(orgInfo));
             }
        const tierDetails = {
              tierName: this.loyaltyEnquiryResponse.membership.tierName,
              currentTierValue: this.loyaltyEnquiryResponse.membership.currentTierValue,
              nextTierName: this.loyaltyEnquiryResponse.membership.nextTierName,
              nextTierMilestone: this.loyaltyEnquiryResponse.membership.nextTierMilestone,
              tierUpgradeCriteria: this.loyaltyEnquiryResponse.membership.tierUpgradeCriteria,
             };
           
        this.genericService.setItem('rememberMe', this.rememberMe.toString());
        this.genericService.setItem('tierDetails', JSON.stringify(tierDetails));
        this.genericService.setItem('homePageObject' , JSON.stringify(homePageObject));
        this.genericService.setItem('companyLogo', this.loyaltyEnquiryResponse.additionalInfo.companyLogo);

            //  if(this.platform.is('ios'))
            //  {
            //    this.router.navigateByUrl('app/tabs/home')
            //  }
            //  else
            //  {
        this.navCtrl.navigateRoot('app/tabs/home');
            //  }


            // this.router.navigateByUrl('app/tabs/home');
       } else {
         alert(this.loyaltyEnquiryResponse.status.message);
       }
        loading.dismiss();
      }, err => {
       loading.dismiss();
      });
    } else {
      alert('Please enter mobileNumber or email and password ');
    }
  }

  showPassword() {
    this.show = !this.show;
    const x = document.getElementById('password').getElementsByTagName('input')[0]; // undefined
    if (x.type === 'password') {
      x.type = 'text';
    } else {
      x.type = 'password';
    }
  }

  async showOTPAlert(otp) {

    const otpalert = await this.alertCtrl.create({
      header: this.alert.title,
      inputs: [
        {
          type: 'number',
          label: 'OTP',
        }
      ],
      // backdropDismiss:false,
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
           if (data[0] === otp) {
             this.login(true);
           } else {
             alert('Please Enter Valid OTP');
             this.showOTPAlert(this.otpResponse.otpCode);
           }
          }
        }
      ]
    });
    await otpalert.present();
  }

  navigateToSignUp() {
    this.router.navigateByUrl('/enrolment');
  }

  otpController(event, next, prev) {
    if (event.target.value.length < 1 && prev) {
      prev.setFocus();
    } else if (next && event.target.value.length > 0) {
      next.setFocus();
    } else {
     return 0;
    }
  }

  forgotPassword() {
    if (this.mobileNumber) {
      this.navCtrl.navigateForward(['/forgot-password'], { state: {
        mobile: this.mobileNumber
      }});
    } else {
      this.navCtrl.navigateForward(['/forgot-password']);
    }

  }

  goToOTP() {
    if (this.mobileNumber) {
      this.navCtrl.navigateForward(['/otp'], { state: {
        mobile: this.mobileNumber
      }});
    } else {
      this.navCtrl.navigateForward(['/otp']);
    }

  }

  setLanguage(lng) {
    this.genericService.setItem(LNG_KEY, lng);
    if (lng === 'ar') {
      this.genericService.setItem('textDir', 'rtl');
    } else {
      this.genericService.setItem('textDir', 'ltr');
    }
    this.translate.use(lng);
  }


}

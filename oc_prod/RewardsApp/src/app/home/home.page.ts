import { Customer } from './../classes/customer';
import { User } from './../classes/user';
import { Header } from './../classes/header';
import { Constants } from './../enums/constants.enum';
import { LoyaltyInquiry } from './../classes/LoyaltyInquiry';
import { MemberShip } from './../classes/member-ship';
import { UrlEnums } from './../enums/urlenums.enum';
import { HttpHandlerService } from './../services/http-handler.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { IonSlides, Platform, NavController } from '@ionic/angular';
import { GenericService } from '../services/generic.service';
import { ActivatedRoute, Router, RouterOutlet, ActivationStart } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { StatusBar } from '@ionic-native/status-bar/ngx';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage implements OnInit {

  @ViewChild('slideWithNav') slideWithNav: IonSlides;
  @ViewChild(RouterOutlet) outlet: RouterOutlet;

  sliderOne: any;

  // circle parameter
  public n = 1;
  current = 750;
  max = 120;
  stroke = 10;
  radius = 125;
  semicircle = false;
  rounded = false;
  responsive = false;
  clockwise = true;
  color = '#f5ce23';
  background = '#eaeaea';
  duration = 8;
  animation = 'easeOutCubic';
  animationDelay = 0;
  animations: string[] = [];
  gradient = false;

  // Configuration for each Slider
  slideOptsOne: any;
  loyaltyInquiryObject: LoyaltyInquiry;
  headerObject: Header;
  loyaltyEnquiryResponse: any;
  memberShip: MemberShip;
  user: User;
  customer: Customer;
  cardNumber: string;
  loyaltyPoints: string;
  userName: string;
  phoneNumber: string;
  tierName: string;
  points: string;
  optBaseUrl = UrlEnums.BASE_URL;
  bannersResponse: any[] = [];
  bannersApiResponse;
  textDir: string;
  backbuttonSubscription;
  pointsToReachNextLevel;
  tierDetails;
  progressBarValue = 0.041;
  showProgress = false;
  currentTierName: string;
  nextTierName: string;
  cardImageURL = 'assets/images/card.jpg';
  breatheUrl = 'assets/images/breathe.png';
  breatheIOSUrl = 'assets/images/breathe.PNG';
  offerDetailResponse;
  skeletonSlideOpt;
  topImageResponse;
  topImage;
  currency;
  sideimage = 'assets/images/background_image.jpg';
  couponRequestObject;
  offersResponse;
  companyImageUrl;
  localitiesArray: any[] = [];
  storeInquiryObject;
  storeResponse;
  countriesArray: any[] = [];
  filterObject: any;
  citiesArray: any[] = [];
  isMobile = true;
  portalBrandingResponse;
  cardbgcolor;
  cardtextcolor;
  constructor(
    private httpHandler: HttpHandlerService,
    private genericService: GenericService,
    private router: Router,
    private platform: Platform,
    public statusBar: StatusBar,
    private translationService: TranslateService,
    public navCtrl: NavController,
    private datePipe: DatePipe) {
    // this.cardNumber = this.genericService.getItem("cardNumber");
    if (this.platform.is('ios')) {
     this.breatheUrl = this.breatheIOSUrl;
    }

    this.portalBrandingResponse =  JSON.parse(this.genericService.getSessionItem('portalBranding'));
    if (this.portalBrandingResponse) {
    this.cardbgcolor = this.portalBrandingResponse.balanceCardThemeColor ?
     this.portalBrandingResponse.balanceCardThemeColor : this.portalBrandingResponse.themecolor;
    this.cardtextcolor =  this.portalBrandingResponse.balanceCardTextColor ?
     this.portalBrandingResponse.balanceCardTextColor : 'white';
    this.companyImageUrl = this.portalBrandingResponse.logoimage;
    this.genericService.setItem('companyLogo', this.companyImageUrl);
    }

    this.addSpaceForCardNumber();
    this.memberShip = new MemberShip();
    this.headerObject = new Header();
    this.loyaltyInquiryObject = new LoyaltyInquiry();
    this.user = new User();
    this.customer = new Customer();
    this.user = JSON.parse(this.genericService.getItem('user'));
    this.tierDetails =  JSON.parse(this.genericService.getItem('tierDetails'));
    this.slideOptsOne = {
      initialSlide: 0,
      slidesPerView: 1.3,
      speed: 1000,
      pager: false,
      autoplay: true
    };
    this.skeletonSlideOpt = {
      initialSlide: 0,
      slidesPerView: 1.4,
      speed: 1000,
      pager: false,
      autoplay: false,
    };
    // this.companyImageUrl = this.genericService.getItem('companyLogo');
    this.getFiltersFromOptculture();
    this.isMobile = this.genericService.ismobileWeb();
  }


  setInitialData() {
    const detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
    this.tierDetails =  JSON.parse(this.genericService.getItem('tierDetails'));
    this.userName = detailsObject.userName;
    this.phoneNumber = detailsObject.phoneNumber;
    if (this.tierDetails.tierName === 'Platinum Level') {
      this.pointsToReachNextLevel = 0;
      this.progressBarValue = 1;
    } else {
      this.pointsToReachNextLevel = this.tierDetails.nextTierMilestone - this.tierDetails.currentTierValue;
      // this.tierName = detailsObject.tierName?detailsObject.tierName:"SLIVER";
      this.progressBarValue =  parseInt(this.tierDetails.currentTierValue) /
      (parseInt(this.tierDetails.nextTierMilestone) + parseInt(this.tierDetails.currentTierValue));
    }
    this.currentTierName = this.tierDetails.tierName;
    this.nextTierName = this.tierDetails.nextTierName;
    console.log(this.progressBarValue);
    this.showProgress =  true;
    this.points = detailsObject.points ? detailsObject.points : '0';
    console.log(this.points);
    this.currency =  detailsObject.currency ? detailsObject.currency : '0';
  }

  ionViewWillEnter() {
    if ( this.genericService.getItem('fromNotification')) {
      this.getNotifications();
    }
    this.textDir = this.genericService.getItem('textDir');
    this.setInitialData();
    this.translationService.get('OTP_ALERT_TITLE').subscribe(t => {
     console.log(t);
    });
    this.getLoyalityDetails();
    if (!(this.genericService.getItem('tokenUpdated') === 'true') && this.genericService.getItem('deviceToken')) {
      this.genericService.updateToken();
    }
    if (this.slideWithNav) {
      this.slideWithNav.startAutoplay();
    }
    this.user = JSON.parse(this.genericService.getItem('user'));
    this.genericService.removeItem('fingerprintloginreq');
  }


  gotoOfferDetail(offer) {
    // this.httpHandler.httpGETRequest(UrlEnums.CMS_BASE_URL+
    //   UrlEnums.GET_OFFER_DETAIL_URL+"?id="+offer.ID
    //   )
    //   .subscribe(offerDetailResponse =>
    //   {
    //      this.offerDetailResponse = offerDetailResponse;
    //      if(this.offerDetailResponse.Status)
    //      {
    //        let offerdata = this.offerDetailResponse.Response
           this.navCtrl.navigateForward(['/offerdetail'], {state: {
            offerdata : JSON.stringify(offer),
            where: 'home'
          }});
      //    }

      // })

  }


  ionViewWillLeave() {
    if (this.slideWithNav) {
      this.slideWithNav.stopAutoplay();
    }
    this.genericService.removeItem('fromNotification');
    
    // this.backbuttonSubscription.unsubscribe();
  }
  setPointsCircle() {
    // for (let prop in this._ease) {
    //   if (prop.toLowerCase().indexOf('ease') > -1)
    //   {
    //     this.animations.push(prop);
    //   }
    // }
  }

  addSpaceForCardNumber() {
    const cardNumber = this.genericService.getItem('cardNumber');
    let trimmed = cardNumber.replace(/\s+/g, '');
    if (trimmed.length > 16) {
      trimmed = trimmed.substr(0, 16);
    }

    const numbers = [];
    for (let i = 0; i < trimmed.length; i += 4) {
      numbers.push(trimmed.substr(i, 4));
    }

    this.cardNumber = numbers.join(' ');
    console.log(this.cardNumber);
  }

  ngOnInit() {
    // this.bannersResponse =  this.route.snapshot.data.bannners.Response;
    this.router.events.subscribe(e => {
      if (e instanceof ActivationStart && e.snapshot.outlet === 'administration') {
        this.outlet.deactivate();
      }
    });
    console.log(this.bannersResponse);
    // this.getOfferBanners();
    this.getOptOffers();
    // this.regiterToken();
  }



  async getOptOffers() {
	
    const loading = await this.genericService.getLoading();
    loading.present();
    this.couponRequestObject = {
     lookup: {
       emailAddress: '',
       phone: this.genericService.getItem('mobileNumber'),
       membershipNumber: this.genericService.getItem('cardNumber'),
       lastFetchedTime: '2010-01-01 12:00:00 GMT-5:30',
       country: '',
       cityName: '',
       locality: '',
       brandName: '',
       storeName: '',
       subsidiaryNumber: ''
     }
   };
   // console.log(this.datePipe.transform(new Date() , 'yyyy-MM-dd hh:mm:ss z'))

    this.couponRequestObject.header = this.genericService.prepareHeaderObject();
    this.couponRequestObject.user =  this.user;
  //  this.offersUrl =  UrlEnums.BASE_URL+UrlEnums.COUPON_HISTORY_URL;
    this.httpHandler.httpPostRequestWithBody(
     UrlEnums.BASE_URL + UrlEnums.COUPON_HISTORY_URL, this.couponRequestObject)
     .subscribe(offersResponse => {
       loading.dismiss();
       this.genericService.setItem('lastFecthedTime', this.datePipe.transform(new Date() , 'yyyy-MM-dd hh:mm:ss z'));
       this.offersResponse = offersResponse;
        // this.bannersResponse =  JSON.parse(this.genericService.getItem("offers"));
       const newOffers = this.offersResponse.matchedCustomers[0].couponsNew;
       const modifiedOffers = this.offersResponse.matchedCustomers[0].couponsModified;
       const deactivateOffers = this.offersResponse.matchedCustomers[0].couponsDeactivated;
       // tslint:disable-next-line: prefer-for-of
       for (let i = 0; i < newOffers.length; i++) {
         this.bannersResponse.push(newOffers[i]);
       }
       // tslint:disable-next-line: prefer-for-of
       for (let i = 0; i < modifiedOffers.length; i++) {
         this.bannersResponse.push(modifiedOffers[i]);
       }

      //  this.genericService.setItem("offers",JSON.stringify(this.bannersResponse))
       this.setSlides();
      //  console.log(this.allOffers);
      //  if(this.allOffers && this.allOffers.length>0)
      //  {
      //    this.showNoCoupons = true;
      //  }
     }, err => {
       loading.dismiss();
     });
  }

  async getFiltersFromOptculture() {
  const loading = await this.genericService.getLoading();
  loading.present();
  this.storeInquiryObject = {
    inquiryType: 'filters',
    sortBy: {
      type: '',
      order: ''
    },
    storeInquiry: {
      country: '',
      city: '',
      locality: '',
      zipCode: '',
      storeName: '',
      storeId: ''
    },
    filterBy: {
      brand: '',
      subsidiaryId: '',
      subsidiaryName: ''
    }
  };
  this.storeInquiryObject.header = this.genericService.prepareHeaderObject();
  this.storeInquiryObject.user = this.user;
  this.httpHandler.httpPostRequestWithBody(UrlEnums.BASE_URL + UrlEnums.OPT_STORES_INQUIRY_URL, this.storeInquiryObject)
  .subscribe(storeResponse => {
     loading.dismiss();
     this.storeResponse = storeResponse;
    //  console.log(this.storeResponse)
     if (this.storeResponse.storeInfo) {
     const allCountries: any[] = this.storeResponse.storeInfo.country;

     // tslint:disable-next-line: prefer-for-of
     for (let i = 0; i < allCountries.length; i++) {
       this.countriesArray.push(allCountries[i].countryName);
       const allcities = allCountries[i].city;
       const cityList: any[] = [];
       // tslint:disable-next-line: prefer-for-of
       for (let j = 0; j < allcities.length; j++) {

           cityList.push(allcities[j].cityName);
           const allLocalities = allcities[j].locality;
           const localityList: any[] = [];
            // console.log(allLocalities);
           // tslint:disable-next-line: prefer-for-of
           for (let k = 0; k < allLocalities.length; k++) {
              localityList.push(allLocalities[k].localityName);

            }
           const localitiesCityWide = {
              city: allcities[j].cityName,
              localities: localityList
            };
           this.localitiesArray.push(localitiesCityWide);
        }
       const citiesCountryWide = {
          country: allCountries[i].countryName,
          cities: cityList
        };
       this.citiesArray.push(citiesCountryWide);
     }
    //  console.log(this.countriesArray)
    //  console.log(this.citiesArray)
    //  console.log(this.localitiesArray)
     this.filterObject = {
      countries: this.countriesArray,
      cities: this.citiesArray,
      localities: this.localitiesArray,
      brands: this.storeResponse.filter.brand
    };
     this.genericService.setItem('filterObject', JSON.stringify(this.filterObject));
  }
  } , err => {
    loading.dismiss();
  });
}

  async getLoyalityDetails() {
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
         this.memberShip = this.loyaltyEnquiryResponse.membership;
         if (this.loyaltyEnquiryResponse.matchedCustomers > 0) {
            this.userName = this.loyaltyEnquiryResponse.matchedCustomers[0].firstName;
            this.phoneNumber = this.loyaltyEnquiryResponse.matchedCustomers[0].phone;
          }
         this.genericService.setItem('companyLogo', this.loyaltyEnquiryResponse.additionalInfo.companyLogo);
         const pushvalue = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.pushNotifications;
        let pushNotifications;
        if (pushvalue && typeof pushvalue === 'string') {
              pushNotifications =  pushvalue.toLowerCase() === 'true';
            } else {
              pushNotifications = false;
            }
        if(this.genericService.getItem('homePageObject')) {
          const homepageObject = JSON.parse(this.genericService.getItem('homePageObject'));
          homepageObject.pushNotifications = pushNotifications;
          this.genericService.setItem('homePageObject' , JSON.stringify(homepageObject));
        } 
        const fingerPrintvalue = this.loyaltyEnquiryResponse.membership.fingerprintValidation;
        let isFingerprintEnable;
        if (fingerPrintvalue && typeof fingerPrintvalue === 'string') {
               isFingerprintEnable = this.loyaltyEnquiryResponse.membership.fingerprintValidation.toLowerCase() === 'true';
            } else {
              isFingerprintEnable = false;
            }
        let langauage = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.language;
        this.genericService.setItem('isFingerPrintEnable', isFingerprintEnable.toString());
         this.companyImageUrl = this.genericService.getItem('companyLogo');
       } else {
        //  alert(Constants.API_ERROR_MSG);
       }
      }, err => {
        loading.dismiss();
      });
  }


  setSlides() {
     // Item object for Nature
        this.sliderOne = {
          isBeginningSlide: true,
          isEndSlide: false,
          slidesItems: this.bannersResponse
        };

  }

  async loyaltyEnquiry(event) {
    const loading = await this.genericService.getLoading();
    loading.present();
    let email, mobileNumber = '';
    if (this.phoneNumber.includes('@')) {
       email = this.phoneNumber;
    } else {
       mobileNumber = this.phoneNumber;
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
    this.loyaltyInquiryObject.user = this.user;
    this.loyaltyInquiryObject.customer = this.customer;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.LOYALTY_ENQUIRY_URL, this.loyaltyInquiryObject)
      .subscribe(loyaltyEnquiryResponse => {
        loading.dismiss();
        console.log(loyaltyEnquiryResponse);
        this.loyaltyEnquiryResponse = loyaltyEnquiryResponse;
        if (this.loyaltyEnquiryResponse.status.errorCode === '0') {

           let userName: string;
           let phoneNumber: string;
           if (this.loyaltyEnquiryResponse.matchedCustomers.length > 0) {
                userName = this.loyaltyEnquiryResponse.matchedCustomers[0].firstName +
                ' ' + this.loyaltyEnquiryResponse.matchedCustomers[0].lastName;
                phoneNumber = this.loyaltyEnquiryResponse.matchedCustomers[0].phone;
                email = this.loyaltyEnquiryResponse.matchedCustomers[0].emailAddress;
             }
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
           const pushvalue = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.pushNotifications;
           let pushNotifications;
           if (pushvalue && typeof pushvalue === 'string') {
               pushNotifications =  pushvalue.toLowerCase() === 'true';
             } else {
               pushNotifications = false;
             }

           const fingerPrintvalue = this.loyaltyEnquiryResponse.membership.fingerprintValidation;
           let isFingerprintEnable;
           if (fingerPrintvalue && typeof fingerPrintvalue === 'string') {
                isFingerprintEnable = this.loyaltyEnquiryResponse.membership.fingerprintValidation.toLowerCase() === 'true';
             } else {
               isFingerprintEnable = false;
             }
           let langauage = this.loyaltyEnquiryResponse.matchedCustomers[0].mobileAppPreferences.language;
           this.genericService.setItem('isFingerPrintEnable', isFingerprintEnable.toString());
           langauage = langauage ? langauage : 'en';
           this.genericService.setItem('points', points);
           const homePageObject = {
               userName,
               phoneNumber : this.phoneNumber,
               tierName,
               points,
               currency,
               pushNotifications,
               langauage,
               cardNumber: this.memberShip.cardNumber,
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
           this.genericService.setItem('tierDetails', JSON.stringify(tierDetails));
           this.genericService.setItem('homePageObject' , JSON.stringify(homePageObject));
           console.log(this.loyaltyEnquiryResponse.additionalInfo.companyLogo);
           this.genericService.setItem('companyLogo', this.loyaltyEnquiryResponse.additionalInfo.companyLogo);
          //  this.companyImageUrl = this.genericService.getItem('companyLogo');
           this.setInitialData();
           event.target.complete();
             // this.router.navigateByUrl('app/tabs/home');

        } else {
          alert(this.loyaltyEnquiryResponse.status.message);
        }
      } , err => {
        loading.dismiss();
      });
  }
  // Move to Next slide
  slideNext(object, slideView) {
    slideView.slideNext(500).then(() => {
      this.checkIfNavDisabled(object, slideView);
    });
  }

  // Move to previous slide
  slidePrev(object, slideView) {
    slideView.slidePrev(500).then(() => {
      this.checkIfNavDisabled(object, slideView);
    });
  }

  // Method called when slide is changed by drag or navigation
  SlideDidChange(object, slideView) {
    this.checkIfNavDisabled(object, slideView);
  }

  // Call methods to check if slide is first or last to enable disbale navigation
  checkIfNavDisabled(object, slideView) {
    this.checkisBeginning(object, slideView);
    this.checkisEnd(object, slideView);
  }

  checkisBeginning(object, slideView) {
    slideView.isBeginning().then((istrue) => {
      object.isBeginningSlide = istrue;
    });
  }
  checkisEnd(object, slideView) {
    slideView.isEnd().then((istrue) => {
      object.isEndSlide = istrue;
    });
  }

  getOverlayStyle() {
    const isSemi = this.semicircle;
    const transform = (isSemi ? '' : 'translateY(-50%) ') + 'translateX(-50%)';

    return {
      top: isSemi ? 'auto' : '50%',
      bottom: isSemi ? '5%' : 'auto',
      left: '40%',
      transform,
      '-moz-transform': transform,
      '-webkit-transform': transform,
      'font-size':  '15px'
    };
  }

  getNotifications() {
    this.router.navigateByUrl('/notifications');
  }

  goToOffers() {
    this.router.navigateByUrl('/app/tabs/tab2');
  }

  goToNudges() {
    this.router.navigateByUrl('/viewRewardsStatus');
  }
}

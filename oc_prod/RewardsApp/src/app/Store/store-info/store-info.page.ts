import { Platform, NavController } from '@ionic/angular';
import { HttpHandlerService } from './../../services/http-handler.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { GenericService } from 'src/app/services/generic.service';
import { User } from 'src/app/classes/user';
import { UrlEnums } from 'src/app/enums/urlenums.enum';

declare var launchnavigator;
@Component({
  selector: 'app-store-info',
  templateUrl: 'store-info.page.html',
  styleUrls: ['store-info.page.scss'],
})
export class StoreInfoPage implements OnInit {

  city;
  brand;
  storeInfoResponse;
  storeInfo;
  showNoStoreInfo = false;
  backbuttonSubscription;
  imageURL = 'assets/images/nike.jpg ';
  textDir;
  offers;
  couponRequestObject;
  user;
  offersResponse;
  storesOffers: any[] = [];
  constructor(public route: ActivatedRoute,
              public platform: Platform,
              public navCtrl: NavController,
              public genericService: GenericService,
              private router: Router,
              private httpHandler: HttpHandlerService) {
      this.user = new User();
      this.user = JSON.parse(this.genericService.getItem('user'));
//     this.route.params.subscribe(params => {
//       console.log(params);
//       this.city = params['city'];
//       this.brand = params['brand'];
//  });
}

  ngOnInit() {
    this.textDir = this.genericService.getItem('textDir');
    const params = this.router.getCurrentNavigation().extras.state;
    if (params) {
      this.storeInfo = JSON.parse(params.storeInfo);
      console.log(this.storeInfo);
      this.getOptOffers();
    }
      // if(this.storeInfo)
      // {
      //   this.offers =  this.storeInfo.offers;
      // }
    //  this.getStoreInfo()
  }

  gotoOfferDetail(offer) {
   this.navCtrl.navigateForward(['/offerdetail' ], { state: {
    offerdata : JSON.stringify(offer),
    where: 'storeinfo'
  }});
  }

  getOptOffers() {
    this.couponRequestObject = {
     lookup: {
       emailAddress: '',
       phone: '',
       membershipNumber: this.genericService.getItem('cardNumber'),
       lastFetchedTime: '2014-06-16 12:00:00 GMT-5:30',
       country: '',
       cityName: '',
       locality: '',
       brandName: '',
       storeNumber: this.storeInfo.storeId,
       subsidiaryNumber: ''
     }
   };

    this.couponRequestObject.header = this.genericService.prepareHeaderObject();
    this.couponRequestObject.user =  this.user;
    this.httpHandler.httpPostRequestWithBody(
     UrlEnums.BASE_URL + UrlEnums.COUPON_HISTORY_URL, this.couponRequestObject)
     .subscribe(offersResponse => {
        this.offersResponse = offersResponse;
        if (this.offersResponse.matchedCustomers.length > 0) {
          const newOffers = this.offersResponse.matchedCustomers[0].couponsNew;
          const modifiedOffers = this.offersResponse.matchedCustomers[0].couponsModified;
          const deactivateOffers = this.offersResponse.matchedCustomers[0].couponsDeactivated;
          // tslint:disable-next-line: prefer-for-of
          for (let i = 0; i < newOffers.length; i++) {
            this.storesOffers.push(newOffers[i]);
          }
          // tslint:disable-next-line: prefer-for-of
          for (let i = 0; i < modifiedOffers.length; i++) {
            this.storesOffers.push(modifiedOffers[i]);
          }
        }

        console.log(this.storesOffers);
     });
  }


  ionViewWillEnter() {
    this.hardWarebackButtonEvent();
  }

  hardWarebackButtonEvent() {
    this.backbuttonSubscription = this.platform.backButton.subscribe(async () => {
      this.navCtrl.navigateBack('/app/tabs/tab4');
    });
  }

  ionViewWillLeave() {
    this.backbuttonSubscription.unsubscribe();
  }

  getDirections() {
    const fullurl: string = window.location.href;
      navigator.geolocation.getCurrentPosition(res => {
          console.log( res.coords.latitude, res.coords.longitude);
          if (this.platform.is('cordova')) {
            launchnavigator.navigate([this.storeInfo.latitude, this.storeInfo.longitude])
          } else {
            this.navigateInBrowser();
          }
        }, (err) => {
          this.navigateInBrowser();
        });
  }

  navigateInBrowser() {
    window.open('https://www.google.com/maps?saddr=' + this.storeInfo.latitude + ','
    + this.storeInfo.longitude + '&daddr=' + this.storeInfo.latitude + ',' + this.storeInfo.longitude, '_system');
  }

  backButtonEvent() {
    this.navCtrl.pop();
  }


}

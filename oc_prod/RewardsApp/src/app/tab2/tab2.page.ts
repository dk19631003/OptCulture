import { TranslateService } from '@ngx-translate/core';
import { SortPage } from './../Store/sort/sort.page';
import { FilterPage } from './../Store/filter/filter.page';
import { NavController, ModalController, IonInfiniteScroll, Platform, Events } from '@ionic/angular';
import { PurchaseHistory } from './../classes/purchase-history';
import { LookUp } from './../classes/lookup';
import { UrlEnums } from './../enums/urlenums.enum';
import { Header } from './../classes/header';
import { HttpHandlerService } from './../services/http-handler.service';
import { Component, ViewChild, OnInit } from '@angular/core';
import { GenericService } from '../services/generic.service';
import { User } from '../classes/user';
import { RouterOutlet, Router, ActivationStart } from '@angular/router';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-tab2',
  templateUrl: 'tab2.page.html',
  styleUrls: ['tab2.page.scss']
})
export class Tab2Page implements OnInit {
  @ViewChild(RouterOutlet) outlet: RouterOutlet;
  @ViewChild(IonInfiniteScroll) infiniteScroll: IonInfiniteScroll;
    headerObject: Header;
    lookup: LookUp;
    user: User;
    purchaseHistory: PurchaseHistory;
    purchaseHistoryResponse;
    coupons: any[] = [];
    showNoOffers = false;
    textDir: string;
    offersResponse;
    showNoCoupons = false;
    notifications;
    countryName;
    cityName;
    brands: any[] = [];
    brand;
    orderType;
    order = 'DESC';
    locality: string;
    jsonParam: any;
    pagingInfo;
    offersUrl;
    offerspagingResponse: any;
    offers: any[] = [];
    show = false;
    loadDatamsg;
    couponRequestObject;
    allOffers: any[] = [];
    baseURL =  UrlEnums.BASE_URL;
    canunsubscribe;
    offerType: string;
    ismobile = this.genericService.ismobileWeb();
     constructor(private httpHandler: HttpHandlerService,
                 public router: Router,
                 public navCtrl: NavController,
                 public modalCtrl: ModalController,
                 public translationService: TranslateService,
                 public platform: Platform,
                 public statusBar: StatusBar,
                 private datePipe: DatePipe,
                 public events: Events,
                 public genericService: GenericService) {
      this.textDir = this.genericService.getItem('textDir');
      this.headerObject = new Header();
      this.purchaseHistory = new PurchaseHistory();
      this.lookup = new LookUp();
      this.user = new User();
      this.user = JSON.parse(this.genericService.getItem('user'));
     }

     subscribeforfilterResult() {
       this.events.subscribe('filter', returneddata => {
       const  seatSelectedModalReturnedData = returneddata ;
       if (!seatSelectedModalReturnedData.isBackButton) {
          this.countryName = seatSelectedModalReturnedData.country;
          this.cityName = seatSelectedModalReturnedData.city;
          this.locality = seatSelectedModalReturnedData.locality;
          this.brands = seatSelectedModalReturnedData.brands;

          const selectedBrands: any[] = [];
          // tslint:disable-next-line: prefer-for-of
          for (let i = 0; i < this.brands.length; i++) {
           if ( this.brands[i].isEntryChecked) {
           selectedBrands.push(this.brands[i].name);
           }
           }
          this.brand = selectedBrands.join();
          this.cityName = this.cityName ? this.cityName : '';
          this.countryName = this.countryName ? this.countryName : '';
          this.brand = this.brand ? this.brand : '';
          this.locality = this.locality ? this.locality : '';
         //  this.brand = this.brand? this.brand:""
          // this.getCMSOffers();
          this.getOptOffers();
        }
       this.canunsubscribe = true;
       });
     }

     subscribeforSortResult() {
      this.events.subscribe('sortby', returneddata => {
        const seatSelectedModalReturnedData = returneddata;
        this.orderType = seatSelectedModalReturnedData.orderType;
        this.order = seatSelectedModalReturnedData.order;
        this.orderType = this.orderType ? this.orderType : '';
        this.order =  this.order ? this.order : '';
        this.getOptOffers();
        this.canunsubscribe = true;
      });
     }

     goTofilter() {
      this.canunsubscribe = false;
      const brands =  this.brands ? JSON.stringify(this.brands) : '';
      this.navCtrl.navigateForward(['/filter'], {state: {
        country : this.countryName ,
        city: this.cityName,
        locality : this.locality,
        brands
      }});
     }

     goTOSort() {
      this.canunsubscribe = false;
      this.navCtrl.navigateForward(['/sort', {
        orderType : this.orderType ,
        order: this.order
      }]);
     }


    ionViewWillEnter() {
      this.subscribeforSortResult();
      this.subscribeforfilterResult();
      this.textDir = this.genericService.getItem('textDir');
      this.translationService.get('loaddata').subscribe(t => {
        this.loadDatamsg = t;
       });
       this.user = JSON.parse(this.genericService.getItem('user'));
    }

     ngOnInit() {
      this.router.events.subscribe(e => {
        if (e instanceof ActivationStart && e.snapshot.outlet === 'administration') {
          this.outlet.deactivate();
        }
      });
      this.cityName = this.cityName ? this.cityName : '';
      this.brand = this.brands[0];
      this.countryName = this.countryName ? this.countryName : '';
      this.locality = this.locality ? this.locality : '';
      this.brand = this.brand ? this.brand : '';
      this.orderType = this.orderType ? this.orderType : '';
      this.order =  this.order ? this.order : '';
      //  this.getCMSOffers();
      this.getOptOffers();
      // this.getAllNudgets();
     }


     getCoupon(couponCode) {
       alert(couponCode);
     }
     getAllCoupons() {
      this.headerObject = this.genericService.prepareHeaderObject();
      const detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
      const phoneNumber = detailsObject.phoneNumber;
      this.lookup.membershipNumber = '';
      this.lookup.phone = phoneNumber;
      this.lookup.emailAddress = '';
      this.purchaseHistory.header = this.headerObject;
      this.purchaseHistory.lookup = this.lookup;
      this.purchaseHistory.user = this.user;
      this.httpHandler.httpPostRequestWithBody(
        UrlEnums.BASE_URL + UrlEnums.COUPON_HISTORY_URL, this.purchaseHistory)
        .subscribe(purchaseHistoryResponse => {
          console.log(purchaseHistoryResponse);
          this.purchaseHistoryResponse = purchaseHistoryResponse;
          if (this.purchaseHistoryResponse.status.errorCode === '0') {
            this.purchaseHistoryResponse = purchaseHistoryResponse;
            if (this.purchaseHistoryResponse.matchedCustomers.length > 0) {
                this.coupons = this.purchaseHistoryResponse.matchedCustomers[0].coupons;
            } else {
              this.showNoOffers = true;
            }
         } else {
          alert(this.purchaseHistoryResponse.status.message);
         }
        });
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
          country: this.countryName,
          city: this.cityName,
          locality: this.locality,
          brandName: this.brand,
          storeName: '',
          subsidiaryNumber: '' ,
          sortType: this.orderType,
	  sortOrder: this.order
        }
      };
      // console.log(this.datePipe.transform(new Date() , 'yyyy-MM-dd hh:mm:ss z'))

      this.couponRequestObject.header = this.genericService.prepareHeaderObject();
      this.couponRequestObject.user =  this.user;
      this.offersUrl =  UrlEnums.BASE_URL + UrlEnums.COUPON_HISTORY_URL;
      this.httpHandler.httpPostRequestWithBody(
        UrlEnums.BASE_URL + UrlEnums.COUPON_HISTORY_URL, this.couponRequestObject)
        .subscribe(offersResponse => {
          this.genericService.setItem('lastFecthedTime', this.datePipe.transform(new Date() , 'yyyy-MM-dd hh:mm:ss z'));
          this.offersResponse = offersResponse;
          if (this.offersResponse.status.errorCode === '0') {
             this.allOffers = [];
             const newOffers = this.offersResponse.matchedCustomers[0].couponsNew;
             const modifiedOffers = this.offersResponse.matchedCustomers[0].couponsModified;
             const deactivateOffers = this.offersResponse.matchedCustomers[0].couponsDeactivated;
             // tslint:disable-next-line: prefer-for-of
             for (let i = 0; i < newOffers.length; i++) {
              this.allOffers.push(newOffers[i]);
            }
             // tslint:disable-next-line: prefer-for-of
             for (let i = 0; i < modifiedOffers.length; i++) {
              this.allOffers.push(modifiedOffers[i]);
            }
             if (this.allOffers && this.allOffers.length > 0) {
             this.showNoCoupons = true;
           }
           }
          loading.dismiss();
        }, err => {
          loading.dismiss();
        });
     }

     changeType() {

     }
     gotoOfferDetail(offer) {
      this.navCtrl.navigateForward(['/offerdetail'], {state: {
        offerdata : JSON.stringify(offer),
        where: 'offers'
      }});
     }

     openOfferURL(url) {
       window.open(url);
     }

     async  openFilterModal() {
      const selecteddata = {
        country : this.countryName ,
        city: this.cityName,
        locality : this.locality,
        brands : this.brands
      };
      const modal = await this.modalCtrl.create({
        component: FilterPage,
        componentProps : selecteddata
      });
      await modal.present();
      modal.onDidDismiss().then(data => {
      console.log(data);
      if (data.data) {

        const seatSelectedModalReturnedData = data.data;
        if (!seatSelectedModalReturnedData.isBackButton) {
          this.countryName = seatSelectedModalReturnedData.country;
          this.cityName = seatSelectedModalReturnedData.city;
          this.locality = seatSelectedModalReturnedData.locality;
          this.brands = seatSelectedModalReturnedData.brands;

          const selectedBrands: any[] = [];
          // tslint:disable-next-line: prefer-for-of
          for (let i = 0; i < this.brands.length; i++) {
           if ( this.brands[i].isEntryChecked) {
           selectedBrands.push(this.brands[i].name);
           }
           }
          this.brand = selectedBrands.join();
          this.cityName = this.cityName ? this.cityName : '';
          this.countryName = this.countryName ? this.countryName : '';
          this.brand = this.brand ? this.brand : '';
          this.locality = this.locality ? this.locality : '';
         //  this.brand = this.brand? this.brand:""
          // this.getCMSOffers();
          this.getOptOffers();
        }

      }
      });
    }

    async  openSortModal() {
      const selectedorderdata = {
        orderType : this.orderType ,
        order: this.order
      };
      const modal = await this.modalCtrl.create({
        component: SortPage,
        componentProps : selectedorderdata
      });
      await modal.present();
      modal.onDidDismiss().then(data => {
      console.log(data);
      if (data.data) {
        const seatSelectedModalReturnedData = data.data;
        this.orderType = seatSelectedModalReturnedData.orderType;
        this.order = seatSelectedModalReturnedData.order;
        this.orderType = this.orderType ? this.orderType : '';
        this.order =  this.order ? this.order : '';
        this.getOptOffers();
      }
      });
    }

    loadData(event) {
      setTimeout(() => {
        console.log('Done');
        if (this.pagingInfo) {
          this.jsonParam.pagingInfo =   this.pagingInfo;
          this.httpHandler.httpGETRequest(this.offersUrl + '?jsonParam=' + JSON.stringify(this.jsonParam))
          .subscribe(storepagingResponse => {
            this.offerspagingResponse = storepagingResponse;
            this.pagingInfo = this.offerspagingResponse.PagingInfo ?   encodeURIComponent(this.offerspagingResponse.PagingInfo) : null;
            if (this.offerspagingResponse.Status) {
              const pagingstores = this.offerspagingResponse.Response;
              this.offers = this.offers.concat(pagingstores);
            }
            event.target.complete();
          });
        }
        // App logic to determine if all data is loaded
        // and disable the infinite scroll
        if (this.offers.length === 100 || !this.pagingInfo) {
          event.target.disabled = true;
        }
      }, 500);
    }
}

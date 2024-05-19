import { TranslateService } from '@ngx-translate/core';
import { UrlEnums } from './../../enums/urlenums.enum';
import { HttpHandlerService } from './../../services/http-handler.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Route, Router, RouterOutlet, ActivationStart } from '@angular/router';
import { NavController, ModalController, IonInfiniteScroll, Platform, Events } from '@ionic/angular';
import { FilterPage } from '../filter/filter.page';
import { SortPage } from '../sort/sort.page';
import {  HttpClient } from '@angular/common/http';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { GenericService } from 'src/app/services/generic.service';
import { User } from 'src/app/classes/user';

@Component({
  selector: 'app-countries',
  templateUrl: 'countries.page.html',
  styleUrls: ['countries.page.scss'],
})
export class CountriesPage implements OnInit {

  @ViewChild(RouterOutlet) outlet: RouterOutlet;
  @ViewChild(IonInfiniteScroll) infiniteScroll: IonInfiniteScroll;
  countriesResponse: any;
  countries;
  groupedCountries = [];
  showNoCOuntries = false;
  storeInfoResponse;
  storeInfo;
  showNoStoreInfo;
  stores;
  countryName = '';
  cityName = '';
  brands: any[] = [];
  brand;
  orderType;
  order;
  locality = '';
  storeResponse;
  pagingInfo;
  storeUrl;
  storepagingResponse;
  jsonParam: any ;
  loadDatamsg;
  textDir;
  storeInquiryObject;
  allStores: any[] = [];
  user;
  allCountriesAndCities: any;
  countriesArray: any[] = [];
  citiesArray: any[] = [];
  localitiesArray: any[] = [];
  filterObject: any;
  canunsubscribe = true ;
  isMobile = false;
  constructor(private httpHandler: HttpHandlerService,
              public navCtrl: NavController,
              public http: HttpClient,
              public modalCtrl: ModalController,
              public translationService: TranslateService,
              public platform: Platform,
              public statusBar: StatusBar,
              public genericService: GenericService,
              public events: Events,
              private router: Router) {
      this.isMobile = this.platform.is('mobile');
      this.user = new User();
      this.user = JSON.parse(this.genericService.getItem('user'));
      this.allCountriesAndCities = {

      };
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
         this.getStoresFromOptculture();
         this.canunsubscribe = true;
       }
       });
     }

     subscribeforSortResult() {
      this.events.subscribe('sortby', returneddata => {
        const seatSelectedModalReturnedData = returneddata;
        this.orderType = seatSelectedModalReturnedData.orderType;
        this.order = seatSelectedModalReturnedData.order;
        this.orderType = this.orderType ? this.orderType : '';
        this.order =  this.order ? this.order : '';
        this.getStoresFromOptculture();
        this.canunsubscribe = true;
      });
     }

     goTofilter() {
       this.canunsubscribe = false;
       const brands =  this.brands ? JSON.stringify(this.brands) : '';
       this.navCtrl.navigateForward(['/filter'], { state: {
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
    this.getStoresFromOptculture();
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

ionViewWillLeave() {
    if (this.canunsubscribe) {
      this.events.unsubscribe('filter');
      this.events.unsubscribe('sortby');
    }
  }

  loadData(event) {
    setTimeout(() => {
      console.log('Done');
      if (this.pagingInfo) {
        this.jsonParam.pagingInfo =   this.pagingInfo;
        this.httpHandler.httpGETRequest(this.storeUrl + '?jsonParam=' + JSON.stringify(this.jsonParam))
        .subscribe(storepagingResponse => {
          this.storepagingResponse = storepagingResponse;
          this.pagingInfo = this.storepagingResponse.PagingInfo ?   encodeURIComponent(this.storepagingResponse.PagingInfo) : null;
          if (this.storepagingResponse.Status) {
            const pagingstores = this.storepagingResponse.Response;
            this.stores = this.stores.concat(pagingstores);
          }
          event.target.complete();
        });
      }
      // App logic to determine if all data is loaded
      // and disable the infinite scroll
      if (  !this.pagingInfo || this.stores.length === 500) {
        event.target.disabled = true;
      }
    }, 500);
  }

  groupContacts(contacts) {

    const sortedContacts = contacts.sort();
    let currentLetter = false;
    let currentContacts = [];

    sortedContacts.forEach((value, index) => {

        if (value.charAt(0) !== currentLetter) {

            currentLetter = value.charAt(0);

            const newGroup = {
                letter: currentLetter,
                contacts: []
            };

            currentContacts = newGroup.contacts;
            this.groupedCountries.push(newGroup);

        }

        currentContacts.push(value);

    });

}



async getStoresFromOptculture() {
  const loading = await this.genericService.getLoading();
  loading.present();
  this.storeInquiryObject = {
    inquiryType: 'stores',
    sortBy: {
      type: this.orderType,
      order: this.order
    },
    storeInquiry: {
      country: this.countryName,
      city: this.cityName,
      locality: this.locality,
      zipCode: '',
      storeName: '',
      storeId: ''
    },
    filterBy: {
      brand: this.brand,
      subsidiaryId: '',
      subsidiaryName: ''
    }
  };
  this.storeInquiryObject.header = this.genericService.prepareHeaderObject();
  this.storeInquiryObject.user = this.user;
  this.httpHandler.httpPostRequestWithBody(UrlEnums.BASE_URL + UrlEnums.OPT_STORES_INQUIRY_URL, this.storeInquiryObject)
  .subscribe(storeResponse => {

     this.storeResponse = storeResponse;
    //  console.log(this.storeResponse)
     if (this.storeResponse.storeInfo) {
      this.allStores = [];
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
              const allStores = allLocalities[k].stores;
              // tslint:disable-next-line: prefer-for-of
              for (let l = 0; l < allStores.length; l++) {
                this.allStores.push(allStores[l]);
              }
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
    //  this.filterObject = {
    //    countries:this.countriesArray,
    //    cities:this.citiesArray,
    //    localities:this.localitiesArray
    //  }
    //  this.genericService.setItem("filterObject",JSON.stringify(this.filterObject));
      if (this.allStores.length <= 0) {
       this.showNoStoreInfo = true;
     }
    }
     console.log(this.allStores);
     loading.dismiss();
  }, err => {
    loading.dismiss();
  });
}

goToStoreInfo(storeInfo) {
  // this.httpHandler.httpGETRequest(
  //    UrlEnums.CMS_BASE_URL+
  //   UrlEnums.GET_STORE_INFO_URL+
  //   "?StoreID="+storeInfo.ID
  //   )
  // .subscribe(storeInfoResponse =>
  // {

  //    this.storeInfoResponse = storeInfoResponse;
  //    if(this.storeInfoResponse.Status)
  //    {
  //     console.log(storeInfo);
      this.navCtrl.navigateForward(['/store-info'], {state: {
        storeInfo: JSON.stringify(storeInfo)
      }, skipLocationChange: true});
  //    }

  //    if(this.stores.length<=0)
  //    {
  //      this.showNoStoreInfo = true;
  //    }
  // })

  // this.router.navigate(['/cities',countryName])
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
          this.getStoresFromOptculture();
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
        this.getStoresFromOptculture();
      }
      });
    }
}

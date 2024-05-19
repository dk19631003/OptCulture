import { GenericService } from './../../services/generic.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpHandlerService } from './../../services/http-handler.service';
import { HttpHandler } from '@angular/common/http';
import { UrlEnums } from './../../enums/urlenums.enum';
import { Component, OnInit } from '@angular/core';
import { ModalController, NavParams, NavController, Events, Platform } from '@ionic/angular';

@Component({
  selector: 'app-filter',
  templateUrl: './filter.page.html',
  styleUrls: ['./filter.page.scss'],
})
export class FilterPage implements OnInit {

  type;
  lines = 'none';
  countriesResponse;
  countries;
  showNoCOuntries;
  citiesResponse;
  cities;
  showNoCities;
  country;
  brandsResponse;
  brands;
  showNoBrands;
  city;
  modifiedBrands: any[] = [];
  selectedBrands: any[] = [];
  isSelected;
  locality;
  customPopoverOptions: any = {
    cssClass: 'custom-popover'
  };
  noText: string = null;
  localitiesResponse;
  localities;
  textDir;
  showNoLocalities;
  doNotAssignUndefined = false;
  filterobject;
  isMobile: boolean;
  constructor(public httpHandler: HttpHandlerService,
              // public navParams:NavParams,
              public route: ActivatedRoute,
              public navCtrl: NavController,
              public events: Events,
              private platform: Platform,
              private router: Router,
              public genericService: GenericService,
              public modalCtrl: ModalController) {
    this.type = 'Location';
    this.isMobile = this.platform.is('mobile');
   }

  ngOnInit() {
    this.textDir = this.genericService.getItem('textDir');
    const params = this.router.getCurrentNavigation().extras.state;
    if (params) {
    this.country = params.country;
    this.city = params.city;
    this.locality = params.locality ;
    this.selectedBrands = params.brands ? JSON.parse(params.brands) : [];
    this.doNotAssignUndefined = true;
    this.filterobject = JSON.parse(this.genericService.getItem('filterObject'));
    if (this.selectedBrands.length <= 0) {
       this.getBrands();
     } else {
       this.modifiedBrands =  JSON.parse(JSON.stringify(this.selectedBrands));
      }
    this.countries =  this.filterobject.countries;
    this.setSelected();
      //  this.getCOuntries();

    if (this.country) {
        this.getCitiesFromStoreInquiry(this.country);
      }
    if (this.country && this.city) {
        this.getLocalitiesFromStoreInquiry(this.city);
      }
    this.doNotAssignUndefined = false;
    this.setSelected();
    }

  }

   getCitiesFromStoreInquiry(country) {
    this.cities =  this.filterobject.cities.find(item => {
      return item.country === country;
   }).cities;
    this.setSelected();
  }

  getLocalitiesFromStoreInquiry(city) {
   this.localities =   this.filterobject.localities.find(item => {
      return item.city === city;
   }).localities;
   this.setSelected();
  }

  setSelected() {
    if (this.isNotEmpty(this.country) || this.isNotEmpty(this.city) 
    || this.isNotEmpty(this.locality) || this.isBrandSelected() ) {
      this.isSelected = true;
    } else {
      this.isSelected = false;
    }
  }

  isBrandSelected(){
    if(this.modifiedBrands && this.modifiedBrands.length>0) {
      const selected =  this.modifiedBrands.find(brand=> brand.isEntryChecked === true)
      if(selected) {
        return true;
      } else {
        return false;
      }
    } else {
      return false
    }
  }

  isNotEmpty(strvalue){
    return !(strvalue === "")
  }

  getBrands() {
    // this.httpHandler.httpGETRequest(
    //   UrlEnums.CMS_BASE_URL+UrlEnums.GET_BRANDS_URL+"?CityName=")
    //   .subscribe(brandsResponse =>
    //   {

          // this.httpHandler.getBrandsResponse().subscribe((brandsResponse )=>{
          //   this.brandsResponse = brandsResponse
          //   if(this.brandsResponse)
          //   {

                this.brands = this.filterobject.brands;

                if (this.brands.length >= 0) {
               this.brands.forEach(element => {
                  console.log(element);
                  const citiCheck = {
                  name: element,
                  isEntryChecked : false
                 };
                  this.modifiedBrands.push(citiCheck);
                });
              }
          //   }

          // });

      // })
  }

  clearAll() {
     this.country = undefined;
     this.city = undefined;
     this.locality =  undefined;
     // tslint:disable-next-line: prefer-for-of
     this.isSelected = false;
     for (let i = 0; i < this.modifiedBrands.length; i++) {
        this.modifiedBrands[i].isEntryChecked =  false;
     }
    
  }

  selectBrand() {
    for (let i = 0; i < this.modifiedBrands.length; i++) {
      if ( this.modifiedBrands[i].isEntryChecked) {
      this.selectedBrands.push(this.modifiedBrands[i].name);
      }
   }
    this.setSelected();
  }

  changeType(type) {
    this.type = type;
    if (this.type === 'Brand') {
      this.lines = 'full';
      if (this.modifiedBrands.length <= 0) {
        this.getBrands();
      }
    } else {
      this.lines = 'none';
    }
  }







  backButtonEvent() {
    const filterData = {
      isBackButton : true
    };
    this.events.publish('filter', filterData);
    this.navCtrl.pop();
  }


  dismiss() {
   const filterData = {
     country : this.country,
     city: this.city,
     locality : this.locality,
     brands: this.modifiedBrands,
     isBackButton : false
   };
   this.events.publish('filter', filterData);
   this.navCtrl.pop();
  }

}



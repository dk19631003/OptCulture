import { Platform } from '@ionic/angular';
import { UrlEnums } from './../../enums/urlenums.enum';
import { HttpHandlerService } from './../../services/http-handler.service';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-brands',
  templateUrl: 'brands.page.html',
  styleUrls: ['brands.page.scss'],
})
export class BrandsPage implements OnInit {

city;
brands;
groupedBrands = [];
brandsResponse;
showNoBrands = false;
backbuttonSubscription;
  constructor(public route: ActivatedRoute,
              public router: Router,
              public platform: Platform,
              private httpHandler: HttpHandlerService) {
    this.route.params.subscribe(params => {
      console.log(params);
      this.city = params.city;
 }); }

  ngOnInit() {
  }

  ionViewWillEnter() {
    this.hardWarebackButtonEvent();
  }

  hardWarebackButtonEvent()
  {
    this.backbuttonSubscription = this.platform.backButton.subscribe(async () =>
    {
      window.history.back();
    });
  }

  ionViewWillLeave() {
    this.backbuttonSubscription.unsubscribe();
  }

  backButtonEvent()
  {
    window.history.back();
  }



  groupBrands(contacts) {

    const sortedContacts = contacts.sort();
    let currentLetter = false;
    let currentContacts = [];

    sortedContacts.forEach((value, index) => {

        if (value.BrandName.charAt(0) !== currentLetter) {

            currentLetter = value.BrandName.charAt(0);

            const newGroup = {
                letter: currentLetter,
                contacts: []
            };

            currentContacts = newGroup.contacts;
            this.groupedBrands.push(newGroup);

        }

        currentContacts.push(value);

    });
}

goToStoreInfo(brand) {
  this.router.navigate(['/store-info', this.city, brand]);
}
}

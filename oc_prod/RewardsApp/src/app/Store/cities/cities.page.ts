import { Platform } from '@ionic/angular';
import { UrlEnums } from './../../enums/urlenums.enum';
import { HttpHandlerService } from './../../services/http-handler.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-cities',
  templateUrl: 'cities.page.html',
  styleUrls: ['cities.page.scss'],
})
export class CitiesPage implements OnInit {
  countryName;
  groupedCities = [];
  citiesResponse;
  cities;
  showNoCities = false;
  backbuttonSubscription;
  constructor(public route: ActivatedRoute,
              public router: Router,
              public platform: Platform,
              private httpHandler: HttpHandlerService) {
    this.route.params.subscribe(params => {
      console.log(params);
      this.countryName = params.countryName;
 });
   }

  ngOnInit() {
  }

  ionViewWillEnter() {
    this.hardWarebackButtonEvent();
  }

  hardWarebackButtonEvent() {
    this.backbuttonSubscription = this.platform.backButton.subscribe(async () => {
      window.history.back();
    });
  }

  ionViewWillLeave() {
    this.backbuttonSubscription.unsubscribe();
  }

  backButtonEvent() {
    window.history.back();
  }

  groupCities(contacts) {

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
            this.groupedCities.push(newGroup);

        }

        currentContacts.push(value);

    });

}

goToBrands(city) {
  this.router.navigate(['/brands', city]);
}
}

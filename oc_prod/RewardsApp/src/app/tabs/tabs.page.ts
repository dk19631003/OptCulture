import { GenericService } from 'src/app/services/generic.service';
import { Component } from '@angular/core';
import { ToastController, Platform } from '@ionic/angular';
import { Router } from '@angular/router';
import { StatusBar } from '@ionic-native/status-bar/ngx';

@Component({
  selector: 'app-tabs',
  templateUrl: 'tabs.page.html',
  styleUrls: ['tabs.page.scss']
})
export class TabsPage {

  backbuttonSubscription;
  lastTimeBackPress ;
  timePeriodToExit = 2000;
  isHomeSelected = true;
  isOffersSelcted = false;
  isCheckInselected = false;
  isStoreSelected =  false;
  isProfileSelected = false;
  homeIcon = 'assets/images/home_blk.svg';
  offersIcon = 'assets/images/offer_blk.svg';
  cardIcon = 'assets/images/checkin_blk.svg';
  storeMaterialIcon = 'assets/images/store_blk.svg';
  profileIcon = 'assets/images/profile_blk.svg';
  offersClickIcon = 'assets/images/offer_blue.svg';
  homeClickIcon = 'assets/images/home_blue.svg';
  cardClickIcon = 'assets/images/checkin_blue.svg';
  storeClickIcon = 'assets/images/store_blue.svg';
  profileClickIcon = 'assets/images/profile_blue.svg';
  width = 'calc(calc(4 / var(--ion-grid-columns, 12)) * 100%)';
  constructor(public toast: ToastController,
              public router: Router,
              public statusBar: StatusBar,
              public genericService: GenericService,
              public platform: Platform) {
    if (this.platform.is('mobileweb') || this.platform.is('mobile') || this.platform.is('ios') || this.platform.is('android')) {
      this.width = '100%';
    }
  }
  textDir: string;
  ionViewWillEnter() {

    this.lastTimeBackPress = undefined;
    this.backButtonEvent();
    this.textDir = this.genericService.getItem('textDir');

  }

  changeIcons() {
    if (this.router.url === '/app/tabs/home') {
      this.changeHomeIcon();
    } else if (this.router.url === '/app/tabs/tab2') {
      this.changeOffersIcon();
    }

  }
  ionViewWillLeave() {
    this.backbuttonSubscription.unsubscribe();
  }


  ionTabsDidChange() {
    console.log('tab ionTabsDidChange');
    this.textDir = this.genericService.getItem('textDir');
  }

  changeOffersIcon() {
     this.resetAll();
     this.isOffersSelcted = true;
  }

  changeHomeIcon() {
    this.resetAll();
    this.isHomeSelected = true;
  }

  changeCheckInIcon() {
    this.resetAll();
    this.isCheckInselected = true;
  }

  changeStoreIcon() {
    this.resetAll();
    this.isStoreSelected = true;
  }
  changeProfileIcon() {
    this.resetAll();
    this.isProfileSelected = true;
  }
  resetAll() {
    this.isHomeSelected = false;
    this.isOffersSelcted = false;
    this.isCheckInselected = false;
    this.isStoreSelected =  false;
    this.isProfileSelected = false;
  }

  tabChanged() {
    // console.log("tab ionTabsWillChange");
    this.changeIcons();
    this.textDir = this.genericService.getItem('textDir');

  }

  backButtonEvent() {
    console.log('backButtonEvent');
    this.backbuttonSubscription = this.platform.backButton.subscribe(async () => {
      console.log(this.router.url);
      if (this.router.url === '/app/tabs/home') {
        console.log(new Date().getTime() - this.lastTimeBackPress);
        if (this.lastTimeBackPress &&
            (new Date().getTime() - this.lastTimeBackPress < this.timePeriodToExit)) {
              // this.platform.exitApp(); // Exit from app
               navigator['app'].exitApp(); // work in ionic 4

          } else {
            console.log('toast presented');
            this.lastTimeBackPress = new Date().getTime();
            this.presentToast();
          }
      } else {
        this.router.navigateByUrl('app/tabs/home');
        this.changeHomeIcon();
      }
    });
  }

  async presentToast() {
    const toast = await this.toast.
    create({
      message: 'Press back again to exit App.',
      duration: 2000,
      position: 'bottom'
    });

    toast.present();
  }
}

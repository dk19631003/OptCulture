import { SOURCE_TYPE } from './../enums/constants.enum';
import { HttpHandlerService } from './../services/http-handler.service';
import { UrlEnums } from './../enums/urlenums.enum';
import { Platform } from '@ionic/angular';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { GenericService } from '../services/generic.service';
import { User } from '../classes/user';
import { InAppBrowser } from '@ionic-native/in-app-browser/ngx';

@Component({
  selector: 'app-notifications',
  templateUrl: 'notifications.page.html',
  styleUrls: ['notifications.page.scss'],
})
export class NotificationsPage implements OnInit {

  backButtonSubscription;
  notifications: any[] = [];
  notificationsResponse;
  textDir: string;
  showNoNotifications = false;
  detailsObject;
  notificationReqObject;
  user;
  backbuttonSubscription;
  ismobileWeb = this.genericService.ismobileWeb();
  offset;
  canDisableInfinite = false;
  canScrollDown = true;
  modalScrollDistance = 2;
  modalScrollThrottle = 200;
  constructor(private platform: Platform,
              private httpHandler: HttpHandlerService,
              public statusBar: StatusBar,
              public genericService: GenericService,
              private iab: InAppBrowser,
              private router: Router) {
      this.textDir = this.genericService.getItem('textDir');
      this.user = new User();
      this.user = JSON.parse(this.genericService.getItem('user'));
      this.detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
    }

  hardWarebackButtonEvent() {
    this.backbuttonSubscription = this.platform.backButton.subscribe(async () => {
     this.backButtonEvent();
    });
  }

  loadData() {
    let pagingNotifications;
    this.offset = this.offset + 1;
    setTimeout(() => {
      this.notificationReqObject = {
        headerInfo: {
          requestId: this.genericService.prepareHeaderObject().requestId,
          sourceType : SOURCE_TYPE,
          requestType: 'application/json'
      },
      mobileNumber: this.detailsObject.phoneNumber,
      instanceId: this.genericService.getItem('deviceToken')?this.genericService.getItem('deviceToken') : '',
      offSet: this.offset
        };
      this.notificationReqObject.user = this.user;
      this.httpHandler.httpPostRequestWithBody(
       UrlEnums.BASE_URL + UrlEnums.OPT_NOTIFICATION_URL, this.notificationReqObject)
       .subscribe(notificationsResponse => {
         console.log(notificationsResponse);
         this.notificationsResponse = notificationsResponse;
         if (this.notificationsResponse.pushNotificationInfo) {
          pagingNotifications =  this.notificationsResponse.pushNotificationInfo;
          this.notifications = this.notifications.concat(pagingNotifications);
         } else {
          this.canDisableInfinite = this.notificationsResponse.pushNotificationInfo ? false : true ;
         }
        //  event.target.complete();
       });
      // App logic to determine if all data is loaded
      // and disable the infinite scroll
      // if (  this.canDisableInfinite  ) {
      //   event.target.disabled = true;
      // }
    }, 500);
  }

  ionViewWillEnter() {
    this.offset = 0;
    this.textDir = this.genericService.getItem('textDir');
    this.getOptNotifications();
    this.genericService.removeItem('fromNotification');
  }

  onScrollDown() {
    console.log('scrolldown');
    this.loadData();
  }

  onScrollUp() {
    console.log('scrolldown');
  }

  openNotificationInBrowser(notification) {
    if (!notification.redirectUrl.startsWith('http://') && !notification.redirectUrl.startsWith('https://')) {
      notification.redirectUrl = 'http://' + notification.redirectUrl;
    }
    if ((notification.redirectUrl && this.validURL(notification.redirectUrl)) || this.platform .is('ios')) {
      this.iab.create(notification.redirectUrl , '_blank', 'location=yes');
    }  
  }

  ionViewWillLeave() {
  this.backButtonSubscription.unsubscribe();
  }

  ngOnInit() {
  //  this.getOptNotifications();
  }

   validURL(str) {
    const pattern = new RegExp('^(https?:\\/\\/)?' + // protocol
      '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|' + // domain name
      '((\\d{1,3}\\.){3}\\d{1,3}))' + // OR ip (v4) address
      '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + // port and path
      '(\\?[;&a-z\\d%_.~+=-]*)?' + // query string
      '(\\#[-a-z\\d_]*)?$', 'i'); // fragment locator
    return !!pattern.test(str);
  }
  async getOptNotifications() {
    const loading = await this.genericService.getLoading();
    loading.present();
    this.notificationReqObject = {
        mobileNumber: this.detailsObject.phoneNumber,
        sourceType : SOURCE_TYPE,
         instanceId: this.genericService.getItem('deviceToken') ? this.genericService.getItem('deviceToken') : null,
          offSet: '0'
      };

    this.notificationReqObject.headerInfo = {
    requestId: this.genericService.prepareHeaderObject().requestId,
    requestType: 'application/json'
   };
    this.notificationReqObject.user = this.user;
  //  this.couponRequestObject.user =  this.user
  //  this.offersUrl =  UrlEnums.BASE_URL+UrlEnums.OPT_NOTIFICATION_URL;
    this.httpHandler.httpPostRequestWithBody(
     UrlEnums.BASE_URL + UrlEnums.OPT_NOTIFICATION_URL, this.notificationReqObject)
     .subscribe(notificationsResponse => {
       loading.dismiss();
       this.canDisableInfinite = false;
       console.log(notificationsResponse);
       this.notificationsResponse = notificationsResponse;
       if (this.notificationsResponse.statusInfo.errorCode === '0') {
        this.notifications = this.notificationsResponse.pushNotificationInfo;
        if(this.notifications && this.notifications.length<=0){
          this.showNoNotifications = true;
        }
       } else {
         this.showNoNotifications = true;
       }
     }, (err) => {
      loading.dismiss();
      this.showNoNotifications = true;
     });
  }

  backButtonEvent() {
    this.router.navigateByUrl('app/tabs/home');
    // this.platform.ready().then(()=>
    // {
    //   console.log("backbutton subscribed");
    //  this.backButtonSubscription =  this.platform.backButton.subscribe(() =>
    //   {
    //     console.log("clicked backbutton");
    //   //  this.navCtrl.goBack(false);
    //     this.navCtrl.navigateRoot('/poolFromSelect');
    //    // navigator['app'].exitApp();
    //   })
    // })
  }


}

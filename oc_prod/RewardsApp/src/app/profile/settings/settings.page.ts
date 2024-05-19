import { Platform, AlertController, LoadingController, NavController } from '@ionic/angular';
import { Constants, SOURCE_TYPE } from './../../enums/constants.enum';
import { UrlEnums } from './../../enums/urlenums.enum';
import { LookUp } from './../../classes/lookup';
import { Header } from './../../classes/header';
import { Customer } from './../../classes/customer';
import { ImportContact } from './../../classes/importContact';
import { DatePipe } from '@angular/common';
import { Keyboard } from '@ionic-native/keyboard/ngx';
import { GenericService } from 'src/app/services/generic.service';
import { HttpHandlerService } from './../../services/http-handler.service';
import { Component, OnInit, NgZone, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Report } from 'src/app/classes/report';
import { User } from 'src/app/classes/user';
import { UpdateContacts } from 'src/app/classes/updateContact';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { Observable } from 'rxjs';
import { AppVersion } from '@ionic-native/app-version/ngx';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.scss'],
})
export class SettingsPage implements OnInit , OnDestroy {

  selected;
  importContactResponse: any;
  importContactObject: ImportContact;
  isEditable = false;
  customer: Customer;
  headerObject: Header;
  report: Report;
  user: User;
  lookup: LookUp;
  updatedCustomer: UpdateContacts;
  updatedContactsResponse: any;
  isFingerPrintAvailable: boolean;
  langauage;
  pushNotifications: boolean ;
  textDir: string;
  password: string;
  show = false;
  passwordType = 'password';
  givenPasword: string;
  detailsObject;
  smsNotifications: boolean;
  emailNotifications: boolean;
  canUpdate = false;
  backbuttonSubscription;
  customPopoverOptions: any = {
    cssClass: 'custom-popover'
  };
  updateMsg;
  oktext: string;
  timeout: any;
  isMobile = false;
  version ='0.0.1'
  constructor(public httpHandler: HttpHandlerService,
              public router: Router,
              private translate: TranslateService,
              private genericService: GenericService,
              private keyboard: Keyboard,
              public alertController: AlertController,
              public platform: Platform,
              public statusBar: StatusBar,
              public loadingCtrl: LoadingController,
              public zone: NgZone,
              public route: ActivatedRoute,
              public navCtrl: NavController,
              public appVersion: AppVersion,
              public datePipe: DatePipe) {
      this.platform.ready().then(() => {
        if (this.platform.is('cordova')) {
          this.appVersion.getVersionNumber().then(versionnumber => {
            this.version = versionnumber;
          });
        }
      });
      this.isMobile = this.platform.is('mobile');
      this.customer = new Customer();
      this.headerObject = new Header();
      this.importContactObject =  new ImportContact();
      this.report = new Report();
      this.user = new User();
      this.lookup = new LookUp();
      this.updatedCustomer = new UpdateContacts();
      const params = this.router.getCurrentNavigation().extras.state;
      if (params) {
        this.canUpdate = false;
        this.importContactResponse =
        params.importcontactResponse ? JSON.parse(params.importcontactResponse) : params.importcontactResponse;
        this.customer = this.importContactResponse.matchedCustomers[0];

        this.emailNotifications = this.importContactResponse.matchedCustomers[0].suppress.email.isTrue === 'Y' ? false : true;
        this.smsNotifications = this.importContactResponse.matchedCustomers[0].suppress.phone.isTrue === 'Y' ? false : true;
        this.user = JSON.parse(this.genericService.getItem('user'));
        this.password =  this.genericService.getItem('password');
        this.givenPasword = this.genericService.getItem('password');
        this.textDir = this.genericService.getItem('textDir');
        this.langauage = this.genericService.getItem('SELECTED_LANGUAGE');
        this.detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
        console.log(this.detailsObject);
        this.pushNotifications = this.detailsObject.pushNotifications;
        if ( this.genericService.getItem('isFingerPrintEnable') &&
        this.genericService.getItem('isFingerPrintEnable') === 'true') {
          this.isFingerPrintAvailable = true;
        } else {
          this.isFingerPrintAvailable = false;
        }

        this.translate.get('updateMsg').subscribe(t => {
          this.updateMsg = t;
         });
        this.translate.get('ok').subscribe(t => {
          this.oktext = t;
         });
        this.canUpdate = true;
      }


      //  this.importContact();
     }

  ngOnInit() {
   this.backbuttonSubscription = this.platform.backButton.subscribe(async () => {
      this.backButtonEvent();
    });


  }

  ngOnDestroy() {
    clearTimeout(this.timeout);
  }

  ionViewWillLeave() {

    this.backbuttonSubscription.unsubscribe();
  }

  ionViewWillEnter() {


  }


  importContact() {
    this.headerObject = this.genericService.prepareHeaderObject();
    this.headerObject.pcFlag = 'false';
    this.report.contactList = '';
    this.report.contactSource = '';
    this.report.contactType = '';
    this.report.endDate = '';
    this.report.offset = 0;
    this.report.maxRecords = 0;
    this.report.store = '';
    this.report.startDate = '';
    this.lookup.emailAddress = '';
    this.lookup.membershipNumber =  this.genericService.getItem('cardNumber');
    this.lookup.phone = '';
    this.importContactObject.header = this.headerObject;
    this.importContactObject.importType = 'Lookup';
    this.importContactObject.lookup = this.lookup;
    this.importContactObject.report = this.report;
    this.importContactObject.user = this.user;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.IMPORT_CONTACT_URL, this.importContactObject)
      .subscribe(importContactResponse => {
        console.log(importContactResponse);
        this.importContactResponse = importContactResponse;

        if (this.importContactResponse.status.errorCode === '0') {
         this.customer = this.importContactResponse.matchedCustomers[0];
         this.pushNotifications = this.detailsObject.pushNotifications;
         this.emailNotifications = this.importContactResponse.matchedCustomers[0].suppress.email.isTrue === 'Y' ? false : true;
         this.smsNotifications = this.importContactResponse.matchedCustomers[0].suppress.phone.isTrue === 'Y' ? false : true;
        //  this.canUpdate = true;
         this.setCanUpdate();
       } else {
         alert(Constants.API_ERROR_MSG);
         this.pushNotifications = this.detailsObject.pushNotifications;
        //  this.canUpdate = true;;
         this.setCanUpdate();
       }
      });
  }

  setCanUpdate() {
    this.timeout = setTimeout(() => {
      this.canUpdate = true;
    }, 2000);
  }

  backButtonEvent() {
    this.router.navigateByUrl('/app/tabs/tab5');
  }

  callUpdate(key) {
    this.update(key).subscribe(res => {
      console.log(res);
      this.importContact();
    }, err => {
     console.log(err);
     this.importContact();
    });
  }

  update(key) {
    return new Observable((observe) => {
    if (this.canUpdate) {
    if (this.keyboard.isVisible) {
    this.keyboard.hide();
    }
    const currentTimeStamp =  this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');
    const headerObject = new Header();
    headerObject.requestId  = currentTimeStamp;
    headerObject.requestDate = currentTimeStamp;
    headerObject.contactSource = SOURCE_TYPE;
    headerObject.sourceType = SOURCE_TYPE;
    headerObject.contactList = 'List1';
    this.updatedCustomer.header = headerObject;
    this.updatedCustomer.user = this.user;
    this.customer.creationDate = currentTimeStamp;
    let modifiedPassword;
    if (this.password === this.givenPasword) {
      modifiedPassword = '';
    } else {
      modifiedPassword = this.genericService.encryptData(this.password);
    }
    const loyalty = {
      password : '' + modifiedPassword,
      mobileAppPreferences :
      {
       language : this.langauage,
       pushNotifications: this.pushNotifications ? 'true' : 'false'
      }
    };

    this.customer.loyalty = loyalty;

    this.customer.instanceId = this.genericService.getItem('deviceToken');
    this.customer.deviceType = this.genericService.getPlatformName();
    if (key === 'fingerprint') {
      this.customer.loyalty.fingerprintValidation = this.isFingerPrintAvailable ? 'True' : 'False';
    }
    if (key === 'sms') {
      this.customer.suppress.phone.isTrue = this.smsNotifications ? 'N' : 'Y';
      this.customer.suppress.phone.reason = 'Opted-out';
      this.customer.suppress.phone.timestamp = currentTimeStamp;
    } else {
      this.customer.suppress.phone.isTrue = '';
      this.customer.suppress.phone.reason = '';
      this.customer.suppress.phone.timestamp = currentTimeStamp;
    }
    if ( key === 'email') {
      this.customer.suppress.email.isTrue = this.emailNotifications ? 'N' : 'Y';
      this.customer.suppress.email.reason = this.customer.suppress.email.isTrue === 'Y' ? 'Unsubscribed' : '';
      this.customer.suppress.email.timestamp = currentTimeStamp;
    } else {
      this.customer.suppress.email.isTrue = '';
      this.customer.suppress.email.reason = '';
      this.customer.suppress.email.timestamp = currentTimeStamp;
    }
    this.updatedCustomer.customer = this.customer;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.UPDATE_CONTACT_URL, this.updatedCustomer)
      .subscribe(updatedContactsResponse => {
        console.log(updatedContactsResponse);
        this.updatedContactsResponse = updatedContactsResponse;

        if (this.updatedContactsResponse.status.errorCode === '0') {
         this.isEditable = false;
        //  alert(Constants.UPDATE_MSG);
         this.detailsObject.pushNotifications =  this.pushNotifications;
         this.genericService.setItem('homePageObject' , JSON.stringify(this.detailsObject));
      //   this.importContact();
         if (!(key === 'lng')) {
          this.presentAlert(key);
         }
         observe.next('sucess');
       } else {
         alert(Constants.API_ERROR_MSG);
         observe.error(updatedContactsResponse);
       }
      });
    }
  });
  }

  async presentAlert(key) {
    this.translate.get('updateMsg').subscribe(t => {
      this.updateMsg = t;
     });
    this.translate.get('ok').subscribe(t => {
      this.oktext = t;
     });
    const alert = await this.alertController.create({
      message: this.updateMsg,
      buttons: [{
        text: this.oktext,
        handler: () => {
          if (key === 'lng') {
            this.canUpdate = false;
            clearTimeout(this.timeout);
            this.navCtrl.navigateRoot('/app/tabs/home').then(res => {
              window.location.reload();
            });
            // this.router.navigateByUrl('/app/tabs/home')

          }
          console.log('Confirm Okay');
        }
      }
     ]
    });

    await alert.present();
  }


  setLanguage(lng) {
    this.zone.run(() => {
    console.log('before update');
    this.update('lng').subscribe(response => {
      console.log('after update');
      this.translate.use(lng).subscribe(translateResponse => {
        this.presentAlert('lng');
      });
      this.selected = lng;
      this.genericService.setItem('SELECTED_LANGUAGE', lng);
      if (lng === 'ar') {
      this.textDir = 'rtl';
    } else {
      this.textDir = 'ltr';
    }
      this.genericService.setItem('textDir', this.textDir);
      this.translate.get('updateMsg').subscribe(t => {
      this.updateMsg = t;
     });
      this.translate.get('ok').subscribe(t => {
      this.oktext = t;
     });
    });
  });

  }

  fingerPrintChange(value) {
    this.update('fingerprint').subscribe(res => {
      this.genericService.setItem('isFingerPrintEnable', value);
      this.importContact();
    }, err => {
     console.log(err);
    });

  }

  navigateToSupport() {
    this.navCtrl.navigateForward('/support');
  }
}

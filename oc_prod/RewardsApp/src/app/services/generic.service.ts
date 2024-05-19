import { UpdateContacts } from './../classes/updateContact';
import { HttpHandlerService } from './http-handler.service';
import { Constants, SOURCE_TYPE } from './../enums/constants.enum';
import { Report } from 'src/app/classes/report';
import { Header } from './../classes/header';
import { Injectable } from '@angular/core';
import { DatePipe } from '@angular/common';
import * as CryptoJS from 'crypto-js';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { LookUp } from '../classes/lookup';
import { ImportContact } from '../classes/importContact';
import { Platform, LoadingController } from '@ionic/angular';
import { User } from '../classes/user';
import { UrlEnums } from '../enums/urlenums.enum';
import { Customer } from '../classes/customer';
import { StatusBar } from '@ionic-native/status-bar/ngx';
const LNG_KEY = 'SELECTED_LANGUAGE';
import { Device } from '@ionic-native/device/ngx';
import { UUID } from 'angular2-uuid';

@Injectable({
  providedIn: 'root'
})

export class GenericService {

  headerObject: Header;
  portalBrandingResponse: BehaviorSubject<any> =  new BehaviorSubject(null);
  report;
  lookup;
  importContactObject;
  user;
  importContactResponse;
  customer;
  updatedCustomer;
  updatedContactsResponse;
  shouldOpenFingerPrint: BehaviorSubject<boolean> =  new BehaviorSubject(false);
  constructor(private datePipe: DatePipe,
              private httpHandler: HttpHandlerService,
              private platform: Platform,
              private loadingCtrl: LoadingController,
              private statusBar: StatusBar,
	            private device: Device,
              private translate: TranslateService) {
    this.headerObject = new Header();
    this.customer = new Customer();
    this.headerObject = new Header();
    this.importContactObject =  new ImportContact();
    this.report = new Report();
    this.user = new User();
    this.lookup = new LookUp();
    this.updatedCustomer = new UpdateContacts();
    this.user = JSON.parse(this.getItem('user'));
   }

   setStatusBar(color) {
    if (this.platform.is('ios')) {
      this.statusBar.styleDefault();
      this.statusBar.backgroundColorByHexString(color);
     }
   }
    prepareHeaderObject() {
    const currentTimeStamp =  this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');
    this.headerObject.requestId  = currentTimeStamp;
    this.headerObject.requestDate = currentTimeStamp;
    this.headerObject.docSID = '' ;
    this.headerObject.sourceType = SOURCE_TYPE;
    this.headerObject.terminalId = '';
    this.headerObject.storeNumber = '10';
    this.headerObject.employeeId = '';
    this.headerObject.pcFlag = 'false';
    this.headerObject.receiptNumber = '';
    this.headerObject.subsidiaryNumber = '';

    return this.headerObject;
  }

  loadPortalBrandingResponse(): Observable<any>  {
    return this.portalBrandingResponse.asObservable();
  }

  canOpenFingerprint(): Observable<Boolean>{
    return this.shouldOpenFingerPrint.asObservable();
  }

  getPlatformName() {
    if (this.platform.is('android')) {
        return 'android';
    } else if (this.platform.is('ios')) {
        return 'ios';
    } else {
        return 'web';
    }
  }
  setItem(key , value) {
    if (this.platform.is('cordova') || this.platform.is('android') || this.platform.is('ios')) {
      window.localStorage.setItem(key, value);
    } else {
      window.sessionStorage.setItem(key, value);
    }
  }

  getItem(key) {
    if (this.platform.is('cordova') || this.platform.is('android') || this.platform.is('ios')) {
    return  window.localStorage.getItem(key);
    } else {
     return window.sessionStorage.getItem(key);
    }
  }

  removeItem(key) {
    if (this.platform.is('cordova') || this.platform.is('android') || this.platform.is('ios')) {
      window.localStorage.removeItem(key);
    } else {
      window.sessionStorage.removeItem(key);
    }
  }

  setSessionItem(key, value){
    window.sessionStorage.setItem(key, value);
  }

  getSessionItem(key) {
    return window.sessionStorage.getItem(key);
  }

  removeSessionItem(key) {
    window.sessionStorage.removeItem(key);
  }

  encryptData(data) {

    const password = CryptoJS.enc.Utf8.parse('enfldsgbnlsngdlksdsgm');
    const salt = CryptoJS.enc.Hex.parse('de331012de331012');
    const iterations = 20;

// PBE according to PKCS#5 v1.5 (in other words: PBKDF1)
    const md5 = CryptoJS.algo.MD5.create();
    md5.update(password);
    md5.update(salt);
    let result = md5.finalize();
    md5.reset();
    for (let i = 1; i < iterations; i++) {
    md5.update(result);
    result = md5.finalize();
    md5.reset();
}

// splitting key and IV
    const key = CryptoJS.lib.WordArray.create(result.words.slice(0, 2));
    const iv = CryptoJS.lib.WordArray.create(result.words.slice(2, 4));

    const encrypted = CryptoJS.DES.encrypt(data, key, {
    iv
});
    if(encrypted && encrypted.ciphertext) {
      console.log(encrypted.ciphertext.toString(CryptoJS.enc.Base64));

      return  encrypted.ciphertext.toString(CryptoJS.enc.Base64);
    }
   
  }

  decryptData(data) {

    const password = CryptoJS.enc.Utf8.parse('enfldsgbnlsngdlksdsgm');
    const salt = CryptoJS.enc.Hex.parse('de331012de331012');
    const iterations = 20;

    // PBE according to PKCS#5 v1.5 (in other words: PBKDF1)
    const md5 = CryptoJS.algo.MD5.create();
    md5.update(password);
    md5.update(salt);
    let result = md5.finalize();
    md5.reset();
    for (let i = 1; i < iterations; i++) {
        md5.update(result);
        result = md5.finalize();
        md5.reset();
    }

    // splitting key and IV
    const key = CryptoJS.lib.WordArray.create(result.words.slice(0, 2));
    const iv = CryptoJS.lib.WordArray.create(result.words.slice(2, 4));

    const decrypted = CryptoJS.DES.decrypt(data, key, {
      iv
  });

    return this.hex2a(decrypted.toString());
  }

  hex2a(hex) {
    let str = '';
    for (let i = 0; i < hex.length; i += 2) {
        str += String.fromCharCode(parseInt(hex.substr(i, 2), 16));
    }
    return str;
}

setLanguage(lng) {
    this.setItem(LNG_KEY, lng);
    if (lng === 'ar') {
      this.setItem('textDir', 'rtl');
    } else {
      this.setItem('textDir', 'ltr');
    }
    this.translate.use(lng);
  }

   getLoading() {
    return  this.loadingCtrl.create({
      spinner: 'crescent',
      backdropDismiss: true,
      showBackdrop: true,
      translucent : true,
      duration: 30000
    });
  }

  numberWithSpaces(x) {
    const original = x;
    const part1 = x.substring(0, 3);
    const part2 =  x.substring(3, 6);
    const part3 = x.substring(6, 10);
    return part1 + ' ' + part2 + ' ' + part3;
 }

 ismobileWeb() {
   return this.platform.is('mobileweb') || this.platform.is('android') || this.platform.is('ios') || this.platform.is('mobile');
 }
updateToken() {
  this.user = JSON.parse(this.getItem('user'));
  this.headerObject = this.prepareHeaderObject();
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
  this.lookup.membershipNumber =  this.getItem('cardNumber');
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
       if (!(this.getItem('tokenUpdated') === 'true')) {
           this.update('token');
       }

     } else {
       alert(Constants.API_ERROR_MSG);
     }
    });
}
update(key) {
  this.user = JSON.parse(this.getItem('user'));
  const detailsObject = JSON.parse(this.getItem('homePageObject'));
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
  const modifiedPassword = '';
  const loyalty = {
    password : '' + modifiedPassword,
    mobileAppPreferences :
    {
     language :  this.getItem('SELECTED_LANGUAGE'),
    }
  };
  this.customer.loyalty = loyalty;
  this.customer.instanceId = this.getItem('deviceToken');
  this.customer.deviceType = this.getPlatformName();
  this.customer.suppress.phone.isTrue = '';
  this.customer.suppress.phone.reason = '';
  this.customer.suppress.phone.timestamp = '';
  this.customer.suppress.email.isTrue = '';
  this.customer.suppress.email.reason = '';
  this.customer.suppress.email.timestamp = '';
  this.updatedCustomer.customer = this.customer;
  this.httpHandler.httpPostRequestWithBody(
    UrlEnums.BASE_URL + UrlEnums.UPDATE_CONTACT_URL, this.updatedCustomer)
    .subscribe(updatedContactsResponse => {
      console.log(updatedContactsResponse);
      this.updatedContactsResponse = updatedContactsResponse;
      if (this.updatedContactsResponse.status.errorCode === '0') {
        this.setItem('tokenUpdated', 'true');
     } else {
       alert(this.updatedContactsResponse.status.message);
     }
    });


}

  getUDID() {
    console.log(UUID.UUID());
    if (this.platform.is('android') || this.platform.is('android')) {
      return this.device.uuid;
    } else {
      return UUID.UUID();
    }
  }
}

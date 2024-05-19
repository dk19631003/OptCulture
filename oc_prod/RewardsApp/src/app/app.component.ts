import { FingerprintAIO } from '@ionic-native/fingerprint-aio/ngx';
import { AndroidFingerprintAuth, AFAAuthOptions } from '@ionic-native/android-fingerprint-auth/ngx';
import { NotificationsService } from './services/notifications.service';
import { Constants } from './enums/constants.enum';
import { Keyboard } from '@ionic-native/keyboard/ngx';
import { User } from './classes/user';
import { UrlEnums } from './enums/urlenums.enum';
import { HttpHandlerService } from './services/http-handler.service';
import { Component, OnInit, NgZone, ElementRef, Inject, AfterViewInit, HostListener } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { Router } from '@angular/router';
import { Push, PushObject, PushOptions } from '@ionic-native/push/ngx';
import { TranslateService } from '@ngx-translate/core';
import { GenericService } from './services/generic.service';
import { Title, DomSanitizer } from '@angular/platform-browser';
import { DOCUMENT } from '@angular/common';
import {firebase} from '@firebase/app';
import {environment} from '../environments/environment';
import { SwUpdate } from '@angular/service-worker';
import * as CryptoJS from 'crypto-js';


const LNG_KEY = 'SELECTED_LANGUAGE';
declare var window: any;
declare var navigator: any;
declare var cordova: any;
declare let FontFace: any;
@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html'
})
export class AppComponent implements OnInit , AfterViewInit {

  selected;
  public appPages = [
    {
      title: 'Home',
      url: '/home',
      icon: 'home'
    },
    {
      title: 'Registration',
      url: '/enrolment',
    },
    {
      title: 'Profile',
      url: '/profile',
    },
    {
      title: 'Login',
      url: '/login',
    },
    {
      title: 'QR Code',
      url: '/qrcode',
    }

  ];
  tokenResponse: any;
  tokenDetails: User;
  webportalBrandingResponse;
  coverimageUrl: string;
  appTitle: string;
  logoImage: string;
  deferredPrompt: any;
  showButton = false;
  link = 'https://fonts.googleapis.com/css2?family=Raleway:wght@500&display=swap';
  constructor(
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar,
    private router: Router,
    private push: Push,
    private keyboard: Keyboard,
    private translate: TranslateService,
    private genericService: GenericService,
    private httpHandler: HttpHandlerService,
    private zone: NgZone,
    private elementRef: ElementRef,
    private titleService: Title,
    private notificationsService: NotificationsService,
    private swUpdate: SwUpdate,
    private sanitizer: DomSanitizer,
    private faio: FingerprintAIO,
    private androidFingerprintAuth: AndroidFingerprintAuth,
    @Inject(DOCUMENT) private document: HTMLDocument
  ) {
    this.initializeApp();
    this.tokenDetails = new User();
    // swUpdate.available.subscribe(event => {
    //   if (askUserToUpdat()) {
    //     window.location.reload();
    //   }
    // });

  }

  @HostListener('window:beforeinstallprompt', ['$event'])
  onbeforeinstallprompt(e) {
    console.log(e);
    // Prevent Chrome 67 and earlier from automatically showing the prompt
    // e.preventDefault();
    // Stash the event so it can be triggered later.
    this.deferredPrompt = e;
    this.showButton = true;
  }


  addToHomeScreen() {
    // hide our user interface that shows our A2HS button
    this.showButton = false;
    // Show the prompt
    this.deferredPrompt.prompt();
    // Wait for the user to respond to the prompt
    this.deferredPrompt.userChoice
      .then((choiceResult) => {
        if (choiceResult.outcome === 'accepted') {
          console.log('User accepted the A2HS prompt');
        } else {
          console.log('User dismissed the A2HS prompt');
        }
        this.deferredPrompt = null;
      });
  }
  async ngOnInit() {
    if (!this.platform.is('cordova')) {
      firebase.initializeApp(environment.firebase);
      await this.notificationsService.init();
    }
  }

  androidFingerprint() {
    this.androidFingerprintAuth.isAvailable()
    .then((result) => {
      if (result.isAvailable) {
        // it is available
        let fingerprintoptions: AFAAuthOptions = { clientId: 'LoyaltyApp',
        username: 'Anil',
        password: 'Test@123'
      };
        if (this.genericService.getItem('homePageObject')) {
          const detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
          fingerprintoptions = {
            clientId: 'LoyaltyApp',
            username: 'Anil',
            password: 'Test@123',
            dialogTitle: 'Hello' + detailsObject.userName + ',',
            disableBackup: true,
            dialogMessage: 'Use touch ID to login again'
          };
        } else {
          fingerprintoptions = {
            clientId: 'LoyaltyApp',
            username: 'Anil',
            password: 'Test@123',
            disableBackup: true,
            dialogMessage: 'Use touch ID to login again'
          };
        }

        this.androidFingerprintAuth.encrypt( fingerprintoptions)
          .then((fingerprintResult) => {
            if (fingerprintResult.withFingerprint) {
                console.log('Successfully encrypted credentials.');
                console.log('Encrypted credentials: ' + fingerprintResult.token);
                if(this.genericService.getItem('fromNotification')) {
                  this.navigateNotifications();
                } else {
                  this.navigateToHome();
                }
            } else if (fingerprintResult.withBackup) {
              console.log('Successfully authenticated with backup password!');
            } else { console.log('Didn\'t authenticate!'); }
          })
          .catch(error => {
            if (error === this.androidFingerprintAuth.ERRORS.FINGERPRINT_CANCELLED) {
              console.log('Fingerprint authentication cancelled');
            } else { console.error(error); }
          });

      } else {
        // fingerprint auth isn't available
      }
  })
   .catch(error => console.error(error));
  }

  fingerPrintChecking() {
    if (!(this.genericService.getItem('isFingerPrintEnable') === undefined ||
    this.genericService.getItem('isFingerPrintEnable') === null)
    && (this.genericService.getItem('isFingerPrintEnable') === 'true') ) {
      //  if(this.platform.is('android'))
      //  {
      //    this.androidFingerprint();
      //  }
      //  if(this.platform.is('ios'))
      //  {
         this.iosFingerPrint();
      //  }
    } else {
      if(this.genericService.getItem('fromNotification')) {
        this.navigateNotifications();
      } else {
        this.navigateToHome();
      }
    }
  }

  navigateNotifications(){
    this.router.navigateByUrl('/notifications');
  }

 navigateToHome(){
    this.router.navigateByUrl('/app/tabs/home');
 }

  iosFingerPrint() {
    this.faio.isAvailable().then((isAvailable) => {
      console.log(isAvailable);
      this.faio.show({ 
        title: 'Fingerprint', // Android: Used for encryption. iOS: used for dialogue if no `localizedReason` is given.
        subtitle: 'Use touch ID to login again', // Necessary for Android encrpytion of keys. Use random secret key.
        disableBackup: true
    })
    .then((result: any) => {
      console.log(result);
      if(this.genericService.getItem('fromNotification')) {
        this.navigateNotifications();
      } else {
        this.navigateToHome();
      }
     
      // this.mobileNumber =  this.genericService.getItem('mobileNumber');
      // this.password = '';
      // this.login(true);
    })
    .catch((error: any) => {
      if(error.code === -108) {
        this.iosFingerPrint();
      } else {
        if(this.genericService.getItem('fromNotification')) {
          this.navigateNotifications();
        } else {
          this.navigateToHome();
        }
      }
     
       console.log(error)});
    }).catch(error =>{
      if(this.genericService.getItem('fromNotification')) {
        this.navigateNotifications();
      } else {
        this.navigateToHome();
      }
    });

  }

  ngAfterViewInit() {
    this.platform.ready().then(async () => {
      if (!this.platform.is('cordova')) {
       await this.notificationsService.requestPermission();
       const fireAddToHomeScreenImpression = event => {
        fireTracking('Add to homescreen shown');
        // will not work for chrome, untill fixed
        event.userChoice.then(choiceResult => {
          fireTracking(`User clicked ${choiceResult}`);
        });
        // This is to prevent `beforeinstallprompt` event that triggers again on `Add` or `Cancel` click
        window.removeEventListener(
          'beforeinstallprompt',
          fireAddToHomeScreenImpression
        );
      };
       window.addEventListener('beforeinstallprompt', fireAddToHomeScreenImpression);

  // Track web app install by user
       window.addEventListener('appinstalled', event => {
          fireTracking('PWA app installed by user!!! Hurray');
        });

  // Track from where your web app has been opened/browsed
       window.addEventListener('load', () => {
    let trackText;
    if (navigator && navigator.standalone) {
      trackText = 'Launched: Installed (iOS)';
    } else if (matchMedia('(display-mode: standalone)').matches) {
      trackText = 'Launched: Installed';
    } else {
      trackText = 'Launched: Browser Tab';
    }
    fireTracking(trackText);
  });

       function fireTracking(track) {
      console.log(track);
  }

      //  window.addEventListener('beforeinstallprompt', (e) => {
      //   // Prevent Chrome 67 and earlier from automatically showing the prompt
      //   e.preventDefault();
      //   console.log(e);
      //   // Stash the event so it can be triggered later on the button event.
      //   // this.deferredPrompt = e;
      // // Update UI by showing a button to notify the user they can add to home screen
      //   // this.showBtn = true;
      // });
}
    });
  }
  initializeApp() {
    this.platform.ready().then(() => {

      if (this.platform.is('cordova')) {
        this.shortcut();
        this.platform.is('android');
        {
          this.statusBar.styleLightContent();
          this.statusBar.overlaysWebView(false);
        }

        if (this.platform.is('ios')) {
         this.genericService.setStatusBar('#fff');
        }
        this.keyboard.hideFormAccessoryBar(false);
        // this.keyboard.setResizeMode(Keyboard.N);

      }
      this.genericService.canOpenFingerprint().subscribe(canopen =>{
        if(canopen) {
          this.iosFingerPrint();
        }
      })

      // this.languageService.setInitialAppLanguage();
      this.setInitialAppLanguage();
      this.getToken();

    });
  }

  getFontCSS() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.link);
  }

  shortcut() {
    window.plugins.Shortcuts.supportsDynamic( (supported) => {
      if (supported) {
          console.log('Dynamic shortcuts are supported');
      } else {
         console.log('Dynamic shortcuts are NOT supported');
      }
  }, (error) => {
      window.alert('Error: ' + error);
  });
  }

  getToken(): void {
    this.zone.run(() => {
      if (this.genericService.getSessionItem('portalBranding')) {
        this.webportalBrandingResponse = JSON.parse(this.genericService.getSessionItem('portalBranding'));
        document.documentElement.style.setProperty('--coverimageurl', 'url(' + this.webportalBrandingResponse.coverimage + ')');
        document.documentElement.style.setProperty('--themecolor', this.webportalBrandingResponse.themecolor);
        this.document.getElementById('appFavicon').setAttribute('href', this.webportalBrandingResponse.tabImage);
        this.titleService.setTitle(
          this.webportalBrandingResponse.tabName );
        // this.loadFont();
        this.link = this.webportalBrandingResponse.fontURL;
        // this.link = 'https://fonts.googleapis.com/css2?family=Ubuntu&display=swap';
        // font-family: 'Playfair Display', serif;
        document.documentElement.style.setProperty('--globalfont',  this.webportalBrandingResponse.fontName);
        if(this.platform.is('cordova')) {
          this.splashScreen.hide();
         }
        this.checkPreviousAuthorization();
        this.genericService.setStatusBar(this.webportalBrandingResponse.themecolor);
        this.genericService.portalBrandingResponse.next(this.tokenDetails);
      } else {
      const fullurl: string = window.location.href;
      let url: string;
      if (fullurl) {
        if (environment.production) {
              url =  fullurl.split('/RewardsApp')[0];
        } 
      }
      // console.log(window.location.href);
      // url = url ? url : 'https://petpeopleqa.oclinks.app';
      const body = {homePageUrl: url ? url : '', orgId: Constants.orgId};
      this.httpHandler.httpPostRequestWithBody(
        UrlEnums.BASE_URL
       + UrlEnums.WEB_PORTAL_BRANDING_URL, body)
       .subscribe(webportalBrandingResponse => {
         console.log(webportalBrandingResponse);
         this.webportalBrandingResponse = webportalBrandingResponse;
         this.tokenDetails.userName = this.webportalBrandingResponse.username;
         this.tokenDetails.organizationId = this.webportalBrandingResponse.orgId;
         this.tokenDetails.token = this.webportalBrandingResponse.token;
         // this.tokenDetails.userName = "ramakrishna"
         // this.tokenDetails.organizationId ="ocqa"
         // this.tokenDetails.token = "BHANZ1UC1BJPZBJO"
         
         if(this.genericService.getItem('user')) {
          const user = JSON.parse(this.genericService.getItem('user'));
          user.userName = this.webportalBrandingResponse.username;
          user.organizationId = this.webportalBrandingResponse.orgId;
          user.token = this.webportalBrandingResponse.token;
          this.genericService.setItem('user', JSON.stringify(user));
         } else {
          this.genericService.setItem('user', JSON.stringify(this.tokenDetails));
         }
         if(this.platform.is('cordova')) {
          this.splashScreen.hide();
         }
         this.checkPreviousAuthorization();
         this.genericService.portalBrandingResponse.next(this.tokenDetails);
         this.genericService.setSessionItem('portalBranding', JSON.stringify(this.webportalBrandingResponse));
         this.elementRef.nativeElement.style.setProperty('--coverimageurl', 'url(' + this.webportalBrandingResponse.coverimage + ')');
         this.elementRef.nativeElement.style.setProperty('--themecolor', this.webportalBrandingResponse.themecolor);
         this.titleService.setTitle(
           this.webportalBrandingResponse.tabName );
         this.document.getElementById('appFavicon').setAttribute('href', this.webportalBrandingResponse.tabImage);
         this.genericService.setStatusBar(this.webportalBrandingResponse.themecolor);
        //  this.httpHandler.httpGETRequest(
        //  UrlEnums.BASE_URL
        // + UrlEnums.GET_TOKEN_URL + '?username='
        // + this.webportalBrandingResponse.username + '&password='
        // + orgPassword + '&orgid=' + this.webportalBrandingResponse.orgId)
        // .subscribe(tokenResPonse => {
          if (this.platform.is('cordova')) {
            this.pushNotification();
          }
          
          // }, error => {
          //   alert(Constants.API_ERROR_MSG);
          // });
        }, error => {
          alert(Constants.API_ERROR_MSG);
        });
      }
    });
  }

  pushNotification() {
     // to check if we have permission
this.push.hasPermission()
.then((res: any) => {

  if (res.isEnabled) {
    console.log('We have permission to send push notifications');
  } else {
    console.log('We do not have permission to send push notifications');
  }

});

// Create a channel (Android O and above). You'll need to provide the id, description and importance properties.
this.push.createChannel({
id: 'testchannel1',
description: 'My first test channel',
// The importance property goes from 1 = Lowest, 2 = Low, 3 = Normal, 4 = High and 5 = Highest.
importance: 3
}).then(() => console.log('Channel created'));

// Delete a channel (Android O and above)
this.push.deleteChannel('testchannel1').then(() => console.log('Channel deleted'));

// Return a list of currently configured channels
this.push.listChannels().then((channels) => console.log('List of channels', channels));

// to initialize push notifications

const options: PushOptions = {
 android: {
   senderID : '759427207028'
 },
 ios: {
     alert: 'true',
     badge: true,
     sound: 'false'
 }
};

const pushObject: PushObject = this.push.init(options);

pushObject.clearAllNotifications().then( response => {
  console.log('suceess cleared', response);
}).catch( err => {
  console.log('error while clearing');
});
pushObject.on('notification').subscribe((notification: any) => {
  console.log( notification);
 this.genericService.setItem('fromNotification', true);
} );

pushObject.on('registration').subscribe((registration: any) => {
  console.log('Device registered', registration.registrationId);
// alert(registration.registrationId);
  this.genericService.setItem('deviceToken', registration.registrationId);
});

pushObject.on('error').subscribe(error => console.error('Error with Push plugin', error));

  }

  checkPreviousAuthorization(): void {
    if (this.genericService.getItem('homePageObject') ||
    this.genericService.getItem('rememberMe') === 'true' ) {
      console.log('test');
      if(this.platform.is('cordova')){
        if(this.genericService.getItem('fingerprintloginreq')){
          this.router.navigateByUrl('/login');
        } else {
          this.fingerPrintChecking()
        }
      } else {
        this.navigateToHome();
      }
    } else {
      this.router.navigateByUrl('/login');
    }

  }

  setInitialAppLanguage() {

    this.genericService.setItem('textDir', 'ltr');
    if (this.genericService.getItem(LNG_KEY)) {
      const val = this.genericService.getItem(LNG_KEY);
      if (val === 'ar') {
        this.genericService.setItem('textDir', 'rtl');
      }

      this.setLanguage(val);
      this.selected = val;
    } else {
      this.setLanguage('en');
      this.selected = 'en';
    }

  }

  setLanguage(lng) {
    this.translate.use(lng);
    this.selected = lng;
    this.genericService.setItem(LNG_KEY, lng);
  }
}

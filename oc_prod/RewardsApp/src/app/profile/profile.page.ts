import { Keyboard } from '@ionic-native/keyboard/ngx';
import { MemberShip } from './../classes/member-ship';
import { LanguageserviceService } from './../services/languageservice.service';
import { UpdateContacts } from './../classes/updateContact';
import { LookUp } from './../classes/lookup';
import { User } from './../classes/user';
import { Header } from './../classes/header';
import { Customer } from './../classes/customer';
import { Constants, SOURCE_TYPE } from './../enums/constants.enum';
import { UrlEnums } from './../enums/urlenums.enum';
import { HttpHandlerService } from './../services/http-handler.service';
import { Component, OnInit, ViewChild, NgZone } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ImportContact } from '../classes/importContact';
import { Report } from '../classes/report';
import { Router, RouterOutlet, ActivationStart } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GenericService } from '../services/generic.service';
import { Platform, IonContent, ActionSheetController, AlertController, LoadingController, NavController } from '@ionic/angular';
import { Camera, CameraOptions } from '@ionic-native/camera/ngx';
import { FilePath } from '@ionic-native/file-path/ngx';
import { File } from '@ionic-native/file/ngx';
import { WebView } from '@ionic-native/ionic-webview/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';


declare var cordova;
@Component({
  selector: 'app-profile',
  templateUrl: 'profile.page.html',
  styleUrls: ['profile.page.scss'],
})
export class ProfilePage implements OnInit  {

  @ViewChild(RouterOutlet) outlet: RouterOutlet;
  @ViewChild(IonContent) ionContent: IonContent;
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
  pushNotifications: boolean;
  textDir: string;
  password: string;
  show = false;
  passwordType = 'text';
  givenPasword: string;
  detailsObject;
  spacesNumber;
  currentShow = true;
  newShow = true;
  confirmShow = true;
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
  hideTop = false;
  windowHeight;
  keyboardHeight = '0px';
  spaceswithNumberForChange;
  profilePicImageUrl = 'assets/images/empty-profile.png';
  logoutMsg: string;
  confirmText;
  okText;
  cancelText;
  paddingtop = '60px';
  tokenUpdate;
  editProfileForm: FormGroup;
  isSubmitted = false;
  isMobile = false;
  showRewardsPage = Constants.showRewardsPage
  constructor(public httpHandler: HttpHandlerService,
              public router: Router,
              private translate: TranslateService,
              private genericService: GenericService,
              private keyboard: Keyboard,
              public platform: Platform,
              public zone: NgZone,
              private camera: Camera,
              public filePath: FilePath,
              public file: File,
              public webView: WebView,
              public statusBar: StatusBar,
              public actionSheetCtrl: ActionSheetController,
              public alertController: AlertController,
              private sanitizer: DomSanitizer,
              public loadCtrl: LoadingController,
              public navCtrl: NavController,
              private formBuilder: FormBuilder,
              public datePipe: DatePipe) {
    this.isMobile = this.platform.is('mobile');
    this.customer = new Customer();
    this.headerObject = new Header();
    this.importContactObject =  new ImportContact();
    this.report = new Report();
    this.user = new User();
    this.lookup = new LookUp();
    this.updatedCustomer = new UpdateContacts();
    this.user = JSON.parse(this.genericService.getItem('user'));
    this.password =  this.genericService.getItem('password');
    this.givenPasword = this.genericService.getItem('password');
  }

  getImgContent(): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(this.profilePicImageUrl);
  }

  formSubmit() {
    this.isSubmitted = true;
    if (!this.editProfileForm.valid) {
      console.log('Please provide all the required values!');
      return false;
    } else {
      console.log(this.editProfileForm.value);
      const registerData = this.editProfileForm.value;
      this.customer.firstName = registerData.firstName;
      this.customer.lastName = registerData.lastName;
      this.customer.phone = registerData.mobile;
      this.customer.emailAddress = registerData.email;
      this.customer.country = registerData.country;
      this.customer.addressLine1 = registerData.addressLine1;
      this.customer.addressLine2 = registerData.addressLine2;
      this.customer.state = registerData.state;
      this.customer.city = registerData.city;
      this.customer.postal = registerData.postal;
      this.currentPassword = registerData.currentPassword;
      this.newPassword = registerData.newPassword;
      this.confirmPassword = registerData.confirmPassword;
      this.updateProfile();
    }
  }

  get errorControl() {
    return this.editProfileForm.controls;
  }

  formInit() {
    this.editProfileForm = this.formBuilder.group({
      firstName: [this.customer.firstName, [Validators.required]],
      lastName: [this.customer.lastName, [Validators.required]],
      email: [this.customer.emailAddress, [Validators.required , Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
      mobile: [this.spacesNumber,
        [Validators.required, Validators.maxLength(12) , Validators.minLength(12)   ]],
      country: [this.customer.country],
      addressLine1: [this.customer.addressLine1, Validators.maxLength(200)],
      addressLine2: [this.customer.addressLine2, Validators.maxLength(200)],
      city: [this.customer.city],
      state: [this.customer.state],
      postal: [this.customer.postal],
      currentPassword: [''],
      newPassword: [''],
      confirmPassword: ['']
   });
  }

  ngOnInit() {

    this.profilePicImageUrl = window.localStorage.getItem('profileimageUrl') ?
    window.localStorage.getItem('profileimageUrl') : this.profilePicImageUrl;

    this.router.events.subscribe(e => {
      if (e instanceof ActivationStart && e.snapshot.outlet === 'administration') {
        this.outlet.deactivate();
      }
    });

    // this.keyboard.onKeyboardShow().subscribe((e)=>{
    //   console.log(e);
    //   this.zone.run(()=>{
    //     this.keyboardHeight = (e.keyboardHeight+30)+"px";
    //     console.log(this.keyboardHeight)
    //   })
    // })

    // this.keyboard.onKeyboardWillHide().subscribe((e)=>{
    //   console.log(e);
    //   this.zone.run(()=>{
    //   this.keyboardHeight = "0px"
    //   })
    // })
    window.addEventListener('keyboardWillShow', (e: any) => {
      console.log(e);
      this.zone.run(() => {
          if (this.platform.is('android')) {
            this.keyboardHeight = (e.keyboardHeight + 50) + 'px';
            console.log(this.keyboardHeight);
          }

          if (this.platform.is('ios')) {
            this.keyboardHeight = (e.keyboardHeight + 10) + 'px';
            console.log(this.keyboardHeight);
          }

        });
    });


    window.addEventListener('keyboardWillHide', () => {
        this.zone.run(() => {
        this.keyboardHeight = '0px';
        });
      });

  }

  format(valString) {
    if (!valString) {
        return '';
    }
    const val = valString.toString();
    const parts = val.replace(/ /g, '');
    return parts.replace(/\B(?=(?:\w{3})+(?!\w))/g, '');
}

  ionViewWillEnter() {
    this.translate.get('AreYouSureYouWantToLogout').subscribe(t => {
      this.logoutMsg = t;
     });
    this.translate.get('Confirm').subscribe(t => {
      this.confirmText = t;
     });
    this.translate.get('ok').subscribe(t => {
      this.okText = t;
     });
    this.translate.get('Cancel').subscribe(t => {
      this.cancelText = t;
     });
    this.isEditable = false;
    this.importContact();
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.textDir = this.genericService.getItem('textDir');
    this.langauage = this.genericService.getItem('SELECTED_LANGUAGE');
    this.detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
    this.pushNotifications = this.detailsObject.pushNotifications;
    if ( this.genericService.getItem('isFingerPrintEnable') &&
    this.genericService.getItem('isFingerPrintEnable') === 'true') {
      this.isFingerPrintAvailable = true;
    } else {
      this.isFingerPrintAvailable = false;
    }
    this.user = JSON.parse(this.genericService.getItem('user'));

  }

  getMarginBottomPropertyForDrawer() {
    const difference = screen.height - this.platform.height();
    return difference;
  }
  numberWithSpaces(x) {
    const part1 = x.substring(0, 3);
    const part2 =  x.substring(3, 6);
    const part3 = x.substring(6, 10);
    return part1 + ' ' + part2 + ' ' + part3;
 }

 getWithoutSpacesNumber(mobilenumber) {
  const numberarray = mobilenumber.split(' ');
  let x = '';
  // tslint:disable-next-line: prefer-for-of
  for (let i = 0; i < numberarray.length; i++) {
    x = x + numberarray[i];
  }
  return x;
 }
 numberWithSpacesForInput(event) {
  //  console.log(event);
   if (!(event.key === 'Backspace')) {
    const mobileNumber = event.target.value;

    const x = this.getWithoutSpacesNumber(mobileNumber);

    const part1 = x.substring(0, 3);
    const part2 =  x.substring(3, 6);
    const part3 = x.substring(6, 10);
    if (x.length === 3) {
      this.editProfileForm.patchValue({mobile: part1 + ' ' + part2});
     } else if (x.length === 7) {
      this.editProfileForm.patchValue({mobile:  part1 + ' ' + part2 + ' ' + part3});
      this.spaceswithNumberForChange = part1 + ' ' + part2 + ' ' + part3;
     }
    console.log(this.spaceswithNumberForChange);
   }

}
  backButtonEvent() {
    if (this.isEditable) {
      this.isEditable = false;
    } else {
      this.router.navigateByUrl('/app/tabs/home');
    }
  }
  edit() {
    this.isEditable = true;
    setTimeout( () => {
      // console.log(document.getElementById("currentPassword").getElementsByTagName('input'))
       this.showCurrentPassword();
       this.showNewPassword();
       this.showConfirmPassword();
       this.formInit();
    }, 1000);

  }

  async importContact() {
    const loading = await this.genericService.getLoading();
    loading.present();
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
         console.log(this.customer);
         this.spacesNumber = this.numberWithSpaces(this.customer.phone);
         this.spaceswithNumberForChange = this.numberWithSpaces(this.customer.phone);
         const userName = this.importContactResponse.matchedCustomers[0].firstName
         + ' ' + this.importContactResponse.matchedCustomers[0].lastName;
         const phoneNumber = this.importContactResponse.matchedCustomers[0].phone;
         const email = this.importContactResponse.matchedCustomers[0].emailAddress;
         this.detailsObject.userName = userName;
         this.detailsObject.phoneNumber = phoneNumber;
         this.detailsObject.email = email;
         console.log( this.spaceswithNumberForChange, this.spacesNumber);
         this.genericService.setItem('homePageObject', JSON.stringify(this.detailsObject));
         if (!(this.genericService.getItem('tokenUpdated') === 'true') && this.genericService.getItem('deviceToken')) {
             this.update('token');
         }
         this.formInit();
       } else {
         alert(this.importContactResponse.status.message);
       }
        loading.dismiss();
      }, err => {
        this.formInit();
        loading.dismiss();
      });
  }



  async update(key) {
    const loading = await this.genericService.getLoading();
    loading.present();
    const currentTimeStamp =  this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss');
    const headerObject = new Header();
    headerObject.requestId  = currentTimeStamp;
    headerObject.requestDate = currentTimeStamp;
    headerObject.contactSource = SOURCE_TYPE;
    headerObject.contactList = 'List1';
    headerObject.sourceType = SOURCE_TYPE;
    this.updatedCustomer.header = headerObject;
    this.updatedCustomer.user = this.user;
    this.customer.creationDate = currentTimeStamp;
    let modifiedPassword;
    if (this.newPassword && !(this.newPassword === '')) {
      modifiedPassword = this.genericService.encryptData(this.newPassword);
    } else {
      modifiedPassword = '';
    }
    const loyalty = {
      password : '' + modifiedPassword,
      mobileAppPreferences :
      {
       language : this.langauage,
       pushNotifications: this.pushNotifications ? 'true' : 'false'
      }
    };
    this.customer.phone = this.getWithoutSpacesNumber(this.spaceswithNumberForChange);
    this.customer.loyalty = loyalty;
    this.customer.instanceId = this.genericService.getItem('deviceToken');
    this.customer.deviceType = this.genericService.getPlatformName();
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
         this.isEditable = false;
         if (!(key === 'token')) {
         alert(Constants.UPDATE_MSG);
         this.detailsObject.pushNotifications =  this.pushNotifications;
         this.password = this.newPassword;
         this.genericService.setItem('password', this.password);
         this.genericService.setItem('homePageObject' , JSON.stringify(this.detailsObject));
         this.currentPassword = '';
         this.newPassword = '';
         this.confirmPassword = '';
         if (key === 'lng') {
          window.location.reload();
         }
         this.importContact();
          } else {
            this.genericService.setItem('tokenUpdated', 'true');
          }
        } else {
          alert(this.updatedContactsResponse.status.message);
        }
        loading.dismiss();
      }, err => {
        loading.dismiss();
       });


  }

  // logout()
  // {

  //   if(window.confirm(this.logoutMsg))
  //   {
  //     this.genericService.removeItem("homePageObject");
  //      this.router.navigateByUrl('/login');
  //     // navigator['app'].exitApp();
  //   }
  // }

  async logout() {
    this.translate.get('AreYouSureYouWantToLogout').subscribe(t => {
      this.logoutMsg = t;
     });
    this.translate.get('Confirm').subscribe(t => {
      this.confirmText = t;
     });
    this.translate.get('ok').subscribe(t => {
      this.okText = t;
     });
    this.translate.get('Cancel').subscribe(t => {
      this.cancelText = t;
     });
    const alert = await this.alertController.create({
      header: this.confirmText,
      message: this.logoutMsg,
      buttons: [
        {
          text: this.cancelText,
          role: 'cancel',
          cssClass: 'secondary',
          handler: (blah) => {
            console.log('Confirm Cancel: blah');
          }
        }, {
          text: this.okText,
          handler: () => {
            if(this.platform.is('cordova')){
              if (!(this.genericService.getItem('isFingerPrintEnable') === undefined ||
              this.genericService.getItem('isFingerPrintEnable') === null)
              && (this.genericService.getItem('isFingerPrintEnable') === 'true') ) {
                    this.genericService.setItem('fingerprintloginreq',true)
                    this.router.navigateByUrl('/login');
              } else {
                this.logoutAPI(); 
              }
            } else {
              this.logoutAPI();
            }
            
          }
        }
      ]
    });

    await alert.present();
  }

  async logoutAPI(){
    const loading = await this.genericService.getLoading();
    loading.present();
    const logoutRequest = {
      header: this.genericService.prepareHeaderObject(),
      user: this.user
    }
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.LOG_OUT_URL, logoutRequest)
      .subscribe((logoutResponse:any) => {
        loading.dismiss();
        const logoutRespone = logoutResponse;
        if(logoutRespone.status.errorCode === '0'){
          this.removedataForLogout();
        } else {
          alert(logoutRespone.status.message)
        }
      }, err => {
        loading.dismiss();
      })
  }

  removedataForLogout(){
    this.genericService.removeItem('homePageObject');
    this.genericService.removeItem('rememberMe');
    const user = JSON.parse(this.genericService.getItem('user'))
    delete user['sessionID'];
    this.genericService.setItem('user',JSON.stringify(user));
    this.genericService.removeItem('fingerprintloginreq');
    this.router.navigateByUrl('/login');
  }


  openFileBrowser(event: any) {
     event.preventDefault();
     const element: HTMLElement = document.getElementById('profilePhotoId') as HTMLElement;
     element.click();
  }

  onFileChange(event) {
    const files = event.target.files;
    const file = files[0];
    if (files && file) {
        const reader = new FileReader();
        reader.onload = this._handleReaderLoaded.bind(this);
        reader.readAsBinaryString(file);
    }
  }

  _handleReaderLoaded(readerEvt) {
    const binaryString = readerEvt.target.result;
    let base64textString = btoa(binaryString);
    base64textString = 'data:image/(png|jpg);base64, ' + base64textString;
    console.log(btoa(binaryString));
    localStorage.setItem('profileimageUrl', base64textString);
    this.profilePicImageUrl = base64textString;
   }

  getBase64Image(img) {
    const canvas = document.createElement('canvas');
    canvas.width = img.width;
    canvas.height = img.height;

    const ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0);

    const dataURL = canvas.toDataURL('image/png');

    return dataURL.replace(/^data:image\/(png|jpg);base64,/, '');
}

  setLanguage(lng) {
    this.translate.use(lng);
    this.selected = lng;
    this.genericService.setItem('SELECTED_LANGUAGE', lng);
    if (lng === 'ar') {
      this.textDir = 'rtl';
    } else {
      this.textDir = 'ltr';
    }

    this.genericService.setItem('textDir', this.textDir);
    this.update('lng');
  }

  fingerPrintChange(value) {
    this.genericService.setItem('isFingerPrintEnable', value);
  }

  isEmpty(str) {
    return (!str || 0 === str.length);
  }

  updateProfile() {
    this.keyboard.hide();
    if (
      (this.isEmpty(this.currentPassword) &&
       this.isEmpty(this.newPassword) &&
       this.isEmpty(this.confirmPassword)) ||
       (!((this.isEmpty(this.currentPassword) && this.isEmpty(this.newPassword))
        || ((this.isEmpty(this.currentPassword) && this.isEmpty(this.confirmPassword)))
        || ((this.isEmpty(this.newPassword) && this.isEmpty(this.confirmPassword)))
       )) && (!( (!this.isEmpty(this.currentPassword) && !this.isEmpty(this.newPassword) && this.isEmpty(this.confirmPassword))
       || (!this.isEmpty(this.currentPassword) && !this.isEmpty(this.confirmPassword) && this.isEmpty(this.newPassword))
       || (!this.isEmpty(this.newPassword) && !this.isEmpty(this.confirmPassword) && this.isEmpty(this.currentPassword))
       )) ) {
      if (this.newPassword && this.confirmPassword) {
        if (this.newPassword === this.confirmPassword) {
          // if (!(this.confirmPassword ===  this.genericService.getItem('password'))) {
            if (this.currentPassword ===  this.genericService.getItem('password')) {
                this.update('profile');
            } else {
              alert('Please provide correct current password to update new password');
            }
          // } else {
          //   alert('new Password should not be currentPassword');
          // }
        } else {
          alert('new Password and confirm password both should be same');
        }

      } else {
        this.update('profile');
      }
    } else {
      alert('Please enter newpassword and confirmpassword');
    }


  }

  showCurrentPassword() {
    this.currentShow = !this.currentShow;
    const x = document.getElementById('currentPassword').getElementsByTagName('input')[0]; // undefined
    if (x.type === 'password') {
      x.type = 'text';
    } else {
      x.type = 'password';
    }
   }


   showNewPassword() {
    this.newShow = !this.newShow;
    const x = document.getElementById('newPassword').getElementsByTagName('input')[0]; // undefined
    if (x.type === 'password') {
        x.type = 'text';
        // this.keyboardHeight = "291px"
      } else {
        x.type = 'password';
        // this.keyboardHeight = "0px"
      }
    }

    showConfirmPassword() {
      this.confirmShow = !this.confirmShow;
      const x = document.getElementById('confirmPassword').getElementsByTagName('input')[0]; // undefined
      if (x.type === 'password') {
        x.type = 'text';
      } else {
        x.type = 'password';
      }
    }

    public takePicture(sourcetype) {
      // Create options for the Camera Dialog
      const options = {
        quality: 50,
        sourceType: sourcetype,
        destinationType: this.camera.DestinationType.FILE_URI,
        encodingType: this.camera.EncodingType.JPEG,
        mediaType: this.camera.MediaType.PICTURE,
        saveToPhotoAlbum: false,
        correctOrientation: true
      };

      // Get the data of an image
      this.camera.getPicture(options).then((imagePath) => {
     //   this.imageURI =  'data:image/jpeg;base64,' + imagePath;
        // Special handling for Android library
        if (this.platform.is('android') &&  sourcetype === this.camera.PictureSourceType.PHOTOLIBRARY) {
          this.filePath.resolveNativePath(imagePath)
            .then(filePath => {

              console.log(filePath);
             // filePath = filePath.replace(/^file:\/\//, '');
              console.log(filePath);
              const correctPath = filePath.substr(0, filePath.lastIndexOf('/') + 1);
              console.log(correctPath);
              const currentName = imagePath.substring(imagePath.lastIndexOf('/') + 1, imagePath.lastIndexOf('?'));
              console.log(currentName);
              this.copyFileToLocalDir(correctPath, currentName, '1' + currentName);
            });
        } else {
         // this.filePath.resolveNativePath(imagePath)
        //  .then(filePath => {
            const currentName = imagePath.substr(imagePath.lastIndexOf('/') + 1);
            const correctPath = imagePath.substr(0, imagePath.lastIndexOf('/') + 1);
            this.copyFileToLocalDir(correctPath, currentName, '1' + currentName);
        //  })

        }
      }, (err) => {
        console.log(err);
        // this.presentToast('Error while selecting image.');
      });
    }


    // Copy the image to a local folder
    private copyFileToLocalDir(namePath, currentName, newFileName) {
      this.file.copyFile(namePath, currentName, cordova.file.dataDirectory, newFileName).then(success => {
        console.log(success);
        const lastImage = newFileName;
        this.zone.run(() => {
          //  this.profilePicImageUrl =  null;
        this.profilePicImageUrl = this.webView.convertFileSrc(cordova.file.dataDirectory + lastImage);
        this.genericService.setItem('profileimageUrl', this.profilePicImageUrl);
        });

        // console.log(this.imagedir);
      }, error => {
        console.log(error);

      });
    }




  // Always get the accurate path to your apps folder
  public pathForImage(img) {
    if (img === null) {
      return '';
    } else {
      console.log(cordova.file.dataDirectory + img);
      return cordova.file.dataDirectory + img;
    }
  }
   async navigateToSettings() {
    const loading = await this.genericService.getLoading();
    loading.present();
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
         this.spacesNumber = this.numberWithSpaces(this.customer.phone);
         this.spaceswithNumberForChange = this.numberWithSpaces(this.customer.phone);
         const userName = this.importContactResponse.matchedCustomers[0].firstName
         + ' ' + this.importContactResponse.matchedCustomers[0].lastName;
         const phoneNumber = this.importContactResponse.matchedCustomers[0].phone;
         const email = this.importContactResponse.matchedCustomers[0].emailAddress;
         this.detailsObject.userName = userName;
         this.detailsObject.phoneNumber = phoneNumber;
         this.detailsObject.email = email;
         console.log(this.detailsObject);
         this.detailsObject.pushNotifications = JSON.parse(this.genericService.getItem('homePageObject')).pushNotifications;
         this.genericService.setItem('homePageObject', JSON.stringify(this.detailsObject));
         this.navCtrl.navigateForward(['/settings'], { state:  {
          importcontactResponse : JSON.stringify(this.importContactResponse)
        }});
       } else {
         alert(this.importContactResponse.status.message);
       }
       loading.dismiss();
      }, err => {
        loading.dismiss();
       });
    // this.router.navigateByUrl('/settings')
  }
  navigateToTransactions() {
    this.router.navigateByUrl('/transactions');
  }

  navigateToProgramDetails() {
    this.navCtrl.navigateForward('/program-details');
  }

  navigateToNudgets() {
    this.navCtrl.navigateForward('/viewRewardsStatus');
  }

  public  async  presentActionSheet() {
    const actionSheet = await this.actionSheetCtrl.create({
      header: 'Select Image Source',
      buttons: [
        {
          text: 'Load from Library',
          handler: () => {
            this.takePicture(this.camera.PictureSourceType.PHOTOLIBRARY);
          }
        },
        {
          text: 'Use Camera',
          handler: () => {
            this.takePicture(this.camera.PictureSourceType.CAMERA);
          }
        },
        {
          text: 'Cancel',
          role: 'cancel'
        }
      ]
    });
    await actionSheet.present();
  }
}

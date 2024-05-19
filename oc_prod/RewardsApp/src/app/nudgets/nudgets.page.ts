import { NUDGETS, COUPONDISCOUNTINFO, BALANCE } from './../classes/nudget.model';
import { User } from 'src/app/classes/user';
import { Component, OnInit } from '@angular/core';
import { NavController, Events, Platform, ToastController } from '@ionic/angular';
import { HttpHandlerService } from '../services/http-handler.service';
import { GenericService } from '../services/generic.service';
import { UrlEnums } from '../enums/urlenums.enum';
import { StatusBar } from '@ionic-native/status-bar/ngx';

@Component({
  selector: 'app-nudgets',
  templateUrl: './nudgets.page.html',
  styleUrls: ['./nudgets.page.scss'],
})
export class NudgetsPage implements OnInit {

  user: User;
  textDir: string;
  nudgetsResponse: NUDGETS;
  nudgets: COUPONDISCOUNTINFO[] = [];
  isMobile = false;
  points;
  currencyvalue;
  totalPoints;
  constructor(
              private httpHandler: HttpHandlerService,
              public platform: Platform,
              public statusBar: StatusBar,
              public navCtrl: NavController,
              public toastController: ToastController,
              private genericService: GenericService) {
                this.isMobile = this.platform.is('mobile');
                this.textDir = this.genericService.getItem('textDir');
                this.user = new User();
                this.user = JSON.parse(this.genericService.getItem('user'));
  }

  ngOnInit() {
    this.getAllNudgets();
  }

  async getAllNudgets() {
    const loading = await this.genericService.getLoading();
    loading.present();
    const detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
    const phoneNumber = detailsObject.phoneNumber;
    const nudgetReq  = {
      COUPONCODEENQREQ: {
        HEADERINFO: {
          REQUESTID: this.genericService.prepareHeaderObject().requestId
        },
        COUPONCODEINFO: {
          COUPONCODE: 'ALL',
          SUBSIDIARYNUMBER: '',
          STORENUMBER: '',
          DOCSID: '828339504509865',
          RECEIPTNUMBER: '',
          RECEIPTAMOUNT: '',
          DISCOUNTAMOUNT: '',
          // CUSTOMERID: '5300706176317984764',
          // CARDNUMBER: '8580304500000010',
          // PHONE: '9110362585',
          // EMAIL: 'zubeda@OPTCULTURE.COM'
          CUSTOMERID: '',
          CARDNUMBER: detailsObject.cardNumber,
          PHONE: detailsObject.phoneNumber,
          EMAIL: detailsObject.email
          },
        PURCHASEDITEMS: [
          {
            ITEMPRICE: '0',
            ITEMCODE: '',
            QUANTITY: '0',
            ITEMDISCOUNT: '0'
           }
        ],
        USERDETAILS: {
          USERNAME: this.user.userName,
          ORGID: this.user.organizationId,
          TOKEN: this.user.token,
          sessionID: this.user.sessionID
          // USERNAME: 'Smartpromo',
          // ORGID: 'Smartpromo',
          // TOKEN: 'FEEICLOV0O6M6UIZ'
        }
      }
    };

    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.COUPON_CODE_INQUIRY_URL, nudgetReq)
      .subscribe((nudgetsResponse: NUDGETS) => {
       this.nudgetsResponse = nudgetsResponse;
       if (this.nudgetsResponse.COUPONCODERESPONSE.STATUSINFO.ERRORCODE === '0') {
        this.nudgets = this.nudgetsResponse.COUPONCODERESPONSE.COUPONDISCOUNTINFO;
        const balances: BALANCE[] = this.nudgetsResponse.COUPONCODERESPONSE.LOYALTYINFO.BALANCES;
        // tslint:disable-next-line: prefer-for-of
        for (let i = 0 ; i < balances.length ; i++) {
          if (balances[i].VALUECODE === 'Points') {
            this.points =  balances[i].AMOUNT;
          } else if (balances[i].VALUECODE === 'USD') {
            this.currencyvalue = balances[i].AMOUNT;
          }
        }
        this.totalPoints = this.nudgetsResponse.COUPONCODERESPONSE.LOYALTYINFO.LIFETIMEPOINTS;
       } else {
         alert(this.nudgetsResponse.COUPONCODERESPONSE.STATUSINFO.MESSAGE);
       }
       loading.dismiss();
      }, err => {
        loading.dismiss();
      });
   }

   copyToClipboard(text) {
    const dummy = document.createElement('textarea');
    // to avoid breaking orgain page when copying more words
    // cant copy when adding below this code
    // dummy.style.display = 'none'
    document.body.appendChild(dummy);
    // Be careful if you use texarea. setAttribute('value', value), which works with "input" does not work with "textarea". – Eduard
    dummy.value = text;
    dummy.select();
    document.execCommand('copy');
    document.body.removeChild(dummy);
    this.showToast();
  }

  async showToast() {
    const toast = await this.toastController.create({
      message: 'Copied to clipboard',
      position: 'bottom',
      duration: 500,
      showCloseButton: true
    });
    await toast.present();
  }
   backButtonEvent() {
     this.navCtrl.pop();
   }

}

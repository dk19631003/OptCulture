import { ViewRecepitComponent } from './../components/view-recepit/view-recepit.component';
import { User } from './../classes/user';
import { GenericService } from './../services/generic.service';
import { LookUp } from './../classes/lookup';
import { Header } from './../classes/header';
import { HttpHandlerService } from './../services/http-handler.service';
import { UrlEnums } from './../enums/urlenums.enum';
import { Component, OnInit, ViewChild, NgZone } from '@angular/core';
import { AlertController, PopoverController, Platform, ModalController } from '@ionic/angular';
import { Transactionhistory } from '../classes/transactionhistory';
import { Report } from '../classes/report';
import { Router } from '@angular/router';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-transactions',
  templateUrl: 'transactions.page.html',
  styleUrls: ['transactions.page.scss'],
})
export class TransactionsPage implements OnInit {

  recepitNumber;
  rows;
  tablestyle = 'bootstrap';
  columns;
  temp: any[];
  transactionHistory: Transactionhistory;
  headerObject: Header;
  lookup: LookUp;
  report: Report;
  user: User;
  transactionHistoryResponse;
  showNoTransactions = false ;
  textDir: string;
  purchaseHistory;
  purchaseHistoryResponse;
  transactionsList;
  docSid;
  storeNumber;
  isMobile = false;
  constructor(
    private httpHandler: HttpHandlerService,
    private genericService: GenericService,
    private zone: NgZone,
    private router: Router,
    public popoverController: PopoverController,
    public platform: Platform,
    public statusBar: StatusBar,
    public datePipe: DatePipe,
    private alertCtrl: AlertController,
    public modalCtrl: ModalController
  ) {
    this.isMobile = this.platform.is('mobile');
    this.purchaseHistory = new Transactionhistory();
    this.headerObject = new Header();
    this.transactionHistory = new Transactionhistory();
    this.lookup = new LookUp();
    this.report = new Report();
    this.user = new User();
    this.user = JSON.parse(this.genericService.getItem('user'));
    this.temp = this.rows;
  }

  transform(date) {
    return this.datePipe.transform(date, 'dd MMM y');
  }

  splitEST(str) {
    return str.split(' EST')[0].replace(/-/g, '/');
  }

  ngOnInit() {
    this.getTransactionHistory();
  }

  backButtonEvent() {
    this.router.navigateByUrl('app/tabs/tab5');
  }
  ionViewWillEnter() {
    this.textDir = this.genericService.getItem('textDir');
  }

  switchStyle() {
    if (this.tablestyle === 'dark') {
      this.tablestyle = 'bootstrap';
    } else {
      this.tablestyle = 'dark';
    }
  }



  getRowClass(row) {
    return row.gender === 'male' ? 'male-row' : 'female-row';
  }

  async open(row) {
    // let alert = await this.alertCtrl.create({
    //   header: 'Row',
    //   message: `${row.name} is ${row.age} years old!`,
    //   buttons: ['OK']
    // });
    // await alert.present();
   console.log(row);
   // alert("${row.name} is ${row.age} years old!")
  }



  async getTransactionHistory() {
    const loading = await this.genericService.getLoading();
    loading.present();
    this.headerObject = this.genericService.prepareHeaderObject();
    const detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
    const phoneNumber = detailsObject.phoneNumber;
    this.lookup.membershipNumber = detailsObject.cardNumber;
    this.lookup.phone = phoneNumber;
    this.lookup.emailAddress = '';
    this.report.offset = 0;
    this.report.maxRecords = 1000;
    this.report.source = 'ALL';
    this.report.mode = 'ALL';
    this.report.serviceType = 'ALL';
    this.report.startDate = '';
    this.report.endDate = '';
    this.transactionHistory.report =  this.report;
    this.transactionHistory.user = this.user;
    this.transactionHistory.header = this.headerObject;
    this.transactionHistory.lookup = this.lookup;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.TRANSACTION_HISTORY_URL, this.transactionHistory)
      .subscribe(transactionHistoryResponse => {
        this.transactionHistoryResponse = transactionHistoryResponse;
        if (this.transactionHistoryResponse.status.errorCode === '0') {
          this.transactionHistoryResponse = transactionHistoryResponse;
          if (this.transactionHistoryResponse.matchedCustomers[0].transactions.length > 0) {
            this.zone.run(() => {
              this.rows = this.transactionHistoryResponse.matchedCustomers[0].transactions
              .filter(
                transaction => transaction.amount.type === 'Issuance'
                                || transaction.amount.type === 'Redemption'
                                || transaction.amount.type === 'Return'
                                || transaction.amount.type === 'Adjustment'
                                || transaction.amount.type === 'Bonus');
              console.log(this.rows);
              if (this.rows.length <= 0) {
                this.showNoTransactions = true;
              }
            });
          } else {
            this.showNoTransactions = true;
          }
       } else {
        this.showNoTransactions = true;
        alert(this.transactionHistoryResponse.status.message);
       }
        loading.dismiss();
      }, err => {
        loading.dismiss();
      });
  }

  openErecieptUrl(url) {
    window.open(url);
  }
  async  openViewRecieptPopup(erecepiturl) {
    const popover = await this.modalCtrl.create({
      component: ViewRecepitComponent,
      componentProps: {url : erecepiturl},
      cssClass: 'openReciept'
    });

    return await popover.present();
  }

  getPurchaseHistory(row, ev: any) {

    // if(this.docSid)
    // {
    //   this.recepitNumber = "";
    // }
    // else
    // {
    //   this.docSid = "";
    // }
    this.docSid = row.docSID;
    this.recepitNumber = row.receiptNumber;
    this.storeNumber =  row.storeNumber;
    this.headerObject = this.genericService.prepareHeaderObject();
    this.lookup.emailAddress = '';
    this.lookup.phone = '';
    this.lookup.membershipNumber = '';
    this.lookup.receiptNumber = '';
    this.lookup.docSID = this.docSid ? this.docSid : '87168172386123';
    this.lookup.storeNumber = this.storeNumber;
    this.lookup.subsidiaryNumber = '';
    this.report.offset = 0;
    this.report.maxRecords = 1000;
    this.report.startDate = '';
    this.report.endDate = '';
    this.purchaseHistory.report =  this.report;
    this.purchaseHistory.user = this.user;
    this.purchaseHistory.header = this.headerObject;
    this.purchaseHistory.lookup = this.lookup;
    this.httpHandler.httpPostRequestWithBody(
      UrlEnums.BASE_URL + UrlEnums.PURCHASE_HISTORY_URL, this.purchaseHistory)
      .subscribe(purchaseHistoryResponse => {
        console.log(purchaseHistoryResponse);
        this.purchaseHistoryResponse = purchaseHistoryResponse;
        if (this.purchaseHistoryResponse.status.errorCode === '0') {
          this.openViewRecieptPopup(purchaseHistoryResponse);
          // if(this.purchaseHistoryResponse.matchedCustomers[0].transactions.length>0)
          // {
          //   let length = this.purchaseHistoryResponse.matchedCustomers[0].transactions.length;
          //  this.transactionsList = this.purchaseHistoryResponse.matchedCustomers[0].transactions[length-1];
          //  this.paymentDetails = this.purchaseHistoryResponse.matchedCustomers[0].payment;
          // }
          // else
          // {
          //   this.showNoTransactions = true;
          // }
       } else {
        alert(this.purchaseHistoryResponse.status.message);
       }
      });
  }
}

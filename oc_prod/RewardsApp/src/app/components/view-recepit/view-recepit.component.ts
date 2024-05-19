import { GenericService } from 'src/app/services/generic.service';
import { UrlEnums } from './../../enums/urlenums.enum';
import { Transactionhistory } from './../../classes/transactionhistory';
import { Route, Router } from '@angular/router';
import { HttpHandlerService } from './../../services/http-handler.service';
import { LookUp } from './../../classes/lookup';
import { Header } from './../../classes/header';
import { Component, OnInit, NgZone, ViewChild } from '@angular/core';
import { Report } from 'src/app/classes/report';
import { User } from 'src/app/classes/user';
import { NavParams, PopoverController, ModalController } from '@ionic/angular';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-view-recepit',
  templateUrl: 'view-recepit.component.html',
  styleUrls: ['view-recepit.component.scss'],
})
export class ViewRecepitComponent implements OnInit {

  headerObject: Header;
  lookup: LookUp;
  report: Report;
  user: User;

  docSid: string;
  recepitNumber: string;
  paymentDetails;
  storeNumber: string;
  items;
  purchaseHistoryResponse;
  transactionsList;
  Url;
  urlSafe: SafeResourceUrl;
  constructor(  public navParams: NavParams,
                private popCtrl: PopoverController,
                private zone: NgZone,
                public sanitizer: DomSanitizer,
                public modalCtrl: ModalController,
                public genericService: GenericService,
                private router: Router) {
      this.headerObject = new Header();
      this.lookup = new LookUp();
      this.report = new Report();
      this.user = new User();
      this.user = JSON.parse(this.genericService.getItem('user'));

     }

  ngOnInit() {

    //  this.purchaseHistoryResponse = this.navParams.data.purchaseHistoryResponse;
     this.urlSafe = this.sanitizer.bypassSecurityTrustResourceUrl(this.navParams.data.url) ;
    //  this.storeNumber =  this.navParams.data.storeNumber;
    //  if (this.purchaseHistoryResponse.matchedCustomers[0].transactions.length > 0) {
    //         const length = this.purchaseHistoryResponse.matchedCustomers[0].transactions.length;
    //         this.transactionsList = this.purchaseHistoryResponse.matchedCustomers[0].transactions[length - 1];
    //         this.paymentDetails = this.purchaseHistoryResponse.matchedCustomers[0].payment;
    //       }
    //  console.log(this.storeNumber);
      // this.getPurchaseHistory();
  }

  dismiss() {
   this.modalCtrl.dismiss();
  }


}

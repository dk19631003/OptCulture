import { GenericService } from 'src/app/services/generic.service';
import { Component, OnInit } from '@angular/core';
import { ModalController } from '@ionic/angular';

@Component({
  selector: 'app-fingerprint-login-confirmation-popup',
  templateUrl: './fingerprint-login-confirmation-popup.component.html',
  styleUrls: ['./fingerprint-login-confirmation-popup.component.scss'],
})
export class FingerprintLoginConfirmationPopupComponent implements OnInit {

  detailsObject;
  userName = '';
  constructor(private genericService:GenericService,
    private modalCtrl: ModalController
    ) { }

  ngOnInit() {
    this.detailsObject = JSON.parse(this.genericService.getItem('homePageObject'));
    if(this.detailsObject && this.detailsObject.userName) {
      this.userName = this.detailsObject.userName;
    }
  }

  dismiss(){
    this.modalCtrl.dismiss();
  }

  openFingerPrint(){
    this.genericService.shouldOpenFingerPrint.next(true);
    this.modalCtrl.dismiss();
  }

}

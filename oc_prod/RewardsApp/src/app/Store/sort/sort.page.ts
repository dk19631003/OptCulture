import { NavParams, ModalController, Platform, NavController, Events } from '@ionic/angular';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GenericService } from 'src/app/services/generic.service';

@Component({
  selector: 'app-sort',
  templateUrl: './sort.page.html',
  styleUrls: ['./sort.page.scss'],
})
export class SortPage implements OnInit {

  type;
  lines = 'none';
  backbuttonSubscription;
  order;
  orderType;
  isSelected: boolean;
  textDir;
  isMobile: boolean;
  constructor(
    public platform: Platform,
    public route: ActivatedRoute,
    public navCtrl: NavController,
    public events: Events,
    public genericService: GenericService
    ) {
      this.type = 'Type';
      this.orderType = 'Title';
      this.order = 'ASC';
      this.isMobile = this.platform.is('mobile');
     }

  ngOnInit() {
    this.textDir = this.genericService.getItem('textDir');
    this.route.params.subscribe(params => {
      this.orderType = params.orderType;
      this.order = params.order;
      this.setSelected();
      });

    this.backbuttonSubscription = this.platform.backButton.subscribe(async () => {
     this.backButtonEvent();
    });
  }

  changeType(itemType) {
    this.type = itemType;
  }

  setSelected() {
    if (this.order || this.orderType ) {
      this.isSelected = true;
    } else {
      this.isSelected = false;
    }
  }

  clearAll() {
    this.orderType = undefined;
    this.order = undefined;
    this.setSelected();
  }

  changeOrderType() {
     this.setSelected();
  }

  changeOrder() {
    this.setSelected();
  }
 dismiss() {
   const sortDetails = {
     orderType: this.orderType,
     order: this.order
   };
   this.events.publish('sortby', sortDetails);
   this.navCtrl.pop();
 }

 backButtonEvent() {
    this.navCtrl.pop();
  }

 



}

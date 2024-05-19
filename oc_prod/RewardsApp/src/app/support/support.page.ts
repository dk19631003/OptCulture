import { GenericService } from './../services/generic.service';
import { Component, OnInit } from '@angular/core';
import { NavController, Platform } from '@ionic/angular';

@Component({
  selector: 'app-support',
  templateUrl: './support.page.html',
  styleUrls: ['./support.page.scss'],
})
export class SupportPage implements OnInit {

  backbuttonsubscription;
  isMobile = this.gnenericService.ismobileWeb();
  constructor(public navCtrl:NavController,
    public gnenericService: GenericService,
    public platform:Platform) {
      this.backbuttonsubscription = this.platform.backButton.subscribe(res=>{
        this.backButtonEvent();
      })
     }

  ngOnInit() {
  }

  ionViewWillLeave()
  {
    this.backbuttonsubscription.unsubscribe();
  }

  backButtonEvent()
  {
    this.navCtrl.pop();
  }
}

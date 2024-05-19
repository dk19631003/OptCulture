import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NavController, Platform, ToastController } from '@ionic/angular';
import { StatusBar } from '@ionic-native/status-bar/ngx';

@Component({
  selector: 'app-offerdetail',
  templateUrl: './offerdetail.page.html',
  styleUrls: ['./offerdetail.page.scss'],
})
export class OfferdetailPage implements OnInit {
  @ViewChild(RouterOutlet) outlet: RouterOutlet;
  height = '50px';
  offerDetail;
  from;
  htmltext = '<input type="text" name="name">';
  monospace = 'monospace';
  detailsObject;
  constructor(public route: ActivatedRoute,
              public navCtrl: NavController,
              public platform: Platform,
              public statusBar: StatusBar,
              public toastController: ToastController,
              public router: Router) {
      const params = this.router.getCurrentNavigation().extras.state;
      if (params) {
        console.log(params);
        this.offerDetail = JSON.parse(params.offerdata);
        this.from = params.where;
      }
   }

   goToBrowser(url) {
    if (url) {
    window.open(url);
    }
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

   backButtonEvent() {
    //  this.navCtrl.pop();
     if (this.from === 'home') {
     this.router.navigateByUrl('/app/tabs/home');
     } else if (this.from === 'offers') {
       this.router.navigateByUrl('/app/tabs/tab2');
 } else {
        this.navCtrl.pop();
 }
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
  ngOnInit() {

  }

}

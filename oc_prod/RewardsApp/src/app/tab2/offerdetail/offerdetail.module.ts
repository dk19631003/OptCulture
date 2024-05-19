import { TranslateModule } from '@ngx-translate/core';
import { MainPipe } from './../../pipes/sanitize.module';
import { NgxBarcodeModule } from 'ngx-barcode';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Routes, RouterModule } from '@angular/router';

import { IonicModule } from '@ionic/angular';

import { OfferdetailPage } from './offerdetail.page';

const routes: Routes = [
  {
    path: '',
    component: OfferdetailPage
  }
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RouterModule.forChild(routes),
    MainPipe,
    NgxBarcodeModule,
    TranslateModule,
  ],
  declarations: [OfferdetailPage]
})
export class OfferdetailPageModule {}

import { TranslateModule } from '@ngx-translate/core';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Tab3Page } from './tab3.page';
import { NgxBarcodeModule } from 'ngx-barcode';

@NgModule({
  imports: [
    IonicModule,
    CommonModule,
    FormsModule,
    NgxBarcodeModule,
    TranslateModule,
    RouterModule.forChild([{ path: '', component: Tab3Page }]),
  ],
  declarations: [Tab3Page]
})
export class Tab3PageModule {}

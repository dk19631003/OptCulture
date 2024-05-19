import { Injectable } from '@angular/core';
import { LoadingController } from '@ionic/angular';
@Injectable({
    providedIn: 'root'
  })
export class LoaderService {
    private loading;
    constructor(public loadingController: LoadingController) {
      
    }
     show() {
        this.loadingController.create({
            message: 'Please Wait'
          }).then((overlay)=>{
              console.log(overlay)
            this.loading = overlay;
            this.loading.present(); 
            console.log(this.loading)
          });
           
    }
     hide() {
        console.log(this.loading)
        if(this.loading)
         this.loading.dismiss();
    }
}
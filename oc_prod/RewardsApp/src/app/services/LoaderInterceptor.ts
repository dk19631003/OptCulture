import { UrlEnums } from 'src/app/enums/urlenums.enum';
import { LoadingController } from '@ionic/angular';
import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
@Injectable()
export class LoaderInterceptor implements HttpInterceptor {
    private loading;
    constructor(  public loadingController: LoadingController) {
         }
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {


        // this.loadingController.create({
        //     spinner: 'crescent',
        //     backdropDismiss: true,
        //     showBackdrop: true,
        //     translucent : true,
        //     duration: 40000
        //   }).then((overlay) => {
        //     if (this.loading === undefined) {
        //         // console.log("loading present for"+req.url)
        //         console.log(!req.url.includes('processLanguage'));
        //         this.loading = overlay;
        //         if ( (req.method === 'POST') ||
        //         (req.method === 'GET') ||
        //         !(req.url.includes('processLanguage')
        //          && req.url.includes(UrlEnums.WEB_PORTAL_BRANDING_URL)
        //          && req.url.includes(UrlEnums.GET_TOKEN_URL)
        //          ) && req.url.includes(UrlEnums.BASE_URL)
        //          ) {
        //             this.loading.present();
        //         }
        //      } else {
        //         this.loading.dismiss();
        //         // console.log("loading present for"+req.url)
        //         this.loading = overlay;
        //         if ( (req.method === 'POST') ||
        //         (req.method === 'GET' ) ||
        //         !(req.url.includes('processLanguage')
        //         && req.url.includes(UrlEnums.WEB_PORTAL_BRANDING_URL)
        //         && req.url.includes(UrlEnums.GET_TOKEN_URL)
        //         )
        //         && req.url.includes(UrlEnums.BASE_URL)
        //         ) {
        //             this.loading.present();
        //         }
        //      }
        //   });
        return next.handle(req);
    }
}

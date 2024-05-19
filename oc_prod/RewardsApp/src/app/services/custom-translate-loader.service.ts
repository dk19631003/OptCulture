import { GenericService } from './generic.service';
import { UrlEnums } from './../enums/urlenums.enum';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { TranslateLoader } from '@ngx-translate/core';
import { Constants } from '../enums/constants.enum';

@Injectable({
  providedIn: 'root'
})
export class CustomTranslateLoaderService implements TranslateLoader {

  langauageResponse;
  tokenDetails;
  contentHeader = new HttpHeaders()
  // tslint:disable-next-line: max-line-length
  .set('X-OptCulture-ApiKey', 'Njc3QThENkUtNDEyMC00QTg2LThFRTEtRDQ1RUQxNTY2OTI3OjQzYzBmNjYyZDg5MWVmMDczNmE3MjJkNzZmZmYwZDQ3YzA1Y2NiYzg3ZDJhNWYyNjk4OGNlNjNjODNlM2MyZmU=');
  constructor(private http: HttpClient,
              public genericService: GenericService
    ) { }

  getTranslation(lang: string): Observable<any> {

    const apiAddress = UrlEnums.BASE_URL + UrlEnums.OPT_LANG_URL;
    this.tokenDetails = {};
    return Observable.create(observer => {
        if (this.genericService && this.genericService.getSessionItem('portalBranding')) {
            const webportalBrandingResponse = JSON.parse(this.genericService.getSessionItem('portalBranding'));
            this.tokenDetails.userName = webportalBrandingResponse.username;
            this.tokenDetails.organizationId = webportalBrandingResponse.orgId;
            this.tokenDetails.token = webportalBrandingResponse.token;
        } else {
            this.tokenDetails.userName = Constants.optAccountName ;
            this.tokenDetails.organizationId = Constants.orgId;
            this.tokenDetails.token = Constants.token;
        }
        // this.tokenDetails.userName = "ramakrishna"
        // this.tokenDetails.organizationId ="ocqa"
        // this.tokenDetails.token = "BHANZ1UC1BJPZBJO"
        // this.genericService.setItem("user",JSON.stringify(this.tokenDetails));
        const langObject = {
            Head: {
                 user:
                  {
                       userName: this.tokenDetails.userName,
                        organizationId: this.tokenDetails.organizationId,
                        token: this.tokenDetails.token
                    },
                        languageCode: lang
                    }
                };

        // this.http.post(apiAddress, langObject).subscribe((res) => {
        //     this.langauageResponse =  res;
        //     console.log(this.langauageResponse);
        //     if (this.langauageResponse && this.langauageResponse.RESPONSEINFO.STATUS.ERRORCODE === '0') {
        //         observer.next(this.langauageResponse.RESPONSEINFO.STATUS.LANGUAGEJSON);
        //         observer.complete();
        //     } else {
            console.log('failed to retrieve from api, switch to local');
                // tslint:disable-next-line: no-shadowed-variable
            this.http.get('./assets/i18n/en.json').subscribe((res: Response) => {
                    observer.next(res);
                    observer.complete();
                });
            // }
        // },
        //     error => {
        //         console.log('failed to retrieve from api, switch to local');
        //         this.http.get('./assets/i18n/en.json').subscribe((res: Response) => {
        //             observer.next(res);
        //             observer.complete();
        //         });
        //     }
        // );
    });
}
}

import { IBanner } from './../classes/IBanner';
import { UrlEnums } from './../enums/urlenums.enum';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpHandlerService {

  brandsResponse;
  constructor(private http: HttpClient) { }

  httpPostRequestWithBody(url: string, body: any) {
    const headers  = this.setHeaders();
    return this.http.post(url, body,
    {
      headers
    });
  }

  setHeaders() {
      return new HttpHeaders()
       .set('Content-Type', 'application/json');
  }

  httpGETRequest(url: string) {
    const headers = this.setHeaders();
    return this.http.get(url, {
      headers
     });
  }

  httpGETRequestWithBody(url: string, body: any) {
    const headers = this.setHeaders();
    return this.http.get(url, {
      headers  ,
     observe: body
     },
     );
  }


    getBrandsResponse() {

      return new Observable((observ) => {
          observ.next(this.brandsResponse);
      });
    }
}

import { IBanner } from './../classes/IBanner';
import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpHandlerService } from './http-handler.service';

@Injectable()
export class BannersResolver implements Resolve<any[]> {
  constructor(private httpHandler: HttpHandlerService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<any[]> {
      return null;
  }
}
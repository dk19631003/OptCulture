import { GenericService } from './../services/generic.service';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate  {
  constructor(public genericService: GenericService) {

  }
  canActivate() {
    if (this.genericService.getItem('homePageObject') ||
    this.genericService.getItem('rememberMe') === 'true' )  {
     return true;
    } else {
     return false;
    }
  }
}

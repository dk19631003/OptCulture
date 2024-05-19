import {  BannersResolver } from '../services/banners-resolver';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AuthGuard } from '../guards/auth.guard';

const routes: Routes = [
  { path: 'notifications', loadChildren: '../notifications/notifications.module#NotificationsPageModule', canActivate: [AuthGuard] },
  { path: 'transactions', loadChildren: '../transactions/transactions.module#TransactionsPageModule' },
  { path: 'countries', loadChildren: '../Store/countries/countries.module#CountriesPageModule', canActivate: [AuthGuard] },
  { path: 'cities/:countryName', loadChildren: '../Store/cities/cities.module#CitiesPageModule' },
  { path: 'brands/:city', loadChildren: '../Store/brands/brands.module#BrandsPageModule' },
  { path: 'store-info', loadChildren: '../Store/store-info/store-info.module#StoreInfoPageModule', canActivate: [AuthGuard] },
  { path: 'profile', loadChildren: '../profile/profile.module#ProfilePageModule', canActivate: [AuthGuard] },
  { path: 'settings', loadChildren: '../profile/settings/settings.module#SettingsPageModule', canActivate: [AuthGuard] },
  { path: 'offerdetail', loadChildren: '../tab2/offerdetail/offerdetail.module#OfferdetailPageModule' , canActivate: [AuthGuard] },
  { path: 'support', loadChildren: '../support/support.module#SupportPageModule' , canActivate: [AuthGuard] },
//   { path: 'dateselection', loadChildren: './components/dateselection/dateselection.module#DateselectionPageModule' },
  { path: 'filter', loadChildren: '../Store/filter/filter.module#FilterPageModule' , canActivate: [AuthGuard] },
  { path: 'sort', loadChildren: '../Store/sort/sort.module#SortPageModule' , canActivate: [AuthGuard]},
  { path: 'viewRewardsStatus', loadChildren: '../nudgets/nudgets.module#NudgetsPageModule' , canActivate: [AuthGuard] },
  // { path: 'documentupload', loadChildren: './pages/documentupload/documentupload.module#DocumentuploadPageModule' },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  providers: [ BrowserAnimationsModule ],
  exports: [RouterModule]
})
export class PageRoutingModule {}

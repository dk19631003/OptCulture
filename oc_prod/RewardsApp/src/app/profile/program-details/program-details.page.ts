import { GenericService } from 'src/app/services/generic.service';
import { Component, OnInit } from '@angular/core';
import { NavController } from '@ionic/angular';

@Component({
  selector: 'app-program-details',
  templateUrl: './program-details.page.html',
  styleUrls: ['./program-details.page.scss'],
})
export class ProgramDetailsPage implements OnInit {

  programList = [
    {priviliegeTier : 'Tier1', earnrule: '1 Point for $1.00' , redeemrule: ''},
    // {priviliegeTier : 'Tier2', earnrule: '2 Points for $1.00' , redeemrule: '$20.00 for 400 points'}
  ];

  thresholdList = [
    {Rule: 'On reaching level of 300 of Total earned points',
     BonusAmount: '10',
     ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
     // tslint:disable-next-line: max-line-length
     {Rule: 'On reaching level of 600 of Total earned points',
      BonusAmount: '20',
      ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 900 of Total earned points',
     BonusAmount: '20',
     ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 1200 of Total earned points',
     BonusAmount: '25',
     ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 1500 of Total earned points',
     BonusAmount: '25',
     ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 1800 of Total earned points',
     BonusAmount: '25',
     ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 2100 of Total earned points',
     BonusAmount: '25',
     ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 2400 of Total earned points',
     BonusAmount: '25',
     ExpirationRule: 'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 2700 of Total earned points',
    BonusAmount: '25', ExpirationRule:
    'After 3 Month(s) of earning, at the end of the month'},
    {Rule: 'On reaching level of 3000 of Total earned points',
     BonusAmount: '25',
     ExpirationRule: 'After 1 Month(s) of earning, at the end of the month'}];
     
  isMobile = false;
  constructor(public navCtrl: NavController) { }

  ngOnInit() {
  }

  backButtonEvent() {
    this.navCtrl.pop();
  }
}

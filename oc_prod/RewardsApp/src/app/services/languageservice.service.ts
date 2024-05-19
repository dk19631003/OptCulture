import { GenericService } from './generic.service';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

const LNG_KEY = 'SELECTED_LANGUAGE';
@Injectable({
  providedIn: 'root'
})
export class LanguageserviceService {

  selected;
  constructor(private translate: TranslateService ,
              public genericService: GenericService
    ) { }


  getLanguages() {
    return [
      { text: 'English', value: 'en', img: 'assets/imgs/en.png' },
      { text: 'Telugu', value: 'te', img: 'assets/imgs/de.png' },
    ];
  }

  setLanguage(lng) {
    this.translate.use(lng);
    this.selected = lng;
    this.genericService.setItem(LNG_KEY, lng);
  }
}

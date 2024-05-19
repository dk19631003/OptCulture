import { NgModule } from '@angular/core';
import { SanitizePipe } from './sanitize.pipe';



@NgModule({
  declarations:[SanitizePipe], // <---
  imports:[],
  exports:[SanitizePipe] // <---
})

export class MainPipe{}
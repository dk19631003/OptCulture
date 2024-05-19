import { Directive, HostBinding } from '@angular/core';

@Directive({
  selector: '[appShowHideInput]'
})
export class ShowHideInputDirective {

  @HostBinding() type: string;
     
  constructor(){
      this.type='text';
  }
  
  changeType(type:string): void {
      this.type = type;
  }

}

import { Directive, ElementRef, Input, OnInit } from '@angular/core';
import { LoginService } from '../service/login.service';

@Directive({
  selector: '[isAuthorized]',
  providers: [LoginService]
})
export class IsAuthorizedDirective implements OnInit {
  @Input('isAuthorized') role: string;

  constructor(private _elementRef: ElementRef, private loginService: LoginService) {
  }

  ngOnInit(): void {
    if (this.role && this.role.trim() !== '' && !this.loginService.isAuthorized([this.role])) {
      let el: HTMLElement = this._elementRef.nativeElement;
      el.parentNode.removeChild(el);
    }
  }
}

import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FingerprintLoginConfirmationPopupComponent } from './fingerprint-login-confirmation-popup.component';

describe('FingerprintLoginConfirmationPopupComponent', () => {
  let component: FingerprintLoginConfirmationPopupComponent;
  let fixture: ComponentFixture<FingerprintLoginConfirmationPopupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FingerprintLoginConfirmationPopupComponent ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FingerprintLoginConfirmationPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferdetailPage } from './offerdetail.page';

describe('OfferdetailPage', () => {
  let component: OfferdetailPage;
  let fixture: ComponentFixture<OfferdetailPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OfferdetailPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OfferdetailPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

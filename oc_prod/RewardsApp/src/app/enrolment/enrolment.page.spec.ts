import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnrolmentPage } from './enrolment.page';

describe('EnrolmentPage', () => {
  let component: EnrolmentPage;
  let fixture: ComponentFixture<EnrolmentPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EnrolmentPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnrolmentPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

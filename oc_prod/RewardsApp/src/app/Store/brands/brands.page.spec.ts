import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BrandsPage } from './brands.page';

describe('BrandsPage', () => {
  let component: BrandsPage;
  let fixture: ComponentFixture<BrandsPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BrandsPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BrandsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StoreInfoPage } from './store-info.page';

describe('StoreInfoPage', () => {
  let component: StoreInfoPage;
  let fixture: ComponentFixture<StoreInfoPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StoreInfoPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StoreInfoPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

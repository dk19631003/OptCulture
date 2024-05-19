import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SortPage } from './sort.page';

describe('SortPage', () => {
  let component: SortPage;
  let fixture: ComponentFixture<SortPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SortPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SortPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

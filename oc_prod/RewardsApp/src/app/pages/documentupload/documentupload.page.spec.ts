import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentuploadPage } from './documentupload.page';

describe('DocumentuploadPage', () => {
  let component: DocumentuploadPage;
  let fixture: ComponentFixture<DocumentuploadPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DocumentuploadPage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentuploadPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

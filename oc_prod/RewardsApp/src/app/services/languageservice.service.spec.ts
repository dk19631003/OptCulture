import { TestBed } from '@angular/core/testing';

import { LanguageserviceService } from './languageservice.service';

describe('LanguageserviceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LanguageserviceService = TestBed.get(LanguageserviceService);
    expect(service).toBeTruthy();
  });
});

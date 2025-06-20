import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoAdmin } from './no-admin';

describe('NoAdmin', () => {
  let component: NoAdmin;
  let fixture: ComponentFixture<NoAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NoAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

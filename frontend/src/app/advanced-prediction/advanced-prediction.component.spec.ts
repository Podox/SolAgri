import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvancedPredictionComponent } from './advanced-prediction.component';

describe('AdvancedPredictionComponent', () => {
  let component: AdvancedPredictionComponent;
  let fixture: ComponentFixture<AdvancedPredictionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdvancedPredictionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AdvancedPredictionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
//
//  FormEditText.m
//  nics_iOS
//
//

#import "FormDamageInformation.h"

@implementation FormDamageInformation

- (id)initWithTitle:(NSString *)title options:(NSArray *)options
{
    self = [super init];
    if (self) {
        _options = options;
        _title = title;
        [self setup];
    }
    return self;
}

- (void)setup {
    [super setup];
    self.type = @"damageInformation";
    
    _DamageInfoObjects = [[NSMutableArray alloc]init];
    
    _DamageTypes = [NSArray arrayWithObjects: NSLocalizedString(@"Car",nil),
                                            NSLocalizedString(@"Commercial - Multi-Story",nil),
                                            NSLocalizedString(@"Commercial - Single-Story",nil),
                                            NSLocalizedString(@"Commercial - Single-Story",nil),
                                            NSLocalizedString(@"Double Wide Trailer" ,nil),
                                            NSLocalizedString(@"Motorhome",nil),
                                            NSLocalizedString(@"Other - Barn",nil),
                                            NSLocalizedString(@"Other - Garage",nil),
                                            NSLocalizedString(@"Other - Out Building",nil),
                                            NSLocalizedString(@"Residence - Multi-Family",nil),
                                            NSLocalizedString(@"Residence - Multi-Story Multi-Family",nil),
                                            NSLocalizedString(@"Residence - Single Family",nil),
                                            NSLocalizedString(@"Single Wide Trailer",nil),
                                            NSLocalizedString(@"Trailer",nil),
                                            NSLocalizedString(@"Truck",nil),
                                            NSLocalizedString(@"Van",nil),
                                            nil];
    _DamageAmounts = [NSArray arrayWithObjects:NSLocalizedString(@"Minor: 10 percent",nil),
                                                NSLocalizedString(@"Major: 10 to 50 percent",nil),
                                                NSLocalizedString(@"Destroyed: 50+ percent",nil),
                                                nil];
    
    _DamageTypeMenu = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"Select Type",nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
    _DamageTypeMenu.tag = 50;
    for( NSString *title in _DamageTypes)  {
        [_DamageTypeMenu addButtonWithTitle:title];
    }
    [_DamageTypeMenu addButtonWithTitle:NSLocalizedString(@"Cancel",nil)];
    _DamageTypeMenu.cancelButtonIndex = [_DamageTypes count];
    
    _DamageAmountMenu = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"Select Type",nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
    for( NSString *title in _DamageAmounts)  {
        [_DamageAmountMenu addButtonWithTitle:title];
    }
    [_DamageAmountMenu addButtonWithTitle:NSLocalizedString(@"Cancel",nil)];
    _DamageAmountMenu.cancelButtonIndex = [_DamageAmounts count];
    
    _ReselectingInfo = FALSE;
    
    [[self subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    [[NSBundle mainBundle] loadNibNamed:@"FormDamageInformation" owner:self options:nil];
      
    for(UIView * subview in self.view.subviews) {
        [subview setUserInteractionEnabled:YES];
        [self addSubview:subview];
    }
//    self.field.delegate = self;
    
//    CGRect DamageReportFrame = self.superview.superview.frame; //+= buttonHeight;
//    DamageReportFrame.size.width = 512;
//    self.superview.superview.frame = DamageReportFrame;
}

- (NSString *) getData {
    
    NSMutableArray *DamageInfo = [[NSMutableArray alloc]init];
    
    for(int i = 0; i < [_DamageInfoObjects count]; i++){
        
        DamageInformation* newDamageInformation =[[DamageInformation alloc] init];
        DamageInformationObject* currentObject = [_DamageInfoObjects objectAtIndex:i];
        
        newDamageInformation.propertyType = currentObject.DamageTypeButton.titleLabel.text;
        newDamageInformation.damageType = currentObject.DamageAmountButton.titleLabel.text;
        newDamageInformation.id = [NSNumber numberWithInt:0];
        
        [DamageInfo addObject:newDamageInformation];
    }
    
    return [DamageInfo copy];
}

//called when view is loaded each time, even on tablet.
- (void)setData: (NSArray *) data readOnly: (bool)readOnly{
    
    _isDraft = !readOnly;
    
    //clear old buttons before making new ones
    for(DamageInformationObject* dmgInfoObj in _DamageInfoObjects) {
        [dmgInfoObj.DamageTypeButton removeFromSuperview];
        [dmgInfoObj.DamageAmountButton removeFromSuperview];
    }
    [_DamageInfoObjects removeAllObjects];
    
    int y = 0;
    int viewWidth = self.superview.frame.size.width;
    
    CGRect buttonFrame = self.addItemButton.frame;
    buttonFrame.origin.x = viewWidth/2 - buttonFrame.size.width/2;
    self.addItemButton.frame = buttonFrame;
    
    if(_isDraft) {
        [self.addItemButton setHidden:NO];
        [self.addItemButton setEnabled:YES];
        [self.addItemButton bringSubviewToFront:self];
        y += self.addItemButton.frame.size.height + 8;
    } else {
        [self.addItemButton setHidden:YES];
        [self.addItemButton setEnabled:NO];
    }
    
    for(NSDictionary* dataDictionary in data) {
        NSError* error = nil;
        DamageInformation *damageInfo = [[DamageInformation alloc] initWithDictionary:dataDictionary error: &error];
        DamageInformationObject *newDamageInfoObject = [DamageInformationObject new];
        
        [self CreateNewDamageInformationObject: newDamageInfoObject: damageInfo.propertyType];
        [newDamageInfoObject.DamageAmountButton setTitle:damageInfo.damageType forState:UIControlStateNormal];
    }
    
    
    
    CGRect viewFrame = CGRectMake(0, 0, self.superview.frame.size.width, 40);
//    for(UIView * subview in self.subviews) {
    for(NSDictionary* dataDictionary in data) {
        viewFrame.size.height += 40;
    }
    viewFrame.size.width = self.superview.frame.size.width;

    if(viewFrame.size.height <= 72){
        viewFrame.size.height = 72;
    }
    
    self.frame = viewFrame;
    self.superview.frame = viewFrame;
}

//could move this over to [DamageInformationObject init]
-(void)CreateNewDamageInformationObject:(DamageInformationObject *)newDamageInfoObject : (NSString*)typeTitle{
    float buttonHeight = 40;

    int viewWidth = self.superview.frame.size.width;
    
    UIButton *typeButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    typeButton.titleLabel.frame = typeButton.frame;
    [typeButton setTitle:typeTitle forState:UIControlStateNormal];
//    typeButton.titleLabel.font = [UIFont systemFontOfSize:12];
    typeButton.frame = CGRectMake(0, buttonHeight * (_DamageInfoObjects.count+1), viewWidth/2, buttonHeight);
    typeButton.backgroundColor = [UIColor darkGrayColor];
    typeButton.tag =_DamageInfoObjects.count;
    [self addSubview:typeButton];
    if(_isDraft){
        [typeButton addTarget:self action:@selector(ChangeDamageType:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    UIButton *amountButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    amountButton.frame = CGRectMake(viewWidth / 2, buttonHeight * (_DamageInfoObjects.count+1), viewWidth/2, buttonHeight);
    amountButton.backgroundColor = [UIColor lightGrayColor];
    amountButton.tag =_DamageInfoObjects.count;
    [self addSubview:amountButton];
    if(_isDraft){
        [amountButton addTarget:self action:@selector(ChangeDamageAmount:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    //adjust frame for collabsible view
    CGRect viewFrame = self.frame;
    viewFrame.size.height += buttonHeight;
    viewFrame.size.width = viewWidth;
    
    self.frame = viewFrame;
    self.superview.frame = viewFrame;
    
    CGRect collapseClickViewFrame = self.superview.superview.frame;
    collapseClickViewFrame.size.width = self.view.frame.size.width;
    self.superview.superview.frame = collapseClickViewFrame;
    
    newDamageInfoObject.DamageTypeButton = typeButton;
    newDamageInfoObject.DamageAmountButton = amountButton;
    [_DamageInfoObjects addObject:newDamageInfoObject];
}

- (IBAction)addData:(id)sender {
    _ReselectingInfo = false;
    [_DamageTypeMenu showInView:self];
    
}
- (void)addData{
    _ReselectingInfo = false;
    [_DamageTypeMenu showInView:self];
    
}

int indexReselecting = 0;

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {

    if(!_ReselectingInfo)
    {
        if(actionSheet.tag == 50) {  //damagetype menu
            if(buttonIndex !=  [_DamageTypes count]){ //if cancel button wasnt pressed
                DamageInformationObject *newDamageInfoObject = [DamageInformationObject new];
                [self CreateNewDamageInformationObject: newDamageInfoObject: _DamageTypes[buttonIndex]];
            }
            
        }else{  //damageamount menu
            if(buttonIndex !=  [_DamageAmounts count]){ //if cancel button wasnt pressed
                DamageInformationObject *damageInfoObject = _DamageInfoObjects.lastObject;
                [damageInfoObject.DamageAmountButton setTitle: _DamageAmounts[buttonIndex] forState:UIControlStateNormal];
            }
        }
    }else{
        DamageInformationObject *infoObject = _DamageInfoObjects[indexReselecting];
        
        if(actionSheet.tag == 50) {
            if(buttonIndex !=  [_DamageTypes count]){ //if cancel button wasnt pressed
                [infoObject.DamageTypeButton setTitle:_DamageTypes[buttonIndex] forState:UIControlStateNormal ];
            }
        }else{
            if(buttonIndex !=  [_DamageAmounts count]){ //if cancel button wasnt pressed
                [infoObject.DamageAmountButton setTitle:_DamageAmounts[buttonIndex] forState:UIControlStateNormal ];
            }
        }
    }
}

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex{
 
    if(!_ReselectingInfo)
    {
        if(actionSheet.tag == 50) {  //damagetype menu
            if(buttonIndex !=  [_DamageTypes count]){ //if cancel button wasnt pressed
                
                [_DamageAmountMenu showInView:self];//this wont appear for whatever reason
            }
        }
    }
}

-(void)ChangeDamageType:(UIButton *)pressedButton{
    _ReselectingInfo = TRUE;
    indexReselecting = pressedButton.tag;
    [_DamageTypeMenu showInView:self];
}
-(void)ChangeDamageAmount:(UIButton *)pressedButton{
    _ReselectingInfo = TRUE;
    indexReselecting = pressedButton.tag;
    [_DamageAmountMenu showInView:self];
}

- (void)refreshLayout:(UIView *)view {
//    CGRect frame = self.field.frame;
//    frame.size.width = view.frame.size.width;
//    self.field.frame = frame;
//    
//    CGRect arrowFrame = _selectorIcon.frame;
//    arrowFrame.origin.x = self.label.frame.origin.x + view.frame.size.width - arrowFrame.size.width;
//    _selectorIcon.frame = arrowFrame;

}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *touchedView = [super hitTest:point withEvent:event];
  
    if(_isDraft){
        if(point.x > _addItemButton.frame.origin.x && point.x < _addItemButton.frame.origin.x + _addItemButton.frame.size.width && point.y > _addItemButton.frame.origin.y && point.y < _addItemButton.frame.origin.y + _addItemButton.frame.size.height) {
            [self addData];
        }
    }
    return touchedView;
}

@end

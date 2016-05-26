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
//  FormView.m
//  nics_iOS
//
//

#import "FormView.h"

@implementation FormView

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if(self) {
    }
    return self;
}

- (void) awakeFromNib {
    _allInteractableFields = [NSMutableArray new];
    [self parseSchema];
    [self setup];
}

- (void)setup {
    [[NSBundle mainBundle] loadNibNamed:@"FormView" owner:self options:nil];
    [self addSubview:_collapseView];
    
    _dataManager = [DataManager getInstance];
    
    _openedSections = [NSMutableArray new];
    _collapseView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
    _collapseView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    
    _collapseView.CollapseClickDelegate = self;
 
    _view.frame = CGRectMake(0,0, self.superview.frame.size.width, self.frame.size.height);
    self.collapseView.contentSize = CGSizeMake(self.superview.frame.size.width, self.collapseView.contentSize.height);
    self.frame = CGRectMake(0,0, self.superview.frame.size.width -10, self.frame.size.height);
    
    [_collapseView reloadCollapseClick];
    [_collapseView openCollapseClickCellAtIndex:0 animated:NO];
}

-(void)setAsDraft:(bool)isDraft{

    for(FormWidget *widget in _allInteractableFields) {
        for(NSInteger i = 0; i < widget.interactableViews.count; i++){
            CustomTextView * textView = [widget.interactableViews objectAtIndex:i];
            textView.editable = isDraft;
        }
    }
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    [_focusedTextView resignFirstResponder];
    return YES;
}

- (BOOL) textViewShouldBeginEditing:(UITextView *)textView {
    _focusedTextView = textView;
    return YES;
}


-(void) textViewDidEndEditing:(UITextView *)textView {
//    CGRect frame;
//    frame = textView.frame;
    
//    frame.size.height = [textView contentSize].height;
//    frame.size.width = self.superview.superview.frame.size.width;
//    textView.frame = frame;
    [_collapseView reloadCollapseClick];
    [_collapseView openCollapseClickCellsWithIndexes:_openedSections animated:NO];
//    _focusedTextView = nil;
}

-(BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [_focusedTextView resignFirstResponder];
//        _updateFrame = YES;
    } else {
//        _updateFrame = NO;
    }
    
    if(_updateFrame == YES){
        _updateFrame = NO;
    }else{
        _updateFrame = ![self doesFit:textView string:text range:range];
    }
    
//    _updateFrame = YES;
    
    return YES;
}


- (float)doesFit:(UITextView*)textView string:(NSString *)myString range:(NSRange) range;
{
    // Get the textView frame
    float viewHeight = textView.frame.size.height;
    float width = textView.textContainer.size.width;
    
    NSMutableAttributedString *atrs = [[NSMutableAttributedString alloc] initWithAttributedString: textView.textStorage];
    [atrs replaceCharactersInRange:range withString:myString];
    
    NSTextStorage *textStorage = [[NSTextStorage alloc] initWithAttributedString:atrs];
    NSTextContainer *textContainer = [[NSTextContainer alloc] initWithSize: CGSizeMake(width, FLT_MAX)];
    NSLayoutManager *layoutManager = [[NSLayoutManager alloc] init];
    
    [layoutManager addTextContainer:textContainer];
    [textStorage addLayoutManager:layoutManager];
    float textHeight = [layoutManager
                        usedRectForTextContainer:textContainer].size.height;
    
    if (textHeight >= viewHeight - 1) {
        return NO;
    } else
        return YES;
}

-(void)textViewDidChange:(UITextView *)textView {
//    CGFloat temp = round(textView.contentSize.height/textView.font.lineHeight);
//    _updateFrame = YES;
    if(_updateFrame){// || temp != _numLines) {
//        _numLines = temp;
        CGFloat totalHeight = 0;
        
        CGFloat frameHeight = textView.frame.size.height;
        [textView sizeToFit];
        
        CGRect frame = textView.frame;
        frameHeight = textView.frame.size.height - frameHeight;
        CGRect frame2;
        
//        frame.size.width = self.frame.size.width;
        textView.frame = frame;
        
        NSArray* subviews = [textView.superview.superview subviews];
        
        totalHeight += textView.frame.size.height;
        
        NSUInteger index = [subviews indexOfObject:textView.superview] + 1;
        while(index < [subviews count]) {
            UIView* siblingView = [subviews objectAtIndex: index ];
            if(siblingView != nil) {
                frame2 = siblingView.frame;
                frame2.origin.y = frame2.origin.y + frameHeight;
                siblingView.frame = frame2;
                
                totalHeight += frame2.size.height + 16;
            }
            index++;
        }
        CollapseClickCell *cell = [_collapseView collapseClickCellForIndex:[[_openedSections objectAtIndex:0] intValue]];
        
        float contentHeight = ((UIView *)[cell.ContentView.subviews lastObject]).frame.size.height + frameHeight;
        CGRect overallFrame = cell.frame;
        overallFrame.size.height = contentHeight;
        
        cell.frame = overallFrame;
        UIView *subview =[[cell.ContentView subviews] objectAtIndex:0];
        
        CGRect subViewFrame = subview.frame;
        subViewFrame.size.height = contentHeight;
        
        subview.frame = subViewFrame;
    
        _numLines = round(textView.contentSize.height/textView.font.lineHeight);
    }
}

//called from parseSchema to initialize the size of the cutom textfields
- (void)initTextView:(UITextView *)textView {
    CGFloat totalHeight = 0;
    
    CGFloat frameHeight = textView.frame.size.height;
    [textView sizeToFit];
    
    CGRect frame = textView.frame;
    frameHeight = textView.frame.size.height - frameHeight;
    CGRect frame2;
    

    frame.size.width = self.frame.size.width;
    textView.frame = frame;
    
    NSArray* subviews = [textView.superview.superview subviews];
    
    totalHeight += textView.frame.size.height;
    
    NSUInteger index = [subviews indexOfObject:textView.superview] + 1;
    while(index < [subviews count]) {
        UIView* siblingView = [subviews objectAtIndex: index ];
        if(siblingView != nil) {
            frame2 = siblingView.frame;
            frame2.origin.y = frame2.origin.y + frameHeight;
            siblingView.frame = frame2;
            
            totalHeight += frame2.size.height + 16;
        }
        index++;
    }
    CollapseClickCell *cell = [_collapseView collapseClickCellForIndex:[[_openedSections objectAtIndex:0] intValue]];
    
    float contentHeight = ((UIView *)[cell.ContentView.subviews lastObject]).frame.size.height + frameHeight;
    CGRect overallFrame = cell.frame;
    overallFrame.size.height = contentHeight;
    
    cell.frame = overallFrame;
    UIView *subview =[[cell.ContentView subviews] objectAtIndex:0];
    
    CGRect subViewFrame = subview.frame;
    subViewFrame.size.height = contentHeight;
    
    subview.frame = subViewFrame;
    
    _numLines = round(textView.contentSize.height/textView.font.lineHeight);
}

- (void)parseSchema {
    NSString *filePath = [[NSBundle mainBundle] pathForResource:self.schema ofType:@"json"];
    NSData *JSONData = [NSData dataWithContentsOfFile:filePath options:NSDataReadingMappedIfSafe error:nil];
    NSDictionary *jsonObject = [NSJSONSerialization JSONObjectWithData:JSONData options:NSJSONReadingMutableContainers error:nil];
    
    NSError *err = nil;
    FormSchema *schema = [[FormSchema alloc] initWithDictionary:jsonObject error:&err];
    
    _viewMap = [[NSMutableDictionary alloc] init];
    
    if(schema != nil) {
        _sections = schema.sections;
        
        for(FormSchemaSection *section in _sections) {
            section.name = NSLocalizedString(section.name,nil);
            
            for(FormSchemaField *field in [section fields]) {
                
                if([field.type isEqualToString:@"string"]) {
                    
                    FormEditText *editText = [[FormEditText alloc] init];
                    
                    for(NSInteger i = 0; i < editText.interactableViews.count; i++){
                        CustomTextView * textView = [editText.interactableViews objectAtIndex:i];
                        
                        [textView setDelegate:self];
                        [textView setEditable:YES];
                        
                        editText.label.text = NSLocalizedString(field.name,nil);
                        [textView setText:[field defaultText]];
                        [self initTextView:textView];
                        
                    }
//                    editText.frame = CGRectMake(editText.frame.origin.x, editText.frame.origin.y, self.view.superview.frame.size.width, editText.frame.size.height);
                    
                    [_viewMap setObject:editText forKey:field.key];
                    [_allInteractableFields addObject:editText];
                } else if([field.type isEqualToString:@"spinner"]) {
                    
                    FormSpinner *spinner = [[FormSpinner alloc] initWithTitle: [@"Select " stringByAppendingString:field.name] options:field.options];
                    spinner.label.text = NSLocalizedString(field.name,nil);
                    
                    for(NSInteger i = 0; i < spinner.interactableViews.count; i++){
                        CustomTextView * textView = [spinner.interactableViews objectAtIndex:i];
                        
                        [textView setEditable:YES];
                        [textView setText:[field defaultText]];
                    }
                    
                    [_viewMap setObject:spinner forKey:field.key];
                    [_allInteractableFields addObject:spinner];
                } else if([field.type isEqualToString:@"damageInformation"]) {
                    FormDamageInformation *damageInformation = [[FormDamageInformation alloc] initWithTitle: [@"Select " stringByAppendingString:field.name] options:field.options];
                    damageInformation.label.text = NSLocalizedString(field.name,nil);
                    
                    [_viewMap setObject:damageInformation forKey:field.key];
                     [_allInteractableFields addObject:damageInformation];
                    
                    
                    
                } else if([field.type isEqualToString:@"imageSelector"]) {
                    FormImageSelector *imageSelector = [[FormImageSelector alloc] init];
                    [imageSelector setLayout:self.superview.frame];
                    
                    [_viewMap setObject:imageSelector forKey:field.key];
                    [_allInteractableFields addObject:imageSelector];
                    
                } else if([field.type isEqualToString:@"color"]) {
                    FormColorPicker *colorPicker = [[FormColorPicker alloc] init];
                    colorPicker.label.text = NSLocalizedString(field.name,nil);
                    [colorPicker setLayout:self.superview.frame];
                    
                    [_viewMap setObject:colorPicker forKey:field.key];
                    [_allInteractableFields addObject:colorPicker];
                    
                }else if([field.type isEqualToString:@"location"]) {
                    FormLocation *formLocation = [[FormLocation alloc] init];
                    formLocation.label.text = NSLocalizedString(field.name,nil);
                    
                    for(NSInteger i = 0; i < formLocation.interactableViews.count; i++){
                        CustomTextView * textView = [formLocation.interactableViews objectAtIndex:i];
                        
                        [textView setDelegate:self];
                        [textView setEditable:YES];
                        
                        formLocation.label.text = NSLocalizedString(field.name,nil);
                        [textView setText:[field defaultText]];
                        [self initTextView:textView];
                    }
                    [formLocation configureFields];
                    
                    //[damageInformation.field setText:[field defaultText]];
                    
                    [_viewMap setObject:formLocation forKey:field.key];
                    [_allInteractableFields addObject:formLocation];
                }
            }
        }
    }
}

- (NSMutableDictionary *)save {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    for(NSString* key in [_viewMap allKeys]) {
        id object = [_viewMap objectForKey:key];
        if([object isKindOfClass:[FormEditText class]]) {
            
            FormEditText * tempEditText = (FormEditText *)object;
            for(NSInteger i = 0; i < tempEditText.interactableViews.count; i++){
                CustomTextView * textView = [tempEditText.interactableViews objectAtIndex:i];
                [dataDictionary setObject:textView.text forKey:key];
            }
            
        } else if([object isKindOfClass:[FormSpinner class]]) {
            
            FormSpinner * tempSpinner = (FormSpinner *)object;
            for(NSInteger i = 0; i < tempSpinner.interactableViews.count; i++){
                CustomTextView * textView = [tempSpinner.interactableViews objectAtIndex:i];
                [dataDictionary setObject:textView.text forKey:key];
            }
        } else if([object isKindOfClass:[FormLocation class]]) {
            
            FormLocation * tempLocation = (FormLocation *)object;
            [dataDictionary setObject:tempLocation.latitudeTextView.text forKey:@"latitude"];
            [dataDictionary setObject:tempLocation.longitudeTextView.text forKey:@"longitude"];
            [tempLocation cleanNotificationListener];
            
        } else if([object isKindOfClass:[FormDamageInformation class]]) {
            [dataDictionary setObject:[((FormDamageInformation *)object) getData] forKey:key];
        
        } else if([object isKindOfClass:[FormImageSelector class]]) {
            [dataDictionary setObject:[((FormImageSelector *)object) getData] forKey:key];    //getfullpath from widget
        }else if([object isKindOfClass:[FormColorPicker class]]) {
            FormColorPicker* colorPicker = ((FormColorPicker *)object);
            NSString *colorValue = [colorPicker getData];
            [dataDictionary setObject:colorValue forKey:key];
        }
    }
    return dataDictionary;
}

// Required Methods
-(int)numberOfCellsForCollapseClick {
    return (int)[_sections count];
}

-(NSString *)titleForCollapseClickAtIndex:(int)index {
    return [[_sections objectAtIndex:index] name];
}

-(UIView *)viewForCollapseClickContentViewAtIndex:(int)index {
    UIView *newView = [[UIView alloc] init];
    
    CGFloat totalHeight = 0.0f;
    NSMutableArray *spinnerList = [NSMutableArray new];
    for(FormSchemaField *field in [[_sections objectAtIndex:index] fields]) {
        
        FormWidget * view =  [_viewMap objectForKey:[field key]];
        
        [newView addSubview: view];
        
        CGRect frame = view.frame;
        frame.origin.y = totalHeight;

        view.frame = frame;
        
        if( [[[_sections objectAtIndex:index] fields] indexOfObject:field] != [[[_sections objectAtIndex:index] fields] count] - 1 ) {
            totalHeight += (view.label.frame.size.height *2) + 16;  //+ view.field.frame.size.height + 16;
        } else {
            totalHeight += (view.label.frame.size.height *2) + 16;//+ view.field.frame.size.height;
        }
        
        if([view.type isEqualToString:@"spinner"]) {
            [spinnerList addObject:view];
        }
        if([view.type isEqualToString:@"location"]) {
            totalHeight += 60;
        }
        if([view.type isEqualToString:@"damageInformation"]) {
            totalHeight += view.frame.size.height;
        }
        if([view.type isEqualToString:@"imageSelector"]) {
            totalHeight += 300;
        }
    }
    
    newView.frame = CGRectMake(0, 0, self.superview.frame.size.width, totalHeight);
    
    for(FormSpinner *spinner in spinnerList) {
        [spinner refreshLayout:newView];
    }
    
    return newView;
}


-(void)updateDataWithDictionary:(NSDictionary *) data readOnly:(BOOL) readOnly {
    for(NSString* key in [data allKeys]) {
        
        FormWidget* widget = [_viewMap objectForKey:key];
        
//        if([key isEqualToString:@"latitude"] || [key isEqualToString:@"longitude"]){
//            widget = [_viewMap objectForKey:@"Location"];
//        }
        
        if(![widget.type isEqualToString:@"damageInformation"]) {
            for(NSInteger i = 0; i < widget.interactableViews.count; i++){
                CustomTextView * textView = [widget.interactableViews objectAtIndex:i];
                
                [textView setText:[data valueForKey:key]];
                if(readOnly) {
                    [textView setEditable:NO];
                    textView.textColor = [UIColor grayColor];
                }
            }
        }
        if([widget.type isEqualToString:@"location"]){
            FormLocation* location = (FormLocation *)widget;
//            [location.latitudeTextView setText:[data valueForKey:key]];
            
            
            NSString *lonKey = [key stringByReplacingOccurrencesOfString: @"latitude" withString:@"longitude"];
            if([lonKey isEqualToString:key]){
                lonKey = [key stringByReplacingOccurrencesOfString: @"Latitude" withString:@"Longitude"];
            }
            
//            [location.longitudeTextView setText:[data valueForKey: lonKey]];
            
            [location setData:[data valueForKey:key] :[data valueForKey: lonKey] :readOnly];
            
        } else if([widget.type isEqualToString:@"damageInformation"]){
            FormDamageInformation* damageInformation = (FormDamageInformation *)widget;
            [damageInformation setData:[data valueForKey:key] readOnly: readOnly];
        
        }else if([widget.type isEqualToString:@"imageSelector"]){
            FormImageSelector* imageSelector = (FormImageSelector *)widget;
            [imageSelector setData:[data valueForKey:key] : self :  readOnly];    //set fullpath in widget from payload
        }else if([widget.type isEqualToString:@"color"]){
            FormColorPicker* colorPicker = (FormColorPicker *)widget;
            [colorPicker setData:[data valueForKey:key] :  readOnly];    //set fullpath in widget from payload
        }
    
        [_collapseView reloadCollapseClick];
    }
    
    for(FormSchemaSection *section in _sections) {
        int index = (int)[_sections indexOfObject:section];
        
        if([section.name isEqualToString:@""]) {
            [_collapseView openCollapseClickCellAtIndex:index animated:NO];
            [_openedSections insertObject:[NSNumber numberWithInt:index] atIndex:0];
        }
        [_collapseView closeCollapseClickCellAtIndex:index animated:NO];
    }
    
     [_collapseView openCollapseClickCellAtIndex:0 animated:NO];
}


- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *touchedView = [super hitTest:point withEvent:event];

    if(![touchedView isKindOfClass:[CustomTextView class]]){    //wonky way to handle keyboard for devices without a collapse
//    if(touchedView != _focusedTextView) {                     //keyboard button
        if([_dataManager getIsIpad] == false){
            
            CGPoint offset = [_collapseView contentOffset];
            
//            [_focusedTextView resignFirstResponder];
//            [self.view endEditing:YES];
            
            
            [_collapseView setContentOffset:offset];
        }
 
    }
    return touchedView;
}

-(void)didClickCollapseClickCellAtIndex:(int)index isNowOpen:(BOOL)open {
    if(open) {
        [_openedSections insertObject:[NSNumber numberWithInt:index] atIndex:0];
    } else {
        [_openedSections removeObject:[NSNumber numberWithInt:index]];
    }
    
    CollapseClickCell *cell = [_collapseView collapseClickCellForIndex:index];
    for(UIView *myView in cell.subviews){
        myView.frame = CGRectMake(myView.frame.origin.x ,myView.frame.origin.y, myView.superview.frame.size.width, myView.frame.size.height);
    }
}

@end

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

#import "FormColorPicker.h"
#import "ColorPicker.h"

@implementation FormColorPicker

double doubleHitTestBuffer;
double lastHitTestTime;

- (id)init
{
    self = [super init];
    [self setup];
    return self;
}

- (void)setup {
    [super setup];
    self.type = @"color";
    [[NSBundle mainBundle] loadNibNamed:@"FormColorPicker" owner:self options:nil];
    
    for(UIView * subview in self.view.subviews) {
        [subview setUserInteractionEnabled:YES];
        [self addSubview:subview];
    }
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.superview.frame.size.width, self.view.frame.size.height);
    
}

- (void)setLayout: (CGRect)formFrame{
    self.formFrame = formFrame;
    
    CGRect frame = self.view.frame;
    frame.size.width = formFrame.size.width;
    self.view.frame = frame;
    
    frame = _SelectColorButton.frame;
    frame.size.width = formFrame.size.width;
    _SelectColorButton.frame = frame;
    
}

-(CustomTextView*) getButtonview{
    return [self.interactableViews objectAtIndex:0];
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    
    UIView *touchedView = [super hitTest:point withEvent:event];
    
    if(([[NSDate date] timeIntervalSince1970] - lastHitTestTime) < doubleHitTestBuffer){
        lastHitTestTime = [[NSDate date] timeIntervalSince1970];
        return touchedView;
    }
    
    if(!_readOnly){
        if(point.x > self.SelectColorButton.frame.origin.x && point.x < self.SelectColorButton.frame.origin.x + self.SelectColorButton.frame.size.width && point.y > self.SelectColorButton.frame.origin.y && point.y < self.SelectColorButton.frame.origin.y + self.SelectColorButton.frame.size.height) {
            [self selectColorButtonPressed];
        }
    }
    lastHitTestTime = [[NSDate date] timeIntervalSince1970];
    return touchedView;
}

-(void)selectColorButtonPressed{
    UIView *anchor = _SelectColorButton;
    CGRect frame = anchor.frame;
//    frame.size.width = 900;
    anchor.frame = frame;
    
    ColorPicker *viewControllerForPopover = [[ColorPicker alloc] init];
    //    viewControllerForPopover.mapEditView = self;
    viewControllerForPopover.view.frame = anchor.frame;
    viewControllerForPopover.delegate = self;
    
    if([[DataManager getInstance] isIpad]){
        
        UIPopoverController * popover = [[UIPopoverController alloc] initWithContentViewController:viewControllerForPopover];
        CGSize size = CGSizeMake(648,487);
        
        [popover setPopoverContentSize:size];
        [popover presentPopoverFromRect:anchor.frame
                                 inView:anchor.superview
               permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
        
    }else{
//        [self pushViewController:viewControllerForPopover animated:YES];
    }
}

-(void)colorPicked:(UIColor*)color hexValue: (NSString*)hexString{
    
    _selectedColor = color;
    _selectedHexColor = hexString;
    
    [_SelectColorButton setBackgroundColor:color];
}

- (NSString *)getData{
    if(_selectedColor == nil){
        _selectedHexColor = @"#000000";
    }
    return _selectedHexColor;
}

- (void)setData: (NSString *) color : (bool)readOnly{
    _readOnly = readOnly;
    _selectedHexColor = color;
    
    if(_readOnly){
        [_SelectColorButton setTitle:@"" forState:UIControlStateNormal];
    }else{
        [_SelectColorButton setTitle:NSLocalizedString(@"Select Color",nil) forState:UIControlStateNormal];
    }
    if(_selectedHexColor == nil || [_selectedHexColor isEqualToString:@""]){
        _selectedHexColor = @"#000000";
    }
    [self colorPicked:[self colorFromHexString:_selectedHexColor] hexValue:_selectedHexColor];
}

// Assumes input like "#00FF00" (#RRGGBB).
- (UIColor *)colorFromHexString:(NSString *)hexString {
    unsigned rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    [scanner setScanLocation:1]; // bypass '#' character
    [scanner scanHexInt:&rgbValue];
    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}

@end

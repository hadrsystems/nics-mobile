/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
//  Phinics_iOS
//
//

#import "FormSpinner.h"

@implementation FormSpinner

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
    self.type = @"spinner";
    
    [[self subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    [[NSBundle mainBundle] loadNibNamed:@"FormSpinner" owner:self options:nil];
    
    if(_menu != nil) {
        _menu = nil;
    }
    
    _menu = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(_title,nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
    
    for( NSString *title in _options)  {
        [_menu addButtonWithTitle:NSLocalizedString(title,nil)];
    }
    
    [_menu addButtonWithTitle:NSLocalizedString(@"Cancel",nil)];
    _menu.cancelButtonIndex = [_options count];
    
    for(UIView * subview in self.view.subviews) {
        [subview setUserInteractionEnabled:YES];
        [self addSubview:subview];
    }
    
    [self.interactableViews addObject:self.textView];
    self.textView.delegate = self;
//    self.field.delegate = self;
}

- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    [_menu showInView:self];

    return NO;
}
- (void)refreshLayout:(UIView *)view {
    
    CustomTextView *tempTextView = [self.interactableViews objectAtIndex:0];
    
    CGRect frame = tempTextView.frame;
    frame.size.width = view.frame.size.width -20;
    tempTextView.frame = frame;
    
    CGRect arrowFrame = _selectorIcon.frame;
    arrowFrame.origin.x = tempTextView.frame.origin.x + tempTextView.frame.size.width - arrowFrame.size.width;
    _selectorIcon.frame = arrowFrame;

}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if(buttonIndex != _menu.cancelButtonIndex) {
        CustomTextView *tempTextView = [self.interactableViews objectAtIndex:0];
        tempTextView.text = [actionSheet buttonTitleAtIndex:buttonIndex];
    }
}

-(CustomTextView*) getTextView{
    return [self.interactableViews objectAtIndex:0];
}

@end

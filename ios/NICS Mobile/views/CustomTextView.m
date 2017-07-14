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
//  CustomTextView.m
//  nics_iOS
//
//

#import "CustomTextView.h"

@implementation CustomTextView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.keyboardAppearance = UIKeyboardAppearanceDark;
//        self.returnKeyType = UIReturnKeyDone;
        
    }
    return self;
}


- (void)drawRect:(CGRect)rect
{
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextMoveToPoint(context, CGRectGetMinX(rect), CGRectGetMaxY(rect));
    CGContextAddLineToPoint(context, CGRectGetMaxX(rect), CGRectGetMaxY(rect));
    CGContextSetStrokeColorWithColor(context, [[UIColor colorWithRed:0.2890625 green:0.30078125 blue:0.3125 alpha:1.0] CGColor] );
    CGContextSetLineWidth(context, 1.0);
    CGContextStrokePath(context);
}

- (CGRect)textRectForBounds:(CGRect)bounds {
    return CGRectMake(bounds.origin.x + 10, bounds.origin.y + 8,
                      bounds.size.width - 20, bounds.size.height - 16);
}
- (CGRect)editingRectForBounds:(CGRect)bounds {
    return [self textRectForBounds:bounds];
}

- (void) drawPlaceholderInRect:(CGRect)rect {
    [[UIColor colorWithRed:0.2890625 green:0.30078125 blue:0.3125 alpha:1.0] setFill];
//    [[self placeholder] drawInRect:rect withAttributes:@{NSFontAttributeName:self.font, NSForegroundColorAttributeName:[UIColor colorWithRed:0.2890625 green:0.30078125 blue:0.3125 alpha:1.0]}];
    //[[self placeholder] drawInRect:rect withFont:[self font]];
}

- (void)setText:(NSString *)text
{
    [super setText:text];
    
    if(text != nil) {
        CGSize textViewSize = [text sizeWithAttributes:@{NSFontAttributeName: self.font}];
        self.contentSize = CGSizeMake(self.bounds.size.width, textViewSize.height + 16);
    
    } else {
        
        self.contentSize = CGSizeMake(self.bounds.size.width, 20);
    }
    
    [self setNeedsDisplay];
}

- (void)setPlaceHolderText:(NSString*)text{
    _placeHolderText = text;
    [self setText: text];
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    if([self.text isEqual: _placeHolderText]){
        self.text = @"";
    }
    
    return self;
}

@end

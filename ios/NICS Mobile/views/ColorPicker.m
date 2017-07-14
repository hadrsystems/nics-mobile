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
//  ColorPicker.m
//  NICS Mobile
//
//

#import "ColorPicker.h"

@implementation ColorPicker

@synthesize delegate;

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    
    UITouch *touch = [touches anyObject];
    CGPoint touchPoint = [touch locationInView:self.view];
    
    // Create a 1x1 pixel byte array and bitmap context to draw the pixel into.
    NSInteger pointX = trunc(touchPoint.x);
    NSInteger pointY = trunc(touchPoint.y);
    CGImageRef cgImage = _ColorPickerImageView.image.CGImage;
    NSUInteger width = _ColorPickerImageView.frame.size.width;
    NSUInteger height = _ColorPickerImageView.frame.size.height;
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    int bytesPerPixel = 4;
    int bytesPerRow = bytesPerPixel * 1;
    NSUInteger bitsPerComponent = 8;
    unsigned char pixelData[4] = { 0, 0, 0, 0 };
    CGContextRef context = CGBitmapContextCreate(pixelData, 1, 1, bitsPerComponent, bytesPerRow, colorSpace, kCGImageAlphaPremultipliedLast | kCGBitmapByteOrder32Big);
    CGColorSpaceRelease(colorSpace);
    CGContextSetBlendMode(context, kCGBlendModeCopy);
    
    // Draw the pixel we are interested in onto the bitmap context
    CGContextTranslateCTM(context, -pointX, pointY-(CGFloat)height);
    CGContextDrawImage(context, CGRectMake(0.0f, 0.0f, (CGFloat)width, (CGFloat)height), cgImage);
    CGContextRelease(context);
    
    int redInt = (int)pixelData[0];
    int greenInt = (int)pixelData[1];
    int blueInt = (int)pixelData[2];
    int alphaInt = (int)pixelData[3];
    
    CGFloat red   = (CGFloat)pixelData[0] / 255.0f;
    CGFloat green = (CGFloat)pixelData[1] / 255.0f;
    CGFloat blue  = (CGFloat)pixelData[2] / 255.0f;
    CGFloat alpha = (CGFloat)pixelData[3] / 255.0f;
    UIColor* color = [UIColor colorWithRed:red green:green blue:blue alpha:alpha];

//    NSString* hexString = [@"#" stringByAppendingFormat :@"%02x%02x%02x%02x", redInt, greenInt, blueInt, alphaInt]; //with Alpha
     NSString* hexString = [@"#" stringByAppendingFormat :@"%02x%02x%02x", redInt, greenInt, blueInt];
    
    if([delegate respondsToSelector:@selector(colorPicked:hexValue:)])
    {
        [delegate colorPicked:color hexValue: hexString];
    }
    
    if([[DataManager getInstance] getIsIpad]){
        [self dismissViewControllerAnimated:YES completion:nil];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

@end

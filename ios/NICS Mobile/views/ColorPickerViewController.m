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
//  ColorPickerViewController.m
//  ColorPicker
//
//  Copyright Mark Johnson 2009. All rights reserved.
//

#import "ColorPickerViewController.h"
#import <CoreGraphics/CoreGraphics.h>
#import <QuartzCore/CoreAnimation.h>


@implementation ColorPickerViewController

@synthesize colorWheel;
@synthesize tapMeButton;


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
	[self animateColorWheelToShow:NO duration:0];
	colorWheel.pickedColorDelegate = self;
}

- (IBAction) tapMe: (id)sender {
	[self animateColorWheelToShow:YES duration:0.3]; 
}

- (void) pickedColor:(UIColor*)color {
	[self animateColorWheelToShow:NO duration:0.3]; 
	self.view.backgroundColor = color;
	[self.view setNeedsDisplay];
}

- (void) animateColorWheelToShow:(BOOL)show duration:(NSTimeInterval)duration {
	int x;
	float angle;
	float scale;
	if (show==NO) { 
		x = -320;
		angle = -3.12;
		scale = 0.01;
		self.colorWheel.hidden=YES;
	} else {
		x=0;
		angle = 0;
		scale = 1;
		[self.colorWheel setNeedsDisplay];
		self.colorWheel.hidden=NO;
	}
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationDuration:duration];
	
	CATransform3D transform = CATransform3DMakeTranslation(0,0,0);
	//transform = CATransform3DTranslate(transform,x,0,0);
	//transform = CATransform3DRotate(transform, angle,0,0,1);
	transform = CATransform3DScale(transform, scale,scale,1);
	self.colorWheel.transform=CATransform3DGetAffineTransform(transform);
	self.colorWheel.layer.transform=transform;
	[UIView commitAnimations];
}


@end

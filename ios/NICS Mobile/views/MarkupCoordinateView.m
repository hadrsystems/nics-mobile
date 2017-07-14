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
//  MarkupCoordinateView.m
//  nics_iOS
//
//

#import "MarkupCoordinateView.h"

@implementation MarkupCoordinateView

//- (id)initWithCoder:(NSCoder *)aDecoder
//{
//    self = [super initWithCoder:aDecoder];
//    if (self) {
//        [self setup];
//    }
//    return self;
//}

- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    [self setup];
    return self;
}


- (void)setup {
    _dataManager = [DataManager getInstance];
    [self addSubview:[[[NSBundle mainBundle] loadNibNamed:@"MarkupCoordinateView" owner:self options:nil] objectAtIndex:0]];
    
    UIImage *colorPickerImage =[UIImage imageNamed:@"discrete_color_picker"];
    
    _dimView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 460)];
    _dimView.backgroundColor = [UIColor blackColor];
    _dimView.alpha = 0.75f;
    _dimView.userInteractionEnabled = NO;
    
    _colorPickerImageView = [[ColorPickerImageView alloc] initWithImage:[colorPickerImage resizedImageWithContentMode:UIViewContentModeScaleAspectFill bounds:_scrollView.superview.frame.size interpolationQuality:kCGInterpolationHigh]];
    _colorPickerImageView.center = CGPointMake(320.0/2, 392.0/2);
    _colorPickerImageView.pickedColorDelegate = self;
    [_colorPickerImageView setUserInteractionEnabled:YES];
}

- (IBAction)submitButtonPressed:(UIButton *)button {
    NSMutableArray *coordinates = [NSMutableArray new];
    
    MarkupFeature *feature = [MarkupFeature new];
    // CLLocationCoordinate2DMake([_latitude0.text doubleValue], [_longitude0.text doubleValue])];
    [coordinates addObject:@[_latitude0.text, _longitude0.text]];
    [coordinates addObject:@[_latitude1.text, _longitude1.text]];
    [coordinates addObject:@[_latitude2.text, _longitude2.text]];
    [coordinates addObject:@[_latitude3.text, _longitude3.text]];
    feature.geometryFiltered = coordinates;

    
    [_delegate submitMarkupCoordinates:coordinates];
}

- (IBAction)cancelButtonPressed:(UIButton *)button {
    [_delegate cancelMarkupCoordinates];
}

-(void)setShape:(MarkupType)shapeType {
    _coordinateView1.hidden = NO;
    _coordinateView2.hidden = NO;
    _coordinateView3.hidden = NO;
    _radiusView.hidden = YES;
    
    [_scrollView setContentOffset:CGPointZero animated:NO];
    
    CGSize contentSize = _scrollView.contentSize;
    switch (shapeType) {
        case symbol:
            NSLog(@"Creating Symbol");
            _coordinateView1.hidden = YES;
            _coordinateView2.hidden = YES;
            _coordinateView3.hidden = YES;
            
            [_colorPickerConstraint setConstant: _coordinateView0.frame.origin.y + _coordinateView0.frame.size.height + 8];
            contentSize.height = 258;

            break;
            
        case segment:
            NSLog(@"Creating Line");
            _coordinateView2.hidden = YES;
            _coordinateView3.hidden = YES;

            [_colorPickerConstraint setConstant: _coordinateView1.frame.origin.y + _coordinateView1.frame.size.height + 8];
            contentSize.height = 318;
            
            break;
            
        case rectangle:
            NSLog(@"Creating Rectangle");
            _coordinateView2.hidden = YES;
            _coordinateView3.hidden = YES;
            
            [_colorPickerConstraint setConstant: _coordinateView1.frame.origin.y + _coordinateView1.frame.size.height + 8];
            contentSize.height = 318;
            break;
            
        case polygon:
            NSLog(@"Creating Polygon");
            [_colorPickerConstraint setConstant: _coordinateView3.frame.origin.y + _coordinateView3.frame.size.height + 8];
            contentSize.height = 400;
            break;
            
        case circle:
            NSLog(@"Creating Circle");
            _coordinateView1.hidden = YES;
            _coordinateView2.hidden = YES;
            _coordinateView3.hidden = YES;
            _radiusView.hidden = NO;
            
            [_colorPickerConstraint setConstant: _coordinateView1.frame.origin.y + _coordinateView1.frame.size.height + 8];
            contentSize.height = 318;
            break;
            
        default:
            break;
    }
    
    [_contentView needsUpdateConstraints];
    [_contentView layoutIfNeeded];
    
    [_buttonsConstraint setConstant:_colorPickerView.frame.origin.y + _colorPickerView.frame.size.height + 32];

    [_scrollView setContentSize:contentSize];

}

- (IBAction)gpsButtonPressed:(UIButton *)button {
    switch(button.tag) {
        case 200:
            _latitude0.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.latitude];
            _longitude0.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.longitude];
            break;
        case 210:
            _latitude1.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.latitude];
            _longitude1.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.longitude];
            break;
        case 220:
            _latitude2.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.latitude];
            _longitude2.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.longitude];
            break;
        case 230:
            _latitude3.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.latitude];
            _longitude3.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.longitude];
            break;
            
    }

}

- (IBAction)lrfButtonPressed:(UIButton *)button {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error",nil) message:NSLocalizedString(@"Laser Range Finder currently unsupported.",nil) delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
    [alertView show];
}

- (IBAction)showColorPicker:(UIButton *) sender {

    [self.superview addSubview:_dimView];
    [self.superview addSubview:_colorPickerImageView];
    [self.superview bringSubviewToFront:_colorPickerImageView];
    [_delegate updateTitle:@"Color Picker"];

}
- (void) pickedColor:(UIColor*)color {
    [_dimView removeFromSuperview];
    [_colorPickerImageView removeFromSuperview];
    _colorPickerButton.backgroundColor = color;
    [_delegate updateTitle:@"Map Markup"];
}

@end

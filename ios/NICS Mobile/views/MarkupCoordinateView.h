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
/**
 *
 */
//
//  MarkupCoordinateView.h
//  nics_iOS
//
//

#import <UIKit/UIKit.h>
#import <GoogleMaps/GoogleMaps.h>
#import "DataManager.h"
#import "CustomTextField.h"
#import "MarkupFeature.h"
#import "ColorPickerImageView.h"
#import "UIImage+Resize.h"
#import "Enums.h"

@protocol MarkupCoordinateViewDelegate
@required
- (void)submitMarkupCoordinates:(NSArray *) coordinates;
- (void)cancelMarkupCoordinates;
- (void)updateTitle:(NSString *)title;
@end

@interface MarkupCoordinateView : UIView <UIActionSheetDelegate>

@property id <MarkupCoordinateViewDelegate> delegate;

@property DataManager* dataManager;

@property IBOutlet UIButton *colorPickerButton;

@property IBOutlet UIView *coordinateView0;
@property IBOutlet UIView *coordinateView1;
@property IBOutlet UIView *coordinateView2;
@property IBOutlet UIView *coordinateView3;
@property IBOutlet UIView *radiusView;

@property UIView *dimView;
@property IBOutlet UIView *colorPickerView;
@property IBOutlet UIView *buttonView;
@property IBOutlet UIScrollView *scrollView;
@property IBOutlet UIView *contentView;

@property IBOutlet CustomTextField *latitude0;
@property IBOutlet CustomTextField *latitude1;
@property IBOutlet CustomTextField *latitude2;
@property IBOutlet CustomTextField *latitude3;

@property IBOutlet CustomTextField *longitude0;
@property IBOutlet CustomTextField *longitude1;
@property IBOutlet CustomTextField *longitude2;
@property IBOutlet CustomTextField *longitude3;

@property IBOutlet CustomTextField *radius;

@property IBOutlet UIButton *lrfButton0;
@property IBOutlet UIButton *lrfButton1;
@property IBOutlet UIButton *lrfButton2;
@property IBOutlet UIButton *lrfButton3;

@property IBOutlet UIButton *gpsButton0;
@property IBOutlet UIButton *gpsButton1;
@property IBOutlet UIButton *gpsButton2;
@property IBOutlet UIButton *gpsButton3;

@property IBOutlet NSLayoutConstraint *colorPickerConstraint;
@property IBOutlet NSLayoutConstraint *buttonsConstraint;

@property ColorPickerImageView *colorPickerImageView;


- (void) setShape:(MarkupType)shapeType;

@end

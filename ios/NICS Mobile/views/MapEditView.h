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
//  FormEditText.h
//  nics_iOS
//
//

#import <UIKit/UIKit.h>
#import "DataManager.h"
#import "SelectSymbolMenuViewController.h"
#import "MapMarkupViewController.h"
#import "FormLocation.h"
#import "MarkupFeature.h"
#import "ColorPicker.h"
#import "CircleEditView.h"
#import "SymbolEditView.h"
#import "SquareEditView.h"
#import "PolygonEditView.h"
#import "LineEditView.h"

@class FormLocation;
@class MapMarkupViewController;
@class CircleEditView;

@interface MapEditView : UIViewController

typedef enum {
    
    TypeSelectView = 0,
    SymbolView = 1,
    SquareView = 2,
    PolygonView = 3,
    CircleView = 4,
    LineView = 5
    
} MapEditViews;

- (IBAction)SymbolButtonPressed:(id)sender;
- (IBAction)CircleButtonPressed:(id)sender;
- (IBAction)LineButtonPressed:(id)sender;
- (IBAction)PolygonButtonPressed:(id)sender;
- (IBAction)SquareButtonPressed:(id)sender;

-(void)Setup:(CGRect) frame;
-(void)MapViewMarkerDidDrag:(GMSMarker*) marker;
-(void)MapDidTapAtCoordinate:(CLLocationCoordinate2D)coordinate;

-(void)switchViewState:(MapEditViews)newState;

@property DataManager* dataManager;
@property CGRect originalFrame;

@property MapEditViews CurrentView;
@property NSMutableArray* arrayOfViews;

@property MapMarkupViewController* mapViewController;





    
@end

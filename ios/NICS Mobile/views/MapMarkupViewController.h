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
//  ViewController.h
//  SidebarDemo
//
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import <GoogleMaps/GoogleMaps.h>
#import <UIKit/UIKit.h>
#import <objc/runtime.h>
#import "DataManager.h"
#import "MarkupBaseShape.h"
#import "MarkupFireline.h"
#import "MarkupPolygon.h"
#import "MarkupSegment.h"
#import "MarkupSymbol.h"
#import "MarkupReportSymbol.h"
#import "MarkupText.h"
#import "Utils.h"
#import "Enums.h"
#import "MapEditView.h"
#import "ReportInfoWindow.h"
#import "SimpleReportDetailViewController.h"
#import "DamageReportDetailViewController.h"
@class MapEditView;

@interface MapMarkupViewController : UIViewController <UITableViewDelegate, UITableViewDataSource, UIAlertViewDelegate, /*MarkupCoordinateViewDelegate,*/ GMSMapViewDelegate, CLLocationManagerDelegate>

//@property IBOutlet UIBarButtonItem *sidebarButton;
@property IBOutlet GMSMapView *mapView;
@property IBOutlet UITableView *tableView;
@property IBOutlet UIView *shapeButtonView;
//@property IBOutlet MarkupCoordinateView *coordinateView;
@property MapEditView *mapEditView;
@property (weak, nonatomic) IBOutlet UIView *MapEditCanvas;

@property DataManager *dataManager;

@property NSNumber *currentCollabRoomId;
@property NSMutableDictionary *markupShapes;
@property NSMutableArray *markupDraftShapes;
@property NSMutableDictionary *generalMessageSymbols;
@property NSMutableDictionary *damageReportSymbols;
//@property NSMutableArray *undoStack;
//@property NSMutableDictionary *markupFeatures;
@property NSMutableArray *wfsMarkers;

@property MarkupBaseShape *currentShape;

@property NSDateFormatter *dateFormatter;

@property BOOL ignoreUpdate;
@property CGFloat previousZoomLevel;

@property CGRect originalFrame;
@property CGRect originalMapFrame;
//@property UIButton *fullscreenButton;
@property UIButton *extendMapButton;
@property UIButton *gotoReportButton;

@property BOOL *editMapPanelOpen;
@property UIButton *editMapButton;
@property CGRect originalTableFrame;

@property CLLocationCoordinate2D positionToZoomTo;
@property bool zoomingToReport;

@property GMSMarker *myCustomMarker;
@property UIViewController* previousViewToReturnTo;
@property ReportInfoWindow* currentReportWindow;

- (void)updateWfsLayers;

//- (void)setPopoverButtons;
- (void)addFeatureToMap:(MarkupFeature*) feature;
- (void)addMarkupUpdateFromServer;
- (void)reloadTableView;
- (GMSMarker*)getCustomMarker;
-(void)setUiViewControllerToReturnTo:(UIViewController*) controller;
-(void)zoomToPositionOnMapOpen:(double)lat : (double) lon;

-(void) setCustomSymbol:(NSString*)imageName;
-(void) hideCustomMarker;

@end

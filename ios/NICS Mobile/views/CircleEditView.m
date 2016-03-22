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
//  CircleEditViewViewController.m
//  NICS Mobile
//
//

#import "CircleEditView.h"

@interface CircleEditView ()

@end

@implementation CircleEditView

- (void)viewDidLoad {
    [super viewDidLoad];
    _dataManager = [DataManager getInstance];
    _selectedColor = [UIColor blackColor];
    _selectedHexColor = @"#000000";
}

-(void)setMapMarkupView:(MapMarkupViewController*)mapView{
    _mapViewController = mapView;
}

-(void)ViewEnter{
    _circleCenterMarker = [[GMSMarker alloc] init];
    _circleOutsideMarker = [[GMSMarker alloc] init];
    _circleOutsideMarker.draggable = true;
    _circleCenterMarker.draggable = true;
    _circleCenterMarker.title = NSLocalizedString(@"Location", nil);
    _circleOutsideMarker.title = NSLocalizedString(@"Location", nil);
}

- (IBAction)CircleViewConfirmButtonPressed:(id)sender {
    MarkupFeature* feature = [[MarkupFeature alloc]init];
    feature.collabRoomId = [_dataManager getSelectedCollabroomId];
    
    if(_circleCenterMarker.map == nil || _circleOutsideMarker.map == nil){
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Add more points",nil) message:@"You must have 2 points to create a circle feature" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alertView show];
        return;
    }
    
    if(feature.collabRoomId == [NSNumber numberWithInt:-1]){
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Please join a collabroom",nil) message:@"You must join a collabroom before posting new map features" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alertView show];
        return;
    }
    
    feature.fillColor = _selectedHexColor;
    feature.strokeColor = _selectedHexColor;
    feature.strokeWidth = [NSNumber numberWithFloat:2.0];
    
    CLLocation *locA = [[CLLocation alloc] initWithLatitude:_circleCenterMarker.position.latitude longitude:_circleCenterMarker.position.longitude];
    CLLocation *locB = [[CLLocation alloc] initWithLatitude:_circleOutsideMarker.position.latitude longitude:_circleOutsideMarker.position.longitude];
    
    double radius = [locA distanceFromLocation:locB];
    
    feature.geometryFiltered = [self convertCircleToPolygon:_circleCenterMarker.position :radius];
    feature.geometry = [self buildGeometryStringFromPolygon:feature.geometryFiltered];
    feature.radius = [NSNumber numberWithDouble: radius];
    feature.opacity = [NSNumber numberWithFloat:0.2];
    feature.type = @"circle";
    feature.username = [_dataManager getUsername];
    feature.usersessionId = [_dataManager getUserSessionId];
    feature.featureId = @"draft";
    double temp =  [[NSDate date] timeIntervalSince1970];
    NSNumber *date = [NSNumber numberWithLongLong:round(temp)];
    feature.seqtime = date;
    
    
    [_dataManager addMarkupFeatureToStoreAndForward:feature];
    [_mapViewController addFeatureToMap:feature];
    
    [self ViewExit];
    [self ViewEnter];
}

- (IBAction)CircleColorPickerButtonPressed:(id)sender {
    UIView *anchor = sender;
    CGRect frame = anchor.frame;
    frame.size.width = 900;
    anchor.frame = frame;
    
    ColorPicker *viewControllerForPopover = [[ColorPicker alloc] init];
    //    viewControllerForPopover.mapEditView = self;
    viewControllerForPopover.view.frame = anchor.frame;
    viewControllerForPopover.delegate = self;
   
    if([_dataManager isIpad]){
    
        UIPopoverController * popover = [[UIPopoverController alloc] initWithContentViewController:viewControllerForPopover];
        CGSize size = CGSizeMake(648,487);
        
        [popover setPopoverContentSize:size];
        [popover presentPopoverFromRect:anchor.frame
                                 inView:anchor.superview
               permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];

    }else{
        [_mapViewController.navigationController pushViewController:viewControllerForPopover animated:YES];
    }
    
}

-(void)colorPicked:(UIColor*)color hexValue: (NSString*)hexString{
    
    _selectedColor = color;
    _selectedHexColor = hexString;
    
    [_CircleColorPickerButton setBackgroundColor:color];
    [_circleEdit setStrokeColor:_selectedColor];
    [_circleEdit setFillColor:[_selectedColor colorWithAlphaComponent:0.2]];
}

-(NSMutableArray*)convertCircleToPolygon:(CLLocationCoordinate2D)center : (double)radius{
    NSMutableArray* circleGeom = [NSMutableArray new];
    int numberOfPoints = 20;
    double currentAngle = 0;
    
    double radiusDeg = (radius/ 1000) * (180/ M_PI);
    radiusDeg = radiusDeg / 6371;
    //    radius = Math.toDegrees((mRadius / 1000.0) / 6371.0); //earth radius
    
    while(currentAngle < (M_PI*2)){
        
        double centerLat = center.latitude;
        double centerLon = center.longitude;
        
        double lon = (radiusDeg* cos(currentAngle)) + centerLon;
        double lat = (radiusDeg* sin(currentAngle)) + centerLat;
        
        [circleGeom addObject:[NSArray arrayWithObjects: [NSNumber numberWithDouble: lat], [NSNumber numberWithDouble: lon] ,nil] ];
        currentAngle += (M_PI*2)/numberOfPoints;
    }
    [circleGeom addObject:[circleGeom objectAtIndex:0]];
    
    return circleGeom;
}

-(NSString*)buildGeometryStringFromPolygon: (NSMutableArray*)polygonCoords{
    
    NSString* polygonString = @"POLYGON((";
    
    for(int i = 0; i < polygonCoords.count; i++){
        
        NSArray *coord = [polygonCoords objectAtIndex:i];
        
        polygonString = [polygonString stringByAppendingFormat:@"%@%@%@",[NSString stringWithFormat:@"%@",[coord objectAtIndex:1]],@" ", [NSString stringWithFormat:@"%@",[coord objectAtIndex:0]]];
        
        if(i != polygonCoords.count -1){
            polygonString = [polygonString stringByAppendingString:@","];
        }
    }
    
    polygonString = [polygonString stringByAppendingString:@"))"];
    
    return polygonString;
}

- (IBAction)CircleViewCancelButtonPressed:(id)sender {
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys: TypeSelectView,@"newState", nil];
    
    NSNotification *switchMapEditState = [NSNotification notificationWithName:@"mapEditSwitchStateNotification" object:self userInfo:dict];
    [[NSNotificationCenter defaultCenter] postNotification:switchMapEditState];
    
}

-(void)MapTapped: (CLLocationCoordinate2D)coordinate{
    
    if(_circleCenterMarker.map == nil){
        _circleCenterMarker.position = coordinate;
        _circleCenterMarker.map = _mapViewController.mapView;
    }else{
        _circleOutsideMarker.position = coordinate;
        _circleOutsideMarker.map = _mapViewController.mapView;
        
        CLLocation *locA = [[CLLocation alloc] initWithLatitude:_circleCenterMarker.position.latitude longitude:_circleCenterMarker.position.longitude];
        CLLocation *locB = [[CLLocation alloc] initWithLatitude:_circleOutsideMarker.position.latitude longitude:_circleOutsideMarker.position.longitude];
        
        CLLocationDistance distance = [locA distanceFromLocation:locB];
        
        if(_circleEdit == nil){
            _circleEdit = [GMSCircle circleWithPosition:_circleCenterMarker.position radius:distance];
        }else{
            _circleEdit.position = _circleCenterMarker.position;
            _circleEdit.radius = distance;
        }
        [_circleEdit setStrokeColor:_selectedColor];
        [_circleEdit setFillColor:[_selectedColor colorWithAlphaComponent:0.2]];
        _circleEdit.map = _mapViewController.mapView;
    }
}

-(void)MapMarkerDragged: (GMSMarker *)marker{
    if(_circleCenterMarker.map != nil && _circleOutsideMarker != nil){
        CLLocation *locA = [[CLLocation alloc] initWithLatitude:_circleCenterMarker.position.latitude longitude:_circleCenterMarker.position.longitude];
        CLLocation *locB = [[CLLocation alloc] initWithLatitude:_circleOutsideMarker.position.latitude longitude:_circleOutsideMarker.position.longitude];
        
        CLLocationDistance distance = [locA distanceFromLocation:locB];
        
        if(_circleEdit == nil){
            _circleEdit = [GMSCircle circleWithPosition:_circleCenterMarker.position radius:distance];
        }else{
            _circleEdit.position = _circleCenterMarker.position;
            _circleEdit.radius = distance;
        }
        [_circleEdit setStrokeColor:_selectedColor];
        [_circleEdit setFillColor:[_selectedColor colorWithAlphaComponent:0.2]];
        _circleEdit.map = _mapViewController.mapView;
    }
}

-(void)ViewExit{
    _circleCenterMarker.map = nil;
    _circleOutsideMarker.map = nil;
    _circleEdit.map = nil;
    _circleCenterMarker = nil;
    _circleOutsideMarker = nil;
}



@end

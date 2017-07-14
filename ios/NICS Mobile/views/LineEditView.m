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
//  LineEditView.m
//  NICS Mobile
//
//

#import "LineEditView.h"

@interface LineEditView ()

@end

@implementation LineEditView

- (void)viewDidLoad {
    [super viewDidLoad];
    _dataManager = [DataManager getInstance];
    _selectedColor = [UIColor blackColor];
    _selectedHexColor = @"#000000";
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)setMapMarkupView:(MapMarkupViewController*)mapView{
    _mapViewController = mapView;
}

-(void)ViewEnter{
    _MarkerArray = [[NSMutableArray alloc]init];
    _linePath = [[GMSMutablePath alloc]init];

}
- (IBAction)LineColorPickerButtonPressed:(id)sender {
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
    
    [_LineColorPickerButton setBackgroundColor:color];
    [self renderPolyline];
}

- (IBAction)LineConfirmButtonPressed:(id)sender {
    
    MarkupFeature* feature = [[MarkupFeature alloc]init];
    feature.collabRoomId = [_dataManager getSelectedCollabroomId];
    
    if(_MarkerArray.count < 2){
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Add more points",nil) message:@"You must have at least 2 points to create a line feature" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
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
    
    feature.geometryFiltered = [self CreateGeometryArrayFromMarkers:_MarkerArray];
    feature.geometry = [self buildGeometryStringFromPolygon:feature.geometryFiltered];
    feature.opacity = [NSNumber numberWithFloat:1.0];
    feature.type = @"sketch";
    feature.username = [_dataManager getUsername];
    feature.usersessionId = [_dataManager getUserSessionId];
    feature.featureId = @"draft";
    
    double temp =  [[NSDate date] timeIntervalSince1970];
    NSNumber *date = [NSNumber numberWithLongLong:round(temp)];
    feature.seqtime = date;
    
    [_dataManager addMarkupFeatureToStoreAndForward:feature];
    [_mapViewController addFeatureToMap:feature];
    
    [self ViewExit]; //used to clean map of posted feature. doesn't exit this view
}

-(NSMutableArray*)CreateGeometryArrayFromMarkers: (NSMutableArray*) markers{
    
    NSMutableArray* geometry = [NSMutableArray new];
    
    for(int i = 0; i < markers.count; i++){
        GMSMarker* marker = [markers objectAtIndex:i];
        
        [geometry addObject:[NSArray arrayWithObjects: [NSNumber numberWithDouble: marker.position.latitude], [NSNumber numberWithDouble: marker.position.longitude] ,nil] ];
    }
    
    return geometry;
}

-(NSString*)buildGeometryStringFromPolygon: (NSMutableArray*)polygonCoords{
    
    NSString* polygonString = @"LINESTRING(";
    
    for(int i = 0; i < polygonCoords.count; i++){
        
        NSArray *coord = [polygonCoords objectAtIndex:i];
        
        polygonString = [polygonString stringByAppendingFormat:@"%@%@%@",[NSString stringWithFormat:@"%@",[coord objectAtIndex:1]],@" ", [NSString stringWithFormat:@"%@",[coord objectAtIndex:0]]];
        
        if(i != polygonCoords.count -1){
            polygonString = [polygonString stringByAppendingString:@","];
        }
    }
    
    polygonString = [polygonString stringByAppendingString:@")"];
    
    return polygonString;
}

- (IBAction)LineCancelButtonPressed:(id)sender {
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys: TypeSelectView,@"newState", nil];
    
    NSNotification *switchMapEditState = [NSNotification notificationWithName:@"mapEditSwitchStateNotification" object:self userInfo:dict];
    [[NSNotificationCenter defaultCenter] postNotification:switchMapEditState];
}

- (IBAction)LineRemoveLastPointButtonPressed:(id)sender {
    if(_MarkerArray.count > 0){
        GMSMarker* temp = [_MarkerArray objectAtIndex:_MarkerArray.count-1];
        temp.map = nil;
        [_MarkerArray removeLastObject];
        [self renderPolyline];
    }
}

-(void)ViewExit{
    
    for(GMSMarker* marker in _MarkerArray){
        marker.map = nil;
    }
    
    [_MarkerArray removeAllObjects];
    [_linePath removeAllCoordinates];
    _polyline.map = nil;
}

-(void)MapMarkerDragged: (GMSMarker *)marker{
    for(int i = 0 ; i < _MarkerArray.count; i++){
        
        GMSMarker* arrayMarker = [_MarkerArray objectAtIndex:i];
        if(arrayMarker == marker){
            
            [_MarkerArray replaceObjectAtIndex:i withObject:marker];
        }
    }
    [self renderPolyline];
}

-(void)MapTapped: (CLLocationCoordinate2D)coordinate{
    
    GMSMarker* marker = [[GMSMarker alloc] init];
    marker.draggable = true;
    marker.title = NSLocalizedString(@"Location", nil);
    marker.position = coordinate;
    marker.map = _mapViewController.mapView;
    marker.icon = [UIImage imageNamed:@"helispot.png"];
    
    [_MarkerArray addObject:marker];

    [self renderPolyline];
}

-(void)renderPolyline{
    
    [_linePath removeAllCoordinates];
    _polyline.map = nil;
    
    for(GMSMarker* marker in _MarkerArray){
        [_linePath addCoordinate:marker.position];
    }
    
    _polyline = [GMSPolyline polylineWithPath:_linePath];
    [_polyline setStrokeColor:_selectedColor];
    
    _polyline.map = _mapViewController.mapView;
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

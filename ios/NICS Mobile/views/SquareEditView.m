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
//  SquareEditView.m
//  NICS Mobile
//
//

#import "SquareEditView.h"

@interface SquareEditView ()

@end

@implementation SquareEditView

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
    _polygonPath = [[GMSMutablePath alloc]init];
}
- (IBAction)SquareConfirmButtonPressed:(id)sender {
    MarkupFeature* feature = [[MarkupFeature alloc]init];
    feature.collabRoomId = [_dataManager getSelectedCollabroomId];
    
    if(_MarkerArray.count < 2){
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Add more points",nil) message:@"You must have at least 2 points to create a polygon feature" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
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
    feature.opacity = [NSNumber numberWithFloat:0.2];
    feature.type = @"square";
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

- (IBAction)SquareCancelButtonPressed:(id)sender {
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys: TypeSelectView,@"newState", nil];
    
    NSNotification *switchMapEditState = [NSNotification notificationWithName:@"mapEditSwitchStateNotification" object:self userInfo:dict];
    [[NSNotificationCenter defaultCenter] postNotification:switchMapEditState];
}

- (IBAction)SquareColorPickerButtonPressed:(id)sender {
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
    
    [_SquareColorPickerButton setBackgroundColor:color];
    [self renderPolygon];
}

-(NSMutableArray*)CreateGeometryArrayFromMarkers: (NSMutableArray*) markers{
    
    NSMutableArray* geometry = [NSMutableArray new];
    
    GMSMarker* marker1 = [_MarkerArray objectAtIndex:0];
    GMSMarker* marker2 = [_MarkerArray objectAtIndex:1];
    
    [geometry addObject:[NSArray arrayWithObjects: [NSNumber numberWithDouble: marker1.position.latitude], [NSNumber numberWithDouble: marker1.position.longitude] ,nil] ];
    [geometry addObject:[NSArray arrayWithObjects: [NSNumber numberWithDouble: marker1.position.latitude], [NSNumber numberWithDouble: marker2.position.longitude] ,nil] ];
    [geometry addObject:[NSArray arrayWithObjects: [NSNumber numberWithDouble: marker2.position.latitude], [NSNumber numberWithDouble: marker2.position.longitude] ,nil] ];
    [geometry addObject:[NSArray arrayWithObjects: [NSNumber numberWithDouble: marker2.position.latitude], [NSNumber numberWithDouble: marker1.position.longitude] ,nil] ];
    [geometry addObject:[geometry objectAtIndex:0]];
    
    return geometry;
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


-(void)ViewExit{
    for(GMSMarker* marker in _MarkerArray){
        marker.map = nil;
    }
    
    [_MarkerArray removeAllObjects];
    [_polygonPath removeAllCoordinates];
    _polygon.map = nil;
}

-(void)MapMarkerDragged: (GMSMarker *)marker{
    for(int i = 0 ; i < _MarkerArray.count; i++){
        
        GMSMarker* arrayMarker = [_MarkerArray objectAtIndex:i];
        if(arrayMarker == marker){
            
            [_MarkerArray replaceObjectAtIndex:i withObject:marker];
        }
    }
    [self renderPolygon];
}

-(void)MapTapped: (CLLocationCoordinate2D)coordinate{

    if(_MarkerArray.count <2){
    
        GMSMarker* marker = [[GMSMarker alloc] init];
        marker.draggable = true;
        marker.title = NSLocalizedString(@"Location", nil);
        marker.position = coordinate;
        marker.map = _mapViewController.mapView;
        marker.icon = [UIImage imageNamed:@"helispot.png"];
        
        [_MarkerArray addObject:marker];
    }else{
        GMSMarker* marker = [_MarkerArray objectAtIndex:1];
        marker.position = coordinate;
    }
    
    [self renderPolygon];
}

-(void)renderPolygon{
    
    if(_MarkerArray.count >= 2){
        [_polygonPath removeAllCoordinates];
        _polygon.map = nil;
        
        GMSMarker* marker1 = [_MarkerArray objectAtIndex:0];
        GMSMarker* marker2 = [_MarkerArray objectAtIndex:1];
        
        [_polygonPath addCoordinate:marker1.position];
        [_polygonPath addLatitude:marker1.position.latitude longitude:marker2.position.longitude];
        [_polygonPath addCoordinate:marker2.position];
        [_polygonPath addLatitude:marker2.position.latitude longitude:marker1.position.longitude];

        _polygon = [GMSPolygon polygonWithPath:_polygonPath];
        [_polygon setStrokeColor:_selectedColor];
        [_polygon setFillColor:[_selectedColor colorWithAlphaComponent:0.2]];
        
        _polygon.map = _mapViewController.mapView;
    }
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

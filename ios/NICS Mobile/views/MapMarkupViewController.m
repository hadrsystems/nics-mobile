/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
//  ViewController.m
//  SidebarDemo
//
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import "MapMarkupViewController.h"

@interface MapMarkupViewController ()

@end

static NSNumber* selectedIndex;
static int mapType = kGMSTypeNormal;

@implementation MapMarkupViewController

MapMarkupViewController *FullscreenMap = nil;
UIPopoverController *myPopOver = nil;
bool *markupProcessing = false;

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    _dateFormatter = [[NSDateFormatter alloc] init];
    [_dateFormatter setDateFormat:@"MM/dd HH:mm:ss"];
    
    [_coordinateView setDelegate:self];
//    CGRect framez = self.view.frame;
//    framez.size.height = framez.size.height - self.navigationController.navigationBar.frame.size.height;
//    //_coordinateView.mainFrame = self.view.frame;
//    UIView *view = [_coordinateView.subviews objectAtIndex:0];
//    CGRect frame = view.frame;
//    frame.size.width = _mapView.frame.size.width;
//    view.frame = frame;

    
    
    
//    CGRect tableFrame = _tableView.frame;
//    _tableView.bounds = tableFrame;
// 
//    CGRect selfFrame = self.view.frame;
//    selfFrame.size.width = _mapView.frame.size.width;
//    selfFrame.size.height = self.navigationController.navigationBar.frame.size.height;
//    self.view.frame = selfFrame;
    
    
    
    _dataManager = [DataManager getInstance];
    
    _markupShapes = [NSMutableArray new];
    _undoStack = [NSMutableArray new];
    _markupFeatures = [NSMutableArray new];
    _wfsMarkers = [NSMutableArray new];

    _mapView.mapType = _dataManager.CurrentMapType;
    _mapView.trafficEnabled = _dataManager.TrafficDisplay;
    _mapView.indoorEnabled = _dataManager.IndoorDisplay;
    
    [self addMarkupFromServer];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(addMarkupFromServer) name:@"markupFeaturesUpdateReceived" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(checkIosVersionThenWfsUpdate) name:@"WfsUpdateRecieved" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(recieveMapType:) name:@"MapTypeSwitched" object:nil];
//    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadTableView) name:@"CollabRoomSwitched" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(addMarkupFromServer) name:@"CollabRoomSwitched" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(addMarkupFromServer) name:@"IncidentSwitched" object:nil];
    
    _mapView.settings.compassButton = YES;
    _mapView.myLocationEnabled = YES;
    _mapView.settings.myLocationButton = YES;
    
    [_mapView moveCamera:[GMSCameraUpdate setTarget:CLLocationCoordinate2DMake([_dataManager.currentIncident.lat doubleValue], [_dataManager.currentIncident.lon doubleValue]) zoom:10]];
    
    _isFirstLoad = YES;
    
    [_mapView animateToLocation:[_dataManager.locationManager location].coordinate];
    [_mapView animateToZoom:12];
    _mapView.delegate = self;
    
    
    
//    CLLocationCoordinate2D* coord =
    _myCustomMarker = [[GMSMarker alloc] init];
    _myCustomMarker.position = CLLocationCoordinate2DMake(0,0);
    
    UIImage *image;
    
    float mapBtnOffset = 65;
    
    if([_dataManager getIsIpad] == true){
  
        mapBtnOffset = 75;
        
        _fullscreenButton = [UIButton buttonWithType:UIButtonTypeCustom];
    
        image = [UIImage imageNamed:@"enter_fullscreen_button.png"];
        [_fullscreenButton setImage:image forState:UIControlStateNormal];
        
        SEL noArgumentSelectorFullScreen = @selector(FullScreenMap);
        [_fullscreenButton addTarget:self action:noArgumentSelectorFullScreen forControlEvents:UIControlEventTouchUpInside];
    
        _fullscreenButton.frame = CGRectMake(_mapView.bounds.size.width - 200, _mapView.bounds.size.height - mapBtnOffset, 50, 50);
        _fullscreenButton.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleTopMargin;
        [_fullscreenButton setTitle:@"Fullscreen" forState:UIControlStateNormal];
        [_mapView addSubview:_fullscreenButton];
    }
        _extendMapButton = [UIButton buttonWithType:UIButtonTypeCustom];
        
        image  = [UIImage imageNamed:@"down_arrow_icon.png"];
        [_extendMapButton setImage:image forState:UIControlStateNormal];
        
        SEL noArgumentSelectorExtend = @selector(ExtendMapDown);
        [_extendMapButton addTarget:self action:noArgumentSelectorExtend forControlEvents:UIControlEventTouchUpInside];
        
        _extendMapButton.frame = CGRectMake(_mapView.bounds.size.width - 130, _mapView.bounds.size.height - mapBtnOffset, 50, 50);
        _extendMapButton.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleTopMargin;
        [_extendMapButton setTitle:@"Extend" forState:UIControlStateNormal];
        [_mapView addSubview:_extendMapButton];

}

-(void)reloadTableView{
    [_tableView reloadData];
}

-(void)FullScreenMap{
    if(FullscreenMap == nil){
        FullscreenMap = [[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"MapFullscreenViewID"];
    }
    if(myPopOver == nil){
        myPopOver = [[UIPopoverController alloc]initWithContentViewController:FullscreenMap];
        CGRect displayFrom = CGRectMake(1,1,1,1);
        [myPopOver presentPopoverFromRect:displayFrom inView:self.view permittedArrowDirections:UIPopoverArrowDirectionLeft animated:YES];
        [FullscreenMap setPopoverButtons];
    }else{
        [myPopOver dismissPopoverAnimated:YES];
        myPopOver = nil;
    }
}

-(void)ExtendMapDown{
    
    if(_originalMapFrame.size.height == 0){     //store original frame size first time map is extended
        _originalMapFrame =_mapView.frame;
    }
    
    [UIView beginAnimations: @"anim" context: nil];
    [UIView setAnimationBeginsFromCurrentState: YES];
    [UIView setAnimationDuration: 0.35f];
    
    UIImage *image;
    
    if(_mapView.frame.size.height == _originalMapFrame.size.height)
    {
        CGRect newFrame;
        
        if(_dataManager.isIpad){
            newFrame = CGRectMake(0, 0, 512, 622);
        }else{
            newFrame = self.view.superview.frame;
            newFrame.size.height -= self.view.frame.origin.y;
        }
        _mapView.frame = newFrame;
        image = [UIImage imageNamed:@"up_arrow_icon.png"];
        
    }else{
        _mapView.frame = _originalMapFrame;
        image = [UIImage imageNamed:@"down_arrow_icon.png"];
    }
    
    [_extendMapButton setImage:image forState:UIControlStateNormal];
    [UIView commitAnimations];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)mapView:(GMSMapView *)mapView idleAtCameraPosition:(GMSCameraPosition *)position {
    if(_previousZoomLevel != position.zoom) {
        if(position.bearing != 0) {
            GMSCameraPosition *newPosition = [GMSCameraPosition cameraWithTarget:position.target zoom:position.zoom bearing:0 viewingAngle:0];
            GMSCameraUpdate *update = [GMSCameraUpdate setCamera:newPosition];
            [_mapView moveCamera:update];
        }
        [self addMarkupFromServer];
    }
    _previousZoomLevel = position.zoom;

}


-(void) addMarkupFromServer {
    if(_currentShape == nil && !_ignoreUpdate) {
        
        //this currently clears all markers as well.
        //need to make it so this only clears markups. it is a waste of energy to redreaw the tracking markers when they are not being updated
        [_mapView clear];
        
        
        [_markupFeatures removeAllObjects];
        [_markupShapes removeAllObjects];
        
        _myCustomMarker.map = _mapView;
        //[_tableView setHidden:YES];
        
        if(markupProcessing == false){
            markupProcessing = true;
            
            dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
            dispatch_async(queue, ^{
                [_markupFeatures addObjectsFromArray:[_dataManager getAllMarkupFeaturesForCollabroomId:[_dataManager getSelectedCollabroomId]]];
                
                _ignoreUpdate = YES;
                
                MarkupType currentType;
                for(MarkupFeature *feature in _markupFeatures) {
                    currentType = [Enums convertToMarkupTypeFromString:feature.type];
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        if(currentType == symbol) {
                            MarkupSymbol *symbol = [[MarkupSymbol alloc] initWithMap:_mapView feature:feature];
                            [_markupShapes addObject:symbol];

                        } else if(currentType == segment) {
                            if([feature.dashStyle isEqualToString:@"solid"] || [feature.dashStyle isEqualToString:@"completedLine"]) {
                                MarkupSegment *segment = [[MarkupSegment alloc] initWithMap:_mapView feature:feature];
                                [_markupShapes addObject:segment];
                            } else {
                                MarkupFireline *fireline = [[MarkupFireline alloc] initWithMap:_mapView feature:feature];
                                [_markupShapes addObject:fireline];
                            }
                        
                        } else if(currentType == rectangle || currentType == polygon) {
                            MarkupPolygon *polygon = [[MarkupPolygon alloc] initWithMap:_mapView feature:feature];
                            [_markupShapes addObject:polygon];
                        } else if(currentType == text) {
                            MarkupText *text = [[MarkupText alloc] initWithMap:_mapView feature:feature];
                            [_markupShapes addObject:text];
                        }
                    });
                }
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[self tableView] reloadData];
                    [self checkIosVersionThenWfsUpdate];
       /*
                    if(_isFirstLoad && _markupShapes != nil){
                        MarkupBaseShape *lastMarkup = [_markupShapes lastObject];
                        [_mapView animateWithCameraUpdate:lastMarkup.points[0] ];
                        _isFirstLoad = FALSE;
                    }
         */
                });
                markupProcessing = false;
                _ignoreUpdate = NO;
            });
        }
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    
    return view;
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_markupShapes count];
}

-(UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    cell.backgroundColor = [UIColor clearColor];
    if(_markupShapes.count > 0) {
        MarkupBaseShape* shape = _markupShapes[indexPath.row];
        
        NSDate* date = [NSDate dateWithTimeIntervalSince1970:[shape.feature.seqTime longLongValue]/1000.0];
        
        UILongPressGestureRecognizer *longPressGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressCell:)];
        [cell addGestureRecognizer:longPressGesture];
        
        UILabel *label = (UILabel *)[cell.contentView viewWithTag:10];

//        label.text = [NSString stringWithFormat:@"%@%@%@", [_dateFormatter stringFromDate:date], @" - ", shape.feature.username];
        label.text = [NSString stringWithFormat:@"%@%@%@", shape.feature.lastupdate, @" - ", shape.feature.username];
        
        UILabel *timeLabel = (UILabel *)[cell.contentView viewWithTag:20];

        if(!shape.feature.featureattributes) {
            
            if(shape.feature.labelText) {
                timeLabel.text = [NSString stringWithFormat:@"%@%@%@%@",NSLocalizedString(@"Type: ",nil), shape.feature.type, @" - ", shape.feature.labelText];
            } else {
                timeLabel.text = [NSString stringWithFormat:@"%@%@", NSLocalizedString(@"Type: ",nil), shape.feature.type];
            }
        } else {
            timeLabel.text = [NSString stringWithFormat:@"%@%@%@%@", NSLocalizedString(@"Type: ",nil), shape.feature.type, @" - ", shape.feature.featureattributes];
        }
    }
    
    return cell;
}

-(NSIndexPath *)tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    UIView *selectedView = [[UIView alloc]init];
    selectedView.backgroundColor = [UIColor colorWithRed:0.1953125 green:0.5 blue:0.609375 alpha:1.0];
    
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    cell.selectedBackgroundView = selectedView;
    cell.backgroundColor = [UIColor clearColor];
    
    MarkupBaseShape* shape = _markupShapes[indexPath.row];
    
    MarkupType type = [Enums convertToMarkupTypeFromString:shape.feature.type];

    if(type == symbol || type == text) {
        CLLocationCoordinate2D positionCoordinate;
        [[shape.points objectAtIndex:0] getValue:&positionCoordinate];
        
        [_mapView animateToLocation:positionCoordinate];
        [_mapView animateToZoom:12];
    } else {
        GMSCoordinateBounds *bounds = [[GMSCoordinateBounds alloc] initWithPath:[shape getPath]];
        GMSCameraUpdate *update = [GMSCameraUpdate fitBounds:bounds withPadding:100.f];
        
        [_mapView animateWithCameraUpdate:update];
        
    }
    
    return indexPath;
}

- (void)longPressCell:(UILongPressGestureRecognizer *)gesture
{
	// only when gesture was recognized, not when ended
	if (gesture.state == UIGestureRecognizerStateBegan)
	{
		// get affected cell
		UITableViewCell *cell = (UITableViewCell *)[gesture view];
        
		// get indexPath of cell
		NSIndexPath *indexPath = [self.tableView indexPathForCell:cell];
        
        [self.tableView selectRowAtIndexPath:indexPath animated:NO scrollPosition:UITableViewScrollPositionNone];
        
//        MarkupBaseShape* shape = _markupShapes[indexPath.row];
//        if([shape.feature.nickname isEqualToString:@"Piyush Agarwal"]) {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Delete the selected shape?" message:@"Hello world" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"OK", nil];
        objc_setAssociatedObject(alertView, &selectedIndex, [NSNumber numberWithInteger:indexPath.row], OBJC_ASSOCIATION_RETAIN);
        [alertView show];
//        }
        #pragma warning change this to show confirm/cancel dialog for markup delete and only for current user permissions

	}
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    /*      uncomment to allow user to delete markups from app
     
    if (buttonIndex == 0) {
        NSLog(@"user pressed Cancel");
    } else {
        NSLog(@"user pressed OK");
        
        NSNumber *index = objc_getAssociatedObject(alertView, &selectedIndex);
        MarkupBaseShape* shape = _markupShapes[[index integerValue]];
        NSString* response = [_dataManager deleteMarkupFeatureById: shape.feature.featureId];
        [shape removeFromMap];
        [_markupShapes removeObject:shape];

        [[self tableView] reloadData];
        // do something with this action
        NSLog(@"Long-pressed cell %@\n%@", shape.feature.nickname, response);
    }
     */
}

-(void)checkIosVersionThenWfsUpdate
{
#if !(TARGET_IPHONE_SIMULATOR)
    
//         dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);dispatch_async(queue, ^{
             [self updateWfsLayers];
//         });
#else
          dispatch_async(dispatch_get_main_queue(), ^{
              [self updateWfsLayers];});
#endif
}

- (void)updateWfsLayers{
    
        NSMutableArray* wfsFeatures = [ActiveWfsLayerManager getWfsFeatures];
        
    //clear old markers
//    for (GMSMarker *currentMarker in _wfsMarkers)
    for(int i = 0; i < _wfsMarkers.count;i++)
    {
        GMSMarker* marker = [_wfsMarkers objectAtIndex:i];
        marker.map = nil;
    }
    [_wfsMarkers removeAllObjects];
        
        
    for ( WfsFeature *currentFeature in wfsFeatures)
    {
    CLLocationCoordinate2D markerLocation = CLLocationCoordinate2DMake([currentFeature.latitude doubleValue],[currentFeature.longitude doubleValue]);
    GMSMarker *marker = [GMSMarker markerWithPosition: markerLocation];
    marker.appearAnimation = kGMSMarkerAnimationNone;
        
        UIImage* image;
        
    if([currentFeature.age integerValue] > 1440){
         image = [UIImage imageNamed:@"mdt_dot_stale.png"];
        marker.icon = image;
    }else if([currentFeature.speed integerValue] > 0){
        image = [UIImage imageNamed:@"mdt_dot_directional.png"];
        
        
        //rotate image to match course. this could be moved into the utils script as a more general purpose image rotating tool
        CGSize size = image.size;
        
        UIGraphicsBeginImageContext(size);
        
        CGContextRef context = UIGraphicsGetCurrentContext();
        CGContextTranslateCTM(context ,0.5f * image.size.width, 0.5 * image.size.height);
        CGContextRotateCTM(context, [currentFeature.course doubleValue] * M_PI/180);
        [ image drawInRect:(CGRect){ { -size.width * 0.5f, -size.height * 0.5f }, size } ] ;
        UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
        
        UIGraphicsEndImageContext();
        
        marker.icon = newImage;
        
    }else{
        image = [UIImage imageNamed:@"mdt_dot.png"];
        marker.icon = image;
    }
    
    marker.map = _mapView;
    marker.title = currentFeature.name;
    marker.snippet = [currentFeature.timestamp stringByAppendingString: [currentFeature.course stringValue]];//currentFeature.timestamp + [currentFeature.course stringValue];
        
        
        [_wfsMarkers addObject:marker];
    }
}

- (void)mapView:(GMSMapView *)mapView didTapAtCoordinate:(CLLocationCoordinate2D)coordinate {
    NSLog(@"You tapped at %f,%f", coordinate.latitude, coordinate.longitude);
    
    _myCustomMarker.position = CLLocationCoordinate2DMake(coordinate.latitude,coordinate.longitude);
    _myCustomMarker.title = NSLocalizedString(@"Location", nil);
    
    
    _myCustomMarker.snippet = [NSString stringWithFormat:@"%f%@%f", coordinate.latitude, @" , " , coordinate.longitude];
    _myCustomMarker.map = _mapView;
    
    self.dataManager.mapSelectedLatitude =coordinate.latitude;
    self.dataManager.mapSelectedLongitude =coordinate.longitude;
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"mapCustomLocationChanged" object:nil];
    
//    [self.navigationController popViewControllerAnimated:YES];
    
}

- (IBAction)shapeButtonPressed:(UIButton *)button {
    [_shapeButtonView setHidden: YES];
    [_tableView setHidden: YES];
    [_coordinateView setHidden: NO];
    [_coordinateView setShape: (MarkupType)button.tag];
}

- (void)submitMarkupCoordinates:(NSArray *)coordinates {
    [_shapeButtonView setHidden: NO];
    [_tableView setHidden: NO];
    [_coordinateView setHidden: YES];
}

- (void)cancelMarkupCoordinates {
    [_shapeButtonView setHidden: NO];
    [_tableView setHidden: NO];
    [_coordinateView setHidden: YES];
}

- (void) updateTitle:(NSString *)title {
    self.title = title;
}

-(void)setPopoverButtons{
    UIImage *image = [UIImage imageNamed:@"exit_fullscreen_button.png"];
    [_fullscreenButton setImage:image forState:UIControlStateNormal];
    
    [_extendMapButton setHidden:TRUE];
}

-(void)recieveMapType:(NSNotification *)notification{
    _mapView.mapType =  [[[notification userInfo] valueForKey:@"mapType"] intValue];
    _mapView.trafficEnabled = [[[notification userInfo] valueForKey:@"trafficDisplay"] boolValue];
    _mapView.indoorEnabled = [[[notification userInfo] valueForKey:@"indoorDisplay"] boolValue];
}

- (GMSMarker*)getCustomMarker{
    return self.myCustomMarker;
}

-(void)setUiViewControllerToReturnTo:(UIViewController*)controller{
    self.previousViewToReturnTo = controller;
}

@end

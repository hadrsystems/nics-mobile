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
//  SymbolEditView.m
//  NICS Mobile
//
//

#import "SymbolEditView.h"

@interface SymbolEditView ()

@end

@implementation SymbolEditView

-(void)viewWillAppear:(BOOL)animated{
    
    _SymbolSelectSymbolButton.titleLabel.numberOfLines = 2;
    _SymbolSelectSymbolButton.titleLabel.minimumScaleFactor = 0.5;
    _SymbolSelectSymbolButton.titleLabel.adjustsFontSizeToFitWidth = YES;
    _SymbolSelectSymbolButton.titleLabel.lineBreakMode = NSLineBreakByClipping;
    
}

- (void)viewDidLoad {
    [super viewDidLoad];
    _dataManager = [DataManager getInstance];
    _selectedSymbol = @"symbol";
    _selectedSymbolImageView.image = [UIImage imageNamed:_selectedSymbol];
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
    _symbolMarker = [[GMSMarker alloc]init];
    _symbolMarker.draggable = true;
    _symbolMarker.title = NSLocalizedString(@"Location", nil);;
    [_symbolMarker setIcon:[UIImage imageNamed:_selectedSymbol]];
    [_LocationWidgetView setHidden:YES];
    
}
-(void)ViewExit{
    _symbolMarker.map = nil;
    _symbolMarker = nil;
}

-(void)MapMarkerDragged: (GMSMarker *)marker{
    
    [_LocationWidgetView setData : [NSString stringWithFormat:@"%f", marker.position.latitude] : [NSString stringWithFormat:@"%f", marker.position.longitude] : false];
    
    _symbolMarker.position = marker.position;
    _symbolMarker.map = _mapViewController.mapView;
}

-(void)MapTapped: (CLLocationCoordinate2D)coordinate{
    
    [_LocationWidgetView setData : [NSString stringWithFormat:@"%f", coordinate.latitude] : [NSString stringWithFormat:@"%f", coordinate.longitude] : false];
    
    _symbolMarker.position = coordinate;
    _symbolMarker.map = _mapViewController.mapView;
    
}

-(void)setSelectedSymbol:(NSString*)symbolName{
    _selectedSymbol = symbolName;
    _selectedSymbolImageView.image = [UIImage imageNamed:_selectedSymbol];
    [_symbolMarker setIcon:[UIImage imageNamed:_selectedSymbol]];
}

- (IBAction)SymbolSelectCancelButtonPressed:(id)sender {

    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys: TypeSelectView,@"newState", nil];
    NSNotification *switchMapEditState = [NSNotification notificationWithName:@"mapEditSwitchStateNotification" object:self userInfo:dict];
    [[NSNotificationCenter defaultCenter] postNotification:switchMapEditState];
}

- (IBAction)SymbolSelectConfirmButtonPressed:(id)sender {
    MarkupFeature* feature = [[MarkupFeature alloc]init];
    feature.collabRoomId = [_dataManager getSelectedCollabroomId];
    
    if(_symbolMarker.map == nil){
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Please place symbol",nil) message:@"You must place the symbol on the map before pressing confirm" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alertView show];
        return;
    }
    
    if(feature.collabRoomId == [NSNumber numberWithInt:-1]){
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Please join a collabroom",nil) message:@"You must join a collabroom before posting new map features" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alertView show];
        return;
    }
    
    feature.fillColor = @"#FFFFFF";
    feature.strokeColor = @"#FFFFFF";
    feature.strokeWidth = [NSNumber numberWithFloat:2.0];
    feature.geometry = [NSString stringWithFormat: @"%@%f%@%f%@", @"POINT(", _symbolMarker.position.longitude,@" ",_symbolMarker.position.latitude,@")"];
    feature.graphic = [NSString stringWithFormat: @"%@%@", @"images/drawmenu/markers/",_selectedSymbol];
    //    feature.graphic = _selectedSymbol;
    feature.graphicHeight = [NSNumber numberWithInt:24];
    feature.graphicWidth = [NSNumber numberWithInt:24];
    feature.opacity = [NSNumber numberWithFloat:1.0];
    feature.type = @"marker";
    feature.username = [_dataManager getUsername];
    feature.usersessionId = [_dataManager getUserSessionId];
    feature.featureId = @"draft";
    
//    NSTimeInterval temp =  [[NSDate date] timeIntervalSince1970] * 1000.00;
    long long temp = (long long)([[NSDate date] timeIntervalSince1970]);

    NSNumber *date = [NSNumber numberWithLongLong:round(temp)];
    feature.seqtime = date;
    
    [_dataManager addMarkupFeatureToStoreAndForward:feature];
    [_mapViewController addFeatureToMap:feature];
    
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys: TypeSelectView,@"newState", nil];
    NSNotification *switchMapEditState = [NSNotification notificationWithName:@"mapEditSwitchStateNotification" object:self userInfo:dict];
    [[NSNotificationCenter defaultCenter] postNotification:switchMapEditState];
}

- (IBAction)SelectSymbolButtonPressed:(id)sender {
    
    UIView *anchor = sender;
    SelectSymbolMenuViewController *viewControllerForPopover = [[SelectSymbolMenuViewController alloc] init];
    viewControllerForPopover.symbolSelectView = self;
    
    if([_dataManager isIpad]){

        viewControllerForPopover.view.frame = anchor.frame;
        UIPopoverController * popover = [[UIPopoverController alloc] initWithContentViewController:viewControllerForPopover];
        
        [popover presentPopoverFromRect:anchor.frame
                                 inView:anchor.superview
               permittedArrowDirections:UIPopoverArrowDirectionAny animated:YES];
    }else{
        [_mapViewController.navigationController pushViewController:viewControllerForPopover animated:YES];
    }
}

@end

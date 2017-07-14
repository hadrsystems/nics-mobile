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
//  FormEditText.m
//  nics_iOS
//
//

#import "MapEditView.h"

@implementation MapEditView

typedef NSUInteger NSFetchedResultsChangeType;

- (id)init{
    self = [super init];
//    _originalFrame = self.view.frame;
//    [self setup];
    return self;
}

- (void)Setup:(CGRect) frame {
    
    _originalFrame = frame;
    _dataManager = [DataManager getInstance];
    _CurrentView = TypeSelectView;

    //The order that these views are added to the array must match the values of the MapEditViews Enum
    _arrayOfViews = [[NSMutableArray alloc]init];
    [_arrayOfViews addObject:self];
    [_arrayOfViews addObject:[[SymbolEditView alloc]init]];
    [_arrayOfViews addObject:[[SquareEditView alloc]init]];
    [_arrayOfViews addObject:[[PolygonEditView alloc]init]];
    [_arrayOfViews addObject:[[CircleEditView alloc]init]];
    [_arrayOfViews addObject:[[LineEditView alloc]init]];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mapEditSwitchStateNotification:) name:@"mapEditSwitchStateNotification" object:nil];
        
    self.view.frame = _originalFrame;
    for(int i = 0; i < _arrayOfViews.count;i++){//init size of all mapEditViews
        UIViewController *temp = [_arrayOfViews objectAtIndex: i];
        temp.view.frame= _originalFrame;
        [_arrayOfViews setObject:temp atIndexedSubscript:i];
    }

//    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
//    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardWillHideNotification object:nil];
    
    self.view.translatesAutoresizingMaskIntoConstraints = YES;
}

- (IBAction)SymbolButtonPressed:(id)sender {
    [self switchViewState:SymbolView];
}

- (IBAction)CircleButtonPressed:(id)sender {
    [self switchViewState:CircleView];
}

- (IBAction)LineButtonPressed:(id)sender {
    [self switchViewState:LineView];
}

- (IBAction)PolygonButtonPressed:(id)sender {
    [self switchViewState:PolygonView];
}

- (IBAction)SquareButtonPressed:(id)sender {
        [self switchViewState:SquareView];
}

-(void)mapEditSwitchStateNotification:(NSNotification *)notification{

    MapEditViews newState = [[[notification userInfo] valueForKey:@"newState"] intValue];
    [self switchViewState:newState];
}

-(void)switchViewState:(MapEditViews)newState{
 
    [[_arrayOfViews objectAtIndex: _CurrentView] ViewExit];
    
    if(_CurrentView!=TypeSelectView){
        UIViewController* tempController = [_arrayOfViews objectAtIndex: _CurrentView];
        [tempController.view removeFromSuperview];
    }
    _CurrentView = newState;
    
    if(_CurrentView!=TypeSelectView){
        [_mapViewController hideCustomMarker];
        UIViewController* newView =[_arrayOfViews objectAtIndex: _CurrentView];
        [self.view addSubview:newView.view];
    }
    [[_arrayOfViews objectAtIndex: _CurrentView] ViewEnter];
    [[_arrayOfViews objectAtIndex: _CurrentView] setMapMarkupView:_mapViewController];
}

-(void)ViewEnter{
    
}
-(void)ViewExit{
    
}

-(void)setMapMarkupView:(MapMarkupViewController*)mapView{
}

-(void)MapViewMarkerDidDrag:(GMSMarker*) marker{
    if(_CurrentView!=TypeSelectView){
        [[_arrayOfViews objectAtIndex: _CurrentView] MapMarkerDragged:marker];
    }
}

-(void)MapDidTapAtCoordinate:(CLLocationCoordinate2D)coordinate {
    if(_CurrentView!=TypeSelectView){
        [[_arrayOfViews objectAtIndex: _CurrentView] MapTapped:coordinate];
    }
}

@end

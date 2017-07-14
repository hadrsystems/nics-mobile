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
//  RoomCanvasUIViewController.m
//  NICS Mobile
//
//

#import "RoomCanvasUIViewController.h"
#import "MapMarkupViewController.h"
#import "ChatContainerBasicViewController.h"
#import "IncidentButtonBar.h"

@interface RoomCanvasUIViewController ()

@end

@implementation RoomCanvasUIViewController

MapMarkupViewController *mapController = nil;
ChatContainerBasicViewController *chatController = nil;
UIView *selectedRoomController = nil;

UIStoryboard *currentStoryboard;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    _dataManager = [DataManager getInstance];
    currentStoryboard = [UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil];
    
    mapController = [IncidentButtonBar GetMapMarkupController];
    
    CGRect mapFrame = mapController.view.frame;
    mapFrame.size.width = _RoomCanvas.frame.size.width;
    mapFrame.size.height = _RoomCanvas.frame.size.height;
    mapController.view.frame = mapFrame;
    
    [self SetCanvas :mapController.view ];
    
    UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(MapRefreshButtonLongPress:)];
    [longPress setMinimumPressDuration:3];
    [_MapRefreshButton addGestureRecognizer:longPress];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/
/*
- (IBAction)SetCanvasToMap:(id)sender {

 //   chatController = nil;
    
    if(mapController == nil)
    {
        mapController = [currentStoryboard instantiateViewControllerWithIdentifier:@"MapViewID"];
    }
    [self SetCanvas :mapController.view ];
}
*/
/*
- (IBAction)SetCanvasToChat:(id)sender {
    if(chatController == nil)
    {
        chatController = [currentStoryboard instantiateViewControllerWithIdentifier:@"ChatViewID"];
    }
    [self SetCanvas :chatController.view];
    [chatController viewDidAppear:YES];
}
 */

- (void) SetCanvas:(UIView*)newController{
    selectedRoomController = newController;
    [_RoomCanvas addSubview:selectedRoomController ];
}


- (IBAction)MapRefreshButtonPressed:(id)sender {
    [_dataManager requestMarkupFeaturesRepeatedEvery:[[DataManager getMapUpdateFrequencyFromSettings] intValue] immediate:YES];
}

-(void)MapRefreshButtonLongPress:(UILongPressGestureRecognizer*)gesture {
    if ( gesture.state == UIGestureRecognizerStateEnded ) {
        
        NSNotification *resetMapFeaturesNotification = [NSNotification notificationWithName:@"resetMapFeatures" object:self];
        [[NSNotificationCenter defaultCenter] postNotification:resetMapFeaturesNotification];
        
        [_dataManager removeAllFeaturesInCollabroom:[_dataManager getSelectedCollabroomId]];
        [_dataManager requestMarkupFeaturesRepeatedEvery:[[DataManager getMapUpdateFrequencyFromSettings] intValue] immediate:YES];
    
    }
}

@end

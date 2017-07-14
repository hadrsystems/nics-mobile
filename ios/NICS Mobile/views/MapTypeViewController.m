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
//  MapTypeViewController.m
//  NICS Mobile
//
//

#import "MapTypeViewController.h"
#import "MapMarkupViewController.h"

@interface MapTypeViewController ()

@end

NSNotificationCenter *notificationCenter;


@implementation MapTypeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _datamanager = [DataManager getInstance];
    
    [_TrafficToggle setOn:_datamanager.TrafficDisplay];
    [_IndoorToggle setOn:_datamanager.IndoorDisplay];
    
    notificationCenter = [NSNotificationCenter defaultCenter];
    
    if(_datamanager.CurrentMapType == kGMSTypeNormal){
         [_NormalButton setSelected:YES];
    }else if(_datamanager.CurrentMapType == kGMSTypeHybrid){
         [_HybridButton setSelected:YES];
    }else if(_datamanager.CurrentMapType == kGMSTypeSatellite){
        [_SatelliteButton setSelected:YES];
    }else if(_datamanager.CurrentMapType == kGMSTypeTerrain){
         [_TerrainButton setSelected:YES];
    }
    
    // Do any additional setup after loading the view.
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

- (IBAction)NormalButtonPressed:(id)sender {
    _datamanager.CurrentMapType = kGMSTypeNormal;
    [_NormalButton setSelected:YES];
    [_HybridButton setSelected:NO];
    [_SatelliteButton setSelected:NO];
    [_TerrainButton setSelected:NO];
    [self sendMapUpdatedNotification];
}

- (IBAction)HybridButtonPressed:(id)sender {
    _datamanager.CurrentMapType = kGMSTypeHybrid;
    [_NormalButton setSelected:NO];
    [_HybridButton setSelected:YES];
    [_SatelliteButton setSelected:NO];
    [_TerrainButton setSelected:NO];
    [self sendMapUpdatedNotification];
}

- (IBAction)SatelliteButtonPressed:(id)sender {
    _datamanager.CurrentMapType = kGMSTypeSatellite;
    [_NormalButton setSelected:NO];
    [_HybridButton setSelected:NO];
    [_SatelliteButton setSelected:YES];
    [_TerrainButton setSelected:NO];
    [self sendMapUpdatedNotification];
}

- (IBAction)TerrainButtonPressed:(id)sender {
    _datamanager.CurrentMapType = kGMSTypeTerrain;
    [_NormalButton setSelected:NO];
    [_HybridButton setSelected:NO];
    [_SatelliteButton setSelected:NO];
    [_TerrainButton setSelected:YES];
    [self sendMapUpdatedNotification];
}

- (IBAction)TrafficToggled:(id)sender {
    _datamanager.TrafficDisplay = !_datamanager.TrafficDisplay;
    [self sendMapUpdatedNotification];
}

- (IBAction)IndoorToggled:(id)sender {
    _datamanager.IndoorDisplay = !_datamanager.IndoorDisplay;
    [self sendMapUpdatedNotification];
}

-(void)sendMapUpdatedNotification{
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys: [NSNumber numberWithInt:_datamanager.CurrentMapType],@"mapType",[NSNumber numberWithBool:_datamanager.TrafficDisplay], @"trafficDisplay",[NSNumber numberWithBool:_datamanager.IndoorDisplay],@"indoorDisplay" , nil];
    
    NSNotification *MapTypeSwitched = [NSNotification notificationWithName:@"MapTypeSwitched" object:self userInfo:dict];
    [notificationCenter postNotification:MapTypeSwitched];
}
@end

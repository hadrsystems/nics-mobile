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
//  DetailViewController.m
//  nics
//
//

#import "GARViewController.h"

@interface GARViewController ()

@end

@implementation GARViewController

#pragma mark - Managing the detail item

- (void)configureView
{
    [self garTotal:nil];
}

- (void)viewDidLoad
{
    _dataManager = [DataManager getInstance];
    _dateFormatter = [Utils getDateFormatter];
    
    [super viewDidLoad];
    _sliderValues = [NSMutableDictionary new];
    
    // Set the side bar button action. When it's tapped, it'll show up the sidebar.
    _sidebarButton.target = self.revealViewController;
    _sidebarButton.action = @selector(revealToggle:);
    
    // Disable gesture since it interferes with sliders.
    //[self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
    
	// Do any additional setup after loading the view, typically from a nib.
    [self configureView];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction) countdownSliderChanged:(UISlider *)sender
{
    // Action Hooked to 'Value Changed' (continuous)
    
    // Update label (to rounded value)
    
    CGFloat value = [sender value];
    
    CGFloat roundValue = roundf(value);
    [_sliderValues setObject:[NSNumber numberWithInt:roundValue] forKey:[NSNumber numberWithLong:sender.tag]];
    
    UILabel *activeLabel = [self getActiveLabelForSlider:sender];
    
    if(activeLabel != nil) {
        [activeLabel setText:[NSString stringWithFormat:@"%2.f", roundValue]];
        
        if(roundValue > 6) {
            activeLabel.backgroundColor = [UIColor redColor];
        } else if(roundValue > 3) {
            activeLabel.backgroundColor = [UIColor yellowColor];
        } else {
            activeLabel.backgroundColor = [UIColor greenColor];
        }
        
        activeLabel.textColor = [UIColor blackColor];
    }
}


- (IBAction) countdownSliderFinishedEditing:(UISlider *)sender
{
    // Action Hooked to 'Touch Up Inside' (when user releases knob)
    
    // Adjust knob (to rounded value)
    
    CGFloat value = [sender value];
    
    CGFloat roundValue = roundf(value);
    
    if (value != roundValue) {
        [sender setValue:roundValue];
        [_sliderValues setObject:[NSNumber numberWithInt:roundValue] forKey:[NSNumber numberWithLong:sender.tag]];
    }
    [self garTotal:nil];
}

- (IBAction)garTotal:(UIButton *)sender {
    CGFloat total = 0;
    for(NSNumber *number in [_sliderValues allValues]) {
        total += [number floatValue];
    }
    
    
    NSString *riskString = @"";
    NSString *riskTotal = [NSString stringWithFormat:@"%1.f", total];
    [_totalLabel setText:riskTotal];
    if(total > 44) {
        riskString = @">>High Risk<<";
        _totalLabel.backgroundColor = [UIColor redColor];
    } else if(total > 23) {
        riskString = @">Caution<";
        _totalLabel.backgroundColor = [UIColor yellowColor];
    } else {
        riskString = @"Low Risk";
        _totalLabel.backgroundColor = [UIColor greenColor];
    }
    
    _totalLabel.textColor = [UIColor blackColor];
    
    NSString *date = [_dateFormatter stringFromDate:[NSDate date]];
    NSString *garString = [NSString stringWithFormat:@"%@%@%@%@%@%@", @"GAR Condition Update: ", riskString, @" [", riskTotal, @"], ", date];
    
    _garOutputLabel.text = garString;
}

- (IBAction)sendGAR:(UIButton *)sender {
    ChatPayload *payload = [ChatPayload new];
    
    double temp =  [[NSDate date] timeIntervalSince1970] * 1000.00;
    
    NSNumber *date = [NSNumber numberWithLongLong:round(temp)];
    
    payload.created = date;
    payload.lastupdated = date;
//    payload.seqTime = date;
    payload.message = _garOutputLabel.text;
    payload.id = @-1;
    payload.incidentId = [_dataManager getActiveIncidentId];
    payload.collabroomid = [_dataManager getSelectedCollabroomId];
    payload.userId = [_dataManager getUserId];
//    payload.topic = [NSString stringWithFormat:@"%@%@%@%@", @"LDDRS.incidents.", [_dataManager getActiveIncidentName], @".collab.", [_dataManager getSelectedCollabroomName]];
    payload.seqnum = @-1;
    payload.nickname = [_dataManager.userData getNickname];
    payload.userOrgName = @"";
    
    [_dataManager addChatMessageToStoreAndForward:payload];
    
    NSString *dateSent = [_dateFormatter stringFromDate:[NSDate date]];
    _garStatusLabel.text = [NSString stringWithFormat:@"%@%@", @"GAR Sent: ", dateSent];
}

- (UILabel *)getActiveLabelForSlider:(UISlider *)slider {
    UILabel *activeLabel = nil;
    
    switch (slider.tag) {
        case 10:
            activeLabel = _supervisionLabel;
            break;
        case 20:
            activeLabel = _planningLabel;
            break;
        case 30:
            activeLabel = _crewSelectionLabel;
            break;
        case 40:
            activeLabel = _crewFitnessLabel;
            break;
        case 50:
            activeLabel = _environmentLabel;
            break;
        case 60:
            activeLabel = _eventComplexityLabel;
            break;
        default:
            break;
    }
    return activeLabel;
}



@end

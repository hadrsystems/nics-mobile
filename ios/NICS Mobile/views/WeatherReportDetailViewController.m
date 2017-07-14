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

#import "WeatherReportDetailViewController.h"
#import "IncidentButtonBar.h"

@interface WeatherReportDetailViewController ()
@property (strong, nonatomic) UIPopoverController *masterPopoverController;
@end

@implementation WeatherReportDetailViewController

#pragma mark - Managing the detail item

- (void)setPayload:(WeatherReportPayload *)payload
{
   if (_payload != payload) {
        _payload = payload;
    }
    if(_payload == nil){
        _payload = [[WeatherReportPayload alloc]init];
    }
    
    if (self.masterPopoverController != nil) {
        [self.masterPopoverController dismissPopoverAnimated:YES];
    }        
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardWillHideNotification object:nil];
    
    _dataManager = [DataManager getInstance];
    
    if([_dataManager getIsIpad])
    {
        self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, [IncidentButtonBar GetIncidentCanvas].frame.size.width, self.view.frame.size.height);
    }else{
        self.navigationItem.hidesBackButton = YES;
        UIBarButtonItem *backBtn =[[UIBarButtonItem alloc]initWithTitle:@"Back" style:UIBarButtonSystemItemCancel target:self action:@selector(handleBack:)];
        self.navigationItem.leftBarButtonItem=backBtn;
    }
    
    [self configureView];
}

- (void)configureView{
 
    [_formView updateDataWithDictionary:[_payload.messageData toDictionary] readOnly:_hideEditControls];
    
    if(_payload.id == nil){    //if pulled from NICS
        [_formView setAsDraft:true];
        _payload.isDraft = [NSNumber numberWithInt:1];
    }else if(_payload.isDraft == nil){        //if new report
        [_formView setAsDraft:false];
        _payload.isDraft = [NSNumber numberWithInt:0];
    }else{
        [_formView setAsDraft:_payload.isDraft];
    }
    
    if(_hideEditControls) {
        [_buttonView setHidden:YES];
        [_bottomConstraint setConstant:10];
    }
    
    if(_payload.isDraft == [NSNumber numberWithInt:1]) {
        _payload.messageData.latitude = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.latitude];
        _payload.messageData.longitude = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.longitude];
    }
}

- (void)keyboardWasShown:(NSNotification*)notification {

    NSDictionary* info = [notification userInfo];
    CGSize kbSize = [[info objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue].size;
    
    CGRect aRect = self.view.frame;
    aRect.origin.y = 0;
    
    CGPoint originToView = [self.view convertPoint:_formView.focusedTextView.frame.origin fromView:_formView.focusedTextView];
    
    UIEdgeInsets contentInsets;
    if (_formView.focusedTextView != nil && !CGRectContainsPoint(aRect, _formView.focusedTextView.frame.origin) ) {
        contentInsets = UIEdgeInsetsMake(-kbSize.height + _formView.focusedTextView.frame.origin.y, 0.0, 0.0, 0.0);
    } else {
        contentInsets = UIEdgeInsetsMake(-originToView.y + _formView.focusedTextView.frame.size.height + 35, 0.0, 0.0, 0.0);
    }
    
    _formView.contentInset = contentInsets;
    _formView.scrollIndicatorInsets = contentInsets;
}

- (void)keyboardWillBeHidden:(NSNotification*)notification {

        UIEdgeInsets contentInsets = UIEdgeInsetsZero;
        _formView.contentInset = contentInsets;
        _formView.scrollIndicatorInsets = contentInsets;
}

- (IBAction)submitReportButtonPressed:(UIButton *)button {
    [_dataManager deleteWeatherReportFromStoreAndForward:_payload];
    _payload = [self getPayload:NO];
    [_dataManager addWeatherReportToStoreAndForward:_payload];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)submitTabletReportButtonPressed {
    [_dataManager deleteWeatherReportFromStoreAndForward:_payload];
    _payload = [self getPayload:NO];
    [_dataManager addWeatherReportToStoreAndForward:_payload];
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToWeatherReportFromButtonBar];
}

- (IBAction)saveDraftButtonPressed:(UIButton *)button {
    [_dataManager deleteWeatherReportFromStoreAndForward:_payload];
    _payload = [self getPayload:YES];
    [_dataManager addWeatherReportToStoreAndForward:_payload];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)saveTabletDraftButtonPressed {
    [_dataManager deleteWeatherReportFromStoreAndForward:_payload];
    _payload = [self getPayload:YES];
    [_dataManager addWeatherReportToStoreAndForward:_payload];
    
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToWeatherReportFromButtonBar];
}

- (IBAction)cancelButtonPressed:(UIButton *)button {
    if(self.hideEditControls == false){
        [self showCancelAlertView];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)cancelTabletButtonPressed {
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToWeatherReportFromButtonBar];
}

- (WeatherReportPayload *)getPayload:(BOOL)isDraft {
    NSMutableDictionary *dataDictionary = [_formView save];
    
    [dataDictionary setObject: [dataDictionary objectForKey:@"latitude"]forKey:@"wr-latitude"];
    [dataDictionary setObject:[dataDictionary objectForKey:@"longitude"]forKey:@"wr-longitude"];
    [dataDictionary removeObjectForKey:@"latitude"];
    [dataDictionary removeObjectForKey:@"longitude"];
    [dataDictionary setObject:[_dataManager getUsername] forKey:@"user"];
    [dataDictionary setObject:@"Open" forKey:@"status"];
    
    NSError *e = nil;
    WeatherReportData* data = [[WeatherReportData alloc] initWithDictionary: dataDictionary error:&e];
    
    WeatherReportPayload *payload = [WeatherReportPayload new];
    
    if(isDraft) {
        payload.isDraft = [NSNumber numberWithInt:1];
    } else {
        payload.isDraft = [NSNumber numberWithInt:0];
    }
    
    payload.incidentid = [_dataManager getActiveIncidentId];
    payload.incidentname = [_dataManager getActiveIncidentName];
    payload.formtypeid = [NSNumber numberWithInt:WR];
    payload.usersessionid = [_dataManager getUserSessionId];
    payload.messageData = data;
    payload.message = [data toJSONString];
    
    double temp =  [[NSDate date] timeIntervalSince1970] * 1000.00;
    
    NSNumber *date = [NSNumber numberWithLongLong:round(temp)];
    payload.seqtime = date;
    payload.status = [NSNumber numberWithInt:WAITING_TO_SEND];
    
    return payload;
}

-(void) showCancelAlertView{
    
    UIAlertController * alert= [UIAlertController alertControllerWithTitle:@"Cancel Report" message:@"Your report progress will be lost if you leave the report." preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* saveAndCloseButton = [UIAlertAction actionWithTitle:@"Save Draft And Close" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action){
        
        if([_dataManager isIpad]){
            [self saveTabletDraftButtonPressed];
        }else{
            [self saveDraftButtonPressed:nil];
        }
    }];
    
    UIAlertAction* closeButton = [UIAlertAction actionWithTitle:@"Don't Save And Close" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action){
        
        if([_dataManager isIpad]){
            [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToWeatherReportFromButtonBar];
        }else{
            [self.navigationController popViewControllerAnimated:YES];
        }
    }];
    
    UIAlertAction* continueButton = [UIAlertAction actionWithTitle:@"Continue Editing" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action){
        
    }];
    
    [alert addAction:saveAndCloseButton];
    [alert addAction:closeButton];
    [alert addAction:continueButton];
    
    [self presentViewController:alert animated:YES completion:nil];
}

- (void) handleBack:(id)sender
{
    if(self.hideEditControls == false){
        [self showCancelAlertView];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

@end

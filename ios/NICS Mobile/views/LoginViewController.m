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
//  ViewController.m
//
//

#import "LoginViewController.h"
#import "RestClient.h"
#import "FormDamageInformation.h"
#import "IncidentButtonBar.h"

@interface LoginViewController ()

@end

@implementation LoginViewController

static DataManager *dataManager;
bool useOpenAM = false;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.hidesBackButton = YES;
    
    dataManager = [DataManager getInstance];
    
    if(![[self.storyboard valueForKey:@"name"]  isEqual: @"Main_iPhone"])
    {
        [dataManager setIsIpad:TRUE];
    }else{
        [dataManager setIsIpad:FALSE];
    }
    
    if (SYSTEM_VERSION_LESS_THAN(@"8.0")) {
        [_SettingsButton setHidden:TRUE];
    }
    
    _orgSelectMenu = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"Select Incident",nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
}

-(void)viewWillAppear:(BOOL)animated {
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshView) name:@"DidBecomeActive" object:nil];

    if([dataManager getIsIpad]){
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardWillHideNotification object:nil];
        [IncidentButtonBar SetLoginView:self];
    }
    
    self.currentServerLabel.text = [dataManager getServerFromSettings];
    self.VersionLabel.text = [@"Version: " stringByAppendingString:[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]];
    
    [_loadingView setHidden:true];
    [_loginContentView setHidden:false];
    [_settingsView setHidden:false];
    [_activityIndicatorView stopAnimating];
    
    [_autoLoginSwitch setOn:[dataManager getAutoLogin]];
    [_rememberUsernameSwitch setOn:[dataManager getRememberUser]];
    
    if([dataManager getActiveWorkspaceId] == [NSNumber numberWithInteger:2]){
        [_trainingSwitch setOn:true];
    }else{
        [_trainingSwitch setOn:false];
    }
    
    if([dataManager getRememberUser]) {
        _usernameField.text = [dataManager getUsername];
    }
    
    if([dataManager getAutoLogin]) {
        _passwordField.text = [dataManager getPassword];
        [self attemptLogin];
    }
    
    [_loginButton addTarget:self action:@selector(attemptLogin) forControlEvents:UIControlEventTouchUpInside];
    [_okButton addTarget:self action:@selector(submitSelection) forControlEvents:UIControlEventTouchUpInside];
    
    _originalFrame = self.view.frame;
}


-(void) attemptLogin {
    
    if([_usernameField.text length] == 0 || [_passwordField.text length] == 0) {
        return;
    }
    
    _usernameField.text = [_usernameField.text lowercaseString];
    
    [_loadingView setHidden:false];
    [_loginContentView setHidden:true];
    [_settingsView setHidden:TRUE];
    [_activityIndicatorView startAnimating];
    
    [dataManager setAutoLogin:_autoLoginSwitch.isOn];
    [dataManager setRememberUser:_rememberUsernameSwitch.isOn];
    
    if(_trainingSwitch.isOn){
        [dataManager setActiveWorkspaceId:[NSNumber numberWithInteger:2]];
    }else{
        [dataManager setActiveWorkspaceId:[NSNumber numberWithInteger:1]];
    }
    
    if(_rememberUsernameSwitch.isOn) {
        [dataManager setUserName:_usernameField.text];
    }
    
    [_loadingLabel setText:NSLocalizedString(@"Logging in...", nil)];

    [self login];
}


-(void)login{
    [RestClient loginUser:_usernameField.text password:_passwordField.text completion:^(BOOL successful, NSString* msg) {
        if(successful) {
            
            if(_autoLoginSwitch.isOn){
                [dataManager setPassword:_passwordField.text];
            }
            
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [_loadingLabel setText:NSLocalizedString(@"Loading Incidents and Collaboration Rooms...",nil)];
            }];
            
//            OrganizationPayload* orgPayload = dataManager.orgData;
//            
//            for(int i = 0; i < orgPayload.userorgs.count; i++){
//                
//                [_orgSelectMenu addButtonWithTitle:orgPayload.name];
//            }
//            [_orgSelectMenu showInView:self.view];
            
            //move this block into actionSheer response when multiple orgs is implemented.
            sleep(2);
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [[NSNotificationCenter defaultCenter] removeObserver: self];
                [self performSegueWithIdentifier:@"successLoginSegue" sender:self];
            }];

        } else {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                [_activityIndicatorView stopAnimating];
                [_loadingView setHidden:true];
                [_loginContentView setHidden:false];
                [_settingsView setHidden:false];
                
                NSError *jsonError;
                NSData *objectData = [msg dataUsingEncoding:NSUTF8StringEncoding];
                NSDictionary *json = [NSJSONSerialization JSONObjectWithData:objectData
                                                                     options:NSJSONReadingMutableContainers
                                                                       error:&jsonError];
                
                UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Failed to login.",nil) message:[json objectForKey:@"message"] delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"OK", nil];
                [alertView show];
                
                [dataManager setPassword:@""];
            }];
        }
    }];
}

- (void)submitSelection
{
    if(_selectedCollabroomList != nil) {
        [dataManager clearCollabRoomList];
        
        for(CollabroomPayload *payload in _selectedCollabroomList) {
            [dataManager addCollabroom:payload];
        }
    }
    
    if(_selectedIncident != nil) {
        [dataManager setActiveIncident:_selectedIncident];
    }
    
    if(_selectedCollabroom != nil) {
        [dataManager setSelectedCollabRoomId:_selectedCollabroom.collabRoomId collabRoomName:_selectedCollabroom.name];
    }
}

- (void)refreshView{
    self.currentServerLabel.text = [dataManager getServerFromSettings];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

#pragma mark - Keyboard events

- (void)keyboardWillShow:(NSNotification*)aNotification
{
    [Utils AdjustViewForKeyboard:self.view :TRUE:_originalFrame :aNotification];
}

- (void)keyboardWillBeHidden:(NSNotification*)aNotification
{
    [Utils AdjustViewForKeyboard:self.view :FALSE : _originalFrame: aNotification];
}

- (IBAction)SettingsButtonPressed:(id)sender {
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
}
@end

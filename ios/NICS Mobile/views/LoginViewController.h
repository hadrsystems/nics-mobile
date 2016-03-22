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
/**
 *
 */
//
//  LoginViewController.h
//  nics_iOS
//
//

#import <UIKit/UIKit.h>
#import "FormSpinner.h"
#import "IncidentPayload.h"
#import "CollabroomPayload.h"

@interface LoginViewController : UIViewController <UITextFieldDelegate, UIActionSheetDelegate>

@property CGRect originalFrame;

@property IBOutlet UIButton *loginButton;
@property IBOutlet UIButton *okButton;
- (IBAction)SettingsButtonPressed:(id)sender;
@property (weak, nonatomic) IBOutlet UIButton *SettingsButton;

@property IBOutlet UITextField *usernameField;
@property IBOutlet UITextField *passwordField;

@property IBOutlet UISwitch *rememberUsernameSwitch;
@property IBOutlet UISwitch *autoLoginSwitch;
@property (weak, nonatomic) IBOutlet UISwitch *trainingSwitch;
@property (weak, nonatomic) IBOutlet UILabel *currentServerLabel;
@property (weak, nonatomic) IBOutlet UILabel *VersionLabel;

@property IBOutlet UIActivityIndicatorView *activityIndicatorView;

@property IBOutlet UILabel *loadingLabel;
@property IBOutlet UILabel *assignmentLabel;
@property IBOutlet UIView *loadingView;
@property IBOutlet UIView *loginContentView;
@property (weak, nonatomic) IBOutlet UIView *settingsView;

@property IBOutlet UIView *selectCollabroomView;

@property IBOutlet FormSpinner *incidentSpinnerView;
@property IBOutlet FormSpinner *collabroomSpinnerView;

@property IncidentPayload *selectedIncident;
@property NSMutableArray<CollabroomPayload> *selectedCollabroomList;
@property CollabroomPayload *selectedCollabroom;

@property UIActionSheet *orgSelectMenu;

- (void) refreshView;

@end


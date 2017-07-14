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

#import "UserInfoViewController.h"

@interface UserInfoViewController ()
@property (strong, nonatomic) UIPopoverController *masterPopoverController;
- (void)configureView;
@end

@implementation UserInfoViewController

#pragma mark - Managing the detail item

static DataManager *dataManager;

-(void)viewWillAppear:(BOOL)animated {
    [self configureView];
}

- (void)configureView
{
    
    
    [_UserNameLabel setText: [NSLocalizedString(@"User Name", nil) stringByAppendingString:[@": " stringByAppendingString:[dataManager getUsername]]]];

    if( [dataManager getActiveWorkspaceId] == [NSNumber numberWithInteger:2]){
        [_WorkspaceLabel setText:NSLocalizedString(@"Training",nil)];
    }else{
        [_WorkspaceLabel setText:NSLocalizedString(@"Incident",nil)];
    }
    
    _VersionNumberLabel.text =[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"];
    
    [_ServerNameLabel setText: [dataManager getServerFromSettings]];
    _confirmDeleteActivated = false;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
     dataManager = [DataManager getInstance];
    
    if (SYSTEM_VERSION_LESS_THAN(@"8.0")) {
        [_SettingsButton setHidden:TRUE];
    }
    
    // Set the side bar button action. When it's tapped, it'll show up the sidebar.

    // Set the gesture
 //   [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
    
	// Do any additional setup after loading the view, typically from a nib.
}

-(void)viewDidAppear:(BOOL)animated{

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)LogoutButtonPressed:(id)sender {
    if([dataManager getIsIpad]){
        [self dismissViewControllerAnimated:NO completion:nil];
        [[IncidentButtonBar GetOverview] navigateBackToLoginScreen];
        [IncidentButtonBar ClearAllViews];
    }else{
        UIViewController *myController = [self.storyboard instantiateViewControllerWithIdentifier:@"HomeNavigationController"];
        [self presentViewController:myController animated:YES completion:nil];
    }
    
    [dataManager setAutoLogin:FALSE];
    [RestClient logoutUser:[dataManager getUsername]];
    [dataManager disableAllPollingTimers];
}

- (IBAction)SettingsButtonPressed:(id)sender {
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
}

- (IBAction)ClearLocalDataButtonPressed:(id)sender {
    
    if(_confirmDeleteActivated == false){
        [_ClearLocalDataButton setTitle:NSLocalizedString(@"Confirm Data Delete",nil) forState:UIControlStateNormal];
        [_ClearLocalDataButton setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
        _confirmDeleteActivated = true;
    }else{
        if([dataManager getIsIpad]){
            [self dismissViewControllerAnimated:NO completion:nil];
            [[IncidentButtonBar GetOverview] navigateBackToLoginScreen];
            [IncidentButtonBar ClearAllViews];
        }else{
            UIViewController *myController = [self.storyboard instantiateViewControllerWithIdentifier:@"HomeNavigationController"];
            [self presentViewController:myController animated:YES completion:nil];
        }
        [dataManager setAutoLogin:FALSE];
        [RestClient logoutUser:[dataManager getUsername]];
        [dataManager disableAllPollingTimers];
        [dataManager ResetLocalUserData];
    }
}
@end

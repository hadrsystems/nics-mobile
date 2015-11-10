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
//  IncidentCanvasUIViewController.m
//  NICS Mobile
//
//

#import "IncidentCanvasUIViewController.h"
#import "SimpleReportListViewController.h"
#import "SimpleReportDetailViewController.h"
#import "DamageReportListViewController.h"
#import "DamageReportDetailViewController.h"
#import "IncidentButtonBar.h"

@interface IncidentCanvasUIViewController ()

@end

@implementation IncidentCanvasUIViewController

UIView *selectedIncidentController = nil;

UIStoryboard *currentStoryboard;



- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
     currentStoryboard = [UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil];

    _currentReport = nil;
    
    _ReportsMenu = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"Select Report Type",nil) delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
    
    [_ReportsMenu addButtonWithTitle:NSLocalizedString(@"Damage Report",nil)];
//    [_ReportsMenu addButtonWithTitle:NSLocalizedString(@"Resource Request",nil)];
//    [_ReportsMenu addButtonWithTitle:NSLocalizedString(@"Field Report",nil)];
    [_ReportsMenu addButtonWithTitle:NSLocalizedString(@"Explosives Report",nil)];
    [_ReportsMenu addButtonWithTitle:NSLocalizedString(@"Cancel",nil)];
    
    [IncidentButtonBar SetIncidentCanvasController:self];
    [IncidentButtonBar SetIncidentCanvas:_IncidentCanvas];
    
    [IncidentButtonBar SetAddButton:_AddButton];
    [IncidentButtonBar SetSaveDraftButton:_SaveDraftButton];
    [IncidentButtonBar SetCancelButton:_CancelButton];
    [IncidentButtonBar SetSubmitButton:_SubmitButton];
    
    [[IncidentButtonBar GetAddButton] setHidden:TRUE];
    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton]setHidden:TRUE];
    [[IncidentButtonBar GetSubmitButton]setHidden:TRUE];
    
//    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(SetCanvasToGeneralMessage:) name:@"IncidentSwitched" object:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {

    enum ReportTypesMenu reportType = buttonIndex;
    
    switch (reportType) {
        case DamageReport:
            [self SetCanvasToDamageReport];
            break;
//        case ResourceRequest:
//            [self SetCanvasToResourceRequest];
//            break;
//        case FieldReport:
//            [self SetCanvasToFieldReport];
//            break;
        case UxoReport:
            [self SetCanvasToUxoReport];
            break;
        case Cancel:
            
            break;
            
        default:
            break;
    }
    
}

- (IBAction)ChatButtonPressed:(id)sender {
/*
 if(chatController == nil)
 {
 chatController = [currentStoryboard instantiateViewControllerWithIdentifier:@"ChatViewID"];
 }
 [self SetCanvas :chatController.view];
 [chatController viewDidAppear:YES];
*/
    
    if([IncidentButtonBar GetChatController] == nil){
        [IncidentButtonBar SetChatController:[currentStoryboard instantiateViewControllerWithIdentifier:@"ChatViewID"]];
    }else{
        [[IncidentButtonBar GetChatController] viewDidAppear:TRUE];
    }
    _currentReport = @"Chat";
    [self SetCanvas:[IncidentButtonBar GetChatController].view];
    
    [[IncidentButtonBar GetAddButton] setHidden:TRUE];
    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton]setHidden:TRUE];
    [[IncidentButtonBar GetSubmitButton]setHidden:TRUE];
    
    
}

- (IBAction)SetCanvasToGeneralMessage:(id)sender {
    if([IncidentButtonBar GetGeneralMessageListview] == nil){
        [IncidentButtonBar SetGeneralMessageListview:[currentStoryboard instantiateViewControllerWithIdentifier:@"GeneralMessageViewID"]];
    }else{
        [[IncidentButtonBar GetGeneralMessageListview] viewDidAppear:TRUE];
    }
    _currentReport = @"GeneralMessage";
    [self SetCanvas:[IncidentButtonBar GetGeneralMessageListview].view];
    
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton]setHidden:TRUE];
    [[IncidentButtonBar GetSubmitButton]setHidden:TRUE];
}

//Can't get ButtonBar to access the above function
- (void)SetCanvasToGeneralMessageFromButtonBar{
    [[IncidentButtonBar GetGeneralMessageListview] viewDidAppear:TRUE];
    _currentReport = @"GeneralMessage";
    [self SetCanvas:[IncidentButtonBar GetGeneralMessageListview].view];
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
}


- (void)SetCanvasToDamageReport {
    if([IncidentButtonBar GetDamageReportListview] == nil){
        [IncidentButtonBar SetDamageReportListview:[currentStoryboard instantiateViewControllerWithIdentifier:@"DamageReportViewID"]];
    }else{
        [[IncidentButtonBar GetDamageReportListview] viewDidAppear:TRUE];
    }
    _currentReport = @"DamageReport";
    [self SetCanvas:[IncidentButtonBar GetDamageReportListview].view];
    
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton]setHidden:TRUE];
    [[IncidentButtonBar GetSubmitButton]setHidden:TRUE];
}

- (void)SetCanvasToDamageReportFromButtonBar {
    [[IncidentButtonBar GetDamageReportListview] viewDidAppear:TRUE];
    _currentReport = @"DamageReport";
    [self SetCanvas:[IncidentButtonBar GetDamageReportListview].view];
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
}

- (void)SetCanvasToResourceRequest {
    if([IncidentButtonBar GetResourceRequestListview] == nil){
        [IncidentButtonBar SetResourceRequestListview:[currentStoryboard instantiateViewControllerWithIdentifier:@"ResourceRequestViewID"]];
    }else{
        [[IncidentButtonBar GetResourceRequestListview] viewDidAppear:TRUE];
    }
    _currentReport = @"ResourceRequest";
    [self SetCanvas:[IncidentButtonBar GetResourceRequestListview].view];
    
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton]setHidden:TRUE];
    [[IncidentButtonBar GetSubmitButton]setHidden:TRUE];
}

- (void)SetCanvasToResourceRequestFromButtonBar {
    [[IncidentButtonBar GetResourceRequestListview] viewDidAppear:TRUE];
    _currentReport = @"ResourceRequest";
    [self SetCanvas:[IncidentButtonBar GetResourceRequestListview].view];
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
}



- (void)SetCanvasToFieldReport {
    if([IncidentButtonBar GetFieldReportListview] == nil){
        [IncidentButtonBar SetFieldReportListview:[currentStoryboard instantiateViewControllerWithIdentifier:@"FieldReportViewID"]];
    }else{
        [[IncidentButtonBar GetFieldReportListview] viewDidAppear:TRUE];
    }
    _currentReport = @"FieldReport";
    [self SetCanvas:[IncidentButtonBar GetFieldReportListview].view];
    
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton]setHidden:TRUE];
    [[IncidentButtonBar GetSubmitButton]setHidden:TRUE];
}

- (void)SetCanvasToFieldReportFromButtonBar {
    [[IncidentButtonBar GetFieldReportListview] viewDidAppear:TRUE];
    _currentReport = @"FieldReport";
    [self SetCanvas:[IncidentButtonBar GetFieldReportListview].view];
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
}

- (void)SetCanvasToUxoReport {
    if([IncidentButtonBar GetUxoReportListview] == nil){
        [IncidentButtonBar SetUxoReportListview:[currentStoryboard instantiateViewControllerWithIdentifier:@"UxoReportViewID"]];
    }else{
        [[IncidentButtonBar GetUxoReportListview] viewDidAppear:TRUE];
    }
    _currentReport = @"UxoReport";
    [self SetCanvas:[IncidentButtonBar GetUxoReportListview].view];
    
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton]setHidden:TRUE];
    [[IncidentButtonBar GetSubmitButton]setHidden:TRUE];
}

- (void)SetCanvasToUxoReportFromButtonBar {
    [[IncidentButtonBar GetUxoReportListview] viewDidAppear:TRUE];
    _currentReport = @"UxoReport";
    [self SetCanvas:[IncidentButtonBar GetUxoReportListview].view];
    [[IncidentButtonBar GetAddButton] setHidden:FALSE];
}

- (void) SetCanvas:(UIView*)newController{
    selectedIncidentController = newController;
    [[IncidentButtonBar GetIncidentCanvas] addSubview:selectedIncidentController ];
}

- (IBAction)ReportsButtonPressed:(id)sender {
    [_ReportsMenu showInView:self.parentViewController.view];
}

- (IBAction)AddButtonPressed:(id)sender {
    [IncidentButtonBar AddButtonPressed:_currentReport];
}

- (IBAction)SaveDraftButtonPressed:(id)sender {
    [IncidentButtonBar SaveDraftButtonPressed:_currentReport];
}

- (IBAction)CancelButtonPressed:(id)sender {
    [IncidentButtonBar CancelButtonPressed:_currentReport];
}

- (IBAction)SubmitButtonPressed:(id)sender {
    [IncidentButtonBar SubmitButtonPressed:_currentReport];
}
    /*
     #pragma mark - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
     // Get the new view controller using [segue destinationViewController].
     // Pass the selected object to the new view controller.
     }
     */

@end

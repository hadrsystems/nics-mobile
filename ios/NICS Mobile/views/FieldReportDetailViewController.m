/*|~^~|(c) Copyright, 2008-2015 Massachusetts Institute of Technology.
 |~^~|
 |~^~|This material may be reproduced by or for the
 |~^~|U.S. Government pursuant to the copyright license
 |~^~|under the clause at DFARS 252.227-7013 (June, 1995).
 |~^~|*/
//
//  DetailViewController.m
//  nics
//
//

#import "FieldReportDetailViewController.h"
#import "IncidentButtonBar.h"

@interface FieldReportDetailViewController ()
@property (strong, nonatomic) UIPopoverController *masterPopoverController;
@end

@implementation FieldReportDetailViewController

#pragma mark - Managing the detail item

- (void)setPayload:(FieldReportPayload *)payload
{
   if (_payload != payload) {
        _payload = payload;
    }
    if(_payload == nil){
        _payload = [[FieldReportPayload alloc]init];
    }
    
    if (self.masterPopoverController != nil) {
        [self.masterPopoverController dismissPopoverAnimated:YES];
    }else{
        self.navigationItem.hidesBackButton = YES;
        UIBarButtonItem *backBtn =[[UIBarButtonItem alloc]initWithTitle:@"Back" style:UIBarButtonSystemItemCancel target:self action:@selector(handleBack:)];
        self.navigationItem.leftBarButtonItem=backBtn;
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
        self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, [IncidentButtonBar GetIncidentCanvas].frame.size.width, self.view.frame.size.height - 200);
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
        _payload.messageData.frAid = [[_dataManager getActiveIncidentId] stringValue];
        _payload.messageData.frAname = [_dataManager getActiveIncidentName];
        
        FormEditText* editText = [_formView.viewMap objectForKey:@"fr-A-id"];
        
        [editText getTextView].text = _payload.messageData.frAid;
        [[editText getTextView] setEditable:false];
        
        editText  = [_formView.viewMap objectForKey:@"fr-A-name"];
        [editText getTextView].text = _payload.messageData.frAname;
        [[editText getTextView]setEditable:false];
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
    [_dataManager deleteFieldReportFromStoreAndForward:_payload];
    _payload = [self getPayload:NO];
    [_dataManager addFieldReportToStoreAndForward:_payload];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)submitTabletReportButtonPressed {
    [_dataManager deleteFieldReportFromStoreAndForward:_payload];
    _payload = [self getPayload:NO];
    [_dataManager addFieldReportToStoreAndForward:_payload];
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToFieldReportFromButtonBar];
}

- (IBAction)saveDraftButtonPressed:(UIButton *)button {
    [_dataManager deleteFieldReportFromStoreAndForward:_payload];
    _payload = [self getPayload:YES];
    [_dataManager addFieldReportToStoreAndForward:_payload];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)saveTabletDraftButtonPressed {
    [_dataManager deleteFieldReportFromStoreAndForward:_payload];
    _payload = [self getPayload:YES];
    [_dataManager addFieldReportToStoreAndForward:_payload];
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToFieldReportFromButtonBar];
}

- (IBAction)cancelButtonPressed:(UIButton *)button {
    if(self.hideEditControls == false){
        [self showCancelAlertView];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)cancelTabletButtonPressed {
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToFieldReportFromButtonBar];
}

- (FieldReportPayload *)getPayload:(BOOL)isDraft {
    NSMutableDictionary *dataDictionary = [_formView save];
    [dataDictionary setObject:[_dataManager getUsername] forKey:@"user"];
    
    NSError *e = nil;
    FieldReportData* data = [[FieldReportData alloc] initWithDictionary: dataDictionary error:&e];
    
    FieldReportPayload *payload = [FieldReportPayload new];
    
    if(isDraft) {
        payload.isDraft = [NSNumber numberWithInt:1];
    } else {
        payload.isDraft = [NSNumber numberWithInt:0];
    }
    payload.incidentid = [_dataManager getActiveIncidentId];
    payload.incidentname = [_dataManager getActiveIncidentName];
    payload.formtypeid = [NSNumber numberWithInt:FR];
//    payload.senderUserId = [_dataManager getUserId];
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
            [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToFieldReportFromButtonBar];
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

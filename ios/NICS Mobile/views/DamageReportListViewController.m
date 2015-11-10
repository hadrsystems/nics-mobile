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
#import "DamageReportListViewController.h"
#import "SWRevealViewController.h"
#import "IncidentButtonBar.h"

@interface DamageReportListViewController () {
    
}
@end

@implementation DamageReportListViewController

UIStoryboard *currentStoryboard;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    _dataManager = [DataManager getInstance];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewDidAppear:) name:@"DamageReportsUpdateReceived" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewDidAppear:) name:@"IncidentSwitched" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateProgress:) name:[[Enums formTypeEnumToStringAbbrev:DR] stringByAppendingString:@"ReportProgressUpdateReceived"] object:nil];
    [_dataManager requestDamageReportsRepeatedEvery:[DataManager getReportsUpdateFrequencyFromSettings] immediate:NO];
    
    if([_dataManager getIsIpad] == true){
        currentStoryboard = [UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil];
    }
    
    if([_dataManager isIpad]){  //hacky method to get tableview displayed properly
        CGRect thisFrame = self.view.frame;
        thisFrame.size.width = 512;
        self.view.frame = thisFrame;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewDidAppear:(BOOL)animated
{
    _reports = [_dataManager getAllDamageReportsForIncidentId:[_dataManager getActiveIncidentId]];
    
    if(_reports.count <= 0)
    {
        DamageReportPayload* emptyListPayload = [[DamageReportPayload alloc]init];
        [_reports addObject:emptyListPayload];
        
        _emptyList = TRUE;
    }else{
        _emptyList = NO;
    }
    
    [[self tableView] reloadData];
}

- (void) updateProgress:(NSNotification *) notification {
    NSDictionary *userInfo = notification.userInfo;
    NSNumber *updatedReportId = [userInfo objectForKey:@"id"];
    
    for(DamageReportPayload *payload in _reports) {
        if(payload.id == updatedReportId) {
            payload.progress = [userInfo objectForKey:@"progress"];
            break;
        }
    }
    
    [[self tableView] reloadData];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    
    return view;
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _reports.count;
}

-(UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    DamageReportPayload* payload = _reports[indexPath.row];
    
    DamageReportData* data = payload.messageData;
    
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:[payload.seqtime longLongValue]/1000.0];
    
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    cell.backgroundColor = [UIColor clearColor];
    
    UILabel *label = (UILabel *)[cell.contentView viewWithTag:10];
    
    
    if(_emptyList){
        UILabel *noMessagesLabel =(UILabel *)[cell.contentView viewWithTag:40];
        [noMessagesLabel setHidden:false];
        UILabel *timeLabel =(UILabel *)[cell.contentView viewWithTag:20];
        [timeLabel setHidden:TRUE];
//        UIImageView *image = (UIImageView *)[cell.contentView viewWithTag:30];
//        [image setHidden:TRUE];
        UILabel *nameLabel =(UILabel *)[cell.contentView viewWithTag:10];
        [nameLabel setHidden:TRUE];
//        UILabel *recipientAbreviation= (UILabel *)[cell.contentView viewWithTag:50];
//        [recipientAbreviation setHidden:TRUE];
        return cell;
    }
    
    
    if([payload.isDraft isEqual: @1]) {
        label.text = [@"<" stringByAppendingFormat:@"%@%@%@",NSLocalizedString(@"Draft", nil),@">",data.user];
    } else if([payload.status isEqual:[NSNumber numberWithInt:WAITING_TO_SEND]]) {
        if(payload.progress == 0) {
            label.text = [@"<" stringByAppendingFormat:@"%@%@%@",NSLocalizedString(@"Sending", nil),@">",data.user];
        } else {
            label.text = [@"<" stringByAppendingFormat:@"%@%.2f%@%@",NSLocalizedString(@"Sending", nil), [payload.progress doubleValue],@">",data.user];
        }
    } else {
        label.text = data.user;
    }
    
    UILabel *timeLabel = (UILabel *)[cell.contentView viewWithTag:20];
    timeLabel.text = [[Utils getDateFormatter] stringFromDate:date];
    
    [timeLabel setHidden:FALSE];
    //    [image setHidden:FALSE];
    UILabel *nameLabel =(UILabel *)[cell.contentView viewWithTag:10];
    [nameLabel setHidden:FALSE];
    UILabel *noMessagesLabel =(UILabel *)[cell.contentView viewWithTag:40];
    [noMessagesLabel setHidden:TRUE];
    
    return cell;
}

-(NSIndexPath *)tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if(_emptyList){
        return nil;
    }
    
    UIView *selectedView = [[UIView alloc]init];
    selectedView.backgroundColor = [UIColor colorWithRed:0.1953125 green:0.5 blue:0.609375 alpha:1.0];
    
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    cell.selectedBackgroundView =selectedView;
    
    
    DamageReportPayload* payload = _reports[indexPath.row];
//    DamageReportData* data = payload.messageData;
    
    if([_dataManager getIsIpad] == true){
        [self prepareForTabletCanvasSwap:[payload.isDraft boolValue] :indexPath.row];
    }
    
    return indexPath;
}

- (void)prepareForTabletCanvasSwap:(BOOL)isEdit :(NSInteger)index{

    bool newReport = false;
    if([IncidentButtonBar GetDamageReportDetailView] == nil){
        [IncidentButtonBar SetDamageReportDetailView:[currentStoryboard instantiateViewControllerWithIdentifier:@"DamageReportDetailViewID"]];
        newReport = true;
    }
    
    DamageReportPayload *payload;
    if(_reports.count > 0 && index >= 0) {
        payload = _reports[index];
    } else {
        payload = [[DamageReportPayload alloc]init];
    }
    
    if (isEdit==false) {
        if([payload.isDraft isEqual: @1]) {
            [IncidentButtonBar GetDamageReportDetailView].hideEditControls = NO;
            [[IncidentButtonBar GetSaveDraftButton] setHidden:FALSE];
            [[IncidentButtonBar GetSubmitButton] setHidden:FALSE];
        } else {
            [IncidentButtonBar GetDamageReportDetailView].hideEditControls = YES;
            [[IncidentButtonBar GetSaveDraftButton] setHidden:TRUE];
            [[IncidentButtonBar GetSubmitButton] setHidden:TRUE];
        }
        [IncidentButtonBar GetDamageReportDetailView].payload = payload;
        
    } else if (isEdit==true) {
        [IncidentButtonBar GetDamageReportDetailView].hideEditControls = NO;
        [IncidentButtonBar GetDamageReportDetailView].payload = payload;
        [[IncidentButtonBar GetSaveDraftButton] setHidden:FALSE];
        [[IncidentButtonBar GetSubmitButton] setHidden:FALSE];
        
    } else {
        [IncidentButtonBar GetDamageReportDetailView].payload = payload;
        [[IncidentButtonBar GetSaveDraftButton] setHidden:TRUE];
        [[IncidentButtonBar GetSubmitButton] setHidden:TRUE];
    }
    
    if(newReport == false){
        [IncidentButtonBar GetDamageReportDetailView].payload = payload;
        [[IncidentButtonBar GetDamageReportDetailView] configureView];
    }
    
    [[IncidentButtonBar GetAddButton] setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton] setHidden:FALSE];
    
    [[IncidentButtonBar GetIncidentCanvas] addSubview:[IncidentButtonBar GetDamageReportDetailView].view ];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    DamageReportDetailViewController *detailViewController = [segue destinationViewController];
    
    NSInteger index = [self.tableView indexPathForSelectedRow].row;
    NSString* identifier = [segue identifier];
    
    DamageReportPayload *payload;
    if(_reports.count > 0) {
        payload = _reports[index];
    } else {
        payload = [[DamageReportPayload alloc]init];
    }
    
    if ([identifier isEqualToString:@"showDamageReportDetail"]  && !payload.isDraft) {
        detailViewController.hideEditControls = YES;
        [detailViewController setPayload:payload];
        
    } else if ([identifier isEqualToString:@"showDamageReportDetail"] && payload.isDraft) {
        detailViewController.hideEditControls = NO;
        [detailViewController setPayload:payload];
        
    } else if ([identifier isEqualToString:@"editDamageReportDetail"]) {
        detailViewController.hideEditControls = NO;
        [detailViewController setPayload:[[DamageReportPayload alloc]init]];
    } else {
        detailViewController.hideEditControls = NO;
        detailViewController.payload = [[DamageReportPayload alloc]init];
    }
}

@end

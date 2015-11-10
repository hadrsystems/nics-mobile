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
#import "SimpleReportListViewController.h"
#import "SWRevealViewController.h"
#import "IncidentButtonBar.h"

@interface SimpleReportListViewController () {
    
}
@end

@implementation SimpleReportListViewController

UIStoryboard *currentStoryboard;
NSInteger lastIndexSelected;

- (void)viewDidLoad
{
    [super viewDidLoad];
    [Enums simpleReportCategoriesDictionary];
    
    _dataManager = [DataManager getInstance];
    [_dataManager requestSimpleReportsRepeatedEvery:[DataManager getReportsUpdateFrequencyFromSettings] immediate:NO];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewDidAppear:) name:@"simpleReportsUpdateReceived" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateProgress:) name: [[Enums formTypeEnumToStringAbbrev:SR] stringByAppendingString:@"ReportProgressUpdateReceived"] object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewDidAppear:) name:@"IncidentSwitched" object:nil];
    
    if([_dataManager getIsIpad]  == true){
        currentStoryboard = [UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil];
    }
}

- (void) updateProgress:(NSNotification *) notification {
    NSDictionary *userInfo = notification.userInfo;
    NSNumber *updatedReportId = [userInfo objectForKey:@"id"];
    
    for(SimpleReportPayload *payload in _reports) {
        if(payload.id == updatedReportId) {
            payload.progress = [userInfo objectForKey:@"progress"];
            break;
        }
    }
    
    [[self tableView] reloadData];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewDidAppear:(BOOL)animated
{
    _reports = [_dataManager getAllSimpleReportsForIncidentId:[_dataManager getActiveIncidentId]];
    
    if(_reports.count <= 0)
    {
        
        SimpleReportPayload* emptyListPayload = [[SimpleReportPayload alloc]init];
//        SimpleReportData* data = emptyList.messageData;
//        data.user = @"No General Messages have been posted in this Incident yet";
//        emptyList.messageData = data;
        
        [_reports addObject:emptyListPayload];
        
        _emptyList = TRUE;
    }else{
        _emptyList = NO;
    }
    
    [[self tableView] reloadData];
//    [[IncidentButtonBar GetSaveDraftButton]setHidden:TRUE];
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
    SimpleReportPayload* payload = _reports[indexPath.row];
    
    SimpleReportData* data = payload.messageData;
    
    NSDate* date = [NSDate dateWithTimeIntervalSince1970:[payload.seqtime longLongValue]/1000.0];
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    cell.backgroundColor = [UIColor clearColor];
    
    UILabel *label = (UILabel *)[cell.contentView viewWithTag:10];

    if(_emptyList){
        UILabel *noMessagesLabel =(UILabel *)[cell.contentView viewWithTag:40];
        [noMessagesLabel setHidden:false];
        UILabel *timeLabel =(UILabel *)[cell.contentView viewWithTag:20];
        [timeLabel setHidden:TRUE];
        UIImageView *image = (UIImageView *)[cell.contentView viewWithTag:30];
        [image setHidden:TRUE];
        UILabel *nameLabel =(UILabel *)[cell.contentView viewWithTag:10];
        [nameLabel setHidden:TRUE];
        UILabel *recipientAbreviation= (UILabel *)[cell.contentView viewWithTag:50];
        [recipientAbreviation setHidden:TRUE];
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
    
    UILabel *timeLabel =(UILabel *)[cell.contentView viewWithTag:20];
    timeLabel.text = [[Utils getDateFormatter] stringFromDate:date];
    
//    UIImageView *image = (UIImageView *)[cell.contentView viewWithTag:30];
//    [image setImage:[UIImage imageNamed:[Enums convertSRCategoryTypeToNamedImage:[Enums convertToSRCategoryFromString:data.category]]]];
    
    UILabel *recipientAbreviation= (UILabel *)[cell.contentView viewWithTag:50];
    [recipientAbreviation setHidden:FALSE];
    recipientAbreviation.text = [Enums convertSRCategoryTypeToAbreviation: [Enums convertToSRCategoryFromString: data.category]];
    
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
    cell.selectedBackgroundView = selectedView;
    
    lastIndexSelected = indexPath.row;
    
    if([_dataManager getIsIpad] == true){
        [self prepareForTabletCanvasSwap:FALSE :indexPath.row];
    }
    
    float height = self.view.frame.size.height;
    
    return indexPath;
}

- (void)prepareForTabletCanvasSwap:(BOOL)isEdit :(NSInteger)index{
    
    bool newReport = false;
    if([IncidentButtonBar GetGeneralMessageDetailView] == nil){
        [IncidentButtonBar SetGeneralMessageDetailView:[currentStoryboard instantiateViewControllerWithIdentifier:@"SimpleReportDetailViewID"]];
        newReport = true;
    }
    
    SimpleReportPayload *payload;
    if(_reports.count > 0) {
        payload = _reports[index];
    } else {
        payload = [SimpleReportPayload new];
    }
    
    if (isEdit==false) {
        if([payload.isDraft isEqual: @1]) {
            [IncidentButtonBar GetGeneralMessageDetailView].hideEditControls = NO;
            [[IncidentButtonBar GetSaveDraftButton] setHidden:FALSE];
            [[IncidentButtonBar GetSubmitButton] setHidden:FALSE];
        } else {
            [IncidentButtonBar GetGeneralMessageDetailView].hideEditControls = YES;
            [[IncidentButtonBar GetSaveDraftButton] setHidden:TRUE];
            [[IncidentButtonBar GetSubmitButton] setHidden:TRUE];
        }
        [IncidentButtonBar GetGeneralMessageDetailView].payload = payload;
        
    } else if (isEdit==true) {
        [IncidentButtonBar GetGeneralMessageDetailView].hideEditControls = NO;
        [IncidentButtonBar GetGeneralMessageDetailView].payload = nil;
        [[IncidentButtonBar GetSaveDraftButton] setHidden:FALSE];
        [[IncidentButtonBar GetSubmitButton] setHidden:FALSE];
        
    } else {
        [IncidentButtonBar GetGeneralMessageDetailView].payload = payload;
        [[IncidentButtonBar GetSaveDraftButton] setHidden:TRUE];
        [[IncidentButtonBar GetSubmitButton] setHidden:TRUE];
    }
    
    if(newReport == false){
        [[IncidentButtonBar GetGeneralMessageDetailView] configureView];
    }
    
    [[IncidentButtonBar GetAddButton] setHidden:TRUE];
    [[IncidentButtonBar GetCancelButton] setHidden:FALSE];
    
    
    [[IncidentButtonBar GetIncidentCanvas] addSubview:[IncidentButtonBar GetGeneralMessageDetailView].view ];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 
    SimpleReportDetailViewController *detailViewController = [segue destinationViewController];
    
    NSInteger index = [self.tableView indexPathForSelectedRow].row;

    SimpleReportPayload *payload;
    if(_reports.count > 0) {
        payload = _reports[index];
    } else {
        payload = [SimpleReportPayload new];
    }
    
    if ([[segue identifier] isEqualToString:@"showSimpleReportDetail"]) {
        if([payload.isDraft isEqual: @1]) {
            detailViewController.hideEditControls = NO;
        } else {
            detailViewController.hideEditControls = YES;
        }
        detailViewController.payload = payload;
        
    } else if ([[segue identifier] isEqualToString:@"editSimpleReportDetail"]) {
        detailViewController.hideEditControls = NO;
        detailViewController.payload = nil;
        
    } else {
        detailViewController.payload = payload;
    }
    
}

@end

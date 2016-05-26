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
    [_dataManager requestSimpleReportsRepeatedEvery:[[DataManager getReportsUpdateFrequencyFromSettings] intValue] immediate:YES];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewDidAppear:) name:@"simpleReportsUpdateReceived" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateProgress:) name: [[Enums formTypeEnumToStringAbbrev:SR] stringByAppendingString:@"ReportProgressUpdateReceived"] object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewDidAppear:) name:@"IncidentSwitched" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(GeneralMessagesPolledNothing) name:@"GeneralMessagesPolledNothing" object:nil];
    
    if([_dataManager getIsIpad]  == true){
        currentStoryboard = [UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil];
    }
    
    self.refreshControl = [[UIRefreshControl alloc] init];
    self.refreshControl.backgroundColor = [UIColor blackColor];
    self.refreshControl.tintColor = [UIColor whiteColor];
    [self.refreshControl addTarget:self
                            action:@selector(refreshGeneralMessages)
                  forControlEvents:UIControlEventValueChanged];
    
    NSDictionary *attrsDictionary = [NSDictionary dictionaryWithObject:[UIColor whiteColor]
                                                                forKey:NSForegroundColorAttributeName];
    NSAttributedString *attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Checking for new reports", nil)  attributes:attrsDictionary];
    self.refreshControl.attributedTitle = attributedTitle;
}

-(void)refreshGeneralMessages{
    [_dataManager requestSimpleReportsRepeatedEvery:[[DataManager getReportsUpdateFrequencyFromSettings]intValue] immediate:YES];
}
-(void)GeneralMessagesPolledNothing{
    [self.refreshControl endRefreshing];
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
}

-(void)viewDidAppear:(BOOL)animated
{
    _reports = [_dataManager getAllSimpleReportsForIncidentId:[_dataManager getActiveIncidentId]];
    
    if(_reports.count <= 0)
    {
        SimpleReportPayload* emptyListPayload = [[SimpleReportPayload alloc]init];
        [_reports addObject:emptyListPayload];
        
        _emptyList = TRUE;
    }else{
        _emptyList = NO;
    }
    
    [[self tableView] reloadData];
    [self.refreshControl endRefreshing];
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
    
    ReportCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    cell.backgroundColor = [UIColor clearColor];
    cell.lat = [data.latitude stringValue];
    cell.lon = [data.longitude stringValue];
    cell.parent = self.navigationController;
    
    if(_emptyList){
        [cell.NoReportMessage setHidden:false];
        [cell.TimestampLabel setHidden:TRUE];
        [cell.NameLabel setHidden:TRUE];
        [cell.AbbreviationLabel setHidden:TRUE];
        [cell.MapLocationButton setHidden:TRUE];
        [cell.MapLocationButton setEnabled:FALSE];
        return cell;
    }
    
    if([payload.isDraft isEqual: @1]) {
        cell.NameLabel.text = [@"<" stringByAppendingFormat:@"%@%@%@",NSLocalizedString(@"Draft", nil),@">",data.user];
    } else if([payload.status isEqual:[NSNumber numberWithInt:WAITING_TO_SEND]]) {
        if(payload.progress == 0) {
            cell.NameLabel.text = [@"<" stringByAppendingFormat:@"%@%@%@",NSLocalizedString(@"Sending", nil),@">",data.user];
        }else if([payload.progress doubleValue] >= 100){
            cell.NameLabel.text = data.user;
        } else {
            cell.NameLabel.text = [@"<" stringByAppendingFormat:@"%@%.2f%@%@",NSLocalizedString(@"Sending", nil), [payload.progress doubleValue],@">",data.user];
        }
    } else {
        cell.NameLabel.text = data.user;
    }
    
    cell.TimestampLabel.text = [[Utils getDateFormatter] stringFromDate:date];
    [cell.AbbreviationLabel setHidden:FALSE];
    cell.AbbreviationLabel.text = [Enums convertSRCategoryTypeToAbreviation: [Enums convertToSRCategoryFromString: data.category]];
    
    [cell.TimestampLabel setHidden:FALSE];
    [cell.NameLabel setHidden:FALSE];
    [cell.NoReportMessage setHidden:TRUE];
    [cell.MapLocationButton setHidden:FALSE];
    [cell.MapLocationButton setEnabled:TRUE];
    
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
    
    SimpleReportPayload *payload;
    if(_reports.count > 0 && index >= 0) {
        payload = _reports[index];
    } else if( index == -2){
        payload = [IncidentButtonBar GetGeneralMessageDetailView].payload;
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
        
    } else if (isEdit==true) {
        [IncidentButtonBar GetGeneralMessageDetailView].hideEditControls = NO;
        [IncidentButtonBar GetGeneralMessageDetailView].payload = nil;
        [[IncidentButtonBar GetSaveDraftButton] setHidden:FALSE];
        [[IncidentButtonBar GetSubmitButton] setHidden:FALSE];
        
    } else {
        [[IncidentButtonBar GetSaveDraftButton] setHidden:TRUE];
        [[IncidentButtonBar GetSubmitButton] setHidden:TRUE];
    }
    
    [IncidentButtonBar GetGeneralMessageDetailView].payload = payload;
    [[IncidentButtonBar GetGeneralMessageDetailView] configureView];
    
    [[IncidentButtonBar GetAddButton] setHidden:TRUE];
    [[IncidentButtonBar GetFilterButton] setHidden:TRUE];
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

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    //filtered lists not added to damage reports yet
    //    if(_filteredList && indexPath.row==0){
    //        return 180;
    //    }
    return 50;
}

@end

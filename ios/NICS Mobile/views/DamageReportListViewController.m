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
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(newReportReceived:) name:@"damageReportsUpdateReceived" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(newReportReceived:) name:@"IncidentSwitched" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateProgress:) name:[[Enums formTypeEnumToStringAbbrev:DR] stringByAppendingString:@"ReportProgressUpdateReceived"] object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(DamageReportsPolledNothing) name:@"DamageReportsPolledNothing" object:nil];
    [_dataManager requestDamageReportsRepeatedEvery:[[DataManager getReportsUpdateFrequencyFromSettings] intValue] immediate:YES];

    if([_dataManager getIsIpad]  == true){
        currentStoryboard = [UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil];
    }
    
    self.refreshControl = [[UIRefreshControl alloc] init];
    self.refreshControl.backgroundColor = [UIColor blackColor];
    self.refreshControl.tintColor = [UIColor whiteColor];
    [self.refreshControl addTarget:self
                            action:@selector(refreshDamageReports)
                  forControlEvents:UIControlEventValueChanged];
    
    NSDictionary *attrsDictionary = [NSDictionary dictionaryWithObject:[UIColor whiteColor]
                                                                forKey:NSForegroundColorAttributeName];
    NSAttributedString *attributedTitle = [[NSAttributedString alloc] initWithString:NSLocalizedString(@"Checking for new reports", nil)  attributes:attrsDictionary];
    self.refreshControl.attributedTitle = attributedTitle;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)refreshDamageReports{
    [_dataManager requestDamageReportsRepeatedEvery:[[DataManager getReportsUpdateFrequencyFromSettings]intValue] immediate:YES];
}
-(void)DamageReportsPolledNothing{
    [self.refreshControl endRefreshing];
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
    [self.refreshControl endRefreshing];
}

- (void) newReportReceived:(NSNotification *) notification {
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
    [self.refreshControl endRefreshing];
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
    
    ReportCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    cell.backgroundColor = [UIColor clearColor];
    cell.lat = data.drBpropertyLatitude;
    cell.lon = data.drBpropertyLongitude;
    cell.parent = self.navigationController;
    
    if(_emptyList){
        [cell.NoReportMessage setHidden:false];
        [cell.TimestampLabel setHidden:TRUE];
        [cell.NameLabel setHidden:TRUE];
        [cell.MapLocationButton setHidden:TRUE];
        [cell.MapLocationButton setEnabled:FALSE];
        return cell;
    }
    
    if([payload.isDraft isEqual: @1]) {
        cell.NameLabel.text = [@"<" stringByAppendingFormat:@"%@%@%@",NSLocalizedString(@"Draft", nil),@">",data.user];
    } else if([payload.status isEqual:[NSNumber numberWithInt:WAITING_TO_SEND]] && payload.progress < [NSNumber numberWithInt: 100]) {
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
    cell.selectedBackgroundView =selectedView;
    
    
    DamageReportPayload* payload = _reports[indexPath.row];
//    DamageReportData* data = payload.messageData;
    
    if([_dataManager getIsIpad] == true){
        [self prepareForTabletCanvasSwap:[payload.isDraft boolValue] :indexPath.row];
    }
    
    return indexPath;
}

- (void)prepareForTabletCanvasSwap:(BOOL)isEdit :(NSInteger)index{
    
    DamageReportPayload *payload;
    if(_reports.count > 0 && index >= 0) {
        payload = _reports[index];
    } else if( index == -2){
        payload = [IncidentButtonBar GetDamageReportDetailView].payload;
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
        
    } else if (isEdit==true) {
        [IncidentButtonBar GetDamageReportDetailView].hideEditControls = NO;
        [[IncidentButtonBar GetSaveDraftButton] setHidden:FALSE];
        [[IncidentButtonBar GetSubmitButton] setHidden:FALSE];
        
    } else {
        [[IncidentButtonBar GetSaveDraftButton] setHidden:TRUE];
        [[IncidentButtonBar GetSubmitButton] setHidden:TRUE];
    }

    [IncidentButtonBar GetDamageReportDetailView].payload = payload;
    [[IncidentButtonBar GetDamageReportDetailView] configureView];
    
    [[IncidentButtonBar GetAddButton] setHidden:TRUE];
    [[IncidentButtonBar GetFilterButton] setHidden:TRUE];
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

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    //filtered lists not added to damage reports yet
//    if(_filteredList && indexPath.row==0){
//        return 180;
//    }
    return 50;
}

@end

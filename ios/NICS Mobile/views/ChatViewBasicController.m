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
//  ChatViewBasicController.m
//  NICS Mobile
//
//

#import "ChatViewBasicController.h"

@implementation ChatViewBasicController


- (void)viewDidLoad {
    [super viewDidLoad];
    
     _dataManager = [DataManager getInstance];
    
    _dateFormatter = [[NSDateFormatter alloc] init];
    [_dateFormatter setDateFormat:@"MM/dd HH:mm:ss"];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(addMessageToTableFromNotification:) name:@"chatMessagesUpdateReceived" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(chatMessagesPolledNothing) name:@"chatMessagesPolledNothing" object:nil];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(ResetChatForCollabRoomSwitch:) name:@"CollabRoomSwitched" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(ResetChatForCollabRoomSwitch:) name:@"IncidentSwitched" object:nil];
    
    _messages = [_dataManager getAllChatMessagesForCollabroomId:[_dataManager getSelectedCollabroomId] since:0];
    _willPollMoreChats = false;
    _chatPolledAt = 0;
}

-(void)viewDidAppear:(BOOL)animated
{
    if(_chatTableView.contentSize.height >_chatTableView.frame.size.height)
    {
        CGPoint offset = CGPointMake(0, _chatTableView.contentSize.height - _chatTableView.frame.size.height);
        [_chatTableView setContentOffset:offset animated:NO];
    }
 
    if([[_dataManager getSelectedCollabroomName] isEqualToString:@"N/A"]){
        [_messages removeAllObjects];
        ChatPayload *noCollabRoomMessage = [[ChatPayload alloc] init];
        noCollabRoomMessage.message = NSLocalizedString(@"Use the Select Room button above to join your team's chat",nil);
        noCollabRoomMessage.nickname = @"";
        [_messages addObject:noCollabRoomMessage];
        [self.tableView reloadData];
        
    }else if(_messages.count <= 0){
        [_messages removeAllObjects];
        ChatPayload *emptyChatMessage = [[ChatPayload alloc] init];
        emptyChatMessage.message = NSLocalizedString(@"No messages have been posted in this Room yet.",nil);
        emptyChatMessage.nickname = @"";
        [_messages addObject:emptyChatMessage];
        [self.tableView reloadData];
    }
}

-(void)ResetChatForCollabRoomSwitch:(NSNotification*)notification{

    _messages = [_dataManager getAllChatMessagesForCollabroomId:[_dataManager getSelectedCollabroomId] since:0];
    
    if([[_dataManager getSelectedCollabroomName] isEqualToString:@"N/A"]){
        [_messages removeAllObjects];
        ChatPayload *noCollabRoomMessage = [[ChatPayload alloc] init];
        noCollabRoomMessage.message = NSLocalizedString(@"Use the Select Room button above to join your team's chat",nil);
        noCollabRoomMessage.nickname = @"";
        [_messages addObject:noCollabRoomMessage];
        [self.tableView reloadData];
        
    }else if(_messages.count <= 0){
        [_messages removeAllObjects];
        ChatPayload *emptyChatMessage = [[ChatPayload alloc] init];
        emptyChatMessage.message = NSLocalizedString(@"No messages have been posted in this Room yet.",nil);
        emptyChatMessage.nickname = @"";
        [_messages addObject:emptyChatMessage];
        [self.tableView reloadData];
    }
    
    [_chatTableView reloadData];
    
    if(_chatTableView.contentSize.height >_chatTableView.frame.size.height)
    {
        CGPoint offset = CGPointMake(0, _chatTableView.contentSize.height - _chatTableView.frame.size.height);
        [_chatTableView setContentOffset:offset animated:NO];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {

    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if(_willPollMoreChats){
        return [_messages count] +1;
    }else{
        return [_messages count];
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    ChatViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    
    if(_willPollMoreChats && indexPath.row >= [_messages count]){
        cell.MessageLabel.text = NSLocalizedString( @"Checking for new messages",nil);
        cell.UserLabel.text = @"";
        cell.TimeStampLabel.text = @"";
        [cell.activityIndicator setHidden:false];
        [cell.activityIndicator startAnimating];
    }else{

        ChatPayload *payload = [_messages objectAtIndex:( ([_messages count] -1) - indexPath.row)];
        
        [cell.activityIndicator setHidden:true];
        [cell.activityIndicator stopAnimating];
        
        cell.MessageLabel.text = payload.message;
        cell.UserLabel.text = payload.nickname;
        
        NSDate* date = [NSDate dateWithTimeIntervalSince1970:[payload.created longLongValue]/1000.0];
        
        cell.TimeStampLabel.text = [NSString stringWithFormat:@"%@", [_dateFormatter stringFromDate:date]];
        
        cell.MessageLabel.font=[cell.MessageLabel.font fontWithSize:16];
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(_willPollMoreChats && indexPath.row >= [_messages count]){
        return 60;
    }else{
        ChatPayload *payload = [_messages objectAtIndex:( ([_messages count] -1) - indexPath.row)];

        UIFont *cellFont = [UIFont fontWithName:@"Helvetica" size:16];
        CGSize constraintSize = CGSizeMake(tableView.frame.size.width , MAXFLOAT);
        CGSize labelSize = [payload.message sizeWithFont:cellFont constrainedToSize:constraintSize lineBreakMode:UILineBreakModeWordWrap];

        return labelSize.height + 60;
    }
}
/*
- (void) tableView: (UITableView *) tableView didSelectRowAtIndexPath: (NSIndexPath *) indexPath {
    [self.view endEditing:YES];
}
*/
/*
- (NSIndexPath *)tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)path
{
   
    return nil;
}
*/
- (void) addMessageToTable{
    _messages = [_dataManager getAllChatMessagesForCollabroomId:[_dataManager getSelectedCollabroomId] since:0];
    [_chatTableView reloadData];
    
    if(_chatTableView.contentSize.height >_chatTableView.frame.size.height)
    {
        CGPoint offset = CGPointMake(0, _chatTableView.contentSize.height - _chatTableView.frame.size.height);
        [_chatTableView setContentOffset:offset animated:YES];
    }
}

- (void) addMessageToTableFromNotification:(NSNotification *) notification{
    
     _messages = [_dataManager getAllChatMessagesForCollabroomId:[_dataManager getSelectedCollabroomId] since:0];
    
    [_chatTableView reloadData];
    
    if(_chatTableView.contentSize.height >_chatTableView.frame.size.height)
    {
        CGPoint offset = CGPointMake(0, _chatTableView.contentSize.height - _chatTableView.frame.size.height);
        [_chatTableView setContentOffset:offset animated:YES];
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if ((scrollView.contentOffset.y + scrollView.frame.size.height) >= scrollView.contentSize.height + 5)
    {
        if (!_willPollMoreChats)
        {
            _willPollMoreChats = YES;
        }
    }
}

-(void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    if(_willPollMoreChats){
        [self PollForMoreChats];
        _chatPolledAt = [[NSDate date] timeIntervalSince1970];
        [self.tableView reloadData];
    }
}

-(void)PollForMoreChats{
    [_dataManager requestChatMessagesRepeatedEvery:[[DataManager getChatUpdateFrequencyFromSettings]intValue] immediate:YES];
}

-(void)chatMessagesPolledNothing{
    float timeRemainingFromMinimum = ([[NSDate date] timeIntervalSince1970] - _chatPolledAt)/1000;
    
    if(timeRemainingFromMinimum >= 2){
        [self hidePollingMessage];
    }else{
        dispatch_async(dispatch_get_main_queue(), ^{
            [self performSelector:@selector(hidePollingMessage) withObject:nil afterDelay:2 - timeRemainingFromMinimum];
        });
    }
}
-(void)hidePollingMessage{
    _willPollMoreChats = NO;
    [self.tableView reloadData];
}


@end

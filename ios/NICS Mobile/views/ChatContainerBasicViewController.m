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
//  ChatContainerBasicViewController.m
//  NICS Mobile
//
//

#import "ChatContainerBasicViewController.h"

@interface ChatContainerBasicViewController ()

@end

@implementation ChatContainerBasicViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _dataManager = [DataManager getInstance];
    _chatViewBasicController = self.childViewControllers[0];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardWillHideNotification object:nil];
    
    // Do any additional setup after loading the view.
}

-(void)viewDidAppear:(BOOL)animated
{
    if([_dataManager getIsIpad]){
        [super viewDidLoad];
        [_chatViewBasicController viewDidAppear:TRUE];
    }
    _originalFrame = self.view.frame;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)sendButtonPressed:(id)sender {
    [_textField resignFirstResponder];
    
    if(_textField.text.length > 0) {
        ChatPayload *payload = [ChatPayload new];
        
        double temp =  [[NSDate date] timeIntervalSince1970] * 1000.00;
        
        NSNumber *date = [NSNumber numberWithLongLong:round(temp)];
        
        payload.created = date;
        payload.lastupdated = date;
//        payload.seqTime = date;
        payload.message = _textField.text;
        payload.id = @-1;
        payload.incidentId = [_dataManager getActiveIncidentId];
        payload.collabroomid = [_dataManager getSelectedCollabroomId];
        payload.userId = [_dataManager getUserId];
//        payload.topic = [NSString stringWithFormat:@"%@%@%@%@", @"LDDRS.incidents.", [_dataManager getActiveIncidentName], @".collab.", [_dataManager getSelectedCollabroomName]];
        payload.seqnum = @-1;
        payload.nickname = [_dataManager getUsername];
        payload.userOrgName = @"";
        
        userorg* org =[_dataManager.orgData.userorgs objectAtIndex:0];
        payload.userorgid = org.userorgid;
        payload.chatid = @-1;
        
        [_dataManager addChatMessageToStoreAndForward:payload];
        
   //     NSBubbleData *sayBubble = [NSBubbleData dataWithText:_textField.text date:[NSDate dateWithTimeIntervalSinceNow:0] type:BubbleTypeMine];
     //   [_bubbleData addObject:sayBubble];
       // [_bubbleTableView reloadData];
    }
    
    _textField.text = @"";
    [_chatViewBasicController addMessageToTable];
    [self.view endEditing:YES];
    //[self scrollToBottom];

}

#pragma mark - Keyboard events

- (void)keyboardWasShown:(NSNotification*)aNotification
{
    [Utils AdjustViewForKeyboard:self.view :true:_originalFrame :aNotification];
}

- (void)keyboardWillBeHidden:(NSNotification*)aNotification
{
    [Utils AdjustViewForKeyboard:self.view :FALSE:_originalFrame :aNotification];
    
}


-(BOOL)textFieldShouldReturn:(UITextField *)textField {
//    [_textField resignFirstResponder];
    
    [self sendButtonPressed:nil];
    return YES;
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{

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

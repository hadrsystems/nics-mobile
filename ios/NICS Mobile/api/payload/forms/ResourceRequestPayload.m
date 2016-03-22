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
//  FieldReportPayload.m
//  nics_iOS
//
//

#import "ResourceRequestPayload.h"

@implementation ResourceRequestPayload

- (id)init {
    self = [super init];
    
    _messageData = [[ResourceRequestData alloc]init];
    
    _messageData.quantity = @"";
    _messageData.priority = @"";
    _messageData.msgDescription = @"";
    _messageData.type = @"";
    _messageData.source = @"";
    _messageData.location = @"";
    _messageData.eta = @"";
    _messageData.status = @"";
    _messageData.time = @"0";
    _messageData.user = @"";
    
    return self;
}

-(void)parse{
    if([self.message length] != 0) {
        
        NSError *e = nil;
        self.messageData = [[ResourceRequestData alloc] initWithString:self.message error:&e];
    }
}

-(NSMutableDictionary *) toSqlMapping {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    if(self.isDraft) {
        [dataDictionary setObject: self.isDraft forKey:@"isDraft"];
    } else {
        [dataDictionary setObject: @0 forKey:@"isDraft"];
    }
    [dataDictionary setObject:self.incidentid forKey:@"incidentid"];
    
    if(self.formid != nil) {
        [dataDictionary setObject:self.formid forKey:@"formId"];
    } else {
        [dataDictionary setObject:@0 forKey:@"formId"];
    }
    
    [dataDictionary setObject:self.usersessionid forKey:@"usersessionid"];
    [dataDictionary setObject:self.seqtime forKey:@"seqtime"];
    
    if(self.seqnum != nil) {
        [dataDictionary setObject:self.seqnum forKey:@"seqnum"];
    } else {
        [dataDictionary setObject:@0 forKey:@"seqnum"];
    }
    
    [dataDictionary setObject:_messageData.quantity forKey:@"quantity"];
    [dataDictionary setObject:_messageData.priority forKey:@"priority"];
    [dataDictionary setObject:_messageData.description forKey:@"description"];
    [dataDictionary setObject:_messageData.type forKey:@"type"];
    [dataDictionary setObject:_messageData.source forKey:@"source"];
    [dataDictionary setObject:_messageData.location forKey:@"location"];
    [dataDictionary setObject:_messageData.eta forKey:@"eta"];
    [dataDictionary setObject:_messageData.status forKey:@"reqstatus"];
    
    if(_messageData.time) {
        [dataDictionary setObject:_messageData.time forKey:@"time"];
    } else {
        [dataDictionary setObject:@"" forKey:@"time"];
    }
    [dataDictionary setObject:_messageData.user forKey:@"user"];
    
    if(self.status) {
        [dataDictionary setObject:self.status forKey:@"status"];
    } else {
        [dataDictionary setObject:@-1 forKey:@"status"];
    }
    [dataDictionary setObject:[self toJSONString] forKey:@"json"];
    
    return dataDictionary;
}

-(NSMutableDictionary *) getFormRepresentation {
     NSMutableDictionary *formDictionary = [NSMutableDictionary new];
    
    [formDictionary setObject:_messageData.description forKey:@"description"];
    [formDictionary setObject:_messageData.type forKey:@"type"];
    [formDictionary setObject:_messageData.source forKey:@"source"];
    [formDictionary setObject:_messageData.quantity forKey:@"quantity"];
    [formDictionary setObject:_messageData.priority forKey:@"priority"];
    [formDictionary setObject:_messageData.location forKey:@"location"];
    [formDictionary setObject:_messageData.eta forKey:@"eta"];
    [formDictionary setObject:_messageData.status forKey:@"status"];
    
    return formDictionary;
}

@end

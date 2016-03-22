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

#import "SimpleReportPayload.h"

@implementation SimpleReportPayload

-(void) parse{
    if([self.message length] != 0) {
        
        NSError *e = nil;
        self.messageData = [[SimpleReportData alloc] initWithString:self.message error:&e];
        
        if(e != nil) {
            NSLog(@"%@", [e localizedDescription]);
        }
    }
}

-(NSMutableDictionary *) toSqlMapping {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    if(self.isDraft) {
        [dataDictionary setObject: self.isDraft forKey:@"isDraft"];
    } else {
        [dataDictionary setObject: @0 forKey:@"isDraft"];
    }
    [dataDictionary setObject:self.seqtime forKey:@"seqtime"];
    [dataDictionary setObject:self.incidentid forKey:@"incidentid"];
    [dataDictionary setObject:self.formtypeid forKey:@"formtypeid"];
    
    [dataDictionary setObject:_messageData.user forKey:@"user"];
    [dataDictionary setObject:_messageData.latitude forKey:@"latitude"];
    [dataDictionary setObject:_messageData.longitude forKey:@"longitude"];
    [dataDictionary setObject:_messageData.msgDescription forKey:@"description"];
    [dataDictionary setObject:_messageData.category forKey:@"category"];
    
    if(_messageData.image != nil) {
        [dataDictionary setObject:_messageData.image forKey:@"image"];
    } else {
        [dataDictionary setObject:@"" forKey:@"image"];
    }
    
    if(_messageData.fullpath != nil) {
        [dataDictionary setObject:_messageData.fullpath forKey:@"fullpath"];
    } else {
        [dataDictionary setObject:@"" forKey:@"fullpath"];
    }
    
    if(!self.status) {
        self.status = [NSNumber numberWithInt:SENT];
    }
    
    [dataDictionary setObject:self.status forKey:@"status"];
    [dataDictionary setObject:[self toJSONString] forKey:@"json"];
    
    return dataDictionary;
}

@end

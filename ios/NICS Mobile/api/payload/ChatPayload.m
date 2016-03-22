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
//  ChatPayload.m
//  nics_iOS
//
//

#import "ChatPayload.h"

@implementation ChatPayload

-(NSMutableDictionary *) toSqlMapping {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    [dataDictionary setObject:_created forKey:@"created"];
    
    if(_lastupdated != nil){ [dataDictionary setObject:_lastupdated forKey:@"lastupdated"];}
    else{[dataDictionary setObject:[NSNumber numberWithInt:0] forKey:@"lastupdated"];}
    
    [dataDictionary setObject:_message forKey:@"message"];
    
    if(_userId != nil) {
        [dataDictionary setObject:_userId forKey:@"userId"];
    } else {
        [dataDictionary setObject:@0 forKey:@"userId"];
    }
    
    if(_chatid != nil) {
        [dataDictionary setObject:_chatid forKey:@"chatid"];
    } else {
        [dataDictionary setObject:@0 forKey:@"chatid"];
    }
    
    [dataDictionary setObject:_message forKey:@"message"];
    [dataDictionary setObject:_collabroomid forKey:@"collabroomid"];
//    [dataDictionary setObject:_seqTime forKey:@"seqTime"];
    [dataDictionary setObject:_seqnum forKey:@"seqnum"];
//    [dataDictionary setObject:_topic forKey:@"topic"];
    [dataDictionary setObject:_nickname forKey:@"nickname"];
    [dataDictionary setObject:_userOrgName forKey:@"userOrgName"];
    
    if(_userorgid != nil) {
        [dataDictionary setObject:_userorgid forKey:@"userorgid"];
    } else {
        [dataDictionary setObject:@0 forKey:@"userorgid"];
    }
    
    
    [dataDictionary setObject:[self toJSONString] forKey:@"json"];
    
    return dataDictionary;
}

-(NSString *)toJSONStringPost {

    return [self toJSONStringWithKeys:@[ @"chatid", @"message", @"userorgid"]];
//      return [self toJSONStringWithKeys:@[@"created", @"userId", @"message",@"collabroomid", @"seqnum"]];
}

@end

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
//  ChatTable.m
//  nics_iOS
//
//

#import "ChatTable.h"

@implementation ChatTable
static NSDictionary * tableColumnsDictionary;

- (id)initWithName:(NSString *)tableName databaseQueue:(FMDatabaseQueue *) databaseQueue
{
    self = [super initWithName:tableName databaseQueue:databaseQueue];
    if (self) {
        tableColumnsDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:
                                    @"integer primary key", @"id",
                                    @"integer",             @"created",
                                    @"integer",             @"lastupdated",
                                    @"integer",             @"chatid",
                                    @"integer",             @"userId",
                                    @"text",                @"message",
                                    @"integer",             @"incidentId",
                                    @"integer",             @"collabroomid",
                                    @"integer",             @"seqtime",
                                    @"integer",             @"seqnum",
//                                    @"text",                @"topic",
                                    @"text",                @"nickname",
                                    @"text",                @"userOrgName",
                                    @"integer",             @"userorgid",
                                    @"text",                @"json",
                                    nil
                                 ];
        
        [self createTableFromDictionary:tableColumnsDictionary];
        

    }
    return self;
}

- (BOOL) addData:(ChatPayload *) data
{
    return [self insertRowForTableDictionary:tableColumnsDictionary dataDictionary:[data toSqlMapping]];
}

- (void) removeData:(ChatPayload *) data
{
    [self deleteRowsByKey:@"id" value:data.id];
}

- (BOOL) addDataArray:(NSArray *) dataArray {
    NSMutableArray* messagePayloads = [[NSMutableArray alloc] init];
    
    for(ChatPayload *payload in dataArray) {
        [messagePayloads addObject: [payload toSqlMapping]];
    }
    
    return [self insertAllRowsForTableDictionary:tableColumnsDictionary dataArray:messagePayloads];
}

- (NSNumber *) getLastMessageTimestampForCollabroomId: (NSNumber *)collabroomId {
    NSDictionary* result = [[self selectRowsByKey:@"collabroomid" value:collabroomId orderedBy:[NSArray arrayWithObject:@"created"] isDescending:YES] firstObject];
    
    if(result != nil) {
        return [result objectForKey:@"created"];
    } else {
        return @0;
    }
}

- (NSMutableArray<ChatPayload> *) getDataForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    NSDictionary * keys = [[NSDictionary alloc] initWithObjectsAndKeys:
                            collabroomId,   @"collabroomid = ?",
                            timestamp,      @"created > ?",
                            nil];

    NSMutableArray *results = [self selectRowsByKeyDictionary:keys orderedBy:[NSArray arrayWithObject:@"created"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        ChatPayload *payload = [[ChatPayload alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [payload setId:[result objectForKey:@"id"]];
        
        if(error != nil) {
            NSLog(@"%@", [error localizedDescription]);
        }
        
        [parsedResults addObject: payload];
    }
    
    return (NSMutableArray<ChatPayload>*) parsedResults;
}

- (NSMutableArray<ChatPayload> *) getAllChatMessages {
    NSMutableArray *results = [self selectAllRowsAndOrderedBy:[NSArray arrayWithObject:@"created"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        ChatPayload *payload = [[ChatPayload alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [payload setId:[result objectForKey:@"id"]];
        
        if(error != nil) {
            NSLog(@"%@", [error localizedDescription]);
        }
        
        [parsedResults addObject: payload];
    }
    
    return (NSMutableArray<ChatPayload>*) parsedResults;
}

@end

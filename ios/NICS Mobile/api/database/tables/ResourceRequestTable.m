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
//  ResourceRequestTable.m
//  nics_iOS
//
//

#import "ResourceRequestTable.h"

@implementation ResourceRequestTable
static NSDictionary * tableColumnsDictionary;

- (id)initWithName:(NSString *)tableName databaseQueue:(FMDatabaseQueue *) databaseQueue
{
    self = [super initWithName:tableName databaseQueue:databaseQueue];
    if (self) {
        tableColumnsDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:
                                    @"integer primary key", @"id",
                                    @"integer",             @"isDraft",
                                    @"integer",             @"createdUTC",
                                    @"integer",             @"lastUpdatedUTC",
                                    @"integer",             @"incidentId",
                                    @"integer",             @"formId",
                                    @"integer",             @"senderUserId",
                                    @"integer",             @"userSessionId",
                                    @"integer",             @"seqTime",
                                    @"integer",             @"seqNum",
                                    @"integer",             @"status",
                                    @"text",                @"quantity",
                                    @"text",                @"priority",
                                    @"text",                @"description",
                                    @"integer",             @"type",
                                    @"text",                @"source",
                                    @"text",                @"location",
                                    @"text",                @"eta",
                                    @"text",                @"reqstatus",
                                    @"text",                @"time",
                                    @"text",                @"user",
                                    @"text",                @"json",
                                  
                                    nil
                                 ];
        
        [self createTableFromDictionary:tableColumnsDictionary];
        

    }
    return self;
}


- (BOOL) addData:(ResourceRequestPayload *) data
{
    return [self insertRowForTableDictionary:tableColumnsDictionary dataDictionary:[data toSqlMapping]];
}

- (void) removeData:(ResourceRequestPayload *) data
{
    [self deleteRowsByKey:@"id" value:data.id];
}

- (BOOL) addDataArray:(NSArray *) dataArray {
    NSMutableArray* messagePayloads = [[NSMutableArray alloc] init];
    for(ResourceRequestPayload *payload in dataArray) {
        [messagePayloads addObject:[payload toSqlMapping]];
    }
    return [self insertAllRowsForTableDictionary:tableColumnsDictionary dataArray:messagePayloads];
}

- (NSNumber *) getLastResourceRequestTimestampForIncidentId: (NSNumber *)incidentId {
    NSDictionary* result = [[self selectRowsByKey:@"incidentId" value:incidentId orderedBy:[NSArray arrayWithObject:@"lastUpdatedUTC"] isDescending:YES] firstObject];
    
    if(result != nil) {
        return [result objectForKey:@"lastUpdatedUTC"];
    } else {
        return @0;
    }
}

- (NSMutableArray<ResourceRequestPayload> *) getResourceRequestsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    NSDictionary * keys = [[NSDictionary alloc] initWithObjectsAndKeys:
                            incidentId,     @"incidentId = ?",
                            timestamp,      @"lastUpdatedUTC > ?",
                            nil];
    
    NSMutableArray *results = [self selectRowsByKeyDictionary:keys orderedBy:[NSArray arrayWithObject:@"lastUpdatedUTC"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        ResourceRequestPayload *payload = [[ResourceRequestPayload alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [payload parse];
        [payload setId:[result objectForKey:@"id"]];
        [payload setFormtypeid:[result objectForKey:@"formtypeid"]];
        
        if(error != nil) {
            NSLog(@"%@", [error localizedDescription]);
        }
        
        [parsedResults addObject:payload];
    }
    
    return (NSMutableArray<ResourceRequestPayload>*) parsedResults;
}

- (NSMutableArray<ResourceRequestPayload> *) getAllResourceRequests {
    NSMutableArray *results = [self selectAllRowsAndOrderedBy:[NSArray arrayWithObject:@"lastUpdatedUTC"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        ResourceRequestPayload *payload = [[ResourceRequestPayload alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [payload parse];
        [payload setId:[result objectForKey:@"id"]];
        [payload setFormtypeid:[result objectForKey:@"formtypeid"]];
        
        if(error != nil) {
            NSLog(@"%@", [error localizedDescription]);
        }
        
        [parsedResults addObject:payload];
    }
    
    return (NSMutableArray<ResourceRequestPayload>*) parsedResults;
}

@end

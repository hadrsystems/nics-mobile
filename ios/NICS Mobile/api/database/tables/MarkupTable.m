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
//
//  MarkupTable.m
//  Phinics_iOS
//
//

#import "MarkupTable.h"

@implementation MarkupTable
static NSDictionary * tableColumnsDictionary;

- (id)initWithName:(NSString *)tableName databaseQueue:(FMDatabaseQueue *) databaseQueue
{
    self = [super initWithName:tableName databaseQueue:databaseQueue];
    if (self) {
        tableColumnsDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:
                                    @"integer primary key", @"id",
                                    @"integer",             @"collabRoomId",
                                    @"text",                @"dashStyle",
                                    @"text",                @"featureattributes",
                                    @"text",                @"featureId",
                                    @"text",                @"fillColor",
                                    @"text",                @"graphic",
                                    @"real",                @"graphicHeight",
                                    @"real",                @"graphicWidth",
                                    @"integer",             @"gesture",
                                    @"text",                @"ip",
                                    @"real",                @"labelSize",
                                    @"text",                @"labelText",
                                    @"text",                @"username",
                                    @"integer",             @"seqNum",
                                    @"text",                @"strokeColor",
                                    @"real",                @"strokeWidth",
                                    @"integer",             @"time",
                                    @"text",                @"topic",
                                    @"text",                @"type",
                                    @"real",                @"opacity",
                                    @"text",                @"geometry",
                                  @"text",                @"geometryFiltered",
                                    @"real",                @"radius",
                                    @"real",                @"rotation",
                                    @"text",                @"lastupdate",
                                    @"text",                @"json",
                                    nil
                                 ];
        
        [self createTableFromDictionary:tableColumnsDictionary];
        

    }
    return self;
}


- (BOOL) addData:(MarkupFeature *) data
{
    return [self insertRowForTableDictionary:tableColumnsDictionary dataDictionary:[data toDictionary]];
}

- (BOOL) addDataArray:(NSArray *) dataArray {
    NSMutableArray* messagePayloads = [[NSMutableArray alloc] init];
    
    if(dataArray.count > 0) {
        [self deleteRowsByKey:@"collabRoomId" value:((MarkupFeature *)dataArray[0]).collabRoomId];
        
        for(MarkupFeature *payload in dataArray) {
            [messagePayloads addObject:[payload toSqlMapping]];
        }
    }
    
    return [self insertAllRowsForTableDictionary:tableColumnsDictionary dataArray:messagePayloads];
}

- (NSNumber *) getLastMarkupFeatureTimestampForCollabroomId: (NSNumber *)collabroomId {
    NSDictionary* result = [[self selectRowsByKey:@"collabRoomId" value:collabroomId orderedBy:[NSArray arrayWithObject:@"time"] isDescending:YES] firstObject];
    
    if(result != nil) {
        return [result objectForKey:@"time"];
    } else {
        return @0;
    }
}

- (NSMutableArray<MarkupFeature> *) getMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    NSDictionary * keys = [[NSDictionary alloc] initWithObjectsAndKeys:
                            collabroomId,     @"collabRoomId = ?",
                            timestamp,        @"time > ?",
                            nil];
    NSMutableArray *results = [self selectRowsByKeyDictionary:keys orderedBy:[NSArray arrayWithObject:@"time"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        [parsedResults addObject:[[MarkupFeature alloc] initWithString:[result objectForKey:@"json"] error:&error]];
    }
    
    return (NSMutableArray<MarkupFeature>*) parsedResults;
}

@end

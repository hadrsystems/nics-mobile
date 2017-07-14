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
//  MarkupTable.m
//  nics_iOS
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
                                    @"integer",             @"seqtime",
                                    @"text",                @"topic",
                                    @"text",                @"type",
                                    @"real",                @"opacity",
                                    @"text",                @"geometry",
                                    @"text",                @"geometryFiltered",
                                    @"real",                @"radius",
                                    @"real",                @"rotation",
                                    @"text",                @"json",
                                    nil
                                 ];
        
        [self createTableFromDictionary:tableColumnsDictionary];
    }
    return self;
}

- (BOOL) addData:(MarkupFeature *) data
{
    return [self insertRowForTableDictionary:tableColumnsDictionary dataDictionary:[data toSqlMapping]];
}

- (BOOL) addDataArray:(NSArray *) dataArray {
    NSMutableArray* messagePayloads = [[NSMutableArray alloc] init];
    
    if(dataArray.count > 0) {
        for(MarkupFeature *payload in dataArray) {
            [messagePayloads addObject:[payload toSqlMapping]];
        }
    }
    
    return [self insertAllRowsForTableDictionary:tableColumnsDictionary dataArray:messagePayloads];
}

-(void) removeAllFeaturesInCollabroom:(NSNumber*)collabRoomId{
    [self deleteRowsByKey:@"collabRoomId" value:collabRoomId];
}

- (void) removeData:(MarkupFeature *) feature{
    [self deleteRowsByKey:@"id" value:feature.id];
}

- (void) removeDataByFeatureId:(NSString *) featureId{
    [self deleteRowsByKey:@"featureId" value:featureId];
}

- (NSNumber *) getLastMarkupFeatureTimestampForCollabroomId: (NSNumber *)collabroomId {
    NSDictionary* result = [[self selectRowsByKey:@"collabRoomId" value:collabroomId orderedBy:[NSArray arrayWithObject:@"seqtime"] isDescending:YES] firstObject];
    
    if(result != nil) {
        return [result objectForKey:@"seqtime"];
    } else {
        return @0;
    }
}

- (NSMutableArray<MarkupFeature> *) getMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    NSDictionary * keys = [[NSDictionary alloc] initWithObjectsAndKeys:
                            collabroomId,     @"collabRoomId = ?",
                            timestamp,        @"seqtime > ?",
                            nil];
    NSMutableArray *results = [self selectRowsByKeyDictionary:keys orderedBy:[NSArray arrayWithObject:@"seqtime"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        MarkupFeature* feature = [[MarkupFeature alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [feature setId:[result objectForKey:@"id"]];
        [parsedResults addObject:feature];
    }
    
    return (NSMutableArray<MarkupFeature>*) parsedResults;
}

- (NSMutableArray<MarkupFeature> *) getAllMarkupFeatures {
    NSMutableArray *results = [self selectAllRowsAndOrderedBy:[NSArray arrayWithObject:@"seqtime"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        MarkupFeature* feature = [[MarkupFeature alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [feature setId:[result objectForKey:@"id"]];
        [parsedResults addObject:feature];
    }
    
    return (NSMutableArray<MarkupFeature>*) parsedResults;
}

@end

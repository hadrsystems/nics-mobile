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
//  DatabaseManager.m
//  nics_iOS
//
//

#import "DatabaseManager.h"

@implementation DatabaseManager

- (id)init
{
    self = [super init];
    if (self) {
        NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentDir = [documentPaths objectAtIndex:0];
        
        _databaseQueue = [FMDatabaseQueue databaseQueueWithPath:[documentDir stringByAppendingPathComponent:@"nics.db"]];
        
        _chatReceiveTable = [[ChatTable alloc] initWithName: @"chatReceiveTable" databaseQueue:_databaseQueue];
        _chatSendTable = [[ChatTable alloc] initWithName: @"chatSendTable" databaseQueue:_databaseQueue];
        
        _damageReportReceiveTable = [[DamageReportTable alloc] initWithName:@"damageReportReceiveTable" databaseQueue:_databaseQueue];
        _damageReportSendTable = [[DamageReportTable alloc] initWithName:@"damageReportSendTable" databaseQueue:_databaseQueue];
        
        _fieldReportReceiveTable = [[FieldReportTable alloc] initWithName:@"fieldReportReceiveTable" databaseQueue:_databaseQueue];
        _fieldReportSendTable = [[FieldReportTable alloc] initWithName:@"fieldReportSendTable" databaseQueue:_databaseQueue];
        
        _resourceRequestReceiveTable = [[ResourceRequestTable alloc] initWithName:@"resourceRequestReceiveTable" databaseQueue:_databaseQueue];
        _resourceRequestSendTable = [[ResourceRequestTable alloc] initWithName:@"resourceRequestSendTable" databaseQueue:_databaseQueue];
        
        _simpleReportReceiveTable = [[SimpleReportTable alloc] initWithName:@"simpleReportReceiveTable" databaseQueue:_databaseQueue];
        _simpleReportSendTable = [[SimpleReportTable alloc] initWithName:@"simpleReportSendTable" databaseQueue:_databaseQueue];

        _weatherReportReceiveTable = [[WeatherReportTable alloc] initWithName:@"weatherReportReceiveTable" databaseQueue:_databaseQueue];
        _weatherReportSendTable = [[WeatherReportTable alloc] initWithName:@"weatherReportSendTable" databaseQueue:_databaseQueue];
        
        _markupReceiveTable = [[MarkupTable alloc] initWithName:@"markupReceiveTable" databaseQueue:_databaseQueue];
        _markupSendTable = [[MarkupTable alloc] initWithName:@"markupSendTable" databaseQueue:_databaseQueue];
        
        _mdtSendTable = [[MDTTable alloc] initWithName:@"mdtSendTable" databaseQueue:_databaseQueue];
        
        _personalLogTable = [[ChatTable alloc] initWithName:@"personalLogTable" databaseQueue:_databaseQueue];
        
        
    }
    return self;
}


-(void)ClearAllLocalDatabases{
    [_chatReceiveTable deleteAllRows];
    [_chatSendTable deleteAllRows];
    [_damageReportReceiveTable deleteAllRows];
    [_damageReportSendTable deleteAllRows];
    [_fieldReportReceiveTable deleteAllRows];
    [_fieldReportSendTable deleteAllRows];
    [_resourceRequestReceiveTable deleteAllRows];
    [_resourceRequestSendTable deleteAllRows];
    [_simpleReportReceiveTable deleteAllRows];
    [_simpleReportSendTable deleteAllRows];
    [_weatherReportReceiveTable deleteAllRows];
    [_weatherReportSendTable deleteAllRows];
    [_markupReceiveTable deleteAllRows];
    [_markupSendTable deleteAllRows];
    [_mdtSendTable deleteAllRows];
    [_personalLogTable deleteAllRows];
}

#pragma mark Chat Message History/Store & Forward
- (BOOL)addChatMessagesToHistory:(NSArray<ChatPayload> *) payloadArray {
    return [_chatReceiveTable addDataArray:payloadArray];
}

- (BOOL)addChatMessageToHistory:(ChatPayload *) payload {
    return [_chatReceiveTable addData: payload];
}

- (BOOL)addChatMessagesToStoreAndForward:(NSArray<ChatPayload> *) payloadArray {
    return [_chatSendTable addDataArray:payloadArray];
}

- (BOOL)addChatMessageToStoreAndForward:(ChatPayload *) payload {
    return [_chatSendTable addData: payload];
}

- (void)deleteAllChatMessageFromRecieveTable {
    [_chatReceiveTable deleteAllRows];
}

- (void)deleteChatMessageFromStoreAndForward:(ChatPayload *) payload {
    [_chatSendTable removeData: payload];
}

- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId {
    return [_chatReceiveTable getDataForCollabroomId:collabroomId since:0];
}

- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    NSMutableArray<ChatPayload>* temp = [_chatReceiveTable getDataForCollabroomId:collabroomId since:timestamp];
    [temp addObjectsFromArray:[_chatSendTable getDataForCollabroomId:collabroomId since:timestamp]];
    
    [temp sortUsingComparator:^NSComparisonResult(ChatPayload *obj1, ChatPayload *obj2) {
        return [obj2.created compare:obj1.created];
    }];
    
    return temp;
}

- (NSMutableArray<ChatPayload> *)getAllChatMessagesFromStoreAndForward {
    return [_chatSendTable getAllChatMessages];
}

- (NSNumber *)getLastChatMessageTimestampForCollabroomId: (NSNumber *) collabroomId {
    return [_chatReceiveTable getLastMessageTimestampForCollabroomId:collabroomId];
}


#pragma mark Damage Report History/Store & Forward
- (BOOL)addDamageReportsToHistory:(NSArray<DamageReportPayload> *) payloadArray {
    return [_damageReportReceiveTable addDataArray:payloadArray];
}

- (BOOL)addDamageReportToHistory:(DamageReportPayload *) payload {
    return [_damageReportReceiveTable addData: payload];
}

- (BOOL)addDamageReportsToStoreAndForward:(NSArray<DamageReportPayload> *) payloadArray {
    return [_damageReportSendTable addDataArray:payloadArray];
}

- (BOOL)addDamageReportToStoreAndForward:(DamageReportPayload *) payload {
    return [_damageReportSendTable addData: payload];
}

- (void)deleteDamageReportFromStoreAndForward:(DamageReportPayload *) payload {
    [_damageReportSendTable removeData: payload];
}

- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    NSMutableArray<DamageReportPayload>* temp = [_damageReportReceiveTable getDamageReportsForIncidentId:incidentId since:timestamp];
    [temp addObjectsFromArray:[_damageReportSendTable getDamageReportsForIncidentId:incidentId since:timestamp]];
    
    [temp sortUsingComparator:^NSComparisonResult(DamageReportPayload *obj1, DamageReportPayload *obj2) {
        return [obj2.seqtime compare:obj1.seqtime];
    }];
    
    return temp;
}

- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsFromStoreAndForward {
    return [_damageReportSendTable getAllDamageReports];
}

- (NSNumber *)getLastDamageReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_damageReportReceiveTable getLastReportTimestampForIncidentId:incidentId];
}


#pragma mark Field Report History/Store & Forward
- (BOOL)addFieldReportsToHistory:(NSArray<FieldReportPayload> *) payloadArray {
    return [_fieldReportReceiveTable addDataArray:payloadArray];
}

- (BOOL)addFieldReportToHistory:(FieldReportPayload *) payload {
    return [_fieldReportReceiveTable addData: payload];
}

- (BOOL)addFieldReportsToStoreAndForward:(NSArray<FieldReportPayload> *) payloadArray {
    return [_fieldReportSendTable addDataArray:payloadArray];
}

- (BOOL)addFieldReportToStoreAndForward:(FieldReportPayload *) payload {
    return [_fieldReportSendTable addData: payload];
}

- (void)deleteFieldReportFromStoreAndForward:(FieldReportPayload *) payload {
    [_fieldReportSendTable removeData: payload];
}

- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    NSMutableArray<FieldReportPayload>* temp = [_fieldReportReceiveTable getFieldReportsForIncidentId:incidentId since:timestamp];
    [temp addObjectsFromArray:[_fieldReportSendTable getFieldReportsForIncidentId:incidentId since:timestamp]];
    
    [temp sortUsingComparator:^NSComparisonResult(FieldReportPayload *obj1, FieldReportPayload *obj2) {
        return [obj2.seqtime compare:obj1.seqtime];
    }];
    
    return temp;
}

- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsFromStoreAndForward {
    return [_fieldReportSendTable getAllFieldReports];
}

- (NSNumber *)getLastFieldReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_fieldReportReceiveTable getLastReportTimestampForIncidentId:incidentId];
}


#pragma mark Resource Request History/Store & Forward
- (BOOL)addResourceRequestsToHistory:(NSArray<ResourceRequestPayload> *) payloadArray {
    return [_resourceRequestReceiveTable addDataArray:payloadArray];
}

- (BOOL)addResourceRequestToHistory:(ResourceRequestPayload *) payload {
    return [_resourceRequestReceiveTable addData: payload];
}

- (BOOL)addResourceRequestsToStoreAndForward:(NSArray<ResourceRequestPayload> *) payloadArray {
    return [_resourceRequestSendTable addDataArray:payloadArray];
}

- (BOOL)addResourceRequestToStoreAndForward:(ResourceRequestPayload *) payload {
    return [_resourceRequestSendTable addData: payload];
}

- (void)deleteResourceRequestFromStoreAndForward:(ResourceRequestPayload *) payload {
    [_resourceRequestSendTable removeData: payload];
}

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsFromStoreAndForward {
    return [_resourceRequestSendTable getAllResourceRequests];
}

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsForIncidentId: (NSNumber *)incidentId {
    return [_resourceRequestReceiveTable getResourceRequestsForIncidentId:incidentId since:0];
}

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {

    NSMutableArray<ResourceRequestPayload>* temp = [_resourceRequestReceiveTable getResourceRequestsForIncidentId:incidentId since:timestamp];
    [temp addObjectsFromArray:[_resourceRequestSendTable getResourceRequestsForIncidentId:incidentId since:timestamp]];
    
    [temp sortUsingComparator:^NSComparisonResult(ResourceRequestPayload *obj1, ResourceRequestPayload *obj2) {
        return [obj2.seqtime compare:obj1.seqtime];
    }];
    
    return temp;
}

- (NSNumber *)getLastResourceRequestTimestampForIncidentId: (NSNumber *) incidentId {
    return [_resourceRequestReceiveTable getLastResourceRequestTimestampForIncidentId:incidentId];
}


#pragma mark Simple Report History/Store & Forward
- (BOOL)addSimpleReportsToHistory:(NSArray<SimpleReportPayload> *) payloadArray {
    return [_simpleReportReceiveTable addDataArray:payloadArray];
}

- (BOOL)addSimpleReportToHistory:(SimpleReportPayload *) payload {
    return [_simpleReportReceiveTable addData: payload];
}

- (BOOL)addSimpleReportsToStoreAndForward:(NSArray<SimpleReportPayload> *) payloadArray {
    return [_simpleReportSendTable addDataArray:payloadArray];
}

- (BOOL)addSimpleReportToStoreAndForward:(SimpleReportPayload *) payload {
    return [_simpleReportSendTable addData: payload];
}

- (void)deleteSimpleReportFromStoreAndForward:(SimpleReportPayload *) payload {
    [_simpleReportSendTable removeData: payload];
}
- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    
    NSMutableArray<SimpleReportPayload>* temp = [_simpleReportReceiveTable getSimpleReportsForIncidentId:incidentId since:timestamp];
    [temp addObjectsFromArray:[_simpleReportSendTable getSimpleReportsForIncidentId:incidentId since:timestamp]];
    
    [temp sortUsingComparator:^NSComparisonResult(SimpleReportPayload *obj1, SimpleReportPayload *obj2) {
        return [obj2.seqtime compare:obj1.seqtime];
    }];
    
    return temp;
}

- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsFromStoreAndForward {
    return [_simpleReportSendTable getAllSimpleReports];
}

- (NSNumber *)getLastSimpleReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_simpleReportReceiveTable getLastReportTimestampForIncidentId:incidentId];
}

#pragma mark Weather Report History/Store & Forward
- (BOOL)addWeatherReportsToHistory:(NSArray<WeatherReportPayload> *) payloadArray {
    return [_weatherReportReceiveTable addDataArray:payloadArray];
}

- (BOOL)addWeatherReportToHistory:(WeatherReportPayload *) payload {
    return [_weatherReportReceiveTable addData: payload];
}

- (BOOL)addWeatherReportsToStoreAndForward:(NSArray<WeatherReportPayload> *) payloadArray {
    return [_weatherReportSendTable addDataArray:payloadArray];
}

- (BOOL)addWeatherReportToStoreAndForward:(WeatherReportPayload *) payload {
    return [_weatherReportSendTable addData: payload];
}

- (void)deleteWeatherReportFromStoreAndForward:(WeatherReportPayload *) payload {
    [_weatherReportSendTable removeData: payload];
}

- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    NSMutableArray<WeatherReportPayload>* temp = [_weatherReportReceiveTable getWeatherReportsForIncidentId:incidentId since:timestamp];
    [temp addObjectsFromArray:[_weatherReportSendTable getWeatherReportsForIncidentId:incidentId since:timestamp]];
    
    [temp sortUsingComparator:^NSComparisonResult(WeatherReportPayload *obj1, WeatherReportPayload *obj2) {
        return [obj2.seqtime compare:obj1.seqtime];
    }];
    
    return temp;
}

- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsFromStoreAndForward {
    return [_weatherReportSendTable getAllWeatherReports];
}

- (NSNumber *)getLastWeatherReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_weatherReportReceiveTable getLastReportTimestampForIncidentId:incidentId];
}



#pragma mark Markup Features History/Store & Forward
- (BOOL)addMarkupFeaturesToHistory:(NSArray<MarkupFeature> *) payloadArray {
    return [_markupReceiveTable addDataArray:payloadArray];
}

- (BOOL)addMarkupFeatureToHistory:(MarkupFeature *) payload {
    return [_markupReceiveTable addData: payload];
}

- (BOOL)addMarkupFeaturesToStoreAndForward:(NSArray<MarkupFeature> *) payloadArray {
    return [_markupSendTable addDataArray:payloadArray];
}

- (BOOL)addMarkupFeatureToStoreAndForward:(MarkupFeature *) payload {
    return [_markupSendTable addData: payload];
}

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    return [_markupReceiveTable getMarkupFeaturesForCollabroomId:collabroomId since:timestamp];
}

- (NSNumber *)getLastMarkupFeatureTimestampForCollabroomId: (NSNumber *) collabroomId {
    return [_markupReceiveTable getLastMarkupFeatureTimestampForCollabroomId:collabroomId];
}

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForwardForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    return [_markupSendTable getMarkupFeaturesForCollabroomId:collabroomId since:timestamp];
}

-(void) removeAllFeaturesInCollabroom:(NSNumber*)collabRoomId{
    [_markupReceiveTable removeAllFeaturesInCollabroom: collabRoomId];
}

- (void)deleteMarkupFeatureFromStoreAndForward:(MarkupFeature *)feature{
    [_markupSendTable removeData: feature];
}

- (void)deleteMarkupFeatureFromStoreAndForwardByFeatureId:(NSString *)featureId{
    [_markupSendTable removeDataByFeatureId: featureId];
}

- (void)deleteMarkupFeatureFromReceiveTableByFeatureId:(NSString *)featureId{
    [_markupReceiveTable removeDataByFeatureId: featureId];
}

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForward{
    return [_markupSendTable getAllMarkupFeatures];
}

- (BOOL)addPersonalLogMessage:(ChatPayload *) payload {
    return [_personalLogTable addData: payload];
}
@end

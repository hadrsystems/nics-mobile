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
/**
 *
 */
//
//  DatabaseManager.h
//  nics_iOS
//
//

#import <Foundation/Foundation.h>
#import "FMDatabase.h"
#import "FMDatabaseQueue.h"
#import "ChatTable.h"
#import "DamageReportTable.h"
#import "FieldReportTable.h"
#import "ResourceRequestTable.h"
#import "ResourceRequestPayload.h"
#import "SimpleReportTable.h"
#import "WeatherReportTable.h"
#import "MarkupTable.h"
#import "MDTTable.h"

@interface DatabaseManager : NSObject

@property FMDatabase *database;
@property FMDatabaseQueue *databaseQueue;

@property ChatTable *chatReceiveTable;
@property ChatTable *chatSendTable;

@property ChatTable *personalLogTable;

@property DamageReportTable *damageReportReceiveTable;
@property DamageReportTable *damageReportSendTable;

@property FieldReportTable *fieldReportReceiveTable;
@property FieldReportTable *fieldReportSendTable;

@property ResourceRequestTable *resourceRequestReceiveTable;
@property ResourceRequestTable *resourceRequestSendTable;

@property SimpleReportTable *simpleReportReceiveTable;
@property SimpleReportTable *simpleReportSendTable;

@property WeatherReportTable *weatherReportReceiveTable;
@property WeatherReportTable *weatherReportSendTable;

@property MarkupTable *markupReceiveTable;
@property MarkupTable *markupSendTable;

@property MDTTable *mdtSendTable;


-(void)ClearAllLocalDatabases;

#pragma mark Chat Message History/Store & Forward
- (BOOL)addChatMessagesToHistory:(NSArray<ChatPayload> *) payloadArray;
- (BOOL)addChatMessageToHistory:(ChatPayload *) payload;

- (BOOL)addChatMessagesToStoreAndForward:(NSArray<ChatPayload> *) payloadArray;
- (BOOL)addChatMessageToStoreAndForward:(ChatPayload *) payload;

- (void)deleteChatMessageFromStoreAndForward:(ChatPayload *) payload;
- (void)deleteAllChatMessageFromRecieveTable;

- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId;
- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *) timestamp;
- (NSMutableArray<ChatPayload> *)getAllChatMessagesFromStoreAndForward;


- (NSNumber *)getLastChatMessageTimestampForCollabroomId: (NSNumber *) collabroomId;


#pragma mark Damage Report History/Store & Forward
- (BOOL)addDamageReportsToHistory:(NSArray<DamageReportPayload> *) payloadArray;
- (BOOL)addDamageReportToHistory:(DamageReportPayload *) payload;

- (BOOL)addDamageReportsToStoreAndForward:(NSArray<DamageReportPayload> *) payloadArray;
- (BOOL)addDamageReportToStoreAndForward:(DamageReportPayload *) payload;

- (void)deleteDamageReportFromStoreAndForward:(DamageReportPayload *) payload;

- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *) timestamp;
- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsFromStoreAndForward;

- (NSNumber *)getLastDamageReportTimestampForIncidentId: (NSNumber *) incidentId;


#pragma mark Field Report History/Store & Forward
- (BOOL)addFieldReportsToHistory:(NSArray<FieldReportPayload> *) payloadArray;
- (BOOL)addFieldReportToHistory:(FieldReportPayload *) payload;

- (BOOL)addFieldReportsToStoreAndForward:(NSArray<FieldReportPayload> *) payloadArray;
- (BOOL)addFieldReportToStoreAndForward:(FieldReportPayload *) payload;

- (void)deleteFieldReportFromStoreAndForward:(FieldReportPayload *) payload;

- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *) timestamp;
- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsFromStoreAndForward;

- (NSNumber *)getLastFieldReportTimestampForIncidentId: (NSNumber *) incidentId;


#pragma mark Resource Request History/Store & Forward
- (BOOL)addResourceRequestsToHistory:(NSArray<ResourceRequestPayload> *) payloadArray;
- (BOOL)addResourceRequestToHistory:(ResourceRequestPayload *) payload;

- (BOOL)addResourceRequestsToStoreAndForward:(NSArray<ResourceRequestPayload> *) payloadArray;
- (BOOL)addResourceRequestToStoreAndForward:(ResourceRequestPayload *) payload;

- (void)deleteResourceRequestFromStoreAndForward:(ResourceRequestPayload *) payload;

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsForIncidentId: (NSNumber *)incidentId since: (NSNumber *) timestamp;
- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsFromStoreAndForward;

- (NSNumber *)getLastResourceRequestTimestampForIncidentId: (NSNumber *) incidentId;

#pragma mark Simple Report History/Store & Forward
- (BOOL)addSimpleReportsToHistory:(NSArray<SimpleReportPayload> *) payloadArray;
- (BOOL)addSimpleReportToHistory:(SimpleReportPayload *) payload;

- (BOOL)addSimpleReportsToStoreAndForward:(NSArray<SimpleReportPayload> *) payloadArray;
- (BOOL)addSimpleReportToStoreAndForward:(SimpleReportPayload *) payload;

- (void)deleteSimpleReportFromStoreAndForward:(SimpleReportPayload *) payload;

- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *) timestamp;
- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsFromStoreAndForward;

- (NSNumber *)getLastSimpleReportTimestampForIncidentId: (NSNumber *) incidentId;

#pragma mark Weather Report History/Store & Forward
- (BOOL)addWeatherReportsToHistory:(NSArray<WeatherReportPayload> *) payloadArray;
- (BOOL)addWeatherReportToHistory:(WeatherReportPayload *) payload;

- (BOOL)addWeatherReportsToStoreAndForward:(NSArray<WeatherReportPayload> *) payloadArray;
- (BOOL)addWeatherReportToStoreAndForward:(WeatherReportPayload *) payload;

- (void)deleteWeatherReportFromStoreAndForward:(WeatherReportPayload *) payload;

- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *) timestamp;
- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsFromStoreAndForward;

- (NSNumber *)getLastWeatherReportTimestampForIncidentId: (NSNumber *) incidentId;


#pragma mark Markup Features History/Store & Forward
- (BOOL)addMarkupFeaturesToHistory:(NSArray<MarkupFeature> *) payloadArray;
- (BOOL)addMarkupFeatureToHistory:(MarkupFeature *) payload;

- (BOOL)addMarkupFeaturesToStoreAndForward:(NSArray<MarkupFeature> *) payloadArray;
- (BOOL)addMarkupFeatureToStoreAndForward:(MarkupFeature *) payload;

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *) timestamp;
- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForward;
- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForwardForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *) timestamp;
- (void)deleteMarkupFeatureFromStoreAndForward:(MarkupFeature *) feature;
- (void)deleteMarkupFeatureFromStoreAndForwardByFeatureId:(NSString *)featureId;
- (void)deleteMarkupFeatureFromReceiveTableByFeatureId:(NSString *)featureId;
- (NSNumber *)getLastMarkupFeatureTimestampForCollabroomId: (NSNumber *) collabroomId;
- (void) removeAllFeaturesInCollabroom:(NSNumber*)collabRoomId;



- (BOOL)addPersonalLogMessage:(ChatPayload *) payload;
@end

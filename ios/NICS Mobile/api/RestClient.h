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
//  RestClient.h
//  nics_iOS
//
//

#import <Foundation/Foundation.h>
#import "JSONModel.h"
#import "JSONModelLib.h"

#import "AssignmentMessage.h"
#import "ChatMessage.h"
#import "IncidentMessage.h"
#import "LoginMessage.h"
#import "MarkupMessage.h"
#import "CollaborationRoomMessage.h"
#import "SimpleReportMessage.h"
#import "DamageReportMessage.h"
#import "FieldReportMessage.h"
#import "WeatherReportMessage.h"
#import "ResourceRequestMessage.h"
#import "TrackingLayerMessage.h"
#import "UserMessage.h"
#import "OrganizationMessage.h"
#import "WeatherPayload.h"
#import "OrganizationPayload.h"
#import "MDTPayload.h"
#import "WfsFeature.h"
#import "ActiveWfsLayerManager.h"
#import "openAmAuth.h"
#import "MultipartPostQueue.h"
#import "WfsXmlParser.h";

#import "DataManager.h"

@interface RestClient : NSObject

+ (NSString *) authValue;
+ (void) setAuthValue:(NSString *) authValue;

+ (NSString *) synchronousGetFromUrl:(NSString *)url statusCode:(NSInteger *)statusCode;
+ (NSString *) synchronousPostToUrl:(NSString *)url postData:(NSData *)postData length:(NSUInteger)length statusCode:(NSInteger *)statusCode;
+ (NSURLConnection *) synchronousMultipartPostToUrl:(NSString *)url postData:(NSData *)postData imageName:(NSString *)imageName requestParams:(NSMutableDictionary *)requestParams statusCode:(NSInteger *)statusCode;

+ (void) loginUser:(NSString*)username password:(NSString*) password completion:(void (^)(BOOL successful , NSString *msg)) completion;

+ (NSString *) logoutUser:(NSString*)username;
+ (void) getAllIncidentsForUserId:(NSNumber *)userId;
+ (void) getActiveAssignmentForUser:(NSString *)username userId:(NSNumber *)userId activeOnly:(BOOL) activeOnly completion:(void (^)(BOOL successful)) completion;

+ (void) getWeatherUpdateForLatitude:(double)latitude longitude:(double) longitude completion:(void (^)(BOOL successful)) completion;

+(void) getCollabroomsForIncident:(IncidentPayload*)incident offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL successful)) completion;

+ (void) getIncidentForId:(NSNumber *)incidentId isWorkingMap:(BOOL) isWorkingMap userId:(NSNumber *)userId sendAssignmentUpdate:(BOOL) sendAssignmentUpdate;

+ (void) getUserCollabroomsForIncidentId:(NSNumber *)incidentId userId:(NSNumber *)userId;

+ (void) getChatMessagesForCollabroomId:(NSNumber *)collabRoomId completion:(void (^)(BOOL successful))completion;

+ (void) getSimpleReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL successful)) completion;
+ (void) getFieldReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL successful)) completion;
+ (void) getDamageReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL successful)) completion;
+ (void) getResourceRequestsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL successful)) completion;
+ (void) getWeatherReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL successful)) completion;

+ (void) getMarkupHistoryForCollabroomId:(NSNumber *)collabRoomId completion:(void (^)(BOOL successful)) completion;
+ (void)postMapMarkupFeatures;
+ (NSString *) deleteMarkupFeatureById:(NSString *)featureId;

+ (void) getWFSDataLayers;
+ (void) getWfsDataForLayer: (TrackingLayerPayload*) layer;
+ (void) getActiveWFSData;
+ (void) getWfsDataToken: (TrackingLayerPayload*) layer;
+ (void) getUserOrgs:(NSNumber*) userId;

+ (void)postSimpleReports;
+ (void)postFieldReports;
+ (void)postDamageReports;
+ (void)postResourceRequests;
+ (void)postWeatherReports;
+ (void)postChatMessages;
+ (void)postMDTs:(MDTPayload *) payload;

+ (void)setSendingSimpleReport:(BOOL)value;
//+ (void)setBaseUrl:(NSString *)server;
//+ (NSString*)getBaseUrl;

@end

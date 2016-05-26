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
//  DataManager.h
//  nics_iOS
//
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import "RestClient.h"
#import "DatabaseManager.h"
#import "DatabaseTable.h"
#import "ActiveWfsLayerManager.h"
#import "OrganizationPayload.h"
#import <SecureNSUserDefaults/NSUserDefaults+SecureAdditions.h>

@interface DataManager : NSObject <CLLocationManagerDelegate>

#define SYSTEM_VERSION_EQUAL_TO(v)                  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedSame)
#define SYSTEM_VERSION_GREATER_THAN(v)              ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedDescending)
#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)
#define SYSTEM_VERSION_LESS_THAN(v)                 ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedAscending)
#define SYSTEM_VERSION_LESS_THAN_OR_EQUAL_TO(v)     ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedDescending)


@property IncidentPayload *currentIncident;
@property AssignmentPayload *currentAssignment;
@property UserPayload *userData;
@property OrganizationPayload *orgData;
@property (nonatomic) LoginPayload *loginSessionData;
@property NSNotificationCenter *notificationCenter;

@property NSTimer *assignmentsPollingTimer;
@property NSTimer *chatMessagesPollingTimer;
@property NSTimer *damageReportPollingTimer;
@property NSTimer *fieldReportPollingTimer;
@property NSTimer *weatherReportPollingTimer;
@property NSTimer *markupFeaturesPollingTimer;
@property NSTimer *resourceRequestPollingTimer;
@property NSTimer *simpleReportPollingTimer;
@property NSTimer *wfsPollingTimer;

@property NSUserDefaults *userPreferences;

@property DatabaseManager *databaseManager;
@property CLLocationManager *locationManager;
@property CLLocation *currentLocation;

@property NSMutableDictionary *incidentsList;
@property NSMutableDictionary *collabRoomList;

@property UINavigationController *OverviewViewController;
@property double mapSelectedLatitude;
@property double mapSelectedLongitude;

@property bool isIpad;
@property bool isLoggedIn;
@property bool appSuspended;
@property BOOL TrafficDisplay;
@property BOOL IndoorDisplay;
@property int CurrentMapType;
@property NSString *currentCookieDomain;

+(id) getInstance;

-(void)AppHasBeenSuspended:(NSNotification*)_notification;
-(void)AppHasBeenResumed:(NSNotification*)_notification;

- (void)registerDefaultsFromSettingsBundle;
- (void)ResetLocalUserData;

#pragma mark Polling Requests
- (void) disableAllPollingTimers;
- (void) requestActiveAssignmentRepeatedEvery:(int)seconds;
- (void) requestChatMessagesRepeatedEvery:(int)seconds immediate:(BOOL)immediate;
- (void) requestDamageReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate;
- (void) requestFieldReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate;
- (void) requestWeatherReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate;
- (void) requestMarkupFeaturesRepeatedEvery:(int)seconds immediate:(BOOL)immediate;
- (void) requestResourceRequestsRepeatedEvery:(int)seconds immediate:(BOOL)immediate;
- (void) requestSimpleReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate;
- (void) requestMdtRepeatedEvery:(int)seonds immediate:(BOOL)immediate;
- (void) requestWfsUpdateRepeatedEvery:(int)seonds immediate:(BOOL)immediate;
- (void) requestCollabroomsForIncident:(IncidentPayload*)incident;

- (void) requestWfsUpdate;

#pragma mark Chat Message History/Store & Forward
- (BOOL)addChatMessageToHistory:(ChatPayload *) payload;
- (BOOL)addChatMessagesToHistory:(NSArray<ChatPayload> *) payloadArray;

- (BOOL)addChatMessageToStoreAndForward:(ChatPayload *) payload;
- (BOOL)addChatMessagesToStoreAndForward:(NSArray<ChatPayload> *) payloadArray;

- (void)deleteAllChatMessageFromRecieve;
- (void)deleteChatMessageFromStoreAndForward:(ChatPayload *) payload;

- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId;
- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp;
- (NSMutableArray<ChatPayload> *)getAllChatMessagesFromStoreAndForward;

- (NSNumber *)getLastChatMessageTimestampForCollabroomId: (NSNumber *) collabroomId;


#pragma mark Damage Report History/Store & Forward
- (BOOL)addDamageReportToHistory:(DamageReportPayload *) payload;
- (BOOL)addDamageReportsToHistory:(NSArray<DamageReportPayload> *) payloadArray;

- (BOOL)addDamageReportToStoreAndForward:(DamageReportPayload *) payload;
- (BOOL)addDamageReportsToStoreAndForward:(NSArray<DamageReportPayload> *) payloadArray;

- (void)deleteDamageReportFromStoreAndForward:(DamageReportPayload *) payload;

- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsForIncidentId: (NSNumber *)incidentId;
- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp;
- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsFromStoreAndForward;

- (NSNumber *)getLastDamageReportTimestampForIncidentId: (NSNumber *) collabroomId;


#pragma mark Field Report History/Store & Forward
- (BOOL)addFieldReportToHistory:(FieldReportPayload *) payload;
- (BOOL)addFieldReportsToHistory:(NSArray<FieldReportPayload> *) payloadArray;

- (BOOL)addFieldReportToStoreAndForward:(FieldReportPayload *) payload;
- (BOOL)addFieldReportsToStoreAndForward:(NSArray<FieldReportPayload> *) payloadArray;

- (void)deleteFieldReportFromStoreAndForward:(FieldReportPayload *) payload;

- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsForIncidentId: (NSNumber *)incidentId;
- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp;
- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsFromStoreAndForward;

- (NSNumber *)getLastFieldReportTimestampForIncidentId: (NSNumber *) collabroomId;


#pragma mark Resource Request History/Store & Forward
- (BOOL)addResourceRequestsToHistory:(NSArray<ResourceRequestPayload> *) payloadArray;
- (BOOL)addResourceRequestToHistory:(ResourceRequestPayload *) payload;

- (BOOL)addResourceRequestsToStoreAndForward:(NSArray<ResourceRequestPayload> *) payloadArray;
- (BOOL)addResourceRequestToStoreAndForward:(ResourceRequestPayload *) payload;

- (void)deleteResourceRequestFromStoreAndForward:(ResourceRequestPayload *) payload;

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsForIncidentId: (NSNumber *)incidentId;
- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsForIncidentId: (NSNumber *)incidentId since: (NSNumber *) timestamp;
- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsFromStoreAndForward;

- (NSNumber *)getLastResourceRequestTimestampForIncidentId: (NSNumber *) incidentId;


#pragma mark Simple Report History/Store & Forward
- (BOOL)addSimpleReportsToHistory:(NSArray<SimpleReportPayload> *) payloadArray;
- (BOOL)addSimpleReportToHistory:(SimpleReportPayload *) payload;

- (BOOL)addSimpleReportsToStoreAndForward:(NSArray<SimpleReportPayload> *) payloadArray;
- (BOOL)addSimpleReportToStoreAndForward:(SimpleReportPayload *) payload;

- (void)deleteSimpleReportFromStoreAndForward:(SimpleReportPayload *) payload;

- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsForIncidentId: (NSNumber *)incidentId;
- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *) timestamp;
- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsFromStoreAndForward;

- (NSNumber *)getLastSimpleReportTimestampForIncidentId: (NSNumber *) incidentId;

#pragma mark Weather Report History/Store & Forward
- (BOOL)addWeatherReportToHistory:(WeatherReportPayload *) payload;
- (BOOL)addWeatherReportsToHistory:(NSArray<WeatherReportPayload> *) payloadArray;

- (BOOL)addWeatherReportToStoreAndForward:(WeatherReportPayload *) payload;
- (BOOL)addWeatherReportsToStoreAndForward:(NSArray<WeatherReportPayload> *) payloadArray;

- (void)deleteWeatherReportFromStoreAndForward:(WeatherReportPayload *) payload;

- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsForIncidentId: (NSNumber *)incidentId;
- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp;
- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsFromStoreAndForward;

- (NSNumber *)getLastWeatherReportTimestampForIncidentId: (NSNumber *) collabroomId;

#pragma mark Markup Features History/Store & Forward
- (BOOL)addMarkupFeaturesToHistory:(NSArray<MarkupFeature> *) payloadArray;
- (BOOL)addMarkupFeatureToHistory:(MarkupFeature *) payload;

- (BOOL)addMarkupFeaturesToStoreAndForward:(NSArray<MarkupFeature> *) payloadArray;
- (BOOL)addMarkupFeatureToStoreAndForward:(MarkupFeature *) payload;

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId;
- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *) timestamp;
- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForward;
- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForwardForCollabroomId:(NSNumber *)collabroomId;

- (void)deleteMarkupFeatureFromStoreAndForward:(MarkupFeature *) feature;
- (void)deleteMarkupFeatureFromStoreAndForwardByFeatureId:(NSString *)featureId;
- (void)deleteMarkupFeatureFromReceiveTableByFeatureId:(NSString *)featureId;

- (NSNumber *)getLastMarkupFeatureTimestampForCollabroomId: (NSNumber *) collabroomId;

- (NSString *)deleteMarkupFeatureById:(NSString *) featureId;
- (void) removeAllFeaturesInCollabroom:(NSNumber*)collabRoomId;




- (BOOL)addPersonalLogMessage:(ChatPayload *) payload;


- (NSString *)getUsername;
- (NSNumber *)getUserId;
- (NSNumber *)getUserSessionId;

- (NSNumber *)getActiveWorkspaceId;
- (NSNumber *)getActiveIncidentId;
- (NSString *)getActiveIncidentName;

- (NSNumber *)getActiveCollabroomId;
- (NSString *)getActiveCollabroomName;

- (NSNumber *)getSelectedCollabroomId;
- (NSString *)getSelectedCollabroomName;

- (void)setAuthToken:(NSString *)authToken;
- (NSString *)getAuthToken;

- (BOOL)getRememberUser;
- (BOOL)getAutoLogin;
- (NSString *)getPassword;

- (void)setActiveIncident:(IncidentPayload *)incident;
- (void)setCurrentIncident:(IncidentPayload *)incident collabRoomId:(NSNumber *)collabRoomId collabRoomName:(NSString *)collabRoomName;
- (void)setSelectedCollabRoomId:(NSNumber *)collabRoomId collabRoomName:(NSString *)collabRoomName;
- (void)setActiveWorkspaceId:(NSNumber *)workspaceId;

- (void)setRememberUser:(BOOL)value;
- (void)setAutoLogin:(BOOL)value;
- (void)setUserName:(NSString *)userName;
- (void)setPassword:(NSString *)password;

- (void)addCollabroom:(CollabroomPayload *) payload;
- (void)clearCollabRoomList;

- (NSMutableDictionary *)getIncidentsList;
- (NSMutableDictionary *)getCollabroomList;
- (NSMutableDictionary *)getCollabroomNamesList;
- (NSMutableArray *)getCollabroomPayloadArray;
- (CollabroomPayload *)getActiveCollabroomPayload;

- (NSString *)getServerFromSettings;
- (NSString *)getAuthServerFromSettings;
- (NSString *)getGeoServerFromSettings;
- (NSString *)getCookieDomainForCurrentServer;
- (bool)getUseCustomServerFromSettings;

+ (NSNumber *)getChatUpdateFrequencyFromSettings;
+ (NSNumber *)getMapUpdateFrequencyFromSettings;
+ (NSNumber *)getReportsUpdateFrequencyFromSettings;
+ (int)getMdtUpdateFrequencyFromSettings;
+ (NSNumber *)getWfsUpdateFrequencyFromSettings;
+ (bool)getCalTrackingEnabledFromSettings;

- (bool)getTrackingLayerEnabled: (NSString*) layerDisplayName;
- (void)setTrackingLayerEnabled: (NSString*) layerDisplayName : (bool)enabled;

-(void)setOverviewController:(UINavigationController *)controller;
-(UINavigationController*)getOverviewController;

-(void)setIsIpad:(BOOL)setting;
-(BOOL)getIsIpad;
@end

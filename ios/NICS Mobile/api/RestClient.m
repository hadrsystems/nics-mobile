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
//  RestClient.m
//  Phinics_iOS
//
//

#import "RestClient.h"

@implementation RestClient

static BOOL firstRun = YES;

static BOOL receivingSimpleReports = NO;
static BOOL receivingDamageReports = NO;
static BOOL receivingFieldReports = NO;
static BOOL receivingResourceRequests = NO;
static BOOL receivingUxoReports = NO;
static BOOL receivingChatMessages = NO;
static BOOL receivingMapMarkupFeatures = NO;
static BOOL receivingWfsFeatures = NO;
static BOOL sendingSimpleReports = NO;
static BOOL sendingFieldReports = NO;
static BOOL sendingDamageReports = NO;
static BOOL sendingResourceRequests = NO;
static BOOL sendingUxoReports = NO;
static BOOL sendingChatMessages = NO;
static BOOL sendingMDTs = NO;
//static BOOL sendingMapMarkupFeatures = NO;

static NSString* authValue;
static NSString* BASE_URL = nil;
static NSNotificationCenter *notificationCenter;
static DataManager *dataManager;
static openAmAuth *myOpenAmAuth;
static MultipartPostQueue* mMultipartPostQueue;

+ (void) initialize {
    
    notificationCenter = [NSNotificationCenter defaultCenter];
    dataManager = [DataManager getInstance];
    mMultipartPostQueue = [MultipartPostQueue getInstance];
    myOpenAmAuth = [[openAmAuth alloc] init];
}

+ (NSString *) synchronousGetFromUrl:(NSString *)url statusCode:(NSInteger *)statusCode {

    return [myOpenAmAuth synchronousGetFromUrl:url statusCode:statusCode];
}

+ (NSString *) synchronousPostToUrl:(NSString *)url postData:(NSData *)postData length:(NSUInteger)length statusCode:(NSInteger *)statusCode {
    
        return [myOpenAmAuth synchronousPostToUrl:url postData:postData length:length statusCode:statusCode];
}

+ (NSURLConnection *) synchronousMultipartPostToUrl:(NSString *)url postData:(NSData *)postData imageName:(NSString *)imageName requestParams:(NSMutableDictionary *)requestParams statusCode:(NSInteger *)statusCode {
    
        return [myOpenAmAuth synchronousMultipartPostToUrl:url postData:postData imageName:imageName requestParams:requestParams statusCode:statusCode];
}

//initializes auth type
//logs user in
+(void) loginUser:(NSString*)username password:(NSString*) password completion:(void (^)(BOOL successful,NSString* msg)) completion {
    NSLog(@"Attempting to log-in");
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            
            bool authenticated = [myOpenAmAuth setupAuth:username :password];
            
            if(!authenticated){
                if([myOpenAmAuth.OpenAmResponse isEqualToString:@""]){
                    completion(false,NSLocalizedString(@"Could not find server: Make sure device has an internet connection.", nil));
                }else{
                    completion(false,myOpenAmAuth.OpenAmResponse);
                }
                return;
            }
            
            LoginPayload *payload = [[LoginPayload alloc] init];
            payload.username = username;
            NSString *jsonString = [payload toJSONString];
            NSData *postData = [NSData dataWithBytes:[jsonString UTF8String] length:[jsonString length]];
            
            NSInteger statusCode = -1;
            NSString* loginResult = [self synchronousPostToUrl:@"login" postData:postData length:[jsonString length] statusCode:&statusCode];
            
            if(statusCode == 412) {
                NSLog(@"User already logged in...");
                [self logoutUser: username];
                [self loginUser:username password:password completion:completion];
             
            }else if(statusCode == 204){
                NSLog(@"NICS6 usersession not cleared...");
                [self logoutUser: username];
                [self loginUser:username password:password completion:completion];
                
                
            } else if(statusCode == 200 || statusCode == 201){
                NSLog(@"Successfully logged in...");
                LoginMessage *loginResultMessage = [[LoginMessage alloc] initWithString:loginResult error:nil];
                
                LoginPayload *payload = [loginResultMessage.logins objectAtIndex:0];
                payload.workspaceId = [dataManager getActiveWorkspaceId];
                
                [dataManager setLoginSessionData:payload];
                [self getAllIncidentsForUserId: payload.userId];
                [self getUserDataById: payload.userId];
                [self getUserOrgs: payload.userId];
                completion(true,NSLocalizedString(@"Success", nil));
            }else{
               completion(false, [NSString stringWithFormat: @"%ld", statusCode] );
            }
            
        });
}

+(NSString *) logoutUser:(NSString *)username {
    NSLog(@"Logging out user...");
    NSURL* postUrl = [NSURL URLWithString:[NSString stringWithFormat: @"%@%@%@", BASE_URL, @"login/", username]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:postUrl];
    
    [request setValue:myOpenAmAuth.AuthValue forHTTPHeaderField:@"Authorization"];

    [request setHTTPMethod:@"DELETE"];
    
    NSHTTPURLResponse *response = nil;
    NSError *error = nil;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    
    return [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
}

+(void) getUserDataById:(NSNumber *)userId {
    NSInteger statusCode = -1;
    
    NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%@", @"users/", [dataManager getActiveWorkspaceId], @"/", userId] statusCode:&statusCode];
    NSError* error = nil;
    
    UserMessage *message = [[UserMessage alloc] initWithString:json error:&error];
    UserPayload *userData = [message.users objectAtIndex:0];
    
    if(dataManager == nil){
        dataManager = [DataManager getInstance];
    }
    
    dataManager.userData = userData;
    
    json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%@", @"orgs/", [dataManager getActiveWorkspaceId], @"?userId=", userId] statusCode:&statusCode];
    OrganizationMessage *orgMessage = [[OrganizationMessage alloc] initWithString:json error:&error];
    if( [orgMessage.organizations objectAtIndex:0] != nil){
        [dataManager setOrgData: orgMessage.organizations[0]];
    }
}


+(void) getUserCollabroomsForIncidentId:(NSNumber *)incidentId userId:(NSNumber *)userId {
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
    dispatch_async(queue, ^{
        NSInteger statusCode = -1;
        
        NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%@", @"collab/", incidentId, @"/rooms/", userId] statusCode:&statusCode];
        NSError* error = nil;
        
        CollaborationRoomMessage *message = [[CollaborationRoomMessage alloc] initWithString:json error:&error];
        
        if(message != nil) {
            for(CollabroomPayload *payload in message.results) {
                [dataManager addCollabroom:payload];
            }
        }
    });
}

+(void) getActiveAssignmentForUser:(NSString *)username userId:(NSNumber *)userId activeOnly:(BOOL) activeOnly completion:(void (^)(BOOL successful)) completion {
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
    dispatch_async(queue, ^{
        NSInteger statusCode = -1;
        
        NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%s", @"assignments/?username=", username, @"&activeOnly=", activeOnly ? "true" : "false"] statusCode:&statusCode];
        NSError* error = nil;
        
        AssignmentMessage *message = [[AssignmentMessage alloc] initWithString:json error:&error];
        [message parse];
        
        AssignmentPayload *currentAssignment = nil;
        if(message.count == 1) {
            currentAssignment = [message.taskingAssignmentsList firstObject];
            BOOL isCurrent = [dataManager.currentAssignment isEqual: currentAssignment];
            
            NSNumber* incidentId = currentAssignment.phiUnit.incidentId;
            if(!isCurrent) {
                [self getIncidentForId:incidentId isWorkingMap:NO userId:userId sendAssignmentUpdate:YES];
            } else {
                [self getIncidentForId:incidentId isWorkingMap:NO userId:userId sendAssignmentUpdate:NO];
            }
        } else {
            currentAssignment = [[AssignmentPayload alloc] init];
            
            if(firstRun || ((![dataManager.currentAssignment isNil] && ![currentAssignment isNil]) && !([dataManager.currentAssignment.phiOperationalPeriod isEqual:currentAssignment.phiOperationalPeriod]))) {
                [self getIncidentForId:@-1 isWorkingMap:YES userId:userId sendAssignmentUpdate: YES];
                firstRun = NO;
            } else {
                [self getIncidentForId:@-1 isWorkingMap:NO userId:userId sendAssignmentUpdate: NO];
            }
        }
        
        dataManager.currentAssignment = currentAssignment;
        
        completion(true);
    });
}

+(void) getCollabroomsForIncident:(IncidentPayload*)incident offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL))completion{
    

    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
    dispatch_async(queue, ^{
    
        NSInteger statusCode = -1;
        
        NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%@", @"collabroom/", incident.incidentid,@"?userId=",[dataManager getUserId]] statusCode:&statusCode];
        NSError* error = nil;
        
        CollaborationRoomMessage *message =  [[CollaborationRoomMessage alloc] initWithString:json error:&error];
        
        [dataManager clearCollabRoomList];
        
        if(message != nil && statusCode == 200) {
        
            for(CollabroomPayload *payload in message.results) {
                payload.name = [payload.name stringByReplacingOccurrencesOfString: [incident.incidentname stringByAppendingString:@"-"] withString:@""];    //pull incident name out of collab name in case it is there
                [dataManager addCollabroom:payload];
            }
            completion(YES);
        }else{
            completion(NO);
        }
    });
}

+(void) getAllIncidentsForUserId:(NSNumber *)userId {
    NSInteger statusCode = -1;
    
    NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%@", @"incidents/", [dataManager getActiveWorkspaceId], @"/?accessibleByUserId=", userId] statusCode:&statusCode];
    NSError* error = nil;
    
    IncidentMessage *message = [[IncidentMessage alloc] initWithString:json error:&error];
    
    if(message != nil && statusCode == 200) {
        NSMutableDictionary *incidentsList = [NSMutableDictionary new];
        for(IncidentPayload *payload in message.incidents) {
            [incidentsList setObject:payload forKey:payload.incidentname];
        }
        dataManager.incidentsList = incidentsList;
    }
}

+(void) getIncidentForId:(NSNumber *)incidentId isWorkingMap:(BOOL) isWorkingMap userId:(NSNumber *)userId sendAssignmentUpdate:(BOOL) sendAssignmentUpdate {
    NSInteger statusCode = -1;
    NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%@", @"incidents/", [dataManager getActiveWorkspaceId], @"/", incidentId] statusCode:&statusCode];
    NSError* error = nil;
    
    IncidentMessage *message = [[IncidentMessage alloc] initWithString:json error:&error];

    IncidentPayload *incident = [message.incidents objectAtIndex:0];
    
    NSNumber *collabRoomId = @-1;
    NSString *collabRoomName = @"";
    
    if(isWorkingMap) {
        for(CollabroomPayload *payload in incident.collabrooms) {
            if([payload.name rangeOfString:@"WorkingMap"].location != NSNotFound) {
                collabRoomId = payload.collabRoomId;
                collabRoomName = payload.name;
                payload.incidentid = incidentId;
                
                [dataManager addCollabroom:payload];
                
                if(sendAssignmentUpdate) {
                    [dataManager setCurrentIncident:incident collabRoomId:collabRoomId collabRoomName: collabRoomName];
                    [dataManager setSelectedCollabRoomId:collabRoomId collabRoomName:collabRoomName];
                }
            } else {
                [dataManager addCollabroom:payload];
            }
        }
    } else {
        collabRoomName = [dataManager getActiveCollabroomName];
    }
    [dataManager setCurrentIncident:incident collabRoomId:collabRoomId collabRoomName: collabRoomName];
    
    if(sendAssignmentUpdate && !isWorkingMap) {
        [dataManager clearCollabRoomList];
    }
    
//            [self getUserCollabroomsForIncidentId: incidentId userId:userId];
    
    if(sendAssignmentUpdate) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSNotification *activeAssignmentReceivedNotification = [NSNotification notificationWithName:@"assignmentUpdateReceived" object:message];
            [notificationCenter postNotification:activeAssignmentReceivedNotification];
        });
    }
}


+(void) getSimpleReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL))completion{
    if(!receivingSimpleReports && ![incidentId  isEqual: @-1]) {
        receivingSimpleReports = YES;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            NSInteger statusCode = -1;
            
            NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%lld%@%ld", @"reports/",
                                                        incidentId, @"/SR?sortOrder=desc&fromDate=", [[dataManager getLastSimpleReportTimestampForIncidentId:incidentId] longLongValue] + 1, @"&incidentId=", [incidentId longValue]] statusCode:&statusCode];
            NSError* error = nil;
            
            SimpleReportMessage *message = [[SimpleReportMessage alloc] initWithString:json error:&error];
            [message parse];
            
            if([message.reports count] > 0) {
                [dataManager addSimpleReportsToHistory:message.reports];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSNotification *simpleReportsReceivedNotification = [NSNotification notificationWithName:@"simpleReportsUpdateReceived" object:message];
                    [notificationCenter postNotification:simpleReportsReceivedNotification];
                });
            }
            
            receivingSimpleReports = NO;
            completion(YES);
        });
    }
}

+(void) postSimpleReports {
    NSMutableArray* simpleReports = [dataManager getAllSimpleReportsFromStoreAndForward];
    
    for(SimpleReportPayload *payload in simpleReports) {
        if([payload.isDraft isEqual:@0]) {
            [mMultipartPostQueue addPayloadToSendQueue:payload];
            break;
        }
    }
}

+(void) getFieldReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL))completion{
    if(!receivingFieldReports && ![incidentId  isEqual: @-1]) {
        receivingFieldReports = YES;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            NSInteger statusCode = -1;
            
            NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%lld%@%ld", @"reports/", [dataManager getActiveWorkspaceId], @"/FR?sortOrder=desc&fromDate=", [[dataManager getLastFieldReportTimestampForIncidentId:incidentId] longLongValue] + 1, @"&incidentId=", [incidentId longValue]] statusCode:&statusCode];
            NSError* error = nil;
            
            FieldReportMessage *message = [[FieldReportMessage alloc] initWithString:json error:&error];
            [message parse];
            
            if([message.reports count] > 0) {
                [dataManager addFieldReportsToHistory:message.reports];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSNotification *fieldReportsReceivedNotification = [NSNotification notificationWithName:@"fieldReportsUpdateReceived" object:message];
                    [notificationCenter postNotification:fieldReportsReceivedNotification];
                });
            }
            
            receivingFieldReports = NO;
            completion(YES);
        });
    }
}

+(void) postFieldReports {
    if(!sendingFieldReports) {
        sendingFieldReports = YES;
        NSMutableArray* fieldReports = [dataManager getAllFieldReportsFromStoreAndForward];
        
        for(FieldReportPayload *payload in fieldReports) {
            if([payload.isDraft isEqual:@0]) {
                NSString *jsonString = [payload toJSONStringPost];
                NSData *postData = [NSData dataWithBytes:[jsonString UTF8String] length:[jsonString length]];
                
                dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
                dispatch_async(queue, ^{

                    NSInteger statusCode = -1;
                    NSString* result = [self synchronousPostToUrl:[NSString stringWithFormat:@"%@%@%@", @"reports/", [dataManager getActiveWorkspaceId],  @"/FR"] postData:postData length:[jsonString length] statusCode:&statusCode];
                    
                    if(statusCode == 200 || statusCode == 201) {
                        [dataManager deleteFieldReportFromStoreAndForward:payload];
                        [dataManager requestFieldReportsRepeatedEvery:[DataManager getReportsUpdateFrequencyFromSettings] immediate:YES];
                    } else {
                        NSLog(@"%@%@", @"Failed to send Field Report...\n", result);
                    }
                });
            }
        }
        sendingFieldReports = NO;
    }
}


+(void) getDamageReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL))completion {
    if(!receivingDamageReports && ![incidentId  isEqual: @-1]) {
        receivingDamageReports = YES;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            NSInteger statusCode = -1;
            
            NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%lld%@%ld", @"reports/", [dataManager getActiveIncidentId], @"/DMGRPT?sortOrder=desc&fromDate=", [[dataManager getLastDamageReportTimestampForIncidentId:incidentId] longLongValue] + 1, @"&incidentId=", [incidentId longValue]] statusCode:&statusCode];
            NSError* error = nil;
            
            DamageReportMessage *message = [[DamageReportMessage alloc] initWithString:json error:&error];
            [message parse];
            
            if([message.reports count] > 0) {
                 [dataManager addDamageReportsToHistory:message.reports];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSNotification *DamageReportsReceivedNotification = [NSNotification notificationWithName:@"damageReportsUpdateReceived" object:message];
                    [notificationCenter postNotification:DamageReportsReceivedNotification];
                });
            }
            
            receivingDamageReports = NO;
            completion(YES);
        });
    }
}

+(void) postDamageReports {
    NSMutableArray* damageReports = [dataManager getAllDamageReportsFromStoreAndForward];
    
    for(DamageReportPayload *payload in damageReports) {
        if([payload.isDraft isEqual:@0]) {
            [mMultipartPostQueue addPayloadToSendQueue:payload];
            break;
        }
    }
}

+(void) getResourceRequestsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL))completion{
    if(!receivingResourceRequests && ![incidentId  isEqual: @-1]) {
        receivingResourceRequests = YES;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            NSInteger statusCode = -1;
            
            NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%lld%@%ld", @"reports/", [dataManager getActiveWorkspaceId], @"/RESREQ?sortOrder=desc&fromDate=", [[dataManager getLastResourceRequestTimestampForIncidentId:incidentId] longLongValue] + 1, @"&incidentId=", [incidentId longValue]] statusCode:&statusCode];
            NSError* error = nil;
            
            ResourceRequestMessage *message = [[ResourceRequestMessage alloc] initWithString:json error:&error];
            [message parse];
            
            if([message.reports count] > 0) {
                [dataManager addResourceRequestsToHistory:message.reports];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSNotification *resourceRequestsReceivedNotification = [NSNotification notificationWithName:@"resourceRequestsUpdateReceived" object:message];
                    [notificationCenter postNotification:resourceRequestsReceivedNotification];
                });
            }
            
            receivingResourceRequests = NO;
            completion(YES);
        });
    }
}

+(void) postResourceRequests {
    if(!sendingResourceRequests) {
        sendingResourceRequests = YES;
        NSMutableArray* resourceRequests = [dataManager getAllResourceRequestsFromStoreAndForward];
        
        for(ResourceRequestPayload *payload in resourceRequests) {
            if([payload.isDraft isEqual:@0]) {
                NSString *jsonString = [payload toJSONStringPost];
                NSData *postData = [NSData dataWithBytes:[jsonString UTF8String] length:[jsonString length]];
                
                dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
                dispatch_async(queue, ^{
                    
                    NSInteger statusCode = -1;
                    NSString* result = [self synchronousPostToUrl:[NSString stringWithFormat:@"%@%@%@", @"reports/", [dataManager getActiveWorkspaceId],  @"/RESREQ"] postData:postData length:[jsonString length] statusCode:&statusCode];
                    
                    if(statusCode == 200 || statusCode == 201) {
                        [dataManager deleteResourceRequestFromStoreAndForward:payload];
                        [dataManager requestResourceRequestsRepeatedEvery:[DataManager getReportsUpdateFrequencyFromSettings] immediate:YES];
                    } else {
                        NSLog(@"%@%@", @"Failed to send Resource Request...\n", result);
                    }
                });
            }
        }
        sendingResourceRequests = NO;
    }
}

+(void) getUxoReportsForIncidentId:(NSNumber *)incidentId offset:(NSNumber *)offset limit:(NSNumber *)limit completion:(void (^)(BOOL))completion{
    if(!receivingUxoReports && ![incidentId  isEqual: @-1]) {
        receivingUxoReports = YES;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            NSInteger statusCode = -1;
            
            NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%lld", @"reports/",[dataManager getActiveIncidentId], @"/UXO?sortOrder=desc&fromDate=", [[dataManager getLastUxoReportTimestampForIncidentId:incidentId] longLongValue] + 1] statusCode:&statusCode];
            NSError* error = nil;
            
            UxoReportMessage *message = [[UxoReportMessage alloc] initWithString:json error:&error];
            [message parse];
            
            if([message.reports count] > 0) {
                [dataManager addUxoReportsToHistory:message.reports];
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    NSNotification *uxoReportsReceivedNotification = [NSNotification notificationWithName:@"UxoReportsUpdateReceived" object:message];
                    [notificationCenter postNotification:uxoReportsReceivedNotification];
                });
            }
            
            receivingUxoReports = NO;
            completion(YES);
        });
    }
}


+(void) postUxoReports {
    
    NSMutableArray* uxoReports = [dataManager getAllUxoReportsFromStoreAndForward];
    
    for(UxoReportPayload *payload in uxoReports) {
        if([payload.isDraft isEqual:@0]) {
            [mMultipartPostQueue addPayloadToSendQueue:payload];
            break;
        }
    }
}


+(void) getMarkupHistoryForCollabroomId:(NSNumber *)collabRoomId completion:(void (^)(BOOL))completion{
    if(!receivingMapMarkupFeatures && ![collabRoomId  isEqual: @-1]) {
        receivingMapMarkupFeatures = YES;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            NSInteger statusCode = -1;
            
            NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%s%@%@%@%ld", @"features/collabroom/", [dataManager getSelectedCollabroomId], "?geoType=4326", @"&userId=",[dataManager getUserId],@"&dateColumn=seqTime&sortOrder=desc", [[dataManager getLastMarkupFeatureTimestampForCollabroomId:collabRoomId] longValue] + 1] statusCode:&statusCode];
         
            NSError* error = nil;
            
            MarkupMessage *message = [[MarkupMessage alloc] initWithString:json error:&error];
            
            if([message.features count] > 0) {
                for(MarkupFeature *feature in message.features) {
                    feature.collabRoomId = [dataManager getSelectedCollabroomId];
                    [feature filterGeometry];
                }

                MarkupPayload *payload = [[MarkupPayload alloc] init];
                payload.features = message.features;
                payload.incidentId = [dataManager getActiveIncidentId];
                
                [dataManager addMarkupFeaturesToHistory:payload.features];

                dispatch_async(dispatch_get_main_queue(), ^{
                    NSNotification *markupFeaturesReceivedNotification = [NSNotification notificationWithName:@"markupFeaturesUpdateReceived" object:message];
                    [notificationCenter postNotification:markupFeaturesReceivedNotification];
                });
            }
        });
        
        receivingMapMarkupFeatures = NO;
        completion(YES);
    }
}



+(void) getWeatherUpdateForLatitude:(double)latitude longitude:(double) longitude completion:(void (^)(BOOL successful)) completion {
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
    dispatch_async(queue, ^{
        NSInteger statusCode = -1;
        
        NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%f%@%f%@", @"http://forecast.weather.gov/MapClick.php?lat=", latitude, @"&lon=", longitude, @"&FcstType=json"] statusCode:&statusCode];
        NSError* error = nil;
        
        WeatherPayload *payload = [[WeatherPayload alloc] initWithString:json error:&error];
        if(payload != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                NSNotification *weatherUpdateReceivedNotification = [NSNotification notificationWithName:@"weatherUpdateReceived" object:payload];
                [notificationCenter postNotification:weatherUpdateReceivedNotification]; });
        }
    });
}



+(void) getChatMessagesForCollabroomId:(NSNumber *)collabRoomId completion:(void (^)(BOOL))completion {
    if(!receivingChatMessages && ![collabRoomId  isEqual: @-1]) {
        receivingChatMessages = YES;
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            NSInteger statusCode = -1;

            NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%ld", @"chatmsgs/",collabRoomId, @"?fromDate=", [[dataManager getLastChatMessageTimestampForCollabroomId:collabRoomId] longValue] + 1]statusCode:&statusCode];
            NSError* error = nil;
            
            
            NSError *jsonError;
            NSData *objectData = [json dataUsingEncoding:NSUTF8StringEncoding];
            NSDictionary *jsonDict ;
            if(objectData!=nil){
                jsonDict = [NSJSONSerialization JSONObjectWithData:objectData
                                                                 options:NSJSONReadingMutableContainers
                                                                   error:&jsonError];
            }else{
                return;
            }
            

            //this should set automatically but the nics6 json format for chat is really big right now and will probably change
            //So i am setting it manually below until the format is final.
            
            ChatMessage *message = [[ChatMessage alloc] initWithString:json error:&error];
//            ChatMessage *message = [[ChatMessage alloc]init];
//            message.chats = (ChatPayload*)[jsonDict objectForKey:@"chats"];
            
//            NSUInteger count = 0;

            NSMutableArray* chats = [jsonDict objectForKey:@"chats"];
            NSNumber* lastChatTimestamp = [dataManager getLastChatMessageTimestampForCollabroomId:collabRoomId];
            NSUInteger count = 0;
            
            NSUInteger forLoopCounter = 0;
            if([message.chats count] > 0) {
                for(ChatPayload *payload in message.chats) {
                    
                    if([payload.created doubleValue] > [lastChatTimestamp doubleValue]){
                    
                        payload.incidentId = [dataManager getActiveIncidentId];
                        payload.id = payload.chatid;
                        
                        jsonDict = [chats objectAtIndex:forLoopCounter];
                        
                        payload.userId = [[[jsonDict objectForKey:@"userorg"] objectForKey:@"user"] objectForKey:@"userId"];
                        payload.userOrgName = [[[jsonDict objectForKey:@"userorg"] objectForKey:@"org"] objectForKey:@"name"];
                        payload.nickname = [[[jsonDict objectForKey:@"userorg"] objectForKey:@"user"] objectForKey:@"username"];
                        count++;
                        
                        [dataManager addChatMessageToHistory:payload];
                    }
                    forLoopCounter++;
                }
                
            }
            
            
//                [dataManager addChatMessagesToHistory:message.chats];
            if(count > 0){
                dispatch_async(dispatch_get_main_queue(), ^{
                        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys: lastChatTimestamp,@"lastChatTimestamp", nil];
                    
                    NSNotification *chatMessagesReceivedNotification = [NSNotification notificationWithName:@"chatMessagesUpdateReceived" object:self userInfo:dict];
                    [notificationCenter postNotification:chatMessagesReceivedNotification];
                });
//            }
            }
            
            receivingChatMessages = NO;
            completion(YES);
        });
    }
}

+(void) postChatMessages {
    if(!sendingChatMessages) {
        sendingChatMessages = YES;
        NSMutableArray* chatMessages = [dataManager getAllChatMessagesFromStoreAndForward];
        
        for(ChatPayload *payload in chatMessages) {
            NSString *jsonString = [payload toJSONStringPost];
            NSData *postData = [NSData dataWithBytes:[jsonString UTF8String] length:[jsonString length]];
            
            dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
            dispatch_async(queue, ^{
                
                NSInteger statusCode = -1;
                NSString* result = [self synchronousPostToUrl:[NSString stringWithFormat:@"%@%@", @"chatmsgs/",[dataManager getSelectedCollabroomId]] postData:postData length:[jsonString length] statusCode:&statusCode];
                
                if(statusCode == 200 || statusCode == 201) {
                    [dataManager deleteChatMessageFromStoreAndForward:payload];
                    [dataManager requestChatMessagesRepeatedEvery:[DataManager getChatUpdateFrequencyFromSettings] immediate:YES];
                } else {
                    NSLog(@"%@%@", @"Failed to send Chat Message...\n", result);
                }
            });
        }
        sendingChatMessages = NO;
    }
}


+(void) postMDTs: (MDTPayload*) payload {
    if(!sendingMDTs) {
        sendingMDTs = YES;

   //     MDTPayload *payload;
        
            NSString *jsonString = [payload toJSONStringPost];
            NSData *postData = [NSData dataWithBytes:[jsonString UTF8String] length:[jsonString length]];
            
            dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
            dispatch_async(queue, ^{
                
                NSInteger statusCode = -1;
                NSString* result = [self synchronousPostToUrl:[NSString stringWithFormat:@"%@", @"mdtracks/"] postData:postData length:[jsonString length] statusCode:&statusCode];
                
                if(statusCode == 200 || statusCode == 201) {
                    NSString *inStr = [NSString stringWithFormat: @"%ld", (long)statusCode];
                    NSLog(@"%@%@", @"MDT sent ...\n", inStr);
                    //[dataManager deleteChatMessageFromStoreAndForward:payload];
                    //[dataManager requestChatMessagesRepeatedEvery:30 immediate:YES];
                } else {
                    NSLog(@"%@%@", @"Failed to send MDT Message...\n", result);
                }
            });
        }
        sendingMDTs = NO;
    }

+(NSString *) deleteMarkupFeatureById:(NSString *)featureId {
    NSLog(@"Deleting markup feature...");
    NSURL* postUrl = [NSURL URLWithString:[NSString stringWithFormat: @"%@%@%@%@%@", BASE_URL, @"mapmarkups/", dataManager.getActiveWorkspaceId, @"/", featureId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:postUrl];
    
    [request setValue:myOpenAmAuth.AuthValue forHTTPHeaderField:@"Authorization"];
    
    [request setHTTPMethod:@"DELETE"];
    NSHTTPURLResponse *response = nil;
    NSError *error = nil;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    
    return [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
}

+ (void) getWFSData{
  if(receivingWfsFeatures == NO)
  {
      receivingWfsFeatures = YES;
    __block bool failedToPull = NO;
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
    dispatch_async(queue, ^{
    
    NSMutableArray *ParsedWfsFeatures = [[NSMutableArray alloc] init];
    
    for(int i = 0; i < [ActiveWfsLayerManager getTrackingLayers].count ; i++)
    {
        TrackingLayer* currentLayer = [[ActiveWfsLayerManager getTrackingLayers] objectAtIndex:i];
        
        if(currentLayer.active){
            NSMutableString *urlString = [[NSMutableString alloc] init];
            [urlString setString: [[dataManager getGeoServerFromSettings] stringByAppendingString:@"/ows?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName="]];
            [urlString appendString:currentLayer.typeNameURL];
            [urlString appendString:@"&maxFeatures=500"];
            
            NSData *fullStringData = [[NSData alloc] initWithContentsOfURL: [NSURL URLWithString:urlString]];

            if(fullStringData == nil){
                failedToPull = YES;
            }else{
            
                NSError *JSONerror = nil;
                NSMutableDictionary *featureCollectiondictionary = [NSJSONSerialization JSONObjectWithData:fullStringData options:NSJSONReadingMutableContainers error:&JSONerror];
            
                NSArray* allFeatures = featureCollectiondictionary[@"features"];
                for ( NSDictionary *currentFeature in allFeatures)
                {
                    WfsFeature* newFeature = [[WfsFeature alloc] init];
                    [newFeature setupWithDictionary: currentFeature];
        
                    [ParsedWfsFeatures addObject:newFeature];
                }
            }
        }
    }
    receivingWfsFeatures = NO;
        
    if(failedToPull == NO)
    {
        [ActiveWfsLayerManager setWfsFeatures:ParsedWfsFeatures];
    
        NSNotification *WfsUpdateRecievedNotification = [NSNotification notificationWithName:@"WfsUpdateRecieved" object:nil];
        [notificationCenter postNotification:WfsUpdateRecievedNotification];
    }
    
    });
  }
   
}

+ (void)getUserOrgs:(NSNumber*) userId{
    
    NSInteger statusCode = -1;
    NSString* json = [self synchronousGetFromUrl:[NSString stringWithFormat:@"%@%@%@%@", @"orgs/", [dataManager getActiveWorkspaceId], @"?userId=", userId] statusCode:&statusCode];
    
    NSError* error = nil;
    
    OrganizationMessage *message = [[OrganizationMessage alloc] initWithString:json error:&error];
    
//    if([message.organizations count] > 0) {
//        for(OrganizationPayload *org in message.organizations) {
//            org.collabRoomId = [dataManager getSelectedCollabroomId];
//            [feature filterGeometry];
//        }
    
    
    [dataManager setOrgData:message.organizations[0]];
    
    
//        MarkupPayload *payload = [[MarkupPayload alloc] init];
//        payload.features = message.features;
//        payload.incidentId = [dataManager getActiveIncidentId];
//        
//        [dataManager addMarkupFeaturesToHistory:payload.features];
//    }
    
}

+ (NSString *) authValue {
    @synchronized(self) {
        return authValue;
    }
}

+ (void) setAuthValue:(NSString *)value {
    @synchronized(self) {
        authValue = value;
    }
}

+ (void)setSendingSimpleReport:(BOOL)value {
    sendingSimpleReports = value;
}

+ (void)setSendingUxoReport:(BOOL)value {
    sendingUxoReports = value;
}

@end

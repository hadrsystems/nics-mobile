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
//  DataManager.m
//  nics_iOS
//
//

#import "DataManager.h"

int BackgroundMdtPostCounter = 0;

@implementation DataManager

+ (id)getInstance {
    static DataManager *instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    
    return instance;
}

- (id)init {
    if (self = [super init]) {
        
        NSString *idfv = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
        NSString *nicsString = @"nics";
        
        [self registerDefaultsFromSettingsBundle];
        
        [[NSUserDefaults standardUserDefaults] setSecret:[nicsString stringByAppendingString:idfv]];
        
        _userPreferences = [NSUserDefaults standardUserDefaults];
        _databaseManager = [[DatabaseManager alloc] init];
        
        [self getCookieDomainForCurrentServer];
        
        _notificationCenter = [NSNotificationCenter defaultCenter];
        
        _locationManager = [[CLLocationManager alloc] init];
        [_locationManager setDelegate:self];
        _locationManager.distanceFilter = 10.0f;
        _locationManager.desiredAccuracy = kCLLocationAccuracyBest;
        
        _CurrentMapType = 0;
        
        if (SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"8.0")) {
            [_locationManager requestWhenInUseAuthorization];
            [_locationManager requestAlwaysAuthorization];
            [_locationManager startUpdatingLocation];
        }
        
        self.appSuspended = FALSE;
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(AppHasBeenSuspended:) name:@"AppHasBeenSuspended" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(AppHasBeenResumed:) name:@"AppHasBeenResumed" object:nil];
        
        NSDictionary *locationDictionary = [_userPreferences secretObjectForKey:@"lastLocation"];
        if(locationDictionary != nil) {
            CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake([[locationDictionary objectForKey:@"latitude"] doubleValue], [[locationDictionary objectForKey:@"longitude"] doubleValue]);
            
            CLLocation *location = [[CLLocation alloc] initWithCoordinate:coordinate
                altitude:[[locationDictionary objectForKey:@"altitude"] doubleValue]
                horizontalAccuracy:[[locationDictionary objectForKey:@"horizontalAccuracy"] doubleValue]
                verticalAccuracy:[[locationDictionary objectForKey:@"verticalAccuracy"] doubleValue]
                course:[[locationDictionary objectForKey:@"course"] doubleValue]
                speed:[[locationDictionary objectForKey:@"speed"] doubleValue]
                timestamp:[NSDate dateWithTimeIntervalSince1970:[[locationDictionary objectForKey:@"timestamp"] doubleValue]]];
            
            _currentLocation = location;
        }

    }
    return self;
}

- (void)registerDefaultsFromSettingsBundle {
    NSString *settingsBundle = [[NSBundle mainBundle] pathForResource:@"Settings" ofType:@"bundle"];
    if(!settingsBundle) {
        NSLog(@"Could not find Settings.bundle");
        return;
    }
    
    NSDictionary *settings = [NSDictionary dictionaryWithContentsOfFile:[settingsBundle stringByAppendingPathComponent:@"Root.plist"]];
    NSArray *preferences = [settings objectForKey:@"PreferenceSpecifiers"];
    
    NSMutableDictionary *defaultsToRegister = [[NSMutableDictionary alloc] initWithCapacity:[preferences count]];
    for(NSDictionary *prefSpecification in preferences) {
        NSString *key = [prefSpecification objectForKey:@"Key"];
        if(key && [[prefSpecification allKeys] containsObject:@"DefaultValue"]) {
            [defaultsToRegister setObject:[prefSpecification objectForKey:@"DefaultValue"] forKey:key];
        }
    }
    
    [[NSUserDefaults standardUserDefaults] registerDefaults:defaultsToRegister];
}

-(void)ResetLocalUserData{
    NSString *domainName = [[NSBundle mainBundle] bundleIdentifier];
    [[NSUserDefaults standardUserDefaults] removePersistentDomainForName:domainName];
    [_databaseManager ClearAllLocalDatabases];
}

-(void)AppHasBeenSuspended:(NSNotification*)_notification{
    _appSuspended = TRUE;
}
-(void)AppHasBeenResumed:(NSNotification*)_notification{
    _appSuspended = FALSE;
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
     _appSuspended = TRUE;
}

-(void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations {
    CLLocation *location = [locations lastObject];
    
    if(location.coordinate.latitude != _currentLocation.coordinate.latitude && location.coordinate.longitude != _currentLocation.coordinate.longitude) {
    
        _currentLocation = location;
        
        NSMutableDictionary *locationDataDictionary = [NSMutableDictionary new];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:_currentLocation.coordinate.latitude] forKey:@"latitude"];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:_currentLocation.coordinate.longitude] forKey:@"longitude"];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:_currentLocation.altitude] forKey:@"altitude"];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:_currentLocation.horizontalAccuracy] forKey:@"horizontalAccuracy"];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:_currentLocation.verticalAccuracy] forKey:@"verticalAccuracy"];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:_currentLocation.course] forKey:@"course"];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:_currentLocation.speed] forKey:@"speed"];
        [locationDataDictionary setObject:[NSNumber numberWithDouble:[_currentLocation.timestamp timeIntervalSince1970]] forKey:@"timestamp"];
        
        [_userPreferences setSecretObject:locationDataDictionary forKey:@"lastLocation"];
//        [RestClient getWeatherUpdateForLatitude:location.coordinate.latitude longitude:location.coordinate.longitude completion:nil];
        
        
        if(_isLoggedIn){
            if(_appSuspended){
                BackgroundMdtPostCounter++;
                if(BackgroundMdtPostCounter >= 30)
                {
                    NSLog(@"locMan: send location suspended");
                    [self requestMDT];
                    BackgroundMdtPostCounter = 0;
                }
            }
            
        }
    }
}

- (void) disableAllPollingTimers{
    if( _assignmentsPollingTimer!=nil){
        [_assignmentsPollingTimer invalidate];
        _assignmentsPollingTimer = nil;
    }
    if( _chatMessagesPollingTimer!=nil){
        [_chatMessagesPollingTimer invalidate];
        _chatMessagesPollingTimer = nil;
    }
    if( _damageReportPollingTimer!=nil){
        [_damageReportPollingTimer invalidate];
        _damageReportPollingTimer = nil;
    }
    if( _fieldReportPollingTimer!=nil){
        [_fieldReportPollingTimer invalidate];
        _fieldReportPollingTimer = nil;
    }
    if( _weatherReportPollingTimer!=nil){
        [_weatherReportPollingTimer invalidate];
        _weatherReportPollingTimer = nil;
    }
    if( _markupFeaturesPollingTimer!=nil){
        [_markupFeaturesPollingTimer invalidate];
        _markupFeaturesPollingTimer = nil;
    }
    if( _resourceRequestPollingTimer!=nil){
        [_resourceRequestPollingTimer invalidate];
        _resourceRequestPollingTimer = nil;
    }
    if( _simpleReportPollingTimer!=nil){
        [_simpleReportPollingTimer invalidate];
        _simpleReportPollingTimer = nil;
    }
    if( _wfsPollingTimer!=nil){
        [_wfsPollingTimer invalidate];
        _wfsPollingTimer = nil;
    }
}

- (void) requestMdtRepeatedEvery:(int)seonds immediate:(BOOL)immediate{
    
       dispatch_async(dispatch_get_main_queue(), ^{
           _assignmentsPollingTimer = [NSTimer scheduledTimerWithTimeInterval:30 target:self selector:@selector(requestMDT) userInfo:nil repeats:YES];
       });
}

- (void) requestActiveAssignmentRepeatedEvery:(int) seconds {
    if(_assignmentsPollingTimer != nil) {
        [_assignmentsPollingTimer invalidate];
        _assignmentsPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _assignmentsPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestActiveAssignment) userInfo:nil repeats:YES];
    });
}

- (void) requestChatMessagesRepeatedEvery:(int)seconds immediate:(BOOL)immediate {
    if(immediate) {
        [self requestChatMessages];
    }
    
    if(_chatMessagesPollingTimer != nil) {
        [_chatMessagesPollingTimer invalidate];
        _chatMessagesPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _chatMessagesPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestChatMessages) userInfo:nil repeats:YES];
    });
}

- (void) requestSimpleReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate  {
    if(immediate) {
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            [self requestSimpleReports];
        });
    }
    
    if(_simpleReportPollingTimer != nil) {
        [_simpleReportPollingTimer invalidate];
        _simpleReportPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _simpleReportPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestSimpleReports) userInfo:nil repeats:YES];
    });
}

- (void) requestDamageReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate {
    if(immediate) {
        [self requestDamageReports];
    }
    
    if(_damageReportPollingTimer != nil) {
        [_damageReportPollingTimer invalidate];
        _damageReportPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _damageReportPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestDamageReports) userInfo:nil repeats:YES];
    });
}

- (void) requestFieldReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate {
    if(immediate) {
        [self requestFieldReports];
    }
    
    if(_fieldReportPollingTimer != nil) {
        [_fieldReportPollingTimer invalidate];
        _fieldReportPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _fieldReportPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestFieldReports) userInfo:nil repeats:YES];
    });
}

- (void) requestResourceRequestsRepeatedEvery:(int)seconds immediate:(BOOL)immediate {
    if(immediate) {
        [self requestResourceRequests];
    }
    
    if(_resourceRequestPollingTimer != nil) {
        [_resourceRequestPollingTimer invalidate];
        _resourceRequestPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _resourceRequestPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestResourceRequests) userInfo:nil repeats:YES];
    });
}

- (void) requestWeatherReportsRepeatedEvery:(int)seconds immediate:(BOOL)immediate {
    if(immediate) {
        [self requestWeatherReports];
    }
    
    if(_weatherReportPollingTimer != nil) {
        [_weatherReportPollingTimer invalidate];
        _weatherReportPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _weatherReportPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestWeatherReports) userInfo:nil repeats:YES];
    });
}

- (void) requestMarkupFeaturesRepeatedEvery:(int)seconds immediate:(BOOL)immediate {
    if(immediate) {
        [self requestMarkupFeatures];
    }
    
    if(_markupFeaturesPollingTimer != nil) {
        [_markupFeaturesPollingTimer invalidate];
        _markupFeaturesPollingTimer = nil;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _markupFeaturesPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestMarkupFeatures) userInfo:nil repeats:YES];
    });
}

- (void) requestWfsUpdateRepeatedEvery:(int)seconds immediate:(BOOL)immediate{
    if(immediate) {
        [self requestWfsUpdate];
    }
    
    if(_wfsPollingTimer != nil) {
        [_wfsPollingTimer invalidate];
        _wfsPollingTimer = nil;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        _wfsPollingTimer = [NSTimer scheduledTimerWithTimeInterval:seconds target:self selector:@selector(requestWfsUpdate) userInfo:nil repeats:YES];
    });

}


long long lastMdtSync = 0;
-(void) requestMDT {
    
    long long currentTime = [[NSDate date] timeIntervalSince1970] * 1000;
    
    if(currentTime - lastMdtSync > [DataManager getMdtUpdateFrequencyFromSettings] * 1000){
    
        lastMdtSync = currentTime;
        NSLog(@"Requesting MDT update...");
        
        CLLocation *location = [_locationManager location];
        
        MDTPayload * payload = [MDTPayload new];
        payload.deviceId = [self getUsername]; // @"42f7741baa42af75";
        payload.userId = [self getUserId];
        payload.createdUTC = currentTime;
        payload.altitude = location.altitude;
        payload.latitude = location.coordinate.latitude;
        payload.longitude = location.coordinate.longitude;
        payload.accuracy = location.horizontalAccuracy;
        payload.speed = location.speed;
        payload.course = location.course;
        payload.incidentId = [self currentIncident].incidentid;
        [RestClient postMDTs: payload];
    }
}

-(void) requestActiveAssignment {
    NSLog(@"Requesting assignment update...");
    [RestClient getActiveAssignmentForUser:[self getUsername] userId:[self getUserId] activeOnly:YES completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"Refreshed Assignment Information");
        }
    }];

}

-(void) requestChatMessages {
    NSLog(@"Requesting chat messages update...");
    [RestClient getChatMessagesForCollabroomId:[self getSelectedCollabroomId] completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"%@%@", @"Refreshed Chat Messages for Collabroom: ", [self getSelectedCollabroomName]);
        }
    }];
}

-(void) requestSimpleReports {
    NSLog(@"Requesting simple reports update...");
    [RestClient getSimpleReportsForIncidentId:[self getActiveIncidentId] offset:@0 limit:@0 completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"Refreshed Simple Report Information");
        }
    }];
}

-(void) requestDamageReports {
    NSLog(@"Requesting damage reports update...");
    [RestClient getDamageReportsForIncidentId:[self getActiveIncidentId] offset:@0 limit:@0 completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"Refreshed Damage Report Information");
        }
    }];
}

-(void) requestFieldReports {
    NSLog(@"Requesting field reports update...");
    [RestClient getFieldReportsForIncidentId:[self getActiveIncidentId] offset:@0 limit:@0 completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"Refreshed Field Report Information");
        }
    }];
}

-(void) requestResourceRequests {
    NSLog(@"Requesting resource requests update...");
    [RestClient getResourceRequestsForIncidentId:[self getActiveIncidentId] offset:@0 limit:@0 completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"Refreshed Resource Request Information");
        }
    }];
}

-(void) requestWeatherReports {
    NSLog(@"Requesting Weather reports update...");
    [RestClient getWeatherReportsForIncidentId:[self getActiveIncidentId] offset:@0 limit:@0 completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"Refreshed Weather Report Information");
        }
    }];
}

-(void) requestMarkupFeatures {
    NSLog(@"Requesting map markup update...");
    [RestClient getMarkupHistoryForCollabroomId:[self getSelectedCollabroomId] completion:^(BOOL successful) {
        if(successful) {
            NSLog(@"%@%@", @"Refreshed Map Markup for Collabroom: ", [self getSelectedCollabroomName]);
        }
    }];
}

-(void) requestCollabroomsForIncident:(IncidentPayload*)incident{
  NSLog(@"%@",[@"Requesting collabrooms for incident: " stringByAppendingString:incident.incidentname]);
    [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:@"collabroomStartedLoading" object:nil]];
    
    [RestClient getCollabroomsForIncident:incident offset:@0 limit:@0 completion:^(BOOL successful){
        if(successful) {
            NSLog([@"Succesfully refreshed Collabrooms for incident: " stringByAppendingString:incident.incidentname]);
        }else{
            NSLog([@"Failed to refresh Collabrooms for incident: " stringByAppendingString:incident.incidentname]);
        }
        [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:@"collabroomFinishedLoading" object:nil]];
    }];
}

-(void) requestWfsUpdate {
    NSLog(@"Requesting wfs tracking update...");
    [RestClient getActiveWFSData];
}

#pragma mark Chat Message History/Store & Forward
- (BOOL)addChatMessageToHistory:(ChatPayload *) payload {
    return [_databaseManager addChatMessageToHistory: payload];
}

- (BOOL)addChatMessagesToHistory:(NSArray<ChatPayload> *) payloadArray {
    return [_databaseManager addChatMessagesToHistory: payloadArray];
}

- (BOOL)addChatMessageToStoreAndForward:(ChatPayload *) payload {
    BOOL success =  [_databaseManager addChatMessageToStoreAndForward: payload];
    if(success) {
        [RestClient postChatMessages];
    }
    
    return success;
}

- (BOOL)addChatMessagesToStoreAndForward:(NSArray<ChatPayload> *) payloadArray {
    return [_databaseManager addChatMessagesToStoreAndForward: payloadArray];
}

- (void)deleteAllChatMessageFromRecieve {
    [_databaseManager deleteAllChatMessageFromRecieveTable];
}

- (void)deleteChatMessageFromStoreAndForward:(ChatPayload *) payload {
    return [_databaseManager deleteChatMessageFromStoreAndForward: payload];
}

- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId {
    return [_databaseManager getAllChatMessagesForCollabroomId:collabroomId];
}
- (NSMutableArray<ChatPayload> *)getAllChatMessagesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    return [_databaseManager getAllChatMessagesForCollabroomId:collabroomId since:timestamp];
}

- (NSMutableArray<ChatPayload> *)getAllChatMessagesFromStoreAndForward {
    return [_databaseManager getAllChatMessagesFromStoreAndForward];
}

- (NSNumber *)getLastChatMessageTimestampForCollabroomId: (NSNumber *) collabroomId {
    return [_databaseManager getLastChatMessageTimestampForCollabroomId:collabroomId];
}


#pragma mark Damage Report History/Store & Forward
- (BOOL)addDamageReportToHistory:(DamageReportPayload *) payload {
    return [_databaseManager addDamageReportToHistory: payload];
}

- (BOOL)addDamageReportsToHistory:(NSArray<DamageReportPayload> *) payloadArray {
    return [_databaseManager addDamageReportsToHistory: payloadArray];
}

- (BOOL)addDamageReportsToStoreAndForward:(NSArray<DamageReportPayload> *) payloadArray {
    return [_databaseManager addDamageReportsToStoreAndForward: payloadArray];
}

- (BOOL)addDamageReportToStoreAndForward:(DamageReportPayload *) payload {
    BOOL success =  [_databaseManager addDamageReportToStoreAndForward: payload];
    if(success) {
        [RestClient postDamageReports];
    }
    
    return success;
}

- (void)deleteDamageReportFromStoreAndForward:(DamageReportPayload *) payload {
    return [_databaseManager deleteDamageReportFromStoreAndForward: payload];
}

- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsForIncidentId: (NSNumber *)incidentId {
    return [_databaseManager getAllDamageReportsForIncidentId: incidentId since:@0];
}

- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    return [_databaseManager getAllDamageReportsForIncidentId:incidentId since:timestamp];
}

- (NSMutableArray<DamageReportPayload> *)getAllDamageReportsFromStoreAndForward {
    return [_databaseManager getAllDamageReportsFromStoreAndForward];
}

- (NSNumber *)getLastDamageReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_databaseManager getLastDamageReportTimestampForIncidentId:incidentId];
}


#pragma mark Field Report History/Store & Forward
- (BOOL)addFieldReportToHistory:(FieldReportPayload *) payload {
    return [_databaseManager addFieldReportToHistory: payload];
}

- (BOOL)addFieldReportsToHistory:(NSArray<FieldReportPayload> *) payloadArray {
    return [_databaseManager addFieldReportsToHistory: payloadArray];
}

- (BOOL)addFieldReportsToStoreAndForward:(NSArray<FieldReportPayload> *) payloadArray {
    return [_databaseManager addFieldReportsToStoreAndForward: payloadArray];
}

- (BOOL)addFieldReportToStoreAndForward:(FieldReportPayload *) payload {
    BOOL success =  [_databaseManager addFieldReportToStoreAndForward: payload];
    if(success) {
        [RestClient postFieldReports];
    }
    
    return success;
}

- (void)deleteFieldReportFromStoreAndForward:(FieldReportPayload *) payload {
    return [_databaseManager deleteFieldReportFromStoreAndForward: payload];
}

- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsForIncidentId: (NSNumber *)incidentId {
    return [_databaseManager getAllFieldReportsForIncidentId: incidentId since:@0];
}

- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    return [_databaseManager getAllFieldReportsForIncidentId:incidentId since:timestamp];
}

- (NSMutableArray<FieldReportPayload> *)getAllFieldReportsFromStoreAndForward {
    return [_databaseManager getAllFieldReportsFromStoreAndForward];
}

- (NSNumber *)getLastFieldReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_databaseManager getLastFieldReportTimestampForIncidentId:incidentId];
}


#pragma mark Resource Request History/Store & Forward
- (BOOL)addResourceRequestsToHistory:(NSArray<ResourceRequestPayload> *) payloadArray {
    return [_databaseManager addResourceRequestsToHistory: payloadArray];
}

- (BOOL)addResourceRequestToHistory:(ResourceRequestPayload *) payload {
    return [_databaseManager addResourceRequestToHistory: payload];
}

- (BOOL)addResourceRequestsToStoreAndForward:(NSArray<ResourceRequestPayload> *) payloadArray {
    return [_databaseManager addResourceRequestsToStoreAndForward: payloadArray];
}

- (BOOL)addResourceRequestToStoreAndForward:(ResourceRequestPayload *) payload {
    BOOL success =  [_databaseManager addResourceRequestToStoreAndForward: payload];
    if(success) {
        [RestClient postResourceRequests];
    }
    
    return success;
}

- (void)deleteResourceRequestFromStoreAndForward:(ResourceRequestPayload *) payload {
    return [_databaseManager deleteResourceRequestFromStoreAndForward: payload];
}

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsForIncidentId: (NSNumber *)incidentId {
    return [_databaseManager getAllResourceRequestsForIncidentId:incidentId since:@0];
}

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    return [_databaseManager getAllResourceRequestsForIncidentId: incidentId since:timestamp];
}

- (NSMutableArray<ResourceRequestPayload> *)getAllResourceRequestsFromStoreAndForward {
    return [_databaseManager getAllResourceRequestsFromStoreAndForward];
}

- (NSNumber *)getLastResourceRequestTimestampForIncidentId: (NSNumber *) incidentId {
    return [_databaseManager getLastResourceRequestTimestampForIncidentId:incidentId];
}


#pragma mark Simple Report History/Store & Forward
- (BOOL)addSimpleReportsToHistory:(NSArray<SimpleReportPayload> *) payloadArray {
    return [_databaseManager addSimpleReportsToHistory: payloadArray];
}

- (BOOL)addSimpleReportToHistory:(SimpleReportPayload *) payload {
    return [_databaseManager addSimpleReportToHistory: payload];
}

- (BOOL)addSimpleReportsToStoreAndForward:(NSArray<SimpleReportPayload> *) payloadArray {
    return [_databaseManager addSimpleReportsToStoreAndForward:payloadArray];
}

- (BOOL)addSimpleReportToStoreAndForward:(SimpleReportPayload *) payload {
    BOOL success =  [_databaseManager addSimpleReportToStoreAndForward: payload];
    if(success) {
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
        dispatch_async(queue, ^{
            [RestClient postSimpleReports];
        });
    }
    
    return success;
}

- (void)deleteSimpleReportFromStoreAndForward:(SimpleReportPayload *) payload {
    return [_databaseManager deleteSimpleReportFromStoreAndForward: payload];
}

- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsForIncidentId: (NSNumber *)incidentId {
    return [_databaseManager getAllSimpleReportsForIncidentId: incidentId since:@0];
}

- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    return [_databaseManager getAllSimpleReportsForIncidentId: incidentId since:timestamp];
}

- (NSMutableArray<SimpleReportPayload> *)getAllSimpleReportsFromStoreAndForward {
    return [_databaseManager getAllSimpleReportsFromStoreAndForward];
}

- (NSNumber *)getLastSimpleReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_databaseManager getLastSimpleReportTimestampForIncidentId:incidentId];
}

#pragma mark Weather Report History/Store & Forward
- (BOOL)addWeatherReportToHistory:(WeatherReportPayload *) payload {
    return [_databaseManager addWeatherReportToHistory: payload];
}

- (BOOL)addWeatherReportsToHistory:(NSArray<WeatherReportPayload> *) payloadArray {
    return [_databaseManager addWeatherReportsToHistory: payloadArray];
}

- (BOOL)addWeatherReportsToStoreAndForward:(NSArray<WeatherReportPayload> *) payloadArray {
    return [_databaseManager addWeatherReportsToStoreAndForward: payloadArray];
}

- (BOOL)addWeatherReportToStoreAndForward:(WeatherReportPayload *) payload {
    BOOL success =  [_databaseManager addWeatherReportToStoreAndForward: payload];
    if(success) {
        [RestClient postWeatherReports];
    }
    
    return success;
}

- (void)deleteWeatherReportFromStoreAndForward:(WeatherReportPayload *) payload {
    return [_databaseManager deleteWeatherReportFromStoreAndForward: payload];
}

- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsForIncidentId: (NSNumber *)incidentId {
    return [_databaseManager getAllWeatherReportsForIncidentId: incidentId since:@0];
}

- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    return [_databaseManager getAllWeatherReportsForIncidentId:incidentId since:timestamp];
}

- (NSMutableArray<WeatherReportPayload> *)getAllWeatherReportsFromStoreAndForward {
    return [_databaseManager getAllWeatherReportsFromStoreAndForward];
}

- (NSNumber *)getLastWeatherReportTimestampForIncidentId: (NSNumber *) incidentId {
    return [_databaseManager getLastWeatherReportTimestampForIncidentId:incidentId];
}


#pragma mark Markup Features History/Store & Forward
- (BOOL)addMarkupFeaturesToHistory:(NSArray<MarkupFeature> *) payloadArray {
    return [_databaseManager addMarkupFeaturesToHistory: payloadArray];
}

- (BOOL)addMarkupFeatureToHistory:(MarkupFeature *) payload {
    return [_databaseManager addMarkupFeatureToHistory: payload];
}

- (BOOL)addMarkupFeaturesToStoreAndForward:(NSArray<MarkupFeature> *) payloadArray {
    bool success = [_databaseManager addMarkupFeaturesToStoreAndForward:payloadArray];
    if(success){
        [RestClient postMapMarkupFeatures];
    }
    return success;
}

- (BOOL)addMarkupFeatureToStoreAndForward:(MarkupFeature *) payload {
    bool success = [_databaseManager addMarkupFeatureToStoreAndForward: payload];
    if(success){
        [RestClient postMapMarkupFeatures];
    }
    return success;
}

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId {
    return [_databaseManager getAllMarkupFeaturesForCollabroomId:collabroomId since:@0];
}

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesForCollabroomId: (NSNumber *)collabroomId since: (NSNumber *)timestamp {
    return [_databaseManager getAllMarkupFeaturesForCollabroomId:collabroomId since:timestamp];
}

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForward {
    return [_databaseManager getAllMarkupFeaturesFromStoreAndForward];
}

- (NSMutableArray<MarkupFeature> *)getAllMarkupFeaturesFromStoreAndForwardForCollabroomId: (NSNumber *)collabroomId {
    return [_databaseManager getAllMarkupFeaturesFromStoreAndForwardForCollabroomId:collabroomId since:@0];
}

- (void)deleteMarkupFeatureFromStoreAndForward:(MarkupFeature *)feature{
    return [_databaseManager deleteMarkupFeatureFromStoreAndForward: feature];
}

- (void)deleteMarkupFeatureFromStoreAndForwardByFeatureId:(NSString *)featureId{
    return [_databaseManager deleteMarkupFeatureFromStoreAndForwardByFeatureId: featureId];
}
- (void)deleteMarkupFeatureFromReceiveTableByFeatureId:(NSString *)featureId{
    return [_databaseManager deleteMarkupFeatureFromReceiveTableByFeatureId: featureId];
}

- (NSNumber *)getLastMarkupFeatureTimestampForCollabroomId: (NSNumber *) collabroomId {
    return [_databaseManager getLastMarkupFeatureTimestampForCollabroomId:collabroomId];
}

- (NSString *)deleteMarkupFeatureById:(NSString *) featureId {
    return [RestClient deleteMarkupFeatureById: featureId];
}

- (void) removeAllFeaturesInCollabroom:(NSNumber*)collabRoomId{
    [_databaseManager removeAllFeaturesInCollabroom:collabRoomId];
}

- (void)setLoginSessionData:(LoginPayload *)loginSessionData {
    [_userPreferences setSecretObject:loginSessionData.workspaceId forKey:@"nics_WORKSPACE_ID"];
    [_userPreferences setSecretObject:loginSessionData.username forKey:@"nics_USERNAME"];
    [_userPreferences setSecretObject:loginSessionData.userId forKey:@"nics_USER_ID"];
    [_userPreferences setSecretObject:loginSessionData.userSessionId forKey:@"nics_USERSESSION_ID"];
}

- (NSString *)getUsername {
    return [_userPreferences secretStringForKey:@"nics_USERNAME"];
}

- (NSNumber *)getUserId {
    return [NSNumber numberWithInteger:[_userPreferences secretIntegerForKey:@"nics_USER_ID"]];
}

- (NSNumber *)getUserSessionId {
    return [NSNumber numberWithInteger:[_userPreferences secretIntegerForKey:@"nics_USERSESSION_ID"]];
}

- (BOOL)addPersonalLogMessage:(ChatPayload *) payload {
    return [_databaseManager addPersonalLogMessage: payload];
}

- (NSNumber *)getActiveWorkspaceId {
    return [NSNumber numberWithInteger:[_userPreferences secretIntegerForKey:@"nics_WORKSPACE_ID"]];
}
- (NSNumber *)getActiveIncidentId {
    return [NSNumber numberWithInteger:[_userPreferences secretIntegerForKey:@"nics_INCIDENT_ID"]];
}

- (NSString *)getActiveIncidentName {
    return [_userPreferences secretStringForKey:@"nics_INCIDENT_NAME"];
}

- (NSNumber *)getActiveCollabroomId {
    return [NSNumber numberWithInteger:[_userPreferences secretIntegerForKey:@"nics_COLLABROOM_ID"]];
}

- (NSString *)getActiveCollabroomName {
    return [_userPreferences secretStringForKey:@"nics_COLLABROOM_NAME"];
}

//- (void)setActiveCollabroomName: (NSString*) collabName {
//    [_userPreferences setSecretObject:collabName forKey:@"nics_COLLABROOM_NAME"];
//}

- (NSNumber *)getSelectedCollabroomId {
    return [NSNumber numberWithInt:[_userPreferences secretIntegerForKey:@"nics_SELECTED_COLLABROOM_ID"]];
}

- (NSString *)getSelectedCollabroomName {
    return [_userPreferences secretStringForKey:@"nics_SELECTED_COLLABROOM_NAME"];
}

-(BOOL)getRememberUser{
    return [_userPreferences secretBoolForKey:@"nics_REMEMBER_USER"];
}

-(BOOL)getAutoLogin{
    return [_userPreferences secretBoolForKey:@"nics_AUTO_LOGIN"];
}

- (NSString *)getPassword {
    return [_userPreferences secretStringForKey:@"nics_PASSWORD"];
}

- (void)setActiveWorkspaceId:(NSNumber *)workspaceId{
    [_userPreferences setSecretObject:workspaceId forKey:@"nics_WORKSPACE_ID"];
}

- (void)setActiveIncident:(IncidentPayload *)incident {
    [_userPreferences setSecretObject:incident.incidentid forKey:@"nics_INCIDENT_ID"];
    [_userPreferences setSecretObject:incident.incidentname forKey:@"nics_INCIDENT_NAME"];
    [_userPreferences setSecretObject:incident.lat forKey:@"nics_INCIDENT_LATITUDE"];
    [_userPreferences setSecretObject:incident.lon forKey:@"nics_INCIDENT_LONGITUDE"];
    
    _currentIncident = incident;
}

- (void)setCurrentIncident:(IncidentPayload *)incident collabRoomId:(NSNumber *)collabRoomId collabRoomName:(NSString *)collabRoomName {
    [_userPreferences setSecretObject:incident.incidentid forKey:@"nics_INCIDENT_ID"];
    [_userPreferences setSecretObject:incident.incidentname forKey:@"nics_INCIDENT_NAME"];
    [_userPreferences setSecretObject:incident.lat forKey:@"nics_INCIDENT_LATITUDE"];
    [_userPreferences setSecretObject:incident.lon forKey:@"nics_INCIDENT_LONGITUDE"];
    
    if(![collabRoomId  isEqual: @-1]) {
        [_userPreferences setSecretObject:collabRoomId forKey:@"nics_COLLABROOM_ID"];
        [_userPreferences setSecretObject:collabRoomName forKey:@"nics_COLLABROOM_NAME"];
    }
    
    _currentIncident = incident;
}

- (void)setSelectedCollabRoomId:(NSNumber *)collabRoomId collabRoomName:(NSString *)collabRoomName {
    NSString *selectedCollabroom = [self getSelectedCollabroomName];
    
    if(selectedCollabroom == nil || ![selectedCollabroom isEqualToString:collabRoomName]) {
        [_userPreferences setSecretObject:collabRoomId forKey:@"nics_SELECTED_COLLABROOM_ID"];
        [_userPreferences setSecretObject:collabRoomName forKey:@"nics_SELECTED_COLLABROOM_NAME"];
    }
}

- (void)setRememberUser:(BOOL)value{
    [_userPreferences setSecretBool:value forKey:@"nics_REMEMBER_USER"];
}

- (void)setAutoLogin:(BOOL)value{
    [_userPreferences setSecretBool:value forKey:@"nics_AUTO_LOGIN"];
}

- (void)setUserName:(NSString *)userName{
    [_userPreferences setSecretObject:userName forKey:@"nics_USERNAME"];
}

- (void)setPassword:(NSString *)password{
    [_userPreferences setSecretObject:password forKey:@"nics_PASSWORD"];
}

- (void)setAuthToken:(NSString *)authToken{
    [_userPreferences setSecretObject:authToken forKey:@"nics_AuthToken"];
}

- (NSString *)getAuthToken{
    return [_userPreferences secretStringForKey:@"nics_AuthToken"];
}

- (NSMutableDictionary *)getIncidentsList {
    return _incidentsList;
}

- (NSMutableDictionary *)getCollabroomList {
    return _collabRoomList;
}

- (CollabroomPayload *)getActiveCollabroomPayload {
    NSNumber* collabroomId = [self getSelectedCollabroomId];
    CollabroomPayload* payload = [_collabRoomList objectForKey:collabroomId];
    return payload;
}

- (NSMutableArray *)getCollabroomPayloadArray{
    
    NSMutableArray *payloadArray = [[NSMutableArray alloc]init];
    
    for(CollabroomPayload *payload in _collabRoomList){
        [payloadArray addObject:[_collabRoomList objectForKey:payload]];
    }
    
    return payloadArray;
}

- (NSMutableDictionary *)getCollabroomNamesList {
    NSMutableDictionary *collabroomNames = [NSMutableDictionary new];
    
    for(CollabroomPayload *payload in [_collabRoomList allValues]) {
        [collabroomNames setObject:payload.collabRoomId forKey:payload.name];
    }
    
    return collabroomNames;
}

- (void)addCollabroom:(CollabroomPayload *) payload {
    if(_collabRoomList == nil) {
        _collabRoomList = [NSMutableDictionary new];
    }
    
    [_collabRoomList setObject:payload forKey:payload.collabRoomId];
}

- (void)clearCollabRoomList {
    if(_collabRoomList == nil) {
        _collabRoomList = [NSMutableDictionary new];
    }
    
    [_collabRoomList removeAllObjects];
}

- (NSString *)getServerFromSettings{
    if([self getUseCustomServerFromSettings]){
        return [[NSUserDefaults standardUserDefaults] valueForKey:@"customNicsServer"];
    }else{
        return [[NSUserDefaults standardUserDefaults] valueForKey:@"selectedServer"];
    }
}

- (NSString *)getAuthServerFromSettings{
    if([self getUseCustomServerFromSettings]){
        return [[NSUserDefaults standardUserDefaults] valueForKey:@"customAuthServer"];
    }else{
        return [[NSUserDefaults standardUserDefaults] valueForKey:@"selectedAuthServer"];
    }
}

- (NSString *)getGeoServerFromSettings{
    if([self getUseCustomServerFromSettings]){
        return [[NSUserDefaults standardUserDefaults] valueForKey:@"customGeoServer"];
    }else{
        return [[NSUserDefaults standardUserDefaults] valueForKey:@"selectedGeoServer"];
    }
}

- (NSString *)getCookieDomainForCurrentServer{
    
    if([self getUseCustomServerFromSettings]){
        return [[NSUserDefaults standardUserDefaults] valueForKey:@"customCookieDomain"];
    }else{
        NSString *settingsBundle = [[NSBundle mainBundle] pathForResource:@"Settings" ofType:@"bundle"];
        if(!settingsBundle) {
            NSLog(@"Could not find Settings.bundle");
            return nil;
        }
        
        NSDictionary *settings = [NSDictionary dictionaryWithContentsOfFile:[settingsBundle stringByAppendingPathComponent:@"Root.plist"]];
        NSDictionary *domains = [settings objectForKey:@"CookieDomains"];
        return [domains objectForKey:[self getServerFromSettings]];
    }
    
}

- (bool)getUseCustomServerFromSettings{
    return [[NSUserDefaults standardUserDefaults] boolForKey:@"useCustomServer"];
}

+ (NSNumber *)getChatUpdateFrequencyFromSettings{
    return [[NSUserDefaults standardUserDefaults] valueForKey:@"chatUpdateFrequency"];
}
+ (NSNumber *)getMapUpdateFrequencyFromSettings{
    return [[NSUserDefaults standardUserDefaults] valueForKey:@"mapUpdateFrequency"];
}
+ (NSNumber *)getReportsUpdateFrequencyFromSettings{
    return [[NSUserDefaults standardUserDefaults] valueForKey:@"reportsUpdateFrequency"];
}
+ (int)getMdtUpdateFrequencyFromSettings{
    return [[NSUserDefaults standardUserDefaults] integerForKey:@"mdtUpdateFrequency"];
}
+ (NSNumber *)getWfsUpdateFrequencyFromSettings{
    return [[NSUserDefaults standardUserDefaults] valueForKey:@"wfsUpdateFrequency"];
}
+ (bool)getCalTrackingEnabledFromSettings{
    return [[NSUserDefaults standardUserDefaults] boolForKey:@"calTracking"];
}

- (bool)getTrackingLayerEnabled: (NSString*) layerDisplayName{
    return [_userPreferences secretBoolForKey:layerDisplayName];
}
- (void)setTrackingLayerEnabled: (NSString*) layerDisplayName : (bool)enabled{
    [_userPreferences setSecretBool:enabled forKey:layerDisplayName];
}

-(void)setOverviewController:(UINavigationController *)controller{
    _OverviewViewController = controller;
}
-(UINavigationController*)getOverviewController{
    return _OverviewViewController;
}

-(void)setIsIpad:(BOOL)setting{_isIpad = setting;}
-(BOOL)getIsIpad{return _isIpad;}

@end

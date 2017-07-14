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
//  MultipartPostQueue.m
//  NICS Mobile
//
//

#import "MultipartPostQueue.h"

@implementation MultipartPostQueue

static DataManager *dataManager;

static NSURLConnection *activeConnection;
static NSMutableArray *sendQueue;
static ALAssetsLibrary *assetsLibrary;
static NSNotificationCenter *notificationCenter;

+ (id)getInstance {
    static MultipartPostQueue *instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    
    return instance;
}


- (id) init {
    if (self = [super init]) {
        sendQueue = [[NSMutableArray alloc]init];
        assetsLibrary = [ALAssetsLibrary new];
        notificationCenter = [NSNotificationCenter defaultCenter];
        dataManager = [DataManager getInstance];
        [self addCahcedReportsToSendQueue];
    }
    return self;
}

-(void) postReport: (ReportPayload*) reportPayload{

    if(reportPayload.formtypeid == [NSNumber numberWithLong:SR]){
        [self postSimpleReport:reportPayload];
    }else if (reportPayload.formtypeid == [NSNumber numberWithLong:DR]){
        [self postDamageReports:reportPayload];
    }
    

}

-(void) postSimpleReport: (SimpleReportPayload*) payload{

    [assetsLibrary assetForURL:[NSURL URLWithString:payload.messageData.fullpath] resultBlock:^(ALAsset *asset) {
        
        NSInteger statusCode = -1;
        
        ALAssetRepresentation *rep = [asset defaultRepresentation];
        NSNumber *length = [NSNumber numberWithLongLong:rep.size];
        
        Byte *buffer = (Byte*)malloc([length unsignedLongValue]);
        NSUInteger buffered = [rep getBytes:buffer fromOffset:0.0 length:[length unsignedLongValue] error:nil];
        
        NSData *imageData = [NSData dataWithBytesNoCopy:buffer length:buffered freeWhenDone:YES];
        
        if(imageData) {
            // create post request params
            
            NSMutableDictionary *requestParams = [NSMutableDictionary new];
            [requestParams setObject:[[UIDevice currentDevice].identifierForVendor UUIDString] forKey:@"deviceId"];
            [requestParams setObject:payload.incidentid forKey:@"incidentid"];
            [requestParams setObject:payload.usersessionid forKey:@"usersessionid"];
            [requestParams setObject:payload.messageData.latitude forKey:@"latitude"];
            [requestParams setObject:payload.messageData.longitude forKey:@"longitude"];
            [requestParams setObject:@0.0 forKey:@"altitude"];
            [requestParams setObject:@0.0 forKey:@"track"];
            [requestParams setObject:@0.0 forKey:@"speed"];
            [requestParams setObject:@0.0 forKey:@"accuracy"];
            [requestParams setObject:payload.messageData.msgDescription forKey:@"description"];
            [requestParams setObject:payload.messageData.category forKey:@"category"];
            [requestParams setObject:payload.seqtime forKey:@"seqtime"];
            
            activeConnection = [RestClient synchronousMultipartPostToUrl:[NSString stringWithFormat:@"%@%@%@", @"reports/", payload.incidentid,  @"/SR"] postData:imageData imageName:payload.messageData.fullpath requestParams:requestParams statusCode:&statusCode];
            
            [activeConnection scheduleInRunLoop:[NSRunLoop mainRunLoop]
                                        forMode:NSDefaultRunLoopMode];
            [activeConnection start];
        }
        
    } failureBlock:^(NSError *error) {

    }];
}

-(void) postDamageReports: (DamageReportPayload*) payload{
    [assetsLibrary assetForURL:[NSURL URLWithString:payload.messageData.drDfullPath] resultBlock:^(ALAsset *asset) {
        NSInteger statusCode = -1;
        
        ALAssetRepresentation *rep = [asset defaultRepresentation];
        NSNumber *length = [NSNumber numberWithLongLong:rep.size];
        
        Byte *buffer = (Byte*)malloc([length unsignedLongValue]);
        NSUInteger buffered = [rep getBytes:buffer fromOffset:0.0 length:[length unsignedLongValue] error:nil];
        
        NSData *imageData = [NSData dataWithBytesNoCopy:buffer length:buffered freeWhenDone:YES];
        
        if(imageData) {
            NSMutableDictionary *requestParams = [NSMutableDictionary new];
            [requestParams setObject:[[UIDevice currentDevice].identifierForVendor UUIDString] forKey:@"deviceId"];
            [requestParams setObject:payload.incidentid forKey:@"incidentId"];
            [requestParams setObject:payload.usersessionid forKey:@"usersessionid"];
            [requestParams setObject:payload.seqtime forKey:@"seqtime"];
            [requestParams setObject:@"0" forKey:@"deviceId"];
            [requestParams setObject:payload.message forKey:@"msg"];
            
            activeConnection = [RestClient synchronousMultipartPostToUrl:[NSString stringWithFormat:@"%@%@%@", @"reports/", payload.incidentid,  @"/DMGRPT"] postData:imageData imageName:payload.messageData.drDfullPath requestParams:requestParams statusCode:&statusCode];
            
            [activeConnection scheduleInRunLoop:[NSRunLoop mainRunLoop] forMode:NSDefaultRunLoopMode];
            [activeConnection start];
        }
        
    }failureBlock:^(NSError *error) {
        
    }];
}


-(void)addPayloadToSendQueue:(ReportPayload*) payload{
    [sendQueue addObject:payload];
    
    if(sendQueue.count == 1){
        [self postReport: payload];
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    
}

- (void)connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten
totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite {
    
    ReportPayload* payload =[sendQueue objectAtIndex:0];
    
        double percentage = ((double)totalBytesWritten/(double)totalBytesExpectedToWrite) * 100.0;
    
    NSLog(@"%@", [[Enums formTypeEnumToStringFull:[payload.formtypeid intValue]] stringByAppendingString: [NSString stringWithFormat:@"%f", percentage]] );
    
        [payload setProgress:[NSNumber numberWithDouble:percentage]];

        NSMutableDictionary *userInfo = [NSMutableDictionary new];
        [userInfo setObject:[NSNumber numberWithDouble: percentage] forKey:@"progress"];
        [userInfo setObject:payload.id forKey:@"id"];
    
        NSNotification *reportProgressNotification = [NSNotification notificationWithName: [[Enums formTypeEnumToStringAbbrev:[payload.formtypeid intValue]] stringByAppendingString:@"ReportProgressUpdateReceived"] object:nil userInfo:userInfo];
        [notificationCenter postNotification:reportProgressNotification];
}

//this system should get moved to some sort of queue to better manage multiple reports being sent at one time.
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    
    NSString *response = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    ReportPayload* reportPayload = [sendQueue objectAtIndex:0];
    
    if(response) {
        if(reportPayload != nil){
            if(reportPayload.formtypeid == [NSNumber numberWithLong:SR]){
                [dataManager deleteSimpleReportFromStoreAndForward:reportPayload];
                [reportPayload setStatus: [NSNumber numberWithInt:SENT]];
                [dataManager addSimpleReportToStoreAndForward:reportPayload];
                
                [dataManager requestSimpleReportsRepeatedEvery:[[DataManager getReportsUpdateFrequencyFromSettings] intValue] immediate:YES];
                
            }else if (reportPayload.formtypeid == [NSNumber numberWithLong:DR]){
                [dataManager deleteDamageReportFromStoreAndForward:reportPayload];
                [reportPayload setStatus: [NSNumber numberWithInt:SENT]];
                [dataManager addDamageReportToStoreAndForward:reportPayload];
                
                [dataManager requestDamageReportsRepeatedEvery:[[DataManager getReportsUpdateFrequencyFromSettings] intValue] immediate:YES];
            }
        }
        activeConnection = nil;
        
        [sendQueue removeObjectAtIndex:0];
        if(sendQueue.count >= 1){
            [self postReport: [sendQueue objectAtIndex:0]];
        }
        
    }else {
        NSLog(@"%@", [[Enums formTypeEnumToStringFull:reportPayload.formtypeid] stringByAppendingString:@" Failed to send...\n"]);
        
        activeConnection = nil;
        
        if(sendQueue.count >= 1){
            [self postReport: [sendQueue objectAtIndex:0]];
        }
    }
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"%@",error);
    
    activeConnection = nil;
    
    if(sendQueue.count >= 1){
        [self postReport: [sendQueue objectAtIndex:0]];
    }
}

//Called when app is started to queue up any unsent reports that may have been canceled from app closing
-(void)addCahcedReportsToSendQueue{
    
    NSMutableArray* Reports = [dataManager getAllSimpleReportsFromStoreAndForward];
    [Reports addObjectsFromArray:[dataManager getAllDamageReportsFromStoreAndForward]];
    
    for(ReportPayload *payload in Reports) {
        if([payload.isDraft isEqual:@0] && [payload.status isEqualToNumber:@(WAITING_TO_SEND)]) {
            [self addPayloadToSendQueue:payload];
        }
    }
}

@end



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
//  FieldReportPayload.m
//  Phinics_iOS
//
//

#import "UxoReportPayload.h"

@implementation UxoReportPayload

- (id)init {
    self = [super init];
    
    _messageData = [[UxoReportData alloc]init];
    
    _messageData.user = @"";
    _messageData.reportingunit = @"";
    _messageData.reportinglocation = @"";
    _messageData.latitude = @"0";
    _messageData.longitude = @"0";
    _messageData.contactinfo = @"";
    _messageData.uxotype = @"";
    _messageData.size = @"";
    _messageData.shape = @"";
    _messageData.color = @"";
    _messageData.condition = @"";
    _messageData.cbrncontamination = @"";
    _messageData.resourcethreatened = @"";
    _messageData.impactonmission = @"";
    _messageData.protectivemeasures = @"";
    _messageData.recommendedpriority = @"";
//    _messageData.image = @"";
    _messageData.fullPath = @"";
    
    return self;
}

-(void)parse{
    if([self.message length] != 0) {
        
        NSError *e = nil;
        self.messageData = [[UxoReportData alloc] initWithString:self.message error:&e];
    }
}

-(NSMutableDictionary *) toSqlMapping {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    if(self.isDraft) {
        [dataDictionary setObject: self.isDraft forKey:@"isDraft"];
    } else {
        [dataDictionary setObject: @0 forKey:@"isDraft"];
    }
    [dataDictionary setObject:self.incidentid forKey:@"incidentid"];
    
    if(self.formid != nil) {
        [dataDictionary setObject:self.formid forKey:@"formId"];
    } else {
        [dataDictionary setObject:@0 forKey:@"formId"];
    }
    
    [dataDictionary setObject:self.formtypeid forKey:@"formtypeid"];
    
//    if(self.senderUserId) {
//        [dataDictionary setObject:self.senderUserId forKey:@"senderUserId"];
//    } else {
//        [dataDictionary setObject:@0 forKey:@"senderUserId"];
//    }
    
    [dataDictionary setObject:self.usersessionid forKey:@"usersessionid"];
    [dataDictionary setObject:self.seqtime forKey:@"seqtime"];
    
    if(self.seqnum != nil) {
        [dataDictionary setObject:self.seqnum forKey:@"seqnum"];
    } else {
        [dataDictionary setObject:@0 forKey:@"seqnum"];
    }
    
    // Part A 'Incident Identification'
    if(_messageData.user != nil) {
        [dataDictionary setObject:_messageData.user forKey:@"user"];
    } else {
        [dataDictionary setObject:@"" forKey:@"user"];
    }
    
//    if(_messageData.image != nil) {
//        [dataDictionary setObject:_messageData.image forKey:@"image"];
//    } else {
//        [dataDictionary setObject:@"" forKey:@"image"];
//    }
    
    if(_messageData.fullPath != nil) {
        [dataDictionary setObject:_messageData.fullPath forKey:@"fullPath"];
    } else {
        [dataDictionary setObject:@"" forKey:@"fullPath"];
    }
    
    @try {
        [dataDictionary setObject:_messageData.user forKey:@"user"];
        [dataDictionary setObject:_messageData.reportingunit forKey:@"reportingUnit"];
        [dataDictionary setObject:_messageData.reportinglocation forKey:@"reportingLocation"];
        [dataDictionary setObject:_messageData.latitude forKey:@"latitude"];
        [dataDictionary setObject:_messageData.longitude forKey:@"longitude"];
        [dataDictionary setObject:_messageData.contactinfo forKey:@"contactInfo"];
        [dataDictionary setObject:_messageData.uxotype forKey:@"UxoType"];
        [dataDictionary setObject:_messageData.size forKey:@"Size"];
        [dataDictionary setObject:_messageData.shape forKey:@"Shape"];
        [dataDictionary setObject:_messageData.color forKey:@"Color"];
        [dataDictionary setObject:_messageData.condition forKey:@"Condition"];
        [dataDictionary setObject:_messageData.cbrncontamination forKey:@"CbrnContamination"];
        [dataDictionary setObject:_messageData.resourcethreatened forKey:@"ResourceThreatened"];
        [dataDictionary setObject:_messageData.impactonmission forKey:@"ImpactOnMission"];
        [dataDictionary setObject:_messageData.protectivemeasures forKey:@"ProtectiveMeasures"];
        [dataDictionary setObject:_messageData.recommendedpriority forKey:@"RecommendedPriority"];
        
        if(self.status) {
            [dataDictionary setObject:self.status forKey:@"status"];
        } else {
            [dataDictionary setObject:@-1 forKey:@"status"];
        }
        
    } @catch (NSException* e) {
        NSLog(@"Exception: %@", e);
    }
        [dataDictionary setObject:[self toJSONString] forKey:@"json"];
    
    return dataDictionary;
}

@end

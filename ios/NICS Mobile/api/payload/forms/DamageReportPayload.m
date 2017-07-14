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
//  DamageReportPayload.m
//  nics_iOS
//
//

#import "DamageReportPayload.h"

@implementation DamageReportPayload

- (id)init {
    self = [super init];
    
    _messageData = [[DamageReportData alloc]init];
    
    _messageData.drAownerLastName = @"";
    _messageData.drAownerFirstName = @"";
    _messageData.drAownerLandlinePhone = @"";
    _messageData.drAownerCellPhone = @"";
    _messageData.drAownerEmail = @"";
    
    _messageData.drBpropertyAddress = @"";
    _messageData.drBpropertyCity = @"";
    _messageData.drBpropertyZipCode = @"";
    _messageData.drBpropertyLatitude = @"0";
    _messageData.drBpropertyLongitude = @"0";
    
    _messageData.drCdamageInformation = [[NSArray alloc]init];
    _messageData.drDfullPath = @"";
    
    _messageData.user = @"";
    
    return self;
}

-(void)parse{
    if([self.message length] != 0) {
        
        NSError *e = nil;
        _messageData = [[DamageReportData alloc] initWithString:self.message  error:&e];
    }
}

-(NSMutableDictionary *) toSqlMapping {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    if(self.isDraft) {
        [dataDictionary setObject: self.isDraft forKey:@"isDraft"];
    } else {
        [dataDictionary setObject: @0 forKey:@"isDraft"];
    }
    
    [dataDictionary setObject:self.seqtime forKey:@"seqtime"];
    [dataDictionary setObject:self.incidentid forKey:@"incidentid"];
    
    if(self.formtypeid != nil) {
        [dataDictionary setObject:self.formtypeid forKey:@"formtypeid"];
    } else {
        [dataDictionary setObject:@0 forKey:@"formtypeid"];
    }
    
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
    
    if(_messageData != nil) {
        // Part A 'Property Owner Identification'
        if(_messageData.user != nil) {
            [dataDictionary setObject:_messageData.user forKey:@"user"];
        } else {
            [dataDictionary setObject:@"" forKey:@"user"];
        }
        [dataDictionary setObject:_messageData.drAownerLastName forKey:@"ownerLastName"];
        [dataDictionary setObject:_messageData.drAownerFirstName forKey:@"ownerFirstName"];
        [dataDictionary setObject:_messageData.drAownerLandlinePhone forKey:@"ownerLandlinePhone"];
        [dataDictionary setObject:_messageData.drAownerCellPhone forKey:@"ownerCellPhone"];
        [dataDictionary setObject:_messageData.drAownerEmail forKey:@"ownerEmail"];
        
        // Part B 'Property Information'
        [dataDictionary setObject:_messageData.drBpropertyAddress forKey:@"propertyAddress"];
        [dataDictionary setObject:_messageData.drBpropertyCity forKey:@"propertyCity"];
        [dataDictionary setObject:_messageData.drBpropertyZipCode forKey:@"propertyZipCode"];
        [dataDictionary setObject:_messageData.drBpropertyLatitude forKey:@"propertyLatitude"];
        [dataDictionary setObject:_messageData.drBpropertyLongitude forKey:@"propertyLongitude"];
        
        // Part C 'Damage Information'
        [dataDictionary setObject:_messageData.drCdamageInformation forKey:@"damageInformation"];
        [dataDictionary setObject:_messageData.drDfullPath forKey:@"fullPath"];
    }
    
    if(self.status) {
        [dataDictionary setObject:self.status forKey:@"status"];
    } else {
        [dataDictionary setObject:@-1 forKey:@"status"];
    }
    [dataDictionary setObject:[self toJSONString] forKey:@"json"];
    
    return dataDictionary;
}

@end

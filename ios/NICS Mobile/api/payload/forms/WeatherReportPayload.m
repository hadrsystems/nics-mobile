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
//  WeatherReportPayload.m
//  NICS Mobile
//
//
//

#import "WeatherReportPayload.h"

@implementation WeatherReportPayload

- (id)init {
    self = [super init];
    
    _messageData = [[WeatherReportData alloc]init];
    
    _messageData.user = @"";
    _messageData.status = @"Open";
    _messageData.datasource = @"";
    _messageData.latitude = @"0";
    _messageData.longitude = @"0";
    _messageData.elevation = @"";
    _messageData.drybulbtemp = @"";
    _messageData.wetbulbtemp = @"";
    _messageData.relativehumidity = @"";
    _messageData.winddirection = @"";
    _messageData.windspeed = @"";
    _messageData.aspect = @"";
    _messageData.physicallocation = @"";
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"hh:mm:ss a"];
    _messageData.timetaken = [formatter stringFromDate:[NSDate date]];
    
    return self;
}

-(void)parse{
    if([self.message length] != 0) {
        
        NSError *e = nil;
        self.messageData = [[WeatherReportData alloc] initWithString:self.message error:&e];
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
    [dataDictionary setObject:self.incidentname forKey:@"incidentname"];
    
    if(self.formid != nil) {
        [dataDictionary setObject:self.formid forKey:@"formId"];
    } else {
        [dataDictionary setObject:@0 forKey:@"formId"];
    }
    
    [dataDictionary setObject:self.formtypeid forKey:@"formtypeid"];
    
    [dataDictionary setObject:self.usersessionid forKey:@"usersessionid"];
    [dataDictionary setObject:self.seqtime forKey:@"seqtime"];
    
    if(self.seqnum != nil) {
        [dataDictionary setObject:self.seqnum forKey:@"seqnum"];
    } else {
        [dataDictionary setObject:@0 forKey:@"seqnum"];
    }
    
    if(_messageData.status != nil) {
        [dataDictionary setObject:_messageData.status forKey:@"status"];
    } else {
        [dataDictionary setObject:@"Open" forKey:@"status"];
    }
    
    @try {
        //        [dataDictionary setObject:_messageData.user forKey:@"user"];
        //        [dataDictionary setObject:_messageData.status forKey:@"status"];
        if(_messageData.user != nil) {
            [dataDictionary setObject:_messageData.user forKey:@"user"];
        } else {
            [dataDictionary setObject:@"" forKey:@"user"];
        }
        [dataDictionary setObject:_messageData.datasource forKey:@"datasource"];
        [dataDictionary setObject:_messageData.latitude forKey:@"latitude"];
        [dataDictionary setObject:_messageData.longitude forKey:@"longitude"];
        [dataDictionary setObject:_messageData.elevation forKey:@"elevation"];
        [dataDictionary setObject:_messageData.drybulbtemp forKey:@"drybulbtemp"];
        [dataDictionary setObject:_messageData.wetbulbtemp forKey:@"wetbulbtemp"];
        [dataDictionary setObject:_messageData.relativehumidity forKey:@"relativehumidity"];
        [dataDictionary setObject:_messageData.winddirection forKey:@"winddirection"];
        [dataDictionary setObject:_messageData.windspeed forKey:@"windspeed"];
        [dataDictionary setObject:_messageData.aspect forKey:@"aspect"];
        [dataDictionary setObject:_messageData.physicallocation forKey:@"physicallocation"];
        [dataDictionary setObject:_messageData.timetaken forKey:@"timetaken"];
        
        if(self.status != nil) {
            [dataDictionary setObject:self.status forKey:@"status"];
        } else {
            [dataDictionary setObject:@"Open" forKey:@"status"];
        }
        
    } @catch (NSException* e) {
        NSLog(@"Exception: %@", e);
    }
    [dataDictionary setObject:[self toJSONString] forKey:@"json"];
    
    return dataDictionary;
}


@end

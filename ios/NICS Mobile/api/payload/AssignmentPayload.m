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
//  AssignmentPayload.m
//  nics_iOS
//
//

#import "AssignmentPayload.h"

@implementation AssignmentPayload

- (BOOL)isEqual:(id)object {
    if(object != nil) {
        AssignmentPayload *otherPayload = (AssignmentPayload *) object;
        
        return [_phiOperationalPeriod isEqual:otherPayload.phiOperationalPeriod] && [_phiUnit isEqual:otherPayload.phiUnit];
    }
    
    return false;
}

- (NSDictionary *)getFormRepresentation {
    NSMutableDictionary *formFieldDictionary = [[NSMutableDictionary alloc] init];

    if(self.phiOperationalPeriod.start > 0) {
        NSDate* date = [NSDate dateWithTimeIntervalSince1970:self.phiOperationalPeriod.start/1000.0];
        [formFieldDictionary setObject:[[Utils getDateFormatter] stringFromDate:date] forKey:@"from"];
    } else {
        [formFieldDictionary setObject:@"N/A" forKey:@"from"];
    }
        
    if(self.phiOperationalPeriod.end > 0) {
        NSDate* date = [NSDate dateWithTimeIntervalSince1970:self.phiOperationalPeriod.end/1000.0];
        [formFieldDictionary setObject:[[Utils getDateFormatter] stringFromDate:date] forKey:@"to"];
    } else {
        [formFieldDictionary setObject:@"N/A" forKey:@"to"];
    }
    
    if(self.phiUnit.unitName) {
        [formFieldDictionary setObject:self.phiUnit.unitName forKey:@"unit_name"];
    } else {
        [formFieldDictionary setObject:@"N/A" forKey:@"unit_name"];
    }
    
    NSString *unitList = @"";
    if([self.phiResourcesAssigns count] > 0) {
        for(ResourcePayload *resource in self.phiResourcesAssigns) {
            unitList = [NSString stringWithFormat:@"%@%s", [unitList stringByAppendingString:[resource getNickname]], "\n"];
            if(resource.leader) {
                [formFieldDictionary setObject:[resource getNickname] forKey:@"leader"];
                [formFieldDictionary setObject:resource.userEmailAddr forKey:@"leader_contact"];
            }
        }
    } else {
        unitList = @"N/A";
    }
    [formFieldDictionary setObject:[NSString stringWithFormat:@"%lu", (unsigned long)[self.phiResourcesAssigns count]] forKey:@"#_persons"];
    [formFieldDictionary setObject:unitList forKey:@"personnel"];
    
    NSString *taskList = @"";
    if([self.phiTaskAssigns count] > 0) {
        for(TaskPayload *resource in self.phiTaskAssigns) {
            [resource.form parse];
            TaskFormMessage *message = resource.form.formMessage;
            
            NSString *taskInfo = [NSString stringWithFormat:@"%@%@%@%@%@%@%@%@%@%@%@%@%@%@%@%@%@",
                                  @"Name: ", message.taskName, @"\n",
                                  @"==========\n",
                                  @"Location: ", message.taskLocation, @"\n",
                                  @"Location Description: ", message.taskLocdescription, @"\n",
                                  @"Work Assignment: ", message.taskWorkassignment, @"\n",
                                  @"Special Instructions: ", message.taskSpecialinstructions, @"\n",
                                  @"==========\n\n"];
            
            taskList = [taskList stringByAppendingString:taskInfo];
            
        }
    } else {
        taskList = @"N/A";
    }
    [formFieldDictionary setObject:taskList forKey:@"task_list"];
    
    return formFieldDictionary;
}

- (BOOL)isNil {
    if(_phiResourcesAssigns == nil && _phiTaskAssigns == nil && _phiOperationalPeriod == nil && _phiUnit == nil) {
        return YES;
    }
    return NO;
}
@end

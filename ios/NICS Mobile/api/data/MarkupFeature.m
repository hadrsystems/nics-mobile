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
//  MarkupFeature.m
//  nics_iOS
//
//

#import "MarkupFeature.h"

@implementation MarkupFeature

-(NSMutableDictionary *) toSqlMapping {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    [dataDictionary setObject:self.collabRoomId forKey:@"collabRoomId"];
    
    if(self.dashStyle != nil) {
        [dataDictionary setObject:self.dashStyle forKey:@"dashStyle"];
    } else {
        [dataDictionary setObject:@"" forKey:@"dashStyle"];
    }
    
    if(self.featureattributes != nil) {
        [dataDictionary setObject:self.featureattributes forKey:@"featureattributes"];
    } else {
        [dataDictionary setObject:@"" forKey:@"featureattributes"];
    }
    
    if(self.featureId != nil) {
        [dataDictionary setObject:self.featureId forKey:@"featureId"];
    }else{
        [dataDictionary setObject:@"" forKey:@"featureId"];
    }
    
    if(self.fillColor != nil) {
        [dataDictionary setObject:self.fillColor forKey:@"fillColor"];
    } else {
        [dataDictionary setObject:@"" forKey:@"fillColor"];
    }
    
    
    if(self.graphic != nil) {
        [dataDictionary setObject:self.graphic forKey:@"graphic"];
    } else {
        [dataDictionary setObject:@"" forKey:@"graphic"];
    }
    
    if(self.graphicHeight != nil) {
        [dataDictionary setObject:self.graphicHeight forKey:@"graphicHeight"];
    } else {
        [dataDictionary setObject:@0 forKey:@"graphicHeight"];
    }
    
    if(self.graphicWidth != nil) {
        [dataDictionary setObject:self.graphicWidth forKey:@"graphicWidth"];
    } else {
        [dataDictionary setObject:@0 forKey:@"graphicWidth"];
    }
    
    if(self.gesture) {
        [dataDictionary setObject:self.gesture forKey:@"gesture"];
    } else {
        [dataDictionary setObject:@0 forKey:@"gesture"];
    }
    
    if(self.ip){
        [dataDictionary setObject:self.ip forKey:@"ip"];
    }else{
         [dataDictionary setObject:@"" forKey:@"ip"];
    }
    
    if(self.labelSize != nil) {
        [dataDictionary setObject:self.labelSize forKey:@"labelSize"];
    } else {
        [dataDictionary setObject:@0 forKey:@"labelSize"];
    }
    
    if(self.labelText != nil) {
        [dataDictionary setObject:self.labelText forKey:@"labelText"];
    } else {
        [dataDictionary setObject:@"" forKey:@"labelText"];
    }
    [dataDictionary setObject:self.username forKey:@"username"];
    
    if(self.seqNum != nil) {
        [dataDictionary setObject:self.seqNum forKey:@"seqNum"];
    }else {
        [dataDictionary setObject: @0 forKey:@"seqNum"];
    }
    
    if(self.strokeColor != nil) {
        [dataDictionary setObject:self.strokeColor forKey:@"strokeColor"];
    }else {
        [dataDictionary setObject: @0 forKey:@"strokeColor"];
    }
    
    if(self.strokeWidth != nil) {
        [dataDictionary setObject:self.strokeWidth forKey:@"strokeWidth"];
    } else {
        [dataDictionary setObject:@0 forKey:@"strokeWidth"];
    }
    
     if(self.seqtime != nil) {
         [dataDictionary setObject:self.seqtime forKey:@"seqtime"];
     }else{
         [dataDictionary setObject:@1 forKey:@"seqtime"];
     }
    
    if(self.topic != nil) {
        [dataDictionary setObject:self.topic forKey:@"topic"];
    }else{
        [dataDictionary setObject:@"" forKey:@"topic"];
    }
    
    if(self.type != nil) {
        [dataDictionary setObject:self.type forKey:@"type"];
    }else{
         [dataDictionary setObject:@"" forKey:@"type"];
    }
    
     if(self.opacity != nil) {
         [dataDictionary setObject:self.opacity forKey:@"opacity"];
     }else{
         [dataDictionary setObject:@0 forKey:@"opacity"];
     }
    

//    [dataDictionary setObject:[self getPointsString] forKey:@"geometry"];
    [dataDictionary setObject:self.geometry forKey:@"geometry"];
    if(self.geometryFiltered ==nil){
        [self filterGeometry];
    }
    [dataDictionary setObject:self.geometryFiltered forKey:@"geometryFiltered"];

    if(self.radius != nil) {
        [dataDictionary setObject:self.radius forKey:@"radius"];
    }else{
         [dataDictionary setObject:@0 forKey:@"radius"];
    }
    
    
    if(self.rotation != nil) {
        [dataDictionary setObject:self.rotation forKey:@"rotation"];
    } else {
        [dataDictionary setObject:@0 forKey:@"rotation"];
    }
    
//    if(self.lastupdate != nil){
//        [dataDictionary setObject:[self lastupdate] forKey:@"lastupdate"];
//    }else{
//        [dataDictionary setObject:@"" forKey:@"lastupdate"];
//    }
    
    NSString* jsonString =[self toJSONString];
    jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\\/" withString:@"/"];
    [dataDictionary setObject:jsonString forKey:@"json"];
    
    return dataDictionary;
}

-(NSString *) getPointsString {
    BOOL isFirst = true;
    NSMutableString *string = [NSMutableString new];
    
    for(NSArray *pointArray in self.geometryFiltered) {
        if(!isFirst) {
            [string appendString:@","];
        } else {
            isFirst = false;
        }
        [string appendFormat:@"%f%@%f", [[pointArray objectAtIndex:0] doubleValue], @" ", [[pointArray objectAtIndex:1] doubleValue]];
    }
    
    return string;
}

-(NSMutableArray *) getCLPointsArray {
    NSMutableArray *clPointsArray = [NSMutableArray new];
    
    if(self.geometryFiltered.count==0){
        [self filterGeometry];
    }
    

    
    for(NSArray *pointArray in self.geometryFiltered) {
        if(pointArray.count < 1){
            CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake(0, 0);
            [clPointsArray addObject:[NSValue valueWithBytes:&coordinate objCType:@encode(CLLocationCoordinate2D)]];
            return clPointsArray;
        }
        
        double latitude = [[pointArray objectAtIndex:0] doubleValue];
        double longitude = [[pointArray objectAtIndex:1] doubleValue];
        CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake(latitude, longitude);
        [clPointsArray addObject:[NSValue valueWithBytes:&coordinate objCType:@encode(CLLocationCoordinate2D)]];
    }
    
    return clPointsArray;
}

-(void) filterGeometry{
    NSString *geomTemp = _geometry;
    
    geomTemp = [geomTemp stringByReplacingOccurrencesOfString:@"(" withString:@""];
    geomTemp = [geomTemp stringByReplacingOccurrencesOfString:@")" withString:@""];
    
    geomTemp = [geomTemp stringByReplacingOccurrencesOfString:@"POLYGON" withString:@""];
    geomTemp = [geomTemp stringByReplacingOccurrencesOfString:@"POINT" withString:@""];
    geomTemp = [geomTemp stringByReplacingOccurrencesOfString:@"LINESTRING" withString:@""];
    
    NSArray* seperatedCommas = [geomTemp componentsSeparatedByString: @","];
    
    NSMutableArray *fullySeperated = [NSMutableArray new];
    
    //formatter doesn't work in all languages.
//    NSNumberFormatter *format = [[NSNumberFormatter alloc] init];
//    format.numberStyle = NSNumberFormatterDecimalStyle;
    
    for (int i = 0; i < seperatedCommas.count; i++) {
        NSArray* seperateSpaces = [seperatedCommas[i] componentsSeparatedByString: @" "];
        
        NSMutableArray* pointArray = [NSMutableArray new];
        
        //this method doesn't work in all languages.
//        NSNumber* num = [format numberFromString:[seperateSpaces objectAtIndex:1]];
        NSNumber* num = [NSNumber numberWithDouble:[(NSString *)[seperateSpaces objectAtIndex:1] doubleValue]];
        
        [pointArray addObject:num];
        
        
//        num = [format numberFromString:[seperateSpaces objectAtIndex:0]];
        num = [NSNumber numberWithDouble:[(NSString *)[seperateSpaces objectAtIndex:0] doubleValue]];
        [pointArray addObject:num];
        
        [fullySeperated addObject: pointArray];
    }
    
    _geometryFiltered = fullySeperated;
}

@end

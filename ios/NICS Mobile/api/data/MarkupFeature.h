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
//  MarkupFeature.h
//  nics_iOS
//
//

#import  <GoogleMaps/GoogleMaps.h>
#import "JSONModel.h"

@protocol MarkupFeature

@end

@interface MarkupFeature : JSONModel

@property NSNumber<Optional> *collabRoomId;
@property NSNumber<Optional> *id;
@property NSString<Optional> *dashStyle;
@property NSString<Optional> *featureattributes;
@property NSString<Optional> *featureId;
@property NSString<Optional> *fillColor;
@property NSString<Optional> *graphic;
@property NSNumber<Optional> *graphicHeight;
@property NSNumber<Optional> *graphicWidth;
@property NSNumber<Optional> *gesture;
@property NSString<Optional> *ip;
@property NSNumber<Optional> *labelSize;
@property NSString<Optional> *labelText;
@property NSString *username;
@property NSNumber<Optional> *usersessionId;
@property NSNumber<Optional> *seqNum;
@property NSString<Optional> *strokeColor;
@property NSNumber<Optional> *strokeWidth;
@property NSNumber<Optional> *seqtime;
@property NSString<Optional> *topic;
@property NSString *type;
@property NSNumber *opacity;
@property NSString *geometry;
@property NSArray<Optional> *geometryFiltered;
@property NSNumber<Optional> *radius;
@property NSNumber<Optional> *rotation;
@property NSString<Optional> *lastupdate;
@property NSString<Optional> *photoPath;
@property NSString<Optional> *reportTypeAndId;

-(NSMutableDictionary *) toSqlMapping;
-(NSString *) getPointsString;
-(NSMutableArray *) getCLPointsArray;
-(void) filterGeometry;

@end

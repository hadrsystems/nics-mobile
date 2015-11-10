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
//  WfsFeature.h
//  NICS Mobile
//
//

#import "JSONModel.h"

/*
 
 {
 "type":"Feature",
 "id":"phi_mdt_view.fid-4752317f_14ce1994aab_-45f6",
 "geometry”:
    {
    "type":"Point",
    "coordinates":[-71.26576910000001,42.459273]},
    "geometry_name":"geom",
    "properties":
        {
        "uid":431098,
        "id":"michael.flood@ll.mit.edu",
        "name":"michael.flood@ll.mit.edu",
        "description":"",
        "timestamp":"2015-04-22T15:14:48Z",
        "speed":0,
        "course":0,
        "extendeddata":null,
        "styler":null,
        "age":51.087253
        }
    }
 }
 
 */

@interface WfsFeature : NSObject//JSONModel

    @property (strong, nonatomic) NSString* type;
    @property (strong, nonatomic) NSString* id;

//geometry
    @property (strong, nonatomic) NSString* geoType;
    @property (strong, nonatomic) NSNumber* latitude;
    @property (strong, nonatomic) NSNumber* longitude;
    @property (strong, nonatomic) NSString* geometry_name;


//properties
    @property (strong, nonatomic) NSString* uid;
    @property (strong, nonatomic) NSString* propertyId;
    @property (strong, nonatomic) NSString* name;
    @property (strong, nonatomic) NSString* propDescription;
    @property (strong, nonatomic) NSString* timestamp;
    @property (strong, nonatomic) NSNumber* speed;
    @property (strong, nonatomic) NSNumber* course;
    @property (strong, nonatomic) NSString* extendeddata;
    @property (strong, nonatomic) NSString* styler;
    @property (strong, nonatomic) NSNumber* age;

-(void)setupWithDictionary:(NSDictionary*) feature;

@end


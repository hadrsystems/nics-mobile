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
//  MarkupSymbol.m
//  nics_iOS
//
//

#import "MarkupSegment.h"

@implementation MarkupSegment


- (id)initWithMap:(GMSMapView *)view feature:(MarkupFeature *)feature
{
    self = [super initWithMap:view feature:feature];
    
    if(self) {
        self.points = [feature getCLPointsArray];
        
        GMSMutablePath *path = [GMSMutablePath path];
        CLLocationCoordinate2D locationCoordinate;
        
        for(NSValue *value in self.points) {
            [value getValue:&locationCoordinate];
            [path addCoordinate:locationCoordinate];
        }
        self.path = path;
        
        _polyline = [GMSPolyline polylineWithPath:path];
        _polyline.strokeColor = [Utils colorWithHexString:feature.strokeColor];
        _polyline.strokeWidth = [feature.strokeWidth floatValue];
        
        _polyline.map = self.mapView;
    }

    return self;
}

- (void)removeFromMap {
    if(_polyline != nil) {
        _polyline.map = nil;
    }
}

@end

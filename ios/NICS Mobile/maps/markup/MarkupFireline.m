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

#import "MarkupFireline.h"

@implementation MarkupFireline


- (id)initWithMap:(GMSMapView *)view feature:(MarkupFeature *)feature
{
    self = [super initWithMap:view feature:feature];
    
    if(self) {
        self.points = [feature getCLPointsArray];
        
        GMSCoordinateBounds *lineBounds = [GMSCoordinateBounds new];
        
        
        GMSMutablePath *coordinatePath = [GMSMutablePath path];
        
        NSMutableArray *coordinates = [NSMutableArray new];
        CLLocation *location;
        CLLocationCoordinate2D markerLocation;
        
        for(id point in self.points) {
            [point getValue:&markerLocation];
            
            location = [[CLLocation alloc] initWithLatitude:markerLocation.latitude longitude:markerLocation.longitude];
            lineBounds = [lineBounds includingCoordinate:markerLocation];
            
            [coordinates addObject:location];
            [coordinatePath addCoordinate:location.coordinate];
        }
        self.path = coordinatePath;
        
        CLLocationCoordinate2D modSW = CLLocationCoordinate2DMake(lineBounds.southWest.latitude - 0.01, lineBounds.southWest.longitude - 0.01);
        CLLocationCoordinate2D modNE = CLLocationCoordinate2DMake(lineBounds.northEast.latitude + 0.01,  lineBounds.northEast.longitude + 0.01);
        
        lineBounds = [lineBounds includingCoordinate:modSW];
        lineBounds = [lineBounds includingCoordinate:modNE];
        
        CGPoint sw = [view.projection pointForCoordinate:modSW];
        CGPoint ne = [view.projection pointForCoordinate:modNE];
        
        UIBezierPath *path = [UIBezierPath bezierPath];
    
        int size = self.points.count * 2;
        float floatPoints[size];
        memset(floatPoints, 0, size * sizeof(int));
        
        for(int i = 0; i < coordinates.count; i++) {
            CLLocation *location = [coordinates objectAtIndex:i];
            
            CGPoint pt = [view.projection pointForCoordinate:location.coordinate];
            
            floatPoints[(i * 2)] = pt.x - sw.x;
            floatPoints[(i * 2) + 1] = pt.y - ne.y;
            
            if(i == 0) {
                [path moveToPoint:CGPointMake(floatPoints[0], floatPoints[1])];
            } else {
                [path addLineToPoint:CGPointMake(floatPoints[i * 2], floatPoints[(i * 2) + 1])];
            }
        }
        
        if([feature.dashStyle isEqualToString:@"map"]) {
            CGPoint start = CGPointMake(floatPoints[0], floatPoints[1]);
            CGPoint end = CGPointMake(floatPoints[size - 2], floatPoints[size - 1]);
            [path moveToPoint:start];
            [path addArcWithCenter:start radius:1.5 startAngle:0 endAngle:360 clockwise:YES];
            
            [path moveToPoint:end];
            [path addArcWithCenter:end radius:1.5 startAngle:0 endAngle:360 clockwise:YES];
        }
        
        float width = ne.x - sw.x;
        float height = sw.y - ne.y;
        
        if(width > 0 && height > 0 && width < 2048 && height < 2048) {
            UIImage *image = [self generateImageFromPath:path Size:CGSizeMake(width, height) Color:feature.strokeColor dashStyle:feature.dashStyle];
            
            _groundOverlay = [GMSGroundOverlay groundOverlayWithBounds:lineBounds icon:image];
            _groundOverlay.anchor = CGPointMake(0.5, 0.5);
            _groundOverlay.map = self.mapView;
        }
    }

    return self;
}

- (UIImage *)generateImageFromPath:(UIBezierPath *)path Size:(CGSize)size Color:textColor dashStyle:(NSString *)dashStyle
{
    UIGraphicsBeginImageContextWithOptions(size, NO, 0.0f);
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    [path setLineJoinStyle:kCGLineJoinRound];
    [[Utils colorWithHexString:@"black"] set];
    [path setLineWidth:2.0];
        
    if([dashStyle isEqualToString:@"plannedFireline"]) {
        [path setLineCapStyle:kCGLineCapSquare];
        [path setLineWidth:3.0];
        CGFloat dashes[] = {0, 8};
        [path setLineDash:dashes count:2 phase:0];
    } else if([dashStyle isEqualToString:@"secondary-fire-line"]) {
        [path setLineCapStyle:kCGLineCapRound];
        CGFloat dashes[] = {0, 8};
        [path setLineDash:dashes count:2 phase:0];
    } else if([dashStyle isEqualToString:@"fireSpreadPrediction"]) {
        [[UIColor colorWithRed:0.964844f green:0.578125f blue:0.117188f alpha:1.0f] set];
    } else if([dashStyle isEqualToString:@"completed-dozer-line"]) {
        
    } else if([dashStyle isEqualToString:@"proposedDozer"]) {
    
        
    } else if([dashStyle isEqualToString:@"fire-edge-line"]) {
        [[UIColor redColor] set];
        [path setLineWidth:2.0];
        [path stroke];
        
        [path setLineWidth:6.0];
        CGFloat dashes[] = {1, 8};
        [path applyTransform:CGAffineTransformMakeTranslation(2, 2)];
        [path setLineDash:dashes count:2 phase:0];
        
    } else if([dashStyle isEqualToString:@"map"]) {
        [[Utils colorWithHexString:@"black"] set];
        [path setLineWidth:4.0];
        [path stroke];
        
        [[UIColor colorWithRed:0.964844f green:0.578125f blue:0.117188f alpha:1.0f] set];
        [path setLineWidth:2.0];
    }
    
    [path stroke];
    CGContextAddPath(context, path.CGPath);
    
    // transfer image
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}

- (void)removeFromMap {
    if(_groundOverlay != nil) {
        _groundOverlay.map = nil;
    }
}

@end

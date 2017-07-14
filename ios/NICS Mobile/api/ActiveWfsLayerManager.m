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
//  ActiveWfsLayerManager.m
//  NICS Mobile
//
//

#import "ActiveWfsLayerManager.h"

@interface ActiveWfsLayerManager ()

@end

@implementation ActiveWfsLayerManager

NSMutableArray* trackingLayers;


+(void) setTrackingLayers :(NSMutableArray*) newTrackingLayers {
    trackingLayers = newTrackingLayers;
    
    TrackingLayerPayload* damagePayload = [[TrackingLayerPayload alloc]init];
    damagePayload.displayname = NSLocalizedString(@"SCOUT Damage Surveys",nil);
    damagePayload.layername = @"nics_dmgrpt";
    damagePayload.internalurl = [[DataManager getInstance] getGeoServerFromSettings];
    
    TrackingLayerPayload* generalPayload = [[TrackingLayerPayload alloc]init];
    generalPayload.displayname = NSLocalizedString(@"SCOUT General Messages",nil);
    generalPayload.layername = @"nics_sr";
    generalPayload.internalurl = [[DataManager getInstance] getGeoServerFromSettings];
    
    TrackingLayerPayload* explosivePayload = [[TrackingLayerPayload alloc]init];
    explosivePayload.displayname = NSLocalizedString(@"SCOUT Explosive Reports",nil);
    explosivePayload.layername = @"nics_urrpt";
    explosivePayload.internalurl = [[DataManager getInstance] getGeoServerFromSettings];
    
    [trackingLayers addObject:damagePayload];
    [trackingLayers addObject:generalPayload];
    [trackingLayers addObject:explosivePayload];
}
+(NSMutableArray*) getTrackingLayers{ return trackingLayers;}

+(void) UpdateTrackingLayer:(TrackingLayerPayload*)newLayer{
    
    for(int i = 0; i < trackingLayers.count; i++){
        
        TrackingLayerPayload* layer = [trackingLayers objectAtIndex:i];
        if([layer.displayname isEqualToString:newLayer.displayname]){
            layer = newLayer;
        }
    }
}

+(NSMutableArray*) GetAllActiveFeatures{
    
    NSMutableArray *features = [[NSMutableArray alloc]init];
    
    for(TrackingLayerPayload* layer in trackingLayers){
        if([[DataManager getInstance]getTrackingLayerEnabled:layer.displayname]){
            [features addObjectsFromArray:[layer features]];
        }
    }
    return features;
}

@end

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

@interface TrackingLayer ()
@end

@implementation TrackingLayer
-(id)initWithParams:(NSString*) newTitle : (NSString*) newTypeNameUrl{
   
    self = [super init];
    self.title = newTitle;
    self.typeNameURL = newTypeNameUrl;
    self.active = FALSE;
    
    return self;
}

@end




@interface ActiveWfsLayerManager ()

@end

@implementation ActiveWfsLayerManager

NSMutableArray* trackingLayers;
NSMutableArray* wfsFeatures;

+ (void) initialize {

    //init tracking layers
    trackingLayers = [[NSMutableArray alloc]init];
    [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"NICS Damage Surveys",nil)  : @"nics_dmgrpt"]];
    [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"NICS General Messages",nil)  : @"nics_sr"]];
    [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"NICS Mobile Users",nil) : @"phi_mdt_view"]];
    [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"SARApp PLI",nil)  : @"sar_view"]];
    
    if([DataManager getCalTrackingEnabledFromSettings]){
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-BEU/XMY AVL",nil)  : @"ca_beu_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-COR-HMT AVL",nil)  : @"ca_corona_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-ORC AVL",nil)  : @"ca_ocfa_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-VNC AVL",nil)  : @"ca_ventura_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-XFR AVL",nil)  : @"ca_fku_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-XMY-XSL (RIV temp.) AVL",nil)  : @"avlnmea_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-XRI AVL",nil)  : @"avlxriground_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-XSD-RCIP AVL",nil)  : @"avlrcip_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CA-XSL AVL",nil)  : @"ca_slu_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"CDF AFF",nil)  : @"avlxriair_view"]];
        [trackingLayers addObject:[[TrackingLayer alloc]initWithParams: NSLocalizedString(@"Delorme PLI",nil)  : @"avldelorme_view"]];
    }
}

+(BOOL) isTrackingLayerOn:(NSString*)layerName{

    for(TrackingLayer* layer in trackingLayers){
        if([layer.title isEqualToString:layerName]){
            return layer.active;
        }
    }
    return false;
}

+(NSMutableArray*) getTrackingLayers{ return trackingLayers;}

+(void) setTrackingLayerActiveAtIndex: (int)index : (bool) isActive{
    TrackingLayer *layer = [trackingLayers objectAtIndex:index];
    layer.active = isActive;
    trackingLayers[index] = layer;
}

+(NSMutableArray*) getWfsFeatures{
    return wfsFeatures;
}
+(void)setWfsFeatures : (NSMutableArray*) newFeatures{
    wfsFeatures = newFeatures;
}

@end

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
//  WfsXmlParser.h
//  NICS Mobile
//
//

#import <Foundation/Foundation.h>
#import "WfsFeature.h"
#import "TrackingLayerPayload.h"
#import "ActiveWfsLayerManager.h"

@protocol WfsXmlFinishedParsingDelegate <NSObject>

-(void)WfsXmlParsingComplete : (NSMutableArray*)features : (TrackingLayerPayload*)layer;

@end

@interface WfsXmlParser : NSObject <NSXMLParserDelegate>{
    __unsafe_unretained id<WfsXmlFinishedParsingDelegate> delegate;
}
@property(nonatomic,assign)id delegate;
@property TrackingLayerPayload* layer;

@property NSXMLParser *xmlParser;

@property NSMutableArray* featureStorage;
//@property WfsFeature* currentFeature;
@property NSMutableDictionary* currentFeatureDictionary;
@property NSMutableDictionary* currentPropertiesDictionary;
@property NSMutableArray* currentCoordinates;

@property NSString* currentElement;
@property NSString* foundValue;

-(void)parseXml : (NSData*)xmlData : (TrackingLayerPayload*) layer;

@end

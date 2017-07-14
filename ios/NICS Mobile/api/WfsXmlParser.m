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
//  WfsXmlParser.m
//  NICS Mobile
//
//

#import "WfsXmlParser.h"

@implementation WfsXmlParser

-(void)parseXml : (NSData*)xmlData : (TrackingLayerPayload*) layer {
    self.xmlParser = [[NSXMLParser alloc] initWithData:xmlData];
    self.xmlParser.delegate = self;
    self.layer = layer;
    
    self.foundValue = @"";
    [self.xmlParser parse];
}

-(void)parserDidStartDocument:(NSXMLParser*)parser{
    _featureStorage = [[NSMutableArray alloc]init];
}

-(void)parserDidEndDocument:(NSXMLParser *)parser{
//    if([delegate respondsToSelector:@selector(XmlParsingComplete:features:layer:)])
//    {
//        [delegate WfsXmlParsingComplete : _featureStorage : _layer];
//    }
    
    //Would be cleaner to return array in a delegate rather than directly updating the trackinglayers from the parser.
    _layer.features = _featureStorage;
    [ActiveWfsLayerManager UpdateTrackingLayer: _layer];
}

-(void)parser:(NSXMLParser *)parser parseErrorOccurred:(NSError *)parseError{
    NSLog(@"%@", [parseError localizedDescription]);
}

-(void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict{
    
    // If the current element name is equal to "geoname" then initialize the temporary dictionary.
    if ([elementName isEqualToString:@"gml:featureMember"]) {
//        self.currentFeature = [[WfsFeature alloc]init];
        _currentFeatureDictionary = [[NSMutableDictionary alloc]init];
        
        _currentPropertiesDictionary= [[NSMutableDictionary alloc]init];
        _currentCoordinates = [[NSMutableArray alloc]init];
        [_currentCoordinates addObject:@(0.0)];
        [_currentCoordinates addObject:@(0.0)];
        
//        [self.currentFeatureDictionary[@"properties"] setValue:@"hello" forKey:@"age"];
//        NSString *test =self.currentFeatureDictionary[@"properties"][@"age"];
    }
    // Keep the current element.
    self.currentElement = elementName;
}

-(void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName{
    
    if ([elementName isEqualToString:@"gml:featureMember"]) {
        
        [self.currentFeatureDictionary setObject:_currentPropertiesDictionary forKey:@"properties"];
        
        NSMutableDictionary* geom = [[NSMutableDictionary alloc]init];
        [geom setObject:_currentCoordinates forKey:@"coordinates"];
        
        [self.currentFeatureDictionary setObject:geom forKey:@"geometry"];
        
        WfsFeature * feature = [[WfsFeature alloc]init];
        [feature setupWithDictionary:_currentFeatureDictionary];
        [_featureStorage addObject:feature];
    
    }else if ([elementName rangeOfString:@"type" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentFeatureDictionary setValue:_foundValue forKey:@"type"];
    }else if ([elementName rangeOfString:@"objectid" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentFeatureDictionary setValue:_foundValue forKey:@"id"];
    }else if ([elementName rangeOfString:@"lat" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentCoordinates replaceObjectAtIndex:1 withObject: [NSNumber numberWithDouble: [_foundValue doubleValue]]];
    }else if ([elementName rangeOfString:@"lon" options:NSCaseInsensitiveSearch].location != NSNotFound){   //0
                [_currentCoordinates replaceObjectAtIndex:0 withObject: [NSNumber numberWithDouble: [_foundValue doubleValue]]];
    }else if ([elementName rangeOfString:@"heading" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentPropertiesDictionary setObject:_foundValue forKey:@"course"];
    }else if ([elementName rangeOfString:@"speed" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentPropertiesDictionary setObject:_foundValue forKey:@"speed"];
    }else if ([elementName rangeOfString:@"datetime" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentPropertiesDictionary setObject:_foundValue forKey:@"timestamp"];
    }else if ([elementName rangeOfString:@"last_updated" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentPropertiesDictionary setObject:_foundValue forKey:@"timestamp"];
    }else if ([elementName rangeOfString:@"name" options:NSCaseInsensitiveSearch].location != NSNotFound){
        [_currentPropertiesDictionary setObject:_foundValue forKey:@"name"];
    }else{
        [_currentPropertiesDictionary setObject:_foundValue forKey:elementName];
    }

    self.foundValue = @"";
}

-(void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string{
    self.foundValue = string;
}

@end

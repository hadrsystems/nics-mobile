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
//  WeatherView.m
//  nics_iOS
//
//

#import "WeatherView.h"

@implementation WeatherView

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if(self) {
        [self setup];
    }
    return self;
}

- (void)setup {
    _numberFormatter = [NSNumberFormatter new];
    
    [[NSBundle mainBundle] loadNibNamed:@"WeatherView" owner:self options:nil];
    [self addSubview:self.view];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWeatherUpdate:) name:@"weatherUpdateReceived" object:nil];
    
}

- (void)handleWeatherUpdate:(NSNotification *)notification {
   WeatherPayload *payload = (WeatherPayload *)[notification object];
    
    _currentLocation.text = payload.productionCenter;
    _temperatureFarenheight.text = [NSString stringWithFormat:@"%d%@", [payload.currentobservation.Temp intValue], @" °F"];
    [_temperatureFarenheight sizeToFit];
    
    _temperatureCelcius.text = [NSString stringWithFormat:@"%.f%@", round((([[_numberFormatter numberFromString:payload.currentobservation.Temp] intValue] - 32) * 5) / 9), @" °C" ];
    [_temperatureCelcius sizeToFit];
    
  //  _description.text = [payload.data.weather objectAtIndex:0];
    
    NSMutableCharacterSet *nonNumberCharacterSet = [NSMutableCharacterSet decimalDigitCharacterSet];
    [nonNumberCharacterSet invertedSet];
    
    NSString* imageName = [[[[[[payload.data.iconLink objectAtIndex:0] lastPathComponent] componentsSeparatedByString:@"."] objectAtIndex:0] componentsSeparatedByCharactersInSet:nonNumberCharacterSet] componentsJoinedByString:@""];
    imageName = [imageName stringByReplacingOccurrencesOfString:@"m_" withString:@""];
    
    _imageView.image = [UIImage imageNamed:imageName];
}


- (void) dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end

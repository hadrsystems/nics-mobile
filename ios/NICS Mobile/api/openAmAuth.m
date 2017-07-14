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
//  openAmAuth.m
//  NICS Mobile
//
//

#import "openAmAuth.h"

NSString* myUsername;
NSString* myPassword;

@implementation openAmAuth

static DataManager *dataManager;
static MultipartPostQueue* mMultipartPostQueue;

- (id)init {
    self = [super init];
    
    dataManager = [DataManager getInstance];
    mMultipartPostQueue = [MultipartPostQueue getInstance];
    _AuthValue = [dataManager getAuthToken];
    return self;
}

-(BOOL)setupAuth:(NSString*) username :(NSString*) password{
    
    myUsername = username;
    myPassword = password;
    
    if(![[dataManager getPassword] isEqualToString:password]){
        _AuthValue = nil;
        [dataManager setAuthToken:nil];
    }
    
    if(_AuthValue == nil){
        return [self requestAuthToken:username :password];
    }else{
        if([self validateAuthToken: _AuthValue :password]){
            return true;
        }else{
            return [self requestAuthToken:username :password];
        }
    }
}

-(NSDictionary*)getCookies{
    NSDictionary *properties = [NSDictionary dictionaryWithObjectsAndKeys:
                                [dataManager getCookieDomainForCurrentServer], NSHTTPCookieDomain,
                                @"/", NSHTTPCookiePath,  // IMPORTANT!
                                @"iPlanetDirectoryPro", NSHTTPCookieName,
                                _AuthValue, NSHTTPCookieValue,
                                nil];
    NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:properties];
    
    NSDictionary *properties2 = [NSDictionary dictionaryWithObjectsAndKeys:
                                [dataManager getCookieDomainForCurrentServer], NSHTTPCookieDomain,
                                @"/", NSHTTPCookiePath,  // IMPORTANT!
                                @"AMAuthCookie", NSHTTPCookieName,
                                _AuthValue, NSHTTPCookieValue,
                                nil];
    NSHTTPCookie *cookie2 = [NSHTTPCookie cookieWithProperties:properties2];
    
    NSArray* cookies = [NSArray arrayWithObjects: cookie,cookie2, nil];
    return [NSHTTPCookie requestHeaderFieldsWithCookies:cookies];
}

-(BOOL) requestAuthToken:(NSString*) username :(NSString*) password{

    NSLog(@"Requesting OpenAM Auth");

        NSInteger statusCode = -1;
        
        NSURL* postUrl = [NSURL URLWithString:[[dataManager getAuthServerFromSettings] stringByAppendingString:@"json/authenticate"]];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:postUrl];
        
        [request setValue:username forHTTPHeaderField:@"X-OpenAM-Username"];
        [request setValue:password forHTTPHeaderField:@"X-OpenAM-Password"];
        [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    
        NSData * json = [NSJSONSerialization dataWithJSONObject:[[NSDictionary alloc] init] options:NSJSONWritingPrettyPrinted error:nil];
        
        NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[json length]];
        [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
        [request setHTTPMethod:@"POST"];
        
        NSHTTPURLResponse *response = nil;
        NSError *error = nil;
        //getting the data
        NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        
        statusCode = [response statusCode];
        _OpenAmResponse = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
        OpenAMAuthenticationData *message = [[OpenAMAuthenticationData alloc] initWithString:_OpenAmResponse error:&error];
    
        //need to pull token from rokenResponse
        _AuthValue = message.tokenId;
        [dataManager setAuthToken:_AuthValue];
        
        if(statusCode == 412) {
            NSLog(@"User already logged in...");
            return true;
            
        } else if(statusCode == 200 || statusCode == 201){
            NSLog(@"Successfully logged in...");
            return true;
        }else{
            NSLog(_OpenAmResponse);
            return false;
        }
}

-(BOOL) validateAuthToken:(NSString*) tokenId :(NSString*) password {

    NSLog(@"Validating existing OpenAM Auth");
    
    NSInteger statusCode = -1;
    
    NSString* encodedString = [tokenId stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    encodedString = [@"identity/isTokenValid?tokenid=" stringByAppendingString:encodedString];
    
    NSURL* postUrl = [NSURL URLWithString:[[dataManager getAuthServerFromSettings] stringByAppendingString:encodedString]];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:postUrl];
    [request setHTTPMethod:@"GET"];
    
    NSHTTPURLResponse *response = nil;
    NSError *error = nil;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    
    statusCode = [response statusCode];
    _OpenAmResponse = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
    
    NSRange range = [_OpenAmResponse rangeOfString:@"true"];
    
    if (range.location == NSNotFound) {
        return false;
    }else {
        return true;
    }
}

- (NSString *) synchronousGetFromUrl:(NSString *)url statusCode:(NSInteger *)statusCode {
    
    NSURL* getUrl;
    if(![url hasPrefix:@"http"]) {
        getUrl = [NSURL URLWithString:[[dataManager getServerFromSettings] stringByAppendingString:url]];
    } else {
        getUrl = [NSURL URLWithString:url];
    }
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:getUrl];
    
    [request setValue:_AuthValue forHTTPHeaderField:@"Authorization"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setValue:myUsername forHTTPHeaderField:@"CUSTOM-uid"];
    [request setAllHTTPHeaderFields:[self getCookies]];
    
    [request setHTTPMethod:@"GET"];
    
    NSHTTPURLResponse *response = nil;
    NSError *error = nil;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    
    *statusCode = [response statusCode];
    return [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
}

- (NSString *) synchronousPostToUrl:(NSString *)url postData:(NSData *)postData length:(NSUInteger)length statusCode:(NSInteger *)statusCode {
    
    NSURL* postUrl = [NSURL URLWithString:[[dataManager getServerFromSettings] stringByAppendingString:url]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:postUrl];
    
    [request setAllHTTPHeaderFields:[self getCookies]];
    [request setValue:_AuthValue forHTTPHeaderField:@"AMAuthCookie"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setValue:myUsername forHTTPHeaderField:@"CUSTOM-uid"];
    [request setValue:[NSString stringWithFormat:@"%lu", (unsigned long)length] forHTTPHeaderField:@"Content-Length"];
    
    [request setHTTPBody:postData];
    [request setHTTPMethod:@"POST"];
    
    NSLog(@"<NSURLRequest %@>", request);
    
    NSHTTPURLResponse *response = nil;
    NSError *error = nil;
    NSData *responseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    
    *statusCode = [response statusCode];
    return [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
}

- (NSURLConnection *) synchronousMultipartPostToUrl:(NSString *)url postData:(NSData *)postData imageName:(NSString *)imageName requestParams:(NSMutableDictionary *)requestParams statusCode:(NSInteger *)statusCode {

    // the boundary string : a random string, that will not repeat in post data, to separate post data fields.
    NSString *boundaryConstant = @"----------V2ymHFg03ehbqgZCaKO6jy";
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundaryConstant];
    
    //create the body
    NSMutableData *postBody = [NSMutableData data];
    [postBody appendData:[[NSString stringWithFormat:@"--%@\r\n", boundaryConstant] dataUsingEncoding:NSUTF8StringEncoding]];
    
    //add key values from the NSDictionary object
    NSEnumerator *keys = [requestParams keyEnumerator];
    int i;
    for (i = 0; i < [requestParams count]; i++) {
        NSString *tempKey = [keys nextObject];
        [postBody appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"\r\n\r\n",tempKey] dataUsingEncoding:NSUTF8StringEncoding]];
        [postBody appendData:[[NSString stringWithFormat:@"%@",[requestParams objectForKey:tempKey]] dataUsingEncoding:NSUTF8StringEncoding]];
        [postBody appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n", boundaryConstant] dataUsingEncoding:NSUTF8StringEncoding]];
    }
    
    //add data field and file data  filename=\"%@\" \r\n"
    [postBody appendData:[[@"Content-Disposition: form-data; name=\"data\";  filename=\"" stringByAppendingFormat:@"%@%@", imageName, @"\" \r\n"] dataUsingEncoding:NSUTF8StringEncoding]];
    [postBody appendData:[@"Content-Type: application/octet-stream\r\n\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
    [postBody appendData:[NSData dataWithData:postData]];
    [postBody appendData:[[NSString stringWithFormat:@"\r\n--%@--\r\n", boundaryConstant] dataUsingEncoding:NSUTF8StringEncoding]];
    
    NSURL* postUrl = [NSURL URLWithString:[[dataManager getServerFromSettings] stringByAppendingString:url]];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:postUrl];
    [request setHTTPMethod:@"POST"];
    [request setValue:contentType forHTTPHeaderField: @"Content-Type"];
    [request setValue:myUsername forHTTPHeaderField:@"CUSTOM-uid"];
    [request setAllHTTPHeaderFields:[self getCookies]];
    
    //add the body to the post
    [request setHTTPBody:postBody];
    
    return [[NSURLConnection alloc] initWithRequest:request delegate:mMultipartPostQueue startImmediately:NO];
}

@end

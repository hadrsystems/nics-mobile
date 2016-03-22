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
//  ReportInfoWindow.m
//  NICS Mobile
//
//
//

#import "ReportInfoWindow.h"

@implementation ReportInfoWindow

-(void)setupImage:(NSString*)imagePath{
    
    if([imagePath isEqualToString:@"marker"]){
     
//        CGRect labelFrame = _label1.frame;
//        labelFrame.origin.x = 0;
//        _label1.frame = labelFrame;
        
//        [_imageView setHidden:YES];
        [_imageView setImage:_marker.icon];
        [_activityIndicator setHidden:YES];
        

        
    }else if(imagePath!=nil){
    
    _imagePath = imagePath;
    
    ALAssetsLibrary *assetsLibrary = [ALAssetsLibrary new];
    
    [_activityIndicator setHidden:NO];
    [_activityIndicator startAnimating];
    
    NSArray* splitImagePath = [imagePath componentsSeparatedByString: @"/"];
    
    if([splitImagePath[0] isEqualToString:@"assets-library:"]){    //if the fullpath is a locally cached image that wasn't from web (if report was a draft mainly)
        
        [assetsLibrary assetForURL:[NSURL URLWithString:imagePath] resultBlock:^(ALAsset *asset) {
            _imageView.image = [UIImage imageWithCGImage:[[asset defaultRepresentation] fullResolutionImage]];
            [_activityIndicator stopAnimating];
            [_activityIndicator setHidden:YES];
            
        } failureBlock:^(NSError *error) {
            [_activityIndicator stopAnimating];
            [_activityIndicator setHidden:YES];
        }
         ];
        
        
        
    }else{
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsPath = [paths objectAtIndex:0]; //Get the docs directory
        documentsPath = [documentsPath stringByAppendingString:@"/"];
        documentsPath = [documentsPath stringByAppendingString:splitImagePath[splitImagePath.count-1]];
        
        
        NSData *pngData = [NSData dataWithContentsOfFile:documentsPath];
        
        if(pngData != nil){
            UIImage *image = [UIImage imageWithData:pngData];
            _imageView.image = image;
            [_activityIndicator stopAnimating];
            [_activityIndicator setHidden:YES];
        }else{
            [self downloadImageWithURL:[NSURL URLWithString:imagePath] completionBlock:^(BOOL succeeded, UIImage *image) {
                if(succeeded) {
                    _imageView.image = image;
                    
                    if (_mapview.selectedMarker == _marker) {  //Only set if the selected marker equals to the downloaded marker
                        [_mapview setSelectedMarker:_marker];
                    }
                    
                }
                [_activityIndicator stopAnimating];
                [_activityIndicator setHidden:YES];
            }];
        }
    }
    
} else {
    [_activityIndicator stopAnimating];
    [_activityIndicator setHidden:YES];
    NSLog(@"null fullpath sent to imageSelector widget");
}

}


- (void)downloadImageWithURL:(NSURL *)url completionBlock:(void (^)(BOOL succeeded, UIImage *image))completionBlock
{
    NSLog(@"%@", @"Starting download");
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:500];
    
    [request setValue:RestClient.authValue forHTTPHeaderField:@"Authorization"];
    [request setHTTPMethod:@"GET"];
    
    [NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
                               if(!error) {
                                   UIImage *image = [[UIImage alloc] initWithData:data];
                                   completionBlock(YES, image);
                                   
                                   NSArray* splitImagePath = [_imagePath componentsSeparatedByString: @"/"];
                                   
                                   NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
                                   NSString *documentsPath = [paths objectAtIndex:0]; //Get the docs directory
                                   documentsPath = [documentsPath stringByAppendingString:@"/"];
                                   documentsPath = [documentsPath stringByAppendingString:splitImagePath[splitImagePath.count-1]];
                                   [data writeToFile:documentsPath atomically:YES]; //Write the file
                                   
                                   
                                   NSLog(@"%@", @"Finished download");
                               } else {
                                   NSLog(@"%@", [error localizedDescription]);
                                   NSLog(@"%@", @"Failed download");
                                   completionBlock(NO, nil);
                               }
                           }
     
     ];
}


@end

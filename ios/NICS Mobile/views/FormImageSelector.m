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
//  FormEditText.m
//  nics_iOS
//
//

#import "FormImageSelector.h"
#import "IncidentButtonBar.h"

@implementation FormImageSelector

- (id)init
{
    self = [super init];
    [self setup];
    return self;
}

- (void)setup {
    [super setup];
    self.type = @"imageSelector";
    [[NSBundle mainBundle] loadNibNamed:@"FormImageSelector" owner:self options:nil];
    
    for(UIView * subview in self.view.subviews) {
        [subview setUserInteractionEnabled:YES];
        [self addSubview:subview];
    }
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.superview.frame.size.width, self.view.frame.size.height);
    
    self.dataManager = [DataManager getInstance];
    _assetsLibrary = [ALAssetsLibrary new];
//    self.field.delegate = self.view;
}

- (void)setLayout: (CGRect)formFrame{
    
    CGRect frame = self.view.frame;
    frame.size.width = formFrame.size.width -10;
    self.view.frame = frame;
    
    frame = _CameraImageView.frame;
    frame.size.width = formFrame.size.width -10;
    _CameraImageView.frame = frame;
    
    frame = _CaptureImageButton.frame;
    frame.size.width = (formFrame.size.width-10) /2;
    _CaptureImageButton.frame = frame;
    
    frame = _BrowseImageButton.frame;
    frame.size.width = (formFrame.size.width-10) /2;
    frame.origin.x = frame.size.width;
    _BrowseImageButton.frame = frame;

}

- (void)setData: (NSString *) fullPath : (UINavigationController *) view : (bool)readOnly{
    
    _formView = view;
    [_CameraImageView setImage: nil];
    _readOnly = readOnly;
    
    if(readOnly){
        [_CaptureImageButton setHidden:YES];
        [_BrowseImageButton setHidden:YES];
        [_CaptureImageButton setEnabled:FALSE];
         [_BrowseImageButton setEnabled:FALSE];
    }else{
        [_CaptureImageButton setHidden:NO];
        [_BrowseImageButton setHidden:NO];
        [_CaptureImageButton setEnabled:true];
        [_BrowseImageButton setEnabled:true];
    }
    
    // Update the user interface for the detail item.
    if(fullPath != nil) {
        
        [_activityIndicator setHidden:NO];
        [_activityIndicator startAnimating];
        
        _imagePath = fullPath;

        NSArray* splitImagePath = [_imagePath componentsSeparatedByString: @"/"];
        
        if([splitImagePath[0] isEqualToString:@"assets-library:"]){    //if the fullpath is a locally cached image that wasn't from web (if report was a draft mainly)
            
            [_assetsLibrary assetForURL:[NSURL URLWithString:_imagePath] resultBlock:^(ALAsset *asset) {
                    _CameraImageView.image = [UIImage imageWithCGImage:[[asset defaultRepresentation] fullResolutionImage]];
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
                _CameraImageView.image = image;
                [_activityIndicator stopAnimating];
                [_activityIndicator setHidden:YES];
            }else{
                [self downloadImageWithURL:[NSURL URLWithString:_imagePath] completionBlock:^(BOOL succeeded, UIImage *image) {
                    if(succeeded) {
                        _CameraImageView.image = image;
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

- (NSString *)getData{
    if(_imagePath == nil){
        _imagePath = @"";
    }
    return _imagePath;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *touchedView = [super hitTest:point withEvent:event];
    
    if(!_readOnly){
        if(point.x > self.BrowseImageButton.frame.origin.x && point.x < self.BrowseImageButton.frame.origin.x + self.BrowseImageButton.frame.size.width && point.y > self.BrowseImageButton.frame.origin.y && point.y < self.BrowseImageButton.frame.origin.y + self.BrowseImageButton.frame.size.height) {
            
            [self BrowseGalleryButtonPressed];
            
        }else if(point.x > self.CaptureImageButton.frame.origin.x && point.x < self.CaptureImageButton.frame.origin.x + self.CaptureImageButton.frame.size.width && point.y > self.CaptureImageButton.frame.origin.y && point.y < self.CaptureImageButton.frame.origin.y + self.CaptureImageButton.frame.size.height) {
            
            [self captureImageButtonPressed];
        }
    }
    
    return touchedView;
}

- (void)captureImageButtonPressed {
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        
        UIImagePickerController *imagePicker = [UIImagePickerController new];
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        imagePicker.allowsEditing = YES;
        [imagePicker setDelegate:self];
        
        if([self.dataManager isIpad]){
            [IncidentButtonBar OpenImagePickerCameraForTablet:imagePicker];
        }else{
            [[self.dataManager getOverviewController] presentViewController:imagePicker animated:YES completion:nil];
        }
        
    } else {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error",nil) message:NSLocalizedString(@"Device has no camera",nil) delegate:nil cancelButtonTitle:NSLocalizedString(@"OK",nil) otherButtonTitles: nil];
        [alertView show];
    }
    
}

- (void)BrowseGalleryButtonPressed {
    UIImagePickerController *imagePicker = [UIImagePickerController new];
    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    
    [imagePicker setDelegate:self];
    
    if([self.dataManager getIsIpad] == true){
        UIPopoverController *myPopOver = [[UIPopoverController alloc]initWithContentViewController:imagePicker];
        CGRect displayFrom = _formView.view.frame;
        [myPopOver presentPopoverFromRect:displayFrom inView: [IncidentButtonBar GetOverview].view permittedArrowDirections:UIPopoverArrowDirectionLeft animated:YES];
        
    }else{
        [[self.dataManager getOverviewController] presentViewController:imagePicker animated:YES completion:nil];
    }
}


- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    [picker dismissViewControllerAnimated:YES completion:nil];
    
    // Edited image works great (if you allowed editing)
    //myUIImageView.image = [info objectForKey:UIImagePickerControllerEditedImage];
    // AND the original image works great
    //myUIImageView.image = [info objectForKey:UIImagePickerControllerOriginalImage];
    // AND do whatever you want with it, (NSDictionary *)info is fine now
    //UIImage *myImage = [info objectForKey:UIImagePickerControllerEditedImage];
    
    UIImage *image =  [info objectForKey:UIImagePickerControllerOriginalImage];
    
    image = [Utils fixImageOrientation:image];
    
    [_CameraImageView setImage:image];
    
    _imagePath = [((NSURL *)[info objectForKey:UIImagePickerControllerReferenceURL]) absoluteString];
    if(_imagePath == nil) {
        [_assetsLibrary writeImageToSavedPhotosAlbum:[image CGImage] orientation:(ALAssetOrientation)[image imageOrientation] completionBlock:^(NSURL *assetURL, NSError *error){
            
            if(!error) {
                _imagePath = [assetURL absoluteString];
//                _isImageSaved = YES;
            }
        }];
    } else {
//        _isImageSaved = YES;
    }
}

-(void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
     [picker dismissViewControllerAnimated:YES completion:nil];
}

- (void)downloadImageWithURL:(NSURL *)url completionBlock:(void (^)(BOOL succeeded, UIImage *image))completionBlock
{
    NSLog(@"%@%@", @"Starting download: ", url );
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:500];
    
    [request setValue:RestClient.authValue forHTTPHeaderField:@"Authorization"];
    [request setHTTPMethod:@"GET"];
    [request setAllHTTPHeaderFields:[self getCookies]];
    
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


-(NSDictionary*)getCookies{
    DataManager* dataManager = [DataManager getInstance];
    
    NSDictionary *properties = [NSDictionary dictionaryWithObjectsAndKeys:
                                [dataManager getCookieDomainForCurrentServer], NSHTTPCookieDomain,
                                @"/", NSHTTPCookiePath,  // IMPORTANT!
                                @"iPlanetDirectoryPro", NSHTTPCookieName,
                                [dataManager getAuthToken], NSHTTPCookieValue,
                                nil];
    NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:properties];
    
    NSDictionary *properties2 = [NSDictionary dictionaryWithObjectsAndKeys:
                                 [dataManager getCookieDomainForCurrentServer], NSHTTPCookieDomain,
                                 @"/", NSHTTPCookiePath,  // IMPORTANT!
                                 @"AMAuthCookie", NSHTTPCookieName,
                                 [dataManager getAuthToken], NSHTTPCookieValue,
                                 nil];
    NSHTTPCookie *cookie2 = [NSHTTPCookie cookieWithProperties:properties2];
    
    NSArray* cookies = [NSArray arrayWithObjects: cookie,cookie2, nil];
    return [NSHTTPCookie requestHeaderFieldsWithCookies:cookies];
}

@end

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
//  DetailViewController.m
//  nics
//
//

#import "SimpleReportDetailViewController.h"

@interface SimpleReportDetailViewController ()
@property (strong, nonatomic) UIPopoverController *masterPopoverController;
- (void)configureView;
@end

@implementation SimpleReportDetailViewController

#pragma mark - Managing the detail item

- (void)setPayload:(SimpleReportPayload *)payload
{
    if (_payload != payload) {
        _payload = payload;
    }
    
    if (self.masterPopoverController != nil) {
        [self.masterPopoverController dismissPopoverAnimated:YES];
    }
}

- (void)configureView
{
    [_imageView setImage:nil];
    
    _categoryView.options = [Enums simpleReportCategoriesList];
    [_categoryView setup];
    _categoryView.label.text = NSLocalizedString(@"Recipient",nil);
    [_categoryView refreshLayout:_descriptionView];

    if(_hideEditControls) {
        [_paddingTopConstraint setConstant:0];
        
   //     [_imageSelectionView removeFromSuperview];
   //     [_locationButtonView removeFromSuperview];
   //     [_buttonView removeFromSuperview];
        
        [_locationButtonView setHidden:TRUE];
        
        [_imageSelectionView setHidden:TRUE];
        [_locationButtonView setHidden:TRUE];
        [_buttonView setHidden:TRUE];
        
        [[_categoryView getTextView] setEditable:NO];
        
        [_latitudeView setEnabled:NO];
        [_longitudeView setEnabled:NO];
        [_descriptionView setEditable:NO];
    }else{

        [_imageSelectionView setHidden:FALSE];
        [_locationButtonView setHidden:FALSE];
        [_buttonView setHidden:FALSE];
        [[_categoryView getTextView] setEditable:YES];
        
        [_locationButtonView setHidden:FALSE];
        
        [_latitudeView setEnabled:YES];
        [_longitudeView setEnabled:YES];
        [_descriptionView setEditable:YES];
    }
    
    // Update the user interface for the detail item.
    if(_payload.id != nil) {
        _latitudeView.text = [_payload.messageData.latitude stringValue];
        _longitudeView.text = [_payload.messageData.longitude stringValue];
        [_categoryView getTextView].text = _payload.messageData.category;
        _descriptionView.text = _payload.messageData.msgDescription;
        
        [_imageLoadingView setHidden:NO];
        [_imageLoadingView startAnimating];
        
       _imagePath = _payload.messageData.fullpath;

        NSArray* splitImagePath = [_imagePath componentsSeparatedByString: @"/"];

        if([splitImagePath[0] isEqualToString:@"assets-library:"]){    //if the fullpath is a locally cached image that wasn't from web (if report was a draft mainly)
            
            [_assetsLibrary assetForURL:[NSURL URLWithString:_imagePath] resultBlock:^(ALAsset *asset) {
                _imageView.image = [UIImage imageWithCGImage:[[asset defaultRepresentation] fullResolutionImage]];
                [_imageLoadingView stopAnimating];
                [_imageLoadingView setHidden:YES];
                
            } failureBlock:^(NSError *error) {
                [_imageLoadingView stopAnimating];
                [_imageLoadingView setHidden:YES];
            }
             ];

        }else if(splitImagePath[0] == nil){
            if(_imagePath != nil){
                
                [_assetsLibrary assetForURL:[NSURL URLWithString:_imagePath] resultBlock:^(ALAsset *asset) {
                    _imageView.image = [UIImage imageWithCGImage:[[asset defaultRepresentation] fullResolutionImage]];
                    [_imageLoadingView stopAnimating];
                    [_imageLoadingView setHidden:YES];
                    
                } failureBlock:^(NSError *error) {
                    [_imageLoadingView stopAnimating];
                    [_imageLoadingView setHidden:YES];
                }
                 ];
            }else{
                [_imageLoadingView stopAnimating];
                [_imageLoadingView setHidden:YES];
            }
            
        }else{

            NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
            NSString *documentsPath = [paths objectAtIndex:0]; //Get the docs directory
            documentsPath = [documentsPath stringByAppendingString:@"/"];
            documentsPath = [documentsPath stringByAppendingString:splitImagePath[splitImagePath.count-1]];
            
            
            NSData *pngData = [NSData dataWithContentsOfFile:documentsPath];
            
            if(pngData != nil){
                UIImage *image = [UIImage imageWithData:pngData];
                _imageView.image = image;
                [_imageLoadingView stopAnimating];
                [_imageLoadingView setHidden:YES];
            }else{
                [self downloadImageWithURL:[NSURL URLWithString:_imagePath] completionBlock:^(BOOL succeeded, UIImage *image) {
                    if(succeeded) {
                        _imageView.image = image;
                    }
                    [_imageLoadingView stopAnimating];
                    [_imageLoadingView setHidden:YES];
                }];
            }
        
        }

        
//        }
    } else {
        [_categoryView getTextView].text = [[Enums simpleReportCategoriesDictionary] objectForKey:[NSNumber numberWithInt:Empty]];
        [_imageLoadingView stopAnimating];
        [_imageLoadingView setHidden:YES];
        
        _latitudeView.text = @"";
        _longitudeView.text = @"";
        [_categoryView getTextView].text = @"";
        _descriptionView.text = @"";

    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillBeHidden:) name:UIKeyboardWillHideNotification object:nil];
    
	// Do any additional setup after loading the view, typically from a nib.
    _dataManager = [DataManager getInstance];
    _assetsLibrary = [ALAssetsLibrary new];
    
    [self configureView];
    
    if([_dataManager isIpad]){
        _scrollView.contentSize = CGSizeMake(512, _contentView.frame.size.height + _imageSelectionView.frame.size.height + _imageView.frame.size.height);//self.scrollView.frame.size.height);
    }else{
        self.navigationItem.hidesBackButton = YES;
        UIBarButtonItem *backBtn =[[UIBarButtonItem alloc]initWithTitle:@"Back" style:UIBarButtonSystemItemCancel target:self action:@selector(handleBack:)];
        self.navigationItem.leftBarButtonItem=backBtn;
    }
    
}

- (void) viewWillAppear:(BOOL)animated {
    _originalFrame = self.view.frame;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Split view

- (void)splitViewController:(UISplitViewController *)splitController willHideViewController:(UIViewController *)viewController withBarButtonItem:(UIBarButtonItem *)barButtonItem forPopoverController:(UIPopoverController *)popoverController
{
    barButtonItem.title = NSLocalizedString(@"Master", @"Master");
    [self.navigationItem setLeftBarButtonItem:barButtonItem animated:YES];
    self.masterPopoverController = popoverController;
}

- (void)splitViewController:(UISplitViewController *)splitController willShowViewController:(UIViewController *)viewController invalidatingBarButtonItem:(UIBarButtonItem *)barButtonItem
{
    // Called when the view is shown again in the split view, invalidating the button and popover controller.
    [self.navigationItem setLeftBarButtonItem:nil animated:YES];
    self.masterPopoverController = nil;
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
                
//                UIImageWriteToSavedPhotosAlbum(image, self, @selector(imageSavedToPhotosAlbum: didFinishSavingWithError: contextInfo:), nil);
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

- (void)imageSavedToPhotosAlbum:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    NSString *message;
    NSString *title;
    
    if (!error) {
        title = NSLocalizedString(@"SaveSuccessTitle", @"");
        message = NSLocalizedString(@"SaveSuccessMessage", @"");
    } else {
        title = NSLocalizedString(@"SaveFailedTitle", @"");
        message = [error description];
    }
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title
                                                    message:message
                                                   delegate:nil
                                          cancelButtonTitle:NSLocalizedString(@"ButtonOK", @"")
                                          otherButtonTitles:nil];
    [alert show];
}

-(BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    _focusedTextView = textField;
    
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    
    return YES;
}

-(BOOL)textViewShouldBeginEditing:(UITextView *)textView {
    _focusedTextView = textView;
    
    return YES;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    if (textView == _descriptionView) {
        if ([text isEqualToString:@"\n"]) {
            [textView resignFirstResponder];
            return NO;
        }
    }
    return YES;
}


- (IBAction)captureImagePressed:(UIButton *)button
{
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        
        UIImagePickerController *imagePicker = [UIImagePickerController new];
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        imagePicker.allowsEditing = YES;
        [imagePicker setDelegate:self];
        
        if([_dataManager isIpad]){
            [IncidentButtonBar OpenImagePickerCameraForTablet:imagePicker];
        }else{
            [self presentViewController:imagePicker animated:YES completion:nil];
        }
        
    } else {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error",nil) message:NSLocalizedString(@"Device has no camera",nil) delegate:nil cancelButtonTitle:NSLocalizedString(@"OK",nil) otherButtonTitles: nil];
        [alertView show];
    }
}

- (IBAction)browseGalleryPressed:(UIButton *)button
{
    UIImagePickerController *imagePicker = [UIImagePickerController new];
    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    
    [imagePicker setDelegate:self];
    
  if([_dataManager getIsIpad] == true){
    UIPopoverController *myPopOver = [[UIPopoverController alloc]initWithContentViewController:imagePicker];
    CGRect displayFrom = CGRectMake(1,1,1,1);
    [myPopOver presentPopoverFromRect:displayFrom inView:self.view permittedArrowDirections:UIPopoverArrowDirectionLeft animated:YES];
  }else{
      [self presentViewController:imagePicker animated:YES completion:nil];
  }
}

- (IBAction)submitReportButtonPressed:(UIButton *)button {
    
    if(_imagePath && _isImageSaved) {
        [_dataManager deleteSimpleReportFromStoreAndForward:_payload];
        _payload = [self getPayload:NO];
        [_dataManager addSimpleReportToStoreAndForward:_payload];
        [self.navigationController popViewControllerAnimated:YES];

    } else {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error",nil) message:NSLocalizedString(@"Please select or take an image to submit.",nil) delegate:nil cancelButtonTitle:NSLocalizedString(@"OK",nil) otherButtonTitles: nil];
        [alertView show];
    }
}

- (void)submitTabletReportButtonPressed {
    
//    if(_imagePath && _isImageSaved) {
        [_dataManager deleteSimpleReportFromStoreAndForward:_payload];
        _payload = [self getPayload:NO];
        [_dataManager addSimpleReportToStoreAndForward:_payload];
        [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToGeneralMessageFromButtonBar];
        
//    } else {
//        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error",nil) message:NSLocalizedString(@"Please select or take an image to submit.",nil) delegate:nil cancelButtonTitle:NSLocalizedString(@"OK",nil) otherButtonTitles: nil];
//        [alertView show];
//    }
}

- (IBAction)saveDraftButtonPressed:(UIButton *)button {
    [_dataManager deleteSimpleReportFromStoreAndForward:_payload];
    _payload = [self getPayload:YES];
    [_dataManager addSimpleReportToStoreAndForward:_payload];
    [self.navigationController popViewControllerAnimated:YES];
}

-(void)saveTabletDraftButtonPressed{
    [_dataManager deleteSimpleReportFromStoreAndForward:_payload];
    _payload = [self getPayload:YES];
    [_dataManager addSimpleReportToStoreAndForward:_payload];
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToGeneralMessageFromButtonBar];
}

- (IBAction)cancelButtonPressed:(UIButton *)button {
    if(self.hideEditControls == false){
        [self showCancelAlertView];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)cancelTabletButtonPressed {
    [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToGeneralMessageFromButtonBar];
}

- (SimpleReportPayload *)getPayload:(BOOL)isDraft {
    SimpleReportData* data = [SimpleReportData new];
    data.user = [_dataManager getUsername];
    data.latitude = [NSNumber numberWithDouble:[_latitudeView.text doubleValue]];
    data.longitude = [NSNumber numberWithDouble:[_longitudeView.text doubleValue]];
    data.msgDescription = _descriptionView.text;
    data.category = [_categoryView getTextView].text;
    if(_isImageSaved && _imagePath) {
        data.image = _imagePath;
        data.fullpath = _imagePath;
    }
    
    SimpleReportPayload *payload = [SimpleReportPayload new];
    
    if(isDraft) {
        payload.isDraft = [NSNumber numberWithInt:1];
    } else {
        payload.isDraft = [NSNumber numberWithInt:0];
    }
    payload.incidentid = [_dataManager getActiveIncidentId];
    payload.incidentname = [_dataManager getActiveIncidentName];
    payload.formtypeid = [NSNumber numberWithInt:SR];
//    payload.senderUserId = [_dataManager getUserId];
    payload.usersessionid = [_dataManager getUserSessionId];
    payload.messageData = data;
    payload.message = [data toJSONString];
    
    double temp =  [[NSDate date] timeIntervalSince1970] * 1000.00;
    
    NSNumber *date = [NSNumber numberWithLongLong:round(temp)];
    payload.seqtime = date;
    
    payload.status = [NSNumber numberWithInt:WAITING_TO_SEND];
    
    return payload;
}

- (IBAction)gpsButtonPressed:(UIButton *)button {
    _latitudeView.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.latitude];
    _longitudeView.text = [NSString stringWithFormat:@"%f", _dataManager.currentLocation.coordinate.longitude];
}

- (IBAction)lrfButtonPressed:(UIButton *)button {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error",nil) message:NSLocalizedString(@"Laser Range Finder currently unsupported.",nil) delegate:nil cancelButtonTitle:NSLocalizedString(@"OK",nil) otherButtonTitles: nil];
    [alertView show];
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
    
    [_imageView setImage:image];
    
    _imagePath = [((NSURL *)[info objectForKey:UIImagePickerControllerReferenceURL]) absoluteString];
    if(_imagePath == nil) {
        [_assetsLibrary writeImageToSavedPhotosAlbum:[image CGImage] orientation:(ALAssetOrientation)[image imageOrientation] completionBlock:^(NSURL *assetURL, NSError *error){
            
            if(!error) {
                _imagePath = [assetURL absoluteString];
                _isImageSaved = YES;
            }
        }];
    } else {
        _isImageSaved = YES;
    }
}

- (void)keyboardWillShow:(NSNotification*)notification {

    [Utils AdjustViewForKeyboard:self.view :TRUE:_originalFrame :notification];
}

- (void)keyboardWillBeHidden:(NSNotification*)notification {
    
    [Utils AdjustViewForKeyboard:self.view :FALSE:_originalFrame :notification];
}

-(void) showCancelAlertView{
    UIAlertController * alert= [UIAlertController alertControllerWithTitle:@"Cancel Report" message:@"Your report progress will be lost if you leave the report." preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* saveAndCloseButton = [UIAlertAction actionWithTitle:@"Save Draft And Close" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action){
        
        if([_dataManager isIpad]){
            [self saveTabletDraftButtonPressed];
        }else{
            [self saveDraftButtonPressed:nil];
        }
    }];
    
    UIAlertAction* closeButton = [UIAlertAction actionWithTitle:@"Don't Save And Close" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action){
        
        if([_dataManager isIpad]){
            [[IncidentButtonBar GetIncidentCanvasController] SetCanvasToGeneralMessageFromButtonBar];
        }else{
            [self.navigationController popViewControllerAnimated:YES];
        }
    }];
    
    UIAlertAction* continueButton = [UIAlertAction actionWithTitle:@"Continue Editing" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action){
        
    }];
    
    [alert addAction:saveAndCloseButton];
    [alert addAction:closeButton];
    [alert addAction:continueButton];
    
    [self presentViewController:alert animated:YES completion:nil];
}


- (void) handleBack:(id)sender
{
    if(self.hideEditControls == false){
        [self showCancelAlertView];
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

@end

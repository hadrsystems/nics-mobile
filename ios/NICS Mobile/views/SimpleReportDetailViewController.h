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
/**
 *
 */
//
//  DetailViewController.h
//  nics
//
//

#import <UIKit/UIKit.h>
#import "AssetsLibrary/AssetsLibrary.h"
#import "DataManager.h"
#import "SimpleReportPayload.h"
#import "FormSpinner.h"
#import "Enums.h"
#import "IncidentButtonBar.h"

@interface SimpleReportDetailViewController : UIViewController <UISplitViewControllerDelegate, UITextFieldDelegate, UITextViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (strong, nonatomic) SimpleReportPayload *payload;
@property DataManager *dataManager;
@property ALAssetsLibrary *assetsLibrary;
@property NSString *imagePath;
@property BOOL isImageSaved;
@property UIView *focusedTextView;
@property CGRect originalFrame;

@property IBOutlet UIActivityIndicatorView *imageLoadingView;
@property IBOutlet UIScrollView *scrollView;

@property IBOutlet UIView *locationButtonView;
@property IBOutlet UIView *buttonView;
@property (weak, nonatomic) IBOutlet UIView *contentView;

@property IBOutlet UIButton *saveAsDraftButton;
@property IBOutlet UIButton *submitButton;
@property IBOutlet UIButton *cancelButton;
@property IBOutlet UIImageView *imageView;
@property IBOutlet UITextField *latitudeView;
@property IBOutlet UITextField *longitudeView;
@property IBOutlet FormSpinner *categoryView;
@property IBOutlet UITextView *descriptionView;

@property IBOutlet UIView *imageSelectionView;
@property IBOutlet NSLayoutConstraint *paddingTopConstraint;

- (IBAction)captureImagePressed:(UIButton *)button;
- (IBAction)browseGalleryPressed:(UIButton *)button;

- (IBAction)lrfButtonPressed:(UIButton *)button;
- (IBAction)gpsButtonPressed:(UIButton *)button;

- (IBAction)submitReportButtonPressed:(UIButton *)button;
- (void)submitTabletReportButtonPressed;
- (IBAction)saveDraftButtonPressed:(UIButton *)button;
- (void)saveTabletDraftButtonPressed;
- (IBAction)cancelButtonPressed:(UIButton *)button;
- (void)cancelTabletButtonPressed;

- (SimpleReportPayload *)getPayload:(BOOL)isDraft;
- (void) configureView;

@property BOOL hideEditControls;

@end
    

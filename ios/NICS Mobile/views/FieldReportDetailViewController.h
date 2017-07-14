/*|~^~|(c) Copyright, 2008-2015 Massachusetts Institute of Technology.
 |~^~|
 |~^~|This material may be reproduced by or for the
 |~^~|U.S. Government pursuant to the copyright license
 |~^~|under the clause at DFARS 252.227-7013 (June, 1995).
 |~^~|*/
/**
 *
 */
//
//  DetailViewController.h
//  nics
//
//

#import <UIKit/UIKit.h>
#import "FormView.h"
#import "FormEditText.h"
#import "DataManager.h"
#import "FieldReportPayload.h"
#import "Enums.h"

@class FormView;

@interface FieldReportDetailViewController : UIViewController

@property (strong, nonatomic) FieldReportPayload *payload;
@property BOOL hideEditControls;

@property IBOutlet FormView *formView;
@property IBOutlet UIView *buttonView;

@property IBOutlet UIButton *saveAsDraftButton;
@property IBOutlet UIButton *submitButton;
@property IBOutlet UIButton *cancelButton;

@property IBOutlet NSLayoutConstraint *bottomConstraint;

@property DataManager *dataManager;

- (void)configureView;

- (IBAction)submitReportButtonPressed:(UIButton *)button;
- (void)submitTabletReportButtonPressed;

- (IBAction)saveDraftButtonPressed:(UIButton *)button;
- (void)saveTabletDraftButtonPressed;

- (IBAction)cancelButtonPressed:(UIButton *)button;
- (void)cancelTabletButtonPressed;

- (FieldReportPayload *)getPayload:(BOOL)isDraft;
@end

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
//  WeatherView.h
//  nics_iOS
//
//

#import <UIKit/UIKit.h>
#import "DataManager.h"
#import "CollapseClick.h"
#import "FormDamageInformation.h"
#import "FormLocation.h"
#import "FormImageSelector.h"
#import "FormEditText.h"
#import "FormSpinner.h"
#import "FormColorPicker.h"
#import "FormSchema.h"
#import "JSONModelLib.h"
#import "AssignmentMessage.h"

@interface FormView : CollapseClick <CollapseClickDelegate, UITextViewDelegate, UIImagePickerControllerDelegate>

@property UITextView *focusedTextView;

@property (weak, nonatomic) IBOutlet UIView *view;

@property (weak, nonatomic) IBOutlet CollapseClick *collapseView;

@property NSString *schema;

@property FormSchema *schemaJson;

@property NSMutableArray *sections;
@property NSMutableArray *allInteractableFields;

@property NSMutableDictionary *viewMap;

@property DataManager *dataManager;

@property NSMutableArray *openedSections;
@property BOOL updateFrame;
@property CGFloat numLines;

-(void)updateDataWithDictionary:(NSDictionary *) data readOnly:(BOOL) readOnly;
-(NSMutableDictionary *) save;
-(void)setAsDraft:(bool)isDraft;

@end

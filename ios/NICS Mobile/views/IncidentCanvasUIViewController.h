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
//  IncidentCanvasUIViewController.h
//  NICS Mobile
//
//

#import <UIKit/UIKit.h>

@interface IncidentCanvasUIViewController : UIViewController

@property NSString *currentReport;

//indexes here need to line up with the button order in the pop up _ReportsMenu in the .m
enum ReportTypesMenu : NSUInteger {
    DamageReport = 0,
//    ResourceRequest = 1,
//    FieldReport = 2,
    WeatherReport = 1,
    Cancel = 2
};

@property (weak, nonatomic) IBOutlet UIView *IncidentCanvas;
@property (weak, nonatomic) IBOutlet UIButton *AddButton;
@property (weak, nonatomic) IBOutlet UIButton *SaveDraftButton;
@property (weak, nonatomic) IBOutlet UIButton *CancelButton;
@property (weak, nonatomic) IBOutlet UIButton *SubmitButton;
@property (weak, nonatomic) IBOutlet UIButton *FilterButton;

@property UIActionSheet *ReportsMenu;

- (IBAction)SetCanvasToGeneralMessage:(id)sender;
- (IBAction)ReportsButtonPressed:(id)sender;
- (IBAction)AddButtonPressed:(id)sender;
- (IBAction)SaveDraftButtonPressed:(id)sender;
- (IBAction)CancelButtonPressed:(id)sender;
- (IBAction)SubmitButtonPressed:(id)sender;
- (IBAction)ChatButtonPressed:(id)sender;
- (IBAction)FilterButtonPressed:(id)sender;

- (void)SetCanvasToGeneralMessageFromButtonBar;

- (void)SetCanvasToDamageReport;
- (void)SetCanvasToDamageReportFromButtonBar;

- (void)SetCanvasToResourceRequest;
- (void)SetCanvasToResourceRequestFromButtonBar;

- (void)SetCanvasToFieldReport;
- (void)SetCanvasToFieldReportFromButtonBar;

- (void)SetCanvasToWeatherReport;
- (void)SetCanvasToWeatherReportFromButtonBar;

@end

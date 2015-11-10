/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
//  IncidentButtonBar.m
//  NICS Mobile
//
//

#import "IncidentButtonBar.h"

@implementation IncidentButtonBar

/*
 Static class to hold references to each of the panels in the tablet layout
 only used in the tablet layout.
 
 It was created to handle the button bar on the bottom right to work for
 whichever report you are currently in but it is also used as an access point for the app to reference other views when swapping canvases.
 
 TO-DO: Change the name to make more sense.
 */

static LoginViewController *loginViewController;

static ChatContainerBasicViewController *chatController;

static SimpleReportListViewController *generalMessageListview;
static SimpleReportDetailViewController *generalMessageDetailView;

static DamageReportListViewController *damageReportListview;
static DamageReportDetailViewController *damageReportDetailview;

static ResourceRequestListViewController *resourceRequestListview;
static ResourceRequestDetailViewController *resourceRequestDetailview;

static FieldReportListViewController *fieldReportListview;
static FieldReportDetailViewController *fieldReportDetailview;

static UxoReportListViewController *uxoReportListview;
static UxoReportDetailViewController *uxoReportDetailview;

static MapMarkupViewController *mapMarkupView;

static IncidentCanvasUIViewController *incidentCanvasController;
static UIView *incidentCanvas;

static UIButton *SaveDraftButton;
static UIButton *AddButton;
static UIButton *CancelButton;
static UIButton *SubmitButton;

+ (void)AddButtonPressed:(NSString*)currentReport{
    
    [AddButton setHidden:TRUE];
    if([currentReport isEqualToString:@"GeneralMessage"]){
        [generalMessageListview prepareForTabletCanvasSwap:TRUE:0];
    }else if([currentReport isEqualToString:@"DamageReport"]){
        [damageReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"ResourceRequest"]){
        [resourceRequestListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"FieldReport"]){
        [fieldReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"UxoReport"]){
        [uxoReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }
}

+ (void)SaveDraftButtonPressed:(NSString*)currentReport{
    
    [SaveDraftButton setHidden:TRUE];
    [CancelButton setHidden:TRUE];
    [SubmitButton setHidden:TRUE];
    
    if([currentReport isEqualToString:@"GeneralMessage"]){
        [generalMessageDetailView saveTabletDraftButtonPressed];
     }else if([currentReport isEqualToString:@"DamageReport"]){
        [damageReportDetailview saveTabletDraftButtonPressed];
     }else if([currentReport isEqualToString:@"ResourceRequest"]){
         [resourceRequestDetailview saveTabletDraftButtonPressed];
     }else if([currentReport isEqualToString:@"FieldReport"]){
         [fieldReportDetailview saveTabletDraftButtonPressed];
     }else if([currentReport isEqualToString:@"UxoReport"]){
         [uxoReportDetailview saveTabletDraftButtonPressed];
     }
}

+ (void)CancelButtonPressed:(NSString*)currentReport{
    
    [SaveDraftButton setHidden:TRUE];
    [CancelButton setHidden:TRUE];
    [SubmitButton setHidden:TRUE];
    
    if([currentReport isEqualToString:@"GeneralMessage"]){
        [generalMessageDetailView cancelTabletButtonPressed];
    }else if([currentReport isEqualToString:@"DamageReport"]){
        [damageReportDetailview cancelTabletButtonPressed];
    }else if([currentReport isEqualToString:@"ResourceRequest"]){
        [resourceRequestDetailview cancelTabletButtonPressed];
    }else if([currentReport isEqualToString:@"FieldReport"]){
        [fieldReportDetailview cancelTabletButtonPressed];
    }else if([currentReport isEqualToString:@"UxoReport"]){
        [uxoReportDetailview cancelTabletButtonPressed];
    }
}

+ (void)SubmitButtonPressed:(NSString*)currentReport{
    
    [SaveDraftButton setHidden:TRUE];
    [CancelButton setHidden:TRUE];
    [SubmitButton setHidden:TRUE];
    
    if([currentReport isEqualToString:@"GeneralMessage"]){
        [generalMessageDetailView submitTabletReportButtonPressed];
    }else if([currentReport isEqualToString:@"DamageReport"]){
        [damageReportDetailview submitTabletReportButtonPressed];
    }else if([currentReport isEqualToString:@"ResourceRequest"]){
        [resourceRequestDetailview submitTabletReportButtonPressed];
    }else if([currentReport isEqualToString:@"FieldReport"]){
        [fieldReportDetailview submitTabletReportButtonPressed];
    }else if([currentReport isEqualToString:@"UxoReport"]){
        [uxoReportDetailview submitTabletReportButtonPressed];
    }
}

+ (void)OpenImagePickerCameraForTablet:(UIImagePickerController*)imagePicker{
    [loginViewController presentViewController:imagePicker animated:YES completion:nil];
}

+(void)SetLoginView:(LoginViewController*)loginController{loginViewController = loginController;}
+(LoginViewController*)GetLoginView{return loginViewController;}

+(void)SetIncidentCanvasController:(IncidentCanvasUIViewController*)controller{incidentCanvasController = controller;}
+(IncidentCanvasUIViewController*) GetIncidentCanvasController{return incidentCanvasController;}

+ (void)SetIncidentCanvas:(UIView*)view{incidentCanvas = view;}
+ (UIView*) GetIncidentCanvas{return incidentCanvas;}

+(void)SetGeneralMessageListview:(SimpleReportListViewController*)listController{generalMessageListview = listController;}
+(SimpleReportListViewController*)GetGeneralMessageListview{ return generalMessageListview;}

+(void)SetGeneralMessageDetailView:(SimpleReportDetailViewController*)detailController{generalMessageDetailView = detailController;}
+(SimpleReportDetailViewController*)GetGeneralMessageDetailView{return generalMessageDetailView;}

+(void)SetDamageReportListview:(DamageReportListViewController*)listController{damageReportListview = listController;}
+(DamageReportListViewController*)GetDamageReportListview{return damageReportListview;}

+(void)SetDamageReportDetailView:(DamageReportDetailViewController*)detailController{damageReportDetailview = detailController;}
+(DamageReportDetailViewController*) GetDamageReportDetailView{return damageReportDetailview;}

+(void)SetResourceRequestListview:(ResourceRequestListViewController*)listController{resourceRequestListview = listController;}
+(ResourceRequestListViewController*)GetResourceRequestListview{return resourceRequestListview;}

+(void)SetResourceRequestDetailView:(ResourceRequestDetailViewController*)detailController{resourceRequestDetailview = detailController;}
+(ResourceRequestDetailViewController*) GetResourceRequestDetailView{return resourceRequestDetailview;}

+(void)SetFieldReportListview:(FieldReportListViewController*)listController{fieldReportListview = listController;}
+(FieldReportListViewController*)GetFieldReportListview{return fieldReportListview;}

+(void)SetFieldReportDetailView:(FieldReportDetailViewController*)detailController{fieldReportDetailview = detailController;}
+(FieldReportDetailViewController*)GetFieldReportDetailView{return fieldReportDetailview;}

+(void)SetUxoReportListview:(UxoReportListViewController*)listController{uxoReportListview = listController;}
+(UxoReportListViewController*)GetUxoReportListview{return uxoReportListview;}

+(void)SetUxoReportDetailView:(UxoReportDetailViewController*)detailController{uxoReportDetailview = detailController;}
+(UxoReportDetailViewController*)GetUxoReportDetailView{return uxoReportDetailview;}


+(void)SetChatController:(ChatContainerBasicViewController*)detailController{chatController = detailController;}
+(ChatContainerBasicViewController*) GetChatController{return chatController;}

+(void)SetMapMarkupController:(MapMarkupViewController *)mapController{
    mapMarkupView = mapController;
}
+(MapMarkupViewController*) GetMapMarkupController{
    return mapMarkupView;
}

+ (void) SetAddButton:(UIButton*)button{AddButton = button;}
+(UIButton*) GetAddButton{return AddButton;}

+ (void) SetSaveDraftButton:(UIButton*)button{SaveDraftButton = button;}
+(UIButton*) GetSaveDraftButton{return SaveDraftButton;}

+ (void) SetCancelButton:(UIButton*)button{CancelButton = button;}
+(UIButton*) GetCancelButton{return CancelButton;}

+ (void) SetSubmitButton:(UIButton*)button{SubmitButton = button;}
+(UIButton*) GetSubmitButton{return SubmitButton;}

@end

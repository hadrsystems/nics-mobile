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
//  IncidentButtonBar.m
//  NICS Mobile
//
//

#import "IncidentButtonBar.h"

@implementation IncidentButtonBar

/*
 Static class to hold references to each of the panels in the tablet layout
 only used in the tablet layout. DO NOT reference in iPhone code
 
 It was created to handle the button bar on the bottom right to work for
 whichever report you are currently in but it is also used as an access point for the app to reference other views when swapping canvases.
 
 TO-DO: Change the name to make more sense.
 */

static LoginViewController *loginViewController;
static OverviewViewControllerTablet *tabletOverviewViewController;

static ChatContainerBasicViewController *chatController;

static SimpleReportListViewController *generalMessageListview;
static SimpleReportDetailViewController *generalMessageDetailView;

static DamageReportListViewController *damageReportListview;
static DamageReportDetailViewController *damageReportDetailview;

static ResourceRequestListViewController *resourceRequestListview;
static ResourceRequestDetailViewController *resourceRequestDetailview;

static FieldReportListViewController *fieldReportListview;
static FieldReportDetailViewController *fieldReportDetailview;

static WeatherReportListViewController *weatherReportListview;
static WeatherReportDetailViewController *weatherReportDetailview;

static MapMarkupViewController *mapMarkupView;

static IncidentCanvasUIViewController *incidentCanvasController;
static UIView *incidentCanvas;

static UIButton *SaveDraftButton;
static UIButton *AddButton;
static UIButton *CancelButton;
static UIButton *SubmitButton;
static UIButton *FilterButton;

+ (void)AddButtonPressed:(NSString*)currentReport{
    
    [AddButton setHidden:TRUE];
    [FilterButton setHidden:TRUE];
    if([currentReport isEqualToString:@"GeneralMessage"]){
        [generalMessageListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"DamageReport"]){
        [damageReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"ResourceRequest"]){
        [resourceRequestListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"FieldReport"]){
        [fieldReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"WeatherReport"]){
        [weatherReportListview prepareForTabletCanvasSwap:TRUE:-1];
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
     }else if([currentReport isEqualToString:@"WeatherReport"]){
         [weatherReportDetailview saveTabletDraftButtonPressed];
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
    }else if([currentReport isEqualToString:@"WeatherReport"]){
        [weatherReportDetailview cancelTabletButtonPressed];
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
    }else if([currentReport isEqualToString:@"WeatherReport"]){
        [weatherReportDetailview submitTabletReportButtonPressed];
    }
}

+ (void)FilterButtonPressed:(NSString*)currentReport{
    
    if([currentReport isEqualToString:@"GeneralMessage"]){
//        [generalMessageListview prepareForTabletCanvasSwap:TRUE:0];
    }else if([currentReport isEqualToString:@"DamageReport"]){
//        [damageReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"ResourceRequest"]){
//        [resourceRequestListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"FieldReport"]){
//        [fieldReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }else if([currentReport isEqualToString:@"WeatherReport"]){
//        [weatherReportListview prepareForTabletCanvasSwap:TRUE:-1];
    }
}

+ (void)OpenImagePickerCameraForTablet:(UIImagePickerController*)imagePicker{
    [loginViewController presentViewController:imagePicker animated:YES completion:nil];
}
+(void)SetLoginView:(LoginViewController*)loginController{loginViewController = loginController;}
+(LoginViewController*)GetLoginView{return loginViewController;}

+(void)ClearAllViews{
    tabletOverviewViewController = nil;
    chatController = nil;
    generalMessageListview = nil;
    generalMessageDetailView = nil;
    damageReportListview = nil;
    damageReportDetailview = nil;
    resourceRequestListview = nil;
    resourceRequestDetailview = nil;
    fieldReportListview = nil;
    fieldReportDetailview = nil;
    weatherReportListview = nil;
    weatherReportDetailview = nil;
    mapMarkupView = nil;
    incidentCanvasController = nil;
}

+(void)SetOverview:(OverviewViewControllerTablet*)overview{tabletOverviewViewController = overview;}
+(OverviewViewControllerTablet*)GetOverview{return tabletOverviewViewController;}

+(void)SetIncidentCanvasController:(IncidentCanvasUIViewController*)controller{incidentCanvasController = controller;}
+(IncidentCanvasUIViewController*) GetIncidentCanvasController{return incidentCanvasController;}

+ (void)SetIncidentCanvas:(UIView*)view{incidentCanvas = view;}
+ (UIView*) GetIncidentCanvas{return incidentCanvas;}

+(SimpleReportListViewController*)GetGeneralMessageListview{
    if(generalMessageListview == nil){
        generalMessageListview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"GeneralMessageViewID"];
    }
    return generalMessageListview;
}

+(SimpleReportDetailViewController*)GetGeneralMessageDetailView{
    if(generalMessageDetailView == nil){
        generalMessageDetailView =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"SimpleReportDetailViewID"];
    }
    return generalMessageDetailView;}

+(DamageReportListViewController*)GetDamageReportListview{
    if(damageReportListview == nil){
        damageReportListview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"DamageReportViewID"];
    }
    return damageReportListview;
}

+(DamageReportDetailViewController*) GetDamageReportDetailView{
    if(damageReportDetailview == nil){
        damageReportDetailview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"DamageReportDetailViewID"];
    }
    return damageReportDetailview;}

+(ResourceRequestListViewController*)GetResourceRequestListview{
    if(resourceRequestListview == nil){
        resourceRequestListview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"ResourceRequestViewID"];
    }
    return resourceRequestListview;
}

+(ResourceRequestDetailViewController*) GetResourceRequestDetailView{
    if(resourceRequestDetailview == nil){
        resourceRequestDetailview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"ResourceRequestDetailViewID"];
    }
    return resourceRequestDetailview;}

+(FieldReportListViewController*)GetFieldReportListview{
    if(fieldReportListview == nil){
        fieldReportListview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"FieldReportViewID"];
    }
    return fieldReportListview;
}

+(FieldReportDetailViewController*)GetFieldReportDetailView{
    if(fieldReportDetailview == nil){
        fieldReportDetailview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"FieldReportDetailViewID"];
    }
    return fieldReportDetailview;}

+(WeatherReportListViewController*)GetWeatherReportListview{
    if(weatherReportListview == nil){
        weatherReportListview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"WeatherReportViewID"];
    }
    return weatherReportListview;
}

+(WeatherReportDetailViewController*)GetWeatherReportDetailView{
    if(weatherReportDetailview == nil){
        weatherReportDetailview =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"weatherReportDetailViewID"];
    }
    return weatherReportDetailview;}


+(ChatContainerBasicViewController*) GetChatController{
    if(chatController == nil){
        chatController =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"ChatViewID"];
    }
    return chatController;}

+(MapMarkupViewController*) GetMapMarkupController{
    if(mapMarkupView == nil){
        mapMarkupView =[[UIStoryboard storyboardWithName:@"Main_iPad_Prototype" bundle:nil] instantiateViewControllerWithIdentifier:@"MapViewID"];
    }
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

+ (void)SetFilterButton:(UIButton*)button{FilterButton = button;}
+ (UIButton*)GetFilterButton{return FilterButton;}

@end

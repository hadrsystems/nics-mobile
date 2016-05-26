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
//  RestClient.h
//  nics_iOS
//
//

#import <Foundation/Foundation.h>

@interface Enums : NSObject

typedef enum {
    WAITING_TO_SEND = 0,
    SENT = 1,
} ReportStatus;

typedef enum {
    Empty,
    AgencyRepresentative,
    CompClaimDamageAdvisory,
    FinanceSection,
    GroundSupportRequest,
    IncidentCommander,
    LiaisonOfficer,
    LogisticsSection,
    OperationsSection,
    Other,
    PlansSection,
    PublicInformationOfficer,
    SafetyOfficer,
    SuppressionRepair
} SimpleReportCategoryType;


typedef enum {
    NONE = 0,
    ROC = 1,
    RESC = 2,
    ABC = 3,
    TWO_15 = 4,
    SITREP = 5,
    ASSGN = 6,
    SR = 7,
    FR = 8,
    TASK = 9,
    RESREQ = 10,
    NINE_110 = 11,
    DR = 12,
    UXO = 13,
    SVRRPT = 14,
    AGRRPT = 15,
    MITAM = 16,
    WR = 17
} FormType;
#define FormTypeArrayAbbrev @"NONE",@"ROC",@"RESC",@"ABC",@"TWO_15",@"SITREP",@"ASSGN",@"SR",@"FR",@"TASK",@"RESREQ",@"NINE_110",@"DR",@"UXO",@"SVRRPT",@"AGRRPT",@"MITAM",@"WR",nil

#define FormTypeArrayFull @"NONE",@"Report on Condition",@"RESC",@"ABC",@"215",@"SITREP",@"Assignment Form",@"Simple Report",@"Field Report",@"Task",@"Resource Request",@"9110 - Notification Report",@"Damage Report",@"Explosive Report",@"Catan Survivor Request",@"Catan Survivor Aggrogate Request",@"MITAM",@"Weather Report",nil

+(NSString*) formTypeEnumToStringAbbrev:(FormType)enumVal;
+(NSString*) formTypeEnumToStringFull:(FormType)enumVal;

typedef enum {
    VL,
    VH,
    O,
    E,
    A,
    H,
    C
} ResourceRequestType;

typedef enum {
    symbol = 100,
    segment = 110,
    rectangle = 120,
    polygon = 130,
    circle = 140,
    text = 150,
    ExplosiveReportMarkup = 160,
    GeneralMessageMarkup = 170,
    DamageReportMarkup = 180
} MarkupType;

+ (NSDictionary *) simpleReportCategoriesDictionary;

+ (NSArray *) simpleReportCategoriesList;

+ (SimpleReportCategoryType) convertToSRCategoryFromString: (NSString *) type;
//+ (NSString *) convertSRCategoryTypeToNamedImage:(SimpleReportCategoryType) type;
+ (NSString *) convertSRCategoryTypeToAbreviation:(SimpleReportCategoryType) type;

+ (ResourceRequestType) convertToResReqTypeFromString: (NSString *) type;
+ (NSString *) convertResReqFullToAbbreviation:(NSString*) fullname;
+ (NSString *) convertResReqAbbreviationToFull:(NSString*) abbreviation;
+ (NSString *) convertResReqTypeToNamedImage:(ResourceRequestType) type;

+ (MarkupType) convertToMarkupTypeFromString: (NSString *) type;
@end

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
//  RestClient.m
//  nics_iOS
//
//

#import "Enums.h"

@implementation Enums

static NSDictionary *simpleReportCategories;

+(NSString*) formTypeEnumToStringAbbrev:(FormType)enumVal
{
    NSArray *formTypeAbbrevArray = [[NSArray alloc] initWithObjects:FormTypeArrayAbbrev];
    return [formTypeAbbrevArray objectAtIndex:enumVal];
}

+(NSString*) formTypeEnumToStringFull:(FormType)enumVal
{
    NSArray *formTypeFullArray = [[NSArray alloc] initWithObjects:FormTypeArrayFull];
    return [formTypeFullArray objectAtIndex: enumVal];
}

+ (NSDictionary *)simpleReportCategoriesDictionary {
    if(!simpleReportCategories) {
        simpleReportCategories = [NSDictionary dictionaryWithObjectsAndKeys:
           @"", [NSNumber numberWithInt:Empty],
           NSLocalizedString(@"Agency Representative",nil), [NSNumber numberWithInt:AgencyRepresentative],
           NSLocalizedString(@"Comp/Claim/Damage Advisory",nil), [NSNumber numberWithInt:CompClaimDamageAdvisory],
           NSLocalizedString(@"Finance Section",nil), [NSNumber numberWithInt:FinanceSection],
           NSLocalizedString(@"Ground Support Request",nil), [NSNumber numberWithInt:GroundSupportRequest],
           NSLocalizedString(@"Incident Commander",nil), [NSNumber numberWithInt:IncidentCommander],
           NSLocalizedString(@"Liaison Officer",nil), [NSNumber numberWithInt:LiaisonOfficer],
           NSLocalizedString(@"Logistics Section",nil), [NSNumber numberWithInt:LogisticsSection],
           NSLocalizedString(@"Operations Section",nil), [NSNumber numberWithInt:OperationsSection],
           NSLocalizedString(@"Other (See Message)",nil), [NSNumber numberWithInt:Other],
           NSLocalizedString(@"Plans Section",nil), [NSNumber numberWithInt:PlansSection],
           NSLocalizedString(@"Public Information Officer",nil), [NSNumber numberWithInt:PublicInformationOfficer],
           NSLocalizedString(@"Safety Officer",nil), [NSNumber numberWithInt:SafetyOfficer],
           NSLocalizedString(@"Suppression Repair",nil), [NSNumber numberWithInt:SuppressionRepair],
           nil];
    }
    return simpleReportCategories;
}

+ (NSArray *)simpleReportCategoriesList {
    return [[self simpleReportCategoriesDictionary] allValues];
}

+ (SimpleReportCategoryType) convertToSRCategoryFromString: (NSString *) type {
    return [[[simpleReportCategories allKeysForObject:type] lastObject] intValue];
}

+ (NSString *) convertSRCategoryTypeToAbreviation:(SimpleReportCategoryType) type {
    NSString* abreviation = @"";
    switch (type) {
        case Empty:
            abreviation = NSLocalizedString(@"NONE",nil);
            break;
            
        case AgencyRepresentative:
            abreviation = NSLocalizedString(@"AREP",nil);
            break;
            
        case CompClaimDamageAdvisory:
            abreviation = NSLocalizedString(@"DMG",nil);
            break;
            
        case FinanceSection:
            abreviation = NSLocalizedString(@"FSC",nil);
            break;
            
        case GroundSupportRequest:
            abreviation = NSLocalizedString(@"GSUL",nil);
            break;
            
        case IncidentCommander:
            abreviation = NSLocalizedString(@"IC",nil);
            break;
            
        case LiaisonOfficer:
            abreviation = NSLocalizedString(@"LNO",nil);
            break;
            
        case LogisticsSection:
            abreviation = NSLocalizedString(@"LSC",nil);
            break;
            
        case OperationsSection:
            abreviation = NSLocalizedString(@"OPS",nil);
            break;
            
        case Other:
            abreviation = NSLocalizedString(@"MSG",nil);
            break;
            
        case PlansSection:
            abreviation = NSLocalizedString(@"PLAN",nil);
            break;
            
        case PublicInformationOfficer:
            abreviation = NSLocalizedString(@"PIO",nil);
            break;
            
        case SafetyOfficer:
            abreviation = NSLocalizedString(@"SOFR",nil);
            break;
            
        case SuppressionRepair:
            abreviation = NSLocalizedString(@"SUPR",nil);
            break;
            
        default:
            abreviation = NSLocalizedString(@"MSG",nil);
            break;
    }
    
    return abreviation;
}

/*
+ (NSString *) convertSRCategoryTypeToNamedImage:(SimpleReportCategoryType) type {
    NSString* imageName = @"";
    switch (type) {
        case Boom:
            imageName = @"SRCategoryBoom";
            break;
        
        case Buoy:
            imageName = @"SRCategoryBuoy";
            break;
        
        case Casualty:
            imageName = @"SRCategoryCasualty";
            break;
        
        case Container:
            imageName = @"SRCategoryContainer";
            break;
        
        case Disaster:
            imageName = @"SRCategoryDisaster";
            break;
        
        case Environment:
            imageName = @"SRCategoryEnvironment";
            break;
        
        case Evidence:
            imageName = @"SRCategoryEvidence";
            break;
        
        case Fire:
            imageName = @"SRCategoryFire";
            break;
        
        case HAZMAT:
            imageName = @"SRCategoryHazmat";
            break;
        
        case Incident:
            imageName = @"SRCategoryIncident";
            break;
        
        case Information:
            imageName = @"SRCategoryInformation";
            break;
        
        case Oil:
            imageName = @"SRCategoryOil";
            break;
        
        case Safety:
            imageName = @"SRCategorySafety";
            break;
        
        case Salvage:
            imageName = @"SRCategorySalvage";
            break;
        
        case SAR:
            imageName = @"SRCategoryInformation";
            break;
        
        case TarBall:
            imageName = @"SRCategoryTarBall";
            break;
        
        case Terrorism:
            imageName = @"SRCategoryInformation";
            break;
        
        case Vessel:
            imageName = @"SRCategoryVessel";
            break;
        
        case Wildlife:
            imageName = @"SRCategoryWildlife";
            break;
        
        default:
            imageName = @"SRCategoryInformation";
            break;
    }
    
    return imageName;
}
*/

+ (ResourceRequestType) convertToResReqTypeFromString: (NSString *) type {
    if([type isEqualToString:NSLocalizedString(@"Vessel",nil)] || [type isEqualToString:NSLocalizedString(@"VL",nil)]) {
        return VL;
    } else if([type isEqualToString:NSLocalizedString(@"Vehicle",nil)] || [type isEqualToString:NSLocalizedString(@"VH",nil)]) {
        return VH;
    } else if([type isEqualToString:NSLocalizedString(@"Overhead",nil)] || [type isEqualToString:NSLocalizedString(@"O",nil)]) {
        return O;
    } else if([type isEqualToString:NSLocalizedString(@"Equipment",nil)] || [type isEqualToString:NSLocalizedString(@"E",nil)]) {
        return E;
    } else if([type isEqualToString:NSLocalizedString(@"Aircraft",nil)] || [type isEqualToString:NSLocalizedString(@"A",nil)]) {
        return A;
    } else if([type isEqualToString:NSLocalizedString(@"Helo",nil)] || [type isEqualToString:NSLocalizedString(@"H",nil)]) {
        return H;
    } else if([type isEqualToString:NSLocalizedString(@"Crew",nil)] || [type isEqualToString:NSLocalizedString(@"C",nil)]) {
        return C;
    }
    
    return VL;
}
+ (NSString *) convertResReqFullToAbbreviation:(NSString*) fullname {

    if([fullname  isEqualToString: NSLocalizedString(@"Vessel",nil)]){
        return NSLocalizedString(@"VL",nil);
    }else if([fullname isEqualToString:NSLocalizedString(@"Vehicle",nil)]){
        return NSLocalizedString(@"VH",nil);
    }else if([fullname isEqualToString:NSLocalizedString(@"Overhead",nil)]){
        return NSLocalizedString(@"O",nil);
    }else if([fullname isEqualToString:NSLocalizedString(@"Equipment",nil)]){
        return NSLocalizedString(@"E",nil);
    }else if([fullname isEqualToString:NSLocalizedString(@"Aircraft",nil)]){
        return NSLocalizedString(@"A",nil);
    }else if([fullname isEqualToString:NSLocalizedString(@"Helo",nil)]){
        return NSLocalizedString(@"H",nil);
    }else if([fullname isEqualToString:NSLocalizedString(@"Crew",nil)]){
        return NSLocalizedString(@"C",nil);
    }
    return NSLocalizedString(@"VL",nil);
}

+ (NSString *) convertResReqAbbreviationToFull:(NSString*) abbreviation {
    
    if([abbreviation  isEqualToString: NSLocalizedString(@"VL",nil)]){
        return NSLocalizedString(@"Vessel",nil);
    }else if([abbreviation isEqualToString:NSLocalizedString(@"VH",nil)]){
        return NSLocalizedString(@"Vehicle",nil);
    }else if([abbreviation isEqualToString:NSLocalizedString(@"O",nil)]){
        return NSLocalizedString(@"Overhead",nil);
    }else if([abbreviation isEqualToString:NSLocalizedString(@"E",nil)]){
        return NSLocalizedString(@"Equipment",nil);
    }else if([abbreviation isEqualToString:NSLocalizedString(@"A",nil)]){
        return NSLocalizedString(@"Aircraft",nil);
    }else if([abbreviation isEqualToString:NSLocalizedString(@"H",nil)]){
        return NSLocalizedString(@"Helo",nil);
    }else if([abbreviation isEqualToString:NSLocalizedString(@"C",nil)]){
        return NSLocalizedString(@"Crew",nil);
    }
    return NSLocalizedString(@"Vessel",nil);
}

+ (NSString *) convertResReqTypeToNamedImage:(ResourceRequestType) type {
    NSString* imageName = @"";
    switch (type) {
        case VL:
            imageName = @"ResReqTypeVessel";
            break;
        case VH:
            imageName = @"ResReqTypeVehicle";
            break;
        case O:
            imageName = @"ResReqTypeOverhead";
            break;
        case E:
            imageName = @"ResReqTypeEquipment";
            break;
        case A:
            imageName = @"ResReqTypeAircraft";
            break;
        case H:
            imageName = @"ResReqTypeHelicopter";
            break;
        case C:
            imageName = @"ResReqTypeCrew";
            break;
        default:
            break;
    }
    
    return imageName;
}


+ (MarkupType) convertToMarkupTypeFromString: (NSString *) type {
    if([type isEqualToString:@"symbol"] || [type isEqualToString:@"point"] || [type isEqualToString:@"marker"]) {
        return symbol;
    } else if([type isEqualToString:@"segment"] || [type isEqualToString:@"sketch"] || [type isEqualToString:@"line"]) {
        return segment;
    } else if([type isEqualToString:@"rectangle"]) {
        return rectangle;
    } else if([type isEqualToString:@"trapezoid"] || [type isEqualToString:@"box"] || [type isEqualToString:@"circle"] || [type isEqualToString:@"triangle"] || [type isEqualToString:@"polygon"] ||
 [type isEqualToString:@"hexagon"]) {
        return polygon;
    } else if([type isEqualToString:@"label"]) {
        return text;
    } else if([type isEqualToString:@"Explosive Report"]) {
        return ExplosiveReportMarkup;
    } else if([type isEqualToString:@"General Message"]) {
        return GeneralMessageMarkup;
    }else if([type isEqualToString:@"Damage Report"]) {
        return DamageReportMarkup;
    }
    
    return polygon;
}

@end

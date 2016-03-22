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
//  FieldReportData.m
//  nics_iOS
//
//

#import "FieldReportData.h"

@implementation FieldReportData

+(JSONKeyMapper*)keyMapper
{
//    return [JSONKeyMapper mapperFromUnderscoreCaseToCamelCase],

    return [[JSONKeyMapper alloc] initWithDictionary:
            @{
              
              @"fr-A-activitynum":@"frAactivitynum",
              @"fr-A-casenum":@"frAcasenum",
              @"fr-A-date":@"frAdate",
              @"fr-A-enforcenum":@"frAenforcenum",
              @"fr-A-fedprojnum":@"frAfedprojnum",
              @"fr-A-id":@"frAid",
              @"fr-A-invest":@"frAinvest",
              @"fr-A-location":@"frAlocation",
              @"fr-A-name":@"frAname",
              @"fr-A-source":@"frAsource",
              @"fr-A-ticketnum":@"frAticketnum",
              @"fr-B-descriptioncause":@"frBdescriptioncause",
              @"fr-B-descriptionspill":@"frBdescriptionspill",
              @"fr-B-matspilled":@"frBmatspilled",
              @"fr-B-pathdischarge":@"frBpathdischarge",
              @"fr-B-source":@"frBsource",
              @"fr-B-totalpot":@"frBtotalpot",
              @"fr-B-totalrecover":@"frBtotalrecover",
              @"fr-B-totalspilled":@"frBtotalspilled",
              @"fr-B-waterbody":@"frBwaterbody",
              @"fr-C-airtemp":@"frCairtemp",
              @"fr-C-currentdir":@"frCcurrentdir",
              @"fr-C-currentspeed":@"frCcurrentspeed",
              @"fr-C-precipitation":@"frCprecipitation",
              @"fr-C-seaheight":@"frCseaheight",
              @"fr-C-sunrise":@"frCsunrise",
              @"fr-C-sunset":@"frCsunset",
              @"fr-C-visibility":@"frCvisibility",
              @"fr-C-watertemp":@"frCwatertemp",
              @"fr-C-winddir":@"frCwinddir",
              @"fr-C-windspeed":@"frCwindspeed",
              @"fr-D-address":@"frDaddress",
              @"fr-D-indcompany":@"frDindcompany",
              @"fr-D-licensedoc":@"frDlicensedoc",
              @"fr-D-phone":@"frDphone",
              @"fr-D-relationship":@"frDrelationship",
              @"fr-D-witness1add":@"frDwitness1add",
              @"fr-D-witness1name":@"frDwitness1name",
              @"fr-D-witness1phone":@"frDwitness1phone",
              @"fr-D-witness2add":@"frDwitness2add",
              @"fr-D-witness2name":@"frDwitness2name",
              @"fr-D-witness2phone":@"frDwitness2phone",
              @"fr-E-cleansample":@"frEcleansample",
              @"fr-E-date":@"frEdate",
              @"fr-E-id":@"frEid",
              @"fr-E-number":@"frEnumber",
              @"fr-E-samp1":@"frEsamp1",
              @"fr-E-samp2":@"frEsamp2",
              @"fr-E-samp3":@"frEsamp3",
              @"fr-E-samp4":@"frEsamp4",
              @"fr-E-samplestaken":@"frEsamplestaken",
              @"fr-E-src":@"frEsrc",
              @"fr-E-syspectedsrc":@"frEsyspectedsrc",
              @"fr-E-time":@"frEtime",
              @"fr-E-witnesses":@"frEwitnesses",
              @"fr-F-address":@"frFaddress",
              @"fr-F-agent-phone":@"frFagentPhone",
              @"fr-F-agent":@"frFagent",
              @"fr-F-callsign":@"frFcallsign",
              @"fr-F-chiefeng":@"frFchiefeng",
              @"fr-F-cofr":@"frFcofr",
              @"fr-F-designation":@"frFdesignation",
              @"fr-F-flag":@"frFflag",
              @"fr-F-grosston":@"frFgrosston",
              @"fr-F-inspection":@"frFinspection",
              @"fr-F-iopp":@"frFiopp",
              @"fr-F-keeldate":@"frFkeeldate",
              @"fr-F-master":@"frFmaster",
              @"fr-F-name":@"frFname",
              @"fr-F-oiltrans":@"frFoiltrans",
              @"fr-F-ownerop":@"frFownerop",
              @"fr-F-personincharge":@"frFpersonincharge",
              @"fr-F-phone":@"frFphone",
              @"fr-F-recordbook":@"frFrecordbook",
              @"fr-F-sopep":@"frFsopep",
              @"fr-F-vessel":@"frFvessel",
              @"fr-G-address":@"frGaddress",
              @"fr-G-facility":@"frGfacility",
              @"fr-G-opsman":@"frGopsman",
              @"fr-G-otherfacadd":@"frGotherfacadd",
              @"fr-G-otherfacility":@"frGotherfacility",
              @"fr-G-otherfacphone":@"frGotherfacphone",
              @"fr-G-otherparty":@"frGotherparty",
              @"fr-G-othervessel":@"frGothervessel",
              @"fr-G-ownerop":@"frGownerop",
              @"fr-G-phone":@"frGphone",
              @"fr-G-type":@"frGtype",
              @"fr-H-negfac":@"frHnegfac",
              @"fr-H-posfac":@"frHposfac",
              @"fr-I-notes":@"frInotes"
              }];
}

@end

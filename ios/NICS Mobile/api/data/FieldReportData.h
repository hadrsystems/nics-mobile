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
//  FieldReportData.h
//  nics_iOS
//
//

#import "JSONModel.h"

@interface FieldReportData : JSONModel

@property NSString* frAactivitynum;
@property NSString* frAcasenum;
@property NSString* frAdate;
@property NSString* frAenforcenum;
@property NSString* frAfedprojnum;
@property NSString* frAid;
@property NSString* frAinvest;
@property NSString* frAlocation;
@property NSString* frAname;
@property NSString* frAsource;
@property NSString* frAticketnum;

@property NSString* frBdescriptioncause;
@property NSString* frBdescriptionspill;
@property NSString* frBmatspilled;
@property NSString* frBpathdischarge;
@property NSString* frBsource;
@property NSString* frBtotalpot;
@property NSString* frBtotalrecover;
@property NSString* frBtotalspilled;
@property NSString* frBwaterbody;

@property NSString* frCairtemp;
@property NSString* frCcurrentdir;
@property NSString* frCcurrentspeed;
@property NSString* frCprecipitation;
@property NSString* frCseaheight;
@property NSString* frCsunrise;
@property NSString* frCsunset;
@property NSString* frCvisibility;
@property NSString* frCwatertemp;
@property NSString* frCwinddir;
@property NSString* frCwindspeed;

@property NSString* frDaddress;
@property NSString* frDindcompany;
@property NSString* frDlicensedoc;
@property NSString* frDphone;
@property NSString* frDrelationship;
@property NSString* frDwitness1add;
@property NSString* frDwitness1name;
@property NSString* frDwitness1phone;
@property NSString* frDwitness2add;
@property NSString* frDwitness2name;
@property NSString* frDwitness2phone;

@property NSString* frEcleansample;
@property NSString* frEdate;
@property NSString* frEid;
@property NSString* frEnumber;
@property NSString* frEsamp1;
@property NSString* frEsamp2;
@property NSString* frEsamp3;
@property NSString* frEsamp4;
@property NSString* frEsamplestaken;
@property NSString* frEsrc;
@property NSString* frEsyspectedsrc;
@property NSString* frEtime;
@property NSString* frEwitnesses;

@property NSString* frFaddress;
@property NSString* frFagentPhone;
@property NSString* frFagent;
@property NSString* frFcallsign;
@property NSString* frFchiefeng;
@property NSString* frFcofr;
@property NSString* frFdesignation;
@property NSString* frFflag;
@property NSString* frFgrosston;
@property NSString* frFinspection;
@property NSString* frFiopp;
@property NSString* frFkeeldate;
@property NSString* frFmaster;
@property NSString* frFname;
@property NSString* frFoiltrans;
@property NSString* frFownerop;
@property NSString* frFpersonincharge;
@property NSString* frFphone;
@property NSString* frFrecordbook;
@property NSString* frFsopep;
@property NSString* frFvessel;

@property NSString* frGaddress;
@property NSString* frGfacility;
@property NSString* frGopsman;
@property NSString* frGotherfacadd;
@property NSString* frGotherfacility;
@property NSString* frGotherfacphone;
@property NSString* frGotherparty;
@property NSString* frGothervessel;
@property NSString* frGownerop;
@property NSString* frGphone;
@property NSString* frGtype;

@property NSString* frHnegfac;
@property NSString* frHposfac;

@property NSString* frInotes;

@property NSString* user;

@end

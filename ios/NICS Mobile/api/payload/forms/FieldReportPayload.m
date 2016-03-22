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
//  FieldReportPayload.m
//  nics_iOS
//
//

#import "FieldReportPayload.h"

@implementation FieldReportPayload

- (id)init {
    self = [super init];
    
    _messageData = [[FieldReportData alloc]init];
    
    _messageData.user = @"";
    
    _messageData.frAid = @"";
    _messageData.frAname = @"";
    _messageData.frAcasenum= @"";
    _messageData.frAsource = @"";
    _messageData.frAlocation = @"";
    _messageData.frAdate = @"";
    _messageData.frAinvest = @"";
    _messageData.frAactivitynum = @"";
    _messageData.frAenforcenum = @"";
    _messageData.frAticketnum = @"";
    _messageData.frAfedprojnum = @"";
    _messageData.frBmatspilled = @"";
    _messageData.frBtotalspilled = @"";
    _messageData.frBtotalpot = @"";
    _messageData.frBtotalrecover = @"";
    _messageData.frBsource = @"";
    _messageData.frBwaterbody = @"";
    _messageData.frBdescriptionspill = @"";
    _messageData.frBdescriptioncause= @"";
    _messageData.frBpathdischarge= @"";

    _messageData.frCwinddir = @"";
    _messageData.frCseaheight = @"";
    _messageData.frCcurrentdir = @"";
    _messageData.frCvisibility = @"";
    _messageData.frCsunset = @"";
    _messageData.frCairtemp = @"";
    _messageData.frCwindspeed = @"";
    _messageData.frCcurrentspeed = @"";
    _messageData.frCprecipitation= @"";
    _messageData.frCsunrise = @"";
    _messageData.frCwatertemp = @"";

    _messageData.frDindcompany = @"";
    _messageData.frDaddress = @"";
    _messageData.frDphone = @"";
    _messageData.frDrelationship = @"";
    _messageData.frDlicensedoc = @"";
    _messageData.frDwitness1name = @"";
    _messageData.frDwitness1add = @"";
    _messageData.frDwitness1phone= @"";
    _messageData.frDwitness2name = @"";
    _messageData.frDwitness2add = @"";
    _messageData.frDwitness2phone= @"";
    _messageData.frEsamplestaken = @"";
    _messageData.frEnumber = @"";
    _messageData.frEsyspectedsrc = @"";
    _messageData.frEid = @"";
    _messageData.frEdate = @"";
    _messageData.frEtime = @"";
    _messageData.frEwitnesses = @"";
    _messageData.frEsrc = @"";
    _messageData.frEcleansample = @"";
    _messageData.frEsamp1 = @"";
    _messageData.frEsamp2 = @"";
    _messageData.frEsamp3 = @"";
    _messageData.frEsamp4 = @"";
    _messageData.frFname = @"";
    _messageData.frFcallsign = @"";
    _messageData.frFflag = @"";
    _messageData.frFownerop = @"";
    _messageData.frFphone = @"";
    _messageData.frFgrosston = @"";
    _messageData.frFvessel = @"";
    _messageData.frFdesignation = @"";
    _messageData.frFaddress = @"";
    _messageData.frFagent = @"";
    _messageData.frFagentPhone = @"";
    _messageData.frFcofr = @"";
    _messageData.frFsopep = @"";
    _messageData.frFoiltrans = @"";
    _messageData.frFiopp = @"";
    _messageData.frFrecordbook = @"";
    _messageData.frFinspection = @"";
    _messageData.frFpersonincharge= @"";
    _messageData.frFmaster = @"";
    _messageData.frFchiefeng = @"";
    _messageData.frFkeeldate = @"";

    _messageData.frGfacility = @"";
    _messageData.frGtype = @"";
    _messageData.frGaddress = @"";
    _messageData.frGphone = @"";
    _messageData.frGownerop = @"";
    _messageData.frGopsman = @"";
    _messageData.frGotherparty = @"";
    _messageData.frGothervessel = @"";
    _messageData.frGotherfacility= @"";
    _messageData.frGotherfacadd = @"";
    _messageData.frGotherfacphone= @"";
    _messageData.frHnegfac = @"";
    _messageData.frHposfac = @"";
    
    _messageData.frInotes = @"";
    
    return self;
}

-(void)parse{
    if([self.message length] != 0) {
        
        NSError *e = nil;
        self.messageData = [[FieldReportData alloc] initWithString:self.message error:&e];
    }
}

-(NSMutableDictionary *) toSqlMapping {
    NSMutableDictionary *dataDictionary = [NSMutableDictionary new];
    
    if(self.isDraft) {
        [dataDictionary setObject: self.isDraft forKey:@"isDraft"];
    } else {
        [dataDictionary setObject: @0 forKey:@"isDraft"];
    }
    
    if(self.formid != nil) {
        [dataDictionary setObject:self.formid forKey:@"formId"];
    } else {
        [dataDictionary setObject:@0 forKey:@"formId"];
    }
    
//    if(self.senderUserId) {
//        [dataDictionary setObject:self.senderUserId forKey:@"senderUserId"];
//    } else {
//        [dataDictionary setObject:@0 forKey:@"senderUserId"];
//    }
    
    [dataDictionary setObject:self.usersessionid forKey:@"usersessionid"];
    [dataDictionary setObject:self.seqtime forKey:@"seqtime"];
    
    if(self.seqnum != nil) {
        [dataDictionary setObject:self.seqnum forKey:@"seqnum"];
    } else {
        [dataDictionary setObject:@0 forKey:@"seqnum"];
    }
    
    // Part A 'Incident Identification'
    if(_messageData.user != nil) {
        [dataDictionary setObject:_messageData.user forKey:@"user"];
    } else {
        [dataDictionary setObject:@"" forKey:@"user"];
    }
    
    @try {
        [dataDictionary setObject:_messageData.frAid forKey:@"incidentid"];
        [dataDictionary setObject:_messageData.frAname forKey:@"incidentName"];
        [dataDictionary setObject:_messageData.frAcasenum forKey:@"incidentPollutionCaseNumber"];
        [dataDictionary setObject:_messageData.frAsource forKey:@"incidentSourceOfIncident"];
        [dataDictionary setObject:_messageData.frAlocation forKey:@"incidentLocationOfIncident"];
        [dataDictionary setObject:_messageData.frAdate forKey:@"incidentDateOfIncident"];
        [dataDictionary setObject:_messageData.frAinvest forKey:@"incidentInvestigators"];
        [dataDictionary setObject:_messageData.frAactivitynum forKey:@"incidentActivityNumber"];
        [dataDictionary setObject:_messageData.frAenforcenum forKey:@"incidentEnforcementNumber"];
        [dataDictionary setObject:_messageData.frAticketnum forKey:@"incidentTicketNumber"];
        [dataDictionary setObject:_messageData.frAfedprojnum forKey:@"incidentFederalProjectNumber"];
        
        // Part B 'Discharge Data'
        [dataDictionary setObject:_messageData.frBmatspilled forKey:@"dischargeDataMaterialSpilled"];
        [dataDictionary setObject:_messageData.frBtotalspilled forKey:@"dischargeDataTotalSpilledEstimate"];
        [dataDictionary setObject:_messageData.frBtotalpot forKey:@"dischargeDataTotalPotential"];
        [dataDictionary setObject:_messageData.frBtotalrecover forKey:@"dischargeDataTotalRecovered"];
        [dataDictionary setObject:_messageData.frBsource forKey:@"dischargeDataSource"];
        [dataDictionary setObject:_messageData.frBwaterbody forKey:@"dischargeDataWaterBody"];
        [dataDictionary setObject:_messageData.frBdescriptionspill forKey:@"dischargeDataDescriptionOfSpill"];
        [dataDictionary setObject:_messageData.frBdescriptioncause forKey:@"dischargeDataDescriptionOfCause"];
        [dataDictionary setObject:_messageData.frBpathdischarge forKey:@"dischargeDataPathOfDischarge"];
        
        // Part C 'Weather and Tides'
        [dataDictionary setObject:_messageData.frCwinddir forKey:@"weatherAndTidesWindDirection"];
        [dataDictionary setObject:_messageData.frCseaheight forKey:@"weatherAndTidesSeaHeight"];
        [dataDictionary setObject:_messageData.frCcurrentdir forKey:@"weatherAndTidesCurrentDirection"];
        [dataDictionary setObject:_messageData.frCvisibility forKey:@"weatherAndTidesVisibility"];
        [dataDictionary setObject:_messageData.frCsunset forKey:@"weatherAndTidesSunset"];
        [dataDictionary setObject:_messageData.frCairtemp forKey:@"weatherAndTidesAirTemperature"];
        [dataDictionary setObject:_messageData.frCwindspeed forKey:@"weatherAndTidesWindSpeed"];
        [dataDictionary setObject:_messageData.frCcurrentspeed forKey:@"weatherAndTidesCurrentSpeed"];
        [dataDictionary setObject:_messageData.frCprecipitation forKey:@"weatherAndTidesPrecipitation"];
        [dataDictionary setObject:_messageData.frCsunrise forKey:@"weatherAndTidesSunrise"];
        [dataDictionary setObject:_messageData.frCwatertemp forKey:@"weatherAndTidesWaterTemp"];
        
        // Part D 'Suspect/Responisble Party and Witnesses'
        [dataDictionary setObject:_messageData.frDindcompany forKey:@"suspectAndWitnessesIndividualOrCompany"];
        [dataDictionary setObject:_messageData.frDaddress forKey:@"suspectAndWitnessesAddress"];
        [dataDictionary setObject:_messageData.frDphone forKey:@"suspectAndWitnessesPhone"];
        [dataDictionary setObject:_messageData.frDrelationship forKey:@"suspectAndWitnessesRelationship"];
        [dataDictionary setObject:_messageData.frDlicensedoc forKey:@"suspectAndWitnessesLicenseOrDocumentNumber"];
        [dataDictionary setObject:_messageData.frDwitness1name forKey:@"suspectAndWitnessesFirstWitnessName"];
        [dataDictionary setObject:_messageData.frDwitness1add forKey:@"suspectAndWitnessesFirstWitnessAddress"];
        [dataDictionary setObject:_messageData.frDwitness1phone forKey:@"suspectAndWitnessesFirstWitnessPhone"];
        [dataDictionary setObject:_messageData.frDwitness2name forKey:@"suspectAndWitnessesSecondWitnessName"];
        [dataDictionary setObject:_messageData.frDwitness2add forKey:@"suspectAndWitnessesSecondWitnessAddress"];
        [dataDictionary setObject:_messageData.frDwitness2phone forKey:@"suspectAndWitnessesSecondWitnessPhone"];
        
        // Part E 'Oil Samples'
        [dataDictionary setObject:_messageData.frEsamplestaken forKey:@"oilSamplesSamplesTaken"];
        [dataDictionary setObject:_messageData.frEnumber forKey:@"oilSamplesNumber"];
        [dataDictionary setObject:_messageData.frEsyspectedsrc forKey:@"oilSamplesSuspectedSource"];
        [dataDictionary setObject:_messageData.frEid forKey:@"oilSamplesIdentification"];
        [dataDictionary setObject:_messageData.frEdate forKey:@"oilSamplesDate"];
        [dataDictionary setObject:_messageData.frEtime forKey:@"oilSamplesTime"];
        [dataDictionary setObject:_messageData.frEwitnesses forKey:@"oilSamplesWitness"];
        [dataDictionary setObject:_messageData.frEsrc forKey:@"oilSamplesSource"];
        [dataDictionary setObject:_messageData.frEcleansample forKey:@"oilSamplesCleanSample"];
        [dataDictionary setObject:_messageData.frEsamp1 forKey:@"oilSamplesSuspectedSourceSample1"];
        [dataDictionary setObject:_messageData.frEsamp2 forKey:@"oilSamplesSuspectedSourceSample2"];
        [dataDictionary setObject:_messageData.frEsamp3 forKey:@"oilSamplesSuspectedSourceSample3"];
        [dataDictionary setObject:_messageData.frEsamp4 forKey:@"oilSamplesSuspectedSourceSample4"];
        
        // Part F 'Vessel'
        [dataDictionary setObject:_messageData.frFname forKey:@"vesselName"];
        [dataDictionary setObject:_messageData.frFcallsign forKey:@"vesselCallSign"];
        [dataDictionary setObject:_messageData.frFflag forKey:@"vesselFlag"];
        [dataDictionary setObject:_messageData.frFownerop forKey:@"vesselOwnerOrOperator"];
        [dataDictionary setObject:_messageData.frFphone forKey:@"vesselPhone"];
        [dataDictionary setObject:_messageData.frFgrosston forKey:@"vesselGrossTonnage"];
        [dataDictionary setObject:_messageData.frFvessel forKey:@"vesselTypeOfVessel"];
        [dataDictionary setObject:_messageData.frFdesignation forKey:@"vesselDestination"];
        [dataDictionary setObject:_messageData.frFaddress forKey:@"vesselAddress"];
        [dataDictionary setObject:_messageData.frFagent forKey:@"vesselAgent"];
        [dataDictionary setObject:_messageData.frFagentPhone forKey:@"vesselAgentPhone"];
        [dataDictionary setObject:_messageData.frFcofr forKey:@"vesselCofr"];
        [dataDictionary setObject:_messageData.frFsopep forKey:@"vesselSopep"];
        [dataDictionary setObject:_messageData.frFoiltrans forKey:@"vesselOilTransportationProcedures"];
        [dataDictionary setObject:_messageData.frFiopp forKey:@"vesselIopp"];
        [dataDictionary setObject:_messageData.frFrecordbook forKey:@"vesselOilRecordBook"];
        [dataDictionary setObject:_messageData.frFinspection forKey:@"vesselDeclarationOfInspection"];
        [dataDictionary setObject:_messageData.frFpersonincharge forKey:@"vesselPersonInCharge"];
        [dataDictionary setObject:_messageData.frFmaster forKey:@"vesselMaster"];
        [dataDictionary setObject:_messageData.frFchiefeng forKey:@"vesselChiefEngineer"];
        [dataDictionary setObject:_messageData.frFkeeldate forKey:@"vesselKeelLaidDate"];
        
        // Part G 'Facility and Other Parties'
        [dataDictionary setObject:_messageData.frGfacility forKey:@"facilityAndOtherPartiesFacility"];
        [dataDictionary setObject:_messageData.frGtype forKey:@"facilityAndOtherPartiesType"];
        [dataDictionary setObject:_messageData.frGaddress forKey:@"facilityAndOtherPartiesAddress"];
        [dataDictionary setObject:_messageData.frGphone forKey:@"facilityAndOtherPartiesPhone"];
        [dataDictionary setObject:_messageData.frGownerop forKey:@"facilityAndOtherPartiesOwnerOrOperator"];
        [dataDictionary setObject:_messageData.frGopsman forKey:@"facilityAndOtherPartiesOperationsManual"];
        [dataDictionary setObject:_messageData.frGotherparty forKey:@"facilityAndOtherPartiesOtherParty"];
        [dataDictionary setObject:_messageData.frGothervessel forKey:@"facilityAndOtherPartiesOtherVessel"];
        [dataDictionary setObject:_messageData.frGotherfacility forKey:@"facilityAndOtherPartiesOtherFacility"];
        [dataDictionary setObject:_messageData.frGotherfacadd forKey:@"facilityAndOtherPartiesOtherFacilityAddress"];
        [dataDictionary setObject:_messageData.frGotherfacphone forKey:@"facilityAndOtherPartiesOtherFacilityPhone"];
        
        // Part H 'Other Factors'
        [dataDictionary setObject:_messageData.frHnegfac forKey:@"factorsAggravating"];
        [dataDictionary setObject:_messageData.frHposfac forKey:@"factorsMitigating"];
        
        
        [dataDictionary setObject:_messageData.frInotes forKey:@"notes"];
        
        if(self.status) {
            [dataDictionary setObject:self.status forKey:@"status"];
        } else {
            [dataDictionary setObject:@-1 forKey:@"status"];
        }
        
    } @catch (NSException* e) {
        NSLog(@"Exception: %@", e);
    }
        [dataDictionary setObject:[self toJSONString] forKey:@"json"];
    
    return dataDictionary;
}

@end

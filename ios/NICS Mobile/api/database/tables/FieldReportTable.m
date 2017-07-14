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
//  FieldReportTable.m
//  nics_iOS
//
//

#import "FieldReportTable.h"

@implementation FieldReportTable
static NSDictionary * tableColumnsDictionary;

- (id)initWithName:(NSString *)tableName databaseQueue:(FMDatabaseQueue *) databaseQueue
{
    self = [super initWithName:tableName databaseQueue:databaseQueue];
    if (self) {
        tableColumnsDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:
                                    @"integer primary key", @"id",
                                    @"integer",             @"isDraft",
                                    @"integer",             @"createdUTC",
                                    @"integer",             @"lastUpdatedUTC",
                                    @"integer",             @"formId",
                                    @"integer",             @"senderUserId",
                                    @"integer",             @"userSessionId",
                                    @"integer",             @"seqTime",
                                    @"integer",             @"seqNum",
                                    @"integer",             @"status",
                                    @"text",                @"user",
                                  
                                    // Field Report - Part A 'Incident Identification'
                                    @"text",                @"incidentId",
                                    @"text",                @"incidentName",
                                    @"text",                @"incidentPollutionCaseNumber",
                                    @"text",                @"incidentSourceOfIncident",
                                    @"text",                @"incidentLocationOfIncident",
                                    @"text",                @"incidentDateOfIncident",
                                    @"text",                @"incidentInvestigators",
                                    @"text",                @"incidentActivityNumber",
                                    @"text",                @"incidentEnforcementNumber",
                                    @"text",                @"incidentTicketNumber",
                                    @"text",                @"incidentFederalProjectNumber",
                                  
                                    // Field Report - Part B 'Discharge Data'
                                    @"text",                @"dischargeDataMaterialSpilled",
                                    @"text",                @"dischargeDataTotalSpilledEstimate",
                                    @"text",                @"dischargeDataTotalPotential",
                                    @"text",                @"dischargeDataTotalRecovered",
                                    @"text",                @"dischargeDataSource",
                                    @"text",                @"dischargeDataWaterBody",
                                    @"text",                @"dischargeDataDescriptionOfSpill",
                                    @"text",                @"dischargeDataDescriptionOfCause",
                                    @"text",                @"dischargeDataPathOfDischarge",
                                  
                                    // Field Report - Part C 'Weather and Tides'
                                    @"text",                @"weatherAndTidesWindDirection",
                                    @"text",                @"weatherAndTidesSeaHeight",
                                    @"text",                @"weatherAndTidesCurrentDirection",
                                    @"text",                @"weatherAndTidesVisibility",
                                    @"text",                @"weatherAndTidesSunset",
                                    @"text",                @"weatherAndTidesAirTemperature",
                                    @"text",                @"weatherAndTidesWindSpeed",
                                    @"text",                @"weatherAndTidesCurrentSpeed",
                                    @"text",                @"weatherAndTidesPrecipitation",
                                    @"text",                @"weatherAndTidesSunrise",
                                    @"text",                @"weatherAndTidesWaterTemp",
                                  
                                    // Field Report - Part D 'Suspect/Responsible Party and Witnesses'
                                    @"text",                @"suspectAndWitnessesIndividualOrCompany",
                                    @"text",                @"suspectAndWitnessesAddress",
                                    @"text",                @"suspectAndWitnessesPhone",
                                    @"text",                @"suspectAndWitnessesRelationship",
                                    @"text",                @"suspectAndWitnessesLicenseOrDocumentNumber",
                                    @"text",                @"suspectAndWitnessesFirstWitnessName",
                                    @"text",                @"suspectAndWitnessesFirstWitnessAddress",
                                    @"text",                @"suspectAndWitnessesFirstWitnessPhone",
                                    @"text",                @"suspectAndWitnessesSecondWitnessName",
                                    @"text",                @"suspectAndWitnessesSecondWitnessAddress",
                                    @"text",                @"suspectAndWitnessesSecondWitnessPhone",
                                  
                                    // Field Report - Part E 'Oil Samples'
                                    @"text",                @"oilSamplesSamplesTaken",
                                    @"text",                @"oilSamplesNumber",
                                    @"text",                @"oilSamplesSuspectedSource",
                                    @"text",                @"oilSamplesIdentification",
                                    @"text",                @"oilSamplesDate",
                                    @"text",                @"oilSamplesTime",
                                    @"text",                @"oilSamplesWitness",
                                    @"text",                @"oilSamplesSource",
                                    @"text",                @"oilSamplesCleanSample",
                                    @"text",                @"oilSamplesSuspectedSourceSample1",
                                    @"text",                @"oilSamplesSuspectedSourceSample2",
                                    @"text",                @"oilSamplesSuspectedSourceSample3",
                                    @"text",                @"oilSamplesSuspectedSourceSample4",
                                  
                                    // Field Report - Part F 'Vessel'
                                    @"text",                @"vesselName",
                                    @"text",                @"vesselCallSign",
                                    @"text",                @"vesselFlag",
                                    @"text",                @"vesselOwnerOrOperator",
                                    @"text",                @"vesselPhone",
                                    @"text",                @"vesselGrossTonnage",
                                    @"text",                @"vesselTypeOfVessel",
                                    @"text",                @"vesselDestination",
                                    @"text",                @"vesselAddress",
                                    @"text",                @"vesselAgent",
                                    @"text",                @"vesselAgentPhone",
                                    @"text",                @"vesselCofr",
                                    @"text",                @"vesselSopep",
                                    @"text",                @"vesselOilTransportationProcedures",
                                    @"text",                @"vesselIopp",
                                    @"text",                @"vesselOilRecordBook",
                                    @"text",                @"vesselDeclarationOfInspection",
                                    @"text",                @"vesselPersonInCharge",
                                    @"text",                @"vesselMaster",
                                    @"text",                @"vesselChiefEngineer",
                                    @"text",                @"vesselKeelLaidDate",
                                  
                                    // Field Report - Part G 'Facility and Other Parties'
                                    @"text",                @"facilityAndOtherPartiesFacility",
                                    @"text",                @"facilityAndOtherPartiesType",
                                    @"text",                @"facilityAndOtherPartiesAddress",
                                    @"text",                @"facilityAndOtherPartiesPhone",
                                    @"text",                @"facilityAndOtherPartiesOwnerOrOperator",
                                    @"text",                @"facilityAndOtherPartiesOperationsManual",
                                    @"text",                @"facilityAndOtherPartiesOtherParty",
                                    @"text",                @"facilityAndOtherPartiesOtherVessel",
                                    @"text",                @"facilityAndOtherPartiesOtherFacility",
                                    @"text",                @"facilityAndOtherPartiesOtherFacilityAddress",
                                    @"text",                @"facilityAndOtherPartiesOtherFacilityPhone",
                                  
                                    // Field Report - Part H 'Other Factors'
                                    @"text",                @"factorsAggravating",
                                    @"text",                @"factorsMitigating",

                                    // Field Report - Part I 'Notes'
                                    @"text",                @"notes",
                                    @"text",                @"json",
                                  
                                    nil
                                 ];
        
        [self createTableFromDictionary:tableColumnsDictionary];
        

    }
    return self;
}


- (BOOL) addData:(FieldReportPayload *) data
{
    return [self insertRowForTableDictionary:tableColumnsDictionary dataDictionary:[data toSqlMapping]];
}

- (void) removeData:(FieldReportPayload *) data
{
    [self deleteRowsByKey:@"id" value:data.id];
}

- (BOOL) addDataArray:(NSArray *) dataArray {
    NSMutableArray* messagePayloads = [[NSMutableArray alloc] init];
    for(FieldReportPayload *payload in dataArray) {
        [messagePayloads addObject:[payload toSqlMapping]];
    }
    
    return [self insertAllRowsForTableDictionary:tableColumnsDictionary dataArray:messagePayloads];
}

- (NSNumber *) getLastReportTimestampForIncidentId: (NSNumber *)incidentId {
    NSDictionary* result = [[self selectRowsByKey:@"incidentId" value:incidentId orderedBy:[NSArray arrayWithObject:@"lastUpdatedUTC"] isDescending:YES] firstObject];
    
    if(result != nil) {
        return [result objectForKey:@"lastUpdatedUTC"];
    } else {
        return @0;
    }
}

- (NSMutableArray<FieldReportPayload> *) getFieldReportsForIncidentId: (NSNumber *)incidentId since: (NSNumber *)timestamp {
    NSDictionary * keys = [[NSDictionary alloc] initWithObjectsAndKeys:
                            incidentId,     @"incidentId = ?",
                            timestamp,      @"lastUpdatedUTC > ?",
                            nil];

    NSMutableArray *results = [self selectRowsByKeyDictionary:keys orderedBy:[NSArray arrayWithObject:@"lastUpdatedUTC"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        FieldReportPayload *payload = [[FieldReportPayload alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [payload parse];
        [payload setId:[result objectForKey:@"id"]];
        
        if(error != nil) {
            NSLog(@"%@", [error localizedDescription]);
        }
        
        [parsedResults addObject: payload];
    }
    
    return (NSMutableArray<FieldReportPayload>*) parsedResults;
}

- (NSMutableArray<FieldReportPayload> *) getAllFieldReports {
    NSMutableArray *results = [self selectAllRowsAndOrderedBy:[NSArray arrayWithObject:@"lastUpdatedUTC"] isDescending:YES];
    
    NSMutableArray *parsedResults = [NSMutableArray new];
    
    NSError *error;
    
    for(NSDictionary *result in results) {
        FieldReportPayload *payload = [[FieldReportPayload alloc] initWithString:[result objectForKey:@"json"] error:&error];
        [payload parse];
        [payload setId:[result objectForKey:@"id"]];
        [payload setFormtypeid:[result objectForKey:@"formtypeid"]];
        
        if(error != nil) {
            NSLog(@"%@", [error localizedDescription]);
        }
        
        [parsedResults addObject: payload];
    }
    
    return (NSMutableArray<FieldReportPayload>*) parsedResults;
}

@end

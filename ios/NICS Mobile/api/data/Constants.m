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
//  Constants.m
//  NICS Mobile
//
//
//

#import "Constants.h"

@implementation Constants

static NSArray * MAP_MARKUP_SYMBOL_IMAGE_NAMES;

+(NSArray*)GET_MAP_MARKUP_SYMBOL_IMAGE_NAMES{
    
    if(MAP_MARKUP_SYMBOL_IMAGE_NAMES == nil){
        [self initMapMarkupSymbolImageNames];
    }
    
    return  MAP_MARKUP_SYMBOL_IMAGE_NAMES;
}

+(void)initMapMarkupSymbolImageNames{
    MAP_MARKUP_SYMBOL_IMAGE_NAMES = [NSArray arrayWithObjects:
                                     //common symbols
                                     @"0.png",
                                     @"1.png",
                                     @"2.png",
                                     @"3.png",
                                     @"4.png",
                                     @"5.png",
                                     @"6.png",
                                     @"7.png",
                                     @"8.png",
                                     @"9.png",
                                     @"a.png",
                                     @"b.png",
                                     @"c.png",
                                     @"d.png",
                                     @"e.png",
                                     @"f.png",
                                     @"g.png",
                                     @"h.png",
                                     @"i.png",
                                     @"j.png",
                                     @"k.png",
                                     @"l.png",
                                     @"m.png",
                                     @"n.png",
                                     @"o.png",
                                     @"p.png",
                                     @"q.png",
                                     @"r.png",
                                     @"s.png",
                                     @"t.png",
                                     @"u.png",
                                     @"v.png",
                                     @"w.png",
                                     @"x.png",
                                     @"y.png",
                                     @"z.png",
                                     @"aerial_ignition.png",
                                     @"aerial-hazard.png",
                                     @"branch_break2.png",
                                     @"camp.png",
                                     @"division_break2.png",
                                     @"dry_point2.png",
                                     @"f-01.png",
                                     @"fire_origin2.png",
                                     @"first_aid.png",
                                     @"flag-01.png",
                                     @"heat_source2.png",
                                     @"helibase2.png",
                                     @"helispot.png",
                                     @"hot_spot2.png",
                                     @"icp.png",
                                     @"incident_base2.png",
                                     @"ir-downlink.png",
                                     @"life-hazard.png",
                                     @"medivac.png",
                                     @"repeater_mobile_relay2.png",
                                     @"safety-zone.png",
                                     @"segment-01.png",
                                     @"small_x.png",
                                     @"staging_area2.png",
                                     @"t-01.png",
                                     @"water_source2.png",
                                     @"wind.png",
                                     @"x-01.png",
                                     @"zone-break.png",
                                     
                                     //incident symbols
                                     @"civil_disturbance_theme_ch.png",
                                     @"crime_bomb_threat_ch.png",
                                     @"crime_shooting.png",
                                     @"criminal_activity_theme_ch.png",
                                     @"e_med_ambulance_s1_ch.png",
                                     @"e_med_emergency_medical_theme_s1.png",
                                     @"e_med_emt_station_locations_s1.png",
                                     @"e_med_evacuation_helicopter_station_s1.png",
                                     @"e_med_health_department_facility_s1.png",
                                     @"e_med_hospital_s1.png",
                                     @"e_med_hospital_ship_s1.png",
                                     @"e_med_medical_facilities_out_patient_s1.png",
                                     @"e_med_morgue_s1.png",
                                     @"e_med_pharmacies_s1.png",
                                     @"e_med_triage_s1.png",
                                     @"emergency_collection_evacuation_point_s1_ch.png",
                                     @"emergency_food_distribution_centers_s1.png",
                                     @"emergency_incident_command_center_s1.png",
                                     @"emergency_operations_center_s1.png",
                                     @"emergency_operations_theme_s1.png",
                                     @"emergency_public_information_center_s1_ch.png",
                                     @"emergency_shelters_s1.png",
                                     @"emergency_staging_areas_s1.png",
                                     @"emergency_teams_s1.png",
                                     @"emergency_water_distribution_center_s1_ch.png",
                                     @"emgergency_food_distribution_centers_s1.png",
                                     @"fire_hydrant_s1.png",
                                     @"fire_other_water_supply_location_s1.png",
                                     @"fire_station_s1.png",
                                     @"fire_suppression_theme_s1.png",
                                     @"fire_theme.png",
                                     @"geo_after_shock.png",
                                     @"geo_earth_quake_epicenter.png",
                                     @"geo_landslide.png",
                                     @"geo_subsidence.png",
                                     @"hazmat_hazardous_theme.png",
                                     @"hydro_meteor_flood.png",
                                     @"hydro_meteor_fog.png",
                                     @"hydro_meteor_hail.png",
                                     @"hydro_meteor_rain.png",
                                     @"hydro_meteor_snow.png",
                                     @"hydro_meteor_thunder_storm.png",
                                     @"hydro_meteor_tornado_ch.png",
                                     @"hydro_meteor_tropical_cyclone.png",
                                     @"hydro_meteor_tsunami_ch.png",
                                     @"law_atf_s1.png",
                                     @"law_border_patrol_s1.png",
                                     @"law_customs_service_s1.png",
                                     @"law_customs_srvice_s1.png",
                                     @"law_dea_s1.png",
                                     @"law_doj_s1.png",
                                     @"law_enforcement_theme_s1.png",
                                     @"law_fbi_s1.png",
                                     @"law_police_s1.png",
                                     @"law_prison_s1.png",
                                     @"law_secret_service_s1.png",
                                     @"law_tsa_s1.png",
                                     @"law_us_coast_guard_s1.png",
                                     @"law_us_marshall_s1.png",
                                     @"mil_soldier.png",
                                     @"mil_vehicle.png",
                                     @"transport_air_theme.png",
                                     @"transport_marine_theme.png",
                                     @"transport_rail_theme.png",
                                     @"transport_vehicle_theme.png",
                                     //National Guard Symbols
                                     @"airdefenseartillery.png",
                                     @"armor.png",
                                     @"aviation.png",
                                     @"calvary.png",
                                     @"chemical.png",
                                     @"cs_aviation.png",
                                     @"engineer.png",
                                     @"fieldartillery.png",
                                     @"infantry.png",
                                     @"militaryintelligence.png",
                                     @"militarypolice.png",
                                     @"signal.png",
                                     @"specialforces.png",
                                     nil];
}

@end

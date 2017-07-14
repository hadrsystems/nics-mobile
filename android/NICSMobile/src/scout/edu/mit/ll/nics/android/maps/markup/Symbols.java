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
package scout.edu.mit.ll.nics.android.maps.markup;

import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import scout.edu.mit.ll.nics.android.R;

public class Symbols {

	public static OrderedBidiMap<String, Integer> ALL;
	
	public static final OrderedBidiMap<String, Integer> COMMON;
	public static final OrderedBidiMap<String, Integer> INCIDENT;
	public static final OrderedBidiMap<String, Integer> NG;
	
	public static final OrderedBidiMap<String, Integer> WEATHER;
	
	static {
		TreeBidiMap<String, Integer> commonTemp = new TreeBidiMap<String, Integer>();
		
		// COMMON SYMBOLS
		commonTemp.put("images/drawmenu/markers/fire_origin2.png",  R.drawable.fire_origin2);
		commonTemp.put("images/drawmenu/markers/icp.png",  R.drawable.icp);
		commonTemp.put("images/drawmenu/markers/incident_base2.png",  R.drawable.incident_base2);
		commonTemp.put("images/drawmenu/markers/camp.png",  R.drawable.camp);
		commonTemp.put("images/drawmenu/markers/staging_area2.png",  R.drawable.staging_area2);
		commonTemp.put("images/drawmenu/markers/water_source2.png",  R.drawable.water_source2);
		commonTemp.put("images/drawmenu/markers/division_break2.png",  R.drawable.division_break2);
		commonTemp.put("images/drawmenu/markers/branch_break2.png",  R.drawable.branch_break2);
		commonTemp.put("images/drawmenu/markers/zone-break.png",  R.drawable.zone_break);
		commonTemp.put("images/drawmenu/markers/hot_spot2.png",  R.drawable.hot_spot2);
		commonTemp.put("images/drawmenu/markers/dry_point2.png",  R.drawable.dry_point2);
		commonTemp.put("images/drawmenu/markers/heat_source2.png",  R.drawable.heat_source2);
		commonTemp.put("images/drawmenu/markers/medivac.png",  R.drawable.medivac);
		commonTemp.put("images/drawmenu/markers/helibase2.png",  R.drawable.helibase2);
		commonTemp.put("images/drawmenu/markers/helispot.png",  R.drawable.helispot);
		commonTemp.put("images/drawmenu/markers/life-hazard.png",  R.drawable.life_hazard);
		commonTemp.put("images/drawmenu/markers/safety-zone.png",  R.drawable.safety_zone);
		commonTemp.put("images/drawmenu/markers/aerial-hazard.png",  R.drawable.aerial_hazard);
		commonTemp.put("images/drawmenu/markers/repeater_mobile_relay2.png",  R.drawable.repeater_mobile_relay2);
		commonTemp.put("images/drawmenu/markers/ir-downlink.png",  R.drawable.ir_downlink);
		commonTemp.put("images/drawmenu/markers/wind.png",  R.drawable.windico);
		commonTemp.put("images/drawmenu/markers/aerial_ignition.png",  R.drawable.aerial_ignition);
		commonTemp.put("images/drawmenu/markers/F-01.png",  R.drawable.f_01);
		commonTemp.put("images/drawmenu/markers/first_aid.png",  R.drawable.first_aid);
		commonTemp.put("images/drawmenu/markers/flag-01.png",  R.drawable.flag_01);
		commonTemp.put("images/drawmenu/markers/segment-01.png",  R.drawable.segment_01);
		commonTemp.put("images/drawmenu/markers/T-01.png",  R.drawable.t_01);
		commonTemp.put("images/drawmenu/markers/X-01.png",  R.drawable.x_01);
		
		commonTemp.put("images/drawmenu/markers/0.png",  R.drawable.text0);
		commonTemp.put("images/drawmenu/markers/1.png",  R.drawable.text1);
		commonTemp.put("images/drawmenu/markers/2.png",  R.drawable.text2);
		commonTemp.put("images/drawmenu/markers/3.png",  R.drawable.text3);
		commonTemp.put("images/drawmenu/markers/4.png",  R.drawable.text4);
		commonTemp.put("images/drawmenu/markers/5.png",  R.drawable.text5);
		commonTemp.put("images/drawmenu/markers/6.png",  R.drawable.text6);
		commonTemp.put("images/drawmenu/markers/7.png",  R.drawable.text7);
		commonTemp.put("images/drawmenu/markers/8.png",  R.drawable.text8);
		commonTemp.put("images/drawmenu/markers/9.png",  R.drawable.text9);
		
		commonTemp.put("images/drawmenu/markers/a.png",  R.drawable.a);
		commonTemp.put("images/drawmenu/markers/b.png",  R.drawable.b);
		commonTemp.put("images/drawmenu/markers/c.png",  R.drawable.c);
		commonTemp.put("images/drawmenu/markers/d.png",  R.drawable.d);
		commonTemp.put("images/drawmenu/markers/e.png",  R.drawable.e);
		commonTemp.put("images/drawmenu/markers/f.png",  R.drawable.f);
		commonTemp.put("images/drawmenu/markers/g.png",  R.drawable.g);
		commonTemp.put("images/drawmenu/markers/h.png",  R.drawable.h);
		commonTemp.put("images/drawmenu/markers/i.png",  R.drawable.i);
		commonTemp.put("images/drawmenu/markers/j.png",  R.drawable.j);
		commonTemp.put("images/drawmenu/markers/k.png",  R.drawable.k);
		commonTemp.put("images/drawmenu/markers/l.png",  R.drawable.l);
		commonTemp.put("images/drawmenu/markers/m.png",  R.drawable.m);
		commonTemp.put("images/drawmenu/markers/n.png",  R.drawable.n);
		commonTemp.put("images/drawmenu/markers/o.png",  R.drawable.o);
		commonTemp.put("images/drawmenu/markers/p.png",  R.drawable.p);
		commonTemp.put("images/drawmenu/markers/q.png",  R.drawable.q);
		commonTemp.put("images/drawmenu/markers/r.png",  R.drawable.r);
		commonTemp.put("images/drawmenu/markers/s.png",  R.drawable.s);
		commonTemp.put("images/drawmenu/markers/t.png",  R.drawable.t);
		commonTemp.put("images/drawmenu/markers/u.png",  R.drawable.u);
		commonTemp.put("images/drawmenu/markers/v.png",  R.drawable.v);
		commonTemp.put("images/drawmenu/markers/w.png",  R.drawable.w);
		commonTemp.put("images/drawmenu/markers/x.png",  R.drawable.x);
		commonTemp.put("images/drawmenu/markers/y.png",  R.drawable.y);
		commonTemp.put("images/drawmenu/markers/z.png",  R.drawable.z);
		
		commonTemp.put("images/drawmenu/markers/small_x.png",  R.drawable.small_x);
		
        COMMON = commonTemp;
        
        
        // INCIDENT SYMBOLS
		TreeBidiMap<String, Integer> incidentTemp = new TreeBidiMap<String, Integer>();
		
		incidentTemp.put("images/drawmenu/markers/Civil_Disturbance_Theme_ch.png", R.drawable.civil_disturbance_theme_ch);
		incidentTemp.put("images/drawmenu/markers/Criminal_Activity_Theme_ch.png", R.drawable.criminal_activity_theme_ch);
		incidentTemp.put("images/drawmenu/markers/Crime_Bomb_Threat_ch.png", R.drawable.crime_bomb_threat_ch);
		incidentTemp.put("images/drawmenu/markers/Crime_Shooting.png", R.drawable.crime_shooting);
		incidentTemp.put("images/drawmenu/markers/Fire_Theme.png", R.drawable.fire_theme);
		incidentTemp.put("images/drawmenu/markers/Hazmat_Hazardous_Theme.png", R.drawable.hazmat_hazardous_theme);
		incidentTemp.put("images/drawmenu/markers/Transport_Air_Theme.png", R.drawable.transport_air_theme);
		incidentTemp.put("images/drawmenu/markers/Transport_Marine_Theme.png", R.drawable.transport_marine_theme);
		incidentTemp.put("images/drawmenu/markers/Transport_Rail_Theme.png", R.drawable.transport_rail_theme);
		incidentTemp.put("images/drawmenu/markers/Transport_Vehicle_Theme.png", R.drawable.transport_vehicle_theme);
		incidentTemp.put("images/drawmenu/markers/Geo_After_Shock.png", R.drawable.geo_after_shock);
		incidentTemp.put("images/drawmenu/markers/Geo_Earth_Quake_Epicenter.png", R.drawable.geo_earth_quake_epicenter);
		incidentTemp.put("images/drawmenu/markers/Geo_Landslide.png", R.drawable.geo_landslide);
		incidentTemp.put("images/drawmenu/markers/Geo_Subsidence.png", R.drawable.geo_subsidence);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Flood.png", R.drawable.hydro_meteor_flood);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Fog.png", R.drawable.hydro_meteor_fog);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Hail.png", R.drawable.hydro_meteor_hail);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Rain.png", R.drawable.hydro_meteor_rain);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Snow.png", R.drawable.hydro_meteor_snow);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Thunder_Storm.png", R.drawable.hydro_meteor_thunder_storm);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Tornado_ch.png", R.drawable.hydro_meteor_tornado_ch);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Tropical_Cyclone.png", R.drawable.hydro_meteor_tropical_cyclone);
		incidentTemp.put("images/drawmenu/markers/Hydro_Meteor_Tsunami_ch.png", R.drawable.hydro_meteor_tsunami_ch);
		incidentTemp.put("images/drawmenu/markers/mil_soldier.png", R.drawable.mil_soldier);
		incidentTemp.put("images/drawmenu/markers/mil_vehicle.png", R.drawable.mil_vehicle);
		incidentTemp.put("images/drawmenu/markers/E_Med_Emergency_Medical_Theme_S1.png", R.drawable.e_med_emergency_medical_theme_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_EMT_Station_Locations_S1.png", R.drawable.e_med_emt_station_locations_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Ambulance_S1_ch.png", R.drawable.e_med_ambulance_s1_ch);
		incidentTemp.put("images/drawmenu/markers/E_Med_Evacuation_Helicopter_Station_S1.png", R.drawable.e_med_evacuation_helicopter_station_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Health_Department_Facility_S1.png", R.drawable.e_med_health_department_facility_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Hospital_S1.png", R.drawable.e_med_hospital_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Hospital_Ship_S1.png", R.drawable.e_med_hospital_ship_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Medical_Facilities_Out_Patient_S1.png", R.drawable.e_med_medical_facilities_out_patient_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Morgue_S1.png", R.drawable.e_med_morgue_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Pharmacies_S1.png", R.drawable.e_med_pharmacies_s1);
		incidentTemp.put("images/drawmenu/markers/E_Med_Triage_S1.png", R.drawable.e_med_triage_s1);
		incidentTemp.put("images/drawmenu/markers/Emergency_Operations_Theme_S1.png", R.drawable.emergency_operations_theme_s1);
		incidentTemp.put("images/drawmenu/markers/Emergency_Collection_Evacuation_Point_S1_ch.png", R.drawable.emergency_collection_evacuation_point_s1_ch);
		incidentTemp.put("images/drawmenu/markers/Emergency_Incident_Command_Center_S1.png", R.drawable.emergency_incident_command_center_s1);
		incidentTemp.put("images/drawmenu/markers/Emergency_Operations_Center_S1.png", R.drawable.emergency_operations_center_s1);
		incidentTemp.put("images/drawmenu/markers/Emergency_Public_Information_Center_S1_ch.png", R.drawable.emergency_public_information_center_s1_ch);
		incidentTemp.put("images/drawmenu/markers/Emergency_Shelters_S1.png", R.drawable.emergency_shelters_s1);
		incidentTemp.put("images/drawmenu/markers/Emergency_Staging_Areas_S1.png", R.drawable.emergency_staging_areas_s1);
		incidentTemp.put("images/drawmenu/markers/Emergency_Teams_S1.png", R.drawable.emergency_teams_s1);
		incidentTemp.put("images/drawmenu/markers/Emergency_Water_Distribution_Center_S1_ch.png", R.drawable.emergency_water_distribution_center_s1_ch);
		incidentTemp.put("images/drawmenu/markers/Emgergency_Food_Distribution_Centers_S1.png", R.drawable.emergency_food_distribution_centers_s1);
		incidentTemp.put("images/drawmenu/markers/Fire_Suppression_Theme_S1.png", R.drawable.fire_suppression_theme_s1);
		incidentTemp.put("images/drawmenu/markers/Fire_Hydrant_S1.png", R.drawable.fire_hydrant_s1);
		incidentTemp.put("images/drawmenu/markers/Fire_Other_Water_Supply_Location_S1.png", R.drawable.fire_other_water_supply_location_s1);
		incidentTemp.put("images/drawmenu/markers/Fire_Station_S1.png", R.drawable.fire_station_s1);
		incidentTemp.put("images/drawmenu/markers/Law_Enforcement_Theme_S1.png", R.drawable.law_enforcement_theme_s1);
		incidentTemp.put("images/drawmenu/markers/Law_ATF_S1.png", R.drawable.law_atf_s1);
		incidentTemp.put("images/drawmenu/markers/Law_Border_Patrol_S1.png", R.drawable.law_border_patrol_s1);
		incidentTemp.put("images/drawmenu/markers/Law_Customs_Service_S1.png", R.drawable.law_customs_srvice_s1);
		incidentTemp.put("images/drawmenu/markers/Law_DEA_S1.png", R.drawable.law_dea_s1);
		incidentTemp.put("images/drawmenu/markers/Law_DOJ_S1.png", R.drawable.law_doj_s1);
		incidentTemp.put("images/drawmenu/markers/Law_FBI_S1.png", R.drawable.law_fbi_s1);
		incidentTemp.put("images/drawmenu/markers/Law_Police_S1.png", R.drawable.law_police_s1);
		incidentTemp.put("images/drawmenu/markers/Law_Prison_S1.png", R.drawable.law_prison_s1);
		incidentTemp.put("images/drawmenu/markers/Law_Secret_Service_S1.png", R.drawable.law_secret_service_s1);
		incidentTemp.put("images/drawmenu/markers/Law_TSA_S1.png", R.drawable.law_tsa_s1);
		incidentTemp.put("images/drawmenu/markers/Law_US_Coast_Guard_S1.png", R.drawable.law_us_coast_guard_s1);
		incidentTemp.put("images/drawmenu/markers/Law_US_Marshall_S1.png", R.drawable.law_us_marshall_s1);

        INCIDENT = incidentTemp;
        
        
        // NATIONAL GUARD SYMBOLS
		TreeBidiMap<String, Integer> ngTemp = new TreeBidiMap<String, Integer>();
		
		ngTemp.put("images/drawmenu/markers/ng/airdefenseartillery.png",  R.drawable.airdefenseartillery);
		ngTemp.put("images/drawmenu/markers/ng/armor.png",  R.drawable.armor);
		ngTemp.put("images/drawmenu/markers/ng/aviation.png",  R.drawable.aviation);
		ngTemp.put("images/drawmenu/markers/ng/calvary.png",  R.drawable.calvary);
		ngTemp.put("images/drawmenu/markers/ng/chemical.png",  R.drawable.chemical);
		ngTemp.put("images/drawmenu/markers/ng/cs-aviation.png",  R.drawable.cs_aviation);
		ngTemp.put("images/drawmenu/markers/ng/engineer.png",  R.drawable.engineer);
		ngTemp.put("images/drawmenu/markers/ng/fieldartillery.png",  R.drawable.fieldartillery);
		ngTemp.put("images/drawmenu/markers/ng/infantry.png",  R.drawable.infantry);
		ngTemp.put("images/drawmenu/markers/ng/militaryintelligence.png",  R.drawable.militaryintelligence);
		ngTemp.put("images/drawmenu/markers/ng/militarypolice.png",  R.drawable.militarypolice);
		ngTemp.put("images/drawmenu/markers/ng/signal.png",  R.drawable.signal);
		ngTemp.put("images/drawmenu/markers/ng/specialforces.png",  R.drawable.specialforces);
		
        NG = ngTemp;
        
        TreeBidiMap<String, Integer> weatherTemp = new TreeBidiMap<String, Integer>();
        weatherTemp.put("bkn.png",  R.drawable.bkn);
        weatherTemp.put("few.png",  R.drawable.few);
        weatherTemp.put("fg.png", R.drawable.fg);
        weatherTemp.put("hi_ntsra.png", R.drawable.hi_ntsra);
        weatherTemp.put("hi_tsra.png", R.drawable.hi_tsra);
        weatherTemp.put("nbknfg.png", R.drawable.nbknfg);
        weatherTemp.put("nfew.png", R.drawable.nfew);
        weatherTemp.put("nfg.png", R.drawable.nfg);
        weatherTemp.put("novc.png", R.drawable.novc);
        weatherTemp.put("nrasn.png", R.drawable.nrasn);
        weatherTemp.put("nsct.png", R.drawable.nsct);
        weatherTemp.put("nscttsra.png", R.drawable.nscttsra);
        weatherTemp.put("nshra.png", R.drawable.nshra);
        weatherTemp.put("nsn.png", R.drawable.nsn);
        weatherTemp.put("ntsra.png", R.drawable.ntsra);
        weatherTemp.put("ovc.png", R.drawable.ovc);
        weatherTemp.put("ra.png", R.drawable.ra);
        weatherTemp.put("rasn.png", R.drawable.rasn);
        weatherTemp.put("sct.png", R.drawable.sct);
        weatherTemp.put("sctfg.png", R.drawable.sctfg);
        weatherTemp.put("sctshra.png", R.drawable.sctshra);
        weatherTemp.put("scttsra.png", R.drawable.scttsra);
        weatherTemp.put("shra.png", R.drawable.shra);
        weatherTemp.put("skc.png", R.drawable.skc);
        weatherTemp.put("sn.png", R.drawable.sn);
        weatherTemp.put("tsra.png", R.drawable.tsra);
        
        WEATHER = weatherTemp;

		TreeBidiMap<String, Integer> allTemp = new TreeBidiMap<String, Integer>();
		allTemp.putAll(commonTemp);
		allTemp.putAll(incidentTemp);
		allTemp.putAll(ngTemp);
		ALL = allTemp;
	}
}

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
package scout.edu.mit.ll.nics.android.api.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class FieldReportFormData {

	private String user;
	private String userfull;
	private String status;
	
	// Incident Identification
	@SerializedName("incident_id")
	private String incidentId;
	
	@SerializedName("incident_name")
	private String incidentName;
	
	@SerializedName("pollution_case_#")
	private String incidentPollutionCaseNumber;
	
	@SerializedName("source_of_incident")
	private String incidentSource;
	
	@SerializedName("location_of_incident")
	private String incidentLocation;
	
	@SerializedName("date_of_incident")
	private String incidentDate;
	
	@SerializedName("investigators")
	private String incidentInvestigators;
	
	@SerializedName("activity_#")
	private String incidentActivityNumber;
	
	@SerializedName("enforcement_#")
	private String incidentEnforcementNumber;
	
	@SerializedName("ticket_#")
	private String incidentTicketNumber;
	
	@SerializedName("federal_project_#")
	private String incidentFederalProjectNumber;
	

	// Discharge Data
	@SerializedName("material_spilled")
    private String dischargeMaterialSpilled;
	
	@SerializedName("total_spilled_(est)")
    private String dischargeTotalSpilled;
    
	@SerializedName("total_potential")
    private String dischargeTotalPotential;
    
	@SerializedName("total_recovered")
    private String dischargeTotalRecovered;
    
	@SerializedName("source")
    private String dischargeSource;
    
	@SerializedName("water_body")
    private String dischargeWaterBody;
    
	@SerializedName("description_of_spill")
    private String dischargeSpillDescription;
    
	@SerializedName("description_of_cause")
    private String dischargeCauseDescription;
    
	@SerializedName("path_of_discharge")
    private String dischargePath;
    
	
    // Weather and Tides
	@SerializedName("wind_direction")
    private String weatherWindDirection;
	
	@SerializedName("sea_height")
    private String weatherSeaHeight;
    
	@SerializedName("current_direction")
    private String weatherCurrentDirection;
    
	@SerializedName("visibility")
    private String weatherVisibility;
    
	@SerializedName("sunset")
    private String weatherSunset;
    
	@SerializedName("air_temp")
    private String weatherAirTemperature;
    
	@SerializedName("wind_speed")
    private String weatherWindSpeed;
    
	@SerializedName("current_speed")
    private String weatherCurrentSpeed;
    
	@SerializedName("precipitation")
    private String weatherPrecipitation;
    
	@SerializedName("sunrise")
    private String weatherSunrise;
    
	@SerializedName("water_temp")
    private String weatherWaterTemp;

	
    // Suspect Responsible Party and Witnesses
	@SerializedName("individual_/_company")
    private String suspectCompanyName;
	
	@SerializedName("individual_/_company_address")
    private String suspectAddress;
	
	@SerializedName("individual_/_company_phone")
    private String suspectPhone;
	
	@SerializedName("relationship")
    private String suspectRelationship;
	
	@SerializedName("license_or_doc_#")
    private String suspectLicenceOrDocumentNumber;
	
	@SerializedName("witness_1_name")
    private String suspectWitness1Name;
	
	@SerializedName("witness_1_address")
    private String suspectWitness1Address;
	
	@SerializedName("witness_1_phone")
    private String suspectWitness1Phone;
	
	@SerializedName("witness_2_name")
    private String suspectWitness2Name;
	
	@SerializedName("witness_2_address")
    private String suspectWitness2Address;
	
	@SerializedName("witness_2_phone")
    private String suspectWitness2Phone;
	
	
    // Oil Samples
	@SerializedName("samples_taken")
    private String oilSamplesTaken;
	
	@SerializedName("number")
    private String oilNumberOfSamples;
	
	@SerializedName("suspected_source")
    private String oilSuspectedSource;
	
	@SerializedName("identification")
    private String oilSampleIdentification;
	
	@SerializedName("date")
    private String oilSampleDate;
	
	@SerializedName("time")
    private String oilSampleTime;
	
	@SerializedName("witness")
    private String oilWitnesses;
	
	@SerializedName("sample_source")
    private String oilSampleSource;
	
	@SerializedName("clean_sample")
    private String oilCleanSample;
	
	@SerializedName("suspect_source_sample_1")
    private String oilSuspectSourceSample1;
	
	@SerializedName("suspect_source_sample_2")
    private String oilSuspectSourceSample2;
	
	@SerializedName("suspect_source_sample_3")
    private String oilSuspectSourceSample3;
	
	@SerializedName("suspect_source_sample_4")
    private String oilSuspectSourceSample4;
	
	
    // Vessel
	@SerializedName("vessel_name")
    private String vesselName;
	
	@SerializedName("call_sign")
    private String vesselCallSign;
	
	@SerializedName("flag")
    private String vesselFlag;
	
	@SerializedName("owner_/_operator")
    private String vesselOwnerOperator;
	
	@SerializedName("operator_phone")
    private String vesselOperatorPhone;
	
	@SerializedName("gross_tonnage")
    private String vesselGrossTonnage;
	
	@SerializedName("type_of_vessel")
    private String vesselType;
	
	@SerializedName("designation")
    private String vesselDesignation;
	
	@SerializedName("address")
    private String vesselAddress;
	
	@SerializedName("agent")
    private String vesselAgent;
	
	@SerializedName("agent_phone")
    private String vesselAgentPhone;
	
	@SerializedName("COFR")
    private String vesselCOFR;
	
	@SerializedName("SOPEP")
    private String vesselSOPEP;
	
	@SerializedName("oil_trans_procedures")
    private String vesselOilTransportationProcedures;
	
	@SerializedName("IOPP")
    private String vesselIOPP;
	
	@SerializedName("oil_record_book")
    private String vesselOilRecordBook;
	
	@SerializedName("declaration_of_inspection")
    private String vesselDeclarationOfInspection;
	
	@SerializedName("person_in_charge")
    private String vesselPersonInCharge;
	
	@SerializedName("master")
    private String vesselMaster;
	
	@SerializedName("chief_engineer")
    private String vesselChiefEngineer;
	
	@SerializedName("keel_laid_date")
    private String vesselKeelDate;


    // Facility and Other Parties
	@SerializedName("facility")
    private String facilityName;
	
	@SerializedName("type")
    private String facilityType;
	
	@SerializedName("facility_address")
    private String facilityAddress;
	
	@SerializedName("facility_phone")
    private String facilityPhone;
	
	@SerializedName("owner_/_op")
    private String facilityOwnerOperator;
	
	@SerializedName("operations_manager")
    private String facilityOperationsManager;
	
	@SerializedName("other_party")
    private String facilityOtherParty;
	
	@SerializedName("other_vessel")
    private String facilityOtherVessel;
	
	@SerializedName("other_facility")
    private String facilityOtherFacility;
	
	@SerializedName("other_facility_address")
    private String facilityOtherFacilityAddress;
	
	@SerializedName("other_facility_phone")
    private String facilityOtherFacilityPhone;
    
	
    // Other Factors
	@SerializedName("aggravating_(-)")
    private String factorsAggravating;
	
	@SerializedName("mitigating_(+)")
    private String factorsMitigating;
    
    
    // Notes
	@SerializedName("notes")
    private String notes;
		
	public FieldReportFormData(FieldReportData messageData) {
		user = messageData.getUser();
		userfull = messageData.getUserFull();
		status = messageData.getStatus();
		
		// Incident Identification
		incidentId = messageData.getIncidentId();
		incidentName = messageData.getIncidentName();
		incidentPollutionCaseNumber = messageData.getIncidentPollutionCaseNumber();
		incidentSource = messageData.getIncidentSource();
		incidentLocation = messageData.getIncidentLocation();
		incidentDate = messageData.getIncidentDate();
		incidentInvestigators = messageData.getIncidentInvestigators();
		incidentActivityNumber = messageData.getIncidentActivityNumber();
		incidentEnforcementNumber = messageData.getIncidentEnforcementNumber();
		incidentTicketNumber = messageData.getIncidentTicketNumber();
		incidentFederalProjectNumber = messageData.getIncidentFederalProjectNumber();
		
		// Discharge Data
	    dischargeMaterialSpilled = messageData.getDischargeMaterialSpilled();
	    dischargeTotalSpilled = messageData.getDischargeTotalSpilled();
	    dischargeTotalPotential = messageData.getDischargeTotalPotential();
	    dischargeTotalRecovered = messageData.getDischargeTotalRecovered();
	    dischargeSource = messageData.getDischargeSource();
	    dischargeWaterBody = messageData.getDischargeWaterBody();
	    dischargeSpillDescription = messageData.getDischargeSpillDescription();
	    dischargeCauseDescription = messageData.getDischargeCauseDescription();
	    dischargePath = messageData.getDischargePath();
	    
	    // Weather and Tides
	    weatherWindDirection = messageData.getWeatherWindDirection();
	    weatherSeaHeight = messageData.getWeatherSeaHeight();
	    weatherCurrentDirection = messageData.getWeatherCurrentDirection();
	    weatherVisibility = messageData.getWeatherVisibility();
	    weatherSunset = messageData.getWeatherSunset();
	    weatherAirTemperature = messageData.getWeatherAirTemperature();
	    weatherWindSpeed = messageData.getWeatherWindSpeed();
	    weatherCurrentSpeed = messageData.getWeatherCurrentSpeed();
	    weatherPrecipitation = messageData.getWeatherPrecipitation();
	    weatherSunrise = messageData.getWeatherSunrise();
	    weatherWaterTemp = messageData.getWeatherWaterTemp();
		
	    // Suspect Responsible Party and Witnesses
	    suspectCompanyName = messageData.getSuspectCompanyName();
	    suspectAddress = messageData.getSuspectAddress();
	    suspectPhone = messageData.getSuspectPhone();
	    suspectRelationship = messageData.getSuspectRelationship();
	    suspectLicenceOrDocumentNumber = messageData.getSuspectLicenceOrDocumentNumber();
	    suspectWitness1Name = messageData.getSuspectWitness1Name();
	    suspectWitness1Address = messageData.getSuspectWitness1Address();
	    suspectWitness1Phone = messageData.getSuspectWitness1Phone();
	    suspectWitness2Name = messageData.getSuspectWitness2Name();
	    suspectWitness2Address = messageData.getSuspectWitness2Address();
	    suspectWitness2Phone = messageData.getSuspectWitness2Phone();
		
	    // Oil Samples
	    oilSamplesTaken = messageData.getOilSamplesTaken();
	    oilNumberOfSamples = messageData.getOilNumberOfSamples();
	    oilSuspectedSource = messageData.getOilSuspectedSource();
	    oilSampleIdentification = messageData.getOilSampleIdentification();
	    oilSampleDate = messageData.getOilSampleDate();
		oilSampleTime = messageData.getOilSampleTime();
		oilWitnesses = messageData.getOilWitnesses();
		oilSampleSource = messageData.getOilSampleSource();
		oilCleanSample = messageData.getOilCleanSample();
		oilSuspectSourceSample1 = messageData.getOilSuspectSourceSample1();
		oilSuspectSourceSample2 = messageData.getOilSuspectSourceSample2();
		oilSuspectSourceSample3 = messageData.getOilSuspectSourceSample3();
		oilSuspectSourceSample4 = messageData.getOilSuspectSourceSample4();
		
	    // Vessel
	    vesselName = messageData.getVesselName();
		vesselCallSign = messageData.getVesselCallSign();
		vesselFlag = messageData.getVesselFlag();
		vesselOwnerOperator = messageData.getVesselOwnerOperator();
		vesselOperatorPhone = messageData.getVesselOperatorPhone();
		vesselGrossTonnage = messageData.getVesselGrossTonnage();
		vesselType = messageData.getVesselType();
		vesselDesignation = messageData.getVesselDesignation();
		vesselAddress = messageData.getVesselAddress();
		vesselAgent = messageData.getVesselAgent();
		vesselAgentPhone = messageData.getVesselAgentPhone();
		vesselCOFR = messageData.getVesselCOFR();
		vesselSOPEP = messageData.getVesselSOPEP();
		vesselOilTransportationProcedures = messageData.getVesselOilTransportationProcedures();
		vesselIOPP = messageData.getVesselIOPP();
		vesselOilRecordBook = messageData.getVesselOilRecordBook();
		vesselDeclarationOfInspection = messageData.getVesselDeclarationOfInspection();
		vesselPersonInCharge = messageData.getVesselPersonInCharge();
		vesselMaster = messageData.getVesselMaster();
		vesselChiefEngineer = messageData.getVesselChiefEngineer();
	    vesselKeelDate = messageData.getVesselKeelDate();
	    
	    // Facility and Other Parties
	    facilityName = messageData.getFacilityName();
	    facilityType = messageData.getFacilityType();
	    facilityAddress = messageData.getFacilityAddress();
	    facilityPhone = messageData.getFacilityPhone();
	    facilityOwnerOperator = messageData.getFacilityOwnerOperator();
	    facilityOperationsManager = messageData.getFacilityOperationsManager();
	    facilityOtherParty = messageData.getFacilityOtherParty();
	    facilityOtherVessel = messageData.getFacilityOtherVessel();
	    facilityOtherFacility = messageData.getFacilityOtherFacility();
	    facilityOtherFacilityAddress = messageData.getFacilityOtherFacilityAddress();
	    facilityOtherFacilityPhone = messageData.getFacilityOtherFacilityPhone();
	    
	    // Other Factors
	    factorsAggravating = messageData.getFactorsAggravating();
	    factorsMitigating = messageData.getFactorsMitigating();
	    
	    // Notes
	    notes = messageData.getNotes();
	}
	
	public String getUser() {
		return user;
	}
	
	public String getUserFull() {
		return userfull;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public String getStatus(){
		return status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public String getIncidentId() {
		return incidentId;
	}

	public void setIncidentId(String incidentId) {
		this.incidentId = incidentId;
	}

	public String getIncidentName() {
		return incidentName;
	}

	public void setIncidentName(String incidentName) {
		this.incidentName = incidentName;
	}

	public String getIncidentPollutionCaseNumber() {
		return incidentPollutionCaseNumber;
	}

	public void setIncidentPollutionCaseNumber(String incidentCaseNumber) {
		this.incidentPollutionCaseNumber = incidentCaseNumber;
	}

	public String getIncidentSource() {
		return incidentSource;
	}

	public void setIncidentSource(String incidentSource) {
		this.incidentSource = incidentSource;
	}

	public String getIncidentLocation() {
		return incidentLocation;
	}

	public void setIncidentLocation(String incidentLocation) {
		this.incidentLocation = incidentLocation;
	}

	public String getIncidentDate() {
		return incidentDate;
	}

	public void setIncidentDate(String incidentDate) {
		this.incidentDate = incidentDate;
	}

	public String getIncidentInvestigators() {
		return incidentInvestigators;
	}

	public void setIncidentInvestigators(String incidentInvestigators) {
		this.incidentInvestigators = incidentInvestigators;
	}

	public String getIncidentActivityNumber() {
		return incidentActivityNumber;
	}

	public void setIncidentActivityNumber(String incidentActivityNumber) {
		this.incidentActivityNumber = incidentActivityNumber;
	}

	public String getIncidentEnforcementNumber() {
		return incidentEnforcementNumber;
	}

	public void setIncidentEnforcementNumber(String incidentEnforcementNumber) {
		this.incidentEnforcementNumber = incidentEnforcementNumber;
	}

	public String getIncidentTicketNumber() {
		return incidentTicketNumber;
	}

	public void setIncidentTicketNumber(String incidentTicketNumber) {
		this.incidentTicketNumber = incidentTicketNumber;
	}

	public String getIncidentFederalProjectNumber() {
		return incidentFederalProjectNumber;
	}

	public void setIncidentFederalProjectNumber(String incidentFederalProjectNumber) {
		this.incidentFederalProjectNumber = incidentFederalProjectNumber;
	}

	public String getDischargeMaterialSpilled() {
		return dischargeMaterialSpilled;
	}

	public void setDischargeMaterialSpilled(String dischargeMaterialSpilled) {
		this.dischargeMaterialSpilled = dischargeMaterialSpilled;
	}

	public String getDischargeTotalSpilled() {
		return dischargeTotalSpilled;
	}

	public void setDischargeTotalSpilled(String dischargeTotalSpilled) {
		this.dischargeTotalSpilled = dischargeTotalSpilled;
	}

	public String getDischargeTotalPotential() {
		return dischargeTotalPotential;
	}

	public void setDischargeTotalPotential(String dischargeTotalPotential) {
		this.dischargeTotalPotential = dischargeTotalPotential;
	}

	public String getDischargeTotalRecovered() {
		return dischargeTotalRecovered;
	}

	public void setDischargeTotalRecovered(String dischargeTotalRecovered) {
		this.dischargeTotalRecovered = dischargeTotalRecovered;
	}

	public String getDischargeSource() {
		return dischargeSource;
	}

	public void setDischargeSource(String dischargeSource) {
		this.dischargeSource = dischargeSource;
	}

	public String getDischargeWaterBody() {
		return dischargeWaterBody;
	}

	public void setDischargeWaterBody(String dischargeWaterBody) {
		this.dischargeWaterBody = dischargeWaterBody;
	}

	public String getDischargeSpillDescription() {
		return dischargeSpillDescription;
	}

	public void setDischargeSpillDescription(String dischargeSpillDescription) {
		this.dischargeSpillDescription = dischargeSpillDescription;
	}

	public String getDischargeCauseDescription() {
		return dischargeCauseDescription;
	}

	public void setDischargeCauseDescription(String dischargeCauseDescription) {
		this.dischargeCauseDescription = dischargeCauseDescription;
	}

	public String getDischargePath() {
		return dischargePath;
	}

	public void setDischargePath(String dischargePath) {
		this.dischargePath = dischargePath;
	}

	public String getWeatherWindDirection() {
		return weatherWindDirection;
	}

	public void setWeatherWindDirection(String weatherWindDirection) {
		this.weatherWindDirection = weatherWindDirection;
	}

	public String getWeatherSeaHeight() {
		return weatherSeaHeight;
	}

	public void setWeatherSeaHeight(String weatherSeaHeight) {
		this.weatherSeaHeight = weatherSeaHeight;
	}

	public String getWeatherCurrentDirection() {
		return weatherCurrentDirection;
	}

	public void setWeatherCurrentDirection(String weatherCurrentDirection) {
		this.weatherCurrentDirection = weatherCurrentDirection;
	}

	public String getWeatherVisibility() {
		return weatherVisibility;
	}

	public void setWeatherVisibility(String weatherVisibility) {
		this.weatherVisibility = weatherVisibility;
	}

	public String getWeatherSunset() {
		return weatherSunset;
	}

	public void setWeatherSunset(String weatherSunset) {
		this.weatherSunset = weatherSunset;
	}

	public String getWeatherAirTemperature() {
		return weatherAirTemperature;
	}

	public void setWeatherAirTemperature(String weatherAirTemperature) {
		this.weatherAirTemperature = weatherAirTemperature;
	}

	public String getWeatherWindSpeed() {
		return weatherWindSpeed;
	}

	public void setWeatherWindSpeed(String weatherWindSpeed) {
		this.weatherWindSpeed = weatherWindSpeed;
	}

	public String getWeatherCurrentSpeed() {
		return weatherCurrentSpeed;
	}

	public void setWeatherCurrentSpeed(String weatherCurrentSpeed) {
		this.weatherCurrentSpeed = weatherCurrentSpeed;
	}

	public String getWeatherPrecipitation() {
		return weatherPrecipitation;
	}

	public void setWeatherPrecipitation(String weatherPrecipitation) {
		this.weatherPrecipitation = weatherPrecipitation;
	}

	public String getWeatherSunrise() {
		return weatherSunrise;
	}

	public void setWeatherSunrise(String weatherSunrise) {
		this.weatherSunrise = weatherSunrise;
	}

	public String getWeatherWaterTemp() {
		return weatherWaterTemp;
	}

	public void setWeatherWaterTemp(String weatherWaterTemp) {
		this.weatherWaterTemp = weatherWaterTemp;
	}

	public String getSuspectCompanyName() {
		return suspectCompanyName;
	}

	public void setSuspectCompanyName(String suspectCompanyName) {
		this.suspectCompanyName = suspectCompanyName;
	}

	public String getSuspectAddress() {
		return suspectAddress;
	}

	public void setSuspectAddress(String suspectAddress) {
		this.suspectAddress = suspectAddress;
	}

	public String getSuspectPhone() {
		return suspectPhone;
	}

	public void setSuspectPhone(String suspectPhone) {
		this.suspectPhone = suspectPhone;
	}

	public String getSuspectRelationship() {
		return suspectRelationship;
	}

	public void setSuspectRelationship(String suspectRelationship) {
		this.suspectRelationship = suspectRelationship;
	}

	public String getSuspectLicenceOrDocumentNumber() {
		return suspectLicenceOrDocumentNumber;
	}

	public void setSuspectLicenceOrDocumentNumber(String suspectLicenceOrDocumentNumber) {
		this.suspectLicenceOrDocumentNumber = suspectLicenceOrDocumentNumber;
	}

	public String getSuspectWitness1Name() {
		return suspectWitness1Name;
	}

	public void setSuspectWitness1Name(String suspectWitness1Name) {
		this.suspectWitness1Name = suspectWitness1Name;
	}

	public String getSuspectWitness1Address() {
		return suspectWitness1Address;
	}

	public void setSuspectWitness1Address(String suspectWitness1Address) {
		this.suspectWitness1Address = suspectWitness1Address;
	}

	public String getSuspectWitness1Phone() {
		return suspectWitness1Phone;
	}

	public void setSuspectWitness1Phone(String suspectWitness1Phone) {
		this.suspectWitness1Phone = suspectWitness1Phone;
	}

	public String getSuspectWitness2Name() {
		return suspectWitness2Name;
	}

	public void setSuspectWitness2Name(String suspectWitness2Name) {
		this.suspectWitness2Name = suspectWitness2Name;
	}

	public String getSuspectWitness2Address() {
		return suspectWitness2Address;
	}

	public void setSuspectWitness2Address(String suspectWitness2Address) {
		this.suspectWitness2Address = suspectWitness2Address;
	}

	public String getSuspectWitness2Phone() {
		return suspectWitness2Phone;
	}

	public void setSuspectWitness2Phone(String suspectWitness2Phone) {
		this.suspectWitness2Phone = suspectWitness2Phone;
	}

	public String getOilSamplesTaken() {
		return oilSamplesTaken;
	}

	public void setOilSamplesTaken(String oilSamplesTaken) {
		this.oilSamplesTaken = oilSamplesTaken;
	}

	public String getOilNumberOfSamples() {
		return oilNumberOfSamples;
	}

	public void setOilNumberOfSamples(String oilNumberOfSamples) {
		this.oilNumberOfSamples = oilNumberOfSamples;
	}

	public String getOilSuspectedSource() {
		return oilSuspectedSource;
	}

	public void setOilSuspectedSource(String oilSuspectedSource) {
		this.oilSuspectedSource = oilSuspectedSource;
	}

	public String getOilSampleIdentification() {
		return oilSampleIdentification;
	}

	public void setOilSampleIdentification(String oilSampleIdentification) {
		this.oilSampleIdentification = oilSampleIdentification;
	}

	public String getOilSampleDate() {
		return oilSampleDate;
	}

	public void setOilSampleDate(String oilSampleDate) {
		this.oilSampleDate = oilSampleDate;
	}

	public String getOilSampleTime() {
		return oilSampleTime;
	}

	public void setOilSampleTime(String oilSampleTime) {
		this.oilSampleTime = oilSampleTime;
	}

	public String getOilWitnesses() {
		return oilWitnesses;
	}

	public void setOilWitnesses(String oilWitnesses) {
		this.oilWitnesses = oilWitnesses;
	}

	public String getOilSampleSource() {
		return oilSampleSource;
	}

	public void setOilSampleSource(String oilSampleSource) {
		this.oilSampleSource = oilSampleSource;
	}

	public String getOilCleanSample() {
		return oilCleanSample;
	}

	public void setOilCleanSample(String oilCleanSample) {
		this.oilCleanSample = oilCleanSample;
	}

	public String getOilSuspectSourceSample1() {
		return oilSuspectSourceSample1;
	}

	public void setOilSuspectSourceSample1(String oilSuspectSourceSample1) {
		this.oilSuspectSourceSample1 = oilSuspectSourceSample1;
	}

	public String getOilSuspectSourceSample2() {
		return oilSuspectSourceSample2;
	}

	public void setOilSuspectSourceSample2(String oilSuspectSourceSample2) {
		this.oilSuspectSourceSample2 = oilSuspectSourceSample2;
	}

	public String getOilSuspectSourceSample3() {
		return oilSuspectSourceSample3;
	}

	public void setOilSuspectSourceSample3(String oilSuspectSourceSample3) {
		this.oilSuspectSourceSample3 = oilSuspectSourceSample3;
	}

	public String getOilSuspectSourceSample4() {
		return oilSuspectSourceSample4;
	}

	public void setOilSuspectSourceSample4(String oilSuspectSourceSample4) {
		this.oilSuspectSourceSample4 = oilSuspectSourceSample4;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getVesselCallSign() {
		return vesselCallSign;
	}

	public void setVesselCallSign(String vesselCallSign) {
		this.vesselCallSign = vesselCallSign;
	}

	public String getVesselFlag() {
		return vesselFlag;
	}

	public void setVesselFlag(String vesselFlag) {
		this.vesselFlag = vesselFlag;
	}

	public String getVesselOwnerOperator() {
		return vesselOwnerOperator;
	}

	public void setVesselOwnerOperator(String vesselOwnerOperator) {
		this.vesselOwnerOperator = vesselOwnerOperator;
	}

	public String getVesselOperatorPhone() {
		return vesselOperatorPhone;
	}

	public void setVesselOperatorPhone(String vesselOperatorPhone) {
		this.vesselOperatorPhone = vesselOperatorPhone;
	}

	public String getVesselGrossTonnage() {
		return vesselGrossTonnage;
	}

	public void setVesselGrossTonnage(String vesselGrossTonnage) {
		this.vesselGrossTonnage = vesselGrossTonnage;
	}

	public String getVesselType() {
		return vesselType;
	}

	public void setVesselType(String vesselType) {
		this.vesselType = vesselType;
	}

	public String getVesselDesignation() {
		return vesselDesignation;
	}

	public void setVesselDesignation(String vesselDesignation) {
		this.vesselDesignation = vesselDesignation;
	}

	public String getVesselAddress() {
		return vesselAddress;
	}

	public void setVesselAddress(String vesselAddress) {
		this.vesselAddress = vesselAddress;
	}

	public String getVesselAgent() {
		return vesselAgent;
	}

	public void setVesselAgent(String vesselAgent) {
		this.vesselAgent = vesselAgent;
	}

	public String getVesselAgentPhone() {
		return vesselAgentPhone;
	}

	public void setVesselAgentPhone(String vesselAgentPhone) {
		this.vesselAgentPhone = vesselAgentPhone;
	}

	public String getVesselCOFR() {
		return vesselCOFR;
	}

	public void setVesselCOFR(String vesselCOFR) {
		this.vesselCOFR = vesselCOFR;
	}

	public String getVesselSOPEP() {
		return vesselSOPEP;
	}

	public void setVesselSOPEP(String vesselSOPEP) {
		this.vesselSOPEP = vesselSOPEP;
	}

	public String getVesselOilTransportationProcedures() {
		return vesselOilTransportationProcedures;
	}

	public void setVesselOilTransportationProcedures(String vesselOilTransportationProcedures) {
		this.vesselOilTransportationProcedures = vesselOilTransportationProcedures;
	}

	public String getVesselIOPP() {
		return vesselIOPP;
	}

	public void setVesselIOPP(String vesselIOPP) {
		this.vesselIOPP = vesselIOPP;
	}

	public String getVesselOilRecordBook() {
		return vesselOilRecordBook;
	}

	public void setVesselOilRecordBook(String vesselOilRecordBook) {
		this.vesselOilRecordBook = vesselOilRecordBook;
	}

	public String getVesselDeclarationOfInspection() {
		return vesselDeclarationOfInspection;
	}

	public void setVesselDeclarationOfInspection(String vesselDeclarationOfInspection) {
		this.vesselDeclarationOfInspection = vesselDeclarationOfInspection;
	}

	public String getVesselPersonInCharge() {
		return vesselPersonInCharge;
	}

	public void setVesselPersonInCharge(String vesselPersonInCharge) {
		this.vesselPersonInCharge = vesselPersonInCharge;
	}

	public String getVesselMaster() {
		return vesselMaster;
	}

	public void setVesselMaster(String vesselMaster) {
		this.vesselMaster = vesselMaster;
	}

	public String getVesselChiefEngineer() {
		return vesselChiefEngineer;
	}

	public void setVesselChiefEngineer(String vesselChiefEngineer) {
		this.vesselChiefEngineer = vesselChiefEngineer;
	}

	public String getVesselKeelDate() {
		return vesselKeelDate;
	}

	public void setVesselKeelDate(String vesselKeelDate) {
		this.vesselKeelDate = vesselKeelDate;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getFacilityAddress() {
		return facilityAddress;
	}

	public void setFacilityAddress(String facilityAddress) {
		this.facilityAddress = facilityAddress;
	}

	public String getFacilityPhone() {
		return facilityPhone;
	}

	public void setFacilityPhone(String facilityPhone) {
		this.facilityPhone = facilityPhone;
	}

	public String getFacilityOwnerOperator() {
		return facilityOwnerOperator;
	}

	public void setFacilityOwnerOperator(String facilityOwnerOperator) {
		this.facilityOwnerOperator = facilityOwnerOperator;
	}

	public String getFacilityOperationsManager() {
		return facilityOperationsManager;
	}

	public void setFacilityOperationsManager(String facilityOperationsManager) {
		this.facilityOperationsManager = facilityOperationsManager;
	}

	public String getFacilityOtherParty() {
		return facilityOtherParty;
	}

	public void setFacilityOtherParty(String facilityOtherParty) {
		this.facilityOtherParty = facilityOtherParty;
	}

	public String getFacilityOtherVessel() {
		return facilityOtherVessel;
	}

	public void setFacilityOtherVessel(String facilityOtherVessel) {
		this.facilityOtherVessel = facilityOtherVessel;
	}

	public String getFacilityOtherFacility() {
		return facilityOtherFacility;
	}

	public void setFacilityOtherFacility(String facilityOtherFacility) {
		this.facilityOtherFacility = facilityOtherFacility;
	}

	public String getFacilityOtherFacilityAddress() {
		return facilityOtherFacilityAddress;
	}

	public void setFacilityOtherFacilityAddress(String facilityOtherFacilityAddress) {
		this.facilityOtherFacilityAddress = facilityOtherFacilityAddress;
	}

	public String getFacilityOtherFacilityPhone() {
		return facilityOtherFacilityPhone;
	}

	public void setFacilityOtherFacilityPhone(String facilityOtherFacilityPhone) {
		this.facilityOtherFacilityPhone = facilityOtherFacilityPhone;
	}

	public String getFactorsAggravating() {
		return factorsAggravating;
	}

	public void setFactorsAggravating(String factorsAggravating) {
		this.factorsAggravating = factorsAggravating;
	}

	public String getFactorsMitigating() {
		return factorsMitigating;
	}

	public void setFactorsMitigating(String factorsMitigating) {
		this.factorsMitigating = factorsMitigating;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String toJsonString() {
		return new Gson().toJson(this);
	}

}

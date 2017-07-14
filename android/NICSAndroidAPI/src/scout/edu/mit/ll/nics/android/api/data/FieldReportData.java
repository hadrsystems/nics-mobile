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

public class FieldReportData {
	
	private String user;
	private String userfull;
	private String status;
	private String transactionId;
	
	// Incident Identification
	@SerializedName("fr-A-id")
	private String incidentId;
	
	@SerializedName("fr-A-name")
	private String incidentName;

	@SerializedName("fr-A-casenum")
	private String incidentPollutionCaseNumber;
	
	@SerializedName("fr-A-source")
	private String incidentSource;
	
	@SerializedName("fr-A-location")
	private String incidentLocation;
	
	@SerializedName("fr-A-date")
	private String incidentDate;
	
	@SerializedName("fr-A-invest")
	private String incidentInvestigators;
	
	@SerializedName("fr-A-activitynum")
	private String incidentActivityNumber;

	@SerializedName("fr-A-enforcenum")
	private String incidentEnforcementNumber;

	@SerializedName("fr-A-ticketnum")
	private String incidentTicketNumber;

	@SerializedName("fr-A-fedprojnum")
	private String incidentFederalProjectNumber;

	
	// Discharge Data
    @SerializedName("fr-B-matspilled")
    private String dischargeMaterialSpilled;
    
    @SerializedName("fr-B-totalspilled")
    private String dischargeTotalSpilled;
    
    @SerializedName("fr-B-totalpot")
    private String dischargeTotalPotential;
    
    @SerializedName("fr-B-totalrecover")
    private String dischargeTotalRecovered;
    
    @SerializedName("fr-B-source")
    private String dischargeSource;
    
    @SerializedName("fr-B-waterbody")
    private String dischargeWaterBody;
    
    @SerializedName("fr-B-descriptionspill")
    private String dischargeSpillDescription;
    
    @SerializedName("fr-B-descriptioncause")
    private String dischargeCauseDescription;
    
    @SerializedName("fr-B-pathdischarge")
    private String dischargePath;
    
    
    // Weather and Tides
    @SerializedName("fr-C-winddir")
    private String weatherWindDirection;
    
    @SerializedName("fr-C-seaheight")
    private String weatherSeaHeight;
    
    @SerializedName("fr-C-currentdir")
    private String weatherCurrentDirection;
    
    @SerializedName("fr-C-visibility")
    private String weatherVisibility;
    
    @SerializedName("fr-C-sunset")
    private String weatherSunset;
    
    @SerializedName("fr-C-airtemp")
    private String weatherAirTemperature;
    
    @SerializedName("fr-C-windspeed")
    private String weatherWindSpeed;
    
    @SerializedName("fr-C-currentspeed")
    private String weatherCurrentSpeed;
    
    @SerializedName("fr-C-precipitation")
    private String weatherPrecipitation;
    
    @SerializedName("fr-C-sunrise")
    private String weatherSunrise;
    
    @SerializedName("fr-C-watertemp")
    private String weatherWaterTemp;
    
    
    // Suspect Responsible Party and Witnesses
    @SerializedName("fr-D-indcompany")
    private String suspectCompanyName;
    
    @SerializedName("fr-D-address")
    private String suspectAddress;
    
    @SerializedName("fr-D-phone")
    private String suspectPhone;
    
    @SerializedName("fr-D-relationship")
    private String suspectRelationship;
    
    @SerializedName("fr-D-licensedoc")
    private String suspectLicenceOrDocumentNumber;
    
    @SerializedName("fr-D-witness1name")
    private String suspectWitness1Name;
    
    @SerializedName("fr-D-witness1add")
    private String suspectWitness1Address;
    
    @SerializedName("fr-D-witness1phone")
    private String suspectWitness1Phone;
    
    @SerializedName("fr-D-witness2name")
    private String suspectWitness2Name;
    
    @SerializedName("fr-D-witness2add")
    private String suspectWitness2Address;
    
    @SerializedName("fr-D-witness2phone")
    private String suspectWitness2Phone;
    
    
    // Oil Samples
    @SerializedName("fr-E-samplestaken")
    private String oilSamplesTaken;
    
    @SerializedName("fr-E-number")
    private String oilNumberOfSamples;
    
    @SerializedName("fr-E-syspectedsrc")
    private String oilSuspectedSource;
    
    @SerializedName("fr-E-id")
    private String oilSampleIdentification;
    
    @SerializedName("fr-E-date")
    private String oilSampleDate;
    
    @SerializedName("fr-E-time")
    private String oilSampleTime;
    
    @SerializedName("fr-E-witnesses")
    private String oilWitnesses;
    
    @SerializedName("fr-E-src")
    private String oilSampleSource;
    
    @SerializedName("fr-E-cleansample")
    private String oilCleanSample;
    
    @SerializedName("fr-E-samp1")
    private String oilSuspectSourceSample1;
    
    @SerializedName("fr-E-samp2")
    private String oilSuspectSourceSample2;
    
    @SerializedName("fr-E-samp3")
    private String oilSuspectSourceSample3;
    
    @SerializedName("fr-E-samp4")
    private String oilSuspectSourceSample4;
    
    
    // Vessel
    @SerializedName("fr-F-name")
    private String vesselName;
    
    @SerializedName("fr-F-callsign")
    private String vesselCallSign;
    
    @SerializedName("fr-F-flag")
    private String vesselFlag;
    
    @SerializedName("fr-F-ownerop")
    private String vesselOwnerOperator;
    
    @SerializedName("fr-F-phone")
    private String vesselOperatorPhone;
    
    @SerializedName("fr-F-grosston")
    private String vesselGrossTonnage;
    
    @SerializedName("fr-F-vessel")
    private String vesselType;
    
    @SerializedName("fr-F-designation")
    private String vesselDesignation;
    
    @SerializedName("fr-F-address")
    private String vesselAddress;
    
    @SerializedName("fr-F-agent")
    private String vesselAgent;
    
    @SerializedName("fr-F-agent-phone")
    private String vesselAgentPhone;
    
    @SerializedName("fr-F-cofr")
    private String vesselCOFR;
    
    @SerializedName("fr-F-sopep")
    private String vesselSOPEP;
    
    @SerializedName("fr-F-oiltrans")
    private String vesselOilTransportationProcedures;
    
    @SerializedName("fr-F-iopp")
    private String vesselIOPP;
    
    @SerializedName("fr-F-recordbook")
    private String vesselOilRecordBook;
    
    @SerializedName("fr-F-inspection")
    private String vesselDeclarationOfInspection;
    
    @SerializedName("fr-F-personincharge")
    private String vesselPersonInCharge;
    
    @SerializedName("fr-F-master")
    private String vesselMaster;
    
    @SerializedName("fr-F-chiefeng")
    private String vesselChiefEngineer;
    
    @SerializedName("fr-F-keeldate")
    private String vesselKeelDate;
    
    
    // Facility and Other Parties
    @SerializedName("fr-G-facility")
    private String facilityName;
    
    @SerializedName("fr-G-type")
    private String facilityType;
    
    @SerializedName("fr-G-address")
    private String facilityAddress;
    
    @SerializedName("fr-G-phone")
    private String facilityPhone;
    
    @SerializedName("fr-G-ownerop")
    private String facilityOwnerOperator;
    
    @SerializedName("fr-G-opsman")
    private String facilityOperationsManager;
    
    @SerializedName("fr-G-otherparty")
    private String facilityOtherParty;
    
    @SerializedName("fr-G-othervessel")
    private String facilityOtherVessel;
    
    @SerializedName("fr-G-otherfacility")
    private String facilityOtherFacility;
    
    @SerializedName("fr-G-otherfacadd")
    private String facilityOtherFacilityAddress;
    
    @SerializedName("fr-G-otherfacphone")
    private String facilityOtherFacilityPhone;
    
    
    // Other Factors
    @SerializedName("fr-H-negfac")
    private String factorsAggravating;
    
    @SerializedName("fr-H-posfac")
    private String factorsMitigating;
    
    
    // Notes
    @SerializedName("fr-I-notes")
    private String notes;

	public FieldReportData() {
	}
	
	public FieldReportData(FieldReportFormData messageData) {
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

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUserFull() {
		return userfull;
	}
	
	public void setUserFull(String userfull) {
		this.userfull = userfull;
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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public String toJsonString() {
		return new Gson().toJson(this);
	}
	
}

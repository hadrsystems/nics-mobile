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
package scout.edu.mit.ll.nics.android.api.database.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.data.FieldReportData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.FieldReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;

/**
 * @author Glenn L. Primmer
 *
 * This class contains all the items necessary for creating and accessing a chat table in the
 * nics database.
 */
public class FieldReportTable extends DatabaseTable <FieldReportPayload> {
    /**
     * Defines the columns and its SQLite data type.
     */
    protected static final Map<String, String> TABLE_COLUMNS_MAP =
        new HashMap<String, String> () {

			private static final long serialVersionUID = 4434894366573452731L;

			{
              // Header items
                put ("id",                                          "integer primary key autoincrement");
                put ("isDraft",                                     "integer");
                put ("isNew",                                     "integer");
//                put ("createdUTC",    								"integer");
//                put ("lastUpdatedUTC", 								"integer");
                put ("formId",                                      "integer");
//                put ("senderUserId",                                "integer");
                put ("userSessionId",                               "integer");
                put ("seqtime",                                     "integer");
                put ("seqnum",                                      "integer");
                put ("status",                                      "text");
                put ("sendStatus",                                      "integer");
              // User name
                put ("user",                                        "text");
              // Field Report - Part A 'Incident Identification'
                put ("incidentId",                                  "text");
                put ("incidentName",                                "text");
                put ("incidentPollutionCaseNumber",                 "text");
                put ("incidentSourceOfIncident",                    "text");
                put ("incidentLocationOfIncident",                  "text");
                put ("incidentDateOfIncident",                      "text");
                put ("incidentInvestigators",                       "text");
                put ("incidentActivityNumber",                     "text");
                put ("incidentEnforcementNumber",                   "text");
                put ("incidentTicketNumber",                        "text");
                put ("incidentFederalProjectNumber",                "text");
              // Field Report - Part B 'Discharge Data'
                put ("dischargeDataMaterialSpilled",                "text");
                put ("dischargeDataTotalSpilledEstimate",           "text");
                put ("dischargeDataTotalPotential",                 "text");
                put ("dischargeDataTotalRecovered",                 "text");
                put ("dischargeDataSource",                         "text");
                put ("dischargeDataWaterBody",                      "text");
                put ("dischargeDataDescriptionOfSpill",             "text");
                put ("dischargeDataDescriptionOfCause",             "text");
                put ("dischargeDataPathOfDischarge",                "text");
              // Field Report - Part C 'Weather and Tides'
                put ("weatherAndTidesWindDirection",                "text");
                put ("weatherAndTidesSeaHeight",                    "text");
                put ("weatherAndTidesCurrentDirection",             "text");
                put ("weatherAndTidesVisibility",                   "text");
                put ("weatherAndTidesSunset",                       "text");
                put ("weatherAndTidesAirTemperature",               "text");
                put ("weatherAndTidesWindSpeed",                    "text");
                put ("weatherAndTidesCurrentSpeed",                 "text");
                put ("weatherAndTidesPrecipitation",                "text");
                put ("weatherAndTidesSunrise",                      "text");
                put ("weatherAndTidesWaterTemp",                    "text");
              // Field Report - Part D 'Suspect Responsible Party and Witnesses'
                put ("suspectAndWitnessesIndividualOrCompany",      "text");
                put ("suspectAndWitnessesAddress",                  "text");
                put ("suspectAndWitnessesPhone",                    "text");
                put ("suspectAndWitnessesRelationship",             "text");
                put ("suspectAndWitnessesLicenseOrDocumentNumber",  "text");
                put ("suspectAndWitnessesFirstWitnessName",         "text");
                put ("suspectAndWitnessesFirstWitnessAddress",      "text");
                put ("suspectAndWitnessesFirstWitnessPhone",        "text");
                put ("suspectAndWitnessesSecondWitnessName",        "text");
                put ("suspectAndWitnessesSecondWitnessAddress",     "text");
                put ("suspectAndWitnessesSecondWitnessPhone",       "text");
              // Field Report - Part E 'Oil Samples'
                put ("oilSamplesSamplesTaken",                      "text");
                put ("oilSamplesNumber",                            "text");
                put ("oilSamplesSuspectedSource",                   "text");
                put ("oilSamplesIdentification",                    "text");
                put ("oilSamplesDate",                              "text");
                put ("oilSamplesTime",                              "text");
                put ("oilSamplesWitness",                           "text");
                put ("oilSamplesSource",                            "text");
                put ("oilSamplesCleanSample",                       "text");
                put ("oilSamplesSuspectedSourceSample1",            "text");
                put ("oilSamplesSuspectedSourceSample2",            "text");
                put ("oilSamplesSuspectedSourceSample3",            "text");
                put ("oilSamplesSuspectedSourceSample4",            "text");
              // Field Report - Part F 'Vessel'
                put ("vesselName",                                  "text");
                put ("vesselCallSign",                              "text");
                put ("vesselFlag",                                  "text");
                put ("vesselOwnerOrOperator",                       "text");
                put ("vesselPhone",                                 "text");
                put ("vesselGrossTonnage",                          "text");
                put ("vesselTypeOfVessel",                          "text");
                put ("vesselDestination",                           "text");
                put ("vesselAddress",                               "text");
                put ("vesselAgent",                                 "text");
                put ("vesselAgentPhone",                            "text");
                put ("vesselCofr",                                  "text");
                put ("vesselSopep",                                 "text");
                put ("vesselOilTransportationProcedures",           "text");
                put ("vesselIopp",                                  "text");
                put ("vesselOilRecordBook",                         "text");
                put ("vesselDeclarationOfInspection",               "text");
                put ("vesselPersonInCharge",                        "text");
                put ("vesselMaster",                                "text");
                put ("vesselChiefEngineer",                         "text");
                put ("vesselKeelLaidDate",                          "text");
              // Field Report - Part G 'Facility and Other Parties'
                put ("facilityAndOtherPartiesFacility",             "text");
                put ("facilityAndOtherPartiesType",                 "text");
                put ("facilityAndOtherPartiesAddress",              "text");
                put ("facilityAndOtherPartiesPhone",                "text");
                put ("facilityAndOtherPartiesOwnerOrOperator",      "text");
                put ("facilityAndOtherPartiesOperationsManual",     "text");
                put ("facilityAndOtherPartiesOtherParty",           "text");
                put ("facilityAndOtherPartiesOtherVessel",          "text");
                put ("facilityAndOtherPartiesOtherFacility",        "text");
                put ("facilityAndOtherPartiesOtherFacilityAddress", "text");
                put ("facilityAndOtherPartiesOtherFacilityPhone",   "text");
              // Field Report - Part H 'Other Factors'
                put ("factorsAggravating",                          "text");
                put ("factorsMitigating",                           "text");
              // Field Report - Part I 'Notes'
                put ("notes",                                       "text");
                put ("json", "text");
            }
        };

    /**
     * Constructor.
     *
     * @param tableName Name of the table.
     *
     * @param context Android context reference.
     */
    public FieldReportTable (final String           tableName,
                             final Context context) {
        super (tableName,
               context);
    }

    @Override
    public void createTable (SQLiteDatabase database) {
        createTable (TABLE_COLUMNS_MAP, database);
    }

    @Override
    public long addData (final FieldReportPayload data, SQLiteDatabase database) {
        long row = 0L;

        if (database != null) {
            ContentValues contentValues = new ContentValues ();
            contentValues.put ("isDraft",                                     data.isDraft());
            contentValues.put("isNew",										  data.isNew());
            // Header items
//            contentValues.put ("createdUTC",    							  data.getCreatedUTC());
//            contentValues.put ("lastUpdatedUTC", 							  data.getLastUpdatedUTC());
            contentValues.put ("formId",                                      data.getFormId());
//            contentValues.put ("senderUserId",                                data.getSenderUserId());
            contentValues.put ("userSessionId",                               data.getUserSessionId());
            contentValues.put ("seqtime",                                     data.getSeqTime());
            contentValues.put ("seqnum",                                      data.getSeqNum());
            contentValues.put ("sendStatus",                                  data.getSendStatus().getId());
            
            FieldReportData messageData = data.getMessageData();
            // User Name
            contentValues.put ("user",                                        messageData.getUser());
            contentValues.put ("status",                                      messageData.getStatus());
            // Field Report - Part A 'Incident Identification'
            contentValues.put ("incidentId",                                  messageData.getIncidentId());
            contentValues.put ("incidentName",                                messageData.getIncidentName());
            contentValues.put ("incidentPollutionCaseNumber",                 messageData.getIncidentPollutionCaseNumber());
            contentValues.put ("incidentSourceOfIncident",                    messageData.getIncidentSource());
            contentValues.put ("incidentLocationOfIncident",                  messageData.getIncidentLocation());
            contentValues.put ("incidentDateOfIncident",                      messageData.getIncidentDate());
            contentValues.put ("incidentInvestigators",                       messageData.getIncidentInvestigators());
            contentValues.put ("incidentActivityNumber",                      messageData.getIncidentActivityNumber());
            contentValues.put ("incidentEnforcementNumber",                   messageData.getIncidentEnforcementNumber());
            contentValues.put ("incidentTicketNumber",                        messageData.getIncidentTicketNumber());
            contentValues.put ("incidentFederalProjectNumber",                messageData.getIncidentFederalProjectNumber());
            // Field Report - Part B 'Discharge Data'
            contentValues.put ("dischargeDataMaterialSpilled",                messageData.getDischargeMaterialSpilled());
            contentValues.put ("dischargeDataTotalSpilledEstimate",           messageData.getDischargeTotalSpilled());
            contentValues.put ("dischargeDataTotalPotential",                 messageData.getDischargeTotalPotential());
            contentValues.put ("dischargeDataTotalRecovered",                 messageData.getDischargeTotalRecovered());
            contentValues.put ("dischargeDataSource",                         messageData.getDischargeSource());
            contentValues.put ("dischargeDataWaterBody",                      messageData.getDischargeWaterBody());
            contentValues.put ("dischargeDataDescriptionOfSpill",             messageData.getDischargeSpillDescription());
            contentValues.put ("dischargeDataDescriptionOfCause",             messageData.getDischargeCauseDescription());
            contentValues.put ("dischargeDataPathOfDischarge",                messageData.getDischargePath());
            // Field Report - Part C 'Weather and Tides'
            contentValues.put ("weatherAndTidesWindDirection",                messageData.getWeatherWindDirection());
            contentValues.put ("weatherAndTidesSeaHeight",                    messageData.getWeatherSeaHeight());
            contentValues.put ("weatherAndTidesCurrentDirection",             messageData.getWeatherCurrentDirection());
            contentValues.put ("weatherAndTidesVisibility",                   messageData.getWeatherVisibility());
            contentValues.put ("weatherAndTidesSunset",                       messageData.getWeatherSunset());
            contentValues.put ("weatherAndTidesAirTemperature",               messageData.getWeatherAirTemperature());
            contentValues.put ("weatherAndTidesWindSpeed",                    messageData.getWeatherWindSpeed());
            contentValues.put ("weatherAndTidesCurrentSpeed",                 messageData.getWeatherCurrentSpeed());
            contentValues.put ("weatherAndTidesPrecipitation",                messageData.getWeatherPrecipitation());
            contentValues.put ("weatherAndTidesSunrise",                      messageData.getWeatherSunrise());
            contentValues.put ("weatherAndTidesWaterTemp",                    messageData.getWeatherWaterTemp());
            // Field Report - Part D 'Suspect Responsible Party and Witnesses'
            contentValues.put ("suspectAndWitnessesIndividualOrCompany",      messageData.getSuspectCompanyName());
            contentValues.put ("suspectAndWitnessesAddress",                  messageData.getSuspectAddress());
            contentValues.put ("suspectAndWitnessesPhone",                    messageData.getSuspectPhone());
            contentValues.put ("suspectAndWitnessesRelationship",             messageData.getSuspectRelationship());
            contentValues.put ("suspectAndWitnessesLicenseOrDocumentNumber",  messageData.getSuspectLicenceOrDocumentNumber());
            contentValues.put ("suspectAndWitnessesFirstWitnessName",         messageData.getSuspectWitness1Name());
            contentValues.put ("suspectAndWitnessesFirstWitnessAddress",      messageData.getSuspectWitness1Address());
            contentValues.put ("suspectAndWitnessesFirstWitnessPhone",        messageData.getSuspectWitness1Phone());
            contentValues.put ("suspectAndWitnessesSecondWitnessName",        messageData.getSuspectWitness2Name());
            contentValues.put ("suspectAndWitnessesSecondWitnessAddress",     messageData.getSuspectWitness2Address());
            contentValues.put ("suspectAndWitnessesSecondWitnessPhone",       messageData.getSuspectWitness2Phone());
            // Field Report - Part E 'Oil Samples'
            contentValues.put ("oilSamplesSamplesTaken",                      messageData.getOilSamplesTaken());
            contentValues.put ("oilSamplesNumber",                            messageData.getOilNumberOfSamples());
            contentValues.put ("oilSamplesSuspectedSource",                   messageData.getOilSampleSource());
            contentValues.put ("oilSamplesIdentification",                    messageData.getOilSampleIdentification());
            contentValues.put ("oilSamplesDate",                              messageData.getOilSampleDate());
            contentValues.put ("oilSamplesTime",                              messageData.getOilSampleTime());
            contentValues.put ("oilSamplesWitness",                           messageData.getOilWitnesses());
            contentValues.put ("oilSamplesSource",                            messageData.getOilSampleSource());
            contentValues.put ("oilSamplesCleanSample",                       messageData.getOilCleanSample());
            contentValues.put ("oilSamplesSuspectedSourceSample1",            messageData.getOilSuspectSourceSample1());
            contentValues.put ("oilSamplesSuspectedSourceSample2",            messageData.getOilSuspectSourceSample2());
            contentValues.put ("oilSamplesSuspectedSourceSample3",            messageData.getOilSuspectSourceSample3());
            contentValues.put ("oilSamplesSuspectedSourceSample4",            messageData.getOilSuspectSourceSample4());
            // Field Report - Part F 'Vessel'
            contentValues.put ("vesselName",                                  messageData.getVesselName());
            contentValues.put ("vesselCallSign",                              messageData.getVesselCallSign());
            contentValues.put ("vesselFlag",                                  messageData.getVesselFlag());
            contentValues.put ("vesselOwnerOrOperator",                       messageData.getVesselOwnerOperator());
            contentValues.put ("vesselPhone",                                 messageData.getVesselOperatorPhone());
            contentValues.put ("vesselGrossTonnage",                          messageData.getVesselGrossTonnage());
            contentValues.put ("vesselTypeOfVessel",                          messageData.getVesselType());
            contentValues.put ("vesselDestination",                           messageData.getVesselDesignation());
            contentValues.put ("vesselAddress",                               messageData.getVesselAddress());
            contentValues.put ("vesselAgent",                                 messageData.getVesselAgent());
            contentValues.put ("vesselAgentPhone",                            messageData.getVesselAgentPhone ());
            contentValues.put ("vesselCofr",                                  messageData.getVesselCOFR());
            contentValues.put ("vesselSopep",                                 messageData.getVesselSOPEP());
            contentValues.put ("vesselOilTransportationProcedures",           messageData.getVesselOilTransportationProcedures());
            contentValues.put ("vesselIopp",                                  messageData.getVesselIOPP());
            contentValues.put ("vesselOilRecordBook",                         messageData.getVesselOilRecordBook());
            contentValues.put ("vesselDeclarationOfInspection",               messageData.getVesselDeclarationOfInspection());
            contentValues.put ("vesselPersonInCharge",                        messageData.getVesselPersonInCharge());
            contentValues.put ("vesselMaster",                                messageData.getVesselMaster());
            contentValues.put ("vesselChiefEngineer",                         messageData.getVesselChiefEngineer());
            contentValues.put ("vesselKeelLaidDate",                          messageData.getVesselKeelDate());
            // Field Report - Part G 'Facility and Other Parties'
            contentValues.put ("facilityAndOtherPartiesFacility",             messageData.getFacilityName());
            contentValues.put ("facilityAndOtherPartiesType",                 messageData.getFacilityType());
            contentValues.put ("facilityAndOtherPartiesAddress",              messageData.getFacilityAddress());
            contentValues.put ("facilityAndOtherPartiesPhone",                messageData.getFacilityPhone());
            contentValues.put ("facilityAndOtherPartiesOwnerOrOperator",      messageData.getFacilityOwnerOperator());
            contentValues.put ("facilityAndOtherPartiesOperationsManual",     messageData.getFacilityOperationsManager());
            contentValues.put ("facilityAndOtherPartiesOtherParty",           messageData.getFacilityOtherParty());
            contentValues.put ("facilityAndOtherPartiesOtherVessel",          messageData.getFacilityOtherVessel());
            contentValues.put ("facilityAndOtherPartiesOtherFacility",        messageData.getFacilityOtherFacility());
            contentValues.put ("facilityAndOtherPartiesOtherFacilityAddress", messageData.getFacilityOtherFacilityAddress());
            contentValues.put ("facilityAndOtherPartiesOtherFacilityPhone",   messageData.getFacilityOtherFacilityPhone());
            // Field Report - Part H 'Other Factors'
            contentValues.put ("factorsAggravating",                          messageData.getFactorsAggravating());
            contentValues.put ("factorsMitigating",                           messageData.getFactorsMitigating());
            // Field Report - Parg I 'Notes'
            contentValues.put ("notes",                                       messageData.getNotes());
              contentValues.put("json", data.toJsonString());
            try {
                row = database.insert (tableName, null, contentValues);
            } catch (Exception ex) {
                Log.w (Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to add data to table: \"" + tableName + "\"", ex);
            }
        } else {
            Log.w (Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to add data to table: \"" + tableName + "\"");
        }

        return row;
    }
    
    public ArrayList<FieldReportPayload> getAllDataReadyToSend (long collaborationRoomId, SQLiteDatabase database) {
        String orderBy = "seqtime DESC";
        String sqlSelection = "sendStatus==? AND incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.WAITING_TO_SEND.getId ()), String.valueOf(collaborationRoomId)};

        return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
    }

    public ArrayList<FieldReportPayload> getAllDataReadyToSend (String orderBy, SQLiteDatabase database) {
        String sqlSelection = "sendStatus==?";
        String[] sqlSelectionArguments = {String.valueOf (ReportSendStatus.WAITING_TO_SEND.getId ())};

        return getData (sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
    }
    
    public ArrayList<FieldReportPayload> getAllDataHasSent (long collaborationRoomId, SQLiteDatabase database) {
        String orderBy = "seqtime DESC";
        String sqlSelection = "sendStatus==? AND incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.SENT.getId ()), String.valueOf(collaborationRoomId)};

        return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
    }

    public ArrayList<FieldReportPayload> getAllDataHasSent (String orderBy, SQLiteDatabase database) {
        String sqlSelection = "sendStatus==?";
        String[] sqlSelectionArguments = {String.valueOf (ReportSendStatus.SENT.getId ())};

        return getData (sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
    }

    @Override
    protected ArrayList<FieldReportPayload> getData (String   sqlSelection,
                                                String[] sqlSelectionArguments,
                                                String   orderBy,
                                                String limit,
                                                SQLiteDatabase database) {
        ArrayList<FieldReportPayload> dataList = new ArrayList<FieldReportPayload> ();

        if (database != null) {
            try {
                Cursor cursor;

                if (sqlSelection == null) {
                    cursor = database.query(tableName,                                                                 // Table
                                            TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                            null,                                                                      // Selection
                                            null,                                                                      // Selection arguments
                                            null,                                                                      // Group by
                                            null,                                                                      // Having
                                            orderBy,
                                            limit);                                                                     // Order by
                } else {
                    cursor = database.query(tableName,                                                                 // Table
                                            TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                            sqlSelection,                                                              // Selection
                                            sqlSelectionArguments,                                                     // Selection arguments
                                            null,                                                                      // Group by
                                            null,                                                                      // Having
                                            orderBy,
                                            limit);                                                                  // Order by
                }

                if (cursor != null) {
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        FieldReportPayload dataItem = new Gson().fromJson(cursor.getString (cursor.getColumnIndex ("json")), FieldReportPayload.class);
                        dataItem.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        dataItem.setSendStatus(ReportSendStatus.lookUp(cursor.getInt(cursor.getColumnIndex("sendStatus"))));
                        dataItem.setDraft(cursor.getInt(cursor.getColumnIndex("isDraft")) > 0 ? true : false);
                        dataItem.setNew(cursor.getInt(cursor.getColumnIndex("isNew")) > 0 ? true : false);
                        dataItem.parse();
                        
                        dataList.add(dataItem);

                        cursor.moveToNext();
                    }

                    cursor.close();
                }
            } catch (Exception ex) {
                Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                       "Exception occurred while trying to get data from table: \"" + tableName + "\"",
                       ex);
            }
        } else {
            Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                   "Could not get database to get all data from table: \"" + tableName + "\"");
        }

        return dataList;
    }

    /**
     * Gets the last data that was received and stored into the database.
     *
     * @param database The database.
     *
     * @return The timestamp of the last message received or (-1L) if no messages were received for that chat room.
     */
    public long getLastDataTimestamp (SQLiteDatabase database) {
        long lastMessageTimestamp = -1L;

        if (database != null) {
            try {
                // Descending by timestamp so that the newest item is the first item returned.
                String   orderBy               = "seqtime DESC";

                Cursor cursor = database.query(tableName,                                                                   // Table
                                               TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                               null,                                                                        // Selection
                                               null,                                                                        // Selection arguments
                                               null,                                                                        // Group by
                                               null,                                                                        // Having
                                               orderBy);                                                                    // Order by

                if (cursor != null) {
                    cursor.moveToFirst();

                    // First record is our newest item (largest timestamp).
                    if (!cursor.isAfterLast ()) {
                        lastMessageTimestamp = cursor.getLong (cursor.getColumnIndex ("seqtime"));
                    }

                    cursor.close();
                }
            } catch (Exception ex) {
                Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                       "Exception occurred while trying to get data from table: \"" + tableName + "\"",
                       ex);
            }
        } else {
            Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                   "Could not get database to get all data from table: \"" + tableName + "\"");
        }

        return lastMessageTimestamp;
    }
    
	public long getLastDataForIncidentTimestamp(long incidentId, SQLiteDatabase database) {
		long lastMessageTimestamp = -1L;

		if (database != null) {
			try {
				// Descending by time-stamp so that the newest item is the first item returned.
				String orderBy = "seqtime DESC";
				String sqlSelection = "incidentId==?";
				String[] sqlSelectionArguments = { String.valueOf(incidentId) };

				Cursor cursor = database.query(tableName, TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), sqlSelection, sqlSelectionArguments, null, null, orderBy);

				if (cursor != null) {
					cursor.moveToFirst();

					// First record is our newest item (largest time-stamp).
					if (!cursor.isAfterLast()) {
						lastMessageTimestamp = cursor.getLong(cursor.getColumnIndex("seqtime"));
					}

					cursor.close();
				}
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}

		return lastMessageTimestamp;
	}
    
	public FieldReportPayload getLastDataForIncidentId(long incidentId, SQLiteDatabase database) {
		FieldReportPayload lastPayload = null;

		if (database != null) {
			try {
				// Descending by time-stamp so that the newest item is the first item returned.
				String orderBy = "seqtime DESC";
				String sqlSelection = "incidentId==?";
				String[] sqlSelectionArguments = { String.valueOf(incidentId) };

				Cursor cursor = database.query(tableName, TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), sqlSelection, sqlSelectionArguments, null, null, orderBy);

				if (cursor != null) {
					cursor.moveToFirst();

					// First record is our newest item (largest time-stamp).
					if (!cursor.isAfterLast()) {
						lastPayload = new Gson().fromJson(cursor.getString(cursor.getColumnIndex("json")), FieldReportPayload.class);
					}

					cursor.close();
				}
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}
		return lastPayload;
	}
	
	public ArrayList<FieldReportPayload> getDataForIncident(long collaborationRoomId, SQLiteDatabase database) {
        String   orderBy               = "seqtime DESC";
        String   sqlSelection          = "incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId)};

        return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
	}

	@Override
	public long addData(ArrayList<FieldReportPayload> data, SQLiteDatabase database) {
		return 0;
	}
}

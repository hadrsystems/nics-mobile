/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.phinics.android.api.database.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import edu.mit.ll.phinics.android.api.data.UxoReportData;
import edu.mit.ll.phinics.android.api.data.ReportStatus;
import edu.mit.ll.phinics.android.api.payload.forms.UxoReportPayload;
import edu.mit.ll.phinics.android.utils.Constants;

/**
 * @author Glenn L. Primmer
 *
 * This class contains all the items necessary for creating and accessing a chat table in the
 * PHINICS database.
 */
public class UxoReportTable extends DatabaseTable <UxoReportPayload> {
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
//                put ("createdUTC",    								"integer");
//                put ("lastUpdatedUTC", 								"integer");
                put ("formId",                                      "integer");
//                put ("senderUserId",                                "integer");
                put ("userSessionId",                               "integer");
                put ("seqtime",                                     "integer");
                put ("seqnum",                                      "integer");
                put ("status",                                      "integer");
              // User name
                put ("user",                                        "text");
                put ("incidentId",                                  "text");
                
                put ("ReportingUnit",                               "text");
                put ("ReportingLocation",                           "text");
                put ("ContactInfo",                 				"text");
                put ("UxoType",                    					"integer");
                put ("Size",                  						"text");
                put ("Shape",										"text");
                put ("Color", 										"text");
                put ("Condition",									"text");
                put ("CbrnContamination",							"text");
                put ("ResourceThreatened",							"text");
                put ("ImpactOnMission", 							"text");
                put ("ProtectiveMeasures", 							"text");
                put ("RecommendedPriority", 						"integer");
                put ("Latitude",									"text");
                put ("Longitude",									"text");
                put ("FullPath", 									"text");
                
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
    public UxoReportTable (final String           tableName,
                             final Context context) {
        super (tableName,
               context);
    }

    @Override
    public void createTable (SQLiteDatabase database) {
        createTable (TABLE_COLUMNS_MAP, database);
    }

    @Override
    public long addData (final UxoReportPayload data, SQLiteDatabase database) {
        long row = 0L;

        if (database != null) {
            ContentValues contentValues = new ContentValues();
            
            contentValues.put ("isDraft",                                     data.isDraft());
            // Header items
//            contentValues.put ("createdUTC",    							  data.getCreatedUTC());
//            contentValues.put ("lastUpdatedUTC", 							  data.getLastUpdatedUTC());
            contentValues.put ("formId",                                      data.getFormId());
//            contentValues.put ("senderUserId",                                data.getSenderUserId());
            contentValues.put ("userSessionId",                               data.getUserSessionId());
            contentValues.put ("seqtime",                                     data.getSeqTime());
            contentValues.put ("seqnum",                                      data.getSeqNum());
            contentValues.put ("status",                                      data.getStatus().getId());
            
            UxoReportData messageData = data.getMessageData();
            // User Name
            contentValues.put ("user",                                        messageData.getUser());
            contentValues.put ("incidentId",                                  data.getIncidentId());
            contentValues.put ("ReportingUnit",       						  messageData.getReportingUnit());
            contentValues.put ("ReportingLocation",                           messageData.getReportingLocation());
            contentValues.put ("ContactInfo",                 		  		  messageData.getContactInfo());
            contentValues.put ("UxoType",                    		  		  messageData.getUxoType().getId());
            contentValues.put ("Size",                  				  	  messageData.getSize());
            
            contentValues.put ("Shape",               			  			  messageData.getShape());
            contentValues.put ("Color",           					  		  messageData.getColor());
            contentValues.put ("Condition",                			  		  messageData.getCondition());
            contentValues.put ("CbrnContamination",                           messageData.getCbrnContamination());
            contentValues.put ("ResourceThreatened",                          messageData.getResourceThreatened());
            contentValues.put ("ImpactOnMission",               			  messageData.getImpactOnMission());
            contentValues.put ("ProtectiveMeasures",               			  messageData.getProtectiveMeasures());
            contentValues.put ("RecommendedPriority",               		  messageData.getRecommendedPriority().getId());
            contentValues.put ("Latitude",                            		 messageData.getLatitude());
            contentValues.put ("Longitude",                           		messageData.getLongitude());

            contentValues.put ("FullPath",               			  		  messageData.getFullPath());
            
            
            contentValues.put("json", data.toJsonString());
            try {
                row = database.insert (tableName, null, contentValues);
            } catch (Exception ex) {
                Log.w (Constants.PHINICS_DEBUG_ANDROID_TAG, "Exception occurred while trying to add data to table: \"" + tableName + "\"", ex);
            }
        } else {
            Log.w (Constants.PHINICS_DEBUG_ANDROID_TAG, "Could not get database to add data to table: \"" + tableName + "\"");
        }

        return row;
    }
    
    public ArrayList<UxoReportPayload> getAllDataReadyToSend (long collaborationRoomId, SQLiteDatabase database) {
        String orderBy = "seqtime DESC";
        String sqlSelection = "status==? AND incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf(ReportStatus.WAITING_TO_SEND.getId ()), String.valueOf(collaborationRoomId)};

        return getData(sqlSelection, sqlSelectionArguments, orderBy, database);
    }

    public ArrayList<UxoReportPayload> getAllDataReadyToSend (String orderBy,
                                                           SQLiteDatabase database) {
        String sqlSelection = "status==?";
        String[] sqlSelectionArguments = {String.valueOf (ReportStatus.WAITING_TO_SEND.getId ())};

        return getData (sqlSelection,
                        sqlSelectionArguments,
                        orderBy,
                        database);
    }

    @Override
    protected ArrayList<UxoReportPayload> getData (String   sqlSelection,
                                                String[] sqlSelectionArguments,
                                                String   orderBy,
                                                SQLiteDatabase database) {
        ArrayList<UxoReportPayload> dataList = new ArrayList<UxoReportPayload> ();

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
                                            orderBy);                                                                     // Order by
                } else {
                    cursor = database.query(tableName,                                                                 // Table
                                            TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                            sqlSelection,                                                              // Selection
                                            sqlSelectionArguments,                                                     // Selection arguments
                                            null,                                                                      // Group by
                                            null,                                                                      // Having
                                            orderBy);                                                                  // Order by
                }

                if (cursor != null) {
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        UxoReportPayload dataItem = new Gson().fromJson(cursor.getString (cursor.getColumnIndex ("json")), UxoReportPayload.class);
                        dataItem.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        dataItem.setStatus(ReportStatus.lookUp(cursor.getInt(cursor.getColumnIndex("status"))));
                        dataItem.setDraft(cursor.getInt(cursor.getColumnIndex("isDraft")) > 0 ? true : false);
                        dataItem.parse();
                        
                        dataList.add(dataItem);

                        cursor.moveToNext();
                    }

                    cursor.close();
                }
            } catch (Exception ex) {
                Log.w (Constants.PHINICS_DEBUG_ANDROID_TAG,
                       "Exception occurred while trying to get data from table: \"" + tableName + "\"",
                       ex);
            }
        } else {
            Log.w (Constants.PHINICS_DEBUG_ANDROID_TAG,
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
                Log.w (Constants.PHINICS_DEBUG_ANDROID_TAG,
                       "Exception occurred while trying to get data from table: \"" + tableName + "\"",
                       ex);
            }
        } else {
            Log.w (Constants.PHINICS_DEBUG_ANDROID_TAG,
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
				Log.w(Constants.PHINICS_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.PHINICS_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}

		return lastMessageTimestamp;
	}
    
	public ArrayList<UxoReportPayload> getDataForIncident(long collaborationRoomId, SQLiteDatabase database) {
        String   orderBy               = "seqtime DESC";
        String   sqlSelection          = "incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId)};

        return getData (sqlSelection, sqlSelectionArguments, orderBy, database);
	}

	@Override
	public long addData(ArrayList<UxoReportPayload> data, SQLiteDatabase database) {
		return 0;
	}
}

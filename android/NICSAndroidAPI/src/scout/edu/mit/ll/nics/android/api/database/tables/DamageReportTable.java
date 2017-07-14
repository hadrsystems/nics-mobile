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
//import net.sqlcipher.database.SQLiteCursor;
import android.util.Log;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.data.DamageReportData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;

/**
 * @author Glenn L. Primmer
 *
 * This class contains all the items necessary for creating and accessing a chat table in the
 * nics database.
 */
public class DamageReportTable extends DatabaseTable <DamageReportPayload> {
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
                put ("isNew",                                       "integer");
//                put ("createdUTC",    								"integer");
//                put ("lastUpdatedUTC", 								"integer");
                put ("formId",                                      "integer");
//                put ("senderUserId",                                "integer");
                put ("userSessionId",                               "integer");
                put ("seqtime",                                     "integer");
                put ("seqnum",                                      "integer");
                put ("status",										"text");
                put ("sendStatus",                                      "integer");
              // User name
                put ("user",                                        "text");
                put ("incidentId",                                  "text");
                
                put ("ownerLastName",                               "text");
                put ("ownerFirstName",                              "text");
                put ("ownerLandlinePhone",                 			"text");
                put ("ownerCellPhone",                    			"text");
                put ("ownerEmail",                  				"text");

                put ("propertyAddress",								"text");
                put ("propertyCity", 								"text");
                put ("propertyZipCode",								"text");
                put ("propertyLatitude",							"text");
                put ("propertyLongitude",							"text");
                
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
    public DamageReportTable (final String           tableName,
                             final Context context) {
        super (tableName,
               context);
    }

    @Override
    public void createTable (SQLiteDatabase database) {
        createTable (TABLE_COLUMNS_MAP, database);
    }

    @Override
    public long addData (final DamageReportPayload data, SQLiteDatabase database) {
        long row = 0L;

        if (database != null) {
            ContentValues contentValues = new ContentValues();
            
            contentValues.put ("isDraft",                                     data.isDraft());
            contentValues.put ("isNew",                                     data.isNew());
            // Header items
//            contentValues.put ("createdUTC",    							  data.getCreatedUTC());
//            contentValues.put ("lastUpdatedUTC", 							  data.getLastUpdatedUTC());
            contentValues.put ("formId",                                      data.getFormId());
//            contentValues.put ("senderUserId",                                data.getSenderUserId());
            contentValues.put ("userSessionId",                               data.getUserSessionId());
            contentValues.put ("seqtime",                                     data.getSeqTime());
            contentValues.put ("seqnum",                                      data.getSeqNum());
            contentValues.put ("sendStatus",                                      data.getSendStatus().getId());
            
            DamageReportData messageData = data.getMessageData();
            // User Name
            contentValues.put ("user",                                        messageData.getUser());
            contentValues.put ("status",                                      messageData.getStatus());
            contentValues.put ("incidentId",                                  data.getIncidentId());
            contentValues.put ("ownerLastName",       						  messageData.getOwnerLastName());
            contentValues.put ("ownerFirstName",                              messageData.getOwnerFirstName());
            contentValues.put ("ownerLandlinePhone",                 		  messageData.getOwnerLandlinePhone());
            contentValues.put ("ownerCellPhone",                    		  messageData.getOwnerCellPhone());
            contentValues.put ("ownerEmail",                  				  messageData.getOwnerEmail());
            
            contentValues.put ("propertyAddress",               			  messageData.getPropertyAddress());
            contentValues.put ("propertyCity",           					  messageData.getPropertyCity());
            contentValues.put ("propertyZipCode",                			  messageData.getPropertyZipCode());
            contentValues.put ("propertyLatitude",                            messageData.getPropertyLatitude());
            contentValues.put ("propertyLongitude",                           messageData.getPropertyLongitude());

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
    
    public ArrayList<DamageReportPayload> getAllDataReadyToSend (long collaborationRoomId, SQLiteDatabase database) {
        String orderBy = "seqtime DESC";
        String sqlSelection = "sendStatus==? AND incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.WAITING_TO_SEND.getId ()), String.valueOf(collaborationRoomId)};

        return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
    }

    public ArrayList<DamageReportPayload> getAllDataReadyToSend (String orderBy,
                                                           SQLiteDatabase database) {
        String sqlSelection = "sendStatus==?";
        String[] sqlSelectionArguments = {String.valueOf (ReportSendStatus.WAITING_TO_SEND.getId ())};

        return getData (sqlSelection,
                        sqlSelectionArguments,
                        orderBy,
                        NO_LIMIT,
                        database);
    }
    
    public ArrayList<DamageReportPayload> getAllDataHasSent (long collaborationRoomId, SQLiteDatabase database) {
        String orderBy = "seqtime DESC";
        String sqlSelection = "sendStatus==? AND incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.SENT.getId ()), String.valueOf(collaborationRoomId)};

        return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
    }

    public ArrayList<DamageReportPayload> getAllDataHasSent (String orderBy,
                                                           SQLiteDatabase database) {
        String sqlSelection = "sendStatus==?";
        String[] sqlSelectionArguments = {String.valueOf (ReportSendStatus.SENT.getId ())};

        return getData (sqlSelection,
                        sqlSelectionArguments,
                        orderBy,
                        NO_LIMIT,
                        database);
    }

    @Override
    protected ArrayList<DamageReportPayload> getData (String   sqlSelection,
                                                String[] sqlSelectionArguments,
                                                String   orderBy,
                                                String limit,
                                                SQLiteDatabase database) {
        ArrayList<DamageReportPayload> dataList = new ArrayList<DamageReportPayload> ();

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
                                            orderBy,                                                                   // Order by
                                            limit);																	
                } else {
                    cursor = database.query(tableName,                                                                 // Table
                                            TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                            sqlSelection,                                                              // Selection
                                            sqlSelectionArguments,                                                     // Selection arguments
                                            null,                                                                      // Group by
                                            null,                                                                      // Having
                                            orderBy,																   // Order by
                                            limit);                                                                  
                }

                if (cursor != null) {
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        DamageReportPayload dataItem = new Gson().fromJson(cursor.getString (cursor.getColumnIndex ("json")), DamageReportPayload.class);
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
    

	public DamageReportPayload getLastDataForIncidentId(long incidentId, SQLiteDatabase database) {
		DamageReportPayload lastPayload = null;

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
						lastPayload = new Gson().fromJson(cursor.getString(cursor.getColumnIndex("json")), DamageReportPayload.class);
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
	
	public ArrayList<DamageReportPayload> getDataForIncident(long collaborationRoomId, SQLiteDatabase database) {
        String   orderBy               = "seqtime DESC";
        String   sqlSelection          = "incidentId==?";
        String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId)};

        return getData (sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
	}

	@Override
	public long addData(ArrayList<DamageReportPayload> data, SQLiteDatabase database) {
		return 0;
	}
}

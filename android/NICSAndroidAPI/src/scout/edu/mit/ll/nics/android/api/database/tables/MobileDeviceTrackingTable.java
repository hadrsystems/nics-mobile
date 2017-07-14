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
import android.util.Log;


import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.payload.MobileDeviceTrackingPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;

public class MobileDeviceTrackingTable extends DatabaseTable <MobileDeviceTrackingPayload> {
    /**
     * Defines the columns and its SQLite data type.
     */
    protected static final Map<String, String> TABLE_COLUMNS_MAP = new HashMap<String, String> () {
		private static final long serialVersionUID = -2203462814528207398L;
		{
            put ("id",					"integer primary key autoincrement");
            put ("createdUTC",			"integer");
            put ("lastUpdatedUTC",		"integer");
            put ("userId",				"integer");
            put ("deviceId",			"text");
            put ("incidentId",			"integer");
            put ("provider",			"text");
            put ("latitude",			"real");
            put ("longitude",			"real");
            put ("altitude",			"real");
            put ("course",				"real");
            put ("speed",				"real");
            put ("accuracy",			"real");
            put ("sensorTimestamp",		"integer");
            put ("json",				"text");
        }
    };

    /**
     * Constructor.
     *
     * @param tableName Name of the table.
     *
     * @param context Android context reference.
     */
    public MobileDeviceTrackingTable (final String tableName, final Context context) {
        super (tableName, context);
    }

    @Override
    public void createTable (SQLiteDatabase database) {
        createTable (TABLE_COLUMNS_MAP, database);
    }

    @Override
    public long addData (final MobileDeviceTrackingPayload data, SQLiteDatabase database) {
        long row = 0L;

        if (database != null) {
            try {
                ContentValues contentValues = new ContentValues ();

                contentValues.put ("createdUTC",        data.getCreatedUTC());
                contentValues.put ("lastUpdatedUTC",    data.getLastUpdatedUTC());
                contentValues.put ("userId",            data.getUserId());
                contentValues.put ("deviceId",          data.getDeviceId());
                contentValues.put ("incidentId",        data.getIncidentId());
                contentValues.put ("provider",          data.getProvider());
                contentValues.put ("latitude",          data.getLatitude());
                contentValues.put ("longitude",         data.getLongitude());
                contentValues.put ("altitude",          data.getAltitude());
                contentValues.put ("course",            data.getCourse());
                contentValues.put ("speed",             data.getSpeed());
                contentValues.put ("accuracy",          data.getAccuracy());
                contentValues.put ("sensorTimestamp",   data.getSensorTimestamp());
                contentValues.put("json", data.toJsonString());

                row = database.insert (tableName, null, contentValues);
            } catch (Exception ex) {
                Log.w (Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to add data to table: \"" + tableName + "\"", ex);
            }
        } else {
            Log.w (Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to add data to table: \"" + tableName + "\"");
        }
        return row;
    }

    @Override
    protected ArrayList<MobileDeviceTrackingPayload> getData (String   sqlSelection,
                                                         String[] sqlSelectionArguments,
                                                         String   orderBy,
                                                         String limit,
                                                         SQLiteDatabase database) {
    	ArrayList<MobileDeviceTrackingPayload> dataList = new ArrayList<MobileDeviceTrackingPayload> ();

        if (database != null) {
            try {
                Cursor cursor;

                if (sqlSelection == null) {
                    cursor = database.query(tableName,                                                                   // Table
                                            TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                            null,                                                                        // Selection
                                            null,                                                                        // Selection arguments
                                            null,                                                                        // Group by
                                            null,                                                                        // Having
                                            orderBy,
                                            limit);                                                                       // Order by
                } else {
                    cursor = database.query(tableName,                                                                   // Table
                                            TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                            sqlSelection,                                                                // Selection
                                            sqlSelectionArguments,                                                       // Selection arguments
                                            null,                                                                        // Group by
                                            null,                                                                        // Having
                                            orderBy,
                                            limit);                                                                    // Order by
                }

                if (cursor != null) {

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        // Unfortunately, the use of having things simplified in the table constructor leaves us having
                        // to make 2 calls for every data element retrieved.  However, the code is easier to follow.
                        MobileDeviceTrackingPayload dataItem = new Gson().fromJson(cursor.getString (cursor.getColumnIndex ("json")), MobileDeviceTrackingPayload.class);
                        dataItem.setId(cursor.getLong(cursor.getColumnIndex("id")));
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

	@Override
	public long addData(ArrayList<MobileDeviceTrackingPayload> data, SQLiteDatabase database) {
		return 0;
	}
}

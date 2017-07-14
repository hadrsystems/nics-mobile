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
import java.util.LinkedHashMap;

import android.content.ContentValues;
import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteConstraintException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteStatement;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteConstraintException;
import net.sqlcipher.database.SQLiteStatement;
import android.util.Log;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.utils.Constants;

/**
 * @author Glenn L. Primmer
 *
 * This class contains all the items necessary for creating and accessing a chat table in the
 * nics database.
 */
public class MarkupTable extends DatabaseTable <MarkupFeature> {
	
	private ContentValues contentValues;
	private Gson mGson;
	private  ArrayList<MarkupFeature> dataList;
    /**
     * Defines the columns and its SQLite data type.
     */
    protected static final LinkedHashMap<String, String> TABLE_COLUMNS_MAP =
        new LinkedHashMap<String, String> () {
			private static final long serialVersionUID = 4824162161321014810L;
			{
                put ("id",           "integer primary key autoincrement");
                put ("collabroomid", "integer");
                put ("featureId",    "text unique");
                put ("seqtime",      "integer");
                put ("json",         "text");
            }
        };

    /**
     * Constructor.
     *
     * @param tableName Name of the table.
     *
     * @param context Android context reference.
     */
    public MarkupTable (final String tableName, final Context context) {
        super (tableName, context);
        
        mGson = new Gson();
    	contentValues = new ContentValues();
    	dataList = new ArrayList<MarkupFeature>();
    }

    @Override
    public void createTable (SQLiteDatabase database) {
        createTable (TABLE_COLUMNS_MAP, database);
    }

    @Override
    public long addData (final MarkupFeature data,
                         SQLiteDatabase database) {
        long row = 0L;

        if (database != null) {

            contentValues.put("collabroomid", 	 data.getCollabRoomId());
            contentValues.put("featureId",	     data.getFeatureId());
            contentValues.put("seqtime",   		 data.getSeqTime());
            contentValues.put("json",            data.toJsonString());

            try {
                row = database.replace (tableName,      // Table.
                                       null,           // Null column hack.
                                       contentValues); // Values.
            } catch (Exception ex) {
                Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                       "Exception occurred while trying to add data to table: \"" + tableName + "\"",
                       ex);
            }
            
            contentValues.clear();
        } else {
            Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                   "Could not get database to add data to table: \"" + tableName + "\"");
        }

        return row;
    }

    @Override
    public long addData (final ArrayList<MarkupFeature> dataSet,
                         SQLiteDatabase database) {
        long row = 0L;

        if (database != null) {
        	
        	String INSERT_QUERY = createInsert(tableName, TABLE_COLUMNS_MAP);
        	SQLiteStatement statement = database.compileStatement(INSERT_QUERY);
        	String json;

        	database.beginTransaction();
        	try {
				for(MarkupFeature data : dataSet) {
					try {
						statement.clearBindings();
						
						statement.bindLong(2, data.getCollabRoomId());
					
						if(data.getFeatureId() != null) {
							statement.bindString(3, data.getFeatureId());
						}
						
						statement.bindLong(4, data.getSeqTime());
						
						json = data.toJsonString();
						if(json != null) {
							statement.bindString(5, json);
						}
						
						statement.executeUpdateDelete();
					} catch(SQLiteConstraintException e) {
		        		Log.w(Constants.nics_DEBUG_ANDROID_TAG, e.getLocalizedMessage());
					}
				}
				database.setTransactionSuccessful();
			} finally {
        		database.endTransaction();
        	}
            
        } else {
            Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                   "Could not get database to add data to table: \"" + tableName + "\"");
        }

        return row;
    }
    /**
     * Gets the last chat data timestamp that was received for a provided collaboration room ID.
     *
     * @param collaborationRoomId The collaboration room ID.
     *
     * @param database The database.
     *
     * @return The timestamp of the last message recieved or (-1L) if no messages were received for that chat room.
     */
    public long getLastChatDataTimestamp (long collaborationRoomId,
                                          SQLiteDatabase database) {
        long lastMessageTimestamp = -1L;

        if (database != null) {
            try {
                // Decending by timestamp so that the newest item is the first item returned.
                String   orderBy               = "seqtime DESC";
                String   sqlSelection          = "collabroomid==?";
                String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId)};

                Cursor cursor = database.query(tableName,                                                                   // Table
                                               TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
                                               sqlSelection,                                                                // Selection
                                               sqlSelectionArguments,                                                       // Selection arguments
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

    @Override
    public ArrayList<MarkupFeature> getAllData (String orderBy,
                                      SQLiteDatabase database) {
        return getData (null,
                        null,
                        orderBy,
                        NO_LIMIT,
                        database);
    }

    @Override
    public ArrayList<MarkupFeature> getDataSince (long timestamp,
                                        SQLiteDatabase database) {
        String   orderBy               = "seqtime DESC";
        String   sqlSelection          = "seqtime==?";
        String[] sqlSelectionArguments = {String.valueOf (timestamp)};

        return getData (sqlSelection,
                        sqlSelectionArguments,
                        orderBy,
                        NO_LIMIT,
                        database);
    }

    /**
     * Gets all the data for a provided collaboration room ID.
     *
     * @param collaborationRoomId The collaboration room ID
     *
     * @param database The database.
     *
     * @return All the data for a provided collaboration room ID.
     */
    public ArrayList<MarkupFeature> getDataForCollaborationRoom (long collaborationRoomId,
                                                       SQLiteDatabase database) {
        String   orderBy               = "seqtime DESC";
        String   sqlSelection          = "collabroomid==?";
        String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId)};

        return getData (sqlSelection,
                        sqlSelectionArguments,
                        orderBy,
                        NO_LIMIT,
                        database);
    }

    /**
     * Gets all the data for a provided collaboration room ID that is newer than the provided timestamp.
     *
     * @param collaborationRoomId The collaboration room ID.
     *
     * @param timestamp The timestamp.
     *
     * @param database The database.
     *
     * @return All the data for a provided collaboration room ID that is newer than the provided timestamp.
     */
    public ArrayList<MarkupFeature> getDataForCollaborationRoomSince (long collaborationRoomId,
                                                            long timestamp,
                                                            SQLiteDatabase database) {
        String   orderBy               = "seqtime DESC";
        String   sqlSelection          = "collabroomid==? AND seqtime>? ";
        String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId), String.valueOf (timestamp)};

        return getData (sqlSelection,
                        sqlSelectionArguments,
                        orderBy,
                        NO_LIMIT,
                        database);
    }



    protected ArrayList<MarkupFeature> getData (String   sqlSelection,
                                      String[] sqlSelectionArguments,
                                      String   orderBy,
                                      String limit,
                                      SQLiteDatabase database) {
       

        if (database != null) {
            Cursor cursor = null;
            try {
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

                    dataList.clear();
                    while (!cursor.isAfterLast()) {
                        // Unfortunately, the use of having things simplified in the table constructor leaves us having
                        // to make 2 calls for every data element retrieved.  However, the code is easier to follow.
                        MarkupFeature dataItem = mGson.fromJson(cursor.getString(cursor.getColumnIndex ("json")), MarkupFeature.class); 
                        dataItem.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        dataItem.setFeatureId(cursor.getString(cursor.getColumnIndex("featureId")));
                        dataItem.setCollabRoomId(cursor.getLong(cursor.getColumnIndex("collabroomid")));
                        dataItem.setSeqTime(cursor.getLong(cursor.getColumnIndex("seqtime")));
                        
                        dataList.add(dataItem);
                        
                        dataItem = null;
                        
                        cursor.moveToNext();
                    }

                    cursor.close();
                }
            } catch (Exception ex) {
                Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                       "Exception occurred while trying to get data from table: \"" + tableName + "\"",
                       ex);
            } finally {
            	if(cursor != null && !cursor.isClosed()) {
            		cursor.close();
            	}
            }
        } else {
            Log.w (Constants.nics_DEBUG_ANDROID_TAG,
                   "Could not get database to get all data from table: \"" + tableName + "\"");
        }

        return dataList;
    }

	public long getLastDataForCollaborationRoomTimestamp(long collaborationRoomId, SQLiteDatabase database) {
	       long lastMessageTimestamp = -1L;
	       
	        if (database != null) {
	            try {
	                // Decending by timestamp so that the newest item is the first item returned.
	                String   orderBy               = "seqtime DESC";
	                String   sqlSelection          = "collabroomid==?";
	                String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId)};

	                Cursor cursor = database.query(tableName,                                                                   // Table
	                                               TABLE_COLUMNS_MAP.keySet ().toArray (new String[TABLE_COLUMNS_MAP.size ()]), // Columns
	                                               sqlSelection,                                                                // Selection
	                                               sqlSelectionArguments,                                                       // Selection arguments
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
		
	public long deleteDataByCollabroomByFeatureId(final long collabroomId, String featureId, SQLiteDatabase database) {
		long rows = 0L;

		if (database != null) {
			String whereClause = "collabroomid=? AND featureId=?";
			String[] whereArguments = { String.valueOf(collabroomId),  featureId};

			try {
				rows = database.delete(tableName, // Table name.
						whereClause, // SQL where clause.
						whereArguments); // SQL where clause arguments.
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to delete data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to delete data from table: \"" + tableName + "\"");
		}

		return rows;
	}
	
	// TODO: fix featureId=? to handle multiple... commas don't work.
	public long deleteDataByCollabroomByFeatureIds(final long collabroomId, ArrayList<String> featureIds, SQLiteDatabase database) {
		long rows = 0L;

		if (database != null) {
			String whereClause = "collabroomid=? AND featureId=?";
			String featureIdString = "";
			boolean first = true;
			for(String id : featureIds) {
				if(first) {
					featureIdString += id;
					first = false;
				} else {
					featureIdString += ", " + id;
				}
				
			}
			
			String[] whereArguments = { String.valueOf(collabroomId),  featureIdString};

			try {
				rows = database.delete(tableName, // Table name.
						whereClause, // SQL where clause.
						whereArguments); // SQL where clause arguments.
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to delete data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to delete data from table: \"" + tableName + "\"");
		}

		return rows;
	}
	
    public ArrayList<MarkupFeature> getDataForCollaborationRoomByFeatureIds (long collaborationRoomId, ArrayList<String> featureIds, SQLiteDatabase database) {
		String featureIdString = "";
		boolean first = true;
		for(String id : featureIds) {
			if(first) {
				featureIdString += id;
				first = false;
			} else {
				featureIdString += ", " + id;
			}
			
		}
		
		String   orderBy               = "seqtime DESC";
		String   sqlSelection          = "collabroomid==? AND featureId=? ";
		String[] sqlSelectionArguments = {String.valueOf (collaborationRoomId), featureIdString};
		
		return getData (sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
    }
 
}

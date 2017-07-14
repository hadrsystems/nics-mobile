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
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteStatement;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import android.util.Log;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;

public class ChatTable extends DatabaseTable<ChatPayload> {
	/**
	 * Defines the columns and its SQLite data type.
	 */
	private static final LinkedHashMap<String, String> TABLE_COLUMNS_MAP = new LinkedHashMap<String, String>() {
		private static final long serialVersionUID = -2638838079456275653L;

		{
			put("id", "integer primary key autoincrement");
			put("created", "integer");
			put("lastUpdated", "integer");
			put("chatid", "integer unique");
			put("senderUserId", "integer");
			put("message", "text");
			put("collabroomid", "integer");
			put("incidentId", "integer");
			put("seqTime", "integer");
			put("seqnum", "integer");
			put("topic", "text");
			put("nickname", "text");
			put("userOrgName", "text");
			put("userorgid", "integer");
			put("json", "text");
		}
	};

	/**
	 * Constructor.
	 * 
	 * @param tableName Name of the table.
	 * @param context Android context reference.
	 */
	public ChatTable(final String tableName, final Context context) {
		super(tableName, context);
	}

	@Override
	public void createTable(SQLiteDatabase database) {
		createTable(TABLE_COLUMNS_MAP, database);
	}

	@Override
	public long addData(final ChatPayload data, SQLiteDatabase database) {
		long row = 0L;

		if (database != null) {

			ContentValues contentValues = new ContentValues();

			contentValues.put("created", data.getcreated());
			contentValues.put("lastUpdated", data.getlastupdated());
			contentValues.put("chatid", data.getchatid());
			contentValues.put("senderUserId", data.getuserId());
			contentValues.put("message", data.getmessage());
			contentValues.put("collabroomid", data.getcollabroomid());
			contentValues.put("incidentId", data.getIncidentId());
			contentValues.put("seqTime", 0); //data.getSeqTime());
			contentValues.put("seqnum", data.getseqnum());
			contentValues.put("topic", data.getTopic());
			contentValues.put("nickname", data.getNickname());
			contentValues.put("userOrgName", data.getUserOrgName());
			contentValues.put("userorgid", data.getUserorgid());
			contentValues.put("json", data.toFullJsonString());

			try {
				row = database.replace(tableName, null, contentValues);
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to add data to table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to add data to table: \"" + tableName + "\"");
		}

		return row;
	}
	
    @Override
    public long addData (final ArrayList<ChatPayload> dataSet, SQLiteDatabase database) {
        long row = 0L;

        if (database != null) {
        	
        	String INSERT_QUERY = createInsert(tableName, TABLE_COLUMNS_MAP);
        	SQLiteStatement statement = database.compileStatement(INSERT_QUERY);
        	String json;

        	database.beginTransaction();
        	try {
				for(ChatPayload data : dataSet) {
					statement.clearBindings();
					
					statement.bindLong(2, data.getcreated());
					statement.bindLong(3, data.getlastupdated());
					statement.bindLong(4, data.getchatid());
					statement.bindLong(5, data.getuserId());
					
					if(data.getmessage() != null) {
						statement.bindString(6, data.getmessage());
					}
					
					statement.bindLong(7, data.getcollabroomid());
					statement.bindLong(8, data.getIncidentId());
					statement.bindLong(9, 0);//data.getSeqTime());
					statement.bindLong(10, data.getseqnum());
				
					if(data.getTopic() != null) {
						statement.bindString(11, data.getTopic());
					}
					if(data.getNickname() != null) {
						statement.bindString(12, data.getNickname());
					}
					if(data.getUserOrgName() != null) {
						statement.bindString(13, data.getUserOrgName());
					}
					
					json = data.toJsonString();
					if(json != null) {
						statement.bindString(14, json);
					}
					
					statement.executeUpdateDelete();
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
	
	public ChatPayload getLastDataForCollaborationRoom(long collaborationRoomId, SQLiteDatabase database) {
		ChatPayload lastMessage = null;

		if (database != null) {
			try {
				// Descending by time-stamp so that the newest item is the first item returned.
				String orderBy = "created DESC";
				String sqlSelection = "collabroomid==?";
				String[] sqlSelectionArguments = { String.valueOf(collaborationRoomId) };

				Cursor cursor = database.query(tableName, TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), sqlSelection, sqlSelectionArguments, null, null, orderBy);

				if (cursor != null) {
					cursor.moveToFirst();

					// First record is our newest item (largest time-stamp).
					if (!cursor.isAfterLast()) {
						lastMessage = new Gson().fromJson(cursor.getString(cursor.getColumnIndex("json")), ChatPayload.class);
					}

					cursor.close();
				}
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}

		return lastMessage;
	}

	/**
	 * Gets the last chat data time-stamp that was received for a provided collaboration room ID.
	 * 
	 * @param collaborationRoomId The collaboration room ID.
	 * @param database The database.
	 * @return The time-stamp of the last message received or (-1L) if no messages were received for that chat room.
	 */
	public long getLastDataForCollaborationRoomTimestamp(long collaborationRoomId, SQLiteDatabase database) {
		long lastMessageTimestamp = -1L;

		if (database != null) {
			try {
				// Descending by time-stamp so that the newest item is the first item returned.
				String orderBy = "created DESC";
				String sqlSelection = "collabroomid==?";
				String[] sqlSelectionArguments = { String.valueOf(collaborationRoomId) };

				Cursor cursor = database.query(tableName, TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), sqlSelection, sqlSelectionArguments, null, null, orderBy);
				
				if (cursor != null) {
					cursor.moveToFirst();

					// First record is our newest item (largest time-stamp).
					if (!cursor.isAfterLast()) {
						lastMessageTimestamp = cursor.getLong(cursor.getColumnIndex("created"));
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

	/**
	 * Gets all the data for a provided collaboration room ID.
	 * 
	 * @param collaborationRoomId The collaboration room ID
	 * @param database The database.
	 * @return All the data for a provided collaboration room ID.
	 */
	public ArrayList<ChatPayload> getDataForCollaborationRoom(long collaborationRoomId, SQLiteDatabase database) {
		String orderBy = "created ASC";
		String sqlSelection = "collabroomid==?";
		String[] sqlSelectionArguments = { String.valueOf(collaborationRoomId) };

		return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
	}

	/**
	 * Gets all the data for a provided collaboration room ID that is newer than the provided timestamp.
	 * 
	 * @param collaborationRoomId The collaboration room ID.
	 * @param timestamp The timestamp.
	 * @param database The database.
	 * @return All the data for a provided collaboration room ID that is newer than the provided timestamp.
	 */
	public ArrayList<ChatPayload> getDataForCollaborationRoomSince(long collaborationRoomId, long timestamp, SQLiteDatabase database) {
		String orderBy = "created ASC";
		String sqlSelection = "collabroomid==? AND created>? ";
		String[] sqlSelectionArguments = { String.valueOf(collaborationRoomId), String.valueOf(timestamp) };

		return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
	}

	/**
	 * Gets all the data for a provided collaboration room ID that is newer than the provided timestamp.
	 * 
	 * @param collaborationRoomId The collaboration room ID.
	 * @param timestamp The timestamp.
	 * @param amount of results to cap the query by
	 * @param database The database.
	 * @return All the data for a provided collaboration room ID starting with a given timestamp and working backwards in time capping results by a certain amount.
	 */
	public ArrayList<ChatPayload> getDataForCollaborationRoomStartingFromAndGoingBack(long collaborationRoomId, long timestamp, String limit, SQLiteDatabase database) {
		String orderBy = "created DESC";
		String sqlSelection = "collabroomid==? AND created<? ";
		String[] sqlSelectionArguments = { String.valueOf(collaborationRoomId), String.valueOf(timestamp) };

		ArrayList<ChatPayload> payloads = getData(sqlSelection, sqlSelectionArguments, orderBy, limit, database);
		return payloads;
	}
	
	public ArrayList<ChatPayload> getNewChatMessagesFromDate(long collaborationRoomId, long timestamp, SQLiteDatabase database) {
		String orderBy = "created DESC";
		String sqlSelection = "collabroomid==? AND created>? ";
		String[] sqlSelectionArguments = { String.valueOf(collaborationRoomId), String.valueOf(timestamp) };

		ArrayList<ChatPayload> payloads = getData(sqlSelection, sqlSelectionArguments, orderBy, NO_LIMIT, database);
		return payloads;
	}
	
	protected ArrayList<ChatPayload> getData(String sqlSelection, String[] sqlSelectionArguments, String orderBy, String limit, SQLiteDatabase database) {
		ArrayList<ChatPayload> dataList = new ArrayList<ChatPayload>();

		if (database != null) {
			try {
				Cursor cursor;
				if (sqlSelection == null) {
					cursor = database.query(tableName, // Table
							TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), // Columns
							null, // Selection
							null, // Selection arguments
							null, // Group by
							null, // Having
							orderBy, // Order by
							limit); //result limit (-1 = no limit)
					
				} else {
					cursor = database.query(tableName, // Table
							TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), // Columns
							sqlSelection, // Selection
							sqlSelectionArguments, // Selection arguments
							null, // Group by
							null, // Having
							orderBy, // Order by
							limit); //result limit (-1 = no limit)
				}

				if (cursor != null) {
					cursor.moveToFirst();

					while (!cursor.isAfterLast()) {
						// Unfortunately, the use of having things simplified in the table constructor leaves us having
						// to make 2 calls for every data element retrieved. However, the code is easier to follow.
						ChatPayload dataItem = new Gson().fromJson(cursor.getString(cursor.getColumnIndex("json")), ChatPayload.class);
						dataItem.setId(cursor.getLong(cursor.getColumnIndex("id")));
						dataItem.setIncidentId(cursor.getLong(cursor.getColumnIndex("incidentId")));
						dataList.add(dataItem);

						cursor.moveToNext();
					}

					cursor.close();
				}
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}

		return dataList;
	}
}

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
import java.util.Map;

import android.content.Context;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
//import net.sqlcipher.database.SQLiteException;
import android.util.Log;
import scout.edu.mit.ll.nics.android.utils.Constants;

/**
 * @author Glenn L. Primmer Base class for all data base tables used in the mobile application. This class should never be instantiated, only the classes that extend this base class.
 * @param <T> The data type that the database table uses to read in new data and to return existing data.
 */

public abstract class DatabaseTable<T> {
	
	final protected String NO_LIMIT = "1073741824";
	
	/**
	 * The database table name.
	 */
	protected String tableName;

	/**
	 * Reference to the Android application context.
	 */
	protected Context context;

	/**
	 * Constructor.
	 * 
	 * @param tableName Name of the table.
	 * @param context Android context reference.
	 */
	public DatabaseTable(final String tableName, final Context context) {
		this.tableName = tableName;
		this.context = context;
	}

	/**
	 * Creates the table for the provided column map.
	 * 
	 * @param columnMap A map of the column and its SQLite data type.
	 */
	protected void createTable(final Map<String, String> columnMap, SQLiteDatabase database) {
		if (database != null) {
			// Create our sql statement for creating the table.
			String sql = "CREATE TABLE " + tableName + " (";

			boolean firstColumn = true;

			for (String column : columnMap.keySet()) {
				if (firstColumn) {
					sql += column + " " + columnMap.get(column);
					// Going to next element so set the firstColumn flag to false.
					firstColumn = false;
				} else {
					sql += "," + column + " " + columnMap.get(column);
				}
			}

			sql += ")";

			try {
				database.execSQL(sql);
			} catch (SQLException ex) {
				Log.e(Constants.nics_DEBUG_ANDROID_TAG, "SQLException occurred while trying to create table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get the database to create the table: \"" + tableName + "\"");

			// Force a runtime exception to kill the application... This is a serious exception/error.
			throw new RuntimeException("Could not get the database to create the table: \"" + tableName + "\"");
		}
	}

	/**
	 * Drops the table from the database.
	 * 
	 * @param database The database.
	 */
	public void dropTable(SQLiteDatabase database) {

		if (database != null) {
			String sql = "DROP TABLE IF EXISTS " + tableName;

			try {
				database.execSQL(sql);
			} catch (SQLException ex) {
				Log.e(Constants.nics_DEBUG_ANDROID_TAG, "SQLException occurred while trying to drop table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get the database to drop the table: \"" + tableName + "\"");

			// Force a runtime exception to kill the application... This is a serious exception/error.
			throw new RuntimeException("Could not get the database to create the table: \"" + tableName + "\"");
		}
	}

	/**
	 * Deletes a row in the database where the id column is equal to the provided id.
	 * 
	 * @param id ID value of the row to delete.
	 * @param database The database.
	 * @return The number of rows affected by this call. -1 indicates that there was an error in performing this operation.
	 */
	public long deleteData(final long id, SQLiteDatabase database) {
		long rows = 0L;

		if (database != null) {
			String whereClause = "id=?";
			String[] whereArguments = { String.valueOf(id) };

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
	
	/**
	 * Deletes a row in the database where the id column is equal to the provided id.
	 * 
	 * @param id ID value of the row to delete.
	 * @param database The database.
	 * @return The number of rows affected by this call. -1 indicates that there was an error in performing this operation.
	 */
	public long deleteDataByCollabroom(final long collabroomId, SQLiteDatabase database) {
		long rows = 0L;

		if (database != null) {
			String whereClause = "collabRoomId=?";
			String[] whereArguments = { String.valueOf(collabroomId) };

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

	/**
	 * Deletes a row in the database where the id column is equal to the provided id.
	 * 
	 * @param id ID value of the row to delete.
	 * @param database The database.
	 * @return The number of rows affected by this call. -1 indicates that there was an error in performing this operation.
	 */
	public long deleteDataByIncident(final long incidentId, SQLiteDatabase database) {
		long rows = 0L;

		if (database != null) {
			String whereClause = "incidentId=?";
			String[] whereArguments = { String.valueOf(incidentId) };

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
	
	/**
	 * Adds a data item into the table.
	 * 
	 * @param data Data structure that contains the data to insert into the database table.
	 * @param database The database.
	 * @return The number of columns affected by this operation. -1 indicates an error.
	 */
	abstract public long addData(final T data, SQLiteDatabase database);

	/**
	 * Retrieves a list of all the data items that is present in the database table.
	 * 
	 * @param database The database.
	 * @return A list of all the data items that is present in the database table.
	 */
	public ArrayList<T> getAllData(String orderBy, SQLiteDatabase database) {
		return getData(null, null, orderBy, NO_LIMIT, database);
	}

	/**
	 * Retrieves a list of all the data items that have occurred after the provided timestamp.
	 * 
	 * @param timestamp The timestamp to filter on.
	 * @param database The database.
	 * @return All the data items that have occurred after the provided timestamp.
	 */
	ArrayList<T> getDataSince(long timestamp, SQLiteDatabase database) {
		String orderBy = "createdUTC ASC";
		String sqlSelection = "createdUTC>?";
		String[] sqlSelectionArguments = { String.valueOf(timestamp) };

		return getData(sqlSelection, sqlSelectionArguments, orderBy,NO_LIMIT, database);
	}

	public ArrayList<T> getDataByIncidentId(long incidentId, String orderBy, SQLiteDatabase database) {
		String[] args = { String.valueOf(incidentId) };
		return getData("collabRoomId=?", args, orderBy,NO_LIMIT, database);
	}

	/**
	 * Deletes all the records in the table where the createdUTC timestamp is less or equal to the timestamp provided.
	 * 
	 * @param database The database.
	 * @param timestamp The items to delete that have a timestamp equal to or less than this timestamp.
	 * @return The number of records affected.
	 */
	public long deleteItemsSince(SQLiteDatabase database, long timestamp) {
		String whereClause = "createdUTC<=?";
		String[] whereClauseArguments = { String.valueOf(timestamp) };

		long rows = database.delete(tableName, whereClause, whereClauseArguments);

		return rows;
	}

	/**
	 * Retrieves a list of all the data items that qualify for the SQL selection and selection arguments.
	 * 
	 * @param sqlSelection The SQL selection statement.
	 * @param sqlSelectionArguments The SQL selection arguments.
	 * @param orderBy Method for ordering the data.
	 * @param database The database.
	 * @return A list of all the data times that qualify for the SQL selection and selection arguments.
	 */
	abstract protected ArrayList<T> getData(String sqlSelection, String[] sqlSelectionArguments, String orderBy, String limit, SQLiteDatabase database);

	/**
	 * Creates the table.
	 */
	abstract public void createTable(SQLiteDatabase database);

	public long deleteAllData(SQLiteDatabase database) {
		long rows = 0L;

		if (database != null) {
			try {
				rows = database.delete(tableName, null, null);
			} catch (Exception ex) {
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to delete all data from table: \"" + tableName + "\"", ex);
			}
		} else {
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to delete all data from table: \"" + tableName + "\"");
		}

		return rows;
	}

	abstract public long addData(final ArrayList<T> data, SQLiteDatabase database);
	
	static public String createInsert(final String tableName, final Map<String, String> tableColumnsMap) {
	    if (tableName == null || tableColumnsMap == null || tableColumnsMap.size() == 0) {
	        throw new IllegalArgumentException();
	    }
	    final StringBuilder s = new StringBuilder();
	    s.append("REPLACE INTO ").append(tableName).append(" (");
	    for (String column : tableColumnsMap.keySet()) {
	        s.append(column).append(" ,");
	    }
	    int length = s.length();
	    s.delete(length - 2, length);
	    s.append(") VALUES( ");
	    for (int i = 0; i < tableColumnsMap.size(); i++) {
	        s.append(" ? ,");
	    }
	    length = s.length();
	    s.delete(length - 2, length);
	    s.append(')');
	    return s.toString();
	}
}

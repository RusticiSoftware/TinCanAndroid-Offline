/*
    Copyright 2013 Rustici Software

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.rs;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.rs.TCLocalStorageDatabase.LocalState;
import com.rs.TCLocalStorageDatabase.LocalStatements;
import java.util.HashMap;

//Main Database Provider class. Creates, updates, inserts, queries etc.
public class TCOfflineDataManager extends ContentProvider {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TinCanJava-Offline.db";
    public static final String LOCAL_STATE_TABLE_NAME = "local_state_table";
    public static final String LOCAL_STATEMENT_TABLE_NAME       = "local_statements_table";

    private static HashMap<String, String> sLocalStateProjectionMap;
    private static HashMap<String, String> sLocalStatementProjectionMap;

    private static final int LOCAL_STATE = 1;
    private static final int LOCAL_STATE_ID = 2;
    private static final int LOCAL_STATEMENT = 3;
    private static final int LOCAL_STATEMENT_ID = 4;

    private static final UriMatcher sUriMatcher;

    private static final String LOCAL_STATE_TABLE_CREATE =
            "CREATE TABLE " + LOCAL_STATE_TABLE_NAME + " (" +
                    LocalState._ID + " INTEGER PRIMARY KEY, " +
                    LocalState.STATE_ID +" TEXT, " +
                    LocalState.STATE_CONTENTS +" TEXT, " +
                    LocalState.CREATE_DATE +" INTEGER, " +
                    LocalState.POST_DATE +" INTEGER, " +
                    LocalState.QUERY_STRING +" TEXT, " +
                    LocalState.ACTIVITY_ID +" TEXT, " +
                    LocalState.AGENT_JSON +" TEXT " +
                    ");";

    private static final String LOCAL_STATEMENT_TABLE_CREATE =
            "CREATE TABLE " + LOCAL_STATEMENT_TABLE_NAME + " (" +
                    LocalStatements._ID + " INTEGER PRIMARY KEY, " +
                    LocalStatements.STATEMENT_ID +" TEXT, " +
                    LocalStatements.STATEMENT_JSON +" TEXT, " +
                    LocalStatements.CREATE_DATE +" INTEGER, " +
                    LocalStatements.POSTED_DATE +" INTEGER, " +
                    LocalStatements.QUERY_STRING +" TEXT " +
                    ");";


    public static class TCLocalStorageDatabaseOpenHelper extends SQLiteOpenHelper {

        TCLocalStorageDatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(LOCAL_STATE_TABLE_CREATE);
            db.execSQL(LOCAL_STATEMENT_TABLE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        }

    }
    private static TCLocalStorageDatabaseOpenHelper mOpenHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case LOCAL_STATE:
                count = db.delete(LOCAL_STATE_TABLE_NAME, where, whereArgs);
                break;

            case LOCAL_STATE_ID:
                String LocalStateId = uri.getPathSegments().get(1);
                count = db.delete(LOCAL_STATE_TABLE_NAME, LocalState._ID + "=" + LocalStateId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            case LOCAL_STATEMENT:
                count = db.delete(LOCAL_STATEMENT_TABLE_NAME, where, whereArgs);
                break;

            case LOCAL_STATEMENT_ID:
                String LocalStatementId = uri.getPathSegments().get(1);
                count = db.delete(LOCAL_STATEMENT_TABLE_NAME, LocalStatements._ID + "=" + LocalStatementId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LOCAL_STATE:
                return LocalState.CONTENT_TYPE;

            case LOCAL_STATE_ID:
                return LocalState.CONTENT_ITEM_TYPE;

            case LOCAL_STATEMENT:
                return LocalStatements.CONTENT_TYPE;

            case LOCAL_STATEMENT_ID:
                return LocalStatements.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) == LOCAL_STATE) {


            ContentValues values;
            if (initialValues != null) {
                values = new ContentValues(initialValues);
            } else {
                values = new ContentValues();
            }

            Long now = Long.valueOf(System.currentTimeMillis());

            // Make sure that the fields are all set
            if (values.containsKey(LocalState.STATE_ID) == false) {
                values.put(LocalState.STATE_ID, "");
            }

            if (values.containsKey(LocalState.STATE_CONTENTS) == false) {
                values.put(LocalState.STATE_CONTENTS, "");
            }

            if (values.containsKey(LocalState.CREATE_DATE) == false) {
                values.put(LocalState.CREATE_DATE, now);
            }

            if (values.containsKey(LocalState.POST_DATE) == false) {
                values.put(LocalState.POST_DATE, 0);
            }

            if (values.containsKey(LocalState.QUERY_STRING) == false) {
                values.put(LocalState.QUERY_STRING, "");
            }

            if (values.containsKey(LocalState.ACTIVITY_ID) == false) {
                values.put(LocalState.ACTIVITY_ID, "");
            }

            if (values.containsKey(LocalState.AGENT_JSON) == false) {
                values.put(LocalState.AGENT_JSON, "");
            }


            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            long rowId = db.insert(LOCAL_STATE_TABLE_NAME, LocalState.AGENT_JSON, values);
            if (rowId > 0) {
                Uri localStateUri = ContentUris.withAppendedId(LocalState.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(localStateUri, null);
                return localStateUri;
            }
        }
        else if(sUriMatcher.match(uri) == LOCAL_STATEMENT){
            ContentValues values;
            if (initialValues != null) {
                values = new ContentValues(initialValues);
            } else {
                values = new ContentValues();
            }

            Long now = Long.valueOf(System.currentTimeMillis());

            // Make sure that the fields are all set
            if (values.containsKey(LocalStatements.STATEMENT_ID) == false) {
                values.put(LocalStatements.STATEMENT_ID, "");
            }

            if (values.containsKey(LocalStatements.STATEMENT_JSON) == false) {
                values.put(LocalStatements.STATEMENT_JSON, "");
            }

            if (values.containsKey(LocalStatements.CREATE_DATE) == false) {
                values.put(LocalStatements.CREATE_DATE, now);
            }

            if (values.containsKey(LocalStatements.POSTED_DATE) == false) {
                values.put(LocalStatements.POSTED_DATE, 0);
            }

            if (values.containsKey(LocalStatements.QUERY_STRING) == false) {
                values.put(LocalStatements.QUERY_STRING, "");
            }


            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            long rowId = db.insert(LOCAL_STATEMENT_TABLE_NAME, LocalStatements.QUERY_STRING, values);
            if (rowId > 0) {
                Uri LocalStatementsUri = ContentUris.withAppendedId(LocalStatements.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(LocalStatementsUri, null);
                return LocalStatementsUri;
            }
        }


        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TCLocalStorageDatabaseOpenHelper(getContext());
        return true;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();


        switch (sUriMatcher.match(uri)) {
            case LOCAL_STATE:
                qb.setTables(LOCAL_STATE_TABLE_NAME);
                qb.setProjectionMap(sLocalStateProjectionMap);
                break;

            case LOCAL_STATE_ID:
                qb.setTables(LOCAL_STATE_TABLE_NAME);
                qb.setProjectionMap(sLocalStateProjectionMap);
                qb.appendWhere(LocalState._ID + "=" + uri.getPathSegments().get(1));
                break;
            case LOCAL_STATEMENT:
                qb.setTables(LOCAL_STATEMENT_TABLE_NAME);
                qb.setProjectionMap(sLocalStatementProjectionMap);
                break;
            case LOCAL_STATEMENT_ID:
                qb.setTables(LOCAL_STATEMENT_TABLE_NAME);
                qb.setProjectionMap(sLocalStatementProjectionMap);
                qb.appendWhere(LocalStatements._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = LocalState.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case LOCAL_STATE:
                count = db.update(LOCAL_STATE_TABLE_NAME, values, where, whereArgs);
                break;

            case LOCAL_STATE_ID:
                String localStateId = uri.getPathSegments().get(1);
                count = db.update(LOCAL_STATE_TABLE_NAME, values, LocalState._ID + "=" + localStateId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            case LOCAL_STATEMENT:
                count = db.update(LOCAL_STATEMENT_TABLE_NAME, values, where, whereArgs);
                break;

            case LOCAL_STATEMENT_ID:
                String localStatementId = uri.getPathSegments().get(1);
                count = db.update(LOCAL_STATEMENT_TABLE_NAME, values, LocalStatements._ID + "=" + localStatementId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TCLocalStorageDatabase.AUTHORITY, "localState", LOCAL_STATE);
        sUriMatcher.addURI(TCLocalStorageDatabase.AUTHORITY, "localState/#", LOCAL_STATE_ID);
        sUriMatcher.addURI(TCLocalStorageDatabase.AUTHORITY, "localStatements", LOCAL_STATEMENT);
        sUriMatcher.addURI(TCLocalStorageDatabase.AUTHORITY, "localStatements/#", LOCAL_STATEMENT_ID);



        sLocalStateProjectionMap = new HashMap<String, String>();
        sLocalStateProjectionMap.put(LocalState._ID, LocalState._ID);
        sLocalStateProjectionMap.put(LocalState.STATE_ID,     LocalState.STATE_ID);
        sLocalStateProjectionMap.put(LocalState.STATE_CONTENTS,    LocalState.STATE_CONTENTS);
        sLocalStateProjectionMap.put(LocalState.CREATE_DATE,     LocalState.CREATE_DATE);
        sLocalStateProjectionMap.put(LocalState.POST_DATE,     LocalState.POST_DATE);
        sLocalStateProjectionMap.put(LocalState.QUERY_STRING,     LocalState.QUERY_STRING);
        sLocalStateProjectionMap.put(LocalState.ACTIVITY_ID,      LocalState.ACTIVITY_ID);
        sLocalStateProjectionMap.put(LocalState.AGENT_JSON,      LocalState.AGENT_JSON);


        sLocalStatementProjectionMap = new HashMap<String, String>();
        sLocalStatementProjectionMap.put(LocalStatements._ID, LocalStatements._ID);
        sLocalStatementProjectionMap.put(LocalStatements.STATEMENT_ID,          LocalStatements.STATEMENT_ID);
        sLocalStatementProjectionMap.put(LocalStatements.STATEMENT_JSON,         LocalStatements.STATEMENT_JSON);
        sLocalStatementProjectionMap.put(LocalStatements.CREATE_DATE,          LocalStatements.CREATE_DATE);
        sLocalStatementProjectionMap.put(LocalStatements.POSTED_DATE,     LocalStatements.POSTED_DATE);
        sLocalStatementProjectionMap.put(LocalStatements.QUERY_STRING,     LocalStatements.QUERY_STRING);


    }
}
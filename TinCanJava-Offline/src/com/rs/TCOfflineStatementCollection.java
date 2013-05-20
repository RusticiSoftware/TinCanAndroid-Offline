package com.rs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.rs.TCLocalStorageDatabase.LocalStatements;
import com.rs.TCOfflineDataManager.TCLocalStorageDatabaseOpenHelper;
import com.rs.TCOfflineStructures.LocalStatementsItem;
import com.rusticisoftware.tincan.Statement;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: USER
 * Date: 5/8/13
 * Time: 12:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCOfflineStatementCollection extends Activity {

    List<Statement> _statementArray;
    Context appContext;

    //constructor
    public TCOfflineStatementCollection(Context context)
    {
        appContext = context;
        _statementArray = new ArrayList<Statement>();
    }

    //interface for completion notification for addStatement
    public interface addStatementInterface {
        void completionBlock();
        void errorBlock(String error);
    }

    //takes a statement and adds it to the local database
    public void addStatement(Statement statement, addStatementInterface addInterface)
    {
        _statementArray.add(statement);

        Long now = Long.valueOf(System.currentTimeMillis());

        ContentValues initialValues = new ContentValues();
        try
        {
            initialValues.put(LocalStatements.CREATE_DATE, now);
            initialValues.put(LocalStatements.STATEMENT_JSON, statement.toJSON());
            initialValues.put(LocalStatements.QUERY_STRING, "");
            initialValues.put(LocalStatements.STATEMENT_ID, statement.getId().toString());
        }
        catch(Exception e)
        {
            addInterface.errorBlock("initialValues" + e.toString());
            return;
        }

        try
        {
            appContext.getContentResolver().insert(LocalStatements.CONTENT_URI, initialValues);
        }
        catch (Exception e)
        {
            addInterface.errorBlock("insert" + e.toString());
            return;
        }

        addInterface.completionBlock();
    }

    String JSONString()
    {
        JSONArray array = new JSONArray();
        for(int i = 0; i<_statementArray.size();i++)
        {
            Statement st = _statementArray.get(i);
            try {
                array.put(i,st.toJSON());
            }
            catch (Exception e)
            {
                return "";
            }
        }

        String returnString = array.toString();
        returnString = returnString.replace("\\", "");
        return returnString;
    }

    //return a list of all local statements from db
    public List<LocalStatementsItem> getCachedStatements()
    {
        List<LocalStatementsItem> statementArray = new ArrayList<LocalStatementsItem>();

        Cursor cursor;
        SQLiteDatabase database;
        TCLocalStorageDatabaseOpenHelper dbHelper;
        dbHelper = new TCLocalStorageDatabaseOpenHelper(appContext);
        database = dbHelper.getWritableDatabase();
        cursor = database.query(TCOfflineDataManager.LOCAL_STATEMENT_TABLE_NAME, null, null, null, null, null, LocalStatements.CREATE_DATE + " DESC");      //query for all the statements

        cursor.moveToFirst();     //go to the beginning of the query and then loop through all the packages, adding them to the return List
        while (!cursor.isAfterLast()) {
            LocalStatementsItem thisPackage = new LocalStatementsItem();
            thisPackage.id = cursor.getInt(0);
            thisPackage.statementId = cursor.getString(cursor.getColumnIndex("statementId"));
            thisPackage.statementJson = cursor.getString(cursor.getColumnIndex("statementJson"));
            thisPackage.createDate = cursor.getLong(cursor.getColumnIndex("createDate"));
            thisPackage.postedDate = cursor.getLong(cursor.getColumnIndex("postedDate"));
            thisPackage.querystring = cursor.getString(cursor.getColumnIndex("querystring"));

            statementArray.add(thisPackage);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();

        return statementArray;
    }


    //get all statements that haven't been posted
    List<LocalStatementsItem> getUnsentStatements(int limit)
    {
        List<LocalStatementsItem> statementArray = new ArrayList<LocalStatementsItem>();

        Cursor cursor;
        SQLiteDatabase database;
        TCLocalStorageDatabaseOpenHelper dbHelper;
        dbHelper = new TCLocalStorageDatabaseOpenHelper(appContext);
        database = dbHelper.getWritableDatabase();

        String select = LocalStatements.POSTED_DATE + "=" + "\'" + "0" + "\'";

        cursor = database.query(TCOfflineDataManager.LOCAL_STATEMENT_TABLE_NAME, null, select, null, null, null, LocalStatements.CREATE_DATE + " ASC", Integer.toString(limit));      //query for all the unposted statements

        cursor.moveToFirst();     //go to the beginning of the query and then loop through all the packages, adding them to the return List
        while (!cursor.isAfterLast()) {
            LocalStatementsItem thisPackage = new LocalStatementsItem();
            thisPackage.id = cursor.getInt(0);
            thisPackage.statementId = cursor.getString(cursor.getColumnIndex("statementId"));
            thisPackage.statementJson = cursor.getString(cursor.getColumnIndex("statementJson"));
            thisPackage.createDate = cursor.getLong(cursor.getColumnIndex("createDate"));
            thisPackage.postedDate = cursor.getLong(cursor.getColumnIndex("postedDate"));
            thisPackage.querystring = cursor.getString(cursor.getColumnIndex("querystring"));

            statementArray.add(thisPackage);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();

        return statementArray;
    }

    public interface sendUnsentInterface {
        void completionBlock();
        void errorBlock(String error);
    }

    void sendUnsentStatements(int limit, sendUnsentInterface sendInterface)
    {

    }

    //delete a statement from the db after it has been posted
    public void markStatementPosted(Statement statementPosted)
    {
        String statementId = statementPosted.getId().toString();
        SQLiteDatabase database;
        TCLocalStorageDatabaseOpenHelper dbHelper;
        dbHelper = new TCLocalStorageDatabaseOpenHelper(appContext);
        database = dbHelper.getWritableDatabase();

        String select = LocalStatements.STATEMENT_ID + "=" + "\'" + statementId + "\'";

        int count = database.delete(TCOfflineDataManager.LOCAL_STATEMENT_TABLE_NAME,select,null);

        database.close();
        dbHelper.close();
    }


}










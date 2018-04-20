package mauth.oblabs.com.firebaseauthentication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



import java.util.ArrayList;
import java.util.List;

import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "todo";

    // Contacts table name
    private static final String TABLE_CONTACT = "contact";
    private static final String TABLE_ENTITY = "entity";






    // Contacts Table Columns names for contact table
    private static final String KEY_CONTACT_ID = "id";
    private static final String KEY_CONTACT_NUMBER = "number";
    private static final String KEY_CONTACT_NAME = "name";
    private static final String KEY_CONTACT_STATUS = "status";


    //entity table columns name
    private static final String KEY_ENTITY_ID = "id";
    private static final String KEY_ENTITY_KEY = "key";
    private static final String KEY_ENTITY_NAME = "name";
    private static final String KEY_ENTITY_TIMESTAMP = "timestamp";




    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACT_TABLE = "CREATE TABLE " + TABLE_CONTACT + "("
                + KEY_CONTACT_ID + " INTEGER PRIMARY KEY," + KEY_CONTACT_NAME + " TEXT,"
                + KEY_CONTACT_NUMBER + " TEXT,"
                + KEY_CONTACT_STATUS + " INTEGER)";


        db.execSQL(CREATE_CONTACT_TABLE);


        String CREATE_ENTITY_TABLE = "CREATE TABLE " + TABLE_ENTITY + "("
                + KEY_ENTITY_ID + " INTEGER PRIMARY KEY," + KEY_ENTITY_KEY + " TEXT,"
                + KEY_ENTITY_NAME + " TEXT,"
                + KEY_ENTITY_TIMESTAMP + " TEXT)";

        db.execSQL(CREATE_ENTITY_TABLE);




    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);


        // Create tables again
        onCreate(db);
    }


    public void addContact(ContactData contactData) {

        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contactData.getName());
//        values.put(KEY_NUMBER, contactData.getMobile());
//
//
//
//        // Inserting Row
//        long rowInserted = db.insert(TABLE_CONTACT, null, values);
//        db.close(); // Closing database connection
//        if(rowInserted != -1)
//            Log.d("database handler " , "row inserted");
//        else
//            Log.d("database handler " , "row failed");
    }


    public List<ContactData> getAllContact() {
        List<ContactData> chatList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContactData contactData = new ContactData();
                contactData.setName(cursor.getString(1));
                contactData.setMobile(cursor.getString(2));



                // Adding contact to list
                chatList.add(contactData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return chatList;
    }

    public List<ContactData> getAllContactOnStatus(int status) {
        List<ContactData> chatList = new ArrayList<>();
        // Select All Query
       // String selectQuery = "SELECT  * FROM " + TABLE_CONTACT;
        String Query = "Select * from " + TABLE_CONTACT + " where " + KEY_CONTACT_STATUS + " =?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, new String[] {String.valueOf(status)});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContactData contactData = new ContactData();
                contactData.setName(cursor.getString(1));
                contactData.setMobile(cursor.getString(2));



                // Adding contact to list
                chatList.add(contactData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return chatList;
    }


    public List<ContactData> searchContactOnString(String searchCode) {
        List<ContactData> chatList = new ArrayList<>();
        // Select All Query
        // String selectQuery = "SELECT  * FROM " + TABLE_CONTACT;
        String Query = "Select * from " + TABLE_CONTACT + " where (" + KEY_CONTACT_NAME + " like ? OR "+ KEY_CONTACT_NUMBER + " like ?) AND "+KEY_CONTACT_STATUS+" =?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, new String[] {"%"+searchCode+"%","%"+searchCode+"%",String.valueOf(0)});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContactData contactData = new ContactData();
                contactData.setName(cursor.getString(1));
                contactData.setMobile(cursor.getString(2));



                // Adding contact to list
                chatList.add(contactData);
            } while (cursor.moveToNext());
        }

        // return contact list
        return chatList;
    }
    public void deleteContact(String mobileNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, KEY_CONTACT_NUMBER + " = ?",
                new String[] { mobileNumber });
        db.close();
    }


    public boolean checkContactIsPresent(String mobileNumber) {
        Log.d("database handler " , "check contact status");
        SQLiteDatabase sqldb = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_CONTACT + " where " + KEY_CONTACT_NUMBER + " =?" ;
        Cursor cursor = sqldb.rawQuery(Query, new String[] {mobileNumber});
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int updateUgcStatus(String mobileNumber , Integer status){
        Log.d("database handler " , "update status");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CONTACT_STATUS, status);

        // updating row
        return db.update(TABLE_CONTACT, values, KEY_CONTACT_NUMBER + " = ?",
                new String[] { mobileNumber });
    }

    public String getContactName(String mobileNumber){


        String Query = "Select "+KEY_CONTACT_NAME+" from " + TABLE_CONTACT + " where " + KEY_CONTACT_NUMBER + " =?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Query, new String[] {mobileNumber});

        // looping through all rows and adding to list

        if(cursor!=null && cursor.getCount()>0 ){
            Log.d("database handler " , "cursor count is "+cursor.getCount());
            cursor.moveToFirst();
            if(cursor.getColumnCount()>0){
                return cursor.getString(0);
            }else{
                return "No Name";
            }

        }else{
            return "No Name";
        }

    }
}
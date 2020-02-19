package com.example.runningapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static android.content.ContentValues.TAG;


public class dbHandler extends SQLiteOpenHelper {

    //Remote database
    FirebaseDatabase remoteDatabase = FirebaseDatabase.getInstance();
    DatabaseReference userRef = remoteDatabase.getReference("User");

    User userTemp;
    int flag = 0;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "RunningAppData";

    // Contacts table name
    private static final String TABLE_ROUTE = "route";

    // User Table Columns names
   // private static final String KEY_USID = "userID";
//    private static final String KEY_PWD = "password";
//    private static final String KEY_NAME = "name";
//    private static final String KEY_SURN = "surname";
//    private static final String KEY_EMAIL= "email";
//    private static final String KEY_KG = "kg";
//    private static final String KEY_KMW = "kmgoal_weekly";
//    private static final String KEY_KMD = "kmgoal_daily";
//    private static final String KEY_KMM = "kmgoal_monthly";

    //Route Table Columns names
    private static final String KEY_RTID = "routeID";
    private static final String KEY_USID = "userID";
    private static final String KEY_DIST = "distance";
    private static final String KEY_STRT = "startpoint";
    private static final String KEY_END = "endpoint";
    private static final String KEY_STIME = "startingtime";
    private static final String KEY_DUR = "duration";
    private static final String KEY_AVG= "averagespeed";


    public dbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROUTE_TABLE = "CREATE TABLE " + TABLE_ROUTE + "("
                + KEY_RTID + " TEXT PRIMARY KEY," + KEY_USID + " TEXT ," + KEY_DIST + " TEXT,"
                +  KEY_STRT + " TEXT," + KEY_END + " TEXT,"+ KEY_STIME + "TEXT," + KEY_DUR + "TEXT," + KEY_AVG + " TEXT)";
        db.execSQL(CREATE_ROUTE_TABLE);

        //Links the remote database
        remoteDatabase = FirebaseDatabase.getInstance();
        userTemp = new User("0","0","0","0","0",1,1,1,1,1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE);
//        // Creating tables again
        onCreate(db);
    }

    public String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public boolean addRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(KEY_USID, route.getUserID());
        //String encrypt = md5(usr.getPwd());
        //values.put(KEY_PWD, encrypt);
        values.put(KEY_RTID , route.getRouteID());
        values.put(KEY_USID , route.getUserID());
        values.put(KEY_DIST , route.getDistance());
//        values.put(KEY_STRT , route.getStartpoint());
//        values.put(KEY_END , route.getEndpoint());
//        values.put(KEY_STIME , route.getStarting_time());
//        values.put(KEY_DUR , route.getDuration());
        values.put(KEY_AVG , route.getAvgSpeed());

        long result = db.insert(TABLE_ROUTE,null,values);

        return (!(result == -1));
    }

    public void addUser(User user){
        DatabaseReference usr = userRef.child(user.getUserID());

        usr.child("email").setValue(user.getEmail());
        usr.child("kg").setValue(user.getKg());
        usr.child("km_daily").setValue(user.getKmgoal_daily());
        usr.child("km_monthly").setValue(user.getKmgoal_monthly());
        usr.child("km_weekly").setValue(user.getKmgoal_weekly());
        usr.child("name").setValue(user.getName());
        String encrypt = md5(user.getPwd());
        usr.child("password").setValue(encrypt);
        usr.child("surname").setValue(user.getSurname());
        usr.child("height").setValue(user.getHeight());

    }

    public void updateUser(User user){
        DatabaseReference usr = userRef.child(user.getUserID());

        usr.child("email").setValue(user.getEmail());
        usr.child("kg").setValue(user.getKg());
        usr.child("km_daily").setValue(user.getKmgoal_daily());
        usr.child("km_monthly").setValue(user.getKmgoal_monthly());
        usr.child("km_weekly").setValue(user.getKmgoal_weekly());
        usr.child("name").setValue(user.getName());
        String encrypt = md5(user.getPwd());
        usr.child("password").setValue(encrypt);
        usr.child("surname").setValue(user.getSurname());
        usr.child("height").setValue(user.getHeight());

    }

    public void getUser(final String userID){
        // Read from the database
        final ArrayList<Object> children = new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataSnapshot usrSnapshot = dataSnapshot.child(userID);
                Iterable<DataSnapshot> userChildren = usrSnapshot.getChildren();

                for(DataSnapshot singleSnapshot : userChildren){
                    Object var = singleSnapshot.getValue(Object.class);
                    children.add(var);
                    System.out.println("VAR  ------- "+var);
                }
                //userTemp.update(String.valueOf(userID),String.valueOf(children.get(7)),String.valueOf(children.get(6)),String.valueOf(children.get(0)),String.valueOf(children.get(8)),Float.parseFloat(String.valueOf(children.get(2))),Float.parseFloat(String.valueOf(children.get(1))),Float.parseFloat(String.valueOf(children.get(5))),Float.parseFloat(String.valueOf(children.get(3))),Float.parseFloat(String.valueOf(children.get(4))));
                flag = 1;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //System.out.println("User "+ userTemp);
        //userTemp.update(String.valueOf(userID),String.valueOf(children.get(7)),String.valueOf(children.get(6)),String.valueOf(children.get(0)),String.valueOf(children.get(8)),Float.parseFloat(String.valueOf(children.get(2))),Float.parseFloat(String.valueOf(children.get(1))),Float.parseFloat(String.valueOf(children.get(5))),Float.parseFloat(String.valueOf(children.get(3))),Float.parseFloat(String.valueOf(children.get(4))));

        //return userTemp;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////
  /*  public Route getRoute(String routeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTE, new String[] {KEY_RTID,KEY_USID,KEY_DIST  ,
                     KEY_STRT ,KEY_END ,KEY_STIME , KEY_DUR , KEY_AVG }, KEY_USID + "=?",
                new String[] { routeID }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Route route = new Route(cursor.getString(0), cursor.getString(1),
                Float.parseFloat(cursor.getString(2)), Float.parseFloat(cursor.getString(3)), cursor.getString(4),
                Float.parseFloat(cursor.getString(5)), Float.parseFloat(cursor.getString(6)),
                Point.(cursor.getString(7)));

        cursor.close();
        return route;

    }*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTE, new String[] {KEY_RTID,KEY_USID,KEY_DIST  ,
                        KEY_STRT ,KEY_END ,KEY_STIME , KEY_DUR , KEY_AVG }, KEY_USID + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor!=null && cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        else{
            return false;
        }
    }


    public int updateRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USID, route.getUserID());
        values.put(KEY_RTID , route.getRouteID());
        values.put(KEY_USID , route.getUserID());
        values.put(KEY_DIST , route.getDistance());
//        values.put(KEY_STRT , route.getStartpoint());
//        values.put(KEY_END , route.getEndpoint());
//        values.put(KEY_STIME , route.getStarting_time());
//        values.put(KEY_DUR , route.getDuration());
        values.put(KEY_AVG , route.getAvgSpeed());


// updating row
        return db.update(TABLE_ROUTE, values, KEY_USID + " = ?",
                new String[]{String.valueOf(route.getRouteID())});
    }

//    public ArrayList<Expenses> getAllExpenses(User user) {
//        ArrayList<Expenses> expList = new ArrayList<Expenses>();
//        // Select All Query
//        SQLiteDatabase db = this.getWritableDatabase();
//        //Cursor cursor = db.query(TABLE_EXPENSES, new String[] {KEY_REALTIME,KEY_USN ,KEY_PRICE ,KEY_CAT,KEY_PAYMENT}, KEY_USN + "=?",
//        //       new String[] { user.getUsername() }, null, null, KEY_REALTIME);
//        String cateQuery = " SELECT * FROM " + TABLE_EXPENSES + " WHERE " + KEY_USN +
//                " = '" + user.getUsername() + "'" +" ORDER BY " + KEY_REALTIME + " DESC "  ;
//        Cursor cursor = db.rawQuery(cateQuery,null);
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                Expenses exp = new Expenses(cursor.getString(1),
//                        cursor.getInt(0),
//                        cursor.getString(2),
//                        cursor.getDouble(3),
//                        cursor.getString(4),
//                        cursor.getString(5));
//                expList.add(exp);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return expList;
//    }


//    public ArrayList<Expenses> getSortedCategory(User user, String cate){
//        ArrayList<Expenses> categoryList = new ArrayList<Expenses>();
//        SQLiteDatabase db = this.getWritableDatabase();
//        String cateQuery = " SELECT * FROM " + TABLE_EXPENSES + " WHERE " + KEY_USN +
//                " = '" + user.getUsername() + "'" +" AND " + KEY_CAT + " = '"
//                + cate + "' ORDER BY " + KEY_REALTIME + " DESC "   ;
//        Cursor cursor = db.rawQuery(cateQuery,null);
//
//
//        if (cursor.moveToFirst()) {
//            do {
//                // Expenses exp = new Expenses(cursor.getString(0),cursor.getString(1),cursor.getString(2),Double.parseDouble(cursor.getString(3)), cursor.getString(4),cursor.getString(5));
//                Expenses exp = new Expenses(cursor.getString(1),
//                        cursor.getInt(0),
//                        cursor.getString(2),
//                        cursor.getDouble(3),
//                        cursor.getString(4),
//                        cursor.getString(5));
//                categoryList.add(exp);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return categoryList;
//    }

}
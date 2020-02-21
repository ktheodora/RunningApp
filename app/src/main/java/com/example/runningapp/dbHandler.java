package com.example.runningapp;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class dbHandler extends SQLiteOpenHelper {

    SharedPreferences sharedpreferences;


    private static final String PREFS_USER = "USER";
    //Remote database
    FirebaseDatabase remoteDatabase = FirebaseDatabase.getInstance();
    DatabaseReference userRef = remoteDatabase.getReference("User");

    User userTemp;
    boolean isuser = false;
    Context ctx;

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "RunningAppData";

    // Table names
    private static final String TABLE_ROUTE = "route";

    //Route Table Columns names
    private static final String KEY_RTID = "routeID";
    private static final String KEY_USID = "userID";
    private static final String KEY_DIST = "distance";
    private static final String KEY_STRT = "startpoint";
    private static final String KEY_END = "endpoint";
    private static final String KEY_STIME = "startingtime";
    private static final String KEY_DUR = "duration";
    private static final String KEY_AVG= "averagespeed";
    private static final String KEY_STEPS= "steps";



    public dbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROUTE_TABLE = "CREATE TABLE " + TABLE_ROUTE + "("
                + KEY_RTID + " TEXT PRIMARY KEY," + KEY_USID + " TEXT ," + KEY_DIST + " TEXT,"
                +  KEY_STRT + " TEXT," + KEY_END + " TEXT,"+ KEY_STIME + "TEXT," + KEY_DUR + "TEXT," + KEY_AVG + " TEXT," + KEY_STEPS + "TEXT)";
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
        values.put(KEY_RTID , route.getRouteID());
        values.put(KEY_DIST , route.getDistance());
        values.put(KEY_AVG , route.getAvgSpeed());
        values.put(KEY_STIME , route.getStarting_time().toString());
        values.put(KEY_DUR , route.getDuration().toString());
        values.put(KEY_STRT , route.getStartpoint());
        values.put(KEY_END , route.getEndpoint());
        values.put(KEY_STEPS, route.getSteps());

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
        usr.child("weight").setValue(user.getWeight());

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
        usr.child("weight").setValue(user.getWeight());

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
                User userTemp1 = new User();
                userTemp1.update(String.valueOf(userID),String.valueOf(children.get(7)),String.valueOf(children.get(6)),String.valueOf(children.get(0)),String.valueOf(children.get(8)),Float.parseFloat(String.valueOf(children.get(2))),Float.parseFloat(String.valueOf(children.get(1))),Float.parseFloat(String.valueOf(children.get(5))),Float.parseFloat(String.valueOf(children.get(3))),Float.parseFloat(String.valueOf(children.get(4))));

                SharedPreferences settings = ctx.getSharedPreferences(PREFS_USER, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("user_id", userTemp1.getUserID());
                editor.putString("user_pass", userTemp1.getPassword_raw());
                editor.putString("user_name", userTemp1.getName());
                editor.putString("user_surname", userTemp1.getSurname());
                editor.putString("user_email", userTemp1.getEmail());
                editor.putFloat("user_kg", userTemp1.getKg());
                editor.putFloat("user_height", userTemp1.getWeight());
                editor.putFloat("user_km_w", userTemp1.getKmgoal_weekly());
                editor.putFloat("user_km_d", userTemp1.getKmgoal_daily());
                editor.putFloat("user_km_m", userTemp1.getKmgoal_monthly());

                // Commit the edits
                editor.commit();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public User getLoggedUser(){
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_USER, 0);
        String user_id = settings.getString("user_id","0" );
        String user_pass = settings.getString("user_pass","0" );
        String user_name = settings.getString("user_name","0" );
        String user_surname = settings.getString("user_surname","0" );
        String user_email = settings.getString("user_email","0" );
        Float user_kg = settings.getFloat("user_kg",0);
        Float user_weight = settings.getFloat("user_weight",0);
        Float user_km_w = settings.getFloat("user_km_w",0);
        Float user_km_d = settings.getFloat("user_km_d",0);
        Float user_km_m = settings.getFloat("user_km_m",0);

        User usr = new User(user_id,user_pass,user_name,user_email,user_surname,user_kg,user_weight,user_km_w,user_km_d,user_km_m);
        return  usr;
    }


    public Route getRoute(String routeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTE, new String[] {KEY_RTID,KEY_USID,KEY_DIST  ,
                     KEY_STRT ,KEY_END ,KEY_STIME , KEY_DUR , KEY_AVG }, KEY_USID + "=?",
                new String[] { routeID }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Route route = null;
        try {
            route = new Route(cursor.getString(1),
                    cursor.getString(0),
                    cursor.getFloat(2),
                    cursor.getFloat(3),
                    new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(4)),
                    new SimpleDateFormat("HH:mm:ss").parse(cursor.getString(5)),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getFloat(8)
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cursor.close();
        return route;

    }

    public boolean isUser(final String username) {
        final boolean[] yesorno = {false};
        readData(username, new FirebaseCallback() {
            @Override
            public void onCallBack(Boolean isuser) {
                yesorno[0] = isuser;
                System.out.println("AAAAAAAAAAAA- apres -----------"+yesorno[0]);
            }
        });
        return yesorno[0];
    }

    public void readData(String username, final FirebaseCallback firebaseCallback){
        DatabaseReference userNameRef = userRef.child(username);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    isuser = true;
                }else{
                    isuser = false;
                }
                firebaseCallback.onCallBack(isuser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
    }

    public interface FirebaseCallback {
        void onCallBack(Boolean isuser);
    }


    public int updateRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USID, route.getUserID());
        values.put(KEY_RTID , route.getRouteID());
        values.put(KEY_USID , route.getUserID());
        values.put(KEY_DIST , route.getDistance());
        values.put(KEY_STRT , route.getStartpoint());
        values.put(KEY_END , route.getEndpoint());
        values.put(KEY_STIME , route.getStarting_time().toString());
        values.put(KEY_DUR , route.getDuration().toString());
        values.put(KEY_AVG , route.getAvgSpeed());
        values.put(KEY_STEPS, route.getSteps());


// updating row
        return db.update(TABLE_ROUTE, values, KEY_USID + " = ?",
                new String[]{String.valueOf(route.getRouteID())});
    }

    public boolean routeExists(Route r) {
        //String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + KEY_USN + " = " + user.getUsername();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ROUTE, new String[] {KEY_RTID,KEY_USID,KEY_DIST  ,
                        KEY_STRT ,KEY_END ,KEY_STIME , KEY_DUR , KEY_AVG }, KEY_USID + "=?",
                new String[] { r.getRouteID() }, null, null, null, null);
        //Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.getCount()>0) {
            cursor.close();
            return true;
        }
        else{
            return false;
        }
    }

    public ArrayList<Route> getAllRoutes(User user) {
        ArrayList<Route> routeList = new ArrayList<Route>();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.query(TABLE_EXPENSES, new String[] {KEY_REALTIME,KEY_USN ,KEY_PRICE ,KEY_CAT,KEY_PAYMENT}, KEY_USN + "=?",
        //       new String[] { user.getUsername() }, null, null, KEY_REALTIME);
        String cateQuery = " SELECT * FROM " + TABLE_ROUTE + " WHERE " + KEY_USID +
                " = '" + user.getUserID() + "'" ;
        Cursor cursor = db.rawQuery(cateQuery,null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Route rt = null;
                try {
                    rt = new Route(cursor.getString(1),
                            cursor.getString(0),
                            cursor.getFloat(2),
                            cursor.getFloat(3),
                            new SimpleDateFormat("dd/MM/yyyy").parse(cursor.getString(4)),
                            new SimpleDateFormat("HH:mm:ss").parse(cursor.getString(5)),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getFloat(8)
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                routeList.add(rt);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return routeList;
    }

    public boolean removeRoute(Route r) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ROUTE, new String[] {KEY_RTID,KEY_USID,KEY_DIST  ,
                        KEY_STRT ,KEY_END ,KEY_STIME , KEY_DUR , KEY_AVG }, KEY_USID + "=?",
                new String[] { r.getRouteID() }, null, null, null, null);
        //Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor!=null && cursor.getCount()>0) {
            db.delete(TABLE_ROUTE,KEY_RTID + "=?",new String[] { String.valueOf(r.getRouteID()) });
            cursor.close();
            db.close();
            return true;
        }
        else{
            //expense not found, deletion not possible
            return false;
        }
    }
}
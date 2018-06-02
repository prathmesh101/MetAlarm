package com.metalarm.android.metalarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Prathmesh Pardhiye on 20-11-2017.
 */




public class DBHelper {
    final static String DB_NAME="EventDB"; //Create dname
    SQLiteDatabase db;
    Context context;



    //open database
    public void openDatabase(Context context, String database){
        this.context=context;
        db=this.context.openOrCreateDatabase(database,MODE_PRIVATE,null);
        createTable();
    }



    //Create table
    private void createTable(){
        String createQuery="CREATE TABLE IF NOT EXISTS EventListDb(EventId INTEGER PRIMARY KEY, EventLabel VARCHAR,MET VARCHAR);";
       db.execSQL(createQuery);
    }



    //Insert Values
    public void eventInsert(String eventLabel, String metValue){


        String deleteQuery="INSERT INTO EventListDb(EventLabel, MET) VALUES('"+eventLabel+"','"+metValue+"');";
        db.execSQL(deleteQuery);
    }



    //Update Values
    public void eventUpdate(String eventLabel, String met, int id){

        String updateQuery="UPDATE EventListDb SET EventLabel='"+eventLabel+"',MET='"+met+"' WHERE EventId="+id;
        db.execSQL(updateQuery);
    }

    //delete value
    public void eventDelete(int id){
        String delQuery = "DELETE FROM EventListDb WHERE EventId="+id;

        db.execSQL(delQuery);
    }


    //get selected row
    public List<String> getSelectedRow(Integer eventId){

        List<String> row=new ArrayList<>();

        String query="select * from EventListDb where EventId="+eventId;
        Cursor cursor=db.rawQuery(query,null);


        while (cursor.moveToNext()){
            String label=cursor.getString(1);
            String met=cursor.getString(2);

            row.add(label);
            row.add(met);
        }

        return row;
    }



    public ArrayList<HashMap<String,String>> executeSelectEvents(){


        String query="Select * from EventListDb";
        Cursor cursor=db.rawQuery(query,null);
        ArrayList<HashMap<String,String>> eventList=new ArrayList<>();



        while (cursor.moveToNext()){
            HashMap<String,String> event=new HashMap<>();
            String colId=cursor.getColumnName(0);
            String colIDVal=cursor.getString(0);
            String colName=cursor.getColumnName(1);
            String colNameValue=cursor.getString(1);
            String colMET=cursor.getColumnName(2);
            String colMETVal=cursor.getString(2);
            event.put(colId,colIDVal);
            event.put(colName,colNameValue.replace("''","'"));
            event.put(colMET,colMETVal);
            eventList.add(event);


        }



        return eventList;
    }
}

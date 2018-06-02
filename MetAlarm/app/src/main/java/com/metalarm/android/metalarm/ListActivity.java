package com.metalarm.android.metalarm;

import android.content.Intent;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{


    ListView mainEventList;
    DBHelper dbHelper;
    boolean dismiss=false;

    ArrayList<HashMap<String, String>> events;
    HashMap<String,String> setAlarm=new HashMap<>();

    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        mainEventList=(ListView)findViewById(R.id.mainEventList);
        mainEventList.setOnItemClickListener(this);
        dbHelper=new DBHelper();
        dbHelper.openDatabase(getApplicationContext(), DBHelper.DB_NAME);

        loadEventDB();

    }
    private void loadEventDB(){
        ArrayList<HashMap<String, String>> data= dbHelper.executeSelectEvents();
        if(data.size()>0){
            events=data;
            eventsAdapter();
        }else {
            Toast.makeText(getApplicationContext(),"NoEvents",Toast.LENGTH_SHORT).show();
        }

    } 

    private void eventsAdapter(){
        String[] from = {"EventId","EventLabel", "MET"};
        int[] to = {R.id.eventId,R.id.evenLabel, R.id.metVal};
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), events, R.layout.event_item, from, to);
        mainEventList.setAdapter(adapter);
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadEventDB();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView cc=(TextView)view.findViewById(R.id.eventId);
        String eventID=cc.getText().toString();
        Intent intent=new Intent(this,MainEventActivity.class);
        intent.putExtra("id",eventID);
        intent.putExtra("start",false);
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.createNewEvent:
                Intent intent=new Intent(ListActivity.this,MainEventActivity.class);
                intent.putExtra("start",true);
                startActivity(intent);
                return true;
            case R.id.createAlarm:
                dismiss=false;
                if(events.size()>0){
                    setAlarms(0);
                }
                return true;
            case R.id.cancelAlarm:
                dismiss=true;
                if(events.size()>0){
                    dismissAlarm(0);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setAlarms(int position){
        
        Intent intent=new Intent(AlarmClock.ACTION_SET_ALARM);
        Calendar calendar=Calendar.getInstance();
        
        String alarm=events.get(position).get("EventLabel");
        int metHour=Integer.valueOf(events.get(position).get("MET").split(":")[0]);
        int metMinute=Integer.valueOf(events.get(position).get("MET").split(":")[1]);
        calendar.add(Calendar.HOUR,metHour);
        calendar.add(Calendar.MINUTE,metMinute);
        
        intent.putExtra(AlarmClock.EXTRA_HOUR,calendar.get(Calendar.HOUR_OF_DAY));
        intent.putExtra(AlarmClock.EXTRA_MINUTES,calendar.get(Calendar.MINUTE));
        intent.putExtra(AlarmClock.EXTRA_MESSAGE,alarm);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI,true);
        
        setAlarm.put("hour",String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        setAlarm.put("minutes",String.valueOf(calendar.get(Calendar.MINUTE)));
        
        startActivityForResult(intent,position);
    }

    private void dismissAlarm(int position){
        Intent intentDismiss=new Intent(AlarmClock.ACTION_DISMISS_ALARM);
        String label=events.get(position).get("EventLabel");
        intentDismiss.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE,AlarmClock.ALARM_SEARCH_MODE_LABEL);
        intentDismiss.putExtra(AlarmClock.EXTRA_MESSAGE,label);
        startActivityForResult(intentDismiss,position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if(requestCode>=0 && requestCode<events.size()-1){
            if(dismiss){
                dismissAlarm(requestCode+1);
            }else {
                setAlarms(requestCode+1);
            }

        }
    }
}

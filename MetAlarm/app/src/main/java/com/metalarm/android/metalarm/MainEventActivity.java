package com.metalarm.android.metalarm;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.List;

public class MainEventActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper db;
    EditText addEventEditText;
    TextView editMetTimer;



    LinearLayout metContData;

    Button btnSave, btnDiscard,btnDelete;

    String id;
    Boolean start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_event);


        addEventEditText=(EditText)findViewById(R.id.addEventEditText);
        editMetTimer=(TextView)findViewById(R.id.editMetTimer);
        metContData=(LinearLayout)findViewById(R.id.metContData);



        btnSave = (Button)findViewById(R.id.btnSave);
        btnDiscard = (Button)findViewById(R.id.btnDiscard);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        
        //db
        db=new DBHelper();
        db.openDatabase(this, DBHelper.DB_NAME);
        
        
        start=getIntent().getExtras().getBoolean("start");
        
        
        if(!start){
            
            
            id=getIntent().getExtras().getString("id");
            
            setSelectEvent();
            metContData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] time=editMetTimer.getText().toString().split(":");

                    int hour= Integer.valueOf(time[0]);

                    int minute= Integer.valueOf(time[1]);

                    TimePickerDialog tpDialog= createTimePickerDialog(hour,minute);
                    tpDialog.setTitle("Set Mission Elapsed Time");
                    tpDialog.show();

                }
            });
        }else {

            metContData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int hour=0;
                    int minute=0;
                    TimePickerDialog dialog= createTimePickerDialog(hour,minute);
                    dialog.setTitle("Set MET");
                    dialog.show();

                }
            });
        }

        btnSave.setOnClickListener(this);
        btnDiscard.setOnClickListener(this);
        btnDelete.setOnClickListener(this);



    }

    
    
    public TimePickerDialog createTimePickerDialog(int hour, int minute){

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                editMetTimer.setText(hour+":"+minute);
            }
        },hour,minute,true);
        return timePickerDialog;
    }




    public void setSelectEvent(){
        List<String> row=db.getSelectedRow(Integer.valueOf(id));
        addEventEditText.setText(row.get(0));
        editMetTimer.setText(row.get(1));
    }


    private void discard() {


        addEventEditText.setText("");
        editMetTimer.setText("");
        Toast.makeText(getApplicationContext(),"Your event is deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            
            
            case R.id.btnSave:
                String eventLabelValue=addEventEditText.getText().toString();
                String metVal=editMetTimer.getText().toString();
                if(!eventLabelValue.isEmpty() && !metVal.isEmpty()){
                    if(start){
                        eventLabelValue=eventLabelValue.replace("'","''");
                        db.eventInsert(eventLabelValue,metVal);
                        Toast.makeText(getApplicationContext(),"event added", Toast.LENGTH_SHORT).show();
                    }else {
                        eventLabelValue=eventLabelValue.replace("'","''");
                        db.eventUpdate(eventLabelValue,metVal, Integer.parseInt(id));
                        Toast.makeText(getApplicationContext(),"event updated", Toast.LENGTH_SHORT).show();
                    }
                    addEventEditText.setText("");
                    editMetTimer.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(),"Incorrect data", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDiscard:
                discard();
                break;
            case R.id.btnDelete:
                db.eventDelete(Integer.parseInt(id));
                discard();
                break;
        }
    }

   


}

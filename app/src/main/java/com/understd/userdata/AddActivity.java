package com.understd.userdata;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    EditText etDoTitle, etDoDesc, etDoDate, etDoTime;
    ImageButton ibDate, ibTime;

    Calendar dateTime = Calendar.getInstance();

    FirebaseDatabase mDB;
    DatabaseReference mDBtoDo;
    FirebaseAuth mAuth;
    Long jumlahData;
    Integer currentPostId;
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            DateFormat df_date = DateFormat.getDateInstance();
            etDoDate.setText(df_date.format(dateTime.getTime()));
        }
    };
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);

            DateFormat df_time = DateFormat.getTimeInstance();
            etDoTime.setText(df_time.format(dateTime.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setTitle("Add toDoList");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDB = FirebaseDatabase.getInstance();
        mDBtoDo = mDB.getReference("ToDoList");
        mAuth = FirebaseAuth.getInstance();

        etDoTitle = (EditText) findViewById(R.id.editTextDo);
        etDoDesc = (EditText) findViewById(R.id.editTextDoDesc);
        etDoTime = (EditText) findViewById(R.id.editTextDoTime);
        etDoDate = (EditText) findViewById(R.id.editTextDoDate);

        ibDate = (ImageButton) findViewById(R.id.imageButtonDate);
        ibTime = (ImageButton) findViewById(R.id.imageButtonTime);

        ibDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });

        ibTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTime();
            }
        });

        // Temporary Script, please remove after usage

        DatabaseReference dbC = mDB.getReference("ToDoList");
        dbC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                jumlahData = dataSnapshot.getChildrenCount() - 1;
                currentPostId = jumlahData.intValue() + 1;
                String strValue = jumlahData.toString();
                Boolean isDataNotExist = false;
                while (isDataNotExist == false) {
                    Boolean cleanCheck = dataSnapshot.child(currentPostId.toString()).exists();
                    if (cleanCheck) {
                        Log.d("FirebaseCounter", "Child with ID " + currentPostId.toString() + " already Exists! +1");
                        currentPostId += 1;
                    } else {
                        Log.d("FirebaseCounter", "Child with ID " + currentPostId.toString() + " Available to Use!");
                        isDataNotExist = true;
                    }
                }
                Log.d("FirebaseCounter", strValue);
                Log.d("FirebaseCounter", "Next Post Should be : " + currentPostId.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            save();
        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDate() {
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTime() {
        new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.MINUTE), true).show();
    }

    private void save() {
        String title = etDoTitle.getText().toString();
        String desc = etDoDesc.getText().toString();
        String date = etDoDate.getText().toString();
        String time = etDoTime.getText().toString();
        FirebaseUser fUser = mAuth.getCurrentUser();

        if (TextUtils.isEmpty(title)) {
            etDoTitle.setError("Fill blank field");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            etDoTitle.setError("Please choose date");
            return;
        }
        if (TextUtils.isEmpty(time)) {
            etDoTime.setError("Please choose time");
            return;
        }

        // Database Refrence for ToDoList
        DatabaseReference dbToDoList = mDBtoDo.child(currentPostId.toString());
        // Database Refrence for Users
        DatabaseReference users = mDB.getReference("User").child(mAuth.getCurrentUser().getUid()).child("todos");

        Log.d("FirebaseCounter", mAuth.getCurrentUser().getUid());
        dbToDoList.child("title").setValue(title);
        dbToDoList.child("desc").setValue(desc);
        dbToDoList.child("date").setValue(date);
        dbToDoList.child("time").setValue(time);
        dbToDoList.child("owner").setValue(fUser.getUid().toString());
        users.child(currentPostId.toString()).setValue(title);
        finish();
    }
}

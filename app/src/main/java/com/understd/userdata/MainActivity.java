package com.understd.userdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private TextView tvUsername, tvEmail;
    private Button btLogout;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDB;
    private DatabaseReference mDBtodo;

    private RecyclerView mToDOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("to-Do-list");

        tvUsername = (TextView) findViewById(R.id.textViewUsername);
        tvEmail = (TextView) findViewById(R.id.textViewEmail);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();
        mDBtodo = mDB.getReference().child("ToDoList");

        mToDOList = (RecyclerView) findViewById(R.id.recyclerView);
        mToDOList.setHasFixedSize(true);
        mToDOList.setLayoutManager(new LinearLayoutManager(this));

        //------------START MENGAMBIL DATA USERNAME DARI ACCOUNT----------//
        DatabaseReference userName = mDB.getReference()
                .child("User").child(mAuth.getCurrentUser().getUid()).child("username");
        userName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                tvUsername.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //------------END MENGAMBIL DATA USERNAME DARI ACCOUNT----------//

        //------------START MENGAMBIL DATA EMAIL DARI ACCOUNT----------//
        DatabaseReference eMail = mDB.getReference()
                .child("User").child(mAuth.getCurrentUser().getUid()).child("email");
        eMail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getValue(String.class);
                tvEmail.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //------------END MENGAMBIL DATA EMAIL DARI ACCOUNT----------//

        btLogout = (Button) findViewById(R.id.buttonLogout);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent l = new Intent(MainActivity.this, RegisterActivity.class);
                l.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(l);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ToDoList, ToDoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ToDoList, ToDoViewHolder>(
                ToDoList.class,
                R.layout.todolist,
                ToDoViewHolder.class,
                mDBtodo
        ) {
            @Override
            protected void populateViewHolder(ToDoViewHolder viewHolder, ToDoList model, int position) {
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getTime());
                viewHolder.setOwner(model.getOwner());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
            }
        };
        mToDOList.setAdapter(firebaseRecyclerAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, AddActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ToDoViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView toDoDate = (TextView) mView.findViewById(R.id.todo_date);
            toDoDate.setText(date);
        }

        public void setTitle(String title) {
            TextView toDoTitle = (TextView) mView.findViewById(R.id.todo_title);
            toDoTitle.setText(title);
        }

        public void setTime(String time) {
            TextView toDoTime = (TextView) mView.findViewById(R.id.todo_time);
            toDoTime.setText(time);
        }

        public void setDesc(String desc) {
            TextView toDoDesc = (TextView) mView.findViewById(R.id.todo_desc);
            toDoDesc.setText(desc);
        }

        public void setOwner(String owner) {
            TextView toDoOwner = (TextView) mView.findViewById(R.id.todo_owner);
            toDoOwner.setText(owner);
        }

    }
}

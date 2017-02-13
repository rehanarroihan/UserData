package com.understd.userdata;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.android.device.DeviceName;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btLogin;
    private TextView tvRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();

        etEmail = (EditText) findViewById(R.id.editTextEmails);
        etPassword = (EditText) findViewById(R.id.editTextPasswords);

        btLogin = (Button) findViewById(R.id.buttonLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budalMelbu();
            }
        });

        tvRegister = (TextView) findViewById(R.id.textViewRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(LoginActivity.this, RegisterActivity.class);
                s.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(s);
                finish();
            }
        });

        mDialog = new ProgressDialog(this);
    }

    private void budalMelbu() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Sulapan a pean ?");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Sulapan ta yok opo ?");
            return;
        }

        mDialog.setMessage("Sek, tak mikir dilut ..");
        mDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                DatabaseReference lastLogin = mDB.child("User").child(mAuth.getCurrentUser().getUid()).child("last_login");
                String deviceName = DeviceName.getDeviceName(); //Mengambil nama device

                //---------------------START MENGAMBIL DATA WAKTU---------------------//
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String dataWaktu = df.format(c.getTime());
                //---------------------END MENGAMBIL DATA WAKTU---------------------//

                lastLogin.child("time").setValue(dataWaktu);
                lastLogin.child("device").setValue(deviceName);

                mDialog.dismiss();

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Error")
                        .setMessage("Salah bos !")
                        .setCancelable(false)
                        .setPositiveButton("YUHU", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
    }
}

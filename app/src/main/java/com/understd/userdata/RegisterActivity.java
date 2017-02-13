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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword;
    private TextView tvLogin;
    private Button btRegister;
    private ProgressDialog mDialog;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDB;
    private DatabaseReference mDBuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        etUsername = (EditText) findViewById(R.id.editTextUsername);
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPassword = (EditText) findViewById(R.id.editTextPassword);

        btRegister = (Button) findViewById(R.id.buttonRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budalNdaftar();
            }
        });

        tvLogin = (TextView) findViewById(R.id.textViewLogin);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent u = new Intent(RegisterActivity.this, LoginActivity.class);
                u.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(u);
                finish();
            }
        });

        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();

        //Membuat child user di database saat onCreate
        mDBuser = mDB.getReference().child("User");

    }

    private void budalNdaftar() {
        final String username = etUsername.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username harus diisi");
            return;
        }
        if (username.length() <= 5) {
            etUsername.setError("Username minimal 6 char");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email harus diisi");
            return;
        }
        if (!email.matches(emailPattern)) {
            etEmail.setError("Email harus sesuai format");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password harus diisi");
            return;
        }
        if (password.length() <= 7) {
            etPassword.setError("Password minimal 8 char");
            return;
        }

        mDialog.setMessage("Loading, please wait");
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String UID = mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDB = mDBuser.child(UID);
                currentUserDB.child("username").setValue(username);
                currentUserDB.child("email").setValue(email);
                currentUserDB.child("password").setValue(password);

                //---------------------START MENGAMBIL DATA WAKTU---------------------//
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String dataWaktu = df.format(c.getTime());
                //---------------------END MENGAMBIL DATA WAKTU---------------------//

                currentUserDB.child("date_created").setValue(dataWaktu);

                mDialog.dismiss();

                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Berhasil")
                        .setMessage("Registrasi berhasil, silahkan login.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                etUsername.setText("");
                                etEmail.setText("");
                                etPassword.setText("");
                            }
                        }).show();
                mAuth.signOut();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();

                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Berhasil")
                        .setMessage("Registrasi gagal, coba lagi.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
    }
}

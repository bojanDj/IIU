package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView email,pass;
    Button prijaviSe;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    @Override
    protected void onStart() {
        super.onStart();
//        if (firebaseUser != null) {
//            Intent i;
//            if (firebaseUser.getEmail().contains("@admin"))
//                i = new Intent(LoginActivity.this,MainActivityAdmin.class);
//            else
//                i = new Intent(LoginActivity.this,MainActivityEmp.class);
//            startActivity(i);
//            finish();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        email = findViewById(R.id.txtEmailLogin);
        pass = findViewById(R.id.txtPassLogin);
        prijaviSe = findViewById(R.id.btnPrijaviSe2);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        prijaviSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emaile = email.getText().toString();
                String password = pass.getText().toString();
                if (TextUtils.isEmpty(emaile) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Sva polja moraju biti popunjena.", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(emaile, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent i;
                                if (emaile.contains("@admin"))
                                    i = new Intent(LoginActivity.this, MainActivityAdmin.class);
                                else
                                    i = new Intent(LoginActivity.this, MainActivityEmp.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Uneseni podaci nisu odgovarajuci.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });
    }
}

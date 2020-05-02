package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NewEmp extends AppCompatActivity {
    TextView user,pass,email;
    Button registrujSe2;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    NotificationManagerCompat notificationManagerCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_emp);
        user = findViewById(R.id.txtUser);
        pass = findViewById(R.id.txtPass);
        email = findViewById(R.id.txtEmail);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        notificationManagerCompat= NotificationManagerCompat.from(this);
        registrujSe2 = findViewById(R.id.btnRegistrujSe2);
        registrujSe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = user.getText().toString();
                final String password = pass.getText().toString();
                final String emaile = email.getText().toString();
                if (TextUtils.isEmpty(emaile) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                    Toast.makeText(NewEmp.this, "Sva polja moraju biti popunjena.", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(emaile,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userID = firebaseUser.getUid();
                                reference =  FirebaseDatabase.getInstance().getReference("Users").child(userID);

                                HashMap<String,String> map = new HashMap<>();
                                map.put("id",userID);
                                map.put("username",username);
                                map.put("imageURL","");

                                reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.setText("");
                                            pass.setText("");
                                            email.setText("");
                                            Toast.makeText(NewEmp.this,"Uspesno kreiran zaposleni.",Toast.LENGTH_LONG).show();
                                            posaljiNotifikaciju();
                                        }
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            posaljiNotifikaciju2();
                        }
                    });
                }
            }
        });
    }
    public void posaljiNotifikaciju() {
        Notification notification = new NotificationCompat.Builder(this, Chanel.CHANEL_1_ID)
                .setContentTitle("Uspesno...")
                .setContentText("Novi zaposleni je uspesno registrovan.")
                .setSmallIcon(R.drawable.check)
                .build();
        notificationManagerCompat.notify(1, notification);
    }

    public void posaljiNotifikaciju2() {
        Notification notification = new NotificationCompat.Builder(this, Chanel.CHANEL_2_ID)
                .setContentTitle("Zao nam je... " + user.getText().toString())
                .setContentText("Nije uspelo kreiranje naloga.")
                .setSmallIcon(R.drawable.x)
                .build();
        notificationManagerCompat.notify(2, notification);
    }
}

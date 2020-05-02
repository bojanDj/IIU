package com.example.iot;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Opcije extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseUser fuser;
    StorageReference storageReference;
    ImageView slika;
    Button izmeni;
    TextView font,username,name,lastname;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    static SharedPreferences sharedPreferencesOpcije;
    SharedPreferences.Editor editor;
    NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcije);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Licni podaci");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        izmeni = findViewById(R.id.btnIzmeni);
        slika = findViewById(R.id.imageView2);
     //   font = findViewById(R.id.txtFont);
        username = findViewById(R.id.txtUsername);
        name = findViewById(R.id.txtName);
        lastname = findViewById(R.id.txtLastname);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        sharedPreferencesOpcije=this.getSharedPreferences("Opcije", MODE_PRIVATE);
        editor = sharedPreferencesOpcije.edit();
        notificationManagerCompat= NotificationManagerCompat.from(this);

//        izmeni.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String velicina = font.getText().toString();
//                editor.putString("VelicinaFonta",velicina);
//                editor.apply();
//                font.setText("");
//                Toast.makeText(Opcije.this, "Font je promenjen na " + velicina, Toast.LENGTH_SHORT).show();
//            }
//        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    username.setText(username.getText() + "" +user.getUsername());
                    name.setText(name.getText() + "" + user.getName());
                    lastname.setText(lastname.getText() + "" + user.getLastname());
                    if (user.getImageURL().equals("")) {
                        slika.setImageResource(R.drawable.userimg);
                    } else {
                        Glide.with(Opcije.this).load(user.getImageURL()).into(slika);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        slika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,1);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(Opcije.this, "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Sacekajte...");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", ""+mUri);
                        reference.updateChildren(map);
                        posaljiNotifikaciju("Uspesno ste promenili sliku!","");
                        pd.dismiss();
                    } else {
                        Toast.makeText(Opcije.this, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Opcije.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(Opcije.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    public void posaljiNotifikaciju(String naslov,String tekst) {
        Notification notification = new NotificationCompat.Builder(this, Chanel.CHANEL_1_ID)
                .setContentTitle(naslov)
                .setContentText(tekst)
                .setSmallIcon(R.drawable.check)
                .build();
        notificationManagerCompat.notify(1, notification);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meni,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            System.exit(0);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.item3) {
            Intent i = new Intent(Opcije.this,About.class);
            startActivity(i);
            return true;
        }
//        if (item.getItemId() == R.id.item2) {
//            Intent i = new Intent(Opcije.this,Opcije.class);
//            startActivity(i);
//            return true;
//        }
        return false;

    }
}

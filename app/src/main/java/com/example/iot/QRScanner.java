package com.example.iot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    FirebaseAuth auth;
    DatabaseReference reference;
    int brojac = 0;
    final ArrayList<DanZaKorisnika> mDanZaKorisnikas = new ArrayList<>();
    final ArrayList<String> mRec = new ArrayList<>();
    final ArrayList<Integer> idMax = new ArrayList<>();
    int prijava = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;
        auth = FirebaseAuth.getInstance();
        prijava = getIntent().getIntExtra("prijava",0);
        vratiEvidenciju();
        vratiRec();

        if(currentApiVersion >=  Build.VERSION_CODES.M)
            if(!checkPermission()) requestPermission();
    }
    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }
    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Pristup kameri je dozvoljen.", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Pristup kameri nije dozvoljen.", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("Morate prvo da odobrite pristup kameri.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QRScanner.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        String rec = mRec.get(0);
        if (result.getText().equals(rec)) {
            Date datum = new Date();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

            FirebaseUser firebaseUser = auth.getCurrentUser();
            String userID = firebaseUser.getUid();

            HashMap map = new HashMap();
            map.put("id",userID);
            map.put("datum",sdf1.format(datum));

            if (prijava == 1) {
                if (mDanZaKorisnikas.size() == 0) {
                    map.put("dolazak", sdf2.format(datum));
                    map.put("odlazak", "");
                    String uniqueID = UUID.randomUUID().toString();
                    Log.d("Novi ID u bazi", String.valueOf(uniqueID));
                    reference = FirebaseDatabase.getInstance().getReference("Evidencija").child(uniqueID);
                    reference.setValue(map);

                    builder.setTitle("Uspesno prijavljivanje dolaska!");
                    builder.setMessage(sdf1.format(datum) + " " + sdf2.format(datum));
                } else {
                    builder.setTitle("Neuspesno prijavljivanje dolaska!");
                    builder.setMessage("Dolazak je vec prijavljen ranije");
                }
            } else {
                if (mDanZaKorisnikas.size() == 0) {
                    builder.setTitle("Neuspesno prijavljivanje odlaska!");
                    builder.setMessage("Nije prijavljen ni dolazak");
                } else {
                    if (!mDanZaKorisnikas.get(0).getOdlazak().equals("")) {
                        builder.setTitle("Neuspesno prijavljivanje odlaska!");
                        builder.setMessage("Odlazak je vec prijavljen ranije");
                    } else {
                        map.put("dolazak",mDanZaKorisnikas.get(0).getDolazak());
                        map.put("odlazak",sdf2.format(datum));
                        reference = FirebaseDatabase.getInstance().getReference("Evidencija").child(String.valueOf(mDanZaKorisnikas.get(0).getIdEvid()));
                        reference.updateChildren(map);

                        builder.setTitle("Uspesno prijavljivanje odlaska!");
                        builder.setMessage(sdf1.format(datum) + " " + sdf2.format(datum));
                    }
                }
            }

        } else {
            builder.setTitle("Neuspesno prijavljivanje!");
            builder.setMessage("Kod nije dobar.");
        }
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    private void getMax() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Evidencija");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idEvid = snapshot.getKey();
                    int idEvidInt = Integer.parseInt(idEvid);
                    Log.d("idEvidencije", String.valueOf(idEvidInt));
                    if (idEvidInt > idMax.get(0)) {
                        Log.d("USAO","true");
                        idMax.add(idEvidInt);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
private void vratiRec() {
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Code");
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mRec.clear();
            Log.d("AAAAAAAAAAAAAAAAAAA", (String) dataSnapshot.getValue());
            mRec.add((String) dataSnapshot.getValue());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
    private void vratiEvidenciju() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Evidencija");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        final String datum = sdf.format(date);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDanZaKorisnikas.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idEvid = snapshot.getKey();
                    String id = snapshot.child("id").getValue(String.class);
                    String datu = snapshot.child("datum").getValue(String.class);
                    String dol = snapshot.child("dolazak").getValue(String.class);
                    String odl = snapshot.child("odlazak").getValue(String.class);
                    DanZaKorisnika dan = new DanZaKorisnika(id, datu, dol,odl,idEvid);

                    if (dan.getId().equals(firebaseUser.getUid()) && dan.getDatum().equals(datum)) {
                        mDanZaKorisnikas.add(dan);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

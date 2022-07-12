package com.example.proyectoservicio;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private Medidor medidor;
    private LocalFile localFile;
    private Message message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = new Message(this);
        localFile = new LocalFile(Environment.getExternalStorageDirectory(),"/");
        medidor = localFile.loadMedidor();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void callWindowCamera(View view) {
        Intent intent = new Intent(this, CameraOpenCV.class);
        intent.putExtra("Medidor",medidor);
        startActivity(intent);
    }
    public void loadModel(View view){
        Database mDatabase = new Database(FirebaseDatabase.getInstance().getReference(), localFile,message);
        mDatabase.setMedidor(medidor);
        mDatabase.GetMedidorDataBase();
    }
}
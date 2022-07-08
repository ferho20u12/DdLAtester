package com.example.proyectoservicio;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Medidor medidor;
    private Toast toast;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = new Toast(this);
        medidor = new Medidor();
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            loadState();
        }
    }
    public void CallWindowCamara(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }else{
                Intent intent = new Intent(this, CamaraOpenCV.class);
                intent.putExtra("Medidor",medidor);
                startActivity(intent);
            }
        }
    }
    public void CargarModelo(View view){
        ObtenerMedidorDataBase();
    }
    private void ObtenerMedidorDataBase(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Medidor").child("MedidorDePrueba").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    ShowNewMessage("No se pudo conectar con la base de datos");
                }
                else {
                    medidor.setNombre("MedidorDePrueba");
                    medidor.setCantCirculos(Integer.parseInt(String.valueOf(task.getResult().child("CantidadCirculos").getValue())));
                    medidor.setNombreModelReloj(String.valueOf(task.getResult().child("nombreModelReloj").getValue()));
                    medidor.setNombreModelContraReloj(String.valueOf(task.getResult().child("nombreModelContraReloj").getValue()));
                    ObtencionClases();
                    CargarModeloReloj(medidor.getNombreModelReloj());
                    CargarModeloContraReloj(medidor.getNombreModelContraReloj());
                    ShowNewMessage("Datos Cargados");
                }
            }
        });
    }
    private void ObtencionClases(){
        List<Double>classes = new ArrayList<>();
        for(double i=0;i<10;i++){
            classes.add(i);
        }
        medidor.setClasses(classes);
    }
    private void CargarModeloReloj(String nombreModelo){
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel(nombreModelo, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            medidor.setModelFileReloj(modelFile);
                            saveState();
                            ShowNewMessage("Modelo actualizado");
                        }
                    }
                });
    }
    private void CargarModeloContraReloj(String nombreModelo){
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel(nombreModelo, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            medidor.setModelFileContraReloj(modelFile);
                            saveState();
                            ShowNewMessage("Modelo actualizado");
                        }
                    }
                });
    }

    private void ShowNewMessage(String str){
        toast.cancel();
        toast = Toast.makeText(this,str,Toast.LENGTH_LONG);
        toast.show();
    }

    //----------------------------------------------------
    private void saveState() {
        FileOutputStream outStream;
        try {
            File f = new File(Environment.getExternalStorageDirectory(), "/data.dat");
            outStream = new FileOutputStream(f);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(medidor);
            objectOutStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
    private void loadState()
    {
        FileInputStream inStream;
        try {
            File f = new File(Environment.getExternalStorageDirectory(), "/data.dat");
            inStream = new FileInputStream(f);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            medidor = (Medidor) objectInStream.readObject();
            objectInStream.close();
        } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }
    }
}
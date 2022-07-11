package com.example.proyectoservicio;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final DatabaseReference mDatabase ;
    private final LocalFile localFile;
    private final Message message;
    private Medidor medidor;
    public Database(DatabaseReference mDatabase, LocalFile localFile, Message message){
        this.mDatabase = mDatabase;
        this.localFile = localFile;
        this.message = message;
    }
    public Medidor getMedidor() {
        return medidor;
    }
    public void setMedidor(Medidor medidor) {
        this.medidor = medidor;
    }
    public void GetMedidorDataBase(){
        mDatabase.child("Medidor").child("MedidorDePrueba").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (medidor == null) {
                        medidor = new Medidor();
                    }
                    medidor.setNombre("MedidorDePrueba");
                    medidor.setCantCirculos(Integer.parseInt(String.valueOf(task.getResult().child("CantidadCirculos").getValue())));
                    medidor.setNombreModelReloj(String.valueOf(task.getResult().child("nombreModelReloj").getValue()));
                    medidor.setNombreModelContraReloj(String.valueOf(task.getResult().child("nombreModelContraReloj").getValue()));
                    List<Double> classes = new ArrayList<>();
                    for(double i=0;i<10;i++){
                        classes.add(i);
                    }
                    medidor.setClasses(classes);
                    CargarModeloReloj(medidor.getNombreModelReloj());
                    CargarModeloContraReloj(medidor.getNombreModelContraReloj());
                    message.ShowNewMessage("Datos Cargados");
                }
            }
        });
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
                            localFile.guardaMedidor(medidor);
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
                            localFile.guardaMedidor(medidor);
                        }
                    }
                });
    }
}

package com.example.proyectoservicio;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageCarousel carousel;
    private List<CarouselItem> list;
    private Medidor medidor;
    private List<String> urlsModel;
    private Toast toast;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carousel = findViewById(R.id.carousel);
        carousel.registerLifecycle(getLifecycle());
        list = new ArrayList<>();
        urlsModel= new ArrayList<>();
        toast = new Toast(this);
        medidor = new Medidor();

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        CargarSlider();
        CargarDatosMedidor();
        ObtencionClases();
    }
    public void CallWindowCamara(View view) {
        Intent intent = new Intent(this, CamaraOpenCV.class);
        intent.putExtra("Medidor",medidor);
        startActivity(intent);
    }
    private void ObtencionClases(){
        List<Double>classes = new ArrayList<>();
        for(double i=0.0;i<10.0;i+=.5){
            classes.add(i);
        }
        medidor.setClasses(classes);
    }
    private void CargarDatosMedidor() {
        CargarModeloReloj("westinghouse_d4sReloj");
        CargarModeloContraReloj("westinghouse_d4sContraReloj");
        medidor.setCantCirculos(5);
    }
    private void CargarModeloReloj(String nombreModelo){
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build();
        FirebaseModelDownloader.getInstance()
            .getModel(nombreModelo, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                @Override
                public void onSuccess(CustomModel model) {
                    File modelFile = model.getFile();
                    if(modelFile != null){
                        medidor.setPathModelReloj(modelFile.getPath());
                    }
                }
            });
    }
    private void CargarModeloContraReloj(String nombreModelo){
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel(nombreModelo, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        File modelFile = model.getFile();
                        if(modelFile != null){
                            medidor.setPathModelContraReloj(modelFile.getPath());
                        }
                    }
                });
    }
    private void CargarSlider(){
        //Lista donde se guardaran las cantidades de circulos que tiene cada tipo de medidor
        //------------------------ Aqui se carga el slider con las imagenes
        list.add(
                new CarouselItem(
                        R.drawable.medidorwestinghouse,
                        "Westinghouse"
                )
        );
        carousel.setData(list);

    }
    private void ShowNewMessage(String str){
        toast.cancel();
        toast = Toast.makeText(this,str,Toast.LENGTH_LONG);
        toast.show();
    }
}
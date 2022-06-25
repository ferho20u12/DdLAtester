package com.example.proyectoservicio;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageCarousel carousel;
    private List<CarouselItem> list;
    private List<Integer>cantCirculos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carousel = findViewById(R.id.carousel);
        carousel.registerLifecycle(getLifecycle());
        list = new ArrayList<>();
        cantCirculos = new ArrayList<>();
        CargarSlider();
    }
    public void CallWindowCamara(View view) {
        Intent intent = new Intent(this, CamaraOpenCV.class);
        intent.putExtra("cantCirculos", cantCirculos.get(carousel.getCurrentPosition()));
        startActivity(intent);
    }
    private void CargarSlider(){
        //Lista donde se guardaran las cantidades de circulos que tiene cada tipo de medidor
        cantCirculos.add(5);//Medidor 1 -Westinghouse
        cantCirculos.add(4);
        cantCirculos.add(3);
        //------------------------ Aqui se carga el slider con las imagenes
        list.add(
                new CarouselItem(
                        R.drawable.medidorwestinghouse,
                        "Westinghouse"
                )
        );
        list.add(
                new CarouselItem(
                        R.drawable.medidorwestinghouse,
                        "prueba 4 circulos"
                )
        );
        list.add(
                new CarouselItem(
                        R.drawable.medidorwestinghouse,
                        "prueba 3 circulos"
                )
        );
        carousel.setData(list);

    }
}
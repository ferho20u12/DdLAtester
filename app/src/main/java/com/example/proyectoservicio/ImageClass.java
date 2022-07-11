package com.example.proyectoservicio;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ImageClass extends AppCompatActivity {
    private List<Mat>mats;
    private List<Bitmap>bitmaps;
    private List<Double> outputs;
    private Medidor medidor;
    private int indexFlipper;
    private TextView textView;
    private ViewFlipper imageFlipper;
    private Message message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_class);
        imageFlipper = findViewById( R.id.image_flipper );
        textView = findViewById( R.id.textView);
        InicializacionVariables();
        if(!OpenCVLoader.initDebug()) {
            message.ShowNewMessage("Algo salio mal al cargar Open CV");
        }else{
            Obtencion_Imagenes();
            CargarSlider();
            IdentificarNumeros();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LimpiarVariables();
    }
    //------------------------------------------------nuevas funciones

    private void InicializacionVariables(){
        bitmaps = new ArrayList<>();
        mats = new ArrayList<>();
        indexFlipper =0;
        message = new Message(this);
        outputs = new ArrayList<>();
        medidor = (Medidor) getIntent().getSerializableExtra("Medidor");
    }

    //-----------------------------------------------------------------


    @Override
    protected void onResume() {
        super.onResume();
    }

    //---------------------------------------- Conversion

    private Bitmap Mat_to_Bitmap(Mat mat){
        Mat matAux = mat.clone();
        Bitmap bitmap = Bitmap.createBitmap(matAux.cols(), matAux.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matAux,bitmap);
        return bitmap;
    }
    //--------------------------------------------------Obtencion de numeros
    private  void Obtencion_Imagenes(){
        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null) {
            mats.add(new Mat(parametros.getLong("image_outputCrop")));
            for (int i = medidor.getCantCirculos() - 1; i >= 0; i--) {
                mats.add(new Mat(parametros.getLong("image_output" + i)));
            }
            if (mats.get(0) != null) {
                for (Mat mat : mats) {
                    bitmaps.add(Mat_to_Bitmap(mat));
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private void IdentificarNumeros(){
        textView.setText("");
        for(int i=1;i<bitmaps.size();i++){
            if(EsImpar(i))
                Interprete(bitmaps.get(i),medidor.getModelFileContraReloj());
            else
                Interprete(bitmaps.get(i),medidor.getModelFileReloj());
        }

        textView.setText(""+ outputs);
    }
    private boolean EsImpar(int num){
        return num % 2 == 0;
    }

    //---------------------------Slider
    private void CargarSlider(){
        for (Bitmap bmp:bitmaps) {
            ImageView image = new ImageView ( getApplicationContext() );
            image.setImageBitmap(bmp);
            imageFlipper.addView(image);
        }
    }
    public void CargarSiguiente(View view){
        if(indexFlipper < outputs.size()){
            imageFlipper.showNext();
            indexFlipper++;
            if(indexFlipper != 0){
                message.ShowNewMessage("Se leyo "+ outputs.get(indexFlipper -1));
            }

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LimpiarVariables();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void CargarPrevio(View view){
        if(indexFlipper >0){
            indexFlipper--;
            imageFlipper.showPrevious();
        }
    }
    //---------------------------Limpiar Variables
    private void LimpiarVariables(){
        for (Mat mat:mats)
        {
            mat.release();
        }
        mats.clear();
        bitmaps.clear();
        outputs.clear();
    }
    private void Interprete(Bitmap inputImage,File modelFile){
        if(modelFile == null){
            message.ShowNewMessage("Error archivo");
            return;
        }
        try (Interpreter interpreter = new Interpreter(modelFile)) {
            Bitmap bitmap = Bitmap.createScaledBitmap(inputImage, 224, 224, false);
            ByteBuffer input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder());
            for (int y = 0; y < 224; y++) {
                for (int x = 0; x < 224; x++) {
                    int px = bitmap.getPixel(x, y);

                    input.putFloat(((px >> 16) & 0xFF) * 1.f);
                    input.putFloat(((px >> 8) & 0xFF) * 1.f);
                    input.putFloat((px & 0xFF) * 1.f);

                }
            }
            int bufferSize = 10 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
            ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
            interpreter.run(input, modelOutput);
            modelOutput.rewind();
            FloatBuffer probabilities = modelOutput.asFloatBuffer();
            int mayor = 0;
            float probabilidadMayor= 0;
            for (int i = 0; i < probabilities.capacity(); i++) {
                if(probabilidadMayor < probabilities.get(i))
                {
                    mayor=i;
                    probabilidadMayor = probabilities.get(i);
                }
            }
            outputs.add(medidor.getClasses().get(mayor));
        }
    }
}
package com.example.proyectoservicio;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoservicio.ml.Model;
import com.example.proyectoservicio.ml.Model2;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ImageClass extends AppCompatActivity {
    private List<Mat>mats;
    private List<Bitmap>bitmaps;
    private List<Double>_resultados;
    private Medidor medidor;
    private int cont;
    private TextView textView;
    private ViewFlipper imageFlipper;
    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_class);
        imageFlipper = findViewById( R.id.image_flipper );
        textView = findViewById( R.id.textView);
        InicializacionVariables();
        if(!OpenCVLoader.initDebug()) {
            ShowNewMessage("Algo salio mal al cargar Open CV");
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


    private void classifyImage(Bitmap image)
    {
        try {
            int imageSize = 224;
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize,false);
            Model model = Model.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; ++i)
            {
                for (int j = 0; j < imageSize; ++j) {
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * 1.f);
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * 1.f);
                    byteBuffer.putFloat((val & 0xFF) * 1.f);
                }
            }
            inputFeature0.loadBuffer(byteBuffer);
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float [] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0 ; i < confidences.length ; ++i)
            {
                if (confidences[i] > maxConfidence)
                {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            _resultados.add(medidor.getClasses().get(maxPos));
            model.close();
        } catch (IOException e) {
            ShowNewMessage("No jala");
        }
    }
    private void classifyImage2(Bitmap image)
    {
        try {
            int imageSize = 224;
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize,false);
            Model2 model = Model2.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; ++i)
            {
                for (int j = 0; j < imageSize; ++j) {
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * 1.f);
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * 1.f);
                    byteBuffer.putFloat((val & 0xFF) * 1.f);
                }
            }
            inputFeature0.loadBuffer(byteBuffer);
            Model2.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float [] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0 ; i < confidences.length ; ++i)
            {
                if (confidences[i] > maxConfidence)
                {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            _resultados.add(medidor.getClasses().get(maxPos));
            model.close();
        } catch (IOException e) {
            ShowNewMessage("No Jala x2");
        }
    }


    //------------------------------------------------nuevas funciones

    private void InicializacionVariables(){
        bitmaps = new ArrayList<>();
        mats = new ArrayList<>();
        cont=0;
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        _resultados = new ArrayList<>();
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
    private void IdentificarNumeros(){
        textView.setText("");
        for(int i=1;i<bitmaps.size();i++){
            if(i%2==0){
                //classifyImage2(bitmaps.get(i));
                Interprete(bitmaps.get(i),medidor.getPathModelReloj());
            }
            else{
                //classifyImage(bitmaps.get(i));
                Interprete(bitmaps.get(i),medidor.getPathModelReloj());
            }
        }
        StringBuilder str = new StringBuilder();
        if(!ValidarLecturas(str))
            ShowNewMessage("Posible medidor descalibrado");
        str.append(" Kw");
        textView.setText(str.toString());
    }
    private double SinDecimal(double num){
        int entero = (int) num;
        return (double) entero;
    }
    private boolean ValidarLecturas(StringBuilder str){
        //Revision para saber si esta calibrado el medidor(no es tan preciso)
        boolean band  = true;
        int sinpuntoflotante = (int) SinDecimal(_resultados.get(0));
        str.append("").append(sinpuntoflotante);
        for(int i=1;i<_resultados.size();i++){
            double num     = _resultados.get(i);
            double numPast = SinDecimal(_resultados.get(i-1));
            boolean espuntocinco = (num - SinDecimal(num)) == .5;
            //------------La condicional primera revisa si el numero debe ser .5 o debe ser entero
            if(numPast == 8.0 ||numPast == 9.0||numPast == 0.0||numPast == 1.0||numPast == 2.0){
                if(espuntocinco){
                    // si en dado caso este no este calibrado el medidor , se tratara de corregir el error
                    // y se dara aviso
                    _resultados.set(i,SinDecimal(num)+1);
                    band=false;
                }
            }else{
                if(!espuntocinco){
                    _resultados.set(i,SinDecimal(num)+1);
                    band=false;
                }
            }
            sinpuntoflotante = (int) SinDecimal(_resultados.get(i));
            str.append("").append(sinpuntoflotante);
        }
        return band;
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
        if(cont<_resultados.size()){
            imageFlipper.showNext();
            cont++;
            if(cont != 0){
                ShowNewMessage("Se leyo "+_resultados.get(cont-1));
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
        if(cont>0){
            cont--;
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
        _resultados.clear();
    }
    private void ShowNewMessage(String str){
        toast.cancel();
        toast = Toast.makeText(this,str,Toast.LENGTH_LONG);
        toast.show();
    }
    private void Interprete(Bitmap image,String path){
        try (Interpreter interpreter = new Interpreter(new File(path))) {
            Bitmap bitmap = Bitmap.createScaledBitmap(image, 224, 224, true);
            ByteBuffer input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder());
            for (int y = 0; y < 224; y++) {
                for (int x = 0; x < 224; x++) {
                    int px = bitmap.getPixel(x, y);
                    // Get channel values from the pixel value.
                    int r = Color.red(px);
                    int g = Color.green(px);
                    int b = Color.blue(px);
                    // Normalize channel values to [-1.0, 1.0]. This requirement depends
                    // on the model. For example, some models might require values to be
                    // normalized to the range [0.0, 1.0] instead.
                    float rf = (r - 127) / 255.0f;
                    float gf = (g - 127) / 255.0f;
                    float bf = (b - 127) / 255.0f;
                    input.putFloat(rf);
                    input.putFloat(gf);
                    input.putFloat(bf);
                }
            }
            int bufferSize = 1000 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
            ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
            interpreter.run(input, modelOutput);
            modelOutput.rewind();
            FloatBuffer probabilities = modelOutput.asFloatBuffer();
            float probability = probabilities.get(0);
            int mayor = 0;
            for (int i = 0; i < probabilities.capacity(); i++) {
                if(probability > probabilities.get(i)){
                    probability = probabilities.get(i);
                    mayor = i;
                }
            }
            ShowNewMessage("Interprete:  "+medidor.getClasses().get(mayor));
            _resultados.add(medidor.getClasses().get(mayor));

        }
    }
}
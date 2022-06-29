package com.example.proyectoservicio;


import android.content.Intent;
import android.graphics.Bitmap;
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
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    private void ObtencionClases(){
        List<Double>classes = new ArrayList<>();
        for(double i=0.0;i<10.0;i+=.5){
            classes.add(i);
        }
        medidor.setClasses(classes);
    }
    private void InicializacionVariables(){
        bitmaps = new ArrayList<>();
        mats = new ArrayList<>();
        cont=0;
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        _resultados = new ArrayList<>();
        medidor = new Medidor();
        ObtencionClases();
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
            int contCirculos = parametros.getInt("cantCirculos");
            for (int i = contCirculos - 1; i >= 0; i--) {
                mats.add(new Mat(parametros.getLong("image_output" + i)));
            }
            if (mats.get(0) != null) {
                for (Mat mat : mats) {
                    bitmaps.add(Mat_to_Bitmap(mat));
                }
            } else {
                ShowNewMessage("Error no existe imagen de origen");
            }
        }
        else
            ShowNewMessage("Error al cargar Bunble");
    }
    private void IdentificarNumeros(){
        textView.setText("");
        for(int i=1;i<bitmaps.size();i++){
            if(i%2==0){
                classifyImage2(bitmaps.get(i));
            }
            else{
                classifyImage(bitmaps.get(i));
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
        str.append(""+sinpuntoflotante);
        for(int i=1;i<_resultados.size();i++){
            double num     = _resultados.get(i);
            double numPast = SinDecimal(_resultados.get(i-1));
            boolean espuntocinco = (num - SinDecimal(num)) == .5? true : false;
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
            str.append(""+sinpuntoflotante);
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
        toast = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        toast.show();
    }
}
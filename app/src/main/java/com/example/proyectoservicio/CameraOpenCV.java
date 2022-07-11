package com.example.proyectoservicio;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class CameraOpenCV extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private Intent intent;
    private Mat mRgba,mRgba2,mGray;
    private  int heightScreen,widthScreen,side;
    private List<Circle> circles;
    private Message message;
    private Medidor medidor;
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    Mat hierarchy;
    //----------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_open_cv);
        intent = new Intent(CameraOpenCV.this, ImageClass.class);
        ActivityCompat.requestPermissions(CameraOpenCV.this, new String[]{Manifest.permission.CAMERA}, 1);
        cameraBridgeViewBase = findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        message = new Message(this);
        circles = new ArrayList<>();

        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            medidor = (Medidor) getIntent().getSerializableExtra("Medidor");
            baseLoaderCallback = new BaseLoaderCallback(this){
                @Override
                public void onManagerConnected(int status) {
                    super.onManagerConnected(status);
                    if (status == BaseLoaderCallback.SUCCESS) {
                        cameraBridgeViewBase.enableView();
                    } else {
                        super.onManagerConnected(status);
                    }
                }
            };
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height,width, CvType.CV_8SC4);
        mRgba2 = new Mat(height,width, CvType.CV_8SC4);
        mGray = new Mat(height,width,CvType.CV_8UC1);
        hierarchy  = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mRgba2.release();
        mGray.release();
    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        CorregirCamara(mRgba);
        CorregirCamara(mGray);
        mRgba2 = mRgba.clone();
        heightScreen= mRgba.height();
        widthScreen = mRgba.width();
        side = (heightScreen/5)-1;
        Imgproc.rectangle(mRgba,new Point(0,(double) heightScreen/4.0),new Point(widthScreen, heightScreen / 4.0 + side + 20.0),new Scalar(0,255,0,1),3);
        Imgproc.line(mRgba,new Point(1, (heightScreen/4.0) +(side/2.0)),new Point(widthScreen-2.0,(double) (heightScreen/4)+(side/2.0)),new Scalar(255,0,0),1);
        return mRgba;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug()){
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }


    // Funciones propias
    private void CorregirCamara(Mat mat){
        Core.transpose(mat, mat);
        Core.flip(mat, mat, 1);
    }
    private Mat RecortarRectangulo(Mat mat,Point coordenadas,Point rectangulo ){
        Rect rectCrop;
        Mat copy = mat.clone();
        double maxWidth = mat.width(), maxHeight = mat.height();
        boolean band = true;
        while (band){
            band = false;
            if(rectangulo.x >= maxWidth){
                rectangulo.x--;
                band = true;
            }
            if(rectangulo.y >= maxHeight){
                rectangulo.y--;
                band = true;
            }
        }
        rectCrop = new Rect(coordenadas,rectangulo);
        return copy.submat(rectCrop);
    }
    private void DeteccionDeCiculos(Mat mat){
        Mat input = mat.clone();
        Mat circles = new Mat();
        Imgproc.blur(input, input, new Size(7, 7), new Point(2, 2));
        Imgproc.HoughCircles(input, circles, Imgproc.CV_HOUGH_GRADIENT, 2, 100, 100, 90,20,mat.height());
        if (circles.cols() > 0){
            for (int x=0; x < Math.min(circles.cols(), 5); x++ ) {
                double[] circleVec = circles.get(0, x);
                if (circleVec == null) {
                    break;
                }
                Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                int radius = (int) circleVec[2];
                //Imgproc.circle(input, center, radius, new Scalar(255, 255, 255), 2);
                Circle circle = new Circle(center,radius);
                this.circles.add(circle);
            }
        }
    }
    private void BurbujaMejorada(){
        Circle AUX;
        int j;
        int bandera=1;
        for(int i = 0; i<(circles.size()-1)&&bandera==1; i++)
        {
            bandera=0;
            for(j=0; j< circles.size()-i-1; j++)
            {
                if(circles.get(j).getCenter().x> circles.get(j+1).getCenter().x)
                {
                    bandera=1;
                    AUX= new Circle(circles.get(j).getCenter(), circles.get(j).getRadius());
                    circles.set(j, circles.get(j+1));
                    circles.set(j+1,AUX);
                }
            }
        }
    }
    public void CallWindowImageDetection(View view) {
        Mat image_outputCrop;
        circles.clear();
        image_outputCrop = RecortarRectangulo(mGray,new Point(0,(double) heightScreen/4),new Point(widthScreen,(double) (heightScreen/4+side)+20));
        DeteccionDeCiculos(image_outputCrop);
        if(circles.size() == medidor.getCantCirculos() ){
            BurbujaMejorada();
            List<Mat>mats =new ArrayList<>();
            for(Circle circle : circles){
                double x,y;
                int radius;
                radius = circle.getRadius();
                x= circle.getCenter().x-radius;
                y= circle.getCenter().y-radius;
                Mat mat = RecortarRectangulo(image_outputCrop,new Point(x,y),new Point(x+(radius*2),y+(radius*2)));
                mats.add(mat);
            }
            intent.putExtra( "image_outputCrop",image_outputCrop.nativeObj);
            intent.putExtra("Medidor",medidor);
            if(!mats.isEmpty()){
                for(int i = 0; i<mats.size();i++){
                    intent.putExtra( "image_output"+i,mats.get(i).nativeObj);
                }
            }
            startActivity(intent);
        }
        else{
            message.ShowNewMessage("No se detectaron todos los circles, vuelva intentarlo "+ circles.size());
        }
    }

}
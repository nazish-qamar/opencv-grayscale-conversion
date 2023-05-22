package com.example.integrateopencv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button select,camera;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;
    int SELECT_CODE=100;
    int CAMERA_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug())
            Log.d("LOADED", "success");
        else
            Log.d("LOADED", "error");


        //initialize variables
        camera=findViewById(R.id.camera);
        select=findViewById(R.id.select);
        imageView=findViewById(R.id.imageView);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //to open new window to show images on the device
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                //intent to open the tyep of images
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_CODE);
            }
        });

        //selecting image from camera
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ask for camera permission from the user
                getPermissions();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_CODE); //need to provide camera permission in AndroidManifest.xml as well
            }
        });
    }

    private void getPermissions() {
        if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},102);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==102 && grantResults.length>0){
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                getPermissions();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_CODE && data!=null) {
            try {
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData()); //to get the URI
                //to check iamge has been selected successfully
                imageView.setImageBitmap(bitmap);
                //convert bitmap to mat
                mat=new Mat();
                Utils.bitmapToMat(bitmap,mat);

                //convert to grayscale
                Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
                Utils.matToBitmap(mat,bitmap);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(requestCode==CAMERA_CODE && data!=null){
            bitmap= (Bitmap) data.getExtras().get("data");
            //to check iamge has been selected successfully
            imageView.setImageBitmap(bitmap);
            //convert bitmap to mat
            mat=new Mat();
            Utils.bitmapToMat(bitmap,mat);
            //convert to grayscale
            Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
            Utils.matToBitmap(mat,bitmap);
            imageView.setImageBitmap(bitmap);
        }


    }
}
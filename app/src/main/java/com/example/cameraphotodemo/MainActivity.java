package com.example.cameraphotodemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static int PHOTO_REQUEST_CAMERA = 10;//相机权限请求
    private final static int PHOTO_REQUEST_ALBUM = 20;//相册权限请求
    private final static int CAMERA_REQUEST_CODE = 100;//相机跳转code
    private final static int ALBUM_REQUEST_CODE = 200;//相册跳转code
    private final static int TAILOR_REQUEST_CODE = 300;//图片剪裁code
    private ImageView image;
    private TextView tvCamera, tvAlbum;
    private Context context = MainActivity.this;
    private Activity activity = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        image = findViewById(R.id.image);
        tvCamera = findViewById(R.id.tv_camera);
        tvAlbum = findViewById(R.id.tv_album);

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相机
                if ((ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)) {
                    //权限都齐的情况下，跳转相机
                    openCamera();
                } else {
                    if (activity != null) {
                        //请求权限
                        ActivityCompat.requestPermissions(activity, new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, PHOTO_REQUEST_CAMERA);
                    }
                }
            }
        });

        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相册
                if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)) {
                    //权限都齐的情况下，跳转相册
                    openAlbum();
                } else {
                    if (activity != null) {
                        //请求权限
                        ActivityCompat.requestPermissions(activity, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, PHOTO_REQUEST_ALBUM);
                    }
                }
            }
        });
    }

    /**
     * 跳转相机
     */
    private void openCamera(){

    }

    /**
     * 跳转相册
     */
    private void openAlbum(){

    }

    /**
     * 跳转裁剪
     */
    private void openCrop(){

    }

    /**
     * 权限申请回调
     * @param requestCode requestCode
     * @param permissions permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PHOTO_REQUEST_CAMERA:
                //相机权限请求回调
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        //跳转相机
                        openCamera();
                    } else {
                        //无权限提示
                        Toast.makeText(context, "权限未通过", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case PHOTO_REQUEST_ALBUM:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        //跳转相册
                        openAlbum();
                    } else {
                        //无权限提示
                        Toast.makeText(context, "权限未通过", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
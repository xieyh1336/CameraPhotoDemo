package com.example.cameraphotodemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cameraphotodemo.util.FileUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private final static int PHOTO_REQUEST_CAMERA = 10;//相机权限请求
    private final static int PHOTO_REQUEST_ALBUM = 20;//相册权限请求
    private final static int CAMERA_REQUEST_CODE = 100;//相机跳转code
    private final static int ALBUM_REQUEST_CODE = 200;//相册跳转code
    private final static int TAILOR_REQUEST_CODE = 300;//图片剪裁code
    private final static String SAVE_AVATAR_NAME = "head.png";//需要上传的图片的文件名
    private Uri imageUri;//需要上传的图片的Uri
    private File file;//需要上传的图片的文件
    private ImageView image;
    private TextView tvCamera, tvAlbum;
    private Context context = MainActivity.this;
    private Activity activity = MainActivity.this;
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否有相机
        if (activity != null && context != null && intent.resolveActivity(activity.getPackageManager()) != null){
            File file;
            Uri uri = null;
            if (isAndroidQ){
                //适配Android10
                uri = createImageUri(context);
            } else {
                //Android10以下
                file = createImageFile(context);
                if (file != null){
                    //Android10以下
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        //适配Android7.0文件权限
                        uri = FileProvider.getUriForFile(context, "com.example.camera.test", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                }
            }
            imageUri = uri;
            Log.e(TAG, "相机保存的图片Uri：" + imageUri);
            if (uri != null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * Android10创建图片uri，用来保存拍照后的图片
     * @return uri
     */
    private Uri createImageUri(@NonNull Context context){
        String status = Environment.getExternalStorageState();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, SAVE_AVATAR_NAME);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/0/");
        //判断是否有SD卡
        if (status.equals(Environment.MEDIA_MOUNTED)){
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            return context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, contentValues);
        }
    }

    /**
     * Android10以下创建图片file，用来保存拍照后的照片
     * @return file
     */
    private File createImageFile(@NonNull Context context){
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null && !file.exists()){
            if (file.mkdir()){
                Log.e(TAG, "文件夹创建成功");
            } else {
                Log.e(TAG, "file为空或者文件夹创建失败");
            }
        }
        File tempFile = new File(file, SAVE_AVATAR_NAME);
        Log.e(TAG, "临时文件路径：" + tempFile.getAbsolutePath());
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))){
            return null;
        }
        return tempFile;
    }

    /**
     * 跳转相册
     */
    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    /**
     * 跳转裁剪
     */
    private void openCrop(Uri uri){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && context != null){
            file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/0"), SAVE_AVATAR_NAME);
            Log.e(TAG, "裁剪图片存放路径：" + file.getAbsolutePath());
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        //适配Android10，存放图片路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        // 图片格式
        intent.putExtra("outputFormat", "PNG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, TAILOR_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1){
            //回调成功
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    //相机回调
                    Log.e(TAG, "相机回调");
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        //照片裁剪
                        openCrop(imageUri);
                    } else {
                        Toast.makeText(context, "未找到存储卡", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ALBUM_REQUEST_CODE:
                    //相册回调
                    Log.e(TAG, "相册回调");
                    if (data != null && data.getData() != null) {
                        image.setImageURI(data.getData());
                        //如果需要上传操作的可以使用这个方法
                        File file = FileUtils.uriToFile(data.getData(), context);
                        //这里的file就是需要上传的图片了
                    }
                    break;
                case TAILOR_REQUEST_CODE:
                    //图片剪裁回调
                    Log.e(TAG, "图片剪裁回调");
//                    Glide.with(context).load(file).into(image);
                    Uri uri = Uri.fromFile(file);
                    image.setImageURI(uri);
                    //如果需要上传全局的这个file就是需要上传的图片了
                    File file = this.file;
                    break;
            }
        } else {
            Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
        }
    }
}
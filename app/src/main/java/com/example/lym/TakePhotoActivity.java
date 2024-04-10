package com.example.lym;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
//import androidx.core.executor.NiceExecutors;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class TakePhotoActivity extends AppCompatActivity {

    private static final String TAG = "TakePhotoActivity";

    private boolean isFrontFacing = false;  // Flag to track camera orientation

    private PreviewView  pvCamera;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result){
                startCamera(cameraFacing);
            }
        }
    });
    // ImageButton references (as before)
    private ImageButton ibProfileTakePhoto;
    private TextView tvListFriendTakePhoto;
//    private ImageView ivTakePhoto;

    private ImageButton ibTakePhoto;
    private ImageButton ibChangeCamera;
    static final int REQUEST_CODE_CAMERA =1;

    private Camera camera;
    FirebaseAuth mAuthencation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

        Log.d("UserCount", firebaseUser.getUid());

        ibProfileTakePhoto = (ImageButton) findViewById(R.id.ibProfileTakePhoto);
        DatabaseReference avatarUrlRef = usersRef.child("avatar");
        Log.d("UserCount","aaaa1");
        avatarUrlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("UserCount","aaaa2");
                String avatarUrl = snapshot.getValue(String.class);
                Log.d("UserCount", avatarUrl);
                Log.d("UserCount","aaaa3");
                Picasso.get().load(avatarUrl).into(ibProfileTakePhoto);
                ibProfileTakePhoto.setClipToOutline(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        ibTakePhoto = (ImageButton) findViewById(R.id.ibTakePhoto);
        ibChangeCamera = (ImageButton) findViewById(R.id.ibChangeCamera);
        tvListFriendTakePhoto = (TextView) findViewById(R.id.tvListFriendTakePhoto);
        //ivTakePhoto = (ImageView) findViewById(R.id.ivTakePhoto);
        pvCamera = (PreviewView) findViewById(R.id.pvCamera);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        }else {
            startCamera(cameraFacing);
        }

        ibChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraFacing == CameraSelector.LENS_FACING_BACK){
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                }else {
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                }
                startCamera(cameraFacing);
            }
        });

        ibProfileTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakePhotoActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        tvListFriendTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakePhotoActivity.this, AddDeleteFriend.class);
                startActivity(intent);
            }
        });
    }

    public void startCamera(int cameraFacing){
        int aspectRatio = aspectRatio(pvCamera.getWidth(), pvCamera.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);
        listenableFuture.addListener(() ->{
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetRotation(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(aspectRatio) // Use AspectRatio for rotation
                        .build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();
                cameraProvider.unbindAll();

                androidx.camera.core.Camera camera1 = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                ibTakePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(TakePhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            Toast.makeText(TakePhotoActivity.this, "photo...", Toast.LENGTH_SHORT).show();
                        }
                            Toast.makeText(TakePhotoActivity.this, "Capturing photo...", Toast.LENGTH_SHORT).show();
                            takePicture(imageCapture);

                    }
                });
                preview.setSurfaceProvider(pvCamera.getSurfaceProvider());
            }catch (ExecutionException | InterruptedException e){
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(ImageCapture imageCapture){
        final File file = new File(getExternalFilesDir(null),System.currentTimeMillis()+".png");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TakePhotoActivity.this, "image saved at: "+ file.getPath(), Toast.LENGTH_SHORT).show();
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] bytes = baos.toByteArray();
                        Intent intent = new Intent(TakePhotoActivity.this, PostImageActivity.class);
                        intent.putExtra("image", bytes);
                        intent.putExtra("file", file);
                        startActivity(intent);
                    }
                });
                //startCamera(cameraFacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TakePhotoActivity.this,"Faile to saved "+ exception.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
                startCamera(cameraFacing);
            }
        });
    }
    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width,height)/Math.min(width,height);
        if (Math.abs(previewRatio-4.0/3.0)<= Math.abs(previewRatio-16.0/9.0)){
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ibTakePhoto.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}



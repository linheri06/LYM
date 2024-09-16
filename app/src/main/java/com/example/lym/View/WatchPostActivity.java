
package com.example.lym.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.lym.Model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.example.lym.R;

import java.io.IOException;
import java.io.OutputStream;

public class WatchPostActivity extends AppCompatActivity {

    ImageButton iBAvatar;


    ImageView iVPost;

    TextView tVNote;

    ImageView iVAvatar;

    ImageButton btnTakePhoto;

    ImageButton btnImageClassification;

    ImageButton btnSave;

    FirebaseAuth mAuthencation;
    TextView edUserImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_post);
        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();

        iBAvatar = (ImageButton) findViewById(R.id.iBAvatar);
            setAvataCurrent(firebaseUser);
        iVPost = (ImageView) findViewById(R.id.iVPost);
        tVNote = (TextView) findViewById(R.id.tVNote);
        iVAvatar = (ImageView) findViewById(R.id.iVAvatar);
        btnTakePhoto = (ImageButton) findViewById(R.id.btnTakePhoto);
        btnImageClassification = (ImageButton) findViewById(R.id.btnImageClassification);
        btnSave = (ImageButton) findViewById(R.id.btnSaveWatch);
        edUserImage = (TextView) findViewById(R.id.edUserImage);

        String imageUrl = getIntent().getStringExtra("url");

        // Sử dụng Picasso để tải ảnh từ URL vào ImageView
        Picasso.get().load(imageUrl).into(iVPost);

        String idImage = getIntent().getStringExtra("idImage");
        getUserIdFromImageUrl(idImage);

        iBAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(WatchPostActivity.this, ProfileActivity.class);
                startActivity(myintent);
            }

        });

        btnImageClassification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(WatchPostActivity.this, ImageClassification.class);
                startActivity(myintent);

            }
        });
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(WatchPostActivity.this, TakePhotoActivity.class);
                startActivity(myintent);

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the bitmap from the ImageView
                Bitmap bitmap = ((BitmapDrawable) iVPost.getDrawable()).getBitmap();

                // Construct a MediaStore content values object with image metadata
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Image From My App");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Description of the image");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png"); // Assuming PNG format

                // Insert the image into the MediaStore
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Adjust compression as needed
                    Toast.makeText(WatchPostActivity.this, "Image saved to gallery!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(WatchPostActivity.this, "Failed to save image!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    private void setAvataCurrent(FirebaseUser firebaseUser) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        DatabaseReference avatarUrlRef = usersRef.child("avatar");
        //Log.d("UserCount","aaaa1");
        avatarUrlRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatarUrl = snapshot.getValue(String.class);
                Picasso.get().load(avatarUrl).into(iBAvatar);
                iBAvatar.setClipToOutline(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }
    static String userIdImage;
    private void getUserIdFromImageUrl(String idImage) {
        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference().child("images");
        Log.d("UserCount","Cần tìm:"+ idImage);
        //String user;

        imagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // Lấy userId
                    Log.d("UserCount","userid:"+ userId);
                    for (DataSnapshot keySnapshot : userSnapshot.getChildren()) {
                        String key = keySnapshot.getKey(); // Lấy key
                        Log.d("UserCount", "key anh"+ key);
                        if (key.equals(idImage)) {
                            // Tìm thấy key, userId chứa key này
                            Log.d("UserCount", "Tim:"+userId);
                            userIdImage = userId;
                            LoadData(userId,idImage);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

    }

    private void LoadData(String userId, String idImage) {

        Log.d("UserCount", "Timdc:"+userId);

        //do avatar user cua anh
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        DatabaseReference avatarUrlRef = usersRef.child("avatar");
        //Log.d("UserCount","aaaa1");
        avatarUrlRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatarUrl = snapshot.getValue(String.class);
                Picasso.get().load(avatarUrl).into(iVAvatar);
                iVAvatar.setClipToOutline(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });

        //do ten user anh
        DatabaseReference nameUrlRef = usersRef.child("name");
        nameUrlRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                edUserImage.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });

        //Lấy anh
        DatabaseReference imageInfo = FirebaseDatabase.getInstance().getReference().child("images").child(userIdImage).child(idImage);

        imageInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem liệu có dữ liệu hay không
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu từ dataSnapshot và đặt vào đối tượng Image
                    Image image = dataSnapshot.getValue(Image.class);
                    tVNote.setText(image.cap);
                } else {
                    // Xử lý trường hợp không có dữ liệu
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }
}
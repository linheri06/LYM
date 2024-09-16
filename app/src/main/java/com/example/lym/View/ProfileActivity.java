package com.example.lym.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lym.Model.Image;
import com.example.lym.R;
import com.example.lym.View.AddDeleteFriend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import android.content.pm.PackageManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivAvatar;

    Button btnListFriend;
    TextView tvOverview;
    TextView tvDangerous;

    Button btnChangeName;
    Button btnChangeBir;
    Button btnChangePass;
    Button btnDeleteUser;
    Button btnLogout;
    FirebaseAuth mAuthencation;

    ImageButton ibBackTakePhoto;
    private static final int PICK_IMAGE_REQUEST=1;
    private static final int REQUEST_CODE_READ_STORAGE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();

        ivAvatar = (ImageView) findViewById(R.id.ivAvata_profile);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        DatabaseReference avatarUrlRef = usersRef.child("avatar");
        Log.d("UserCount","aaaa1");
        avatarUrlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatarUrl = snapshot.getValue(String.class);

                Log.d("UserCount", avatarUrl);
                Picasso.get().load(avatarUrl).into(ivAvatar);
                ivAvatar.setClipToOutline(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        btnListFriend = (Button) findViewById(R.id.btnListFriend);
        btnChangeName = (Button) findViewById(R.id.btnChangeName);
        btnChangeBir = (Button) findViewById(R.id.btnChangeBir);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        btnDeleteUser = (Button) findViewById(R.id.btnDeleteUser);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        tvDangerous = (TextView) findViewById(R.id.tvDangerous);
        ibBackTakePhoto = (ImageButton) findViewById(R.id.ibBackTakePhotoPro);

        ibBackTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAvtivity(TakePhotoActivity.class);
            }
        });


        btnListFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAvtivity( AddDeleteFriend.class);
            }
        });
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAvtivity( ChangeName.class);
            }
        });

        btnChangeBir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAvtivity( ChangeBir.class);
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAvtivity( ChangePass.class);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                goToAvtivity(LoginActivity.class);
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(firebaseUser);
            }
        });

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvarta();
            }
        });

    }

    private void changeAvarta() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // Lấy Uri của ảnh được chọn
            Uri imageUri = data.getData();
            ivAvatar.setImageURI(imageUri);

            //Lưu lên firebase
            ivAvatar.setDrawingCacheEnabled(true);
            ivAvatar.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) ivAvatar.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data1 = baos.toByteArray();

            FirebaseStorage storage = FirebaseStorage.getInstance("gs://lymandroi.appspot.com");
            StorageReference storageRef = storage.getReference();
            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageRef.child("image"+calendar.getTimeInMillis()+"png");

            // ... mã tải lên của bạn (ví dụ: putBytes)

            mountainsRef.putBytes(data1)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Xử lý tải lên không thành công
                            Toast.makeText(ProfileActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Lấy URL tải xuống sau khi tải lên thành công
                            //Toast.makeText(PostImageActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    mAuthencation = FirebaseAuth.getInstance();
                                    FirebaseUser firebaseUser = mAuthencation.getCurrentUser();
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());

                                    usersRef.child("avatar").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProfileActivity.this, "successfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(ProfileActivity.this,TakePhotoActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(ProfileActivity.this, "User registered failed", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
        }
    }


    private void showConfirmationDialog(FirebaseUser firebaseUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa tài khoản");
        builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản này?");
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(firebaseUser);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        usersRef.removeValue();

        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference().child("friends").child(firebaseUser.getUid());
        friendsRef.removeValue();

        DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference().child("images").child(firebaseUser.getUid());
        imageRef.removeValue();



        firebaseUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Bạn đã xóa tài khoản!", Toast.LENGTH_LONG).show();
                            goToAvtivity(LoginActivity.class);
                        }
                    }
                });

    }

    private void goToAvtivity(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

}
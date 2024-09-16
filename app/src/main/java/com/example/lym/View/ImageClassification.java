package com.example.lym.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.lym.Adapter_friend;
import com.example.lym.Adapter_friend_spinner;
import com.example.lym.ImageAdapter;
import com.example.lym.ImageClassificationAdapter;
import com.example.lym.Model.Image;
import com.example.lym.Model.User;
import com.example.lym.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageClassification extends AppCompatActivity {
    ImageButton ibtnImage_classify;
    Button btnChat_classify;
    ImageButton btnTakePhoto_classify;
    Spinner spinnerClassify;
    RecyclerView revImage_classify;
    ImageClassificationAdapter imageClassificationAdapter;
    FirebaseAuth mAuthencation;
    List<User> friend_list = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classification);
        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();

        ibtnImage_classify = (ImageButton) findViewById(R.id.ibtnImage_classify);
            setAvataCurrent(firebaseUser);
        btnTakePhoto_classify = (ImageButton) findViewById(R.id.btnTakePhoto_classify);
        spinnerClassify = (Spinner) findViewById(R.id.spinnerClassify);
        revImage_classify = (RecyclerView) findViewById(R.id.revImage_classify);
        revImage_classify.setLayoutManager(new GridLayoutManager(ImageClassification.this, 3));
        revImage_classify.setAdapter(imageClassificationAdapter);

        LoadData();

        spinnerClassify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy người dùng được chọn từ Adapter của Spinner
                User selectedUser = (User) parent.getItemAtPosition(position);
                if (selectedUser != null) {
                    String selectedUserId = selectedUser.email; // Thay "getUserId()" bằng phương thức lấy ra userID của người dùng từ đối tượng User của bạn
                    Log.d("SelectedUserId", selectedUserId);
                    LoadImage(selectedUserId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý khi không có mục nào được chọn
            }
        });

        ibtnImage_classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(ImageClassification.this, ProfileActivity.class);
                startActivity(myintent);

            }
        });
        btnTakePhoto_classify.setOnClickListener(v -> {
            Intent intent = new Intent(this, TakePhotoActivity.class);
            startActivity(intent);
        });
    }

    private void LoadImage(String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Duyệt qua kết quả trả về từ truy vấn
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy user ID từ dataSnapshot.getKey()
                    String userId = snapshot.getKey();

                    DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference().child("images").child(userId);
                    imagesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Image> imageList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Image image = snapshot.getValue(Image.class);
                                if (image != null) {
                                    imageList.add(image);
                                }
                            }

                            // Khởi tạo Adapter và đặt Adapter cho RecyclerView
                            ImageAdapter adapter = new ImageAdapter(imageList, new ImageAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String url, long idImage) {
                                    // Xử lý khi người dùng nhấp vào một mục trong RecyclerView
                                    // Ví dụ: Chuyển sang Activity mới và truyền ID của ảnh
                                    Intent intent = new Intent(getApplicationContext(), WatchPostActivity.class);
                                    intent.putExtra("url", url);
                                    String id = Long.toString(idImage);
                                    intent.putExtra("idImage", id);
                                    Log.d("UserCount", url);

                                    startActivity(intent);
                                }
                            });
                            revImage_classify.setAdapter(adapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Xử lý lỗi nếu có
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });


    }

    private void LoadData() {
        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("friends").child(firebaseUser.getUid());

        Adapter_friend_spinner adapter = new Adapter_friend_spinner(ImageClassification.this, friend_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        DatabaseReference me = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        me.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User friendUser = userSnapshot.getValue(User.class);
                if (friendUser != null) { // Check if data is retrieved successfully
                    friend_list.add(friendUser);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String friendUserId = friendSnapshot.getValue(String.class); // Get friend's user ID
                    Log.d("UserCount", friendUserId);
                    // Create a reference to the friend's data in the "users" node
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(friendUserId);

                    // Add a listener to retrieve friend's data
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            Log.d("UserCount", "aaaaa123");
                            User friendUser = userSnapshot.getValue(User.class);
                            if (friendUser != null) { // Check if data is retrieved successfully
                                friend_list.add(friendUser);
                                Log.d("UserCount", friend_list.get(0).name); // Log email for debugging
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle errors
                        }
                    });

                }
                spinnerClassify.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                Log.d("UserCount", avatarUrl);
                Picasso.get().load(avatarUrl).into(ibtnImage_classify);
                ibtnImage_classify.setClipToOutline(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }
}
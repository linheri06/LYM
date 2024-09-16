package com.example.lym.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.lym.Adapter_friend;
import com.example.lym.Model.User;
import com.example.lym.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddDeleteFriend extends AppCompatActivity {

    ImageButton btnComeBack;
    ImageButton btnSearch;
    ImageButton ibBackTakePhoto;
    TextView tvNumberFried;
    EditText edtSearch;
    TextView tvFriedList;
    RecyclerView revFriendList;
    FirebaseAuth mAuthencation;
    Adapter_friend myAdapter;
    ArrayList<User> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delete_friend);
        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();

        btnComeBack = (ImageButton) findViewById(R.id.btnComeBack);
            setAvataCurrent(firebaseUser);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        tvNumberFried = (TextView) findViewById(R.id.tvNumberFried);
            setTvListFriendTakePhoto(firebaseUser);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        tvFriedList = (TextView) findViewById(R.id.tvFriedList);
        revFriendList = findViewById(R.id.revFriendList);
        ibBackTakePhoto = (ImageButton) findViewById(R.id.ibBackTakePhoto);

        LoadData();
        btnComeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(AddDeleteFriend.this, ProfileActivity.class);
                startActivity(myintent);

            }
        });

        ibBackTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(AddDeleteFriend.this, TakePhotoActivity.class);
                startActivity(myintent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriend();
            }
        });
    }

    private void AddFriend() {
        String email = edtSearch.getText().toString();
        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

// Tạo truy vấn tìm kiếm UserID dựa trên Email
        Query query = usersRef.orderByChild("email").equalTo(email);

// Lắng nghe kết quả truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua các kết quả
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // Lấy UserID
                    // ... (Xử lý UserID)
                    Log.d("UserCount", userId);
                    checkFriend(userId, firebaseUser);
                }

                // Xử lý trường hợp không tìm thấy kết quả
                if (!snapshot.exists()) {
                    // Hiển thị thông báo hoặc thực hiện hành động khác
                    Toast.makeText(AddDeleteFriend.this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi truy vấn
            }
        });

    }

    private void checkFriend(String userId, FirebaseUser firebaseUser) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("friends").child(firebaseUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua các khóa con
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    // Lấy friendID
                    String friendId = friendSnapshot.getValue(String.class);

                    // Kiểm tra xem friendID có trùng với UserID cần tìm hay không
                    if (friendId.equals(userId)) {
                        // UserID đã tồn tại trong danh sách bạn bè
                        Toast.makeText(AddDeleteFriend.this, "Đã là bạn bè!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // UserID không tồn tại trong danh sách bạn bè
                // Thực hiện thêm friendID vào danh sách
                DatabaseReference userFriend = FirebaseDatabase.getInstance().getReference().child("friends").child(firebaseUser.getUid());
                userFriend.push().setValue(userId);
                Toast.makeText(AddDeleteFriend.this, "Thêm vào danh sách bạn bè!", Toast.LENGTH_SHORT).show();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                // Add a listener to retrieve friend's data
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        User friendUser = userSnapshot.getValue(User.class);
                        if (friendUser != null) { // Check if data is retrieved successfully
                            list.add(friendUser);
                            Log.d("UserCount", friendUser.email); // Log email for debugging
                            myAdapter.notifyDataSetChanged();
                        }
                         // Notify adapter of data change
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi truy vấn
            }
        });

    }

    private void LoadData(){
        mAuthencation = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuthencation.getCurrentUser();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("friends").child(firebaseUser.getUid());
        revFriendList.setHasFixedSize(true);
        revFriendList.setLayoutManager(new LinearLayoutManager(this));


        list = new ArrayList<>();
        myAdapter = new Adapter_friend(this,list);
        revFriendList.setAdapter(myAdapter);

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
                            User friendUser = userSnapshot.getValue(User.class);
                            if (friendUser != null) { // Check if data is retrieved successfully
                                list.add(friendUser);
                                Log.d("UserCount", friendUser.email); // Log email for debugging
                            }
                            myAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle errors
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setTvListFriendTakePhoto( FirebaseUser firebaseUser) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("friends").child(firebaseUser.getUid());
        //Log.d("UserCount","aaaa12");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int nodeCount = (int) snapshot.getChildrenCount();
                tvNumberFried.setText(String.valueOf(nodeCount)+ " người bạn");
                // Do something with nodeCount (e.g., update UI)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NodeCount", "Lỗi khi lấy số lượng node: " + error.getMessage());
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
                Picasso.get().load(avatarUrl).into(btnComeBack);
                btnSearch.setClipToOutline(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }
}
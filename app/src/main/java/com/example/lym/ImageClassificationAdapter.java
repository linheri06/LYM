    package com.example.lym;

    import android.content.Context;
    import android.net.Uri;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.lym.Model.Image;
    import com.google.firebase.Firebase;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.squareup.picasso.Picasso;

    import java.util.ArrayList;

    public class ImageClassificationAdapter extends RecyclerView.Adapter<ImageClassificationAdapter.ViewHolder>{
        Context context;
        FirebaseAuth firebaseAuth;
        ArrayList<Image> imagelist;
        private OnItemClickListener listener;


        public ImageClassificationAdapter(Context context, ArrayList<Image> imagelist, OnItemClickListener listener) {
            this.context = context;
            this.imagelist = imagelist;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.image_classification_adapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Image image = imagelist.get(position);
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            assert firebaseUser != null;
            String uID = firebaseUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("images");
            databaseReference.child(uID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(holder.imageviewclassification);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return imagelist.size();
        }
        //khai bao su kien lang nghe cick anh
        public interface OnItemClickListener {
            void onItemClick(String url);
        }


        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView imageviewclassification;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageviewclassification = itemView.findViewById(R.id.imageviewclassification);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Lấy ID của ảnh từ position và chuyển nó qua interface
                            listener.onItemClick(imagelist.get(position).url);
                        }
                    }
                });
            }
        }
    }

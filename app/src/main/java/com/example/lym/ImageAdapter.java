package com.example.lym;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lym.Model.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Image> imageList;
    private OnItemClickListener listener;

    public ImageAdapter(List<Image> imageList, OnItemClickListener listener) {
        this.imageList = imageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_classification_adapter, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Đổ dữ liệu vào item RecyclerView
        Image image = imageList.get(position);
        Picasso.get().load(image.getUrl()).into(holder.imageviewclassification); // Sử dụng Picasso để tải ảnh từ URL và đặt vào ImageView
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // Khai báo một interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(String url, long idImage);
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageviewclassification;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageviewclassification = itemView.findViewById(R.id.imageviewclassification);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Lấy ID của ảnh từ position và chuyển nó qua interface
                        listener.onItemClick(imageList.get(position).url, imageList.get(position).date);
                    }
                }
            });
        }
    }
}

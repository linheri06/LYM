package com.example.lym;

import android.content.Context;

import com.example.lym.Model.User;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;

public class Adapter_friend_spinner extends ArrayAdapter<User> {

    private Context mContext;
    private List<User> mFriendList;

    public Adapter_friend_spinner(Context context, List<User> friendList) {
        super(context, 0, friendList);
        mContext = context;
        mFriendList = friendList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View   listItem = LayoutInflater.from(mContext).inflate(R.layout.item_friend, parent, false);


        User currentUser = mFriendList.get(position);

        ImageView avatarImageView = listItem.findViewById(R.id.ivImage);
        TextView nameTextView = listItem.findViewById(R.id.tvName);

        // Set avatar and name
        // Example: Picasso.with(mContext).load(currentUser.getAvatar()).into(avatarImageView);
        // nameTextView.setText(currentUser.getName());
        Picasso.get().load(currentUser.avatar).into(avatarImageView);
        nameTextView.setText(currentUser.name);

        return listItem;
    }
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}


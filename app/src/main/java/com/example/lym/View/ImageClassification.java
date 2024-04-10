package com.example.lym.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.*;

import com.example.lym.R;

public class ImageClassification extends AppCompatActivity {

    ImageButton ibtnImage_classify;
    Button btnChat_classify, btnTakePhoto_classify;
    Spinner spinnerClassify;
    RecyclerView revImage_classify;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classification);

        ibtnImage_classify = (ImageButton) findViewById(R.id.ibtnImage_classify);
        btnChat_classify = (Button) findViewById(R.id.btnChat_classify);
        btnTakePhoto_classify = (Button) findViewById(R.id.btnTakePhoto_classify);
        spinnerClassify = (Spinner) findViewById(R.id.spinnerClassify);
        revImage_classify = (RecyclerView) findViewById(R.id.revImage_classify);
    }
}
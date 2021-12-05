package com.demo.touristguide.Templates;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.demo.touristguide.R;

public class TranslatorTemp extends AppCompatActivity {


    TextView mTranslatorName, mTranslatorPhone, mTranslatorPrice, mTranslatorDesc;
    String translatorName, translatorPhone, translatorPrice , translatorDesc;
    int translatorImage;
    Button mCallBtn;
    ImageView mTranslatorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator_temp);


        initView();

    }

    private void initView() {
        Intent i = getIntent();

        translatorName = i.getStringExtra("translatorName");
        translatorPhone = i.getStringExtra("translatorPhone");
        translatorPrice = i.getStringExtra("translatorPrice");
        translatorDesc = i.getStringExtra("translatorDesc");
        translatorImage = i.getIntExtra("translatorPicture", 0);

        mTranslatorName = findViewById(R.id.translator_detail_name);
        mTranslatorPhone = findViewById(R.id.translator_detail_phone);
        mTranslatorPrice = findViewById(R.id.translator_detail_price);
        mTranslatorImage = findViewById(R.id.translator_detail_image);
        mTranslatorDesc = findViewById(R.id.translator_detail_desc);

        mTranslatorName.setText(translatorName);
        mTranslatorPhone.setText(translatorPhone);
        mTranslatorPrice.setText(translatorPrice);
        mTranslatorDesc.setText(translatorDesc);

        try {
            if (translatorImage != 0) {
                Glide.with(TranslatorTemp.this)
                        .load(translatorImage)
                        .into(mTranslatorImage);
            } else {
                Glide.with(TranslatorTemp.this)
                        .load("https://gfsstore.com/wp-content/themes/gfsstore.com/images/no_image_available.png")
                        .into(mTranslatorImage);
            }
        } catch (Exception e) {
            Glide.with(TranslatorTemp.this)
                    .load("https://gfsstore.com/wp-content/themes/gfsstore.com/images/no_image_available.png")
                    .into(mTranslatorImage);
            Log.e("TranslatorTemp", "onBindViewHolder: Error while put photo" + e.getMessage());
        }

        mCallBtn = findViewById(R.id.translator_detail_call_btn);
        mCallBtn.setOnClickListener(v -> CallNumber());


    }

    private void CallNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + translatorPhone));
        startActivity(intent);
    }
}
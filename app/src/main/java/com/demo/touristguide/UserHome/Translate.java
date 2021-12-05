package com.demo.touristguide.UserHome;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.touristguide.Adapters.TranslatorsAdapter;
import com.demo.touristguide.DataModels.TranslatorDataModel;
import com.demo.touristguide.R;

import java.util.ArrayList;

public class Translate extends AppCompatActivity {


    ArrayList<TranslatorDataModel> mTranslatorsList;
    RecyclerView mTranslatorsRecyclerView;
    LinearLayoutManager layoutManager;
    TranslatorsAdapter translatorAdapter;
    ImageView mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        initViews();

    }

    private void initViews() {

        mTranslatorsList = new ArrayList<TranslatorDataModel>();
        mTranslatorsList.add(new TranslatorDataModel("Aalaa Translation Company", "40",
                "Try Out our Professional Native Translators Specialized in 40+ Subjects Today!", "5 9981 2227", R.drawable.translator1));
        mTranslatorsList.add(new TranslatorDataModel("Authorized document translation services company", "70",
                "Try Out our Professional Native Translators Specialized in 40+ Subjects Today!", "5 9675 4506", R.drawable.translator2));
        mTranslatorsList.add(new TranslatorDataModel("MikDoss Translation Services", "60",
                "Try Out our Professional Native Translators Specialized in 40+ Subjects Today!", "50 633 6162", R.drawable.translator3));
        mTranslatorsList.add(new TranslatorDataModel("Global Translation Center", "90",
                "Try Out our Professional Native Translators Specialized in 40+ Subjects Today!", "5 2241 4013", R.drawable.translator4));
        mTranslatorsList.add(new TranslatorDataModel("Tala Translation Company", "120",
                "Try Out our Professional Native Translators Specialized in 40+ Subjects Today!", "5 6000 2557", R.drawable.translator5));


        mBackBtn = findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(v -> onBackPressed());

        mTranslatorsRecyclerView = findViewById(R.id.translate_recyclerview);
        mTranslatorsRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(Translate.this, RecyclerView.VERTICAL, false);
        mTranslatorsRecyclerView.setLayoutManager(layoutManager);


        showTranslators();

    }

    public void showTranslators() {
        translatorAdapter = new TranslatorsAdapter(Translate.this, mTranslatorsList);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mTranslatorsRecyclerView.setAdapter(translatorAdapter);
    }

}
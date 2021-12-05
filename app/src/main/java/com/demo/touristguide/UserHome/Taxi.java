package com.demo.touristguide.UserHome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.demo.touristguide.Adapters.TranslatorsAdapter;
import com.demo.touristguide.DataModels.TranslatorDataModel;
import com.demo.touristguide.R;

import java.util.ArrayList;

public class Taxi extends AppCompatActivity {

    ArrayList<TranslatorDataModel> mTaxisList;
    RecyclerView mTaxisRecyclerView;
    LinearLayoutManager layoutManager;
    TranslatorsAdapter taxisAdapter;
    ImageView mBackBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);

        initViews();

    }



    private void initViews() {

        mTaxisList = new ArrayList<TranslatorDataModel>();
        mTaxisList.add(new TranslatorDataModel("Mitsubishi Mirage 2021", "100",
                "Automatic / GLX & 1200 CC", "52733 6161", R.drawable.taxi1));

        mTaxisList.add(new TranslatorDataModel("Changan Eado 2021", "150",
                "Automatic / Basic & 1500 CC", "50 833 6152", R.drawable.taxi2));

        mTaxisList.add(new TranslatorDataModel("Suzuki Swift Dzire 2021", "110",
                "Automatic / GL & 1200 CC", "56 353 4862", R.drawable.taxi3));

        mTaxisList.add(new TranslatorDataModel("Renault Dokker 2020", "180",
                "Manual / Standard & 1600 CC", "50 879 9167", R.drawable.taxi4));

        mTaxisList.add(new TranslatorDataModel("MG 5 2021", "80",
                "Automatic / STD & 1500 CC", "52 523 3562", R.drawable.taxi5));

        mTaxisList.add(new TranslatorDataModel("Proton Persona 2020", "175",
                "Automatic & 1600 CC", "51 433 8662", R.drawable.taxi6));



        mBackBtn = findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(v -> onBackPressed());

        mTaxisRecyclerView = findViewById(R.id.translate_recyclerview);
        mTaxisRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(Taxi.this, RecyclerView.VERTICAL, false);
        mTaxisRecyclerView.setLayoutManager(layoutManager);


        showTaxis();

    }

    public void showTaxis() {
        taxisAdapter = new TranslatorsAdapter(Taxi.this, mTaxisList);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mTaxisRecyclerView.setAdapter(taxisAdapter);
    }
}
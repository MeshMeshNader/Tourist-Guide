package com.demo.touristguide.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.demo.touristguide.Templates.DetailsTemp;
import com.demo.touristguide.DataModels.TranslatorDataModel;
import com.demo.touristguide.R;
import com.demo.touristguide.Templates.TranslatorTemp;

import java.util.ArrayList;

public class TranslatorsAdapter extends RecyclerView.Adapter<TranslatorsAdapter.myTranslatorViewHolder> {

    Context context;
    ArrayList<TranslatorDataModel> translatorsList;


    public TranslatorsAdapter(Context context, ArrayList<TranslatorDataModel> translatorsList) {
        this.context = context;
        this.translatorsList = translatorsList;
    }

    @NonNull
    @Override
    public myTranslatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_translator, parent, false);

        myTranslatorViewHolder viewHolder = new myTranslatorViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myTranslatorViewHolder holder, int position) {

        TranslatorDataModel translator = translatorsList.get(position);

        holder.translatorItemName.setText(translator.getName());
        holder.translatorItemPhone.setText(translator.getPhoneNumber());
        holder.translatorItemPrice.setText(translator.getPrice());


        try {
            Glide.with(holder.itemView)
                    .load(translator.getPicture())
                    .into(holder.translatorItemImage);
        } catch (Exception e) {
            Glide.with(holder.itemView)
                    .load("https://gfsstore.com/wp-content/themes/gfsstore.com/images/no_image_available.png")
                    .into(holder.translatorItemImage);
            Log.e("TranslatorAdapter", "onBindViewHolder: Error while put photo" + e.getMessage());
        }

        holder.translatorItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent translatorDetails = new Intent(context, TranslatorTemp.class);
                translatorDetails.putExtra("translatorName", translator.getName());
                translatorDetails.putExtra("translatorPhone", translator.getPhoneNumber());
                translatorDetails.putExtra("translatorPrice", translator.getPrice());
                translatorDetails.putExtra("translatorDesc", translator.getDesc());
                translatorDetails.putExtra("translatorPicture", translator.getPicture());
                context.startActivity(translatorDetails);
            }
        });


    }

    @Override
    public int getItemCount() {
        return translatorsList.size();
    }

    public class myTranslatorViewHolder extends RecyclerView.ViewHolder {

        LinearLayout translatorItemBtn;
        ImageView translatorItemImage;
        TextView translatorItemName, translatorItemPhone, translatorItemPrice;

        public myTranslatorViewHolder(@NonNull View itemView) {
            super(itemView);

            translatorItemBtn = itemView.findViewById(R.id.translator_btn);
            translatorItemImage = itemView.findViewById(R.id.translator_image);
            translatorItemName = itemView.findViewById(R.id.translator_name);
            translatorItemPhone = itemView.findViewById(R.id.translator_phone);
            translatorItemPrice = itemView.findViewById(R.id.translator_price);
        }

    }
}

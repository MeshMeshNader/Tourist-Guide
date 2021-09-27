package com.demo.touristguide.AdminHome.Post;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.demo.touristguide.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;


public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterVH> {

    ArrayList<String> mList;
    private Context context;

    public SliderAdapterExample(Context context, ArrayList<String> mList) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        Glide.with(viewHolder.itemView)
                .load(mList.get(position))
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenfullsizeImage(mList.get(position));
            }
        });

    }


    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mList.size();
    }

    //this method will get the big size of profile image.
    public void OpenfullsizeImage(String url) {
        Intent image = new Intent(context, See_Full_Image.class);
        image.putExtra("image_url", url);
        context.startActivity(image);
    }




}
package com.demo.touristguide.AdminHome.PostsManage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.touristguide.R;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class AddPostImagesAdapter extends RecyclerView.Adapter<AddPostImagesAdapter.myPostImagesViewHolder> {

    ArrayList<String> mImagesURI;
    Context context;

    public AddPostImagesAdapter(ArrayList<String> mImagesURI, Context context) {
        this.mImagesURI = mImagesURI;
        this.context = context;
    }

    @NonNull
    @Override
    public myPostImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_images_item, parent, false);

        myPostImagesViewHolder viewHolder = new myPostImagesViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myPostImagesViewHolder holder, int position) {
        Picasso.with(context).load(mImagesURI.get(position)).into(holder.mMyPostImage);
    }

    @Override
    public int getItemCount() {
        return mImagesURI.size();
    }

    class myPostImagesViewHolder extends RecyclerView.ViewHolder {

        ImageView mMyPostImage;

        public myPostImagesViewHolder(@NonNull View itemView) {
            super(itemView);

            mMyPostImage = itemView.findViewById(R.id.post_Image_item);
        }
    }

}

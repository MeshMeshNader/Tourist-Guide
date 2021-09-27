package com.demo.touristguide.UserHome.Posts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.demo.touristguide.AdminHome.Post.DetailsTemp;
import com.demo.touristguide.DataModels.PostDataModel;
import com.demo.touristguide.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.myPostViewHolder> implements EasyPermissions.PermissionCallbacks {

    Context context;
    ArrayList<PostDataModel> postsList;
    boolean vertical;
    DatabaseReference mPostRef;
    boolean isAdmin;


    double deviceLat, deviceLon, distanceValue;
    DecimalFormat df = new DecimalFormat("#.#");


    public PostsAdapter(Context context, ArrayList<PostDataModel> postsList, boolean vertical, boolean isAdmin, double deviceLat , double deviceLon) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.postsList = postsList;
        this.vertical = vertical;
        this.deviceLat = deviceLat;
        this.deviceLon = deviceLon;
    }



    @NonNull
    @Override
    public myPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        View viewVertical = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_vertical, parent, false);

        myPostViewHolder viewHolder;

        if (vertical)
            viewHolder = new myPostViewHolder(viewVertical);
        else
            viewHolder = new myPostViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myPostViewHolder holder, int position) {

        PostDataModel post = postsList.get(position);

        holder.postItemLabel.setText(post.getPostLabel());
        holder.postItemDesc.setText(post.getPostDescription());

        holder.postItemDistance.setText(getDistance(post.getLat(), post.getLon()));

        try {
            Glide.with(holder.itemView)
                    .load(post.getPostImageURL().get(0))
                    .into(holder.postItemImage);
        } catch (Exception e) {
            Glide.with(holder.itemView)
                    .load("https://gfsstore.com/wp-content/themes/gfsstore.com/images/no_image_available.png")
                    .into(holder.postItemImage);
            Log.e("PostAdapter", "onBindViewHolder: Error while put photo" + e.getMessage());
        }


        holder.postItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postDetails = new Intent(context, DetailsTemp.class);
                postDetails.putExtra("postKey", post.getPostKey());
                postDetails.putExtra("databaseRef", post.getPostSection());
                postDetails.putExtra("userType", "admin");
                context.startActivity(postDetails);
            }
        });


        if (isAdmin) {

            String databaseRef = "";
            if(post.getPostSection().equals("Hotels"))
                databaseRef = "AllHotels";
            else if(post.getPostSection().equals("Restaurants"))
                databaseRef = "AllRestaurants";
            mPostRef = FirebaseDatabase.getInstance().getReference(databaseRef);


            holder.postItemBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context)
                            .setMessage("Do you want to delete this Item!")
                            .setCancelable(false)
                            .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try{
                                        mPostRef.child(post.getPostKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                notifyDataSetChanged();
                                                Toast.makeText(context, "Item Deleted Successfully", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }catch (Exception e){
                                        Log.e("RemovePost", "onClick: " + e.getMessage().toString() );
                                    }
                                }
                            })
                            .setNegativeButton("No!", null)
                            .show();

                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }


    private String getDistance(double lat, double lon) {
        Location loc1 = new Location("");
        loc1.setLatitude(deviceLat);
        loc1.setLongitude(deviceLon);
        Location loc2 = new Location("");
        loc2.setLatitude(lat);
        loc2.setLongitude(lon);
        distanceValue = ((loc1.distanceTo(loc2) + 1) * 0.001);


        return df.format(distanceValue) + " km";
    }



    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }


    public class myPostViewHolder extends RecyclerView.ViewHolder {

        LinearLayout postItemBtn;
        ImageView postItemImage;
        TextView postItemLabel, postItemDesc, postItemDistance;


        public myPostViewHolder(@NonNull View itemView) {
            super(itemView);

            postItemBtn = itemView.findViewById(R.id.post_btn);
            postItemImage = itemView.findViewById(R.id.post_image);
            postItemLabel = itemView.findViewById(R.id.post_label);
            postItemDesc = itemView.findViewById(R.id.post_desc);
            postItemDistance = itemView.findViewById(R.id.post_distance);

        }
    }
}

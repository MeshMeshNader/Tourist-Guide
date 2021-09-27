package com.demo.touristguide.AdminHome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.demo.touristguide.AdminHome.Hotels.HotelManagement;
import com.demo.touristguide.AdminHome.Restaurants.RestaurantManagement;
import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.EditProfile;


public class AdminMainPageFragment extends Fragment implements View.OnClickListener {


    View view;

    AdminHome mBasicHome;
    LinearLayout mHotelBtn, mRestaurantBtn;

    public AdminMainPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_main_page, container, false);

        initViews();


        return view;

    }


    private void initViews() {

        mBasicHome = new AdminHome();

        mBasicHome.mProfileBtn.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_person));
        mBasicHome.mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , EditProfile.class);
                startActivity(intent);
            }
        });

        mHotelBtn = view.findViewById(R.id.admin_hotel_btn);
        mRestaurantBtn = view.findViewById(R.id.admin_restaurant_btn);

        mHotelBtn.setOnClickListener(this);
        mRestaurantBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.admin_hotel_btn:

                mBasicHome.loadOutFragment(new HotelManagement(), "Hotels Manage", 1, getActivity());

                break;

            case R.id.admin_restaurant_btn:

                mBasicHome.loadOutFragment(new RestaurantManagement(), "Restaurants Manage", 2, getActivity());

                break;

        }
    }


}
package com.demo.touristguide.UserHome.Advices.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.Advices.Advices;


public class AdvicesFifthPage extends Fragment {

    View view;
    View firstPage, secondPage, thirdPage, fourthPage;
    ImageView mPrevBtn;
    Advices mAdvices;
    Button mContinue;

    public AdvicesFifthPage() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_advices_fifth_page, container, false);

        initViews();

        return view;
    }

    private void initViews() {
        mAdvices = new Advices();

        firstPage = view.findViewById(R.id.intro_first_box);
        secondPage = view.findViewById(R.id.intro_second_box);
        thirdPage = view.findViewById(R.id.intro_third_box);
        fourthPage = view.findViewById(R.id.intro_fourth_box);

        firstPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(0));
        secondPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(1));
        thirdPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(2));
        fourthPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(3));


        mPrevBtn = view.findViewById(R.id.prev_page_image);
        mPrevBtn.setOnClickListener(v -> prevFragment());

        mContinue = view.findViewById(R.id.continue_btn);
        mContinue.setOnClickListener(v -> goToHomePage());
    }

    private void goToHomePage() {
        getActivity().finish();
    }

    private void prevFragment() {
        mAdvices.loadOutFragmentBack();
    }

}
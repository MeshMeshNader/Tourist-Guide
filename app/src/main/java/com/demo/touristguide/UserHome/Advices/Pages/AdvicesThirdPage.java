package com.demo.touristguide.UserHome.Advices.Pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.Advices.Advices;


public class AdvicesThirdPage extends Fragment {


    View view, firstPage, secondPage, fourthPage, fifthPage;
    ImageView mPrevBtn, mNextBtn;
    Advices mAdvices;
    Button mNext;


    public AdvicesThirdPage() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_advices_third_page, container, false);

        initViews();

        return view;
    }


    private void initViews() {
        mAdvices = new Advices();

        firstPage = view.findViewById(R.id.intro_first_box);
        secondPage = view.findViewById(R.id.intro_second_box);
        fourthPage = view.findViewById(R.id.intro_fourth_box);
        fifthPage = view.findViewById(R.id.intro_fifth_box);

        firstPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(0));
        secondPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(1));
        fourthPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(3));
        fifthPage.setOnClickListener(v -> mAdvices.loadOutFragmentSpecific(4));

        mNextBtn = view.findViewById(R.id.next_page_image);
        mNextBtn.setOnClickListener(v -> nextFragment());

        mPrevBtn = view.findViewById(R.id.prev_page_image);
        mPrevBtn.setOnClickListener(v -> prevFragment());

        mNext = view.findViewById(R.id.next_btn);
        mNext.setOnClickListener(v -> nextFragment());
    }

    private void prevFragment() {
        mAdvices.loadOutFragmentBack();
    }

    private void nextFragment() {
        mAdvices.loadOutFragmentForward();
    }
    
}
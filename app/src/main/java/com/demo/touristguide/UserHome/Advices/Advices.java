package com.demo.touristguide.UserHome.Advices;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.demo.touristguide.Adapters.ViewPagerAdapter;
import com.demo.touristguide.R;
import com.demo.touristguide.UserHome.Advices.Pages.AdvicesFifthPage;
import com.demo.touristguide.UserHome.Advices.Pages.AdvicesFirstPage;
import com.demo.touristguide.UserHome.Advices.Pages.AdvicesFourthPage;
import com.demo.touristguide.UserHome.Advices.Pages.AdvicesSecondPage;
import com.demo.touristguide.UserHome.Advices.Pages.AdvicesThirdPage;

public class Advices extends FragmentActivity {

    public static ViewPager mViewPager;
    public static ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelling);


        mViewPager = (ViewPager) findViewById(R.id.advices_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());


        mViewPagerAdapter.addFragment(new AdvicesFirstPage());
        mViewPagerAdapter.addFragment(new AdvicesSecondPage());
        mViewPagerAdapter.addFragment(new AdvicesThirdPage());
        mViewPagerAdapter.addFragment(new AdvicesFourthPage());
        mViewPagerAdapter.addFragment(new AdvicesFifthPage());

        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(0, true);
    }

    public void loadOutFragmentBack() {
        mViewPager.setCurrentItem(getItem(-1), true);
    }

    public void loadOutFragmentForward() {
        mViewPager.setCurrentItem(getItem(+1), true);
    }

    public void loadOutFragmentSpecific(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }


}
package com.example.chunkhai.rides;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSchedule extends Fragment{
    private static final String TAG = "FragmentSchedule";

    //widgets
    TabLayout tabLayout;
    ViewPager viewPager;

    //vars

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.innerTabs);
        viewPager= (ViewPager) view.findViewById(R.id.vpInnerContentContainer);

        setupViewPager();

        return view;
    }

    private void setupViewPager(){
        Log.d(TAG, "setupViewPager:setup view page with tab fragment2");

        tabLayout.addTab(tabLayout.newTab().setText("Ongoing      Ride"));
        tabLayout.addTab(tabLayout.newTab().setText("Provided     Ride"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending Request"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        TabsAdapter tabsAdapter = new TabsAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public class TabsAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public TabsAdapter(FragmentManager fm, int NoofTabs) {
            super(fm);
            this.mNumOfTabs = NoofTabs;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    FragmentScheduleOngoingRide ongoingRide = new FragmentScheduleOngoingRide();
                    return ongoingRide;
                case 1:
                    FragmentScheduleProvidedRide providedRide = new FragmentScheduleProvidedRide();
                    return providedRide;
                case 2:
                    FragmentSchedulePendingRequest pendingRequest = new FragmentSchedulePendingRequest();
                    return pendingRequest;
                default:
                    return null;
            }
        }
    }
}

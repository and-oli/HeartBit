package com.firebase.uidemo.auth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter{

    int mNoOfTabs;

    public PagerAdapter (FragmentManager fm, int numberOfTabs){
        super(fm);
        this.mNoOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                MedicineFragment med = new MedicineFragment();
                return med;
            }
            case 1:{
                PressureFragment pre = new PressureFragment();
                return pre;
            }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}

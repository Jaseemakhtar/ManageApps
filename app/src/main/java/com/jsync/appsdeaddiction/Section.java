package com.jsync.appsdeaddiction;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jaseem on 30/4/19.
 */

public class Section extends FragmentPagerAdapter {

    public Section(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TabOne();
            case 1:
                return new TabTwo();
            case 2:
                return new TabThree();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}

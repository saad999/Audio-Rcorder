package com.devbracket.audiorecorder.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class View_Pager_Ad extends FragmentPagerAdapter {

    String[] mStr;
    Fragment[] mFr;
    public View_Pager_Ad(FragmentManager fm,Fragment[] fragment,String[] str) {
        super(fm);
        mFr=fragment;
        mStr=str;
    }

    @Override
    public Fragment getItem(int i) {
        return mFr[i];
    }

    @Override
    public int getCount() {
        return mStr.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mStr[position];
    }
}

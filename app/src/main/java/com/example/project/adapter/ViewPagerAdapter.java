package com.example.project.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.project.fragment.fragmentHome;
import com.example.project.fragment.fragmentMessage;
import com.example.project.fragment.fragmentNotification;
import com.example.project.fragment.fragmentProfile;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new fragmentHome();
            case 1:return new fragmentMessage();
            case 2:return new fragmentNotification();
            case 3:return new fragmentProfile();
            default:return new fragmentHome();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}

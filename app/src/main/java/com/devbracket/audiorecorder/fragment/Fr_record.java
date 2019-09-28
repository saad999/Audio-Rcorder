package com.devbracket.audiorecorder.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import com.devbracket.audiorecorder.R;
import com.devbracket.audiorecorder.listener.ClickListener;
import com.melnykov.fab.FloatingActionButton;


public class Fr_record extends Fragment {

    public static FloatingActionButton fab;public static AlphaAnimation alphaAnimation;
    TabLayout tabLayout;
    public static Button button;
    public static ImageView imageView;
    public static Chronometer chronometer;
    public static TextView textView;
    public static Fr_record newInstance() {Bundle args = new Bundle();Fr_record fragment = new Fr_record();fragment.setArguments(args);return fragment; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rec,null);init(view);return view;
    }

    private void init(View view){
        alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setDuration(1000);
        button = view.findViewById(R.id.btnCancel);
        button.setOnClickListener(new ClickListener(getContext(),getActivity()));
        button.setVisibility(View.INVISIBLE);
        fab = view.findViewById(R.id.fab_record);
        fab.setOnClickListener(new ClickListener(getContext(),getActivity()));
        imageView = view.findViewById(R.id.fd_img);
        tabLayout = view.findViewById(R.id.tab);
        chronometer = view.findViewById(R.id.chronometer);
        textView = view.findViewById(R.id.txt);
    }
}

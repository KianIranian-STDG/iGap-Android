package net.iGap.fragments;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.iGap.R;
import net.iGap.module.transition.fragment.ExitFragmentTransition;
import net.iGap.module.transition.fragment.FragmentTransition;

public class FragmentTest extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sub, container, false);
        final ExitFragmentTransition exitFragmentTransition = FragmentTransition.with(this).interpolator(new LinearOutSlowInInterpolator()).to(v.findViewById(R.id.sub_imageView)).start(savedInstanceState);
        //exitFragmentTransition.exitListener(new AnimatorListenerAdapter() {
        //    @Override
        //    public void onAnimationStart(Animator animation) {
        //        Log.d("GGGGG", "onAnimationStart: ");
        //    }
        //
        //    @Override
        //    public void onAnimationEnd(Animator animation) {
        //        Log.d("GGGGG", "onAnimationEnd: ");
        //    }
        //}).interpolator(new FastOutSlowInInterpolator());
        //exitFragmentTransition.startExitListening();
        return v;
    }
}
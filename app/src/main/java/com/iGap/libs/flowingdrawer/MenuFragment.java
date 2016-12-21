package com.iGap.libs.flowingdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.activities.ActivityMain;

public class MenuFragment extends Fragment {

    private boolean isShown;
    private RevealLayout mRevealLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    public void show(int y) {
        if (!isShown) {
            isShown = true;
            mRevealLayout.show(100, y, 0);
            //event call back
            onOpenMenu();
        }
    }

    public void hideView() {
        mRevealLayout.hide();
        isShown = false;
        //event call back
        onCloseMenu();
    }

    public View setupReveal(View view) {
        mRevealLayout = new RevealLayout(view.getContext());
        mRevealLayout.addView(view);
        hideView();
        return mRevealLayout;
    }

    public void onOpenMenu() {
        if (ActivityMain.arcMenu != null && ActivityMain.arcMenu.isMenuOpened()) {
            ActivityMain.arcMenu.toggleMenu();
        }

        if (ActivityMain.appBarLayout != null) {

            ((View) ActivityMain.appBarLayout).setEnabled(false);
        }

    }


    public void onCloseMenu() {
        if (ActivityMain.appBarLayout != null) {

            ((View) ActivityMain.appBarLayout).setEnabled(true);
        }
    }


}

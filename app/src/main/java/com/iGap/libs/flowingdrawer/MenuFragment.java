package com.iGap.libs.flowingdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.interface_package.IOpenDrawer;


public class MenuFragment extends Fragment {

    private boolean isShown;
    private RevealLayout mRevealLayout;
    private IOpenDrawer mOpenDrawerListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setOpenDrawerListener(IOpenDrawer listener) {
        this.mOpenDrawerListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public void show(int y) {
        if (!isShown) {
            isShown = true;
            mRevealLayout.show(100, y, 1000);

            //event call back
            if (mOpenDrawerListener != null) {
                mOpenDrawerListener.onOpenDrawer();
            }
        }
    }

    public void hideView() {
        if (isShown) {
            if (mOpenDrawerListener != null) {
                mOpenDrawerListener.onCloseDrawer();
            }
        }
        mRevealLayout.hide();
        isShown = false;
        //event call back
    }


    /**
     * @param view View
     * @param hide set false if showing with reveal effect is not what you want
     * @return
     */
    public View setupReveal(View view, boolean hide) {
        mRevealLayout = new RevealLayout(view.getContext());
        mRevealLayout.addView(view);
        if (hide) {
            hideView();
        } else {
            isShown = true;
        }
        return mRevealLayout;
    }
}

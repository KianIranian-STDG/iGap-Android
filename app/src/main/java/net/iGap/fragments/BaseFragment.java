/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import net.iGap.G;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.libs.swipeback.SwipeBackFragment;
import net.iGap.libs.swipeback.SwipeBackLayout;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class BaseFragment extends SwipeBackFragment {

    public boolean isNeedResume = false;
    protected Fragment currentFragment;
    public AvatarHandler avatarHandler;

    @Override
    public void onAttach(Context context) {
        //super.onAttach(context);
        super.onAttach(ViewPumpContextWrapper.wrap(context));
        G.fragmentActivity = (FragmentActivity) context;
        currentFragment = this;
        hideKeyboard();
    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        avatarHandler = new AvatarHandler();
        checkFont();
        super.onCreate(savedInstanceState);

        getSwipeBackLayout().setEdgeOrientation(SwipeBackLayout.EDGE_LEFT);

        if (G.oneFragmentIsOpen != null) {
            G.oneFragmentIsOpen.justOne();
        }

        getSwipeBackLayout().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                if (ActivityMain.disableSwipe) {
                    getSwipeBackLayout().setEnableGesture(false);
                } else {
                    getSwipeBackLayout().setEnableGesture(true);
                }
                return false;
            }
        });
    }

    public void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (G.oneFragmentIsOpen != null) {
            G.oneFragmentIsOpen.justOne();
        }
        hideKeyboard();
        if (getActivity() != null) {
            //Todo : check logic and fixed this
            if (getActivity() instanceof ActivityMain) {
                for (int i = getActivity().getSupportFragmentManager().getFragments().size() - 1; i >= 0; i--) {
                    Fragment f = getActivity().getSupportFragmentManager().getFragments().get(i);
                    if (f instanceof BaseFragment && f != currentFragment) {
                        if (((BaseFragment) f).isNeedResume) {
                            f.onResume();
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * check the selected language user and set the language if change it
     */
    private void checkFont() {

        if (G.typeface_IRANSansMobile == null) {
            G.typeface_IRANSansMobile = Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile.ttf");
        }

        if (G.typeface_IRANSansMobile_Bold == null) {
            G.typeface_IRANSansMobile_Bold = Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile_Bold.ttf");
        }

        if (G.typeface_Fontico == null) {
            G.typeface_Fontico = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap-Fontico.ttf");
        }


        if (G.typeface_FonticonNew == null) {
            G.typeface_FonticonNew = Typeface.createFromAsset(G.context.getAssets(), "fonts/font_icon.ttf");
        }

        if (G.typeface_neuropolitical == null) {
            G.typeface_neuropolitical = Typeface.createFromAsset(G.context.getAssets(), "fonts/neuropolitical.ttf");
        }

        if (G.typeface_iGap == null) {
            G.typeface_iGap = Typeface.createFromAsset(G.context.getAssets(), "fonts/font_icon.ttf");
        }
    }


    protected void hideKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void popBackStackFragment() {
        try {
            if (getActivity() != null) {
                getActivity().onBackPressed();

                if (G.iTowPanModDesinLayout != null) {
                    G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
                }
            }
        } catch (Exception empty) {
            empty.printStackTrace();
        }
    }

    public void removeFromBaseFragment() {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), currentFragment).remove();
        }
    }

    public void removeFromBaseFragment(Fragment fragment) {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).remove();
        }
    }

    public void openKeyBoard() {

        try {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            //nothing
        }
    }
}

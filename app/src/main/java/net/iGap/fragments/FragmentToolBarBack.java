package net.iGap.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MyAppBarLayout;

public class FragmentToolBarBack extends BaseFragment {

    public static int numberOfVisible = 0;
    protected MyAppBarLayout appBarLayout;
    protected TextView titleTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        numberOfVisible++;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        numberOfVisible--;
        if (G.fragmentActivity != null) {
            ((ActivityMain) G.fragmentActivity).openNavigation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (G.fragmentActivity != null) {
            ((ActivityMain) G.fragmentActivity).lockNavigation();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = view.findViewById(R.id.title);
        titleTextView.setTypeface(G.typeface_IRANSansMobile);
        appBarLayout = view.findViewById(R.id.ac_appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBackButton = (RippleView) view.findViewById(R.id.chl_ripple_back_Button);
        rippleBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                popBackStackFragment();
            }
        });
    }
}

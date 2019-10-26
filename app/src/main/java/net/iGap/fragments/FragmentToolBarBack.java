package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MyAppBarLayout;

import org.jetbrains.annotations.NotNull;

public abstract class FragmentToolBarBack extends BaseFragment {

    public static int numberOfVisible = 0;
    protected MyAppBarLayout appBarLayout;
    protected TextView titleTextView;
    protected TextView menu_item1;
    protected boolean isSwipeBackEnable = true;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        numberOfVisible++;

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.toolbar_back_fragment, container, false);
        onCreateViewBody(inflater, view, savedInstanceState);
        if (isSwipeBackEnable) {
            return attachToSwipeBack(view);
        } else {
            return view;
        }
    }

    public abstract void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        numberOfVisible--;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = view.findViewById(R.id.title);
        titleTextView.setTypeface(ResourcesCompat.getFont(titleTextView.getContext() , R.font.main_font));
        appBarLayout = view.findViewById(R.id.ac_appBarLayout);
        menu_item1 = view.findViewById(R.id.menu_item1);
        menu_item1.setVisibility(View.GONE);

        RippleView rippleBackButton = view.findViewById(R.id.chl_ripple_back_Button);
        rippleBackButton.setOnClickListener(this::onBackButtonClicked);
    }

    protected void onBackButtonClicked(View view) {
        closeKeyboard(view);
        popBackStackFragment();
    }
}

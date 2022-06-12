package net.iGap.messenger.ui.components;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class FloatingMenuButton extends CoordinatorLayout {

    private final FloatingActionButton satelliteButton;
    private final FloatingActionButton googleMapButton;
    private final FloatingActionButton myLocationButton;
    private final FloatingActionButton menuButton;
    private boolean isFABOpen = false;

    public FloatingMenuButton(Context context, FloatingMenuButtonListener floatingMenuButtonListener) {
        super(context);

        satelliteButton = new FloatingActionButton(context);
        satelliteButton.setSize(FloatingActionButton.SIZE_NORMAL);
        satelliteButton.setImageResource(R.drawable.ic_satellite);
        satelliteButton.setOnClickListener(v -> floatingMenuButtonListener.satellite());
        addView(satelliteButton, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        googleMapButton = new FloatingActionButton(context);
        googleMapButton.setSize(FloatingActionButton.SIZE_NORMAL);
        googleMapButton.setImageResource(R.drawable.ic_google_map);
        googleMapButton.setOnClickListener(v -> floatingMenuButtonListener.googleMap());
        addView(googleMapButton, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        myLocationButton = new FloatingActionButton(context);
        myLocationButton.setSize(FloatingActionButton.SIZE_NORMAL);
        myLocationButton.setImageResource(R.drawable.ic_my_location);
        myLocationButton.setOnClickListener(v -> floatingMenuButtonListener.location());
        addView(myLocationButton, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        menuButton = new FloatingActionButton(context);
        menuButton.setSize(FloatingActionButton.SIZE_NORMAL);
        menuButton.setImageResource(R.drawable.ic_gps_layer);
        menuButton.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)));
        menuButton.setOnClickListener(v -> {
            if (!isFABOpen()) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });
        addView(menuButton, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        menuButton.layout(width - menuButton.getMeasuredWidth() - LayoutCreator.dp(23), height - menuButton.getMeasuredHeight() - LayoutCreator.dp(23), width - LayoutCreator.dp(23), height - LayoutCreator.dp(23));
        satelliteButton.layout(width - menuButton.getMeasuredWidth() - LayoutCreator.dp(23), height - menuButton.getMeasuredHeight() - LayoutCreator.dp(23), width - LayoutCreator.dp(23), height - LayoutCreator.dp(23));
        googleMapButton.layout(width - menuButton.getMeasuredWidth() - LayoutCreator.dp(23), height - menuButton.getMeasuredHeight() - LayoutCreator.dp(23), width - LayoutCreator.dp(23), height - LayoutCreator.dp(23));
        myLocationButton.layout(width - menuButton.getMeasuredWidth() - LayoutCreator.dp(23), height - menuButton.getMeasuredHeight() - LayoutCreator.dp(23), width - LayoutCreator.dp(23), height - LayoutCreator.dp(23));
    }

    @SuppressLint("RestrictedApi")
    public void showFABMenu() {
        isFABOpen = true;
        googleMapButton.setVisibility(View.VISIBLE);
        satelliteButton.setVisibility(View.VISIBLE);
        myLocationButton.setVisibility(View.VISIBLE);
        menuButton.animate().rotationBy(180);
        googleMapButton.animate().translationY(-getResources().getDimension(R.dimen.dp60));
        satelliteButton.animate().translationY(-getResources().getDimension(R.dimen.dp120));
        myLocationButton.animate().translationY(-getResources().getDimension(R.dimen.dp180));
    }

    public void closeFABMenu() {
        isFABOpen = false;
        menuButton.animate().rotation(0);
        googleMapButton.animate().translationY(0);
        satelliteButton.animate().translationY(0);
        myLocationButton.animate().translationY(0);
        myLocationButton.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @SuppressLint("RestrictedApi")
            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    googleMapButton.setVisibility(View.GONE);
                    satelliteButton.setVisibility(View.GONE);
                    myLocationButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) { }

            @Override
            public void onAnimationRepeat(Animator animator) { }
        });
    }

    public boolean isFABOpen() {
        return isFABOpen;
    }

    public  interface FloatingMenuButtonListener{
        void location();
        void googleMap();
        void satellite();
    }
}

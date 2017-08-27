package net.iGap.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentIntroduce;
import net.iGap.fragments.FragmentRegistrationNickname;

public class ActivityRegisteration extends ActivityEnhanced {

    public static final String showProfile = "showProfile";

    FrameLayout layoutRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registeration);

        boolean showPro = false;
        try {
            showPro = getIntent().getExtras().getBoolean(showProfile);
        } catch (Exception e) {

        }

        layoutRoot = (FrameLayout) findViewById(R.id.ar_layout_root);

        if (G.twoPaneMode) {
            setFraymeSize();
        }

        if (showPro) {
            loadFragmentProfile();
        } else {
            loadFragmentIntrodius();
        }
    }

    private void loadFragmentProfile() {

        FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();

        //Bundle  bundle=new Bundle();
        //bundle.putLong(FragmentRegistrationNickname.ARG_USER_ID, userId);
        //
        //fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commit();
    }

    private void setFraymeSize() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int size = Math.min(width, height) - 10;

        ViewGroup.LayoutParams lp = layoutRoot.getLayoutParams();
        lp.width = size;
        lp.height = size;
    }

    private void loadFragmentIntrodius() {

        FragmentIntroduce fragment = new FragmentIntroduce();

        getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commit();
    }
}

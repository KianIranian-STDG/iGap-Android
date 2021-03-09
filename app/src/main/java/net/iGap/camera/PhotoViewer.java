package net.iGap.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.hanks.library.AnimateCheckBox;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.customView.EventEditText;

import org.w3c.dom.Text;

import yogesh.firzen.mukkiasevaigal.M;

public class PhotoViewer extends BaseFragment {
    private FrameLayout rootView;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private LinearLayout toolbarPanel;
    private RippleView rippleView;
    private MaterialDesignTextView designTextView;
    private MaterialDesignTextView emoji;
    private TextView toolbarTitle;
    private MaterialDesignTextView setTextView;
    private TextView countImageTextView;
    private AnimCheckBox animateCheckBox;
    private LinearLayout bottomLayoutPanel;
    private LinearLayout layoutCaption;
    private EventEditText captionEditText;

    public static PhotoViewer newInstance() {
        Bundle args = new Bundle();
        PhotoViewer fragment = new PhotoViewer();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        rootView = (FrameLayout) fragmentView;

        viewPager = new ViewPager(context);
        rootView.addView(viewPager, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        toolbar = new Toolbar(context);
        toolbar.setContentInsetStartWithNavigation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            ViewCompat.setLayoutDirection(toolbar, ViewCompat.LAYOUT_DIRECTION_LTR);
        }
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorEditImageBlack));

        toolbarPanel = new LinearLayout(context);
        toolbarPanel.setOrientation(LinearLayout.HORIZONTAL);

        rippleView = new RippleView(context);
        rippleView.setCentered(true);
        rippleView.setRippleAlpha(200);
        rippleView.setRippleDuration(0);
        rippleView.setRipplePadding(5);
        toolbarPanel.addView(rippleView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        designTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        designTextView.setText(getString(R.string.md_close_button));
        designTextView.setTextColor(getResources().getColor(R.color.whit_background));
        rippleView.addView(designTextView, LayoutCreator.createRelative(48, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

        toolbarTitle = new TextView(context);
        toolbarTitle.setText(getString(R.string.photo));
        toolbarTitle.setTextColor(getResources().getColor(R.color.whit_background));
        toolbarTitle.setTextSize(16);
        toolbarTitle.setTypeface(toolbarTitle.getTypeface(), Typeface.BOLD);
        toolbarPanel.addView(toolbarTitle, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        setTextView = new MaterialDesignTextView(context);
        setTextView.setGravity(Gravity.CENTER);
        setTextView.setText(getString(R.string.check_icon));
        setTextView.setBackgroundColor(getResources().getColor(R.color.whit_background));
        setTextView.setTextSize(22);
        setTextView.setVisibility(View.GONE);
        toolbarPanel.addView(setTextView, LayoutCreator.createLinear(52, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));

        countImageTextView = new TextView(context);
        countImageTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        countImageTextView.setText(getString(R.string.photo));
        countImageTextView.setTextColor(getResources().getColor(R.color.whit_background));
        countImageTextView.setTextSize(22);
        countImageTextView.setTypeface(countImageTextView.getTypeface(), Typeface.BOLD);
        toolbarPanel.addView(countImageTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, 0, 0, R.dimen.dp1, 0));

        animateCheckBox = new AnimCheckBox(context);
        animateCheckBox.setBackground(getResources().getDrawable(R.drawable.background_check));
        animateCheckBox.setCircleColor(R.attr.iGapButtonColor);
        animateCheckBox.setLineColor(getResources().getColor(R.color.whit_background));
        animateCheckBox.setAnimDuration(100);
        animateCheckBox.setCorrectWidth(AndroidUtils.dp(1.2F));
        animateCheckBox.setUnCheckColor(getResources().getColor(R.color.background_checkbox_bottomSheet));
        toolbarPanel.addView(animateCheckBox, LayoutCreator.createLinear(32, 32, Gravity.CENTER | Gravity.END | Gravity.RIGHT));

        toolbar.addView(toolbarPanel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.addView(toolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 60, Gravity.TOP));

        bottomLayoutPanel = new LinearLayout(context);
        bottomLayoutPanel.setOrientation(LinearLayout.VERTICAL);
        bottomLayoutPanel.setBackgroundColor(getResources().getColor(R.color.colorEditImageBlack));

        layoutCaption = new LinearLayout(context);
        layoutCaption.setOrientation(LinearLayout.HORIZONTAL);
        layoutCaption.setMinimumHeight(48);
        layoutCaption.setPadding(4, 0, 4, 0);
        bottomLayoutPanel.addView(layoutCaption, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        emoji = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        emoji.setGravity(Gravity.CENTER);
        emoji.setPadding(8, 0, 8, 8);
        emoji.setText(getString(R.string.md_emoticon_with_happy_face));
        emoji.setTextColor(getResources().getColor(R.color.white));
        emoji.setTextSize(26);
        layoutCaption.addView(emoji, LayoutCreator.createLinear(52, 52, Gravity.CENTER));

        captionEditText = new EventEditText(context);
        captionEditText.setGravity(Gravity.BOTTOM);
        captionEditText.setHint(getString(R.string.type_message));
        captionEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        captionEditText.setMaxLines(4);
        captionEditText.setPadding(10, 0, 10, 8);

        return rootView;
    }

    @Override
    public View createToolBar(Context context) {
        return toolbar;
    }
}

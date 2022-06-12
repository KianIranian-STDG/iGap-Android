package net.iGap.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class EditContactFragment extends BaseFragment {

    public static final String NAME = "name";
    public static final String PHONE = "PHONE";
    private static final String CONTACT_MODE = "MODE";
    private static final String CONTACT_ID = "CONTACT_ID";
    private static final String CONTACT_NAME = "NAME";
    private static final String CONTACT_FAMILY = "FAMILY";

    private final boolean isRtl = G.isAppRtl;

    public static EditContactFragment newInstance(long contactId, Long phone, String name, String lastName) {
        EditContactFragment fragment = new EditContactFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CONTACT_ID, contactId);
        bundle.putString(CONTACT_NAME, name);
        bundle.putString(CONTACT_FAMILY, lastName);
        bundle.putLong(PHONE, phone);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createView(Context context) {

        Bundle bundle = this.getArguments();

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        CircleImageView avatar = new CircleImageView(context);
        avatar.setImageResource(R.drawable.avatar);
        frameLayout.addView(avatar, LayoutCreator.createFrame(80, 80, isRtl ? Gravity.RIGHT : Gravity.LEFT, 30, 30, 30, 0));

        TextView phoneNumber = new TextView(context);
        phoneNumber.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        phoneNumber.setSingleLine();
        phoneNumber.setTypeface(ResourcesCompat.getFont(context, R.font.main_font), Typeface.BOLD);
        phoneNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        phoneNumber.setText(String.format(isRtl ? "%d+" : "+%d", bundle.getLong(PHONE, 0)));
        phoneNumber.setTextColor(Theme.getColor(Theme.key_default_text));
        frameLayout.addView(phoneNumber, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 140, 40, isRtl ? 140 : 0, 0));

        TextView lastViewed = new TextView(context);
        lastViewed.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        lastViewed.setSingleLine();
        lastViewed.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        lastViewed.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        lastViewed.setText(getContext().getString(R.string.contact_last_view));
        lastViewed.setTextColor(Theme.getColor(Theme.key_default_text));
        frameLayout.addView(lastViewed, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 140, 75, isRtl ? 140 : 0, 0));

        EditText firstName = new EditText(context);
        firstName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        firstName.setSingleLine();
        firstName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        firstName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        firstName.setText(bundle.getString(CONTACT_NAME, ""));
        firstName.setFocusable(true);
        firstName.setTextColor(Theme.getColor(Theme.key_default_text));
        frameLayout.addView(firstName, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, 30, 130, 30, 0));

        EditText lastName = new EditText(context);
        lastName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        lastName.setSingleLine();
        lastName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        lastName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        lastName.setText(bundle.getString(CONTACT_FAMILY, ""));
        lastName.setFocusable(true);
        lastName.setTextColor(Theme.getColor(Theme.key_default_text));
        frameLayout.addView(lastName, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, 30, 180, 30, 0));

        return fragmentView;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        Toolbar toolbar = new Toolbar(context);
        toolbar.setTitle("Edit");
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        return toolbar;
    }

   /* @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> result = new ArrayList<>();
        result.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_tool_bar_default));
        return result;
    }*/
}

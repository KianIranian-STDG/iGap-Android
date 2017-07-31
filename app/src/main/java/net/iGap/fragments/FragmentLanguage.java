package net.iGap.fragments;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Locale;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivitySetting;
import net.iGap.helper.HelperCalander;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.onRefreshActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLanguage extends Fragment {

    private FragmentActivity mActivity;
    private SharedPreferences sharedPreferences;

    public FragmentLanguage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        ViewGroup root = (ViewGroup) view.findViewById(R.id.rootFragmentLanguage);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sharedPreferences = mActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stns_ripple_back);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.getSupportFragmentManager().popBackStack();
            }
        });

        TextView txtFa = (TextView) view.findViewById(R.id.txtLanguageFarsi);
        TextView iconFa = (TextView) view.findViewById(R.id.st_icon_fatsi);
        TextView txtEn = (TextView) view.findViewById(R.id.txtLanguageEn);
        TextView iconEn = (TextView) view.findViewById(R.id.st_icon_english);


        String textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());

        if (textLanguage.equals("English")) {
            iconEn.setVisibility(View.VISIBLE);
        } else if (textLanguage.equals("فارسی")) {
            iconFa.setVisibility(View.VISIBLE);
        } else if (textLanguage.equals("العربی")) {

        } else if (textLanguage.equals("Deutsch")) {
        }


        ViewGroup vgFa = (ViewGroup) view.findViewById(R.id.st_layout_fa);
        ViewGroup vgEn = (ViewGroup) view.findViewById(R.id.st_layout_english);

        vgEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!G.selectedLanguage.equals("en")) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SHP_SETTING.KEY_LANGUAGE, "English");
                    editor.apply();
                    setLocale("en");
                    HelperCalander.isLanguagePersian = false;

                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (onRefreshActivity != null) {
                                onRefreshActivity.refresh("en");
                            }
                        }
                    }, 100);

                    G.selectedLanguage = "en";
                }


                mActivity.getSupportFragmentManager().popBackStack();
            }
        });

        vgFa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!G.selectedLanguage.equals("fa")) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SHP_SETTING.KEY_LANGUAGE, "فارسی");
                    editor.apply();
                    G.selectedLanguage = "fa";
                    setLocale("fa");
                    HelperCalander.isLanguagePersian = true;

                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (onRefreshActivity != null) {
                                onRefreshActivity.refresh("fa");
                            }
                        }
                    }, 100);
                }


                mActivity.getSupportFragmentManager().popBackStack();
            }
        });

         /*
         choose language farsi or english ,arabic , .....
         */

    }

    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mActivity.getBaseContext().getResources().updateConfiguration(config, mActivity.getBaseContext().getResources().getDisplayMetrics());
        startActivity(new Intent(mActivity, ActivitySetting.class));
        mActivity.overridePendingTransition(0, 0);
        mActivity.finish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

}

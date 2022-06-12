package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.LanguageCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.customView.RecyclerListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageFragment extends BaseFragment {

    private final List<Language> languages = new ArrayList<>();
    private final String currentLanguage;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private final SharedPreferences sharedPreferences = getSharedManager().getSettingSharedPreferences();

    public LanguageFragment() {
        languages.add(new Language("فارسی", "fa", "Persian", 1, true, true, false));
        languages.add(new Language("English", "en", "English", 0, false, false, false));
        languages.add(new Language("العربی", "ar", "Arabic", 2, true, false, true));
        languages.add(new Language("Français", "fr", "French", 0, false, false, false));
        languages.add(new Language("русский", "ru", "Russian", 0, false, false, false));
        languages.add(new Language("کوردی", "ur", "Kurdi", 1, true, true, false));
        languages.add(new Language("آذری", "iw", "Azari", 1, true, true, false));
        languages.add(new Language("پشتو", "ps", "Pashto", 1, true, true, false));
        currentLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
    }

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        listView.setAdapter(listAdapter = new ListAdapter());
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (view instanceof LanguageCell) {
                changeLanguage(languages.get(position - 1));
                listAdapter.notifyItemChanged(position);
            }
        });
        return fragmentView;
    }

    private void changeLanguage(Language language) {
        if (!G.selectedLanguage.equals(language.getAbbreviation())) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, language.getName()).apply();
            G.selectedLanguage = language.getAbbreviation();
            HelperCalander.isPersianUnicode = language.isPersianUnicode();
            HelperCalander.isLanguagePersian = language.isLanguagePersian();
            HelperCalander.isLanguageArabic = language.isLanguageArabic();
            G.isAppRtl = language.isLanguagePersian();

            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }

            sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, language.getCalenderType()).apply();
            if (G.onDateChanged != null) {
                G.onDateChanged.onChange();
            }

            if (getActivity() instanceof ActivityEnhanced && language.getAbbreviation() != null) {
                G.updateResources(getActivity().getBaseContext());
                if (G.twoPaneMode) {
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(fragment);
                    fragmentTransaction.attach(fragment);
                    fragmentTransaction.commit();
                }
                getActivity().onBackPressed();
            }
        } else {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), this).remove();
            }
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.Language));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    public static class Language {
        private final String name;
        private final String abbreviation;
        private final String description;
        private final int calenderType;
        private final boolean isPersianUnicode;
        private final boolean isLanguagePersian;
        private final boolean isLanguageArabic;

        public Language(String name, String abbreviation, String description, int calenderType, boolean isPersianUnicode, boolean isLanguagePersian, boolean isLanguageArabic) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.description = description;
            this.calenderType = calenderType;
            this.isPersianUnicode = isPersianUnicode;
            this.isLanguagePersian = isLanguagePersian;
            this.isLanguageArabic = isLanguageArabic;
        }

        public String getName() {
            return name;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public String getDescription() {
            return description;
        }

        public int getCalenderType() {
            return calenderType;
        }

        public boolean isPersianUnicode() {
            return isPersianUnicode;
        }

        public boolean isLanguagePersian() {
            return isLanguagePersian;
        }

        public boolean isLanguageArabic() {
            return isLanguageArabic;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType != 0 && viewType != 9;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(context);
                    break;
                default:
                    view = new LanguageCell(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            if (viewType == 0) {
                HeaderCell headerCell = (HeaderCell) holder.itemView;
                headerCell.setText(getString(R.string.selectLanguage));
            } else {
                LanguageCell languageCell = (LanguageCell) holder.itemView;
                Language language = languages.get(position - 1);
                languageCell.setLanguage(language.getDescription(), language.getName(), true);
                languageCell.setLanguageSelected(currentLanguage.equals(language.getName()));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 9;
        }
    }
}

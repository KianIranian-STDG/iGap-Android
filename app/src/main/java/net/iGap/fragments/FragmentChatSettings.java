package net.iGap.fragments;

import android.app.ActivityManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.ThemeColorListAdapter;
import net.iGap.databinding.FragmentChatSettingsBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StatusBarUtil;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentChatSettingViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class FragmentChatSettings extends BaseFragment {

    private FragmentChatSettingViewModel viewModel;
    private FragmentChatSettingsBinding binding;
    private ThemeColorListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentChatSettingViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE));
            }
        }).get(FragmentChatSettingViewModel.class);
        adapter = new ThemeColorListAdapter((oldThemePosition, newThemePosition) -> viewModel.setTheme(oldThemePosition, newThemePosition));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTheme();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_settings, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }

        binding.fcsLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.chat_setting))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

        binding.themeColorList.setLayoutManager(new LinearLayoutManager(binding.themeColorList.getContext(), RecyclerView.HORIZONTAL, G.isAppRtl));
        binding.themeColorList.hasFixedSize();
        binding.themeColorList.setNestedScrollingEnabled(false);
        binding.themeColorList.setAdapter(adapter);

        viewModel.getChatBackground();
        setChatReceivedChatBubble(new Theme().getReceivedChatBubbleColor(getContext()));
        setChatSendBubble(new Theme().getSendChatBubbleColor(getContext()));

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }

        viewModel.getGoToChatBackgroundPage().observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentChatBackground()).setReplace(false).load();
            }
        });

        viewModel.getGoToDateFragment().observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go)
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentData()).setReplace(false).load();
        });

        viewModel.getThemeList().observe(getViewLifecycleOwner(), themeList -> {
            if (themeList != null && binding.themeColorList.getAdapter() instanceof ThemeColorListAdapter) {
                ((ThemeColorListAdapter) binding.themeColorList.getAdapter()).setData(themeList);
            }
        });

        viewModel.getSelectedThemePosition().observe(getViewLifecycleOwner(), selectedPosition -> {
            if (selectedPosition != null && binding.themeColorList.getAdapter() instanceof ThemeColorListAdapter) {
                ((ThemeColorListAdapter) binding.themeColorList.getAdapter()).setSelectedTheme(selectedPosition);
            }
        });

        viewModel.getUpdateNewTheme().observe(getViewLifecycleOwner(), isUpdate -> {
            if (getActivity() != null && isUpdate != null && isUpdate) {
                /*if (Theme.isUnderLollipop()) {
                    if (getActivity() instanceof ActivityEnhanced) {
                        ((ActivityEnhanced) getActivity()).onRefreshActivity(true, "");
                    }
                } else {*/
                Fragment frg;
                frg = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChatSettings.class.getName());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
                /*}*/
            }
        });

        viewModel.getUpdateTwoPaneView().observe(getViewLifecycleOwner(), isUpdate -> {
            if (getActivity() != null && isUpdate != null && isUpdate) {
                Fragment frg;
                frg = getActivity().getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
            }
        });

        viewModel.getUpdateTextSizeSampleView().observe(getViewLifecycleOwner(), textSize -> {
            if (textSize != null) {
                binding.message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                binding.senderMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
            }
        });

        viewModel.getSetChatBackground().observe(getViewLifecycleOwner(), backgroundPath -> {
            if (getActivity() != null && backgroundPath != null) {
                File f = new File(backgroundPath);
                if (f.exists()) {
                    try {
                        Log.wtf(this.getClass().getName(), "set image");
                        Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                        binding.chatBackgroundImage.setImageDrawable(d);
                    } catch (OutOfMemoryError e) {
                        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
                        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                        activityManager.getMemoryInfo(memoryInfo);
                    }
                } else {
                    try {
                        binding.chatBackgroundImage.setImageResource(0);
                        binding.chatBackgroundImage.setBackgroundColor(Color.parseColor(backgroundPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        viewModel.getSetChatBackgroundDefault().observe(getViewLifecycleOwner(), drawableRes -> {
            if (drawableRes != null) {
                binding.chatBackgroundImage.setImageResource(R.drawable.chat_default_background_pattern);
            }
        });
    }

    public void dateIsChanged() {
        viewModel.dateIsChange();
    }

    public Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    private void setChatReceivedChatBubble(int color) {
        binding.receivedChatItem.setBackground(tintDrawable(binding.receivedChatItem.getBackground(), ColorStateList.valueOf(color)));
    }

    private void setChatSendBubble(int color) {
        binding.sendChatItem.setBackground(tintDrawable(binding.sendChatItem.getBackground(), ColorStateList.valueOf(color)));
    }

    private void setTheme() {
        if (getContext() != null) {
            getContext().getTheme().applyStyle(new Theme().getTheme(getContext()), true);
        }
    }

    public void chatBackgroundChange() {
        viewModel.getChatBackground();
    }
}

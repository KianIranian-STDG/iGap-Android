package net.iGap.fragments;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import net.iGap.Theme;
import net.iGap.adapter.ThemeColorListAdapter;
import net.iGap.databinding.FragmentChatSettingsBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StatusBarUtil;
import net.iGap.viewmodel.FragmentChatSettingViewModel;

import org.jetbrains.annotations.NotNull;

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
        adapter = new ThemeColorListAdapter(position -> viewModel.setTheme(position));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTheme();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_settings, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fcsLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
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

        setChatReceivedChatBubble(new Theme().getReceivedChatBubbleColor(getContext()));
        setChatSendBubble(new Theme().getSendChatBubbleColor(getContext()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue tV = new TypedValue();
            TypedArray aa = getContext().obtainStyledAttributes(tV.data, new int[]{R.attr.colorPrimaryDark});
            int clr = aa.getColor(0, 0);
            aa.recycle();
            StatusBarUtil.setColor(getActivity(), clr, 50);
        }

        viewModel.getGoToChatBackgroundPage().observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentChatBackground.newInstance()).setReplace(false).load();
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
                Fragment frg;
                frg = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChatSettings.class.getName());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
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
    }

    public void dateIsChanged() {
        viewModel.dateIsChange();
    }

    public Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    public void setChatReceivedChatBubble(int color) {
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
}

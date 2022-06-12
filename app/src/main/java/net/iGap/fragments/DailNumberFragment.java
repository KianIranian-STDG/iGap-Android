package net.iGap.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import net.iGap.R;
import net.iGap.databinding.FragmentDailNumberBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.DailNumberViewModel;

public class DailNumberFragment extends Fragment {

    private FragmentDailNumberBinding binding;
    private DailNumberViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dail_number, container, false);

        viewModel = new DailNumberViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.zero.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.one.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.two.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.three.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.four.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.five.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.six.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.seven.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.eight.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.nine.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.eraseNumber.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.star.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        binding.btnVideoCall.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)));
        binding.btnVideoCall.setTextColor(Theme.getColor(Theme.key_white));
        HelperToolbar t = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }});

        binding.toolbar.addView(t.getView());
    }
}

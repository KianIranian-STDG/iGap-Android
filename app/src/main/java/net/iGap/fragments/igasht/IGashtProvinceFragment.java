package net.iGap.fragments.igasht;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.card.MaterialCardView;

import net.iGap.R;
import net.iGap.adapter.igahst.ProvinceSuggestionListAdapter;
import net.iGap.databinding.FragmentIgashtProvinceBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.igasht.IGashtProvinceViewModel;

public class IGashtProvinceFragment extends IGashtBaseView<IGashtProvinceViewModel> {

    private FragmentIgashtProvinceBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtProvinceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_province, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.frameLayout.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.background_button_card_to_card), context, Theme.getColor(Theme.key_theme_color)));
        binding.frameLayout.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        MaterialCardView bottom_view = view.findViewById(R.id.bottom_view);
        bottom_view.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_time)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }

                }).getView());

        binding.provinceSearchText.setOnItemClickListener((parent, view1, position, id) -> viewModel.setSelectedLocation(position));

        viewModel.getGoToShowLocationListPage().observe(getViewLifecycleOwner(), isGo -> {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (getActivity() != null && isGo != null) {
                if (isGo) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtLocationListFragment()).setReplace(false).load(true);
                } else {
                    Toast.makeText(getContext(), R.string.select_province, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getProvinceListResult().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.provinceSearchText.setAdapter(new ProvinceSuggestionListAdapter(getContext(), data));
                binding.provinceSearchText.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            }
        });

        viewModel.getClearEditText().observe(getViewLifecycleOwner(), isClear -> {
            if (isClear != null && isClear) {
                binding.provinceSearchText.requestFocus();
                binding.provinceSearchText.setText("");
            }
        });
    }

}

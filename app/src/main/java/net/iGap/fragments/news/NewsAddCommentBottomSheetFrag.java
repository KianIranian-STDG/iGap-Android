package net.iGap.fragments.news;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.NewsAddAccountBottomSheetDialogBinding;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.viewmodel.news.NewsAddCommentVM;

public class NewsAddCommentBottomSheetFrag extends BaseBottomSheet {

    private NewsAddAccountBottomSheetDialogBinding binding;
    private NewsAddCommentVM addCommentVM;
    private CompleteListener completeListener;
    private String newsID;

    public NewsAddCommentBottomSheetFrag setData(String newsID, CompleteListener completeListener) {
        this.completeListener = completeListener;
        this.newsID = newsID;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addCommentVM = ViewModelProviders.of(this).get(NewsAddCommentVM.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.news_add_account_bottom_sheet_dialog, container, false);
        binding.setBottomSheetViewModel(addCommentVM);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addCommentVM.setNewsID(newsID);
        binding.author.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        binding.email.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        binding.comment.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        View lineViewTop = view.findViewById(R.id.lineViewTop);
        lineViewTop.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_sheet_dialog_line), getContext(), Theme.getColor(Theme.key_theme_color)));
        onComplete();
        onTextChange();
    }

    private void onComplete() {
        addCommentVM.getComplete().observe(getViewLifecycleOwner(), aBoolean -> {

            completeListener.onCompleted(aBoolean);
            this.dismiss();

        });
    }

    private void onTextChange() {
        binding.authorET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.author.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.email.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.comment.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    interface CompleteListener {
        void onCompleted(boolean result);
    }
}

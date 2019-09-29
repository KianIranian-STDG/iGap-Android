package net.iGap.news.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.databinding.NewsAddAccountBottomSheetDialogBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.BottomSheetListAdapter;
import net.iGap.dialog.payment.CompleteListener;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.viewmodel.NewsAddCommentVM;

import java.util.ArrayList;
import java.util.List;

public class NewsAddCommentBottomSheetFrag extends BottomSheetDialogFragment {

    private NewsAddAccountBottomSheetDialogBinding binding;
    private NewsAddCommentVM addCommentVM;
    private CompleteListener completeListener;
    private String newsID;

    public NewsAddCommentBottomSheetFrag setData(String newsID, CompleteListener completeListener) {
        this.completeListener = completeListener;
        this.newsID = newsID;
        return this;
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
        onComplete();
        onTextChange();
    }

    private void onComplete() {
        addCommentVM.getComplete().observe(getViewLifecycleOwner(), aBoolean -> {

            if (aBoolean)
                completeListener.onCompleted();
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
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }
}

package net.iGap.kuknos.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosRegulationBottomSheetBinding;
import net.iGap.helper.HelperUrl;
import net.iGap.module.dialog.BaseBottomSheet;

import org.jetbrains.annotations.NotNull;

public class KuknosRegulationsBottomSheetFrag extends BaseBottomSheet {

    private FragmentKuknosRegulationBottomSheetBinding binding;
    private CompleteListener completeListener;

    public static KuknosRegulationsBottomSheetFrag newInstance(String link, CompleteListener listener) {
        KuknosRegulationsBottomSheetFrag frag = new KuknosRegulationsBottomSheetFrag(listener);
        Bundle bundle = new Bundle();
        bundle.putString("tokenRegLink", link);
        frag.setArguments(bundle);
        return frag;
    }

    private KuknosRegulationsBottomSheetFrag(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_regulation_bottom_sheet, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String t = String.format(getString(R.string.terms_and_condition), getString(R.string.terms_and_condition_clickable));
        SpannableString ss = new SpannableString(t);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View textView) {
                HelperUrl.openWebBrowser(getContext(), getArguments().getString("tokenRegLink"));
            }

            @Override
            public void updateDrawState(@NotNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, t.indexOf(getString(R.string.terms_and_condition_clickable)), t.indexOf(getString(R.string.terms_and_condition_clickable)) + getString(R.string.terms_and_condition_clickable).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.termsAndConditionText.setText(ss);
        binding.termsAndConditionText.setMovementMethod(LinkMovementMethod.getInstance());
        binding.termsAndConditionText.setHighlightColor(Color.TRANSPARENT);

        binding.nextBtn.setOnClickListener(v -> {
            completeListener.onCompleted(binding.termsAndConditionCheck.isChecked());
            this.dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    interface CompleteListener {
        void onCompleted(Boolean accepted);
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }
}

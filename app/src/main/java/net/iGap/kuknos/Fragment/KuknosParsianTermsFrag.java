package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosParsianTermsBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosParsianTermsVM;

public class KuknosParsianTermsFrag extends BaseAPIViewFrag<KuknosParsianTermsVM> {

    private FragmentKuknosParsianTermsBinding binding;

    public static KuknosParsianTermsFrag newInstance() {
        return new KuknosParsianTermsFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosParsianTermsVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_parsian_terms, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosBuyPToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        onNext();
        onError();
        onProgress();
    }

    private void onNext() {
        viewModel.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean goNext) {
                if (goNext) {
                    KuknosParsianTermsFrag.this.getParentFragmentManager().popBackStack();
                }
            }
        });
    }

    private void onError() {
        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState() && errorM.getMessage().equals("1")) {
                showDialog(errorM.getResID());
            } else {
                showDialog(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle), errorM.getMessage());
            }
        });
    }

    private void showDialog(int messageResource) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.kuknos_viewRecoveryEP_failTitle)
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(getResources().getString(messageResource)).show();
    }

    private void showDialog(String title, String message) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(message)
                .show();
    }

    private void onProgress() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                binding.fragKuknosBuyPProgressV.setVisibility(View.GONE);
                binding.fragKuknosBuyPSubmit.setText(getResources().getText(R.string.i_agree_with_the_terms));
            } else if (integer == 1) {
                binding.fragKuknosBuyPProgressV.setVisibility(View.VISIBLE);
                binding.fragKuknosBuyPSubmit.setText(getResources().getText(R.string.kuknos_buyP_btn_server));
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
        return true;
    }
}

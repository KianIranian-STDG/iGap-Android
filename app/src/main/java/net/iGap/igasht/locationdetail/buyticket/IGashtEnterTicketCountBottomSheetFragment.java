package net.iGap.igasht.locationdetail.buyticket;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentIgashtTicketCountBottomSheetDialogBinding;

public class IGashtEnterTicketCountBottomSheetFragment extends BottomSheetDialogFragment {

    private IGashtEnterTicketCountBottomViewModel viewModel;
    private FragmentIgashtTicketCountBottomSheetDialogBinding binding;
    private SetTicketCountCallBack callBack;

    public IGashtEnterTicketCountBottomSheetFragment setCallBack(SetTicketCountCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtEnterTicketCountBottomViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_ticket_count_bottom_sheet_dialog, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ticketCountEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Your action on done
                viewModel.onSubmitTicketCountClick(v.getText().toString());
                return true;
            }
            return false;
        });

        viewModel.getTicketCount().observe(getViewLifecycleOwner(), ticketCount -> {
            if (ticketCount != null) {
                callBack.setTicketCount(ticketCount);
                dismiss();
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
        return new BottomSheetDialog(requireContext(), getTheme()) {
            @Override
            public void onBackPressed() {
                Log.wtf(this.getClass().getName(), "onBackPressed");
                callBack.onBackPressed();
                dismiss();
            }
        };
    }

    public interface SetTicketCountCallBack {
        void setTicketCount(int ticketCount);

        void onBackPressed();
    }
}

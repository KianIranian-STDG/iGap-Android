package net.iGap.igasht.locationdetail.buyticket;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;

import java.util.List;

public class SelectTicketTypeBottomSheetFragment extends BottomSheetDialogFragment {

    private List<IGashtServiceAmount> itemList;
    private AddBottomSheetListener<IGashtServiceAmount> callBack;

    public SelectTicketTypeBottomSheetFragment setData(List<IGashtServiceAmount> itemList, AddBottomSheetListener<IGashtServiceAmount> callBack) {
        this.itemList = itemList;
        this.callBack = callBack;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);

        binding.bottomSheetList.setAdapter(new TicketTypeListAdapter(itemList, position -> {
            callBack.setTicketCount(itemList.get(position));
            dismiss();
        }));
        binding.title.setVisibility(View.VISIBLE);
        binding.title.setText(R.string.igasht_ticket_type_title);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                callBack.onBackPressed();
                super.onBackPressed();
            }
        };
    }
}

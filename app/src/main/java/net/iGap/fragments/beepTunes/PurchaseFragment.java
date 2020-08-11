package net.iGap.fragments.beepTunes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

public class PurchaseFragment extends BaseFragment implements ToolbarListener {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beeptunes_purchase, container);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(this)
                .setDefaultTitle("لیست دانلود ها");

        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_purchase_tollBar);
        toolBarContainer.addView(toolbar.getView());


    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }
}

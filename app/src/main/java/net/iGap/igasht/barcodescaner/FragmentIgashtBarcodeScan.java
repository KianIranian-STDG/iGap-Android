package net.iGap.igasht.barcodescaner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.igasht.IGashtBaseView;

public class FragmentIgashtBarcodeScan extends IGashtBaseView {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(G.fragmentActivity).inflate(R.layout.fragment_igasht_barcode_scaner,container,false);
        return view;
    }
}

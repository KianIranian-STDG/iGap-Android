package net.iGap.kuknos.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;

import static android.content.Context.CLIPBOARD_SERVICE;


public class KuknosEquivalentRialFrag extends BaseFragment {
    private Button submit;

    public static KuknosEquivalentRialFrag newInstance() {
        KuknosEquivalentRialFrag fragment = new KuknosEquivalentRialFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kuknos_equivalent_rial, container, false);
        submit = view.findViewById(R.id.fragKuknosRialSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
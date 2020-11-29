package net.iGap.kuknos.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.helper.HelperFragment;


public class KuknosGetSupportFrag extends Fragment {

    private Button submit;

    public static KuknosGetSupportFrag newInstance() {
        KuknosGetSupportFrag fragment = new KuknosGetSupportFrag();
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
        View view= inflater.inflate(R.layout.fragment_kuknos_get_support, container, false);
        submit=view.findViewById(R.id.fragKuknosgetSupportSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),getString(R.string.kuknos_getsupport_toast),Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }


}
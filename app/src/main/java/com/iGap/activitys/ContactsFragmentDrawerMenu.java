package com.iGap.activitys;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.CountryNamesAdapter;
import com.iGap.libs.flowingdrawer.MenuFragment;

import java.util.ArrayList;


public class ContactsFragmentDrawerMenu extends MenuFragment {

    private ViewHolder mViews;
    EditText editsearch;
    private CountryNamesAdapter mAdapter;

    private ArrayList<CountryNamesAdapter.LineItem> mItems = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.contacts_drawer_layout, container, false);

        editsearch = (EditText)rootView.findViewById(R.id.edit_search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                mItems.clear();
                mItems = G.Retrive(editsearch.getText().toString().trim());
                mAdapter = new CountryNamesAdapter(getActivity(),mItems);
                mViews.setAdapter(mAdapter);

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });


        mItems = G.Retrive("");

        return setupReveal(rootView, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);
        mViews.initViews(new LinearLayoutManager(getActivity()));
        mAdapter = new CountryNamesAdapter(getActivity(),mItems);
        mViews.setAdapter(mAdapter);
    }

    private static class ViewHolder {

        private final RecyclerView mRecyclerView;



        public ViewHolder(View view) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        }

        public void initViews(LinearLayoutManager lm) {
            mRecyclerView.setLayoutManager(lm);




        }



        public void setAdapter(RecyclerView.Adapter<?> adapter) {
            try {
                mRecyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

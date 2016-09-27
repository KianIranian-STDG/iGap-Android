package com.iGap.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.StructSharedMedia;
import com.iGap.module.TouchImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by android3 on 9/5/2016.
 */
public class FragmentShowImage extends Fragment {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;

    private ViewPager viewPager;

    private ArrayList<StructSharedMedia> list;
    private int selectedFile = 0;
    private int listSize = 0;


    public static FragmentShowImage newInstance() {
        return new FragmentShowImage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_show_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getIntentData(this.getArguments());

        initComponent(view);
    }


    private void getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image
            list = (ArrayList<StructSharedMedia>) bundle.getSerializable("listPic");
            if (list == null) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return;
            }
            if (list.size() < 1) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return;
            }

            int si = bundle.getInt("SelectedImage");
            if (si >= 0)
                selectedFile = si;

        } else {
            getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
            return;
        }
    }

    private void initComponent(View view) {

        Button btnBack = (Button) view.findViewById(R.id.asi_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "close");

                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();


            }
        });


        Button btnMenu = (Button) view.findViewById(R.id.asi_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpMenuShowImage();
            }
        });

        viewPager = (ViewPager) view.findViewById(R.id.asi_view_pager);

        txtImageNumber = (TextView) view.findViewById(R.id.asi_txt_image_number);
        txtImageName = (TextView) view.findViewById(R.id.asi_txt_image_name);
        txtImageDate = (TextView) view.findViewById(R.id.asi_txt_image_date);


        initViewPager();

    }


    //***************************************************************************************

    private void initViewPager() {


        AdapterViewPager mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);
        listSize = list.size();
        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " of " + listSize);
//        txtImageName.setText(list.get(selectedFile).fileName);
//        txtImageDate.setText(list.get(selectedFile).fileTime);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                txtImageNumber.setText(position + 1 + " of " + listSize);
                txtImageName.setText(list.get(position).fileName);
                txtImageDate.setText(list.get(position).fileTime);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class AdapterViewPager extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }


        @Override
        public Object instantiateItem(View container, int position) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);


            TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);

            //TODO     nejati         if file not exsit download it and than show it


            File imgFile = new File(list.get(position).filePath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                touchImageView.setImageBitmap(myBitmap);
            }


            ((ViewGroup) container).addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //***************************************************************************************

    public void popUpMenuShowImage() {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .items(R.array.pop_up_menu_show_image)
                .contentColor(Color.BLACK)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (which == 0) {
                            showAllMedia();
                        } else if (which == 1) {
                            saveToGalary();
                        }


                    }
                }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp200);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void showAllMedia() {
        Log.e("ddd", "show all media");
    }

    private void saveToGalary() {
        Log.e("ddd", "save to galarry");
    }


}

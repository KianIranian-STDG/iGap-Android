package com.iGap.activitys;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;

/**
 * Created by android3 on 9/5/2016.
 */
public class ActivityShowImage extends ActivityEnhanced {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;

    private ViewPager viewPager;

    private ArrayList<StructSharedMedia> list;
    private int selectedFile = 0;
    private int listSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntentData(getIntent().getExtras());

        setContentView(R.layout.activity_show_image);

        initComponent();

    }

    private void getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image
            list = (ArrayList<StructSharedMedia>) getIntent().getSerializableExtra("listPic");
            if (list == null) {
                finish();
                return;
            }
            if (list.size() < 1) {
                finish();
                return;
            }

            int si = bundle.getInt("SelectedImage");
            if (si >= 0)
                selectedFile = si;

        } else {
            finish();
            return;
        }
    }

    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.asi_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button btnMenu = (Button) findViewById(R.id.asi_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpMenuShowImage();
            }
        });


        txtImageNumber = (TextView) findViewById(R.id.asi_txt_image_number);
        txtImageName = (TextView) findViewById(R.id.asi_txt_image_name);
        txtImageDate = (TextView) findViewById(R.id.asi_txt_image_date);


        initViewPager();

    }


    //***************************************************************************************

    private void initViewPager() {

        viewPager = (ViewPager) findViewById(R.id.asi_view_pager);
        AdapterViewPager mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);
        listSize = list.size();
        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " of " + listSize);
        txtImageName.setText(list.get(selectedFile).fileName);
        txtImageDate.setText(list.get(selectedFile).fileTime);

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

            LayoutInflater inflater = LayoutInflater.from(ActivityShowImage.this);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);


            TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);

            //TODO     nejati         if file not exsit download it and than show it

            touchImageView.setImageResource(Integer.parseInt(list.get(position).filePath));


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

        MaterialDialog dialog = new MaterialDialog.Builder(this)
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

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
import com.iGap.activitys.ActivitySetting;
import com.iGap.module.StructSharedMedia;
import com.iGap.module.TouchImageView;
import com.iGap.realm.RealmAvatarPath;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;


public class FragmentShowImage extends Fragment {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;

    private ViewPager viewPager;

    private ArrayList<StructSharedMedia> list;
    private int selectedFile = 0;
    private int listSize = 0;
    private float MIN_SCALE = 1;
    private AdapterViewPager mAdapter;


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

        if (getIntentData(this.getArguments()))
            initComponent(view);
    }

    private boolean getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image
            list = (ArrayList<StructSharedMedia>) bundle.getSerializable("listPic");
            if (list == null) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }
            if (list.size() < 1) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }

            int si = bundle.getInt("SelectedImage");
            if (si >= 0)
                selectedFile = si;

            return true;

        } else {
            getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
            return false;
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
                ((ActivitySetting) getActivity()).setImage();
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

        mAdapter = new AdapterViewPager();
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

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                view.setScaleX(normalizedposition / 2 + 0.5f);
                view.setScaleY(normalizedposition / 2 + 0.5f);
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
            } else {
                File imgThumpnailFile = new File(list.get(position).tumpnail);
                if (imgThumpnailFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgThumpnailFile.getAbsolutePath());
                    touchImageView.setImageBitmap(myBitmap);
                }

                downloadImage();

            }

            ((ViewGroup) container).addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            int index = list.indexOf(object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

        public int removeView(ViewPager pager, StructSharedMedia v) {
            return removeView(pager, list.indexOf(v));
        }

        //-----------------------------------------------------------------------------

        public int removeView(ViewPager pager, int position) {

            Realm realm = Realm.getDefaultInstance();
            final RealmAvatarPath realmAvatarPath = realm.where(RealmAvatarPath.class).equalTo("id", list.get(position).id).findFirst();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmAvatarPath.deleteFromRealm();
                }
            });
            realm.close();
            pager.setAdapter(null);
            list.remove(position);
            pager.setAdapter(this);
            notifyDataSetChanged();
            if (list.size() > 0) {
                initViewPager();
            }
            return position;
        }

        public StructSharedMedia getView(int position) {
            return list.get(position);
        }

    }

    //***************************************************************************************

    private void downloadImage() {

        Log.e("ddd", " download image");
    }

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
                        } else if (which == 2) {

                            int pageIndex = mAdapter.removeView(viewPager, getCurrentPage());
                            if (list.size() == 0) {
                                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                                ((ActivitySetting) getActivity()).setImage();
                            } else if (pageIndex == mAdapter.getCount()) {
                                pageIndex--;
                            }
                            viewPager.setCurrentItem(pageIndex);
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


    public StructSharedMedia getCurrentPage() {
        return mAdapter.getView(viewPager.getCurrentItem());
    }

    //-----------------------------------------------------------------------------
//    public void setCurrentPage(View pageToShow) {
//        viewPager.setCurrentItem(mAdapter.getItemPosition(pageToShow), true);
//    }


}

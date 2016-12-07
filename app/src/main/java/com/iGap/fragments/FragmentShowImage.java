package com.iGap.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterShearedMedia;
import com.iGap.helper.HelperSaveFile;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.TimeUtils;
import com.iGap.module.TouchImageView;
import com.iGap.proto.ProtoGlobal;

import java.io.File;
import java.util.ArrayList;

import io.meness.github.messageprogress.MessageProgress;

public class FragmentShowImage extends Fragment {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;
    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;

    private ArrayList<ProtoGlobal.RoomMessage> list;
    private int selectedFile = 0;
    private int listSize = 0;
    private AdapterViewPager mAdapter;
    private long peerId = 0;


    public static View appBarLayout;

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
        if (getIntentData(this.getArguments())) initComponent(view);
    }


    @Override
    public void onDetach() {
        if (appBarLayout != null)
            appBarLayout.setVisibility(View.VISIBLE);

        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        if (appBarLayout != null)
            appBarLayout.setVisibility(View.GONE);

        super.onAttach(context);
    }


    private boolean getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image

            peerId = bundle.getLong("PeedId");

            list = (ArrayList<ProtoGlobal.RoomMessage>) bundle.getSerializable("listPic");
            if (list == null) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }
            if (list.size() < 1) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }

            int si = bundle.getInt("SelectedImage");
            if (si >= 0) selectedFile = si;

            return true;
        } else {
            getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
            return false;
        }
    }

    private void initComponent(View view) {

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_menu);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.asi_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                popUpMenuShowImage();
            }
        });
        viewPager = (ViewPager) view.findViewById(R.id.asi_view_pager);

        txtImageNumber = (TextView) view.findViewById(R.id.asi_txt_image_number);
        txtImageName = (TextView) view.findViewById(R.id.asi_txt_image_name);
        ltImageName = (ViewGroup) view.findViewById(R.id.asi_layout_image_name);
        txtImageDate = (TextView) view.findViewById(R.id.asi_txt_image_date);
        toolbarShowImage = (LinearLayout) view.findViewById(R.id.toolbarShowImage);


        initViewPager();
    }

    //***************************************************************************************

    private void initViewPager() {

        mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);
        listSize = list.size();
        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " " + getString(R.string.of) + " " + listSize);
        if (list.get(selectedFile).getAttachment() != null) {
            txtImageName.setText(list.get(selectedFile).getAttachment().getName());
        }
        if (list.get(selectedFile).getUpdateTime() != 0) {
            txtImageDate.setText(TimeUtils.toLocal(list.get(selectedFile).getUpdateTime(), G.CHAT_MESSAGE_TIME));
        }

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ProtoGlobal.RoomMessage sharedMedia = list.get(position);

                txtImageNumber.setText(position + 1 + " " + getString(R.string.of) + " " + listSize);

                if (sharedMedia.getAttachment() != null) {
                    txtImageName.setText(sharedMedia.getAttachment().getName());
                }

                if (sharedMedia.getUpdateTime() != 0) {
                    txtImageDate.setText(TimeUtils.toLocal(sharedMedia.getUpdateTime(), G.CHAT_MESSAGE_TIME));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {

                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                view.setScaleX(normalizedPosition / 2 + 0.5f);
                view.setScaleY(normalizedPosition / 2 + 0.5f);
            }
        });
    }


    public void popUpMenuShowImage() {

        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.pop_up_menu_show_image)
                        .contentColor(Color.BLACK)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which == 0) {
                                    saveToGalary();
                                } else if (which == 1) {
                                    shareImage();
                                }
                            }
                        })
                        .show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp200);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void shareImage() {
        ProtoGlobal.RoomMessage media = list.get(viewPager.getCurrentItem());
        if (media != null) {
            String path = AdapterShearedMedia.getFilePath(media.getAttachment().getToken(), media.getAttachment().getName(), media.getMessageType());
            File file = new File(path);
            if (file.exists()) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "iGap/download this image");
                Uri screenshotUri = Uri.parse(path);

                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, getString(R.string.share_image_from_igap)));
            }
        }
    }

    private void saveToGalary() {

        ProtoGlobal.RoomMessage media = list.get(viewPager.getCurrentItem());
        if (media != null) {
            String path = AdapterShearedMedia.getFilePath(media.getAttachment().getToken(), media.getAttachment().getName(), media.getMessageType());
            File file = new File(path);
            if (file.exists()) {
                HelperSaveFile.savePicToGallary(path);
            }
        }
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
            final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);

            TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);

            MessageProgress progress = (MessageProgress) layout.findViewById(R.id.progress);


            ProtoGlobal.RoomMessage media = list.get(position);
            if (media != null) {
                String path = AdapterShearedMedia.getFilePath(media.getAttachment().getToken(), media.getAttachment().getName(), media.getMessageType());
                File file = new File(path);
                if (file.exists()) {
                    touchImageView.setImageURI(Uri.fromFile(file));
                    progress.setVisibility(View.GONE);
                } else {
                    path = AdapterShearedMedia.getThumpnailPath(media.getAttachment().getToken(), media.getAttachment().getName());
                    file = new File(path);
                    if (file.exists()) {
                        touchImageView.setImageURI(Uri.fromFile(file));
                    }
                }
            }

            progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("ddd", "download started");
                }
            });

            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShowToolbar) {
                        toolbarShowImage.animate().setDuration(150).alpha(0F).start();
                        ltImageName.setVisibility(View.GONE);
                        ltImageName.animate().setDuration(150).alpha(0F).start();
                        toolbarShowImage.setVisibility(View.GONE);
                        isShowToolbar = false;
                    } else {
                        toolbarShowImage.animate().setDuration(150).alpha(1F).start();
                        toolbarShowImage.setVisibility(View.VISIBLE);
                        ltImageName.animate().setDuration(150).alpha(1F).start();
                        ltImageName.setVisibility(View.VISIBLE);
                        isShowToolbar = true;
                    }
                }
            });

            ((ViewGroup) container).addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


}
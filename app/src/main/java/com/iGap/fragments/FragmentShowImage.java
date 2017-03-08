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
import android.support.v4.widget.ContentLoadingProgressBar;
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
import com.iGap.helper.HelperDownloadFile;
import com.iGap.helper.HelperSaveFile;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.TimeUtils;
import com.iGap.module.TouchImageView;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.github.meness.emoji.EmojiTextView;
import io.meness.github.messageprogress.MessageProgress;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.util.ArrayList;

import static com.iGap.module.AndroidUtils.suitablePath;

public class FragmentShowImage extends Fragment {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;
    private EmojiTextView txtImageDesc;
    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;

    private int selectedFile = 0;
    private AdapterViewPager mAdapter;
    private RealmResults<RealmRoomMessage> mRealmList;

    private ArrayList<RealmRoomMessage> mFList = new ArrayList<>();

    private Long mRoomid;
    private String selectedFileToken = "";
    private Realm mRealm;

    public static ArrayList<String> downloadedList = new ArrayList<>();

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
    public void onDestroyView() {
        super.onDestroyView();

        if (appBarLayout != null) appBarLayout.setVisibility(View.VISIBLE);

        if (mRealm != null) mRealm.close();
    }

    @Override
    public void onAttach(Context context) {
        if (appBarLayout != null) appBarLayout.setVisibility(View.GONE);

        super.onAttach(context);
    }

    private boolean getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image

            mRoomid = bundle.getLong("RoomId");
            selectedFileToken = bundle.getString("SelectedImage");

            if (mRoomid == null) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }

            mRealm = Realm.getDefaultInstance();

            mRealmList = mRealm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomid).findAll();


            if (mRealmList.size() < 1) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }

            for (RealmRoomMessage item : mRealmList) {

                boolean isImage = false;

                if (item.getMessageType().toString().contains(ProtoGlobal.RoomMessageType.IMAGE.toString())) {
                    isImage = true;
                } else if (item.getForwardMessage() != null) {
                    if (item.getForwardMessage().getMessageType().toString().contains(ProtoGlobal.RoomMessageType.IMAGE.toString())) {
                        isImage = true;
                    }
                }

                if (isImage) mFList.add(item);
            }





            if (selectedFileToken != null) {
                for (int i = 0; i < mFList.size(); i++) {

                    RealmAttachment attachment = mFList.get(i).getForwardMessage() != null ? mFList.get(i).getForwardMessage().getAttachment() : mFList.get(i).getAttachment();

                    if (attachment != null) {
                        if (selectedFileToken.equals(attachment.getToken())) {
                            selectedFile = i;
                            break;
                        }
                    }
                }
            }

            return true;
        } else {
            getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
            return false;
        }
    }

    private void initComponent(View view) {

        downloadedList.clear();

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().onBackPressed();
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
        txtImageDesc = (EmojiTextView) view.findViewById(R.id.asi_txt_image_desc);
        toolbarShowImage = (LinearLayout) view.findViewById(R.id.toolbarShowImage);

        initViewPager();
    }

    //***************************************************************************************

    private void initViewPager() {

        mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);

        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " " + getString(R.string.of) + " " + mFList.size());

        showImageInfo(mFList.get(selectedFile));

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

                txtImageNumber.setText(position + 1 + " " + getString(R.string.of) + " " + mFList.size());

                showImageInfo(mFList.get(position));

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

    /**
     * show image info, time , name , description
     */
    private void showImageInfo(RealmRoomMessage realmRoomMessage) {
        RealmRoomMessage realmRoomMessageFinal;
        if (realmRoomMessage.getForwardMessage() != null) {
            realmRoomMessageFinal = realmRoomMessage.getForwardMessage();
        } else {
            realmRoomMessageFinal = realmRoomMessage;
        }

        if (realmRoomMessageFinal.getMessage() != null && !realmRoomMessageFinal.getMessage().isEmpty()) {
            txtImageDesc.setText(realmRoomMessageFinal.getMessage());
            txtImageDesc.setVisibility(View.VISIBLE);
        } else {
            txtImageDesc.setVisibility(View.GONE);
        }
        if (realmRoomMessageFinal.getAttachment() != null) {
            txtImageName.setText(realmRoomMessageFinal.getAttachment().getName());
        }
        if (realmRoomMessageFinal.getUpdateTime() != 0) {
            txtImageDate.setText(TimeUtils.toLocal(realmRoomMessageFinal.getUpdateTime(), G.CHAT_MESSAGE_TIME));
        }
    }

    public void popUpMenuShowImage() {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).items(R.array.pop_up_menu_show_image).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                if (which == 0) {
                    shareImage();
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

    private void shareImage() {

        RealmRoomMessage rm = mFList.get(viewPager.getCurrentItem());

        if (rm != null) {

            if (rm.getForwardMessage() != null) rm = rm.getForwardMessage();


            String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
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

        RealmRoomMessage rm = mFList.get(viewPager.getCurrentItem());
        if (rm != null) {

            if (rm.getForwardMessage() != null) rm = rm.getForwardMessage();

            String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
            File file = new File(path);
            if (file.exists()) {
                HelperSaveFile.savePicToGallary(path);
            }
        }
    }

    private class AdapterViewPager extends PagerAdapter {

        @Override
        public int getCount() {
            return mFList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(View container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);

            final TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);
            final MessageProgress progress = (MessageProgress) layout.findViewById(R.id.progress);

            final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) layout.findViewById(R.id.ch_progress_loadingContent);
            contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

            final RealmRoomMessage rm = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage() : mFList.get(position);

            if (HelperDownloadFile.isDownLoading(rm.getAttachment().getToken())) {
                progress.withDrawable(R.drawable.ic_cancel, true);
                startDownload(position, progress, touchImageView, contentLoading);
            } else {
                progress.withDrawable(R.drawable.ic_download, true);
                contentLoading.setVisibility(View.GONE);
            }


            if (rm != null) {
                String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
                File file = new File(path);
                if (file.exists()) {
                    ImageLoader.getInstance().displayImage(suitablePath(path), touchImageView);
                    progress.setVisibility(View.GONE);
                } else {
                    path = getThumpnailPath(rm.getAttachment().getToken(), rm.getAttachment().getName());
                    file = new File(path);
                    if (file.exists()) {
                        ImageLoader.getInstance().displayImage(suitablePath(path), touchImageView);
                    } else if (rm.getAttachment() != null) {
                        // if thumpnail not exist download it
                        ProtoFileDownload.FileDownload.Selector selector = null;
                        long fileSize = 0;

                        if (rm.getAttachment().getSmallThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                            fileSize = rm.getAttachment().getSmallThumbnail().getSize();
                        } else if (rm.getAttachment().getLargeThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                            fileSize = rm.getAttachment().getLargeThumbnail().getSize();
                        }

                        final String filePathTumpnail = G.DIR_TEMP + "/" + "thumb_" + rm.getAttachment().getToken() + "_" + rm.getAttachment().getName();

                        if (selector != null && fileSize > 0) {
                            HelperDownloadFile.startDownload(rm.getAttachment().getToken(), rm.getAttachment().getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(String token, int progress) {

                                    if (progress == 100) {

                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ImageLoader.getInstance().displayImage(suitablePath(filePathTumpnail), touchImageView);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void OnError(String token) {

                                }
                            });
                        }

                    }
                }
            }

            progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String _tpken = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getAttachment().getToken() : mFList.get(position).getAttachment().getToken();

                    if (HelperDownloadFile.isDownLoading(_tpken)) {
                        HelperDownloadFile.stopDownLoad(_tpken);
                    } else {
                        progress.withDrawable(R.drawable.ic_cancel, true);
                        startDownload(position, progress, touchImageView, contentLoading);
                    }
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

        private void startDownload(int position, final MessageProgress progress, final TouchImageView touchImageView, final ContentLoadingProgressBar contentLoading) {

            contentLoading.setVisibility(View.VISIBLE);

            final RealmRoomMessage rm = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage() : mFList.get(position);

            String dirPath = AndroidUtils.suitableAppFilePath(rm.getMessageType()) + "/" +
                    rm.getAttachment().getToken() + "_" + rm.getAttachment().getName();

            if (downloadedList.indexOf(rm.getAttachment().getToken()) == -1) downloadedList.add(rm.getAttachment().getToken());

            HelperDownloadFile.startDownload(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getAttachment().getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(String token, final int progres) {

                    if (progress != null) {

                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progres < 100) {
                                    progress.withProgress(progres);
                                } else {
                                    progress.withProgress(0);
                                    progress.setVisibility(View.GONE);
                                    contentLoading.setVisibility(View.GONE);

                                    String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
                                    ImageLoader.getInstance().displayImage(suitablePath(path), touchImageView);
                                }
                            }
                        });
                    }
                }

                @Override
                public void OnError(String token) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.withProgress(0);
                            progress.withDrawable(R.drawable.ic_download, true);
                            contentLoading.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public String getFilePath(String token, String fileName, ProtoGlobal.RoomMessageType messageType) {

        String result = "";

        Realm realm = Realm.getDefaultInstance();
        RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

        if (attachment != null) {
            if (attachment.getLocalFilePath() != null) result = attachment.getLocalFilePath();
        }

        if (result.length() < 1) result = AndroidUtils.suitableAppFilePath(messageType) + "/" + token + "_" + fileName;

        realm.close();

        return result;
    }

    public String getThumpnailPath(String token, String fileName) {

        String result = "";

        Realm realm = Realm.getDefaultInstance();
        RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

        if (attachment != null) {
            if (attachment.getLocalThumbnailPath() != null) result = attachment.getLocalThumbnailPath();
        }

        if (result.length() < 1) result = G.DIR_TEMP + "/" + "thumb_" + token + "_" + AppUtils.suitableThumbFileName(fileName);

        realm.close();

        return result;
    }
}
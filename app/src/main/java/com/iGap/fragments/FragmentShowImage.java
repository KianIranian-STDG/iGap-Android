package com.iGap.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperSaveFile;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SUID;
import com.iGap.module.StructDownloadAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.TouchImageView;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

import io.meness.github.messageprogress.MessageProgress;
import io.meness.github.messageprogress.OnMessageProgressClick;
import io.meness.github.messageprogress.OnProgress;
import io.realm.Realm;

public class FragmentShowImage extends Fragment implements OnFileDownloadResponse {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;
    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;

    private ArrayList<StructMessageInfo> list;
    private int selectedFile = 0;
    private int listSize = 0;
    private AdapterViewPager mAdapter;

    private long peerId = 0;
    private String pathImage;

    public static View appBarLayout;

    public static FragmentShowImage newInstance() {
        return new FragmentShowImage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

            list = (ArrayList<StructMessageInfo>) bundle.getSerializable("listPic");
            if (list == null) {
                getActivity().getFragmentManager()
                        .beginTransaction()
                        .remove(FragmentShowImage.this)
                        .commit();
                return false;
            }
            if (list.size() < 1) {
                getActivity().getFragmentManager()
                        .beginTransaction()
                        .remove(FragmentShowImage.this)
                        .commit();
                return false;
            }

            int si = bundle.getInt("SelectedImage");
            if (si >= 0) selectedFile = si;

            return true;
        } else {
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .remove(FragmentShowImage.this)
                    .commit();
            return false;
        }
    }

    private void initComponent(View view) {

        MaterialDesignTextView btnBack =
                (MaterialDesignTextView) view.findViewById(R.id.asi_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getFragmentManager()
                        .beginTransaction()
                        .remove(FragmentShowImage.this)
                        .commit();
            }
        });

        MaterialDesignTextView btnMenu =
                (MaterialDesignTextView) view.findViewById(R.id.asi_btn_menu);
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

        G.onFileDownloadResponse = this;

        initViewPager();
    }

    //***************************************************************************************

    private void initViewPager() {

        mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);
        listSize = list.size();
        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " " + getString(R.string.of) + " " + listSize);
        if (list.get(selectedFile).attachment != null) {
            txtImageName.setText(list.get(selectedFile).attachment.name);
        }
        if (list.get(selectedFile).time != 0) {
            txtImageDate.setText(
                    TimeUtils.toLocal(list.get(selectedFile).time, G.CHAT_MESSAGE_TIME));
        }

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("ZZZZ", "setOnClickListener: ");
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                StructMessageInfo sharedMedia = list.get(position);

                txtImageNumber.setText(
                        position + 1 + " " + getString(R.string.of) + " " + listSize);

                if (sharedMedia.attachment != null) {
                    txtImageName.setText(sharedMedia.attachment.name);
                }

                if (sharedMedia.time != 0) {
                    txtImageDate.setText(TimeUtils.toLocal(sharedMedia.time, G.CHAT_MESSAGE_TIME));
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

    @Override
    public void onFileDownload(final String token, final long offset,
                               final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
        // if thumbnail
        if (selector != ProtoFileDownload.FileDownload.Selector.FILE) {
            mAdapter.updateThumbnail(token);
        } else {
            // else file
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateDownloadFields(token, progress, offset);
                }
            });
        }
    }

    @Override
    public void onAvatarDownload(String token, long offset,
                                 ProtoFileDownload.FileDownload.Selector selector, int progress, long userId,
                                 RoomType roomType) {
        // empty
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        if (majorCode == 713 && minorCode == 1) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 2) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 3) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 4) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 5) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 714) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 715) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }

    public void popUpMenuShowImage() {

        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.pop_up_menu_show_image)
                        .contentColor(Color.BLACK)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which,
                                                    CharSequence text) {

                                if (which == 0) {
                                    showAllMedia();
                                    // TODO: 11/15/2016 this part it dose'nt work
                                } else if (which == 1) {

                                    HelperSaveFile.savePicToGallary(pathImage);

                                } else if (which == 2) {

                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_TEXT, "iGap/download this image");
                                    Uri screenshotUri = Uri.parse(pathImage);

                                    intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                                    intent.setType("image/*");
                                    startActivity(Intent.createChooser(intent, "Share image from iGap"));

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

    private void showAllMedia() {
        Log.e("ddd", "show all media");
    }

    private void saveToGalary() {
        Log.e("ddd", "save to galarry");
    }

    public StructMessageInfo getCurrentPage() {
        return mAdapter.getView(viewPager.getCurrentItem());
    }

    private class AdapterViewPager extends PagerAdapter {

        public void updateDownloadFields(String token, int progress, long offset) {
            for (StructMessageInfo item : list) {
                if (item.downloadAttachment != null
                        && item.downloadAttachment.token.equalsIgnoreCase(token)) {
                    item.downloadAttachment.offset = offset;
                    item.downloadAttachment.progress = progress;
                    requestDownloadFile(item);
                }
            }

            notifyDataSetChanged();
        }

        public void updateThumbnail(String token) {
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        private void requestForThumbnail(StructMessageInfo media) {
            if (media.attachment.smallThumbnail == null) {
                return;
            }

            // create new download attachment once with attachment token
            if (media.downloadAttachment == null) {
                media.downloadAttachment = new StructDownloadAttachment(media.attachment.token);
            }

            // request thumbnail
            if (!media.downloadAttachment.thumbnailRequested) {

                ProtoFileDownload.FileDownload.Selector selector =
                        ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                if (media.attachment.getLocalThumbnailPath() == null
                        || media.attachment.getLocalThumbnailPath().isEmpty()) {
                    media.attachment.setLocalThumbnailPath(Long.parseLong(media.messageID),
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES)
                                    + "/"
                                    + media.downloadAttachment.token
                                    + SUID.id().get()
                                    + media.attachment.name);
                }

                // I don't use offset in getting thumbnail
                String identity = media.downloadAttachment.token
                        + '*'
                        + selector.toString()
                        + '*'
                        + media.attachment.smallThumbnail.size
                        + '*'
                        + media.attachment.getLocalThumbnailPath()
                        + '*'
                        + media.downloadAttachment.offset;

                new RequestFileDownload().download(media.downloadAttachment.token, 0,
                        (int) media.attachment.smallThumbnail.size, selector, identity);

                // prevent from multiple requesting thumbnail
                media.downloadAttachment.thumbnailRequested = true;
            }
        }

        private void onLoadFromLocal(final ImageView imageView, String localPath, LocalFileType fileType) {
            Log.i("VVV", "localPath : " + localPath);
            pathImage = localPath;
            ImageLoader.getInstance()
                    .displayImage(AndroidUtils.suitablePath(localPath), imageView,
                            new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view,
                                                            FailReason failReason) {

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                                              Bitmap loadedImage) {
                                    imageView.setImageBitmap(loadedImage);
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {

                                }
                            });
        }

        @Override
        public Object instantiateItem(View container, int position) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final ViewGroup layout =
                    (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container,
                            false);

            TouchImageView touchImageView =
                    (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);
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

            final StructMessageInfo media = list.get(position);

            // runs if message has attachment
            if (media.hasAttachment()) {
                // if file already exists, simply show the local one
                if (media.attachment.isFileExistsOnLocal()) {
                    // load file from local
                    onLoadFromLocal(touchImageView, media.attachment.getLocalFilePath(), LocalFileType.FILE);
                } else {
                    // file doesn't exist on local, I check for a thumbnail
                    // if thumbnail exists, I load it into the view
                    if (media.attachment.isThumbnailExistsOnLocal()) {
                        //                        ViewGroup view = (ViewGroup) layout.findViewById(R.id.sisl_touch_image_view).getParent();
                        //                        if (view != null) {
                        //                            int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(layout.getContext(), media.attachment.width, media.attachment.height);
                        //                            view.setLayoutParams(new LinearLayout.LayoutParams(dimens[0], dimens[1]));
                        //                            view.requestLayout();
                        //                        }

                        // load thumbnail from local
                        onLoadFromLocal(touchImageView, media.attachment.getLocalThumbnailPath(),
                                LocalFileType.THUMBNAIL);
                    } else {
                        requestForThumbnail(media);
                    }

                    // create new download attachment once with attachment token
                    if (media.downloadAttachment == null) {
                        media.downloadAttachment =
                                new StructDownloadAttachment(media.attachment.token);
                    }

                    if (layout.findViewById(R.id.progress) != null) {
                        ((MessageProgress) layout.findViewById(
                                R.id.progress)).withOnMessageProgress(new OnMessageProgressClick() {
                            @Override
                            public void onMessageProgressClick(MessageProgress progress) {
                                // make sure to not request multiple times by checking last offset with the new one
                                if (media.downloadAttachment.lastOffset
                                        < media.downloadAttachment.offset) {
                                    requestDownloadFile(media);
                                    media.downloadAttachment.lastOffset =
                                            media.downloadAttachment.offset;
                                }
                            }
                        });
                    }
                }

                if (layout.findViewById(R.id.progress) != null) {
                    ((MessageProgress) layout.findViewById(R.id.progress)).withOnProgress(
                            new OnProgress() {
                                @Override
                                public void onProgressFinished() {
                                    layout.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                                }
                            });
                }

                updateProgressIfNeeded(layout, media);
            } else if (media.filePath != null) {
                if (new File(media.filePath).exists()) {
                    if (layout.findViewById(R.id.progress) != null) {
                        layout.findViewById(R.id.progress).setVisibility(View.GONE);
                    }
                    Log.i("VVV", "media.filePath : " + media.filePath);
                    onLoadFromLocal(touchImageView, media.filePath, LocalFileType.FILE);
                }
            }

            ((ViewGroup) container).addView(layout);

            return layout;
        }

        /**
         * automatically update progress if layout has one
         */
        private void updateProgressIfNeeded(ViewGroup itemView, StructMessageInfo media) {
            if (itemView.findViewById(R.id.progress) == null) {
                return;
            }

            ((MessageProgress) itemView.findViewById(R.id.progress)).withDrawable(
                    R.drawable.ic_download);
            // update progress when user trying to download
            if (!media.attachment.isFileExistsOnLocal() && media.downloadAttachment != null) {
                ((MessageProgress) itemView.findViewById(R.id.progress)).withProgress(
                        media.downloadAttachment.progress);
            } else {
                if (media.attachment.isFileExistsOnLocal()) {
                    ((MessageProgress) itemView.findViewById(R.id.progress)).performProgress();
                } else {
                    itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                }
            }
        }

        public void requestDownloadFile(final StructMessageInfo media) {
            if (media.downloadAttachment.progress == 100) {
                return; // necessary
            }
            ProtoFileDownload.FileDownload.Selector selector =
                    ProtoFileDownload.FileDownload.Selector.FILE;
            //            final String localFilePath = G.DIR_IMAGES + "/" + media.downloadAttachment.token + SUID.id().get() + media.attachment.name;
            final String localFilePath =
                    media.downloadAttachment.token + SUID.id().get() + media.attachment.name;
            Log.i("GGG", "localFilePath : " + localFilePath);
            if (media.attachment.getLocalFilePath() == null || media.attachment.getLocalFilePath()
                    .isEmpty()) {
                media.attachment.setLocalFilePath(Long.parseLong(media.messageID), localFilePath);
            }

             /*
              * use this for when show contact image, i want
              * store file path to realm that when coming to
              * FragmentShowImage use filepath , because i fill
              * structMessageInfo with RealmAvatar so should exist that info
              * */

            if (peerId != 0) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRegisteredInfo realmRegisteredInfo =
                                realm.where(RealmRegisteredInfo.class)
                                        .equalTo(RealmRegisteredInfoFields.ID, peerId)
                                        .findFirst();
                        for (RealmAvatar avatar : realmRegisteredInfo.getAvatars()) {
                            if (avatar.getFile().getToken().equals(media.attachment.token)) {
                                avatar.getFile().setLocalFilePath(localFilePath);
                            }
                        }
                    }
                });
                realm.close();
            }

            Log.i("GGG",
                    "media.attachment.getLocalFilePath() : " + media.attachment.getLocalFilePath());
            String identity = media.downloadAttachment.token
                    + '*'
                    + selector.toString()
                    + '*'
                    + media.attachment.size
                    + '*'
                    + media.attachment.getLocalFilePath()
                    + '*'
                    + media.downloadAttachment.offset;

            new RequestFileDownload().download(media.downloadAttachment.token,
                    media.downloadAttachment.offset, (int) media.attachment.size, selector, identity);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            int index = list.indexOf(object);
            if (index == -1) {
                return POSITION_NONE;
            } else {
                return index;
            }
        }

        //-----------------------------------------------------------------------------

        public StructMessageInfo getView(int position) {
            return list.get(position);
        }
    }

    //-----------------------------------------------------------------------------
    //    public void setCurrentPage(View pageToShow) {
    //        viewPager.setCurrentItem(mAdapter.getItemPosition(pageToShow), true);
    //    }
}
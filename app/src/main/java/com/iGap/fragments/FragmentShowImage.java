package com.iGap.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivitySetting;
import com.iGap.interface_package.OnFileDownloadResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.StructDownloadAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.TouchImageView;
import com.iGap.module.Utils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarPath;
import com.iGap.realm.RealmRegisteredInfo;
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

    private ViewPager viewPager;

    private ArrayList<StructMessageInfo> list;
    private int selectedFile = 0;
    private int listSize = 0;
    private AdapterViewPager mAdapter;

    private long peerId = 0;


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

            peerId = bundle.getLong("PeedId");

            list = (ArrayList<StructMessageInfo>) bundle.getSerializable("listPic");
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
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
            }
        });

        Button btnMenu = (Button) view.findViewById(R.id.asi_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
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
        txtImageDate = (TextView) view.findViewById(R.id.asi_txt_image_date);

        G.onFileDownloadResponse = this;

        initViewPager();
    }

    //***************************************************************************************

    private void initViewPager() {

        mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);
        listSize = list.size();
        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " of " + listSize);
        if (list.get(selectedFile).attachment != null) {
            txtImageName.setText(list.get(selectedFile).attachment.name);
        }
        if (list.get(selectedFile).time != 0) {
            txtImageDate.setText(TimeUtils.toLocal(list.get(selectedFile).time, G.CHAT_MESSAGE_TIME));
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                StructMessageInfo sharedMedia = list.get(position);

                txtImageNumber.setText(position + 1 + " of " + listSize);

                if (sharedMedia.attachment != null) {
                    txtImageName.setText(sharedMedia.attachment.name);
                }

                if (list.get(selectedFile).time != 0) {
                    txtImageDate.setText(TimeUtils.toLocal(list.get(selectedFile).time, G.CHAT_MESSAGE_TIME));
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
    public void onFileDownload(final String token, final int offset, final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
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
    public void onAvatarDownload(String token, int offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId) {
        // empty
    }

    private class AdapterViewPager extends PagerAdapter {

        public void updateDownloadFields(String token, int progress, int offset) {
            for (StructMessageInfo item : list) {
                if (item.downloadAttachment != null && item.downloadAttachment.token.equalsIgnoreCase(token)) {
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
            // create new download attachment once with attachment token
            if (media.downloadAttachment == null) {
                media.downloadAttachment = new StructDownloadAttachment(media.attachment.token);
            }

            // request thumbnail
            if (!media.downloadAttachment.thumbnailRequested) {

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                if (media.attachment.getLocalThumbnailPath() == null || media.attachment.getLocalThumbnailPath().isEmpty()) {
                    media.attachment.setLocalThumbnailPath(Long.parseLong(media.messageID), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + media.downloadAttachment.token + System.nanoTime() + media.attachment.name);
                }

                // I don't use offset in getting thumbnail
                String identity = media.downloadAttachment.token + '*' + selector.toString() + '*' + media.attachment.smallThumbnail.size + '*' + media.attachment.getLocalThumbnailPath() + '*' + media.downloadAttachment.offset;

                new RequestFileDownload().download(media.downloadAttachment.token, 0, (int) media.attachment.smallThumbnail.size, selector, identity);


                // prevent from multiple requesting thumbnail
                media.downloadAttachment.thumbnailRequested = true;
            }
        }

        private void onLoadFromLocal(final ImageView imageView, String localPath, LocalFileType fileType) {
            ImageLoader.getInstance().displayImage(Utils.suitablePath(localPath), imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
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
            final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);

            TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);

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
                        ViewGroup view = (ViewGroup) layout.findViewById(R.id.sisl_touch_image_view).getParent();
                        if (view != null) {
                            int[] dimens = Utils.scaleDimenWithSavedRatio(layout.getContext(), media.attachment.width, media.attachment.height);
                            view.setLayoutParams(new LinearLayout.LayoutParams(dimens[0], dimens[1]));
                            view.requestLayout();
                        }

                        // load thumbnail from local
                        onLoadFromLocal(touchImageView, media.attachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                    } else {
                        requestForThumbnail(media);
                    }

                    // create new download attachment once with attachment token
                    if (media.downloadAttachment == null) {
                        media.downloadAttachment = new StructDownloadAttachment(media.attachment.token);
                    }

                    if (layout.findViewById(R.id.progress) != null) {
                        ((MessageProgress) layout.findViewById(R.id.progress)).withOnMessageProgress(new OnMessageProgressClick() {
                            @Override
                            public void onMessageProgressClick(MessageProgress progress) {
                                // make sure to not request multiple times by checking last offset with the new one
                                if (media.downloadAttachment.lastOffset < media.downloadAttachment.offset) {
                                    requestDownloadFile(media);
                                    media.downloadAttachment.lastOffset = media.downloadAttachment.offset;
                                }
                            }
                        });
                    }
                }

                if (layout.findViewById(R.id.progress) != null) {
                    ((MessageProgress) layout.findViewById(R.id.progress)).withOnProgress(new OnProgress() {
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

            ((MessageProgress) itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_download);
            // update progress when user trying to download
            if (!media.attachment.isFileExistsOnLocal() && media.downloadAttachment != null) {
                ((MessageProgress) itemView.findViewById(R.id.progress)).withProgress(media.downloadAttachment.progress);
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
            ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;
            final String localFilePath = G.DIR_IMAGES + "/" + media.downloadAttachment.token + System.nanoTime() + media.attachment.name;
            if (media.attachment.getLocalFilePath() == null || media.attachment.getLocalFilePath().isEmpty()) {
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
                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", peerId).findFirst();
                        for (RealmAvatar avatar : realmRegisteredInfo.getAvatar()) {
                            if (avatar.getFile().getToken().equals(media.attachment.token)) {
                                avatar.getFile().setLocalFilePath(localFilePath);
                            }
                        }
                    }
                });
                realm.close();
            }

            String identity = media.downloadAttachment.token + '*' + selector.toString() + '*' + media.attachment.size + '*' + media.attachment.getLocalFilePath() + '*' + media.downloadAttachment.offset;

            new RequestFileDownload().download(media.downloadAttachment.token, media.downloadAttachment.offset, (int) media.attachment.size, selector, identity);
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

        public int removeView(ViewPager pager, StructMessageInfo v) {
            return removeView(pager, list.indexOf(v));
        }

        //-----------------------------------------------------------------------------

        public int removeView(ViewPager pager, int position) {

            Realm realm = Realm.getDefaultInstance();
            final RealmAvatarPath realmAvatarPath = realm.where(RealmAvatarPath.class).equalTo("id", list.get(position).messageID).findFirst();
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

        public StructMessageInfo getView(int position) {
            return list.get(position);
        }

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
                                ((ActivitySetting) getActivity()).setAvatar();
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


    public StructMessageInfo getCurrentPage() {
        return mAdapter.getView(viewPager.getCurrentItem());
    }

    //-----------------------------------------------------------------------------
//    public void setCurrentPage(View pageToShow) {
//        viewPager.setCurrentItem(mAdapter.getItemPosition(pageToShow), true);
//    }


}

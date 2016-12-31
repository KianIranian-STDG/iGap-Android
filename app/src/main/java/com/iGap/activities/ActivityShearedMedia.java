package com.iGap.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.FragmentShowImage;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperDownloadFile;
import com.iGap.helper.HelperMimeType;
import com.iGap.helper.HelperUrl;
import com.iGap.interfaces.OnClientSearchRoomHistory;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.proto.ProtoClientSearchRoomHistory;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmShearedMedia;
import com.iGap.realm.RealmShearedMediaFields;
import com.iGap.request.RequestClientSearchRoomHistory;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.meness.github.messageprogress.MessageProgress;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.context;

/**
 * Created by android3 on 9/4/2016.
 */
public class ActivityShearedMedia extends ActivityEnhanced {

    private Realm mRealm;
    private RealmResults<RealmShearedMedia> mList;
    private RecyclerView recyclerView;
    private mAdapter adapter;
    int mListcount = 0;
    private int spanItemCount = 3;
    private TextView txtSharedMedia;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;
    private OnComplete complete;
    private long roomId = 0;

    public static ArrayList<Long> downloadedList = new ArrayList<>();

    public static String totalCountItem = "";

    private LinearLayout mediaLayout;
    private MusicPlayer musicPlayer;

    private AppBarLayout appBarLayout;

    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = false;


    ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter mFilter;

    private int offset;

    public enum SharedMediaType {

        image(1),
        video(2),
        audio(3),
        voice(4),
        gif(5),
        file(6),
        link(7);

        private int value;

        SharedMediaType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (MusicPlayer.mp != null) {
            MusicPlayer.initLayoutTripMusic(mediaLayout);
        }
    }

    @Override protected void onStop() {
        super.onStop();

        if (mList != null) mList.removeChangeListeners();

        if (mRealm != null) mRealm.close();
    }

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheared_media);

        mRealm = Realm.getDefaultInstance();


        HelperDownloadFile helperDownloadFile = new HelperDownloadFile();

        mediaLayout = (LinearLayout) findViewById(R.id.asm_ll_music_layout);
        musicPlayer = new MusicPlayer(mediaLayout);

        roomId = getIntent().getExtras().getLong("RoomID");

        initComponent();
    }

    @Override public void onBackPressed() {
        FragmentShowImage myFragment = (FragmentShowImage) getFragmentManager().findFragmentByTag("Show_Image_fragment_shared_media");

        if (myFragment != null && myFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(myFragment).commit();

            int count = downloadedList.size();

            for (int i = 0; i < count; i++) {
                Long id = downloadedList.get(i);
                for (int j = mList.size(); j > 0; j--) {
                    if (mList.get(j - 1).getMessageId() == id) {
                        recyclerView.getAdapter().notifyItemChanged(j - 1);
                        break;
                    }
                }

                adapter.needDownloadList.remove(id);
            }
            downloadedList.clear();
        } else if (!adapter.resetSelected()) {
            super.onBackPressed();
        }
    }

    private void initComponent() {

        appBarLayout = (AppBarLayout) findViewById(R.id.asm_appbar_shared_media);

        FragmentShowImage.appBarLayout = appBarLayout;

        MaterialDesignTextView btnBack = (MaterialDesignTextView) findViewById(R.id.asm_btn_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.asm_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.asm_btn_menu);
        RippleView rippleMenu = (RippleView) findViewById(R.id.asm_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                popUpMenuSharedMedai();
            }
        });

        txtSharedMedia = (TextView) findViewById(R.id.asm_txt_sheared_media);

        complete = new OnComplete() {
            @Override public void complete(boolean result, String messageOne, String MessageTow) {

                int whatAction = 0;
                String number = "0";

                if (messageOne != null) {
                    if (messageOne.length() > 0) whatAction = Integer.parseInt(messageOne);
                }

                if (MessageTow != null) if (MessageTow.length() > 0) number = MessageTow;

                callBack(result, whatAction, number);
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.asm_recycler_view_sheared_media);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (adapter.getItemCount() <= lastVisiblePosition + 15) {

                            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offset, mFilter);
                        }
                    }
                }
            }
        });

        fillListImage();
        initAppbarSelected();
    }

    private void initAppbarSelected() {

        RippleView rippleCloseAppBarSelected = (RippleView) findViewById(R.id.asm_ripple_close_layout);
        rippleCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                adapter.resetSelected();
            }
        });

        MaterialDesignTextView btnForwardSelected = (MaterialDesignTextView) findViewById(R.id.asm_btn_forward_selected);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                Log.e("ddd", "forward");
            }
        });

        RippleView rippleDeleteSelected = (RippleView) findViewById(R.id.asm_ripple_close_layout);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                Log.e("ddd", "delete");
            }
        });

        txtNumberOfSelected = (TextView) findViewById(R.id.asm_txt_number_of_selected);


        ll_AppBarSelected = (LinearLayout) findViewById(R.id.asm_ll_appbar_selelected);
    }

    private void callBack(boolean result, int whatAction, String number) {

        switch (whatAction) {

            case 1://for show or gone layout appBar selected
                if (result) {
                    ll_AppBarSelected.setVisibility(View.VISIBLE);
                    txtNumberOfSelected.setText(number);
                } else {
                    ll_AppBarSelected.setVisibility(View.GONE);
                }
                break;
        }
    }

    //********************************************************************************************

    public void popUpMenuSharedMedai() {

        MaterialDialog dialog = new MaterialDialog.Builder(this).items(R.array.pop_up_shared_media).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0:
                        fillListImage();
                        break;
                    case 1:
                        fillListVideo();
                        break;
                    case 2:
                        fillListAudio();
                        break;
                    case 3:
                        fillListVoice();
                        break;
                    case 4:
                        fillListGif();
                        break;
                    case 5:
                        fillListFile();
                        break;
                    case 6:
                        fillListLink();
                        break;
                }
            }
        }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp220);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.getWindow().setAttributes(layoutParams);
    }

    //********************************************************************************************

    private void initLayoutRecycleviewForImage() {

        final GridLayoutManager gLayoutManager = new GridLayoutManager(ActivityShearedMedia.this, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                if (mList.get(position).isItemTime()) {
                    return spanItemCount;
                } else {
                    return 1;
                }
            }
        });

        recyclerView.setLayoutManager(gLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewWidth = recyclerView.getMeasuredWidth();
                float cardViewWidth = getResources().getDimension(R.dimen.dp120);
                int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);

                if (newSpanCount < 3) newSpanCount = 3;

                spanItemCount = newSpanCount;
                gLayoutManager.setSpanCount(newSpanCount);
                gLayoutManager.requestLayout();
            }
        });
    }

    private void fillListImage() {

        txtSharedMedia.setText(R.string.shared_image);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.IMAGE;
        mList = loadLoackData(mFilter, ProtoGlobal.RoomMessageType.IMAGE.getNumber());
        adapter = new ImageAdapter(ActivityShearedMedia.this, mList);
        initLayoutRecycleviewForImage();

    }

    private void fillListVideo() {

        txtSharedMedia.setText(R.string.shared_video);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VIDEO;

        mList = loadLoackData(mFilter, ProtoGlobal.RoomMessageType.VIDEO.getNumber());
        adapter = new VideoAdapter(ActivityShearedMedia.this, mList);
        initLayoutRecycleviewForImage();
    }

    private void fillListAudio() {

        txtSharedMedia.setText(R.string.shared_audio);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO;

        mList = loadLoackData(mFilter, ProtoGlobal.RoomMessageType.AUDIO.getNumber());
        adapter = new VoiceAdapter(ActivityShearedMedia.this, mList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

    }

    private void fillListVoice() {
        txtSharedMedia.setText(R.string.shared_voice);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VOICE;

        mList = loadLoackData(mFilter, ProtoGlobal.RoomMessageType.VOICE.getNumber());
        adapter = new VoiceAdapter(ActivityShearedMedia.this, mList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

    }

    private void fillListGif() {
        txtSharedMedia.setText(R.string.shared_gif);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.GIF;

        mList = loadLoackData(mFilter, ProtoGlobal.RoomMessageType.GIF.getNumber());
        adapter = new GifAdapter(ActivityShearedMedia.this, mList);

        initLayoutRecycleviewForImage();


    }

    private void fillListFile() {
        txtSharedMedia.setText(R.string.shared_file);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.FILE;

        mList = loadLoackData(mFilter, ProtoGlobal.RoomMessageType.FILE.getNumber());
        adapter = new FileAdapter(ActivityShearedMedia.this, mList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

    }

    private void fillListLink() {

        txtSharedMedia.setText(R.string.shared_links);
        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.URL;

        mList = loadLoackData(mFilter, ProtoGlobal.RoomMessageType.TEXT.getNumber());
        adapter = new LinkAdapter(ActivityShearedMedia.this, mList);

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityShearedMedia.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);


    }

    //********************************************************************************************

    private RealmResults<RealmShearedMedia> loadLoackData(ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter, int type) {

        RealmResults<RealmShearedMedia> results = mRealm.where(RealmShearedMedia.class).equalTo(RealmShearedMediaFields.ROOM_ID, roomId).
            equalTo(RealmShearedMediaFields.MESSAGE_TYPE, type).findAllSorted(RealmShearedMediaFields.MESSAGE_TIME, Sort.DESCENDING);

        results.addChangeListener(new RealmChangeListener<RealmResults<RealmShearedMedia>>() {
            @Override public void onChange(RealmResults<RealmShearedMedia> element) {

                adapter.notifyDataSetChanged();
                mListcount = mList.size();
            }
        });

        getDataFromServer(filter);
        mListcount = results.size();

        return results;
    }

    private void getDataFromServer(final ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        offset = 0;

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override public void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {
                isSendRequestForLoading = false;

                Log.e("ddd", resultList.size() + "    resultList.size");
                //                for (ProtoGlobal.RoomMessage message : resultList) {
                //
                //                }

                offset += resultList.size();

                if (notDeletedCount > 0) {
                    saveDataToLocal(resultList, roomId);

                } else {
                    Toast.makeText(ActivityShearedMedia.this, R.string.there_is_no_sheared_media, Toast.LENGTH_LONG).show();
                }

                if (notDeletedCount > offset && notDeletedCount > mListcount) {
                    isThereAnyMoreItemToLoad = true;
                } else {
                    isThereAnyMoreItemToLoad = false;
                }

                Log.e("dddd", "isThereAnyMoreItemToLoad   " + isThereAnyMoreItemToLoad + "    " + offset);
            }

            @Override public void onError(int majorCode, int minorCode) {
                isSendRequestForLoading = false;

            }

            @Override public void onTimeOut() {
                isSendRequestForLoading = false;
                Log.e("ddd", "timeOut  onClientSearchRoomHistory");
            }
        };


        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, offset, filter);
        isSendRequestForLoading = true;
    }

    public static void saveDataToLocal(final List<ProtoGlobal.RoomMessage> RoomMessages, final long roomId) {

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                for (ProtoGlobal.RoomMessage roomMessage : RoomMessages) {

                    RealmShearedMedia mediaList =
                        realm.where(RealmShearedMedia.class).equalTo(RealmShearedMediaFields.MESSAGE_ID, roomMessage.getMessageId()).equalTo(RealmShearedMediaFields.ROOM_ID, roomId).findFirst();

                    if (mediaList == null) {
                        mediaList = realm.createObject(RealmShearedMedia.class);

                        mediaList.setRoomId(roomId);
                        mediaList.setMessageId(roomMessage.getMessageId());
                        mediaList.setMessageType(roomMessage.getMessageType().getNumber());
                        mediaList.setMessageTime(roomMessage.getUpdateTime());

                        ProtoGlobal.File attachment = roomMessage.getAttachment();
                        // attachment= roomMessage.getForwardFrom()!=null ? roomMessage.getForwardFrom().getAttachment():roomMessage.getAttachment();

                        mediaList.setAttachment_token(attachment.getToken());
                        mediaList.setAttachment_name(attachment.getName());
                        mediaList.setAttachment_size(attachment.getSize());
                        mediaList.setAttachment_smallThumbnail_size(attachment.getSmallThumbnail().getSize());
                        mediaList.setAttachment_duration(attachment.getDuration());

                        mediaList.setRoomMessage(SerializationUtils.serialize(roomMessage));
                    }
                }
            }
        });


        realm.close();

    }

    //********************************************************************************************


    /**
     * Simple Class to serialize object to byte arrays
     *
     * @author Nick Russler
     *         http://www.whitebyte.info
     */
    public static class SerializationUtils {

        /**
         * @param obj - object to serialize to a byte array
         * @return byte array containing the serialized obj
         */
        public static byte[] serialize(Object obj) {
            byte[] result = null;
            ByteArrayOutputStream fos = null;

            try {
                fos = new ByteArrayOutputStream();
                ObjectOutputStream o = new ObjectOutputStream(fos);
                o.writeObject(obj);
                result = fos.toByteArray();
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        /**
         * @param arr - the byte array that holds the serialized object
         * @return the deserialized object
         */
        public static Object deserialize(byte[] arr) {
            InputStream fis = null;

            try {
                fis = new ByteArrayInputStream(arr);
                ObjectInputStream o = new ObjectInputStream(fis);
                return o.readObject();
            } catch (IOException e) {
                System.err.println(e);
            } catch (ClassNotFoundException e) {
                System.err.println(e);
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }

            return null;
        }

        /**
         * @param obj - object to be cloned
         * @return a clone of obj
         */
        @SuppressWarnings("unchecked") public static <T> T cloneObject(T obj) {
            return (T) deserialize(serialize(obj));
        }
    }

    static int counts = 0;

    public static void updateCountOfSharedMedia(OnComplete complete, long roomid) {

        if (counts >= 7) {

            totalCountItem = totalCountItem.trim();

            if (totalCountItem.length() < 1) totalCountItem = context.getString(R.string.there_is_no_sheared_media);

            if (complete != null) {
                complete.complete(true, totalCountItem, "");
            }

            Realm realm = Realm.getDefaultInstance();

            final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomid).findFirst();
            if (room != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        room.setSharedMediaCount(totalCountItem);
                    }
                });
            }

            //final RealmResults<RealmShearedMedia> realmShearedMedia = realm.where(RealmShearedMedia.class).equalTo(RealmShearedMediaFields.ROOM_ID, roomid).findAll();
            //if (realmShearedMedia != null) {
            //    realm.executeTransaction(new Realm.Transaction() {
            //        @Override public void execute(Realm realm) {
            //            realmShearedMedia.deleteAllFromRealm();
            //        }
            //    });
            //}

            realm.close();
        }
    }

    public static void getCountOfSharedMedia(final long roomid, String text, final OnComplete complete) {

        counts = 0;

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override public void onClientSearchRoomHistory(final int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList) {

                counts++;
                if (notDeletedCount > 0) {
                    ProtoGlobal.RoomMessageType type = resultList.get(0).getMessageType();

                    switch (type) {
                        case IMAGE:
                        case IMAGE_TEXT:
                            totalCountItem += "\n" + notDeletedCount + " " + context.getString(R.string.shared_image);
                            break;
                        case VIDEO:
                        case VIDEO_TEXT:
                            totalCountItem += "\n" + notDeletedCount + " " + context.getString(R.string.shared_video);
                            break;
                        case AUDIO:
                        case AUDIO_TEXT:
                            totalCountItem += "\n" + notDeletedCount + " " + context.getString(R.string.shared_audio);
                            break;
                        case VOICE:
                            totalCountItem += "\n" + notDeletedCount + " " + context.getString(R.string.shared_voice);
                            break;
                        case GIF:
                        case GIF_TEXT:
                            totalCountItem += "\n" + notDeletedCount + " " + context.getString(R.string.shared_gif);
                            break;
                        case FILE:
                        case FILE_TEXT:
                            totalCountItem += "\n" + notDeletedCount + " " + context.getString(R.string.shared_file);
                            break;
                        default:
                            totalCountItem += "\n" + notDeletedCount + " " + context.getString(R.string.shared_links);
                            break;
                    }
                }

                updateCountOfSharedMedia(complete, roomid);

            }

            @Override public void onError(int majorCode, int minorCode) {
                counts++;
                updateCountOfSharedMedia(complete, roomid);
            }

            @Override public void onTimeOut() {
                counts++;
                updateCountOfSharedMedia(complete, roomid);
            }
        };

        Realm realm = Realm.getDefaultInstance();
        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomid).findFirst();
        if (room != null) {
            if (complete != null) {
                complete.complete(true, room.getSharedMediaCount(), "");
            }
        }
        realm.close();

        if (text.length() == 0) {
            totalCountItem = "";
            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomid, 0, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.IMAGE);
            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomid, 0, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VIDEO);
            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomid, 0, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO);
            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomid, 0, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VOICE);
            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomid, 0, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.GIF);
            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomid, 0, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.FILE);
            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomid, 0, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.URL);
        } else {
            if (complete != null) {
                complete.complete(true, text, "");
            }
        }
    }

    //****************************************************    Adapter    ****************************************

    public abstract class mAdapter extends RecyclerView.Adapter {

        private boolean isSelectedMode = false;    // for determine user select some file
        private int numberOfSelected = 0;
        private RealmResults<RealmShearedMedia> mList;
        protected Context context;

        protected ArrayMap<Long, Boolean> SelectedList = new ArrayMap<>();
        protected ArrayMap<Long, Boolean> DownloadingList = new ArrayMap<>();
        protected ArrayMap<Long, Boolean> needDownloadList = new ArrayMap<>();

        abstract void openSelectedItem(int position, RecyclerView.ViewHolder holder);

        public mAdapter(Context context, final RealmResults realmResults) {
            mList = realmResults;
            this.context = context;
        }

        public class mHolder extends RecyclerView.ViewHolder {

            public MessageProgress messageProgress;

            public mHolder(View view, int position) {
                super(view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        if (isSelectedMode) {
                            setSelectedItem(getPosition());
                        } else {

                            openSelected(getPosition(), mHolder.this);
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override public boolean onLongClick(View view) {
                        isSelectedMode = true;
                        setSelectedItem(getPosition());
                        return true;
                    }
                });

                messageProgress = (MessageProgress) itemView.findViewById(R.id.progress);
                messageProgress.withDrawable(R.drawable.ic_download, true);

                messageProgress.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        downloadFile(getPosition(), messageProgress);
                    }
                });
            }
        }

        @Override public int getItemViewType(int position) {
            return position;
        }

        @Override public int getItemCount() {
            return mList.size();
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (!mList.get(position).isItemTime()) {
                setBackgroundColor(holder, position);
            }
        }

        public View setLayoutHeaderTime(View parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            view.setBackgroundColor(Color.parseColor("#cccccc"));
            return view;
        }

        private void setBackgroundColor(RecyclerView.ViewHolder holder, int position) {

            // set blue back ground for selected file
            FrameLayout layout = (FrameLayout) holder.itemView.findViewById(R.id.smsl_fl_contain_main);

            if (SelectedList.containsKey(mList.get(position).getMessageId())) {
                layout.setForeground(new ColorDrawable(Color.parseColor("#99AADFF7")));
            } else {
                layout.setForeground(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        private void openSelected(int position, RecyclerView.ViewHolder holder) {

            if (needDownloadList.containsKey(mList.get(position).getMessageId())) {

                // first need to download file

            } else {

                openSelectedItem(position, holder);
            }
        }

        private void setSelectedItem(final int position) {

            Long id = mList.get(position).getMessageId();

            if (SelectedList.containsKey(id)) {
                SelectedList.remove(id);
                numberOfSelected--;

                if (numberOfSelected < 1) {
                    isSelectedMode = false;
                }
            } else {
                SelectedList.put(id, true);
                numberOfSelected++;
            }
            notifyItemChanged(position);

            if (complete != null) {
                complete.complete(isSelectedMode, "1", numberOfSelected + "");
            }
        }

        private void startDownload(final int position, final MessageProgress messageProgress) {

            final Long id = mList.get(position).getMessageId();

            DownloadingList.put(id, true);

            messageProgress.withDrawable(R.drawable.ic_cancel, true);

            RealmShearedMedia _re = mList.get(position);

            HelperDownloadFile.startDoanload(_re.getAttachment_token(), _re.getAttachment_name(), _re.getAttachment_size(), ProtoFileDownload.FileDownload.Selector.FILE,
                ProtoGlobal.RoomMessageType.forNumber(_re.getMessageType()), new HelperDownloadFile.UpdateListener() {
                    @Override public void OnProgress(String token, final int progress) {

                        if (messageProgress != null) {

                            messageProgress.post(new Runnable() {
                                @Override public void run() {

                                    if (progress < 100) {
                                        messageProgress.withProgress(progress);
                                    } else {
                                        messageProgress.withProgress(0);
                                        messageProgress.setVisibility(View.GONE);
                                        needDownloadList.remove(id);
                                    }
                                }
                            });
                        }
                    }

                    @Override public void OnError(String token) {

                        Log.e("dddd", "OnError  token  = " + token + "   " + messageProgress);
                        stopDownload(position, messageProgress);
                    }
                });
        }

        private void stopDownload(int position, final MessageProgress messageProgress) {

            messageProgress.post(new Runnable() {
                @Override public void run() {
                    messageProgress.withProgress(0);
                    messageProgress.withDrawable(R.drawable.ic_download, true);
                }
            });

            DownloadingList.remove(mList.get(position).getMessageId());
        }

        private void downloadFile(int position, MessageProgress messageProgress) {

            if (DownloadingList.containsKey(mList.get(position).getMessageId())) {
                HelperDownloadFile.stopDownLoad(mList.get(position).getAttachment_token());
                stopDownload(position, messageProgress);
            } else {
                startDownload(position, messageProgress);
            }
        }

        public boolean resetSelected() {

            boolean result = isSelectedMode;

            if (isSelectedMode == true) {
                isSelectedMode = false;

                SelectedList.clear();
                notifyDataSetChanged();
                numberOfSelected = 0;
            }
            complete.complete(false, "1", "0");//

            return result;
        }

        public String getThumpnailPath(String token, String fileName) {

            String result = "";

            RealmAttachment attachment = mRealm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

            if (attachment != null) {
                if (attachment.getLocalThumbnailPath() != null) result = attachment.getLocalThumbnailPath();
            }

            if (result.length() < 1) result = G.DIR_TEMP + "/" + "thumb_" + token + "_" + AppUtils.suitableThumbFileName(fileName);

            return result;
        }

        public String getFilePath(String token, String fileName, ProtoGlobal.RoomMessageType messageType) {

            String result = "";

            RealmAttachment attachment = mRealm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

            if (attachment != null) {
                if (attachment.getLocalFilePath() != null) result = attachment.getLocalFilePath();
            }

            if (result.length() < 1) result = AndroidUtils.suitableAppFilePath(messageType) + "/" + token + "_" + fileName;

            return result;
        }

        public class ViewHolderTime extends RealmViewHolder {
            public TextView txtTime;

            public ViewHolderTime(View view, int position) {
                super(view);
                txtTime = (TextView) itemView.findViewById(R.id.smslt_txt_time);
                txtTime.setText(HelperCalander.checkHijriAndReturnTime(mList.get(position).getMessageTime()));
            }
        }
    }

    //****************************************************

    public class ImageAdapter extends mAdapter {

        public ImageAdapter(Context context, RealmResults realmResults) {
            super(context, realmResults);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (mList.get(position).isItemTime()) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (!mList.get(position).isItemTime()) {

                //  final MyHoldersImage mi = (MyHoldersImage) holder;

                //  File file = new File(mi.filePath);
                //   if (file.exists()) {
                //      ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mi.filePath), mi.imvPicFile);
                //  } else {
                //File file = new File(mi.tempFilePath);
                //if (file.exists()) {
                //    ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mi.tempFilePath), mi.imvPicFile);
                //}
            }
        }

        public class ViewHolder extends mHolder {

            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                RealmShearedMedia _re = mList.get(position);

                tempFilePath = getThumpnailPath(_re.getAttachment_token(), _re.getAttachment_name());
                filePath = getFilePath(_re.getAttachment_token(), _re.getAttachment_name(), ProtoGlobal.RoomMessageType.forNumber(_re.getMessageType()));

                imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);

                File file = new File(filePath);
                if (file.exists()) {
                    messageProgress.setVisibility(View.GONE);
                } else {

                    needDownloadList.put(mList.get(position).getMessageId(), true);

                    messageProgress.setVisibility(View.VISIBLE);
                }

                File filet = new File(tempFilePath);
                if (filet.exists()) {
                    imvPicFile.setImageURI(Uri.fromFile(new File(tempFilePath)));
                } else {

                    if (_re.getAttachment_smallThumbnail_size() > 0) {
                        HelperDownloadFile.startDoanload(_re.getAttachment_token(), _re.getAttachment_name(), _re.getAttachment_smallThumbnail_size(),
                            ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, ProtoGlobal.RoomMessageType.forNumber(_re.getMessageType()), new HelperDownloadFile.UpdateListener() {
                                @Override public void OnProgress(String token, int progress) {

                                    Log.e("ddd", token + "    " + progress);

                                    imvPicFile.post(new Runnable() {
                                        @Override public void run() {
                                            imvPicFile.setImageURI(Uri.fromFile(new File(tempFilePath)));
                                        }
                                    });
                                }

                                @Override public void OnError(String token) {

                                }
                            });
                    }
                }
            }
        }

        @Override void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            showImage(position, holder);
        }

        private void showImage(int position, RecyclerView.ViewHolder holder) {

            String filePath = mList.get(position).getAttachment_token();

            ArrayList<ProtoGlobal.RoomMessage> listPic = new ArrayList<>();
            int selectedPicture = 1;

            for (RealmShearedMedia re : mList) {
                if (re.getRoomMessage() != null) listPic.add((ProtoGlobal.RoomMessage) SerializationUtils.deserialize(re.getRoomMessage()));
            }

            for (int i = 0; i < listPic.size(); i++) { // determin selected image in list image
                if (listPic.get(i).getAttachment().getToken().equals(filePath)) {
                    selectedPicture = i;
                    break;
                }
            }

            Fragment fragment = FragmentShowImage.newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable("listPic", listPic);
            bundle.putInt("SelectedImage", selectedPicture);
            fragment.setArguments(bundle);

            ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.asm_ll_parent, fragment, "Show_Image_fragment_shared_media").commit();
        }
    }

    //****************************************************

    public class VideoAdapter extends mAdapter {

        public VideoAdapter(Context context, RealmResults<RealmShearedMedia> realmResult) {
            super(context, realmResult);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (mList.get(position).isItemTime()) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (!mList.get(position).isItemTime()) {
                ViewHolder vh = (ViewHolder) holder;
                File file = new File(vh.tempFilePath);

                if (file.exists()) {
                    ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(vh.tempFilePath), vh.imvPicFile);
                } else {
                    vh.imvPicFile.setImageResource(R.mipmap.j_video);
                }
            }
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                RealmShearedMedia _re = mList.get(position);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);

                itemView.findViewById(R.id.smsl_ll_video).setVisibility(View.VISIBLE);

                TextView txtVideoIcon = (TextView) itemView.findViewById(R.id.smsl_txt_video_icon);

                TextView txtVideoTime = (TextView) itemView.findViewById(R.id.smsl_txt_video_time);
                txtVideoTime.setText(AppUtils.humanReadableDuration(_re.getAttachment_duration()));

                TextView txtVideoSize = (TextView) itemView.findViewById(R.id.smsl_txt_video_size);
                txtVideoSize.setText("(" + AndroidUtils.humanReadableByteCount(_re.getAttachment_size(), true) + ")");

                tempFilePath = getThumpnailPath(_re.getAttachment_token(), _re.getAttachment_name());
                filePath = getFilePath(_re.getAttachment_token(), _re.getAttachment_name(), ProtoGlobal.RoomMessageType.forNumber(_re.getMessageType()));

                File file = new File(filePath);
                if (file.exists()) {
                    messageProgress.setVisibility(View.GONE);
                } else {
                    needDownloadList.put(_re.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playVideo(position, holder);
        }

        private void playVideo(int position, RecyclerView.ViewHolder holder) {

            ViewHolder vh = (ViewHolder) holder;

            Intent intent = HelperMimeType.appropriateProgram(vh.filePath);
            if (intent != null) context.startActivity(intent);
        }
    }

    //****************************************************

    public class VoiceAdapter extends mAdapter {

        public VoiceAdapter(Context context, RealmResults<RealmShearedMedia> realmResult) {
            super(context, realmResult);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (mList.get(position).isItemTime()) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                RealmShearedMedia _re = mList.get(position);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
                imvPicFile.setImageResource(R.drawable.green_music_note);

                tempFilePath = getThumpnailPath(_re.getAttachment_token(), _re.getAttachment_name());
                filePath = getFilePath(_re.getAttachment_token(), _re.getAttachment_name(), ProtoGlobal.RoomMessageType.forNumber(_re.getMessageType()));

                TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
                txtFileName.setText(_re.getAttachment_name());

                TextView txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
                txtFileSize.setText("(" + AndroidUtils.humanReadableByteCount(_re.getAttachment_size(), true) + ")");

                TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
                File file = new File(filePath);

                if (file.exists()) {
                    messageProgress.setVisibility(View.GONE);
                    MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
                    Uri uri = (Uri) Uri.fromFile(file);
                    mediaMetadataRetriever.setDataSource(context, uri);
                    String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    if (artist == null) artist = context.getString(R.string.unknown_artist);

                    txtFileInfo.setText(artist);

                    try {
                        mediaMetadataRetriever.setDataSource(context, uri);
                        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                        if (data != null) {
                            Bitmap mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imvPicFile.setImageBitmap(mediaThumpnail);
                        } else {
                            file = new File(tempFilePath);
                            if (file.exists()) {
                                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(tempFilePath), imvPicFile);
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    needDownloadList.put(_re.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);
                    file = new File(tempFilePath);
                    if (file.exists()) {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(tempFilePath), imvPicFile);
                    }
                }
            }
        }

        @Override void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playAudio(position, holder);
        }

        private void playAudio(int position, RecyclerView.ViewHolder holder) {

            VideoAdapter.ViewHolder vh = (VideoAdapter.ViewHolder) holder;

            MusicPlayer.startPlayer(vh.filePath, mList.get(position).getAttachment_name(), roomId, true, mList.get(position).getMessageId() + "");
        }
    }

    //****************************************************

    public class GifAdapter extends mAdapter {

        public GifAdapter(Context context, RealmResults<RealmShearedMedia> realmResult) {
            super(context, realmResult);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (mList.get(position).isItemTime()) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_gif, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        public class ViewHolder extends mHolder {

            GifImageView gifView;
            GifDrawable gifDrawable;

            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                gifView = (GifImageView) itemView.findViewById(R.id.smslg_gif_view);
                RealmShearedMedia _re = mList.get(position);

                tempFilePath = getThumpnailPath(_re.getAttachment_token(), _re.getAttachment_name());
                filePath = getFilePath(_re.getAttachment_token(), _re.getAttachment_name(), ProtoGlobal.RoomMessageType.forNumber(_re.getMessageType()));

                File file = new File(filePath);
                if (file.exists()) {
                    gifView.setImageURI(Uri.fromFile(file));

                    messageProgress.withDrawable(R.drawable.ic_play, true);
                    messageProgress.setVisibility(View.GONE);
                    messageProgress.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            if (gifDrawable != null) {
                                gifDrawable.start();
                                messageProgress.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {

                    needDownloadList.put(_re.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);
                }

                gifDrawable = (GifDrawable) gifView.getDrawable();
            }
        }

        @Override void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playAndPusGif(position, holder);
        }

        private void playAndPusGif(int position, RecyclerView.ViewHolder holder) {

            ViewHolder vh = (ViewHolder) holder;

            GifDrawable gifDrawable = vh.gifDrawable;
            if (gifDrawable != null) {
                if (gifDrawable.isPlaying()) {
                    gifDrawable.pause();
                    vh.messageProgress.setVisibility(View.VISIBLE);
                } else {
                    gifDrawable.start();
                    vh.messageProgress.setVisibility(View.GONE);
                }
            }
        }
    }

    //****************************************************

    public class FileAdapter extends mAdapter {

        public FileAdapter(Context context, RealmResults<RealmShearedMedia> realmResult) {
            super(context, realmResult);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (mList.get(position).isItemTime()) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (!mList.get(position).isItemTime()) {
                ViewHolder vh = (ViewHolder) holder;
                File file = new File(vh.tempFilePath);

                if (file.exists()) {
                    ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(vh.tempFilePath), vh.imvPicFile);
                } else {
                    Bitmap bitmap = HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource(mList.get(position).getAttachment_name()));
                    if (bitmap != null) vh.imvPicFile.setImageBitmap(bitmap);
                }
            }
        }

        public class ViewHolder extends mHolder {
            public ImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view, int position) {
                super(view, position);

                RealmShearedMedia _re = mList.get(position);

                imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
                tempFilePath = getThumpnailPath(_re.getAttachment_token(), _re.getAttachment_name());
                filePath = getFilePath(_re.getAttachment_token(), _re.getAttachment_name(), ProtoGlobal.RoomMessageType.forNumber(_re.getMessageType()));

                File file = new File(filePath);
                if (file.exists()) {
                    messageProgress.setVisibility(View.GONE);
                } else {
                    needDownloadList.put(_re.getMessageId(), true);
                    messageProgress.setVisibility(View.VISIBLE);
                }

                TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
                TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
                TextView txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
                txtFileSize.setVisibility(View.INVISIBLE);

                txtFileName.setText(_re.getAttachment_name());
                txtFileInfo.setText(AndroidUtils.humanReadableByteCount(_re.getAttachment_size(), true));
            }
        }

        @Override void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            openFile(position, holder);
        }

        private void openFile(int position, RecyclerView.ViewHolder holder) {

            ViewHolder vh = (ViewHolder) holder;

            Intent intent = HelperMimeType.appropriateProgram(vh.filePath);
            if (intent != null) context.startActivity(intent);
        }
    }

    //****************************************************

    public class LinkAdapter extends mAdapter {

        public LinkAdapter(Context context, RealmResults<RealmShearedMedia> realmResult) {
            super(context, realmResult);
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (mList.get(position).isItemTime()) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view, position);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_media_sub_layout_link, null);
                viewHolder = new ViewHolder(view, position);
            }

            return viewHolder;
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        public class ViewHolder extends mHolder {
            public TextView txtLink;

            public ViewHolder(View view, int position) {
                super(view, position);

                txtLink = (TextView) itemView.findViewById(R.id.smsll_txt_shared_link);

                ProtoGlobal.RoomMessage item = (ProtoGlobal.RoomMessage) SerializationUtils.deserialize(mList.get(position).getRoomMessage());

                txtLink.setText(HelperUrl.setUrlLink(item.getMessage(), true, false, "", true));

                txtLink.setMovementMethod(LinkMovementMethod.getInstance());
                messageProgress.setVisibility(View.GONE);
            }
        }

        @Override void openSelectedItem(int position, RecyclerView.ViewHolder holder) {

        }
    }


}

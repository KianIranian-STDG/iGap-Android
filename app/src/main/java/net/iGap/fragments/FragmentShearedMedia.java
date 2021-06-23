/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnProgress;
import net.iGap.model.GoToSharedMediaModel;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.RadiusImageView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.TimeUtils;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.observers.interfaces.OnClientSearchRoomHistory;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoClientCountRoomHistory;
import net.iGap.proto.ProtoClientSearchRoomHistory;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmConstants;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestClientCountRoomHistory;
import net.iGap.request.RequestClientSearchRoomHistory;
import net.iGap.structs.MessageObject;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.fragmentActivity;
import static net.iGap.module.AndroidUtils.suitablePath;

public class FragmentShearedMedia extends BaseFragment implements ToolbarListener {

    public static String SELECTED_TAB_ID = "selected_tab";
    private static final String ROOM_ID = "RoomId";
    private static final String USERNAME = "USERNAME";
    public static ArrayList<Long> list = new ArrayList<>();
    private static long countOFImage = 0;
    private static long countOFVIDEO = 0;
    private static long countOFAUDIO = 0;
    private static long countOFVOICE = 0;
    private static long countOFGIF = 0;
    private static long countOFFILE = 0;
    private static long countOFLink = 0;
    public ArrayList<StructShearedMedia> SelectedList = new ArrayList<>();
    public ArrayList<Long> bothDeleteMessageId = new ArrayList<>();
    protected ArrayMap<Long, Boolean> needDownloadList = new ArrayMap<>();
    private RealmResults<RealmRoomMessage> mRealmList;
    private ArrayList<StructShearedMedia> mNewList;
    private RealmChangeListener<RealmResults<RealmRoomMessage>> changeListener;
    private ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter mFilter;
    private RecyclerView recyclerView;
    private RecyclerView.OnScrollListener onScrollListener;
    private mAdapter adapter;
    private OnComplete complete;
    private ProgressBar progressBar;
    private LinearLayout ll_AppBarSelected;
    private LinearLayout mediaLayout;
    private TextView txtNumberOfSelected;
    private ProtoGlobal.Room.Type roomType;
    private int numberOfSelected = 0;
    private int listCount = 0;
    private int changeSize = 0;
    private int spanItemCount = 4;
    private int offset;
    private boolean isChangeSelectType = false;
    private boolean canUpdateAfterDownload = false;
    private boolean isSelectedMode = false; // for determine user select some file
    private boolean isSendRequestForLoading = false;
    private boolean isThereAnyMoreItemToLoad = false;
    private long nextMessageId = 0;
    private long roomId = 0;
    private MaterialDesignTextView btnGoToPage;
    public static GoToPositionFromShardMedia goToPositionFromShardMedia;
    public static boolean goToPosition = false;
    private int mCurrentSharedMediaType = 1; // 1 = image / 2 = video / 3 = audio / 4 = voice / 5 = gif / 6 = file / 7 = link
    private HelperToolbar mHelperToolbar;
    private boolean isToolbarInEditMode = false;
    private HashMap<Integer, String> mSharedTypesList = new HashMap<Integer, String>();
    private List<SharedButtons> mSharedTypeButtonsList = new ArrayList<>();
    private LinearLayout mediaTypesLayout;

    private void initSharedTypes() {
        mSharedTypesList.clear();
        mSharedTypesList.put(1, getString(R.string.images));
        mSharedTypesList.put(2, getString(R.string.videos));
        mSharedTypesList.put(3, getString(R.string.audios));
        mSharedTypesList.put(4, getString(R.string.voices));
        mSharedTypesList.put(5, getString(R.string.gifs));
        mSharedTypesList.put(6, getString(R.string.files));
        mSharedTypesList.put(7, getString(R.string.links));
    }

    public static FragmentShearedMedia newInstance(GoToSharedMediaModel model) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, model.getRoomId());
        args.putInt(SELECTED_TAB_ID, model.getType());
        FragmentShearedMedia fragment = new FragmentShearedMedia();
        fragment.setArguments(args);
        return fragment;
    }

    public static void updateStringSharedMediaCount(ProtoClientCountRoomHistory.ClientCountRoomHistoryResponse.Builder proto, long roomId) {

        if (proto != null) {

            countOFImage = proto.getImage();
            countOFVIDEO = proto.getVideo();
            countOFAUDIO = proto.getAudio();
            countOFVOICE = proto.getVoice();
            countOFGIF = proto.getGif();
            countOFFILE = proto.getFile();
            countOFLink = proto.getUrl();

            String result = countOFImage + "\n" + countOFVIDEO + "\n" + countOFAUDIO + "\n" + countOFVOICE + "\n" + countOFGIF + "\n" + countOFFILE + "\n" + countOFLink;

            RealmRoom.setCountShearedMedia(roomId, result);
        }
    }

    public static void getCountOfSharedMedia(long roomId) {

        new RequestClientCountRoomHistory().clientCountRoomHistory(roomId);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_sheared_media, container, false));
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mediaLayout = (LinearLayout) view.findViewById(R.id.asm_ll_music_layout);
        //MusicPlayer.setMusicPlayer(mediaLayout);

        roomId = getArguments().getLong(ROOM_ID);
        roomType = RealmRoom.detectType(roomId);

        initSharedTypes();
        initComponent(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        canUpdateAfterDownload = true;

        setListener();

        //MusicPlayer.shearedMediaLayout = mediaLayout;
        ActivityMain.setMediaLayout();
    }

    @Override
    public void onStop() {
        super.onStop();

        canUpdateAfterDownload = false;

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MusicPlayer.shearedMediaLayout = null;
        G.onClientSearchRoomHistory = null;

        ActivityMain.setMediaLayout();
    }

    private void initComponent(View view) {
        LinearLayout toolbarLayout = view.findViewById(R.id.frg_shared_media_ll_toolbar_layout);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                //.setRightIcons(R.string.sort_icon)
                .setPlayerEnable(true)
                .setIsSharedMedia(true)
                //.setSearchBoxShown(true)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.shared_media))
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(this);

        toolbarLayout.addView(mHelperToolbar.getView());


        progressBar = view.findViewById(R.id.asm_progress_bar_waiting);
        AppUtils.setProgresColler(progressBar);

        mediaTypesLayout = view.findViewById(R.id.asm_ll_media_types_buttons);

        complete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                int whatAction = 0;
                String number = "0";

                if (messageOne != null) {
                    if (messageOne.length() > 0) whatAction = Integer.parseInt(messageOne);
                }

                if (MessageTow != null) if (MessageTow.length() > 0) number = MessageTow;

                callBack(result, whatAction, number);
            }
        };

        recyclerView = view.findViewById(R.id.asm_recycler_view_sheared_media);
        recyclerView.setItemViewCacheSize(400);
        recyclerView.setItemAnimator(null);


        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 30 >= offset) {

                            new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, nextMessageId, mFilter);

                            isSendRequestForLoading = true;
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };

        recyclerView.addOnScrollListener(onScrollListener);

        checkSelectedDefaultTab();
        initAppbarSelected(view);
        makeSharedTypesViews();
        checkSharedButtonsBackgrounds();
    }

    private void checkSelectedDefaultTab() {

        if (getArguments() != null && getArguments().getInt(SELECTED_TAB_ID, 0) != 0) {

            mCurrentSharedMediaType = 0;
            int tab = getArguments().getInt(SELECTED_TAB_ID);
            mediaTypesClickHandler(tab);
            mCurrentSharedMediaType = tab;

        } else {
            openLayout();
        }
    }

    private void openLayout() {

        if (countOFImage > 0) {
            fillListImage();
        } else if (countOFVIDEO > 0) {
            fillListVideo();
        } else if (countOFAUDIO > 0) {
            fillListAudio();
        } else if (countOFVOICE > 0) {
            fillListVoice();
        } else if (countOFGIF > 0) {
            fillListGif();
        } else if (countOFFILE > 0) {
            fillListFile();
        } else if (countOFLink > 0) {
            fillListLink();
        } else {
            fillListImage();
        }
    }

    private void initAppbarSelected(View view) {
        RippleView rippleCloseAppBarSelected = view.findViewById(R.id.asm_ripple_close_layout);
        rippleCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.resetSelected();
            }
        });

        btnGoToPage = view.findViewById(R.id.asm_btn_goToPage);
        btnGoToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SelectedList.size() == 1) {
                    long messageId = SelectedList.get(0).messageId;
                    RealmRoomMessage.setGap(messageId);
                    goToPositionFromShardMedia.goToPosition(messageId);
                    goToPosition = true;
                    popBackStackFragment();
                    adapter.resetSelected();
                    popBackStackFragment();
                }
            }
        });

        MaterialDesignTextView btnForwardSelected = view.findViewById(R.id.asm_btn_forward_selected);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MessageObject> messageInfos = new ArrayList<>(SelectedList.size());
                for (StructShearedMedia media : SelectedList) {
                    messageInfos.add(MessageObject.create(media.item));
                }
                FragmentChat.mForwardMessages = messageInfos;
                adapter.resetSelected();
                if (getActivity() instanceof ActivityMain) {
                    ((ActivityMain) getActivity()).setForwardMessage(true);
                    ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                    /*new HelperFragment(getActivity().getSupportFragmentManager()).popBackStack(3);*/
                }
            }
        });

        RippleView rippleDeleteSelected = view.findViewById(R.id.asm_riple_delete_selected);

        if (roomType == ProtoGlobal.Room.Type.CHANNEL)
            rippleDeleteSelected.setVisibility(View.GONE);

        rippleDeleteSelected.setOnRippleCompleteListener(rippleView -> DbManager.getInstance().doRealmTask(realm -> {
            
            String count = SelectedList.size() + "";
            final RealmRoom realmRoom = RealmRoom.getRealmRoom(realm, roomId);

            if (roomType == ProtoGlobal.Room.Type.CHAT && bothDeleteMessageId != null && bothDeleteMessageId.size() > 0) {
                // TODO: 1/10/21 OPTIMAZE CODE
                // show both Delete check box
                String delete;
                if (HelperCalander.isPersianUnicode) {
                    delete = HelperCalander.convertToUnicodeFarsiNumber(Objects.requireNonNull(getContext()).getResources().getString(R.string.st_desc_delete) + count);
                } else {
                    delete = HelperCalander.convertToUnicodeFarsiNumber(Objects.requireNonNull(getContext()).getResources().getString(R.string.st_desc_delete) + "the");
                }
                new MaterialDialog.Builder(getContext()).limitIconToDefaultSize().content(delete).title(R.string.message).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive((dialog, which) -> {
                    if (!dialog.isPromptCheckBoxChecked()) {
                        bothDeleteMessageId = null;
                    }
                    if (realmRoom != null) {
                        ArrayList<Long> selectedListForDel = new ArrayList<>();

                        for (StructShearedMedia item : SelectedList) {
                            selectedListForDel.add(item.messageId);
                        }
                        getMessageController().deleteSelectedMessage(roomType.getNumber(), roomId, selectedListForDel, bothDeleteMessageId);
                    }
                    resetItems();
                }).checkBoxPromptRes(R.string.delete_item_dialog, false, null).show();

            } else {
                new MaterialDialog.Builder(Objects.requireNonNull(getContext())).title(R.string.message).content(getContext().getResources().getString(R.string.st_desc_delete)+ count).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        bothDeleteMessageId = null;
                        if (realmRoom != null) {
                            ArrayList<Long> messageIds = new ArrayList<>();
                            for (StructShearedMedia item : SelectedList) {
                                messageIds.add(item.messageId);
                            }
                            getMessageController().deleteSelectedMessage(roomType.getNumber(), roomId, messageIds, bothDeleteMessageId);
                        }
                        resetItems();
                    }
                }).show();
            }
        }));

        txtNumberOfSelected = view.findViewById(R.id.asm_txt_number_of_selected);

        ll_AppBarSelected = view.findViewById(R.id.asm_ll_appbar_selelected);
    }

    private void resetItems() {
        for (StructShearedMedia item : SelectedList) {
            list.add(item.messageId);
        }

        switch (mFilter) {
            case IMAGE:
                countOFImage -= SelectedList.size();
                break;
            case VIDEO:
                countOFVIDEO -= SelectedList.size();
                break;
            case AUDIO:
                countOFAUDIO -= SelectedList.size();
                break;
            case FILE:
                countOFFILE -= SelectedList.size();
                break;
            case GIF:
                countOFGIF -= SelectedList.size();
                break;
            case VOICE:
                countOFVOICE -= SelectedList.size();
                break;
            case URL:
                countOFLink -= SelectedList.size();
                break;
        }

        updateStringSharedMediaCount(null, roomId);

        adapter.resetSelected();
    }

    @Override //back button at toolbar
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

    @Override
    public void onSearchClickListener(View view) {
        if (!isToolbarInEditMode) {
            isToolbarInEditMode = true;
            openKeyBoard();
        }
    }

    @Override
    public void onBtnClearSearchClickListener(View view) {
        if (mHelperToolbar.getEditTextSearch().getText().length() > 0) {
            mHelperToolbar.getEditTextSearch().setText("");
        } else {
            isToolbarInEditMode = false;

        }
    }

    @Override
    public void onSearchTextChangeListener(View view, String text) {

    }

    @Override //menu button at toolbar
    public void onRightIconClickListener(View view) {
        popUpMenuSharedMedia();
    }

    private void makeSharedTypesViews() {
        for (int i = 1; i < mSharedTypesList.size() + 1; i++) makeButton(i);
    }

    private void makeButton(final int pos) {

        TextView textView = new TextView(getContext());
        textView.setText(mSharedTypesList.get(pos));
        Utils.setTextSize(textView, R.dimen.smallTextSize);
        textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.main_font));
        textView.setSingleLine(true);

        textView.setBackgroundResource(new Theme().getButtonSelectorBackground(textView.getContext()));
        textView.setTextColor(ContextCompat.getColorStateList(textView.getContext(), R.color.button_text_color_selector));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (pos == 0 || pos == mSharedTypesList.size() + 1) {
            lp.setMargins(getDimen(R.dimen.dp10), getDimen(R.dimen.dp4), getDimen(R.dimen.dp10), getDimen(R.dimen.dp2));
        } else {
            lp.setMargins(getDimen(R.dimen.dp4), getDimen(R.dimen.dp4), getDimen(R.dimen.dp4), getDimen(R.dimen.dp2));
        }
        textView.setLayoutParams(lp);
        textView.setPadding(getDimen(R.dimen.round_buttons_large_padding), getDimen(R.dimen.round_buttons_small_padding), getDimen(R.dimen.round_buttons_large_padding), getDimen(R.dimen.round_buttons_small_padding));

        mSharedTypeButtonsList.add(new SharedButtons(textView, pos));

        textView.setOnClickListener(v -> {

            if (isSelectedMode && mCurrentSharedMediaType != pos) adapter.resetSelected();

            mediaTypesClickHandler(pos);
            checkSharedButtonsBackgrounds();

        });

        mediaTypesLayout.addView(textView);
    }

    private void checkSharedButtonsBackgrounds() {
        for (int i = 0; i < mSharedTypeButtonsList.size(); i++) {
            mSharedTypeButtonsList.get(i).getButton().setSelected(mCurrentSharedMediaType == mSharedTypeButtonsList.get(i).getId());
        }
    }

    private void mediaTypesClickHandler(int pos) {
        switch (pos) {

            case 1:
                if (mCurrentSharedMediaType != 1)
                    fillListImage();
                break;

            case 2:
                if (mCurrentSharedMediaType != 2)
                    fillListVideo();
                break;

            case 3:
                if (mCurrentSharedMediaType != 3)
                    fillListAudio();
                break;

            case 4:
                if (mCurrentSharedMediaType != 4)
                    fillListVoice();
                break;

            case 5:
                if (mCurrentSharedMediaType != 5)
                    fillListGif();
                break;

            case 6:
                if (mCurrentSharedMediaType != 6)
                    fillListFile();
                break;

            case 7:
                if (mCurrentSharedMediaType != 7)
                    fillListLink();
                break;

            default:
                fillListImage();
        }
    }

    private int getDimen(int id) {
        return (int) getContext().getResources().getDimension(id);
    }

    //********************************************************************************************

    private void callBack(boolean result, int whatAction, String number) {

        switch (whatAction) {

            case 1://for show or gone layout appBar selected
                if (result) {
                    ll_AppBarSelected.setVisibility(View.VISIBLE);
                    txtNumberOfSelected.setText(number);
                    if (SelectedList.size() == 1) {
                        btnGoToPage.setVisibility(View.VISIBLE);
                    } else {
                        btnGoToPage.setVisibility(View.GONE);
                    }
                } else {
                    ll_AppBarSelected.setVisibility(View.GONE);
                }
                break;
        }
    }

    //********************************************************************************************

    public void popUpMenuSharedMedia() {

        List<String> items = new ArrayList<>();
        items.add(getString(R.string.name));
        items.add(getString(R.string.date));
        items.add(getString(R.string.size));

        new TopSheetDialog(getContext()).setListData(items, -1, position -> {

        }).show();
    }

    private void initLayoutRecycleviewForImage() {

        final PreCashGridLayout gLayoutManager = new PreCashGridLayout(fragmentActivity, spanItemCount);

        gLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < mNewList.size() && mNewList.get(position).isItemTime) {
                    return spanItemCount;
                } else {
                    return 1;
                }
            }
        });


/*
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.dp10);
        recyclerView.addItemDecoration(new GridViewItemDecorView(spacingInPixels));
*/

        recyclerView.setLayoutManager(gLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewWidth = recyclerView.getMeasuredWidth();
                float cardViewWidth = G.context.getResources().getDimension(R.dimen.dp100);
                int newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);

                if (newSpanCount < 4) {
                    newSpanCount = 4;
                }

                //spanItemCount = newSpanCount;
                gLayoutManager.setSpanCount(spanItemCount);
                gLayoutManager.requestLayout();
            }
        });
    }

    private void fillListImage() {

        mCurrentSharedMediaType = 1;

        isChangeSelectType = true;

        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.IMAGE;
        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.IMAGE);
        adapter = new ImageAdapter(fragmentActivity, mNewList);
        initLayoutRecycleviewForImage();

        isChangeSelectType = false;
    }

    private void fillListVideo() {

        mCurrentSharedMediaType = 2;
        isChangeSelectType = true;

        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VIDEO;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.VIDEO);
        adapter = new VideoAdapter(fragmentActivity, mNewList);
        initLayoutRecycleviewForImage();

        isChangeSelectType = false;
    }

    private void fillListAudio() {

        mCurrentSharedMediaType = 3;
        isChangeSelectType = true;

        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.AUDIO);
        adapter = new AudioAdapter(fragmentActivity, mNewList);

        recyclerView.setLayoutManager(new PreCachingLayoutManager(fragmentActivity, 5000));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;
    }

    private void fillListVoice() {

        mCurrentSharedMediaType = 4;
        isChangeSelectType = true;

        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.VOICE;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.VOICE);
        adapter = new VoiceAdapter(fragmentActivity, mNewList);

        recyclerView.setLayoutManager(new PreCachingLayoutManager(fragmentActivity, 5000));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;
    }

    private void fillListGif() {

        mCurrentSharedMediaType = 5;
        isChangeSelectType = true;

        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.GIF;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.GIF);
        adapter = new GifAdapter(fragmentActivity, mNewList);

        initLayoutRecycleviewForImage();

        isChangeSelectType = false;
    }

    private void fillListFile() {

        mCurrentSharedMediaType = 6;
        isChangeSelectType = true;

        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.FILE;

        mNewList = loadLocalData(mFilter, ProtoGlobal.RoomMessageType.FILE);
        adapter = new FileAdapter(fragmentActivity, mNewList);

        recyclerView.setLayoutManager(new PreCachingLayoutManager(fragmentActivity, 5000));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;
    }

    private void fillListLink() {

        mCurrentSharedMediaType = 7;
        isChangeSelectType = true;

        mFilter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.URL;

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }
        mRealmList = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRoomMessage.filterMessage(realm, roomId, ProtoGlobal.RoomMessageType.TEXT);
        });

        setListener();

        changeSize = mRealmList.size();

        getDataFromServer(mFilter);

        listCount = mRealmList.size();

        mNewList = addTimeToList(mRealmList);
        adapter = new LinkAdapter(recyclerView.getContext(), mNewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);

        isChangeSelectType = false;
    }

    //********************************************************************************************

    private ArrayList<StructShearedMedia> loadLocalData(ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter, ProtoGlobal.RoomMessageType type) {

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }

        mRealmList = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRoomMessage.filterMessage(realm, roomId, type);
        });

        setListener();

        changeSize = mRealmList.size();
        isSendRequestForLoading = false;
        isThereAnyMoreItemToLoad = true;

        getDataFromServer(filter);
        listCount = mRealmList.size();

        return addTimeToList(mRealmList);
    }

    private ArrayList<StructShearedMedia> addTimeToList(RealmResults<RealmRoomMessage> list) {

        ArrayList<StructShearedMedia> result = new ArrayList<>();

        String firstItemTime = "";
        String secondItemTime = "";
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy/MM/dd");

        int isTimeHijri = HelperCalander.isTimeHijri();

        for (int i = 0; i < list.size(); i++) {

            Long time = list.get(i).getUpdateTime();
            secondItemTime = month_date.format(time);

            if (secondItemTime.compareTo(firstItemTime) > 0 || secondItemTime.compareTo(firstItemTime) < 0) {

                StructShearedMedia timeItem = new StructShearedMedia();

                if (isTimeHijri == 1) {
                    timeItem.messageTime = HelperCalander.getPersianCalander(time);
                } else if (isTimeHijri == 2) {
                    timeItem.messageTime = HelperCalander.getArabicCalander(time);
                } else {
                    timeItem.messageTime = secondItemTime;
                }

                timeItem.isItemTime = true;

                //check time is today for set string instead of number date
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                if (TimeUtils.getCheckDateIsToday(calendar.getTime()))
                    timeItem.isToday = true;

                result.add(timeItem);

                firstItemTime = secondItemTime;
            }

            StructShearedMedia _item = new StructShearedMedia();
            _item.item = list.get(i);
            _item.messageId = list.get(i).getMessageId();

            result.add(_item);
        }

        return result;
    }

    private void getDataFromServer(final ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        offset = 0;
        nextMessageId = 0;

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, final int notDeletedCount, final List<ProtoGlobal.RoomMessage> resultList, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter identity) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                if (resultList.size() > 0) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            int _listc = listCount;


                            saveDataToLocal(resultList, roomId);

                            nextMessageId = resultList.get(0).getMessageId();

                            isSendRequestForLoading = false;
                            isThereAnyMoreItemToLoad = true;

                            int deletedCount = 0;
                            for (int i = 0; i < resultList.size(); i++) {
                                if (resultList.get(i).getDeleted()) {
                                    deletedCount++;
                                }
                            }

                            offset += resultList.size() - deletedCount;
                        }
                    }).start();
                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError(final int majorCode, int minorCode, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter identity) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);

                        if (majorCode == 620) {

                            isThereAnyMoreItemToLoad = false;

                            //if (onScrollListener != null) {
                            //    recyclerView.removeOnScrollListener(onScrollListener);
                            //}
                        } else {
                            isSendRequestForLoading = false;
                        }
                    }
                });
            }
        };

        new RequestClientSearchRoomHistory().clientSearchRoomHistory(roomId, nextMessageId, filter);
        progressBar.setVisibility(View.VISIBLE);
        isSendRequestForLoading = true;
    }

    public void saveDataToLocal(final List<ProtoGlobal.RoomMessage> RoomMessages, final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm1 -> {
            for (final ProtoGlobal.RoomMessage roomMessage : RoomMessages) {
                RealmRoomMessage.putOrUpdate(realm1, roomId, roomMessage, new StructMessageOption().setFromShareMedia());
            }
        });
    }

    //********************************************************************************************

    private void setListener() {

        changeListener = new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
            @Override
            public void onChange(RealmResults<RealmRoomMessage> element) {

                if (isChangeSelectType) {
                    return;
                }

                if (changeSize - element.size() != 0) {

                    mNewList.clear();
                    mNewList.addAll(addTimeToList(element));

                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (adapter instanceof ImageAdapter) {
                        adapter = new ImageAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof VideoAdapter) {
                        adapter = new VideoAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof AudioAdapter) {
                        adapter = new AudioAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof VoiceAdapter) {
                        adapter = new VoiceAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof GifAdapter) {
                        adapter = new GifAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof FileAdapter) {
                        adapter = new FileAdapter(fragmentActivity, mNewList);
                    } else if (adapter instanceof LinkAdapter) {
                        adapter = new LinkAdapter(fragmentActivity, mNewList);
                    }

                    recyclerView.setAdapter(adapter);

                    recyclerView.scrollToPosition(position);

                    listCount = element.size();
                    changeSize = element.size();
                }
            }
        };

        mRealmList.addChangeListener(changeListener);
    }

    /**
     * Simple Class to serialize object to byte arrays
     *
     * @author Nick Russler
     * http://www.whitebyte.info
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
        @SuppressWarnings("unchecked")
        public static <T> T cloneObject(T obj) {
            return (T) deserialize(serialize(obj));
        }
    }

    public class StructShearedMedia {
        RealmRoomMessage item;
        boolean isItemTime = false;
        String messageTime;
        long messageId;
        boolean isToday = false;

        public StructShearedMedia() {
        }

        public StructShearedMedia(long messageId) {
            this.messageId = messageId;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof StructShearedMedia) {
                return this.messageId == ((StructShearedMedia) obj).messageId;
            }
            return super.equals(obj);
        }
    }

    private class PreCashGridLayout extends GridLayoutManager {

        private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 7000;

        public PreCashGridLayout(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        protected int getExtraLayoutSpace(RecyclerView.State state) {
            return DEFAULT_EXTRA_LAYOUT_SPACE;
        }
    }

    //****************************************************    Adapter    ****************************************

    public abstract class mAdapter extends RecyclerView.Adapter {

        protected ArrayList<StructShearedMedia> mList;
        protected Context context;

        public mAdapter(Context context, ArrayList<StructShearedMedia> mList) {
            this.mList = mList;
            this.context = context;
        }

        abstract void openSelectedItem(int position, RecyclerView.ViewHolder holder);

        @Override
        public int getItemViewType(int position) {

            if (mList.get(position).isItemTime) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if (holder.getItemViewType() == 1) {
                setBackgroundColor(holder, position);

                mAdapter.mHolder holder1 = (mAdapter.mHolder) holder;

                if (mList.get(position).item.getAttachment() != null) {

                    holder1.messageProgress.setTag(mList.get(position).messageId);
                    holder1.messageProgress.withDrawable(R.drawable.ic_download, true);

                    if (getDownloader().isDownloading(mList.get(position).item.getAttachment().getCacheId())) {
                        startDownload(position, holder1.messageProgress);
                    }
                }
            } else {
                mAdapter.ViewHolderTime holder1 = (mAdapter.ViewHolderTime) holder;

                //convert numbers to persian if language was
                String date;
                if (G.selectedLanguage.equals("fa")) {
                    holder1.txtTime.setGravity(Gravity.RIGHT);
                    date = HelperCalander.convertToUnicodeFarsiNumber(mList.get(position).messageTime);
                } else {
                    holder1.txtTime.setGravity(Gravity.LEFT);
                    date = mList.get(position).messageTime;
                }
                //check if date was today set text to txtTime else set date
                holder1.txtTime.setText(
                        !mList.get(position).isToday ?
                                date : context.getString(R.string.today)
                );
            }
        }

        private int getDimen(int id) {
            return (int) context.getResources().getDimension(id);
        }

        public View setLayoutHeaderTime(View parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return view;
        }

        private void setBackgroundColor(RecyclerView.ViewHolder holder, int position) {

            try {
                // set blue back ground for selected file
                FrameLayout layout = holder.itemView.findViewById(R.id.smsl_fl_contain_main);

                if (SelectedList.indexOf(new StructShearedMedia(mList.get(position).messageId)) != -1) {
                    layout.setForeground(getContext().getResources().getDrawable(R.drawable.selected_item_foreground));
                } else {
                    layout.setForeground(new ColorDrawable(Color.TRANSPARENT));
                }
            } catch (Exception e) {

            }
        }

        private void openSelected(int position, RecyclerView.ViewHolder holder) {

            if (needDownloadList.containsKey(mList.get(position).messageId)) {

                // first need to download file

            } else {

                openSelectedItem(position, holder);
            }
        }

        private void setSelectedItem(final int position) {

            if (mList == null || mList.size() == 0) {
                return;
            }

            long messageId = mList.get(position).messageId;

            int index = SelectedList.indexOf(new StructShearedMedia(messageId));

            if (index != -1) {
                SelectedList.remove(mList.get(position));
                numberOfSelected--;
                if (bothDeleteMessageId != null) {
                    bothDeleteMessageId.remove(messageId);
                }

                if (numberOfSelected < 1) {
                    isSelectedMode = false;
                }
            } else {
                SelectedList.add(mList.get(position));

                if (RealmRoomMessage.isBothDelete(RealmRoomMessage.getMessageTime(messageId))) {
                    if (bothDeleteMessageId == null) {
                        bothDeleteMessageId = new ArrayList<>();
                    }
                    bothDeleteMessageId.add(messageId);
                }

                numberOfSelected++;
            }
            notifyItemChanged(position);

            if (complete != null) {
                complete.complete(isSelectedMode, "1", numberOfSelected + "");
            }
        }

        private void startDownload(final int position, final MessageProgress messageProgress) {

            messageProgress.withDrawable(R.drawable.ic_cancel, true);

            final RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();
            ProtoGlobal.RoomMessageType messageType = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getMessageType() : mList.get(position).item.getMessageType();

            messageProgress.withOnProgress(new OnProgress() {
                @Override
                public void onProgressFinished() {
                    if (messageProgress.getTag() != null && messageProgress.getTag().equals(mList.get(position).messageId)) {
                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageProgress.withProgress(0);
                                messageProgress.setVisibility(View.GONE);
                                updateViewAfterDownload(at.getCacheId());
                            }
                        });
                    }
                }
            });

            DownloadObject fileObject = DownloadObject.createForRoomMessage(mList.get(position).item);

            if (fileObject != null)
                getDownloader().download(fileObject, ProtoFileDownload.FileDownload.Selector.FILE, arg -> {
                    if (canUpdateAfterDownload) {
                        G.handler.post(() -> {
                            switch (arg.status) {
                                case LOADING:
                                case SUCCESS:
                                    if (arg.data != null)
                                        messageProgress.withProgress(arg.data.getProgress());
                                    break;
                                case ERROR:
                                    if (messageProgress.getTag() != null && messageProgress.getTag().equals(mList.get(position).messageId)) {
                                        messageProgress.withProgress(0);
                                        messageProgress.withDrawable(R.drawable.ic_download, true);
                                    }
                            }
                        });
                    }
                });
        }

        private void updateViewAfterDownload(String cashId) {
            for (int j = mNewList.size() - 1; j >= 0; j--) {
                try {
                    if (mNewList.get(j).item != null && mNewList.get(j).item.isValid() && !mNewList.get(j).item.isDeleted()) {
                        String mCashId = mNewList.get(j).item.getForwardMessage() != null ? mNewList.get(j).item.getForwardMessage().getAttachment().getCacheId() : mNewList.get(j).item.getAttachment().getCacheId();
                        if (mCashId.equals(cashId)) {
                            needDownloadList.remove(mNewList.get(j).messageId);

                            final int finalJ = j;
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    recyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.getAdapter().notifyItemChanged(finalJ);
                                        }
                                    });
                                }
                            });
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        private void stopDownload(int position) {

            getDownloader().cancelDownload(mList.get(position).item.getAttachment().getCacheId());
        }

        private void downloadFile(int position, MessageProgress messageProgress) {

            if (getDownloader().isDownloading(mList.get(position).item.getAttachment().getCacheId())) {
                stopDownload(position);
            } else {
                startDownload(position, messageProgress);
            }
        }

        public boolean resetSelected() {

            boolean result = isSelectedMode;

            if (isSelectedMode) {
                isSelectedMode = false;

                SelectedList.clear();
                notifyDataSetChanged();
                // TODO: 1/2/2017  nejate best action for notify
                numberOfSelected = 0;
            }
            complete.complete(false, "1", "0");//

            return result;
        }

        public String getThumpnailPath(int position) {

            String result = "";

            RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

            if (at != null) {
                if (at.getLocalThumbnailPath() != null) {
                    result = at.getLocalThumbnailPath();
                }

                if (result.length() < 1) {
                    AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), G.DIR_TEMP, true);
                }
            }

            return result;
        }

        public String getFilePath(int position) {

            String result = "";

            RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

            if (at != null) {
                if (at.getLocalFilePath() != null) {
                    result = at.getLocalFilePath();
                }

                ProtoGlobal.RoomMessageType messageType = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getMessageType() : mList.get(position).item.getMessageType();

                if (result.length() < 1) {
                    result = AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), messageType);
                }
            }

            return result;
        }

        public class mHolder extends RecyclerView.ViewHolder {

            public MessageProgress messageProgress;

            public mHolder(View view) {
                super(view);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isSelectedMode) {
                            setSelectedItem(getPosition());
                        } else {

                            openSelected(getPosition(), mAdapter.mHolder.this);
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        isSelectedMode = true;
                        setSelectedItem(getPosition());
                        return true;
                    }
                });

                messageProgress = itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(messageProgress.progressBar);

                messageProgress.withDrawable(R.drawable.ic_download, true);


                messageProgress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isSelectedMode) {
                            setSelectedItem(getPosition());
                        } else {
                            downloadFile(getPosition(), messageProgress);
                        }
                    }
                });

                messageProgress.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        isSelectedMode = true;
                        setSelectedItem(getPosition());
                        return true;
                    }
                });
            }
        }

        public class ViewHolderTime extends RecyclerView.ViewHolder {
            public TextView txtTime;
            public TextView txtHeader;
            public View vSplitter;

            public ViewHolderTime(View view) {
                super(view);
                txtTime = itemView.findViewById(R.id.smslt_txt_time);
                txtHeader = itemView.findViewById(R.id.smslt_txt_header);
                vSplitter = itemView.findViewById(R.id.smslt_time_shared_splitter);
            }
        }
    }

    //****************************************************

    public class ImageAdapter extends mAdapter {

        public ImageAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new ImageAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                final ImageAdapter.ViewHolder vh = (ImageAdapter.ViewHolder) holder;

                vh.tempFilePath = getThumpnailPath(position);
                vh.filePath = getFilePath(position);

                vh.imvPicFile.setImageResource(R.drawable.shared_media_images_holder);

                vh.imvPicFile.setTag(mList.get(position).messageId);

                // if thumpnail not exist download it
                File filet = new File(vh.tempFilePath);
                if (filet.exists()) {

                    if (vh.imvPicFile.getTag() != null && vh.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                        G.imageLoader.displayImage(suitablePath(vh.tempFilePath), vh.imvPicFile);
                    }
                } else {
                    DownloadObject fileObject = DownloadObject.createForThumb(mList.get(position).item, false);

                    if (fileObject != null) {
                        getDownloader().download(fileObject, ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, arg -> {
                            if (canUpdateAfterDownload) {
                                G.handler.post(() -> {
                                    switch (arg.status) {
                                        case LOADING:
                                        case SUCCESS:
                                            if (arg.data == null)
                                                return;
                                            if (arg.data.getProgress() == 100) {
                                                if (vh.imvPicFile.getTag() != null && vh.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(arg.data.getFilePath()), vh.imvPicFile);
                                                }
                                            }
                                    }
                                });
                            }
                        });
                    }
                }

                File file = new File(vh.filePath);
                if (file.exists()) {
                    vh.messageProgress.setVisibility(View.GONE);
                } else {

                    needDownloadList.put(mList.get(position).messageId, true);

                    vh.messageProgress.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            showImage(position, holder.itemView);
        }

        private void showImage(int position, View itemView) {
            if (getActivity() != null) {
                long selectedFileToken = mList.get(position).messageId;
                FragmentShowContent fragment = FragmentShowContent.newInstance();
                Bundle bundle = new Bundle();
                bundle.putLong(RealmConstants.REALM_ROOM_ID, roomId);
                bundle.putLong(RealmConstants.REALM_SELECTED_IMAGE, selectedFileToken);
                bundle.putString(RealmConstants.REALM_MESSAGE_TYPE, ProtoGlobal.RoomMessageType.IMAGE.toString());
                fragment.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        }

        public class ViewHolder extends mHolder {

            public RadiusImageView imvPicFile;
            public String tempFilePath;
            public String filePath;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = itemView.findViewById(R.id.smsl_imv_file_pic);

            }
        }
    }


    //****************************************************

    public class VideoAdapter extends mAdapter {

        public VideoAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new VideoAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                final VideoAdapter.ViewHolder holder1 = (VideoAdapter.ViewHolder) holder;
                holder1.layoutInfo.setVisibility(View.VISIBLE);

                RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                holder1.imvPicFile.setImageResource(R.drawable.shared_media_videos_holder);
                holder1.imvPicFile.setTag(mList.get(position).messageId);

                final String tempFilePath;
                String filePath;

                holder1.txtVideoTime.setText(AndroidUtils.formatDuration((int) (at.getDuration() * 1000L)));

                holder1.txtVideoSize.setVisibility(View.GONE);
                //holder1.txtVideoIcon.setVisibility(View.GONE);
                //holder1.txtVideoSize.setText("(" + AndroidUtils.humanReadableByteCount(at.getSize(), true) + ")");

                tempFilePath = getThumpnailPath(position);

                File filethumpnail = new File(tempFilePath);

                if (filethumpnail.exists()) {
                    if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), holder1.imvPicFile);
                    }
                } else {
                    holder1.imvPicFile.setImageResource(R.drawable.shared_media_videos_holder);
                    DownloadObject fileObject = DownloadObject.createForThumb(mList.get(position).item, false);

                    if (fileObject != null) {
                        getDownloader().download(fileObject, ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, arg -> {
                            if (canUpdateAfterDownload) {
                                switch (arg.status) {
                                    case SUCCESS:
                                    case LOADING:
                                        if (arg.data == null)
                                            return;
                                        if (arg.data.getProgress() == 100) {
                                            if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                                G.imageLoader.displayImage(AndroidUtils.suitablePath(arg.data.getFilePath()), holder1.imvPicFile);
                                            }
                                        }
                                }
                            }
                        });
                    }
                }

                filePath = getFilePath(position);

                File file = new File(filePath);
                if (file.exists()) {
                    holder1.messageProgress.setVisibility(View.GONE);
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    holder1.messageProgress.setVisibility(View.VISIBLE);
                }
            }
        }

        void openVideoByDefaultApp(int position) {

            try {
                RealmRoomMessage rm = mNewList.get(position).item;
                if (rm.getAttachment().isFileExistsOnLocal()) {
                    File file = new File(rm.getAttachment().getLocalFilePath());
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                    } else {
                        uri = Uri.fromFile(file);
                    }

                    intent.setDataAndType(uri, "video/*");

                    try {
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } catch (Exception e) {
                        // to prevent from 'No Activity found to handle Intent'
                        e.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playVideo(position, holder.itemView);
        }

        private void playVideo(int position, View itemView) {
            SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            if (sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1) == 0) {
                openVideoByDefaultApp(position);
            } else {
                if (getActivity() != null) {
                    long selectedFileToken = mNewList.get(position).messageId;
                    Fragment fragment = FragmentShowContent.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putLong("RoomId", roomId);
                    bundle.putLong("SelectedImage", selectedFileToken);
                    bundle.putString("TYPE", ProtoGlobal.RoomMessageType.VIDEO.toString());
                    fragment.setArguments(bundle);

                    //FragmentTransitionLauncher.with(G.fragmentActivity).from(itemView).prepare(fragment);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }
            }
        }

        public class ViewHolder extends mHolder {
            public RadiusImageView imvPicFile;
            public TextView txtVideoIcon;
            public TextView txtVideoTime;
            public LinearLayout layoutInfo;
            public TextView txtVideoSize;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = itemView.findViewById(R.id.smsl_imv_file_pic);

                layoutInfo = itemView.findViewById(R.id.smsl_ll_video);

                txtVideoIcon = itemView.findViewById(R.id.smsl_txt_video_icon);

                txtVideoTime = itemView.findViewById(R.id.smsl_txt_video_time);

                txtVideoSize = itemView.findViewById(R.id.smsl_txt_video_size);
            }
        }
    }

    //****************************************************

    public class AudioAdapter extends mAdapter {

        public AudioAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new AudioAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                AudioAdapter.ViewHolder holder1 = (AudioAdapter.ViewHolder) holder;

                holder1.imvPicFile.setImageResource(R.drawable.shared_media_audios_holder);
                holder1.imvPicFile.setTag(mList.get(position).messageId);
                holder1.imvPicFile.setScaleType(ImageView.ScaleType.CENTER_CROP);

                RealmAttachment at = mList.get(position).item.getAttachment();

                String tempFilePath = getThumpnailPath(position);
                holder1.filePath = getFilePath(position);

                holder1.txtFileName.setGravity(Gravity.LEFT);
                holder1.txtFileName.setText(at.getName());

                holder1.txtFileSize.setText("" + AndroidUtils.humanReadableByteCount(at.getSize(), true));
                holder1.txtFileInfo.setVisibility(View.GONE);
                holder1.txtFileSize.setGravity(Gravity.LEFT);
                File file = new File(holder1.filePath);

                if (file.exists()) {
                    holder1.messageProgress.setVisibility(View.GONE);

                    try {

                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        Uri uri = Uri.fromFile(file);
                        mediaMetadataRetriever.setDataSource(context, uri);
                        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                        if (artist == null) {
                            artist = context.getString(R.string.unknown_artist);
                        }
                        holder1.txtFileInfo.setText(artist);

                        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                        if (data != null) {
                            Bitmap mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);

                            if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                holder1.imvPicFile.setImageBitmap(mediaThumpnail);
                            }
                        } else {
                            file = new File(tempFilePath);
                            if (file.exists()) {

                                if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), holder1.imvPicFile);
                                }
                            }
                        }
                    } catch (Exception e) {
                        holder1.txtFileInfo.setVisibility(View.GONE);
                    }
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    holder1.messageProgress.setVisibility(View.VISIBLE);
                    file = new File(tempFilePath);

                    if (file.exists()) {

                        if (holder1.imvPicFile.getTag() != null && holder1.imvPicFile.getTag().equals(mList.get(position).messageId)) {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(tempFilePath), holder1.imvPicFile);
                        }
                    }
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playAudio(position, holder);
        }

        private void playAudio(int position, RecyclerView.ViewHolder holder) {

            AudioAdapter.ViewHolder vh = (AudioAdapter.ViewHolder) holder;

            String name = mList.get(position).item.getAttachment().getName();

            MusicPlayer.startPlayer(name, vh.filePath, name, roomId, true, mList.get(position).messageId + "");
        }

        public class ViewHolder extends mHolder {
            public RadiusImageView imvPicFile;
            public TextView tvIconFile;
            public TextView txtFileName;
            public TextView txtFileSize;
            public TextView txtFileInfo;
            public String filePath;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = itemView.findViewById(R.id.smslf_imv_image_file);
                tvIconFile = itemView.findViewById(R.id.smslf_imv_icon_file);
                tvIconFile.setVisibility(View.GONE);
                imvPicFile.setVisibility(View.VISIBLE);

                txtFileName = itemView.findViewById(R.id.smslf_txt_file_name);
                txtFileSize = itemView.findViewById(R.id.smslf_txt_file_size);
                txtFileInfo = itemView.findViewById(R.id.smslf_txt_file_info);
            }
        }
    }

    //****************************************************

    public class VoiceAdapter extends mAdapter {

        public VoiceAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new VoiceAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                VoiceAdapter.ViewHolder holder1 = (VoiceAdapter.ViewHolder) holder;

                //holder1.imvPicFile.setTextColor(getContext().getResources().getColor(R.color.shared_media_music_list_icon_color));
                holder1.imvPicFile.setTag(mList.get(position).messageId);

                RealmAttachment at = mList.get(position).item.getAttachment();

                String tempFilePath = getThumpnailPath(position);
                holder1.filePath = getFilePath(position);

                holder1.txtFileName.setGravity(Gravity.LEFT);
                holder1.txtFileName.setText(at.getName());

                holder1.txtFileSize.setText("" + AndroidUtils.humanReadableByteCount(at.getSize(), true));
                holder1.txtFileInfo.setVisibility(View.GONE);
                holder1.txtFileSize.setGravity(Gravity.LEFT);
                File file = new File(holder1.filePath);

                if (file.exists()) {
                    holder1.messageProgress.setVisibility(View.GONE);
                    holder1.imvPicFile.setText(getString(R.string.voice_icon));

                    try {

                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        Uri uri = Uri.fromFile(file);
                        mediaMetadataRetriever.setDataSource(context, uri);
                        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                        if (artist == null) {
                            artist = context.getString(R.string.unknown_artist);
                        }
                        holder1.txtFileInfo.setText(artist);

                    } catch (Exception e) {
                        holder1.txtFileInfo.setVisibility(View.GONE);
                    }
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    holder1.messageProgress.setVisibility(View.VISIBLE);
                    holder1.imvPicFile.setText("");

                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playAudio(position, holder);
        }

        private void playAudio(int position, RecyclerView.ViewHolder holder) {

            VoiceAdapter.ViewHolder vh = (VoiceAdapter.ViewHolder) holder;

            String name = mList.get(position).item.getAttachment().getName();

            MusicPlayer.startPlayer(name, vh.filePath, name, roomId, true, mList.get(position).messageId + "");
        }

        public class ViewHolder extends mHolder {
            public TextView imvPicFile;
            public TextView txtFileName;
            public TextView txtFileSize;
            public TextView txtFileInfo;
            public String filePath;

            public ViewHolder(View view) {
                super(view);

                imvPicFile = itemView.findViewById(R.id.smslf_imv_icon_file);

                txtFileName = itemView.findViewById(R.id.smslf_txt_file_name);
                txtFileSize = itemView.findViewById(R.id.smslf_txt_file_size);
                txtFileInfo = itemView.findViewById(R.id.smslf_txt_file_info);
            }
        }
    }

    //****************************************************

    public class GifAdapter extends mAdapter {

        public GifAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_gif, null);
                viewHolder = new GifAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {

                final GifAdapter.ViewHolder holder1 = (GifAdapter.ViewHolder) holder;

                holder1.gifView.setTag(mList.get(position).messageId);

                RealmAttachment at = mList.get(position).item.getForwardMessage() != null ? mList.get(position).item.getForwardMessage().getAttachment() : mList.get(position).item.getAttachment();

                holder1.filePath = getFilePath(position);

                File file = new File(holder1.filePath);
                if (file.exists()) {
                    holder1.gifView.setImageURI(Uri.fromFile(file));

                    holder1.gifDrawable = (GifDrawable) holder1.gifView.getDrawable();

                    holder1.messageProgress.withDrawable(R.mipmap.photogif, true);
                    holder1.messageProgress.setVisibility(View.GONE);
                    holder1.messageProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (holder1.gifDrawable != null) {
                                holder1.gifDrawable.start();
                                holder1.messageProgress.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    holder1.messageProgress.setVisibility(View.VISIBLE);

                    holder1.tempFilePath = getThumpnailPath(position);

                    File filethumpnail = new File(holder1.tempFilePath);

                    if (filethumpnail.exists()) {

                        if (holder1.gifView.getTag() != null && holder1.gifView.getTag().equals(mList.get(position).messageId)) {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(holder1.tempFilePath), holder1.gifView);
                        }
                    } else {
                        DownloadObject fileObject = DownloadObject.createForThumb(mList.get(position).item, false);
                        if (fileObject != null) {
                            getDownloader().download(fileObject, ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, arg -> {
                                if (canUpdateAfterDownload) {
                                    switch (arg.status) {
                                        case LOADING:
                                        case SUCCESS:
                                            if (arg.data == null)
                                                return;

                                            if (arg.data.getProgress() == 100) {
                                                if (holder1.gifView.getTag() != null && holder1.gifView.getTag().equals(mList.get(position).messageId)) {
                                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(arg.data.getFilePath()), holder1.gifView);
                                                }
                                            }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            playAndPusGif(position, holder);
        }

        private void playAndPusGif(int position, RecyclerView.ViewHolder holder) {

            final GifAdapter.ViewHolder vh = (GifAdapter.ViewHolder) holder;

            GifDrawable gifDrawable = vh.gifDrawable;
            if (gifDrawable != null) {
                if (gifDrawable.isPlaying()) {
                    gifDrawable.pause();
                    vh.messageProgress.setVisibility(View.VISIBLE);
                } else {
                    gifDrawable.start();
                    vh.messageProgress.setVisibility(View.GONE);
                }
            } else {
                File file = new File(vh.filePath);
                if (file.exists()) {
                    vh.gifView.setImageURI(Uri.fromFile(file));
                    vh.gifDrawable = (GifDrawable) vh.gifView.getDrawable();
                    vh.messageProgress.withDrawable(R.drawable.ic_play, true);

                    vh.messageProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (vh.gifDrawable != null) {
                                vh.gifDrawable.start();
                                vh.messageProgress.setVisibility(View.GONE);
                            }
                        }
                    });

                    vh.gifDrawable.start();
                    vh.messageProgress.setVisibility(View.GONE);
                }
            }
        }

        public class ViewHolder extends mHolder {

            public String tempFilePath;
            public String filePath;
            GifImageView gifView;
            GifDrawable gifDrawable;

            public ViewHolder(View view) {
                super(view);

                gifView = itemView.findViewById(R.id.smslg_gif_view);
            }
        }
    }

    //****************************************************

    public class FileAdapter extends mAdapter {

        public FileAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new FileAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {
                FileAdapter.ViewHolder vh = (FileAdapter.ViewHolder) holder;

                RealmAttachment at = mList.get(position).item.getAttachment();

                vh.tempFilePath = getThumpnailPath(position);
                vh.filePath = getFilePath(position);

                File file = new File(vh.filePath);
                if (file.exists()) {
                    vh.messageProgress.setVisibility(View.GONE);
                    vh.iconPicFile.setText(getString(R.string.attach_icon));
                } else {
                    needDownloadList.put(mList.get(position).messageId, true);
                    vh.messageProgress.setVisibility(View.VISIBLE);
                    vh.iconPicFile.setText("");
                }

                vh.txtFileSize.setVisibility(View.INVISIBLE);

                vh.txtFileName.setText(at.getName());
                vh.txtFileInfo.setText(AndroidUtils.humanReadableByteCount(at.getSize(), true));

                //do not change else , cause of layout that not support language change direction
                if (!G.isAppRtl) {
                    vh.txtFileName.setGravity(Gravity.LEFT);
                } else {
                    vh.txtFileName.setGravity(Gravity.RIGHT);
                }

                /*File fileTemp = new File(vh.tempFilePath);

                if (fileTemp.exists()) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(vh.tempFilePath), vh.imvPicFile);
                } else {
                    Bitmap bitmap = HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource(mList.get(position).item.getAttachment().getName()));
                    if (bitmap != null) vh.imvPicFile.setImageBitmap(bitmap);
                }*/
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            openFile(position, holder);
        }

        private void openFile(int position, RecyclerView.ViewHolder holder) {

            FileAdapter.ViewHolder vh = (FileAdapter.ViewHolder) holder;

            Intent intent = HelperMimeType.appropriateProgram(vh.filePath);
            if (intent != null) {
                try {
                    context.startActivity(intent);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(context, R.string.can_not_open_file, Toast.LENGTH_SHORT).show();
            }
        }

        public class ViewHolder extends mHolder {
            public TextView iconPicFile;
            public String tempFilePath;
            public String filePath;

            public TextView txtFileName;
            public TextView txtFileInfo;
            public TextView txtFileSize;

            public ViewHolder(View view) {
                super(view);

                iconPicFile = itemView.findViewById(R.id.smslf_imv_icon_file);

                txtFileName = itemView.findViewById(R.id.smslf_txt_file_name);
                txtFileInfo = itemView.findViewById(R.id.smslf_txt_file_info);
                txtFileSize = itemView.findViewById(R.id.smslf_txt_file_size);
            }
        }
    }

    //****************************************************

    public class LinkAdapter extends mAdapter {

        public LinkAdapter(Context context, ArrayList<StructShearedMedia> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            RecyclerView.ViewHolder viewHolder = null;

            if (position == 0) {
                View view = setLayoutHeaderTime(viewGroup);
                viewHolder = new ViewHolderTime(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_media_layout_link, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new LinkAdapter.ViewHolder(view);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            if (holder.getItemViewType() == 1) {
                LinkAdapter.ViewHolder vh = (LinkAdapter.ViewHolder) holder;
                vh.bind(mList.get(position).item.getMessage());
            }
        }

        @Override
        void openSelectedItem(int position, RecyclerView.ViewHolder holder) {
            /*String link = ((ViewHolder) holder).rawLink;
            if (getActivity() == null || link == null) return;
            if (HelperUrl.isTextEmail(link)){
                HelperUrl.openEmail(getActivity() , link);
            }else {
                HelperUrl.openWebBrowser(getActivity() , link);
            }*/
        }

        public class ViewHolder extends mHolder {
            private TextView tvMessage;
            private LinearLayout lytLinks;
            String rawLink = "";

            public ViewHolder(View view) {
                super(view);

                tvMessage = itemView.findViewById(R.id.tvMessage);
                lytLinks = itemView.findViewById(R.id.lytLinks);
            }

            public void bind(String message) {

                tvMessage.setText(EmojiManager.getInstance().replaceEmoji(message, tvMessage.getPaint().getFontMetricsInt()));
                Linkify.addLinks(tvMessage, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);

                String[] links = getUrlsFromText(tvMessage);
                if (links.length != 0) {

                    int txtColor = new Theme().getLinkColor(tvMessage.getContext());
                    lytLinks.removeAllViews();
                    for (String link : links) {

                        if (message.contains(link)) {
                            tvMessage.setText(message.replace(link, "").trim());
                        } else if (message.contains(link.replace("http://", ""))) {
                            tvMessage.setText(message.replace(link.replace("http://", ""), "").trim());
                        } else if (message.contains(link.replace("mailto:", ""))) {
                            tvMessage.setText(message.replace(link.replace("mailto:", ""), "").trim());
                        }

                        if (link.startsWith("mailto:")) {
                            link = link.replace("mailto:", "");
                        }

                        rawLink = link;

                        TextView tvLink = new TextView(tvMessage.getContext());
                        tvLink.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvLink.setSingleLine(true);
                        tvLink.setLinkTextColor(txtColor);
                        tvLink.setTextColor(txtColor);
                        Utils.setTextSize(tvLink, R.dimen.standardTextSize);
                        tvLink.setText(link);
                        lytLinks.addView(tvLink);
                        enableLinkOperation(tvLink);

                    }

                }
                messageProgress.setVisibility(View.GONE);
            }

            private void enableLinkOperation(TextView txt) {

                if (getActivity() != null) {
                    BetterLinkMovementMethod
                            .linkify(Linkify.ALL, txt)
                            .setOnLinkClickListener((tv, url) -> {
                                if (HelperUrl.isTextEmail(url.replace("mailto:", ""))) {
                                    HelperUrl.openEmail(getActivity(), url.replace("mailto:", ""));
                                } else {
                                    HelperUrl.openWebBrowser(getActivity(), url);
                                }
                                return true;
                            })
                            .setOnLinkLongClickListener((tv, url) -> {
                                if (HelperUrl.isTextLink(url)) {
                                    G.isLinkClicked = true;
                                    HelperUrl.openLinkDialog(getActivity(), url.replace("mailto:", ""));
                                }
                                return true;
                            });
                }
            }

            private String[] getUrlsFromText(TextView textView) {
                URLSpan[] spans = textView.getUrls();
                String[] results = new String[spans.length];
                for (int i = 0; i < spans.length; i++) {
                    results[i] = spans[i].getURL();
                }
                return results;
            }

        }
    }

    public interface GoToPositionFromShardMedia {
        void goToPosition(Long aLong);
    }

    private class SharedButtons {

        private TextView button;
        private int id;

        public SharedButtons(TextView button, int id) {
            this.button = button;
            this.id = id;
        }

        public TextView getButton() {
            return button;
        }

        public void setButton(TextView button) {
            this.button = button;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

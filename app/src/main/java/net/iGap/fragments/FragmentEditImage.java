package net.iGap.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hanks.library.AnimateCheckBox;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.filterImage.BitmapUtils;
import net.iGap.fragments.filterImage.FragmentFilterImage;
import net.iGap.fragments.filterImage.FragmentPaintImage;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.EmojiView;
import net.iGap.libs.emojiKeyboard.KeyboardView;
import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.customView.EventEditText;
import net.iGap.module.structs.StructBottomSheet;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

public class FragmentEditImage extends BaseFragment implements NotifyFrameLayout.Listener {

    private final static String PATH = "PATH";
    private final static String ISCHAT = "ISCHAT";
    private final static String ISNICKNAMEPAGE = "ISNICKNAMEPAGE";
    private final static String SELECT_POSITION = "SLECT_POSITION";
    private int selectPosition = 0;
    private ViewPager viewPager;
    private AdapterViewPager mAdapter;
    private TextView txtEditImage;
    public static UpdateImage updateImage;
    private EventEditText edtChat;
    private TextView paintTv;
    private TextView iconOk;
    private ViewGroup layoutCaption;
    private MaterialDesignTextView channelOrGroupProfileSetTv;
    private MaterialDesignTextView imvSendButton;
    private ViewGroup rootSend;
    private MaterialDesignTextView imvSmileButton;
    private String SAMPLE_CROPPED_IMAGE_NAME;
    private boolean isChatPage = true;
    private boolean isMultiItem = true;
    private boolean isNicknamePage = false;
    public static CompleteEditImage completeEditImage;
    private int num = 0;
    private TextView txtCountImage;
    private AnimateCheckBox checkBox;
    public static HashMap<String, StructBottomSheet> textImageList = new HashMap<>();
    public static ArrayList<StructBottomSheet> itemGalleryList = new ArrayList<StructBottomSheet>();
    private OnImageEdited onProfileImageEdited;
    private boolean isReOpenChatAttachment = true;
    private GalleryListener galleryListener;

    private NotifyFrameLayout rootView;
    private FrameLayout keyboardContainer;
    private EmojiView emojiView;

    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int emojiPadding;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private AttachFile attachFile = new AttachFile(G.fragmentActivity);

    private SharedPreferences emojiSharedPreferences;
    private String finalImagePath;

    private Boolean isTheFirstFocus;

    public void setOnProfileImageEdited(OnImageEdited onProfileImageEdited) {
        this.onProfileImageEdited = onProfileImageEdited;
    }

    private FragmentEditImage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = new NotifyFrameLayout(inflater.getContext()) {
            @Override
            public boolean dispatchKeyEventPreIme(KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (isKeyboardVisible()) {
                        showPopup(-1);
                        return true;
                    }
                    return false;
                }
                return super.dispatchKeyEventPreIme(event);
            }
        };

        rootView.setListener(this);
        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);
        keyboardContainer = view.findViewById(R.id.fl_chat_keyboardContainer);
        paintTv = view.findViewById(R.id.txtPaintImage);
        rootView.addView(view, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            emojiSharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.EMOJI, MODE_PRIVATE);
        }

    }

    public static FragmentEditImage newInstance(String path, boolean isChatPage, boolean isNicknamePage, int selectPosition) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        args.putBoolean(ISCHAT, isChatPage);
        args.putBoolean(ISNICKNAMEPAGE, isNicknamePage);
        args.putInt(SELECT_POSITION, selectPosition);
        FragmentEditImage fragment = new FragmentEditImage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NotNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        if (itemGalleryList == null || itemGalleryList.size() == 0) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentEditImage.this).commit();
            }
            return;
        }

        if (isChatPage) {

            layoutCaption.setVisibility(VISIBLE);
            imvSendButton.setVisibility(VISIBLE);
            channelOrGroupProfileSetTv.setVisibility(View.GONE);
            checkBox.setVisibility(VISIBLE);
            if (textImageList.size() > 0) {
                txtCountImage.setVisibility(VISIBLE);
                txtCountImage.setText(textImageList.size() + "");
            } else {
                txtCountImage.setVisibility(View.GONE);
            }
            if (itemGalleryList != null && itemGalleryList.size() == 1) {
                checkBox.setVisibility(View.GONE);
                txtCountImage.setVisibility(View.GONE);
                isMultiItem = false;

            }
        } else {
            channelOrGroupProfileSetTv.setVisibility(VISIBLE);
            layoutCaption.setVisibility(View.GONE);
            imvSendButton.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
            txtCountImage.setVisibility(View.GONE);
            isMultiItem = false;
        }

        edtChat.setListener(event -> {
            if (!isPopupShowing() && event.getAction() == MotionEvent.ACTION_DOWN) {
                showPopup(KeyboardView.MODE_KEYBOARD);
            }
        });

        updateImage = pathImageFilter -> {
            this.finalImagePath = pathImageFilter;
            serCropAndFilterImage(pathImageFilter);
            G.handler.post(() -> mAdapter.notifyDataSetChanged());
        };

        setViewPager();
        setCheckBoxItem();
        messageBox(view);


        view.findViewById(R.id.pu_ripple_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                if (getActivity() != null) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.this).remove();
                }
                if (G.openBottomSheetItem != null && isChatPage && isReOpenChatAttachment)
                    G.openBottomSheetItem.openBottomSheet(false);
            }
        });

        view.findViewById(R.id.pu_txt_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToCropPage(v);
            }

        });


        channelOrGroupProfileSetTv.setOnClickListener(v -> {

            if (onProfileImageEdited != null) {
                onProfileImageEdited.profileImageAdd(itemGalleryList.get(0).getPath());
                return;
            }

            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.this).remove();
            }

            closeKeyboard();

        });

        imvSendButton.setOnClickListener(v -> {
            if (finalImagePath != null) {
                finalImagePath = BitmapUtils.insertImage(getActivity().getContentResolver(), BitmapFactory.decodeFile(finalImagePath), System.currentTimeMillis() + "_profile.jpg", null);
            }
            if (iconOk.isShown()) {
                iconOk.performClick();
            }

            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.this).remove();
            }
            if (galleryListener != null) galleryListener.onImageSent();

            if (textImageList.size() == 0) {
                setValueCheckBox(viewPager.getCurrentItem());
            }

            completeEditImage.result("", edtChat.getText().toString(), textImageList);
            closeKeyboard();

        });
    }

    private void messageBox(final View view) {
        if (textImageList.containsKey(itemGalleryList.get(selectPosition).path)) {
            edtChat.setText(EmojiManager.getInstance().replaceEmoji(textImageList.get(itemGalleryList.get(selectPosition).path).getText(), edtChat.getPaint().getFontMetricsInt()));
        } else {
            edtChat.setText("");
        }

        txtEditImage.setOnClickListener(v -> {
            hideKeyboard();
            if (getActivity() != null && itemGalleryList.size() > 0) {
                if (!isNicknamePage) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentFilterImage.newInstance(itemGalleryList.get(viewPager.getCurrentItem()).path)).setReplace(false).load();
                } else {
                    FragmentFilterImage fragment = FragmentFilterImage.newInstance(itemGalleryList.get(viewPager.getCurrentItem()).path);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.registrationFrame, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                }
            }
        });
        paintTv.setOnClickListener(v -> {
            hideKeyboard();
            if (getActivity() != null && itemGalleryList.size() > 0) {
                String path = itemGalleryList.get(viewPager.getCurrentItem()).path;
                if (path == null || path.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isNicknamePage) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPaintImage.newInstance(path)).setReplace(false).load();
                } else {
                    FragmentPaintImage fragment = FragmentPaintImage.newInstance(path);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.registrationFrame, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                }
            }
        });
        iconOk.setOnClickListener(v -> {

            String path = itemGalleryList.get(viewPager.getCurrentItem()).getPath();
            String message = edtChat.getText().toString();

            itemGalleryList.get(viewPager.getCurrentItem()).setSelected(false);
            checkBox.setChecked(true);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.setting_items_value_color));

            StructBottomSheet item = new StructBottomSheet();
            item.setPath(path);
            item.setText(message);
            item.setId(itemGalleryList.get(viewPager.getCurrentItem()).getId());

            textImageList.put(path, item);
            if (textImageList.size() > 0 && isMultiItem) {
                txtCountImage.setVisibility(VISIBLE);
                txtCountImage.setText(textImageList.size() + "");
            } else {
                txtCountImage.setVisibility(View.GONE);
            }

            v.setVisibility(View.GONE);

            showPopup(-1);

        });
        isTheFirstFocus = true;
        edtChat.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (!isTheFirstFocus) {
                    changeEmojiButtonImageResource(R.string.icon_emoji_smile);
                    onEmojiClicked();
                } else {
                    changeEmojiButtonImageResource(R.string.icon_emoji_smile);

                }

            } else {
                isTheFirstFocus = false;
                changeEmojiButtonImageResource(R.string.icon_keyboard);
            }
        });
        edtChat.requestFocus();

        edtChat.setOnClickListener(v -> {
            if (isPopupShowing()) {
                imvSmileButton.performClick();
            }
        });

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String oldPath = "";
                if (textImageList.containsKey(itemGalleryList.get(viewPager.getCurrentItem()).path)) {
                    oldPath = textImageList.get(itemGalleryList.get(viewPager.getCurrentItem()).path).getText();
                }
                if (!oldPath.equals(s.toString())) {
                    iconOk.setVisibility(VISIBLE);
                } else {
                    iconOk.setVisibility(View.GONE);
                }
            }
        });

        imvSmileButton.setOnClickListener(v -> onEmojiClicked());
    }


    private void onEmojiClicked() {
        if (!isPopupShowing()) {
            showPopup(KeyboardView.MODE_EMOJI);
        } else {
            showPopup(KeyboardView.MODE_KEYBOARD);
        }
    }

    private void openKeyboardInternal() {
        edtChat.requestFocus();
        AndroidUtils.showKeyboard(edtChat);
    }

    public void closeKeyboard() {
        edtChat.clearFocus();
        AndroidUtils.hideKeyboard(edtChat);
        changeEmojiButtonImageResource(R.string.icon_emoji_smile);
        isTheFirstFocus = true;

    }

    private boolean isKeyboardVisible() {
        return AndroidUtils.usingKeyboardInput || keyboardVisible;
    }

    private boolean isPopupShowing() {
        return emojiView != null && emojiView.getVisibility() == VISIBLE;
    }

    private void onWindowSizeChanged() {
        int size = rootView.getHeight();
        if (!keyboardVisible) {
            size -= emojiPadding;
        }
    }

    private void showPopup(int show) {

        if (show == KeyboardView.MODE_EMOJI) {
            if (emojiView == null) {
                createEmojiView();
            }

            edtChat.requestFocus();
            emojiView.setVisibility(VISIBLE);
            keyboardContainer.setVisibility(VISIBLE);

            if (keyboardHeight <= 0) {
                keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(300));
            }
            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(300));
            }

            int currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardHeight;

            ViewGroup.LayoutParams layoutParams = keyboardContainer.getLayoutParams();
            layoutParams.width = AndroidUtils.displaySize.x;
            layoutParams.height = currentHeight;
            keyboardContainer.setLayoutParams(layoutParams);

            if (keyboardVisible) {
                closeKeyboard();
            }

            if (rootView != null) {
                emojiPadding = currentHeight;
                rootView.requestLayout();
                changeEmojiButtonImageResource(R.string.icon_keyboard);
                rootSend.setVisibility(View.GONE);
                onWindowSizeChanged();
            }
        } else if (show == KeyboardView.MODE_KEYBOARD) {
            changeEmojiButtonImageResource(R.string.icon_emoji_smile);

            keyboardContainer.setVisibility(VISIBLE);

            if (keyboardHeight <= 0) {
                keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(300));
            }

            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(300));
            }

            ViewGroup.LayoutParams layoutParams = keyboardContainer.getLayoutParams();
            layoutParams.width = AndroidUtils.displaySize.x;
            layoutParams.height = keyboardHeight;
            keyboardContainer.setLayoutParams(layoutParams);

            openKeyboardInternal();

            if (emojiView != null) {
                emojiView.setVisibility(View.GONE);
            }

            rootSend.setVisibility(View.GONE);

            if (rootView != null) {
                rootView.requestLayout();
                onWindowSizeChanged();
            }

        } else {
            rootSend.setVisibility(VISIBLE);
            keyboardContainer.setVisibility(View.GONE);
            if (emojiView != null)
                emojiView.setVisibility(View.GONE);
            closeKeyboard();
        }

        if (show == KeyboardView.MODE_KEYBOARD) {
            emojiPadding = 0;
        }
    }

    private void createEmojiView() {
        if (emojiView == null) {
            emojiView = new EmojiView(rootView.getContext(), false, true);
            emojiView.setVisibility(View.GONE);
            emojiView.setContentView(EmojiView.EMOJI);
            emojiView.setListener(new EmojiView.Listener() {
                @Override
                public void onBackSpace() {
                    if (edtChat.length() == 0) {
                        return;
                    }
                    edtChat.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }

                @Override
                public void onStickerClick(StructIGSticker structIGSticker) {

                }

                @Override
                public void onStickerSettingClick() {

                }

                @Override
                public void onAddStickerClicked() {

                }

                @Override
                public void onEmojiSelected(String unicode) {
                    int i = edtChat.getSelectionEnd();

                    if (i < 0) i = 0;

                    try {
                        CharSequence sequence = EmojiManager.getInstance().replaceEmoji(unicode, edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false);
                        if (edtChat.getText() != null)
                            edtChat.setText(edtChat.getText().insert(i, sequence));
                        int j = i + sequence.length();
                        edtChat.setSelection(j, j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        keyboardContainer.addView(emojiView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));
    }

    private void setCheckBoxItem() {
        checkBox.setOnClickListener(v -> {
            if (isMultiItem) setValueCheckBox(viewPager.getCurrentItem());
        });

        if (itemGalleryList.get(viewPager.getCurrentItem()).isSelected) {
            checkBox.setChecked(false);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
        } else {
            checkBox.setChecked(true);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.setting_items_value_color));
        }

    }

    private void setViewPager() {

        mAdapter = new AdapterViewPager(itemGalleryList);
        viewPager.setAdapter(mAdapter);

        viewPager.setCurrentItem(selectPosition);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (itemGalleryList.get(position).isSelected) {
                    checkBox.setChecked(false);
                    checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                } else {
                    checkBox.setChecked(true);
                    checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.setting_items_value_color));
                }

                if (textImageList.containsKey(itemGalleryList.get(position).path)) {
                    edtChat.setText(EmojiManager.getInstance().replaceEmoji(textImageList.get(itemGalleryList.get(position).path).getText(), edtChat.getPaint().getFontMetricsInt()));
                } else {
                    edtChat.setText("");
                }
                iconOk.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setIsReOpenChatAttachment(boolean enable) {
        this.isReOpenChatAttachment = enable;
    }

    public void setGalleryListener(GalleryListener listener) {
        this.galleryListener = listener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path;
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            path = AttachFile.getFilePathFromUri(resultUri);

            serCropAndFilterImage(path);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                path = result.getUri().getPath();
                serCropAndFilterImage(path);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
            }
        }

    }

    @Override
    public void onSizeChanged(int keyboardSize, boolean land) {
        if (keyboardSize > LayoutCreator.dp(50) && keyboardVisible /*&& !AndroidUtilities.isInMultiwindow && !forceFloatingEmoji*/) {
            if (land) {
                keyboardHeightLand = keyboardSize;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, keyboardHeightLand).apply();
            } else {
                keyboardHeight = keyboardSize;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, keyboardHeight).apply();
            }
        }

        if (isPopupShowing()) {
            int newHeight;
            if (land) {
                newHeight = keyboardHeightLand;
            } else {
                newHeight = keyboardHeight;
            }

            ViewGroup.LayoutParams layoutParams = keyboardContainer.getLayoutParams();
            if (layoutParams.width != AndroidUtils.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtils.displaySize.x;
                layoutParams.height = newHeight;
                keyboardContainer.setLayoutParams(layoutParams);

                if (rootView != null) {
                    emojiPadding = layoutParams.height;
                    rootView.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }

        if (lastSizeChangeValue1 == keyboardSize && lastSizeChangeValue2 == land) {
            onWindowSizeChanged();
            return;
        }
        lastSizeChangeValue1 = keyboardSize;
        lastSizeChangeValue2 = land;

        boolean oldValue = keyboardVisible;
        keyboardVisible = keyboardSize > 0;

        if (emojiPadding != 0 && !keyboardVisible && keyboardVisible != oldValue && !isPopupShowing()) {
            emojiPadding = 0;
            rootView.requestLayout();
        }
        onWindowSizeChanged();
    }

    public interface UpdateImage {
        void result(String path);
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    public interface CompleteEditImage {
        void result(String path, String message, HashMap<String, StructBottomSheet> textImageList);
    }

    @Override
    public boolean onBackPressed() {
        if (isPopupShowing()) {
            showPopup(-1);
            return true;
        } else
            return super.onBackPressed();
    }

    @Override
    public void onPause() {
        showPopup(-1);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//
//        if (getView() == null) {
//            return;
//        }
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && getActivity() != null) {
//                    closeKeyboard();
//                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.this).remove();
//                    if (G.openBottomSheetItem != null && isChatPage && isReOpenChatAttachment)
//                        G.openBottomSheetItem.openBottomSheet(false);
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    private class AdapterViewPager extends PagerAdapter {

        ArrayList<StructBottomSheet> itemGalleryList;

        public AdapterViewPager(ArrayList<StructBottomSheet> itemGalleryList) {
            this.itemGalleryList = itemGalleryList;
        }

        @Override
        public int getCount() {
            return itemGalleryList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(G.fragmentActivity);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.adapter_viewpager_edittext, container, false);
            final ImageView imgPlay = layout.findViewById(R.id.img_editImage);
            if (itemGalleryList.get(position).path != null) {
                new Thread(() -> {
                    String oldPath = itemGalleryList.get(position).path;
                    String finalPath = attachFile.saveGalleryPicToLocal(oldPath);
                    G.runOnUiThread(() -> {
                        //check if old path available in selected list , replace new path with that
                        if (!oldPath.equals(finalPath)) {
                            StructBottomSheet item = textImageList.get(oldPath);
                            if (item != null) {
                                textImageList.remove(oldPath);
                                textImageList.put(finalPath, item);
                            }
                        }
                        itemGalleryList.get(position).path = finalPath;
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(finalPath), imgPlay);
                    });
                }).start();
            }

            imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isMultiItem) setValueCheckBox(position);
                }
            });
            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void setValueCheckBox(int position) {

        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
            itemGalleryList.get(position).setSelected(true);
            textImageList.remove(itemGalleryList.get(position).path);

        } else {
            checkBox.setChecked(true);
            StructBottomSheet item = new StructBottomSheet();
            item.setText(edtChat.getText().toString());
            item.setPath(itemGalleryList.get(position).path);
            item.setId(itemGalleryList.get(position).getId());
            textImageList.put(itemGalleryList.get(position).path, item);
            checkBox.setUnCheckColor(G.context.getResources().getColor(R.color.setting_items_value_color));
            itemGalleryList.get(position).setSelected(false);
        }
        if (textImageList.size() > 0 && isChatPage) {
            txtCountImage.setVisibility(VISIBLE);
            txtCountImage.setText(textImageList.size() + "");
        } else {
            txtCountImage.setVisibility(View.GONE);
        }
    }


    private void initView(View view) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            isChatPage = bundle.getBoolean(ISCHAT);
            isNicknamePage = bundle.getBoolean(ISNICKNAMEPAGE);
            selectPosition = bundle.getInt(SELECT_POSITION);
        }

        layoutCaption = view.findViewById(R.id.layout_caption);
        channelOrGroupProfileSetTv = view.findViewById(R.id.txtSet);
        imvSendButton = view.findViewById(R.id.pu_txt_sendImage);

        iconOk = view.findViewById(R.id.chl_imv_ok_message);
        rootSend = view.findViewById(R.id.pu_layout_cancel_crop);
        txtEditImage = view.findViewById(R.id.txtEditImage);
        edtChat = view.findViewById(R.id.chl_edt_chat);
        edtChat.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtChat.setInputType(edtChat.getInputType() | EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edtChat.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Config.MAX_TEXT_ATTACHMENT_LENGTH)});
        txtCountImage = view.findViewById(R.id.stfaq_txt_countImageEditText);
        viewPager = view.findViewById(R.id.viewPagerEditText);
        checkBox = view.findViewById(R.id.checkBox_editImage);
        imvSmileButton = view.findViewById(R.id.chl_imv_smile_button);
    }

    private void goToCropPage(View v) {
        AndroidUtils.closeKeyboard(v);
        if (itemGalleryList.size() == 0) {
            return;
        }
        String newPath = "file://" + itemGalleryList.get(viewPager.getCurrentItem()).path;
        if (newPath.lastIndexOf(".") <= 0) {
            return;
        }

        String fileNameWithOutExt = newPath.substring(newPath.lastIndexOf("/"));
        String extension = newPath.substring(newPath.lastIndexOf("."));
        SAMPLE_CROPPED_IMAGE_NAME = fileNameWithOutExt.substring(0, fileNameWithOutExt.lastIndexOf(".")) + num + extension;
        num++;
        Uri uri = Uri.parse(newPath);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            UCrop.Options options = new UCrop.Options();
            options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.black));
            options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.black));
            options.setCompressionQuality(80);
            options.setFreeStyleCropEnabled(true);

            UCrop.of(uri, Uri.fromFile(new File(G.DIR_IMAGES, SAMPLE_CROPPED_IMAGE_NAME)))
                    .withOptions(options)
                    .useSourceImageAspectRatio()
                    .start(G.context, FragmentEditImage.this);
        } else {
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(120, 120)
                    .setAutoZoomEnabled(false)
                    .setInitialCropWindowPaddingRatio(.08f) // padding window from all
                    .setBorderCornerLength(50)
                    .setBorderCornerOffset(0)
                    .setAllowCounterRotation(true)
                    .setBorderCornerThickness(8.0f)
                    .setShowCropOverlay(true)
                    .setAspectRatio(1, 1)
                    .setFixAspectRatio(false)
                    .setBorderCornerColor(getResources().getColor(R.color.whit_background))
                    .setBackgroundColor(getResources().getColor(R.color.ou_background_crop))
                    .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                    .start(G.fragmentActivity, FragmentEditImage.this);
        }
    }

    public static ArrayList<StructBottomSheet> insertItemList(String path, boolean isSelected) {

        if (itemGalleryList == null) {
            itemGalleryList = new ArrayList<>();
        }

        if (!HelperPermission.grantedUseStorage()) {
            return itemGalleryList;
        }
        StructBottomSheet item = new StructBottomSheet();
        item.setId(itemGalleryList.size());
        item.setPath(path);
        item.setText("");
        item.isSelected = isSelected;
        itemGalleryList.add(0, item);
        textImageList.put(path, item);

        return itemGalleryList;
    }

    public static ArrayList<StructBottomSheet> insertItemList(String path, String message, boolean isSelected) {

        if (itemGalleryList == null) {
            itemGalleryList = new ArrayList<>();
        }

        if (!HelperPermission.grantedUseStorage()) {
            return itemGalleryList;
        }
        StructBottomSheet item = new StructBottomSheet();
        item.setId(itemGalleryList.size());
        item.setPath(path);
        item.setText(message);
        item.isSelected = isSelected;
        itemGalleryList.add(0, item);
        textImageList.put(path, item);

        return itemGalleryList;
    }

    public static ArrayList<StructBottomSheet> insertItemList(String path, boolean isSelected, CompleteEditImage completeEdit) {

        if (itemGalleryList == null) {
            itemGalleryList = new ArrayList<>();
        }

        if (!HelperPermission.grantedUseStorage()) {
            return itemGalleryList;
        }
        StructBottomSheet item = new StructBottomSheet();
        item.setId(itemGalleryList.size());
        item.setPath(path);
        item.setText("");
        item.isSelected = isSelected;
        itemGalleryList.add(0, item);
        textImageList.put(path, item);

        completeEditImage = completeEdit;

        return itemGalleryList;
    }

    private void serCropAndFilterImage(String path) {

        int po = (viewPager.getCurrentItem());

        if (textImageList.containsKey(itemGalleryList.get(po).getPath())) {

            String message = textImageList.get(itemGalleryList.get(po).getPath()).getText();
            int id = textImageList.get(itemGalleryList.get(po).getPath()).getId();

            textImageList.remove(itemGalleryList.get(po).getPath());
            StructBottomSheet item = new StructBottomSheet();
            item.setPath(path);
            item.setText(message);
            item.setId(id);

            textImageList.put(path, item);
        }
        itemGalleryList.get(po).setPath(path);

        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rootView != null)
            rootView.setListener(null);

        updateImage = null;
    }

    public interface GalleryListener {
        void onImageSent();
    }

    @FunctionalInterface
    public interface OnImageEdited {
        void profileImageAdd(String path);
    }

}

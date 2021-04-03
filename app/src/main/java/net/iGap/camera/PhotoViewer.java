package net.iGap.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hanks.library.AnimateCheckBox;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.EmojiView;
import net.iGap.libs.emojiKeyboard.KeyboardView;
import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.customView.EventEditText;
import net.iGap.module.dialog.ChatAttachmentPopup;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnRotateImage;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yogesh.firzen.mukkiasevaigal.M;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

public class PhotoViewer extends BaseFragment implements NotifyFrameLayout.Listener, OnPhotoEditorListener {
    private NotifyFrameLayout rootView;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private LinearLayout toolbarPanel;
    private RippleView rippleView;
    private MaterialDesignTextView designTextView;
    private MaterialDesignTextView emoji;
    private TextView toolbarTitle;
    private TextView imageNumber;
    private MaterialDesignTextView setTextView;
    private MaterialDesignTextView iconOkTextView;
    private TextView countImageTextView;
    private AnimCheckBox animateCheckBox;
    private LinearLayout bottomLayoutPanel;
    private LinearLayout layoutCaption;
    private EventEditText captionEditText;

    private FrameLayout chatKeyBoardContainer;
    private LinearLayout cancelCropLayout;
    private boolean keyboardVisible;
    private MaterialDesignTextView cropTextView;
    private MaterialDesignTextView editTextView;
    private MaterialDesignTextView paintTextView;
    private MaterialDesignTextView addTextView;
    private MaterialDesignTextView sendTextView;
    private View emptyView;
    private AttachFile attachFile = new AttachFile(G.fragmentActivity);
    private AdapterViewPager mAdapter;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private ArrayList<StructBottomSheet> itemGalleryList;
    public HashMap<String, StructBottomSheet> textImageList = new HashMap<>();
    private static final String PATH = "path";
    private String path;
    private SharedPreferences emojiSharedPreferences;
    private EmojiView emojiView;
    private int emojiPadding;
    private ZoomLayout zoomLayout;
    private TextStickerView textStickersParentView;
    private ImageViewTouch imageViewTouch;
    private List<View> addedViews;
    private FrameLayout stickerBorder;
    private TextView textTv;

    public static PhotoViewer newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        PhotoViewer fragment = new PhotoViewer();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createView(Context context) {
        rootView = new NotifyFrameLayout(context) {
            @Override
            public boolean dispatchKeyEventPreIme(KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (isKeyboardVisible()) {
                        showPopUPView(-1);
                        return true;
                    }
                    return false;
                }
                return super.dispatchKeyEventPreIme(event);
            }
        };
        rootView.setListener(this);
        rootView.setBackgroundColor(context.getResources().getColor(R.color.black_register));
        rootView.setClickable(true);

        imageViewTouch = new ImageViewTouch(context);
        rootView.addView(imageViewTouch, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        viewPager = new ViewPager(context);
        viewPager.setVisibility(View.GONE);
        zoomLayout = new ZoomLayout(context);
        textStickersParentView = new TextStickerView(context);
        textStickersParentView.setVisibility(View.GONE);
        zoomLayout.addView(textStickersParentView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));
        rootView.addView(zoomLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        rootView.addView(viewPager, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        toolbar = new Toolbar(context);
        toolbar.setContentInsetStartWithNavigation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            rootView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            ViewCompat.setLayoutDirection(toolbar, ViewCompat.LAYOUT_DIRECTION_LTR);
            ViewCompat.setLayoutDirection(rootView, ViewCompat.LAYOUT_DIRECTION_LTR);
        }
        toolbar.setBackgroundColor(context.getResources().getColor(R.color.colorEditImageBlack));

        toolbarPanel = new LinearLayout(context);
        toolbarPanel.setOrientation(LinearLayout.HORIZONTAL);

        rippleView = new RippleView(context);
        rippleView.setCentered(true);
        rippleView.setRippleAlpha(200);
        rippleView.setRippleDuration(0);
        rippleView.setRipplePadding(5);
        toolbarPanel.addView(rippleView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        designTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        designTextView.setText(context.getString(R.string.md_close_button));
        designTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        designTextView.setGravity(Gravity.CENTER);
        rippleView.addView(designTextView, LayoutCreator.createRelative(48, LayoutCreator.MATCH_PARENT));


        toolbarTitle = new TextView(context);
        toolbarTitle.setText(context.getString(R.string.photo));
        toolbarTitle.setTextColor(context.getResources().getColor(R.color.whit_background));
        toolbarTitle.setTextSize(16);
        toolbarTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        toolbarTitle.setTypeface(toolbarTitle.getTypeface(), Typeface.BOLD);
        toolbarPanel.addView(toolbarTitle, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1F));

        imageNumber = new TextView(context);
        imageNumber.setGravity(Gravity.CENTER | Gravity.LEFT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imageNumber.setTextAppearance(android.R.attr.textAppearanceMedium);
        } else {
            imageNumber.setTextAppearance(context, android.R.attr.textAppearanceMedium);
        }
        imageNumber.setTextColor(context.getResources().getColor(R.color.white));
        imageNumber.setTextSize(18);
        toolbarPanel.addView(imageNumber, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, 1F));

        setTextView = new MaterialDesignTextView(context);
        setTextView.setGravity(Gravity.CENTER);
        setTextView.setText(context.getString(R.string.check_icon));
        setTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        setTextView.setTextSize(22);
        setTextView.setVisibility(View.GONE);
        toolbarPanel.addView(setTextView, LayoutCreator.createLinear(52, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));

        countImageTextView = new TextView(context);
        countImageTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        countImageTextView.setText(context.getString(R.string.photo));
        countImageTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        countImageTextView.setTextSize(22);
        countImageTextView.setTypeface(countImageTextView.getTypeface(), Typeface.BOLD);
        toolbarPanel.addView(countImageTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, 0, 0, 1, 0));

        animateCheckBox = new AnimCheckBox(context);
        animateCheckBox.setBackground(context.getResources().getDrawable(R.drawable.background_check));
        animateCheckBox.setCircleColor(R.attr.iGapButtonColor);
        animateCheckBox.setLineColor(context.getResources().getColor(R.color.whit_background));
        animateCheckBox.setAnimDuration(100);
        animateCheckBox.setCorrectWidth(1);
        animateCheckBox.setUnCheckColor(context.getResources().getColor(R.color.background_checkbox_bottomSheet));
        toolbarPanel.addView(animateCheckBox, LayoutCreator.createLinear(32, 32, Gravity.CENTER | Gravity.END | Gravity.RIGHT));


        toolbar.addView(toolbarPanel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(toolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 60, Gravity.TOP));

        bottomLayoutPanel = new LinearLayout(context);
        bottomLayoutPanel.setOrientation(LinearLayout.VERTICAL);
        bottomLayoutPanel.setBackgroundColor(context.getResources().getColor(R.color.colorEditImageBlack));

        layoutCaption = new LinearLayout(context);
        layoutCaption.setOrientation(LinearLayout.HORIZONTAL);
        layoutCaption.setMinimumHeight(48);
        layoutCaption.setPadding(4, 0, 4, 0);
        bottomLayoutPanel.addView(layoutCaption, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        emoji = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        emoji.setGravity(Gravity.CENTER);
        emoji.setPadding(8, 0, 8, 8);
        emoji.setText(context.getString(R.string.md_emoticon_with_happy_face));
        emoji.setTextColor(context.getResources().getColor(R.color.white));
        emoji.setTextSize(26);
        layoutCaption.addView(emoji, LayoutCreator.createLinear(52, 52, Gravity.CENTER));

        captionEditText = new EventEditText(context);
        captionEditText.setGravity(Gravity.BOTTOM);
        captionEditText.setHint(context.getString(R.string.type_message));
        captionEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        captionEditText.setMaxLines(4);
        captionEditText.setPadding(10, 0, 10, 8);
        captionEditText.setTextColor(context.getResources().getColor(R.color.white));
        captionEditText.setHintTextColor(context.getResources().getColor(R.color.light_gray));
        captionEditText.setTextSize(14);
        captionEditText.setBackground(null);
        layoutCaption.addView(captionEditText, LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, 1, Gravity.CENTER_VERTICAL));

        iconOkTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        iconOkTextView.setGravity(Gravity.BOTTOM);
        iconOkTextView.setPadding(8, 0, 8, 8);
        iconOkTextView.setText(context.getString(R.string.check_icon));
        iconOkTextView.setTextColor(context.getResources().getColor(R.color.white));
        iconOkTextView.setTextSize(26);
        iconOkTextView.setVisibility(View.GONE);
        layoutCaption.addView(iconOkTextView, LayoutCreator.createLinear(52, 52, Gravity.BOTTOM));

        chatKeyBoardContainer = new FrameLayout(context);

        bottomLayoutPanel.addView(chatKeyBoardContainer, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        cancelCropLayout = new LinearLayout(context);
        cancelCropLayout.setOrientation(LinearLayout.HORIZONTAL);
        cropTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        cropTextView.setGravity(Gravity.CENTER);
        cropTextView.setText(context.getString(R.string.md_crop_button));
        cropTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(cropTextView, LayoutCreator.createLinear(52, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));

        editTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        editTextView.setGravity(Gravity.CENTER);
        editTextView.setText(context.getString(R.string.md_igap_tune));
        editTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(editTextView, LayoutCreator.createLinear(52, 52, Gravity.RIGHT));

        paintTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        paintTextView.setGravity(Gravity.CENTER);
        paintTextView.setText(context.getString(R.string.md_igap_paint));
        paintTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(paintTextView, LayoutCreator.createLinear(52, 52, Gravity.RIGHT));

        addTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        addTextView.setGravity(Gravity.CENTER);
        addTextView.setBackground(context.getResources().getDrawable(R.drawable.text));
        addTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(addTextView, LayoutCreator.createLinear(52, 52, Gravity.RIGHT));


        emptyView = new View(context);
        cancelCropLayout.addView(emptyView, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1F));

        sendTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        sendTextView.setGravity(Gravity.CENTER);
        sendTextView.setText(context.getString(R.string.md_send_button));
        sendTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        sendTextView.setVisibility(View.GONE);
        cancelCropLayout.addView(sendTextView, LayoutCreator.createLinear(52, 52, Gravity.RIGHT));

        bottomLayoutPanel.addView(cancelCropLayout, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        rootView.addView(bottomLayoutPanel, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addedViews = new ArrayList<>();
        path = getArguments().getString(PATH);

        if (!HelperPermission.grantedUseStorage()) {
            try {
                HelperPermission.getStoragePermision(getContext(), new OnGetPermission() {

                    @Override
                    public void Allow() throws IOException {
                        openPhotoForEdit(path, null, false);
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (getActivity() != null) {
            emojiSharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.EMOJI, MODE_PRIVATE);
        }
        openPhotoForEdit(path, null, false);
        captionEditText.setListener(new EventEditText.Listener() {
            @Override
            public void onInternalTouchEvent(MotionEvent event) {
                if (!isPopupShowing() && event.getAction() == MotionEvent.ACTION_DOWN) {
                    showPopUPView(KeyboardView.MODE_KEYBOARD);
                }
            }
        });
        addTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onAddTextShow();
                TextEditorDialogFragment textEditorDialogFragment =
                        TextEditorDialogFragment.newInstance(getActivity());
                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                    FrameLayout rootView = new FrameLayout(getContext());
                    stickerBorder = new FrameLayout(getContext());
//                    stickerBorder.setBackground(getResources().getDrawable(R.drawable.background_border));
                    rootView.addView(stickerBorder, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 8, 8, 8, 8));

                    textTv = new TextView(getContext());
                    textTv.setTextColor(colorCode);
                    textTv.setText(inputText);
                    stickerBorder.addView(textTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
                    textTv.setGravity(Gravity.CENTER);
                    textTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            14);


                    MultiTouchListener multiTouchListener = new MultiTouchListener(
                            null,
                            textStickersParentView,
                            imageViewTouch,
                            PhotoViewer.this, getContext());

                    multiTouchListener.setOnGestureControl(new OnGestureControl() {
                        boolean isDownAlready = false;

                        @Override
                        public void onClick() {
                            boolean isBackgroundVisible = stickerBorder.getTag() != null && (boolean) stickerBorder.getTag();
                            String textInput = textTv.getText().toString();
                            int currentTextColor = textTv.getCurrentTextColor();
                            showTextEditDialog(rootView, textInput, currentTextColor);
                        }

                        @Override
                        public void onDown() {
                            boolean isBackgroundVisible = stickerBorder.getTag() != null && (boolean) stickerBorder.getTag();
                            if (!isBackgroundVisible) {
                                // stickerBorder.setBackgroundResource(R.drawable.background_border);
                                stickerBorder.setTag(true);
                                updateViewsBordersVisibilityExcept(rootView);
                                isDownAlready = true;
                            } else {
                                isDownAlready = false;
                            }
                        }

                        @Override
                        public void onLongClick() {
                        }
                    });
                    rootView.setOnTouchListener(multiTouchListener);

                    addViewToParent(rootView);
                });
            }
        });


        rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                if (getActivity() != null) {
                    new HelperFragment(getParentFragmentManager(), PhotoViewer.this).remove();
                }
            }
        });

    }

    private void onAddTextShow() {
        imageViewTouch.setVisibility(View.GONE);
        textStickersParentView.updateImageBitmap(BitmapFactory.decodeFile(this.path));
        textStickersParentView.setVisibility(View.VISIBLE);

        autoScaleImageToFitBounds();
    }

    private void showTextEditDialog(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.newInstance(getActivity(), text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode1) -> editText(rootView, inputText, colorCode1));
    }

    private void editText(View view, String inputText, int colorCode) {
        if (textTv != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            textTv.setText(inputText);
            textTv.setTextColor(colorCode);
            textStickersParentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    private void autoScaleImageToFitBounds() {
        textStickersParentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textStickersParentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                scaleImage();
            }
        });
    }

    private void scaleImage() {
        final float zoomLayoutWidth = zoomLayout.getWidth();
        final float zoomLayoutHeight = zoomLayout.getHeight();

        final float imageViewWidth = textStickersParentView.getWidth();
        final float imageViewHeight = textStickersParentView.getHeight();

        // To avoid divideByZero exception
        if (imageViewHeight != 0 && imageViewWidth != 0 && zoomLayoutHeight != 0 && zoomLayoutWidth != 0) {
            final float offsetFactorX = zoomLayoutWidth / imageViewWidth;
            final float offsetFactorY = zoomLayoutHeight / imageViewHeight;

            float scaleFactor = Math.min(offsetFactorX, offsetFactorY);
            zoomLayout.setChildScale(scaleFactor);
        }
    }

    private void addViewToParent(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textStickersParentView.setVisibility(View.VISIBLE);
        textStickersParentView.addView(view, params);
        addedViews.add(view);
        updateViewsBordersVisibilityExcept(view);
    }

    private void updateViewsBordersVisibilityExcept(@Nullable View keepView) {
        for (View view : addedViews) {
            if (view != keepView) {
                stickerBorder.setBackgroundResource(0);
                stickerBorder.setTag(false);
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

            ViewGroup.LayoutParams layoutParams = chatKeyBoardContainer.getLayoutParams();
            if (layoutParams.width != AndroidUtils.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtils.displaySize.x;
                layoutParams.height = newHeight;
                chatKeyBoardContainer.setLayoutParams(layoutParams);

                if (rootView != null) {
                    rootView.requestLayout();
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = keyboardSize > 0;
        if (keyboardVisible && isPopupShowing()) {
            showPopUPView(-1);
        }
        if (emojiPadding != 0 && !keyboardVisible && keyboardVisible != oldValue && !isPopupShowing()) {
            emojiPadding = 0;
            rootView.requestLayout();
        }
    }

    @Override
    public void onAddViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener() {

    }

    @Override
    public void onStopViewChangeListener() {

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


            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup containe, int position, Object object) {
            //   container.removeView((View) object);
        }
    }


    public void openPhotoForEdit(String path, String message, boolean isSelected) {

        this.path = path;


        ImageHelper.correctRotateImage(path, true, new OnRotateImage() {
            @Override
            public void startProcess() {

            }

            @Override
            public void success(String newPath) {
                G.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (itemGalleryList == null) {
                            itemGalleryList = new ArrayList<>();
                        }

                        StructBottomSheet item = new StructBottomSheet();
                        item.setId(itemGalleryList.size());
                        item.setPath(newPath);
                        item.setText("");
                        item.isSelected = isSelected;
                        itemGalleryList.add(0, item);
                        textImageList.put(newPath, item);
                        setUpViewPager();
                        imageViewTouch.setImageBitmap(BitmapFactory.decodeFile(newPath));
                        imageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
                    }
                });

            }
        });
    }

    private void setUpViewPager() {
        mAdapter = new AdapterViewPager(itemGalleryList);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (itemGalleryList.get(position).isSelected) {
                    animateCheckBox.setChecked(false);
                    animateCheckBox.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                } else {
                    animateCheckBox.setChecked(true);
                    animateCheckBox.setUnCheckColor(G.context.getResources().getColor(R.color.setting_items_value_color));
                }

                if (textImageList.containsKey(itemGalleryList.get(position).path)) {
                    captionEditText.setText(EmojiManager.getInstance().replaceEmoji(textImageList.get(itemGalleryList.get(position).path).getText(), captionEditText.getPaint().getFontMetricsInt()));
                } else {
                    captionEditText.setText("");
                }
                iconOkTextView.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void showPopUPView(int event) {


        if (event == KeyboardView.MODE_KEYBOARD) {
            emoji.setText(getActivity().getString(R.string.md_emoticon_with_happy_face));
            chatKeyBoardContainer.setVisibility(VISIBLE);

            if (keyboardHeight <= 0) {
                keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(300));
            }

            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(300));
            }

            ViewGroup.LayoutParams layoutParams = chatKeyBoardContainer.getLayoutParams();
            layoutParams.width = AndroidUtils.displaySize.x;
            layoutParams.height = keyboardHeight;
            chatKeyBoardContainer.setLayoutParams(layoutParams);

            openKeyboardInternal();

            if (emojiView != null) {
                emojiView.setVisibility(View.GONE);
            }

            cancelCropLayout.setVisibility(View.GONE);

            if (rootView != null) {
                rootView.requestLayout();

            }
        } else {
            cancelCropLayout.setVisibility(VISIBLE);
            chatKeyBoardContainer.setVisibility(View.GONE);
            if (emojiView != null)
                emojiView.setVisibility(View.GONE);
            closeKeyboard();
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
                    if (captionEditText.length() == 0) {
                        return;
                    }
                    captionEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
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
                    int i = captionEditText.getSelectionEnd();

                    if (i < 0) i = 0;

                    try {
                        CharSequence sequence = EmojiManager.getInstance().replaceEmoji(unicode, captionEditText.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false);
                        if (captionEditText.getText() != null)
                            captionEditText.setText(captionEditText.getText().insert(i, sequence));
                        int j = i + sequence.length();
                        captionEditText.setSelection(j, j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        chatKeyBoardContainer.addView(emojiView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));
    }


    private boolean isPopupShowing() {
        return emojiView != null && emojiView.getVisibility() == VISIBLE;
    }

    private boolean isKeyboardVisible() {
        return AndroidUtils.usingKeyboardInput || keyboardVisible;
    }

    private void openKeyboardInternal() {
        captionEditText.requestFocus();
        AndroidUtils.showKeyboard(captionEditText);
    }

    public void closeKeyboard() {
        captionEditText.clearFocus();
        AndroidUtils.hideKeyboard(captionEditText);
    }
}

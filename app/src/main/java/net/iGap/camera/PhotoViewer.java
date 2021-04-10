package net.iGap.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.hanks.library.AnimateCheckBox;
import com.zomato.photofilters.imageprocessors.Filter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.filterImage.BitmapUtils;
import net.iGap.fragments.filterImage.FragmentFilterImage;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.EmojiView;
import net.iGap.libs.emojiKeyboard.KeyboardView;
import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.libs.emojiKeyboard.NotifyLinearLayout;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.photoEdit.BrushDrawingView;
import net.iGap.libs.photoEdit.BrushViewChangeListener;
import net.iGap.libs.photoEdit.FilterImageView;
import net.iGap.libs.photoEdit.PhotoEditorView;
import net.iGap.libs.photoEdit.ViewType;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yogesh.firzen.mukkiasevaigal.M;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

public class PhotoViewer extends BaseFragment implements NotifyLinearLayout.Listener,
        OnPhotoEditorListener, BrushConfigDialog.Properties, BrushViewChangeListener, EmojiDialogFrag.EmojiListener, FilterDialogFragment.FiltersListFragmentListener {
    private static final float MAX_PERCENT = 100;
    private static final float MAX_ALPHA = 255;
    private static final float INITIAL_WIDTH = 50;
    private NotifyLinearLayout rootView;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private LinearLayout toolbarPanel;
    private RippleView rippleView;
    private MaterialDesignTextView designTextView;
    private MaterialDesignTextView revertTextView;
    private MaterialDesignTextView emoji;
    private MaterialDesignTextView keyboardEmoji;
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
    private List<View> redoViews;
    private FrameLayout stickerBorder;
    private TextView textTv;
    private CustomPaintView customPaintView;
    private boolean isEraser = false;
    private float brushSize = INITIAL_WIDTH;
    private float brushAlpha = MAX_ALPHA;
    private int brushColor = Color.WHITE;
    private BrushConfigDialog brushConfigDialog;
    private Modes mode;
    private MutableLiveData<Integer> onPaintChanged = new MutableLiveData<>();

    public static UpdateImage updateImage;
    private Bitmap filteredImageBitmap;

    public static PhotoViewer newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        PhotoViewer fragment = new PhotoViewer();
        fragment.setArguments(args);
        return fragment;
    }

    public interface UpdateImage {
        void result(Bitmap path);
    }

    @SuppressLint("ResourceType")
    @Override
    public View createView(Context context) {
        rootView = new NotifyLinearLayout(context) {
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
        rootView.setOrientation(LinearLayout.VERTICAL);

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

        revertTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        revertTextView.setText(context.getString(R.string.forward_icon));
        revertTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        revertTextView.setGravity(Gravity.CENTER);
        revertTextView.setVisibility(View.GONE);
        toolbarPanel.addView(revertTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, 0, 0, 8, 0));

        countImageTextView = new TextView(context);
        countImageTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        countImageTextView.setText(context.getString(R.string.photo));
        countImageTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        countImageTextView.setTextSize(22);
        countImageTextView.setTypeface(countImageTextView.getTypeface(), Typeface.BOLD);
        toolbarPanel.addView(countImageTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, 0, 0, 8, 0));

        animateCheckBox = new AnimCheckBox(context);
        animateCheckBox.setBackground(context.getResources().getDrawable(R.drawable.background_check));
        animateCheckBox.setCircleColor(R.attr.iGapButtonColor);
        animateCheckBox.setLineColor(context.getResources().getColor(R.color.whit_background));
        animateCheckBox.setAnimDuration(100);
        animateCheckBox.setCorrectWidth(1);
        animateCheckBox.setUnCheckColor(context.getResources().getColor(R.color.background_checkbox_bottomSheet));
        toolbarPanel.addView(animateCheckBox, LayoutCreator.createLinear(32, 32, Gravity.CENTER | Gravity.END | Gravity.RIGHT, 0, 0, 8, 0));


        toolbar.addView(toolbarPanel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(toolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 60, Gravity.CENTER));


        viewPager = new ViewPager(context);
        viewPager.setVisibility(View.GONE);
        zoomLayout = new ZoomLayout(context);
        zoomLayout.setVisibility(View.GONE);
        textStickersParentView = new TextStickerView(context, false);
        textStickersParentView.setDrawingCacheEnabled(true);
        textStickersParentView.setVisibility(View.GONE);
        zoomLayout.addView(textStickersParentView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));
        rootView.addView(zoomLayout, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER));

        customPaintView = new CustomPaintView(context);
        customPaintView.setVisibility(View.GONE);
        // zoomLayout.addView(customPaintView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        rootView.addView(customPaintView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER));
        rootView.addView(viewPager, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER));

        imageViewTouch = new ImageViewTouch(context);
        imageViewTouch.setScaleType(ImageView.ScaleType.FIT_CENTER);
        rootView.addView(imageViewTouch, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER));


        bottomLayoutPanel = new LinearLayout(context);
        bottomLayoutPanel.setOrientation(LinearLayout.VERTICAL);
        bottomLayoutPanel.setBackgroundColor(context.getResources().getColor(R.color.colorEditImageBlack));

        layoutCaption = new LinearLayout(context);
        layoutCaption.setOrientation(LinearLayout.HORIZONTAL);
        layoutCaption.setMinimumHeight(48);
        layoutCaption.setPadding(4, 0, 4, 0);
        bottomLayoutPanel.addView(layoutCaption, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        keyboardEmoji = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        keyboardEmoji.setGravity(Gravity.CENTER);
        keyboardEmoji.setPadding(8, 0, 8, 8);
        keyboardEmoji.setText(context.getString(R.string.md_emoticon_with_happy_face));
        keyboardEmoji.setTextColor(context.getResources().getColor(R.color.white));
        keyboardEmoji.setTextSize(26);
        layoutCaption.addView(keyboardEmoji, LayoutCreator.createLinear(52, 52, Gravity.CENTER));

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
        cropTextView.setVisibility(View.GONE);
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

        emoji = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        emoji.setGravity(Gravity.CENTER);
        emoji.setPadding(8, 0, 8, 8);
        emoji.setText(context.getString(R.string.md_emoticon_with_happy_face));
        emoji.setTextColor(context.getResources().getColor(R.color.white));
        emoji.setTextSize(26);
        cancelCropLayout.addView(emoji, LayoutCreator.createLinear(52, 52, Gravity.CENTER));

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

        rootView.addView(bottomLayoutPanel, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addedViews = new ArrayList<>();
        redoViews = new ArrayList<>();
        path = getArguments().getString(PATH);
        brushConfigDialog = new BrushConfigDialog();
        brushConfigDialog.setPropertiesChangeListener(this);
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
                if (mode == Modes.PAINT || mode == Modes.ADD_TEXT || mode == Modes.EMOJI || mode == Modes.FILTER) {
                    textStickersParentView.setPaintMode(false, null);
                } else {
                    onAddTextShow();
                }

                mode = Modes.ADD_TEXT;


                TextEditorDialogFragment textEditorDialogFragment =
                        TextEditorDialogFragment.newInstance(getActivity());
                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                    FrameLayout rootView = new FrameLayout(getContext());
                    stickerBorder = new FrameLayout(getContext());
                    stickerBorder.setPadding(10, 10, 10, 10);
                    rootView.addView(stickerBorder, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 8, 8, 8, 8));

                    textTv = new TextView(getContext());
                    textTv.setTextColor(colorCode);
                    textTv.setText(inputText);
                    stickerBorder.addView(textTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 14, 14, 14, 14));
                    textTv.setGravity(Gravity.CENTER);
                    textTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            35);


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

        paintTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewTouch.setVisibility(View.GONE);
                zoomLayout.setVisibility(VISIBLE);
                textStickersParentView.setVisibility(View.VISIBLE);
                if (mode == Modes.PAINT || mode == Modes.ADD_TEXT || mode == Modes.EMOJI || mode == Modes.FILTER) {
                    textStickersParentView.setPaintMode(true, null);
                } else {
                    textStickersParentView.setPaintMode(true, BitmapFactory.decodeFile(path));
                }
                mode = Modes.PAINT;
                textStickersParentView.getmBrushDrawingView().setBrushViewChangeListener(PhotoViewer.this);
                String tag = brushConfigDialog.getTag();

                // Avoid IllegalStateException "Fragment already added"
                if (brushConfigDialog.isAdded()) return;

                brushConfigDialog.show(getParentFragmentManager(), tag);

                updateBrushParams();

            }
        });
        initStroke();

        revertTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean revert = undo();
                revertTextView.setVisibility(revert ? VISIBLE : View.GONE);
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
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == Modes.PAINT) {
                    textStickersParentView.setPaintMode(false, null);
                } else if (mode == Modes.ADD_TEXT) {
                    textStickersParentView.setPaintMode(false, null);
                } else {
                    imageViewTouch.setVisibility(View.GONE);
                    textStickersParentView.setPaintMode(false, BitmapFactory.decodeFile(path));
                    zoomLayout.setVisibility(VISIBLE);
                    textStickersParentView.setVisibility(View.VISIBLE);
                }
                mode = Modes.EMOJI;
                EmojiDialogFrag emojiDialogFrag = new EmojiDialogFrag();
                emojiDialogFrag.setEmojiListener(PhotoViewer.this::onEmojiClick);
                emojiDialogFrag.show(getParentFragmentManager(), emojiDialogFrag.getTag());
            }
        });
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = Modes.FILTER;
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentFilterImage.newInstance(path)).setReplace(false).load();
            }
        });
        updateImage = new UpdateImage() {

            @Override
            public void result(Bitmap finalBitmap) {
                filteredImageBitmap = finalBitmap;
                textStickersParentView.updateImageBitmap(finalBitmap);
            }
        };
        sendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                path = BitmapUtils.insertImage(getActivity().getContentResolver(), filteredImageBitmap, System.currentTimeMillis() + "_profile.jpg", null);

            }
        });
    }

    private Bitmap getFinalBitmapAfterAddText(View view) {

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache());
        Bitmap resultBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        int textStickerHeightCenterY = textStickersParentView.getHeight() / 2;
        int textStickerWidthCenterX = textStickersParentView.getWidth() / 2;

        int imageViewHeight = textStickersParentView.getBitmapHolderImageView().getHeight();
        int imageViewWidth = textStickersParentView.getBitmapHolderImageView().getWidth();

        view.setDrawingCacheEnabled(false);
        // Crop actual image from textStickerView
        return Bitmap.createBitmap(resultBitmap, textStickerWidthCenterX - (imageViewWidth / 2), textStickerHeightCenterY - (imageViewHeight / 2), imageViewWidth, imageViewHeight);
    }

    private void onAddTextShow() {
        imageViewTouch.setVisibility(View.GONE);
        textStickersParentView.setPaintMode(false, BitmapFactory.decodeFile(this.path));
        zoomLayout.setVisibility(VISIBLE);
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
        revertTextView.setVisibility(VISIBLE);
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

    public void addEmoji(Typeface emojiTypeface, String emojiName) {
        FrameLayout rootView = new FrameLayout(getContext());
        stickerBorder = new FrameLayout(getContext());
        rootView.addView(stickerBorder, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 8, 8, 8, 8));

        textTv = new TextView(getContext());
        textTv.setTextColor(Color.BLACK);
        textTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                18);
        textTv.setGravity(Gravity.CENTER);
        textTv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        stickerBorder.addView(textTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));


        if (emojiTypeface != null) {
            textTv.setTypeface(emojiTypeface);
        }
        textTv.setTextSize(56);
        textTv.setText(emojiName);

        MultiTouchListener multiTouchListener = new MultiTouchListener(
                null,
                textStickersParentView,
                imageViewTouch,
                PhotoViewer.this, getContext());

        multiTouchListener.setOnGestureControl(new OnGestureControl() {
            boolean isDownAlready = false;

            @Override
            public void onClick() {
            }

            @Override
            public void onDown() {
            }

            @Override
            public void onLongClick() {
            }
        });
        rootView.setOnTouchListener(multiTouchListener);

        addViewToParent(rootView);
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

    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        if (redoViews.size() > 0) {
            redoViews.remove(redoViews.size() - 1);
        }
        addedViews.add(brushDrawingView);
        revertTextView.setVisibility(VISIBLE);
        onPaintChanged.setValue(addedViews.size());
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (addedViews.size() > 0) {
            View removeView = addedViews.remove(addedViews.size() - 1);
            if (!(removeView instanceof BrushDrawingView)) {
                textStickersParentView.removeView(removeView);
            }
            onPaintChanged.setValue(addedViews.size());
            redoViews.add(removeView);
        }
    }

    @Override
    public void onStartDrawing() {

    }

    @Override
    public void onStopDrawing() {

    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        addEmoji(null, emojiUnicode);
    }

    @Override
    public void onFilterSelected(PhotoFilter filter) {
        textStickersParentView.setFilterEffect(filter);
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

                        Glide.with(getContext()).asDrawable().load(new File(finalPath)).centerCrop().into(imgPlay);
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
                        //   G.imageLoader.displayImage(newPath, imageViewTouch);
                        Glide.with(getContext()).asDrawable().load(new File(newPath)).centerCrop().into(imageViewTouch);
//                        imageViewTouch.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(newPath),getSize(-1), getSize(-1), true));
                        imageViewTouch.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
                    }
                });

            }
        });
    }


    public boolean undo() {
        if (addedViews.size() > 0) {
            View removeView = addedViews.get(addedViews.size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return textStickersParentView.getmBrushDrawingView() != null && textStickersParentView.getmBrushDrawingView().undo();
            } else {
                addedViews.remove(addedViews.size() - 1);
                textStickersParentView.removeView(removeView);
                redoViews.add(removeView);
                onPaintChanged.setValue(addedViews.size());
            }
        }
        return addedViews.size() != 0;
    }


    public int getSize(float size) {
        return (int) (size < 0 ? size : dp(size));
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(getContext().getResources().getDisplayMetrics().density * value);
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
            keyboardEmoji.setText(getActivity().getString(R.string.md_emoticon_with_happy_face));
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
        } else if (event == KeyboardView.MODE_EMOJI) {
            if (emojiView == null) {
                createEmojiView();
            }

            captionEditText.requestFocus();
            emojiView.setVisibility(VISIBLE);
            chatKeyBoardContainer.setVisibility(VISIBLE);

            if (keyboardHeight <= 0) {
                keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(300));
            }
            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(300));
            }

            int currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardHeight;

            ViewGroup.LayoutParams layoutParams = chatKeyBoardContainer.getLayoutParams();
            layoutParams.width = AndroidUtils.displaySize.x;
            layoutParams.height = currentHeight;
            chatKeyBoardContainer.setLayoutParams(layoutParams);

            if (keyboardVisible) {
                closeKeyboard();
            }

            if (rootView != null) {
                emojiPadding = currentHeight;
                rootView.requestLayout();
                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                bottomLayoutPanel.setVisibility(View.GONE);
            }
        } else {
            cancelCropLayout.setVisibility(VISIBLE);
            chatKeyBoardContainer.setVisibility(View.GONE);
            if (emojiView != null)
                emojiView.setVisibility(View.GONE);
            closeKeyboard();
        }

    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        keyboardEmoji.setText(drawableResourceId);
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

    private void initStroke() {
        textStickersParentView.setBrushSize(INITIAL_WIDTH);
        textStickersParentView.setBrushColor(Color.WHITE);
        textStickersParentView.setStrokeAlpha(MAX_ALPHA);
    }

    @Override
    public void onColorChanged(int colorCode) {
        brushColor = colorCode;
        updateBrushParams();
    }

    @Override
    public void onOpacityChanged(int opacity) {
        brushAlpha = (opacity / MAX_PERCENT) * MAX_ALPHA;
        updateBrushParams();
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        this.brushSize = brushSize;
        updateBrushParams();
    }

    private void updateBrushParams() {
        textStickersParentView.setBrushColor(brushColor);
        textStickersParentView.setBrushSize(brushSize);
        textStickersParentView.setStrokeAlpha(brushAlpha);
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

    private ArrayList<String> getEmojis(Context context) {
        ArrayList<String> convertedEmojiList = new ArrayList<>();
        String[] emojiList = context.getResources().getStringArray(R.array.photo_editor_emoji);
        for (String emojiUnicode : emojiList) {
            convertedEmojiList.add(convertEmoji(emojiUnicode));
        }
        return convertedEmojiList;
    }

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }


    enum Modes {
        PAINT, ADD_TEXT, EMOJI, FILTER
    }
}

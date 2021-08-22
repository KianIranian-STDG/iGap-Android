package net.iGap.story;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;

import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentGallery;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.filterImage.BitmapUtils;
import net.iGap.fragments.filterImage.FragmentFilterImage;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.libs.emojiKeyboard.EmojiView;
import net.iGap.libs.emojiKeyboard.KeyboardView;
import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.photoEdit.BrushDrawingView;
import net.iGap.libs.photoEdit.BrushViewChangeListener;
import net.iGap.libs.photoEdit.PhotoEditor;
import net.iGap.libs.photoEdit.SaveSettings;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.model.GalleryItemModel;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.EventEditText;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnRotateImage;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoStoryGetStories;
import net.iGap.realm.RealmStory;
import net.iGap.story.viewPager.StoryDisplayFragment;
import net.iGap.story.viewPager.StoryViewFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

public class PhotoViewer extends BaseFragment implements NotifyFrameLayout.Listener,
        OnPhotoEditorListener, BrushConfigDialog.Properties, BrushViewChangeListener, EmojiDialogFrag.EmojiListener, FilterDialogFragment.FiltersListFragmentListener {
    private static final float MAX_PERCENT = 100;
    private static final float MAX_ALPHA = 255;
    private static final float INITIAL_WIDTH = 50;
    private static final String SELECTED_PHOTOS = "selectedPhotos";
    private NotifyFrameLayout rootView;
    private CustomViewPager viewPager;
    private Toolbar toolbar;
    private RippleView rippleView;
    private MaterialDesignTextView designTextView;
    private MaterialDesignTextView revertTextView;
    private MaterialDesignTextView emoji;
    private MaterialDesignTextView keyboardEmoji;
    private MaterialDesignTextView iconOkTextView;
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
    private HashMap<Integer, List<View>> addedViews;
    private HashMap<Integer, StoryModes> modes;
    private List<View> redoViews;
    private FrameLayout stickerBorder;
    private TextView textTv;
    private boolean isEraser = false;
    private float brushSize = INITIAL_WIDTH;
    private float brushAlpha = MAX_ALPHA;
    private int brushColor = Color.WHITE;
    private BrushConfigDialog brushConfigDialog;
    public static StoryModes mode;
    private MutableLiveData<Integer> onPaintChanged = new MutableLiveData<>();
    private HashMap<Integer, ZoomLayout> viewHolders = new HashMap<>();
    private List<String> finalBitmapsPaths = new ArrayList<>();
    public List<GalleryItemModel> selectedPhotos;
    private int viewHolderPostion = 0;
    public static UpdateImage updateImage;
    private OnEditActions onEditActions;
    private ImageView pickerViewSendButton;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private ProgressBar progressBar;
    private int counter = 0;

    public static PhotoViewer newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        PhotoViewer fragment = new PhotoViewer();
        fragment.setArguments(args);
        return fragment;
    }


    public static PhotoViewer newInstance(ArrayList<GalleryItemModel> selectedPhotos) {
        Bundle args = new Bundle();
        args.putSerializable(SELECTED_PHOTOS, selectedPhotos);
        PhotoViewer fragment = new PhotoViewer();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public interface UpdateImage {
        void result(Bitmap path);

        void resultWithPath(String path);
    }

    @SuppressLint("ResourceType")
    @Override
    public View createView(Context context) {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
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


        viewPager = new CustomViewPager(context);
        rootView.addView(viewPager, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        cancelCropLayout = new LinearLayout(context);
        cancelCropLayout.setOrientation(LinearLayout.HORIZONTAL);
        cancelCropLayout.setBackgroundColor(context.getResources().getColor(R.color.colorEditImageBlack2));


        rippleView = new RippleView(context);
        rippleView.setCentered(true);
        rippleView.setRippleAlpha(200);
        rippleView.setRippleDuration(0);
        rippleView.setRipplePadding(5);
        cancelCropLayout.addView(rippleView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        designTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        designTextView.setText(context.getString(R.string.md_close_button));
        designTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        designTextView.setGravity(Gravity.CENTER);
        rippleView.addView(designTextView, LayoutCreator.createRelative(48, LayoutCreator.MATCH_PARENT));

        emptyView = new View(context);
        cancelCropLayout.addView(emptyView, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1F));

        revertTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        revertTextView.setText(context.getString(R.string.forward_icon));
        revertTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        revertTextView.setGravity(Gravity.CENTER);
        revertTextView.setVisibility(View.GONE);
        cancelCropLayout.addView(revertTextView, LayoutCreator.createLinear(52, 52));

        cropTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        cropTextView.setGravity(Gravity.CENTER);
        cropTextView.setText(context.getString(R.string.md_crop_button));
        cropTextView.setVisibility(View.GONE);
        cropTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(cropTextView, LayoutCreator.createLinear(52, LayoutCreator.MATCH_PARENT));

        editTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        editTextView.setGravity(Gravity.CENTER);
        editTextView.setText(getString(R.string.md_igap_tune));
        editTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(editTextView, LayoutCreator.createLinear(52, LayoutCreator.MATCH_PARENT, 0, 0, 8, 0));


        emoji = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        emoji.setGravity(Gravity.CENTER);
        emoji.setText(getString(R.string.md_emoticon_with_happy_face));
        emoji.setTextColor(context.getResources().getColor(R.color.white));
        emoji.setTextSize(26);
        cancelCropLayout.addView(emoji, LayoutCreator.createLinear(52, LayoutCreator.MATCH_PARENT, 0, 0, 8, 0));

        addTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        addTextView.setGravity(Gravity.CENTER);
        addTextView.setBackground(context.getResources().getDrawable(R.drawable.ic_cam_text));
        addTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(addTextView, LayoutCreator.createLinear(30, 30, 0, 0, 8, 0));

        paintTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        paintTextView.setGravity(Gravity.CENTER);
        paintTextView.setText(getString(R.string.md_igap_paint));
        paintTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        cancelCropLayout.addView(paintTextView, LayoutCreator.createLinear(52, 52, 0, 0, 8, 0));

        rootView.addView(cancelCropLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 60, Gravity.TOP));

        bottomLayoutPanel = new LinearLayout(context);
        bottomLayoutPanel.setOrientation(LinearLayout.VERTICAL);
        bottomLayoutPanel.setBackgroundColor(context.getResources().getColor(R.color.colorEditImageBlack));


        layoutCaption = new LinearLayout(context);
        layoutCaption.setOrientation(LinearLayout.HORIZONTAL);
        layoutCaption.setMinimumHeight(48);
        layoutCaption.setPadding(4, 0, 4, 0);
        bottomLayoutPanel.addView(layoutCaption, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 60, Gravity.CENTER));

        keyboardEmoji = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        keyboardEmoji.setGravity(Gravity.CENTER);
        keyboardEmoji.setPadding(8, 0, 8, 8);
        keyboardEmoji.setText(context.getString(R.string.md_emoticon_with_happy_face));
        keyboardEmoji.setTextColor(context.getResources().getColor(R.color.white));
        keyboardEmoji.setTextSize(26);
        layoutCaption.addView(keyboardEmoji, LayoutCreator.createLinear(30, 30, Gravity.CENTER, 5, 0, 0, 0));

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
        layoutCaption.addView(captionEditText, LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, 1, Gravity.CENTER));

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

        pickerViewSendButton = new ImageView(context);
        pickerViewSendButton.setScaleType(ImageView.ScaleType.CENTER);
        pickerViewSendButton.setImageResource(R.drawable.attach_send);
        layoutCaption.addView(pickerViewSendButton, LayoutCreator.createLinear(40, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.GONE);
        layoutCaption.addView(progressBar, LayoutCreator.createLinear(40, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));

        sendTextView = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        sendTextView.setGravity(Gravity.CENTER);
        sendTextView.setText(context.getString(R.string.md_send_button));
        sendTextView.setTextColor(context.getResources().getColor(R.color.whit_background));
        sendTextView.setVisibility(View.GONE);

        rootView.addView(bottomLayoutPanel, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addedViews = new HashMap<>();
        modes = new HashMap<>();
        redoViews = new ArrayList<>();
        path = getArguments().getString(PATH);
        selectedPhotos = (List<GalleryItemModel>) getArguments().getSerializable(SELECTED_PHOTOS);
        brushConfigDialog = new BrushConfigDialog();
        brushConfigDialog.setPropertiesChangeListener(this);
        mode = StoryModes.NONE;
        modes.put(viewHolderPostion, StoryModes.NONE);
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
        if (path != null) {
            openPhotoForEdit(path, null, false);
        } else {
            openPhotoForEdit(selectedPhotos, null, false);
            for (int i = 0; i < itemGalleryList.size(); i++) {
                modes.put(i, StoryModes.NONE);
            }
        }
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
                viewPager.setPagingEnabled(true);
                paintTextView.setBackground(null);
                textStickersParentView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
                if (mode == StoryModes.PAINT || mode == StoryModes.ADD_TEXT || mode == StoryModes.EMOJI || mode == StoryModes.FILTER) {
                    textStickersParentView.setPaintMode(false, "");
                } else {
                    textStickersParentView.setPaintMode(false, "");
                    autoScaleImageToFitBounds();
                }

                mode = StoryModes.ADD_TEXT;
                modes.put(viewHolderPostion, StoryModes.ADD_TEXT);

                TextEditorDialogFragment textEditorDialogFragment =
                        TextEditorDialogFragment.newInstance(getActivity());
                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode, width) -> {
                    FrameLayout rootView = new FrameLayout(getContext());
                    stickerBorder = new FrameLayout(getContext());
                    stickerBorder.setPadding(10, 10, 10, 10);
                    rootView.addView(stickerBorder, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 8, 8, 8, 8));

                    textTv = new TextView(getContext());
                    textTv.setId(R.id.story_added_text);
                    textTv.setTextColor(colorCode);
                    textTv.setText(inputText);
                    textTv.setGravity(Gravity.CENTER);
                    textTv.setTextSize(30);
                    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textTv, 22, 30, 1,
                            TypedValue.COMPLEX_UNIT_DIP);
                    stickerBorder.addView(textTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 14, 14, 14, 14));

                    onEditActions.addText(rootView, inputText, colorCode);

                });
            }
        });

        paintTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setPagingEnabled(false);
                textStickersParentView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
                if (mode == StoryModes.PAINT || mode == StoryModes.ADD_TEXT || mode == StoryModes.EMOJI || mode == StoryModes.FILTER) {
                    textStickersParentView.setPaintMode(true, "");
                } else {
                    textStickersParentView.setPaintMode(true, "");
                }
                mode = StoryModes.PAINT;
                modes.put(viewHolderPostion, StoryModes.PAINT);
                textStickersParentView.getmBrushDrawingView().setBrushViewChangeListener(PhotoViewer.this);
                String tag = brushConfigDialog.getTag();

                // Avoid IllegalStateException "Fragment already added"
                if (brushConfigDialog.isAdded()) return;

                brushConfigDialog.setBrushColor(brushColor);
                brushConfigDialog.setBrushOpacity(brushAlpha);
                brushConfigDialog.setBrushSize(brushSize);
                brushConfigDialog.show(getParentFragmentManager(), tag);
                onEditActions.paint();
                updateBrushParams();

            }
        });

        revertTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textStickersParentView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
                boolean revert = undo();
                if (addedViews.get(viewHolderPostion).size() == 0) {
                    paintTextView.setBackground(null);
                }
                revertTextView.setVisibility(addedViews.get(viewHolderPostion).size() > 0 ? View.VISIBLE : View.GONE);
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
                paintTextView.setBackground(null);
                viewPager.setPagingEnabled(true);
                textStickersParentView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
                if (mode == StoryModes.PAINT) {
                    textStickersParentView.setPaintMode(false, "");
                } else if (mode == StoryModes.ADD_TEXT) {
                    textStickersParentView.setPaintMode(false, "");
                } else {
                    textStickersParentView.setPaintMode(false, "");
                }
                mode = StoryModes.EMOJI;
                modes.put(viewHolderPostion, StoryModes.EMOJI);
                EmojiDialogFrag emojiDialogFrag = new EmojiDialogFrag();
                emojiDialogFrag.setEmojiListener(PhotoViewer.this::onEmojiClick);
                emojiDialogFrag.show(getParentFragmentManager(), emojiDialogFrag.getTag());
                onEditActions.emoji();
            }
        });
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintTextView.setBackground(null);
                viewPager.setPagingEnabled(true);
                mode = StoryModes.FILTER;
                modes.put(viewHolderPostion, StoryModes.FILTER);
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentFilterImage.newInstance(itemGalleryList.get(viewHolderPostion).getPath())).setReplace(false).load();
            }
        });
        updateImage = new UpdateImage() {

            @Override
            public void result(Bitmap finalBitmap) {
                textStickersParentView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
                textStickersParentView.updateImageBitmap(finalBitmap);

            }

            @Override
            public void resultWithPath(String path) {
                textStickersParentView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
                serFilterImage(path);
            }
        };
        pickerViewSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerViewSendButton.setVisibility(View.GONE);
                progressBar.setVisibility(VISIBLE);
                editTextView.setEnabled(false);
                emoji.setEnabled(false);
                revertTextView.setEnabled(false);
                paintTextView.setEnabled(false);
                addTextView.setEnabled(false);
                captionEditText.setEnabled(false);
                keyboardEmoji.setEnabled(false);
                rootView.setEnabled(false);
                for (int i = 0; i < itemGalleryList.size(); i++) {
                    if (viewHolders.get(i) != null) {
                        reservedView = viewHolders.get(i);
                        new SaveBitmapAsync(itemGalleryList.get(i).path, null, viewHolders.get(i).findViewById(R.id.textstickerView), modes.get(i)).execute();
                    } else {
                        zoomLayout = new ZoomLayout(context);
                        zoomLayout.measure(View.MeasureSpec.makeMeasureSpec(viewPager.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(viewPager.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
                        zoomLayout.layout(0, 0, viewPager.getMeasuredWidth(), viewPager.getMeasuredHeight());

                        textStickersParentView = new TextStickerView(context, false);

                        textStickersParentView.setId(R.id.textstickerView);
                        zoomLayout.addView(textStickersParentView);


                        if (itemGalleryList.get(i).path != null && !viewHolders.containsKey(i)) {


                            String oldPath = itemGalleryList.get(i).path;
                            String finalPath = attachFile.saveGalleryPicToLocal(oldPath);

                            //check if old path available in selected list , replace new path with that
                            if (!oldPath.equals(finalPath)) {
                                StructBottomSheet item = textImageList.get(oldPath);
                                if (item != null) {
                                    textImageList.remove(oldPath);
                                    textImageList.put(finalPath, item);
                                }
                            }
                            itemGalleryList.get(i).path = finalPath;


                            textStickersParentView.measure(View.MeasureSpec.makeMeasureSpec(viewPager.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(viewPager.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
                            textStickersParentView.layout(0, 0, viewPager.getMeasuredWidth(), viewPager.getMeasuredHeight());


                            Palette palette = Palette.from(BitmapFactory.decodeFile(finalPath)).generate();
                            GradientDrawable gd = new GradientDrawable(
                                    GradientDrawable.Orientation.TOP_BOTTOM,
                                    new int[]{
                                            palette.getLightMutedColor(0xFF616261), palette.getMutedColor(0xFF616261), palette.getDarkMutedColor(0xFF616261)});

                            textStickersParentView.setBackground(gd);
                            textStickersParentView.setPaintMode(false, finalPath);

                            Bitmap b = Bitmap.createBitmap(textStickersParentView.getMeasuredWidth(), textStickersParentView.getMeasuredHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas mCanvas = new Canvas(b);
                            mCanvas.translate((-textStickersParentView.getScrollX()), (-textStickersParentView.getScrollY()));
                            textStickersParentView.draw(mCanvas);

                            new SaveBitmapAsync(itemGalleryList.get(i).path, b, textStickersParentView, modes.get(i)).execute();


                        }

                    }
                }


            }
        });
        captionEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPopupShowing()) {
                    keyboardEmoji.performClick();
                }
            }
        });
        keyboardEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPopupShowing()) {
                    showPopUPView(KeyboardView.MODE_EMOJI);
                } else {
                    showPopUPView(KeyboardView.MODE_KEYBOARD);
                }

            }
        });

        captionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                StructBottomSheet item = new StructBottomSheet();
                item.setText(s.toString());
                item.setPath(itemGalleryList.get(viewHolderPostion).path);
                item.setId(itemGalleryList.get(viewHolderPostion).getId());
                itemGalleryList.get(viewHolderPostion).setText(s.toString());
                textImageList.put(itemGalleryList.get(viewHolderPostion).path, item);
            }
        });

    }

    ZoomLayout reservedView;

    public class SaveBitmapAsync extends AsyncTask<String, String, Exception> {
        String imagePath;
        TextStickerView textStickerView;
        StoryModes mode;
        Bitmap finalImageBitmap;

        public SaveBitmapAsync(String imagePath, Bitmap finalImageBitmap, TextStickerView textStickerView, StoryModes mode) {
            this.imagePath = imagePath;
            this.textStickerView = textStickerView;
            this.mode = mode;
            this.finalImageBitmap = finalImageBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textStickerView.setDrawingCacheEnabled(false);
        }

        @Override
        protected Exception doInBackground(String... strings) {

            try {
                Bitmap finalBitmap = null;
                textStickerView.setDrawingCacheEnabled(true);
                finalBitmap = Bitmap.createBitmap(textStickerView.getDrawingCache());
                Bitmap resultBitmap = finalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                int textStickerHeightCenterY = textStickerView.getHeight() / 2;
                int textStickerWidthCenterX = textStickerView.getWidth() / 2;

                int imageViewHeight = textStickerView.getBitmapHolderImageView().getHeight();
                int imageViewWidth = textStickerView.getBitmapHolderImageView().getWidth();
                Bitmap finalResultBitmap = Bitmap.createBitmap(resultBitmap, textStickerWidthCenterX - (imageViewWidth / 2), textStickerHeightCenterY - (imageViewHeight / 2), imageViewWidth, imageViewHeight);

                if (textStickerView != null && this.mode != StoryModes.NONE) {
                    String savedPath = G.DIR_TEMP + "/" + System.currentTimeMillis() + "_edited_image.jpg";
                    File imageFile = new File(savedPath);

                    imageFile.createNewFile();

                    SaveSettings saveSettings = new SaveSettings.Builder()
                            .setTransparencyEnabled(true)
                            .build();
                    File file = new File(imageFile.getAbsolutePath());
                    FileOutputStream out = new FileOutputStream(file, false);
                    finalResultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    BitmapUtils.insertImage(getActivity().getContentResolver(), finalResultBitmap, System.currentTimeMillis() + "_edited_image.jpg", null);
                    this.imagePath = imageFile.getAbsolutePath();
                } else if (this.mode == StoryModes.NONE) {
                    File imageFile = new File(this.imagePath);
                    FileOutputStream fileOutputStream = new FileOutputStream(imageFile, false);
                    if (finalImageBitmap != null) {
                        finalImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    } else {
                        finalResultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    }

                }

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }


        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {
                //Clear all views if its enabled in save settings
                setCreatedFinalBitmap(imagePath);
            } else {


            }
        }
    }

    private void setCreatedFinalBitmap(String path) {


        if (textImageList.containsKey(itemGalleryList.get(counter).getPath())) {

            String message = textImageList.get(itemGalleryList.get(counter).getPath()).getText();
            int id = textImageList.get(itemGalleryList.get(counter).getPath()).getId();

            textImageList.remove(itemGalleryList.get(counter).getPath());
            StructBottomSheet item = new StructBottomSheet();
            item.setPath(path);
            item.setText(message);
            item.setId(id);

            textImageList.put(path, item);
        }

        itemGalleryList.get(counter).setPath(path);
        finalBitmapsPaths.add(path);
        counter++;
        if (counter == itemGalleryList.size()) {
            new HelperFragment(getActivity().getSupportFragmentManager(), PhotoViewer.this).popBackStack(2);
            EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_UPLOAD, finalBitmapsPaths, itemGalleryList);
        }

    }

    private void serFilterImage(String path) {

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


    }


    private void showTextEditDialog(final View rootView, MultiTouchListener multiTouchListener) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.newInstance(getActivity(), multiTouchListener.getAddedText(), multiTouchListener.getColorCode());
        textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode1, width) -> {
            multiTouchListener.setColorCode(colorCode1);
            multiTouchListener.setAddedText(inputText);
            editText(rootView, inputText, colorCode1);
        });
    }

    private void editText(View view, String inputText, int colorCode) {
        if (view.findViewById(R.id.story_added_text) != null && addedViews.get(viewHolderPostion).contains(view) && !TextUtils.isEmpty(inputText)) {
            ((TextView) view.findViewById(R.id.story_added_text)).setText(inputText);
            ((TextView) view.findViewById(R.id.story_added_text)).setTextColor(colorCode);
            textStickersParentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.get(viewHolderPostion).indexOf(view);
            if (i > -1) addedViews.get(viewHolderPostion).set(i, view);
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


    private void updateViewsBordersVisibilityExcept(@Nullable View keepView) {
        for (View view : addedViews.get(viewHolderPostion)) {
            if (view != keepView) {
                stickerBorder.setBackgroundResource(0);
                stickerBorder.setTag(false);
            }
        }
    }

    public void addEmoji(Typeface emojiTypeface, String emojiName) {
        FrameLayout rootView = new FrameLayout(getContext());
        stickerBorder = new FrameLayout(getContext());
        rootView.addView(stickerBorder, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.NO_GRAVITY, 8, 8, 8, 8));

        textTv = new TextView(getContext());
        textTv.setTextColor(Color.BLACK);
        textTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                18);
        textTv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        stickerBorder.addView(textTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));


        if (emojiTypeface != null) {
            textTv.setTypeface(emojiTypeface);
        }
        textTv.setTextSize(56);
        textTv.setText(emojiName);


        onEditActions.addEmoji(rootView);
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
                    emojiPadding = layoutParams.height;
                    rootView.requestLayout();
                }
            }
        }
        if (lastSizeChangeValue1 == keyboardSize && lastSizeChangeValue2 == land) {
            return;
        }
        lastSizeChangeValue1 = keyboardSize;
        lastSizeChangeValue2 = land;

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
        if (addedViews.get(viewHolderPostion) == null) {
            List<View> views = new ArrayList<>();
            views.add(brushDrawingView);
            addedViews.put(viewHolderPostion, views);
        } else {
            addedViews.get(viewHolderPostion).add(brushDrawingView);
        }

        revertTextView.setVisibility(VISIBLE);
        onPaintChanged.setValue(addedViews.size());
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (addedViews.get(viewHolderPostion).size() > 0) {
            View removeView = addedViews.get(viewHolderPostion).remove(addedViews.get(viewHolderPostion).size() - 1);
            if (!(removeView instanceof BrushDrawingView)) {
                textStickersParentView.removeView(removeView);
            }
            onPaintChanged.setValue(addedViews.size());
            redoViews.add(removeView);
            if (addedViews.get(viewHolderPostion).size() == 0) {
                mode = StoryModes.NONE;
                modes.put(viewHolderPostion, StoryModes.NONE);
            }
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

    public interface OnEditActions {
        void emoji();

        void paint();

        void addText(View view, String addedText, int colorCode);

        void addEmoji(View view);

        void filter();
    }


    private class AdapterViewPager extends PagerAdapter implements PhotoViewer.OnEditActions, OnPhotoEditorListener {

        ArrayList<StructBottomSheet> itemGalleryList;

        public AdapterViewPager(ArrayList<StructBottomSheet> itemGalleryList) {
            this.itemGalleryList = itemGalleryList;
            onEditActions = this;
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
            zoomLayout = new ZoomLayout(context);
            textStickersParentView = new TextStickerView(context, false);
            textStickersParentView.setDrawingCacheEnabled(true);
            textStickersParentView.setId(R.id.textstickerView);
            zoomLayout.addView(textStickersParentView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

            if (itemGalleryList.get(position).path != null && !viewHolders.containsKey(position)) {
                String oldPath = itemGalleryList.get(position).path;
                String finalPath = attachFile.saveGalleryPicToLocal(oldPath);
                //check if old path available in selected list , replace new path with that
                if (!oldPath.equals(finalPath)) {
                    StructBottomSheet item = textImageList.get(oldPath);
                    if (item != null) {
                        textImageList.remove(oldPath);
                        textImageList.put(finalPath, item);
                    }
                }
                itemGalleryList.get(position).path = finalPath;
                textStickersParentView.setPaintMode(false, finalPath);

            }

            if (!viewHolders.containsKey(position)) {
                viewHolders.put(position, zoomLayout);
                Palette.from(BitmapFactory.decodeFile(itemGalleryList.get(position).getPath())).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {
                        GradientDrawable gd = new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                new int[]{
                                        palette.getLightMutedColor(0xFF616261), palette.getMutedColor(0xFF616261), palette.getDarkMutedColor(0xFF616261)});

                        viewHolders.get(position).findViewById(R.id.textstickerView).setBackground(gd);
                    }
                });
                container.addView(zoomLayout, 0);
                List<View> views = new ArrayList<>();
                addedViews.put(position, views);
                return zoomLayout;

            } else {
                container.removeView(viewHolders.get(position));
                container.addView(viewHolders.get(position), 0);
                return viewHolders.get(position);
            }

        }

        @Override
        public void destroyItem(ViewGroup containe, int position, Object object) {

        }

        @Override
        public void emoji() {

        }

        @Override
        public void paint() {

        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void addText(View rootView, String addedText, int colorCode) {
            TextStickerView textStickerView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
            MultiTouchListener multiTouchListener = new MultiTouchListener(
                    null, viewPager,
                    textStickerView,
                    textStickerView.getBitmapHolderImageView(),
                    AdapterViewPager.this, getContext(), addedText, colorCode);

            multiTouchListener.setOnGestureControl(new OnGestureControl() {
                boolean isDownAlready = false;

                @Override
                public void onClick() {
                    showTextEditDialog(rootView, multiTouchListener);
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
        public void addEmoji(View view) {
            TextStickerView textStickerView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
            MultiTouchListener multiTouchListener = new MultiTouchListener(
                    null, viewPager,
                    textStickerView,
                    textStickerView.getBitmapHolderImageView(),
                    AdapterViewPager.this, getContext(), null, 0);

            multiTouchListener.setOnGestureControl(new OnGestureControl() {
                boolean isDownAlready = false;

                @Override
                public void onClick() {
                }

                @Override
                public void onDown() {
                    if (mode == StoryModes.PAINT) {
                        viewPager.setPagingEnabled(false);
                    }
                }

                @Override
                public void onLongClick() {
                }
            });
            view.setOnTouchListener(multiTouchListener);
            addViewToParent(view);
        }

        @Override
        public void filter() {

        }

        private void updateViewsBordersVisibilityExcept(@Nullable View keepView) {
            for (View view : addedViews.get(viewHolderPostion)) {
                if (view != keepView) {
                    stickerBorder.setBackgroundResource(0);
                    stickerBorder.setTag(false);
                }
            }
        }


        private void addViewToParent(View view) {
            TextStickerView textStickerView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            textStickerView.setVisibility(View.VISIBLE);
            textStickerView.addView(view, params);
            if (addedViews.get(viewHolderPostion) == null) {
                List<View> views = new ArrayList<>();
                views.add(view);
                addedViews.put(viewHolderPostion, views);
            } else {
                addedViews.get(viewHolderPostion).add(view);
            }
            revertTextView.setVisibility(VISIBLE);
            updateViewsBordersVisibilityExcept(view);
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
    }

    public void openPhotoForEdit(List<GalleryItemModel> selectedPhotos, String message, boolean isSelected) {

        if (itemGalleryList == null) {
            itemGalleryList = new ArrayList<>();
        }
        for (GalleryItemModel items : selectedPhotos) {
            StructBottomSheet item = new StructBottomSheet();
            item.setId(itemGalleryList.size());
            item.setPath(items.getAddress());
            item.setText(message);
            item.isSelected = isSelected;
            itemGalleryList.add(0, item);
            textImageList.put(path, item);
        }
        itemGalleryList.addAll(0, FragmentEditImage.itemGalleryList);
        Collections.reverse(itemGalleryList);
        setUpViewPager();

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
                        textImageList.put(path, item);
                        setUpViewPager();
                    }
                });

            }
        });
    }


    public boolean undo() {
        if (addedViews.get(viewHolderPostion).size() > 0) {
            View removeView = addedViews.get(viewHolderPostion).get(addedViews.get(viewHolderPostion).size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return textStickersParentView.getmBrushDrawingView() != null && textStickersParentView.getmBrushDrawingView().undo();
            } else {
                addedViews.get(viewHolderPostion).remove(addedViews.get(viewHolderPostion).size() - 1);
                textStickersParentView.removeView(removeView);
                redoViews.add(removeView);
                onPaintChanged.setValue(addedViews.size());
                if (addedViews.get(viewHolderPostion).size() == 0) {
                    mode = StoryModes.NONE;
                    modes.put(viewHolderPostion, StoryModes.NONE);
                }
            }
        }
        return addedViews.get(viewHolderPostion).size() != 0;
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
//        viewPager.setOffscreenPageLimit(itemGalleryList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewHolderPostion = position;
                if (addedViews.get(viewHolderPostion).isEmpty()) {
                    paintTextView.setBackground(null);
                    revertTextView.setVisibility(View.GONE);
                } else {
                    revertTextView.setVisibility(VISIBLE);
                }
                if (textImageList.containsKey(itemGalleryList.get(position).path)) {
                    captionEditText.setText(EmojiManager.getInstance().replaceEmoji(textImageList.get(itemGalleryList.get(position).path).getText(), captionEditText.getPaint().getFontMetricsInt()));
                    if (!textImageList.get(itemGalleryList.get(position).path).getText().isEmpty()) {
                        captionEditText.setText(textImageList.get(itemGalleryList.get(position).path).getText());
                    }
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


    @Override
    public void onColorChanged(int colorCode) {
        brushColor = colorCode;
        paintTextView.setBackground(tintDrawable(getContext(), R.drawable.paint_circular_back, colorCode));
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
        TextStickerView textStickerView = viewHolders.get(viewHolderPostion).findViewById(R.id.textstickerView);
        textStickerView.setBrushColor(brushColor);
        textStickerView.setBrushSize(brushSize);
        textStickerView.setStrokeAlpha(brushAlpha);
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

    public Drawable tintDrawable(Context context, @DrawableRes int drawableRes, int colorCode) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable != null) {
            drawable.mutate();
            DrawableCompat.setTint(drawable, colorCode);
        }
        return drawable;
    }

    public void closeKeyboard() {
        captionEditText.clearFocus();
        AndroidUtils.hideKeyboard(captionEditText);
    }


}

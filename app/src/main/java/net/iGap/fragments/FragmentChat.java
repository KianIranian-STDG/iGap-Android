package net.iGap.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.ViewStubCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityTrimVideo;
import net.iGap.activities.CallActivity;
import net.iGap.adapter.AdapterDrBot;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.adapter.items.ItemBottomSheetForward;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.AnimatedStickerItem;
import net.iGap.adapter.items.chat.AudioItem;
import net.iGap.adapter.items.chat.BadgeView;
import net.iGap.adapter.items.chat.CardToCardItem;
import net.iGap.adapter.items.chat.ContactItem;
import net.iGap.adapter.items.chat.FileItem;
import net.iGap.adapter.items.chat.GifWithTextItem;
import net.iGap.adapter.items.chat.GiftStickerItem;
import net.iGap.adapter.items.chat.ImageWithTextItem;
import net.iGap.adapter.items.chat.LocationItem;
import net.iGap.adapter.items.chat.LogItem;
import net.iGap.adapter.items.chat.LogWallet;
import net.iGap.adapter.items.chat.LogWalletBill;
import net.iGap.adapter.items.chat.LogWalletCardToCard;
import net.iGap.adapter.items.chat.LogWalletTopup;
import net.iGap.adapter.items.chat.NewChatItemHolder;
import net.iGap.adapter.items.chat.ProgressWaiting;
import net.iGap.adapter.items.chat.StickerItem;
import net.iGap.adapter.items.chat.TextItem;
import net.iGap.adapter.items.chat.TimeItem;
import net.iGap.adapter.items.chat.UnreadMessage;
import net.iGap.adapter.items.chat.VideoWithTextItem;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.adapter.items.chat.VoiceItem;
import net.iGap.controllers.MessageController;
import net.iGap.fragments.chatMoneyTransfer.ParentChatMoneyTransferFragment;
import net.iGap.fragments.emoji.SuggestedStickerAdapter;
import net.iGap.fragments.emoji.add.FragmentSettingAddStickers;
import net.iGap.fragments.emoji.add.StickerDialogFragment;
import net.iGap.fragments.emoji.remove.StickerSettingFragment;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.giftStickers.buyStickerCompleted.BuyGiftStickerCompletedBottomSheet;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperGetMessageState;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.RoomObject;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.libs.MyWebViewClient;
import net.iGap.libs.Tuple;
import net.iGap.libs.emojiKeyboard.EmojiView;
import net.iGap.libs.emojiKeyboard.KeyboardView;
import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.model.PassCode;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.BotInit;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.CircleImageView;
import net.iGap.module.ContactUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.FileListerDialog.FileListerDialog;
import net.iGap.module.FileListerDialog.OnFileSelectedListener;
import net.iGap.module.FontIconTextView;
import net.iGap.module.IntentRequests;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MessageLoader;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyLinearLayoutManager;
import net.iGap.module.ResendMessage;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.Theme;
import net.iGap.module.TimeUtils;
import net.iGap.module.VoiceRecord;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.customView.EventEditText;
import net.iGap.module.dialog.ChatAttachmentPopup;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Status;
import net.iGap.module.enums.Additional;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.ProgressState;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.structs.StructBottomSheetForward;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.module.structs.StructWebView;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.IDispatchTochEvent;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.observers.interfaces.IOnBackPressed;
import net.iGap.observers.interfaces.IResendMessage;
import net.iGap.observers.interfaces.ISendPosition;
import net.iGap.observers.interfaces.IUpdateLogItem;
import net.iGap.observers.interfaces.LocationListener;
import net.iGap.observers.interfaces.OnBotClick;
import net.iGap.observers.interfaces.OnChatDelete;
import net.iGap.observers.interfaces.OnChatEditMessageResponse;
import net.iGap.observers.interfaces.OnChatMessageRemove;
import net.iGap.observers.interfaces.OnChatMessageSelectionChanged;
import net.iGap.observers.interfaces.OnChatSendMessage;
import net.iGap.observers.interfaces.OnChatSendMessageResponse;
import net.iGap.observers.interfaces.OnClientGetRoomMessage;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.observers.interfaces.OnConnectionChangeStateChat;
import net.iGap.observers.interfaces.OnDeleteChatFinishActivity;
import net.iGap.observers.interfaces.OnForwardBottomSheet;
import net.iGap.observers.interfaces.OnGetFavoriteMenu;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnGroupAvatarResponse;
import net.iGap.observers.interfaces.OnHelperSetAction;
import net.iGap.observers.interfaces.OnLastSeenUpdateTiming;
import net.iGap.observers.interfaces.OnMessageReceive;
import net.iGap.observers.interfaces.OnSetAction;
import net.iGap.observers.interfaces.OnUpdateUserOrRoomInfo;
import net.iGap.observers.interfaces.OnUserContactsBlock;
import net.iGap.observers.interfaces.OnUserContactsUnBlock;
import net.iGap.observers.interfaces.OnUserInfoResponse;
import net.iGap.observers.interfaces.OnUserUpdateStatus;
import net.iGap.observers.interfaces.OnVoiceRecord;
import net.iGap.observers.interfaces.OpenBottomSheetItem;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoChannelGetMessagesStats;
import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.proto.ProtoClientRoomReport;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomAccess;
import net.iGap.realm.RealmRoomDraft;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageContact;
import net.iGap.realm.RealmRoomMessageLocation;
import net.iGap.realm.RealmStickerItem;
import net.iGap.realm.RealmString;
import net.iGap.realm.RealmUserInfo;
import net.iGap.repository.StickerRepository;
import net.iGap.request.RequestChannelUpdateDraft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestChatUpdateDraft;
import net.iGap.request.RequestClientGetFavoriteMenu;
import net.iGap.request.RequestClientGetRoomHistory;
import net.iGap.request.RequestClientGetRoomMessage;
import net.iGap.request.RequestClientJoinByUsername;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestClientRoomReport;
import net.iGap.request.RequestClientSubscribeToRoom;
import net.iGap.request.RequestClientUnsubscribeFromRoom;
import net.iGap.request.RequestGroupUpdateDraft;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;
import net.iGap.viewmodel.controllers.CallManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static net.iGap.G.context;
import static net.iGap.G.twoPaneMode;
import static net.iGap.R.id.ac_ll_parent;
import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;
import static net.iGap.module.AttachFile.getFilePathFromUri;
import static net.iGap.module.AttachFile.request_code_VIDEO_CAPTURED;
import static net.iGap.module.AttachFile.request_code_pic_audi;
import static net.iGap.module.AttachFile.request_code_pic_file;
import static net.iGap.module.MessageLoader.getLocalMessage;
import static net.iGap.module.enums.ProgressState.HIDE;
import static net.iGap.module.enums.ProgressState.SHOW;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageStatus.LISTENED_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageStatus.SEEN_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.AUDIO_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.AUDIO_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.FILE_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.FILE_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.GIF_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.LOCATION_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.LOG_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.STICKER_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.VOICE_VALUE;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.WALLET_VALUE;
import static net.iGap.realm.RealmRoomMessage.makeSeenAllMessageOfRoom;
import static net.iGap.realm.RealmRoomMessage.makeUnreadMessage;

public class FragmentChat extends BaseFragment
        implements IMessageItem, OnChatSendMessageResponse, OnChatMessageSelectionChanged<AbstractMessage>, OnChatMessageRemove, OnVoiceRecord,
        OnUserInfoResponse, OnSetAction, OnUserUpdateStatus, OnLastSeenUpdateTiming, OnGroupAvatarResponse, OnChatDelete, LocationListener,
        OnConnectionChangeStateChat, OnBotClick, ToolbarListener, ChatAttachmentPopup.ChatPopupListener, EventManager.EventDelegate {

    // TODO: 12/28/20 refactor
    @Deprecated
    public static IUpdateLogItem iUpdateLogItem;


    public static OnComplete onMusicListener;
    public static OnForwardBottomSheet onForwardBottomSheet;
    public static OnComplete hashListener;
    public static OnComplete onComplete;
    public static OnUpdateUserOrRoomInfo onUpdateUserOrRoomInfo;
    public static ArrayList<Long> resentedMessageId = new ArrayList<>();
    public static int forwardMessageCount = 0;
    public static ArrayList<MessageObject> mForwardMessages;
    public static boolean canClearForwardList = true;
    public static boolean isInSelectionMode = false;
    public static boolean canUpdateAfterDownload = false;
    public static String titleStatic;
    public static long messageId;
    public static long mRoomIdStatic = 0;
    public static long lastChatRoomId = 0;
    public static ArrayList<String> listPathString;
    private static List<StructBottomSheet> contacts;
    private boolean isPaused;

    public static StructIGSticker structIGSticker;

    /**
     * *************************** common method ***************************
     */

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private final int END_CHAT_LIMIT = 5;
    public Runnable gongingRunnable;
    public Handler gongingHandler;
    public MusicPlayer musicPlayer;
    public String title;
    public String phoneNumber;
    public long mRoomId = 0;
    public CardView cardFloatingTime;
    public TextView txtFloatingTime;
    public boolean rcTouchListener;
    BotInit botInit;
    boolean isAnimateStart = false;
    boolean isScrollEnd = false;
    private boolean isShareOk = true;
    private boolean isReply = false;
    private boolean swipeBack = false;
    private AttachFile attachFile;
    private EventEditText edtSearchMessage;
    private SharedPreferences sharedPreferences;
    private SharedPreferences emojiSharedPreferences;
    private EventEditText edtChat;
    private ProgressBar editTextProgress;
    private FrameLayout chatRoom_send_container;
    private MaterialDesignTextView imvSendButton;
    private MaterialDesignTextView imvAttachFileButton;
    private MaterialDesignTextView imvMicButton;
    private MaterialDesignTextView sendMoney;
    //  private MaterialDesignTextView btnReplaySelected;
    private MaterialDesignTextView btnCancelSendingFile;
    private CircleImageView imvUserPicture;
    private TextView txtVerifyRoomIcon;
    private ImageView imgBackGround;
    private RecyclerView recyclerView;
    private RealmRoom managedRoom;

    private WebView webViewChatPage;
    private boolean isStopBot;
    private String urlWebViewForSpecialUrlChat;
    private RelativeLayout rootWebView;
    private ProgressBar progressWebView;
    private MaterialDesignTextView imvSmileButton;
    private LocationManager locationManager;
    private OnComplete complete;
    private View viewAttachFile;
    private View viewMicRecorder;
    private VoiceRecord voiceRecord;
    private MaterialDesignTextView btnHashLayoutClose;
    private SearchHash searchHash;
    private MessagesAdapter<AbstractMessage> mAdapter;
    private ProtoGlobal.Room.Type chatType;
    private GroupChatRole groupRole;
    private ChannelChatRole channelRole;
    private MaterialDialog dialogWait;
    private Uri latestUri;
    private final Calendar lastDateCalendar = Calendar.getInstance();
    private TextView iconMute;
    private LinearLayout ll_Search;
    private LinearLayout layoutAttachBottom;
    private LinearLayout ll_attach_text;
    private ConstraintLayout ll_AppBarSelected;
    private MaterialDesignTextView mBtnCopySelected, mBtnForwardSelected, mBtnReplySelected, mBtnDeleteSelected;
    private TextView mTxtSelectedCounter;
    // private LinearLayout ll_navigate_Message;
    private LinearLayout ll_navigateHash;
    private LinearLayout mReplayLayout;
    private LinearLayout pinedMessageLayout;
    private ProgressBar prgWaiting;
    //  private AVLoadingIndicatorView avi;
    private ViewGroup vgSpamUser;
    private RecyclerView.OnScrollListener scrollListener;
    private RecyclerView rcvDrBot;
    private FrameLayout llScrollNavigate;
    private FastItemAdapter fastItemAdapterForward;
    private BottomSheetDialog bottomSheetDialogForward;
    private View viewBottomSheetForward;
    private boolean showVoteChannel = true;
    private RealmResults<RealmRoom> results = null;
    private RealmResults<RealmContacts> resultsContact = null;
    private TextView txtSpamUser;
    private TextView txtSpamClose;
    private BadgeView txtNewUnreadMessage;
    private TextView imvCancelForward;
    private TextView btnUp;
    private TextView btnDown;
    private TextView txtChannelMute;
    private TextView iconChannelMute;
    private TextView btnUpHash;
    private TextView btnDownHash;
    private TextView txtHashCounter;
    private TextView txtFileNameForSend;
    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txtEmptyMessages;
    private String userName = "";
    private String mainVideoPath = "";
    private String color;
    private String initialize;
    private String groupParticipantsCountLabel;
    private String channelParticipantsCountLabel;
    private String userStatus;
    private Boolean isGoingFromUserLink = false;
    private Boolean isNotJoin = false; // this value will be trued when come to this chat with username
    private final boolean firsInitScrollPosition = false;
    private boolean initHash = false;
    private boolean hasDraft = false;
    private boolean hasForward = false;
    private boolean blockUser = false;
    private boolean isChatReadOnly = false;
    private boolean isMuteNotification;
    private boolean sendByEnter = false;
    private boolean isShowLayoutUnreadMessage = false;
    private boolean isCloudRoom;
    private boolean isEditMessage = false;
    private boolean isBot = false;
    private long biggestMessageId = 0;
    private long lastMessageId = 0;
    private long replyToMessageId = 0;
    private long userId;
    private boolean isShowStartButton = false;
    private long lastSeen;
    private long chatPeerId;
    private long userTime;
    private long savedScrollMessageId;
    private long latestButtonClickTime; // use from this field for avoid from show button again after click it
    private int countNewMessage = 0;
    private int unreadCount = 0;
    private int latestRequestCode;
    private final boolean isEmojiSHow = false;
    private boolean isPublicGroup = false;
    private boolean roomIsPublic;
    private ArrayList<Long> bothDeleteMessageId;
    private ArrayList<Long> messageIds;
    private ViewGroup layoutMute;
    private String report = "";
    private FrameLayout rootView;
    private boolean isAllSenderId = true;
    private ArrayList<Long> multiForwardList = new ArrayList<>();
    private final ArrayList<StructBottomSheetForward> mListForwardNotExict = new ArrayList<>();

    /**
     * **********************************************************************
     * *************************** Message Loader ***************************
     * **********************************************************************
     */

    private boolean addToView; // allow to message for add to recycler view or no
    private boolean topMore = true; // more message exist in local for load in up direction (topMore default value is true for allowing that try load top message )
    private boolean bottomMore; // more message exist in local for load in bottom direction
    private boolean isWaitingForHistoryUp; // client send request for getHistory, avoid for send request again
    private boolean isWaitingForHistoryDown; // client send request for getHistory, avoid for send request again
    private boolean allowGetHistoryUp = true; // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private boolean allowGetHistoryDown = true; // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))
    private boolean firstUp = true; // if is firstUp getClientRoomHistory with low limit in UP direction
    private boolean firstDown = true; // if is firstDown getClientRoomHistory with low limit in DOWN direction
    private String lastRandomRequestIdUp = ""; // last RandomRequestId Up
    private String lastRandomRequestIdDown = ""; // last RandomRequestId Down
    private long gapMessageIdUp; // messageId that maybe lost in local
    private long gapMessageIdDown; // messageId that maybe lost in local
    private long reachMessageIdUp; // messageId that will be checked after getHistory for detect reached to that or no
    private long reachMessageIdDown; // messageId that will be checked after getHistory for detect reached to that or no
    private long startFutureMessageIdUp; // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this changeState messageId for get history won't be detected.
    private long startFutureMessageIdDown; // for get history from local or online in next step use from this param, ( hint : don't use from adapter items, because maybe this item was deleted and in this changeState messageId for get history won't be detected.
    private long progressIdentifierUp = 0; // store identifier for Up progress item and use it if progress not removed from view after check 'instanceOf' in 'progressItem' method
    private long progressIdentifierDown = 0; // store identifier for Down progress item and use it if progress not removed from view after check 'instanceOf' in 'progressItem' method
    private int firstVisiblePosition; // difference between start of adapter item and items that Showing.
    private int firstVisiblePositionOffset; // amount of offset from top of view for first visible item in adapter
    private int visibleItemCount; // visible item in recycler view
    private int totalItemCount; // all item in recycler view
    private final int scrollEnd = 80; // (hint: It should be less than MessageLoader.LOCAL_LIMIT ) to determine the limits to get to the bottom or top of the list

    private HelperToolbar mHelperToolbar;
    private ViewGroup layoutToolbar;
    private boolean isPinAvailable = false;

    private SoundPool soundPool;
    private boolean soundInChatPlay = false;
    private boolean sendMessageLoaded;
    private boolean receiveMessageLoaded;
    private int sendMessageSound;
    private int receiveMessageSound;
    private final String TAG = "abbasiKeyboard";
    private ChatAttachmentPopup mAttachmentPopup;
    private int messageLentghCounter;
    private int oldMessageLentghCounter;

    @Nullable
    private RealmRoomAccess currentRoomAccess;
    private RealmObjectChangeListener<RealmRoomAccess> roomAccessChangeListener;

    public static boolean allowResendMessage(long messageId) {
        if (resentedMessageId == null) {
            resentedMessageId = new ArrayList<>();
        }

        if (resentedMessageId.contains(messageId)) {
            return false;
        }

        resentedMessageId.add(messageId);
        return true;
    }

    public static void removeResendList(long messageId) {
        FragmentChat.resentedMessageId.remove(messageId);
    }

    //    private EmojiView emojiView;
    private FrameLayout keyboardContainer;
    private KeyboardView keyboardView;
    private NotifyFrameLayout notifyFrameLayout;

    private int keyboardHeight = -1;
    private int keyboardHeightLand = -1;

    private boolean keyboardVisible;
    private boolean showKeyboardOnResume;
    private boolean keyboardViewVisible;

    private StickerRepository stickerRepository;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable = null;
    private RecyclerView suggestedRecyclerView;
    private SuggestedStickerAdapter suggestedAdapter;
    private FrameLayout suggestedLayout;
    private String lastChar;
    public static View.OnClickListener onLinkClick;

    /**
     * get images for show in bottom sheet
     */
    public static ArrayList<StructBottomSheet> getAllShownImagesPath(Activity activity) {
        ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<>();
        Uri uri;
        Cursor cursor;
        int column_index_data = 0, column_index_folder_name;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN
        };

        cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                StructBottomSheet item = new StructBottomSheet();
                item.setId(listOfAllImages.size());
                item.setPath(absolutePathOfImage);
                item.isSelected = true;
                listOfAllImages.add(0, item);
            }
            cursor.close();
        }
        return listOfAllImages;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.getInstance().loadRecentEmoji();
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;
        G.locationListener = this;

        StickerRepository.getInstance().getUserStickersGroup();

        notifyFrameLayout = new NotifyFrameLayout(context) {
            @Override
            public boolean dispatchKeyEventPreIme(KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (topFragmentIsChat() && keyboardViewVisible) {
                        showPopup(-1);
                        return true;
                    }
                    return false;
                }
                return super.dispatchKeyEventPreIme(event);
            }
        };

        Bundle extras = getArguments();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");

            if (mustCheckPermission())
                currentRoomAccess = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.where(RealmRoomAccess.class).equalTo("id", mRoomId + "_" + AccountManager.getInstance().getCurrentUser().getId()).findFirst();
                });
        }

        notifyFrameLayout.setListener(this::onScreenSizeChanged);

        rootView = (FrameLayout) inflater.inflate(R.layout.activity_chat, container, false);

        notifyFrameLayout.addView(rootView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        keyboardContainer = rootView.findViewById(R.id.fl_chat_keyboardContainer);

        sendMoney = rootView.findViewById(R.id.btn_chatRoom_wallet);

        /**
         * init chat box edit text and send item because we need change this color in dark mode!
         * */

        edtChat = rootView.findViewById(R.id.et_chatRoom_writeMessage);
        edtChat.setGravity(Gravity.CENTER_VERTICAL);

        imvSendButton = rootView.findViewById(R.id.btn_chatRoom_send);
        editTextProgress = rootView.findViewById(R.id.editTextProgress);
        chatRoom_send_container = rootView.findViewById(R.id.chatRoom_send_container);

        edtChat.setListener(this::chatMotionEvent);

        getEventManager().addObserver(EventManager.CALL_STATE_CHANGED, this);
        getEventManager().addObserver(EventManager.EMOJI_LOADED, this);
        getEventManager().addObserver(EventManager.ON_MESSAGE_DELETE, this);
        getEventManager().addObserver(EventManager.ON_EDIT_MESSAGE, this);
        getEventManager().addObserver(EventManager.ON_PINNED_MESSAGE, this);
        getEventManager().addObserver(EventManager.CHAT_CLEAR_MESSAGE, this);
        getEventManager().addObserver(EventManager.CHANNEL_ADD_VOTE, this);
        getEventManager().addObserver(EventManager.CHANNEL_GET_VOTE, this);
        getEventManager().addObserver(EventManager.CHANNEL_UPDATE_VOTE, this);
        getEventManager().addObserver(EventManager.CHANNEL_UPDATE_VOTE, this);
        getEventManager().addObserver(EventManager.CHAT_UPDATE_STATUS, this);
        if (twoPaneMode)
            EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.CHAT_BACKGROUND_CHANGED, this);

        return attachToSwipeBack(notifyFrameLayout);
    }

    public void exportChat() {


        RealmResults<RealmRoomMessage> realmRoomMessages = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).sort("createTime").findAll();
        });
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/iGap", "iGap Messages");

        if (!root.exists()) {
            root.mkdir();
        }


        FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(G.fragmentActivity);
        fileListerDialog.setDefaultDir(root);
        fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.DIRECTORY_ONLY);
        fileListerDialog.show();

        fileListerDialog.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                final MaterialDialog[] dialog = new MaterialDialog[1];
                if (realmRoomMessages.size() != 0 && chatType != CHANNEL) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog[0] = new MaterialDialog.Builder(G.currentActivity)
                                    .title(R.string.export_chat)
                                    .content(R.string.just_wait_en)
                                    .progress(false, realmRoomMessages.size(), true)
                                    .show();
                        }
                    });
                    try {
                        File filepath = new File(file, title + ".txt");
                        FileWriter writer = new FileWriter(filepath);

                        for (RealmRoomMessage export : realmRoomMessages) {

                            if (export.getMessageType().toString().equalsIgnoreCase("TEXT")) {

                                writer.append(RealmRegisteredInfo.getNameWithId(export.getUserId()) + "  text message " + "  :  " + export.getMessage() + "  date  :" + HelperCalander.milladyDate(export.getCreateTime()) + "\n");

                            } else {
                                writer.append(RealmRegisteredInfo.getNameWithId(export.getUserId()) + "  text message " + export.getMessage() + "  :  message in format " + export.getMessageType() + "  date  :" + HelperCalander.milladyDate(export.getCreateTime()) + "\n");
                            }
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog[0].incrementProgress(1);
                                }
                            });

                        }
                        writer.flush();
                        writer.close();
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog[0].dismiss();
                            }
                        }, 500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imvSendButton = rootView.findViewById(R.id.btn_chatRoom_send);

        cardFloatingTime = rootView.findViewById(R.id.cardFloatingTime);
        txtFloatingTime = rootView.findViewById(R.id.txtFloatingTime);
        txtChannelMute = rootView.findViewById(R.id.chl_txt_mute_channel);
        iconChannelMute = rootView.findViewById(R.id.chl_icon_mute_channel);
        layoutMute = rootView.findViewById(R.id.chl_ll_channel_footer);

        gongingRunnable = new Runnable() {
            @Override
            public void run() {
                cardFloatingTime.setVisibility(View.GONE);
            }
        };
        gongingHandler = new Handler(Looper.getMainLooper());

        startPageFastInitialize();

        if (getActivity() != null && !getActivity().isFinishing() && isAdded()) {
            initMain();
        }

        if ((chatType == CHAT) && !isCloudRoom && !isBot) {
            sendMoney.setVisibility(View.VISIBLE);
        }

        if (currentRoomAccess != null && getRoom() != null) {
            checkRoomAccess(currentRoomAccess);

            roomAccessChangeListener = (realmRoomAccess, changeSet) -> checkRoomAccess(realmRoomAccess);

            currentRoomAccess.addChangeListener(roomAccessChangeListener);
        }

        setupIntentReceiverForGetDataInTwoPanMode();


    }

    private void checkRoomAccess(RealmRoomAccess realmRoomAccess) {
        if (isNotJoin)
            return;

        if (realmRoomAccess != null && realmRoomAccess.isValid() && getRoom() != null) {
            if (chatType == CHANNEL) {
                if (realmRoomAccess.isCanPostMessage()) {
                    rootView.findViewById(R.id.layout_attach_file).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.tv_chat_sendMessagePermission).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.layout_attach_file).setVisibility(View.GONE);

                    if (currentRoleIsOwnerOrAdmin())
                        rootView.findViewById(R.id.tv_chat_sendMessagePermission).setVisibility(View.VISIBLE);
                    else
                        rootView.findViewById(R.id.tv_chat_sendMessagePermission).setVisibility(View.GONE);
                }

                if (chatType == CHANNEL) {
                    if (currentRoleIsOwnerOrAdmin())
                        layoutMute.setVisibility(View.GONE);
                    else
                        layoutMute.setVisibility(View.VISIBLE);
                }

                if (!realmRoomAccess.isCanEditMessage() || !realmRoomAccess.isCanPostMessage() && (rootView.findViewById(R.id.replayLayoutAboveEditText) != null && rootView.findViewById(R.id.replayLayoutAboveEditText).getVisibility() == View.VISIBLE)) {
                    if (keyboardViewVisible)
                        hideKeyboard();

                    edtChat.setText("");

                    removeEditedMessage();
                }
            } else if (chatType == GROUP && realmRoomAccess.getRealmPostMessageRights() != null) {
                if (keyboardView != null)
                    keyboardView.setStickerPermission(realmRoomAccess.getRealmPostMessageRights().isCanSendSticker());

                if (mAttachmentPopup != null) {
                    mAttachmentPopup.setMediaPermission(realmRoomAccess.getRealmPostMessageRights().isCanSendMedia());
                }

                if (realmRoomAccess.getRealmPostMessageRights().isCanSendText()) {
                    rootView.findViewById(R.id.layout_attach_file).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.tv_chat_sendMessagePermission).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.layout_attach_file).setVisibility(View.GONE);
                    rootView.findViewById(R.id.tv_chat_sendMessagePermission).setVisibility(View.VISIBLE);

                    if (keyboardViewVisible)
                        hideKeyboard();

                    edtChat.setText("");
                }

            }

        }
    }

    public long getRoomId() {
        return mRoomId;
    }

    private boolean currentRoleIsOwnerOrAdmin() {
        return getRoom() != null && getRoom().getType() != null && (getRoom().getType().equals(CHANNEL) || getRoom().getType().equals(GROUP) && getRoom().getType().equals(CHANNEL) ? getRoom().getChannelRoom().getRole().equals(ChannelChatRole.ADMIN) || getRoom().getChannelRoom().getRole().equals(ChannelChatRole.OWNER) : getRoom().getGroupRoom().getRole().equals(GroupChatRole.ADMIN) || getRoom().getGroupRoom().getRole().equals(GroupChatRole.OWNER));
    }

    private void setupIntentReceiverForGetDataInTwoPanMode() {
        //todo://fix chat fragment back stack and remove this code
        if (getActivity() instanceof ActivityMain) {
            ((ActivityMain) getActivity()).dataTransformer = (id, data) -> {
                if (id == AttachFile.request_code_trim_video) {
                    manageTrimVideoResult(data);
                }
            };
        }
    }

    private boolean mustCheckPermission() {
        if (getRoom().getType().equals(CHAT))
            return false;
        else
            return getRoom().getType().equals(CHANNEL) || getRoom().getType().equals(GROUP);
    }

    private void removeRoomAccessChangeListener() {
        if (currentRoomAccess != null && roomAccessChangeListener != null) {
            currentRoomAccess.removeChangeListener(roomAccessChangeListener);
            currentRoomAccess = null;
            roomAccessChangeListener = null;
        }
    }

    private void soundInChatInit() {
        if (soundInChatPlay) {
            try {
                if (soundPool == null) {
                    soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 0);
                }

                if (sendMessageSound == 0 && !sendMessageLoaded) {
                    sendMessageLoaded = true;
                    sendMessageSound = soundPool.load(getContext(), R.raw.send_message_sound, 1);
                }

                if (receiveMessageSound == 0 && !receiveMessageLoaded) {
                    receiveMessageLoaded = true;
                    receiveMessageSound = soundPool.load(getContext(), R.raw.receive_message_sound, 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isNotJoin) {
            makeSeenAllMessageOfRoom(mRoomId);
        }
    }

    @Override
    public void onResume() {
        isPaused = false;
        super.onResume();

        if (showKeyboardOnResume || (keyboardViewVisible && keyboardView != null && keyboardView.getCurrentMode() == KeyboardView.MODE_KEYBOARD)) {
            showPopup(KeyboardView.MODE_KEYBOARD);
            openKeyboardInternal();
        }

        if (FragmentShearedMedia.list != null && FragmentShearedMedia.list.size() > 0) {
            deleteSelectedMessageFromAdapter(FragmentShearedMedia.list);
            FragmentShearedMedia.list.clear();
        }
        canUpdateAfterDownload = true;

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                    return;
                }

                initLayoutHashNavigationCallback();
                showSpamBar();

                updateShowItemInScreen();

                if (isGoingFromUserLink) {
                    new RequestClientSubscribeToRoom().clientSubscribeToRoom(mRoomId);
                }

                DbManager.getInstance().doRealmTask(realm -> {
                    final RealmRoom room = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
                    if (room != null) {
                        if (G.connectionState == ConnectionState.CONNECTING || G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                            setConnectionText(G.connectionState);
                        } else {
                            if (room.getType() != CHAT) {
                                /**
                                 * set member count
                                 * set this code in onResume for update this value when user
                                 * come back from profile activities
                                 */

                                String members = null;
                                if (room.getType() == GROUP && room.getGroupRoom() != null) {
                                    members = room.getGroupRoom().getParticipantsCountLabel();
                                } else if (room.getType() == CHANNEL && room.getChannelRoom() != null) {
                                    members = room.getChannelRoom().getParticipantsCountLabel();
                                }

                                final String finalMembers = members;
                                if (finalMembers != null) {
                                    if (finalMembers != null && HelperString.isNumeric(finalMembers) && Integer.parseInt(finalMembers) == 1) {
                                        txtLastSeen.setText(finalMembers + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                                    } else {
                                        txtLastSeen.setText(finalMembers + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                                    }
                                    //    avi.setVisibility(View.GONE);

                                    if (HelperCalander.isPersianUnicode)
                                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                                }
                            } else {
                                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, room.getChatRoom().getPeerId());
                                if (realmRegisteredInfo != null) {
                                    setUserStatus(realmRegisteredInfo.getStatus(), realmRegisteredInfo.getLastSeen());
                                }
                            }
                        }
                    }
                    /**
                     * hint: should use from this method here because we need checkAction
                     * changeState after set members count for avoid from hide action if exist
                     */
                    checkAction();

                    if (room != null) {
                        if (txtName == null) {
                            txtName = mHelperToolbar.getTextViewChatUserName();
                        }
                        txtName.setText(EmojiManager.getInstance().replaceEmoji(room.getTitle(), txtName.getPaint().getFontMetricsInt()));
                        checkToolbarNameSize();
                    }
                });

                try {
                    mHelperToolbar.checkIsAvailableOnGoingCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Config.LOW_START_PAGE_TIME);

        mRoomIdStatic = mRoomId;
        lastChatRoomId = mRoomId;
        titleStatic = title;


        G.onUserInfoResponse = this;
        G.onSetAction = this;
        G.onUserUpdateStatus = this;
        G.onLastSeenUpdateTiming = this;
        G.onChatDelete = this;
        G.onConnectionChangeStateChat = this;
        HelperNotification.getInstance().cancelNotification();
        G.onBotClick = this;

        /*finishActivity = new FinishActivity() {
            @Override
            public void finishActivity() {
                // ActivityChat.this.finish();
                finishChat();
            }
        };*/

        initCallbacks();
        HelperNotification.getInstance().isChatRoomNow = true;

        onUpdateUserOrRoomInfo = messageId -> {

            if (messageId != null && messageId.length() > 0) {
                G.handler.post(() -> {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null) {
                        int start = linearLayoutManager.findFirstVisibleItemPosition();

                        if (start < 0) {
                            start = 0;
                        }

                        for (int i = start; i < mAdapter.getItemCount() && i < start + 15; i++) {
                            try {
                                MessageObject messageObject = mAdapter.getItem(i).messageObject;
                                if (messageObject != null && (String.valueOf(messageObject.id)).equals(messageId)) {
                                    mAdapter.notifyItemChanged(i);
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };

        if (isCloudRoom) {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.VISIBLE);
            mHelperToolbar.getCloudChatIcon().setImageResource(R.drawable.ic_cloud_space_blue);

            mHelperToolbar.getUserAvatarChat().setVisibility(View.GONE);
        } else {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.GONE);
            mHelperToolbar.getUserAvatarChat().setVisibility(View.VISIBLE);
            setAvatar();
        }

        if (mForwardMessages == null) {
            rootView.findViewById(R.id.ac_ll_forward).setVisibility(View.GONE);
        }
        RealmRoom realmRoom = getRoom();
        if (realmRoom != null) {

            isMuteNotification = realmRoom.getMute();
            if (!isBot) {
                txtChannelMute.setText(isMuteNotification ? R.string.unmute : R.string.mute);
                iconChannelMute.setText(isMuteNotification ? R.string.unmute_icon : R.string.mute_icon);
            }
            iconMute.setVisibility(isMuteNotification ? View.VISIBLE : View.GONE);

        }

        registerListener();

        //enable attachment popup camera if was visible
        if (mAttachmentPopup != null && mAttachmentPopup.isShowing) mAttachmentPopup.enableCamera();

        onLinkClick = v -> {
            if (keyboardViewVisible) {
                hideKeyboard();
            }
        };
    }

    private void checkToolbarNameSize() {

        int finalWidth = mHelperToolbar.getUserNameLayout().getMeasuredWidth();
        int verifyWidth = mHelperToolbar.getChatVerify().getMeasuredWidth();
        int muteWidth = mHelperToolbar.getChatMute().getMeasuredWidth();

        finalWidth = finalWidth - (muteWidth + verifyWidth);

        if (mHelperToolbar.getSecondRightButton().isShown()) {
            finalWidth = finalWidth - (LayoutCreator.getDimen(R.dimen.toolbar_icon_size) * 2);
        } else if (mHelperToolbar.getRightButton().isShown()) {
            finalWidth = finalWidth - LayoutCreator.getDimen(R.dimen.toolbar_icon_size);
        } else {
            finalWidth = finalWidth - LayoutCreator.getDimen(R.dimen.dp4);
        }

        txtName.setMaxWidth(finalWidth);

    }

    @Override
    public void onPause() {
        isPaused = true;
        showKeyboardOnResume = false;
        storingLastPosition();
        showPopup(-1);
        super.onPause();

        lastChatRoomId = 0;

        if (isGoingFromUserLink && isNotJoin) {
            new RequestClientUnsubscribeFromRoom().clientUnsubscribeFromRoom(mRoomId);
        }
        onMusicListener = null;
//        iUpdateLogItem = null;

        unRegisterListener();

        //disable attachment popup camera
        if (mAttachmentPopup != null && mAttachmentPopup.isShowing)
            mAttachmentPopup.disableCamera();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAttachmentPopup = null;
        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();

        getEventManager().removeObserver(EventManager.CALL_STATE_CHANGED, this);
        getEventManager().removeObserver(EventManager.EMOJI_LOADED, this);
        getEventManager().removeObserver(EventManager.ON_MESSAGE_DELETE, this);
        getEventManager().removeObserver(EventManager.ON_EDIT_MESSAGE, this);
        getEventManager().removeObserver(EventManager.ON_PINNED_MESSAGE, this);
        getEventManager().removeObserver(EventManager.CHAT_CLEAR_MESSAGE, this);
        getEventManager().removeObserver(EventManager.CHANNEL_ADD_VOTE, this);
        getEventManager().removeObserver(EventManager.CHANNEL_GET_VOTE, this);
        getEventManager().removeObserver(EventManager.CHANNEL_UPDATE_VOTE, this);
        getEventManager().removeObserver(EventManager.CHAT_UPDATE_STATUS, this);

        if (twoPaneMode)
            getEventManager().removeObserver(EventManager.CHAT_BACKGROUND_CHANGED, this);

        mHelperToolbar.unRegisterTimerBroadcast();

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }

        if (notifyFrameLayout != null)
            notifyFrameLayout.setListener(null);

        if (onLinkClick != null)
            onLinkClick = null;

        showKeyboardOnResume = false;

        if (keyboardView != null)
            keyboardView.onDestroyParentFragment();

        G.onSetAction = null;
        G.onUpdateUserStatusInChangePage = null;
        G.onUserUpdateStatus = null;

        removeRoomAccessChangeListener();
    }

    @Override
    public void onStop() {
        canUpdateAfterDownload = false;
        if (G.onChatSendMessage != null)
            G.onChatSendMessage = null;
        setDraft();
        HelperNotification.getInstance().isChatRoomNow = false;

        //if (isNotJoin) { // hint : commented this code, because when going to profile and return can't load message
        //
        //    /**
        //     * delete all  deleted row from database
        //     */
        //    RealmRoom.deleteRoom(mRoomId);
        //}


        // room id have to be set to default, otherwise I'm in the room always!

        //MusicPlayer.chatLayout = null;
        //ActivityCall.stripLayoutChat = null;

        if (isNotJoin) {
            getMessageDataStorage().deleteRoomAllMessage(mRoomId);
        }

        try {
            MusicPlayer.chatLayout = null;
            MusicPlayer.shearedMediaLayout = null;

            if (!CallManager.getInstance().isCallAlive() && MusicPlayer.mp != null && MusicPlayer.mainLayout != null) {
                MusicPlayer.initLayoutTripMusic(MusicPlayer.mainLayout);
                MusicPlayer.mainLayout.setVisibility(View.VISIBLE);
                MusicPlayer.playerStateChangeListener.postValue(false);
            }
        } catch (Exception ex) {

        }

        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        mRoomId = -1;

        if (webViewChatPage != null) closeWebViewForSpecialUrlChat(true);

        if (G.fragmentActivity instanceof ActivityMain) {
            ((ActivityMain) G.fragmentActivity).resume();
        }
   /*     if (G.locationListenerResponse != null)
            G.locationListenerResponse = null;*/

        if (G.locationListener != null)
            G.locationListener = null;


        if (notifyFrameLayout != null)
            notifyFrameLayout.setListener(null);

    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * If it's in the app and the screen lock is activated after receiving the result of the camera and .... The page code is displayed.
         * The wizard will  be set ActivityMain.isUseCamera = true to prevent the page from being opened....
         */
        if (PassCode.getInstance().isPassCode()) ActivityMain.isUseCamera = true;

        if (resultCode == RESULT_CANCELED) {
            HelperSetAction.sendCancel(messageId);

            hideProgress();
        }

        if (requestCode == AttachFile.request_code_position && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                attachFile.requestGetPosition(complete, FragmentChat.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (resultCode == Activity.RESULT_OK) {

            HelperSetAction.sendCancel(messageId);

            if (requestCode == AttachFile.request_code_contact_phone) {
                latestUri = data.getData();
                sendMessage(requestCode, "");
                return;
            }

            listPathString = null;
            if (AttachFile.request_code_TAKE_PICTURE == requestCode) {

                listPathString = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    listPathString.add(AttachFile.mCurrentPhotoPath);
                } else {
                    listPathString.add(AttachFile.imagePath);
                }

                latestUri = null; // check
            } else if (request_code_VIDEO_CAPTURED == requestCode) {

                listPathString = new ArrayList<>();
                listPathString.add(AttachFile.videoPath);

                latestUri = null; // check
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (data != null && data.getClipData() != null) { // multi select file
                        listPathString = AttachFile.getClipData(data.getClipData());

                        if (listPathString != null) {
                            for (int i = 0; i < listPathString.size(); i++) {
                                if (listPathString.get(i) != null) {
                                    listPathString.set(i, getFilePathFromUri(Uri.fromFile(new File(listPathString.get(i)))));
                                }
                            }
                        }
                    }
                }

                if (listPathString == null || listPathString.size() < 1) {
                    listPathString = new ArrayList<>();

                    if (data.getData() != null) {
                        listPathString.add(getFilePathFromUri(data.getData()));
                    }
                }
            }
            latestRequestCode = requestCode;

            if (requestCode == request_code_VIDEO_CAPTURED) {
                if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
                    Intent intent = new Intent(G.fragmentActivity, ActivityTrimVideo.class);
                    intent.putExtra("PATH", listPathString.get(0));
                    startActivityForResult(intent, AttachFile.request_code_trim_video);
                    return;
                } else {

                    listPathString = new ArrayList<>();
                    mainVideoPath = new File(AttachFile.videoPath).getPath();
                    listPathString.add(mainVideoPath);

                    showDraftLayout();
                    setDraftMessage(requestCode);
                    latestRequestCode = requestCode;
                    return;
                }
            }

            if (requestCode == AttachFile.request_code_trim_video) {
                manageTrimVideoResult(data);
                return;
            }

            if (listPathString.size() == 1) {
                if (requestCode == AttachFile.requestOpenGalleryForVideoMultipleSelect) {
                    boolean isGif = listPathString.get(0) != null && listPathString.get(0).toLowerCase().endsWith(".gif");
                    if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1 && !isGif) {
                        Intent intent = new Intent(G.fragmentActivity, ActivityTrimVideo.class);
                        intent.putExtra("PATH", listPathString.get(0));
                        startActivityForResult(intent, AttachFile.request_code_trim_video);
                        return;
                    } else {
                        mainVideoPath = listPathString.get(0);
                        showDraftLayout();
                        setDraftMessage(requestCode);
                    }
                }
            } else if (listPathString.size() > 1) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (final String path : listPathString) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !path.toLowerCase().endsWith(".gif")) {
                                        String localPathNew = attachFile.saveGalleryPicToLocal(path);
                                        sendMessage(requestCode, localPathNew);
                                    } else {
                                        sendMessage(requestCode, path);
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }

            if (listPathString.size() == 1 && listPathString.get(0) != null) {

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {

                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect) {
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {

                            if (FragmentEditImage.itemGalleryList == null) {
                                FragmentEditImage.itemGalleryList = new ArrayList<>();
                            }

                            FragmentEditImage.itemGalleryList.clear();
                            FragmentEditImage.textImageList.clear();
                            Uri uri = Uri.parse(listPathString.get(0));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(uri, HelperGetDataFromOtherApp.FileType.image), true);
                            } else {
                                FragmentEditImage.insertItemList(uri.toString(), true);
                            }
                            if (getActivity() != null) {
                                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, 0)).setReplace(false).load();
                            }
                            hideProgress();
                        } else {
                            //# get gif there

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        try {

                                            sendMessage(requestCode, listPathString.get(0));
                                            prgWaiting.setVisibility(View.GONE);
                                            visibilityTextEmptyMessages();

                                        } catch (Exception e) {
                                        }

                                    }
                                }
                            });
                        }
                    } else if (requestCode == AttachFile.request_code_TAKE_PICTURE) {

                        if (FragmentEditImage.itemGalleryList == null) {
                            FragmentEditImage.itemGalleryList = new ArrayList<>();
                        }

                        FragmentEditImage.itemGalleryList.clear();
                        FragmentEditImage.textImageList.clear();
                        ImageHelper.correctRotateImage(listPathString.get(0), true);
                        FragmentEditImage.insertItemList(listPathString.get(0), true);
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, 0)).setReplace(false).load();
                        }
                        hideProgress();
                    } else {
                        showDraftLayout();
                        setDraftMessage(requestCode);
                    }
                } else {

                    if (requestCode == AttachFile.request_code_TAKE_PICTURE) {

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ImageHelper.correctRotateImage(listPathString.get(0), true);
                                showDraftLayout();
                                setDraftMessage(requestCode);
                            }
                        });
                        thread.start();
                    } else if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !listPathString.get(0).toLowerCase().endsWith(".gif")) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                                showDraftLayout();
                                setDraftMessage(requestCode);
                            }
                        });
                        thread.start();
                    } else {
                        showDraftLayout();
                        setDraftMessage(requestCode);

                    }
                }
            }
        }
    }

    public void manageTrimVideoResult(Intent data) {
        if (data.getData() == null) return;
        latestRequestCode = request_code_VIDEO_CAPTURED;
        listPathString = new ArrayList<>();
        mainVideoPath = data.getData().getPath();
        listPathString.add(mainVideoPath);
        showDraftLayout();
        setDraftMessage(request_code_VIDEO_CAPTURED);
    }

    private void manageSelectedVideoResult(String path) {
        latestRequestCode = request_code_VIDEO_CAPTURED;
        listPathString = new ArrayList<>();
        mainVideoPath = path;
        listPathString.add(mainVideoPath);
        showDraftLayout();
        setDraftMessage(request_code_VIDEO_CAPTURED);
    }

    private RealmRoom getRoom() {
        if (managedRoom == null) {
            return managedRoom = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
            });
        } else
            return managedRoom;
    }

    private void invalidateViews() {
//        int childCount = recyclerView.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            recyclerView.getChildAt(i).invalidate();
//        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * set just important item to view in onCreate and load another objects in onResume
     * actions : set app color, load avatar, set background, set title, set status chat or member for group or channel
     */
    private void startPageFastInitialize() {
        Bundle extras = getArguments();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            isGoingFromUserLink = extras.getBoolean("GoingFromUserLink");
            isNotJoin = extras.getBoolean("ISNotJoin");
            userName = extras.getString("UserName");
            messageId = extras.getLong("MessageId");
            chatPeerId = extras.getLong("peerId");
        }

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(G.twoPaneMode ? R.string.close_icon : R.string.back_icon)
                .setRightIcons(R.string.more_icon, R.string.voice_call_icon)
                .setLogoShown(false)
                .setChatRoom(true)
                .setPlayerEnable(true)
                .setListener(this);

        layoutToolbar = rootView.findViewById(R.id.ac_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());
        mHelperToolbar.registerTimerBroadcast();


        attachFile = new AttachFile(G.fragmentActivity);


        RealmRoom realmRoom = getRoom();
        pageSettings();

        // avi = (AVLoadingIndicatorView)  rootView.findViewById(R.id.avi);
        txtName = mHelperToolbar.getTextViewChatUserName();
        txtLastSeen = mHelperToolbar.getTextViewChatSeenStatus();
        imvUserPicture = mHelperToolbar.getUserAvatarChat();
        txtVerifyRoomIcon = mHelperToolbar.getChatVerify();
        txtVerifyRoomIcon.setVisibility(View.GONE);

        //set layout direction to views

        //todo : set gravity right for arabic and persian
        if (!G.isAppRtl) {
            txtName.setGravity(Gravity.LEFT);
            txtLastSeen.setGravity(Gravity.LEFT);
        } else {
            txtName.setGravity(Gravity.LEFT);
            txtLastSeen.setGravity(Gravity.LEFT);
        }

        /**
         * need this info for load avatar
         */
        if (realmRoom != null) {
            chatType = realmRoom.getType();
            if (chatType == CHAT) {
                chatPeerId = realmRoom.getChatRoom().getPeerId();
                RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
                    return RealmRegisteredInfo.getRegistrationInfo(realm, chatPeerId);
                });
                if (realmRegisteredInfo != null) {
                    title = realmRegisteredInfo.getDisplayName();
                    lastSeen = realmRegisteredInfo.getLastSeen();
                    userStatus = realmRegisteredInfo.getStatus();
                    isBot = realmRegisteredInfo.isBot();

                    if (isBot) {

                        if (getMessagesCount() == 0) {
                            layoutMute.setVisibility(View.VISIBLE);
                            txtChannelMute.setText(R.string.start);
                            iconChannelMute.setText("");

                            View layoutAttach = rootView.findViewById(R.id.layout_attach_file);
                            layoutAttach.setVisibility(View.GONE);

                            layoutMute.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!isChatReadOnly) {
                                        edtChat.setText("/Start");
                                        imvSendButton.performClick();
                                        removeStartButton();
                                    }
                                }
                            });
                            isShowStartButton = true;
                        }

                    }

                    if (realmRegisteredInfo.isVerified()) {
                        txtVerifyRoomIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    /**
                     * when userStatus isn't EXACTLY lastSeen time not used so don't need
                     * this time and also this time not exist in room info
                     */
                    title = realmRoom.getTitle();
                    userStatus = G.fragmentActivity.getResources().getString(R.string.last_seen_recently);
                }
            } else {
                mRoomId = realmRoom.getId();
                title = realmRoom.getTitle();
                if (chatType == GROUP) {
                    groupParticipantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    isPublicGroup = roomIsPublic = !realmRoom.getGroupRoom().isPrivate();
                } else {
                    groupParticipantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    showVoteChannel = realmRoom.getChannelRoom().isReactionStatus();
                    if (realmRoom.getChannelRoom().isVerified()) {
                        txtVerifyRoomIcon.setVisibility(View.VISIBLE);
                    }
                    roomIsPublic = !realmRoom.getChannelRoom().isPrivate();
                }
            }

            if (chatType == CHAT) {
                setUserStatus(userStatus, lastSeen);
            } else if ((chatType == GROUP) || (chatType == CHANNEL)) {
                if (groupParticipantsCountLabel != null) {

                    if (HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                    } else {
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                    }
                    // avi.setVisibility(View.GONE);

                }
            }
        } else if (chatPeerId != 0) {
            /**
             * when user start new chat this block will be called
             */
            chatType = CHAT;
            RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
                return RealmRegisteredInfo.getRegistrationInfo(realm, chatPeerId);
            });
            title = realmRegisteredInfo.getDisplayName();
            lastSeen = realmRegisteredInfo.getLastSeen();
            userStatus = realmRegisteredInfo.getStatus();
            setUserStatus(userStatus, lastSeen);
        }

        if (title != null) {
            txtName.setText(EmojiManager.getInstance().replaceEmoji(title, txtName.getPaint().getFontMetricsInt()));
        }
        /**
         * change english number to persian number
         */
        if (HelperCalander.isPersianUnicode) {
            txtName.setText(EmojiManager.getInstance().replaceEmoji(txtName.getText().toString(), txtName.getPaint().getFontMetricsInt()));
            txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
        }

        /**
         * hint: don't check isCloudRoom with (( RealmRoom.isCloudRoom(mRoomId, realm); ))
         * because in first time room not exist in RealmRoom and value is false always.
         * so just need to check this value with chatPeerId
         */
        if (chatPeerId == AccountManager.getInstance().getCurrentUser().getId()) {
            isCloudRoom = true;
        }

        viewAttachFile = rootView.findViewById(R.id.layout_attach_file);
        iconMute = mHelperToolbar.getChatMute();
        iconMute.setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
        isMuteNotification = realmRoom.getMute();
        isChatReadOnly = realmRoom.getReadOnly();
        //gone video , voice button call then if status was ok visible them
        mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);

        if (isChatReadOnly) {
            viewAttachFile.setVisibility(View.GONE);
            (rootView.findViewById(R.id.chl_recycler_view_chat)).setPadding(0, 0, 0, 0);
        } else if (chatType == CHAT && AccountManager.getInstance().getCurrentUser().getId() != chatPeerId && !isBot) {
            // gone or visible view call
            RealmCallConfig callConfig = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmCallConfig.class).findFirst();
            });

            if (callConfig != null) {
                if (callConfig.isVoice_calling()) {
                    mHelperToolbar.getSecondRightButton().setVisibility(View.VISIBLE);

                } else {
                    mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);
                }

            } else {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        }
        manageExtraLayout();
    }

    private void removeStartButton() {
        try {
            if (isShowStartButton) {
                if (rootView != null) {
                    rootView.post(() -> {
                        rootView.findViewById(R.id.chl_ll_channel_footer).setVisibility(View.GONE);
                        if (webViewChatPage == null)
                            rootView.findViewById(R.id.layout_attach_file).setVisibility(View.VISIBLE);
                    });
                }
                isShowStartButton = false;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void goneCallButtons() {
        mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);
    }

    private long getMessagesCount() {
        return DbManager.getInstance().doRealmTask((DbManager.RealmTaskWithReturn<Integer>) realm -> {
            return realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).findAll().size();
        });
    }

    private void initDrBot() {
        llScrollNavigate = rootView.findViewById(R.id.ac_ll_scrool_navigate);
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) llScrollNavigate.getLayoutParams();
        param.bottomMargin = (int) getResources().getDimension(R.dimen.dp60);

        rcvDrBot = rootView.findViewById(R.id.rcvDrBot);
        rcvDrBot.setLayoutManager(new LinearLayoutManager(G.context, LinearLayoutManager.HORIZONTAL, false));
        rcvDrBot.setItemViewCacheSize(200);

        new RequestClientGetFavoriteMenu().clientGetFavoriteMenu(new OnGetFavoriteMenu() {
            @Override
            public void onGetList(List<ProtoGlobal.Favorite> favoriteList) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AdapterDrBot adapterDrBot = new AdapterDrBot(favoriteList, new AdapterDrBot.OnHandleDrBot() {
                            @Override
                            public void goToRoomBot(ProtoGlobal.Favorite favorite) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        HelperUrl.checkUsernameAndGoToRoom(getActivity(), favorite.getValue().replace("@", ""), HelperUrl.ChatEntry.chat);
                                    }
                                });
                            }

                            @Override
                            public void sendMessageBOt(ProtoGlobal.Favorite favorite) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isChatReadOnly) {
                                            if (favorite.getValue().equals("$financial")) {
                                                if (getActivity() != null) {
                                                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPayment.newInstance()).setReplace(false).load();
                                                    return;
                                                }
                                            }
                                            edtChat.setText(favorite.getValue());
                                            imvSendButton.performClick();
                                            scrollToEnd();
                                        }
                                    }
                                });
                            }
                        });
                        rcvDrBot.setAdapter(adapterDrBot);
                        rcvDrBot.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


    }

    private void checkConnection(String action) {
        if (action != null && !isBot) {
            txtLastSeen.setText(action);
        } else {

            if (chatType == CHAT) {
                if (isCloudRoom) {
                    txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                    goneCallButtons();
                } else if (isBot) {
                    txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.bot));
                } else {
                    if (userStatus != null) {
                        if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                            txtLastSeen.setText(LastSeenTimeUtil.computeTime(txtLastSeen.getContext(), chatPeerId, userTime, true, false));
                        } else {
                            txtLastSeen.setText(userStatus);
                        }
                    }
                }
            } else if (chatType == GROUP) {
                if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                } else {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                }

            } else if (chatType == CHANNEL) {
                if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                } else {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                }

            }
        }

        if (HelperCalander.isPersianUnicode) {
            txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
        }

    }

    private void setConnectionText(final ConnectionState connectionState) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                G.connectionState = connectionState;
                if (connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                    checkConnection(G.context.getResources().getString(R.string.waiting_for_network));
                } else if (connectionState == ConnectionState.CONNECTING) {
                    checkConnection(G.context.getResources().getString(R.string.connecting));
                } else if (connectionState == ConnectionState.UPDATING) {
                    checkConnection(null);
                } else if (connectionState == ConnectionState.IGAP) {
                    checkConnection(null);
                }
            }
        });
    }

    private void initMain() {
        HelperGetMessageState.clearMessageViews();
        initPinedMessage();

        viewMicRecorder = rootView.findViewById(R.id.layout_mic_recorde);
        prgWaiting = rootView.findViewById(R.id.chl_prgWaiting);
        AppUtils.setProgresColler(prgWaiting);
        voiceRecord = new VoiceRecord(G.fragmentActivity, viewMicRecorder, viewAttachFile, this);

        prgWaiting.setVisibility(View.VISIBLE);

        txtEmptyMessages = rootView.findViewById(R.id.empty_messages);

        if (isBot) {
            txtEmptyMessages.setText(G.fragmentActivity.getResources().getString(R.string.empty_text_dr_bot));
            txtChannelMute.setText(R.string.start);
            iconChannelMute.setText("");
        }

        lastDateCalendar.clear();

        locationManager = (LocationManager) G.fragmentActivity.getSystemService(LOCATION_SERVICE);

        /**
         * Hint: don't need to get info here. currently do this action in {{@link #startPageFastInitialize()}}
         Bundle extras = getArguments();
         if (extras != null) {
         mRoomId = extras.getLong("RoomId");
         isGoingFromUserLink = extras.getBoolean("GoingFromUserLink");
         isNotJoin = extras.getBoolean("ISNotJoin");
         userName = extras.getString("UserName");
         messageId = extras.getLong("MessageId");
         }
         */

        /**
         * get userId . use in chat set action.
         */

        RealmUserInfo realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });

        if (realmUserInfo == null) {
            //finish();
            finishChat();
            return;
        }
        userId = realmUserInfo.getUserId();

        managedRoom = getRoom();
        if (managedRoom != null) { // room exist

            title = managedRoom.getTitle();
            initialize = managedRoom.getInitials();
            color = managedRoom.getColor();
            isChatReadOnly = managedRoom.getReadOnly();
            unreadCount = managedRoom.getUnreadCount();
            savedScrollMessageId = managedRoom.getLastScrollPositionMessageId();
            firstVisiblePositionOffset = managedRoom.getLastScrollPositionOffset();

            if (messageId != 0) {
                savedScrollMessageId = messageId;
                firstVisiblePositionOffset = 0;
            }

            if (chatType == CHAT) {

                RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
                    return RealmRegisteredInfo.getRegistrationInfo(realm, chatPeerId);
                });
                if (realmRegisteredInfo != null) {
                    initialize = realmRegisteredInfo.getInitials();
                    color = realmRegisteredInfo.getColor();
                    phoneNumber = realmRegisteredInfo.getPhoneNumber();

                    if (realmRegisteredInfo.getId() == Config.drIgapPeerId) {
                        // if (realmRegisteredInfo.getUsername().equalsIgnoreCase("")) {
                        initDrBot();
                    }

                } else {
                    title = managedRoom.getTitle();
                    initialize = managedRoom.getInitials();
                    color = managedRoom.getColor();
                    userStatus = G.fragmentActivity.getResources().getString(R.string.last_seen_recently);
                }
            } else if (chatType == GROUP) {
                RealmGroupRoom realmGroupRoom = managedRoom.getGroupRoom();
                groupRole = realmGroupRoom.getRole();
                groupParticipantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
            } else if (chatType == CHANNEL) {
                RealmChannelRoom realmChannelRoom = managedRoom.getChannelRoom();
                channelRole = realmChannelRoom.getRole();
                channelParticipantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
            }
        } else {
            //chatPeerId = extras.getLong("peerId");
            chatType = CHAT;
            RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
                return RealmRegisteredInfo.getRegistrationInfo(realm, chatPeerId);
            });
            if (realmRegisteredInfo != null) {
                title = realmRegisteredInfo.getDisplayName();
                initialize = realmRegisteredInfo.getInitials();
                color = realmRegisteredInfo.getColor();
                lastSeen = realmRegisteredInfo.getLastSeen();
                userStatus = realmRegisteredInfo.getStatus();
            }
        }

        rootView.findViewById(R.id.ac_ll_selected_and_pin).setBackground(new Theme().tintDrawable(rootView.findViewById(R.id.ac_ll_selected_and_pin).getBackground(), getContext(), R.attr.rootBackgroundColor));

        initComponent();
        initAppbarSelected();
        getDraft();
        getUserInfo();
        insertShearedData();

        if (structIGSticker != null) {
            G.handler.postDelayed(() -> sendStickerAsMessage(structIGSticker), 1000);
        }

        RealmRoomMessage rm = null;
        RealmResults<RealmRoomMessage> result = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoomMessage.class).
                    equalTo("roomId", mRoomId).findAll();
        });
        if (result.size() > 0) {
            rm = result.last();
            if (rm != null && rm.getMessage() != null) {
                if (rm.getRealmAdditional() != null && (rm.getRealmAdditional().getAdditionalType() == Additional.WEB_VIEW.getAdditional())) {
                    String additionalData = rm.getRealmAdditional().getAdditionalData();
                    if (!additionalData.isEmpty()) openWebViewForSpecialUrlChat(additionalData);
                }
            }
        }


        FragmentShearedMedia.goToPositionFromShardMedia = new FragmentShearedMedia.GoToPositionFromShardMedia() {
            @Override
            public void goToPosition(Long messageId) {

                if (messageId != 0) {
                    savedScrollMessageId = messageId;
                    firstVisiblePositionOffset = 0;

                    if (goToPositionWithAnimation(savedScrollMessageId, 2000)) {
                        savedScrollMessageId = 0;
                    } else {
                        RealmRoomMessage rm = DbManager.getInstance().doRealmTask(realm -> {
                            return realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
                        });
                        rm = RealmRoomMessage.getFinalMessage(rm);
                        if (rm != null) {
                            resetMessagingValue();
                            savedScrollMessageId = FragmentChat.messageId = messageId;
                            firstVisiblePositionOffset = 0;
                            getMessages();
                        }
                    }
                }
            }
        };

        sendChatTracker();
    }

    private void sendChatTracker() {
        if (chatType == CHAT) {
            if (isBot) {
                HelperTracker.sendTracker(HelperTracker.TRACKER_BOT_VIEW);
            } else {
                HelperTracker.sendTracker(HelperTracker.TRACKER_CHAT_VIEW);
            }
        } else if (chatType == GROUP) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_GROUP_VIEW);
        } else if (chatType == CHANNEL) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANNEL_VIEW);
        }
    }

    /**
     * show join/mute layout if needed
     */
    private void manageExtraLayout() {
        if (isNotJoin) {
            final LinearLayout layoutJoin = rootView.findViewById(R.id.ac_ll_join);

            layoutJoin.setVisibility(View.VISIBLE);
            layoutMute.setVisibility(View.GONE);
            viewAttachFile.setVisibility(View.GONE);

            rootView.findViewById(R.id.tv_chat_sendMessagePermission).setVisibility(View.GONE);

            layoutJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HelperUrl.showIndeterminateProgressDialog(getActivity());

                    new RequestClientJoinByUsername().clientJoinByUsername(userName, new RequestClientJoinByUsername.OnClientJoinByUsername() {
                        @Override
                        public void onClientJoinByUsernameResponse() {
                            /**
                             * if user joined to this room set lastMessage for that
                             */
                            RealmRoom.setLastMessage(mRoomId);

                            isNotJoin = false;
                            HelperUrl.closeDialogWaiting();
                            RealmRoom.joinRoom(mRoomId);

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    layoutJoin.setVisibility(View.GONE);
                                    if (chatType == CHANNEL) {
                                        layoutMute.setVisibility(View.VISIBLE);
                                        initLayoutChannelFooter();
                                    }
                                    rootView.findViewById(ac_ll_parent).invalidate();


                                    if (chatType == GROUP) {
                                        viewAttachFile.setVisibility(View.VISIBLE);
                                        isChatReadOnly = false;
                                    }

                                }
                            });
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {
                            HelperUrl.dialogWaiting.dismiss();
                        }
                    });
                }
            });
        }
    }

    private void initPinedMessage() {
        final long pinMessageId = RealmRoom.hasPinedMessage(mRoomId);
        pinedMessageLayout = rootView.findViewById(R.id.ac_ll_strip_Pin);
        if (pinMessageId > 0) {
            RealmRoomMessage realmRoomMessage = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoomMessage.class).equalTo("messageId", pinMessageId).findFirst();
            });

            if (realmRoomMessage != null && realmRoomMessage.isValid() && !realmRoomMessage.isDeleted()) {
                realmRoomMessage = RealmRoomMessage.getFinalMessage(realmRoomMessage);
                isPinAvailable = true;
                pinedMessageLayout.setVisibility(View.VISIBLE);
                TextView txtPinMessage = rootView.findViewById(R.id.pl_txt_pined_Message);
                MaterialDesignTextView iconPinClose = rootView.findViewById(R.id.pl_btn_close);

                String text = realmRoomMessage.getMessage();
                if (text == null || text.length() == 0) {
                    text = AppUtils.conversionMessageType(realmRoomMessage.getMessageType());
                }
                txtPinMessage.setText(text);
                iconPinClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (channelRole == ChannelChatRole.MEMBER || groupRole == GroupChatRole.MEMBER || isNotJoin) {
                            RealmRoom.updatePinedMessageDeleted(mRoomId, false);
                            pinedMessageLayout.setVisibility(View.GONE);
                            isPinAvailable = false;
                        } else {
                            sendRequestPinMessage(0);
                        }
                    }
                });

                pinedMessageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!goToPositionWithAnimation(pinMessageId, 1000)) {
                            RealmRoomMessage rm = DbManager.getInstance().doRealmTask(realm -> {
                                return realm.where(RealmRoomMessage.class).equalTo("messageId", pinMessageId).findFirst();
                            });
                            rm = RealmRoomMessage.getFinalMessage(rm);
                            if (rm != null) {
                                resetMessagingValue();
                                savedScrollMessageId = pinMessageId;
                                firstVisiblePositionOffset = 0;
                                getMessages();
                            } else {
                                new RequestClientGetRoomMessage().clientGetRoomMessage(mRoomId, pinMessageId, new OnClientGetRoomMessage() {
                                    @Override
                                    public void onClientGetRoomMessageResponse(ProtoGlobal.RoomMessage message) {
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                DbManager.getInstance().doRealmTask(realm -> {
                                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                                        @Override
                                                        public void execute(Realm realm) {
                                                            RealmRoomMessage.setGapInTransaction(realm, messageId);
                                                        }
                                                    }, () -> {
                                                        resetMessagingValue();
                                                        savedScrollMessageId = pinMessageId;
                                                        firstVisiblePositionOffset = 0;
                                                        getMessages();
                                                    });
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int majorCode, int minorCode) {

                                    }
                                });
                            }
                        }
                    }
                });

            } else {
                pinedMessageLayout.setVisibility(View.GONE);
            }
        } else {
            pinedMessageLayout.setVisibility(View.GONE);
        }
    }

    private void sendRequestPinMessage(final long id) {
        if (id == 0) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.igap)
                    .content(String.format(context.getString(R.string.pin_messages_content), context.getString(R.string.unpin)))
                    .positiveText(R.string.this_page)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (pinedMessageLayout != null) {
                                pinedMessageLayout.setVisibility(View.GONE);
                            }
                            RealmRoom.updatePinedMessageDeleted(mRoomId, false);
                            isPinAvailable = false;
                        }
                    }).negativeText(R.string.cancel);

            if (currentRoomAccess != null && currentRoomAccess.isCanPinMessage()) {
                builder.neutralText(R.string.all_member)
                        .onNeutral((dialog, which) -> {
                            getMessageController().pinMessage(mRoomId, id, chatType.getNumber());
                            isPinAvailable = false;
                        });
            }

            builder.show();

        } else {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.igap)
                    .content(String.format(context.getString(R.string.pin_messages_content), context.getString(R.string.PIN)))
                    .positiveText(R.string.ok).
                    onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            getMessageController().pinMessage(mRoomId, id, chatType.getNumber());
                        }
                    }).negativeText(R.string.cancel).show();
        }

    }

    private boolean goToPositionWithAnimation(long messageId, int time) {

        int position = mAdapter.findPositionByMessageId(messageId);
        if (position != -1) {
            LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
            linearLayout.scrollToPositionWithOffset(position, 0);
            shouldLoadMessage = true;

            mAdapter.getItem(position).messageObject.isSelected = true;
            mAdapter.notifyItemChanged(position);

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        int position = mAdapter.findPositionByMessageId(messageId);
                        if (position != -1) {
                            mAdapter.getItem(position).messageObject.isSelected = false;
                            mAdapter.notifyItemChanged(position);
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }, time);

            return true;
        }
        return false;
    }

    private void registerListener() {

        G.dispatchTochEventChat = new IDispatchTochEvent() {
            @Override
            public void getToch(MotionEvent event) {
                if (voiceRecord != null) {
                    voiceRecord.dispatchTouchEvent(event);
                }
            }
        };

        G.onBackPressedChat = new IOnBackPressed() {
            @Override
            public boolean onBack() {
                return onBackPressed();
            }
        };

        G.iSendPositionChat = new ISendPosition() {
            @Override
            public void send(Double latitude, Double longitude, String imagePath) {
            /*    if (isBot) {
                    if (G.locationListenerResponse != null)
                        G.locationListenerResponse.setLocationResponse(latitude, longitude);
                } else*/
                sendPosition(latitude, longitude, imagePath);
            }
        };
    }

    private void unRegisterListener() {

        G.dispatchTochEventChat = null;
        G.onBackPressedChat = null;
        G.iSendPositionChat = null;
    }

    public boolean onBackPressed() {
        if (mAttachmentPopup != null)
            mAttachmentPopup.directDismiss();

        boolean stopSuperPress = true;

        try {
            if (webViewChatPage != null) {
                closeWebViewForSpecialUrlChat(false);
                return true;
            }

            if (mAdapter != null && mAdapter.getSelections().size() > 0) {
                mAdapter.deselect();
            } else if (topFragmentIsChat() && keyboardView != null && keyboardViewVisible) {
                hideKeyboardView();
            } else if (ll_Search != null && ll_Search.isShown()) {
                goneSearchBox(edtSearchMessage);
            } else if (ll_navigateHash != null && ll_navigateHash.isShown()) {
                goneSearchHashFooter();
            } else if (isEditMessage) {
                removeEditedMessage();
            } else {
                stopSuperPress = false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stopSuperPress;
    }

    private void closeWebViewForSpecialUrlChat(boolean isStopBot) {

        if (webViewChatPage != null) {
            if (webViewChatPage.canGoBack() && (!webViewChatPage.getUrl().trim().toLowerCase().equals(urlWebViewForSpecialUrlChat.trim().toLowerCase())) && !isStopBot) {
                webViewChatPage.goBack();
            } else {
                makeWebViewGone();
                //        if (!isStopBot) popBackStackFragment();
            }
        }
    }

    private boolean topFragmentIsChat() {
        boolean topFragmentIsChat = false;
        if (getActivity() != null) {
            int i = getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1;
            String topFragmentName = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getName();

            if (topFragmentName != null)
                topFragmentIsChat = topFragmentName.equals(FragmentChat.class.getName());

            showKeyboardOnResume = keyboardViewVisible && keyboardView.getCurrentMode() == KeyboardView.MODE_KEYBOARD;
        }
        return topFragmentIsChat;
    }

    private void makeWebViewGone() {
        recyclerView.setVisibility(View.VISIBLE);
        viewAttachFile.setVisibility(View.VISIBLE);
        rootWebView.setVisibility(View.GONE);
        webViewChatPage = null;
    }

    /**
     * get settings changeState and change view
     */
    private void pageSettings() {
        /**
         * get sendByEnter action from setting value
         */
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        emojiSharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.EMOJI, MODE_PRIVATE);
        sendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0) == 1;

        soundInChatPlay = sharedPreferences.getInt(SHP_SETTING.KEY_PLAY_SOUND_IN_CHAT, 1) == 1;

        if (soundInChatPlay)
            soundInChatInit();

        /**
         * set background
         */

        keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, LayoutCreator.dp(280));
        keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, LayoutCreator.dp(280));

        recyclerView = rootView.findViewById(R.id.chl_recycler_view_chat);

        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        imgBackGround = rootView.findViewById(R.id.chl_img_view_chat);
        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                try {
                    Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                    imgBackGround.setImageDrawable(d);
                } catch (OutOfMemoryError e) {
                    ActivityManager activityManager = (ActivityManager) G.context.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    activityManager.getMemoryInfo(memoryInfo);
                }
            } else {
                try {
                    imgBackGround.setBackgroundColor(Color.parseColor(backGroundPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            /*Picasso.get().load(R.drawable.chat_default_background_pattern).fit().centerCrop().into(imgBackGround);*/
            imgBackGround.setImageResource(R.drawable.chat_default_background_pattern);
        }

    }

    /**
     * initialize some callbacks that used in this page
     */
    public void initCallbacks() {
        getSendMessageUtil().setOnChatSendMessageResponseChatPage(this);
        //  G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        G.onChatSendMessage = new OnChatSendMessage() {
            @Override
            public void Error(int majorCode, int minorCode, final int waitTime, long messageId) {
                if (majorCode == 234) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (G.fragmentActivity.hasWindowFocus()) {
                                    showErrorDialog(waitTime);
                                }
                            } catch (Exception e) {
                            }

                        }
                    });
                } else if (majorCode == 233 && minorCode == 1) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getContext() != null && mAdapter != null) {
                                mAdapter.removeMessage(messageId);
                            }
                        }
                    });
                }

            }
        };

        G.onChatEditMessageResponse = new OnChatEditMessageResponse() {
            @Override
            public void onChatEditMessage(long roomId, final long messageId, long messageVersion, final String message, ProtoResponse.Response response) {
                if (mRoomId == roomId && mAdapter != null) {
                    // I'm in the room
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // update message text in adapter
                            mAdapter.updateMessageText(messageId, message);
                        }
                    });
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        onMusicListener = new OnComplete() {
            @Override
            public void complete(boolean result, String messageID, String beforeMessageID) {

                if (result) {
                    updateShowItemInScreen();
                } else {
                    onPlayMusic(messageID);
                }
            }
        };

        iUpdateLogItem = new IUpdateLogItem() {// TODO: 12/29/20 MESSAGE_REFACTOR_NEED_TEST
            @Override
            public void onUpdate(byte[] log, long messageId) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter == null) {
                            return;
                        }
                        for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {

                            try {
                                AbstractMessage item = mAdapter.getAdapterItem(i);

                                if (item.messageObject != null && item.messageObject.id == messageId) {
                                    // bagi May crash
                                    item.messageObject.log.data = log;
                                    mAdapter.notifyAdapterItemChanged(i);
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                HelperLog.getInstance().setErrorLog(e);
                            }
                        }
                    }
                });
            }
        };

        /**
         * after get position from gps
         */
        complete = new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {
                try {
                    if (getActivity() != null) {
                        String[] split = messageOne.split(",");
                        Double latitude = Double.parseDouble(split[0]);
                        Double longitude = Double.parseDouble(split[1]);
                        FragmentMap fragment = FragmentMap.getInctance(latitude, longitude, FragmentMap.Mode.sendPosition);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                    }
                } catch (Exception e) {
                    HelperLog.getInstance().setErrorLog(e);
                }
            }
        };

        G.onHelperSetAction = new OnHelperSetAction() {
            @Override
            public void onAction(ProtoGlobal.ClientAction ClientAction) {
                HelperSetAction.setActionFiles(mRoomId, messageId, ClientAction, chatType);
            }
        };

        G.onDeleteChatFinishActivity = new OnDeleteChatFinishActivity() {
            @Override
            public void onFinish() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //finish();
                        finishChat();
                    }
                });
            }
        };

        G.onUpdateUserStatusInChangePage = (peerId, status, lastSeen) -> {
            if (chatType == CHAT) {
                setUserStatus(status, lastSeen);
                new RequestUserInfo().userInfo(peerId);
            }
        };
    }

    private RealmRoomMessage getFirstUnreadMessage() {
        if (managedRoom != null && managedRoom.isValid()) {
            return managedRoom.getFirstUnreadMessage();
        }
        return null;
    }

    private void initComponent() {

        iconMute = mHelperToolbar.getChatMute();

        final RealmRoom realmRoom = getRoom();

        ll_attach_text = rootView.findViewById(R.id.ac_ll_attach_text);

        txtFileNameForSend = rootView.findViewById(R.id.ac_txt_file_neme_for_sending);
        btnCancelSendingFile = rootView.findViewById(R.id.ac_btn_cancel_sending_file);
        btnCancelSendingFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_attach_text.setVisibility(View.GONE);
                edtChat.setFilters(new InputFilter[]{});
                edtChat.setText(EmojiManager.getInstance().replaceEmoji(edtChat.getText(), edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false));
                edtChat.setSelection(edtChat.getText().length());

                if (edtChat.getText().length() == 0) {
                    sendButtonVisibility(false);
                }
            }
        });

        // final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.2);

        imvSmileButton = rootView.findViewById(R.id.tv_chatRoom_emoji);
        edtChat.requestFocus();

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmojiSHow) {

                    imvSmileButton.performClick();
                }

                if (botInit != null) botInit.close();
            }
        });


        imvAttachFileButton = rootView.findViewById(R.id.vtn_chatRoom_attach);
        layoutAttachBottom = rootView.findViewById(R.id.ll_chatRoom_send);
        imvMicButton = rootView.findViewById(R.id.btn_chatRoom_mic);


        if (isBot) {
            botInit = new BotInit(rootView, false);
            sendButtonVisibility(false);

            RealmRoomMessage rm = null;
            String lastMessage = "";
            boolean backToMenu = true;

            RealmResults<RealmRoomMessage> result = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).notEqualTo("authorHash", RealmUserInfo.getCurrentUserAuthorHash()).findAll();
            });

            if (result.size() > 0) {
                rm = result.last();
                if (rm.getMessage() != null) {
                    lastMessage = rm.getMessage();
                }
            }

            try {
                if (rm.getRealmAdditional() != null && rm.getRealmAdditional().getAdditionalType() == AdditionalType.UNDER_KEYBOARD_BUTTON) {
                    botInit.updateCommandList(false, lastMessage, getActivity(), backToMenu, rm, rm.getRoomId());
                }

            } catch (Exception e) {
            }

        }


        mAdapter = new MessagesAdapter<>(managedRoom, this, this, this, avatarHandler, compositeDisposable, isCloudRoom);

        mAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<AbstractMessage>() {
            @Override
            public boolean filter(AbstractMessage item, CharSequence constraint) {
                return !item.messageObject.message.toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //FragmentMain.PreCachingLayoutManager layoutManager = new FragmentMain.PreCachingLayoutManager(ActivityChat.this, 7500);
        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(G.fragmentActivity);
        layoutManager.setStackFromEnd(true);

        if (recyclerView == null) {
            recyclerView = rootView.findViewById(R.id.chl_recycler_view_chat);
        }

        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemViewCacheSize(20);


        if (realmRoom != null && !realmRoom.getReadOnly()) {
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return true;
                }

                @Override
                public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    try {// TODO: 12/28/20  MESSAGE_REFACTOR
                        MessageObject message_ = (mAdapter.getItem(viewHolder.getAdapterPosition())).messageObject;
                        if (message_ != null && message_.status != ProtoGlobal.RoomMessageStatus.SENDING_VALUE && message_.status != ProtoGlobal.RoomMessageStatus.FAILED_VALUE) {
                            if (isReply)
                                reply((mAdapter.getItem(viewHolder.getAdapterPosition())).messageObject, false);
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                    isReply = false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                }

                @Override
                public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    if (actionState == ACTION_STATE_SWIPE && isCurrentlyActive) {
                        setTouchListener(recyclerView, dX);
                    }

                    dX = dX + ViewMaker.dpToPixel(25);
                    if (dX > 0)
                        dX = 0;

                    if (dX < -ViewMaker.dpToPixel(150)) {
                        dX = -ViewMaker.dpToPixel(150);
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                    return super.getSwipeThreshold(viewHolder);
                }

                @Override
                public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) { // TODO: 12/28/20 MESSAGE_REFACTOR
                    MessageObject message_ = null;
                    try {
                        message_ = (mAdapter.getItem(viewHolder.getAdapterPosition())).messageObject;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (message_ != null && (message_.status == ProtoGlobal.RoomMessageStatus.SENDING_VALUE || message_.status == ProtoGlobal.RoomMessageStatus.FAILED_VALUE)) {
                        return 0;
                    } else if (viewHolder instanceof VoiceItem.ViewHolder) {
                        return 0;
                    } else if (viewHolder instanceof AudioItem.ViewHolder) {
                        return 0;
                    } else if (viewHolder instanceof NewChatItemHolder) {
                        return super.getSwipeDirs(recyclerView, viewHolder);
                    }

                    // we disable swipe with returning Zero
                    return 0;
                }

                @Override
                public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                    if (swipeBack) {
                        swipeBack = false;
                        return 0;
                    }
                    return super.convertToAbsoluteDirection(flags, layoutDirection);
                }

                @Override
                public boolean isItemViewSwipeEnabled() {
                    return !FragmentChat.isInSelectionMode;
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        /**
         * load message , use handler for load async
         */

        visibilityTextEmptyMessages();

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                visibilityTextEmptyMessages();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                visibilityTextEmptyMessages();
            }
        });

        //added run time -> counter of un read messages
        llScrollNavigate = rootView.findViewById(R.id.ac_ll_scrool_navigate);
        txtNewUnreadMessage = new BadgeView(getContext());
        txtNewUnreadMessage.getTextView().setTypeface(ResourcesCompat.getFont(txtNewUnreadMessage.getContext(), R.font.main_font));
        txtNewUnreadMessage.getTextView().setSingleLine();
        txtNewUnreadMessage.getTextView().setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});//set max length
        txtNewUnreadMessage.setBadgeColor(new Theme().getPrimaryDarkColor(txtNewUnreadMessage.getContext()));
        llScrollNavigate.addView(txtNewUnreadMessage, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER | Gravity.TOP));

        G.handler.post(() -> {
            AndroidUtils.globalQueue.postRunnable(this::getMessages);
            manageForwardedMessage(false);
        });

        MaterialDesignTextView txtNavigationLayout = rootView.findViewById(R.id.ac_txt_down_navigation);

        llScrollNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAllRequestFetchHistory();
                //Todo : also needed: block future Request

                latestButtonClickTime = System.currentTimeMillis();
                /**
                 * have unread
                 */
                if (countNewMessage > 0 && getFirstUnreadMessage() != null) {
                    /**
                     * if unread message is exist in list set position to this item and create
                     * unread layout otherwise should clear list and load from unread again
                     */

                    if (!getFirstUnreadMessage().isValid() || getFirstUnreadMessage().isDeleted()) {
                        resetAndGetFromEnd();
                        return;
                    }

                    int position = mAdapter.findPositionByMessageId(getFirstUnreadMessage().getMessageId());
                    String m = getFirstUnreadMessage().getMessage();
                    if (position > 0) {
                        mAdapter.add(position, new UnreadMessage(mAdapter, FragmentChat.this).setMessage(MessageObject.create(makeUnreadMessage(countNewMessage))).withIdentifier(SUID.id().get()));
                        isShowLayoutUnreadMessage = true;
                        LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                        linearLayout.scrollToPositionWithOffset(position, 0);
                    } else {
                        resetMessagingValue();
                        unreadCount = countNewMessage;
                        getMessages();

                        if (getFirstUnreadMessage() == null) {
                            resetAndGetFromEnd();
                            return;
                        }

                        int position1 = mAdapter.findPositionByMessageId(getFirstUnreadMessage().getMessageId());
                        if (position1 > 0) {
                            LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                            linearLayout.scrollToPositionWithOffset(position1 - 1, 0);
                        }
                    }
                    setCountNewMessageZero();
                } else {
                    /**
                     * if addToView is true this means that all new message is in adapter
                     * and just need go to end position in list otherwise we should clear all
                     * items and reload again from bottom
                     */

                    if (!addToView) {
                        resetMessagingValue();
                        getMessages();
                    } else {
                        scrollToEnd();
                    }
                }
            }
        });


        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {

            @Override
            public boolean onFling(int velocityX, int velocityY) {

            /*    if (Math.abs(velocityY) > MAX_VELOCITY_Y) {
                    velocityY = MAX_VELOCITY_Y * (int) Math.signum((double)velocityY);
                    mRecyclerView.fling(velocityX, velocityY);
                    return true;
                }*/

                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int pastVisibleItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                cardFloatingTime.setVisibility(View.VISIBLE);
                long item = mAdapter.getItemByPosition(layoutManager.findFirstVisibleItemPosition());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(item);
                if (item != 0L) {
                    txtFloatingTime.setText(TimeUtils.getChatSettingsTimeAgo(G.fragmentActivity, calendar.getTime()));
                }
                gongingHandler.removeCallbacks(gongingRunnable);
                gongingHandler.postDelayed(gongingRunnable, 1000);

                if (totalItemCount - pastVisibleItems <= 2 && !isAnimateStart) {
                    isAnimateStart = true;
                    isAnimateStart = false;
                    setDownBtnGone();
//                    llScrollNavigate.animate()
//                            .alpha(0.0f)
//                            .translationY(llScrollNavigate.getHeight() / 2)
//                            .setDuration(200)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationEnd(animation);
//                                }
//                            });

                } else if (!isScrollEnd && !isAnimateStart) {
                    isAnimateStart = true;
                    setDownBtnVisible();
                    isAnimateStart = false;

//                    llScrollNavigate.animate()
//                            .alpha(1.0f)
//                            .translationY(0)
//                            .setDuration(200)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationEnd(animation);
//                                }
//                            });

                    txtNewUnreadMessage.getTextView().setText(countNewMessage + "");
                    if (countNewMessage == 0) {
                        txtNewUnreadMessage.setVisibility(View.GONE);

                    } else {

                        txtNewUnreadMessage.setVisibility(View.VISIBLE);
                    }

                }
            }
        });

 /*       if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //    event.addBatch(0,0,0,0,0,0);
            //            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
        } else if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

            card.setCardBackgroundColor(Color.parseColor("#20000000"));

        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            *//* Reset Color *//*
            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
            //  card.setOnClickListener(clickListener);

        }
        return false;
*/
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                return false;
            }
        });

        imvUserPicture = mHelperToolbar.getUserAvatarChat();
       /* imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });

        rootView.findViewById(R.id.ac_txt_cloud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });*/

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastChar = null;

                if (!addToView) {
                    resetAndGetFromEnd();
                }

                if (suggestedLayout != null) {
                    if (suggestedLayout.getVisibility() == View.VISIBLE)
                        suggestedLayout.setVisibility(View.GONE);
                    lastChar = null;
                }


                if (isShowLayoutUnreadMessage) {
                    removeLayoutUnreadMessage();
                }

                HelperSetAction.setCancel(mRoomId);

                clearDraftRequest();

                if (hasForward) {
                    manageForwardedMessage(false);

                    if (edtChat.getText().length() == 0) {
                        return;
                    }
                }
                if (ll_attach_text.getVisibility() == View.VISIBLE) {
                    if (listPathString.size() == 0) {
                        return;
                    }
                    sendMessage(latestRequestCode, listPathString.get(0));
                    listPathString.clear();
                    ll_attach_text.setVisibility(View.GONE);
                    edtChat.setFilters(new InputFilter[]{});
                    edtChat.setText("");

                    clearReplyView();
                    return;
                }

                /**
                 * if use click on edit message, the message's text will be put to the EditText
                 * i set the message object for that view's tag to obtain it here
                 * request message edit only if there is any changes to the message text
                 */

                if (edtChat.getTag() != null && edtChat.getTag() instanceof MessageObject) {
                    final MessageObject messageInfo = (MessageObject) edtChat.getTag();
                    final String message = getWrittenMessage();
                    if (!message.equals(messageInfo.message) && edtChat.getText().length() > 0) {

                        if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                            Toast.makeText(getContext(), getResources().getString(R.string.please_check_your_connenction), Toast.LENGTH_SHORT).show();
                            return;
                        }


                        chatRoom_send_container.setVisibility(View.VISIBLE);
                        imvSendButton.setVisibility(View.GONE);
                        editTextProgress.setVisibility(View.VISIBLE);

                        getMessageController().editMessage(messageInfo.id, mRoomId, message, chatType.getNumber());
                    } else {
                        removeEditedMessage();
                    }
                } else { // new message has written
                    sendNewMessage();
                    scrollToEnd();
                }

                oldMessageLentghCounter = 0;
                messageLentghCounter = 0;

            }
        });

        G.openBottomSheetItem = new OpenBottomSheetItem() {
            @Override
            public void openBottomSheet(boolean isNew) {
                if (mAttachmentPopup != null) mAttachmentPopup.setIsNewDialog(isNew);
                imvAttachFileButton.performClick();
                if (mAttachmentPopup != null) mAttachmentPopup.notifyRecyclerView();
            }

        };

        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {
                listPathString = null;
                listPathString = new ArrayList<>();

                if (textImageList.size() == 0) {
                    return;
                }

                ArrayList<StructBottomSheet> itemList = new ArrayList<StructBottomSheet>();
                for (Map.Entry<String, StructBottomSheet> items : textImageList.entrySet()) {
                    itemList.add(items.getValue());
                }

                Collections.sort(itemList);

                for (StructBottomSheet item : itemList) {
                    edtChat.setText(EmojiManager.getInstance().replaceEmoji(item.getText(), edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false));
                    listPathString.add(item.getPath());
                    latestRequestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
                    ll_attach_text.setVisibility(View.VISIBLE);
                    imvSendButton.performClick();
                }
            }
        };

        imvAttachFileButton.setOnClickListener(view -> {
            if (getActivity() == null) return;
            if (mAttachmentPopup == null) initPopupAttachment();
            mAttachmentPopup.setMessagesLayoutHeight(recyclerView.getMeasuredHeight());
            mAttachmentPopup.show();
        });

        sendMoney.setOnClickListener(view -> {
            if ((chatType == CHAT) && !isCloudRoom && !isBot) {
                showSelectItem();
            } else {
                showCardToCard();
            }

            if (keyboardViewVisible) {
                hideKeyboard();
            }
        });


        imvMicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (mustCheckPermission()) {
                    if (currentRoomAccess != null && !currentRoomAccess.getRealmPostMessageRights().isCanSendMedia()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.restrictions_on_sending_multimedia_messages), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                if (keyboardViewVisible) {
                    hideKeyboard();
                }

                if (ContextCompat.checkSelfPermission(G.fragmentActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    try {
                        HelperPermission.getMicroPhonePermission(G.fragmentActivity, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    voiceRecord.setItemTag("ivVoice");
                    // viewAttachFile.setVisibility(View.GONE);
                    viewMicRecorder.setVisibility(View.VISIBLE);

                    AppUtils.setVibrator(50);
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            voiceRecord.startVoiceRecord();
                        }
                    }, 60);
                }

                return true;
            }
        });


        // to toggle between keyboard and emoji popup
        imvSmileButton.setOnClickListener(v -> onEmojiButtonClick());

        edtChat.addTextChangedListener(new TextWatcher() {
            boolean processChange = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {

                if (text.length() > 0) {
                    HelperSetAction.setActionTyping(mRoomId, chatType);
                }

                // if in the seeting page send by enter is on message send by enter key
                if (text.toString().endsWith(System.getProperty("line.separator"))) {
                    if (sendByEnter) imvSendButton.performClick();
                }

                messageLentghCounter = ((int) Math.ceil((float) text.length() / (float) Config.MAX_TEXT_LENGTH));
                if (messageLentghCounter > 1 && messageLentghCounter != oldMessageLentghCounter && getContext() != null) {
                    oldMessageLentghCounter = messageLentghCounter;
                    Toast.makeText(getContext(), getString(R.string.message_is_long) + " " + messageLentghCounter + " " + getString(R.string.message), Toast.LENGTH_SHORT).show();
                }

                if ((count - before) > 1) {
                    processChange = true;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (ll_attach_text.getVisibility() == View.GONE && !hasForward) {

                    if (edtChat.getText() != null && edtChat.getText().length() > 0) {
                        sendButtonVisibility(true);
                    } else {
                        if (!isEditMessage) {
                            sendButtonVisibility(false);
                        }
                    }

                    if (processChange) {
                        ImageSpan[] spans = editable.getSpans(0, editable.length(), ImageSpan.class);

                        for (ImageSpan span : spans) {
                            editable.removeSpan(span);
                        }

                        EmojiManager.getInstance().replaceEmoji(editable, edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(20), false);
                        processChange = false;
                    }

                }

                if (edtChat.getText() != null && !EmojiManager.getInstance().isValidEmoji(edtChat.getText()) && suggestedLayout != null && suggestedLayout.getVisibility() == View.VISIBLE) {
                    suggestedLayout.setVisibility(View.GONE);
                    lastChar = null;
                } else if (lastChar != null) {
                    lastChar = null;
                }

            }
        });
    }

    private void onScreenSizeChanged(int height, boolean land) {

        if (height > LayoutCreator.dp(50) && keyboardVisible) {
            if (land) {
                keyboardHeightLand = height;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, keyboardHeightLand).apply();
            } else {
                keyboardHeight = height;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, keyboardHeight).apply();
            }
        }

        if (isPopupShowing()) {
            int newHeight = land ? keyboardHeightLand : keyboardHeight;

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) keyboardView.getLayoutParams();
            if (layoutParams.width != AndroidUtils.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtils.displaySize.x;
                layoutParams.height = newHeight;
                keyboardView.setLayoutParams(layoutParams);
            }
        }

        keyboardVisible = height > 0;

//        if (notifyFrameLayout != null) {
//            notifyFrameLayout.requestLayout();
//        }

        if (suggestedLayout != null && suggestedLayout.getVisibility() == View.VISIBLE) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) suggestedLayout.getLayoutParams();
            if (keyboardViewVisible)
                layoutParams.bottomMargin = keyboardHeight + LayoutCreator.dp(60);
            else
                layoutParams.bottomMargin = LayoutCreator.dp(60);
        }
    }

    private boolean isPopupShowing() {
        return keyboardViewVisible || keyboardView != null;
    }

    private void onEmojiButtonClick() {
        if (keyboardView == null)
            createKeyboardView();

        if (isPopupShowing() && keyboardView.getCurrentMode() != KeyboardView.MODE_KEYBOARD && keyboardView.getCurrentMode() != -1) {
            showPopup(KeyboardView.MODE_KEYBOARD);
            openKeyboardInternal();
        } else {
            AndroidUtils.hideKeyboard(edtChat);
            showPopup(KeyboardView.MODE_EMOJI);
        }
    }

    private void createKeyboardView() {
        if (getContext() != null) {
            keyboardView = new KeyboardView(getContext(), new KeyboardView.Listener() {
                @Override
                public void onViewCreated(int mode) {

                }

                @Override
                public void onStickerSettingClicked() {
                    if (getActivity() != null) {
                        showPopup(-1);
                        new HelperFragment(getActivity().getSupportFragmentManager(), new StickerSettingFragment()).setReplace(false).load();
                    }
                }

                @Override
                public void onBackSpace() {
                    if (edtChat.length() == 0) {
                        return;
                    }
                    edtChat.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }

                @Override
                public void onSendStickerAsMessage(StructIGSticker structIGSticker) {
                    sendStickerAsMessage(structIGSticker);
                }

                @Override
                public void onAddStickerClicked() {
                    if (getActivity() != null) {
                        showPopup(-1);
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentSettingAddStickers.newInstance()).setReplace(false).load();
                    }
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


                    if (EmojiManager.getInstance().isValidEmoji(unicode) && edtChat.getText().toString().equals(unicode)) {
                        getStickerByEmoji(unicode);
                    } else if (edtChat.getText() != null && !edtChat.getText().toString().equals("")) {
                        if (suggestedLayout != null && suggestedLayout.getVisibility() == View.VISIBLE) {
                            suggestedLayout.setVisibility(View.GONE);
                            lastChar = null;
                        }
                    }

                }

            }, KeyboardView.MODE_KEYBOARD);

            keyboardView.setVisibility(View.GONE);

            if (mustCheckPermission())
                keyboardView.setStickerPermission(currentRoomAccess != null && currentRoomAccess.getRealmPostMessageRights() != null && currentRoomAccess.getRealmPostMessageRights().isCanSendSticker());

            keyboardContainer.addView(keyboardView);
        }
    }

    private void showPopup(int mode) {

        if (mode == KeyboardView.MODE_EMOJI) {
            changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
        } else {
            changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
        }

        if (mode != -1) {
            keyboardViewVisible = true;
            if (keyboardView != null && keyboardView.getVisibility() == View.GONE)
                keyboardView.setVisibility(View.VISIBLE);
        }

        if (mode == KeyboardView.MODE_EMOJI) {
            if (keyboardView == null) {
                createKeyboardView();
            }

            if (keyboardView.getParent() == null)
                keyboardContainer.addView(keyboardView);

            keyboardVisible = false;

            if (keyboardHeight <= 0) {
                keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, 0);
            }

            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, 0);
            }

            int currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardHeight;
            keyboardView.setKeyboardHeight(keyboardHeightLand, keyboardHeight);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) keyboardView.getLayoutParams();
            layoutParams.height = currentHeight;
            keyboardView.setLayoutParams(layoutParams);

            keyboardView.setCurrentMode(KeyboardView.MODE_EMOJI, EmojiView.STICKER);

            keyboardView.setVisibility(View.VISIBLE);

        } else if (mode == KeyboardView.MODE_ATTACHMENT) {

        } else if (mode == KeyboardView.MODE_KEYBOARD) {

            if (keyboardView == null)
                createKeyboardView();

            if (keyboardView != null) {

                keyboardView.setCurrentMode(KeyboardView.MODE_KEYBOARD, -1);

                if (keyboardHeight <= 0) {
                    keyboardHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, 0);
                }

                if (keyboardHeightLand <= 0) {
                    keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, 0);
                }

                int currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardHeight;
                keyboardView.setKeyboardHeight(keyboardHeightLand, keyboardHeight);

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) keyboardView.getLayoutParams();
                layoutParams.height = currentHeight;
                keyboardView.setLayoutParams(layoutParams);

                keyboardView.setVisibility(View.VISIBLE);

                keyboardVisible = true;
            }
        } else {
            if (keyboardView != null)
                keyboardView.setCurrentMode(mode, -1);

            showKeyboardOnResume = false;

            closeKeyboard();
            G.handler.postDelayed(this::hideKeyboardView, 100);
        }
    }

    @Override
    protected void hideKeyboard() {
        showPopup(-1);
    }

    private void getStickerByEmoji(String unicode) {
        if (lastChar == null) {
            lastChar = unicode;

            if (suggestedLayout == null && getContext() != null) {
                stickerRepository = StickerRepository.getInstance();
                suggestedAdapter = new SuggestedStickerAdapter();
                compositeDisposable = new CompositeDisposable();

                suggestedLayout = new FrameLayout(getContext());

                suggestedRecyclerView = new RecyclerView(getContext());

                suggestedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                suggestedRecyclerView.setAdapter(suggestedAdapter);
                suggestedRecyclerView.setClipToPadding(false);
                suggestedRecyclerView.setPadding(LayoutCreator.dp(2), LayoutCreator.dp(3), LayoutCreator.dp(8), LayoutCreator.dp(2));
                suggestedAdapter.setListener(structIGSticker -> {
                    lastChar = null;
                    suggestedLayout.setVisibility(View.GONE);
                    suggestedAdapter.clearData();
                    suggestedRecyclerView.scrollToPosition(0);

                    if (disposable != null && !disposable.isDisposed())
                        disposable.dispose();

                    edtChat.setText("");
                    sendStickerAsMessage(structIGSticker);
                });

                suggestedLayout.addView(suggestedRecyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));
                rootView.addView(suggestedLayout, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.BOTTOM, 6, 8, 6, keyboardViewVisible ? LayoutCreator.pxToDp(keyboardHeight) + 60 : 60));
            }

            suggestedRecyclerView.setBackground(Theme.getInstance().tintDrawable(getResources().getDrawable(R.drawable.shape_suggested_sticker), rootView.getContext(), R.attr.iGapEditTxtColor));

            disposable = stickerRepository
                    .getStickerByEmoji(lastChar)
                    .filter(structIGStickers -> structIGStickers.size() > 0 && lastChar != null)
                    .subscribe(structIGStickers -> {
                        suggestedAdapter.setIgStickers(structIGStickers);
                        suggestedLayout.setVisibility(View.VISIBLE);
                    });

            compositeDisposable.add(disposable);
        }
    }

    private void hideKeyboardView() {
        if (keyboardView == null)
            return;

        if (suggestedLayout != null && suggestedLayout.getVisibility() == View.VISIBLE) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) suggestedLayout.getLayoutParams();
            layoutParams.bottomMargin = LayoutCreator.dp(60);
        }

        keyboardViewVisible = false;
        keyboardView.setVisibility(View.GONE);
    }

    private final Runnable openKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            edtChat.requestFocus();
            AndroidUtils.showKeyboard(edtChat);
        }
    };

    private void chatMotionEvent(MotionEvent event) {
        if (/*isPopupShowing() && */event.getAction() == MotionEvent.ACTION_DOWN) {
            showPopup(KeyboardView.MODE_KEYBOARD);
            openKeyboardInternal();
        }
    }

    public boolean isKeyboardViewOpen() {
        return keyboardViewVisible;
    }

    private void closeKeyboard() {
        AndroidUtils.hideKeyboard(edtChat);
    }

    private void openKeyboardInternal() {
        edtChat.requestFocus();
        AndroidUtils.showKeyboard(edtChat);
        if (!keyboardVisible) {
            G.cancelRunOnUiThread(openKeyboardRunnable);
            G.runOnUiThread(openKeyboardRunnable, 50);
        }
    }

    private void initPopupAttachment() {

        //clear at first time to load image gallery
        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();

        mAttachmentPopup = ChatAttachmentPopup.create()
                .setContext(getActivity())
                .setRootView(rootView)
                .setFragment(FragmentChat.this)
                .setFragmentActivity(G.fragmentActivity)
                .setFragmentActivity(getActivity())
                .setSharedPref(sharedPreferences)
                .setMessagesLayoutHeight(recyclerView.getMeasuredHeight())
                .setChatBoxHeight(viewAttachFile.getMeasuredHeight())
                .setListener(FragmentChat.this)
                .build();


        if (mAttachmentPopup != null && mustCheckPermission()) {
            mAttachmentPopup.setMediaPermission(currentRoomAccess != null && currentRoomAccess.getRealmPostMessageRights() != null && currentRoomAccess.getRealmPostMessageRights().isCanSendMedia());
        }

    }

    private void removeEditedMessage() {
        imvSendButton.setText(G.fragmentActivity.getResources().getString(R.string.md_send_button));
        editTextProgress.setVisibility(View.GONE);
        edtChat.setTag(null);
        clearReplyView();
        isEditMessage = false;
        edtChat.setText("");
    }

    private void cancelAllRequestFetchHistory() {
        RequestQueue.cancelRequest(lastRandomRequestIdDown);
        RequestQueue.cancelRequest(lastRandomRequestIdUp);
        isWaitingForHistoryUp = false;
        isWaitingForHistoryDown = false;
    }

    private void showCardToCard() {
        cardToCardClick(null);
    }

    private void showSelectItem() {
        RealmRoom realmRoom = getRoom();
        if (realmRoom != null) {
            chatType = realmRoom.getType();
            if (chatType == CHAT) {
                chatPeerId = realmRoom.getChatRoom().getPeerId();
                if (imvUserPicture != null && txtName != null) {
                    if (getActivity() != null) {
                        ParentChatMoneyTransferFragment fragment = new ParentChatMoneyTransferFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("userName", txtName.getText().toString());
                        bundle.putLong("roomId", mRoomId);
                        bundle.putLong("peerId", chatPeerId);
                        bundle.putString("phoneNumber", phoneNumber);
                        fragment.setArguments(bundle);
                        fragment.setDelegate(new ParentChatMoneyTransferFragment.Delegate() {
                            @Override
                            public void onGiftStickerGetStartPayment(StructIGSticker structIGSticker, String paymentToken) {

                                if (paymentToken == null) {
                                    Toast.makeText(getContext(), getString(R.string.wallet_error_server), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                new HelperFragment(FragmentChat.this.getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.gift_sticker_title), paymentToken, result -> {
                                    if (result.isSuccess()) {
                                        Toast.makeText(getActivity(), getString(R.string.successful_payment), Toast.LENGTH_LONG).show();

                                        BuyGiftStickerCompletedBottomSheet bottomSheet = BuyGiftStickerCompletedBottomSheet.getInstance(structIGSticker);
                                        bottomSheet.setDelegate(new BuyGiftStickerCompletedBottomSheet.Delegate() {
                                            @Override
                                            public void onNegativeButton(StructIGSticker structIGSticker) {
                                                new HelperFragment(FragmentChat.this.getActivity().getSupportFragmentManager(), FragmentSettingAddStickers.newInstance()).setReplace(false).load();
                                            }

                                            @Override
                                            public void onPositiveButton(StructIGSticker structIGSticker) {
                                                sendStickerAsMessage(structIGSticker);
                                            }
                                        });

                                        bottomSheet.show(FragmentChat.this.getActivity().getSupportFragmentManager(), null);
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.unsuccessful_payment), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                            @Override
                            public void cardToCardClicked(String cardNum, String amountNum, String descriptionTv) {
                                sendNewMessageCardToCard(amountNum, cardNum, descriptionTv);

                                ll_attach_text.setVisibility(View.GONE);
                                edtChat.setFilters(new InputFilter[]{});
                                edtChat.setText("");

                                clearReplyView();
                            }
                        });
                        fragment.show(getActivity().getSupportFragmentManager(), "PaymentFragment");
                    }
                }
            }
        }
    }


    @Override
    public void onActiveGiftStickerClick(StructIGSticker structIGSticker, int mode, MessageObject messageObject) {// TODO: 12/28/20 MESSAGE_REFACTOR
//        showPopup(-1);
//        new HelperFragment(getFragmentManager()).loadActiveGiftStickerCard(structIGSticker, v -> forwardSelectedMessageToOutOfChat(structMessage), mode);
    }

    @Override
    public void onVoteClick(MessageObject messageObject, int reactionValue) {
        getMessageController().channelAddMessageVote(messageObject, reactionValue);
    }


    private void sendNewMessageCardToCard(String amount, String cardNumber, String description) {
        String mplCardNumber = cardNumber.replace("-", "");
        int mplAmount = Integer.parseInt(amount.replace(",", ""));

        JsonArray rootJsonArray = new JsonArray();
        JsonArray dataJsonArray = new JsonArray();

        JsonObject valueObject = new JsonObject();
        valueObject.addProperty("cardNumber", mplCardNumber);
        valueObject.addProperty("amount", mplAmount);
        valueObject.addProperty("userId", AccountManager.getInstance().getCurrentUser().getId());

        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("label", "Card to Card");
        rootObject.addProperty("imageUrl", "");
        rootObject.addProperty("actionType", "27");
        rootObject.add("value", valueObject);

        dataJsonArray.add(rootObject);
        rootJsonArray.add(dataJsonArray);

        final RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(mRoomId, description, replyMessageId(), rootJsonArray.toString(), AdditionalType.CARD_TO_CARD_MESSAGE);
        final MessageObject messageObject = MessageObject.create(roomMessage);
        if (messageObject != null) {

            edtChat.setText("");
            lastMessageId = messageObject.id;
            mAdapter.add(new CardToCardItem(mAdapter, chatType, FragmentChat.this).setMessage(messageObject).withIdentifier(SUID.id().get()));
            clearReplyView();
            scrollToEnd();

            /**
             * send splitted message in every one second
             */

            if (!description.isEmpty()) {
                G.handler.postDelayed(() -> {
                    if (!messageObject.deleted) {
                        getSendMessageUtil().build(chatType, mRoomId, messageObject);
                    }
                }, 1000);
            } else {
                getSendMessageUtil().build(chatType, mRoomId, messageObject);
            }
        } else {
            Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkEmptyMessageWithSemiSapce(String[] messages) {
        boolean haveCharacterExceptSemiSpace = false;

        char[] message = messages[0].toCharArray();
        for (int i = 0; i < message.length; i++) {

            if (message[i] != 8204) {
                haveCharacterExceptSemiSpace = true;
            }
        }

        return haveCharacterExceptSemiSpace;
    }

    private void sendNewMessage() {
        String[] messages = HelperString.splitStringEvery(getWrittenMessage(), Config.MAX_TEXT_LENGTH);
        if (messages.length == 0) {
            edtChat.setText("");
            Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
        } else if (!checkEmptyMessageWithSemiSapce(messages)) {
            return;
        } else {
            for (int i = 0; i < messages.length; i++) {
                final String message = messages[i];

                final RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(mRoomId, message, replyMessageId());
                final MessageObject messageObject = MessageObject.create(roomMessage);
                if (messageObject != null) {
                    edtChat.setText("");
                    lastMessageId = messageObject.id;
                    mAdapter.add(new TextItem(mAdapter, chatType, FragmentChat.this).setMessage(messageObject).withIdentifier(SUID.id().get()));
                    clearReplyView();
                    scrollToEnd();

                    /**
                     * send splitted message in every one second
                     */
                    if (messages.length > 1) {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!messageObject.deleted) {
                                    getSendMessageUtil().build(chatType, mRoomId, messageObject);
                                }
                            }
                        }, 1000 * i);
                    } else {
                        getSendMessageUtil().build(chatType, mRoomId, messageObject);
                    }
                } else {
                    Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void openWebViewFragmentForSpecialUrlChat(String mUrl) {
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.ac_ll_parent, FragmentWebView.newInstance(mUrl)).commit();
    }

    private void openWebViewForSpecialUrlChat(String mUrl) {


        try {
            setDownBtnGone();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (botInit != null) botInit.close();
     /*   StructWebView urlWebView = getUrlWebView(mUrl);
        if (urlWebView == null) {
            return;
        } else {

            urlWebViewForSpecialUrlChat = urlWebView.getUrl();
        }
        */

        urlWebViewForSpecialUrlChat = mUrl;
        if (rootWebView == null) rootWebView = rootView.findViewById(R.id.rootWebView);
        if (progressWebView == null) progressWebView = rootView.findViewById(R.id.progressWebView);
        if (webViewChatPage == null) {
            try {
                webViewChatPage = new WebView(getContext());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                rootWebView.addView(webViewChatPage, params);
//                webViewChatPage = rootView.findViewById(R.id.webViewChatPage);
            } catch (Exception e) {
                return;
            }
        }
        recyclerView.setVisibility(View.GONE);
        viewAttachFile.setVisibility(View.GONE);
        rootWebView.setVisibility(View.VISIBLE);
        webViewChatPage.getSettings().setLoadsImagesAutomatically(true);
        webViewChatPage.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewChatPage.clearCache(true);
        webViewChatPage.clearHistory();
        webViewChatPage.clearView();
        webViewChatPage.clearFormData();
        webViewChatPage.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webViewChatPage.getSettings().setJavaScriptEnabled(true);
        webViewChatPage.getSettings().setDomStorageEnabled(true);
        progressWebView.setVisibility(View.VISIBLE);

        webViewChatPage.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressWebView.setVisibility(View.GONE);

                } else {
                    progressWebView.setVisibility(View.VISIBLE);

                }
            }
        });
        webViewChatPage.setWebViewClient(new MyWebViewClient() {

            @Override
            protected void onReceivedError(WebView webView, String url, int errorCode, String description) {
            }

            @Override
            protected boolean handleUri(WebView webView, Uri uri) {
                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                // Returning false means that you are going to load this url in the webView itself
                // Returning true means that you need to handle what to do with the url e.g. open web page in a Browser

                // final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // startActivity(intent);
                return false;

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.toLowerCase().equals("igap://close")) {
                    makeWebViewGone();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        webViewChatPage.loadUrl(urlWebViewForSpecialUrlChat);
    }

    private void setTouchListener(RecyclerView recyclerView, float dX) {
        if (dX < -ViewMaker.dpToPixel(140)) {
            if (!isReply && getActivity() != null) {
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.PARCELABLE_WRITE_RETURN_VALUE));
                } else {
                    //deprecated in API 26
                    v.vibrate(50);
                }
            }
            isReply = true;
        } else {
            isReply = false;
        }

        recyclerView.setOnTouchListener((v, event) -> {
            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            return false;
        });

    }

    private void visibilityTextEmptyMessages() {
        if (mAdapter.getItemCount() > 0 || (prgWaiting != null && prgWaiting.getVisibility() == View.VISIBLE)) {
            txtEmptyMessages.setVisibility(View.GONE);
        } else {
            txtEmptyMessages.setVisibility(View.VISIBLE);
        }
    }

    private void dialogReport(final boolean isMessage, final long messageId) {
        if (!AndroidUtils.canOpenDialog()) {
            return;
        }
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.st_Abuse));
        items.add(getString(R.string.st_Spam));
        items.add(getString(R.string.st_Violence));
        items.add(getString(R.string.st_Pornography));
        items.add(getString(R.string.st_Other));

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.st_Abuse))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.ABUSE, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.ABUSE, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Spam))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.SPAM, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.SPAM, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Violence))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.VIOLENCE, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.VIOLENCE, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Pornography))) {
                if (isMessage) {
                    new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.PORNOGRAPHY, "");
                } else {
                    new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.PORNOGRAPHY, "");
                }
            } else if (items.get(position).equals(getString(R.string.st_Other))) {
                final MaterialDialog dialogReport = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.report).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE).alwaysCallInputCallback().input(G.context.getString(R.string.description), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        if (input.length() > 0) {

                            report = input.toString();
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(true);

                        } else {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(false);
                        }
                    }
                }).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (isMessage) {
                            new RequestClientRoomReport().roomReport(mRoomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.OTHER, report);
                        } else {
                            new RequestClientRoomReport().roomReport(mRoomId, 0, ProtoClientRoomReport.ClientRoomReport.Reason.OTHER, report);
                        }
                    }
                }).negativeText(R.string.cancel).build();

                View positive = dialogReport.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                DialogAnimation.animationDown(dialogReport);
                dialogReport.show();
            }
        });
        bottomSheetFragment.show(getFragmentManager(), "bottomSheet");

        G.onReport = () -> error(G.fragmentActivity.getResources().getString(R.string.st_send_report));


    }

    private void putExtra(Intent intent, StructMessageInfo messageInfo) {
        try {
            String message = messageInfo.realmRoomMessage.getForwardMessage() != null ? messageInfo.realmRoomMessage.getForwardMessage().getMessage() : messageInfo.realmRoomMessage.getMessage();
            if (message != null) {
                intent.putExtra(Intent.EXTRA_TEXT, message);
            }
            String filePath = messageInfo.realmRoomMessage.getForwardMessage() != null ? messageInfo.realmRoomMessage.getForwardMessage().getAttachment().getLocalFilePath() : messageInfo.getAttachment().getLocalFilePath();
            if (filePath != null) {
                intent.putExtra(Intent.EXTRA_STREAM, AppUtils.createtUri(new File(filePath)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *************************** callbacks ***************************
     */

    @Override
    public void onSenderAvatarClick(View view, MessageObject messageObject, int position) { // TODO: 12/28/20 MESSAGE_REFACTOR_NEED_TEST
        /**
         * set null for avoid from clear group room message adapter if user try for clearChatHistory
         */
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(mRoomId, messageObject.userId, GROUP.toString())).setReplace(false).load();
        }
    }

    @Override
    public void onUploadOrCompressCancel(View view, final MessageObject messageObject, int pos) {// TODO: 12/28/20 MESSAGE_REFACTOR
        HelperSetAction.sendCancel(messageObject.id);

        if (Uploader.getInstance().cancelCompressingAndUploading(messageObject.id + "")) {

            getMessageController().cancelUploadFile(mRoomId, messageObject);

            if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                if (messageId == ((StructMessageInfo) edtChat.getTag()).realmRoomMessage.getMessageId()) {
                    edtChat.setTag(null);
                }
            }

            if (pos >= 0) {
                mAdapter.remove(pos);
            }
        }
    }


    public void onChatClearMessage(final long roomId, final long clearId) {// TODO: 12/28/20 MESSAGE_REFACTOR_NEED_TEST
        setDownBtnGone();
        saveMessageIdPositionState(0);
        addToView = true;
        if (botInit != null)
            botInit.updateCommandList(false, "clear", getActivity(), false, null, 0, false);
        mAdapter.clear();
        recyclerView.removeAllViews();

        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
            edtChat.setTag(null);
        }
        if (mAdapter != null) {
            boolean cleared = false;
            if (mAdapter.getAdapterItemCount() > 1) {
                try {
                    if (mAdapter.getAdapterItem(mAdapter.getAdapterItemCount() - 1).messageObject.id == clearId) {
                        cleared = true;
                        mAdapter.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!cleared) {
                int selectedPosition = -1;
                for (int i = (mAdapter.getAdapterItemCount() - 1); i >= 0; i--) {
                    try {
                        MessageObject messageObject = mAdapter.getAdapterItem(i).messageObject;
                        if (messageObject != null && messageObject.id == clearId) {
                            selectedPosition = i;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (selectedPosition != -1) {
                    for (int i = selectedPosition; i >= 0; i--) {
                        mAdapter.remove(i);
                    }
                }
            }
        }

        /**
         * remove tag from edtChat if the message has deleted
         */
        if (edtChat != null && edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
            edtChat.setTag(null);
        }

    }

    @Override
    public void onChatMessageSelectionChanged(int selectedCount, Set<AbstractMessage> selectedItems) { // TODO: 12/28/20  MESSAGE_REFACTOR
        if (selectedCount > 0) {
            FragmentChat.isInSelectionMode = true;
            //toolbar.setVisibility(View.GONE);
            mBtnReplySelected.setVisibility(View.VISIBLE);
            mBtnDeleteSelected.setVisibility(View.VISIBLE);

            mTxtSelectedCounter.setText(selectedCount + " " + context.getResources().getString(R.string.item_selected));

            if (HelperCalander.isPersianUnicode) {
                mTxtSelectedCounter.setText(convertToUnicodeFarsiNumber(mTxtSelectedCounter.getText().toString()));
            }

            if (selectedCount > 1 || isNotJoin) {
                mBtnReplySelected.setVisibility(View.INVISIBLE);
            } else {

                if (chatType == CHANNEL) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        mBtnReplySelected.setVisibility(View.INVISIBLE);
                    }
                }
            }

            isAllSenderId = true;

            for (AbstractMessage message : selectedItems) {

                RealmRoom realmRoom = getRoom();
                if (realmRoom != null) {
                    if (chatType == CHANNEL) {
                        if (channelRole == ChannelChatRole.MEMBER) {
                            mBtnReplySelected.setVisibility(View.INVISIBLE);
                            mBtnDeleteSelected.setVisibility(View.GONE);
                            isAllSenderId = false;
                        }

                        if (!RealmUserInfo.getCurrentUserAuthorHash().equals(message.messageObject.authorHash)) {  // if message dose'nt belong to owner
                            if (currentRoomAccess != null && currentRoomAccess.isCanDeleteMessage()) {
                                mBtnDeleteSelected.setVisibility(View.VISIBLE);
                            } else {
                                mBtnDeleteSelected.setVisibility(View.GONE);
                            }
                        } else {
                            mBtnDeleteSelected.setVisibility(View.VISIBLE);
                        }
                    } else if (chatType == GROUP) {

                        if (!RealmUserInfo.getCurrentUserAuthorHash().equals(message.messageObject.authorHash)) {  // if message dose'nt belong to owner
                            if (currentRoomAccess != null && currentRoomAccess.isCanDeleteMessage()) {
                                mBtnDeleteSelected.setVisibility(View.VISIBLE);
                            } else {
                                mBtnDeleteSelected.setVisibility(View.GONE);
                            }
                        } else {
                            mBtnDeleteSelected.setVisibility(View.VISIBLE);
                        }
                    } else if (realmRoom.getReadOnly()) {
                        mBtnReplySelected.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (!isAllSenderId) {
                mBtnDeleteSelected.setVisibility(View.GONE);
            }


            if (isPinAvailable) pinedMessageLayout.setVisibility(View.GONE);
            ll_AppBarSelected.setVisibility(View.VISIBLE);
            showPopup(-1);
        } else {
            FragmentChat.isInSelectionMode = false;
            if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);
            ll_AppBarSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPreChatMessageRemove(final StructMessageInfo messageInfo, int position) {
//        if (mAdapter.getAdapterItemCount() > 1 && position == mAdapter.getAdapterItemCount() - 1) {
//            //RealmRoom.setLastMessageAfterLocalDelete(mRoomId, parseLong(messageInfo.messageID));
//
//            RealmRoom.setLastMessage(mRoomId);
//        }
    }

    @Override
    public void onMessageUpdate(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, final String identity, final ProtoGlobal.RoomMessage roomMessage) {
        // I'm in the room
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (roomId == mRoomId && mAdapter != null) {
                    mAdapter.updateMessageIdAndStatus(messageId, identity, status, roomMessage);
                }
            }
        });

        if (soundPool != null && sendMessageSound != 0)
            playSendSound(roomId, roomMessage, chatType);

    }

    @Override
    public synchronized void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {

        if (roomMessage.getMessageId() <= biggestMessageId) {
            return;
        }

        if (soundPool != null && sendMessageSound != 0)
            playReceiveSound(roomId, roomMessage, roomType);

        if (isBot) {
            if (rootView != null) {
                rootView.post(() -> {

                    if (getActivity() == null || getActivity().isFinishing())
                        return;

                    if (roomMessage.getAdditionalType() == Additional.WEB_VIEW.getAdditional()) {
                        openWebViewForSpecialUrlChat(roomMessage.getAdditionalData());
                        return;
                    }

                    RealmRoomMessage rm = null;
                    boolean backToMenu = true;
                    RealmResults<RealmRoomMessage> result = DbManager.getInstance().doRealmTask(realm -> {
                        return realm.where(RealmRoomMessage.class).
                                equalTo("roomId", mRoomId).notEqualTo("authorHash", RealmUserInfo.getCurrentUserAuthorHash()).findAll();
                    });
                    if (result.size() > 0) {
                        rm = result.last();
                        if (rm != null && rm.getMessage() != null) {
                            if (rm.getMessage().toLowerCase().equals("/start") || rm.getMessage().equals("/back")) {
                                backToMenu = false;
                            }
                        }
                    }

                    if (roomMessage.getAuthor().getUser().getUserId() == chatPeerId) {

                        if (rm != null && rm.getRealmAdditional() != null && roomMessage.getAdditionalType() == AdditionalType.UNDER_KEYBOARD_BUTTON)
                            botInit.updateCommandList(false, message, getActivity(), backToMenu, roomMessage, roomId, true);
                        else
                            botInit.updateCommandList(false, "clear", getActivity(), backToMenu, null, 0, true);
                    }

                    if (isShowStartButton) {
                        rootView.findViewById(R.id.chl_ll_channel_footer).setVisibility(View.GONE);
                        if (webViewChatPage == null)
                            rootView.findViewById(R.id.layout_attach_file).setVisibility(View.VISIBLE);
                        isShowStartButton = false;
                    }
                });
            }
        }

        DbManager.getInstance().doRealmTask(realm -> {
            final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", roomMessage.getMessageId()).findFirst();

            if (realmRoomMessage != null && realmRoomMessage.isValid() && !realmRoomMessage.isDeleted()) {
                if (roomMessage.getAuthor().getUser() != null) {
                    RealmRoomMessage messageCopy = realm.copyFromRealm(realmRoomMessage);
                    // I'm in the room
                    if (roomId == mRoomId) {
//                        if (roomMessage.getAuthor().getUser().getUserId() != G.userId)
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * when client load item from unread and don't come down, don't let add the message
                                 * to the list and after insuring that not any more message in DOWN can add message
                                 */
                                if (addToView) {
                                    switchAddItem(new ArrayList<>(Collections.singletonList(new StructMessageInfo(messageCopy))), false);
                                    if (isShowLayoutUnreadMessage) {
                                        removeLayoutUnreadMessage();
                                    }
                                }

                                setBtnDownVisible(messageCopy);

                            }
                        });
                    }
                }
            }
        });
    }

    private void playReceiveSound(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        if (roomType == CHAT)
            if (roomId == this.mRoomId && sendMessageSound != 0 && !isPaused) {
                try {
                    soundPool.play(sendMessageSound, 1.0f, 1.0f, 1, 0, 1.0f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private void playSendSound(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        if (roomType == CHAT)
            if (roomId == this.mRoomId && receiveMessageSound != 0 && !isPaused) {
                try {
                    soundPool.play(receiveMessageSound, 1.0f, 1.0f, 1, 0, 1.0f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private StructWebView getUrlWebView(String additionalData) {

        Gson gson = new Gson();
        StructWebView item = new StructWebView();
        try {
            item = gson.fromJson(additionalData, StructWebView.class);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e1) {
            e1.printStackTrace();
        }

        return item;
    }

    @Override
    public void onMessageFailed(long roomId, long messageId) {
        G.handler.post(() -> {
            if (roomId == mRoomId && mAdapter != null) {
                mAdapter.updateMessageStatus(messageId, ProtoGlobal.RoomMessageStatus.FAILED);
            }
        });
    }

    @Override
    public void onVoiceRecordDone(final String savedPath) {
        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        sendCancelAction();

        RealmRoomMessage roomMessage = RealmRoomMessage.makeVoiceMessage(mRoomId, chatType, savedPath, getWrittenMessage());

        if (isReply()) {
            RealmRoomMessage copyReplyMessage = DbManager.getInstance().doRealmTask(realm -> {
                RealmRoomMessage copyReplyMessage1 = realm.where(RealmRoomMessage.class).equalTo("messageId", getReplyMessageId()).findFirst();
                if (copyReplyMessage1 != null) {
                    return realm.copyFromRealm(copyReplyMessage1);
                }
                return null;
            });

            if (copyReplyMessage != null) {
                roomMessage.setReplyTo(copyReplyMessage);
            }
        }

        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmRoom.setLastMessageWithRoomMessage(realm, mRoomId, realm.copyToRealmOrUpdate(roomMessage));
            });
        }).start();


        mAdapter.add(new VoiceItem(mAdapter, chatType, this).setMessage(MessageObject.create(roomMessage)));
        scrollToEnd();
        clearReplyView();
    }

    @Override
    public void onVoiceRecordCancel() {
        //empty

        sendCancelAction();
    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

        if (isCloudRoom) {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.VISIBLE);
            imvUserPicture.setVisibility(View.GONE);
        } else {
            mHelperToolbar.getCloudChatIcon().setVisibility(View.GONE);
            imvUserPicture.setVisibility(View.VISIBLE);
            setAvatar();
        }
    }

    @Override
    public void onUserInfoTimeOut() {
        //empty
    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {
        //empty
    }

    @Override
    public void onOpenClick(View view, MessageObject messageObject, int pos) {

        int messageType = messageObject.isForwarded() ? messageObject.forwardedMessage.messageType : messageObject.messageType;

        if (messageType == STICKER_VALUE) {
            checkSticker(messageObject);
        } else if (messageType == IMAGE_VALUE || messageType == IMAGE_TEXT_VALUE) {
            showImage(messageObject, view);
        } else if (messageType == VIDEO_VALUE || messageType == VIDEO_TEXT_VALUE) {
            if (messageObject.status != MessageObject.STATUS_SENDING && messageObject.status != MessageObject.STATUS_FAILED) {
                if (sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1) == 0) {
                    openMessage(messageObject);
                } else {
                    showImage(messageObject, view);
                }
            }
        } else if (messageType == FILE_VALUE || messageType == FILE_TEXT_VALUE) {
            openMessage(messageObject);
        }

    }

    private void checkSticker(MessageObject message) {
        try {
            StructIGSticker structIGSticker = new Gson().fromJson(message.additionalData, StructIGSticker.class);
            if (!structIGSticker.isGiftSticker())
                openFragmentAddStickerToFavorite(structIGSticker.getGroupId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFragmentAddStickerToFavorite(String groupId) {
        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(groupId);

        boolean canSendSticker = currentRoomAccess != null && currentRoomAccess.getRealmPostMessageRights().isCanSendSticker();
        StickerDialogFragment dialogFragment = StickerDialogFragment.getInstance(stickerGroup, mustCheckPermission() ? !canSendSticker || isChatReadOnly : isChatReadOnly);
        dialogFragment.setListener(this::sendStickerAsMessage);

        if (getActivity() != null) {
            showPopup(-1);
            dialogFragment.show(getActivity().getSupportFragmentManager(), "dialogFragment");
        }

    }

    private void openMessage(MessageObject messageObject) {
        String filePath = null;
        String token = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().token : messageObject.getAttachment().token;
        RealmAttachment realmAttachment = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmAttachment.class).equalTo("token", token).findFirst();
        });

        if (realmAttachment != null) {
            filePath = realmAttachment.getLocalFilePath();
        } else if (messageObject.getAttachment() != null) {
            filePath = messageObject.getAttachment().filePath;
        }

        if (filePath == null || filePath.length() == 0) {
            return;
        }

        Intent intent = HelperMimeType.appropriateProgram(filePath);
        if (intent != null) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.can_not_support_file, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.can_not_open_file, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownloadAllEqualCashId(String cashId, String messageID) { // TODO: 12/28/20 MESSAGE_REFACTOR

        int start = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (start < 0) {
            start = 0;
        }

        for (int i = start; i < mAdapter.getItemCount() && i < start + 15; i++) {
            try {
                AbstractMessage item = mAdapter.getAdapterItem(i);
                AttachmentObject attachmentObject = item.messageObject.forwardedMessage != null ? item.messageObject.forwardedMessage.attachment : item.messageObject.attachment;
                if (attachmentObject != null) {
                    if (attachmentObject.cacheId != null && attachmentObject.cacheId.equals(cashId) && !(item.messageObject.id + "").equals(messageID)) {
                        mAdapter.notifyItemChanged(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCountNewMessageZero() {

        // Todo : notify FragmentMain List for update unread count.

        countNewMessage = 0;
        txtNewUnreadMessage.setVisibility(View.GONE);
        txtNewUnreadMessage.getTextView().setText(countNewMessage + "");

        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom.setCount(realm, mRoomId, 0);
                    RealmRoom.removeFirstUnreadMessage(realm, mRoomId);
                }
            });
        });
    }

    @Override
    public void onItemShowingMessageId(final MessageObject messageObject) {// TODO: 12/28/20 MESSAGE_REFACTOR_NEED_TEST
        if (getFirstUnreadMessage() != null && getFirstUnreadMessage().isValid() && getFirstUnreadMessage().getMessageId() <= messageObject.id) {
            setCountNewMessageZero();
        }

        if (!isPaused && chatType != CHANNEL && (!messageObject.isSenderMe() && messageObject.status != SEEN_VALUE && messageObject.status != LISTENED_VALUE)) {
            messageObject.status = SEEN_VALUE;

            getMessageDataStorage().setStatusSeenInChat(mRoomId, messageObject.id);
            getMessageController().sendUpdateStatus(chatType.getNumber(), mRoomId, messageObject.id, SEEN_VALUE);
        }
    }

    @Override
    public void onVoiceListenedStatus(int roomType, long roomId, long messageId, int roomMessageStatus) {
        getMessageController().sendUpdateStatus(roomType, roomId, messageId, roomMessageStatus);
    }

    @Override
    public void onPlayMusic(String messageId) {

        if (messageId != null && messageId.length() > 0) {

            try {
                if (MusicPlayer.downloadNextMusic(messageId)) {
                    mAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }
        }
    }

    @Override
    public void onOpenLinkDialog(String url) {

        mAdapter.deselect();

        if (getActivity() == null) return;
        HelperUrl.openLinkDialog(getActivity(), url);

        if (keyboardViewVisible) {
            hideKeyboard();
        }
    }

    @Override
    public boolean getShowVoteChannel() {
        return showVoteChannel;
    }

    @Override
    public void sendFromBot(Object message) {
        if (message instanceof MessageObject) {
            mAdapter.add(new TextItem(mAdapter, chatType, FragmentChat.this).setMessage((MessageObject) message).withIdentifier(SUID.id().get()));
            scrollToEnd();
        } else if (message instanceof String) {
            openWebViewFragmentForSpecialUrlChat(message.toString());
        }
    }

    @Override
    public void onContainerClick(View view, final MessageObject messageObject, int pos) {// TODO: 12/28/20  MESSAGE_REFACTOR
        if (messageObject == null || getContext() == null) {
            return;
        }
        if (mAdapter.getSelectedItems().size() > 0) {
            view.performLongClick();
            return;
        }
        List<Integer> items = setupMessageContainerClickDialogItems(messageObject);
        if (items.size() == 0)
            return;
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setListDataWithResourceId(getContext(), items, -1, position -> {
            handleContainerBottomSheetClick(messageObject, pos, items.get(position));
        });
        if (getFragmentManager() != null)
            bottomSheetFragment.show(getFragmentManager(), "bottomSheet");

        hideKeyboard();

    }

    private List<Integer> setupMessageContainerClickDialogItems(MessageObject messageObject) {
        List<Integer> items = new ArrayList<>();

        int roomMessageType;
        if (messageObject.forwardedMessage != null) {
            roomMessageType = messageObject.forwardedMessage.messageType;
        } else {
            roomMessageType = messageObject.messageType;
        }

        if (!AndroidUtils.canOpenDialog()) {
            return new ArrayList<>();
        }
        if (!isAdded() || G.fragmentActivity.isFinishing()) {
            return new ArrayList<>();
        }

        boolean shareLinkIsOn = false;
        RealmRoom room = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", messageObject.roomId).findFirst();
        });

        if (room != null && room.getChannelRoom() != null && !room.getChannelRoom().isPrivate()) {
            shareLinkIsOn = true;
        }

        if (!isNotJoin)
            items.add(R.string.replay_item_dialog);
        if (roomMessageType != STICKER_VALUE)
            items.add(R.string.share_item_dialog);

        if (shareLinkIsOn)
            items.add(R.string.share_link_item_dialog);

        if (RoomObject.isRoomPublic(room)) {
            if (MessageObject.canSharePublic(messageObject)) {
                if (messageObject.getAttachment().publicUrl != null)
                    items.add(R.string.share_file_link);
            }
        }

        items.add(R.string.forward_item_dialog);
        items.add(R.string.delete_item_dialog);

        if (isFileExistInLocalStorage(messageObject)) {
            if (MusicPlayer.isPause && String.valueOf(messageObject.id).equals(MusicPlayer.messageId)) {
                items.add(R.string.delete_from_storage);
            } else if (!String.valueOf(messageObject.id).equals(MusicPlayer.messageId)) {
                items.add(R.string.delete_from_storage);
            }
        }

        //check and remove share base on type and download state
        if (roomMessageType == LOCATION_VALUE || roomMessageType == VOICE_VALUE) {
            items.remove(Integer.valueOf(R.string.share_item_dialog));
        } else if (roomMessageType != TEXT_VALUE && roomMessageType != CONTACT_VALUE) {
            String filepath_;
            if (messageObject.forwardedMessage != null) {
                filepath_ = messageObject.forwardedMessage.getAttachment().filePath != null ? messageObject.forwardedMessage.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.forwardedMessage.getAttachment().cacheId, messageObject.forwardedMessage.getAttachment().name, roomMessageType);
            } else {
                filepath_ = messageObject.getAttachment().filePath != null ? messageObject.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.getAttachment().cacheId, messageObject.getAttachment().name, messageObject.messageType);
            }

            if (!new File(filepath_).exists()) {
                items.remove(Integer.valueOf(R.string.share_item_dialog));
            }
        }

        if (isFileExistInLocalStorage(messageObject)) {
            if (roomMessageType == IMAGE_VALUE || roomMessageType == VIDEO_VALUE || roomMessageType == GIF_VALUE) {
                items.add(R.string.save_to_gallery);
            } else if (roomMessageType == AUDIO_VALUE || roomMessageType == VOICE_VALUE) {
                items.add(R.string.save_to_Music);
            } else if (roomMessageType == FILE_VALUE) {
                items.add(R.string.saveToDownload_item_dialog);
            }
        }

        switch (roomMessageType) {
            case TEXT_VALUE:
            case FILE_TEXT_VALUE:
            case IMAGE_TEXT_VALUE:
            case VIDEO_TEXT_VALUE:
            case AUDIO_TEXT_VALUE:
            case GIF_TEXT_VALUE:
                items.add(1, R.string.copy_item_dialog);
                items.add(R.string.edit_item_dialog);
                break;
            case FILE_VALUE:
            case IMAGE_VALUE:
            case VIDEO_VALUE:
            case AUDIO_VALUE:
            case GIF_VALUE:
                items.add(R.string.edit_item_dialog);
                break;
            case VOICE_VALUE:
            case LOCATION_VALUE:
            case CONTACT_VALUE:
            case STICKER_VALUE:
            case LOG_VALUE:
                break;
        }


        if (messageObject.forwardedMessage != null || (rootView.findViewById(R.id.replayLayoutAboveEditText) != null && rootView.findViewById(R.id.replayLayoutAboveEditText).getVisibility() == View.VISIBLE)) {
            items.remove(Integer.valueOf(R.string.edit_item_dialog));
        }

        RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", messageObject.roomId).findFirst();
        });

        if (realmRoom != null) {
            //if user clicked on any message which he wasn't its sender, remove edit mList option
            boolean showLayoutPin = !RealmRoom.isPinedMessage(mRoomId, messageObject.id) && currentRoomAccess != null && currentRoomAccess.isCanPinMessage();
            if (chatType == CHANNEL) {
                if (channelRole == ChannelChatRole.MEMBER) {
                    items.remove(Integer.valueOf(R.string.replay_item_dialog));
                }
                if (!RealmUserInfo.getCurrentUserAuthorHash().equals(messageObject.authorHash)) {
                    if (currentRoomAccess != null && !currentRoomAccess.isCanEditMessage()) {
                        items.remove(Integer.valueOf(R.string.edit_item_dialog));
                    }

                    if (currentRoomAccess != null && !currentRoomAccess.isCanDeleteMessage()) {
                        items.remove(Integer.valueOf(R.string.delete_item_dialog));
                    }
                }
            } else if (chatType == GROUP) {

                if (groupRole != GroupChatRole.MEMBER) {
                    showLayoutPin = true;
                }
                //GroupChatRole roleSenderMessage = RealmGroupRoom.detectMemberRole(mRoomId, message.realmRoomMessage.getUserId());
                if (!RealmUserInfo.getCurrentUserAuthorHash().equals(messageObject.authorHash)) {
                    if (currentRoomAccess != null && !currentRoomAccess.isCanEditMessage()) {
                        items.remove(Integer.valueOf(R.string.edit_item_dialog));
                    }

                    if (currentRoomAccess != null && !currentRoomAccess.isCanDeleteMessage()) {
                        items.remove(Integer.valueOf(R.string.delete_item_dialog));
                    }
                }
            } else if (realmRoom.getReadOnly()) {
                items.remove(Integer.valueOf(R.string.replay_item_dialog));
            } else {
                if (messageObject.userId != AccountManager.getInstance().getCurrentUser().getId()) {
                    items.remove(Integer.valueOf(R.string.edit_item_dialog));
                }
            }
            if (showLayoutPin && !isNotJoin) {
                items.add(R.string.PIN);
            }

        }

        if (isChatReadOnly) {
            items.remove(Integer.valueOf(R.string.edit_item_dialog));
        }

        if (RealmRoom.isNotificationServices(mRoomId)) {
            items.remove(Integer.valueOf(R.string.report));
        }

        if (channelRole != ChannelChatRole.OWNER || groupRole != GroupChatRole.OWNER || isNotJoin) {
            items.add(R.string.report);
        } else {
            items.remove(Integer.valueOf(R.string.report));
        }

        if (messageObject.additional != null && messageObject.additionalType == AdditionalType.CARD_TO_CARD_MESSAGE) {
            items.clear();
            items.add(R.string.replay_item_dialog);
            items.add(R.string.delete_item_dialog);
        }

        if (messageObject.additional != null && messageObject.additionalType == AdditionalType.GIFT_STICKER) {
            items.clear();
            items.add(R.string.replay_item_dialog);
            items.add(R.string.delete_item_dialog);
        }

        return items;
    }

    private void handleContainerBottomSheetClick(MessageObject message, int adapterPosition, int item) {
        switch (item) {
            case R.string.PIN:
                pinSelectedMessage(message);
                break;

            case R.string.replay_item_dialog:
                G.handler.postDelayed(() -> reply(message, false), 200);
                break;

            case R.string.copy_item_dialog:
                copyMessageToClipboard(message, true);
                break;

            case R.string.share_item_dialog:
                shearedDataToOtherProgram(message);
                break;

            case R.string.share_link_item_dialog:
                shearedLinkDataToOtherProgram(message);
                break;

            case R.string.share_file_link:
                shareMediaLink(message);
                break;
            case R.string.forward_item_dialog:
                forwardSelectedMessageToOutOfChat(message);
                break;

            case R.string.delete_item_dialog:
                confirmAndDeleteMessage(message, false);
                break;

            case R.string.delete_from_storage:
                confirmAndDeleteFromStorage(message, adapterPosition);
                break;

            case R.string.edit_item_dialog:
                editSelectedMessage(message);
                break;

            case R.string.save_to_gallery:
                saveSelectedMessageToGallery(message, adapterPosition);
                break;

            case R.string.save_to_Music:
                saveSelectedMessageToMusic(message, adapterPosition);
                break;

            case R.string.saveToDownload_item_dialog:
                saveSelectedMessageToDownload(message, adapterPosition);
                break;

            case R.string.report:
                reportSelectedMessage(message);
                break;
        }
    }

    private void reportSelectedMessage(MessageObject messageObject) {
        long messageId;
        if (messageObject.forwardedMessage != null) {
            messageId = messageObject.forwardedMessage.id;
        } else {
            messageId = messageObject.id;
        }
        dialogReport(true, messageId);
    }

    private void saveSelectedMessageToDownload(MessageObject messageObject, int pos) {
        String filename;
        String filepath;
        int messageType;

        if (messageObject.forwardedMessage != null) {
            filename = messageObject.forwardedMessage.getAttachment().name;
            filepath = messageObject.forwardedMessage.getAttachment().filePath != null ? messageObject.forwardedMessage.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.forwardedMessage.getAttachment().cacheId, filename, messageObject.forwardedMessage.messageType);
        } else {
            filename = messageObject.getAttachment().name;
            filepath = messageObject.getAttachment().filePath != null ? messageObject.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.getAttachment().cacheId, filename, messageObject.messageType);
        }

        if (new File(filepath).exists()) {
            getStoragePermission(filepath, filename);
        } else {
            final int _messageType = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.messageType : messageObject.messageType;
            String cacheId = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().cacheId : messageObject.getAttachment().cacheId;
            final String name = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().name : messageObject.getAttachment().name;
            String fileToken = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().token : messageObject.getAttachment().token;
            String fileUrl = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().publicUrl : messageObject.getAttachment().publicUrl;
            Long size = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().size : messageObject.getAttachment().size;

            if (cacheId == null) {
                return;
            }

            final String _path = AndroidUtils.getFilePathWithCashId(cacheId, name, _messageType);
            // TODO: 1/6/21 MESSAGE_REFACTOR
            DownloadObject fileObject = DownloadObject.createForRoomMessage(messageObject);

            if (fileObject != null) {
                getDownloader().download(fileObject, arg -> {
                    if (arg.data != null && arg.data.getProgress() == 100) {
                        if (canUpdateAfterDownload) {
                            G.handler.post(() -> {
                                getStoragePermission(_path, name);
                            });
                        }
                    }
                });
            }

            onDownloadAllEqualCashId(cacheId, messageObject.id + "");
            mAdapter.notifyItemChanged(pos);
        }
    }

    private void getStoragePermission(final String filePath, final String fileName) {
        if (!HelperPermission.grantedUseStorage()) {
            try {
                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        copyFileToDownload(filePath, fileName);
                    }

                    @Override
                    public void deny() {
                    }
                });
            } catch (IOException e) {
                FileLog.e(e);
            }
        } else {
            copyFileToDownload(filePath, fileName);
        }
    }

    private void copyFileToDownload(final String filePath, final String fileName) {
        try {
            if (filePath == null || fileName == null) {
                return;
            }
            File src = new File(filePath);
            if (!src.exists()) {
                return;
            }
            String destinationPath = " ";
            if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).exists()) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
            }
            destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
            File file = new File(destinationPath);
            AndroidUtils.copyFileToDownload(src, file, () -> Toast.makeText(G.currentActivity, R.string.file_save_to_download_folder, Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(G.currentActivity, R.string.file_can_not_save_to_selected_folder, Toast.LENGTH_SHORT).show();
            FileLog.e(e);
        }
    }

    private void saveSelectedMessageToMusic(MessageObject message, int pos) {
        String filename;
        String filepath;

        if (message.forwardedMessage != null) {
            filename = message.forwardedMessage.getAttachment().name;
            filepath = message.forwardedMessage.getAttachment().filePath != null ? message.forwardedMessage.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(message.forwardedMessage.getAttachment().cacheId, filename, message.forwardedMessage.messageType);
        } else {
            filename = message.getAttachment().name;
            filepath = message.getAttachment().filePath != null ? message.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(message.getAttachment().cacheId, message.getAttachment().name, message.messageType);
        }
        if (new File(filepath).exists()) {
            HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.music, R.string.save_to_music_folder);
        } else {
            final int _messageType = message.forwardedMessage != null ? message.forwardedMessage.messageType : message.messageType;
            String cacheId = message.forwardedMessage != null ? message.forwardedMessage.getAttachment().cacheId : message.getAttachment().cacheId;
            final String name = message.forwardedMessage != null ? message.forwardedMessage.getAttachment().name : message.getAttachment().name;
            String fileToken = message.forwardedMessage != null ? message.forwardedMessage.getAttachment().token : message.getAttachment().token;
            String fileUrl = message.forwardedMessage != null ? message.forwardedMessage.getAttachment().publicUrl : message.getAttachment().publicUrl;
            Long size = message.forwardedMessage != null ? message.forwardedMessage.getAttachment().size : message.getAttachment().size;
            if (cacheId == null) {
                return;
            }
            ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

            final String _path = AndroidUtils.getFilePathWithCashId(cacheId, name, _messageType);
            // TODO: 1/6/21 MESSAGE_REFACTOR
            DownloadObject fileObject = DownloadObject.createForRoomMessage(message);

            if (fileObject != null) {
                getDownloader().download(fileObject, selector, arg -> {
                    if (canUpdateAfterDownload) {
                        G.handler.post(() -> {
                            if (arg.status == Status.SUCCESS || arg.status == Status.LOADING) {
                                if (arg.data != null && arg.data.getProgress() == 100) {
                                    HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.music, R.string.save_to_music_folder);
                                }
                            }
                        });
                    }
                });
            }
            onDownloadAllEqualCashId(cacheId, message.id + "");
            mAdapter.notifyItemChanged(pos);
        }
    }

    private void saveSelectedMessageToGallery(MessageObject messageObject, int pos) {
        String filename;
        String filepath;
        int messageType;

        if (messageObject.forwardedMessage != null) {
            messageType = messageObject.forwardedMessage.messageType;
            filename = messageObject.forwardedMessage.getAttachment().name;
            filepath = messageObject.forwardedMessage.getAttachment().filePath != null ? messageObject.forwardedMessage.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.forwardedMessage.getAttachment().cacheId, filename, messageType);
        } else {
            messageType = messageObject.messageType;
            filename = messageObject.getAttachment().name;
            filepath = messageObject.getAttachment().filePath != null ? messageObject.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.getAttachment().cacheId, messageObject.getAttachment().name, messageType);
        }
        if (new File(filepath).exists()) {
            if (messageType == VIDEO_VALUE) {
                HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
            } else if (messageType == GIF_VALUE) {
                HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.gif, R.string.file_save_to_picture_folder);
            } else if (messageType == IMAGE_VALUE) {
                HelperSaveFile.saveFileToDownLoadFolder(filepath, filename, HelperSaveFile.FolderType.image, R.string.picture_save_to_galary);
            }
        } else {
            final int _messageType = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.messageType : messageObject.messageType;
            String cacheId = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().cacheId : messageObject.getAttachment().cacheId;
            final String name = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().name : messageObject.getAttachment().name;
            String fileToken = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().token : messageObject.getAttachment().token;
            String fileUrl = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().publicUrl : messageObject.getAttachment().publicUrl;
            long size = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().size : messageObject.getAttachment().size;

            if (cacheId == null) {
                return;
            }
            ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

            final String _path = AndroidUtils.getFilePathWithCashId(cacheId, name, _messageType);

            DownloadObject fileObject = DownloadObject.createForRoomMessage(messageObject);

            if (fileObject != null) {
                getDownloader().download(fileObject, selector, arg -> {
                    if (canUpdateAfterDownload) {
                        G.handler.post(() -> {
                            switch (arg.status) {
                                case SUCCESS:
                                case LOADING:
                                    if (arg.data == null)
                                        return;
                                    if (arg.data.getProgress() == 100) {
                                        if (_messageType == VIDEO_VALUE) {
                                            HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                                        } else if (_messageType == GIF_VALUE) {
                                            HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.gif, R.string.file_save_to_picture_folder);
                                        } else if (_messageType == IMAGE_VALUE) {
                                            HelperSaveFile.saveFileToDownLoadFolder(_path, name, HelperSaveFile.FolderType.image, R.string.picture_save_to_galary);
                                        }
                                    }
                                    break;
                            }
                        });
                    }
                });

                mAdapter.notifyItemChanged(pos);
            }

            onDownloadAllEqualCashId(cacheId, messageObject.id + "");
        }
    }

    private void editSelectedMessage(MessageObject message) {// TODO: 12/28/20 MESSAGE_REFACTOR
        if (message.message != null && !message.message.isEmpty()) {
            edtChat.setText(EmojiManager.getInstance().replaceEmoji(message.message, edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false));
            edtChat.setSelection(edtChat.getText().toString().length());
        }
        // put message object to edtChat's tag to obtain it later and
        // found is user trying to edit a message
        edtChat.setTag(message);
        isEditMessage = true;
        sendButtonVisibility(true);
        reply(message, true);
        G.runOnUiThread(() -> editTextRequestFocus(edtChat));
    }

    private void forwardSelectedMessageToOutOfChat(MessageObject message) {
        mForwardMessages = new ArrayList<>(Arrays.asList(message));
        if (getActivity() instanceof ActivityMain) {
            ((ActivityMain) getActivity()).setForwardMessage(true);
        }
        finishChat();
        new HelperFragment(getFragmentManager()).removeAll(true);
    }

    private void pinSelectedMessage(MessageObject messageObject) {
        long _messageId;
        _messageId = messageObject.id;
        RealmRoom.updatePinedMessageDeleted(mRoomId, true);
        sendRequestPinMessage(_messageId);
    }

    private void copyMessageToClipboard(MessageObject messageObject, boolean isShowEmpty) {
        ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
        String _text = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.message : messageObject.message;
        if (_text != null && _text.length() > 0) {
            ClipData clip = ClipData.newPlainText("Copied Text", _text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, R.string.text_copied, Toast.LENGTH_SHORT).show();
        } else {
            if (isShowEmpty)
                Toast.makeText(context, R.string.text_is_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmAndDeleteFromStorage(MessageObject messageObject, int pos) {
        if (getContext() == null) return;
        new MaterialDialog.Builder(getContext())
                .content(R.string.are_you_sure)
                .positiveText(R.string.yes)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    deleteFileFromStorageIfExist(messageObject);
                    getMessageDataStorage().deleteFileFromStorage(messageObject, object -> {
                        mAdapter.notifyAdapterItemChanged(pos);
                    });
                }).show();
    }

    @SuppressLint("SetTextI18n")
    private void confirmAndDeleteMessage(MessageObject messageObject, boolean isFromMultiSelect) {

        if (getContext() == null || messageObject == null) return;

        boolean bothDelete = MessageController.isBothDelete(messageObject.getUpdateOrCreateTime());
        bothDeleteMessageId = new ArrayList<>();
        if (bothDelete) {
            bothDeleteMessageId.add(messageObject.id);
        }

        messageIds = new ArrayList<>();
        messageIds.add(messageObject.id);

        boolean isCanDeleteAttachFromDevice = isFileExistInLocalStorage(messageObject);

        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .limitIconToDefaultSize()
                .customView(R.layout.st_dialog_delete_message, false)
                .title(R.string.message)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .show();

        View dialogView = dialog.getCustomView();
        AppCompatCheckBox checkBoxDelDevice = dialogView.findViewById(R.id.del_from_device);
        AppCompatCheckBox checkBoxDelBoth = dialogView.findViewById(R.id.del_for);
        TextView txtContent = dialogView.findViewById(R.id.content);

        txtContent.setText(getString(R.string.st_desc_delete));
        checkBoxDelBoth.setText(getString(R.string.st_checkbox_delete) + " " + title);

        if (!isCanDeleteAttachFromDevice) {
            checkBoxDelDevice.setVisibility(View.GONE);
        }

        if (!bothDelete || isCloudRoom || messageObject.userId != AccountManager.getInstance().getCurrentUser().getId()) {
            checkBoxDelBoth.setVisibility(View.GONE);
            bothDeleteMessageId = null;
        }

        if (chatType != CHAT) {
            checkBoxDelBoth.setVisibility(View.GONE);
        }

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
            if (!checkBoxDelBoth.isChecked()) {
                bothDeleteMessageId = null;
            }

            if (checkBoxDelDevice.isChecked()) {
                deleteFileFromStorageIfExist(messageObject);
            }

            getMessageController().deleteSelectedMessage(chatType.getNumber(), mRoomId, messageIds, bothDeleteMessageId);

            if (isFromMultiSelect)
                deleteSelectedMessageFromAdapter(messageIds);

            dialog.dismiss();
        });
    }

    private void editTextRequestFocus(EditText editText) {
        if (getContext() != null) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    private void doForwardDialogMessage(MessageObject message, boolean isMessage) {
        if (message == null) {
            mForwardMessages = getMessageStructFromSelectedItems();
            deselectMessageAndShowPinIfNeeded();
        } else {
            mForwardMessages = new ArrayList<>(Arrays.asList(message));
        }

        initAttachForward(isMessage);
        itemAdapterBottomSheetForward();
    }

    private void deselectMessageAndShowPinIfNeeded() {
        if (ll_AppBarSelected != null && ll_AppBarSelected.getVisibility() == View.VISIBLE) {
            mAdapter.deselect();
            if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);
            ll_AppBarSelected.setVisibility(View.GONE);
            clearReplyView();
        }
    }

    @Override
    public void onFailedMessageClick(View view, final MessageObject messageObject, final int pos) {// TODO: 12/28/20 MESSAGE_REFACTOR_NEED_TEST
        final List<MessageObject> failedMessages = mAdapter.getFailedMessages();
        hideKeyboard();
        new ResendMessage(G.fragmentActivity, new IResendMessage() {
            @Override
            public void deleteMessage() {
                if (pos >= 0 && mAdapter.getAdapterItemCount() > pos) {
                    mAdapter.remove(pos);
                    removeLayoutTimeIfNeed();
                }
            }

            @Override
            public void resendMessage() {

                for (int i = 0; i < failedMessages.size(); i++) {
                    if (failedMessages.get(i).id == messageObject.id) {
                        if (failedMessages.get(i).getAttachment() != null) {
                            if (!Uploader.getInstance().isCompressingOrUploading(messageObject.id + "")) {
                                UploadObject fileObject = UploadObject.createForMessage(messageObject, chatType);
                                if (fileObject != null) {
                                    Uploader.getInstance().upload(fileObject);
                                }
                            }
                        }
                        break;
                    }
                }

                mAdapter.updateMessageStatus(messageObject.id, ProtoGlobal.RoomMessageStatus.SENDING);

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyItemChanged(mAdapter.findPositionByMessageId(messageObject.id));
                    }
                }, 300);


            }

            @Override
            public void resendAllMessages() {
                for (int i = 0; i < failedMessages.size(); i++) {
                    final int j = i;
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.updateMessageStatus(failedMessages.get(j).id, ProtoGlobal.RoomMessageStatus.SENDING);
                        }
                    }, 1000 * i);

                }
            }

            @Override
            public void copyMessage() {
                copyMessageToClipboard(messageObject, false);
            }
        }, messageObject.id, failedMessages);
    }


    /**
     * @param replyMessage when click on replay message this method call.
     *                     if message in view scroll to position with animation called,but if message exist in room db reset message value and get message.
     *                     and if not exist in db and view get message from history request and put into db and call again onReplayClick method.
     */

    @Override
    public void onReplyClick(MessageObject replyMessage) {// TODO: 12/29/20 MESSAGE_REFACTOR
        if (!goToPositionWithAnimation(replyMessage.id, 1000)) {
            long replayMessageId = Math.abs(replyMessage.id);
            if (!goToPositionWithAnimation(replayMessageId, 1000)) {
                if (RealmRoomMessage.existMessageInRoom(replayMessageId, mRoomId)) {
                    resetMessagingValue();
                    savedScrollMessageId = replayMessageId;
                    firstVisiblePositionOffset = 0;
                    getMessages();
                } else {
                    new RequestClientGetRoomHistory().getRoomHistory(mRoomId, replayMessageId - 1, 1, DOWN, new RequestClientGetRoomHistory.OnHistoryReady() {
                        @Override
                        public void onHistory(List<ProtoGlobal.RoomMessage> messageList) {
                            G.handler.post(() -> {
                                DbManager.getInstance().doRealmTransaction(realm1 -> {
                                    for (ProtoGlobal.RoomMessage roomMessage : messageList) {
                                        RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(realm1, mRoomId, roomMessage, new StructMessageOption().setGap());
                                        onReplyClick(MessageObject.create(realmRoomMessage, false, true, false));
                                    }
                                });

                            });
                        }

                        @Override
                        public void onErrorHistory(int major, int minor) {
                            G.handler.post(() -> {
                                if (major == 626) {
                                    HelperError.showSnackMessage(getResources().getString(R.string.not_found_message), false);
                                } else if (minor == 624) {
                                    HelperError.showSnackMessage(getResources().getString(R.string.ivnalid_data_provided), false);
                                } else {
                                    HelperError.showSnackMessage(getResources().getString(R.string.there_is_no_connection_to_server), false);
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onForwardClick(MessageObject messageObject) {// TODO: 12/28/20 MESSAGE_REFACTOR
        doForwardDialogMessage(messageObject, false);
    }

    @Override
    public void onForwardFromCloudClick(MessageObject messageObject) {// TODO: 12/28/20 MESSAGE_REFACTOR
        doForwardDialogMessage(messageObject, true);
    }

    @Override
    public void onSetAction(final long roomId, final long userIdR, final ProtoGlobal.ClientAction clientAction) {
        if (mRoomId == roomId && (userId != userIdR || (isCloudRoom))) {
            final String action = HelperGetAction.getAction(roomId, userIdR, chatType, clientAction);

            RealmRoom.setAction(roomId, userIdR, action);

            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (action != null && !isBot) {
                        txtLastSeen.setText(action);
                    } else if (chatType == CHAT) {
                        if (isCloudRoom) {
                            txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                            goneCallButtons();
                        } else if (isBot) {
                            txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.bot));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(txtLastSeen.getContext(), chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                    } else if (chatType == GROUP) {
                        if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                        } else {
                            txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                        }
                    }
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {
        if (chatType == CHAT && chatPeerId == userId && !isCloudRoom) {
            userStatus = AppUtils.getStatsForUser(status);
            setUserStatus(userStatus, time);
        }
    }

    @Override
    public void onLastSeenUpdate(final long userIdR, final String showLastSeen) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (chatType == CHAT && userIdR == chatPeerId && userId != userIdR) { // userId != userIdR means that , this isn't update status for own user
                    txtLastSeen.setText(showLastSeen);
                    //  avi.setVisibility(View.GONE);
                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    //}
                    // change english number to persian number
                    if (HelperCalander.isPersianUnicode)
                        txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            }
        });
    }

    /**
     * GroupAvatar and ChannelAvatar
     */
    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {
        if (!isCloudRoom) {
            avatarHandler.getAvatar(new ParamWithAvatarType(imvUserPicture, roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
        }
    }

    @Override
    public void onAvatarAddError() {
        //empty
    }

    /**
     * Channel Message Reaction
     */

    @Override
    public void onChatDelete(long roomId) {
        if (roomId == mRoomId) {
            //  finish();
            finishChat();
        }
    }

    @Override
    public void onChatDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onChangeState(final ConnectionState connectionState) {
        setConnectionText(connectionState);
    }


    private void updateShowItemInScreen() {
        /**
         * after comeback from other activity or background  the view should update
         */
        try {
            // this only notify item that show on the screen and no more
            recyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * *************************** init layout ***************************
     */

    /**
     * detect that editText have character or just have space
     */
    private boolean isMessageWrote() {
        return !getWrittenMessage().isEmpty();
    }

    /**
     * get message and remove space from start and end
     */
    private String getWrittenMessage() {
        return edtChat.getText().toString().trim();
    }

    /**
     * clear history for this room
     */
    public void clearHistory(long roomId) {
        getMessageController().clearHistoryMessage(roomId);
    }

    /**
     * message will be replied or no
     */
    private boolean isReply() {
        return mReplayLayout != null && mReplayLayout.getTag() instanceof MessageObject;
    }

    private long replyMessageId() {
        if (isReply()) {
            return ((MessageObject) mReplayLayout.getTag()).id;
        }
        return 0;
    }

    /**
     * if isReply() is true use from this method
     */
    private long getReplyMessageId() {
        return ((MessageObject) mReplayLayout.getTag()).id;
    }

    /**
     * if isReply() is true use from this method
     * if replay layout is visible, gone it
     */
    private void clearReplyView() {
        if (mReplayLayout != null) {
            mReplayLayout.setTag(null);
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void hideProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                    visibilityTextEmptyMessages();
                }
            }
        });
    }

    /**
     * clear all items that exist in view
     */
    private void clearAdapterItems() {
        mAdapter.clear();
        recyclerView.removeAllViews();
    }

    /**
     * client should send request for get user info because need to update user online timing
     */
    private void getUserInfo() {
        if (chatType == CHAT) {
            new RequestUserInfo().userInfo(chatPeerId);
        }
    }

    /**
     * call this method for set avatar for this room and this method
     * will be automatically detect id and chat type for show avatar
     */
    private void setAvatar() {
        long idForGetAvatar;
        AvatarHandler.AvatarType type;
        if (chatType == CHAT) {
            idForGetAvatar = chatPeerId;
            type = AvatarHandler.AvatarType.USER;
        } else {
            idForGetAvatar = mRoomId;
            type = AvatarHandler.AvatarType.ROOM;
        }

        final RealmRoom realmRoom = getRoom();
        if (realmRoom == null || !realmRoom.isValid()) {
            avatarHandler.getAvatar(new ParamWithAvatarType(imvUserPicture, chatPeerId).avatarSize(R.dimen.dp60).avatarType(AvatarHandler.AvatarType.USER).showMain());
        } else {
            Bitmap init = HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp60), realmRoom.getInitials(), realmRoom.getColor());
            avatarHandler.getAvatar(new ParamWithInitBitmap(imvUserPicture, idForGetAvatar).initBitmap(init).showMain());
        }
    }

    private void resetAndGetFromEnd() {
        setDownBtnGone();
        resetMessagingValue();
        setCountNewMessageZero();
        getMessages();
    }

    private ArrayList<MessageObject> getMessageStructFromSelectedItems() {// TODO: 12/28/20 MESSAGE_REFACTOR
        ArrayList<MessageObject> messageInfos = new ArrayList<>(mAdapter.getSelectedItems().size());
        for (int item : mAdapter.getSelections()) {
            messageInfos.add(mAdapter.getAdapterItem(item).messageObject);
        }
        return messageInfos;
    }

    /**
     * show current changeState for user if this room is chat
     *
     * @param status current changeState
     * @param time   if changeState is not online set latest online time
     */
    private void setUserStatus(final String status, final long time) {
        if (G.connectionState == ConnectionState.CONNECTING || G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
            setConnectionText(G.connectionState);
        } else {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    userStatus = status;
                    userTime = time;
                    if (isCloudRoom) {
                        txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                        goneCallButtons();
                        //  avi.setVisibility(View.GONE);
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        //}
                    } else if (isBot) {
                        txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.bot));
                    } else {
                        if (status != null && txtLastSeen != null) {
                            if (status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                txtLastSeen.setText(LastSeenTimeUtil.computeTime(txtLastSeen.getContext(), chatPeerId, time, true, false));
                            } else {
                                txtLastSeen.setText(status);
                            }
                            // avi.setVisibility(View.GONE);
                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            //    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                            //}
                            // change english number to persian number
                            if (HelperCalander.isPersianUnicode)
                                txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));

                            checkAction();
                        }
                    }
                }
            });
        }
    }

    private void reply(MessageObject message, boolean isEdit) {// TODO: 12/28/20 refactor message
        if (mAdapter != null) {
            Set<AbstractMessage> messages = mAdapter.getSelectedItems();
            // replay works if only one message selected
            inflateReplayLayoutIntoStub(message == null ? messages.iterator().next().messageObject : message, isEdit);

            ll_AppBarSelected.setVisibility(View.GONE);
            if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);


            mAdapter.deselect();

            edtChat.requestFocus();

            showPopup(KeyboardView.MODE_KEYBOARD);
            openKeyboardInternal();

            //disable chat search when reply a message
            if (ll_Search != null && ll_Search.isShown()) goneSearchBox(edtSearchMessage);
//
        }
    }

    private void checkAction() {
        final RealmRoom realmRoom = getRoom();
        if (realmRoom != null && realmRoom.getActionState() != null) {
            if (realmRoom.getActionState() != null && (chatType == GROUP || chatType == CHANNEL) || ((isCloudRoom || (!isCloudRoom && realmRoom.getActionStateUserId() != userId)))) {
                txtLastSeen.setText(realmRoom.getActionState());
            } else if (chatType == CHAT) {
                if (isCloudRoom) {
                    txtLastSeen.setText(G.fragmentActivity.getResources().getString(R.string.chat_with_yourself));
                    goneCallButtons();
                } else {
                    if (userStatus != null) {
                        if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                            txtLastSeen.setText(LastSeenTimeUtil.computeTime(txtLastSeen.getContext(), chatPeerId, userTime, true, false));
                        } else {
                            txtLastSeen.setText(userStatus);
                        }
                    }
                }
            } else if (chatType == GROUP) {
                if (groupParticipantsCountLabel != null && HelperString.isNumeric(groupParticipantsCountLabel) && Integer.parseInt(groupParticipantsCountLabel) == 1) {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.one_member_chat));
                } else {
                    txtLastSeen.setText(groupParticipantsCountLabel + " " + G.fragmentActivity.getResources().getString(R.string.member_chat));
                }
            }
//              change english number to persian number
            if (HelperCalander.isPersianUnicode)
                txtLastSeen.setText(convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));

        }
    }

    /**
     * change message status from sending to failed
     *
     * @param fakeMessageId messageId that create when created this message
     */
    private void makeFailed(final long fakeMessageId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoomMessage.setStatusFailedInChat(realm, fakeMessageId);
        });
    }

    private void showErrorDialog(final int time) {

        if (dialogWait != null && dialogWait.isShowing()) {
            return;
        }

        boolean wrapInScrollView = true;
        dialogWait = new MaterialDialog.Builder(G.currentActivity).title(G.fragmentActivity.getResources().getString(R.string.title_limit_chat_to_unknown_contact)).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();
        if (v == null) {
            return;
        }
        //dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(true);
        final TextView remindTime = v.findViewById(R.id.remindTime);
        final TextView txtText = v.findViewById(R.id.textRemindTime);
        txtText.setText(G.fragmentActivity.getResources().getString(R.string.text_limit_chat_to_unknown_contact));
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                long s = seconds % 60;
                long m = (seconds / 60) % 60;
                long h = (seconds / (60 * 60)) % 24;
                remindTime.setText(String.format("%d:%02d:%02d", h, m, s));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    /**
     * open profile for this room or user profile if room is chat
     */
    private void goToProfile() {
        if (getActivity() != null) {
            if (chatType == CHAT) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(mRoomId, chatPeerId, CHAT.toString())).setReplace(false).load();
            } else if (chatType == GROUP) {
                if (!isChatReadOnly) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentGroupProfile.newInstance(mRoomId, isNotJoin)).setReplace(false).load();
                }
            } else if (chatType == CHANNEL) {
                if (!isNotJoin) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentChannelProfile.newInstance(mRoomId, isNotJoin)).setReplace(false).load();
                }
            }
        }

        if (!isNotJoin && mAdapter != null && mAdapter.getSelections().size() > 0)
            mAdapter.deselect();
    }

    /**
     * copy text
     */
    private void copySelectedItemTextToClipboard() {
        String copyText = "";
        for (AbstractMessage abstractMessage : mAdapter.getSelectedItems()) {
            String message = abstractMessage.messageObject.forwardedMessage != null ? abstractMessage.messageObject.forwardedMessage.message : abstractMessage.messageObject.message;
            if (message == null || message.length() == 0) {
                continue;
            }

            if (copyText.length() > 0) {
                copyText = copyText + "\n" + message;
            } else {
                copyText = message;
            }
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Copied Text", copyText));
        Toast.makeText(ll_AppBarSelected.getContext(), R.string.copied, Toast.LENGTH_SHORT).show();

        mAdapter.deselect();
        ll_AppBarSelected.setVisibility(View.GONE);
        if (isPinAvailable) pinedMessageLayout.setVisibility(View.VISIBLE);
        clearReplyView();
    }

    private void onSelectRoomMenu(String message, long item) {
        switch (message) {
            case "txtMuteNotification":
                muteNotification(item);
                break;
            case "txtClearHistory":
                clearHistory(item);
                break;
            case "txtDeleteChat":
                deleteChat(item);
                break;
        }
    }

    private void deleteChat(final long chatId) {
        new RequestChatDelete().chatDelete(chatId);
    }

    private void muteNotification(final long roomId) {

        isMuteNotification = !isMuteNotification;
        new RequestClientMuteRoom().muteRoom(roomId, isMuteNotification);

        if (isMuteNotification) {
            txtChannelMute.setText(R.string.unmute);
            iconChannelMute.setText(R.string.unmute_icon);
            iconMute.setVisibility(View.VISIBLE);
        } else {
            txtChannelMute.setText(R.string.mute);
            iconChannelMute.setText(R.string.mute_icon);
            iconMute.setVisibility(View.GONE);
        }
    }

    private void removeLayoutUnreadMessage() {
        /**
         * remove unread layout message if already exist in chat list
         */
        if (isShowLayoutUnreadMessage) {
            for (int i = (mAdapter.getItemCount() - 1); i >= 0; i--) {
                if (mAdapter.getItem(i) instanceof UnreadMessage) {
                    mAdapter.remove(i);
                    break;
                }
            }
        }
        isShowLayoutUnreadMessage = false;
    }

    private void setDownBtnVisible() {
        if (llScrollNavigate != null)
            llScrollNavigate.setVisibility(View.VISIBLE);
        isScrollEnd = true;
    }

    private void setDownBtnGone() {
        if (llScrollNavigate != null)
            llScrollNavigate.setVisibility(View.GONE);
        isScrollEnd = false;
    }

    private void setBtnDownVisible(RealmRoomMessage realmRoomMessage) {
        if (isEnd()) {
            scrollToEnd();
        } else {
            if (countNewMessage == 0) {
                removeLayoutUnreadMessage();
            }
            countNewMessage++;
            setDownBtnVisible();
            if (txtNewUnreadMessage != null) {
                txtNewUnreadMessage.getTextView().setText(countNewMessage + "");
                txtNewUnreadMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * check difference position to end of adapter
     *
     * @return true if lower than END_CHAT_LIMIT otherwise return false
     */
    private boolean isEnd() {
        if (addToView) {
            return ((recyclerView.getLayoutManager()) == null) || ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() + END_CHAT_LIMIT > recyclerView.getAdapter().getItemCount();
        }
        return false;
        //return addToView && ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() + END_CHAT_LIMIT > recyclerView.getAdapter().getItemCount();
    }

    /**
     * open fragment show image and show all image for this room
     */
    private void showImage(final MessageObject messageObject, View view) {

        if (getActivity() != null) {
            if (!isAdded() || getActivity().isFinishing()) {
                return;
            }

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            FragmentShowImage fragment = FragmentShowImage.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("RoomId", mRoomId);
            bundle.putInt("TYPE", messageObject.messageType);
            bundle.putLong("SelectedImage", messageObject.id);
            fragment.setArguments(bundle);

            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    /**
     * scroll to bottom if unread not exits otherwise go to unread line
     * hint : just do in loaded message
     */
    private void scrollToEnd() {
        if (recyclerView == null || recyclerView.getAdapter() == null) return;
        if (recyclerView.getAdapter().getItemCount() < 2) {
            return;
        }

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                    int lastPosition = llm.findLastVisibleItemPosition();
                    if (lastPosition + 30 > mAdapter.getItemCount()) {
                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    } else {
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    private void storingLastPosition() {// TODO: 12/28/20 MESSAGE_REFACTOR
        try {
            if (recyclerView != null && mAdapter != null) {

                int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (mAdapter.getItem(firstVisiblePosition) instanceof TimeItem || mAdapter.getItem(firstVisiblePosition) instanceof UnreadMessage) {
                    firstVisiblePosition++;
                }

                if (mAdapter.getItem(firstVisiblePosition) instanceof TimeItem || mAdapter.getItem(firstVisiblePosition) instanceof UnreadMessage) {
                    firstVisiblePosition++;
                }

                long lastScrolledMessageID = 0;

                if (mAdapter.getAdapterItemCount() - lastVisiblePosition > Config.STORE_MESSAGE_POSITION_LIMIT) {
                    lastScrolledMessageID = mAdapter.getItem(firstVisiblePosition).messageObject.id;
                }

                saveMessageIdPositionState(lastScrolledMessageID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {


        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && G.twoPaneMode) {
            G.maxChatBox = width - (width / 3) - ViewMaker.i_Dp(R.dimen.dp80);
        } else {
            G.maxChatBox = width - ViewMaker.i_Dp(R.dimen.dp80);
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateShowItemInScreen();
                checkToolbarNameSize();
            }
        }, 300);
        super.onConfigurationChanged(newConfig);

        if (mAttachmentPopup != null && mAttachmentPopup.isShowing) mAttachmentPopup.updateHeight();

    }

    /**
     * save latest messageId position that user saw in chat before close it
     */
    private void saveMessageIdPositionState(final long messageId) {
        RealmRoom.setLastScrollPosition(mRoomId, messageId, firstVisiblePositionOffset);
    }

    private void sendStickerAsMessage(StructIGSticker structIGSticker) {

        String additional = new Gson().toJson(structIGSticker);
        long identity = AppUtils.makeRandomId();
        int[] imageSize = AndroidUtils.getImageDimens(structIGSticker.getPath());
        RealmRoomMessage roomMessage = new RealmRoomMessage();
        roomMessage.setMessageId(identity);
        roomMessage.setMessageType(ProtoGlobal.RoomMessageType.STICKER);
        roomMessage.setRoomId(mRoomId);
        roomMessage.setMessage(structIGSticker.getName());
        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
        roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
        roomMessage.setCreateTime(TimeUtils.currentLocalTime());

        RealmAdditional realmAdditional = new RealmAdditional();
        realmAdditional.setId(AppUtils.makeRandomId());
        realmAdditional.setAdditionalType(structIGSticker.isGiftSticker() ? AdditionalType.GIFT_STICKER : AdditionalType.STICKER);
        realmAdditional.setAdditionalData(additional);

        roomMessage.setRealmAdditional(realmAdditional);

        RealmAttachment realmAttachment = new RealmAttachment();
        realmAttachment.setId(identity);
        realmAttachment.setLocalFilePath(structIGSticker.getPath());

        realmAttachment.setWidth(imageSize[0]);
        realmAttachment.setHeight(imageSize[1]);
        realmAttachment.setSize(new File(structIGSticker.getPath()).length());
        realmAttachment.setName(new File(structIGSticker.getPath()).getName());
        realmAttachment.setDuration(0);

        roomMessage.setAttachment(realmAttachment);

        roomMessage.getAttachment().setToken(structIGSticker.getToken());
        roomMessage.setAuthorHash(RealmUserInfo.getCurrentUserAuthorHash());
        roomMessage.setShowMessage(true);
        roomMessage.setCreateTime(TimeUtils.currentLocalTime());

        if (isReply()) {
            RealmRoomMessage copyReplyMessage = DbManager.getInstance().doRealmTask(realm -> {
                RealmRoomMessage copyReplyMessage1 = realm.where(RealmRoomMessage.class).equalTo("messageId", getReplyMessageId()).findFirst();
                if (copyReplyMessage1 != null) {
                    return realm.copyFromRealm(copyReplyMessage1);
                }
                return null;
            });

            if (copyReplyMessage != null) {
                roomMessage.setReplyTo(copyReplyMessage);
            }
        }

        new Thread(() -> DbManager.getInstance().doRealmTransaction(realm -> {
            realm.copyToRealmOrUpdate(roomMessage);
            RealmStickerItem stickerItem = realm.where(RealmStickerItem.class).equalTo("id", structIGSticker.getId()).findFirst();
            if (stickerItem != null && stickerItem.isValid()) {
                stickerItem.setRecent();
            }
        })).start();

        MessageObject messageObject = MessageObject.create(roomMessage);

        if (structIGSticker.isGiftSticker()) {
            mAdapter.add(new GiftStickerItem(mAdapter, chatType, FragmentChat.this).setMessage(messageObject));
        } else {
            if (structIGSticker.getType() == StructIGSticker.ANIMATED_STICKER)
                mAdapter.add(new AnimatedStickerItem(mAdapter, chatType, FragmentChat.this).setMessage(messageObject));
            else
                mAdapter.add(new StickerItem(mAdapter, chatType, FragmentChat.this).setMessage(messageObject));
        }

        scrollToEnd();

        if (isReply()) {
            mReplayLayout.setTag(null);
            mReplayLayout.setVisibility(View.GONE);
        }

        if (FragmentChat.structIGSticker != null) {
            FragmentChat.structIGSticker = null;
            if (getActivity() instanceof ActivityMain) {
                ((ActivityMain) getActivity()).checkHasSharedData(false);
            }
        }
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        if (imvSmileButton != null)
            imvSmileButton.setText(drawableResourceId);
    }

    /**
     * *************************** draft ***************************
     */
    private void setDraftMessage(final int requestCode) {

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listPathString == null) return;
                if (listPathString.size() < 1) return;
                if (listPathString.get(0) == null) return;
                String filename = listPathString.get(0).substring(listPathString.get(0).lastIndexOf("/") + 1);
                switch (requestCode) {
                    case AttachFile.request_code_TAKE_PICTURE:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                        break;
                    case AttachFile.requestOpenGalleryForImageMultipleSelect:
                        if (listPathString.size() == 1) {
                            if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                                txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                            } else {
                                txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.gif_selected_for_send) + "\n" + filename);
                            }
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.image_selected_for_send) + "\n" + filename);
                        }

                        break;

                    case AttachFile.requestOpenGalleryForVideoMultipleSelect:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.multi_video_selected_for_send) + "\n" + filename);
                        break;
                    case request_code_VIDEO_CAPTURED:

                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.video_selected_for_send));
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.video_selected_for_send) + "\n" + filename);
                        }
                        break;

                    case AttachFile.request_code_pic_audi:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.audio_selected_for_send) + "\n" + filename);
                        } else {
                            txtFileNameForSend.setText(listPathString.size() + G.fragmentActivity.getResources().getString(R.string.audio_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_pic_file:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.file_selected_for_send) + "\n" + filename);
                        break;
                    case AttachFile.request_code_open_document:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.file_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_paint:
                        if (listPathString.size() == 1) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.pain_selected_for_send) + "\n" + filename);
                        }
                        break;
                    case AttachFile.request_code_contact_phone:
                        txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.phone_selected_for_send) + "\n" + filename);
                        break;
                    case IntentRequests.REQ_CROP:
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.crop_selected_for_send) + "\n" + filename);
                        } else {
                            txtFileNameForSend.setText(G.fragmentActivity.getResources().getString(R.string.gif_selected_for_send) + "\n" + filename);
                        }
                        break;
                }
            }
        }, 100);
    }

    private void showDraftLayout() {
        /**
         * onActivityResult happens before onResume, so Presenter does not have View attached. because use handler
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (listPathString == null) return;
                if (listPathString.size() < 1) return;
                if (listPathString.get(0) == null) return;

                if (ll_attach_text == null) { // have null error , so reInitialize for avoid that

                    ll_attach_text = rootView.findViewById(R.id.ac_ll_attach_text);
                    layoutAttachBottom = rootView.findViewById(R.id.ll_chatRoom_send);
                    imvSendButton = rootView.findViewById(R.id.btn_chatRoom_send);
                }

                ll_attach_text.setVisibility(View.VISIBLE);
                // set maxLength  when layout attachment is visible
                edtChat.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Config.MAX_TEXT_ATTACHMENT_LENGTH)});

                sendButtonVisibilityWithNoAnim(true);
            }
        }, 100);
    }


    private void sendButtonVisibilityWithNoAnim(boolean visibility) {
        layoutAttachBottom.setVisibility(visibility ? View.GONE : View.VISIBLE);
        layoutAttachBottom.clearAnimation();
        imvSendButton.clearAnimation();
        imvSendButton.setVisibility(visibility ? View.VISIBLE : View.GONE);
        chatRoom_send_container.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }


    private boolean isSendVisibilityAnimInProcess;
    private boolean isAttachVisibilityAnimInProcess;
    private Animation animGone, animVisible;

    private void sendButtonVisibility(boolean visibility) {

        if (animGone == null || animVisible == null) {
            animGone = AnimationUtils.loadAnimation(imvSendButton.getContext(), R.anim.fade_scale_hide);
            animVisible = AnimationUtils.loadAnimation(imvSendButton.getContext(), R.anim.fade_scale_show);
        }

        //animGone.setDuration(70);
        //animVisible.setDuration(70);

        if (!visibility && isSendVisibilityAnimInProcess) {
            animGone.reset();
            animVisible.reset();
            imvSendButton.clearAnimation();
            layoutAttachBottom.clearAnimation();
            isSendVisibilityAnimInProcess = false;
            isAttachVisibilityAnimInProcess = false;
            // layoutAttachBottom.setVisibility(View.GONE);
        }

        if (visibility && isAttachVisibilityAnimInProcess) {
            animGone.reset();
            animVisible.reset();
            imvSendButton.clearAnimation();
            layoutAttachBottom.clearAnimation();
            isSendVisibilityAnimInProcess = false;
            isAttachVisibilityAnimInProcess = false;
            chatRoom_send_container.setVisibility(View.GONE);
            imvSendButton.setVisibility(View.GONE);
        }

        if (!visibility && !layoutAttachBottom.isShown()) {
            if (isAttachVisibilityAnimInProcess) return;
            attachLayoutAnimateVisible();
        }

        if (visibility && !imvSendButton.isShown()) {
            if (isSendVisibilityAnimInProcess) return;
            sendButtonAnimateVisible();
        }

    }

    private void sendButtonAnimateVisible() {

        layoutAttachBottom.startAnimation(animGone);
        isSendVisibilityAnimInProcess = true;

        animGone.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isSendVisibilityAnimInProcess = true;
                isAttachVisibilityAnimInProcess = false;
                layoutAttachBottom.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutAttachBottom.setVisibility(View.GONE);
                imvSendButton.startAnimation(animVisible);
                imvSendButton.setVisibility(View.VISIBLE);
                chatRoom_send_container.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animVisible.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isSendVisibilityAnimInProcess = false;
                imvSendButton.clearAnimation();
                layoutAttachBottom.clearAnimation();
                edtChat.requestLayout();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void attachLayoutAnimateVisible() {

        imvSendButton.startAnimation(animGone);
        isAttachVisibilityAnimInProcess = true;

        animGone.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imvSendButton.setVisibility(View.VISIBLE);
                chatRoom_send_container.setVisibility(View.VISIBLE);
                isSendVisibilityAnimInProcess = false;
                isAttachVisibilityAnimInProcess = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imvSendButton.setVisibility(View.GONE);
                chatRoom_send_container.setVisibility(View.GONE);
                layoutAttachBottom.startAnimation(animVisible);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animVisible.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layoutAttachBottom.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAttachVisibilityAnimInProcess = false;
                imvSendButton.clearAnimation();
                layoutAttachBottom.clearAnimation();
                edtChat.requestLayout();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * *************************** inner classes ***************************
     */

    private void setDraft() {
        if (!isNotJoin) {
            if (edtChat == null) {
                return;
            }

            if (mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE) {
                replyToMessageId = replyMessageId();
            } else {
                replyToMessageId = 0;
            }

            String message = edtChat.getText().toString();
            if (!message.trim().isEmpty() || ((mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE))) {
                hasDraft = true;
                RealmRoom.setDraft(mRoomId, message, replyToMessageId, chatType);
            } else {
                clearDraftRequest();
            }
        }
    }

    private void getDraft() {
        RealmRoom realmRoom = getRoom();
        if (realmRoom != null) {
            RealmRoomDraft draft = realmRoom.getDraft();
            if (draft != null && draft.getMessage().length() > 0) {
                hasDraft = true;
                edtChat.setText(EmojiManager.getInstance().replaceEmoji(draft.getMessage(), edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false));
                edtChat.setSelection(edtChat.getText().toString().length());
            }
        }
//        clearLocalDraft();
    }

    private void clearLocalDraft() {
        RealmRoom.clearDraft(mRoomId);
    }

    private void clearDraftRequest() {
        if (hasDraft) {
            hasDraft = false;
            if (chatType == CHAT) {
                new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, "", 0);
            } else if (chatType == GROUP) {
                new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, "", 0);
            } else if (chatType == CHANNEL) {
                new RequestChannelUpdateDraft().channelUpdateDraft(mRoomId, "", 0);
            }

            clearLocalDraft();
        }
    }

    /**
     * *************************** sheared data ***************************
     */


    private void insertShearedData() {
        /**
         * run this method with delay , because client get local message with delay
         * for show messages with async changeState and before run getLocalMessage this shared
         * item added to realm and view, and after that getLocalMessage called and new item
         * got from realm and add to view again but in this time from getLocalMessage method
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HelperGetDataFromOtherApp.hasSharedData) {
                    HelperGetDataFromOtherApp.hasSharedData = false;

                    boolean isOpenEditImageFragment = false;
                    boolean isAllowToClearChatEditText = true;
                    for (HelperGetDataFromOtherApp.SharedData sharedData : HelperGetDataFromOtherApp.sharedList) {

                        edtChat.setText(EmojiManager.getInstance().replaceEmoji(sharedData.message, edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false));

                        switch (sharedData.fileType) {
                            case message:
                                imvSendButton.performClick();
                                break;
                            case video:
                                if (HelperGetDataFromOtherApp.sharedList.size() == 1) {
                                    mainVideoPath = sharedData.address;
                                    if (mainVideoPath == null) return;

                                    if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
                                        Intent intent = new Intent(G.fragmentActivity, ActivityTrimVideo.class);
                                        intent.putExtra("PATH", mainVideoPath);
                                        startActivityForResult(intent, AttachFile.request_code_trim_video);
                                        isAllowToClearChatEditText = false;
                                    } else {
                                        sendMessage(request_code_VIDEO_CAPTURED, mainVideoPath);
                                    }

                                } else {
                                    sendMessage(request_code_VIDEO_CAPTURED, sharedData.address);
                                }
                                break;
                            case file:
                                sendMessage(AttachFile.request_code_open_document, sharedData.address);
                                break;
                            case audio:
                                sendMessage(AttachFile.request_code_pic_audi, sharedData.address);
                                break;
                            case image:
                                //maybe share data was more than one ... add to list then after for open edit image
                                FragmentEditImage.insertItemList(sharedData.address, sharedData.message, false);
                                isOpenEditImageFragment = true;
                                //sendMessage(AttachFile.request_code_TAKE_PICTURE, sharedData.address);
                                break;
                        }

                        if (isAllowToClearChatEditText) edtChat.setText("");
                    }

                    if (isOpenEditImageFragment && getActivity() != null) {

                        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, FragmentEditImage.itemGalleryList.size() - 1);
                        fragmentEditImage.setIsReOpenChatAttachment(false);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                    }

                    HelperGetDataFromOtherApp.sharedList.clear();
                    //update main room list ui after share done
                    if (getActivity() instanceof ActivityMain) {
                        ((ActivityMain) getActivity()).checkHasSharedData(false);
                    }

                }
            }
        }, 300);
    }

    private void shearedLinkDataToOtherProgram(MessageObject messageObject) {
        // when chat is channel this method will be called

        if (messageObject == null) return;
        RealmRoom room = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", messageObject.roomId).findFirst();
        });

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://igap.net/" + room.getChannelRoom().getUsername() + "/" + messageObject.id);
        startActivity(Intent.createChooser(intent, G.context.getString(R.string.share_link_item_dialog)));
    }

    private void shareMediaLink(MessageObject messageObject) {
        if (messageObject == null) return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, messageObject.attachment.publicUrl);
        startActivity(Intent.createChooser(intent, G.context.getString(R.string.share_link_item_dialog)));
    }

    private void shearedDataToOtherProgram(MessageObject messageObject) {
        if (messageObject == null)
            return;

        try {
            isShareOk = true;
            Intent intent = new Intent(Intent.ACTION_SEND);
            String chooserDialogText = "";

            int type = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.messageType : messageObject.messageType;

            switch (type) {

                case TEXT_VALUE:
                    intent.setType("text/plain");
                    String message = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.message : messageObject.message;
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    break;
                case CONTACT_VALUE:
                    intent.setType("text/plain");
                    String messageContact;
                    if (messageObject.forwardedMessage != null) {
                        messageContact = messageObject.forwardedMessage.contact.firstName + " " + messageObject.forwardedMessage.contact.lastName + "\n" + messageObject.forwardedMessage.contact.phones;
                    } else {
                        messageContact = messageObject.contact.firstName + "\n" + messageObject.contact.phones;
                    }
                    intent.putExtra(Intent.EXTRA_TEXT, messageContact);
                    break;
                case LOCATION_VALUE:
                    intent.setType("image/*");
                    String imagePathPosition = messageObject.forwardedMessage != null ? AppUtils.getLocationPath(messageObject.forwardedMessage.location.lat, messageObject.forwardedMessage.location.lan) : AppUtils.getLocationPath(messageObject.location.lat, messageObject.location.lan);
                    if (imagePathPosition != null) {
                        intent.putExtra(Intent.EXTRA_STREAM, AppUtils.createtUri(new File(imagePathPosition)));
                    }
                    break;
                case VOICE_VALUE:
                case AUDIO_VALUE:
                case AUDIO_TEXT_VALUE:
                    AppUtils.shareItem(intent, messageObject);
                    intent.setType("audio/*");
                    chooserDialogText = getActivity().getResources().getString(R.string.share_audio_file);
                    break;
                case IMAGE_VALUE:
                case IMAGE_TEXT_VALUE:
                    AppUtils.shareItem(intent, messageObject);
                    intent.setType("image/*");
                    chooserDialogText = getActivity().getResources().getString(R.string.share_image);
                    break;
                case VIDEO_VALUE:
                case VIDEO_TEXT_VALUE:
                    AppUtils.shareItem(intent, messageObject);
                    intent.setType("video/*");
                    chooserDialogText = getActivity().getResources().getString(R.string.share_video_file);
                    break;
                case FILE_VALUE:
                case FILE_TEXT_VALUE:
                    String filePath = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.getAttachment().filePath : messageObject.getAttachment().filePath;
                    if (filePath != null) {
                        Uri uri = AppUtils.createtUri(new File(filePath));

                        ContentResolver cR = context.getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String mimeType = mime.getExtensionFromMimeType(cR.getType(uri));

                        if (mimeType == null || mimeType.length() < 1) {
                            mimeType = "*/*";
                        } else {
                            mimeType = "application/*" + mimeType;
                        }
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.setType(mimeType);
                        chooserDialogText = getActivity().getResources().getString(R.string.share_file);
                    } else {

                        isShareOk = false;
                    }
                    break;
            }

            if (!isShareOk) {
                G.handler.post(() -> Toast.makeText(context, R.string.file_not_download_yet, Toast.LENGTH_SHORT).show());
                return;
            }

            startActivity(Intent.createChooser(intent, chooserDialogText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init layout for hashtag up and down
     */
    private void initLayoutHashNavigationCallback() {

        hashListener = new OnComplete() {
            @Override
            public void complete(boolean result, String text, String messageId) {
                if (ll_Search != null && ll_Search.getVisibility() == View.VISIBLE && !text.startsWith("#")) {
                    if (!initHash) {
                        initHash = true;
                        initHashView();
                    }
                    searchHash.setHashString(text);
                    searchHash.setPosition(messageId);
                    ll_navigateHash.setVisibility(View.VISIBLE);
                    viewAttachFile.setVisibility(View.GONE);
                } else if (text.startsWith("#")) {
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.ac_ll_parent, SearchFragment.newInstance(text, true)).commit();
                }

//                if (chatType == CHANNEL || chatType == GROUP) {
//                    if (!initHash) {
//                        initHash = true;
//                        initHashView();
//                    }
//                    searchHash.setHashString(text);
//                    searchHash.setPosition(messageId);
//                    ll_navigateHash.setVisibility(View.VISIBLE);
//                    viewAttachFile.setVisibility(View.GONE);
//                } else {
//
//                }
                if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER) {
                    if (layoutMute != null) layoutMute.setVisibility(View.GONE);
                }
            }
        };
    }

    /**
     * init layout hashtak for up and down
     */
    private void initHashView() {
        ll_navigateHash = rootView.findViewById(R.id.ac_ll_hash_navigation);
        btnUpHash = rootView.findViewById(R.id.ac_btn_hash_up);
        btnDownHash = rootView.findViewById(R.id.ac_btn_hash_down);
        txtHashCounter = rootView.findViewById(R.id.ac_txt_hash_counter);

        searchHash = new SearchHash();

        btnHashLayoutClose = rootView.findViewById(R.id.ac_btn_hash_close);
        btnHashLayoutClose.setOnClickListener(v -> goneSearchBox(v));

        btnUpHash.setOnClickListener(view -> searchHash.upHash());

        btnDownHash.setOnClickListener(view -> searchHash.downHash());
    }

    /**
     * manage need showSpamBar for user or no
     */
    private void showSpamBar() {
        RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRegisteredInfo.getRegistrationInfo(realm, chatPeerId);
        });
        RealmContacts realmContacts = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmContacts.class).equalTo("id", chatPeerId).findFirst();
        });
        if (realmRegisteredInfo != null && realmRegisteredInfo.getId() != AccountManager.getInstance().getCurrentUser().getId()) {
            if (phoneNumber == null) {
                if (realmContacts == null && chatType == CHAT && !isChatReadOnly) {
                    initSpamBarLayout(realmRegisteredInfo);
                    vgSpamUser.setVisibility(View.VISIBLE);
                }
            }

            if (realmRegisteredInfo.getId() != AccountManager.getInstance().getCurrentUser().getId()) {
                if (!realmRegisteredInfo.getDoNotshowSpamBar()) {

                    if (realmRegisteredInfo.isBlockUser()) {
                        initSpamBarLayout(realmRegisteredInfo);
                        blockUser = true;
                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                        vgSpamUser.setVisibility(View.VISIBLE);
                    } else {
                        if (vgSpamUser != null) {
                            vgSpamUser.setVisibility(View.GONE);
                        }
                    }
                }
            }

            if (realmContacts != null && realmRegisteredInfo.getId() != AccountManager.getInstance().getCurrentUser().getId()) {
                if (realmContacts.isBlockUser()) {
                    if (!realmRegisteredInfo.getDoNotshowSpamBar()) {
                        initSpamBarLayout(realmRegisteredInfo);
                        blockUser = true;
                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                        vgSpamUser.setVisibility(View.VISIBLE);
                    } else {
                        initSpamBarLayout(realmRegisteredInfo);
                        blockUser = true;
                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                        vgSpamUser.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (vgSpamUser != null) {
                        vgSpamUser.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    /**
     * init spamBar layout
     */
    private void initSpamBarLayout(final RealmRegisteredInfo registeredInfo) {
        vgSpamUser = rootView.findViewById(R.id.layout_add_contact);
        txtSpamUser = rootView.findViewById(R.id.chat_txt_addContact);
        txtSpamClose = rootView.findViewById(R.id.chat_txt_close);
        txtSpamClose.setOnClickListener(view -> {
            vgSpamUser.setVisibility(View.GONE);
            if (registeredInfo != null) {
                long registeredInfoID = registeredInfo.getId();
                new Thread(() -> {
                    DbManager.getInstance().doRealmTransaction(realm1 -> {
                        RealmRegisteredInfo registeredInfo2 = realm1.where(RealmRegisteredInfo.class).equalTo("id", registeredInfoID).findFirst();
                        if (registeredInfo2 != null) {
                            registeredInfo2.setDoNotshowSpamBar(true);
                        }
                    });

                }).start();
            }
        });

        txtSpamUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockUser) {
                    G.onUserContactsUnBlock = new OnUserContactsUnBlock() {
                        @Override
                        public void onUserContactsUnBlock(final long userId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = false;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.block_user));
                                    }
                                }
                            });
                        }
                    };

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.unblock_the_user).content(R.string.unblock_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestUserContactsUnblock().userContactsUnblock(chatPeerId);
                        }
                    }).negativeText(R.string.cancel).show();
                } else {

                    G.onUserContactsBlock = new OnUserContactsBlock() {
                        @Override
                        public void onUserContactsBlock(final long userId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = true;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(G.fragmentActivity.getResources().getString(R.string.un_block_user));
                                    }
                                }
                            });
                        }
                    };

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.block_the_user).content(R.string.block_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new RequestUserContactsBlock().userContactsBlock(chatPeerId);
                        }
                    }).negativeText(R.string.cancel).show();

                }
            }
        });
    }

    /**
     * initialize bottomSheet for use in attachment for forward
     */


    private void initAttachForward(boolean isMessage) {
        canClearForwardList = true;
        multiForwardList = new ArrayList<>();
        viewBottomSheetForward = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_forward, null);

        fastItemAdapterForward = new FastItemAdapter();

        EditText edtSearch = viewBottomSheetForward.findViewById(R.id.edtSearch);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                hideKeyboard();
            return true;
        });
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        final AppCompatTextView textSend = viewBottomSheetForward.findViewById(R.id.txtSend);
        textSend.setVisibility(View.GONE);
        final RecyclerView rcvItem = viewBottomSheetForward.findViewById(R.id.rcvBottomSheetForward);
        rcvItem.setLayoutManager(new GridLayoutManager(rcvItem.getContext(), 4, GridLayoutManager.VERTICAL, false));
        rcvItem.setItemViewCacheSize(100);
        rcvItem.setAdapter(fastItemAdapterForward);
        edtSearch.setBackground(new Theme().tintDrawable(edtSearch.getBackground(), getContext(), R.attr.iGapDividerLine));

        bottomSheetDialogForward = new BottomSheetDialog(getActivity(), R.style.BaseBottomSheetDialog);
        bottomSheetDialogForward.setContentView(viewBottomSheetForward);
        final BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) viewBottomSheetForward.getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        fastItemAdapterForward.getItemFilter().withFilterPredicate((IItemAdapter.Predicate<ItemBottomSheetForward>) (item, constraint) -> item.structBottomSheetForward.getDisplayName().toLowerCase().contains(String.valueOf(constraint)));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fastItemAdapterForward.filter(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        viewBottomSheetForward.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBehavior.setPeekHeight(viewBottomSheetForward.getHeight());
                    viewBottomSheetForward.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        //height is ready


        textSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canClearForwardList = false;
                forwardToChatRoom(mListForwardNotExict, isMessage);
                prgWaiting.setVisibility(View.VISIBLE);
                viewBottomSheetForward.setEnabled(false);
            }
        });

        onForwardBottomSheet = structBottomSheetForward -> {

            if (structBottomSheetForward.isNotExistRoom()) {
                if (structBottomSheetForward.isChecked()) {
                    mListForwardNotExict.add(structBottomSheetForward);
                } else {
                    mListForwardNotExict.remove(structBottomSheetForward);
                }
            } else {
                if (structBottomSheetForward.isChecked()) {
                    multiForwardList.add(structBottomSheetForward.getId());
                } else {
                    multiForwardList.remove(structBottomSheetForward.getId());
                }
            }

            if (mListForwardNotExict.size() + multiForwardList.size() > 0) {
                textSend.setVisibility(View.VISIBLE);
            } else {
                textSend.setVisibility(View.GONE);
            }
        };

        bottomSheetDialogForward.show();

        bottomSheetDialogForward.setOnDismissListener(dialog -> {
            if (canClearForwardList) {
                removeForwardModeFromRoomList();
                mForwardMessages = null;
            }
        });
    }

    private void cardToCardClick(View v) {
        if (v == null) {
            showDraftLayout();
        } else {
            if ((Boolean) v.getTag()) {
                mAttachmentPopup.dismiss();
                showDraftLayout();
            } else {
                mAttachmentPopup.dismiss();
                HelperError.showSnackMessage(G.currentActivity.getString(R.string.disable), false);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void inflateReplayLayoutIntoStub(MessageObject messageObject, boolean isEdit) {// TODO: 12/28/20  refactor message
        if (rootView.findViewById(R.id.replayLayoutAboveEditText) == null) {
            ViewStubCompat stubView = rootView.findViewById(R.id.replayLayoutStub);
            stubView.setInflatedId(R.id.replayLayoutAboveEditText);
            stubView.setLayoutResource(R.layout.layout_chat_reply);
            stubView.inflate();

            inflateReplayLayoutIntoStub(messageObject, isEdit);
        } else {
            mReplayLayout = rootView.findViewById(R.id.replayLayoutAboveEditText);
            mReplayLayout.setVisibility(View.VISIBLE);
            TextView replayTo = mReplayLayout.findViewById(R.id.replayTo);
            replayTo.setTypeface(ResourcesCompat.getFont(mReplayLayout.getContext(), R.font.main_font));
            TextView replayFrom = mReplayLayout.findViewById(R.id.replyFrom);
            replayFrom.setTypeface(ResourcesCompat.getFont(mReplayLayout.getContext(), R.font.main_font));

            FontIconTextView replayIcon = rootView.findViewById(R.id.lcr_imv_replay);
            if (isEdit)
                replayIcon.setText(getString(R.string.edit_icon));
            else
                replayIcon.setText(getString(R.string.reply_icon));

            ImageView thumbnail = mReplayLayout.findViewById(R.id.thumbnail);
            TextView closeReplay = mReplayLayout.findViewById(R.id.cancelIcon);
            closeReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //clearReplyView();

                    if (isEdit)
                        removeEditedMessage();
                    else
                        clearReplyView();

                }
            });
            thumbnail.setVisibility(View.VISIBLE);
            if (messageObject.isForwarded()) {
                AppUtils.rightFileThumbnailIcon(thumbnail, messageObject.forwardedMessage.messageType, messageObject.forwardedMessage);
                String _text = AppUtils.conversionMessageType(messageObject.forwardedMessage.messageType);
                if (_text != null && _text.length() > 0) {
                    ReplySetText(replayTo, _text);
                } else {
                    ReplySetText(replayTo, messageObject.forwardedMessage.message);
                }
            } else {
                AppUtils.rightFileThumbnailIcon(thumbnail, messageObject.messageType, messageObject);
                String _text = AppUtils.conversionMessageType(messageObject.messageType);
                if (_text != null && _text.length() > 0) {
                    ReplySetText(replayTo, _text);
                } else {
                    ReplySetText(replayTo, messageObject.message);
                }
            }

            if (!isEdit) {
                if (chatType == CHANNEL) {
                    RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
                        return realm.where(RealmRoom.class).equalTo("id", messageObject.roomId).findFirst();
                    });
                    if (realmRoom != null) {
                        replayFrom.setText(EmojiManager.getInstance().replaceEmoji(realmRoom.getTitle(), replayFrom.getPaint().getFontMetricsInt()));
                    }
                } else {
                    RealmRegisteredInfo userInfo = DbManager.getInstance().doRealmTask(realm -> {
                        return RealmRegisteredInfo.getRegistrationInfo(realm, messageObject.userId);
                    });
                    if (userInfo != null) {
                        replayFrom.setText(EmojiManager.getInstance().replaceEmoji(userInfo.getDisplayName(), replayFrom.getPaint().getFontMetricsInt()));
                    }
                }
            } else {
                replayFrom.setText(getString(R.string.edit));
            }

            // I set tag to retrieve it later when sending message
            mReplayLayout.setTag(messageObject);
        }
    }

    private void ReplySetText(TextView replayTo, String text) {
        ArrayList<Tuple<Integer, Integer>> a = AbstractMessage.getBoldPlaces(text);
        text = AbstractMessage.removeBoldMark(text, a);
        replayTo.setText(EmojiManager.getInstance().replaceEmoji(text, replayTo.getPaint().getFontMetricsInt()));
    }

    private void initLayoutChannelFooter() {
        ViewGroup layoutAttach = rootView.findViewById(R.id.layout_attach_file);


        layoutAttach.setVisibility(View.GONE);
        if (!isNotJoin) layoutMute.setVisibility(View.VISIBLE);


        layoutMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSelectRoomMenu("txtMuteNotification", mRoomId);
            }
        });


        if (isMuteNotification) {
            txtChannelMute.setText(R.string.unmute);
            iconChannelMute.setText(R.string.unmute_icon);
        } else {
            txtChannelMute.setText(R.string.mute);
            iconChannelMute.setText(R.string.mute_icon);
        }
    }

    private void initAppbarSelected() {
        ll_AppBarSelected = rootView.findViewById(R.id.ac_layout_selected_item);

        mTxtSelectedCounter = rootView.findViewById(R.id.ac_layout_selected_txt_counter);
        mBtnCopySelected = rootView.findViewById(R.id.ac_layout_selected_btn_copy);
        mBtnForwardSelected = rootView.findViewById(R.id.ac_layout_selected_btn_forward);
        mBtnReplySelected = rootView.findViewById(R.id.ac_layout_selected_btn_reply);
        mBtnDeleteSelected = rootView.findViewById(R.id.ac_layout_selected_btn_delete);

        //  btnReplaySelected = (MaterialDesignTextView)  rootView.findViewById(R.id.chl_btn_replay_selected);
        //mBtnReplySelected = rootView.findViewById(R.id.chl_ripple_replay_selected);

        if (chatType == CHANNEL) {
            if (channelRole == ChannelChatRole.MEMBER) {
                mBtnReplySelected.setVisibility(View.INVISIBLE);
            }
        } else {
            mBtnReplySelected.setVisibility(View.VISIBLE);
        }
        mBtnReplySelected.setOnClickListener(v -> {// TODO: 12/28/20 MESSAGE_REFACTOR
            if (mAdapter != null && !mAdapter.getSelectedItems().isEmpty() && mAdapter.getSelectedItems().size() == 1) {
                reply(mAdapter.getSelectedItems().iterator().next().messageObject, false);
            }
        });
        mBtnCopySelected.setOnClickListener(v -> {
            copySelectedItemTextToClipboard();
        });

        mBtnForwardSelected.setOnClickListener(v -> {

            // forward selected messages to room list for selecting room
            if (mAdapter != null && mAdapter.getSelectedItems().size() > 0) {
                onForwardClick(null);
            }

        });

        mBtnDeleteSelected.setOnClickListener(v -> {

            messageIds = new ArrayList<>();
            bothDeleteMessageId = new ArrayList<>();

            G.handler.post(() -> {
                for (final AbstractMessage item : mAdapter.getSelectedItems()) {

                    if (mAdapter.getSelectedItems().size() == 1) {
                        confirmAndDeleteMessage(item.messageObject, true);
                        return;
                    }

                    try {
                        if (item != null && item.messageObject != null) {
                            Long messageId = item.messageObject.id;
                            messageIds.add(messageId);
                            boolean bothDelete = RealmRoomMessage.isBothDelete(item.messageObject.getUpdateOrCreateTime());
                            if (bothDelete)
                                bothDeleteMessageId.add(messageId);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                if (chatType == CHAT && !isCloudRoom && bothDeleteMessageId.size() > 0 && mAdapter.getSelectedItems().iterator().next().messageObject.userId == AccountManager.getInstance().getCurrentUser().getId()) {

                    String delete;
                    String textCheckBox = getContext().getResources().getString(R.string.st_checkbox_delete) + " " + title;
                    delete = getContext().getResources().getString(R.string.st_desc_deletes);

                    new MaterialDialog.Builder(getContext())
                            .content(delete)
                            .title(R.string.message)
                            .positiveText(R.string.ok)
                            .negativeText(R.string.cancel)
                            .onPositive((dialog, which) -> {
                                if (!dialog.isPromptCheckBoxChecked()) {
                                    bothDeleteMessageId = null;
                                }

                                getMessageController().deleteSelectedMessage(chatType.getNumber(), mRoomId, messageIds, bothDeleteMessageId);
                                deleteSelectedMessageFromAdapter(messageIds);

                            })
                            .checkBoxPrompt(textCheckBox, false, null)
                            .show();

                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.message)
                            .content(getActivity().getResources().getString(R.string.st_desc_deletes))
                            .positiveText(R.string.ok)
                            .negativeText(R.string.cancel)
                            .onPositive((dialog, which) -> {

                                bothDeleteMessageId = null;
                                getMessageController().deleteSelectedMessage(chatType.getNumber(), mRoomId, messageIds, bothDeleteMessageId);
                                deleteSelectedMessageFromAdapter(messageIds);

                            }).show();
                }
            });
        });

        if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER && !isNotJoin) {
            initLayoutChannelFooter();
        }
    }

    private void deleteSelectedMessageFromAdapter(ArrayList<Long> list) {
        for (Long mId : list) {
            try {
                mAdapter.removeMessage(mId);
                // remove tag from edtChat if the message has deleted
                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    if (mId == ((StructMessageInfo) edtChat.getTag()).realmRoomMessage.getMessageId()) {
                        edtChat.setTag(null);
                    }
                }

                removeLayoutTimeIfNeed();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void removeLayoutTimeIfNeed() {
        if (mAdapter != null) {
            int size = mAdapter.getItemCount();
            for (int i = 0; i < size; i++) {

                if (mAdapter.getItem(i) instanceof TimeItem) {
                    if (i < size - 1) {
                        if (mAdapter.getItem(i + 1) instanceof TimeItem) {
                            mAdapter.remove(i);
                        }
                    } else {
                        mAdapter.remove(i);
                    }
                }
            }
        }
    }

    private void initLayoutSearchNavigation() {
        final RippleView rippleClose = rootView.findViewById(R.id.chl_btn_close_ripple_search_message);
        rippleClose.setOnClickListener(view -> {
            if (edtSearchMessage.getText().toString().length() == 0) {
                goneSearchBox(view);
            } else {
                // deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
                goneSearchHashFooter();
            }
        });

        ll_Search = rootView.findViewById(R.id.ac_ll_search_message);
        edtSearchMessage = rootView.findViewById(R.id.chl_edt_search_message);
        edtSearchMessage.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        edtSearchMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                hideKeyboard();
            return true;
        });

        edtSearchMessage.setListener(event -> {
            if (/*isPopupShowing() && */event.getAction() == MotionEvent.ACTION_DOWN) {
                showPopup(KeyboardView.MODE_KEYBOARD);
                openKeyboardInternal();
            }
        });

        edtSearchMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    if (FragmentChat.hashListener != null) {
                        FragmentChat.hashListener.complete(true, charSequence.toString(), "");
                    }
                } else {
                    goneSearchHashFooter();
                }
                //mAdapter.filter(charSequence);
                //
                //new Handler().postDelayed(new Runnable() {
                //    @Override
                //    public void run() {
                //        messageCounter = mAdapter.getAdapterItemCount();
                //
                //        if (messageCounter > 0) {
                //            selectedPosition = messageCounter - 1;
                //            recyclerView.scrollToPosition(selectedPosition);
                //
                //            if (charSequence.length() > 0) {
                //                selectMessage(selectedPosition);
                //                txtMessageCounter.setText(messageCounter + " " + getString(of) + " " + messageCounter);
                //            } else {
                //                txtMessageCounter.setText("0 " + getString(of) + " 0");
                //            }
                //        } else {
                //            txtMessageCounter.setText("0 " + getString(of) + " " + messageCounter);
                //            selectedPosition = 0;
                //        }
                //    }
                //}, 600);
                //
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void goneSearchBox(View view) {

        if (edtSearchMessage != null) {
            edtSearchMessage.setText("");
            ll_Search.setVisibility(View.GONE);
            layoutToolbar.setVisibility(View.VISIBLE);
        }
        goneSearchHashFooter();

        hideKeyboard();

    }

    private void goneSearchHashFooter() {

        mAdapter.toggleSelection(searchHash.lastMessageId, false, null);
        if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER && !isNotJoin) {
            layoutMute.setVisibility(View.VISIBLE);
        } else {
            if (!isNotJoin && !isChatReadOnly) viewAttachFile.setVisibility(View.VISIBLE);
        }
        ll_navigateHash.setVisibility(View.GONE);

    }

    private void itemAdapterBottomSheetForward() {

        String[] fieldNames = {"isPinned", "pinId", "updatedTime"};
        Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING};
        results = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("keepRoom", false).equalTo("isDeleted", false).
                    equalTo("readOnly", false).notEqualTo("id", mRoomId).findAll().sort(fieldNames, sort);
        });

        resultsContact = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmContacts.class).findAll().sort("display_name");
        });

        List<Long> te = new ArrayList<>();
        te.add(chatPeerId);
        long identifier = 100L;
        for (RealmRoom r : results) {
            StructBottomSheetForward item = new StructBottomSheetForward();
            item.setId(r.getId());
            if (r.getType() == ProtoGlobal.Room.Type.CHAT) {
                te.add(r.getChatRoom().getPeerId());
            }
            item.setDisplayName(r.getTitle());
            if (r.getChatRoom() != null) item.setPeer_id(r.getChatRoom().getPeerId());
            item.setType(r.getType());
            item.setContactList(false);
            item.setNotExistRoom(false);
            identifier = identifier + 1;
            if (r.getChatRoom() != null && r.getChatRoom().getPeerId() > 0 && r.getChatRoom().getPeerId() == userId) {
                fastItemAdapterForward.add(0, new ItemBottomSheetForward(item, avatarHandler).withIdentifier(identifier));
            } else {
                fastItemAdapterForward.add(new ItemBottomSheetForward(item, avatarHandler).withIdentifier(identifier));
            }
        }

        for (RealmContacts r : resultsContact) {
            if (!te.contains(r.getId())) {
                StructBottomSheetForward item = new StructBottomSheetForward();
                item.setId(r.getId());
                item.setDisplayName(r.getDisplay_name());
                item.setContactList(true);
                item.setNotExistRoom(true);
                identifier = identifier + 1;
                fastItemAdapterForward.add(new ItemBottomSheetForward(item, avatarHandler).withIdentifier(identifier));
            }
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    bottomSheetDialogForward.show();
                }
            }
        }, 100);
    }

    /**
     * *************************** Messaging ***************************
     */

    private void sendMessage(int requestCode, String filePath) {

        if (filePath == null || (filePath.length() == 0 && requestCode != AttachFile.request_code_contact_phone)) {
            clearReplyView();
            return;
        }

        showPopup(-1);

        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        long messageId = AppUtils.makeRandomId();
        final long updateTime = TimeUtils.currentLocalTime();
        ProtoGlobal.RoomMessageType messageType = null;
        String fileName;
        long duration = 0;
        long fileSize;
        int[] imageDimens = {0, 0};
        final long senderID = AccountManager.getInstance().getCurrentUser().getId();

        /**
         * check if path is uri detect real path from uri
         */
        String path = getFilePathFromUri(Uri.parse(filePath));
        if (path != null) {
            filePath = path;
        }

        if (requestCode == AttachFile.requestOpenGalleryForVideoMultipleSelect && filePath.toLowerCase().endsWith(".gif")) {
            requestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
        }

        fileName = new File(filePath).getName();
        fileSize = new File(filePath).length();

        RealmRoomMessage roomMessage = new RealmRoomMessage();
        StructMessageInfo structMessageInfoNew = new StructMessageInfo(roomMessage);

        switch (requestCode) {
            case IntentRequests.REQ_CROP:
            case AttachFile.requestOpenGalleryForImageMultipleSelect:

                if (!filePath.toLowerCase().endsWith(".gif")) {
                    if (isMessageWrote()) {
                        messageType = IMAGE_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    }
                } else {
                    if (isMessageWrote()) {
                        messageType = GIF_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.GIF;
                    }
                }

                imageDimens = AndroidUtils.getImageDimens(filePath);
                break;
            case AttachFile.request_code_TAKE_PICTURE:
                if (AndroidUtils.getImageDimens(filePath)[0] == 0 && AndroidUtils.getImageDimens(filePath)[1] == 0) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Picture Not Loaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }

                break;

            case AttachFile.requestOpenGalleryForVideoMultipleSelect:
            case request_code_VIDEO_CAPTURED:
                duration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath) / 1000; //mainVideoPath

                if (isMessageWrote()) {
                    messageType = VIDEO_TEXT;
                } else {
                    messageType = VIDEO;
                }

                break;
            case AttachFile.request_code_pic_audi:
                duration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath) / 1000;
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO;
                }

                String songArtist = AndroidUtils.getAudioArtistName(filePath);
                long songDuration = AndroidUtils.getAudioDuration(G.fragmentActivity, filePath);
                structMessageInfoNew.setSongArtist(songArtist);
                structMessageInfoNew.setSongLength(songDuration);
                break;
            case AttachFile.request_code_pic_file:
            case AttachFile.request_code_open_document:

                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.FILE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.FILE;
                }

                break;
            case AttachFile.request_code_contact_phone:
                if (latestUri == null) {
                    break;
                }
                messageType = CONTACT;
                ContactUtils contactUtils = new ContactUtils(G.fragmentActivity, latestUri);
                String name = contactUtils.retrieveName();
                String number = contactUtils.retrieveNumber();
                structMessageInfoNew.setContactValues(name, "", number);
                break;
            case AttachFile.request_code_paint:

                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                break;
        }

        final ProtoGlobal.RoomMessageType finalMessageType = messageType;
        final String finalFilePath = filePath;
        final String finalFileName = fileName;
        final long finalDuration = duration;
        final long finalFileSize = fileSize;
        final int[] finalImageDimens = imageDimens;

        roomMessage.setMessageId(messageId);
        roomMessage.setMessageType(finalMessageType);
        roomMessage.setMessage(getWrittenMessage());
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
        });
        RealmRoomMessage.isEmojiInText(roomMessage, getWrittenMessage());

        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
        roomMessage.setRoomId(mRoomId);

        RealmAttachment realmAttachment = new RealmAttachment();
        realmAttachment.setId(messageId);
        realmAttachment.setLocalFilePath(finalFilePath);
        realmAttachment.setWidth(finalImageDimens[0]);
        realmAttachment.setHeight(finalImageDimens[1]);
        realmAttachment.setSize(finalFileSize);
        realmAttachment.setName(finalFileName);
        realmAttachment.setDuration(finalDuration);
        if (messageType != CONTACT) {
            roomMessage.setAttachment(realmAttachment);
        }

        roomMessage.setUserId(senderID);
        roomMessage.setAuthorHash(RealmUserInfo.getCurrentUserAuthorHash());
        roomMessage.setShowMessage(true);
        roomMessage.setCreateTime(updateTime);
        if (isReply()) {
            MessageObject replyLayoutObject = (MessageObject) mReplayLayout.getTag();
            RealmRoomMessage replyMessage = new RealmRoomMessage();
            replyMessage.setUserId(replyLayoutObject.userId);
            replyMessage.setUpdateTime(replyLayoutObject.updateTime);
            replyMessage.setStatusVersion(replyLayoutObject.statusVersion);
            replyMessage.setShowTime(replyLayoutObject.needToShow);
            replyMessage.setRoomId(replyLayoutObject.roomId);
            replyMessage.setPreviousMessageId(replyLayoutObject.previousMessageId);
            replyMessage.setFutureMessageId(replyLayoutObject.futureMessageId);
            replyMessage.setMessageId(replyLayoutObject.id);
            replyMessage.setEdited(replyLayoutObject.edited);
            replyMessage.setDeleted(replyLayoutObject.deleted);
            replyMessage.setCreateTime(replyLayoutObject.createTime);
            replyMessage.setMessage(replyLayoutObject.message);
            replyMessage.setMessageType(ProtoGlobal.RoomMessageType.forNumber(replyLayoutObject.messageType));
            replyMessage.setStatus(ProtoGlobal.RoomMessageStatus.forNumber(replyLayoutObject.status).toString());

            if (replyLayoutObject.getAttachment() != null) {
                AttachmentObject attachmentObject = replyLayoutObject.getAttachment();
                RealmAttachment replyToAttachment = new RealmAttachment();
                replyToAttachment.setId(replyLayoutObject.id);
                replyToAttachment.setLocalFilePath(attachmentObject.filePath);
                replyToAttachment.setWidth(attachmentObject.width);
                replyToAttachment.setHeight(attachmentObject.height);
                replyToAttachment.setSize(attachmentObject.size);
                replyToAttachment.setName(attachmentObject.name);
                replyToAttachment.setDuration(attachmentObject.duration);
                replyMessage.setAttachment(replyToAttachment);
            }

            roomMessage.setReplyTo(replyMessage); // TODO: 1/13/21 MESSAGE_REFACTOR
        }
        long replyMessageId = 0;
        if (roomMessage.getReplyTo() != null) {
            if (roomMessage.getReplyTo().getMessageId() < 0) {
                replyMessageId = roomMessage.getReplyTo().getMessageId() * (-1);
            } else {
                replyMessageId = roomMessage.getReplyTo().getMessageId();
            }
        }
        /**
         * make channel extra if room is channel
         */

        if (chatType == CHANNEL) {
            RealmChannelExtra realmChannelExtra = new RealmChannelExtra();
            realmChannelExtra.setMessageId(messageId);
            if (RealmRoom.showSignature(mRoomId)) {
                realmChannelExtra.setSignature(AccountManager.getInstance().getCurrentUser().getName());
            } else {
                realmChannelExtra.setSignature("");
            }

            realmChannelExtra.setThumbsUp("0");
            realmChannelExtra.setThumbsDown("0");
            realmChannelExtra.setViewsLabel("1");
            roomMessage.setChannelExtra(realmChannelExtra);
        }

        if (finalMessageType == CONTACT) {
            if (latestUri != null) {
                ContactUtils contactUtils = new ContactUtils(G.fragmentActivity, latestUri);
                String name = contactUtils.retrieveName();
                String number = contactUtils.retrieveNumber();
                RealmRoomMessageContact realmRoomMessageContact = new RealmRoomMessageContact();
                realmRoomMessageContact.setId(AppUtils.makeRandomId());
                realmRoomMessageContact.setFirstName(name);
                realmRoomMessageContact.setLastName("");
                RealmList<RealmString> listString = new RealmList<>();
                RealmString phoneRealmStr = new RealmString();
                phoneRealmStr.setString(number);
                listString.add(phoneRealmStr);
                realmRoomMessageContact.setPhones(listString);

                roomMessage.setRoomMessageContact(realmRoomMessageContact);
            }

        }

        String makeThumbnailFilePath = "";
        if (finalMessageType == VIDEO || finalMessageType == VIDEO_TEXT) {
            makeThumbnailFilePath = finalFilePath; // mainVideoPath
        }

        if (finalMessageType == VIDEO || finalMessageType == VIDEO_TEXT) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(makeThumbnailFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
            if (bitmap != null) {
                roomMessage.getAttachment().setLocalThumbnailPath(AndroidUtils.saveBitmap(bitmap));
                roomMessage.getAttachment().setWidth(bitmap.getWidth());
                roomMessage.getAttachment().setHeight(bitmap.getHeight());
            }
        }

        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm1 -> {
                RealmRoom room = realm1.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
                if (room != null) {
                    room.setDeleted(false);
                }
                RealmRoom.setLastMessageWithRoomMessage(realm1, roomMessage.getRoomId(), realm1.copyToRealmOrUpdate(roomMessage));

            });
        }).start();


        if (finalMessageType == CONTACT) {
            ChatSendMessageUtil messageUtil = getSendMessageUtil().newBuilder(chatType, finalMessageType, mRoomId).message(getWrittenMessage());
            messageUtil.contact(structMessageInfoNew.realmRoomMessage.getRoomMessageContact().getFirstName(),
                    structMessageInfoNew.realmRoomMessage.getRoomMessageContact().getLastName(),
                    structMessageInfoNew.realmRoomMessage.getRoomMessageContact().getPhones().first().getString());
            if (isReply()) {
                messageUtil.replyMessage(replyMessageId);
            }
            messageUtil.sendMessage(Long.toString(messageId));
        }

        if (isReply()) {
            mReplayLayout.setTag(null);
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    mReplayLayout.setVisibility(View.GONE);
                }
            });
        }

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                switchAddItem(new ArrayList<>(Collections.singletonList(structMessageInfoNew)), false);
            }
        });

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollToEnd();
            }
        }, 100);

    }

    public void sendCancelAction() {

        HelperSetAction.sendCancel(messageId);
    }

    public void sendPosition(final Double latitude, final Double longitude, final String imagePath) {
        sendCancelAction();

        if (isShowLayoutUnreadMessage) {
            removeLayoutUnreadMessage();
        }
        final long messageId = AppUtils.makeRandomId();
        RealmRoomMessage roomMessage = new RealmRoomMessage();
        roomMessage.setMessageId(messageId);

        RealmRoomMessageLocation realmRoomMessageLocation = new RealmRoomMessageLocation();
        realmRoomMessageLocation.setId(AppUtils.makeRandomId());
        realmRoomMessageLocation.setLocationLat(latitude);
        realmRoomMessageLocation.setLocationLong(longitude);
        realmRoomMessageLocation.setImagePath(imagePath);

        roomMessage.setLocation(realmRoomMessageLocation);

        roomMessage.setCreateTime(TimeUtils.currentLocalTime());
        roomMessage.setMessageType(ProtoGlobal.RoomMessageType.LOCATION);
        roomMessage.setRoomId(mRoomId);
        roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
        roomMessage.setAuthorHash(RealmUserInfo.getCurrentUserAuthorHash());
        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());

        if (replyMessageId() > 0) {
            RealmRoomMessage realmRoomMessageCopy = DbManager.getInstance().doRealmTask(realm -> {
                RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", replyMessageId()).findFirst();
                if (realmRoomMessage != null) {
                    return realm.copyFromRealm(realmRoomMessage);
                } else {
                    return null;
                }
            });

            if (realmRoomMessageCopy != null) {
                roomMessage.setReplyTo(realmRoomMessageCopy);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTransaction(realm -> {
                    RealmRoomMessage managedMessage = realm.copyToRealmOrUpdate(roomMessage);
                    RealmRoom.setLastMessageWithRoomMessage(realm, mRoomId, managedMessage);
                });
            }
        }).start();


        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchAddItem(new ArrayList<>(Collections.singletonList(new StructMessageInfo(roomMessage))), false);
                MessageObject locationMessage = MessageObject.create(roomMessage);
                getSendMessageUtil().build(chatType, mRoomId, locationMessage);
                scrollToEnd();
            }
        }, 300);

        clearReplyView();
    }

    /**
     * do forward actions if any message forward to this room
     */
    private void manageForwardedMessage(boolean isMessage) {
        if ((mForwardMessages != null && !isChatReadOnly) || multiForwardList.size() > 0) {
            final LinearLayout ll_Forward = rootView.findViewById(R.id.ac_ll_forward);
            int multiForwardSize = multiForwardList.size();
            if ((hasForward || multiForwardSize > 0) && mForwardMessages != null) {

                for (int i = 0; i < mForwardMessages.size(); i++) {
                    if (hasForward) {
                        sendForwardedMessage(mForwardMessages.get(i), mRoomId, true, i, false);
                    } else {
                        for (int k = 0; k < multiForwardSize; k++) {
                            sendForwardedMessage(mForwardMessages.get(i), multiForwardList.get(k), false, (i + k), isMessage);
                        }
                    }
                }

                if (hasForward) {
                    imvCancelForward.performClick();
                } else {
                    multiForwardList.clear();
                    removeForwardModeFromRoomList();
                    mForwardMessages = null;
                }

            } else {
                imvCancelForward = rootView.findViewById(R.id.cslhf_imv_cansel);
                imvCancelForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ll_Forward.setVisibility(View.GONE);
                        hasForward = false;
                        removeForwardModeFromRoomList();
                        mForwardMessages = null;
                        if (edtChat.getText().length() == 0) {

                            sendButtonVisibility(false);
                        }
                    }
                });

                sendButtonVisibility(true);

                int _count = mForwardMessages != null ? mForwardMessages.size() : 0;
                String str = _count > 1 ? G.fragmentActivity.getResources().getString(R.string.messages_selected) : G.fragmentActivity.getResources().getString(R.string.message_selected);

                TextView emMessage = rootView.findViewById(R.id.cslhf_txt_message);

                FontIconTextView forwardIcon = rootView.findViewById(R.id.cslhs_imv_forward);

                if (HelperCalander.isPersianUnicode) {

                    emMessage.setText(EmojiManager.getInstance().replaceEmoji(convertToUnicodeFarsiNumber(_count + " " + str), emMessage.getPaint().getFontMetricsInt()));
                } else {

                    emMessage.setText(EmojiManager.getInstance().replaceEmoji(_count + " " + str, emMessage.getPaint().getFontMetricsInt()));
                }

                hasForward = true;
                ll_Forward.setVisibility(View.VISIBLE);
            }
        }
    }

    private void removeForwardModeFromRoomList() {
        if (getActivity() instanceof ActivityMain) {
            ((ActivityMain) getActivity()).setForwardMessage(false);
        }
    }

    private void sendForwardedMessage(final MessageObject sourceMessage, final long destinationRoomId, final boolean isSingleForward, int k, boolean isMessage) {
        final long messageId = AppUtils.makeRandomId();

        Log.i("mmdCreate", "sendForwardedMessage: new message " + sourceMessage.toString());

        RealmRoom destinationRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", destinationRoomId).findFirst();
        });

        if (destinationRoom == null || destinationRoom.getReadOnly()) {
            return;
        }

        Log.i("mmdCreate", "sendForwardedMessage: new messageId " + messageId + " source message id " + sourceMessage.id + " destinationRoom room id" + destinationRoom.getId());

        final int type = destinationRoom.getType().getNumber();


        getMessageDataStorage().createForwardMessage(destinationRoomId, messageId, sourceMessage, isMessage, object -> {

            MessageObject createdForwardMessage = (MessageObject) object[0];
            RealmRoomMessage forwardedRealm = (RealmRoomMessage) object[1];
            Long sourceRoomId = (Long) object[2];
            Long sourceMessageId = (Long) object[3];

            Log.i("mmdCreate", "sendForwardedMessage commit to db successfully createdForwardMessage: " + createdForwardMessage.toString());

            if (forwardedRealm.isValid() && !createdForwardMessage.deleted) {
                if (isSingleForward) {
                    switchAddItem(new ArrayList<>(Collections.singletonList(new StructMessageInfo(forwardedRealm))), false);
                    scrollToEnd();
                }
                Log.i("mmdCreate", "final process for sending message to a room with Type: " + type + " destinationRoomId: " + createdForwardMessage.roomId + " sourceRoomId: " + sourceRoomId + " sourceMessageId: " + sourceMessageId);
                getSendMessageUtil().buildForward(type, createdForwardMessage.roomId, createdForwardMessage, sourceRoomId, sourceMessageId);
            }
        });

    }

    private MessageObject makeLayoutTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String timeString = TimeUtils.getChatSettingsTimeAgo(G.fragmentActivity, calendar.getTime());

        RealmRoomMessage timeMessage = RealmRoomMessage.makeTimeMessage(time, timeString);

        return MessageObject.create(timeMessage);
    }

    private void switchAddItem(ArrayList<StructMessageInfo> infos, boolean addTop) {
        if (prgWaiting != null && infos.size() > 0) {
            prgWaiting.setVisibility(View.GONE);
        }
        long identifier = SUID.id().get();
        for (StructMessageInfo info : infos) {
            MessageObject messageObject = MessageObject.create(info.realmRoomMessage);
            int messageType;
            if (messageObject.isForwarded()) {
                messageType = messageObject.forwardedMessage.messageType;
            } else {
                messageType = messageObject.messageType;
            }

            if (!messageObject.isTimeOrLogMessage() || (messageType == LOG_VALUE)) {
                int index = 0;
                if (addTop) {
                    if (messageObject.needToShow) {
                        for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                            if (mAdapter.getAdapterItem(i) instanceof TimeItem) {// TODO: 12/28/20 MESSAGE_REFACTOR
                                if (!RealmRoomMessage.isTimeDayDifferent(messageObject.getUpdateOrCreateTime(), mAdapter.getAdapterItem(i).messageObject.getUpdateOrCreateTime())) {
                                    mAdapter.remove(i);
                                }
                                break;
                            }
                        }
                        mAdapter.add(0, new TimeItem(mAdapter, this).setMessage(makeLayoutTime(messageObject.getUpdateOrCreateTime())).withIdentifier(identifier++));

                        index = 1;
                    }
                } else {


                    /**
                     * don't allow for add lower messageId to bottom of list
                     */
                    if (messageObject.id > biggestMessageId) {
                        if (messageObject.status != MessageObject.STATUS_SENDING) {
                            biggestMessageId = messageObject.id;
                        }
                    } else {
                        continue;
                    }


                    if (lastMessageId == messageObject.id) {
                        continue;
                    } else {
                        lastMessageId = messageObject.id;
                    }

                    if (messageObject.needToShow) {
                        if (mAdapter.getItemCount() > 0) {
                            if (mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).messageObject != null && RealmRoomMessage.isTimeDayDifferent(messageObject.getUpdateOrCreateTime(), mAdapter.getAdapterItem(mAdapter.getItemCount() - 1).messageObject.getUpdateOrCreateTime())) {
                                mAdapter.add(new TimeItem(mAdapter, this).setMessage(makeLayoutTime(messageObject.getUpdateOrCreateTime())).withIdentifier(identifier++));

                            }
                        } else {
                            mAdapter.add(new TimeItem(mAdapter, this).setMessage(makeLayoutTime(messageObject.getUpdateOrCreateTime())).withIdentifier(identifier++));
                        }
                    }
                }

                switch (messageType) {
                    case TEXT_VALUE:
                        if (messageObject.getAdditional() != null && messageObject.getAdditional().type == AdditionalType.CARD_TO_CARD_MESSAGE)
                            if (!addTop) {
                                mAdapter.add(new CardToCardItem(mAdapter, chatType, FragmentChat.this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new CardToCardItem(mAdapter, chatType, FragmentChat.this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        else {
                            if (!addTop) {
                                mAdapter.add(new TextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new TextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        }
                        break;
                    case WALLET_VALUE:
                        if (messageObject.wallet.cardToCard != null) {
                            if (!addTop) {
                                mAdapter.add(new LogWalletCardToCard(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogWalletCardToCard(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        } else if (messageObject.wallet.topupObject != null) {
                            if (!addTop) {
                                mAdapter.add(new LogWalletTopup(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogWalletTopup(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                            ;
                        } else if (messageObject.wallet.billObject != null) {
                            if (!addTop) {
                                mAdapter.add(new LogWalletBill(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogWalletBill(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new LogWallet(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogWallet(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        }
                        break;
                    case IMAGE_VALUE:
                    case IMAGE_TEXT_VALUE:
                        if (!addTop) {
                            mAdapter.add(new ImageWithTextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageWithTextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO_VALUE:
                    case VIDEO_TEXT_VALUE:
                        if (!addTop) {
                            mAdapter.add(new VideoWithTextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoWithTextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case LOCATION_VALUE:
                        if (!addTop) {
                            mAdapter.add(new LocationItem(mAdapter, chatType, this, getActivity()).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new LocationItem(mAdapter, chatType, this, getActivity()).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case FILE_VALUE:
                    case FILE_TEXT_VALUE:
                        if (!addTop) {
                            mAdapter.add(new FileItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new FileItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case STICKER_VALUE:
                        if (messageObject.getAdditional() != null && messageObject.getAdditional().type == AdditionalType.GIFT_STICKER) {
                            if (!addTop) {
                                mAdapter.add(new GiftStickerItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new GiftStickerItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        } else if (messageObject.getAttachment() != null && messageObject.getAttachment().name != null && messageObject.getAttachment().isAnimatedSticker()) {
                            if (!addTop) {
                                mAdapter.add(new AnimatedStickerItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new AnimatedStickerItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new StickerItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new StickerItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        }
                        break;
                    case VOICE_VALUE:
                        if (!addTop) {
                            mAdapter.add(new VoiceItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VoiceItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case AUDIO_VALUE:
                    case AUDIO_TEXT_VALUE:
                        if (!addTop) {
                            mAdapter.add(new AudioItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new AudioItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case CONTACT_VALUE:
                        if (!addTop) {
                            mAdapter.add(new ContactItem(getActivity(), mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ContactItem(getActivity(), mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case GIF_VALUE:
                    case GIF_TEXT_VALUE:
                        if (!addTop) {
                            mAdapter.add(new GifWithTextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifWithTextItem(mAdapter, chatType, this).setMessage(messageObject).withIdentifier(identifier));
                        }
                        break;
                    case LOG_VALUE:
                        if (messageObject.needToShow) {
                            if (!addTop) {
                                mAdapter.add(new LogItem(mAdapter, this).setMessage(messageObject).withIdentifier(identifier));
                            } else {
                                mAdapter.add(index, new LogItem(mAdapter, this).setMessage(messageObject).withIdentifier(identifier));
                            }
                        }
                        break;
                }
            }
            identifier++;
        }
    }

    boolean shouldLoadMessage = false;

    private void getMessages() {
        DbManager.getInstance().doRealmTask(realm -> {
            ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction;
            ArrayList<StructMessageInfo> messageInfos = new ArrayList<>();
            RealmResults<RealmRoomMessage> results;
            RealmResults<RealmRoomMessage> resultsDown = null;
            RealmResults<RealmRoomMessage> resultsUp;
            long fetchMessageId = 0; // with this value realm will be queried for get message
            if (hasUnread() || hasSavedState()) {
                RealmRoomMessage firstUnreadMessage = null;
                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();

                if (room != null) {
                    room = realm.copyFromRealm(room);
                    firstUnreadMessage = room.getFirstUnreadMessage();
                }

                if (hasSavedState()) {
                    fetchMessageId = getSavedState();

                    if (hasUnread()) {
                        if (firstUnreadMessage == null) {
                            resetMessagingValue();
                            getMessages();
                            return;
                        }
                    }
                } else {
                    if (firstUnreadMessage == null) {
                        resetMessagingValue();
                        getMessages();
                        return;
                    }
                    G.runOnUiThread(this::unreadLayoutMessage);
                    fetchMessageId = firstUnreadMessage.getMessageId();
                }

                if (hasUnread()) {
                    countNewMessage = unreadCount;
                    G.runOnUiThread(() -> {
                        txtNewUnreadMessage.setVisibility(View.VISIBLE);
                        txtNewUnreadMessage.getTextView().setText(String.valueOf(countNewMessage));
                    });
                }

                startFutureMessageIdUp = fetchMessageId;

                // we have firstUnreadMessage but for gapDetection method we need RealmResult so get this message with query; if we change gap detection method will be can use from firstUnreadMessage
                resultsDown = realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).notEqualTo("createTime", 0).equalTo("deleted", false).equalTo("showMessage", true).equalTo("messageId", fetchMessageId).findAll();

                addToView = false;
                direction = DOWN;
            } else {
                shouldLoadMessage = true;
                addToView = true;
                direction = UP;
            }

            Number latestMessageId = realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).notEqualTo("createTime", 0).equalTo("deleted", false).equalTo("showMessage", true).max("messageId");
            if (latestMessageId != null) {
                resultsUp = realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).equalTo("messageId", latestMessageId.longValue()).findAll();
            } else {
                resultsUp = realm.where(RealmRoomMessage.class).equalTo("roomId", -100).findAll();
            }

            long gapMessageId;
            if (direction == DOWN) {
                RealmQuery realmQuery = realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).lessThanOrEqualTo("messageId", fetchMessageId).notEqualTo("createTime", 0).equalTo("deleted", false).equalTo("showMessage", true);
                if (realmQuery.count() > 1) {
                    resultsUp = realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).equalTo("messageId", fetchMessageId).notEqualTo("createTime", 0).equalTo("deleted", false).equalTo("showMessage", true).findAll();
                    gapDetection(resultsUp, UP);
                } else {
                    getOnlineMessage(fetchMessageId, UP);
                }

                results = resultsDown;
                gapMessageId = gapDetection(results, direction);
            } else {
                results = resultsUp;
                gapMessageId = gapDetection(resultsUp, UP);
            }

            if (results.size() > 0) {

                Object[] object = getLocalMessage(realm, mRoomId, results.first().getMessageId(), gapMessageId, true, direction);
                messageInfos = (ArrayList<StructMessageInfo>) object[0];
                if (messageInfos.size() > 0) {
                    if (direction == UP) {
                        topMore = (boolean) object[1];
                        startFutureMessageIdUp = messageInfos.get(messageInfos.size() - 1).realmRoomMessage.getMessageId();
                    } else {
                        bottomMore = (boolean) object[1];
                        startFutureMessageIdDown = messageInfos.get(messageInfos.size() - 1).realmRoomMessage.getMessageId();
                    }
                } else {
                    if (direction == UP) {
                        startFutureMessageIdUp = 0;
                    } else {
                        startFutureMessageIdDown = 0;
                    }
                }
                if (gapMessageId > 0) {
                    boolean hasSpaceToGap = (boolean) object[2];
                    if (!hasSpaceToGap) {

                        long oldMessageId = 0;
                        if (messageInfos.size() > 0) {
                            oldMessageId = messageInfos.get(messageInfos.size() - 1).realmRoomMessage.getMessageId();
                        }
                        getOnlineMessage(oldMessageId, direction);
                    }
                } else {
                    if ((direction == UP && !topMore) || (direction == DOWN && !bottomMore)) {
                        if (messageInfos.size() > 0) {
                            getOnlineMessage(messageInfos.get(messageInfos.size() - 1).realmRoomMessage.getMessageId(), direction);
                        } else {
                            getOnlineMessage(0, direction);
                        }
                    }
                }
            } else {
                long oldMessageId = 0;
                if (direction == DOWN) {
                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("roomId", mRoomId).notEqualTo("createTime", 0).equalTo("showMessage", true).equalTo("messageId", fetchMessageId).findFirst();
                    if (realmRoomMessage != null) {
                        oldMessageId = realmRoomMessage.getMessageId();
                    }
                }

                getOnlineMessage(oldMessageId, direction);
            }

            final ArrayList<StructMessageInfo> finalMessageInfos = messageInfos;
            G.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (direction == UP) {
                        shouldLoadMessage = true;
                        switchAddItem(finalMessageInfos, true);
                    } else {
                        shouldLoadMessage = true;
                        switchAddItem(finalMessageInfos, false);
                        if (hasSavedState()) {
                            shouldLoadMessage = false;
                            if (messageId != 0) {
                                if (goToPositionWithAnimation(savedScrollMessageId, 1000)) {
                                    savedScrollMessageId = 0;
                                }
                            } else {
                                int position = mAdapter.findPositionByMessageId(savedScrollMessageId);
                                LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                                linearLayout.scrollToPositionWithOffset(position, firstVisiblePositionOffset);
                                savedScrollMessageId = 0;
                            }
                        }
                    }

                    scrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                            View view = linearLayoutManager.getChildAt(0);
                            if (firstVisiblePosition > 0 && view != null) {
                                firstVisiblePositionOffset = view.getTop();
                            }

                            visibleItemCount = linearLayoutManager.getChildCount();
                            totalItemCount = linearLayoutManager.getItemCount();

                            if (shouldLoadMessage) {
                                if (firstVisiblePosition < scrollEnd) {
                                    loadMessage(UP);
                                    if (totalItemCount <= scrollEnd) {
                                        loadMessage(DOWN);
                                    }
                                } else if (firstVisiblePosition + visibleItemCount >= (totalItemCount - scrollEnd)) {
                                    loadMessage(DOWN);
                                }
                            }
                        }

                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                shouldLoadMessage = true;
                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();

                                if (firstVisiblePosition < scrollEnd) {
                                    loadMessage(UP);
                                }
                            }
                        }
                    };
                    // TODO: 2/14/21 Should write a better logic to loadMessage

                    recyclerView.addOnScrollListener(scrollListener);


                    if (unreadCount > 0) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            });
        });
    }

    /**
     * manage load message from local or from server(online)
     */
    private void loadMessage(final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        long gapMessageId;
        long startFutureMessageId;
        if (direction == UP) {
            gapMessageId = gapMessageIdUp;
            startFutureMessageId = startFutureMessageIdUp;
        } else {
            gapMessageId = gapMessageIdDown;
            startFutureMessageId = startFutureMessageIdDown;
        }
        if ((direction == UP && topMore) || (direction == DOWN && bottomMore)) {
            Object[] object = DbManager.getInstance().doRealmTask(realm -> {
                return getLocalMessage(realm, mRoomId, startFutureMessageId, gapMessageId, false, direction);
            });
            if (direction == UP) {
                topMore = (boolean) object[1];
            } else {
                bottomMore = (boolean) object[1];
            }
            final ArrayList<StructMessageInfo> structMessageInfos = (ArrayList<StructMessageInfo>) object[0];
            if (structMessageInfos.size() > 0) {
                if (direction == UP) {
                    startFutureMessageIdUp = structMessageInfos.get(structMessageInfos.size() - 1).realmRoomMessage.getMessageId();
                } else {
                    startFutureMessageIdDown = structMessageInfos.get(structMessageInfos.size() - 1).realmRoomMessage.getMessageId();
                }
            } else {
                /**
                 * don't set zero. when user come to room for first time with -@roomId-
                 * for example : @public ,this block will be called and set zero this value and finally
                 * don't allow to user for get top history, also that sounds this block isn't helpful
                 */
                //if (direction == UP) {
                //    startFutureMessageIdUp = 0;
                //} else {
                //    startFutureMessageIdDown = 0;
                //}
            }

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    switchAddItem(structMessageInfos, direction == UP);
                }
            });

            /**
             * if gap is exist ,check that reached to gap or not and if
             * reached send request to server for clientGetRoomHistory
             */
            if (gapMessageId > 0) {
                boolean hasSpaceToGap = (boolean) object[2];
                if (!hasSpaceToGap) {
                    /**
                     * send request to server for clientGetRoomHistory
                     */
                    long oldMessageId;
                    if (structMessageInfos.size() > 0) {
                        oldMessageId = structMessageInfos.get(structMessageInfos.size() - 1).realmRoomMessage.getMessageId();
                    } else {
                        oldMessageId = gapMessageId;
                    }

                    getOnlineMessage(oldMessageId, direction);
                }
            }
        } else if (gapMessageId > 0) {
            /**
             * detect old messageId that should get history from server with that
             * (( hint : in scroll changeState never should get online message with messageId = 0
             * in some cases maybe startFutureMessageIdUp Equal to zero , so i used from this if.))
             */
            if (startFutureMessageId != 0) {
                getOnlineMessage(startFutureMessageId, direction);
            }
        } else {

            if (((direction == UP && allowGetHistoryUp) || (direction == DOWN && allowGetHistoryDown)) && startFutureMessageId != 0) {
                getOnlineMessage(startFutureMessageId, direction);
            }
        }
    }

    /**
     * get message history from server
     *
     * @param oldMessageId if set oldMessageId=0 messages will be get from latest message that exist in server
     */
    private void getOnlineMessage(final long oldMessageId, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if ((direction == UP && !isWaitingForHistoryUp && allowGetHistoryUp) || (direction == DOWN && !isWaitingForHistoryDown && allowGetHistoryDown)) {
            /**
             * show progress when start for get history from server
             */
            progressItem(SHOW, direction);

            if (!getRequestManager().isUserLogin()) {
                getOnlineMessageAfterTimeOut(oldMessageId, direction);
                return;
            }
            long reachMessageId;
            if (direction == UP) {
                reachMessageId = reachMessageIdUp;
                isWaitingForHistoryUp = true;
            } else {
                reachMessageId = reachMessageIdDown;
                isWaitingForHistoryDown = true;
            }


            String requestId = DbManager.getInstance().doRealmTask(realm -> {

                int limit = Config.LIMIT_GET_HISTORY_NORMAL;
                if ((firstUp && direction == UP) || (firstDown && direction == DOWN)) {
                    limit = Config.LIMIT_GET_HISTORY_LOW;
                }
                return MessageLoader.getOnlineMessage(realm, mRoomId, oldMessageId, reachMessageId, limit, direction, new OnMessageReceive() {
                    @Override
                    public void onMessage(final long roomId, long startMessageId, long endMessageId, List<RealmRoomMessage> realmRoomMessages, boolean gapReached, boolean jumpOverLocal, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
                        if (roomId != mRoomId) {
                            return;
                        }
                        hideProgress();
                        /**
                         * hide progress received history
                         */
                        progressItem(HIDE, direction);
                        if (direction == UP) {
                            firstUp = false;
                            startFutureMessageIdUp = startMessageId;
                            isWaitingForHistoryUp = false;
                        } else {
                            firstDown = false;
                            startFutureMessageIdDown = endMessageId;
                            isWaitingForHistoryDown = false;
                        }

                        //                    if (realmRoomMessages.size() == 0) { // Hint : link browsable ; Commented Now!!!
                        //                        getOnlineMessage(oldMessageId, direction);
                        //                        return;
                        //                    }

                        /**
                         * I do this for set addToView true
                         */
                        if (direction == DOWN && realmRoomMessages.size() < (Config.LIMIT_GET_HISTORY_NORMAL - 1)) {
                            getOnlineMessage(startFutureMessageIdDown, direction);
                        }

                        /**
                         * when reached to gap and not jumped over local, set gapMessageIdUp = 0; do this action
                         * means that gap not exist (need this value for future get message) set topMore/bottomMore
                         * local after that gap reached true for allow that get message from
                         */
                        if (gapReached && !jumpOverLocal) {
                            if (direction == UP) {
                                gapMessageIdUp = 0;
                                reachMessageIdUp = 0;
                                topMore = true;
                            } else {
                                gapMessageIdDown = 0;
                                reachMessageIdDown = 0;
                                bottomMore = true;
                            }

                            gapDetection(realmRoomMessages, direction);
                        } else if ((direction == UP && isReachedToTopView()) || direction == DOWN && isReachedToBottomView()) {
                            /**
                             * check this changeState because if user is near to top view and not scroll get top message from server
                             */
                            //getOnlineMessage(startFutureMessageId, directionEnum);
                        }

                        final ArrayList<StructMessageInfo> structMessageInfos = new ArrayList<>();
                        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                            //suggestion: may be could remove db query in constructor of StructMessageInfo and add query in load item in adapter with cache
                            structMessageInfos.add(new StructMessageInfo(realmRoomMessage));
                        }

                        switchAddItem(structMessageInfos, direction == UP);

                    }

                    @Override
                    public void onError(int majorCode, int minorCode, long messageIdGetHistory, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
                        hideProgress();
                        /**
                         * hide progress if have any error
                         */
                        progressItem(HIDE, direction);

                        if (majorCode == 617) {

                            if (!isWaitingForHistoryUp && !isWaitingForHistoryDown && mAdapter.getItemCount() == 0) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }

                            if (direction == UP) {
                                isWaitingForHistoryUp = false;
                                allowGetHistoryUp = false;
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //TODO [Saeed Mozaffari] [2017-03-06 9:50 AM] - for avoid from 'Inconsistency detected. Invalid item position' error i set notifyDataSetChanged. Find Solution And Clear it!!!
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                addToView = true;
                                isWaitingForHistoryDown = false;
                                allowGetHistoryDown = false;
                            }
                        }

                        /**
                         * if time out came up try again for get history with previous value
                         */
                        if (majorCode == 5) {
                            if (direction == UP) {
                                isWaitingForHistoryUp = false;
                            } else {
                                isWaitingForHistoryDown = false;
                            }
                        }

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (majorCode == 5) {
                                    getOnlineMessageAfterTimeOut(messageIdGetHistory, direction);
                                }
                            }
                        });


                    }
                });
            });

            if (direction == UP) {
                lastRandomRequestIdUp = requestId;
            } else {
                lastRandomRequestIdDown = requestId;
            }
        }
    }

    private void getOnlineMessageAfterTimeOut(final long messageIdGetHistory, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if (getRequestManager().isUserLogin()) {
            getOnlineMessage(messageIdGetHistory, direction);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getOnlineMessageAfterTimeOut(messageIdGetHistory, direction);
                }
            }, 1000);
        }
    }

    /**
     * detect gap exist in this room or not
     * (hint : if gapMessageId==0 means that gap not exist)
     * if gapMessageIdUp exist, not compute again
     */
    private long gapDetection(List<RealmRoomMessage> results, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        if (((direction == UP && gapMessageIdUp == 0) || (direction == DOWN && gapMessageIdDown == 0)) && results.size() > 0) {
            Object[] objects = DbManager.getInstance().doRealmTask(realm -> {
                return MessageLoader.gapExist(realm, mRoomId, results.get(0).getMessageId(), direction);
            });
            if (direction == UP) {
                reachMessageIdUp = (long) objects[1];
                return gapMessageIdUp = (long) objects[0];
            } else {
                reachMessageIdDown = (long) objects[1];
                return gapMessageIdDown = (long) objects[0];
            }
        }
        return 0;
    }

    private long gapDetection(RealmResults<RealmRoomMessage> results, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        return DbManager.getInstance().doRealmTask(realm -> {
            return gapDetection(realm.copyFromRealm(results), direction);
        });
    }

    /**
     * return true if now view is near to top
     */
    private boolean isReachedToTopView() {
        return firstVisiblePosition <= 5;
    }

    /**
     * return true if now view is near to bottom
     */
    private boolean isReachedToBottomView() {
        return (firstVisiblePosition + visibleItemCount >= (totalItemCount - 5));
    }

    /**
     * make unread layout message
     */
    private void unreadLayoutMessage() {
        int unreadMessageCount = unreadCount;
        if (unreadMessageCount > 0) {
            RealmRoomMessage unreadMessage = RealmRoomMessage.makeUnreadMessage(unreadMessageCount);
            mAdapter.add(0, new UnreadMessage(mAdapter, FragmentChat.this).setMessage(MessageObject.create(unreadMessage)).withIdentifier(SUID.id().get()));
            isShowLayoutUnreadMessage = true;

        }
    }

    /**
     * return first unread message for current room
     * (reason : use from this method for avoid from closed realm error)
     */
    private RealmRoomMessage getFirstUnreadMessage(Realm realm) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
        if (realmRoom != null) {
            return realmRoom.getFirstUnreadMessage();
        }
        return null;
    }

    /**
     * check that this room has unread or no
     */
    private boolean hasUnread() {
        return unreadCount > 0;
    }

    /**
     * check that this room has saved changeState or no
     */
    private boolean hasSavedState() {
        return savedScrollMessageId > 0;
    }

    /**
     * return saved scroll messageId
     */
    private long getSavedState() {
        return savedScrollMessageId;
    }

    /**
     * manage progress changeState in adapter
     *
     * @param progressState SHOW or HIDE changeState detect with enum
     * @param direction     define direction for show progress in UP or DOWN
     */
    private void progressItem(final ProgressState progressState, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                int progressIndex = 0;
                if (direction == DOWN) {
                    progressIndex = mAdapter.getAdapterItemCount() - 1;
                }
                if (progressState == SHOW) {
                    if ((mAdapter.getAdapterItemCount() > 0) && !(mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (direction == DOWN && progressIdentifierDown == 0) {
                                    progressIdentifierDown = SUID.id().get();
                                    mAdapter.add(new ProgressWaiting(mAdapter, FragmentChat.this).withIdentifier(progressIdentifierDown));
                                } else if (direction == UP && progressIdentifierUp == 0) {
                                    progressIdentifierUp = SUID.id().get();
                                    mAdapter.add(0, new ProgressWaiting(mAdapter, FragmentChat.this).withIdentifier(progressIdentifierUp));
                                }
                            }
                        });
                    }
                } else {
                    /**
                     * i do this action with delay because sometimes instance wasn't successful
                     * for detect progress so client need delay for detect this instance
                     */
                    if ((mAdapter.getItemCount() > 0) && (mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        mAdapter.remove(progressIndex);
                        if (direction == DOWN) {
                            progressIdentifierDown = 0;
                        } else {
                            progressIdentifierUp = 0;
                        }
                    } else {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                /**
                                 * if not detected progress item for remove use from item identifier and remove progress item
                                 */
                                if (direction == DOWN && progressIdentifierDown != 0) {
                                    for (int i = (mAdapter.getItemCount() - 1); i >= 0; i--) {
                                        if (mAdapter.getItem(i).getIdentifier() == progressIdentifierDown) {
                                            mAdapter.remove(i);
                                            progressIdentifierDown = 0;
                                            break;
                                        }
                                    }
                                } else if (direction == UP && progressIdentifierUp != 0) {
                                    for (int i = 0; i < (mAdapter.getItemCount() - 1); i++) {
                                        if (mAdapter.getItem(i).getIdentifier() == progressIdentifierUp) {
                                            mAdapter.remove(i);
                                            progressIdentifierUp = 0;
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * reset to default value for reload message again
     */
    private void resetMessagingValue() {
        prgWaiting.setVisibility(View.VISIBLE);
        clearAdapterItems();

        addToView = true;
        topMore = false;
        bottomMore = false;
        isWaitingForHistoryUp = false;
        isWaitingForHistoryDown = false;
        gapMessageIdUp = 0;
        gapMessageIdDown = 0;
        reachMessageIdUp = 0;
        reachMessageIdDown = 0;
        allowGetHistoryUp = true;
        allowGetHistoryDown = true;
        startFutureMessageIdUp = 0;
        startFutureMessageIdDown = 0;
        firstVisiblePosition = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        unreadCount = 0;
        biggestMessageId = 0;
    }

    private void deleteFileFromStorageIfExist(MessageObject messageObject) {
        String path = getFilePathIfExistInStorage(messageObject);
        if (path == null) return;
        if (!path.contains(G.IGAP + "/") && !path.contains("/net.iGap/"))
            return; //dont remove images was not in igap folder
        File file = new File(path);
        if (file.exists()) file.delete();
    }

    private boolean isFileExistInLocalStorage(MessageObject messageObject) {
        if (messageObject.messageType == STICKER_VALUE) return false;
        String path = getFilePathIfExistInStorage(messageObject);
        if (path == null) return false;
        if (!path.contains(G.IGAP + "/") && !path.contains("/net.iGap/"))
            return false; //just remove from igap folder

        return new File(path).exists();
    }

    private String getFilePathIfExistInStorage(MessageObject messageObject) {

        if (messageObject.getAttachment() == null) return null;

        String filepath;
        if (messageObject.forwardedMessage != null) {
            filepath = messageObject.forwardedMessage.getAttachment().filePath != null ? messageObject.forwardedMessage.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.forwardedMessage.getAttachment().cacheId, messageObject.forwardedMessage.getAttachment().name, messageObject.forwardedMessage.messageType);
        } else {
            filepath = messageObject.getAttachment().filePath != null ? messageObject.getAttachment().filePath : AndroidUtils.getFilePathWithCashId(messageObject.getAttachment().cacheId, messageObject.getAttachment().name, messageObject.messageType);
        }

        return filepath;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        G.fragmentActivity = (FragmentActivity) activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState); //No call for super(). Bug on API Level > 11.
    }

    public void finishChat() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (getActivity() != null && isAdded()) {
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChat.class.getName());
                    removeFromBaseFragment(fragment);

                    if (G.iTowPanModDesinLayout != null) {
                        G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.hide);
                    }
                }
            }
        });
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                HelperError.showSnackMessage(error, false);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onBotCommandText(Object message, int botAction) {

        if (message instanceof String) {
            if (botAction == 0) {
                if (!isChatReadOnly) {
                    if (edtChat != null)
                        edtChat.setText(EmojiManager.getInstance().replaceEmoji(message.toString(), edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false));
                    imvSendButton.performClick();
                }
            } else {
                openWebViewForSpecialUrlChat(message.toString());
            }
        } else if (message instanceof RealmRoomMessage) {
            mAdapter.add(new TextItem(mAdapter, chatType, FragmentChat.this).setMessage(MessageObject.create((ProtoGlobal.RoomMessage) message)).withIdentifier(SUID.id().get()));

        }

    }

    @Override
    public boolean requestLocation() {
        try {
            attachFile.requestGetPosition(complete, FragmentChat.this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void forwardToChatRoom(final ArrayList<StructBottomSheetForward> forwardList, boolean isMessage) {

        if (forwardList != null && forwardList.size() > 0) {

            final int[] count = {0};
            for (int i = 0; i < forwardList.size(); i++) {
                new RequestChatGetRoom().chatGetRoom(forwardList.get(i).getId(), new RequestChatGetRoom.OnChatRoomReady() {
                    @Override
                    public void onReady(ProtoGlobal.Room room) {
                        if (!multiForwardList.contains(room.getId())) {
                            multiForwardList.add(room.getId());
                            RealmRoom.putOrUpdate(room);
                        }

                        count[0]++;
                        if (count[0] >= forwardList.size()) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    G.refreshRealmUi();
                                    bottomSheetDialogForward.dismiss();
                                    hideProgress();
                                    forwardList.clear();
                                    manageForwardedMessage(isMessage);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int major, int minor) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetDialogForward.dismiss();
                                hideProgress();
                                error(G.fragmentActivity.getResources().getString(R.string.faild));
                            }
                        });
                    }
                });
            }


        } else {
            manageForwardedMessage(isMessage);
            bottomSheetDialogForward.dismiss();
            hideProgress();
        }

    }

    private void showPaymentDialog() {
        showSelectItem();
//        RealmRoom realmRoom = getRoom();
//        if (realmRoom != null) {
//            chatType = realmRoom.getType();
//            if (chatType == CHAT) {
//                chatPeerId = realmRoom.getChatRoom().getPeerId();
//                if (imvUserPicture != null && txtName != null) {
//                    paymentDialog = PaymentFragment.newInstance(chatPeerId, imvUserPicture.getDrawable(), txtName.getText().toString());
////                    paymentDialog.show(getFragmentManager(), "payment_dialog");
//                    new HelperFragment(getActivity().getSupportFragmentManager(), paymentDialog).setTag("PaymentFragment").setReplace(false).load();
//                }
//            }
//        }

    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (webViewChatPage != null) {
            closeWebViewForSpecialUrlChat(false);
            return;
        }

        hideKeyboard();

        if (G.twoPaneMode) {
            if (getActivity() instanceof ActivityMain) {
                ((ActivityMain) getActivity()).goToTabletEmptyPage();
            }
        } else {
            popBackStackFragment();
        }
    }

    @Override
    public void onChatAvatarClickListener(View view) {
        goToProfile();
    }

    @Override
    public void onRightIconClickListener(View view) {

        showPopup(-1);

        List<String> items = new ArrayList<>();
        items.add(getString(R.string.Search));
        items.add(getString(R.string.clear_history));
        items.add(getString(R.string.delete_chat));
        if (!isCloudRoom) {
            if (isMuteNotification)
                items.add(getString(R.string.unmute_notification));
            else
                items.add(getString(R.string.mute_notification));
            items.add(getString(R.string.chat_to_group));
        }

        items.add(getString(R.string.clean_up));
        items.add(getString(R.string.export_chat));

        if (chatType == CHAT) {
            if (!isChatReadOnly && !blockUser && !isBot) {
            } else {
                items.remove(getString(R.string.chat_to_group));
                items.remove(getString(R.string.export_chat));
            }
        } else {
            items.remove(getString(R.string.delete_chat));
            items.remove(getString(R.string.chat_to_group));

            if (chatType == GROUP && isPublicGroup) {
                items.remove(getString(R.string.clear_history));
            }

            if (chatType == CHANNEL) {
                items.remove(getString(R.string.clear_history));
                items.remove(getString(R.string.export_chat));
            }
            if (channelRole != ChannelChatRole.OWNER || groupRole != GroupChatRole.OWNER || isNotJoin) {
                items.add(getString(R.string.report));
            } else {
                items.remove(getString(R.string.report));
            }
        }

        RealmRoom realmRoom1 = getRoom();
        if (realmRoom1 != null) {

                /*if (realmRoom.getMute()) {
                    txtMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.unmute_notification));
                    iconMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.md_unMuted));
                } else {
                    txtMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.mute_notification));
                    iconMuteNotification.setText(G.fragmentActivity.getResources().getString(R.string.md_muted));
                }*/
        } else {
            items.remove(getString(R.string.clear_history));
            items.remove(getString(R.string.delete_chat));
            if (!isMuteNotification)
                items.remove(getString(R.string.mute_notification));
            else
                items.remove(getString(R.string.unmute_notification));
            items.remove(getString(R.string.chat_to_group));
            items.remove(getString(R.string.clean_up));
        }

        if (isNotJoin) {
            items.remove(getString(R.string.clear_history));
            if (!isMuteNotification)
                items.remove(getString(R.string.mute_notification));
            else
                items.remove(getString(R.string.unmute_notification));
            items.remove(getString(R.string.clean_up));
        }

        if (RealmRoom.isNotificationServices(mRoomId)) {
            items.remove(getString(R.string.report));
        }

        if (RealmRoom.isBot(chatPeerId)) {
            items.remove(getString(R.string.delete_chat));
        }


        if ((chatType == CHAT) && !isCloudRoom && !isBot && !RealmRoom.isNotificationServices(mRoomId)) {
            items.add(getString(R.string.SendMoney));
        } else {
            items.remove(getString(R.string.SendMoney));
        }

        if (isBot) {
            if (webViewChatPage != null) {
                items.remove(getString(R.string.Search));
                items.remove(getString(R.string.clear_history));
                items.remove(getString(R.string.delete_chat));
                if (!isMuteNotification)
                    items.remove(getString(R.string.mute_notification));
                else
                    items.remove(getString(R.string.unmute_notification));
                items.remove(getString(R.string.chat_to_group));
                items.remove(getString(R.string.clean_up));
                items.remove(getString(R.string.SendMoney));
                items.remove(getString(R.string.export_chat));
                items.add(getString(R.string.stop));
            }
        }

        if (getContext() != null) {
            TopSheetDialog topSheetDialog = new TopSheetDialog(getContext()).setListData(items, -1, position -> {
                if (items.get(position).equals(getString(R.string.Search))) {
                    initLayoutSearchNavigation();
                    layoutToolbar.setVisibility(View.GONE);
                    ll_Search.setVisibility(View.VISIBLE);
                    if (!initHash) {
                        initHash = true;
                        initHashView();
                    }

                    showPopup(KeyboardView.MODE_KEYBOARD);
                    G.handler.postDelayed(() -> editTextRequestFocus(edtSearchMessage), 200);

                } else if (items.get(position).equals(getString(R.string.clear_history))) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            onSelectRoomMenu("txtClearHistory", mRoomId);
                        }
                    }).negativeText(R.string.no).show();
                } else if (items.get(position).equals(getString(R.string.delete_chat))) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.delete_chat).content(R.string.delete_chat_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            onSelectRoomMenu("txtDeleteChat", mRoomId);
                        }
                    }).negativeText(R.string.no).show();
                } else if (items.get(position).equals(getString(R.string.mute_notification)) || items.get(position).equals(getString(R.string.unmute_notification))) {
                    onSelectRoomMenu("txtMuteNotification", mRoomId);
                } else if (items.get(position).equals(getString(R.string.chat_to_group))) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.convert_chat_to_group_title).content(R.string.convert_chat_to_group_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //finish();
                            finishChat();
                            dialog.dismiss();
                            G.handler.post(() -> G.onConvertToGroup.openFragmentOnActivity("ConvertToGroup", mRoomId));
                        }
                    }).show();
                } else if (items.get(position).equals(getString(R.string.clean_up))) {
                    resetMessagingValue();
                    setDownBtnGone();
                    setCountNewMessageZero();
                    DbManager.getInstance().doRealmTask(realm -> {
                        RealmRoomMessage.ClearAllMessageRoomAsync(realm, mRoomId, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                recyclerView.addOnScrollListener(scrollListener);
                                saveMessageIdPositionState(0);
                                /**
                                 * get history from server
                                 */
                                topMore = true;
                                getOnlineMessage(0, UP);
                            }
                        });
                    });

                } else if (items.get(position).equals(getString(R.string.report))) {
                    dialogReport(false, 0);
                } else if (items.get(position).equals(getString(R.string.SendMoney))) {
                    showPaymentDialog();
                } else if (items.get(position).equals(getString(R.string.export_chat))) {
                    if (HelperPermission.grantedUseStorage()) {
                        exportChat();
                    } else {
                        try {
                            HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                                @Override
                                public void Allow() {
                                    exportChat();
                                }

                                @Override
                                public void deny() {
                                    Toast.makeText(G.currentActivity, R.string.export_message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (items.get(position).equals(getString(R.string.stop))) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.stop).content(R.string.stop_message_bot).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //   onSelectRoomMenu("txtClearHistory", mRoomId);
                            closeWebViewForSpecialUrlChat(true);
                            //  popBackStackFragment();

                        }
                    }).negativeText(R.string.no).show();
                }
            });
            topSheetDialog.show();
        }
            /*final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
            View v = dialog.getCustomView();

            DialogAnimation.animationUp(dialog);
            dialog.show();*/

    }

    @Override
    public void onSecondRightIconClickListener(View view) {
        if (CallManager.getInstance().getCallPeerId() == chatPeerId) {
            Intent activityIntent = new Intent(getActivity(), CallActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(activityIntent);
        } else if (!CallManager.getInstance().isCallAlive()) {
            CallSelectFragment selectFragment = CallSelectFragment.getInstance(chatPeerId, false, null);
            if (getFragmentManager() != null)
                selectFragment.show(getFragmentManager(), null);

            if (keyboardViewVisible) {
                hideKeyboard();
            }
        } else {
            Toast.makeText(getContext(), "NOT ALLOWED", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFourthRightIconClickListener(View view) {

    }

    @Override
    public void onAttachPopupVideoPickerResult(List<String> results) {

        if (results.size() == 1) {
            manageSelectedVideoResult(results.get(0));
            return;
        }

        for (String path : results) {
            sendMessage(request_code_VIDEO_CAPTURED, path);
        }
        edtChat.setText("");

    }

    @Override
    public void onAttachPopupShowed() {

    }

    @Override
    public void onAttachPopupDismiss() {
    }

    @Override
    public void onAttachPopupLocation(String message) {
        try {
            if (getActivity() != null) {
                String[] split = message.split(",");
                Double latitude = Double.parseDouble(split[0]);
                Double longitude = Double.parseDouble(split[1]);
                FragmentMap fragment = FragmentMap.getInctance(latitude, longitude, FragmentMap.Mode.sendPosition);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    @Override
    public void onAttachPopupFilePicked(List<String> selectedPathList, String caption) {
        if (caption != null) edtChat.setText(caption);
        for (String path : selectedPathList) {
            sendMessage(request_code_pic_file, path);
            edtChat.setText("");
        }
    }

    @Override
    public void onAttachPopupMusicPickerResult(String result) {
        if (result == null) return;
        Intent data = new Intent();
        data.setData(Uri.parse(result));
        onActivityResult(request_code_pic_audi, Activity.RESULT_OK, data);
    }

    @Override
    public void onAttachPopupSendSelected() {

        final ArrayList<StructBottomSheet> itemList = new ArrayList<StructBottomSheet>();
        for (Map.Entry<String, StructBottomSheet> items : FragmentEditImage.textImageList.entrySet()) {
            itemList.add(items.getValue());
        }
        Collections.sort(itemList);

        new Thread(() -> G.handler.post(() -> {

            if (itemList.size() == 1) {
                showDraftLayout();
                listPathString.add(itemList.get(0).getPath());
                listPathString.set(0, attachFile.saveGalleryPicToLocal(itemList.get(0).getPath()));
                setDraftMessage(AttachFile.requestOpenGalleryForImageMultipleSelect);
                latestRequestCode = AttachFile.requestOpenGalleryForImageMultipleSelect;
                //sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, pathStrings.get(0));
            } else {
                for (StructBottomSheet items : itemList) {

                    //if (!path.toLowerCase().endsWith(".gif")) {
                    String localPathNew = attachFile.saveGalleryPicToLocal(items.path);
                    edtChat.setText(EmojiManager.getInstance().replaceEmoji(items.getText(), edtChat.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false));
                    sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, localPathNew);
                    //}
                }

            }

        })).start();
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.CALL_STATE_CHANGED) {
            if (args == null || args.length == 0) return;
            boolean state = (boolean) args[0];
            G.handler.post(() -> {
                mHelperToolbar.getCallLayout().setVisibility(state ? View.VISIBLE : View.GONE);
                if (MusicPlayer.chatLayout != null) MusicPlayer.chatLayout.setVisibility(View.GONE);
                if (MusicPlayer.mainLayout != null) MusicPlayer.mainLayout.setVisibility(View.GONE);
            });
        } else if (id == EventManager.EMOJI_LOADED) {
            G.runOnUiThread(this::invalidateViews);
        } else if (id == EventManager.CHAT_BACKGROUND_CHANGED) {
            G.handler.post(() -> {
                String path = (String) args[0];
                if (new File(path).exists())
                    ImageLoadingServiceInjector.inject().loadImage(imgBackGround, path, true);
            });
        } else if (id == EventManager.ON_MESSAGE_DELETE) {
            long roomId = (long) args[0];
            long messageId = (long) args[1];
            boolean update = (boolean) args[2];

            if (roomId == mRoomId && update) {
                G.runOnUiThread(() -> {
                    if (mAdapter == null) {
                        return;
                    }

                    ArrayList<Long> messages = new ArrayList<>(1);
                    messages.add(messageId);
                    deleteSelectedMessageFromAdapter(messages);

                    if (mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE) {
                        RealmRoomMessage roomMessage = (RealmRoomMessage) mReplayLayout.getTag();
                        if (roomMessage != null && messageId == roomMessage.getMessageId()) {
                            clearReplyView();
                        }
                    }
                });
            }
        } else if (id == EventManager.ON_EDIT_MESSAGE) {
            G.runOnUiThread(() -> {
                long roomId = (long) args[0];
                long messageId = (long) args[1];
                String newMessage = (String) args[2];

                if (mRoomId == roomId && mAdapter != null) {
                    mAdapter.updateMessageText(messageId, newMessage);
                    removeEditedMessage();
                }
            });
        } else if ((long) args[0] == mRoomId && id == EventManager.ON_PINNED_MESSAGE) {
            G.runOnUiThread(this::initPinedMessage);
        } else if (id == EventManager.CHAT_CLEAR_MESSAGE) {
            G.runOnUiThread(() -> {
                long roomId = (long) args[0];
                if (roomId == mRoomId) {
                    long clearID = (long) args[1];
                    onChatClearMessage(roomId, clearID);
                }

            });
        } else if (id == EventManager.CHANNEL_ADD_VOTE) {
            G.runOnUiThread(() -> {
                long roomId = (long) args[0];
                if (roomId == mRoomId) {

                    long messageId = (long) args[1];
                    String reactionCount = (String) args[2];
                    ProtoGlobal.RoomMessageReaction reaction = (ProtoGlobal.RoomMessageReaction) args[3];
                    long forwardedMessageId = (long) args[4];

                    if (mAdapter != null) {
                        mAdapter.updateVote(roomId, messageId, reactionCount, reaction, forwardedMessageId);
                    }
                }
            });
        } else if (id == EventManager.CHANNEL_GET_VOTE) {
            G.runOnUiThread(() -> {
                if (mAdapter != null) {

                    List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats> states = (List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats>) args[0];

                    for (final ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats stats : states) {
                        mAdapter.updateMessageState(stats.getMessageId(), stats.getThumbsUpLabel(), stats.getThumbsDownLabel(), stats.getViewsLabel());

                    }
                }
            });

        } else if (id == EventManager.CHANNEL_UPDATE_VOTE) {
            G.runOnUiThread(() -> {
                showVoteChannel = (boolean) args[1];
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            });

        } else if (id == EventManager.CHAT_UPDATE_STATUS) {
            G.runOnUiThread(() -> {
                long roomId = (long) args[0];
                long messageId = (long) args[1];
                ProtoGlobal.RoomMessageStatus status = (ProtoGlobal.RoomMessageStatus) args[2];
                if (roomId == mRoomId) {
                    if (mAdapter != null) {
                        mAdapter.updateMessageStatus(messageId, status);
                    }
                }

            });
        }
    }

    /**
     * *** SearchHash ***
     */

    private class SearchHash {

        public String lastMessageId = "";
        private String hashString = "";
        private int currentHashPosition;

        private ArrayList<String> hashList = new ArrayList<>();

        void setHashString(String hashString) {
            this.hashString = hashString.toLowerCase();
        }

        public void setPosition(String messageId) {

            if (mAdapter == null) {
                return;
            }

            if (lastMessageId.length() > 0) {
                mAdapter.toggleSelection(lastMessageId, false, null);
            }

            currentHashPosition = 0;
            hashList.clear();

            for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
//                if (mAdapter.getItem(i).mMessage != null) {
//
//                    if (messageId.length() > 0) {
//                        if ((mAdapter.getItem(i).mMessage.getMessageId() + "").equals(messageId)) {
//                            currentHashPosition = hashList.size();
//                            lastMessageId = messageId;
//                            mAdapter.getItem(i).structMessage.isSelected = true;
//                            mAdapter.notifyItemChanged(i);
//                        }
//                    }
//
//                    String mText = mAdapter.getItem(i).mMessage.getForwardMessage() != null ? mAdapter.getItem(i).mMessage.getForwardMessage().getMessage() : mAdapter.getItem(i).mMessage.getMessage();
//
//                    if (mText.toLowerCase().contains(hashString)) {
//                        hashList.add(mAdapter.getItem(i).mMessage.getMessageId() + "");
//                    }
//                }
            }

            if (messageId.length() == 0) {
                txtHashCounter.setText(hashList.size() + " / " + hashList.size());

                if (hashList.size() > 0) {
                    currentHashPosition = hashList.size() - 1;
                    goToSelectedPosition(hashList.get(currentHashPosition));
                }
            } else {
                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        void downHash() {
            if (currentHashPosition < hashList.size() - 1) {

                currentHashPosition++;

                goToSelectedPosition(hashList.get(currentHashPosition));

                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        void upHash() {
            if (currentHashPosition > 0) {

                currentHashPosition--;

                goToSelectedPosition(hashList.get(currentHashPosition));
                txtHashCounter.setText((currentHashPosition + 1) + " / " + hashList.size());
            }
        }

        private void goToSelectedPosition(String messageid) {

            mAdapter.toggleSelection(lastMessageId, false, null);

            lastMessageId = messageid;

            mAdapter.toggleSelection(lastMessageId, true, recyclerView);
        }
    }

}

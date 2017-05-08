package net.iGap.activities;

import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.libs.call.PeerConnectionParameters;
import net.iGap.libs.call.WebRtcClient;
import net.iGap.module.MaterialDesignTextView;
import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

/**
 * Created by android3 on 5/8/2017.
 */

public class ActivityCall extends ActivityEnhanced implements WebRtcClient.RtcListener {

    public static final String UserIdStr = "USERID";

    long userID;

    TextView txtName;
    TextView txtStatus;
    AVLoadingIndicatorView avLoadingIndicatorView;
    ImageView imvPicture;
    LinearLayout layoutAnswer;
    LinearLayout layoutCaller;
    LinearLayout layoutOption;
    Button btnAnswerInputCall;
    Button btnEndInputCall;
    MaterialDesignTextView btnClose;
    MaterialDesignTextView btnEndCall;
    MaterialDesignTextView btnCall;
    MaterialDesignTextView btnMic;
    MaterialDesignTextView btnChat;
    MaterialDesignTextView btnSpeaker;

    //************************************************************************

    private String host = "ns37269.ip-91-121-3.eu";
    private String port = "3000";

    private final static int VIDEO_CALL_SENT = 666;
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;

    private VideoRendererGui.ScalingType scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
    private GLSurfaceView glSurfaceView;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;

    private WebRtcClient client;
    private String mSocketAddress;
    private String callerId;

    //************************************************************************

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
            LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_call);

        userID = getIntent().getExtras().getLong(UserIdStr);

        initComponent();

        initCall();

        Toast.makeText(ActivityCall.this, userID + "", Toast.LENGTH_SHORT).show();
    }

    @Override public void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        if (client != null) {
            client.onResume();
        }
    }

    @Override public void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        if (client != null) {
            client.onPause();
        }
    }

    @Override public void onDestroy() {
        if (client != null) {
            client.onDestroy();
        }
        super.onDestroy();
    }

    //***************************************************************************************

    private void initComponent() {

        txtName = (TextView) findViewById(R.id.fcr_txt_name);
        txtStatus = (TextView) findViewById(R.id.fcr_txt_status);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.fcr_txt_avi);
        imvPicture = (ImageView) findViewById(R.id.fcr_imv_igap_icon);

        layoutAnswer = (LinearLayout) findViewById(R.id.fcr_layout_answer);
        layoutCaller = (LinearLayout) findViewById(R.id.fcr_layout_caller);
        layoutOption = (LinearLayout) findViewById(R.id.fcr_layout_option);

        btnAnswerInputCall = (Button) findViewById(R.id.fcr_btn_answer_input_call);
        btnEndInputCall = (Button) findViewById(R.id.fcr_btn_end_input_call);

        btnClose = (MaterialDesignTextView) findViewById(R.id.fcr_btn_close);
        btnEndCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_end);
        btnCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_call);

        btnChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_chat);

        btnSpeaker = (MaterialDesignTextView) findViewById(R.id.fcr_btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnSpeaker.getText().toString().equals(G.context.getResources().getString(R.string.md_muted))) {
                    btnSpeaker.setText(R.string.md_unMuted);
                } else {
                    btnSpeaker.setText(R.string.md_muted);
                }
            }
        });

        btnMic = (MaterialDesignTextView) findViewById(R.id.fcr_btn_mic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnMic.getText().toString().equals(G.context.getResources().getString(R.string.md_mic))) {
                    btnMic.setText(R.string.md_mic_off);
                } else {
                    btnMic.setText(R.string.md_mic);
                }
            }
        });

        btnChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                HelperPublicMethod.goToChatRoom(userID, null, null);
            }
        });
    }

    private void initCall() {

        mSocketAddress = "http://" + host + (":" + port + "/");

        // mSocketAddress = http://localhost:8888/

        glSurfaceView = (GLSurfaceView) findViewById(R.id.fcr_glview_call);
        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.setKeepScreenOn(true);
        VideoRendererGui.setView(glSurfaceView, new Runnable() {
            @Override public void run() {
                init();
            }
        });

        // local and remote render
        remoteRender = VideoRendererGui.create(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        localRender = VideoRendererGui.create(LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            final List<String> segments = intent.getData().getPathSegments();
            callerId = segments.get(0);
        }
    }

    private void init() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        PeerConnectionParameters params = new PeerConnectionParameters(true, false, displaySize.x, displaySize.y, 30, 1, VIDEO_CODEC_VP9, true, 1, AUDIO_CODEC_OPUS, true);

        // client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext());
    }

    // *********************************************************************************************

    @Override public void onCallReady(String callId) {
        if (callerId != null) {
            try {
                answer(callerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            call(callId);
        }
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
        startCam();
    }

    public void call(String callId) {
        Intent msg = new Intent(Intent.ACTION_SEND);
        msg.putExtra(Intent.EXTRA_TEXT, mSocketAddress + callId);
        msg.setType("text/plain");
        startActivityForResult(Intent.createChooser(msg, "Call someone :"), VIDEO_CALL_SENT);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CALL_SENT) {
            startCam();
        }
    }

    public void startCam() {
        // Camera settings
        client.start("android_test");
    }

    @Override public void onStatusChanged(final String newStatus) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onLocalStream(MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        VideoRendererGui.update(localRender, LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType);
    }

    @Override public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
        VideoRendererGui.update(remoteRender, REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT, scalingType);
        VideoRendererGui.update(localRender, LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED, scalingType);
    }

    @Override public void onRemoveRemoteStream(int endPoint) {
        VideoRendererGui.update(localRender, LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType);
    }
}

/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.webrtc;


import android.content.Context;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import io.realm.Realm;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.proto.ProtoSignalingGetConfiguration;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.request.RequestSignalingAccept;
import net.iGap.request.RequestSignalingCandidate;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestSignalingOffer;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class WebRTC extends ActivityEnhanced implements PeerConnection.Observer, VideoRenderer.Callbacks {
    public static PeerConnection peerConnection;
    public static PeerConnectionFactory peerConnectionFactory;
    public static MediaConstraints mediaConstraints;
    public static MediaConstraints audioConstraints;
    private AudioTrack audioTrack;
    private AudioSource audioSource;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack videoTrack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_rtc);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        Button btnCall = (Button) findViewById(R.id.btnCall);
        Button btnAnswer = (Button) findViewById(R.id.btnAnswer);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOffer();
            }
        });
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAnswer();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveCall();
            }
        });

        start();
    }


    public void start() {
        peerConnectionInstance();
        MediaStream mediaStream = peerConnectionFactoryInstance().createLocalMediaStream("ARDAMS");
        addAudioTrack(mediaStream);
        //addVideoTrack(mediaStream);

        peerConnectionInstance().addStream(mediaStream);
        //setAudioEnabled(true);
        //setVideoEnabled(true);
    }

    private void addAudioTrack(MediaStream mediaStream) {
        this.audioSource = peerConnectionFactoryInstance().createAudioSource(audioConstraintsGetInstance());
        this.audioTrack = peerConnectionFactoryInstance().createAudioTrack("ARDAMSa0", audioSource);
        this.audioTrack.setEnabled(true);
        mediaStream.addTrack(audioTrack);
    }

    private void addVideoTrack(MediaStream mediaStream) {
        /**
         * video
         */
        videoCapturer = createVideoCapturer(this);
        if (videoCapturer != null) {
            this.videoSource = peerConnectionFactoryInstance().createVideoSource(videoCapturer);
            this.videoTrack = peerConnectionFactoryInstance().createVideoTrack("ARDAMSv0", videoSource);

            /**
             * ******************************** VideoRenderer ********************************
             */
            try {
                /**
                 * To create our VideoRenderer, we can use the
                 * included VideoRendererGui for simplicity
                 * First we need to set the GLSurfaceView that it should render to
                 */
                GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.surface_view_web_rtc);

                /**
                 * Then we set that view, and pass a Runnable
                 * to run once the surface is ready
                 */
                VideoRendererGui.setView(videoView, new Runnable() {
                    @Override
                    public void run() {
                        Log.i("WWW", "setView");
                    }
                });

                /**
                 * Now that VideoRendererGui is ready, we can get our VideoRenderer
                 */
                VideoRenderer renderer = VideoRendererGui.createGui(0, 0, 100, 100, RendererCommon.ScalingType.SCALE_ASPECT_FIT, true);
                /**
                 * And finally, with our VideoRenderer ready, we
                 * can add our renderer to the VideoTrack.
                 */
                //localVideoTrack.addRenderer(renderer);
                this.videoTrack.addRenderer(new VideoRenderer(this));
                this.videoTrack.addRenderer(renderer);
                this.videoTrack.setEnabled(true);
                mediaStream.addTrack(videoTrack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.videoSource = null;
            this.videoTrack = null;
        }
    }


    /**
     * First, we initiate the PeerConnectionFactory with our application context and some options.
     */
    public PeerConnectionFactory peerConnectionFactoryInstance() {
        if (peerConnectionFactory == null) {
            PeerConnectionFactory.initializeAndroidGlobals(this,  // Context
                    true,  // Audio Enabled
                    true,  // Video Enabled
                    true); // Hardware Acceleration Enabled

            peerConnectionFactory = new PeerConnectionFactory(new PeerConnectionFactoryOptions());
        }
        return peerConnectionFactory;
    }

    public PeerConnection peerConnectionInstance() {
        if (peerConnection == null) {
            List<PeerConnection.IceServer> iceServers = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();
            for (ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer ice : realmCallConfig.getIceServer()) {
                iceServers.add(new PeerConnection.IceServer(ice.getUrl(), ice.getUsername(), ice.getCredential()));
            }
            realm.close();

            PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);
            configuration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
            configuration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
            configuration.iceTransportsType = PeerConnection.IceTransportsType.RELAY;
            peerConnection = peerConnectionFactoryInstance().createPeerConnection(iceServers, mediaConstraintsGetInstance(), this);
        }

        return peerConnection;
    }

    public void createOffer() {
        peerConnectionInstance().createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                setLocalDescription(SessionDescription.Type.OFFER, sessionDescription.description);

                long userIdCallee = 449; // Huawei
                if (G.userId == 449) {
                    userIdCallee = 510; // Lg Big
                }
                new RequestSignalingOffer().signalingOffer(userIdCallee, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING, sessionDescription.description);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraintsGetInstance());
    }

    public void createAnswer() {
        Log.i("WWW", "createAnswer 1");
        peerConnectionInstance().createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.i("WWW", "createAnswer 2");
                setLocalDescription(SessionDescription.Type.ANSWER, sessionDescription.description);
                Log.i("WWW", "onCreateSuccess sessionDescription : " + sessionDescription);
                Log.i("WWW", "onCreateSuccess sessionDescription.description : " + sessionDescription.description);
                Log.i("WWW", "onCreateSuccess sessionDescription.type : " + sessionDescription.type);
                acceptCall(sessionDescription.description);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraintsGetInstance());
    }

    public void setLocalDescription(final SessionDescription.Type type, String sdp) {
        peerConnectionInstance().setLocalDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.i("WWW", "setLocalDescription createOffer sessionDescription : " + sessionDescription.toString());
                Log.i("WWW", "setLocalDescription createOffer sessionDescription.description : " + sessionDescription.description);
                Log.i("WWW", "setLocalDescription createOffer sessionDescription.type : " + sessionDescription.type);
            }

            @Override
            public void onSetSuccess() {
                Log.i("WWW", "onSetSuccess");
            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, new SessionDescription(type, sdp));
    }

    public static void leaveCall() {
        new RequestSignalingLeave().signalingLeave();
    }


    public static MediaConstraints mediaConstraintsGetInstance() {
        if (mediaConstraints == null) {
            mediaConstraints = new MediaConstraints();
            mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return mediaConstraints;
    }

    public static MediaConstraints audioConstraintsGetInstance() {
        if (audioConstraints == null) {
            audioConstraints = new MediaConstraints();
            audioConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return audioConstraints;
    }

    private static void acceptCall(String sdp) {
        new RequestSignalingAccept().signalingAccept(sdp);
    }

    public void setVideoEnabled(boolean enabled) {
        if (this.videoTrack != null) {
            this.videoTrack.setEnabled(enabled);
        }

        if (this.videoCapturer != null) {
            try {
                if (enabled) {
                    this.videoCapturer.startCapture(320, 480, 30);
                } else {
                    this.videoCapturer.stopCapture();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAudioEnabled(boolean enabled) {
        this.audioTrack.setEnabled(enabled);
    }


    public VideoCapturer createVideoCapturer(@NonNull Context context) {
        VideoCapturer videoCapturer = null;
        boolean camera2EnumeratorIsSupported = false;
        try {
            camera2EnumeratorIsSupported = Camera2Enumerator.isSupported(context);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }

        CameraEnumerator enumerator;
        if (camera2EnumeratorIsSupported) {
            enumerator = new Camera2Enumerator(context);
        } else {
            enumerator = new Camera1Enumerator(true);
        }

        String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;


        //if (videoCapturer != null) {
        //    this.videoCapturer = videoCapturer;
        //    /**
        //     * ******************************** VideoSource/VideoTrack ********************************
        //     */
        //
        //    /**
        //     * First we create a VideoSource
        //     */
        //    VideoSource videoSource = peerConnectionFactoryInstance().createVideoSource(videoCapturer);
        //
        //    /**
        //     * Once we have that, we can create our VideoTrack
        //     * Note that VIDEO_TRACK_ID can be any string that uniquely
        //     * identifies that video track in your application
        //     */
        //    VideoTrack localVideoTrack = peerConnectionFactoryInstance().createVideoTrack("VIDEO_TRACK_ID", videoSource);
        //
        //    /**
        //     * ******************************** AudioSource/AudioTrack ********************************
        //     */
        //    /**
        //     * First we create an AudioSource
        //     */
        //    AudioSource audioSource = peerConnectionFactoryInstance().createAudioSource(mediaConstraintsGetInstance());
        //
        //    /**
        //     * Once we have that, we can create our AudioTrack
        //     * Note that AUDIO_TRACK_ID can be any string that uniquely
        //     * identifies that audio track in your application
        //     */
        //    AudioTrack localAudioTrack = peerConnectionFactoryInstance().createAudioTrack("AUDIO_TRACK_ID", audioSource);
        //
        //
        //    /**
        //     * ******************************** VideoRenderer ********************************
        //     */
        //
        //    try {
        //        /**
        //         * To create our VideoRenderer, we can use the
        //         * included VideoRendererGui for simplicity
        //         * First we need to set the GLSurfaceView that it should render to
        //         */
        //        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.surface_view_web_rtc);
        //
        //        /**
        //         * Then we set that view, and pass a Runnable
        //         * to run once the surface is ready
        //         */
        //        VideoRendererGui.setView(videoView, new Runnable() {
        //            @Override
        //            public void run() {
        //                Log.i("WWW", "setView");
        //            }
        //        });
        //
        //        /**
        //         * Now that VideoRendererGui is ready, we can get our VideoRenderer
        //         */
        //        VideoRenderer renderer = VideoRendererGui.createGui(0, 0, 100, 100, RendererCommon.ScalingType.SCALE_ASPECT_FIT, true);
        //        /**
        //         * And finally, with our VideoRenderer ready, we
        //         * can add our renderer to the VideoTrack.
        //         */
        //        localVideoTrack.addRenderer(renderer);
        //
        //    } catch (Exception e) {
        //        e.printStackTrace();
        //    }
        //
        //    MediaStream mediaStream = peerConnectionFactoryInstance().createLocalMediaStream("LOCAL_MEDIA_STREAM_ID");
        //
        //    /**
        //     * Now we can add our tracks
        //     */
        //    mediaStream.addTrack(localVideoTrack);
        //    mediaStream.addTrack(localAudioTrack);
        //}
    }


    /**
     * ***********************************************************************************************
     * ********************************** Peer Connection Observers **********************************
     * ***********************************************************************************************
     */

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.i("WWW", "onSignalingChange : " + signalingState);
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.i("WWW", "onIceConnectionChange : " + iceConnectionState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.i("WWW", "onIceConnectionReceivingChange : " + b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.i("WWW", "onIceGatheringChange : " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.i("WWW", "onIceCandidate");
        Log.i("WWW", "WebRtc onIceCandidate : " + iceCandidate.toString());
        Log.i("WWW", "WebRtc onIceCandidate.sdp : " + iceCandidate.sdp);
        Log.i("WWW", "WebRtc onIceCandidate.sdpMid : " + iceCandidate.sdpMid);
        Log.i("WWW", "WebRtc onIceCandidate.sdpMLineIndex : " + iceCandidate.sdpMLineIndex);
        new RequestSignalingCandidate().signalingCandidate(iceCandidate.toString());

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.i("WWW", "onIceCandidatesRemoved : " + iceCandidates);
    }

    @Override
    public void onAddStream(MediaStream stream) {
        Log.i("WWW", "onAddStream : " + stream);

        for (AudioTrack audioTrack : stream.audioTracks) {
            audioTrack.setEnabled(true);
        }

        if (stream.videoTracks != null && stream.videoTracks.size() == 1) {
            VideoTrack videoTrack = stream.videoTracks.getFirst();
            videoTrack.setEnabled(true);
            videoTrack.addRenderer(new VideoRenderer(this));
        }
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.i("WWW", "onRemoveStream : " + mediaStream);
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.i("WWW", "onDataChannel : " + dataChannel);
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.i("WWW", "onRenegotiationNeeded");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.i("WWW", "onAddTrack RtpReceiver : " + rtpReceiver + "  ||  MediaStream[] : " + mediaStreams);
    }

    @Override
    public void renderFrame(VideoRenderer.I420Frame i420Frame) {
        Log.i("WWW", "renderFrame : " + i420Frame);
    }
}
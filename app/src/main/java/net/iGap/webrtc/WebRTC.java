/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.webrtc;


import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.activities.ActivityCall;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmIceServer;
import net.iGap.request.RequestSignalingAccept;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestSignalingOffer;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebRTC {

    private static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private static final int VIDEO_RESOLUTION_WIDTH = 720;
    private static final int VIDEO_RESOLUTION_HEIGHT = 480;
    private static final int FPS = 30;

    private PeerConnection peerConnection;
    private PeerConnectionFactory peerConnectionFactory;
    private MediaStream mediaStream;

    private String offerSdp;
    private MediaConstraints mediaConstraints;
    private MediaConstraints audioConstraints;
    private VideoCapturer videoCapturer;
    private VideoTrack videoTrackFromCamera;
    VideoSource videoSource;
    private ProtoSignalingOffer.SignalingOffer.Type callTYpe;

    private static WebRTC webRTCInstance;
    EglBase.Context eglBaseContext = null;

    public static WebRTC getInstance() {
        if (webRTCInstance == null) {
            webRTCInstance = new WebRTC();
        }
        return webRTCInstance;
    }

    public void muteSound() {

        if (mediaStream == null) {
            return;
        }

        for (AudioTrack audioTrack : mediaStream.audioTracks) {
            audioTrack.setEnabled(false);
        }
    }


    public void switchCamera() {
        if (Camera.getNumberOfCameras() > 1) {
            if (videoCapturer instanceof CameraVideoCapturer) {
                ((CameraVideoCapturer) videoCapturer).switchCamera(null);
            }
        }
    }

    public void unMuteSound() {

        if (mediaStream == null) {
            return;
        }

        for (AudioTrack audioTrack : mediaStream.audioTracks) {
            audioTrack.setEnabled(true);
        }
    }

    private void addAudioTrack(MediaStream mediaStream) {
        AudioSource audioSource = peerConnectionFactoryInstance().createAudioSource(audioConstraintsGetInstance());
        AudioTrack audioTrack = peerConnectionFactoryInstance().createAudioTrack("ARDAMSa0", audioSource);
        audioTrack.setEnabled(true);
        mediaStream.addTrack(audioTrack);
    }


    public void setCallType(ProtoSignalingOffer.SignalingOffer.Type callTYpe) {
        this.callTYpe = callTYpe;
    }

    public EglBase.Context getEglBaseContext() {
        if (eglBaseContext == null)
            eglBaseContext = EglBase.create().getEglBaseContext();
        return eglBaseContext;
    }


    private void addVideoTrack(MediaStream mediaStream) {

        if (callTYpe == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING) {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(false));
            videoSource = peerConnectionFactoryInstance().createVideoSource(videoCapturer.isScreencast());
            SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", getEglBaseContext());
            videoCapturer.initialize(surfaceTextureHelper, G.context, videoSource.getCapturerObserver());
            videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
            videoTrackFromCamera = peerConnectionFactoryInstance().createVideoTrack(VIDEO_TRACK_ID, videoSource);
            videoTrackFromCamera.setEnabled(true);

            videoTrackFromCamera.addSink(new VideoSink() {
                @Override
                public void onFrame(VideoFrame videoFrame) {
                    if (G.onVideoCallFrame != null) {
                        G.onVideoCallFrame.onPeerFrame(videoFrame);
                    /*    Log.i("#peymanW2",videoFrame.getRotatedWidth()+"");
                        Log.i("#peymanH2",videoFrame.getRotatedHeight()+"");*/
                    }
                }
            });


            mediaStream.addTrack(videoTrackFromCamera);
        }
    }


    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    public void pauseVideoCapture() {
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startVideoCapture() {
        if (videoCapturer != null) {
            try {
                videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * First, we initiate the PeerConnectionFactory with our application context and some options.
     */
    private PeerConnectionFactory peerConnectionFactoryInstance() {
        if (peerConnectionFactory == null) {

            initializePeerConnectionFactory();

            PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(G.context).createInitializationOptions());
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(eglBaseContext, true, true);
            DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(eglBaseContext);
            peerConnectionFactory = PeerConnectionFactory.builder()
                    .setOptions(options)
                    .setVideoEncoderFactory(defaultVideoEncoderFactory)
                    .setVideoDecoderFactory(defaultVideoDecoderFactory)
                    .createPeerConnectionFactory();

        }
        return peerConnectionFactory;
    }

    private void initializePeerConnectionFactory() {
        try {
            Set<String> HARDWARE_AEC_BLACKLIST = new HashSet<String>() {{
                add("Pixel");
                add("Pixel XL");
                add("Moto G5");
                add("Moto G (5S) Plus");
                add("Moto G4");
                add("TA-1053");
                add("Mi A1");
                add("E5823"); // Sony z5 compact
            }};

            Set<String> OPEN_SL_ES_WHITELIST = new HashSet<String>() {{
                add("Pixel");
                add("Pixel XL");
            }};


            if (WebRtcAudioUtils.isAcousticEchoCancelerSupported()) {
                WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
            }

            if (WebRtcAudioUtils.isAutomaticGainControlSupported()) {
                WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);
            }

            if (WebRtcAudioUtils.isNoiseSuppressorSupported()) {
                WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
            }

//            if (HARDWARE_AEC_BLACKLIST.contains(Build.MODEL)) {
//                    WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
//            }

            if (!OPEN_SL_ES_WHITELIST.contains(Build.MODEL)) {
                WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true);
            }

            PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(G.context).createInitializationOptions());
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    PeerConnection peerConnectionInstance() {
        if (peerConnection == null) {
            List<PeerConnection.IceServer> iceServers = new ArrayList<>();
            DbManager.getInstance().doRealmTask(realm -> {
                RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();
                for (RealmIceServer ice : realmCallConfig.getIceServer()) {
                    iceServers.add(new PeerConnection.IceServer(ice.getUrl(), ice.getUsername(), ice.getCredential()));
                }
            });

            PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);
            configuration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
            configuration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
            configuration.iceTransportsType = PeerConnection.IceTransportsType.RELAY;


            PeerConnection.Observer observer = new PeerConnectionObserver();
            MediaConstraints mediaConstraints = mediaConstraintsGetInstance();

            peerConnection = peerConnectionFactoryInstance().createPeerConnection(iceServers, mediaConstraints, observer);

            mediaStream = peerConnectionFactoryInstance().createLocalMediaStream("ARDAMS");
            addAudioTrack(mediaStream);
            addVideoTrack(mediaStream);
            peerConnection.addStream(mediaStream);

        }

        return peerConnection;
    }

    public void createOffer(final long userIdCallee) {
        peerConnectionInstance().createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                offerSdp = sessionDescription.description;
                new RequestSignalingOffer().signalingOffer(userIdCallee, callTYpe, sessionDescription.description);
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

    public void setOfferLocalDescription() {
        setLocalDescription(SessionDescription.Type.OFFER, offerSdp);
    }

    public void createAnswer() {

        peerConnectionInstance().createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                setLocalDescription(SessionDescription.Type.ANSWER, sessionDescription.description);
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

    private void setLocalDescription(final SessionDescription.Type type, String sdp) {
        peerConnectionInstance().setLocalDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
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

    private MediaConstraints mediaConstraintsGetInstance() {
        if (mediaConstraints == null) {
            mediaConstraints = new MediaConstraints();
            mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return mediaConstraints;
    }

    private MediaConstraints audioConstraintsGetInstance() {
        if (audioConstraints == null) {
            audioConstraints = new MediaConstraints();
            audioConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return audioConstraints;
    }

    private void acceptCall(String sdp) {
        new RequestSignalingAccept().signalingAccept(sdp);
    }

    public void leaveCall() {
        //don't need for close/dispose here, this action will be doing in onLeave callback
        //close();
        //dispose();
        /**
         * set peer connection null for try again
         */
        //clearConnection();
        new RequestSignalingLeave().signalingLeave();
    }

    public void close() {

        ActivityCall.allowOpenCall = false;
        try {

            if (videoCapturer != null) {
                videoCapturer.stopCapture();
                videoCapturer = null;
            }

            if (peerConnection != null) {
                peerConnection.close();
                peerConnection.dispose();
            }

            if (peerConnectionFactory != null) {
                peerConnectionFactory.dispose();
            }

            peerConnectionFactory = null;
            peerConnection = null;
            webRTCInstance = null;

        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
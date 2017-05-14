package net.iGap.libs.call;

import android.opengl.EGLContext;
import android.util.Log;
import java.util.HashMap;
import java.util.LinkedList;
import net.iGap.G;
import net.iGap.interfaces.ISignalingAccept;
import net.iGap.interfaces.ISignalingCondidate;
import net.iGap.interfaces.ISignalingLeave;
import net.iGap.interfaces.ISignalingOffer;
import net.iGap.interfaces.ISignalingRinging;
import net.iGap.interfaces.ISignalingSesionHold;
import net.iGap.proto.ProtoSignalingLeave;
import net.iGap.proto.ProtoSignalingOffer;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;

public class WebRtcClient {
    private final static String TAG = "dddd";
    private final static int MAX_PEER = 2;
    private boolean[] endPoints = new boolean[MAX_PEER];
    private PeerConnectionFactory factory;
    private HashMap<String, Peer> peers = new HashMap<>();
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints = new MediaConstraints();
    private MediaStream localMS;
    private VideoSource videoSource;
    private RtcListener mListener;

    Action action = new Action();



    /**
     * Implement this interface to be notified of events.
     */
    public interface RtcListener {
        void onCallReady(String callId);

        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream, int endPoint);

        void onRemoveRemoteStream(int endPoint);
    }



    /**
     * Send a message through the signaling server
     *
     * @param to id of recipient
     * @param type type of message
     * @param payload payload of message
     * @throws JSONException
     */
    public void sendMessage(String to, String type, JSONObject payload) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("type", type);
        message.put("payload", payload);

        // TODO: 5/14/2017  onmessage
    }

    private class Action {

        private void initPeer(String id) {

            if (!peers.containsKey(id)) {
                // if MAX_PEER is reach, ignore the call
                int endPoint = findEndPoint();
                if (endPoint != MAX_PEER) {
                    Peer peer = addPeer(id, endPoint);
                }
            }
        }

        private void CreateOfferCommand(String peerId) {
            Log.e(TAG, "CreateOfferCommand  " + peerId);

            Peer peer = peers.get(peerId);
            peer.pc.createOffer(peer, pcConstraints);
        }

        private void CreateAnswerCommand(String peerId, String type, String strSdp) {

            Log.e(TAG, "CreateAnswerCommand");

            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), strSdp);
            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, pcConstraints);
        }

        private void SetRemoteSDPCommand(String peerId, String type, String strSdp) {

            Log.e(TAG, "SetRemoteSDPCommand");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(type), strSdp);
            peer.pc.setRemoteDescription(peer, sdp);
        }

        private void AddIceCandidateCommand(String peerId, String id, int lable, String candicate) {

            Log.e(TAG, "AddIceCandidateCommand");

            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(id, lable, candicate);
                pc.addIceCandidate(candidate);
                }
        }

    }

    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String id;
        private int endPoint;

        @Override public void onCreateSuccess(final SessionDescription sdp) {
            // TODO: modify sdp to use pcParams prefered codecs
            try {

                Log.e("ddddd", " onCreateSuccess " + sdp.type.canonicalForm() + "   " + sdp.description + "   " + sdp);


                JSONObject payload = new JSONObject();
                payload.put("type", sdp.type.canonicalForm());
                payload.put("sdp", sdp.description);
                sendMessage(id, sdp.type.canonicalForm(), payload);
                pc.setLocalDescription(Peer.this, sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override public void onSetSuccess() {
        }

        @Override public void onCreateFailure(String s) {
        }

        @Override public void onSetFailure(String s) {
        }

        @Override public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        }

        @Override public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

            Log.e("ddddd", " onIceConnectionChange   " + iceConnectionState);

            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                removePeer(id);
                mListener.onStatusChanged("DISCONNECTED");
            }
        }

        @Override public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        }

        @Override public void onIceCandidate(final IceCandidate candidate) {

            Log.e("ddddd", " onIceCandidate   " + candidate.sdpMLineIndex + "   " + candidate.sdpMid + "    " + candidate.sdp + "    " + candidate);

            try {
                JSONObject payload = new JSONObject();
                payload.put("label", candidate.sdpMLineIndex);
                payload.put("id", candidate.sdpMid);
                payload.put("candidate", candidate.sdp);
                sendMessage(id, "candidate", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override public void onAddStream(MediaStream mediaStream) {
            Log.e(TAG, "onAddStream " + mediaStream.label());
            // remote streams are displayed from 1 to MAX_PEER (0 is localStream)
            mListener.onAddRemoteStream(mediaStream, endPoint + 1);
        }

        @Override public void onRemoveStream(MediaStream mediaStream) {
            Log.e(TAG, "onRemoveStream " + mediaStream.label());
            removePeer(id);
        }

        @Override public void onDataChannel(DataChannel dataChannel) {
        }

        @Override public void onRenegotiationNeeded() {

        }

        public Peer(String id, int endPoint) {
            Log.e(TAG, "new Peer: " + id + " " + endPoint);
            this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;

            //   pc.addStream(localMS); //, new MediaConstraints()  todo uncommnt

            mListener.onStatusChanged("CONNECTING");
        }
    }

    private Peer addPeer(String id, int endPoint) {
        Peer peer = new Peer(id, endPoint);
        peers.put(id, peer);

        endPoints[endPoint] = true;
        return peer;
    }

    private void removePeer(String id) {
        Peer peer = peers.get(id);
        mListener.onRemoveRemoteStream(peer.endPoint);
        peer.pc.close();
        peers.remove(peer.id);
        endPoints[peer.endPoint] = false;
    }

    public WebRtcClient(RtcListener listener, PeerConnectionParameters params, EGLContext mEGLcontext, long id) {
        mListener = listener;
        pcParams = params;
        PeerConnectionFactory.initializeAndroidGlobals(listener, true, true, params.videoCodecHwAcceleration, mEGLcontext);
        factory = new PeerConnectionFactory();

        initListener();


        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

        action.initPeer(id + "");
        action.CreateOfferCommand(id + "");


    }

    /**
     * Call this method in Activity.onPause()
     */
    public void onPause() {
        if (videoSource != null) videoSource.stop();
    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        for (Peer peer : peers.values()) {
            peer.pc.dispose();
        }

        if (videoSource != null) {
            videoSource.dispose();
        }

        factory.dispose();

    }

    private int findEndPoint() {
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    /**
     * Start the client.
     *
     * Set up the local stream and notify the signaling server.
     * Call this method after onCallReady.
     *
     * @param name client name
     */
    public void start(String name) {
        setCamera();
        try {
            JSONObject message = new JSONObject();
            message.put("name", name);

            // TODO: 5/14/2017  onready to steam


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCamera() {
        localMS = factory.createLocalMediaStream("ARDAMS");
        if (pcParams.videoCallEnabled) {
            MediaConstraints videoConstraints = new MediaConstraints();
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));

            videoSource = factory.createVideoSource(getVideoCapturer(), videoConstraints);
            localMS.addTrack(factory.createVideoTrack("ARDAMSv0", videoSource));
        }

        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        localMS.addTrack(factory.createAudioTrack("ARDAMSa0", audioSource));

        mListener.onLocalStream(localMS);
    }

    private VideoCapturer getVideoCapturer() {
        String frontCameraDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        return VideoCapturerAndroid.create(frontCameraDeviceName);
    }

    public interface SendRequest {

        void OnReadyToStream(JSONObject message);

        void OnMessage(JSONObject message);

        void OnId(String id);
    }

    private void initListener() {

        G.iSignalingOffer = new ISignalingOffer() {
            @Override public void onOffer(long called_userId, ProtoSignalingOffer.SignalingOffer.Type type, String callerSdp) {

                Log.e("dddd", " G.iSignalingOffer    " + callerSdp + "   " + called_userId + "     " + type);
            }
        };

        G.iSignalingRinging = new ISignalingRinging() {
            @Override public void onRinging() {

                Log.e("dddd", " G.iSignalingRinging    ");
            }
        };

        G.iSignalingAccept = new ISignalingAccept() {
            @Override public void onAccept(String called_sdp) {

                Log.e("dddd", " G.iSignalingAccept    " + called_sdp);
            }
        };

        G.iSignalingCondidate = new ISignalingCondidate() {
            @Override public void onCondidate(String peer_candidate) {
                Log.e("dddd", " G.iSignalingCondidate    " + peer_candidate);
            }
        };

        G.iSignalingLeave = new ISignalingLeave() {
            @Override public void onLeave(ProtoSignalingLeave.SignalingLeaveResponse.Type type) {

                Log.e("dddd", " G.iSignalingLeave    " + type.toString());

                //   MISSED = 0;REJECTED = 1;ACCEPTED = 2;NOT_ANSWERED = 3;UNAVAILABLE = 4;DISCONNECTED = 5;FINISHED = 6;

            }
        };

        G.iSignalingSesionHold = new ISignalingSesionHold() {
            @Override public void onHold(Boolean hold) {

                Log.e("dddd", " G.iSignalingSesionHold    " + hold.toString());
            }
        };
    }



}

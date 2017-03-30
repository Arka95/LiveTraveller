package com.example.arkabhowmik.livetraveller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.hardware.Camera.Parameters;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.arkabhowmik.livetraveller.app.AppConfig;
import com.example.arkabhowmik.livetraveller.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Arka Bhowmik on 3/18/2017.
 */
public class Broadcast extends Activity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    public static final String IP = "LastIP";
    public static final String MyPREFERENCES = "LivetravellerPrefs";
    private static final String TAG = Broadcast.class.getSimpleName();
    private static  String SIGNALING_URI = "http://192.168.0.100:7800";
    private static final String VIDEO_TRACK_ID = "video1";
    private static final String AUDIO_TRACK_ID = "audio1";
    private static final String LOCAL_STREAM_ID = "stream1";
    private static final String SDP_MID = "sdpMid";
    private static final String SDP_M_LINE_INDEX = "sdpMLineIndex";
    private static final String SDP = "sdp";
    private static final String CREATEOFFER = "createoffer";
    private static final String OFFER = "offer";
    private static final String ANSWER = "answer";
    private static final String CANDIDATE = "candidate";
    String uid=new String();
Integer type=4;
    private Camera camera;
    private boolean isFlashOn=false;
    private Camera.Parameters params;
    private boolean hasFlash;
    private PeerConnectionFactory peerConnectionFactory;
    private VideoSource localVideoSource;
    private PeerConnection peerConnection;
    private MediaStream localMediaStream;
    //private VideoRenderer otherPeerRenderer;
    private Socket socket;
    PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d("RTCAPP", "onSignalingChange:" + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d("RTCAPP", "onIceConnectionChange:" + iceConnectionState.toString());
        }


        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            try {
                JSONObject obj = new JSONObject();
                obj.put(SDP_MID, iceCandidate.sdpMid);
                obj.put(SDP_M_LINE_INDEX, iceCandidate.sdpMLineIndex);
                obj.put(SDP, iceCandidate.sdp);
                socket.emit(CANDIDATE, obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }
    };
    private boolean createOffer = false;
    SdpObserver sdpObserver = new SdpObserver() {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            try {
                JSONObject obj = new JSONObject();
                obj.put(SDP, sessionDescription.description);
                if (createOffer) {
                    socket.emit(OFFER, obj);
                } else {
                    socket.emit(ANSWER, obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rtc);


        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SIGNALING_URI = sharedPreferences.getString(IP, AppConfig.BASE_URL)+":7800";


        uid=getIntent().getStringExtra("uid");

        ImageButton leave = (ImageButton) findViewById(R.id.buttonLeave);
        ImageButton rotate = (ImageButton) findViewById(R.id.buttonSwap);
        rotate.setOnClickListener(this);
        if(VideoCapturerAndroid.getDeviceCount()<2)
            ((ViewGroup)rotate.getParent()).removeView(rotate);
        leave.setOnClickListener(this);
        updateBroadcast(uid,"happy",type.toString());

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);

        PeerConnectionFactory.initializeAndroidGlobals(
                this,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        peerConnectionFactory = new PeerConnectionFactory();

        String backFacingCam = VideoCapturerAndroid.getNameOfBackFacingDevice();
        VideoCapturerAndroid vc = VideoCapturerAndroid.create(backFacingCam);

        localVideoSource = peerConnectionFactory.createVideoSource(vc, new MediaConstraints());
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);
        localVideoTrack.setEnabled(true);

        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localAudioTrack.setEnabled(true);

        localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID);
        localMediaStream.addTrack(localVideoTrack);
        localMediaStream.addTrack(localAudioTrack);

        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glview_call);

        VideoRendererGui.setView(videoView, null);
        try {
            VideoRenderer renderer = VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            localVideoTrack.addRenderer(renderer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (peerConnection != null)
            return;

        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));

        peerConnection = peerConnectionFactory.createPeerConnection(
                iceServers,
                new MediaConstraints(),
                peerConnectionObserver);
        peerConnection.addStream(localMediaStream);
/*this is a 3 way handshake
* AT first the broadcast creates and OFFER to send video
* Then the client chooses if he/she wishes to watch it by ANSWERing and joining the network
* As soon as we find a client, the broadcaster now starts emitting the broadcast video
* */
        try {
            socket = IO.socket(SIGNALING_URI);
            socket.on(CREATEOFFER, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    createOffer = true;
                    peerConnection.createOffer(sdpObserver, new MediaConstraints());
                    Log.d("CREATEOFFER", "Creating Offer");
                }

            }).on(OFFER, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER,
                                obj.getString(SDP));
                        peerConnection.setRemoteDescription(sdpObserver, sdp);
                        peerConnection.createAnswer(sdpObserver, new MediaConstraints());
                        Log.d("ONOFFER", "Creating ANSWER after onOffer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }).on(ANSWER, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER,
                                obj.getString(SDP));
                        peerConnection.setRemoteDescription(sdpObserver, sdp);
                        Log.d("OnAnswer", "Got an Answer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }).on(CANDIDATE, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        peerConnection.addIceCandidate(new IceCandidate(obj.getString(SDP_MID),
                                obj.getInt(SDP_M_LINE_INDEX),
                                obj.getString(SDP)));
                        Log.d("ONCANDIDATE", "Found a candidate");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

//------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public void onClick (View v){

        if (v.getId() == R.id.buttonLeave) {
            type = 5;
            updateBroadcast(uid, "happy", type.toString());
            finish();
            onBackPressed();
        }
        if (v.getId() == R.id.buttonSwap) {
            String frontFacingCam = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        }
    }


//-----------------------------------------------------------------------------------------------------------------------------------
    private void updateBroadcast(final String uid, final String bid,final String type) {
        // Tag used to cancel the request

        String tag_string_req = "req_isbroadcasting";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SESSION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Broadcast Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Broadcast Updation Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", uid);
                params.put("type", type);
                params.put("broadcast_id", bid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void updateLocation(final String uid, final Double lat, final Double longi) {
        // Tag used to cancel the request
        String tag_string_req = "req_locupdate";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SESSION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Loading Session Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("success");

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Location Updation Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", uid);
                params.put("type", "2");
                params.put("lat", lat.toString());
                params.put("longi", longi.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
